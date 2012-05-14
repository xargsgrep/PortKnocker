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
package com.xargsgrep.portknocker.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.model.Application;

public class ApplicationArrayAdapter extends ArrayAdapter<Application> {
	
	Context context;
	List<Application> applications;

	public ApplicationArrayAdapter(Context context, List<Application> applications) {
		super(context, -1, applications);
		this.context = context;
		this.applications = applications;
	}
	
	@Override
	public int getCount() {
		return applications.size();
	}
	
	@Override
	public Application getItem(int position) {
		return applications.get(position);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getView(position, convertView);
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getView(position, convertView);
	}
	
	private View getView(int position, View convertView) {
		View view = convertView;
		if (view == null) view = LayoutInflater.from(getContext()).inflate(R.layout.icon_text_item, null);
		
		ImageView imageView = (ImageView) view.findViewById(R.id.icon);
		TextView textView = (TextView) view.findViewById(R.id.text);
		
		Application application = applications.get(position);
        //imageView.setImageDrawable(application.getIcon() == null ? context.getResources().getDrawable(R.drawable.ic_launcher) : application.getIcon());
        imageView.setImageDrawable(application.getIcon() == null ? null : application.getIcon());
		textView.setText(application.getLabel());
		
		return view;
	}
}
