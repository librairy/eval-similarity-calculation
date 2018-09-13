package org.librairy.eval.algorithms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.librairy.eval.metrics.JensenShannon;
import org.librairy.eval.model.Neighbour;
import org.librairy.eval.model.Neighbourhood;
import org.librairy.eval.model.Point;

public class PivotAlgorithm implements ClustererAlgorithm {
	private static final Logger LOG = LoggerFactory.getLogger(PivotAlgorithm.class);
	
	private final String id;
	private final int numPivots;
	private List<Point> pivots;
	private List<Point> points;
	
	
	public PivotAlgorithm(int numPivots) {
		this.id = "Pivot-based";
		this.numPivots = numPivots;
		this.pivots = new ArrayList<Point>();
		this.points = new ArrayList<Point>();
	}

	@Override
	public void add(Point point) {
		this.points.add(point);
	}

//	ClustererReport cluster(): agrupa los puntos del dataset y devuelve un informe resultado de la operación. 
//   	En este método es donde se podrían inicializar los N pivotes, ya que se dispone del dataset completo. 
//		Una posible optimización de rendimiento sería hacerlo de forma incremental en el método 'add'según se 
//		vayan añadiendo puntos, pero creo que es mejor idea hacerlo desde aquí por si se define otra lógica
//		distinta a la asignación de los N primeros puntos como pivotes.

	@Override
	public ClustererReport cluster() {
//		LOG.info("Entering PivoAlgorithm.cluster() "+this.points.size());
		try {
			this.pivots = this.points.subList(0, this.numPivots);
		}catch(Exception e){
			e.printStackTrace();
		}
        ClustererReport report = new ClustererReport();
        report.setNumClusters(Long.valueOf(this.numPivots));
        report.setNumComparisons(0l);
        return report;
	}

//	Neighbourhood getNeighbourhood(Point point, Integer n): identifica los n puntos más cercanos a uno dado (point)
//	En este método es donde tendrías que comparar el punto dado 'point' con los pivotes que finalmente se han definido en el dataset

	@Override
	public Neighbourhood getNeighbourhood(Point point, Integer size) {
//        LOG.debug("Creating neighbourhood around " + point + " with "+ size + " neighbours");
        Neighbourhood neighbourhood = new Neighbourhood();
        List<Neighbour> neighbours = pivots.parallelStream().map(neighbourPoint -> new Neighbour(neighbourPoint, JensenShannon.similarity(point.getVector(), neighbourPoint.getVector()))).filter(a -> a != null).collect(Collectors.toList());

        List<Neighbour> topNeighbours = neighbours.parallelStream().sorted((a, b) -> -a.getScore().compareTo(b.getScore())).limit(size).collect(Collectors.toList());

        neighbourhood.setNumberOfNeighbours(neighbours.size());
        neighbourhood.setReference(point);
        neighbourhood.setClosestNeighbours(topNeighbours);
        return neighbourhood;

	}

//	String getCluster(Point point): devuelve el identificador (String) del cluster al que pertenece un punto
//	Una vez se han identificado los grupos de puntos en el metodo 'cluster', se asignan identificadores a 
//	cada uno de esos grupos. Con este método se devuelve el identificador del grupo al que pertence el 
//	punto dado 'point'.
	@Override
	public String getCluster(Point point) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public boolean close() {
		return false;
	}

    @Override
    public String toString() {
        return getId() + " Algorithm";
    }

}
