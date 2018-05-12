package org.librairy.eval.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class JsonlWriter<T> {

    private static final Logger LOG = LoggerFactory.getLogger(JsonlWriter.class);
    private final File file;

    private BufferedWriter writer;
    private ObjectMapper jsonMapper = new ObjectMapper();
    private Class<T> type;

    public JsonlWriter(File jsonFile) throws IOException {
        if (jsonFile.exists()) jsonFile.delete();
        if (!jsonFile.getParentFile().exists()) jsonFile.getParentFile().mkdirs();
        this.file = jsonFile;
        this.writer = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(jsonFile))));
    }


    public void write(T data){

        String json = null;
        try {
            json = jsonMapper.writeValueAsString(data);
            writer.write(json+"\n");
        } catch (Exception e) {
            LOG.error("Unexpected error writing to file: " + file.getAbsolutePath(), e);
        }
    }

    public void close(){
        try {
            writer.close();
        } catch (IOException e) {
            LOG.warn("Error closing file: " + file.getAbsolutePath(), e);
        }
    }

}
