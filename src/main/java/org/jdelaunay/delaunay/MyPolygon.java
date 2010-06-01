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
	private MyTriangle refTriangle;
	
	/**
	 * True, if we set Z coordinate of polygon to new point else set an average of polygon and mesh Z coordinate.
	 */
	private boolean usePolygonZ;
	
	/**
	 * True, if we remove triangle who are inside the polygon.
	 */
	private boolean isEmpty;

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
	 * Generate a polygon.
	 * 
	 * @param polygon
	 * @param isEmpty True, if we remove triangle who are inside the polygon.
	 */
	public MyPolygon(Polygon polygon, boolean isEmpty) {
		super();
		init(polygon);
		this.isEmpty=isEmpty;
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
	 * Generate a polygon with property.
	 * 
	 * @param polygon
	 * @param _property
	 * @param True, if we set Z coordinate of polygon to new point else set an average of polygon and mesh Z coordinate.
	 */
	public MyPolygon(Polygon polygon, int _property, boolean usePolygonZ) {
		super(_property);
		init(polygon);
		this.usePolygonZ=usePolygonZ;
	}
	
	/**
	 * Generate a polygon with property.
	 * 
	 * @param polygon
	 * @param _property
	 * @param usePolygonZ True, if we set Z coordinate of polygon to new point else set an average of polygon and mesh Z coordinate.
	 * @param isEmpty True, if we remove triangle who are inside the polygon.
	 */
	public MyPolygon(Polygon polygon, int _property, boolean usePolygonZ, boolean isEmpty) {
		super(_property);
		init(polygon);
		this.usePolygonZ=usePolygonZ;
		this.isEmpty=isEmpty;
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
			
			refTriangle=null;
			
			usePolygonZ=false;
			isEmpty=false;
			
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
	 * @return True, if we set Z coordinate of polygon to new point else set an average of polygon and mesh Z coordinate.
	 */
	public boolean isUsePolygonZ() {
		return usePolygonZ;
	}
	
	/**
	 * @param usePolygonZ True, if we set Z coordinate of polygon to new point else set an average of polygon and mesh Z coordinate.
	 */
	public void setUsePolygonZ(boolean usePolygonZ) {
		this.usePolygonZ = usePolygonZ;
	}

	
	/**
	 * @return True, if we remove triangle who are inside the polygon.
	 */
	public boolean isEmpty() { 
		return isEmpty;
	}

	/**
	 * True, if we remove triangle who are inside the polygon.
	 * @param isEmpty
	 */
	public void setEmpty(boolean isEmpty) { 
		this.isEmpty = isEmpty;
	}
	
	/**
	 * @return The reference triangle.
	 */
	public MyTriangle getRefTriangle() {
		return refTriangle;
	}

	/**
	 * Set the reference triangle.
	 * @param refTriangle
	 */
	public void setRefTriangle(MyTriangle refTriangle) {
		this.refTriangle = refTriangle;
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
