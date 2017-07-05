package org.librairy.eval.algorithms;

import org.librairy.eval.expressions.DistributionExpression;
import org.librairy.eval.expressions.TopExpression;
import org.librairy.eval.model.DirichletDistribution;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class HierarchicalEntropyAlgorithm implements Algorithm {

    private final Integer maxSize;

    public HierarchicalEntropyAlgorithm(Integer maxSize){
        this.maxSize = maxSize;
    }

    @Override
    public List<DistributionExpression> getShapesFrom(List<DirichletDistribution> distributions) {

        AtomicInteger top = new AtomicInteger(1);

        Map<String, List<TopExpression>> expressions = distributions.stream().map(v -> new TopExpression(top.get(), v)).collect(Collectors.groupingBy(TopExpression::getExpression));

        top.incrementAndGet();

        while(top.get() <= distributions.get(0).getVector().size()) {

            List<Map.Entry<String, List<TopExpression>>> oversizeExpressions = expressions
                    .entrySet()
                    .parallelStream()
                    .filter(entry -> entry.getValue().size() > maxSize)
                    .collect(Collectors.toList());

            if (oversizeExpressions.isEmpty()) break;

            oversizeExpressions.forEach(entry -> {
                expressions.remove(entry.getKey());

                Map<String, List<TopExpression>> newExpressions = entry.getValue()
                        .parallelStream()
                        .map(e -> new TopExpression(top.get(), e.getDirichletDistribution()))
                        .collect(Collectors.groupingBy(TopExpression::getExpression));
                expressions.putAll(newExpressions);
            });
            top.incrementAndGet();
        }

        return expressions.entrySet().parallelStream().flatMap(entry -> entry.getValue().stream()).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Hierarchical-Entropy" + maxSize+ " Algorithm";
    }
}
