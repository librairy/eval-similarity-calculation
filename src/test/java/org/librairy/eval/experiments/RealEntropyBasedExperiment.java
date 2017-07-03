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
import org.librairy.eval.dao.ShapeDao;
import org.librairy.eval.expressions.TopExpression;
import org.librairy.eval.model.DirichletDistribution;
import org.librairy.eval.expressions.DistributionExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
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
public class RealEntropyBasedExperiment extends AbstractExperiment {


    private static final Logger LOG = LoggerFactory.getLogger(RealEntropyBasedExperiment.class);

    Integer numTopSimilar   = 5;
    Double threshold        = 0.75;
    Integer top             = 1;

    @Autowired
    ShapeDao shapeDao;

    @Autowired
    DomainsDao domainsDao;

    @Test
    public void evaluate(){

        String domainUri = "http://librairy.linkeddata.es/resources/domains/patentsNSGA";


        Integer maxSize = 1000;

        List<DirichletDistribution> vectors = createSampling(domainUri,maxSize);

        // threshold = 0.75
        StringBuilder summary = new StringBuilder();
        summary.append(IntStream.range(0, 1)
                .mapToObj(i -> evaluationOf(vectors.size(), vectors.get(0).getVector().size(), numTopSimilar, threshold, vectors))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");

        top = 2;
        summary.append(IntStream.range(0, 1)
                .mapToObj(i -> evaluationOf(vectors.size(), vectors.get(0).getVector().size(), numTopSimilar, threshold, vectors))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");

        top = 3;
        summary.append(IntStream.range(0, 1)
                .mapToObj(i -> evaluationOf(vectors.size(), vectors.get(0).getVector().size(), numTopSimilar, threshold, vectors))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");

        top = 1;
        threshold = 0.6;
        summary.append(IntStream.range(0, 1)
                .mapToObj(i -> evaluationOf(vectors.size(), vectors.get(0).getVector().size(), numTopSimilar, threshold, vectors))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");

        threshold = 0.5;
        summary.append(IntStream.range(0, 1)
                .mapToObj(i -> evaluationOf(vectors.size(), vectors.get(0).getVector().size(), numTopSimilar, threshold, vectors))
                .map(r -> r.toString())
                .collect(Collectors.joining("\n"))).append("\n");

        System.out.println(summary);

    }


    private List<DirichletDistribution> createSampling(String domainUri, Integer maxSize){

        List<DirichletDistribution> vectors = new ArrayList<>();

        Optional<String> offset = Optional.empty();

        Integer size = 500;
        Integer partialSize = 0;

        Boolean finished = false;
        AtomicInteger counter = new AtomicInteger(0);

        while(!finished){
            LOG.info("["+counter.getAndIncrement()+"] Getting " + size + " documents");
            List<Item> docs = domainsDao.listItems(domainUri, size, offset, false);
            for (Item item : docs){

                if (partialSize >= maxSize) continue;
                List<Double> distribution = shapeDao.get(domainUri, item.getUri());
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
        return distributions.stream().map(v -> new TopExpression(top, v)).collect(Collectors.toList());
    }

}
