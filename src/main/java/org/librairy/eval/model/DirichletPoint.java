package org.librairy.eval.model;

import com.google.common.primitives.Doubles;
import org.apache.commons.math3.ml.clustering.Clusterable;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class DirichletPoint implements Clusterable{


    private final double[] point;

    private DirichletDistribution distribution;

    public DirichletPoint(DirichletDistribution dirichletDistribution){
        this.point = Doubles.toArray(dirichletDistribution.getVector());
        this.distribution = dirichletDistribution;
    }

    @Override
    public double[] getPoint() {
        return point;
    }
}
