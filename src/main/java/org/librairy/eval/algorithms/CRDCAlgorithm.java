package org.librairy.eval.algorithms;

import org.librairy.eval.model.Neighbour;
import org.librairy.eval.model.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class CRDCAlgorithm extends AbstractKeyValueAlgorithm {

    private static final Logger LOG = LoggerFactory.getLogger(CRDCAlgorithm.class);


    public CRDCAlgorithm(Double threshold){
        super("CRDC",threshold);
    }


    @Override
    public String getCluster(Point point) {
        List<Double> vector = point.getVector();
        List<Neighbour> sortedVector = IntStream.range(0, vector.size()).mapToObj(i -> new Neighbour(new Point(String.valueOf(i)), vector.get(i))).sorted((a, b) -> -a.getScore().compareTo(b.getScore())).collect(Collectors.toList());

        StringBuilder shape = new StringBuilder();
        Double acc = 0.0;
        for(int i=0; i< vector.size();i++){
            if (acc >= threshold) break;
            Neighbour res = sortedVector.get(i);
            Integer score = res.getScore().intValue();
            //shape.append("t").append(res.getPoint().getId()).append("_").append(i).append("|").append(score).append(" ");
            shape.append("t").append(res.getPoint().getId()).append("|");
            acc += res.getScore();
        }

        return shape.toString().trim();
    }
}
