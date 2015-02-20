package com.xargsgrep.portknocker.utils;

import android.os.Environment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.xargsgrep.portknocker.model.Host;

import java.io.File;
import java.io.FileWriter;
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

        File storageDir = new File(Environment.getExternalStorageDirectory(), "PortKnocker");
        if (!storageDir.exists())
        {
            storageDir.mkdirs();
        }

        File file = new File(storageDir, fileName);
        FileWriter fileWriter = new FileWriter(file);

        mapper.writeValue(fileWriter, hosts);

        return file.getAbsolutePath();
    }

    public static List<Host> deserializeHosts(String filePath) throws Exception
    {
        if (!isExternalStorageAccessible())
        {
            throw new Exception("External storage is not currently accessible");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(filePath), new TypeReference<List<Host>>() {});
    }

    private static boolean isExternalStorageAccessible()
    {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
