/*
 * Copyright (c) 2017. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.eval.experiments;

import org.junit.Test;
import org.librairy.eval.algorithms.GradientAlgorithm;
import org.librairy.eval.expressions.GradientExpression;
import org.librairy.eval.model.DirichletDistribution;
import org.librairy.eval.expressions.DistributionExpression;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class SyntheticGradientBasedExperiment extends AbstractExperiment {

    Double ratio            = 0.99;

    @Test
    public void evaluate(){

        Integer numVectors      = 1000;
        Integer numTopics       = 40;
        Integer numTopSimilar   = 5;
        Double threshold        = 0.75;

        StringBuilder summary = new StringBuilder();

        summary.append(IntStream.range(0, 1)
                .mapToObj(i -> evaluationOf(numVectors, numTopics, threshold, new GradientAlgorithm(ratio)))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");

        System.out.println(summary.toString());
    }

    @Test
    public void evaluateAll(){

        StringBuilder summary = new StringBuilder();

        ratio = 0.99;
        summary.append(IntStream.range(0, 3)
                .mapToObj(i -> evaluationOf(1000, 5, 0.75, new GradientAlgorithm(ratio)))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");
        ratio = 0.99;
        summary.append(IntStream.range(0, 3)
                .mapToObj(i -> evaluationOf(1000, 10, 0.75, new GradientAlgorithm(ratio)))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");
        ratio = 0.99;
        summary.append(IntStream.range(0, 3)
                .mapToObj(i -> evaluationOf(1000, 20, 0.75, new GradientAlgorithm(ratio)))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");
        ratio = 0.99;
        summary.append(IntStream.range(0, 3)
                .mapToObj(i -> evaluationOf(1000, 40, 0.75, new GradientAlgorithm(ratio)))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");
        ratio = 0.90;
        summary.append(IntStream.range(0, 3)
                .mapToObj(i -> evaluationOf(1000, 20, 0.75, new GradientAlgorithm(ratio)))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");
        ratio = 0.85;
        summary.append(IntStream.range(0, 3)
                .mapToObj(i -> evaluationOf(1000, 20, 0.75, new GradientAlgorithm(ratio)))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");
        ratio = 0.90;
        summary.append(IntStream.range(0, 3)
                .mapToObj(i -> evaluationOf(1000, 40, 0.75, new GradientAlgorithm(ratio)))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");
        ratio = 0.85;
        summary.append(IntStream.range(0, 3)
                .mapToObj(i -> evaluationOf(1000, 40, 0.75, new GradientAlgorithm(ratio)))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");
        ratio = 0.99;
        summary.append(IntStream.range(0, 3)
                .mapToObj(i -> evaluationOf(1000, 40, 0.6, new GradientAlgorithm(ratio)))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");
        ratio = 0.99;
        summary.append(IntStream.range(0, 3)
                .mapToObj(i -> evaluationOf(1000, 40, 0.5, new GradientAlgorithm(ratio)))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");
        System.out.println(summary);
    }

    @Override
    protected List<DistributionExpression> getShapesFrom(List<DirichletDistribution> distributions) {
        return distributions.stream().map(v -> new GradientExpression(ratio, v)).collect(Collectors.toList());
    }

}
