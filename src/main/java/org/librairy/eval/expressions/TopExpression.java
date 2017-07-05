/*
 * Copyright (c) 2017. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.eval.expressions;

import lombok.Data;
import org.librairy.eval.model.DirichletDistribution;
import scala.Tuple2;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@Data
public class TopExpression extends DistributionExpression {

    public TopExpression(Integer n, DirichletDistribution dirichletDistribution){
        super(IntStream
                .range(0,dirichletDistribution.getVector().size())
                .mapToObj(i -> new Tuple2<Integer,Double>(i,dirichletDistribution.getVector().get(i)))
                .sorted( (a,b) -> -a._2.compareTo(b._2))
                .map( t -> String.valueOf(t._1))
                .limit(n)
                .collect(Collectors.joining("-")), dirichletDistribution);
    }

}
