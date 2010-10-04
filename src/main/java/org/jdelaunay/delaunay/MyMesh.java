package org.jdelaunay.delaunay;

/**
 * Delaunay Package.
 *
 * @author Jean-Yves MARTIN, Erwan BOCHER, Adelin PIAU
 * @date 2009-01-12
 * @revision 2010-10-04
 * @version 2.1
 */

import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.StringTokenizer;

public class MyMesh {
	
	// Vectors with points and edges
	protected MyQuadTreeMapper<MyPoint> pointsQuadTree;
	protected MyQuadTreeMapper<MyEdge> edgesQuadTree;
	protected MyQuadTreeMapper<MyTriangle>  trianglesQuadTree;
	
	protected LinkedList<MyPolygon> polygons;
	protected ArrayList<MyEdge> constraintsEdges;

	
	// bounding box
	protected int maxx, maxy;
	private MyBox theBox;

	// GIDs
	private int point_GID;
	private int edge_GID;
	private int triangle_GID;

	// Parameters
	private boolean meshComputed;
	private double precision;
	private double tolarence;
	private double minArea, maxArea;
	private double minAngle;
	private int refinement;
	private boolean verbose;
	private boolean displayCircles;
	private long duration;
	private long startComputation;
	private MyDrawing affiche;
	private boolean usePolygonZ;
//	private boolean isBoundingBoxInit;

	// Working index vector
	private LinkedList<MyEdge> badEdgesQueueList;
	private LinkedList<MyEdge> boundaryEdges;

	// constants
	public static final double epsilon = 0.00001;
	public static final int maxIter = 5;

	public static final int refinement_maxArea = 1;
	public static final int refinement_minAngle = 2;
	public static final int refinement_softInterpolate = 4;
	public static final int refinement_obtuseAngle = 8;

	/**
	 * Create an empty Mesh. Allocate data structures
	 * 
	 */
	public MyMesh() {
		
		this.maxx = 1200;
		this.maxy = 700;
		this.theBox = new MyBox();
//		this.isBoundingBoxInit=false;
		
		
		// Generate vectors
		this.pointsQuadTree = new MyQuadTreeMapper<MyPoint>(theBox);
		this.edgesQuadTree = new MyQuadTreeMapper<MyEdge>(theBox);
		this.trianglesQuadTree = new MyQuadTreeMapper<MyTriangle>(theBox);

		
		this.constraintsEdges = new ArrayList<MyEdge>();
		this.polygons = new LinkedList<MyPolygon>();



		this.meshComputed = false;

		this.point_GID = 0;
		this.edge_GID = 0;
		this.triangle_GID = 0;

		this.precision = 0.0;
		this.tolarence = 0.000001;
		this.maxArea = 600;
		this.minArea = 1;
		this.minAngle = 5;
		this.refinement = 0;
		this.verbose = false;
		this.duration = 0;
		this.affiche = null;
		this.displayCircles = false;
	}
	
	public void init(ArrayList<MyPoint> points)
	{
		setPoints(points);
		edgesQuadTree=new MyQuadTreeMapper<MyEdge>(getBoundingBox());
		trianglesQuadTree=new MyQuadTreeMapper<MyTriangle>(getBoundingBox());
	}
	
	public void init(MyBox boundingBox) throws DelaunayError
	{
		setPoints(boundingBox.getPoints());
		edgesQuadTree=new MyQuadTreeMapper<MyEdge>(getBoundingBox());
		trianglesQuadTree=new MyQuadTreeMapper<MyTriangle>(getBoundingBox());
	}

	/**
	 * Tell if delaunay has been applied
	 * 
	 * @return
	 */
	public boolean isMeshComputed() {
		return this.meshComputed;
	}

	/**
	 * Display circles around triangles when displayed in the JPanel
	 * 
	 * @param displayCircles
	 */
	public void setDisplayCircles(boolean displayCircles) {
		this.displayCircles = displayCircles;
	}

	/**
	 * Compute Mesh bounding box
	 */
	public MyBox getBoundingBox() {
//		isBoundingBoxInit=true;
		this.theBox = pointsQuadTree.getBox();
		return this.theBox;
	}

	/**
	 * Get the current number of points in the Mesh
	 * 
	 * @return NbPoints
	 */
	public int getNbPoints() {
		return this.pointsQuadTree.size();
	}

	/**
	 * Get the current number of edges in the Mesh
	 * 
	 * @return NbEdges
	 */
	public int getNbEdges() {
		return this.edgesQuadTree.size();
	}

	/**
	 * Get the current number of triangles in the Mesh
	 * 
	 * @return NbTriangles
	 */
	public int getNbTriangles() {
		return this.trianglesQuadTree.size();
	}

	/**
	 * Get the points structure
	 * 
	 * @return points
	 */
	public ArrayList<MyPoint> getPoints() {
		return pointsQuadTree.getAll();
	}

	/**
	 * Set the points as the points of the array
	 * 
	 * @param point
	 */
	public void setPoints(ArrayList<MyPoint> point) {
		for(MyPoint p:point)
			p.setGID(++point_GID);
		
		this.pointsQuadTree = new MyQuadTreeMapper<MyPoint>(point);
		
		edgesQuadTree.setBox(pointsQuadTree.getBox());
		trianglesQuadTree.setBox(pointsQuadTree.getBox());
	}

	/**
	 * Set the points as the points of the array
	 * 
	 * @param point
	 */
	public void setPoints(LinkedList<MyPoint> point) {
		for(MyPoint p:point)
			p.setGID(++point_GID);
		this.pointsQuadTree = new MyQuadTreeMapper<MyPoint>(point);
		
		edgesQuadTree.setBox(pointsQuadTree.getBox());
		trianglesQuadTree.setBox(pointsQuadTree.getBox());
	}

	/**
	 * Set the points as the array
	 * 
	 * @param point
	 */
	public void setPointsRef(ArrayList<MyPoint> point) {
		for(MyPoint p:point)
			p.setGID(++point_GID);
		
		this.pointsQuadTree = new MyQuadTreeMapper<MyPoint>(point);
		
		edgesQuadTree.setBox(pointsQuadTree.getBox());
		trianglesQuadTree.setBox(pointsQuadTree.getBox());
	}

	/**
	 * Get the edges structure
	 * 
	 * @return edges
	 */
	public ArrayList<MyEdge> getEdges() {
		return edgesQuadTree.getAll();
	}

	/**
	 * Set the edges as the edges of the ArrayList
	 * 
	 * @param edges
	 */
	public void setConstraintEdges(ArrayList<MyEdge> edges) {
		this.constraintsEdges = new ArrayList<MyEdge>();
		for (MyEdge anEdge : edges)
			this.constraintsEdges.add(anEdge);
	}

	/**
	 * Set the edges as the edges of the ArrayList
	 * 
	 * @param edges
	 */
	public void setEdges(ArrayList<MyEdge> edges) {
		setConstraintEdges(edges);
	}

	/**
	 * Set the edges as the edges of the LinkedList
	 * 
	 * @param edges
	 */
	public void setConstraintEdges(LinkedList<MyEdge> edges) {
		this.constraintsEdges = new ArrayList<MyEdge>();
		for (MyEdge anEdge : edges)
			this.constraintsEdges.add(anEdge);
	}

	/**
	 * Set the edges as the edges of the ArrayList
	 * 
	 * @param edges
	 */
	public void setEdges(LinkedList<MyEdge> edges) {
		setConstraintEdges(edges);
	}

	/**
	 * Set the edges as the LinkedList
	 * 
	 * @param edges
	 */
	public void setConstraintEdgesRef(ArrayList<MyEdge> edges) {
		this.constraintsEdges = edges;
	}

	/**
	 * Set the edges as the LinkedList
	 * 
	 * @param edges
	 */
	public void setEdgesRef(ArrayList<MyEdge> edges) {
		setConstraintEdgesRef(edges);
	}

	/**
	 * Get the complementary edges structure This structure memorize the edges
	 * that have to be added to the triangularization
	 * 
	 * @return edges
	 */
	public ArrayList<MyEdge> getConstraintsEdges() {
		return constraintsEdges;
	}

	/**
	 * Get the triangle structure
	 * 
	 * @return triangle
	 */
	public ArrayList<MyTriangle> getTriangles() {
		return trianglesQuadTree.getAll();
	}

	/**
	 * Get the polygon structure
	 * 
	 * @return triangle
	 */
	public LinkedList<MyPolygon> getPolygons() {
		return this.polygons;
	}
	
	/**
	 * Get one polygon.
	 * @param index
	 * @return A polygon in mesh.
	 */
	public MyPolygon getPolygon(int index) {
		return polygons.get(index);
	}

	/**
	 * Get the box
	 * 
	 * @return the theBox
	 */
	public MyBox getTheBox() {
		return theBox;
	}

	/**
	 * Set precision for proximity.
	 * 
	 * @param precision
	 */
	public void setPrecision(double precision) {
		this.precision = precision;
	}

	/**
	 * Get precision for proximity.
	 * 
	 * @return
	 */
	public double getPrecision() {
		return this.precision;
	}

	/**
	 * Get maximum area for refinement.
	 * 
	 * @return maxArea
	 */
	public double getMaxArea() {
		return this.maxArea;
	}

	/**
	 * Set maximum area for refinement.
	 * 
	 * @param maxArea
	 */
	public void setMaxArea(double maxArea) {
		this.maxArea = maxArea;
	}

	/**
	 * Get minimum area for refinement.
	 * 
	 * @return minArea
	 */
	public double getMinArea() {
		return this.minArea;
	}

	/**
	 * Set minimum area for refinement.
	 * 
	 * @param minArea
	 */
	public void setMinArea(double minArea) {
		this.minArea = minArea;
	}

	/**
	 * Get minimum angle for triangles.
	 * 
	 * @return minAngle
	 */
	public double getMinAngle() {
		return minAngle;
	}

	/**
	 * Set minimum angle for triangles.
	 * 
	 * @param minAngle
	 */
	public void setMinAngle(double minAngle) {
		this.minAngle = minAngle;
	}

	/**
	 * Set refinement. Refinement value can be any value of : refinement_minArea
	 * = remove triangles with a too small area refinement_maxArea = split too
	 * large triangles refinement_minAngle = remove triangle with a too small
	 * angle
	 * 
	 * @param refinement
	 */
	public void setRefinment(int refinement) {
		this.refinement = refinement;
	}

	/**
	 * Add refinement. Refinement value can be any composed value of :
	 * refinement_minArea = remove triangles with a too small area
	 * refinement_maxArea = split too large triangles refinement_minAngle =
	 * remove triangle with a too small angle
	 * 
	 * @param refinement
	 */
	public void addRefinment(int refinement) {
		this.refinement += refinement;
	}

	/**
	 * @param verbose
	 *            mode
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * Start timer
	 */
	public void setStart() {
		startComputation = Calendar.getInstance().getTime().getTime();
	}

	/**
	 * End of timer - generate duration
	 */
	public void setEnd() {
		duration = Calendar.getInstance().getTime().getTime()
				- startComputation;
	}

	/**
	 * Get Duration
	 * 
	 * @return
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * Get JPanel in which Mesh is displayed
	 * 
	 * @return
	 */
	public MyDrawing getAffiche() {
		return affiche;
	}

	/**
	 * Set JPanel in which Mesh is displayed
	 * 
	 * @param affiche
	 */
	public void setAffiche(MyDrawing affiche) {
		this.affiche = affiche;
	}

	/**
	 * get a point from its GID
	 * 
	 * @param gid
	 * @return aPoint
	 */
	public MyPoint getPointFromGID(int gid) {
		return pointsQuadTree.searchGID(gid);
	}

	/**
	 * get an edge from its GID
	 * 
	 * @param gid
	 * @return aPoint
	 */
	public MyEdge getEdgeFromGID(int gid) {
		return edgesQuadTree.searchGID(gid);
	}

	/**
	 * search for a point
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @throws DelaunayError
	 */
	protected MyPoint searchPoint(double x, double y, double z) throws DelaunayError {
		return pointsQuadTree.search(new MyPoint(x, y,z), epsilon);
	}

//	/**
//	 * search for a point
//	 * 
//	 * @param x
//	 * @param y
//	 */
//	protected MyPoint searchPoint(double x, double y) {
//		return pointsQuadTree.search(new MyPoint(x, y), epsilon);
//	}

	/**
	 * Get point, creates it if necessary
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @throws DelaunayError
	 */
	public MyPoint getPoint(double x, double y, double z) throws DelaunayError {
		MyPoint aPoint = searchPoint(x, y, z);

		if (aPoint == null)
			aPoint = new MyPoint(x, y, z);

		return aPoint;
	}

//	/**
//	 * Get point, creates it if necessary
//	 * 
//	 * @param x
//	 * @param y
//	 */
//	public MyPoint getPoint(double x, double y) {
//		MyPoint aPoint = searchPoint(x, y);
//
//		if (aPoint == null)
//			aPoint = new MyPoint(x, y);
//
//		return aPoint;
//	}

	/**
	 * Add the bounding box to current data
	 * @throws DelaunayError 
	 */
	public void addBoundingBox() throws DelaunayError {
		getBoundingBox();
		// Add bounding Box
		MyPoint aPoint1 = new MyPoint(theBox.minx, theBox.miny, 0);
		MyPoint aPoint2 = new MyPoint(theBox.minx, theBox.maxy, 0);
		MyPoint aPoint3 = new MyPoint(theBox.maxx, theBox.maxy, 0);
		MyPoint aPoint4 = new MyPoint(theBox.maxx, theBox.miny, 0);

		point_GID++;
		aPoint1.setGID(point_GID);
		pointsQuadTree.add(aPoint1);
		point_GID++;
		aPoint2.setGID(point_GID);
		pointsQuadTree.add(aPoint2);
		point_GID++;
		aPoint3.setGID(point_GID);
		pointsQuadTree.add(aPoint3);
		point_GID++;
		aPoint4.setGID(point_GID);
		pointsQuadTree.add(aPoint4);
		
		// Generate lines, taking into account the fact there are points with
		// the same x and y
		MyPoint LastPoint;

		// Do not remove points order because it is linked to the order we chose
		// for the points
		// join points 1 and 2 - same x
		LastPoint = aPoint1;
		for (MyPoint aPoint : pointsQuadTree.getAll()) {
			if (aPoint.getX() == LastPoint.getX()) {
				constraintsEdges.add(new MyEdge(LastPoint, aPoint));
				LastPoint = aPoint;
			}
		}
		constraintsEdges.add(new MyEdge(LastPoint, aPoint2));

		// join points 2 and 3 - same y
		LastPoint = aPoint2;
		for (MyPoint aPoint : pointsQuadTree.getAll()) {
			if (aPoint.getY() == LastPoint.getY()) {
				constraintsEdges.add(new MyEdge(LastPoint, aPoint));
				LastPoint = aPoint;
			}
		}
		constraintsEdges.add(new MyEdge(LastPoint, aPoint3));

		// join points 1 and 4 - same y
		LastPoint = aPoint1;
		for (MyPoint aPoint : pointsQuadTree.getAll()) {
			if (aPoint.getY() == LastPoint.getY()) {
				constraintsEdges.add(new MyEdge(LastPoint, aPoint));
				LastPoint = aPoint;
			}
		}
		constraintsEdges.add(new MyEdge(LastPoint, aPoint4));

		// join points 4 and 3 - same x
		LastPoint = aPoint4;
		for (MyPoint aPoint : pointsQuadTree.getAll()) {
			if (aPoint.getX() == LastPoint.getX()) {
				constraintsEdges.add(new MyEdge(LastPoint, aPoint));
				LastPoint = aPoint;
			}
		}
		constraintsEdges.add(new MyEdge(LastPoint, aPoint3));
	}

	/**
	 * Create a new edge in the mesh
	 * 
	 * @param aPoint1
	 * @param aPoint2
	 * @throws DelaunayError
	 */
	public void createEdge(MyPoint aPoint1, MyPoint aPoint2)
			throws DelaunayError {
		if (isMeshComputed())
			throw new DelaunayError(DelaunayError.DelaunayError_Generated);
		else {
			if (pointsQuadTree.search(aPoint1)==null)
			{	

				point_GID++;
				aPoint1.setGID(point_GID);
				pointsQuadTree.add(aPoint1);
			}

			if (pointsQuadTree.search(aPoint2)==null)
			{	

				point_GID++;
				aPoint2.setGID(point_GID);
				pointsQuadTree.add(aPoint2);
			}
			MyEdge anEdge = new MyEdge(aPoint1, aPoint2);
			constraintsEdges.add(anEdge);
		}
	}

	/**
	 * Create a new edge in the mesh as a copy of current edge
	 * 
	 * @param anEdge
	 * @throws DelaunayError
	 */
	public void createEdge(MyEdge anEdge) throws DelaunayError {
		if (isMeshComputed())
			throw new DelaunayError(DelaunayError.DelaunayError_Generated);
		else if (!constraintsEdges.contains(anEdge)) {
			MyPoint aPoint1 = anEdge.getStartPoint();
			MyPoint aPoint2 = anEdge.getEndPoint();
			createEdge(aPoint1, aPoint2);
		}
	}

	// ------------------------------------------------------------------------------------------
	// Random mesh elements

	/**
	 * Set bounding box for the generation
	 * 
	 * @param maxx
	 * @param maxy
	 */
	public void setMax(int maxx, int maxy) {
		this.maxx = maxx;
		this.maxy = maxy;
	}

