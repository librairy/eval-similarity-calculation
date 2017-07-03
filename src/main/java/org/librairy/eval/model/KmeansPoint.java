package org.librairy.eval.model;

import com.google.common.primitives.Doubles;
import lombok.Data;
import org.apache.commons.math3.ml.clustering.Clusterable;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@Data
public class KmeansPoint implements Clusterable{


    private final double[] point;

    private DirichletDistribution distribution;

    public KmeansPoint(DirichletDistribution dirichletDistribution){
        this.point = Doubles.toArray(dirichletDistribution.getVector());
        this.distribution = dirichletDistribution;
    }

}
