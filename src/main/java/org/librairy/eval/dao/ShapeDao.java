package org.librairy.eval.dao;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.exceptions.InvalidQueryException;
import org.librairy.boot.model.domain.resources.Item;
import org.librairy.boot.storage.dao.DBSessionManager;
import org.librairy.boot.storage.dao.DomainsDao;
import org.librairy.boot.storage.generator.URIGenerator;
import org.librairy.eval.model.DirichletDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@Component
public class ShapeDao {

    @Autowired
    DomainsDao domainsDao;

    @Autowired
    DBSessionManager sessionManager;

    private static final Logger LOG = LoggerFactory.getLogger(ShapeDao.class);


    public List<Double> get(String domainUri, String resourceUri){

        String query = "select vector"
                + " from shapes"
                + " where uri='"+resourceUri+"' ALLOW FILTERING;";


        LOG.debug("Executing query: " + query);
        try{
            ResultSet result = sessionManager.getSpecificSession("lda", URIGenerator.retrieveId(domainUri)).execute(query);
            Row row = result.one();

            if (row == null ) return Collections.emptyList();

            List<Double> scores = row.getList(0, Double.class);

            return scores;
        }catch (InvalidQueryException e){
            LOG.warn("Query error: " + e.getMessage());
            return Collections.emptyList();
        }catch (Exception e){
            LOG.error("Unexpected error", e);
            return Collections.emptyList();
        }

    }

    public List<DirichletDistribution> get(String domainUri){
        Integer size = 100;
        Optional<String> offset = Optional.empty();
        List<DirichletDistribution> shapes = new ArrayList<>();

        while(true){

            List<Item> items = domainsDao.listItems(domainUri, size, offset, false);

            items.forEach( item -> {
                List<Double> vector = get(domainUri, item.getUri());
                DirichletDistribution dirichletDistribution = new DirichletDistribution(item.getUri(), vector);
                shapes.add(dirichletDistribution);
            });

            if (items.size() < size) break;

            offset = Optional.of(items.get(size - 1).getUri());
        }

        return shapes;
    }

}
