package org.librairy.eval.model;

import lombok.Data;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@Data
public class Result {

    String algorithm;

    Integer size;

    Integer topics;

    Double minScore;

    Integer tp;

    Integer tn;

    Integer fp;

    Integer fn;

    Double precision;

    Double recall;

    Integer clusters;

    Integer totalSimilarities;

    Integer calculatedSimilarities;

    Integer minimumSimilarities;

    Long time;


    public Double getEffectiveness(){
        return ((precision*precision)+(recall*recall))/2.0;
    }

    public Double getCost(){
        return (Math.min(Math.max(Double.valueOf(calculatedSimilarities),Double.valueOf(minimumSimilarities)),Double.valueOf(totalSimilarities)) - (Double.valueOf(minimumSimilarities))) / (Double.valueOf(totalSimilarities) - (Double.valueOf(minimumSimilarities)));
    }

    public Double getEfficiency(){
        return Math.max(getEffectiveness() - getCost(),0.0);
    }

    public Double getSavingSimilarities(){
        return 100.0 - (Double.valueOf(calculatedSimilarities)*100.0)/Double.valueOf(totalSimilarities);
    }

    public Double getFMeasure(){
        return 2* (precision*recall)/(precision+recall);
    }

}
