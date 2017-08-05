package org.librairy.eval.algorithms;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.librairy.eval.expressions.DistributionExpression;
import org.librairy.eval.model.DirichletDistribution;
import org.librairy.eval.model.DirichletPoint;
import org.librairy.metrics.distance.HellingerDistance;
import org.librairy.metrics.distance.JensenShannonDivergence;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class KMeansHeAlgorithm implements Algorithm {

    private Integer iterations;
    private int numCluster;
    private int numPoints;


    public KMeansHeAlgorithm(Integer iterations){
        this.iterations = iterations;
    }

    @Override
    public List<DistributionExpression> getShapesFrom(List<DirichletDistribution> distributions) {
        this.numCluster     = distributions.get(0).getVector().size();
        this.numPoints      = distributions.size();
        KMeansPlusPlusClusterer<DirichletPoint> kmeans = new KMeansPlusPlusClusterer<DirichletPoint>(numCluster,iterations, (double[] a, double[] b) ->  HellingerDistance.apply(a, b));

        List<DirichletPoint> points = distributions.stream().map(d -> new DirichletPoint(d)).collect(Collectors.toList());
        List<CentroidCluster<DirichletPoint>> clusters = kmeans.cluster(points);

        return clusters.stream().flatMap(cluster -> cluster.getPoints().stream().map(point -> new DistributionExpression(cluster.toString(), point.getDistribution()))).collect(Collectors.toList());
    }

    @Override
    public Integer getExtraPairs() {
        return iterations * (numPoints*numCluster);
    }

    @Override
    public String toString() {
        return "KMeans-He-"+ iterations + " Algorithm";
    }
}
