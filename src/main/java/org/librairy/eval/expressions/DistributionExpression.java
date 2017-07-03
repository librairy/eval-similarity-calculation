/*
 * Copyright (c) 2017. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.eval.expressions;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.librairy.eval.model.DirichletDistribution;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@Data
@AllArgsConstructor
public class DistributionExpression {

    String expression;

    DirichletDistribution dirichletDistribution;

}
