/*
 * Copyright (c) 2017. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.eval.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@Data
public class DirichletDistribution {

    private List<Double> vector;

    private String id;

    public DirichletDistribution(){}

    public DirichletDistribution(String id, Integer dimension){
        this.id = id;
        Double[] vector = new Double[dimension];


        List<Integer> indexes = new ArrayList<>();
        for (int i=0;i<dimension;i++){
            indexes.add(i);
        }

        Double acc = new Double(0.0);
        while(!indexes.isEmpty()){

            Collections.shuffle(indexes);

            Integer index = indexes.get(0);
            Double limit = 1.0 - acc;
            Double partialLimit = 0.9 * limit;
            Double val = Math.random()*partialLimit;
            if (indexes.size() == 1){
                val = limit;
            }
            if (val == 0.0){
                val = limit/indexes.size();
            }
            vector[index]= val;
            acc += val;
            indexes.remove(index);
        }

        this.vector = Arrays.asList(vector);
    }

    public DirichletDistribution(String id, List<Double> vector){
        this.id = id;
        this.vector = vector;
    }
}
