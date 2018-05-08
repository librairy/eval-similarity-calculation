package org.librairy.eval.algorithms;

import org.librairy.eval.model.Neighbourhood;
import org.librairy.eval.model.Point;
import org.librairy.eval.model.Neighbour;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class CRDCAlgorithm implements ClustererAlgorithm {

    private final Double threshold;

    public CRDCAlgorithm(Double threshold){
        this.threshold = threshold;
    }

    @Override
    public void add(Point point) {
        // calculate cluster
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

    private String clusterOf(Point point) {
        List<Double> vector = point.getVector();
        List<Neighbour> sortedVector = IntStream.range(0, vector.size()).mapToObj(i -> new Neighbour(new Point(String.valueOf(i)), vector.get(i))).sorted((a, b) -> -a.getScore().compareTo(b.getScore())).collect(Collectors.toList());

        StringBuilder shape = new StringBuilder();
        Double acc = 0.0;
        for(int i=0; i< vector.size();i++){
            if (acc >= threshold) break;
            Neighbour res = sortedVector.get(i);
            Integer score = res.getScore().intValue();
            shape.append("t").append(res.getPoint().getId()).append("_").append(i).append("|").append(score).append(" ");
            acc += res.getScore();
        }

        return shape.toString().trim();
    }

    @Override
    public String toString() {
        return "Cumulative RDC-" + threshold + " Algorithm";
    }
}
