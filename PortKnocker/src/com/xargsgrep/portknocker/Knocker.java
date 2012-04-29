package com.xargsgrep.portknocker;

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

import com.xargsgrep.portknocker.model.Host;
import com.xargsgrep.portknocker.model.Port;
import com.xargsgrep.portknocker.model.Port.Protocol;
import com.xargsgrep.portknocker.utils.SocketUtils;
import com.xargsgrep.portknocker.utils.StringUtils;

public class Knocker {

	private static final String ENETUNREACH = "ENETUNREACH";
	private static final int TCP_SOCKET_TIMEOUT = 1;

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
	public static KnockResult doKnock(Host host) {
		for (Port port : host.getPorts()) {
			SocketAddress socketAddress = new InetSocketAddress(host.getHostname(), port.getPort());

			Socket socket = null;
			DatagramSocket datagramSocket = null;
			try {
				if (port.getProtocol() == Protocol.TCP) {
					socket = new Socket();
					// set timeout to the lowest possible value since we just want to transmit a packet, we don't care about receiving a syn-ack.
					// this also prevents multiple syn packets being sent (invalidating knock sequence) while waiting for the timeout (if it's high enough)
					socket.connect(socketAddress, TCP_SOCKET_TIMEOUT);
				}
				else { // PROTOCOL.UDP
					datagramSocket = new DatagramSocket();
					byte[] data = new byte[] { 0 };
					datagramSocket.send(new DatagramPacket(data, data.length, socketAddress));
				}
			}
			catch (SocketTimeoutException e) { 
				// this is ok since the timeout is as low as can be and the remote socket isn't expected to be open anyway
			}
			catch (ConnectException e) { 
				if (StringUtils.contains(e.getMessage(), ENETUNREACH)) {
					// TCP: host unreachable
					return new KnockResult(false, e.getMessage()); 
				}
				// ok otherwise
			}
			catch (UnknownHostException e) {
				// TCP: unable to resolve hostname
				return new KnockResult(false, e.getMessage()); 
			}
			catch (IllegalArgumentException e) {
				// UDP: unable to resolve hostname
				return new KnockResult(false, e.getMessage()); 
			}
			catch (SocketException e) {
				// UDP: host unreachable
				return new KnockResult(false, e.getMessage());
			}
			catch (IOException e) {
				return new KnockResult(false, e.getMessage());
			}
			finally {
				SocketUtils.closeQuietly(socket);
				SocketUtils.closeQuietly(datagramSocket);
			}

			try { Thread.sleep(host.getDelay()); }
			catch (InterruptedException e) { }
		}

		return new KnockResult(true, null);
	}

	public static class KnockResult {
		private final boolean success;
		private final String error;
		
		public KnockResult(boolean success, String error) {
			this.success = success;
			this.error = error;
		}
		
		public boolean isSuccess() { return success; }
		public String getError() { return error; }
	}
	
}
