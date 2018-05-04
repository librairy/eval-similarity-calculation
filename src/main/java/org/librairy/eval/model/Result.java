package org.librairy.eval.model;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class Result {

    String algorithm;

    Integer size;

    Integer topics;

    Double minScore;

    Integer tp;

    Integer tn;

    Integer fp;

    Integer fn;

    Integer clusters;

    Integer totalSimilarities;

    Integer calculatedSimilarities;

    Integer minimumSimilarities;

    Long time;


    public Double getEffectiveness(){
        Double precision = getPrecision();
        Double recall = getRecall();
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

    public String getAlgorithm() {
        return algorithm;
    }

    public Integer getSize() {
        return size;
    }

    public Integer getTopics() {
        return topics;
    }

    public Double getMinScore() {
        return minScore;
    }

    public Integer getTp() {
        return tp;
    }

    public Integer getTn() {
        return tn;
    }

    public Integer getFp() {
        return fp;
    }

    public Integer getFn() {
        return fn;
    }

    public Integer getClusters() {
        return clusters;
    }

    public Integer getTotalSimilarities() {
        return totalSimilarities;
    }

    public Integer getCalculatedSimilarities() {
        return calculatedSimilarities;
    }

    public Integer getMinimumSimilarities() {
        return minimumSimilarities;
    }

    public Long getTime() {
        return time;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public void setTopics(Integer topics) {
        this.topics = topics;
    }

    public void setMinScore(Double minScore) {
        this.minScore = minScore;
    }

    public void setTp(Integer tp) {
        this.tp = tp;
    }

    public void setTn(Integer tn) {
        this.tn = tn;
    }

    public void setFp(Integer fp) {
        this.fp = fp;
    }

    public void setFn(Integer fn) {
        this.fn = fn;
    }

    public void setClusters(Integer clusters) {
        this.clusters = clusters;
    }

    public void setTotalSimilarities(Integer totalSimilarities) {
        this.totalSimilarities = totalSimilarities;
    }

    public void setCalculatedSimilarities(Integer calculatedSimilarities) {
        this.calculatedSimilarities = calculatedSimilarities;
    }

    public void setMinimumSimilarities(Integer minimumSimilarities) {
        this.minimumSimilarities = minimumSimilarities;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
