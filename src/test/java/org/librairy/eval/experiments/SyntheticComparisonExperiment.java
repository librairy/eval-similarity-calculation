/*
 * Copyright (c) 2017. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.eval.experiments;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.junit.Test;
import org.librairy.eval.algorithms.Algorithm;
import org.librairy.eval.expressions.DistributionExpression;
import org.librairy.eval.expressions.GradientExpression;
import org.librairy.eval.expressions.TopExpression;
import org.librairy.eval.model.*;
import org.librairy.metrics.distance.JensenShannonDivergence;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class SyntheticComparisonExperiment extends AbstractExperiment {

    Algorithm entropyBased  = distributions -> distributions.stream().map(v -> new TopExpression(1, v)).collect(Collectors.toList());

    Algorithm gradientBased = distributions -> distributions.stream().map(v -> new GradientExpression(0.99, v)).collect(Collectors.toList());

    Algorithm kMeansBased   = distributions -> {
        DistanceMeasure distance = new DistanceMeasure() {
            @Override
            public double compute(double[] doubles, double[] doubles1) throws DimensionMismatchException {
                return JensenShannonDivergence.apply(doubles, doubles1);
            }
        };

        KMeansPlusPlusClusterer<KmeansPoint> kmeans = new KMeansPlusPlusClusterer<KmeansPoint>(distributions.get(0).getVector().size(),100, distance);


        List<KmeansPoint> points = distributions.stream().map(d -> new KmeansPoint(d)).collect(Collectors.toList());
        List<CentroidCluster<KmeansPoint>> clusters = kmeans.cluster(points);

        return clusters.stream().flatMap(cluster -> cluster.getPoints().stream().map(point -> new DistributionExpression(cluster.toString(), point.getDistribution()))).collect(Collectors.toList());
    };

    @Test
    public void evaluate(){

        StringBuilder summary = new StringBuilder();


//        Integer numTopics       = 40;
        Integer numTopSimilar   = 5;
        Double minScore         = 0.75;
        List<Integer> sizes = Arrays.asList(new Integer[]{
                100,500,1000,2000,3000,4000,5000,10000,20000
        });

        for(Integer size: sizes){
            Integer recommendedTopics   = Double.valueOf(2*Math.sqrt(size/2)).intValue();
            Integer numTopics           = recommendedTopics != 0? recommendedTopics : 2;
            List<DirichletDistribution> corpus = createSampling(size, numTopics);
            Map<String, List<Similarity>> goldStandard = createGoldStandard(corpus, minScore);
            summary.append("gradient\t").append(evaluationOf(size, numTopics,  minScore,corpus,goldStandard,gradientBased)).append("\n");
            summary.append("entropy\t").append(evaluationOf(size, numTopics, minScore,corpus,goldStandard,entropyBased)).append("\n");
            summary.append("kmeans\t").append(evaluationOf(size, numTopics, minScore,corpus,goldStandard,kMeansBased)).append("\n");
        }
        System.out.println(summary);
    }


    @Override
    protected List<DistributionExpression> getShapesFrom(List<DirichletDistribution> distributions) {
        return Collections.emptyList();
    }

}
