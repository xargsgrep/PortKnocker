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

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.xargsgrep.portknocker.utils.SerializationUtils.createPortKnockerFolderIfNotExists;

public class HostListActivity extends ActionBarActivity
{
    private static final int FILE_CHOOSER_REQUEST_CODE = 1000;
    private static final String KEY_SHOW_DELETE_DIALOG = "showDeleteDialog";
    private static final SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US);
    private static final InputFilter FILENAME_INPUT_FILTER = new FilenameInputFilter();

    private DatabaseManager databaseManager;
    private AlertDialog deleteDialog;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private boolean readWritePermissionsGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseManager = new DatabaseManager(this);

        getSupportActionBar().setHomeButtonEnabled(false);

        verifyStoragePermissions();

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

        // Check if this application was started by an intent.
        checkForActionViewIntent();
    }

    private void checkForActionViewIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();

        if (action!=null && Arrays.asList(Intent.ACTION_VIEW, Intent.ACTION_EDIT).contains(action))
        {
            if(readWritePermissionsGranted) {
                Uri uri = intent.getData();

                if (uri.getScheme().equals("content")) {
                    String filename = Environment.getExternalStorageDirectory() + "/PortKnocker/" + getFileName(uri);
                    try {
                        ContentResolver resolver = getContentResolver();
                        InputStream input = resolver.openInputStream(uri);
                        // Read in the content stream into local file to be able
                        // to open it in import function
                        InputStreamToFile(input, filename);
                        importHostsFromFullyQualifiedFilename(filename);
                    } catch (FileNotFoundException io) {
                        showToast(getString(R.string.toast_msg_import_error) + filename);
                    }
                } else {
                    String filename = uri.getPath();
                    importHostsFromFullyQualifiedFilename(filename);
                    // Make sure we don't read in again on device canvas change
                }
                intent.setAction(Intent.ACTION_MAIN);
            } else {
                showToast(getString(R.string.toast_msg_no_permission_for_external_mem_access));
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    private void InputStreamToFile(InputStream in, String file) {
        try {
            File storageFolder = createPortKnockerFolderIfNotExists();
            OutputStream out = new FileOutputStream(new File(file));
            int size = 0;
            byte[] buffer = new byte[1024];

            while ((size = in.read(buffer)) != -1) {
                out.write(buffer, 0, size);
            }
            out.close();
        }
        catch (Exception e) {
            Log.e("HostListActivity", "InputStreamToFile exception: " + e.getMessage());
        }
    }

    private void importHostsFromFullyQualifiedFilename(String filePath) {
        try
        {
            List<Host> hosts = SerializationUtils.deserializeHosts(filePath);
            for (Host host : hosts)
            {
                databaseManager.saveHost(host);
            }

            Fragment hostListFragment = getSupportFragmentManager().findFragmentByTag(HostListFragment.TAG);
            if(hostListFragment != null) {
                ((HostListFragment) hostListFragment).refreshHosts();
            }
            showToast(getString(R.string.toast_msg_import_from_file_success) + filePath);
        }
        catch (Exception e)
        {
            showToast(getString(R.string.toast_msg_import_error) + e.getMessage());
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
                if(readWritePermissionsGranted)
                    showExportHostDialog();
                else
                    showToast(getString(R.string.toast_msg_no_permission_for_external_mem_access));
                return true;
            case R.id.menu_item_import:
                if(readWritePermissionsGranted) {
                    Intent getContentIntent = FileUtils.createGetContentIntent();
                    Intent intent = Intent.createChooser(getContentIntent, "Select a file");
                    startActivityForResult(intent, FILE_CHOOSER_REQUEST_CODE);
                }
                else
                    showToast(getString(R.string.toast_msg_no_permission_for_external_mem_access));
                return true;
            case R.id.menu_item_send:
                sendHostsToOtherApp();
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

        alert.setTitle(R.string.export_host_dialog_title);
        alert.setMessage(R.string.export_host_dialog_message);

        final EditText input = new EditText(this);
        input.setText(String.format("hosts-%s.json", FILE_DATE_FORMAT.format(new Date())));
        input.setFilters(new InputFilter[] {FILENAME_INPUT_FILTER});

        alert.setView(input);

        alert.setPositiveButton(R.string.export_host_dialog_ok_button, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                String value = input.getText().toString();
                List<Host> hosts = databaseManager.getAllHosts();
                try
                {
                    String filePath = SerializationUtils.serializeHosts(value, hosts);
                    showToast(getString(R.string.export_host_dialog_export_complete) + filePath);
                }
                catch (Exception e)
                {
                    showToast(getString(R.string.export_host_dialog_export_failure) + e.getMessage());
                }
            }
        });

        alert.setNegativeButton(R.string.export_host_dialog_cancel_button, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton) { }
        });

        alert.show();
    }

    private void sendHostsToOtherApp() {
        List<Host> hosts = databaseManager.getAllHosts();
        try {
            // Sharing per e-mail app but also others works reliable only by using a file
            String path = SerializationUtils.serializeHosts("PortKnockerConfig.json", hosts);

            Intent shareIntent = new Intent();
            Uri uriToCsvFile = FileProvider.getUriForFile(this, "com.xargsgrep.portknocker.fileprovider", new File(path));
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uriToCsvFile);
            shareIntent.setType("application/octet-stream");

            startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_dialog_chooser_title)));
        }
        catch (Exception e)
        {
            showToast(getString(R.string.export_host_dialog_export_failure) + e.getMessage());
        }
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
                    String filename = uri.getPath();
                    importHostsFromFullyQualifiedFilename(filename);
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void verifyStoragePermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
            readWritePermissionsGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readWritePermissionsGranted = true;
                } else {
                    readWritePermissionsGranted = false;
                }
                return;
            }
        }
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
