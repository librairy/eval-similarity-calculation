package org.librairy.eval.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.librairy.service.modeler.facade.rest.model.ShapeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class WikiCorpus {

    private static final Logger LOG = LoggerFactory.getLogger(WikiCorpus.class);

    private static final String CORPUS_URL = "https://delicias.dia.fi.upm.es/nextcloud/index.php/s/4tPyd5Ps51sCuRx/download";


    static{
        Unirest.setDefaultHeader("Accept", "application/json");
        Unirest.setDefaultHeader("Content-Type", "application/json");

        com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
//        jacksonObjectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        jacksonObjectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        Unirest.setObjectMapper(new com.mashape.unirest.http.ObjectMapper() {

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    private final String filePath;

    public WikiCorpus(String filePath) {
        this.filePath = filePath;
    }

    public void createVectors(Integer sampleSize, Integer testSize, Integer parallel){
        BufferedReader reader;
        try {

            InputStream inputStream = (filePath.startsWith("http"))? new URL(CORPUS_URL).openStream() : new FileInputStream(filePath);
            reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(inputStream)));

            File outputFile1 = new File("src/main/resources/sample_20.jsonl.gz");
            if (outputFile1.exists()) outputFile1.delete();
            BufferedWriter writer1 = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(outputFile1, false))));

            File outputFile2 = new File("src/main/resources/sample_200.jsonl.gz");
            if (outputFile2.exists()) outputFile2.delete();
            BufferedWriter writer2 = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(outputFile2, false))));

            File outputFile3 = new File("src/main/resources/test_20.jsonl.gz");
            if (outputFile3.exists()) outputFile3.delete();
            BufferedWriter writer3 = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(outputFile3, false))));

            File outputFile4 = new File("src/main/resources/test_200.jsonl.gz");
            if (outputFile4.exists()) outputFile4.delete();
            BufferedWriter writer4 = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(outputFile4, false))));


            String line;
            ObjectMapper jsonMapper = new ObjectMapper();
            AtomicInteger sampleCounter = new AtomicInteger();
            AtomicInteger testCounter = new AtomicInteger();
            AtomicInteger totalCounter = new AtomicInteger();
            Integer interval = sampleSize < 1000? 100 : sampleSize / 1000;

            try{
                ThreadPoolExecutor executor = new ThreadPoolExecutor(parallel, parallel, 0l, TimeUnit.MILLISECONDS, new LinkedBlockingDeque(10), new ThreadPoolExecutor.CallerRunsPolicy());
                while(!Strings.isNullOrEmpty(line = reader.readLine()) && sampleCounter.get() < sampleSize ){

                    if (sampleCounter.get() > sampleSize && testCounter.get() > testSize) break;

                    com.fasterxml.jackson.databind.JsonNode json = jsonMapper.readTree(line);

                    executor.submit(() -> {
                        try{
                            String text = json.get("text").asText();
                            String url  = json.get("url").asText();
                            if (text.length() < 2000) return;

                            ShapeRequest request = new ShapeRequest();
                            request.setText(text);

                            String newsShape = jsonMapper.writeValueAsString(newDistribution(url, Unirest.post("http://librairy.linkeddata.es/20news-model/shape").body(request).asJson()));
                            String wikiShape = jsonMapper.writeValueAsString(newDistribution(url, Unirest.post("http://librairy.linkeddata.es/wiki-model/shape").body(request).asJson()));

                            if (sampleCounter.get() <= sampleSize){

                                writer1.write(newsShape+"\n");
                                writer2.write(wikiShape+"\n");

                                if ((sampleCounter.incrementAndGet()) % interval == 0) LOG.info("Added " + (sampleCounter.get()) + " sampling docs");
                            }else{
                                writer3.write(newsShape+"\n");
                                writer4.write(wikiShape+"\n");

                                if ((testCounter.incrementAndGet()) % interval == 0) LOG.info("Added " + (sampleCounter.get()) + " testing docs");
                            }

                        }catch (Exception e){
                            LOG.error("Error reading document",e);
                        }

                    });
                    if (totalCounter.incrementAndGet() % 100 == 0) Thread.sleep(20);
                }

                LOG.info("waiting to finish..");
                executor.shutdown();
                executor.awaitTermination(1,TimeUnit.HOURS);
            }catch(Exception e){
                LOG.error("Unexpected error",e);
            }finally{
                writer1.close();
                writer2.close();
                writer3.close();
                writer4.close();
            }

            LOG.info((sampleCounter.get()) + " sampling documents added");
            LOG.info((testCounter.get()) + " testing documents added");


        } catch (Exception e) {
            LOG.error("Unexpected Error", e);
        }
    }

    public DirichletDistribution newDistribution(String url, HttpResponse<JsonNode> response){
        DirichletDistribution dirichletDistribution = new DirichletDistribution();
        dirichletDistribution.setId(url);
        Iterator<Object> iterator = response.getBody().getObject().getJSONArray("vector").iterator();
        List<Double> vector = new ArrayList<>();
        while(iterator.hasNext()){
            Double value = (Double) iterator.next();
            vector.add(value);
        }
        dirichletDistribution.setVector(vector);
        return dirichletDistribution;
    }

}