	/**
	 * Generate random points
	 * 
	 * @param _NbPoints
	 * @throws DelaunayError 
	 */
	public void setRandomPoints(int _NbPoints) throws DelaunayError {
		ArrayList<MyPoint> points= new ArrayList<MyPoint>();
		for (int i = 0; i < _NbPoints; i++) {
			// Generate random coordinates
			double x = Math.random() * maxx;
			double y = Math.random() * maxy;
			double z = Math.random() * (maxx + maxy) / 20.0;
			if (i == 0) {
				x = 0;
				y = 0;
			}
			MyPoint aPoint = new MyPoint(x, y, z);
			aPoint.setGID(i);
			points.add(aPoint);
		}
		pointsQuadTree= new MyQuadTreeMapper<MyPoint>(points);
		
		edgesQuadTree.setBox(pointsQuadTree.getBox());
		trianglesQuadTree.setBox(pointsQuadTree.getBox());
		
	}

	/**
	 * Generate random edges Can be applied only if points are created
	 * 
	 * @param _NbEdges
	 */
	public void setRandomEdges(int _NbEdges) {
		ArrayList<MyPoint> points=pointsQuadTree.getAll();
		int NbPoints = pointsQuadTree.size() - 1;
		if (NbPoints > 1) {
			for (int i = 0; i < _NbEdges; i++) {
				int start = (int) Math.round(Math.random() * NbPoints);
				int end = (int) Math.round(Math.random() * NbPoints);
				while (end == start)
					end = (int) Math.round(Math.random() * NbPoints);
				MyEdge anEdge = new MyEdge(points.get(start), points.get(end));
				anEdge.setGID(i);
				anEdge.setLocked(true);
				constraintsEdges.add(anEdge);
			}
		}
	}

	// ------------------------------------------------------------------------------------------
	/**
	 * Generate the Delaunay's triangularization with a flip-flop algorithm.
	 * Mesh must have been set. Triangularization can only be done once.
	 * Otherwise call reprocessDelaunay
	 * 
	 * @throws DelaunayError
	 */
	public void processDelaunay() throws DelaunayError {
		if (isMeshComputed())
			throw new DelaunayError(DelaunayError.DelaunayError_Generated);
		else if (getNbPoints() < 3)
			throw new DelaunayError(
					DelaunayError.DelaunayError_notEnoughPointsFound);
		else {
			boolean startedLocaly = false;
			if (startComputation == 0) {
				setStart();
				startedLocaly = true;
			}
			// general data structures
			badEdgesQueueList = new LinkedList<MyEdge>();
			boundaryEdges = new LinkedList<MyEdge>();
			LinkedList<MyPoint> badPointList = new LinkedList<MyPoint>();

			if(polygons.size()>0)
			{
				// adding points of polygon to the mesh
				if (verbose)
					System.out.println("Adding point of "+polygons.size()+" polygon"+(polygons.size()>1?"s":""));
			
				System.out.println("\n");
				MyPoint p;
				long i=1, s=polygons.size();
				
				for(MyPolygon aPolygon : polygons)//FIXME when we have 267011 polygons, it's very very too long
				{
					System.out.print("\r"+i+"/"+s);
					i++;
					for(MyPoint aPoint2:aPolygon.getPoints())
					{
						p=searchPoint(aPoint2.getX(), aPoint2.getY(), aPoint2.getZ());
						if(p!=null)
						{	if(aPolygon.isUsePolygonZ())
								p=aPoint2;
						}
						else
						{	
							point_GID++;
							aPoint2.setGID(point_GID);
							pointsQuadTree.add(aPoint2);
						}
					}
				}
			}
			
			// sort points
			if (verbose)
				System.out.println("Sorting points");
			ListIterator<MyPoint> iterPoint =	sortAndSimplify( pointsQuadTree.getAll()).listIterator();

			// we build a first triangle with the 3 first points we find
			if (verbose)
				System.out.println("Processing triangularization");
			MyTriangle aTriangle;
			MyPoint p1, p2, p3;
			MyEdge e1, e2, e3;
			p1 = p2 = p3 = null;

			
			p1 = iterPoint.next();
			while (p1.isLocked())
				p1 = iterPoint.next();

			p2 = iterPoint.next();
			while (p2.isLocked())
				p2 = iterPoint.next();
			e1 = new MyEdge(p1, p2);

			// The 3 points MUST NOT be colinear
			p3 = iterPoint.next();
			while (p3.isLocked())
				p3 = iterPoint.next();
			while ((e1.isColinear2D(p3)) && (iterPoint.hasNext())) {
				badPointList.add(p3);
				
				p3 = iterPoint.next();
				while (p3.isLocked())
					p3 = iterPoint.next();
			}

			// The triangle's edges MUST be in the right direction
			if (e1.isLeft(p3)) {
				e2 = new MyEdge(p2, p3);
				e3 = new MyEdge(p3, p1);
			} else {
				e1.setStartPoint(p2);
				e1.setEndPoint(p1);

				e2 = new MyEdge(p1, p3);
				e3 = new MyEdge(p3, p2);
			}


			edge_GID++;
			e1.setGID(edge_GID);
			edgesQuadTree.add(e1);
			
			edge_GID++;
			e2.setGID(edge_GID);
			edgesQuadTree.add(e2);
			
			edge_GID++;
			e3.setGID(edge_GID);
			edgesQuadTree.add(e3);


			aTriangle = new MyTriangle(e1, e2, e3);
			triangle_GID++;
			aTriangle.setGID(triangle_GID);
			trianglesQuadTree.add(aTriangle);

			// Then process the other points - order don't care
			boundaryEdges.add(e1);
			boundaryEdges.add(e2);
			boundaryEdges.add(e3);

			// flip-flop on a list of points
			boolean ended = false;
			MyPoint aPoint=null;
			MyPoint LastTestedPoint=null;
			int count = 0;
			while (! ended) {
				boolean hasGotPoint = false;
				if (! badPointList.isEmpty()) {
					aPoint = badPointList.getFirst();
					if (LastTestedPoint != aPoint) {
						badPointList.removeFirst();
						hasGotPoint = true;
					}

				}

				if (! hasGotPoint)
					if (iterPoint.hasNext()) {
						count++;
						aPoint = iterPoint.next();
					}
					else {
						ended = true;
						aPoint = null;
					}
				LastTestedPoint = aPoint;
				
				if (aPoint!= null)
					if (!aPoint.isLocked()) {
						if (myInsertPoint(aPoint) == null)
							badPointList.addFirst(aPoint);
					}

			}
			
			meshComputed = true;
			
			// Add the edges in the edges array
			if (verbose)
				System.out.println("Adding edges");
			processEdges(constraintsEdges);
			
			
			if(polygons.size()>0)
			{
				if (verbose)
					System.out.println("Processing edges of "+polygons.size()+" polygon"+(polygons.size()>1?"s":""));
				processPolygons();
			}

			
			// It's fine, we computed the mesh
			if (verbose) {
				System.out.println("End processing");
				System.out.println("Triangularization end phase : ");
				System.out.println("  Points : " + pointsQuadTree.size());
				System.out.println("  Edges : " + edgesQuadTree.size());
				System.out.println("  Triangles : " + trianglesQuadTree.size());
			}
			if (startedLocaly)
				setEnd();
		}			
	}
	
	/**
	 * Re-Generate the Delaunay's triangularization with a flip-flop algorithm.
	 * Mesh must have been set. Every triangle and edge is removed to restart
	 * the process.
	 * 
	 * @throws DelaunayError
	 */
	public void reprocessDelaunay() throws DelaunayError {
		edgesQuadTree = new MyQuadTreeMapper<MyEdge>(theBox);
		trianglesQuadTree = new MyQuadTreeMapper<MyTriangle>(theBox);
		meshComputed = false;

		// Restart the process
		processDelaunay();
	}


	
	
//	/**
//	 * Generate quadtree.
//	 * @throws DelaunayError
//	 */
//	public void generateQuadTree() throws DelaunayError {
//		if (!isMeshComputed())
//			throw new DelaunayError(DelaunayError.DelaunayError_notGenerated);
//		else {
//			MyBox theBox = this.getBoundingBox();
//			if (this.trianglesQuadTree.canBeUsed())
//				this.trianglesQuadTree.remap(theBox);
//			else
//				this.trianglesQuadTree.setBox(theBox);
//			this.trianglesQuadTree.add(triangles);
//		}
//	}

	/**
	 * Add a point inside a triangle and rebuild triangularization
	 * 
	 * @param aTriangle
	 * @param aPoint
	 * @throws DelaunayError
	 */
	public void addPoint(MyTriangle aTriangle, MyPoint aPoint)
			throws DelaunayError {
		if (!aTriangle.isInside(aPoint))
			throw new DelaunayError(DelaunayError.DelaunayError_outsideTriangle);
		else {
			// add point in the triangle
			if (pointsQuadTree.search(aPoint)==null)
			{	
				point_GID++;
				aPoint.setGID(point_GID);
				pointsQuadTree.add(aPoint);
			}
			addPointInsideTriangle(aTriangle, aPoint);

			// Process badTriangleQueueList
			processBadEdges();
		}
	}

	/**
	 * Add a point on an edge and rebuild triangularization
	 * 
	 * @param anEdge
	 * @param aPoint
	 * @throws DelaunayError
	 */
	public void addPoint(MyEdge anEdge, MyPoint aPoint) throws DelaunayError {
		if (!anEdge.isInside(aPoint))
			throw new DelaunayError(DelaunayError.DelaunayError_outsideTriangle);
		else {
			// Add point
			processAddPoint(anEdge, aPoint);

			// Then apply the flip-flop algorithm
			processBadEdges();
		}
	}

	/**
	 * Add a point in the mesh and rebuild triangularization Returns the
	 * triangle that contains the point, null otherwise.
	 * 
	 * @param aPoint
	 * @return returnedPoint
	 * @throws DelaunayError
	 */
	private MyPoint addPoint(MyPoint aPoint, double precision)
			throws DelaunayError {
		// First we check if the point is in the points list
		boolean foundPoint = pointsQuadTree.search(aPoint,precision)!=null;
		MyPoint returnedPoint = aPoint;

		if (!foundPoint) {

			MyPoint p=searchPoint(aPoint.getX(), aPoint.getY(), aPoint.getZ());
				
			if(p!=null)
			{
				if(aPoint.isZUse())
					p.setZ(aPoint.getZ());

				foundPoint=true;
			}

			if (!foundPoint) {
				// First we find inside which triangle it is
				MyTriangle foundTriangle = getTriangle(aPoint);
				
				// Point is not in the mesh
				if (foundTriangle != null) {
					// the point is inside the foundTriangle triangle
					
					// if we want to use Z of triangle and not Z of polygon
					if(aPoint.isUseByPolygon() && !usePolygonZ)
					{	aPoint.setZ(foundTriangle.interpolateZ(aPoint));
					}
					
					if(foundTriangle.getEdge(0).isOnEdge(aPoint))
					{
						addPointOnEdge(aPoint, foundTriangle.getEdge(0));
					}
					else if(foundTriangle.getEdge(1).isOnEdge(aPoint))
					{
						addPointOnEdge(aPoint, foundTriangle.getEdge(1));
					}
					else if(foundTriangle.getEdge(2).isOnEdge(aPoint))
					{
						addPointOnEdge(aPoint, foundTriangle.getEdge(2));
					}
					else
						addPointInsideTriangle(foundTriangle, aPoint);
				} 
				else
				{
					//Search if aPoint is on an Edge.
					MyEdge anEdge=edgesQuadTree.searchInWhichElementItIs(aPoint);

					if(anEdge!=null)
					{
						//add point on edge
						if(!aPoint.isZUse())
							aPoint.setZ(anEdge.getZOnEdge(aPoint));
						
						point_GID++;
						aPoint.setGID(point_GID);
						pointsQuadTree.add(aPoint);
						addPointOnEdge(aPoint, anEdge);
					}
					else
					{
						// the point is outside the mesh
						// The boundary edge list is ok
						// We insert the point in the mesh
						point_GID++;
						aPoint.setGID(point_GID);
						pointsQuadTree.add(aPoint);
						myInsertPoint(aPoint);
					}
				}
			}
			
		}
		return returnedPoint;
	}

	
	/**
	 * Adding point on an edge and make new triangles.
	 * @param aPoint
	 * @param anEdge
	 */
	private void addPointOnEdge(MyPoint aPoint, MyEdge anEdge)
	{
		
		point_GID++;
		aPoint.setGID(point_GID);
		pointsQuadTree.add(aPoint);
		
		MyEdge newEdge1=null;
		MyEdge newEdge2=null;
		MyEdge newEdge3, anEdge4, anEdge5;
		MyTriangle tmp1, tmp2, triangle1, triangle2;
		// Make new triangle at left of edge.
		if(anEdge.getLeft()!=null)
		{
			for(int j=0;j<3;j++)
			{
				if(anEdge.getLeft().getPoint(j)!=anEdge.getStartPoint() && anEdge.getLeft().getPoint(j)!=anEdge.getEndPoint())
				{
					
					newEdge3=new MyEdge(anEdge.getLeft().getPoint(j),aPoint);
					
					newEdge1= new MyEdge(anEdge.getStartPoint(), aPoint);
					anEdge4=anEdge.getLeft().getEdgeFromPoints(anEdge.getStartPoint(), anEdge.getLeft().getPoint(j));
					
	
					
					tmp1=anEdge4.getRight();

					tmp2=anEdge4.getLeft();
					
					triangle1=new MyTriangle(newEdge1, anEdge4, newEdge3);
					
					if(anEdge4.isRight(aPoint))//FIXME setRight / setLeft not very good!
					{
						anEdge4.setRight(triangle1);
						anEdge4.setLeft(tmp2);
					}
					else
					{
						anEdge4.setRight(tmp1);
						anEdge4.setLeft(triangle1);
					}
	
					newEdge2= new MyEdge(aPoint,anEdge.getEndPoint());
					anEdge5=anEdge.getLeft().getEdgeFromPoints(anEdge.getEndPoint(), anEdge.getLeft().getPoint(j));
					
					tmp1=anEdge5.getRight();
					tmp2=anEdge5.getLeft();
					
					triangle2=new MyTriangle(newEdge2, newEdge3, anEdge5  );
					
					if(anEdge5.isRight(aPoint))//FIXME setRight / setLeft not very good!
					{
						anEdge5.setRight(triangle2);
						anEdge5.setLeft(tmp2);
					}
					else
					{
						anEdge5.setRight(tmp1);
						anEdge5.setLeft(triangle2);
					}
					

					triangle_GID++;
					triangle1.setGID(triangle_GID);
					trianglesQuadTree.add(triangle1);

					triangle_GID++;
					triangle2.setGID(triangle_GID);
					trianglesQuadTree.add(triangle2);
					
					if(anEdge.isUseByPolygon())
					{
						newEdge3.setUseByPolygon(true);
					}

					edge_GID++;
					newEdge3.setGID(edge_GID);
					edgesQuadTree.add(newEdge3);
					
					j=3;
				}
			}
			trianglesQuadTree.remove(anEdge.getLeft());
		}
		
		// Make new triangle at right of edge.
		if(anEdge.getRight()!=null)
		{
			for(int j=0;j<3;j++)
			{
				if(anEdge.getRight().getPoint(j)!=anEdge.getStartPoint() && anEdge.getRight().getPoint(j)!=anEdge.getEndPoint())
				{
					
					if(newEdge1==null && newEdge2==null)
					{
						newEdge1= new MyEdge(anEdge.getStartPoint(), aPoint);
						newEdge2= new MyEdge(aPoint,anEdge.getEndPoint());
					}
					
					newEdge3=new MyEdge(anEdge.getRight().getPoint(j),aPoint);
					
					newEdge1= new MyEdge(anEdge.getStartPoint(), aPoint);
					anEdge4=anEdge.getRight().getEdgeFromPoints(anEdge.getStartPoint(), anEdge.getRight().getPoint(j));
					

					tmp1=anEdge4.getRight();

					tmp2=anEdge4.getLeft();
					
					triangle1=new MyTriangle(newEdge1, anEdge4, newEdge3);
					
					if(anEdge4.isRight(aPoint))//FIXME setRight / setLeft not very good!
					{
						anEdge4.setRight(triangle1);
						anEdge4.setLeft(tmp2);
					}
					else
					{
						anEdge4.setRight(tmp1);
						anEdge4.setLeft(triangle1);
					}
	
					newEdge2= new MyEdge(aPoint,anEdge.getEndPoint());
					anEdge5=anEdge.getRight().getEdgeFromPoints(anEdge.getEndPoint(), anEdge.getRight().getPoint(j));
					
					
					tmp1=anEdge5.getRight();
					tmp2=anEdge5.getLeft();
					
					triangle2=new MyTriangle(newEdge2, newEdge3, anEdge5  );
					
					if(anEdge5.isRight(aPoint))//FIXME setRight / setLeft not very good!
					{
						anEdge5.setRight(triangle2);
						anEdge5.setLeft(tmp2);
					}
					else
					{
						anEdge5.setRight(tmp1);
						anEdge5.setLeft(triangle2);
					}
					

					triangle_GID++;
					triangle1.setGID(triangle_GID);
					trianglesQuadTree.add(triangle1);

					triangle_GID++;
					triangle2.setGID(triangle_GID);
					trianglesQuadTree.add(triangle2);
					
					
					if(anEdge.isUseByPolygon())
					{
						newEdge3.setUseByPolygon(true);
					}

					edge_GID++;
					newEdge3.setGID(edge_GID);
					edgesQuadTree.add(newEdge3);
					j=3;
					
				}
			}

			trianglesQuadTree.remove(anEdge.getRight());
		}
		
		if(anEdge.isUseByPolygon())
		{
			newEdge1.setUseByPolygon(true);
			newEdge2.setUseByPolygon(true);
		}
		
			
		edge_GID++;
		newEdge1.setGID(edge_GID);
		edgesQuadTree.add(newEdge1);
		
		edge_GID++;
		newEdge2.setGID(edge_GID);
		edgesQuadTree.add(newEdge2);

		edgesQuadTree.remove(anEdge);
		anEdge=null;
	}
	
