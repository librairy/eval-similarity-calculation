package org.librairy.eval.algorithms;

import org.librairy.eval.expressions.DistributionExpression;
import org.librairy.eval.model.DirichletDistribution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class RandomizeSelectionAlgorithm implements Algorithm {

    private final Integer numClusters;


    public RandomizeSelectionAlgorithm(Integer numClusters){
        this.numClusters = numClusters;
    }


    @Override
    public List<DistributionExpression> getShapesFrom(List<DirichletDistribution> distributions) {

        Map<Integer, Integer> distribution = new HashMap<>();
        IntStream.range(0,numClusters).forEach(i -> distribution.put(i,0));

        int maxClusterSize = Double.valueOf(Math.ceil(Double.valueOf(distributions.size()) / Double.valueOf(numClusters))).intValue();

        Random random = new Random();


        return distributions.stream().map(v -> {

            int cluster = 0;
            boolean assigned = false;
            while(!assigned){
                cluster = random.nextInt(numClusters);
                Integer currentSize = distribution.get(cluster);
                if (currentSize < maxClusterSize) break;
            }
            distribution.put(cluster, distribution.get(cluster)+1);
            return new DistributionExpression(String.valueOf(cluster), v);

        }).collect(Collectors.toList());
    }

    @Override
    public Integer getExtraPairs() {
        return 0;
    }


    @Override
    public String toString() {
        return "Randomize Selection " + numClusters + " Algorithm";
    }

}
