package org.librairy.corpus;

import org.junit.Test;
import org.librairy.eval.model.Corpus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class WikiCorpusIntTest {

    private static final Logger LOG = LoggerFactory.getLogger(WikiCorpusIntTest.class);

    @Test
    public void create(){

        String url = "https://delicias.dia.fi.upm.es/nextcloud/index.php/s/4tPyd5Ps51sCuRx/download";

        Corpus corpus = new Corpus(url);
        corpus.createVectors(1000000,5);

    }

}
