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
import org.librairy.eval.model.DirichletDistribution;
import org.librairy.eval.expressions.DistributionExpression;
import org.librairy.eval.model.KmeansPoint;
import org.librairy.metrics.distance.JensenShannonDivergence;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class SyntheticKMeansBasedExperiment extends AbstractExperiment {

    Integer iterations = 1;

    @Test
    public void evaluate(){

        Integer numVectors      = 1000;
        Integer numTopics       = 40;
        Integer numTopSimilar   = 5;
        Double threshold        = 0.5;

        StringBuilder summary = new StringBuilder();

        summary.append(IntStream.range(0, 1)
                .mapToObj(i -> evaluationOf(numVectors, numTopics, numTopSimilar, threshold))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");

        System.out.println(summary.toString());
    }

    @Test
    public void evaluateAll(){

        StringBuilder summary = new StringBuilder();

        iterations = 100;
        summary.append(IntStream.range(0, 3)
                .mapToObj(i -> evaluationOf(1000, 5, 5, 0.75))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");
        iterations = 100;
        summary.append(IntStream.range(0, 3)
                .mapToObj(i -> evaluationOf(1000, 10, 5, 0.75))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");
        iterations = 100;
        summary.append(IntStream.range(0, 3)
                .mapToObj(i -> evaluationOf(1000, 20, 5, 0.75))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");
        iterations = 100;
        summary.append(IntStream.range(0, 3)
                .mapToObj(i -> evaluationOf(1000, 40, 5, 0.75))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");
        iterations = 200;
        summary.append(IntStream.range(0, 3)
                .mapToObj(i -> evaluationOf(1000, 20, 5, 0.75))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");
        iterations = 300;
        summary.append(IntStream.range(0, 3)
                .mapToObj(i -> evaluationOf(1000, 20, 5, 0.75))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");
        iterations = 200;
        summary.append(IntStream.range(0, 3)
                .mapToObj(i -> evaluationOf(1000, 40, 5, 0.75))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");
        iterations = 300;
        summary.append(IntStream.range(0, 3)
                .mapToObj(i -> evaluationOf(1000, 40, 5, 0.75))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");
        iterations = 100;
        summary.append(IntStream.range(0, 3)
                .mapToObj(i -> evaluationOf(1000, 40, 5, 0.6))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");
        iterations = 100;
        summary.append(IntStream.range(0, 3)
                .mapToObj(i -> evaluationOf(1000, 40, 5, 0.5))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");
        System.out.println(summary);
    }

    @Test
    public void evaluateComparison(){

        StringBuilder summary = new StringBuilder();
        evaluationOf(1000, 5, 5, 0.75);

        iterations = 100;
        summary.append(evaluationOf(100, 40, 5, 0.75)).append("\n");
        summary.append(evaluationOf(500, 40, 5, 0.75)).append("\n");
        summary.append(evaluationOf(1000, 40, 5, 0.75)).append("\n");
        summary.append(evaluationOf(2000, 40, 5, 0.75)).append("\n");
        summary.append(evaluationOf(3000, 40, 5, 0.75)).append("\n");
        summary.append(evaluationOf(4000, 40, 5, 0.75)).append("\n");
        summary.append(evaluationOf(5000, 40, 5, 0.75)).append("\n");
        summary.append(evaluationOf(10000, 40, 5, 0.75)).append("\n");
        summary.append(evaluationOf(20000, 40, 5, 0.75)).append("\n");
        summary.append(evaluationOf(30000, 40, 5, 0.75)).append("\n");
        summary.append(evaluationOf(40000, 40, 5, 0.75)).append("\n");
        summary.append(evaluationOf(50000, 40, 5, 0.75)).append("\n");
        summary.append(evaluationOf(100000, 40, 5, 0.75)).append("\n");
        System.out.println(summary);
    }


    @Override
    protected List<DistributionExpression> getShapesFrom(List<DirichletDistribution> distributions) {


        DistanceMeasure distance = new DistanceMeasure() {
            @Override
            public double compute(double[] doubles, double[] doubles1) throws DimensionMismatchException {
                return JensenShannonDivergence.apply(doubles, doubles1);
            }
        };

        KMeansPlusPlusClusterer<KmeansPoint> kmeans = new KMeansPlusPlusClusterer<KmeansPoint>(distributions.get(0).getVector().size(),iterations, distance);


        List<KmeansPoint> points = distributions.stream().map(d -> new KmeansPoint(d)).collect(Collectors.toList());
        List<CentroidCluster<KmeansPoint>> clusters = kmeans.cluster(points);

        return clusters.stream().flatMap(cluster -> cluster.getPoints().stream().map(point -> new DistributionExpression(cluster.toString(), point.getDistribution()))).collect(Collectors.toList());
    }
}
