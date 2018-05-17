package org.librairy.eval.model;

import org.librairy.eval.algorithms.ClustererAlgorithm;
import org.librairy.eval.algorithms.ClustererReport;
import org.librairy.eval.utils.JsonlReader;
import org.librairy.eval.utils.JsonlWriter;
import org.librairy.eval.utils.ParallelExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class Evaluation {

    private static final Logger LOG = LoggerFactory.getLogger(Evaluation.class);

    private final Path testSet;
    private final Path trainingSet;
    private final String baseDir;


    public Evaluation(String baseDir) {
        this.baseDir        = baseDir;
        this.trainingSet    = Paths.get(baseDir,"training-set.jsonl.gz");
        this.testSet        = Paths.get(baseDir,"test-set.jsonl.gz");
    }

    public void execute(String testId, List<ClustererAlgorithm> algorithms, Integer numNeighbours){

        LOG.info("Ready to evaluate the following algorithm: " + algorithms);
        ConcurrentHashMap<String,Report> reports = new ConcurrentHashMap<>();
        algorithms.forEach( algorithm -> reports.put(algorithm.getId(), new Report(testId, algorithm)));

        ParallelExecutor executor = new ParallelExecutor();
        try {

            LOG.info("Adding training points to the spaces ..");
            JsonlReader<Point> readerTrain = new JsonlReader(trainingSet.toFile(), Point.class);
            Optional<Point> trainingPoint;
            AtomicInteger trainingCounter = new AtomicInteger();
            while((trainingPoint = readerTrain.next()).isPresent()){
                if (trainingCounter.incrementAndGet() % 100 == 0) {
                    LOG.info(trainingCounter.get() + " points added");
                    Thread.currentThread().sleep(20);
                }
                final Point currentPoint = trainingPoint.get();
                for (ClustererAlgorithm algorithm: algorithms){
                    executor.execute(() -> {
                        algorithm.add(currentPoint);
                    });
                }
            }
            executor.pause();
            LOG.info(trainingCounter.get() + " points added");

            LOG.info("Creating clusters ..");
            for (ClustererAlgorithm algorithm: algorithms){
                executor.execute(() -> {
                    LOG.info("Discovering clusters by algorithm: " + algorithm.getId());
                    Report report = reports.get(algorithm.getId());
                    ClustererReport clusterReport = algorithm.cluster();
                    report.setNumberOfClusters(clusterReport.getNumClusters());
                    report.increaseCalculatedSimilarities(clusterReport.getRequiredComparisons());
                    reports.put(algorithm.getId(), report);
                    LOG.info("done!");
                });
            }
            executor.pause();

            LOG.info("Getting the closest points to the test set and evaluate results ..");
            JsonlReader<Neighbourhood> readerTest = new JsonlReader(testSet.toFile(), Neighbourhood.class);
            Optional<Neighbourhood> testNeighbourhood;
            AtomicInteger testCounter = new AtomicInteger();
            while((testNeighbourhood = readerTest.next()).isPresent()){
                if (testCounter.incrementAndGet() % 100 == 0) {
                    LOG.info(testCounter.get() + " points tested");
                    Thread.currentThread().sleep(20);
                }
                Neighbourhood rNeighbourhood = testNeighbourhood.get();
                final Neighbourhood referenceNeighbourhood = new Neighbourhood(rNeighbourhood.getReference(), rNeighbourhood.getClosestNeighbours().stream().limit(numNeighbours).collect(Collectors.toList()));
                for (ClustererAlgorithm algorithm: algorithms){
//                    executor.execute(() -> {
                        Report report = reports.get(algorithm.getId());
                        report.update(referenceNeighbourhood, algorithm.getNeighbourhood(referenceNeighbourhood.getReference(), numNeighbours));
                        reports.put(algorithm.getId(), report);
//                    });
                }
            }
            executor.pause();
            LOG.info(testCounter.get() + " points tested");

            // Update reports size
            for (ClustererAlgorithm algorithm: algorithms){
                executor.execute(() -> {
                    Report report = reports.get(algorithm.getId());
                    report.setSize(trainingCounter.get(), testCounter.get(), numNeighbours);
                    reports.put(algorithm.getId(), report);
                });
            }
            executor.stop();

            // Print results
            LOG.info("writing reports ..");
            SimpleDateFormat dateFormatter = new SimpleDateFormat("YYYYMMdd'T'HHmmss");
            String fileName = dateFormatter.format(new Date())+"-reports.jsonl.gz";
            JsonlWriter<Report> writer = new JsonlWriter(Paths.get(baseDir,"results",fileName).toFile());
            reports.entrySet().stream().sorted((a,b) -> a.getKey().compareTo(b.getKey())).forEach(entry -> {
                writer.write(entry.getValue());
                LOG.info(""+entry.getValue());
            });
            writer.close();

            // Closing
            algorithms.forEach(algorithm -> algorithm.close());

            LOG.info("evaluations completed!");

        } catch (Exception e) {
            LOG.error("Unexpected error in evaluation",e);
        }
    }

}
