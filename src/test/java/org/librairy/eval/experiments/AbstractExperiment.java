/*
 * Copyright (c) 2017. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.eval.experiments;

import org.librairy.eval.algorithms.Algorithm;
import org.librairy.eval.evaluations.AbstractEvaluation;
import org.librairy.eval.expressions.DistributionExpression;
import org.librairy.eval.model.DirichletDistribution;
import org.librairy.eval.model.Result;
import org.librairy.eval.model.Similarity;
import org.librairy.metrics.similarity.JensenShannonSimilarity;

import java.util.List;
import java.util.Map;
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


    public Result evaluationOf(Integer numVectors, Integer numTopics, Double threshold, Algorithm algorithm) {
        List<DirichletDistribution> sample = createSampling(numVectors, numTopics);
        Map<String, List<Similarity>> goldStandard = createGoldStandard(sample, threshold, (p,q) -> JensenShannonSimilarity.apply(p,q));
        return evaluationOf(numVectors, numTopics, threshold, sample, goldStandard, algorithm, (p,q) -> JensenShannonSimilarity.apply(p,q));
    }

}
