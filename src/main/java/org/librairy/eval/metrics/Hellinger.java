package org.librairy.eval.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class Hellinger {

    private static final Logger LOG = LoggerFactory.getLogger(Hellinger.class);

    public static Double distance(double[] v1, double[] v2){

        Double sum = 0.0;

        if (v1.length != v2.length) return -1.0;

        for (int i=0; i<v1.length;i++){
            sum += Math.pow(Math.sqrt(v1[i]) - Math.sqrt(v2[i]),2);
        }

        return (1 / Math.sqrt(2))*Math.sqrt(sum);
    }

}
