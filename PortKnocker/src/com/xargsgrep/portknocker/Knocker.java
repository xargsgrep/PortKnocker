package com.xargsgrep.portknocker;

import java.io.IOException;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

import android.util.Log;

import com.xargsgrep.portknocker.model.Host;
import com.xargsgrep.portknocker.model.Port;
import com.xargsgrep.portknocker.model.Port.Protocol;

public class Knocker {
	
	private static final int TCP_SOCKET_TIMEOUT = 1000;

	public static boolean doKnock(Host host) {
		for (Port port : host.getPorts()) {
			SocketAddress socketAddress = new InetSocketAddress(host.getHostname(), port.getPort());
		
			Socket socket = null;
			DatagramSocket datagramSocket = null;
			try {
				if (port.getProtocol() == Protocol.TCP) {
					Log.d(Knocker.class.toString(), "TCP "+host.getHostname()+":"+port.getPort());
					socket = new Socket();
					socket.connect(socketAddress, TCP_SOCKET_TIMEOUT);
				} else { // PROTOCOL.UDP
					Log.d(Knocker.class.toString(), "UDP "+host.getHostname()+":"+port.getPort());
					datagramSocket = new DatagramSocket();
					datagramSocket.connect(socketAddress);
					byte[] data = new byte[] { 0 };
					datagramSocket.send(new DatagramPacket(data, data.length));
				}
			} catch (ConnectException e) {
				// do nothing
			} catch (SocketException e) {
				return false;
			} catch (IOException e) {
				// do nothing
			} finally {
				closeQuietly(socket);
				closeQuietly(datagramSocket);
			}
			
			try { Thread.sleep(host.getDelay()); } catch (InterruptedException e) { }
		}
		
		return true;
	}
	
	private static void closeQuietly(Socket socket) {
		try {
			if (socket != null && socket.isConnected()) socket.close();
		} catch (IOException e) {}
	}
	
	private static void closeQuietly(DatagramSocket socket) {
		if (socket != null && socket.isConnected()) socket.close();
	}
	
}
