package com.xargsgrep.portknocker.model;

public class Application {
	private String label;
	private String intent;
    	
	public Application(String label, String intent) {
		this.label = label;
		this.intent = intent;
	}

	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getIntent() {
		return intent;
	}
	public void setIntent(String intent) {
		this.intent = intent;
	}
}
