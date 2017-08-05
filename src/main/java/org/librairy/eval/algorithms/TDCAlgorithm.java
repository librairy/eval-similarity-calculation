package org.librairy.eval.algorithms;

import org.librairy.eval.expressions.DistributionExpression;
import org.librairy.eval.expressions.GradientExpression;
import org.librairy.eval.model.DirichletDistribution;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class TDCAlgorithm implements Algorithm {

    private final Double ratio;

    public TDCAlgorithm(Double ratio){
        this.ratio = ratio;
    }

    @Override
    public List<DistributionExpression> getShapesFrom(List<DirichletDistribution> distributions) {
        return distributions.stream().map(v -> new GradientExpression(ratio, v)).collect(Collectors.toList());
    }

    @Override
    public Integer getExtraPairs() {
        return 0;
    }


    @Override
    public String toString() {
        return "TDC Algorithm";
    }

}
