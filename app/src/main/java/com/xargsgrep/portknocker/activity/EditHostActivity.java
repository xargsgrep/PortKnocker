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
package com.xargsgrep.portknocker.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.adapter.PortArrayAdapter;
import com.xargsgrep.portknocker.db.DatabaseManager;
import com.xargsgrep.portknocker.fragment.HostFragment;
import com.xargsgrep.portknocker.fragment.MiscFragment;
import com.xargsgrep.portknocker.fragment.PortsFragment;
import com.xargsgrep.portknocker.model.Host;
import com.xargsgrep.portknocker.model.Port;
import com.xargsgrep.portknocker.utils.BundleUtils;
import com.xargsgrep.portknocker.utils.StringUtils;
import com.xargsgrep.portknocker.widget.HostWidget;

import java.util.List;

public class EditHostActivity extends ActionBarActivity implements ActionBar.TabListener
{
    public static final int MENU_ITEM_SAVE = 2;
    public static final int MENU_ITEM_ADD_PORT = 3;
    public static final int MENU_ITEM_DEBUG_INFO = 4;

    public static final int TAB_INDEX_HOST = 0;
    public static final int TAB_INDEX_PORTS = 1;
    public static final int TAB_INDEX_MISC = 2;

    public static final String KEY_HOST_ID = "hostId";
    public static final String KEY_SELECTED_TAB_INDEX = "selectedTabIndex";
    public static final String KEY_SAVE_HOST_RESULT = "saveHostResult";
    public static final String KEY_SHOW_CANCEL_DIALOG = "showCancelDialog";

    private static final int MAX_PORT_VALUE = 65535;

