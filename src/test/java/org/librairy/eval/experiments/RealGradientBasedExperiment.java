/*
 * Copyright (c) 2017. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.eval.experiments;

import es.cbadenes.lab.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.librairy.boot.model.domain.resources.Item;
import org.librairy.boot.storage.dao.DomainsDao;
import org.librairy.boot.storage.generator.URIGenerator;
import org.librairy.eval.Config;
import org.librairy.eval.algorithms.EntropyAlgorithm;
import org.librairy.eval.algorithms.GradientAlgorithm;
import org.librairy.eval.dao.ShapeDao;
import org.librairy.eval.expressions.DistributionExpression;
import org.librairy.eval.expressions.GradientExpression;
import org.librairy.eval.model.DirichletDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@Category(IntegrationTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Config.class)
public class RealGradientBasedExperiment extends AbstractExperiment {


    private static final Logger LOG = LoggerFactory.getLogger(RealGradientBasedExperiment.class);

    Integer numVectors      = 1000;
    Integer numTopics       = 40;
    Integer numTopSimilar   = 5;
    Double threshold        = 0.75;
    Double ratio            = 0.99;

    @Autowired
    ShapeDao shapeDao;

    @Autowired
    DomainsDao domainsDao;

    @Test
    public void evaluate(){

        evaluateDomain("patentsNSGA");

    }

    @Test
    public void evaluateAll(){

        List<String> domains = Arrays.asList(new String[]{
                "blueBottle","patents","patents133","patents424","patentsNSGA"
        });


        StringBuilder summary =new StringBuilder();
        for(String domain: domains){
            summary.append(evaluateDomain(domain)).append("\n");
        }
        System.out.println(summary);
    }


    private List<DirichletDistribution> createSampling(String domainUri, Integer maxSize){

        List<DirichletDistribution> vectors = new ArrayList<>();

        Optional<String> offset = Optional.empty();

        Integer size = 500;
        Integer partialSize = 0;

        Boolean finished = false;
        AtomicInteger counter = new AtomicInteger(0);
        Integer dim = 0;
        while(!finished){
            LOG.info("["+counter.getAndIncrement()+"] Getting " + size + " documents");
            List<Item> docs = domainsDao.listItems(domainUri, size, offset, false);
            for (Item item : docs){

                if (partialSize >= maxSize) continue;
                List<Double> distribution = shapeDao.get(domainUri, item.getUri());
                if (dim != 0 && distribution.size() != dim) continue;
                dim = distribution.size();
                DirichletDistribution dd = new DirichletDistribution(URIGenerator.retrieveId(item.getUri()), distribution);
                vectors.add(dd);
                partialSize+=1;
            }

            finished = ((docs.size() < size) || (partialSize >= maxSize));

            if (!finished) offset = Optional.of(URIGenerator.retrieveId(docs.get(docs.size()-1).getUri()));

        }

        return vectors;
    }


    @Override
    protected List<DistributionExpression> getShapesFrom(List<DirichletDistribution> distributions) {
        return distributions.stream().map(v -> new GradientExpression(ratio, v)).collect(Collectors.toList());
    }

    private String evaluateDomain(String domainId){
        String domainUri = "http://librairy.linkeddata.es/resources/domains/"+domainId;
        Integer maxSize = 1000;

        List<DirichletDistribution> vectors = createSampling(domainUri,maxSize);

        // threshold = 0.75
        ratio = 0.99;
        threshold = 0.75;
        StringBuilder summary = new StringBuilder();
        summary.append(IntStream.range(0, 1)
                .mapToObj(i -> evaluationOf(maxSize, numTopics, threshold, new GradientAlgorithm(ratio)))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");

        ratio = 0.90;
        threshold = 0.75;
        summary.append(IntStream.range(0, 1)
                .mapToObj(i -> evaluationOf(maxSize, numTopics, threshold, new GradientAlgorithm(ratio)))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");

        ratio = 0.85;
        threshold = 0.75;
        summary.append(IntStream.range(0, 1)
                .mapToObj(i -> evaluationOf(maxSize, numTopics, threshold, new GradientAlgorithm(ratio)))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");

        ratio = 0.99;
        threshold = 0.6;
        summary.append(IntStream.range(0, 1)
                .mapToObj(i -> evaluationOf(maxSize, numTopics, threshold, new GradientAlgorithm(ratio)))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");

        ratio = 0.99;
        threshold = 0.5;
        summary.append(IntStream.range(0, 1)
                .mapToObj(i -> evaluationOf(maxSize, numTopics, threshold, new GradientAlgorithm(ratio)))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");

        return summary.toString();
    }
}
