/*
 * Copyright (c) 2017. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.eval.experiments;

import org.junit.Test;
import org.librairy.eval.algorithms.*;
import org.librairy.eval.expressions.DistributionExpression;
import org.librairy.eval.model.DirichletDistribution;
import org.librairy.eval.model.Similarity;
import org.librairy.metrics.similarity.JensenShannonSimilarity;

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
        Double minScore         = 0.9;
        List<Integer> sizes = Arrays.asList(new Integer[]{
                5000
//                ,500,1000,2000,3000,4000,5000,10000,20000
        });

        for(Integer size: sizes){
            Integer recommendedTopics   = Double.valueOf(2*Math.sqrt(size/2)).intValue();
            //Integer numTopics           = recommendedTopics != 0? recommendedTopics : 2;
            Integer numTopics           = 15;
            List<DirichletDistribution> corpus = createSampling(size, numTopics);
            Map<String, List<Similarity>> goldStandard = createGoldStandard(corpus, minScore,(p,q) -> JensenShannonSimilarity.apply(p,q));
            summary.append("gradient\t").append(evaluationOf(size, numTopics,  minScore,corpus,goldStandard,new TDCAlgorithm(0.99),(p,q) -> JensenShannonSimilarity.apply(p,q))).append("\n");
            summary.append("entropy\t").append(evaluationOf(size, numTopics, minScore,corpus,goldStandard,new RDCAlgorithm(1),(p,q) -> JensenShannonSimilarity.apply(p,q))).append("\n");
            summary.append("kmeans\t").append(evaluationOf(size, numTopics, minScore,corpus,goldStandard,new KMeansJSAlgorithm(100),(p,q) -> JensenShannonSimilarity.apply(p,q))).append("\n");
            summary.append("dbscan\t").append(evaluationOf(size, numTopics, minScore,corpus,goldStandard,new DBSCANJSAlgorithm(2),(p, q) -> JensenShannonSimilarity.apply(p,q))).append("\n");
            summary.append("CRDC\t").append(evaluationOf(size, numTopics, minScore,corpus,goldStandard,new CRDCAlgorithm(0.9),(p, q) -> JensenShannonSimilarity.apply(p,q))).append("\n");
        }
        System.out.println(summary);
    }


    @Override
    protected List<DistributionExpression> getShapesFrom(List<DirichletDistribution> distributions) {
        return Collections.emptyList();
    }

}
