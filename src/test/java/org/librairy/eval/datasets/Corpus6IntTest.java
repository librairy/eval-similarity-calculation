package org.librairy.eval.datasets;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class Corpus6IntTest extends AbstractCorpus{

    private static final Logger LOG = LoggerFactory.getLogger(Corpus6IntTest.class);

    @Test
    public void prepare(){

        String baseDir          = "src/main/resources/test6";
        Integer trainingSize    = 100000;
        Integer testSize        = 100;
        Integer topPoints       = 25;
        Integer minTextSize     = 2000;
        String endpoint         = "http://librairy.linkeddata.es/wiki-model"; // 200 dimension

        super.create(baseDir, trainingSize, testSize, topPoints, minTextSize, endpoint);
    }


}
