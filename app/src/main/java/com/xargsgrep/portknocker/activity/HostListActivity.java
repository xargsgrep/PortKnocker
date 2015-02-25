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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.xargsgrep.portknocker.R;
import com.xargsgrep.portknocker.asynctask.KnockerAsyncTask;
import com.xargsgrep.portknocker.db.DatabaseManager;
import com.xargsgrep.portknocker.filter.FilenameInputFilter;
import com.xargsgrep.portknocker.fragment.HostListFragment;
import com.xargsgrep.portknocker.fragment.SettingsFragment;
import com.xargsgrep.portknocker.model.Host;
import com.xargsgrep.portknocker.utils.BundleUtils;
import com.xargsgrep.portknocker.utils.SerializationUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HostListActivity extends ActionBarActivity
{
    private static final int FILE_CHOOSER_REQUEST_CODE = 1000;
    private static final String KEY_SHOW_DELETE_DIALOG = "showDeleteDialog";
    private static final SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US);
    private static final InputFilter FILENAME_INPUT_FILTER = new FilenameInputFilter();

    private DatabaseManager databaseManager;
    private AlertDialog deleteDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseManager = new DatabaseManager(this);

        getSupportActionBar().setHomeButtonEnabled(false);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment hostListFragment = getSupportFragmentManager().findFragmentByTag(HostListFragment.TAG);
        if (hostListFragment == null)
        {
            hostListFragment = HostListFragment.newInstance();
            ft.add(R.id.fragment_content, hostListFragment, HostListFragment.TAG);
        }
        ft.show(hostListFragment);
        ft.commit();

        Bundle extras = getIntent().getExtras();
        if (BundleUtils.contains(extras, EditHostActivity.KEY_SAVE_HOST_RESULT) && savedInstanceState == null)
        {
            Boolean saveResult = extras.getBoolean(EditHostActivity.KEY_SAVE_HOST_RESULT);
            Toast.makeText(this, getResources().getString(saveResult ? R.string.toast_msg_save_success : R.string.toast_msg_save_failure), Toast.LENGTH_SHORT).show();
        }

        if (BundleUtils.contains(extras, "hostId") && savedInstanceState == null)
        {
            // clicked on widget
            Long hostId = extras.getLong("hostId");
            Host host = databaseManager.getHost(hostId);

            KnockerAsyncTask knockerAsyncTask = new KnockerAsyncTask(this, host.getPorts().size());
            knockerAsyncTask.execute(host);
        }
        else if (savedInstanceState != null)
        {
            if (savedInstanceState.getBoolean(KEY_SHOW_DELETE_DIALOG))
            {
                ((HostListFragment) hostListFragment).showDeleteDialog();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.host_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Fragment hostListFragment = getSupportFragmentManager().findFragmentByTag(HostListFragment.TAG);

        switch (item.getItemId())
        {
            case R.id.menu_item_settings:
                Intent settingsIntent;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
                {
                    // PreferenceFragment is not part of the compatibility package
                    settingsIntent = new Intent(this, SettingsActivityCompat.class);
                }
                else
                {
                    settingsIntent = new Intent(this, SettingsActivity.class);
                    settingsIntent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
                    settingsIntent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsFragment.class.getName());
                }
                startActivity(settingsIntent);
                return true;
            case R.id.menu_item_export:
                showExportHostDialog();
                return true;
            case R.id.menu_item_import:
                Intent getContentIntent = FileUtils.createGetContentIntent();
                Intent intent = Intent.createChooser(getContentIntent, "Select a file");
                startActivityForResult(intent, FILE_CHOOSER_REQUEST_CODE);
                return true;
            case R.id.menu_item_sort_hostname:
                ((HostListFragment) hostListFragment).sortByHostname();
                return true;
            case R.id.menu_item_sort_label:
                ((HostListFragment) hostListFragment).sortByLabel();
                return true;
            case R.id.menu_item_sort_newest:
                ((HostListFragment) hostListFragment).sortByNewest();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showExportHostDialog()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Export Hosts");
        alert.setMessage("Enter filename");

        final EditText input = new EditText(this);
        input.setText(String.format("hosts-%s.json", FILE_DATE_FORMAT.format(new Date())));
        input.setFilters(new InputFilter[] {FILENAME_INPUT_FILTER});

        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                String value = input.getText().toString();
                List<Host> hosts = databaseManager.getAllHosts();
                try
                {
                    String filePath = SerializationUtils.serializeHosts(value, hosts);
                    showToast("Exported hosts to file: " + filePath);
                }
                catch (Exception e)
                {
                    showToast("Exporting hosts failed: " + e.getMessage());
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton) { }
        });

        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case FILE_CHOOSER_REQUEST_CODE:
                if (resultCode == RESULT_OK)
                {
                    Uri uri = data.getData();
                    String filePath = FileUtils.getPath(this, uri);

                    try
                    {
                        List<Host> hosts = SerializationUtils.deserializeHosts(filePath);
                        for (Host host : hosts)
                        {
                            databaseManager.saveHost(host);
                        }

                        Fragment hostListFragment = getSupportFragmentManager().findFragmentByTag(HostListFragment.TAG);
                        ((HostListFragment) hostListFragment).refreshHosts();

                        showToast("Imported hosts from file: " + filePath);
                    }
                    catch (Exception e)
                    {
                        showToast("Importing hosts failed: " + e.getMessage());
                    }
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_SHOW_DELETE_DIALOG, (deleteDialog != null && deleteDialog.isShowing()));
    }

    @Override
    protected void onDestroy()
    {
        if (deleteDialog != null && deleteDialog.isShowing()) deleteDialog.dismiss();
        super.onDestroy();
    }

    public void setDeleteDialog(AlertDialog deleteDialog)
    {
        this.deleteDialog = deleteDialog;
    }

    private void showToast(String text)
    {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}
