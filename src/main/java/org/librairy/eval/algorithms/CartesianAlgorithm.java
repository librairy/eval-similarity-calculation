package org.librairy.eval.algorithms;

import org.librairy.eval.model.Neighbourhood;
import org.librairy.eval.model.Point;

import java.nio.file.Path;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class CartesianAlgorithm implements ClustererAlgorithm {



    public String clusterOf(Point dirichletDistribution) {
        return "cluster";
    }

    @Override
    public String toString() {
        return "Cartesian Algorithm";
    }

    @Override
    public void add(Point point) {

    }

    @Override
    public ClustererReport cluster() {
        return null;
    }

    @Override
    public Neighbourhood getNeighbourhood(Point point, Integer size) {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }
}
