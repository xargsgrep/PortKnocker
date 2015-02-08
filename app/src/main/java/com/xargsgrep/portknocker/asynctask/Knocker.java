/*
 *  Copyright 2014 Ahsan Rabbani
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xargsgrep.portknocker.asynctask;

import com.xargsgrep.portknocker.model.Host;
import com.xargsgrep.portknocker.model.Port;
import com.xargsgrep.portknocker.model.Port.Protocol;
import com.xargsgrep.portknocker.utils.SocketUtils;
import com.xargsgrep.portknocker.utils.StringUtils;

import java.io.IOException;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class Knocker
{
    private static final String ENETUNREACH = "ENETUNREACH";

    public static KnockResult doKnock(Host host, KnockerAsyncTask asyncTask)
    {
        KnockResult result = new KnockResult(false, null);
        for (int i = 0; i < host.getPorts().size(); i++)
        {
            Port port = host.getPorts().get(i);
            result = doKnock(host, port);

            if (result.isSuccess())
            {
                result.setLaunchIntentPackage(host.getLaunchIntentPackage());
                asyncTask.doPublishProgress(i + 1);

                if (i < host.getPorts().size() - 1)
                {
                    // no need to sleep after last knock
                    try
                    {
                        Thread.sleep(host.getDelay());
                    }
                    catch (InterruptedException e)
                    {
                        if (asyncTask.isCancelled()) break;
                    }
                }
            }
            else
            {
                break;
            }
        }

        return result;
    }

    /*
     * no network/TCP --> java.net.ConnectException: failed to connect to /1.1.1.1 (port 1234) after 3000ms: connect failed: ENETUNREACH (Network is unreachable)
     * no network/UDP --> java.net.SocketException: sendto failed: ENETUNREACH (Network is unreachable)
     *
     * unable to resolve host/TCP --> java.net.UnknownHostException: Host is unresolved: fakedomain.com
     * unable to resolve host/UDP --> java.lang.IllegalArgumentException: Socket address unresolved: fakedomain.com:1234
     *
     * TCP:
     * router/firewall port closed --> java.net.SocketTimeoutException: failed to connect to fakedomain.com/1.1.1.1 (port 1234) after 3000ms
     * router/firewall port open, socket closed & REJECT packet --> java.net.ConnectException: failed to connect to fakedomain.com/1.1.1.1 (port 1234) after 3000ms: isConnected failed: ECONNREFUSED (Connection refused)
     * router/firewall port open, socket closed & DROP packet --> java.net.SocketTimeoutException: failed to connect to fakedomain.com/1.1.1.1 (port 1234) after 3000ms
     *
     * UDP:
     * router/firewall port closed --> no exception
     * router/firewall port open, socket closed & REJECT packet --> no exception
     * router/firewall port open, socket closed & DROP packet --> no exception
     *
     */
    private static KnockResult doKnock(Host host, Port port)
    {
        SocketAddress socketAddress = new InetSocketAddress(host.getHostname(), port.getPort());

        Socket socket = null;
        DatagramSocket datagramSocket = null;
        try
        {
            if (port.getProtocol() == Protocol.TCP)
            {
                socket = new Socket();
                // set timeout to the lowest possible value since we just want to transmit a packet, we don't care about receiving a syn-ack.
                // this also prevents multiple syn packets being sent (invalidating knock sequence) while waiting for the timeout (if it's high enough)
                socket.connect(socketAddress, host.getTcpConnectTimeout());
            }
            else
            { // PROTOCOL.UDP
                datagramSocket = new DatagramSocket();
                byte[] data = new byte[] {0};
                datagramSocket.send(new DatagramPacket(data, data.length, socketAddress));
            }
        }
        catch (SocketTimeoutException e)
        {
            // this is ok since the timeout is as low as can be and the remote socket isn't expected to be open anyway
        }
        catch (ConnectException e)
        {
            if (StringUtils.contains(e.getMessage(), ENETUNREACH))
            {
                // TCP: host unreachable
                return new KnockResult(false, e.getMessage());
            }
            // ok otherwise
        }
        catch (UnknownHostException e)
        {
            // TCP: unable to resolve hostname
            return new KnockResult(false, e.getMessage());
        }
        catch (IllegalArgumentException e)
        {
            // UDP: unable to resolve hostname
            return new KnockResult(false, e.getMessage());
        }
        catch (SocketException e)
        {
            // UDP: host unreachable
            return new KnockResult(false, e.getMessage());
        }
        catch (IOException e)
        {
            return new KnockResult(false, e.getMessage());
        }
        finally
        {
            SocketUtils.closeQuietly(socket);
            SocketUtils.closeQuietly(datagramSocket);
        }

        return new KnockResult(true, null);
    }

    public static class KnockResult
    {
        private final boolean success;
        private final String error;
        private String launchIntentPackage;

        public KnockResult(boolean success, String error)
        {
            this.success = success;
            this.error = error;
        }

        public boolean isSuccess()
        {
            return success;
        }

        public String getError()
        {
            return error;
        }

        public String getLaunchIntentPackage()
        {
            return launchIntentPackage;
        }

        public void setLaunchIntentPackage(String launchIntentPackage)
        {
            this.launchIntentPackage = launchIntentPackage;
        }
    }
}
