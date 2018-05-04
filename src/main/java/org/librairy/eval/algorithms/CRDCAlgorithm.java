package org.librairy.eval.algorithms;

import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.librairy.eval.expressions.DistributionExpression;
import org.librairy.eval.model.DirichletDistribution;
import org.librairy.eval.model.Document;
import org.librairy.eval.model.Score;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class CRDCAlgorithm implements Algorithm {

    private final Double threshold;
    private final Integer numTopics;

    public CRDCAlgorithm(Double threshold,Integer numTopics){
        this.threshold = threshold;
        this.numTopics = numTopics;
    }

    @Override
    public List<DistributionExpression> getShapesFrom(List<DirichletDistribution> distributions) {
        //return distributions.stream().map(v -> new TopExpression(top, v)).collect(Collectors.toList());
        return distributions.stream().map(v -> new DistributionExpression(toString(v.getVector()), v)).collect(Collectors.toList());
    }

    public String toString(List<Double> vector) {
        List<Score> sortedVector = IntStream.range(0, vector.size()).mapToObj(i -> new Score(vector.get(i), new Document(String.valueOf(i)), null)).sorted((a, b) -> -a.getValue().compareTo(b.getValue())).collect(Collectors.toList());

        StringBuilder shape = new StringBuilder();
        int index = 0;
        Double acc = 0.0;
        for(int i=0; i< vector.size();i++){
            if (acc >= threshold) break;
            Score res = sortedVector.get(i);
            Integer score = res.getValue().intValue();
            shape.append("t").append(res.getReference().getId()).append("_").append(i).append("|").append(score).append(" ");
            acc += res.getValue();
        }

        return shape.toString().trim();
    }

    public List<Double> toVector(String shape) {
        if (Strings.isNullOrEmpty(shape)) return Collections.emptyList();

        Double[] vector = new Double[numTopics];
        Arrays.fill(vector,0.0);

        String[] topics = shape.split(" ");

        for(int i=0; i< topics.length; i++){

            String topic = topics[i];
            String[] topicValues = topic.split("\\|");
            Double score    = Double.valueOf(topicValues[1]);
            Integer index   = Integer.valueOf(StringUtils.substringAfter(StringUtils.substringBefore(topicValues[0],"_"),"t"));
            vector[index] = score;
        }

        return Arrays.asList(vector);
    }

    @Override
    public Integer getExtraPairs() {
        return 0;
    }

    @Override
    public String toString() {
        return "Cumulative RDC-" + threshold + " Algorithm";
    }
}
