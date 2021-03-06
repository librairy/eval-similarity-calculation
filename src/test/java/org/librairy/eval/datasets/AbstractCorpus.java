package org.librairy.eval.datasets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.librairy.eval.metrics.JensenShannon;
import org.librairy.eval.model.Neighbour;
import org.librairy.eval.model.Neighbourhood;
import org.librairy.eval.model.Point;
import org.librairy.eval.model.WikiArticle;
import org.librairy.eval.utils.JsonlReader;
import org.librairy.eval.utils.JsonlWriter;
import org.librairy.eval.utils.ParallelExecutor;
import org.librairy.service.modeler.facade.rest.model.ShapeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public abstract class AbstractCorpus {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractCorpus.class);

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

    protected ObjectMapper jsonMapper = new ObjectMapper();

    protected Point shapeToPoint(String url, HttpResponse<JsonNode> shapeResponse){
        Point point = new Point();
        point.setId(url);
        Iterator<Object> iterator = shapeResponse.getBody().getObject().getJSONArray("vector").iterator();
        List<Double> vector = new ArrayList<>();
        while(iterator.hasNext()){
            Double value = (Double) iterator.next();
            vector.add(value);
        }
        point.setVector(vector);
        return point;
    }

    public void create(String baseDir, Integer trainingSize, Integer testSize, Integer topPoints, Integer minTextSize, String endpoint){
        File baseFolder = new File(baseDir);
        if (!baseFolder.exists()) baseFolder.mkdirs();
        JsonlReader<WikiArticle> reader;
        ParallelExecutor executor;
        JsonlWriter<Point> trainingWriter;
        JsonlWriter<Neighbourhood> testWriter;
        ConcurrentHashMap<Point,Neighbourhood> neighbourhoods = new ConcurrentHashMap<>();
        try {
            Properties properties = new Properties();
            FileInputStream input = new FileInputStream("src/test/resources/parameters.properties");
            properties.load(input);

            reader = new JsonlReader<>(new File(properties.getProperty("wiki.path")), WikiArticle.class);

            trainingWriter  = new JsonlWriter<>(Paths.get(baseDir, "training-set.jsonl.gz").toFile());
            testWriter      = new JsonlWriter<>(Paths.get(baseDir, "test-set.jsonl.gz").toFile());

            AtomicInteger trainingCounter = new AtomicInteger();
            AtomicInteger testCounter = new AtomicInteger();

            Integer trainingInterval    = Math.min(trainingSize, 100);
            Integer testInterval        = Math.min(testSize, 100);

            try{
                executor = new ParallelExecutor();
                Optional<WikiArticle> row;

                // Load test points
                while((row = reader.next()).isPresent() && testCounter.get() < testSize){

                    final WikiArticle article = row.get();

                    executor.execute(() -> {
                        try{
                            String text = article.getText();
                            if (text.length() < minTextSize) return;

                            Integer index = testCounter.incrementAndGet();
                            if (index > testSize) return;

                            if (index % testInterval== 0) {
                                LOG.info(testCounter.get() + " test points added");
                                Thread.sleep(20);
                            }

                            HttpResponse<JsonNode> shape = getShape(endpoint, text);
                            if (shape != null){
                                Point point = shapeToPoint(article.getUrl(), shape);
                                neighbourhoods.put(point, new Neighbourhood(point,topPoints));
                            }
                        }catch (Exception e){
                            LOG.error("Error reading document",e);
                            testCounter.decrementAndGet();
                        }

                    });
                }
                executor.pause();

                // Loading training points

                while((row = reader.next()).isPresent() && trainingCounter.get() < trainingSize){

                    final WikiArticle article = row.get();

                    executor.execute(() -> {
                        try{
                            String text = article.getText();
                            if (text.length() < minTextSize) return;

                            Integer index = trainingCounter.incrementAndGet();
                            if (index > trainingSize) return;

                            if (index % trainingInterval == 0) {
                                LOG.info(trainingCounter.get() + " training points added");
                                Thread.sleep(20);
                            }

                            HttpResponse<JsonNode> shape = getShape(endpoint, text);
                            if (shape != null){
                                Point point = shapeToPoint(article.getUrl(), shape);
                                trainingWriter.write(point);

                                for (Point refPoint : neighbourhoods.keySet()){
                                    neighbourhoods.get(refPoint).add(point);
                                }

                            }
                        }catch (Exception e){
                            LOG.error("Error reading document",e);
                        }

                    });
                }
                executor.stop();

                // write neighbourhoods
                LOG.info("writing neighbourhoods ..");
                for(Map.Entry<Point,Neighbourhood> entry : neighbourhoods.entrySet()){
                    testWriter.write(entry.getValue());
                }
                LOG.info("done!");

            }catch(Exception e){
                LOG.error("Unexpected error",e);
            }finally{
                testWriter.close();
                trainingWriter.close();
            }

        } catch (Exception e) {
            LOG.error("Unexpected Error", e);
        }
    }

    private HttpResponse<JsonNode> getShape(String endpoint, String text){
        HttpResponse<JsonNode> shape = null;
        Boolean added = false;
        UnirestException error = null;
        AtomicInteger retries = new AtomicInteger(5);
        ShapeRequest request = new ShapeRequest();
        request.setText(text);
        do{
            try{
                shape = Unirest.post(endpoint+"/shape").body(request).asJson();
                added = true;
                error = null;
            }catch (UnirestException e){
                error = e;
            }
        }while(!added && retries.decrementAndGet() > 0);
        if (error != null){
            LOG.warn("Error getting shape",error);
        }
        return shape;
    }

}
