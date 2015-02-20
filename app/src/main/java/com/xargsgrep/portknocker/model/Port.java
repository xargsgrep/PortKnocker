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

public class Port
{
    private long hostId;
    private int index;
    private int port = -1;
    private Protocol protocol = Protocol.TCP;

    public static enum Protocol {TCP, UDP}

    public Port() { }

    @JsonIgnore
    public long getHostId()
    {
        return hostId;
    }

    public void setHostId(long hostId)
    {
        this.hostId = hostId;
    }

    @JsonIgnore
    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public Protocol getProtocol()
    {
        return protocol;
    }

    public void setProtocol(Protocol protocol)
    {
        this.protocol = protocol;
    }
}
