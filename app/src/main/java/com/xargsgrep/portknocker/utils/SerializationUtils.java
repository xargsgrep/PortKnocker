package com.xargsgrep.portknocker.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.xargsgrep.portknocker.model.Host;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.List;

public class SerializationUtils
{
    public static String serializeHosts(String fileName, List<Host> hosts) throws Exception
    {
        if (!isExternalStorageAccessible())
        {
            throw new Exception("External storage is not currently accessible");
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        File storageDir = createPortKnockerFolderIfNotExists();

        File file = new File(storageDir, fileName);
        FileWriter fileWriter = new FileWriter(file);

        mapper.writeValue(fileWriter, hosts);

        return file.getAbsolutePath();
    }

    @NonNull
    public static File createPortKnockerFolderIfNotExists() {
        File storageDir = new File(Environment.getExternalStorageDirectory(), "PortKnocker");
        if (!storageDir.exists())
        {
            storageDir.mkdirs();
        }
        return storageDir;
    }

    public static List<Host> deserializeHosts(Context context, Uri fileUri) throws Exception
    {
        if (!isExternalStorageAccessible())
        {
            throw new Exception("External storage is not currently accessible");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ContentResolver resolver = context.getContentResolver();
        InputStream input = resolver.openInputStream(fileUri);

        return objectMapper.readValue(input, new TypeReference<List<Host>>() {});
    }

    private static boolean isExternalStorageAccessible()
    {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
