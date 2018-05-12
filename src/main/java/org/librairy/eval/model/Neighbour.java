package org.librairy.eval.model;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class Neighbour {

    Point point;

    Double score = 0.0;

    public Neighbour() {
    }

    public Neighbour(Point distribution, Double score) {
        this.point = distribution;
        this.score = score;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Neighbour{" +
                "point=" + point +
                ", score=" + score +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Neighbour neighbour = (Neighbour) o;

        return point != null ? point.equals(neighbour.point) : neighbour.point == null;

    }

    @Override
    public int hashCode() {
        return point != null ? point.hashCode() : 0;
    }
}
