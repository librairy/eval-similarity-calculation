package org.librairy.eval.algorithms;

import org.librairy.eval.expressions.DistributionExpression;
import org.librairy.eval.expressions.GradientExpression;
import org.librairy.eval.model.DirichletDistribution;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class GradientAlgorithm implements Algorithm {

    @Override
    public List<DistributionExpression> getShapesFrom(List<DirichletDistribution> distributions) {
        return distributions.stream().map(v -> new GradientExpression(0.99, v)).collect(Collectors.toList());
    }
}
