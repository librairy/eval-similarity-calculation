/*
 * Copyright (c) 2017. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.eval.experiments;

import org.librairy.eval.evaluations.AbstractEvaluation;
import org.librairy.eval.expressions.DistributionExpression;
import org.librairy.eval.model.DirichletDistribution;
import org.librairy.eval.model.Result;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public abstract class AbstractExperiment extends AbstractEvaluation{

    protected abstract List<DistributionExpression> getShapesFrom(List<DirichletDistribution> distributions);

    protected List<DirichletDistribution> createSampling(Integer size, Integer dimension) {
        return IntStream
                .range(0, size)
                .mapToObj(i -> new DirichletDistribution(String.valueOf(i), dimension))
                .collect(Collectors.toList());
    }


    public Result evaluationOf(Integer numVectors, Integer numTopics, Integer numTopSimilar, Double threshold) {
        return evaluationOf(numVectors, numTopics, numTopSimilar, threshold, createSampling(numVectors, numTopics));
    }

    public Result evaluationOf(Integer numVectors, Integer numTopics, Integer numTopSimilar, Double threshold, List<DirichletDistribution> vectors){
        return evaluationOf(numVectors, numTopics, numTopSimilar, threshold, vectors, createGoldStandard(vectors, threshold, numTopSimilar), (a) -> getShapesFrom(a));
    }

}
