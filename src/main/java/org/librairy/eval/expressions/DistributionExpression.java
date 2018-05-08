/*
 * Copyright (c) 2017. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.eval.expressions;

import org.librairy.eval.model.Point;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class DistributionExpression {

    String expression;

    Point dirichletDistribution;

    public DistributionExpression() {
    }

    public DistributionExpression(String expression, Point dirichletDistribution) {
        this.expression = expression;
        this.dirichletDistribution = dirichletDistribution;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Point getDirichletDistribution() {
        return dirichletDistribution;
    }

    public void setDirichletDistribution(Point dirichletDistribution) {
        this.dirichletDistribution = dirichletDistribution;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DistributionExpression that = (DistributionExpression) o;

        if (expression != null ? !expression.equals(that.expression) : that.expression != null) return false;
        return dirichletDistribution != null ? dirichletDistribution.equals(that.dirichletDistribution) : that.dirichletDistribution == null;

    }

    @Override
    public int hashCode() {
        int result = expression != null ? expression.hashCode() : 0;
        result = 31 * result + (dirichletDistribution != null ? dirichletDistribution.hashCode() : 0);
        return result;
    }
}
