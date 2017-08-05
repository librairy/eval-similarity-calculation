package org.librairy.eval.algorithms;

import org.librairy.eval.expressions.DistributionExpression;
import org.librairy.eval.expressions.TopExpression;
import org.librairy.eval.model.DirichletDistribution;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class RDCAlgorithm implements Algorithm {

    private final Integer top;

    public RDCAlgorithm(Integer top){
        this.top = top;
    }

    @Override
    public List<DistributionExpression> getShapesFrom(List<DirichletDistribution> distributions) {
        return distributions.stream().map(v -> new TopExpression(top, v)).collect(Collectors.toList());
    }

    @Override
    public Integer getExtraPairs() {
        return 0;
    }

    @Override
    public String toString() {
        return "RDC-" + top + " Algorithm";
    }
}
