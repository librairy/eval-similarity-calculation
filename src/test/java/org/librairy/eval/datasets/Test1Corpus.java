package org.librairy.eval.datasets;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class Test1Corpus extends AbstractCorpus{

    private static final Logger LOG = LoggerFactory.getLogger(Test1Corpus.class);

    @Test
    public void prepare(){

        String baseDir          = "src/main/resources/test1";
        Integer trainingSize    = 1000;
        Integer testSize        = 100;
        Integer topPoints       = 25;
        Integer minTextSize     = 2000;
        String endpoint         = "http://librairy.linkeddata.es/20news-model"; // 20 dimension

        super.create(baseDir, trainingSize, testSize, topPoints, minTextSize, endpoint);
    }


}