	/**
	 * Add a point in the mesh and rebuild triangularization Returns the
	 * triangle that contains the point, null otherwise.
	 * 
	 * @param aPoint
	 * @throws DelaunayError
	 */
	public void addPoint(MyPoint aPoint) throws DelaunayError {
		addPoint(aPoint, this.precision);
	}

	/**
	 * Get the triangle in which the point is
	 * 
	 * @param aPoint
	 */
	public MyTriangle getTriangle(MyPoint aPoint) {
		MyTriangle foundTriangle = null;
		
		// Try to use QuadTree if it is possible
		if (this.trianglesQuadTree.canBeUsed()) {
			foundTriangle = this.trianglesQuadTree.search(aPoint);
		} else {
			ListIterator<MyTriangle> iterTriangle = trianglesQuadTree.getAll().listIterator();
			while ((iterTriangle.hasNext()) && (foundTriangle == null)) {
				MyTriangle aTriangle = iterTriangle.next();
				if (aTriangle.isInside(aPoint)) {
					foundTriangle = aTriangle;
				}
			}
		}

		return foundTriangle;
	}

	/**
	 * Add a point inside a triangle The point is supposed to be in the points
	 * list
	 * 
	 * @param aTriangle
	 * @param aPoint
	 * @throws DelaunayError
	 */
	private void addPointInsideTriangle(MyTriangle aTriangle, MyPoint aPoint) {
		
		point_GID++;
		aPoint.setGID(point_GID);
		pointsQuadTree.add(aPoint);
		
		// Save current edges
		MyEdge oldEdge[] = new MyEdge[3];
		for (int i = 0; i < 3; i++) {
			oldEdge[i] = aTriangle.edges[i];
		}
		MyPoint firstPoint = aTriangle.edges[0].getStartPoint();
		MyPoint secondPoint = aTriangle.edges[0].getEndPoint();
		MyPoint alterPoint = aTriangle.getAlterPoint(firstPoint, secondPoint);

		// First step, we check if the point is one of the current points

		// We need 2 more triangles
		MyTriangle aTriangle1 = new MyTriangle();
		aTriangle1.setProperty(aTriangle.getProperty());
		MyTriangle aTriangle2 = new MyTriangle();
		aTriangle2.setProperty(aTriangle.getProperty());
		MyTriangle aTriangle3 = aTriangle;
		
		// Create 3 new edges
		MyEdge anEdge[] = new MyEdge[3];
		anEdge[0] = new MyEdge(secondPoint, aPoint);
		anEdge[1] = new MyEdge(aPoint, firstPoint);
		anEdge[2] = new MyEdge(aPoint, alterPoint);
		for (int i = 0; i < 3; i++) {
			edge_GID++;
			anEdge[i].setGID(edge_GID);
			edgesQuadTree.add(anEdge[i]);
		}

		// set edges
		// Triangle 1 : firstPoint, secondPoint, aPoint
		aTriangle1.edges[0] = oldEdge[0];
		aTriangle1.edges[1] = anEdge[0];
		aTriangle1.edges[2] = anEdge[1];

		// Triangle 2 : secondPoint, aPoint, alterPoint
		if ((secondPoint == oldEdge[1].getStartPoint())
				|| (secondPoint == oldEdge[1].getEndPoint()))
			aTriangle2.edges[0] = oldEdge[1];
		else
			aTriangle2.edges[0] = oldEdge[2];
		aTriangle2.edges[1] = anEdge[2];
		aTriangle2.edges[2] = anEdge[0];

		// Triangle 3 : firstPoint, aPoint, alterPoint
		if ((firstPoint == oldEdge[2].getStartPoint())
				|| (firstPoint == oldEdge[2].getEndPoint()))
			aTriangle3.edges[0] = oldEdge[2];
		else
			aTriangle3.edges[0] = oldEdge[1];
		aTriangle3.edges[1] = anEdge[1];
		aTriangle3.edges[2] = anEdge[2];

		// Link outside edges to triangles
		if (aTriangle1.edges[0].getLeft() == aTriangle)
			aTriangle1.edges[0].setLeft(aTriangle1);
		else
			aTriangle1.edges[0].setRight(aTriangle1);
		if (aTriangle2.edges[0].getLeft() == aTriangle)
			aTriangle2.edges[0].setLeft(aTriangle2);
		else
			aTriangle2.edges[0].setRight(aTriangle2);
		if (aTriangle3.edges[0].getLeft() == aTriangle)
			aTriangle3.edges[0].setLeft(aTriangle3);
		else
			aTriangle3.edges[0].setRight(aTriangle3);

		// Link inside edges to triangles

		// anEdge[0] is connected to triangles 1 and 2
		// firstPoint is not in anEdge[0]
		// Triangle with anEdge[0] and firstPoint is Triangle 1
		if (anEdge[0].isLeft(firstPoint)) {
			anEdge[0].setLeft(aTriangle1);
			anEdge[0].setRight(aTriangle2);
		} else {
			anEdge[0].setRight(aTriangle1);
			anEdge[0].setLeft(aTriangle2);
		}

		// anEdge[1] is connected to triangles 1 and 3
		// alterPoint is not in anEdge[1]
		// Triangle with anEdge[1] and alterPoint is Triangle 3
		if (anEdge[1].isLeft(alterPoint)) {
			anEdge[1].setLeft(aTriangle3);
			anEdge[1].setRight(aTriangle1);
		} else {
			anEdge[1].setRight(aTriangle3);
			anEdge[1].setLeft(aTriangle1);
		}

		// anEdge[2] is connected to triangles 3 and 2
		// firstPoint is not in anEdge[2]
		// Triangle with anEdge[2] and firstPoint is Triangle 3
		if (anEdge[2].isLeft(firstPoint)) {
			anEdge[2].setLeft(aTriangle3);
			anEdge[2].setRight(aTriangle2);
		} else {
			anEdge[2].setRight(aTriangle3);
			anEdge[2].setLeft(aTriangle2);
		}

		// Rebuild all topologies
		aTriangle1.recomputeCenter();
		aTriangle2.recomputeCenter();
		aTriangle3.recomputeCenter();

		// Add edges to the bad edges list
		if (!isMeshComputed())
			for (int i = 0; i < 3; i++) {
				if (!badEdgesQueueList.contains(aTriangle1.edges[i]))
					badEdgesQueueList.add(aTriangle1.edges[i]);
				if (!badEdgesQueueList.contains(aTriangle2.edges[i]))
					badEdgesQueueList.add(aTriangle2.edges[i]);
				if (!badEdgesQueueList.contains(aTriangle3.edges[i]))
					badEdgesQueueList.add(aTriangle3.edges[i]);
			}
		
		
		triangle_GID++;
		aTriangle1.setGID(triangle_GID);
		trianglesQuadTree.add(aTriangle1);

		triangle_GID++;
		aTriangle2.setGID(triangle_GID);
		trianglesQuadTree.add(aTriangle2);
	}

	/**
	 * Add a point on an edge
	 * 
	 * @param anEdge
	 * @param aPoint
	 * @return impactedTriangles
	 */
	private LinkedList<MyTriangle> processAddPoint(MyEdge anEdge, MyPoint aPoint) {
		LinkedList<MyTriangle> impactedTriangles = new LinkedList<MyTriangle>();
		if (!anEdge.isExtremity(aPoint)) {
			// point is not an extremity => insert it
			MyPoint start = anEdge.getStartPoint();

			MyPoint end = anEdge.getEndPoint();

			// triangles and their copies to generate the new ones
			MyTriangle triangleList[] = new MyTriangle[2];
			MyTriangle new_triangleList[] = new MyTriangle[2];
			// The end part of anEdge after spliting
			MyEdge remainEdge = null;
			// points of the triangles to be joined with the new point
			MyPoint alterPointList[] = new MyPoint[2];
			// edges that link alterPointList and the point + one more for
			// remainEdge
			MyEdge newEdges[] = new MyEdge[3];
			// connections from alterPointList to start and end
			MyEdge alterEdgeList_start[] = new MyEdge[2];
			MyEdge alterEdgeList_end[] = new MyEdge[2];
			// triangles connected to the alteredges linked to end
			MyTriangle alterTriangleList_end[] = new MyTriangle[2];

			// Do the same thing right and left
			for (int k = 0; k < 2; k++) {
				// Get base triangle
				MyTriangle aTriangle1 = null;
				if (k == 0)
					aTriangle1 = anEdge.getLeft();
				else
					aTriangle1 = anEdge.getRight();
				triangleList[k] = aTriangle1;
				new_triangleList[k] = null;

				alterPointList[k] = null;
				newEdges[k] = null;
				alterEdgeList_start[k] = null;
				alterEdgeList_end[k] = null;
				alterTriangleList_end[k] = null;

				if (aTriangle1 != null) {
					new_triangleList[k] = new MyTriangle(aTriangle1);

					alterPointList[k] = aTriangle1.getAlterPoint(start, end);
					newEdges[k] = new MyEdge(alterPointList[k], aPoint);

					alterEdgeList_start[k] = aTriangle1.getEdgeFromPoints(
							start, alterPointList[k]);
					alterEdgeList_end[k] = aTriangle1.getEdgeFromPoints(end,
							alterPointList[k]);

					if (alterEdgeList_end[k] == null)
						System.out.println("ERREUR");
					else if (alterEdgeList_end[k].getLeft() == aTriangle1)
						alterTriangleList_end[k] = alterEdgeList_end[k]
								.getRight();
					else
						alterTriangleList_end[k] = alterEdgeList_end[k]
								.getLeft();
				}
			}

			
			edgesQuadTree.remove(anEdge);
			
			// then split anEdge
			remainEdge = new MyEdge(anEdge);
			remainEdge.setStartPoint(aPoint);		
			anEdge.setEndPoint(aPoint);

			edge_GID++;
			anEdge.setGID(edge_GID);
			edgesQuadTree.add(anEdge);
			

			// Make the triangles ok
			// changes in the new triangles
			// - anEdge by remainEdge
			// - start by aPoint
			// - alterEdgeList_start by newEdges
			// changes in the old triangles
			// - end by aPoint
			// - alterEdgeList_end by newEdges
			for (int k = 0; k < 2; k++) {
				if (new_triangleList[k] != null) {
					// change anEdge
					int i = 0;
					boolean found = false;
					while ((i < 3) && (!found)) {
						if (new_triangleList[k].edges[i] == anEdge)
							found = true;
						else
							i++;
					}
					if (found)
						new_triangleList[k].edges[i] = remainEdge;

					// change alterEdgeList_start
					i = 0;
					found = false;
					while ((i < 3) && (!found)) {
						if (new_triangleList[k].edges[i] == alterEdgeList_start[k])
							found = true;
						else
							i++;
					}
					if (found)
						new_triangleList[k].edges[i] = newEdges[k];
				}
				if (triangleList[k] != null) {
					// change alterEdgeList_end
					boolean found = false;
					int i = 0;
					found = false;
					while ((i < 3) && (!found)) {
						if (triangleList[k].edges[i] == alterEdgeList_end[k])
							found = true;
						else
							i++;
					}
					if (found)
						triangleList[k].edges[i] = newEdges[k];
				}
			}
			// --------------------------------------------------
			// change connections
			// change alterEdgeList_end connection
			for (int k = 0; k < 2; k++) {
				if (alterEdgeList_end[k] != null) {
					if (alterEdgeList_end[k].getLeft() == triangleList[k])
						alterEdgeList_end[k].setLeft(new_triangleList[k]);
					else
						alterEdgeList_end[k].setRight(new_triangleList[k]);
				}
			}

			// change remainEdge connections
			for (int k = 0; k < 2; k++) {
				if (remainEdge != null) {
					if (remainEdge.getLeft() == triangleList[k])
						remainEdge.setLeft(new_triangleList[k]);
					if (remainEdge.getRight() == triangleList[k])
						remainEdge.setRight(new_triangleList[k]);
				}
			}

			// add connection for the newEdges
			for (int k = 0; k < 2; k++) {
				if (newEdges[k] != null) {
					if (newEdges[k].isLeft(end)) {
						newEdges[k].setLeft(new_triangleList[k]);
						newEdges[k].setRight(triangleList[k]);
					} else {
						newEdges[k].setLeft(triangleList[k]);
						newEdges[k].setRight(new_triangleList[k]);
					}
				}
			}

			// --------------------------------------------------
			// Recompute triangle centers
			for (int k = 0; k < 2; k++) {
				if (triangleList[k] != null)
					triangleList[k].recomputeCenter();
				if (new_triangleList[k] != null)
					new_triangleList[k].recomputeCenter();
			}

			// --------------------------------------------------
			// Add elements to the lists
			// add point to the list
			point_GID++;
			aPoint.setGID(point_GID);
			pointsQuadTree.add(aPoint);
			// add the 3 new edges to the list
			newEdges[2] = remainEdge;
			for (int k = 0; k < 3; k++) {
				if (newEdges[k] != null) {
					edge_GID++;
					newEdges[k].setGID(edge_GID);
					edgesQuadTree.add(newEdges[k]);//FIXME too slow
					if (!isMeshComputed())
						if (!badEdgesQueueList.contains(newEdges[k]))
							badEdgesQueueList.add(newEdges[k]);
				}

			}
			// add the 2 new triangle to the list
			for (int k = 0; k < 2; k++) {
				if (new_triangleList[k] != null)
				{	
					triangle_GID++;
					new_triangleList[k].setGID(triangle_GID);
					trianglesQuadTree.add(new_triangleList[k]);
				}
			}

			for (int k = 0; k < 2; k++) {
				if (triangleList[k] != null)
					impactedTriangles.add(triangleList[k]);
				if (new_triangleList[k] != null)
					impactedTriangles.add(new_triangleList[k]);
			}

		}
		return impactedTriangles;
	}

	/**
	 * Add a new edge to the current triangularization. If Delaunay
	 * triangularization has not been done, it generates an error.
	 * 
	 * @param p1
	 * @param p2
	 * @throws DelaunayError
	 */
	public void addEdge(MyPoint p1, MyPoint p2) throws DelaunayError {
		if (!isMeshComputed())
			throw new DelaunayError(DelaunayError.DelaunayError_notGenerated);
		else if (p1.squareDistance(p2) < tolarence)
			throw new DelaunayError(DelaunayError.DelaunayError_proximity);
		else if (pointsQuadTree.search(p1)==null)
			throw new DelaunayError(DelaunayError.DelaunayError_pointNotFound);
		else if (pointsQuadTree.search(p2)==null)
			throw new DelaunayError(DelaunayError.DelaunayError_pointNotFound);
		else {
			badEdgesQueueList = new LinkedList<MyEdge>();
			ArrayList<MyEdge> theList = new ArrayList<MyEdge>();
			MyEdge anEdge = new MyEdge(p1, p2);
			theList.add(anEdge);
			processEdges(theList);
		}
	}

	/**
	 * Add a new edge to the current triangularization. If Delaunay
	 * triangularization has not been done, it generates an error.
	 * 
	 * @param anEdge
	 * @throws DelaunayError
	 */
	public void addEdge(MyEdge anEdge) throws DelaunayError {
		if (!isMeshComputed())
			throw new DelaunayError(DelaunayError.DelaunayError_notGenerated);
		else if (anEdge.getStartPoint().squareDistance(anEdge.getEndPoint()) < tolarence)
			throw new DelaunayError(DelaunayError.DelaunayError_proximity);
		else if (!pointsQuadTree.contains(anEdge.getStartPoint()))
			throw new DelaunayError(DelaunayError.DelaunayError_pointNotFound);
		else if (!pointsQuadTree.contains(anEdge.getEndPoint()))
			throw new DelaunayError(DelaunayError.DelaunayError_pointNotFound);
		else {
			badEdgesQueueList = new LinkedList<MyEdge>();
			ArrayList<MyEdge> theList = new ArrayList<MyEdge>();
			theList.add(anEdge);
			processEdges(theList);
		}
	}
	
	/**
	 * Add a polygon to the Mesh
	 * 
	 * @param aPolygon
	 * @throws DelaunayError
	 */
	public void addPolygon(MyPolygon aPolygon) throws DelaunayError {
		
		polygons.add(aPolygon);
		
		if (isMeshComputed())
		{
			badEdgesQueueList=new LinkedList<MyEdge>();
			constraintsEdges = new ArrayList<MyEdge>();
			boundaryEdges = new LinkedList<MyEdge>();
			
			if (verbose)
				System.out.println("Adding point of polygon");
		
			usePolygonZ=aPolygon.isUsePolygonZ();
			
			// adding points of polygon to the mesh
			for (MyPoint aPoint : aPolygon.getPoints()) {
				addPoint(aPoint);
			}	
			
			if (verbose)
				System.out.println("Processing edges of polygon");
			processOnePolygon(aPolygon);
		}
	}
	
	

	/**
	 * Add a level edge
	 * @param startPoint
	 * @param endPoint
	 * @throws DelaunayError
	 */
	public void addLevelEdge(MyPoint startPoint, MyPoint endPoint) throws DelaunayError {
		addLevelEdge(new MyEdge(startPoint, endPoint));
	}
	
