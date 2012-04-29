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
