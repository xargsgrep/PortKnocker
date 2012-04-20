package com.xargsgrep.portknocker.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
		View view = convertView;
		if (view == null) view = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, null);
		
		Application application = applications.get(position);
		((TextView) view).setText(application.getLabel());
		
		return view;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) view = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, null);
		
		Application application = applications.get(position);
		((TextView) view).setText(application.getLabel());
		
		return view;
	}

}
