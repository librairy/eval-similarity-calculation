/*
 * Copyright (c) 2017. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.eval.experiments;

import org.junit.Test;
import org.librairy.eval.algorithms.EntropyAlgorithm;
import org.librairy.eval.algorithms.GradientAlgorithm;
import org.librairy.eval.algorithms.KMeansAlgorithm;
import org.librairy.eval.expressions.DistributionExpression;
import org.librairy.eval.model.DirichletDistribution;
import org.librairy.eval.model.Similarity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class SyntheticComparisonExperiment extends AbstractExperiment {

    @Test
    public void evaluate(){

        StringBuilder summary = new StringBuilder();


//        Integer numTopics       = 40;
        Integer numTopSimilar   = 5;
        Double minScore         = 0.75;
        List<Integer> sizes = Arrays.asList(new Integer[]{
                100,500,1000,2000,3000,4000,5000,10000,20000
        });

        for(Integer size: sizes){
            Integer recommendedTopics   = Double.valueOf(2*Math.sqrt(size/2)).intValue();
            Integer numTopics           = recommendedTopics != 0? recommendedTopics : 2;
            List<DirichletDistribution> corpus = createSampling(size, numTopics);
            Map<String, List<Similarity>> goldStandard = createGoldStandard(corpus, minScore);
            summary.append("gradient\t").append(evaluationOf(size, numTopics,  minScore,corpus,goldStandard,new GradientAlgorithm(0.99))).append("\n");
            summary.append("entropy\t").append(evaluationOf(size, numTopics, minScore,corpus,goldStandard,new EntropyAlgorithm(1))).append("\n");
            summary.append("kmeans\t").append(evaluationOf(size, numTopics, minScore,corpus,goldStandard,new KMeansAlgorithm(100))).append("\n");
        }
        System.out.println(summary);
    }


    @Override
    protected List<DistributionExpression> getShapesFrom(List<DirichletDistribution> distributions) {
        return Collections.emptyList();
    }

}
