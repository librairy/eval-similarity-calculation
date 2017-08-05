/*
 * Copyright (c) 2017. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.eval.experiments;

import org.junit.Test;
import org.librairy.eval.algorithms.RDCAlgorithm;
import org.librairy.eval.expressions.TopExpression;
import org.librairy.eval.model.DirichletDistribution;
import org.librairy.eval.expressions.DistributionExpression;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class SyntheticEntropyBasedExperiment extends AbstractExperiment {

    Integer top             = 1;

    @Test
    public void evaluate(){

        Integer numVectors      = 1000;
        Integer numTopics       = 40;
        Integer numTopSimilar   = 5;
        Double threshold        = 0.5;

        StringBuilder summary = new StringBuilder();

        summary.append(IntStream.range(0, 1)
                .mapToObj(i -> evaluationOf(numVectors, numTopics, threshold, new RDCAlgorithm(1)))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");

        System.out.println(summary.toString());
    }

    @Test
    public void evaluateAll(){

        StringBuilder summary = new StringBuilder();

        top = 1;
        summary.append(IntStream.range(0, 3)
                .mapToObj(i -> evaluationOf(1000, 5, 0.75, new RDCAlgorithm(top)))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");
        top = 1;
        summary.append(IntStream.range(0, 3)
                .mapToObj(i -> evaluationOf(1000, 10, 0.75, new RDCAlgorithm(top)))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");
        top = 1;
        summary.append(IntStream.range(0, 3)
                .mapToObj(i -> evaluationOf(1000, 20, 0.75, new RDCAlgorithm(top)))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");
        top = 1;
        summary.append(IntStream.range(0, 3)
                .mapToObj(i -> evaluationOf(1000, 40, 0.75, new RDCAlgorithm(top)))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");
        top = 2;
        summary.append(IntStream.range(0, 3)
                .mapToObj(i -> evaluationOf(1000, 20, 0.75, new RDCAlgorithm(top)))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");
        top = 3;
        summary.append(IntStream.range(0, 3)
                .mapToObj(i -> evaluationOf(1000, 20, 0.75, new RDCAlgorithm(top)))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");
        top = 2;
        summary.append(IntStream.range(0, 3)
                .mapToObj(i -> evaluationOf(1000, 40, 0.75, new RDCAlgorithm(top)))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");
        top = 3;
        summary.append(IntStream.range(0, 3)
                .mapToObj(i -> evaluationOf(1000, 40, 0.75, new RDCAlgorithm(top)))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");
        top = 1;
        summary.append(IntStream.range(0, 3)
                .mapToObj(i -> evaluationOf(1000, 40, 0.6, new RDCAlgorithm(top)))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");
        top = 1;
        summary.append(IntStream.range(0, 3)
                .mapToObj(i -> evaluationOf(1000, 40, 0.50, new RDCAlgorithm(top)))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");
        System.out.println(summary);
    }

    @Test
    public void evaluateComparison(){

        StringBuilder summary = new StringBuilder();
        summary.append(evaluationOf(100, 40, 0.75, new RDCAlgorithm(1))).append("\n");
        summary.append(evaluationOf(500, 40, 0.75, new RDCAlgorithm(1))).append("\n");
        summary.append(evaluationOf(1000, 40, 0.75, new RDCAlgorithm(1))).append("\n");
        summary.append(evaluationOf(2000, 40, 0.75, new RDCAlgorithm(1))).append("\n");
        summary.append(evaluationOf(3000, 40, 0.75, new RDCAlgorithm(1))).append("\n");
        summary.append(evaluationOf(4000, 40, 0.75, new RDCAlgorithm(1))).append("\n");
        summary.append(evaluationOf(5000, 40, 0.75, new RDCAlgorithm(1))).append("\n");
        summary.append(evaluationOf(10000, 40, 0.75, new RDCAlgorithm(1))).append("\n");
        summary.append(evaluationOf(20000, 40, 0.75, new RDCAlgorithm(1))).append("\n");
        summary.append(evaluationOf(30000, 40, 0.75, new RDCAlgorithm(1))).append("\n");
        summary.append(evaluationOf(40000, 40, 0.75, new RDCAlgorithm(1))).append("\n");
        summary.append(evaluationOf(50000, 40, 0.75, new RDCAlgorithm(1))).append("\n");
        summary.append(evaluationOf(100000, 40, 0.75, new RDCAlgorithm(1))).append("\n");
        System.out.println(summary);
    }


    @Override
    protected List<DistributionExpression> getShapesFrom(List<DirichletDistribution> distributions) {
        return distributions.stream().map(v -> new TopExpression(top, v)).collect(Collectors.toList());
    }
}
