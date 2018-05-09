package org.librairy.eval;

import org.junit.Test;
import org.librairy.eval.algorithms.ADimAlgorithm;
import org.librairy.eval.algorithms.CRDCAlgorithm;
import org.librairy.eval.algorithms.ClustererAlgorithm;
import org.librairy.eval.model.Evaluation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class EvaluationsIntTest {

    private static final Logger LOG = LoggerFactory.getLogger(EvaluationsIntTest.class);

    @Test
    public void test1(){
        runTest("test1",Arrays.asList(new ClustererAlgorithm[]{new CRDCAlgorithm(0.2), new ADimAlgorithm(0.1)}),25);
    }

    @Test
    public void test2(){
        runTest("test2",Arrays.asList(new ClustererAlgorithm[]{new CRDCAlgorithm(0.2), new ADimAlgorithm(0.1)}),25);
    }

    @Test
    public void test3(){
        runTest("test3",Arrays.asList(new ClustererAlgorithm[]{new CRDCAlgorithm(0.2), new ADimAlgorithm(0.1)}),25);
    }

    @Test
    public void test4(){
        runTest("test4",Arrays.asList(new ClustererAlgorithm[]{new CRDCAlgorithm(0.2), new ADimAlgorithm(0.1)}),25);
    }

    @Test
    public void test5(){
        runTest("test5",Arrays.asList(new ClustererAlgorithm[]{new CRDCAlgorithm(0.2), new ADimAlgorithm(0.1)}),25);
    }

    @Test
    public void test6(){
        runTest("test6",Arrays.asList(new ClustererAlgorithm[]{new CRDCAlgorithm(0.2), new ADimAlgorithm(0.1)}),25);
    }

    @Test
    public void test7(){
        runTest("test7",Arrays.asList(new ClustererAlgorithm[]{new CRDCAlgorithm(0.2), new ADimAlgorithm(0.1)}),25);
    }

    @Test
    public void test8(){
        runTest("test8",Arrays.asList(new ClustererAlgorithm[]{new CRDCAlgorithm(0.2), new ADimAlgorithm(0.1)}),25);
    }

    private void runTest(String testId,List<ClustererAlgorithm> algorithms, Integer size){
        LOG.info("Running evaluation "+ testId + ":");
        new Evaluation("src/main/resources/"+testId).execute(testId,algorithms, size);
    }

}
