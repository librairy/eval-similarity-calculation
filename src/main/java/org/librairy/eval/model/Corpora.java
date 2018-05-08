package org.librairy.eval.model;

import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class Corpora {

    List<Point> documents;

    public Corpora(List<Point> documents) {
        this.documents = documents;
    }

    public List<Point> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Point> documents) {
        this.documents = documents;
    }

    @Override
    public String toString() {
        return "Corpora{" +
                "documents=" + documents +
                '}';
    }
}
