/*
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details.
 *
 * Ahsan Rabbani <ahsan@xargsgrep.com>
 *
 */
package com.xargsgrep.portknocker.utils;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;

public class SocketUtils {
	
	public static void closeQuietly(Socket socket) {
		try { if (socket != null && socket.isConnected()) socket.close(); }
		catch (IOException e) { }
	}

	public static void closeQuietly(DatagramSocket socket) {
		if (socket != null && socket.isConnected()) socket.close();
	}
	
}
