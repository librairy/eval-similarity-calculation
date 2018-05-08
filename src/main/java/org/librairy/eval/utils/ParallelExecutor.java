package org.librairy.eval.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class ParallelExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(ParallelExecutor.class);

    private ThreadPoolExecutor executor;


    public ParallelExecutor() {
        initialize();
    }

    private void initialize(){
        Integer parallel = Runtime.getRuntime().availableProcessors()-1;
        this.executor = new ThreadPoolExecutor(parallel,parallel,0l, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(parallel), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public void execute(Runnable task){
        this.executor.submit(task);
    }

    public void pause(){
        LOG.info("waiting for task executions");
        stop();
        initialize();
    }

    public void stop(){
        try {
            this.executor.shutdown();
            this.executor.awaitTermination(1,TimeUnit.HOURS);
        } catch (InterruptedException e) {
            LOG.warn("Unexpected interruption waiting for finish",e);
        }
    }
}
