package org.jdelaunay.delaunay;

/**
 * Delaunay Package.
 *
 * @author Erwan BOCHER, Adelin PIAU, Alexis GUÉGANNO
 * @date 2010-05-20
 * @revision 2010-11-08
 * @version 2.2
 */

import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

public final class ConstraintPolygon extends Element {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Polygon polygon;
	private ArrayList<Edge> edges;
	private DelaunayTriangle refTriangle;

	
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
	public ConstraintPolygon(Polygon polygon) throws DelaunayError {
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
	public ConstraintPolygon(Polygon polygon, boolean isEmpty) throws DelaunayError {
		super();
		init(polygon);
		this.usePolygonZ=false;
		this.isEmpty=isEmpty;
	}

	/**
	 * Generate a polygon with property.
	 * 
	 * @param polygon
	 * @param property
	 * @throws DelaunayError 
	 */
	public ConstraintPolygon(Polygon polygon, int property) throws DelaunayError {
		super(property);
		this.usePolygonZ=false;
		this.isEmpty=false;
		init(polygon);
	}
	
	/**
	 * Generate a polygon with property.
	 * 
	 * @param polygon
	 * @param property
	 * @param True, if we set Z coordinate of polygon to new point else set an average of polygon and mesh Z coordinate.
	 * @throws DelaunayError 
	 */
	public ConstraintPolygon(Polygon polygon, int property, boolean usePolygonZ) throws DelaunayError {
		super(property);
		this.usePolygonZ=usePolygonZ;
		this.isEmpty=false;
		init(polygon);
	}
	
	/**
	 * Generate a polygon with property.
	 * 
	 * @param polygon
	 * @param property
	 * @param usePolygonZ True, if we set Z coordinate of polygon to new point else set an average of polygon and mesh Z coordinate.
	 * @param isEmpty True, if we remove triangle who are inside the polygon.
	 * @throws DelaunayError 
	 */
	public ConstraintPolygon(Polygon polygon, int property, boolean usePolygonZ, boolean isEmpty) throws DelaunayError {
		super(property);
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
		if (polygon.isEmpty()) {
			throw new IllegalArgumentException("Polygon is empty");
		}
		else {
			this.polygon = polygon;
			
			refTriangle=null;
			
			// create edge list
			edges = new ArrayList<Edge>();

			// add edges to the edge list
			// each point is created one
			int nbPoints = polygon.getNumPoints();
			
			if(Double.isNaN(polygon.getCoordinates()[0].z)) {
				polygon.getCoordinates()[0].z = 0;
			}
			Point lastPoint = new Point(polygon.getCoordinates()[0]);

			Point aPoint;
			Edge aEdge;
			for (int i = 1; i < nbPoints; i++) {
				if(Double.isNaN(polygon.getCoordinates()[i].z)) {
					polygon.getCoordinates()[i].z = 0;
				}
				aPoint = new Point(polygon.getCoordinates()[i]);
				aEdge=new Edge(lastPoint, aPoint);
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
		for(Edge aEdge:edges){
			aEdge.setUseZ(usePolygonZ);
                }
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
	public DelaunayTriangle getRefTriangle() {
		return refTriangle;
	}

	/**
	 * Set the reference triangle.
	 * @param refTriangle
	 */
	public void setRefTriangle(DelaunayTriangle refTriangle) {
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
	public ArrayList<Edge> getEdges() {
		return edges;
	}
	
	/**
	 * Get points.
	 * 
	 * @return points
	 * @throws DelaunayError 
	 */
	public ArrayList<Point> getPoints() throws DelaunayError {
		ArrayList<Point> points= new ArrayList<Point>();
		Point aPoint;
		for (int i = 0; i < polygon.getNumPoints()-1; i++)
		{	
			aPoint=new Point(polygon.getCoordinates()[i]);
			aPoint.setUseByPolygon(true);
			aPoint.setUseZ(usePolygonZ);
			points.add(aPoint);
		}
		return points;
	}
	
	/**
	 * Get the minimum boundary of this element.
	 * @return
	 */
	@Override
	public BoundaryBox getBoundingBox() {
		BoundaryBox aBox = new BoundaryBox();

		int nbPoints = polygon.getNumPoints();
		for (int i = 0; i < nbPoints; i++) {
			Coordinate pt = polygon.getCoordinates()[i];
			aBox.alterBox( pt.x, pt.y, pt.z );
		}
		
		return aBox;
	}

	/**
	 * Check if this polygon contain the point given in argument.
	 * @param aPoint
	 * @return
	 */
	@Override
	public boolean contains(Point aPoint) {
		return polygon.contains(new GeometryFactory().createPoint(aPoint.getCoordinate()));
	}
	
	public boolean contains(Edge anEdge) throws DelaunayError { //FIXME make better code
		Point aPoint = anEdge.getBarycenter();
		boolean intersectPolygon=polygon.contains(new GeometryFactory().createPoint(aPoint.getCoordinate()));
		boolean edgeColinear=false, intersectEdge=false;
		for(int i=0;i<edges.size();i++ )
		{
			if(edges.get(i).haveSamePoint(anEdge)) {
				edgeColinear = true;
			}
			else if(edges.get(i).intersects(anEdge.getStartPoint(), anEdge.getEndPoint())==Edge.INTERSECT) {
				intersectEdge = true;
			}
		}
		return (intersectPolygon && !intersectEdge) || (!intersectPolygon && edgeColinear && !intersectEdge);
	}
	
	/**
	 * check if this polygon contain the point given in argument.
	 * @param coordinate
	 * @return
	 * @throws DelaunayError
	 */
	@Override
	public boolean contains(Coordinate coordinate) throws DelaunayError {
		return polygon.contains(new GeometryFactory().createPoint(coordinate));

	}

	/**
	 *
	 * @param anEdge
	 * @return
	 * @throws DelaunayError
	 */
	public boolean isIntersect(Edge anEdge) throws DelaunayError{
		boolean intersect=false;
		for(int i=0;i<edges.size() && !intersect;i++ )
		{
			intersect=edges.get(i).intersects(anEdge.getStartPoint(),anEdge.getEndPoint()) == Edge.INTERSECT;
		}
			
		return intersect;
	}

	/**
	 * Returns true : the polygon is used by itself...
	 * @return
	 */
	@Override
	public boolean isUseByPolygon() {
		return true;
	}

	/**
	 * Do nothing.
	 * @param useByPolygon
	 */
	@Override
	public void setUseByPolygon(boolean useByPolygon) {
	}

	@Override
	public void removeIndicator() {
	}

	@Override
	public int getIndicator() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int setIndicator(int indicator) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
