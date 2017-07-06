package org.librairy.eval;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.cbadenes.lab.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.librairy.eval.Config;
import org.librairy.eval.dao.ShapeDao;
import org.librairy.eval.model.Corpora;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@Category(IntegrationTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Config.class)
public class CorpusManagement {

    @Autowired
    ShapeDao shapeDao;

    @Test
    public void create() throws IOException {

        String domainUri = "http://librairy.linkeddata.es/resources/domains/aies6";

        Corpora corpora = new Corpora();
        corpora.setDocuments(shapeDao.get(domainUri));

        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.writeValue(new File("src/main/resources/corpora.json"), corpora);

        System.out.println(corpora.getDocuments().size());

    }

    @Test
    public void load() throws IOException {

        ObjectMapper jsonMapper = new ObjectMapper();
        Corpora corpora = jsonMapper.readValue(new File("src/main/resources/corpora.json"), Corpora.class);

        System.out.println(corpora.getDocuments().size());



    }
}
