package org.librairy.eval.corpus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.librairy.eval.model.DirichletDistribution;
import org.librairy.service.modeler.facade.rest.model.ShapeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
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

    public void createVectors(Integer numArticles){
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new URL(CORPUS_URL).openStream())));

            File outputFile1 = new File("src/main/resources/wiki1M_20news.jsonl.gz");
            if (outputFile1.exists()) outputFile1.delete();
            BufferedWriter writer1 = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(outputFile1, false))));

            File outputFile2 = new File("src/main/resources/wiki1M_wiki200.jsonl.gz");
            if (outputFile2.exists()) outputFile2.delete();
            BufferedWriter writer2 = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(outputFile2, false))));


            String line;
            ObjectMapper jsonMapper = new ObjectMapper();
            AtomicInteger counter = new AtomicInteger();
            AtomicInteger it = new AtomicInteger();
            Integer interval = numArticles < 1000? 100 : numArticles / 1000;


            ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 0l, TimeUnit.MILLISECONDS, new LinkedBlockingDeque(10), new ThreadPoolExecutor.CallerRunsPolicy());
            while(!Strings.isNullOrEmpty(line = reader.readLine()) && counter.get() < numArticles ){

                com.fasterxml.jackson.databind.JsonNode json = jsonMapper.readTree(line);

                executor.submit(() -> {
                    try{
                        String text = json.get("text").asText();
                        String url  = json.get("url").asText();
                        if (text.length() < 2000) return;

                        if ((counter.incrementAndGet()) % interval == 0) LOG.info("Added " + (counter.get()) + " docs");

                        ShapeRequest request = new ShapeRequest();
                        request.setText(text);

                        if (counter.get() <= numArticles){
                            writer1.write(jsonMapper.writeValueAsString(newDistribution(url, Unirest.post("http://localhost:8080/shape").body(request).asJson()))+"\n");
                            writer2.write(jsonMapper.writeValueAsString(newDistribution(url, Unirest.post("http://localhost:8081/shape").body(request).asJson()))+"\n");
                        }

                    }catch (Exception e){
                        LOG.error("Error reading document",e);
                    }

                });

            }

            LOG.info("waiting to finish..");
            executor.shutdown();
            executor.awaitTermination(1,TimeUnit.HOURS);

            writer1.close();
            writer2.close();
            LOG.info((counter.get()) + " documents added");


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
