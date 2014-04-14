package com.xargsgrep.portknocker.model;

import android.graphics.drawable.Drawable;

public class Application {
	
	private String label;
	private Drawable icon;
	private String intent;
    	
	public Application(String label, Drawable icon, String intent) {
		this.label = label;
		this.icon = icon;
		this.intent = intent;
	}

	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
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
