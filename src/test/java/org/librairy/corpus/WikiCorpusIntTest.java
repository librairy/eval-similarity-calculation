package org.librairy.corpus;

import org.junit.Test;
import org.librairy.eval.corpus.WikiCorpus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class WikiCorpusIntTest {

    private static final Logger LOG = LoggerFactory.getLogger(WikiCorpusIntTest.class);

    @Test
    public void create(){

        WikiCorpus corpus = new WikiCorpus();
        corpus.createVectors(1000000);

    }

}
