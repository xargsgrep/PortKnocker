/*
 *  Copyright 2014 Ahsan Rabbani
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xargsgrep.portknocker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.model.Application;

import java.util.List;

public class ApplicationArrayAdapter extends ArrayAdapter<Application>
{
    private Context context;
    private List<Application> applications;

    public ApplicationArrayAdapter(Context context, List<Application> applications)
    {
        super(context, -1, applications);
        this.context = context;
        this.applications = applications;
    }

    @Override
    public int getCount()
    {
        return applications.size();
    }

    @Override
    public Application getItem(int position)
    {
        return applications.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return getView(position, convertView);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        return getView(position, convertView);
    }

    private View getView(int position, View convertView)
    {
        View view = convertView;
        if (view == null)
        {
            view = LayoutInflater.from(getContext()).inflate(R.layout.icon_text_item, null);
        }

        ImageView imageView = (ImageView) view.findViewById(R.id.icon);
        TextView textView = (TextView) view.findViewById(R.id.text);

        Application application = applications.get(position);
//        imageView.setImageDrawable(application.getIcon() == null ? context.getResources().getDrawable(R.drawable.ic_launcher) : application.getIcon());
        imageView.setImageDrawable(application.getIcon() == null ? null : application.getIcon());
        textView.setText(application.getLabel());

        return view;
    }
}
