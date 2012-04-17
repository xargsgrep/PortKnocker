package com.xargsgrep.portknocker.listener;

import android.view.View;
import android.view.View.OnClickListener;

public abstract class PositionOnClickListener implements OnClickListener {
	
	protected int position;
	
	public PositionOnClickListener(int position) {
		this.position = position;
	}
	
	@Override
	public abstract void onClick(View v);

}
