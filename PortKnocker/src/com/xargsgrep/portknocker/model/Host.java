package com.xargsgrep.portknocker.model;

import java.util.ArrayList;
import java.util.List;

public class Host {
	private long id;
	private String label;
	private String hostname;
	private int delay = 0;
	private String launchApp;
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
	public String getLaunchApp() {
		return launchApp;
	}
	public void setLaunchApp(String launchApp) {
		this.launchApp = launchApp;
	}
	public List<Port> getPorts() {
		return ports;
	}
	public void setPorts(List<Port> ports) {
		this.ports = ports;
	}
}
