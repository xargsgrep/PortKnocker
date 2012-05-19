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
package com.xargsgrep.portknocker.model;

import java.util.ArrayList;
import java.util.List;

public class Host {
	
	public static final int DEFAULT_DELAY = 1000;
	
	private long id;
	private String label;
	private String hostname;
	private int delay = DEFAULT_DELAY;
	private String launchIntentPackage;
	private List<Port> ports = new ArrayList<Port>();
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public int getDelay() {
		return delay;
	}
	public void setDelay(int delay) {
		this.delay = delay;
	}
	public String getLaunchIntentPackage() {
		return launchIntentPackage;
	}
	public void setLaunchIntentPackage(String launchIntentPackage) {
		this.launchIntentPackage = launchIntentPackage;
	}
	public List<Port> getPorts() {
		return ports;
	}
	public void setPorts(List<Port> ports) {
		this.ports = ports;
	}
	
	public String getPortsString() {
		StringBuilder portsString = new StringBuilder();
		
		if (ports.size() > 0) {
			for (Port port : ports) {
				portsString.append(port.getPort());
				portsString.append(":");
				portsString.append(port.getProtocol());
				portsString.append(", ");
			}
			portsString.replace(portsString.length()-2, portsString.length(), "");
		}
		
		return portsString.toString();
	}
}