	/**
	 * Add a level edge
	 * @param anEdge
	 * @throws DelaunayError
	 */
	public void addLevelEdge(MyEdge anEdge) throws DelaunayError {
		
		anEdge.setLevelEdge(true);
		anEdge.setUseZ(true);
		if (!isMeshComputed())
		{
			createEdge(anEdge);
		}
		else
		{
			if (verbose)
				System.out.println("Adding level edge");
		
			addPoint(anEdge.getStartPoint());
			addPoint(anEdge.getEndPoint());
			addEdge(anEdge);
		}
	}
	
	
	/**
	 * Set new property to triangles who are inside the polygon.
	 * @param aPolygon
	 * @param refTriangle
	 * @return List of triangle who are inside the polygon.
	 * @throws DelaunayError 
	 */
	public LinkedList<MyTriangle> setPropertyToTriangleInPolygon(MyPolygon aPolygon, MyTriangle refTriangle) throws DelaunayError {
		LinkedList<MyTriangle> triangleOfPolygon = new LinkedList<MyTriangle>();

		refTriangle.setProperty(aPolygon.getProperty());
		triangleOfPolygon.add(refTriangle);
		ListIterator<MyTriangle> triangleOfPolygonIt=triangleOfPolygon.listIterator();

		MyTriangle aTriangleInPolygon;
		MyTriangle unknowTriangle;
		MyEdge aEdge;
		
		while (triangleOfPolygonIt.hasNext()) {
			aTriangleInPolygon = (MyTriangle) triangleOfPolygonIt.next();

			aTriangleInPolygon.setMarked(0, true);
			
			for(int i=0; i<3;i++)
			{
				aEdge= aTriangleInPolygon.getEdge(i);
				unknowTriangle = aEdge.getLeft();
				
				// if left triangle is inside the polygon
				if(unknowTriangle!=null && !unknowTriangle.equals(aTriangleInPolygon) && !unknowTriangle.isMarked(0) && aPolygon.contains(unknowTriangle.getBarycenter()))
				{
					unknowTriangle.setProperty(aPolygon.getProperty());
					triangleOfPolygonIt.add(unknowTriangle);
					triangleOfPolygonIt.previous();
				}else
				{
					unknowTriangle = aEdge.getRight();
					
					// if right triangle is inside the polygon
					if(unknowTriangle!=null && !unknowTriangle.equals(aTriangleInPolygon) && !unknowTriangle.isMarked(0) && aPolygon.contains(unknowTriangle.getBarycenter()))
					{
						unknowTriangle.setProperty(aPolygon.getProperty());
						triangleOfPolygonIt.add(unknowTriangle);
						triangleOfPolygonIt.previous();
					}
				}
			}
		}
		
		while (triangleOfPolygonIt.hasPrevious()) {
			aTriangleInPolygon = (MyTriangle) triangleOfPolygonIt.previous();
			aTriangleInPolygon.setMarked(0, false);
		}
		
		return triangleOfPolygon;
	}
	
	
	
	


	/**
	 * Refine mesh according to the type of refinement that has been defined in
	 * the refinement variable
	 * 
	 * @throws DelaunayError
	 */
	public void refineMesh() throws DelaunayError {
		if (!isMeshComputed())
			throw new DelaunayError(DelaunayError.DelaunayError_notGenerated);
		else {
			// check all triangle to save all the ones with a bad area criteria
			LinkedList<MyTriangle> badTrianglesList = new LinkedList<MyTriangle>();
			LinkedList<MyPoint> barycenter = new LinkedList<MyPoint>();

			boolean softInterpolate = ((refinement & refinement_softInterpolate) != 0);
			int iterDone = 0;
			int nbDone = 0;
			do {
				iterDone++;
				nbDone = 0;

				if ((refinement & refinement_maxArea) != 0) {
					// Look for triangles with large area
					// Generate barycenter for each bad triangle
					for (MyTriangle aTriangle : trianglesQuadTree.getAll()) {
						double area = aTriangle.computeArea();
						if (area > maxArea) {
							badTrianglesList.add(aTriangle);

							// Generate barycenter
							MyPoint newPoint = aTriangle.getBarycenter();
							if (softInterpolate) {
								double ZValue = aTriangle
										.softInterpolateZ(newPoint);
								newPoint.setZ(ZValue);
							}
							barycenter.add(newPoint);
						}
					}

					// Add barycenters to triangles
					while (!badTrianglesList.isEmpty()) {
						MyTriangle aTriangle = badTrianglesList.getFirst();
						MyPoint newPoint = barycenter.getFirst();
						badTrianglesList.removeFirst();
						barycenter.removeFirst();
						nbDone++;

						// build a new point inside
						addPoint(aTriangle, newPoint);
					}
				}

				if ((refinement & refinement_minAngle) != 0) {
					// Look for triangles with a very small angle
					for (MyTriangle aTriangle : trianglesQuadTree.getAll()) {	
						if (aTriangle.badAngle(minAngle) >= 0)
							badTrianglesList.add(aTriangle);
					}

					// Try to flip-flap
					// We do not count modifications for this : if it fails we
					// do not want to retry
					while (!badTrianglesList.isEmpty()) {
						MyTriangle aTriangle = badTrianglesList.getFirst();
						badTrianglesList.removeFirst();

						// try to flip-flap with the longest edge
						MyEdge longest = aTriangle.edges[0];
						double maxLength = longest.getSquared2DLength();
						for (int i = 1; i < 3; i++) {
							double length = aTriangle.edges[i]
									.getSquared2DLength();
							if (length > maxLength) {
								maxLength = length;
								longest = aTriangle.edges[i];
							}
						}

						// try to flip-flap
						tryFlipFlap(aTriangle, longest);

					}
				}

				if ((refinement & refinement_obtuseAngle) != 0) {
					// Look for triangles with an obtuse angle
					for (MyTriangle aTriangle : trianglesQuadTree.getAll()) {
						if (aTriangle.getMaxAmgle() >= 90)
							badTrianglesList.add(aTriangle);
					}
					
					// Try to flip-flap
					while (!badTrianglesList.isEmpty()) {
						/*MyTriangle aTriangle =*/ badTrianglesList.getFirst();
						badTrianglesList.removeFirst();

						// There might be something to do...
					}
				}

			} while (nbDone != 0);
		}
	}

	/**
	 *  Add edges of polygons and set polygon's property to triangle who are inside the polygon.
	 * @throws DelaunayError 
	 */
	private void processPolygons() throws DelaunayError {
		
		for(MyPolygon aPolygon : polygons)
		{	
			processOnePolygon(aPolygon);
		}
	}
	
	
	/**
	 *  Add edges of one polygon and set polygon's property to triangle who are inside the polygon.
	 * @throws DelaunayError 
	 */
	private void processOnePolygon(MyPolygon aPolygon) throws DelaunayError {
		
		usePolygonZ=aPolygon.isUsePolygonZ();
		
		if(aPolygon.isEmpty())
		{			

			// Adding edges of polygon to the mesh.
			for(MyEdge anEdge : processEdges(aPolygon.getEdges()) )
			{
				if(anEdge.isLocked())
				{	
					// Set property of polygon to triangle who are inside the polygon.
					if(anEdge.getLeft()!=null && aPolygon.contains(anEdge.getLeft().getBarycenter()))
					{	
						aPolygon.setRefTriangle(anEdge.getLeft());
						if(aPolygon.mustBeTriangulated())
							processSomePoints(removeTriangleInPolygon(aPolygon, anEdge.getLeft()), aPolygon);// Create a polygon with NEW triangle inside.
						else
							removeTriangleInPolygon(aPolygon, anEdge.getLeft());// Create a polygon with NO triangle inside.
						break;
					}else if(anEdge.getRight()!=null && aPolygon.contains(anEdge.getRight().getBarycenter()))
					{	
						aPolygon.setRefTriangle(anEdge.getRight());
						if(aPolygon.mustBeTriangulated())
							processSomePoints(removeTriangleInPolygon(aPolygon, anEdge.getRight()), aPolygon);// Create a polygon with NEW triangle inside.
						else
							removeTriangleInPolygon(aPolygon, anEdge.getRight());// Create a polygon with NO triangle inside.
						break;
					}
				}
			}
		}
		else
		{
			// Create a polygon with triangle inside.
			
			// Adding edges of polygon to the mesh.
			for(MyEdge aEdge : processEdges(aPolygon.getEdges()) )
			{	if(aEdge.isLocked())
				{	
					// Set property of polygon to triangle who are inside the polygon.
					if(aEdge.getLeft()!=null && aPolygon.contains(aEdge.getLeft().getBarycenter()))
					{	
						aPolygon.setRefTriangle(aEdge.getLeft());
						setPropertyToTriangleInPolygon(aPolygon, aEdge.getLeft());
						break;
					}else if(aEdge.getRight()!=null && aPolygon.contains(aEdge.getRight().getBarycenter()))
					{	
						aPolygon.setRefTriangle(aEdge.getRight());
						setPropertyToTriangleInPolygon(aPolygon, aEdge.getRight());
						break;
					}
				}
			}
		}
	}
	
	/**
	 * Add edges defined at the beginning of the process
	 * 
	 * @param constraintsEdges
	 * @throws DelaunayError 
	 */
	private ArrayList<MyEdge> processEdges(ArrayList<MyEdge> constraintsEdges) throws DelaunayError {
		
		int nbEdges2 = constraintsEdges.size();
		if (nbEdges2 > 0)
			MyTools.quickSort_Edges(constraintsEdges, 0, nbEdges2 - 1, false);
		
		// Process unconnected edges
		ArrayList<MyEdge> remain0 = processEdges_Step0(constraintsEdges);
		if (verbose)
			System.out.println("Edges left after initial phase : "
					+ remain0.size());
		
		// Process exact existing edges
		ArrayList<MyEdge> remain1 = processEdges_Step1(remain0);
		if (verbose)
			System.out.println("Edges left after phase 1 : " + remain1.size());
		
		// next try
		ArrayList<MyEdge> remain2 = processEdges_Step2(remain1);
		
		if (verbose)
			System.out.println("Edges left after phase 2 : " + remain2.size());

		// Process remaining edges
		int nbEdges4 = remain2.size();
		if (nbEdges4 > 0)
			MyTools.quickSort_Edges(remain2, 0, nbEdges4 - 1, true);
		return processOtherEdges(remain2);
	}

	/**
	 * Mark existing edges (constraintsEdges and edges are supposed to be
	 * sorted)
	 * 
	 * @param constraintsEdges
	 * @return list of remaining edges
	 */
	private ArrayList<MyEdge> processEdges_Step0(
			ArrayList<MyEdge> constraintsEdges) {
		ArrayList<MyEdge> remainEdges = new ArrayList<MyEdge>();

		// While there is still an edge to process
		for (MyEdge anEdge : constraintsEdges) {
			if (anEdge.isOutsideMesh()) {
				anEdge.setLocked(true);
//				edges.add(anEdge);//edges
				edge_GID++;
				anEdge.setGID(edge_GID);
				edgesQuadTree.add(anEdge);
			} else {
				// To be connected
				remainEdges.add(anEdge);
			}
		}

		return remainEdges;
	}

	/**
	 * Mark existing edges (constraintsEdges and edges are supposed to be
	 * sorted)
	 * 
	 * @param constraintsEdges
	 * @return list of remaining edges
	 */
	private ArrayList<MyEdge> processEdges_Step1(
			ArrayList<MyEdge> constraintsEdges) {
		ArrayList<MyEdge> remainEdges = new ArrayList<MyEdge>();
		ArrayList<MyEdge> possibleIntersectEdges;
		MyEdge currentEdge = null;
		MyEdge currentEdge2 = null;
		int index = 0;
		int maxIndex = 0;
		double cle_ref1, cle_ref2, cle_ref3, cle_ref4;
		double cle1, cle2, cle3, cle4;
		double x;

		// While there is still an edge to process
		ListIterator<MyEdge> iterEdge = constraintsEdges.listIterator();
		while (iterEdge.hasNext()) {
			// Get first edge then remove it from the list
			currentEdge = iterEdge.next();

			// Compute edge intersection with the Mesh
			MyPoint p1 = currentEdge.getStartPoint();
			MyPoint p2 = currentEdge.getEndPoint();

			cle_ref1 = p1.getX();
			cle_ref2 = p1.getY();
			cle_ref3 = p2.getX();
			cle_ref4 = p2.getY();
			if (cle_ref3 < cle_ref1) {
				x = cle_ref3;
				cle_ref3 = cle_ref1;
				cle_ref1 = x;

				x = cle_ref4;
				cle_ref4 = cle_ref2;
				cle_ref2 = x;
			} else if ((cle_ref3 == cle_ref1) && (cle_ref4 < cle_ref2)) {
				x = cle_ref4;
				cle_ref4 = cle_ref2;
				cle_ref2 = x;
			}

			possibleIntersectEdges=edgesQuadTree.searchAll(currentEdge.getBoundingBox());//FIXME searchAll is very slow

			if(possibleIntersectEdges!=null)
			{
				index=possibleIntersectEdges.size();
				boolean found = false;
				boolean ended = false;
				int i = index;
				while ((!found) && (!ended) && (i < maxIndex)) {
					currentEdge2 = possibleIntersectEdges.get(i);
					MyPoint p3 = currentEdge2.getStartPoint();
					MyPoint p4 = currentEdge2.getEndPoint();
					cle1 = p3.getX();
					cle2 = p3.getY();
					cle3 = p4.getX();
					cle4 = p4.getY();
					if (cle3 < cle1) {
						x = cle3;
						cle3 = cle1;
						cle1 = x;
	
						x = cle4;
						cle4 = cle2;
						cle2 = x;
					} else if ((cle3 == cle1) && (cle4 < cle2)) {
						x = cle4;
						cle4 = cle2;
						cle2 = x;
					}
	
					if (cle1 < cle_ref1) {
						// possible edge not reached
						i++;
						index++;
					} else if (cle1 > cle_ref1) {
						// possible edge over
						ended = true;
					} else if (cle2 < cle_ref2) {
						// possible edge not reached
						i++;
						index++;
					} else if (cle2 > cle_ref2) {
						// possible edge over
						ended = true;
					} else if (cle3 < cle_ref3) {
						// possible edge not reached
						i++;
						index++;
					} else if (cle3 > cle_ref3) {
						// possible edge over
						ended = true;
					} else if (cle4 < cle_ref4) {
						// possible edge not reached
						i++;
						index++;
					} else if (cle4 > cle_ref4) {
						// possible edge over
						ended = true;
					} else {
						// probable equality
						if ((p1 == p3) && (p2 == p4))
							found = true;
						else if ((p1 == p4) && (p2 == p3)) {
							found = true;
							// but in reverse order - swap it
							currentEdge2.swap();
						} else
							i++;
					}
				}
	
				if (found) {
					// Edge exists => mark it
					currentEdge2.setLocked(true);
					currentEdge2.setProperty(currentEdge.getProperty());
				} else {
					// Not found
					remainEdges.add(currentEdge);
				}
			}
			else {
				// Not found
				remainEdges.add(currentEdge);
			}
		}
		return remainEdges;
	}


	private MyEdge lookForSwap(MyEdge testEdge, MyPoint start, MyPoint end) throws DelaunayError {
		MyEdge canLink = null;
		int i = 0;

		while ((canLink == null) && (i < 2)) {
			MyTriangle aTriangle;
			if (i == 0)
				aTriangle = testEdge.getLeft();
			else
				aTriangle = testEdge.getRight();

			if (aTriangle != null) {
				// Check for the edge that does not include start
				MyEdge possibleEdge = null;
				for (int j = 0; j < 3; j++) {
					MyEdge anEdge = aTriangle.edges[j];
					if ((anEdge.getStartPoint() != start)
							&& (anEdge.getEndPoint() != start))
						possibleEdge = anEdge;
				}

				// Check for the triangle that is not aTriangle;
				MyTriangle alterTriangle = null;
				if (possibleEdge != null)
					if (possibleEdge.getLeft() == aTriangle)
						alterTriangle = possibleEdge.getRight();
					else
						alterTriangle = possibleEdge.getLeft();

				// Check if the last point is end
				boolean match = false;
				if (alterTriangle != null)
					if (alterTriangle.belongsTo(end))
						match = true;

				// Check if we can swap that edge
				if (match) {
					if (!possibleEdge.isLocked())
						if (possibleEdge.getIntersection(start, end) != null)
							canLink = possibleEdge;
				}
			}
			i++;
		}

		return canLink;
	}

	/**
	 * Mark existing edges (constraintsEdges and edges are supposed to be
	 * sorted)
	 * 
	 * @param constraintsEdges
	 * @return list of remaining edges
	 * @throws DelaunayError 
	 */
	private ArrayList<MyEdge> processEdges_Step2(
			ArrayList<MyEdge> constraintsEdges) throws DelaunayError {
		ArrayList<MyEdge> remainEdges = new ArrayList<MyEdge>();
		ArrayList<MyEdge> EdgesToSwap = new ArrayList<MyEdge>();
		ArrayList<MyEdge> possibleIntersectEdges;
		MyEdge currentEdge = null;
		MyEdge currentEdge2 = null;
		int index = 0;
		int maxIndex=0;
		double cle_ref1, cle_ref3;
		double cle1, cle3;
		double x;

		// While there is still an edge to process
		ListIterator<MyEdge> iterEdge = constraintsEdges.listIterator();
		while (iterEdge.hasNext()) {
			// Get first edge then remove it from the list
			currentEdge = iterEdge.next();
			boolean found = false;

			// Compute edge intersection with the Mesh
			MyPoint p1 = currentEdge.getStartPoint();
			MyPoint p2 = currentEdge.getEndPoint();

			cle_ref1 = p1.getX();
			cle_ref3 = p2.getX();
			if (cle_ref3 < cle_ref1) {
				x = cle_ref3;
				cle_ref3 = cle_ref1;
				cle_ref1 = x;
			}

			
			possibleIntersectEdges = edgesQuadTree.searchAll(currentEdge.getBoundingBox());//FIXME searchAll is very slow
			if(possibleIntersectEdges!=null)
			{	
				maxIndex=possibleIntersectEdges.size();
				boolean ended = false;
				int i = index;
				while ((!found) && (!ended) && (i < maxIndex)) {
					currentEdge2 = possibleIntersectEdges.get(i);
					MyPoint p3 = currentEdge2.getStartPoint();
					MyPoint p4 = currentEdge2.getEndPoint();
					cle1 = p3.getX();
					cle3 = p4.getX();
					if (cle3 < cle1) {
						x = cle3;
						cle3 = cle1;
						cle1 = x;
					}
	
					if (cle1 < cle_ref1) {
						// possible edge not reached
						i++;
						index++;
					} else if (cle3 < cle_ref1) {
						// possible edge not reached
						i++;
					} else if (cle1 > cle_ref3) {
						// possible edge over
						ended = true;
					} else {
						// probable equality
						boolean mayTest = false;
						MyPoint start = null, end = null;
						if ((p1 == p3) || (p1 == p4)) {
							mayTest = true;
							start = p1;
							end = p2;
						} else if ((p2 == p4) || (p2 == p3)) {
							mayTest = true;
							start = p2;
							end = p1;
						}
	
						if (mayTest) {
							MyEdge swapEdge = lookForSwap(currentEdge2, start, end);
							if (swapEdge != null) {
								swapEdge.setProperty(currentEdge.getProperty());
								EdgesToSwap.add(swapEdge);

								found = true;
	
								// look for swapping edge
								if (p1 == swapEdge.getEndPoint())
									swapEdge.swap();
							}
						}
	
						if (!found)
							i++;
					}
				}
				if (!found) {
					remainEdges.add(currentEdge);
				}
			}
		}

		// swap edges
		for (MyEdge anEdge : EdgesToSwap) {
			if (!anEdge.isLocked())
				swapTriangle(anEdge.getLeft(), anEdge.getRight(), anEdge, true);
			anEdge.setLocked(true);
		}

		return remainEdges;
	}


