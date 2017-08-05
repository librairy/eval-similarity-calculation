package org.librairy.eval.model;

import lombok.Data;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@Data
public class Related {

    DirichletDistribution distribution;

    Double score = 0.0;

    public String toString(){

        NumberFormat formatter = new DecimalFormat("#0.00");

        return "["+formatter.format(score)+"] - [" +formatter.format(distribution.getStats().getAverage())+"/"+formatter.format(distribution.getStats().getMin()) +"/"+formatter.format(distribution.getStats().getMax())+"] - ["+distribution.getSortedTopics(0.95)+"]";

    }
}
