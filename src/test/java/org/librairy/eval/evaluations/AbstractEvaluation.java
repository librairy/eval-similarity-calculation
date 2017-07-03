/*
 * Copyright (c) 2017. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.eval.evaluations;

import com.google.common.primitives.Doubles;
import org.apache.commons.lang3.StringUtils;
import org.librairy.eval.expressions.DistributionExpression;
import org.librairy.eval.algorithms.Algorithm;
import org.librairy.eval.model.DirichletDistribution;
import org.librairy.eval.model.Result;
import org.librairy.eval.model.Similarity;
import org.librairy.metrics.similarity.JensenShannonSimilarity;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public abstract class AbstractEvaluation {

    protected Map<String,List<Similarity>> createGoldStandard(List<DirichletDistribution> vectors, Double threshold, Integer numTopSimilar){
        Map<String,List<Similarity>> goldStandard = new HashMap<>();
        for(DirichletDistribution dd : vectors){
            List<Similarity> topSimilar = vectors.stream()
                    .filter(v -> !v.getId().equalsIgnoreCase(dd.getId()))
                    .map(v -> {
                        Similarity sim = new Similarity();
                        sim.setId(v.getId());
                        sim.setScore(JensenShannonSimilarity.apply(Doubles.toArray(dd.getVector()), Doubles.toArray(v.getVector())));
                        return sim;
                    })
                    .sorted((v1, v2) -> -v1.getScore().compareTo(v2.getScore()))
                    .filter(s -> s.getScore() > threshold)
                    .limit(numTopSimilar)
                    .collect(Collectors.toList());
            goldStandard.put(dd.getId(), topSimilar);
        }
        return goldStandard;
    }

    public Result evaluationOf(Integer numVectors, Integer numTopics, Integer numTopSimilar, Double threshold, List<DirichletDistribution> vectors, Map<String,List<Similarity>> goldStandard, Algorithm algorithm){

        // Recommendations based on algorithm
        Instant start   = Instant.now();
        List<DistributionExpression> shapes = algorithm.getShapesFrom(vectors);
        Instant end     = Instant.now();
        long time = Duration.between(start, end).toMillis();

        Map<String,List<DirichletDistribution>> vectorsByExpression = new HashMap<>();
        for(DistributionExpression gd : shapes){
            List<DirichletDistribution> list = new ArrayList<>();
            if (vectorsByExpression.containsKey(gd.getExpression())){
                list = vectorsByExpression.get(gd.getExpression());
            }
            list.add(gd.getDirichletDistribution());
            vectorsByExpression.put(gd.getExpression(), list);
        }

        Map<String,List<Similarity>> recommendations = new HashMap<>();
        for(DistributionExpression gd : shapes){
            List<DirichletDistribution> related = vectorsByExpression.get(gd.getExpression()).stream().filter(d -> !d.getId().equalsIgnoreCase(gd.getDirichletDistribution().getId())).collect(Collectors.toList());
            List<Similarity> similarities = new ArrayList<>();
            for (DirichletDistribution dd : related){
                double similarityScore = JensenShannonSimilarity.apply(Doubles.toArray(dd.getVector()), Doubles.toArray(gd.getDirichletDistribution().getVector()));
                if (similarityScore > threshold){
                    Similarity sim = new Similarity();
                    sim.setId(dd.getId());
                    sim.setScore(similarityScore);
                    similarities.add(sim);
                }
            }
            List<Similarity> topSimilarities = similarities.stream().sorted((a, b) -> -a.getScore().compareTo(b.getScore())).limit(numTopSimilar).collect(Collectors.toList());
            recommendations.put(gd.getDirichletDistribution().getId(), topSimilarities);
        }


        // Results
        Integer tp = 0;
        Integer fp = 0;
        Integer fn = 0;
        Integer tn = 0;

        for ( Map.Entry<String,List<Similarity>> entry : goldStandard.entrySet()){

            List<Similarity> recommendation = recommendations.get(entry.getKey());

            tp += Long.valueOf(entry.getValue().stream().filter(recommendation::contains).count()).intValue();

            fp += Long.valueOf(recommendation.stream().filter(r -> !entry.getValue().contains(r)).count()).intValue();

            fn += Long.valueOf(entry.getValue().stream().filter( e -> !recommendation.contains(e)).count()).intValue();

            tn += Long.valueOf(vectors.stream().filter( v ->
                    (recommendation.stream().filter( r -> r.getId().equalsIgnoreCase(v.getId())).count() == 0) &&
                            (entry.getValue().stream().filter( e -> e.getId().equalsIgnoreCase(v.getId())).count() == 0)
            ).count()).intValue();

        }

        System.out.println(StringUtils.repeat("=",50));
        System.out.println("Algorithm: " + algorithm);
        System.out.println("Num Vectors: " + numVectors);
        System.out.println("Num Topics: " + numTopics);
        System.out.println("Top Similar: " + numTopSimilar);
        System.out.println("Min Score: " + threshold);


        System.out.println("TP="+tp);
        System.out.println("TN="+tn);
        System.out.println("FP="+fp);
        System.out.println("FN="+fn);


        Double precision    = (Double.valueOf(tp) + Double.valueOf(fp)) == 0.0? 0.0 : Double.valueOf(tp) / (Double.valueOf(tp) + Double.valueOf(fp));
        Double recall       = (Double.valueOf(tp) + Double.valueOf(fn)) == 0.0? 0.0 : Double.valueOf(tp) / (Double.valueOf(tp) + Double.valueOf(fn));

        System.out.println("Precision@"+numTopSimilar+"=" + precision);
        System.out.println("Recall@"+numTopSimilar+"=" + recall);

        Integer clusters =  Long.valueOf(shapes.stream().map(s -> s.getExpression()).distinct().count()).intValue();
        System.out.println("Num Groups: " + clusters);
        Integer totalSimilarities = Long.valueOf(vectors.size()*vectors.size()).intValue();
        System.out.println("Total Similarities: " + totalSimilarities);
        Integer calculatedSimilarities = Long.valueOf(vectorsByExpression.entrySet().stream().map(e -> e.getValue().size()*e.getValue().size()).reduce((a,b) -> a+b).get()).intValue();
        System.out.println("Calculated Similarities: " + calculatedSimilarities);
        Double saving = 100.0 - calculatedSimilarities*100.0/totalSimilarities;
        System.out.println("Efficiency: " + saving + "%");

        Result result = new Result();

        result.setSize(numVectors);
        result.setTopics(numTopics);
        result.setTop(numTopSimilar);
        result.setMinScore(threshold);
        result.setTp(tp);
        result.setTn(tn);
        result.setFn(fn);
        result.setFp(fp);
        result.setPrecision(precision);
        result.setRecall(recall);
        result.setClusters(clusters);
        result.setTotalSimilarities(totalSimilarities);
        result.setCalculatedSimilarities(calculatedSimilarities);
        result.setTime(time);

        return result;

    }


}
