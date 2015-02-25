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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.activity.EditHostActivity;
import com.xargsgrep.portknocker.fragment.HostFragment;
import com.xargsgrep.portknocker.fragment.MiscFragment;
import com.xargsgrep.portknocker.fragment.PortsFragment;

public class EditHostFragmentPagerAdapter extends FragmentPagerAdapter
{
    private Context context;
    private Long hostId;

    public EditHostFragmentPagerAdapter(FragmentManager fm, Context context, Long hostId)
    {
        super(fm);
        this.context = context;
        this.hostId = hostId;
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case EditHostActivity.TAB_INDEX_HOST:
                return HostFragment.newInstance(hostId);
            case EditHostActivity.TAB_INDEX_PORTS:
                return PortsFragment.newInstance(hostId);
            case EditHostActivity.TAB_INDEX_MISC:
                return MiscFragment.newInstance(hostId);
        }
        throw new IllegalArgumentException("Invalid item position in ViewPager: " + position);
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case EditHostActivity.TAB_INDEX_HOST:
                return context.getString(R.string.host_tab_name);
            case EditHostActivity.TAB_INDEX_PORTS:
                return context.getString(R.string.ports_tab_name);
            case EditHostActivity.TAB_INDEX_MISC:
                return context.getString(R.string.misc_tab_name);
        }
        throw new IllegalArgumentException("Invalid item position in ViewPager: " + position);
    }

    @Override
    public int getCount()
    {
        return 3;
    }
}
