package org.librairy.eval.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class KeyStore {

    private static final Logger LOG = LoggerFactory.getLogger(KeyStore.class);

    private AtomicInteger counter;

    private ObjectMapper jsonMapper;

    private IndexWriter indexWriter;

    private FSDirectory directory;


    private Analyzer analyzer = new Analyzer() {
        @Override
        protected TokenStreamComponents createComponents(String s) {
            Tokenizer tokenizer = new WhitespaceTokenizer();
            return new TokenStreamComponents(tokenizer);
        }
    };

    public KeyStore(String path) {
        try {
            File indexFile = new File(path);
            this.jsonMapper = new ObjectMapper();
            FileUtils.deleteDirectory(indexFile);
            this.directory = FSDirectory.open(indexFile.toPath());
            IndexWriterConfig writerConfig = new IndexWriterConfig(analyzer);
            writerConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            writerConfig.setRAMBufferSizeMB(500.0);
            this.indexWriter = new IndexWriter(directory, writerConfig);
            this.counter = new AtomicInteger();
        } catch (Exception e) {
            LOG.error("Unexpected error creating keystore",e);
            new RuntimeException(e);
        }
    }

    public void add(Point point, String label) {
        // add point to lucene index
        try {
            if ((indexWriter == null) || (!indexWriter.isOpen())) throw new RuntimeException("Algorithm closed. A new instance is required");

            Document luceneDoc = new Document();
            // id
            luceneDoc.add(new StringField("id", point.getId(), Field.Store.YES));

            luceneDoc.add(new StringField("label", label, Field.Store.YES));
            // point
            String json = jsonMapper.writeValueAsString(point);
            luceneDoc.add(new StringField("point", json, Field.Store.YES));

            indexWriter.addDocument(luceneDoc);
            if (counter.incrementAndGet() % 100 == 0 ) {
                indexWriter.commit();
            }
        } catch (Exception e) {
            new RuntimeException(e);
        }
    }

    public List<Point> get(String label){

        try{
            if (indexWriter.hasUncommittedChanges()) indexWriter.commit();

            IndexReader reader = DirectoryReader.open(directory);
            IndexSearcher searcher  = new IndexSearcher(reader);
            searcher.setSimilarity(new BooleanSimilarity());

            QueryParser parser = new QueryParser("label", analyzer);
            String queryString = label;

            Query query = parser.parse(queryString);
            TopDocs results = searcher.search(query, counter.get());

            return Arrays.stream(results.scoreDocs).parallel().map(sd -> {
                try {

                    Document docIndexed     = reader.document(sd.doc);
                    String jsonPoint        = String.format(docIndexed.get("point"));
                    return jsonMapper.readValue(jsonPoint, Point.class);

                } catch (Exception e) {
                    LOG.warn("Error getting neighbour", e);
                    return null;
                }
            }).filter(a -> a != null).collect(Collectors.toList());
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void close(){
        try {
            this.indexWriter.close();
        } catch (IOException e) {
            LOG.error("Unexpected error closing index", e);
        }
    }

}
