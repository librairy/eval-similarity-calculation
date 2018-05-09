package org.librairy.eval.algorithms;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.miscellaneous.DelimitedTermFrequencyTokenFilter;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.dom4j.DocumentFactory;
import org.librairy.eval.metrics.JensenShannon;
import org.librairy.eval.model.KeyStore;
import org.librairy.eval.model.Neighbourhood;
import org.librairy.eval.model.Point;
import org.librairy.eval.model.Neighbour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.GZIPInputStream;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class CRDCAlgorithm extends AbstractKeyValueAlgorithm {

    private static final Logger LOG = LoggerFactory.getLogger(CRDCAlgorithm.class);


    public CRDCAlgorithm(Double threshold){
        super("CRDC",threshold);
    }


    @Override
    public String getCluster(Point point) {
        List<Double> vector = point.getVector();
        List<Neighbour> sortedVector = IntStream.range(0, vector.size()).mapToObj(i -> new Neighbour(new Point(String.valueOf(i)), vector.get(i))).sorted((a, b) -> -a.getScore().compareTo(b.getScore())).collect(Collectors.toList());

        StringBuilder shape = new StringBuilder();
        Double acc = 0.0;
        for(int i=0; i< vector.size();i++){
            if (acc >= threshold) break;
            Neighbour res = sortedVector.get(i);
            Integer score = res.getScore().intValue();
            //shape.append("t").append(res.getPoint().getId()).append("_").append(i).append("|").append(score).append(" ");
            shape.append("t").append(res.getPoint().getId()).append("|");
            acc += res.getScore();
        }

        return shape.toString().trim();
    }
}
