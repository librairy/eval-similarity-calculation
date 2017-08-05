package org.librairy.eval.algorithms;

import org.librairy.eval.expressions.DistributionExpression;
import org.librairy.eval.model.DirichletDistribution;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class CRDCAlgorithm implements Algorithm {

    private final Double threshold;

    public CRDCAlgorithm(Double threshold){
        this.threshold = threshold;
    }

    @Override
    public List<DistributionExpression> getShapesFrom(List<DirichletDistribution> distributions) {
        //return distributions.stream().map(v -> new TopExpression(top, v)).collect(Collectors.toList());
        return distributions.stream().map(v -> new DistributionExpression(v.getSortedTopics(threshold), v)).collect(Collectors.toList());
    }

    @Override
    public Integer getExtraPairs() {
        return 0;
    }

    @Override
    public String toString() {
        return "Cumulative RDC-" + threshold + " Algorithm";
    }
}