	/**
	 * Mark existing edges (constraintsEdges and edges are supposed to be
	 * sorted)
	 * 
	 * @param constraintsEdges
	 * @return list of remaining edges
	 * @throws DelaunayError 
	 */
	private ArrayList<MyEdge> processOtherEdges(ArrayList<MyEdge> constraintsEdges) throws DelaunayError {

		// List of triangles that are created when there is an intersection
		MyEdge currentEdge = null;
		ArrayList<MyEdge> possibleEdges =null;
		int iter = 0;
		int maxIter = constraintsEdges.size();
		ArrayList<MyEdge> edgesReturn = new ArrayList<MyEdge>();
		
		if (verbose)
			System.out.println("Processing mesh intersection for " + maxIter
					+ " edges");

		// While there is still an edge to process
		ListIterator<MyEdge> iterEdge = constraintsEdges.listIterator();
		while (iterEdge.hasNext()) {//FIXME Processing edge very too slow!
			
			iter++;
			if (verbose)
				System.out.println("Processing edge " + iter + " / " + maxIter);

			// Get first edge then remove it from the list
			currentEdge = iterEdge.next();

			// Compute edge intersection with the Mesh
			MyPoint p1 = currentEdge.getStartPoint();
			MyPoint p2 = currentEdge.getEndPoint();
			// Intersection points - this is an ArrayList because we need to
			// sort it
			ArrayList<MyPoint> addedPoints = new ArrayList<MyPoint>();
			ArrayList<MyEdge> IntersectedEdges = new ArrayList<MyEdge>();
			// Edges that can participate to p1 p2
			possibleEdges = new ArrayList<MyEdge>();

			// First we get all intersection points
			// We need then because we have to compare alterPoint with this list
			// of points

			ArrayList<Object[]> result=edgesQuadTree.searchIntersection(currentEdge);
			for (int i =0;i<result.size();i++ ) {
				MyEdge anEdge = (MyEdge) result.get(i)[0];


				MyPoint p3 = anEdge.getStartPoint();
				MyPoint p4 = anEdge.getEndPoint();

				// possible intersection
				MyPoint IntersectionPoint1 = null;
				MyPoint IntersectionPoint2 = null;
				MyEdge saveEdge = anEdge;

				switch ((Integer)result.get(i)[1]) {
				case 3:
					// There is an intersection point
					IntersectionPoint1 = anEdge.getIntersection(p1, p2, (p1.isZUse()|| p2.isZUse()));
					possibleEdges.add(anEdge);
					saveEdge = null;
					break;
				case 1:
					// There is an intersection point
					IntersectionPoint1 = anEdge.getIntersection(p1, p2, (p1.isZUse()|| p2.isZUse()));
					break;
				case 2:
					// points are on the same line and intersects

					// p1 and p2 cannot be inside the edge because they
					// participate
					// to the mesh
					// so, start and end MUST be inside p1-p2
					IntersectionPoint1 = p3;
					IntersectionPoint2 = p4;
					possibleEdges.add(anEdge);
					saveEdge = null;
					break;
				}
				if (IntersectionPoint1 != null) {
					if (!addedPoints.contains(IntersectionPoint1)) {
						addedPoints.add(IntersectionPoint1);
						IntersectedEdges.add(saveEdge);
					}
				}
				if (IntersectionPoint2 != null) {
					if (!addedPoints.contains(IntersectionPoint2)) {
						addedPoints.add(IntersectionPoint2);
						IntersectedEdges.add(saveEdge);
					}
				}
			}
			
			// Intersect p1-p2 with all found edges
			ListIterator<MyEdge> intersect1 = IntersectedEdges.listIterator();
			ListIterator<MyPoint> intersect2 = addedPoints.listIterator();
			while (intersect1.hasNext()) {
				MyEdge anEdge = intersect1.next();
				MyPoint IntersectionPoint = intersect2.next();

				if (anEdge != null) {
					MyPoint start = anEdge.getStartPoint();
					MyPoint end = anEdge.getEndPoint();

					// if the intersection point is one of the start or end
					// points, do nothing
					if ((IntersectionPoint != start)
							&& (IntersectionPoint != end)) {
						// Get the 2 alterPoints
						MyPoint alterPoints[] = new MyPoint[2];
						for (int k = 0; k < 2; k++) {
							// Get base triangle
							MyTriangle aTriangle1 = null;
							if (k == 0)
								aTriangle1 = anEdge.getLeft();
							else
								aTriangle1 = anEdge.getRight();

							if (aTriangle1 != null)
								alterPoints[k] = aTriangle1
										.getAlterPoint(anEdge);
							else
								alterPoints[k] = null;
						}
						

						// Add point on the edge
						processAddPoint(anEdge, IntersectionPoint);

						// And define the edges that link IntersectionPoint and
						// the alterPoints as possible edges
						// NB : the 2 triangles still exists and the edge we
						// look for belongs to the triangle
						for (int k = 0; k < 2; k++) {
							MyTriangle aTriangle1 = null;
							if (k == 0)
								aTriangle1 = anEdge.getLeft();
							else
								aTriangle1 = anEdge.getRight();

							for (int l = 0; l < 2; l++) {
								if (aTriangle1 != null) {
									MyEdge possible = aTriangle1
											.getEdgeFromPoints(
													IntersectionPoint,
													alterPoints[l]);
									if (possible != null)
										possibleEdges.add(possible);
								}
							}
						}
					} else {
						possibleEdges.add(anEdge);
					}
				}
			}

			// We keep only points between p1 and p2
			ListIterator<MyPoint> iterPoint = addedPoints.listIterator();
			while (iterPoint.hasNext()) {
				MyPoint aPoint = iterPoint.next();
				if (!currentEdge.isInside(aPoint)) {
					// Not between p1 and p2 => removed
					iterPoint.remove();
				} else
					aPoint.isLocked();
			}
			
			// Then we mark all edges from p1 to p2
			int size = addedPoints.size();
			if (size > 2)
				MyTools.quickSort_Points(addedPoints);
			MyPoint LastPoint = p1;

			for (MyPoint p : addedPoints) {
				MyEdge anEdge = checkTwoPointsEdge(p, LastPoint, possibleEdges);
				if (anEdge != null) {
					anEdge.setLocked(true);
					LastPoint.setLocked(true);
					p.setLocked(true);
					anEdge.setProperty(currentEdge.getProperty());

					// look for swapping edge
					if (anEdge.getEndPoint() == p)
						anEdge.swap();
				}
				LastPoint = p;
			}
			edgesReturn.addAll(possibleEdges);
		}

		// Then apply the flip-flop algorithm
		processBadEdges();
		
		return edgesReturn;//possibleEdges;
	}

	
	/**
	 * sort points, remove same points and reset points and edges
	 */
	private ArrayList<MyPoint> sortAndSimplify(ArrayList<MyPoint> points) {
		int NbPoints = getNbPoints();
		HashMap<MyPoint, MyPoint> Replace = new HashMap<MyPoint, MyPoint>();

		// sort points
		if (NbPoints > 0)
			MyTools.quickSort_Points(points);

		// Remove same points double precision2 = precision precision;
		MyPoint current;
		boolean found = false;
		int index;
		double precision2 = precision * precision;

		for (int i = 0; i < NbPoints - 1; i++) {
			current = points.get(i);

			if (!Replace.containsKey(current)) {
				// the point is not currently replaced
				index = i + 1;
				found = false;
				while ((!found) && (index < NbPoints)) {
					MyPoint newPoint = points.get(index);
					double dist = newPoint.squareDistance_1D(current);
					if (dist > precision2)
						found = true;
					else {
						dist = newPoint.squareDistance_2D(current);
						if (dist < precision2) {
							// newPoint is just near current => replace it
							Replace.put(newPoint, current);
						}
					}
					index++;
				}
			}
		}

		// We have the replacement list - apply it in edges
		for (MyEdge anEdge : edgesQuadTree.getAll()) {
			MyPoint aPoint;
			aPoint = anEdge.getStartPoint();
			if (Replace.containsKey(aPoint)) {
				edgesQuadTree.remove(anEdge);
				anEdge.setStartPoint(Replace.get(aPoint));
				
				aPoint = anEdge.getEndPoint();
				if (Replace.containsKey(aPoint)) {
					anEdge.setEndPoint(Replace.get(aPoint));
				}
				edge_GID++;
				anEdge.setGID(edge_GID);
				edgesQuadTree.add(anEdge);
			}
			else
			{
				aPoint = anEdge.getEndPoint();
				if (Replace.containsKey(aPoint)) {
					edgesQuadTree.remove(anEdge);
					anEdge.setEndPoint(Replace.get(aPoint));
					edge_GID++;
					anEdge.setGID(edge_GID);
					edgesQuadTree.add(anEdge);
				}
			}
		}

		for (MyEdge anEdge : constraintsEdges) {
			MyPoint aPoint;
			aPoint = anEdge.getStartPoint();
			if (Replace.containsKey(aPoint)) {
				anEdge.setStartPoint(Replace.get(aPoint));
			}
			aPoint = anEdge.getEndPoint();
			if (Replace.containsKey(aPoint)) {
				anEdge.setEndPoint(Replace.get(aPoint));
			}
		}

		// Then remove points from the list
		for (MyPoint aPoint : Replace.keySet()) {
			points.remove(aPoint);
		}
		return points;
	}

	
	
	/**
	 * Insert o point to the current triangularization
	 * 
	 * @param aPoint
	 */
	private MyTriangle myInsertPoint(MyPoint aPoint) {
		return myInsertPoint(aPoint, 0);
	}
	
	/**
	 * Insert o point to the current triangularization
	 * 
	 * @param aPoint
	 * @param property Property for the new triangle.
	 */
	private MyTriangle myInsertPoint(MyPoint aPoint, int property) {
		MyTriangle foundTriangle = null;
		// We build triangles with all boundary edges for which the point is on
		// the left
		MyPoint p1, p2;
		MyEdge anEdge1, anEdge2;
		LinkedList<MyEdge> oldEdges = new LinkedList<MyEdge>();
		LinkedList<MyEdge> newEdges = new LinkedList<MyEdge>();
		for (MyEdge anEdge : boundaryEdges) {
			// as the boundary edge anEdge already exists, we check if the
			// point is on the left for the reverse order of the edge
			// So, the point must be on the right of the BoundaryEdge
			boolean test = false;
			p1 = null;
			p2 = null;
			test = anEdge.isRight(aPoint);
			if (test) {
				// We have the edge and the 2 point, in reverse order
				p2 = anEdge.getStartPoint();
				p1 = anEdge.getEndPoint();

				// triangle points order is p1, p2, aPoint
				// check if there is an edge between p2 and aPoint
				anEdge1 = MyTools.checkTwoPointsEdge(p2, aPoint, newEdges);
				if (anEdge1 == null) {
					anEdge1 = new MyEdge(p2, aPoint);
					edge_GID++;
					anEdge1.setGID(edge_GID);
					edgesQuadTree.add(anEdge1);
					newEdges.add(anEdge1);
				} else {
					// second use of the edge => remove it from the list
					newEdges.remove(anEdge1);
				}
				
				
				// check if there is an edge between aPoint and p1
				anEdge2 = MyTools.checkTwoPointsEdge(aPoint, p1, newEdges);
				

				if (anEdge2 == null) {

					anEdge2 = new MyEdge(aPoint, p1);
					edge_GID++;
					anEdge2.setGID(edge_GID);
					edgesQuadTree.add(anEdge2);
					newEdges.add(anEdge2);
					
				} else {
					// second use of the edge => remove it from the list
					newEdges.remove(anEdge2);
				}

				
				// create triangle : take care of the order : anEdge MUST be
				// first
				MyTriangle aTriangle = new MyTriangle(anEdge, anEdge1, anEdge2);
				aTriangle.setProperty(property);
				triangle_GID++;
				aTriangle.setGID(triangle_GID);
				trianglesQuadTree.add(aTriangle);

				// We say we founded a first triangle
				if (foundTriangle == null)
					foundTriangle = aTriangle;

				// Mark the edge to be removed
				oldEdges.add(anEdge);

				// add the edges to the bad edges list
				if (!isMeshComputed()) {
					if (!badEdgesQueueList.contains(anEdge))
						badEdgesQueueList.add(anEdge);
					if (!badEdgesQueueList.contains(anEdge1))
						badEdgesQueueList.add(anEdge1);
					if (!badEdgesQueueList.contains(anEdge2))
						badEdgesQueueList.add(anEdge2);
				}
			}
		}

		// remove old edges
		for (MyEdge anEdge : oldEdges)
			boundaryEdges.remove(anEdge);

		// add the newEdges to the boundary list
		for (MyEdge anEdge : newEdges)
			if ((anEdge.getLeft() == null) || (anEdge.getRight() == null))
				boundaryEdges.add(anEdge);

		
		// Process badTriangleQueueList
		processBadEdges();
		
		return foundTriangle;
	}

	private boolean swapTriangle(MyTriangle aTriangle1, MyTriangle aTriangle2,
			MyEdge anEdge, boolean forced) {
		
		
		boolean exchange = false;
		MyEdge anEdge10, anEdge11, anEdge12;
		MyEdge anEdge20, anEdge21, anEdge22;
		MyPoint p1, p2, p3, p4;

		if ((aTriangle1 != null) && (aTriangle2 != null)) {
			p1 = anEdge.getStartPoint();
			p2 = anEdge.getEndPoint();

			p3 = p4 = null;

			// Test for each triangle if the remaining point of the
			// other triangle is inside or not
			// Triangle 1 is p1, p2, p3 or p2, p1, p3
			p3 = aTriangle1.getAlterPoint(p1, p2);
			if (p3 != null)
				if (aTriangle2.inCircle(p3) == 1)
					exchange = true;

			// Triangle 2 is p2, p1, p4 or p1, p2, p4
			p4 = aTriangle2.getAlterPoint(p1, p2);
			if (p4 != null)
				if (aTriangle1.inCircle(p4) == 1)
					exchange = true;
			
			if (p3 != p4)
				if ((exchange) || (forced)) {
					// We need to exchange points of the triangles

					// rebuild the two triangles
					// Triangle 1 is p1, p2, p3 or p2, p1, p3
					// Triangle 2 is p2, p1, p4 or p1, p2, p4
					anEdge10 = anEdge;
					anEdge11 = checkTwoPointsEdge(p3, p1, aTriangle1.edges, 3);
					anEdge12 = checkTwoPointsEdge(p1, p4, aTriangle2.edges, 3);
										
					anEdge20 = anEdge;
					anEdge21 = checkTwoPointsEdge(p2, p4, aTriangle2.edges, 3);
					anEdge22 = checkTwoPointsEdge(p3, p2, aTriangle1.edges, 3);
					if ((anEdge11 == null) || (anEdge12 == null)
							|| (anEdge21 == null) || (anEdge22 == null)) {
						System.out.println("ERREUR");
					} else {
						edgesQuadTree.remove(anEdge);
						// Set points
						anEdge.setStartPoint(p3);
						anEdge.setEndPoint(p4);
						edge_GID++;
						anEdge.setGID(edge_GID);
						edgesQuadTree.add(anEdge);
						
						
						// Put it into triangles
						aTriangle1.edges[0] = anEdge10;
						aTriangle1.edges[1] = anEdge11;
						aTriangle1.edges[2] = anEdge12;

						aTriangle2.edges[0] = anEdge20;
						aTriangle2.edges[1] = anEdge21;
						aTriangle2.edges[2] = anEdge22;

						// We have to reconnect anEdge12 and anEdge22
						if (anEdge12.getLeft() == aTriangle2)
							anEdge12.setLeft(aTriangle1);
						else
							anEdge12.setRight(aTriangle1);
						if (anEdge22.getLeft() == aTriangle1)
							anEdge22.setLeft(aTriangle2);
						else
							anEdge22.setRight(aTriangle2);

						// The set right side for anEdge
						if (anEdge.isLeft(p1)) {
							anEdge.setLeft(aTriangle1);
							anEdge.setRight(aTriangle2);
						} else {
							anEdge.setLeft(aTriangle2);
							anEdge.setRight(aTriangle1);
						}

						// do not forget to recompute circles
						aTriangle1.recomputeCenter();
						aTriangle2.recomputeCenter();
					}
				}
		}
		return exchange;
	}

