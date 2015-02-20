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
package com.xargsgrep.portknocker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class Host
{
    public static final int DEFAULT_DELAY = 1000;
    public static final int DEFAULT_TCP_CONNECT_TIMEOUT = 100;

    private long id;
    private String label;
    private String hostname;
    private int delay = DEFAULT_DELAY;
    private int tcpConnectTimeout = DEFAULT_TCP_CONNECT_TIMEOUT;
    private String launchIntentPackage;
    private List<Port> ports = new ArrayList<>();

    @JsonIgnore
    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getHostname()
    {
        return hostname;
    }

    public void setHostname(String hostname)
    {
        this.hostname = hostname;
    }

    public int getDelay()
    {
        return delay;
    }

    public void setDelay(int delay)
    {
        this.delay = delay;
    }

    public String getLaunchIntentPackage()
    {
        return launchIntentPackage;
    }

    public int getTcpConnectTimeout()
    {
        return tcpConnectTimeout;
    }

    public void setTcpConnectTimeout(int tcpConnectTimeout)
    {
        this.tcpConnectTimeout = tcpConnectTimeout;
    }

    public void setLaunchIntentPackage(String launchIntentPackage)
    {
        this.launchIntentPackage = launchIntentPackage;
    }

    public List<Port> getPorts()
    {
        return ports;
    }

    public void setPorts(List<Port> ports)
    {
        this.ports = ports;
    }

    @JsonIgnore
    public String getPortsString()
    {
        StringBuilder portsString = new StringBuilder();

        if (ports.size() > 0)
        {
            for (Port port : ports)
            {
                portsString.append(port.getPort());
                portsString.append(":");
                portsString.append(port.getProtocol());
                portsString.append(", ");
            }
            portsString.replace(portsString.length() - 2, portsString.length(), "");
        }

        return portsString.toString();
    }
}
