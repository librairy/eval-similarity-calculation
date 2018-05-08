package org.librairy.eval.algorithms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class ClustererReport {

    private static final Logger LOG = LoggerFactory.getLogger(ClustererReport.class);

    private Long numClusters;

    private Long numComparisons;

    public ClustererReport() {
    }

    public Long getNumClusters() {
        return numClusters;
    }

    public void setNumClusters(Long numClusters) {
        this.numClusters = numClusters;
    }

    public Long getRequiredComparisons() {
        return numComparisons;
    }

    public void setNumComparisons(Long numComparisons) {
        this.numComparisons = numComparisons;
    }

    @Override
    public String toString() {
        return "ClustererReport{" +
                "numClusters=" + numClusters +
                ", numComparisons=" + numComparisons +
                '}';
    }
}
