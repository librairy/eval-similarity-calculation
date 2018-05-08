package org.librairy.eval.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class  JsonlReader<T> {

    private static final Logger LOG = LoggerFactory.getLogger(JsonlReader.class);

    private BufferedReader reader;
    private ObjectMapper jsonMapper = new ObjectMapper();
    private Class<T> type;

    public JsonlReader(File jsonFile, Class<T> type) throws IOException {
        this.reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(jsonFile))));
        this.type = type;
    }

    public List<T> read(File jsonlFile) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(jsonlFile))));

        List<T> elements = new ArrayList<>();
        String line;
        AtomicInteger counter = new AtomicInteger();
        while((line = reader.readLine()) != null){
            elements.add(jsonMapper.readValue(line,type));
            if (counter.incrementAndGet() % 100 == 0 ) LOG.info("parsed " + counter.get() + " elements from: " + jsonlFile.getName());
        }

        reader.close();
        return elements;
    }

    public Optional<T> next() throws IOException {
        String line;
        if ((line = reader.readLine()) == null){
            reader.close();
            return Optional.empty();
        }
        return Optional.of(jsonMapper.readValue(line,type));
    }

}