	/**
	 * Process the flip-flop algorithm on the list of triangles
	 */
	private void processBadEdges() {
		if (!isMeshComputed()) {
			LinkedList<MyEdge> AlreadySeen = new LinkedList<MyEdge>();
			while (!badEdgesQueueList.isEmpty()) {
				MyEdge anEdge = badEdgesQueueList.getFirst();
				badEdgesQueueList.removeFirst();

				boolean doIt = true;

				if (anEdge.isLocked())
					doIt = false;
				else if (AlreadySeen.contains(anEdge))
					doIt = false;
				
				if (doIt) {
					AlreadySeen.add(anEdge);
					// We cannot process marked edges
					// We check if the two triangles around the edge are ok
					MyTriangle aTriangle1 = anEdge.getLeft();
					MyTriangle aTriangle2 = anEdge.getRight();
					if ((aTriangle1 != null) && (aTriangle2 != null)) {

						if (swapTriangle(aTriangle1, aTriangle2, anEdge, false)) {
							// Add the triangle"s edges to the bad edges list			
							MyEdge addEdge;
							for (int j = 0; j < 3; j++) {
								addEdge = aTriangle1.edges[j];
								if ((addEdge.getLeft() != null)
										&& (addEdge.getRight() != null)) {
									if (addEdge != anEdge)
										if (!badEdgesQueueList
												.contains(addEdge))
											badEdgesQueueList.add(addEdge);
								}	
								addEdge = aTriangle2.edges[j];
								if ((addEdge.getLeft() != null)
										&& (addEdge.getRight() != null)) {
									if (addEdge != anEdge)
										if (!badEdgesQueueList
												.contains(addEdge))
											badEdgesQueueList.add(addEdge);
								}
							}
						}
					}
				}
			}
		} else
			while (!badEdgesQueueList.isEmpty())
				badEdgesQueueList.removeFirst();
	}

	/**
	 * Check mesh topology
	 */
	public void checkTopology() {
		for (MyTriangle aTestTriangle : trianglesQuadTree.getAll())
			aTestTriangle.checkTopology();
	}

	/**
	 * process a flat triangle
	 * 
	 * @param aTriangle
	 * @throws DelaunayError 
	 */
	private void changeFlatTriangle(MyTriangle aTriangle,
			LinkedList<MyPoint> addedPoints, LinkedList<MyPoint> impactPoints,
			LinkedList<Double> Factor) throws DelaunayError {
		// Save all possible (edges and triangles)
		MyEdge edgeToProcess[] = new MyEdge[3];
		MyTriangle trianglesToProcess[] = new MyTriangle[3];
		int nbElements = 0;
		for (int i = 0; i < 3; i++) {
			MyEdge anEdge = aTriangle.edges[i];
			MyTriangle alterTriangle;
			if (anEdge.getLeft() == aTriangle)
				alterTriangle = anEdge.getRight();
			else
				alterTriangle = anEdge.getLeft();

			if (!anEdge.isLocked())
				if (alterTriangle != null) {
					if (!alterTriangle.isFlatSlope()) {
						edgeToProcess[nbElements] = anEdge;
						trianglesToProcess[nbElements] = alterTriangle;
						nbElements++;
					}
				}
		}

		// Then we split all possible edges
		double aFactor = 1;
		double minLength = 0, maxLength = 0;
		for (int i = 0; i < nbElements; i++) {
			MyEdge anEdge = edgeToProcess[i];

			MyTriangle alterTriangle = trianglesToProcess[i];
			MyPoint alterPoint = alterTriangle.getAlterPoint(anEdge);
			MyPoint alterPoint0 = aTriangle.getAlterPoint(anEdge);

			// Split the edge in the middle
			MyPoint middle = anEdge.getBarycenter();

			// Get distances to each points
			double dist = middle.squareDistance_2D(alterPoint);
			if (aFactor == 1) {
				minLength = dist;
				maxLength = dist;
			}
			if (minLength > dist)
				minLength = dist;
			if (maxLength < dist)
				maxLength = dist;

			dist = middle.squareDistance_2D(anEdge.getStartPoint());
			if (minLength > dist)
				minLength = dist;
			if (maxLength < dist)
				maxLength = dist;

			dist = middle.squareDistance_2D(alterPoint0);
			if (minLength > dist)
				minLength = dist;
			if (maxLength < dist)
				maxLength = dist;

			if (maxLength > 0)
				aFactor = Math.sqrt(minLength / maxLength);
			aFactor /= 2;
		}

		for (int i = 0; i < nbElements; i++) {
			MyEdge anEdge = edgeToProcess[i];

			MyTriangle alterTriangle = trianglesToProcess[i];
			MyPoint alterPoint = alterTriangle.getAlterPoint(anEdge);

			double basicZ = anEdge.getStartPoint().getZ();
			double altZ = alterPoint.getZ();

			// Split the edge in the middle
			MyPoint middle = anEdge.getBarycenter();

			// split triangle
			LinkedList<MyTriangle> impactedTriangles = processAddPoint(anEdge,
					middle);

			// Move middle
			middle.setZ(basicZ + (altZ - basicZ) * aFactor);

			// Recompute all centers because it one point moved
			for (MyTriangle aTriangle1 : impactedTriangles) {
				aTriangle1.recomputeCenter();
			}

			addedPoints.add(middle);
			impactPoints.add(alterPoint);
			Factor.add(aFactor);

			int iter = 20;
			while ((Math.abs(middle.getZ() - basicZ) < MyTools.epsilon)
					&& (iter > 0)) {
				// too flat, change altitudes
				LinkedList<MyPoint> todo = new LinkedList<MyPoint>();
				todo.add(middle);
				while (!todo.isEmpty()) {
					MyPoint thePoint = todo.getFirst();
					todo.removeFirst();

					ListIterator<MyPoint> iter0 = addedPoints.listIterator();
					ListIterator<MyPoint> iter1 = impactPoints.listIterator();
					ListIterator<Double> iter2 = Factor.listIterator();

					double minEcart = -1;
					boolean first = true;
					while (iter0.hasNext()) {
						MyPoint nextPoint = iter0.next();
						MyPoint nextAlter = iter1.next();
						aFactor = iter2.next().doubleValue();
						if (nextPoint == thePoint) {
							double ecart = (nextAlter.getZ() - thePoint.getZ())
									* aFactor;
							if (first)
								minEcart = ecart;
							else if (ecart * minEcart <= 0)
								minEcart = 0;
							else if (Math.abs(ecart) < Math.abs(minEcart))
								minEcart = ecart;
							first = false;
							if (!todo.contains(nextAlter))
								todo.add(nextAlter);
						}
					}
					if (!first) {
						thePoint.setZ(thePoint.getZ() + minEcart);
					}
				}
				iter--;
			}
		}

		// processBadEdges() will remove edges
		processBadEdges();

	}

	/**
	 * Remove all flat triangles
	 * 
	 * @throws DelaunayError
	 */
	public void removeFlatTriangles_b() throws DelaunayError {// old
		if (!isMeshComputed())
			throw new DelaunayError(DelaunayError.DelaunayError_notGenerated);
		else {
			// Check triangles to be removed
			int maxGID = 0;
			LinkedList<MyTriangle> badTrianglesList = new LinkedList<MyTriangle>();
			LinkedList<MyTriangle> veryBadTrianglesList = new LinkedList<MyTriangle>();
			for (MyTriangle aTriangle : trianglesQuadTree.getAll()) {	
				if (aTriangle.getGid() > maxGID)
					maxGID = aTriangle.getGid();
				if (aTriangle.isFlatSlope()) {
					// Check if we can remove flatness (there might be at least
					// one non-marked edge
					boolean canRemove = false;
					int i = 0;
					while ((!canRemove) && (i < 3)) {
						MyEdge anEdge = aTriangle.edges[i];
						if (!anEdge.isLocked())
							// it must not be on the mesh edge
							if ((anEdge.getLeft() != null)
									&& (anEdge.getRight() != null))
								canRemove = true;
							else
								i++;
						else
							i++;
					}
					if (canRemove) {
						badTrianglesList.add(aTriangle);
					} else {
						veryBadTrianglesList.add(aTriangle);
					}
				}
			}

			// change flatness
			LinkedList<MyPoint> addedPoints = new LinkedList<MyPoint>();
			LinkedList<MyPoint> impactPoints = new LinkedList<MyPoint>();
			LinkedList<Double> Factor = new LinkedList<Double>();
			int nbDone = 1;
			while ((!badTrianglesList.isEmpty()) && (nbDone > 0)) {
				LinkedList<MyTriangle> todoList = new LinkedList<MyTriangle>();
				ListIterator<MyTriangle> iterTriangle = badTrianglesList
						.listIterator();
				while (iterTriangle.hasNext()) {
					MyTriangle aTriangle = iterTriangle.next();

					// Check if we can remove that triangle
					// That means all neighbor triangle is not flat
					boolean canChange = true;
					int i = 0;
					while ((canChange) && (i < 3)) {
						MyEdge anEdge = aTriangle.edges[i];
						if (!anEdge.isLocked()) {
							// That edge can be tried
							if (anEdge.getLeft() == aTriangle) {
								// check if right triangle is not flat
								if (anEdge.getRight() != null)
									if (anEdge.getRight().isFlatSlope()) {
										canChange = false;
									}
							} else {
								// check if right triangle is not flat
								if (anEdge.getLeft() != null)
									if (anEdge.getLeft().isFlatSlope()) {
										canChange = false;
									}
							}
						}

						if (canChange)
							i++;
					}
					if (canChange) {
						// We can change that triangle
						todoList.add(aTriangle);
					}
				}

				if (todoList.isEmpty()) {
					// we've got a problem : we MUST process at least one
					iterTriangle = badTrianglesList.listIterator();
					while (iterTriangle.hasNext()) {
						MyTriangle aTriangle = iterTriangle.next();

						// Check if we can remove that triangle
						// That means at least one neighbor triangle is not flat
						boolean canChange = false;
						int i = 0;
						while ((!canChange) && (i < 3)) {
							MyEdge anEdge = aTriangle.edges[i];
							if (!anEdge.isLocked()) {
								// That edge can be tried
								if (anEdge.getLeft() == aTriangle) {
									// check if right triangle is not flat
									if (anEdge.getRight() != null)
										if (!anEdge.getRight().isFlatSlope()) {
											canChange = true;
										}
								} else {
									// check if right triangle is not flat
									if (anEdge.getLeft() != null)
										if (!anEdge.getLeft().isFlatSlope()) {
											canChange = true;
										}
								}
							}

							if (!canChange)
								i++;
						}
						if (canChange) {
							// We can change that triangle
							todoList.add(aTriangle);
						}
					}
				}

				// Apply changes
				nbDone = 0;
				while (!todoList.isEmpty()) { //FIXME too long!
					MyTriangle aTriangle = todoList.getFirst();
					todoList.removeFirst();
					changeFlatTriangle(aTriangle, addedPoints, impactPoints,
							Factor);
					nbDone++;
					badTrianglesList.remove(aTriangle);
				}

				if (verbose)
					System.out.println("Remove " + nbDone
							+ " flat triangles / " + badTrianglesList.size());
			}
			if (verbose)
				if (!badTrianglesList.isEmpty())
					System.out.println("Remain : " + badTrianglesList.size()
							+ " flat triangles");

		}
	}

	
	
	/**
	 * Remove all flat triangles
	 * 
	 * @throws DelaunayError
	 */
	public void removeFlatTriangles() throws DelaunayError {//it's the new version
		if (!isMeshComputed())
			throw new DelaunayError(DelaunayError.DelaunayError_notGenerated);
		else {
			
			if(verbose)
				System.out.println("Remove flat triangles...\n\n");
			

			

			MyEdge anEdge;
			MyPoint aPoint;
			MyTriangle unknowTriangle;
			ArrayList<MyTriangle> todoTriangles= new ArrayList<MyTriangle>();
			for(MyTriangle aTriangle:trianglesQuadTree.getAll())
			{
				if(aTriangle.isFlatSlope()) 
				{
					todoTriangles.add(aTriangle);
				}
			}
			
			LinkedList<Object[]> todoPoint;
			LinkedList<MyEdge> todoEdges= new LinkedList<MyEdge>();
			double a,b,c;
			int size=todoTriangles.size(), cpt=0;
			for(MyTriangle aTriangle:todoTriangles)
			{
				System.out.print("\r"+cpt+" / "+size);
				cpt++;
				todoPoint = new LinkedList<Object[]>();
				for(int i=0; i<3;i++)// for all edges of triangle
				{
					anEdge= aTriangle.getEdge(i);
					
					if(!anEdge.isMarked(0))
					{
						if(!anEdge.isLevelEdge() && !anEdge.isUseByPolygon())
						{
							aPoint=anEdge.getBarycenter();
							
							unknowTriangle = anEdge.getLeft();
							if(aTriangle.equals(unknowTriangle))
								unknowTriangle = anEdge.getRight();
							
							if(unknowTriangle!=null && unknowTriangle.getAlterPoint(anEdge.getStartPoint(), anEdge.getEndPoint()).getZ() <anEdge.getStartPoint().getZ() )
								aPoint.setZ(aPoint.getZ()-1);//FIXME
							else
								aPoint.setZ(aPoint.getZ()+1);//FIXME
			
							todoPoint.add(new Object[]{aPoint, anEdge});

							
						}
					anEdge.setMarked(0, true);
					}
					
				}
				
				
				if(todoPoint.size()==2)
				{
					todoEdges.add(new MyEdge((MyPoint)todoPoint.getFirst()[0], (MyPoint)todoPoint.get(1)[0]));
				
					if(((MyPoint)todoPoint.getFirst()[0]).squareDistance(aTriangle.getAlterPoint(((MyEdge)todoPoint.getFirst()[1]))) < ((MyPoint)todoPoint.get(1)[0]).squareDistance(aTriangle.getAlterPoint(((MyEdge)todoPoint.get(1)[1]))))
						todoEdges.add(new MyEdge((MyPoint)todoPoint.getFirst()[0], aTriangle.getAlterPoint(((MyEdge)todoPoint.getFirst()[1]))));
					else
						todoEdges.add(new MyEdge((MyPoint)todoPoint.get(1)[0], aTriangle.getAlterPoint(((MyEdge)todoPoint.get(1)[1]))));
							
				}else if(todoPoint.size()==3)
				{
					
					a=((MyPoint)todoPoint.get(0)[0]).squareDistance(((MyPoint)todoPoint.get(1)[0]));
					b=((MyPoint)todoPoint.get(1)[0]).squareDistance(((MyPoint)todoPoint.get(2)[0]));
					c=((MyPoint)todoPoint.get(2)[0]).squareDistance(((MyPoint)todoPoint.get(0)[0]));
					
					if(a<=c && b<=c)
					{
						todoEdges.add(new MyEdge((MyPoint)todoPoint.getFirst()[0], ((MyPoint)todoPoint.get(1)[0])));
						todoEdges.add(new MyEdge((MyPoint)todoPoint.get(1)[0], ((MyPoint)todoPoint.get(2)[0])));
						todoEdges.add(new MyEdge((MyPoint)todoPoint.get(1)[0], aTriangle.getAlterPoint(((MyEdge)todoPoint.get(1)[1]))));
						
					}else if(b<=a && c<=a)
					{
						todoEdges.add(new MyEdge((MyPoint)todoPoint.get(2)[0], ((MyPoint)todoPoint.get(0)[0])));
						todoEdges.add(new MyEdge((MyPoint)todoPoint.get(1)[0], ((MyPoint)todoPoint.get(2)[0])));
						todoEdges.add(new MyEdge((MyPoint)todoPoint.get(2)[0], aTriangle.getAlterPoint(((MyEdge)todoPoint.get(2)[1]))));
						
					}else if(c<=b && a<=b)
					{
						todoEdges.add(new MyEdge((MyPoint)todoPoint.get(2)[0], ((MyPoint)todoPoint.get(0)[0])));
						todoEdges.add(new MyEdge((MyPoint)todoPoint.getFirst()[0], ((MyPoint)todoPoint.get(1)[0])));
						todoEdges.add(new MyEdge((MyPoint)todoPoint.get(0)[0], aTriangle.getAlterPoint(((MyEdge)todoPoint.get(0)[1]))));
					}
						
						
					
				}else if(todoPoint.size()==1)
				{
					todoEdges.add(new MyEdge((MyPoint)todoPoint.getFirst()[0], aTriangle.getAlterPoint(((MyEdge)todoPoint.getFirst()[1]))));
				}
				
				
			}
			System.out.println("ajout des edgess...");
			size=todoEdges.size();
			cpt=0;
			for(MyEdge e:todoEdges)// trop lent!!!!
			{
		//		TODO
				/*TODO :
				 * suppr triangle et sous triangles
				 * découpe des edges
				 * ajout des edges
				 * ajout des triangles
				*/
				
			//	System.out.print("\r"+cpt+" / "+size);
				cpt++;
//				addPoint(e.getStartPoint());
//				addPoint(e.getEndPoint());
//				addEdge(e);
				addLevelEdge(e);
			}
//			//TODO finir removeFlatTriangles
//			for(MyTriangle aTriangle:checkTriangles)
//			{
//				fip flap
//			}
			
			System.out.println(trianglesQuadTree.size());

			if(verbose)
				System.out.println("Remove flat triangles. Done.\n");
		}
	}
	
//	/**
//	 * Remove the flat triangles and their neighbors.
//	 * @param aTriangle
//	 * @return 
//	 * @throws DelaunayError 
//	 */
//	private ArrayList<MyEdge> findAndChangeFlatTriangle(MyTriangle aTriangle) throws DelaunayError
//	{
//		LinkedList<MyTriangle> flatTriangles = new LinkedList<MyTriangle>();
//		flatTriangles.add(aTriangle);
//		ListIterator<MyTriangle> flatTrianglesIt=flatTriangles.listIterator();
//
//		MyTriangle aFlatTriangle;
//		MyTriangle unknowTriangle;
//		MyEdge aEdge;
//		
//		ArrayList<MyEdge> skeletonEdges = new ArrayList<MyEdge>();
//		
//		aTriangle.setMarked(0, true);
//		while (flatTrianglesIt.hasNext()) { // process flat triangles and their neighbors.
//			aFlatTriangle = flatTrianglesIt.next();
//			
//			
//			
//			for(int i=0; i<3;i++)// for all edges of triangle
//			{
//				aEdge= aFlatTriangle.getEdge(i);
//				if(!aEdge.isLevelEdge())// if aEdge is a level edge, do not thing
//				{
//					skeletonEdges.add(aEdge);
//					
//					unknowTriangle = aEdge.getLeft();
//					if(aFlatTriangle.equals(unknowTriangle))
//						unknowTriangle = aEdge.getRight();
//					
//					if(unknowTriangle!=null && !unknowTriangle.isMarked(0))
//					{
//						aFlatTriangle.setMarked(0, true);
//						
//						if(unknowTriangle.isFlatSlope())
//						{
//							// a flat triangle
//							flatTrianglesIt.add(unknowTriangle);
//							flatTrianglesIt.previous();
//						}
////						else
////						{
////							// not a flat triangle
////							//TODO
////						}
//					}
//				}
//			}
//			
//		}
//
//
//		
//		//TODO use 
////		for(MyPoint aPoint:skeletonPoints)
////		{
////			aPoint.setZ(aPoint.getZ()+1);//FIXME
////			addPoint(aPoint);
////		
////		}
//		// for adding point of skeleton
//		
//		return skeletonEdges;
//	}
	
	
	/**
	 * Remove a triangle from the Mesh.
	 * 
	 * @param aTriangle
	 */
	public void removeTriangle(MyTriangle aTriangle) throws DelaunayError {
		if (!isMeshComputed())
			throw new DelaunayError(DelaunayError.DelaunayError_notGenerated);
		else {
			// get longest edge
			MyEdge longest = aTriangle.edges[0];
			double maxLength = longest.getStartPoint().squareDistance_2D(
					longest.getEndPoint());
			for (int i = 1; i < 3; i++) {
				double length = aTriangle.edges[i].getStartPoint()
						.squareDistance_2D(aTriangle.edges[i].getEndPoint());
				if (length > maxLength) {
					maxLength = length;
					longest = aTriangle.edges[i];
				}
			}

			// remove it
			tryFlipFlap(aTriangle, longest);
		}
	}

	
	
