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
package com.xargsgrep.portknocker.utils;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;

public class SocketUtils
{
    public static void closeQuietly(Socket socket)
    {
        try
        {
            if (socket != null && socket.isConnected()) socket.close();
        }
        catch (IOException e) { }
    }

    public static void closeQuietly(DatagramSocket socket)
    {
        if (socket != null && socket.isConnected()) socket.close();
    }
}
