package com.xargsgrep.portknocker.utils;

import android.os.Environment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.xargsgrep.portknocker.model.Host;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SerializationUtils
{
    private static final String FILE_NAME = "hosts.json";

    public static void serializeHosts(List<Host> hosts)
    {
        if (!isExternalStorageAccessible())
        {
            throw new RuntimeException("External storage is not currently accessible.");
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try
        {
            File storageDir = new File(Environment.getExternalStorageDirectory(), "PortKnocker");
            if (!storageDir.exists())
            {
                storageDir.mkdirs();
            }
            FileWriter fileWriter = new FileWriter(new File(storageDir, FILE_NAME));
            mapper.writeValue(fileWriter, hosts);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static List<Host> deserializeHosts()
    {
        if (!isExternalStorageAccessible())
        {
            throw new RuntimeException("External storage is not currently accessible.");
        }

        ObjectMapper objectMapper = new ObjectMapper();

        try
        {
            File storageDir = new File(Environment.getExternalStorageDirectory(), "PortKnocker");
            return objectMapper.readValue(new File(storageDir, FILE_NAME), new TypeReference<List<Host>>() {});
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static boolean isExternalStorageAccessible()
    {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
