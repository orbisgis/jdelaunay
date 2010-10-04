package org.jdelaunay.delaunay;

/**
 * Delaunay Package.
 *
 * @author Erwan BOCHER, Adelin PIAU
 * @date 2010-05-20
 * @revision 2010-10-04
 * @version 2.2
 */

import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

public class MyPolygon extends MyElement {

	private Polygon polygon;
	private ArrayList<MyEdge> edges;
	private MyTriangle refTriangle;

	
	/**
	 * True, if we want to set Z coordinate of polygon to new point else set an average of polygon and mesh Z coordinate.
	 */
	private boolean usePolygonZ;
	
	/**
	 * True, if we want to remove triangle who are inside the polygon.
	 */
	private boolean isEmpty;

	
	/**
	 * True, if we want to make new triangulation inside the polygon.
	 */
	private boolean mustBeTriangulated;
	
	/**
	 * Generate a polygon.
	 * 
	 * @param polygon
	 * @throws DelaunayError 
	 */
	public MyPolygon(Polygon polygon) throws DelaunayError {
		super();
		this.usePolygonZ=false;
		this.isEmpty=false;
		init(polygon);
	}
	
	/**
	 * Generate a polygon.
	 * 
	 * @param polygon
	 * @param isEmpty True, if we remove triangle who are inside the polygon.
	 * @throws DelaunayError 
	 */
	public MyPolygon(Polygon polygon, boolean isEmpty) throws DelaunayError {
		super();
		init(polygon);
		this.usePolygonZ=false;
		this.isEmpty=isEmpty;
	}

	/**
	 * Generate a polygon with property.
	 * 
	 * @param polygon
	 * @param _property
	 * @throws DelaunayError 
	 */
	public MyPolygon(Polygon polygon, int _property) throws DelaunayError {
		super(_property);
		this.usePolygonZ=false;
		this.isEmpty=false;
		init(polygon);
	}
	
	/**
	 * Generate a polygon with property.
	 * 
	 * @param polygon
	 * @param _property
	 * @param True, if we set Z coordinate of polygon to new point else set an average of polygon and mesh Z coordinate.
	 * @throws DelaunayError 
	 */
	public MyPolygon(Polygon polygon, int _property, boolean usePolygonZ) throws DelaunayError {
		super(_property);
		this.usePolygonZ=usePolygonZ;
		this.isEmpty=false;
		init(polygon);
	}
	
	/**
	 * Generate a polygon with property.
	 * 
	 * @param polygon
	 * @param _property
	 * @param usePolygonZ True, if we set Z coordinate of polygon to new point else set an average of polygon and mesh Z coordinate.
	 * @param isEmpty True, if we remove triangle who are inside the polygon.
	 * @throws DelaunayError 
	 */
	public MyPolygon(Polygon polygon, int _property, boolean usePolygonZ, boolean isEmpty) throws DelaunayError {
		super(_property);
		this.usePolygonZ=usePolygonZ;
		this.isEmpty=isEmpty;
		init(polygon);
	}
	
	/**
	 * Initialize data.
	 * 
	 * @param polygon
	 * @throws DelaunayError 
	 */
	private void init(Polygon polygon) throws DelaunayError {
		if (polygon.isEmpty())
			throw new IllegalArgumentException("Polygon is empty");
		else {
			this.polygon = polygon;
			
			refTriangle=null;
			
			// create edge list
			edges = new ArrayList<MyEdge>();

			// add edges to the edge list
			// each point is created one
			int nbPoints = polygon.getNumPoints();
			
			if(Double.isNaN(polygon.getCoordinates()[0].z))
				polygon.getCoordinates()[0].z=0;
			MyPoint lastPoint = new MyPoint(polygon.getCoordinates()[0]);

			MyPoint aPoint;
			MyEdge aEdge;
			for (int i = 1; i < nbPoints; i++) {
				if(Double.isNaN(polygon.getCoordinates()[i].z))
					polygon.getCoordinates()[i].z=0;
				aPoint = new MyPoint(polygon.getCoordinates()[i]);
				aEdge=new MyEdge(lastPoint, aPoint);
				aEdge.setUseByPolygon(true);
				edges.add(aEdge);
				lastPoint = aPoint;
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
		for(MyEdge aEdge:edges)
			aEdge.setUseZ(usePolygonZ);
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
	 * @return True, if we make new triangulation inside the polygon.
	 */
	public boolean mustBeTriangulated()
	{
		return mustBeTriangulated;
	}
	
	/**
	 * True, if we want to make new triangulation inside the polygon.
	 */
	public void setMustBeTriangulated(boolean mustBeTriangulated)
	{
		this.mustBeTriangulated=mustBeTriangulated;
		setEmpty(mustBeTriangulated);
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
	 * @throws DelaunayError 
	 */
	public ArrayList<MyPoint> getPoints() throws DelaunayError {
		ArrayList<MyPoint> points= new ArrayList<MyPoint>();
		MyPoint aPoint;
		for (int i = 0; i < polygon.getNumPoints()-1; i++)
		{	
			aPoint=new MyPoint(polygon.getCoordinates()[i]);
			aPoint.setUseByPolygon(true);
			aPoint.setUseZ(usePolygonZ);
			points.add(aPoint);
		}
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
	public boolean contains(MyPoint aPoint) { //FIXME make better code
		return polygon.contains(new GeometryFactory().createPoint(aPoint.getCoordinate()));
	}
	
	public boolean contains(Coordinate coordinate) {  //FIXME make better code
		return polygon.contains(new GeometryFactory().createPoint(coordinate));
	}

	@Override
	public boolean isUseByPolygon() {
		return true;
	}

	@Override
	public void setUseByPolygon(boolean useByPolygon) {
	}

}
