package org.librairy.eval.algorithms;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.librairy.eval.expressions.DistributionExpression;
import org.librairy.eval.model.DirichletDistribution;
import org.librairy.eval.model.KmeansPoint;
import org.librairy.metrics.distance.JensenShannonDivergence;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class KMeansAlgorithm implements Algorithm {

    @Override
    public List<DistributionExpression> getShapesFrom(List<DirichletDistribution> distributions) {
        KMeansPlusPlusClusterer<KmeansPoint> kmeans = new KMeansPlusPlusClusterer<KmeansPoint>(distributions.get(0).getVector().size(),100, (double[] a, double[] b) ->  JensenShannonDivergence.apply(a, b));

        List<KmeansPoint> points = distributions.stream().map(d -> new KmeansPoint(d)).collect(Collectors.toList());
        List<CentroidCluster<KmeansPoint>> clusters = kmeans.cluster(points);

        return clusters.stream().flatMap(cluster -> cluster.getPoints().stream().map(point -> new DistributionExpression(cluster.toString(), point.getDistribution()))).collect(Collectors.toList());
    }
}
