package org.librairy.eval.datasets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
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
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
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
        try {
            //"https://delicias.dia.fi.upm.es/nextcloud/index.php/s/4tPyd5Ps51sCuRx/download";
            reader = new JsonlReader<>(new File("/Users/cbadenes/Corpus/wikipedia-articles/all-articles.jsonl.gz"), WikiArticle.class);

            trainingWriter  = new JsonlWriter<>(Paths.get(baseDir, "training-set.jsonl.gz").toFile());
            testWriter      = new JsonlWriter<>(Paths.get(baseDir, "test-set.jsonl.gz").toFile());

            AtomicInteger trainingCounter = new AtomicInteger();
            AtomicInteger testCounter = new AtomicInteger();

            Integer trainingInterval    = Math.min(trainingSize, 100);
            Integer testInterval        = Math.min(testSize, 100);

            try{
                List<Point> points = new ArrayList<>();
                executor = new ParallelExecutor();
                Optional<WikiArticle> row;
                while((row = reader.next()).isPresent() && trainingCounter.incrementAndGet() <= trainingSize){

                    final WikiArticle article = row.get();

                    executor.execute(() -> {
                        try{
                            String text = article.getText();
                            if (text.length() < minTextSize) return;

                            ShapeRequest request = new ShapeRequest();
                            request.setText(text);

                            HttpResponse<JsonNode> shape = Unirest.post(endpoint+"/shape").body(request).asJson();
                            Point point = shapeToPoint(article.getUrl(), shape);
                            trainingWriter.write(point);
                            points.add(point);

                        }catch (Exception e){
                            LOG.error("Error reading document",e);
                        }

                    });
                    if (trainingCounter.get() % trainingInterval == 0) {
                        LOG.info(trainingCounter.get() + " training points added");
                        Thread.sleep(20);
                    }
                }
                executor.pause();
                LOG.info(trainingCounter.get()-1 + " training points completed");

                while((row = reader.next()).isPresent() && testCounter.incrementAndGet() <= testSize){

                    final WikiArticle article = row.get();

                    executor.execute(() -> {
                        try{
                            String text = article.getText();
                            if (text.length() < minTextSize) return;

                            ShapeRequest request = new ShapeRequest();
                            request.setText(text);

                            HttpResponse<JsonNode> shape = Unirest.post(endpoint+"/shape").body(request).asJson();
                            Point reference = shapeToPoint(article.getUrl(), shape);

                            // calculate neighbourhood
                            List<Neighbour> neighbours  = points.stream().map(point -> new Neighbour(point, JensenShannon.similarity(point.getVector(), reference.getVector()))).sorted((a, b) -> -a.getScore().compareTo(b.getScore())).limit(topPoints).collect(Collectors.toList());
                            Neighbourhood neighbourhood = new Neighbourhood();
                            neighbourhood.setReference(reference);
                            neighbourhood.setClosestNeighbours(neighbours);
                            neighbourhood.setNumberOfNeighbours(topPoints);

                            testWriter.write(neighbourhood);


                        }catch (Exception e){
                            LOG.error("Error reading document",e);
                        }

                    });
                    if (testCounter.get() % testInterval == 0) {
                        LOG.info(testCounter.get() + " test points completed");
                        Thread.sleep(20);
                    }
                }
                executor.stop();
                LOG.info(testCounter.get()-1 + " test points added");

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

}