	/**
	 * Remove triangles who are inside the polygon from the Mesh.
	 * @param aPolygon
	 * @param refTriangle
	 * @return Points of polygon.
	 * @throws DelaunayError
	 */
	public ArrayList<MyPoint> removeTriangleInPolygon(MyPolygon aPolygon, MyTriangle refTriangle) throws DelaunayError {
		
		LinkedList<MyTriangle> triangleOfPolygon = new LinkedList<MyTriangle>();
		
		triangleOfPolygon.add(refTriangle);
		ListIterator<MyTriangle> triangleOfPolygonIt=triangleOfPolygon.listIterator();

		MyTriangle aTriangleInPolygon;
		MyTriangle unknowTriangle;
		MyEdge aEdge;
		
		ArrayList<MyPoint> pointOfPolygon = new ArrayList<MyPoint>();
		
		if(verbose)
			System.out.println("Search and remove triangles inside polygon.");
		
		// Search and remove triangles who are inside the polygon.
		while (triangleOfPolygonIt.hasNext()) {
			aTriangleInPolygon = (MyTriangle) triangleOfPolygonIt.next();
			
			aTriangleInPolygon.setMarked(0, true);
			
			for(int i=0; i<3;i++)
			{
				aEdge= aTriangleInPolygon.getEdge(i);
				unknowTriangle = aEdge.getLeft();
				
				if(unknowTriangle==null || unknowTriangle.equals(aTriangleInPolygon) || unknowTriangle.isMarked(0) )
				{
					unknowTriangle = aEdge.getRight();
					
					if(unknowTriangle!=null && !unknowTriangle.equals(aTriangleInPolygon) && !unknowTriangle.isMarked(0) )
					{
						if(aPolygon.contains(unknowTriangle.getBarycenter()))// right edge is inside the polygon
						{
							aEdge.setRight(null);
							triangleOfPolygonIt.add(unknowTriangle);
							triangleOfPolygonIt.previous();
						}
						else{ // right edge is outside the polygon 
							aEdge.setUseByPolygon(true);
							aEdge.setLeft(null);
						}
							
					}
				}
				else if( aPolygon.contains(unknowTriangle.getBarycenter()))// left edge is inside the polygon
				{
					aEdge.setLeft(null);
					triangleOfPolygonIt.add(unknowTriangle);
					triangleOfPolygonIt.previous();
				}
				else // left edge is outside the polygon 
				{
					aEdge.setUseByPolygon(true);
					aEdge.setRight(null);
					
				}

				
				if(!pointOfPolygon.contains(aEdge.getStartPoint()))
					pointOfPolygon.add(aEdge.getStartPoint());

				if(!pointOfPolygon.contains(aEdge.getEndPoint()))
					pointOfPolygon.add(aEdge.getEndPoint());

					
			}
			trianglesQuadTree.remove(aTriangleInPolygon);
		}


		edgesQuadTree.removeAllStric(aPolygon); 
		pointsQuadTree.removeAllStric(aPolygon); 	//FIXME it's make problem in wrl file

		return pointOfPolygon;
	}
	
	private void processSomePoints(ArrayList<MyPoint> somePoint, MyPolygon aPolygon){
		
		if(somePoint.size()>2) {
			
			ListIterator<MyPoint> iterPoint= somePoint.listIterator();
			if (verbose)
				System.out.println("Processing triangularization");
			
			LinkedList<MyPoint> badPointList = new LinkedList<MyPoint>();
			boundaryEdges= new LinkedList<MyEdge>();
			
			MyPoint p1, p2, p3;
			MyEdge e1, e2, e3;
			p1 = p2 = p3 = null;
	
			p1 = iterPoint.next();
			p2 = iterPoint.next();
			p3 = iterPoint.next();
			
			// The triangle's edges MUST be in the right direction
			e1 = new MyEdge(p1, p2);
			if (e1.isLeft(p3)) {
				e2 = new MyEdge(p2, p3);
				e3 = new MyEdge(p3, p1);
			} else {
				e1.setStartPoint(p2);
				e1.setEndPoint(p1);
	
				e2 = new MyEdge(p1, p3);
				e3 = new MyEdge(p3, p2);
			}


			// Then process the other points - order don't care
			boundaryEdges.add(e1);
			boundaryEdges.add(e2);
			boundaryEdges.add(e3);
			
			MyTriangle refTriangle=new MyTriangle(e1, e2, e3);
			refTriangle.setProperty(aPolygon.getProperty());
			aPolygon.setRefTriangle(refTriangle);

			triangle_GID++;
			refTriangle.setGID(triangle_GID);
			trianglesQuadTree.add(refTriangle);
			
			// flip-flop on a list of points
			boolean ended = false;
			MyPoint aPoint=null;
			MyPoint LastTestedPoint=null;
			int count = 0;

			while (! ended) {
				boolean hasGotPoint = false;
				if (! badPointList.isEmpty()) {
					aPoint = badPointList.getFirst();
					if (LastTestedPoint != aPoint) {
						badPointList.removeFirst();
						hasGotPoint = true;
					}

				}

				if (! hasGotPoint)
					if (iterPoint.hasNext()) {
						count++;
						aPoint = iterPoint.next();
					}
					else {
						ended = true;
						aPoint = null;
					}
				LastTestedPoint = aPoint;
				
				if (aPoint!= null)
				{
					aPoint.setUseByPolygon(true);
					if (myInsertPoint(aPoint, aPolygon.getProperty()) == null)
							badPointList.addFirst(aPoint);
				}

			}
		}
		else
			System.err.println("Error in processSomePoints()");
	}
	
	
	/**
	 * try to apply a flip-flap algorithm on it
	 * 
	 * @param aTriangle
	 * @param removeEdge
	 */
	private void tryFlipFlap(MyTriangle aTriangle, MyEdge removeEdge) {
		// get the two other edges
		int k = 0;
		MyEdge[] remain = new MyEdge[2];
		for (int i = 0; i < 3; i++) {
			if (aTriangle.edges[i] != removeEdge) {
				remain[k] = aTriangle.edges[i];
				k++;
			}
		}

		boolean marked = removeEdge.isLocked();
		int property = removeEdge.getProperty();

		// Use the flip-flop algorithm on the longest edge

		MyTriangle aTriangle1 = removeEdge.getLeft();
		MyTriangle aTriangle2 = removeEdge.getRight();
		if ((aTriangle1 != null) && (aTriangle2 != null)) {
			// We can't flip-flap a triangle on the border

			// Force an Flip-flop the two triangle
			// So, we keep the same number of triangle
			// but we rearrange them
			swapTriangle(aTriangle1, aTriangle2, removeEdge, true);
		} else {
			// triangle is on the border so we really remove it

			// remove references from edges
			for (int i = 0; i < 3; i++) {
				if (aTriangle.edges[i].getLeft() == aTriangle)
					aTriangle.edges[i].setLeft(null);
				if (aTriangle.edges[i].getRight() == aTriangle)
					aTriangle.edges[i].setRight(null);
			}

			// remove longest edge
			edgesQuadTree.remove(removeEdge);

			// and finally the triangle itself
			trianglesQuadTree.remove(aTriangle);
		}

		// mark the two saved edges and remove mark on longest if necessary
		if (marked) {
			remain[0].setLocked(true);
			remain[1].setLocked(true);
			removeEdge.setLocked(false);
		}

		if (property != 0) {
			removeEdge.removeProperties();
			remain[0].removeProperties();
			remain[1].removeProperties();
		}
	}

	/**
	 * Check if the current mesh triangularization is correct or not
	 * 
	 * @throws DelaunayError
	 */
	public void checkTriangularization() throws DelaunayError {
		if (!isMeshComputed())
			throw new DelaunayError(DelaunayError.DelaunayError_notGenerated);
		else {

			if(verbose)
				System.out.println("First - check if every point belongs to at least one edge");
			
			// First - check if every point belongs to at least one edge
						
			
			
			ArrayList<MyEdge> edges= edgesQuadTree.getAll();
			int cpt=0, edgesSize=edges.size();
			MyEdge anEdge;
			boolean found;
			for (MyPoint aPoint : pointsQuadTree.getAll()) {
				found = false;
				for(int i=0;i<edgesSize && !found;i++ ){
					anEdge = edges.get(i);
					if (anEdge.getStartPoint() == aPoint)
						found = true;
					else if (anEdge.getEndPoint() == aPoint)
						found = true;
				}
				if (!found)
				{
					cpt++;
					System.out.println("not connect : "+aPoint);
//					throw new DelaunayError(
//							DelaunayError.DelaunayError_nonInsertedPoint);
				}
			}
			
			if(cpt!=0 && verbose)//TODO change System.err.println to DelaunayError
			{
				System.err.println(cpt+" point"+(cpt>1?"s are":" is")+" not found in edges!");
				System.err.println("Skip check of topology and delaunay.");
			}
			else
			{
				if(verbose)
					System.out.println("Second - check topology");
				
				// Second - check topology
				for (MyTriangle aTriangle : trianglesQuadTree.getAll()) {
					if (!aTriangle.checkTopology())
						throw new DelaunayError(
								DelaunayError.DelaunayError_incorrectTopology);
				}
	
				//When we have polygons, delaunay can't be good because polygon add constraint to the mesh.
				if(polygons.isEmpty()) 
				{
					if(verbose)
						System.out.println("Third - check delaunay");
					
					// Third - check delaunay
					for (MyTriangle aTriangle : trianglesQuadTree.getAll()) {
						if (!aTriangle.checkDelaunay(pointsQuadTree.getAll()))
						{
//							throw new DelaunayError(DelaunayError.DelaunayError_incorrectTopology);
							System.out.println("Incorrect topology. It's normal if you have process some edges with constraint like level edges.");
							break;
						}
									
					}
				}
				else if(verbose)
					System.out.println("Skip check of Delaunay because mesh contains polygons.");
			}
		}
		
		if(verbose)
			System.out.println("Checking triangularization done.");
	}

	/**
	 * Check if the edge already exists returns null if it doesn't
	 * 
	 * @param p1
	 * @param p2
	 * @param EdgeList
	 * @return
	 */
	private MyEdge checkTwoPointsEdge(MyPoint p1, MyPoint p2,
			ArrayList<MyEdge> EdgeList) {
		// Check if the two points already lead to an existing edge.
		// If the edge exists it must be in the non-processed edges
		MyEdge theEdge = null;
		ListIterator<MyEdge> iter1 = EdgeList.listIterator();
		while (iter1.hasNext() && (theEdge == null)) {
			MyEdge anEdge = iter1.next();
			if (((anEdge.getStartPoint() == p1) && (anEdge.getEndPoint() == p2))
					|| ((anEdge.getStartPoint() == p2) && (anEdge.getEndPoint() == p1)))
				theEdge = anEdge;
		}
		return theEdge;
	}

	/**
	 * Check if the edge already exists. Returns null if it doesn't
	 * 
	 * @param p1
	 * @param p2
	 * @param EdgeQueueList
	 * @param size
	 * 
	 * @return
	 */
	private MyEdge checkTwoPointsEdge(MyPoint p1, MyPoint p2,
			MyEdge[] EdgeQueueList, int size) {
		// Check if the two points already lead to an existing edge.
		// If the edge exists it must be in the non-processed edges
		MyEdge theEdge = null;
		int i = 0;
		while ((i < size) && (theEdge == null)) {
			MyEdge anEdge = EdgeQueueList[i];
			if (((anEdge.getStartPoint() == p1) && (anEdge.getEndPoint() == p2))
					|| ((anEdge.getStartPoint() == p2) && (anEdge.getEndPoint() == p1)))
				theEdge = anEdge;
			else
				i++;
		}
		return theEdge;
	}

	public MyPoint findUperPoint(MyEdge edge) {

		MyPoint p1 = edge.getStartPoint();
		MyPoint p2 = edge.getEndPoint();
		if (p1.getZ() > p2.getZ()) {
			return p1;
		} else if (p1.getZ() > p2.getZ()) {
			return p2;
		} else {
			return p1;
		}

	}

	// ----------------------------------------------------------------
//	/**
//	 * Get point max GID
//	 * 
//	 * @return
//	 */
//	public int getMaxGID_Points() {
//		return point_GID;
//	}

//	/**
//	 * Get edges max GID
//	 * 
//	 * @return
//	 */
//	public int getMaxGID_Edges() {
//		return edge_GID;
//	}

//	/**
//	 * Get triangles max GID
//	 * 
//	 * @return
//	 */
//	public int getMaxGID_Triangles() {
//		return triangle_GID;
//	}

	// ----------------------------------------------------------------
	/**
	 * Draw Mesh in the JPanel : triangles and edges. If duration is positive,
	 * also display it Must be used only when using package drawing
	 * 
	 * @param g
	 */
	protected void displayObject(Graphics g) {
		getBoundingBox();
		double scaleX, scaleY;
		double minX, minY;
		int XSize = 1200;
		int YSize = 600;
		int decalageX = 10;
		int decalageY = YSize + 30;
		int legende = YSize + 60;
		int bordure = 10;

		scaleX = XSize / (theBox.maxx - theBox.minx);
		scaleY = YSize / (theBox.maxy - theBox.miny);
		if (scaleX > scaleY)
			scaleX = scaleY;
		else
			scaleY = scaleX;
		minX = theBox.minx;
		// minY = theBox.maxy;// coordinate 0 in Y is at top of screen (don't
		// forget make change in sub method)
		minY = theBox.miny;// coordinate 0 in Y is at bottom of screen
		scaleY = -scaleY;

		g.setColor(Color.white);
		g.fillRect(decalageX - bordure, decalageY - YSize - bordure, 2
				* bordure + XSize, 2 * bordure + YSize);
		g.fillRect(decalageX - bordure, legende - bordure, 2 * bordure + XSize,
				2 * bordure + 50);

		g.setColor(Color.black);
		g.drawString(trianglesQuadTree.size() + " Triangles - " + edgesQuadTree.size()
				+ " Edges - " + pointsQuadTree.size() + " Points", decalageX,
				legende + 10);
		if (duration > 0) {
			g.drawString("Computation time : " + duration + " ms", decalageX,
					legende + 25);
		}
		
		// Draw triangles
		if (!trianglesQuadTree.isEmpty()) {
			for (MyTriangle aTriangle : trianglesQuadTree.getAll()) {
				aTriangle.displayObject(g, decalageX, decalageY, minX, minY,
						scaleX, scaleY);
			}

			if (displayCircles)
				for (MyTriangle aTriangle : trianglesQuadTree.getAll()) {
					aTriangle.displayObjectCircles(g, decalageX, decalageY);
				}
		}

		// Draw lines
		if (!constraintsEdges.isEmpty())
			for (MyEdge aVertex : constraintsEdges) {
				aVertex.displayObject(g, decalageX, decalageY, minX, minY,
						scaleX, scaleY);
			}

		if (!edgesQuadTree.isEmpty())
			for (MyEdge aVertex : edgesQuadTree.getAll()) {
				if (aVertex.isLocked()) {
					aVertex.displayObject(g, decalageX, decalageY, minX, minY,
							scaleX, scaleY);
				}
			}

		if ((pointsQuadTree.size() > 0) && (pointsQuadTree.size() < 100)) {
			for (MyPoint aPoint : pointsQuadTree.getAll()) {
				aPoint.displayObject(g, decalageX, decalageY, minX, minY,
						scaleX, scaleY);
			}
		}
	}

