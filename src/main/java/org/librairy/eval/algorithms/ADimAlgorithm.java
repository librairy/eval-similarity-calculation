package org.librairy.eval.algorithms;

import com.google.common.base.Strings;
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
public class ADimAlgorithm extends AbstractKeyValueAlgorithm{

    private static final Logger LOG = LoggerFactory.getLogger(ADimAlgorithm.class);

    public ADimAlgorithm(Double threshold){
        super("ADim",threshold);
    }


    @Override
    public String getCluster(Point point) {
        List<Double> vector = point.getVector();
        String label = IntStream.range(0, vector.size()).mapToObj(i -> new Neighbour(new Point("t" + String.valueOf(i)), vector.get(i))).filter(neighbour -> neighbour.getScore() >= threshold).sorted((a, b) -> a.getPoint().getId().compareTo(b.getPoint().getId())).map(neighbour -> neighbour.getPoint().getId()).collect(Collectors.joining("|"));
        return Strings.isNullOrEmpty(label)? "empty" : label;
    }
}
