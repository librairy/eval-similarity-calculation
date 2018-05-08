/*
 * Copyright (c) 2017. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.eval.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class Point {

    private static final Logger LOG = LoggerFactory.getLogger(Point.class);

    private List<Double> vector;

    private String id;

    public Point(){}

    public Point(String id){
        this.id = id;
        this.vector = Collections.emptyList();
    }

    public void initializeRamdon(Integer dimension){
        Random random = new Random();

        List<Integer> probabilities = IntStream.range(0, dimension).mapToObj(i -> random.nextInt(Double.valueOf(Math.pow(10,random.nextInt(3)+1)).intValue())+1).collect(Collectors.toList());

        Integer total = probabilities.stream().reduce((a,b) -> a+b).get();

        this.vector = probabilities.stream().map(val -> {
            double ratio = Double.valueOf(val) / Double.valueOf(total);

            if (ratio == 0.0){
                LOG.debug("val:" + val + " / total: " + total);
            }

            return ratio;

        }).collect(Collectors.toList());
    }

    public Point(String id, List<Double> vector){
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        return id != null ? id.equals(point.id) : point.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Point{" +
                "id='" + id + '\'' +
                ", vector=" + vector +
                '}';
    }
}
