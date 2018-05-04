package org.librairy.eval.model;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class Related {

    DirichletDistribution distribution;

    Double score = 0.0;

    public Related(DirichletDistribution distribution, Double score) {
        this.distribution = distribution;
        this.score = score;
    }

    public DirichletDistribution getDistribution() {
        return distribution;
    }

    public void setDistribution(DirichletDistribution distribution) {
        this.distribution = distribution;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Related{" +
                "distribution=" + distribution +
                ", score=" + score +
                '}';
    }
}
