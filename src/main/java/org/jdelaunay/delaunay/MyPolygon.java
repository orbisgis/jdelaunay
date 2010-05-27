package org.jdelaunay.delaunay;

/**
 * Delaunay Package.
 *
 * @author Erwan BOCHER, Adelin PIAU
 * @date 2010-05-20
 * @revision 2010-05-20
 * @version 2.0
 */

import java.util.ArrayList;

import com.vividsolutions.jts.geom.*;

public class MyPolygon extends MyElement {

	private Polygon polygon;
	private ArrayList<MyEdge> edges;

	/**
	 * Generate a polygon.
	 * 
	 * @param polygon
	 */
	public MyPolygon(Polygon polygon) {
		super();
		init(polygon);
	}

	/**
	 * Generate a polygon with property.
	 * 
	 * @param polygon
	 * @param _property
	 */
	public MyPolygon(Polygon polygon, int _property) {
		super(_property);
		init(polygon);
	}

	/**
	 * Initialize data.
	 * 
	 * @param polygon
	 */
	private void init(Polygon polygon) {
		if (polygon.isEmpty())
			throw new IllegalArgumentException("Polygon is empty");
		else {
			this.polygon = polygon;

			// create edge list
			edges = new ArrayList<MyEdge>();

			// add edges to the edge list
			// each point is created one
			int nbPoints = polygon.getNumPoints();
			MyPoint lastPoint = null;
			MyPoint firstPoint = null;

			for (int i = 0; i < nbPoints; i++) {
				MyPoint aPoint = new MyPoint(polygon.getCoordinates()[i]);
				if (firstPoint == null)
					firstPoint = aPoint;
				if (lastPoint != null) {
					edges.add(new MyEdge(lastPoint, aPoint));
				}
				lastPoint = aPoint;
			}
			if ((lastPoint != null) && (lastPoint != firstPoint)) {
				edges.add(new MyEdge(lastPoint, firstPoint));
			}
		}
	}

	/**
	 * @return a polygon.
	 */
	public Polygon getPolygon() {
		return polygon;
	}

	/**
	 * Get the edges structure.
	 * 
	 * @return edges
	 */
	public ArrayList<MyEdge> getEdges() {
		return edges;
	}
	
	/**
	 * Get points.
	 * 
	 * @return points
	 */
	public ArrayList<MyPoint> getPoints() {
		ArrayList<MyPoint> points= new ArrayList<MyPoint>();
		for (int i = 0; i < polygon.getNumPoints()-1; i++)
			points.add(new MyPoint(polygon.getCoordinates()[i]));
		return points;
	}
	
	/* (non-Javadoc)
	 * @see org.jdelaunay.delaunay.MyElement#getBoundingBox()
	 */
	public MyBox getBoundingBox() {
		MyBox aBox = new MyBox();

		int nbPoints = polygon.getNumPoints();
		for (int i = 0; i < nbPoints; i++) {
			Coordinate pt = polygon.getCoordinates()[i];
			aBox.alterBox( pt.x, pt.y, pt.z );
		}
		
		return aBox;
	}

	/* (non-Javadoc)
	 * @see org.jdelaunay.delaunay.MyElement#contains(org.jdelaunay.delaunay.MyPoint)
	 */
	public boolean contains(MyPoint aPoint) { //FIXME check if we have better code
		return polygon.contains(new GeometryFactory().createPoint(aPoint.getCoordinate()));
	}

}
