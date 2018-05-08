package org.librairy.eval.datasets;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class Test4Corpus extends AbstractCorpus{

    private static final Logger LOG = LoggerFactory.getLogger(Test4Corpus.class);

    @Test
    public void prepare(){

        String baseDir          = "src/main/resources/test4";
        Integer trainingSize    = 1000000;
        Integer testSize        = 1000;
        Integer topPoints       = 25;
        Integer minTextSize     = 2000;
        String endpoint         = "http://librairy.linkeddata.es/wiki-model"; // 200 dimension

        super.create(baseDir, trainingSize, testSize, topPoints, minTextSize, endpoint);
    }


}