    private DatabaseManager databaseManager;
    private AlertDialog cancelDialog;
    // null when creating a new host
    private Long hostId;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_edit);

        databaseManager = new DatabaseManager(this);

        Bundle extras = getIntent().getExtras();
        hostId = (BundleUtils.contains(extras, KEY_HOST_ID)) ? extras.getLong(KEY_HOST_ID) : null;
        Host host = (hostId == null) ? null : databaseManager.getHost(hostId);

        if (host != null) getSupportActionBar().setSubtitle(host.getLabel());
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        addTab(getString(R.string.host_tab_name));
        addTab(getString(R.string.ports_tab_name));
        addTab(getString(R.string.misc_tab_name));

        if (savedInstanceState != null)
        {
            getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt(KEY_SELECTED_TAB_INDEX));
            if (savedInstanceState.getBoolean(KEY_SHOW_CANCEL_DIALOG)) showCancelDialog();
        }
    }

    private void addTab(String text)
    {
        ActionBar.Tab tab = getSupportActionBar().newTab();
        tab.setText(text);
        tab.setTabListener(this);
        getSupportActionBar().addTab(tab);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
    {
        Fragment hostFragment = getSupportFragmentManager().findFragmentByTag(HostFragment.TAG);
        Fragment portsFragment = getSupportFragmentManager().findFragmentByTag(PortsFragment.TAG);
        Fragment miscFragment = getSupportFragmentManager().findFragmentByTag(MiscFragment.TAG);

        switch (tab.getPosition())
        {
            case TAB_INDEX_HOST:
                if (hostFragment == null)
                {
                    hostFragment = HostFragment.newInstance(hostId);
                    ft.add(R.id.fragment_content, hostFragment, HostFragment.TAG);
                }
                ft.show(hostFragment);
                if (portsFragment != null) ft.hide(portsFragment);
                if (miscFragment != null) ft.hide(miscFragment);
                break;
            case TAB_INDEX_PORTS:
                if (portsFragment == null)
                {
                    portsFragment = PortsFragment.newInstance(hostId);
                    ft.add(R.id.fragment_content, portsFragment, PortsFragment.TAG);
                }
                ft.show(portsFragment);
                if (hostFragment != null) ft.hide(hostFragment);
                if (miscFragment != null) ft.hide(miscFragment);
                break;
            case TAB_INDEX_MISC:
                if (miscFragment == null)
                {
                    miscFragment = MiscFragment.newInstance(hostId);
                    ft.add(R.id.fragment_content, miscFragment, MiscFragment.TAG);
                }
                ft.show(miscFragment);
                if (hostFragment != null) ft.hide(hostFragment);
                if (portsFragment != null) ft.hide(portsFragment);
                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        showCancelDialog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuItem save = menu.add(Menu.NONE, MENU_ITEM_SAVE, 2, "Save").setIcon(R.drawable.ic_menu_save);
        MenuItem debugInfo = menu.add(Menu.NONE, MENU_ITEM_DEBUG_INFO, 3, "Debug Info");

        MenuItemCompat.setShowAsAction(save, MenuItem.SHOW_AS_ACTION_ALWAYS);
        MenuItemCompat.setShowAsAction(debugInfo, MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                showCancelDialog();
                return true;
            case MENU_ITEM_SAVE:
                saveHost();
                return true;
            case MENU_ITEM_DEBUG_INFO:
                Toast.makeText(this, "Host ID: " + hostId, Toast.LENGTH_LONG).show();
                return false;
            default:
                // so PortsFragment.onOptionsItemSelected methods get called
                return false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SELECTED_TAB_INDEX, getSupportActionBar().getSelectedNavigationIndex());
        outState.putBoolean(KEY_SHOW_CANCEL_DIALOG, (cancelDialog != null && cancelDialog.isShowing()));
    }

    @Override
    protected void onDestroy()
    {
        if (cancelDialog != null && cancelDialog.isShowing()) cancelDialog.dismiss();
        super.onDestroy();
    }

    private void saveHost()
    {
        HostFragment hostFragment = (HostFragment) getSupportFragmentManager().findFragmentByTag(HostFragment.TAG);
        PortsFragment portsFragment = (PortsFragment) getSupportFragmentManager().findFragmentByTag(PortsFragment.TAG);
        MiscFragment miscFragment = (MiscFragment) getSupportFragmentManager().findFragmentByTag(MiscFragment.TAG);

        Host host = (hostId == null) ? new Host() : databaseManager.getHost(hostId);

        host.setLabel(hostFragment.getHostLabelEditText().getText().toString());
        host.setHostname(hostFragment.getHostnameEditText().getText().toString());

        if (portsFragment != null)
        {
            // could be null if user saves without going to ports tab
            // hackish, but if user clicks save while focused in a port EditText it won't get saved because that's done onFocusChange
            portsFragment.clearFoci();
            host.getPorts().clear();

            List<Port> ports = ((PortArrayAdapter) portsFragment.getListAdapter()).getPorts();
            for (Port port : ports)
            {
                if (port.getPort() > -1) host.getPorts().add(port);
            }
        }

        if (miscFragment != null)
        {
            // could be null if user saves without going to misc tab
            int delay = miscFragment.getDelaySeekBar().getProgress();
            host.setDelay(delay);

            int tcpConnectTimeout = miscFragment.getTcpConnectTimeoutSeekBar().getProgress();
            host.setTcpConnectTimeout(tcpConnectTimeout);

            String launchIntent = miscFragment.getSelectedLaunchIntent();
            host.setLaunchIntentPackage(launchIntent);
        }

        boolean isValid = validateAndDisplayErrors(host);
        if (isValid)
        {
            boolean saveResult;
            if (hostId == null)
            {
                saveResult = databaseManager.saveHost(host);
            }
            else
            {
                saveResult = databaseManager.updateHost(host);
                HostWidget.updateAllAppWidgetsForHost(this, hostId);
            }

            returnToHostListActivity(saveResult);
        }
    }

    private boolean validateAndDisplayErrors(Host host)
    {
        String errorText = "";
        if (StringUtils.isBlank(host.getLabel()))
        {
            errorText = getString(R.string.toast_msg_enter_label);
        }
        else if (StringUtils.isBlank(host.getHostname()))
        {
            errorText = getString(R.string.toast_msg_enter_hostname);
        }
        else if (host.getPorts() == null || host.getPorts().size() == 0)
        {
            errorText = getString(R.string.toast_msg_enter_port);
        }
        else
        {
            for (Port port : host.getPorts())
            {
                if (port.getPort() > MAX_PORT_VALUE)
                {
                    errorText = getString(R.string.toast_msg_invalid_port) + port.getPort();
                    break;
                }
            }
        }

        if (StringUtils.isNotBlank(errorText))
        {
            Toast toast = Toast.makeText(this, errorText, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            return false;
        }

        return true;
    }

    private void returnToHostListActivity(Boolean saveResult)
    {
        Intent hostListIntent = new Intent(this, HostListActivity.class);
        hostListIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (saveResult != null) hostListIntent.putExtra(KEY_SAVE_HOST_RESULT, saveResult);
        startActivity(hostListIntent);
    }

    private void showCancelDialog()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(R.string.confirm_dialog_cancel_edit_title);
        dialogBuilder.setIcon(R.drawable.ic_dialog_confirm);

        dialogBuilder.setPositiveButton(
                R.string.confirm_dialog_confirm,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        returnToHostListActivity(null);
                    }
                }
        );
        dialogBuilder.setNegativeButton(
                R.string.confirm_dialog_cancel,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which) { }
                }
        );

        cancelDialog = dialogBuilder.create();
        cancelDialog.show();
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) { }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) { }
}
