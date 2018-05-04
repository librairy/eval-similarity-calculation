/*
 * Copyright (c) 2017. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.eval.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class DirichletDistribution {

    private List<Double> vector;

    private String id;

    public DirichletDistribution(){}

    public DirichletDistribution(String id, Integer dimension){
        this.id = id;

        Random random = new Random();

        List<Integer> probabilities = IntStream.range(0, dimension).mapToObj(i -> random.nextInt(Double.valueOf(Math.pow(10,random.nextInt(3)+1)).intValue())+1).collect(Collectors.toList());

        Integer total = probabilities.stream().reduce((a,b) -> a+b).get();

        this.vector = probabilities.stream().map(val -> {
            double ratio = Double.valueOf(val) / Double.valueOf(total);

            if (ratio == 0.0){
                System.out.println("val:" + val + " / total: " + total);

            }

            return ratio;

        }).collect(Collectors.toList());
    }

    public DirichletDistribution(String id, List<Double> vector){
        this.id = id;
        this.vector = vector;
    }

    public List<Double> getVector() {
        return vector;
    }

    public void setVector(List<Double> vector) {
        this.vector = vector;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    public DoubleSummaryStatistics getStats(){
        return vector.stream().collect(DoubleSummaryStatistics::new, DoubleSummaryStatistics::accept, DoubleSummaryStatistics::combine);
    }

    @Override
    public String toString() {
        return "DirichletDistribution{" +
                "id='" + id + '\'' +
                ", vector=" + vector +
                '}';
    }
}
