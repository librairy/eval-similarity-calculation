package org.librairy.eval.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class Neighbourhood {

    private static final Logger LOG = LoggerFactory.getLogger(Neighbourhood.class);

    private Point reference;

    private List<Neighbour> closestNeighbours;

    private Integer numberOfNeighbours;

    public Neighbourhood() {
    }

    public Neighbourhood(Point reference, List<Neighbour> closestNeighbours) {
        this.reference = reference;
        this.closestNeighbours = closestNeighbours;
        this.numberOfNeighbours = closestNeighbours.size();
    }

    public Point getReference() {
        return reference;
    }

    public void setReference(Point reference) {
        this.reference = reference;
    }

    public List<Neighbour> getClosestNeighbours() {
        return closestNeighbours;
    }

    public void setClosestNeighbours(List<Neighbour> closestNeighbours) {
        this.closestNeighbours = closestNeighbours;
    }

    public Integer getNumberOfNeighbours() {
        return numberOfNeighbours;
    }

    public void setNumberOfNeighbours(Integer numberOfNeighbours) {
        this.numberOfNeighbours = numberOfNeighbours;
    }

    @Override
    public String toString() {
        return "Neighbourhood{" +
                "reference=" + reference +
                ", closestNeighbours=" + closestNeighbours +
                ", numberOfNeighbours=" + numberOfNeighbours +
                '}';
    }
}
