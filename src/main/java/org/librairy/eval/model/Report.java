package org.librairy.eval.model;

import org.librairy.eval.algorithms.ClustererAlgorithm;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class Report {

    ClustererAlgorithm algorithm;

    Long tp                     = 0l;

    Long fp                     = 0l;

    Long fn                     = 0l;

    Long numberOfClusters       = 0l;

    Long maxSimilarities        = 0l;

    Long calculatedSimilarities = 0l;

    Long minSimilarities        = 0l;

    Long trainingSize           = 0l;

    Long testSize               = 0l;

    public Report() {
    }

    public Report(ClustererAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public Double getEffectiveness(){
        Double precision = getPrecision();
        Double recall = getRecall();
        return ((precision*precision)+(recall*recall))/2.0;
    }

    public Double getCost(){
        return (Math.min(Math.max(Double.valueOf(calculatedSimilarities),Double.valueOf(minSimilarities)),Double.valueOf(maxSimilarities)) - (Double.valueOf(minSimilarities))) / (Double.valueOf(maxSimilarities) - (Double.valueOf(minSimilarities)));
    }

    public Long increaseCalculatedSimilarities(Long num){
        calculatedSimilarities += num;
        return calculatedSimilarities;
    }

    public void update(Neighbourhood reference, Neighbourhood custom){

        tp += custom.getClosestNeighbours().stream().filter( point -> reference.getClosestNeighbours().contains(point)).count();
        fp += custom.getClosestNeighbours().stream().filter( point -> !reference.getClosestNeighbours().contains(point)).count();
        fn += reference.getClosestNeighbours().stream().filter( point -> !custom.getClosestNeighbours().contains(point)).count();

        increaseCalculatedSimilarities(Long.valueOf(custom.getNumberOfNeighbours()));
    }

    public Double getEfficiency(){
        return Math.max(getEffectiveness() - getCost(),0.0);
    }

    public Double getSavingSimilarities(){
        return 100.0 - (Double.valueOf(calculatedSimilarities)*100.0)/Double.valueOf(maxSimilarities);
    }

    public Double getFMeasure(){
        Double precision = getPrecision();
        Double recall = getRecall();
        return 2* (precision*recall)/(precision+recall);
    }

    public Double getPrecision(){
        return Double.valueOf(tp) / (Double.valueOf(tp)+ Double.valueOf(fp));
    }


    public Double getRecall(){
        return Double.valueOf(tp) / (Double.valueOf(tp)+ Double.valueOf(fn));
    }

    public void setSize(Integer training, Integer test, Integer numNeighbours) {
        this.trainingSize       = Long.valueOf(training);
        this.testSize           = Long.valueOf(test);

        this.maxSimilarities    = Long.valueOf(test*training);
        this.minSimilarities    = Long.valueOf(test*numNeighbours);
    }

    public Long getNumberOfClusters() {
        return numberOfClusters;
    }

    public void setNumberOfClusters(Long numberOfClusters) {
        this.numberOfClusters = numberOfClusters;
    }

    public ClustererAlgorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(ClustererAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public Long getTp() {
        return tp;
    }

    public void setTp(Long tp) {
        this.tp = tp;
    }

    public Long getFp() {
        return fp;
    }

    public void setFp(Long fp) {
        this.fp = fp;
    }

    public Long getFn() {
        return fn;
    }

    public void setFn(Long fn) {
        this.fn = fn;
    }

    public Long getMaxSimilarities() {
        return maxSimilarities;
    }

    public void setMaxSimilarities(Long maxSimilarities) {
        this.maxSimilarities = maxSimilarities;
    }

    public Long getCalculatedSimilarities() {
        return calculatedSimilarities;
    }

    public void setCalculatedSimilarities(Long calculatedSimilarities) {
        this.calculatedSimilarities = calculatedSimilarities;
    }

    public Long getMinSimilarities() {
        return minSimilarities;
    }

    public void setMinSimilarities(Long minSimilarities) {
        this.minSimilarities = minSimilarities;
    }

    public Long getTrainingSize() {
        return trainingSize;
    }

    public void setTrainingSize(Long trainingSize) {
        this.trainingSize = trainingSize;
    }

    public Long getTestSize() {
        return testSize;
    }

    public void setTestSize(Long testSize) {
        this.testSize = testSize;
    }
}
