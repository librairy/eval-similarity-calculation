package org.librairy.eval.algorithms;

import org.librairy.eval.expressions.DistributionExpression;
import org.librairy.eval.expressions.TopExpression;
import org.librairy.eval.model.DirichletDistribution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class HierarchicalRDCAlgorithm implements Algorithm {

    private final Integer maxSize;

    public HierarchicalRDCAlgorithm(Integer maxSize){
        this.maxSize = maxSize;
    }

    @Override
    public List<DistributionExpression> getShapesFrom(List<DirichletDistribution> distributions) {

        AtomicInteger top = new AtomicInteger(1);

        Map<String, List<TopExpression>> expressions = distributions.stream().map(v -> new TopExpression(top.get(), v)).collect(Collectors.groupingBy(TopExpression::getExpression));

        top.incrementAndGet();

        List<String> alreadyVisited = new ArrayList<>();

        while(top.get() <= distributions.get(0).getVector().size()) {

            List<Map.Entry<String, List<TopExpression>>> oversizedExpressions = expressions
                    .entrySet()
                    .stream()
                    .filter(entry -> !alreadyVisited.contains(entry.getKey()))
                    .filter(entry -> entry.getValue().size() > maxSize)
                    .collect(Collectors.toList());

            if (oversizedExpressions.isEmpty()) break;

            oversizedExpressions.stream().forEach(entry -> {
                expressions.remove(entry.getKey());

                Map<String, List<TopExpression>> newExpressions = entry.getValue()
                        .stream()
                        .map(e -> new TopExpression(top.get(), e.getDirichletDistribution()))
                        .collect(Collectors.groupingBy(TopExpression::getExpression));

                // aggregate low-frequency distribution
                Map<String, List<TopExpression>> lowFreqExpressions = newExpressions.entrySet().stream()
                        .filter(e -> e.getValue().size() < (maxSize / 2))
                        .flatMap(e -> e.getValue().stream())
                        .map(e -> new TopExpression(top.get() - 1, e.getDirichletDistribution()))
                        .collect(Collectors.groupingBy(TopExpression::getExpression));
                expressions.putAll(lowFreqExpressions);
                lowFreqExpressions.entrySet().stream().forEach(e -> alreadyVisited.add(e.getKey()));

                // individual high-frequency distribution
                Map<String, List<TopExpression>> highFreqExpression = newExpressions.entrySet().stream()
                        .filter(e -> e.getValue().size() >= (maxSize / 2))
                        .flatMap(e -> e.getValue().stream())
                        .collect(Collectors.groupingBy(TopExpression::getExpression));
                expressions.putAll(highFreqExpression);
            });
            top.incrementAndGet();
        }

        return expressions.entrySet().stream().flatMap(entry -> entry.getValue().stream()).collect(Collectors.toList());

    }

    @Override
    public Integer getExtraPairs() {
        return 0;
    }

    @Override
    public String toString() {
        return "Hierarchical-Entropy" + maxSize+ " Algorithm";
    }
}
