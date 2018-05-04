package org.librairy.eval.model;

import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class Corpora {

    List<DirichletDistribution> documents;

    public Corpora(List<DirichletDistribution> documents) {
        this.documents = documents;
    }

    public List<DirichletDistribution> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DirichletDistribution> documents) {
        this.documents = documents;
    }

    @Override
    public String toString() {
        return "Corpora{" +
                "documents=" + documents +
                '}';
    }
}
