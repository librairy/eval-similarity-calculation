/*
 * Copyright (c) 2017. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.eval.evaluations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.Doubles;
import es.cbadenes.lab.test.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.librairy.eval.algorithms.*;
import org.librairy.eval.metrics.SimilarityMetric;
import org.librairy.eval.model.*;
import org.librairy.metrics.similarity.HellingerSimilarity;
import org.librairy.metrics.similarity.JensenShannonSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@Category(IntegrationTest.class)
public class AlgorithmsEvaluation extends AbstractEvaluation {

    private static final Logger LOG = LoggerFactory.getLogger(AlgorithmsEvaluation.class);

    Map<String,DataReport> reports;
    Map<String,Algorithm> algorithms;

    Double minScore             = 0.34  ; // 0.83
//    List<Integer> sizes = Arrays.asList(new Integer[]{200,300,400,500,600,700,800,900,1000});
    List<Integer> sizes = Arrays.asList(new Integer[]{1000});


    @Before
    public void setup(){
        this.algorithms = new HashMap<>();
        algorithms.put("TDC",           new TDCAlgorithm(0.99));
        algorithms.put("RDC",           new RDCAlgorithm(1));
        algorithms.put("CRDC",           new CRDCAlgorithm(0.90));//0.90
        algorithms.put("Random",        new RandomizeSelectionAlgorithm(44)); // num topics
//        algorithms.put("RDC-11",         new RDCAlgorithm(11));
//        algorithms.put("RDC-22",         new RDCAlgorithm(22));
//        algorithms.put("RDC-44",         new RDCAlgorithm(44));
//        algorithms.put("hentropy-100",   new HierarchicalRDCAlgorithm(100));
//        algorithms.put("hentropy-200",   new HierarchicalRDCAlgorithm(200));
//        algorithms.put("hentropy-300",   new HierarchicalRDCAlgorithm(300));
//        algorithms.put("kmeans-20",      new KMeansJSAlgorithm(20));


//        algorithms.put("kmeans-100",     new KMeansJSAlgorithm(100));
//        algorithms.put("dbscan-10",     new DBSCANJSAlgorithm(10));


//        algorithms.put("dbscan-100",     new DBSCANJSAlgorithm(100));
//        algorithms.put("dbscan-200",     new DBSCANJSAlgorithm(200));

        this.reports = new HashMap<>();
        reports.put("general",          result -> result.toString()+"\n");
        reports.put("time",             result -> result.getTime() + "\t");
        reports.put("cost",       result -> result.getCost() + "\t");
        reports.put("efficiency",       result -> result.getEfficiency() + "\t");
        reports.put("fMeasure",         result -> result.getFMeasure() + "\t");
        reports.put("precision",         result -> result.getPrecision() + "\t");
        reports.put("recall",         result -> result.getRecall() + "\t");
        reports.put("pairs",     result -> result.getCalculatedSimilarities() + "\t");
        reports.put("clusters",         result -> result.getClusters() + "\t");
        reports.put("effectiveness",    result -> result.getEffectiveness() + "\t");
    }


    @Test
    public void evaluationRealBasedOnJS() throws IOException {
        algorithms.put("K-Means",      new KMeansJSAlgorithm(50));
        algorithms.put("DBSCAN",     new DBSCANJSAlgorithm(50));
        evaluation("JS","real",(p,q) -> JensenShannonSimilarity.apply(p,q));
    }

    @Test
    public void evaluationSyntheticBasedOnJS() throws IOException {
        algorithms.put("K-Means",      new KMeansJSAlgorithm(50));
        algorithms.put("DBSCAN",     new DBSCANJSAlgorithm(50));
        evaluation("JS","synthetic",(p,q) -> JensenShannonSimilarity.apply(p,q));
    }

    @Test
    public void evaluationRealBasedOnHe() throws IOException {
        algorithms.put("K-Means",      new KMeansHeAlgorithm(50));
        algorithms.put("DBSCAN",     new DBSCANHeAlgorithm(50));
        evaluation("He","real",(p,q) -> HellingerSimilarity.apply(p,q));
    }

    @Test
    public void evaluationSyntheticBasedOnHe() throws IOException {
        algorithms.put("K-Means",      new KMeansHeAlgorithm(50));
        algorithms.put("DBSCAN",     new DBSCANHeAlgorithm(50));
        evaluation("He","synthetic",(p,q) -> HellingerSimilarity.apply(p,q));
    }


    private void evaluation(String label, String dataset, SimilarityMetric metric) throws IOException {
        String fileName = new StringBuilder().append("results/").append(dataset).append("/").append(label).append("/res").append("-min").append(StringUtils.replace(String.valueOf(minScore),".","_")).toString();
        new File(fileName).getParentFile().mkdirs();

        Map<String,FileWriter> writers = new HashMap<>();
        reports.entrySet().forEach(entry -> {try {writers.put(entry.getKey(), new FileWriter(new File(fileName + "-" + entry.getKey() + ".csv")));} catch (IOException e) {e.printStackTrace();}});

        // header
        writers.entrySet().forEach(entry -> {try {entry.getValue().write("Size\t");} catch (IOException e) {e.printStackTrace();}});
        algorithms.entrySet().stream().sorted((a,b) -> a.getKey().compareTo(b.getKey())).forEach( algorithm -> writers.entrySet().forEach(writer -> {try {writer.getValue().write(algorithm.getKey() + "\t");} catch (IOException e) {e.printStackTrace();}}));


        for(Integer size: sizes){
            writers.entrySet().forEach(entry -> {try {entry.getValue().write("\n" + size + "\t");} catch (IOException e) {e.printStackTrace();}});

            List<DirichletDistribution> corpus = retrieveCorpus(size,dataset);
            Integer numTopics = corpus.get(0).getVector().size();

            Map<String, List<Similarity>> goldStandard = createGoldStandard(corpus, minScore, metric);

            algorithms.entrySet().stream().sorted((a,b) -> a.getKey().compareTo(b.getKey())).forEach( entry -> {
                Result result = evaluationOf(size, numTopics, minScore,corpus,goldStandard,entry.getValue(),metric);
                result.setAlgorithm(entry.getKey());

                writers.entrySet().forEach(writer -> {try {writer.getValue().write(reports.get(writer.getKey()).get(result));} catch (IOException e) {e.printStackTrace();}});
            });
        }

        writers.entrySet().forEach(writer -> {try {writer.getValue().close();} catch (IOException e) {e.printStackTrace();}});

    }


    @Test
    public void evaluateSimilarities() throws IOException {

        Integer corpusSize = 200;
        //List<DirichletDistribution> corpus = retrieveCorpus(corpusSize,"real");
        List<DirichletDistribution> corpus = retrieveCorpus(corpusSize,"synthetic");


        Integer sample  = 10;
        Integer top     = 20;
        for (int i=0;i<sample;i++){

            System.out.println("====== '" + corpus.get(i).getId()+"'");
            DirichletDistribution ref = corpus.get(i);

            Related refRel = new Related();
            refRel.setDistribution(ref);
            System.out.println(refRel);

            corpus.stream()
                    .filter( el -> !el.getId().equals(ref.getId()))
//                    .filter( el -> el.getSortedTopics(44).equals(ref.getSortedTopics(44)) )
                    .map(d -> {
                        Related rel = new Related();
                        rel.setDistribution(d);
                        rel.setScore(JensenShannonSimilarity.apply(Doubles.toArray(ref.getVector()), Doubles.toArray(d.getVector())));
                        return rel;
                    })
                    .sorted((a, b) -> -a.getScore().compareTo(b.getScore()))
                    .limit(top)
                    .forEach(rel -> System.out.println(rel));

        }


    }

    @Test
    public void evaluateTopics() throws IOException {


        Integer corpusSize = 200;
        List<DirichletDistribution> corpus = retrieveCorpus(corpusSize,"real");


        Integer sample  = 1;
        Integer top     = 200;
        for (int i=0;i<sample;i++){

            System.out.println("======");
            DirichletDistribution ref = corpus.get(i);

            Related refRel = new Related();
            refRel.setDistribution(ref);
            System.out.println(refRel);

            corpus.stream()
                    .filter( el -> !el.getId().equals(ref.getId()))
                    .filter( el -> el.getHighestTopic() == ref.getHighestTopic() )
                    .map(d -> {
                        Related rel = new Related();
                        rel.setDistribution(d);
                        rel.setScore(JensenShannonSimilarity.apply(Doubles.toArray(ref.getVector()), Doubles.toArray(d.getVector())));
                        return rel;
                    })
                    .sorted((a, b) -> -a.getScore().compareTo(b.getScore()))
                    .limit(top)
                    .forEach(rel -> System.out.println(rel));

        }

    }


    private List<DirichletDistribution> retrieveCorpus(Integer maxSize, String label) throws IOException {
        ObjectMapper jsonMapper = new ObjectMapper();
        Corpora corpora = jsonMapper.readValue(new File("src/main/resources/"+label+"-corpora.json"), Corpora.class);
        return corpora.getDocuments().stream().limit(maxSize).collect(Collectors.toList());

    }

}
