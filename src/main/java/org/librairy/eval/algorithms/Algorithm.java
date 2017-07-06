package org.librairy.eval.algorithms;

import org.librairy.eval.expressions.DistributionExpression;
import org.librairy.eval.model.DirichletDistribution;

import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public interface Algorithm  {

    List<DistributionExpression> getShapesFrom(List<DirichletDistribution> distributions);

    Integer getExtraPairs();
}
