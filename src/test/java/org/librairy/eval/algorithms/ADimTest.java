package org.librairy.eval.algorithms;

import org.junit.Test;
import org.librairy.eval.model.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class ADimTest {

    private static final Logger LOG = LoggerFactory.getLogger(ADimTest.class);

    @Test
    public void simpleLabel(){

        Point point = new Point();
        point.setId("id1");
        point.setVector(Arrays.asList(new Double[]{0.2,0.3,0.1,0.2,0.2}));
        LOG.info("Point: " + point);

        for (int i=0;i<=10;i++){
            Double threshold = i/10.0;
            LOG.info("l"+threshold+": " + new ADimAlgorithm(threshold).getCluster(point));
        }


    }

}
