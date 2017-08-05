package org.librairy.eval.metrics;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public interface DistanceMetric {

    double apply(double[] p, double[] q);
}
