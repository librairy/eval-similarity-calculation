package org.librairy.eval.model;

import org.junit.Assert;
import org.junit.Test;
import org.librairy.eval.metrics.JensenShannon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class NeighbourhoodTest {

    private static final Logger LOG = LoggerFactory.getLogger(NeighbourhoodTest.class);


    @Test
    public void validate(){

        Point p1 = new Point("p1");
        p1.initializeRamdon(4);

        Neighbourhood neighbourhood = new Neighbourhood(p1,2);

        Double minScore;

        Point p2 = new Point("p2");
        p2.initializeRamdon(4);
        minScore = JensenShannon.similarity(p1.getVector(),p2.getVector());
        neighbourhood.add(p2);

        Point p3 = new Point("p3");
        p3.initializeRamdon(4);
        minScore = Math.min(minScore, JensenShannon.similarity(p1.getVector(),p3.getVector()));
        neighbourhood.add(p3);

        Point p4 = new Point("p4");
        Double currentScore = 0.0;
        do{
            p4.initializeRamdon(4);
            currentScore = JensenShannon.similarity(p1.getVector(), p4.getVector());
        }while(currentScore > minScore);

        Neighbour n4 = new Neighbour(p4,currentScore);

        neighbourhood.add(p4);
        boolean isNeighbour = neighbourhood.getClosestNeighbours().contains(n4);
        if (currentScore <= minScore){
            Assert.assertFalse(isNeighbour);
        }else{
            Assert.assertTrue(isNeighbour);
        }
    }

    @Test
    public void contains(){

        Point p1 = new Point("p1");
        Neighbour n1 = new Neighbour(p1, 0.0);

        Point p2 = new Point("p2");
        Neighbour n2 = new Neighbour(p2, 0.0);

        Point p3 = new Point("p3");
        Neighbour n3 = new Neighbour(p3, 0.0);

        Point p4 = new Point("p4");
        Neighbour n4 = new Neighbour(p4, 0.0);

        List<Neighbour> neighbours = Arrays.asList(new Neighbour[]{n1,n2,n3});

        LOG.info("contains?" +neighbours.contains(n1));
        LOG.info("contains?" +neighbours.contains(n2));
        LOG.info("contains?" +neighbours.contains(n3));
        LOG.info("contains?" +neighbours.contains(n4));



    }

}
