package com.xargsgrep.portknocker.model;

public class Port {

	private long id;
	private long hostId;
	private int index;
	private int port;
	private Protocol protocol = Protocol.TCP;
	
	public static enum Protocol { TCP, UDP };

	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public long getHostId() {
		return hostId;
	}
	
	public void setHostId(long hostId) {
		this.hostId = hostId;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}
	
}
