package org.librairy.eval.evaluations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.Doubles;
import es.cbadenes.lab.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.librairy.eval.Config;
import org.librairy.eval.dao.ShapeDao;
import org.librairy.eval.model.Corpora;
import org.librairy.eval.model.DirichletDistribution;
import org.librairy.metrics.similarity.JensenShannonSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@Category(IntegrationTest.class)
public class SimilarityDistribution {


    @Test
    public void individual() throws IOException {

        ObjectMapper jsonMapper = new ObjectMapper();
        Corpora corpora = jsonMapper.readValue(new File("src/main/resources/real-corpora.json"), Corpora.class);


        // Getting distribution of similarities

        List<DirichletDistribution> sample = corpora.getDocuments().stream().limit(500).collect(Collectors.toList());


        Integer maxTop = Math.min(50, sample.size());

        StringBuilder out = new StringBuilder();
        out.append("Top");
        IntStream.range(0,sample.size()).forEach(i -> out.append("\td").append(i));
        out.append("\n");

        List<List<Double>> similarities = sample.parallelStream().map(d1 -> sample.stream().filter(d -> !d.getId().equals(d1.getId())).map(d2 -> JensenShannonSimilarity.apply(Doubles.toArray(d1.getVector()), Doubles.toArray(d2.getVector()))).sorted((a, b) -> -a.compareTo(b)).collect(Collectors.toList())).collect(Collectors.toList());

        for(int i=0; i < maxTop; i++){


            out.append(String.valueOf(i));

            final Integer index = i;
            similarities.forEach( l -> out.append("\t").append(l.get(index)));
            out.append("\n");

        }

//        System.out.println(out.toString());

        FileWriter writer = new FileWriter("results/real/individual-similarities.csv");
        writer.write(out.toString());
        writer.close();

    }

    @Test
    public void aggregated() throws IOException {

        ObjectMapper jsonMapper = new ObjectMapper();
        Corpora corpora = jsonMapper.readValue(new File("src/main/resources/synthetic-corpora.json"), Corpora.class);


        // Getting distribution of similarities

        List<DirichletDistribution> sample = corpora.getDocuments().stream().limit(1000).collect(Collectors.toList());


        StringBuilder out = new StringBuilder();
        out.append("Score").append("\t").append("Num").append("\n");

        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        Map<String, List<String>> similarities = sample.parallelStream().flatMap(d1 -> sample.stream().filter(e -> !e.getId().equals(d1.getId())).map(d2 -> decimalFormat.format(JensenShannonSimilarity.apply(Doubles.toArray(d1.getVector()), Doubles.toArray(d2.getVector()))))).collect(Collectors.groupingBy(String::toString));

        similarities.entrySet().stream().sorted((a,b) -> a.getKey().compareTo(b.getKey())).forEach( entry -> out.append(entry.getKey()).append("\t").append(entry.getValue().size()).append("\n"));

        System.out.println(out.toString());

        FileWriter writer = new FileWriter("results/synthetic/aggregated-similarities.csv");
        writer.write(out.toString());
        writer.close();
    }

    @Test
    public void stats() throws IOException {

        ObjectMapper jsonMapper = new ObjectMapper();
        Corpora corpora = jsonMapper.readValue(new File("src/main/resources/synthetic-corpora.json"), Corpora.class);


        // Getting distribution of similarities

        List<DirichletDistribution> sample = corpora.getDocuments().stream().limit(1000).collect(Collectors.toList());


        DoubleSummaryStatistics stats = sample.parallelStream().flatMap(d1 -> sample.stream().filter(e -> !e.getId().equals(d1.getId())).map(d2 -> JensenShannonSimilarity.apply(Doubles.toArray(d1.getVector()), Doubles.toArray(d2.getVector())))).collect(DoubleSummaryStatistics::new, DoubleSummaryStatistics::accept, DoubleSummaryStatistics::combine);

        System.out.println(stats);


    }
}
