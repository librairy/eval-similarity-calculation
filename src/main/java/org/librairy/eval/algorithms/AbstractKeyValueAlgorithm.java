package org.librairy.eval.algorithms;

import org.librairy.eval.metrics.JensenShannon;
import org.librairy.eval.model.KeyStore;
import org.librairy.eval.model.Neighbour;
import org.librairy.eval.model.Neighbourhood;
import org.librairy.eval.model.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public abstract class AbstractKeyValueAlgorithm implements ClustererAlgorithm {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractKeyValueAlgorithm.class);
    private final KeyStore keystore;
    private final String id;
    protected Double threshold;
    private Set labels;

    public AbstractKeyValueAlgorithm(String id, Double threshold){
        this.id = id;
        this.threshold = threshold;
        this.labels  = new TreeSet();
        this.keystore = new KeyStore("target/"+getId());
    }

    @Override
    public void add(Point point) {
        String label = getCluster(point);
        this.labels.add(label);
        this.keystore.add(point, label);
    }

    @Override
    public ClustererReport cluster() {
        ClustererReport report = new ClustererReport();
        report.setNumClusters(Long.valueOf(labels.size()));
        report.setNumComparisons(0l);
        return report;
    }

    @Override
    public Neighbourhood getNeighbourhood(Point point, Integer size) {

        LOG.debug("Creating neighbourhood around " + point + " with "+ size + " neighbours");
        Neighbourhood neighbourhood = new Neighbourhood();
        String label = getCluster(point);
        List<Neighbour> neighbours = keystore.get(label).parallelStream().map(neighbourPoint -> new Neighbour(neighbourPoint, JensenShannon.similarity(point.getVector(), neighbourPoint.getVector()))).filter(a -> a != null).collect(Collectors.toList());

        List<Neighbour> topNeighbours = neighbours.parallelStream().sorted((a, b) -> -a.getScore().compareTo(b.getScore())).limit(size).collect(Collectors.toList());

        neighbourhood.setNumberOfNeighbours(neighbours.size());
        neighbourhood.setReference(point);
        neighbourhood.setClosestNeighbours(topNeighbours);
        return neighbourhood;
    }

    public abstract String getCluster(Point point);

    @Override
    public String getId() {
        return id+"-"+threshold;
    }

    @Override
    public String toString() {
        return getId() + " Algorithm";
    }
}
