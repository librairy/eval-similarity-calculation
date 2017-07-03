/*
 * Copyright (c) 2017. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.eval.expressions;

import lombok.Data;
import org.librairy.eval.model.DirichletDistribution;

import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@Data
public class GradientExpression extends DistributionExpression {

    public GradientExpression(Double ratio, DirichletDistribution dirichletDistribution){
        super(createExpression(dirichletDistribution,ratio),dirichletDistribution);
    }

    private static String createExpression(DirichletDistribution dirichletDistribution, Double ratio){
        List<Double> vector = dirichletDistribution.getVector();
        StringBuilder bits = new StringBuilder();


        Double last = vector.get(0);
        Double limit = 0.0;
        Double acc = last;
        for(int i=1; i< vector.size(); i++){
            limit = Math.max(last, Math.abs(1.0-acc));
            Double current = vector.get(i);
//            bits.append(current>last?"1":"0");

            Double slope = current-last;
            if (Math.abs(slope) > (ratio * limit)){
                if (slope > 0) bits.append("1");
                else bits.append("2");
            }else bits.append("0");

            last = current;
            acc += current;
//            limit -= current;
        }

        return  bits.toString();
    }

}