	/**
	 * Refresh Mesh display in the JPanel Must be used only when using package
	 * drawing
	 */
	public void refresh() {
		if (affiche != null)
			affiche.refresh();
	}

	// ----------------------------------------------------------------
	/**
	 * Save the Mesh elements in a XML file
	 */
	public void saveMeshXML() {
		Writer writer;
		try {
			writer = new FileWriter("Mesh.xml");
			writer.write("<Mesh>\n");

			// Write points
			writer.write("\t<Points>\n");
			for (MyPoint aPoint : pointsQuadTree.getAll()) {
				writer.write("\t\t<Point id=\"" + aPoint.getGid() + "\">\n");
				writer.write("\t\t\t<X>" + aPoint.getX() + "</X>\n");
				writer.write("\t\t\t<Y>" + aPoint.getY() + "</Y>\n");
				writer.write("\t\t\t<Z>" + aPoint.getZ() + "</Z>\n");
				if (aPoint.getProperty() == 0)
					writer.write("\t\t\t<Type />\n");
				else
					writer.write("\t\t\t<Type>" + aPoint.getProperty()
							+ "</Type>\n");
				writer.write("\t\t</Point>\n");
			}
			writer.write("\t</Points>\n");
			writer.flush();

			// Write edges
			writer.write("\t<Edges>\n");
			for (MyEdge anEdge : edgesQuadTree.getAll()) {
				writer.write("\t\t<Segment id=\"" + anEdge.getGid() + "\">\n");
				writer.write("\t\t\t<Start>" + anEdge.getStartPoint().getGid()
						+ "</Start>\n");
				writer.write("\t\t\t<End>" + anEdge.getEndPoint().getGid()
						+ "</End>\n");
				if (anEdge.getProperty() == 0)
					writer.write("\t\t\t<Type />\n");
				else
					writer.write("\t\t\t<Type>" + anEdge.getProperty()
							+ "</Type>\n");
				if (anEdge.getLeft() == null)
					writer.write("\t\t\t<Left>-1</Left>\n");
				else
					writer.write("\t\t\t<Left>" + anEdge.getLeft().getGid()
							+ "</Left>\n");
				if (anEdge.getRight() == null)
					writer.write("\t\t\t<Right>-1</Right>\n");
				else
					writer.write("\t\t\t<Right>" + anEdge.getRight().getGid()
							+ "</Right>\n");
				writer.write("\t\t</Segment>\n");
			}
			writer.write("\t</Edges>\n");
			writer.flush();

			// Write triangles
			writer.write("\t<Triangles>\n");
			for (MyTriangle aTriangle : trianglesQuadTree.getAll()) {
				writer.write("\t\t<Triangle id=\"" + aTriangle.getGid()
						+ "\">\n");
				for (int i = 0; i < 3; i++)
					writer.write("\t\t\t<Edge>" + aTriangle.edges[i].getGid()
							+ "</Edge>\n");
				writer.write("\t\t</Triangle>\n");
			}
			writer.write("\t</Triangles>\n");

			writer.write("</Mesh>\n");
			writer.close();
		} catch (IOException e) {
		}
	}

	/**
	 * Save the Mesh in a file
	 */
	public void saveMesh() {
		saveMesh("Mesh.txt");
	}

	/**
	 * Save the Mesh in a file
	 * 
	 * @param path
	 */
	public void saveMesh(String path) {
		Writer writer;
		try {
			writer = new FileWriter(path);
			for (MyPoint aPoint : pointsQuadTree.getAll()) {
				writer.write(aPoint.getX() + "\t" + aPoint.getY() + "\t"
						+ aPoint.getZ() + "\t" + aPoint.gid + "\n");
			}

			writer.write("\n");
			/*
			 * for (MyEdge anEdge : edges) {
			 * writer.write(anEdge.getStartPoint().gid + "\t" +
			 * anEdge.getEndPoint().gid + "\t" + anEdge.gid + "\n"); }
			 */
			for (MyEdge anEdge : constraintsEdges) {
				writer.write(anEdge.getStartPoint().gid + "\t"
						+ anEdge.getEndPoint().gid + "\t" + anEdge.gid + "\n");
			}

			writer.close();
		} catch (IOException e) {
		}
	}

	/**
	 * Read Mesh points from the file
	 * @throws DelaunayError 
	 */
	public void readMesh() throws DelaunayError {
		readMesh("Mesh.txt");
	}

	/**
	 * Read Mesh points from the file
	 * @throws DelaunayError 
	 */
	public void readMesh(String path) throws DelaunayError {
		Reader reader;
		try {
			String delimiteurs = "\t";
			reader = new FileReader(path);

			BufferedReader in = new BufferedReader(reader);
			String ligne = in.readLine();
			int step = 0;
			int i = 0;
			while (ligne != null) {
				StringTokenizer st = new StringTokenizer(ligne, delimiteurs);
				switch (step) {
				case 0:
					MyPoint aPoint = new MyPoint();
					i = 0;
					while (st.hasMoreTokens()) {
						String mot = st.nextToken();
						switch (i) {
						case 0:
							aPoint.setX(Double.parseDouble(mot));
							break;
						case 1:
							aPoint.setY(Double.parseDouble(mot));
							break;
						case 2:
							aPoint.setZ(Double.parseDouble(mot));
							break;
						case 3:
							aPoint.gid = Integer.parseInt(mot);
							break;
						}
						i++;
					}
					if (i >= 3)
					{
						point_GID++;
						aPoint.setGID(point_GID);
						pointsQuadTree.add(aPoint);
					}
					else
						step++;
					break;
				case 1:
					MyEdge anEdge = new MyEdge();
					int gid;
					i = 0;
					while (st.hasMoreTokens()) {
						String mot = st.nextToken();
						switch (i) {
						case 0:
							gid = Integer.parseInt(mot);
							anEdge.setStartPoint(getPointFromGID(gid));
							break;
						case 1:
							gid = Integer.parseInt(mot);
							anEdge.setEndPoint(getPointFromGID(gid));
							break;
						case 2:
							anEdge.gid = Integer.parseInt(mot);
							break;
						}
						i++;
					}
					if (i >= 1)
						constraintsEdges.add(anEdge);
					else
						step++;
					break;
				default:
					step++;
					break;
				}
				ligne = in.readLine();
			}
			in.close();
		} catch (IOException e) {
		}
	}

	/**
	 * Save Mesh
	 * 
	 * @param path
	 */
	public void saveMeshUTF(String path) {
		try {
			DataOutputStream output = new DataOutputStream(
					new FileOutputStream(path));
			int NbTriangles = getNbTriangles();

			output.writeUTF("Mesh triangles quantity:\n");
			output.writeUTF(NbTriangles + "\n");
			output.writeUTF("Triangle\tP1\tP2\tP3\n");
			int i = 0;
			for (MyTriangle aTriangle : trianglesQuadTree.getAll()) {
				i++;
				output.writeUTF(i + "\t");
				for (int j = 0; j < 3; j++) {
					MyPoint aPoint = aTriangle.getPoint(j);
					output.writeUTF(aPoint.getX() + "\t" + aPoint.getY() + "\t"
							+ aPoint.getZ());
					if (j < 2)
						output.writeUTF("\t");
					else
						output.writeUTF("\n");
				}
			}
			output.writeUTF("\n");
			output.flush();
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ----------------------------------------------------------------
	/**
	 * Export to VRML file Mesh.wrl
	 * @throws DelaunayError 
	 */
	public void VRMLexport() throws DelaunayError {
		VRMLexport("Mesh.wrl");
	}

	/**
	 * Export to VRML file
	 * 
	 * @param path
	 * @throws DelaunayError 
	 */
	public void VRMLexport(String path) throws DelaunayError {
		try {
			Writer writer = new FileWriter(path);
			processVRMLexport(writer);
			writer.close();
		} catch (IOException e) {
		}

	}

	public void processVRMLexport(Writer writer) throws DelaunayError {
		if (writer != null)
			try {
				ArrayList<MyPoint> points=pointsQuadTree.getAll();
				ArrayList<MyEdge> edges=edgesQuadTree.getAll();
				ArrayList<MyTriangle> triangles=trianglesQuadTree.getAll();
				
//				setAllGIDs(points);
//				setAllGIDs(triangles);
//				setAllGIDs(edges);//TODO check me use or not?
				
				
				writer.write("#VRML V2.0 utf8\n");
				writer.write("Background{\n");
				writer.write("skyColor 0 0.3 0.8\n");
				writer.write("}\n");
				writer.write("\n");
				writer.write("Transform {\n");
				writer.write("scale 1 1 1\n");
				writer.write("children [\n");
				writer.write("Shape {\n");
				writer.write("appearance Appearance {\n");
				writer.write("material Material {\n");
				writer.write("diffuseColor 1 1 .6\n");
				writer.write("} # end material\n");
				writer.write("} # end appearance\n");
				writer.write("geometry IndexedFaceSet {\n");
				writer.write("convex FALSE\n");
				writer.write("solid FALSE\n");
				writer.write("\n");
				int size = points.size();
				double zcamera = 0;
				double dx = 0, dy = 0, dz = 0;
				if (size > 3) {
					double xmin = 0, xmax = 0;
					double ymin = 0, ymax = 0;
					double zmin = 0, zmax = 0;
					double x, y, z;

					int maxGid = -1;

					x = points.get(0).getX();
					y = points.get(0).getY();
					z = points.get(0).getZ();
					xmin = x;
					xmax = xmin;
					ymin = y;
					ymax = ymin;
					zmin = z;
					zmax = zmin;
					for (MyPoint aPoint : points) {
						x = aPoint.getX();
						y = aPoint.getY();
						z = aPoint.getZ();
						if (xmax < x)
							xmax = x;
						if (xmin > x)
							xmin = x;
						if (ymax < y)
							ymax = y;
						if (ymin > y)
							ymin = y;
						if (zmax < z)
							zmax = z;
						if (zmin > z)
							zmin = z;
					}

					double distance = xmax - xmin;
					if (distance < ymax - ymin)
						distance = ymax - ymin;
					if (distance < zmax - zmin)
						distance = zmax - zmin;

					dx = (xmax + xmin) / 2;
					dy = (ymax + ymin) / 2;
					dz = zmin;
					zcamera = (zmax - zmin) + distance;

					writer.write("coord Coordinate {\n");
					writer.write("point [\n");
					writer.write("#x y z pt\n");
					for (MyPoint aPoint : points) {
						writer.write(" #Point " + (aPoint.gid - 1) + "\n");
						writer.write(" " + (aPoint.getX() - dx) + " "
								+ (aPoint.getY() - dy) + " "
								+ (aPoint.getZ() - dz) + "\n");
						if (maxGid < aPoint.gid)
							maxGid = aPoint.gid;
					}

					// add points for walls
					Hashtable<MyPoint, MyPoint> addPoint = new Hashtable<MyPoint, MyPoint>();
					for (MyEdge anEdge : edges) {
						if (anEdge.getProperty() == 1) {
							writer.write("#wall points\n");
							for (int j = 0; j < 2; j++) {
								MyPoint aPoint;
								if (j == 0)
									aPoint = anEdge.getStartPoint();
								else
									aPoint = anEdge.getEndPoint();
								if (!addPoint.containsKey(aPoint)) {
									maxGid++;
									MyPoint aPoint2 = new MyPoint(aPoint);
									aPoint2.setZ(aPoint.getZ() + 2);
									aPoint2.gid = maxGid;

									writer.write(" #Wall Point " + (maxGid - 1)
											+ "\n");
									writer.write(" " + (aPoint2.getX() - dx)
											+ " " + (aPoint2.getY() - dy) + " "
											+ (aPoint2.getZ() - dz) + "\n");
									addPoint.put(aPoint, aPoint2);
								}
							}
						}
					}
					writer.write("] # end point\n");
					writer.write("} # end coord\n");
					writer.write("\n");

					// Now generate triangles
					maxGid = -1;
					writer.write("coordIndex [\n");
					for (MyTriangle aTriangle : triangles) {
						writer.write("#triangle " + (aTriangle.gid - 1) + "\n");
						for (int i = 0; i < 3; i++)
							writer
									.write((aTriangle.getPoint(i).gid - 1)
											+ "\t");
						writer.write("-1\n");
						if (maxGid < aTriangle.gid)
							maxGid = aTriangle.gid;
					}

					// add walls
					int wallNumber = 0;
					for (MyEdge anEdge : edges) {
						if (anEdge.getProperty() == 1) {
							wallNumber++;
							writer.write("#wall " + (wallNumber - 1) + "\n");
							MyPoint p1 = anEdge.getStartPoint();
							MyPoint p2 = anEdge.getEndPoint();
							MyPoint p3 = addPoint.get(p1);
							MyPoint p4 = addPoint.get(p2);

							writer.write((p1.gid - 1) + "\t");
							writer.write((p2.gid - 1) + "\t");
							writer.write((p4.gid - 1) + "\t");
							writer.write((p3.gid - 1) + "\t");

							writer.write("-1\n");
						}
					}
					writer.write("\n");
					writer.write("] # end coordIndex\n");
					writer.write("\n");

					writer.write("# color definitions\n");
					writer.write("colorPerVertex FALSE\n");
					writer.write("color Color {\n");
					writer.write("color [\n");
					writer
							.write("#defining a palette of colors to use in the colorIndex\n");
					writer.write("0.0 1.0 0.0 # color #0 is green\n");
					writer.write("1.0 0.75 0.5 # color #1 is wall\n");
					writer.write("] # end inner color group\n");
					writer.write("} # end color node\n");
					writer.write("colorIndex [\n");
					writer.write("#color node\n");
					for (MyTriangle aTriangle : triangles) {
						writer.write("0 #triangle " + (aTriangle.gid - 1)
								+ "\n");
					}

					wallNumber = 0;
					for (MyEdge anEdge : edges) {
						if (anEdge.getProperty() == 1) {
							wallNumber++;
							writer.write("1 #wall edge " + (wallNumber - 1)
									+ "\n");
						}
					}
					writer.write("] # end colorIndex\n");
					writer.write("\n");
				}
				writer.write("} # end geometry\n");
				writer.write("} # end shape\n");
				writer.write("] # end children\n");
				writer.write("} # end transform\n");
				writer.write("\n");

				// Add sewer
				for (MyEdge anEdge : edges) {
					long type = anEdge.getProperty();
					if (type != 0) {
						double cx = 1, cy = 1, cz = 1;
						if (type == 2) {
							cy = 0;
						} else if (type == 16) {
							cx = 0;
							cy = 0;
						} else if (type == 8) {
							cz = 0;
						}
						VRMLexport_line(writer, anEdge.getStartPoint(), anEdge
								.getEndPoint(), dx, dy, dz, cx, cy, cz);
					}
				}

				writer.write("\n");
				writer.write("Viewpoint {\n");
				writer.write("description \"middle\"\n");
				writer.write("position 0 0 " + zcamera + "\n");
				writer.write("} # end viewpoint\n");
				writer.write("\n");
			} catch (IOException e) {
			}
	}

	private void VRMLexport_line(Writer writer, MyPoint start, MyPoint end,
			double dx, double dy, double dz, double cx, double cy, double cz) {
		try {

			double x, y, z;
			x = (start.getX() + end.getX()) / 2;
			y = (start.getY() + end.getY()) / 2;
			z = (start.getZ() + end.getZ()) / 2;

			double length = Math.sqrt(start.squareDistance(end));

			double ux = (end.getX() - start.getX()) / length;
			double uy = (end.getY() - start.getY()) / length;
			double uz = (end.getZ() - start.getZ()) / length;
			double vx = 0;
			double vy = 1;
			double vz = 0;

			double a = uy * vz - uz * vy;
			double b = uz * vx - ux * vz;
			double c = ux * vy - uy * vx;

			double vLen = Math.sqrt(a * a + b * b + c * c);
			if (vLen > 0) {
				a /= vLen;
				b /= vLen;
				c /= vLen;
			}
			double Angle = Math.asin(vLen);
			if (Math.abs(uy - Math.cos(Angle)) < 0.1)
				Angle = -Angle;

			writer.write("Transform {\n");
			writer.write("translation " + (x - dx) + " " + (y - dy) + " "
					+ (z - dz) + " " + "\n");
			writer.write("rotation " + a + " " + b + " " + c + " " + Angle
					+ "\n");

			writer.write("children [\n");
			writer.write("Shape {\n");
			writer.write("geometry Cylinder {\n");
			writer.write("height " + length + "\n");
			writer.write("radius .2\n");
			writer.write("top TRUE\n");
			writer.write("side TRUE\n");
			writer.write("bottom FALSE\n");
			writer.write("} # end geometry\n");
			writer.write("appearance Appearance {\n");
			writer.write(" material Material {\n");

			writer.write("diffuseColor " + cz + " " + cy + " " + cz + "\n");
			writer.write("} # end material\n");
			writer.write("} # end appearance\n");
			writer.write("} # end shape\n");
			writer.write("] # end chilren\n");
			writer.write("} # end Transform\n");
		} catch (IOException e) {
		}

	}

	// ----------------------------------------------------------------
	/**
	 * Set missing GIDs for elements
	 * 
	 * @param elements
	 */
	protected void setAllGIDs(ArrayList<? extends MyElement> elements) {
		// set a GID to every element
		int maxGID = 0;
		for (Object anObject : elements) {
			int value = ((MyElement) anObject).getGid();
			if (value > maxGID)
				maxGID = value;
		}

		for (Object anObject : elements) {
			if (((MyElement) anObject).getGid() == -1) {
				maxGID++;
				((MyElement) anObject).setGid(maxGID);
			}
		}

		// sort elements
		MyTools.quickSortGID(elements, 0, elements.size() - 1);

		// Values are orderer => give values from 1 to the end
		maxGID = 0;
		for (Object anObject : elements) {
			maxGID++;
			((MyElement) anObject).setGid(maxGID);
		}
	}
}
