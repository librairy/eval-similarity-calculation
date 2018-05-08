package org.librairy.eval.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class CSVReader {

    private static final Logger LOG = LoggerFactory.getLogger(CSVReader.class);
    private final String separator;

    private BufferedReader reader;

    public CSVReader(File csvFile, String separator) throws IOException {
        this.reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(csvFile))));
        this.separator = separator;
    }

    public Optional<String[]> next() throws IOException {
        String line;
        if ((line = reader.readLine()) == null){
            reader.close();
            return Optional.empty();
        }
        return Optional.of(line.split(separator));
    }

}
