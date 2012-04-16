package com.xargsgrep.portknocker.manager;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TabHost;

public class TabManager implements TabHost.OnTabChangeListener {
    private final FragmentActivity activity;
    private final TabHost tabHost;
    private final int containerId;
    private final Map<String, TabInfo> tabs = new HashMap<String, TabInfo>();
    private TabInfo lastTab;

    public TabManager(FragmentActivity activity, TabHost tabHost, int containerId) {
        this.activity = activity;
        this.tabHost = tabHost;
        this.containerId = containerId;
        this.tabHost.setOnTabChangedListener(this);
    }

    @Override
    public void onTabChanged(String tabId) {
        TabInfo newTab = tabs.get(tabId);
        if (lastTab != newTab) {
            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            if (lastTab != null) {
                if (lastTab.fragment != null) {
                    ft.detach(lastTab.fragment);
                }
            }
            if (newTab != null) {
                if (newTab.fragment == null) {
                    newTab.fragment = Fragment.instantiate(activity, newTab.clazz.getName(), newTab.args);
                    ft.add(containerId, newTab.fragment, newTab.tag);
                } else {
                    ft.attach(newTab.fragment);
                }
            }

            lastTab = newTab;
            ft.commit();
            activity.getSupportFragmentManager().executePendingTransactions();
        }
    }
    
    public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
        tabSpec.setContent(new DummyTabFactory(activity));
        String tag = tabSpec.getTag();

        TabInfo info = new TabInfo(tag, clss, args);

        // Check to see if we already have a fragment for this tab, probably
        // from a previously saved state.  If so, deactivate it, because our
        // initial state is that a tab isn't shown.
        info.fragment = activity.getSupportFragmentManager().findFragmentByTag(tag);
        if (info.fragment != null && !info.fragment.isDetached()) {
            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            ft.detach(info.fragment);
            ft.commit();
        }

        tabs.put(tag, info);
        tabHost.addTab(tabSpec);
    }

    static final class TabInfo {
        private final String tag;
        private final Class<?> clazz;
        private final Bundle args;
        private Fragment fragment;

        TabInfo(String tag, Class<?> clazz, Bundle args) {
            this.tag = tag;
            this.clazz = clazz;
            this.args = args;
        }
    }

    static class DummyTabFactory implements TabHost.TabContentFactory {
        private final Context context;

        public DummyTabFactory(Context context) {
            this.context = context;
        }

        @Override
        public View createTabContent(String tag) {
            View v = new View(context);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
    }

}