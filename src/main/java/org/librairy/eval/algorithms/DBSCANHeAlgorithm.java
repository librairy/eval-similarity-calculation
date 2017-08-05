package org.librairy.eval.algorithms;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.librairy.eval.expressions.DistributionExpression;
import org.librairy.eval.model.DirichletDistribution;
import org.librairy.eval.model.DirichletPoint;
import org.librairy.metrics.distance.HellingerDistance;
import org.librairy.metrics.distance.JensenShannonDivergence;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class DBSCANHeAlgorithm implements Algorithm {

    private final Integer minPoints;
    private int numPoints;

    public DBSCANHeAlgorithm(Integer minPoints){
        this.minPoints = minPoints;
    }


    @Override
    public List<DistributionExpression> getShapesFrom(List<DirichletDistribution> distributions) {
        this.numPoints      = distributions.size();

        double epsilon = 0.1;
        DistanceMeasure distanceMeasure = (DistanceMeasure) (doubles, doubles1) -> HellingerDistance.apply(doubles,doubles1);
        DBSCANClusterer dbscanClusterer = new DBSCANClusterer(epsilon, minPoints, distanceMeasure);


        List<DirichletPoint> points = distributions.stream().map(d -> new DirichletPoint(d)).collect(Collectors.toList());
        List<Cluster<DirichletPoint>> clusterPoints = dbscanClusterer.cluster(points);


        List<DistributionExpression> expressions = new ArrayList<>();

        if (clusterPoints.isEmpty()) return distributions.stream().map(d -> new DistributionExpression("1",d)).collect(Collectors.toList());

        Integer index = 0;
        for(Cluster<DirichletPoint> centroid : clusterPoints){
            final String expression = String.valueOf(index++);
            centroid.getPoints().stream().forEach(point -> expressions.add(new DistributionExpression(expression, point.getDistribution())));
        }

        if (expressions.size() != distributions.size()){
            List<DirichletPoint> clusteredPoints = clusterPoints.stream().flatMap(cluster -> cluster.getPoints().stream()).collect(Collectors.toList());
            final String expression = String.valueOf(index++);
            points.stream().filter(point -> !clusteredPoints.contains(point)).forEach(p -> expressions.add(new DistributionExpression(expression, p.getDistribution())));
        }

        return expressions;
    }

    @Override
    public Integer getExtraPairs() {
        return Double.valueOf(Math.ceil(Double.valueOf(numPoints)/ Double.valueOf(minPoints))).intValue();
    }

    @Override
    public String toString() {
        return "DBSCAN-He-"+minPoints+" Algorithm";
    }
}
