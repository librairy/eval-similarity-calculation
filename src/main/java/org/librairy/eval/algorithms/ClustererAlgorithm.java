package org.librairy.eval.algorithms;

import org.librairy.eval.model.Neighbourhood;
import org.librairy.eval.model.Point;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public interface ClustererAlgorithm {

    /**
     * Add a new point to the vectorial space
     * @param point
     */
    void add(Point point);

    /**
     * Create clusters from the added points
     * @return
     */
    ClustererReport cluster();

    /**
     * Closest neighbours to a given point
     * @param point
     * @param size
     * @return
     */
    Neighbourhood getNeighbourhood(Point point, Integer size);


    /**
     * Locate cluster for a given point
     * @param point
     * @return
     */
    String getCluster(Point point);

    /**
     * Algorithm Label
     * @return
     */
    String getId();

}
