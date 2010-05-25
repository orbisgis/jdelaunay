package org.jdelaunay.delaunay;

/**
 * Delaunay Package.
 *
 * @author Jean-Yves MARTIN, Erwan BOCHER, Adelin PIAU
 * @date 2009-01-12
 * @revision 2010-05-24
 * @version 2.0
 */

import java.awt.*;
import java.io.*;
import java.util.*;

import com.vividsolutions.jts.geom.GeometryFactory;

public class MyMesh {
	// Vectors with points and edges
	protected ArrayList<MyPoint> points;
	protected ArrayList<MyEdge> edges;
	protected LinkedList<MyTriangle> triangles;
	protected LinkedList<MyPolygon> polygons;
	
	protected ArrayList<MyEdge> constraintsEdges;

	// bounding box
	protected int maxx, maxy;
	private MyBox theBox;

	// GIDs
	protected int point_GID;
	protected int edge_GID;
	protected int triangle_GID;

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

	// Working index vector
	private LinkedList<MyEdge> badEdgesQueueList;
	private LinkedList<MyEdge> boundaryEdges;

	// constants
	public static final double epsilon = 0.00001;
	public static final int maxIter = 5;

	public static final int refinement_maxArea = 1;
	public static final int refinement_minAngle = 2;
	public static final int refinement_softInterpolate = 4;

	/**
	 * Create an empty Mesh. Allocate data structures
	 * 
	 */
	public MyMesh() {
		// Generate vectors
		this.points = new ArrayList<MyPoint>();
		this.edges = new ArrayList<MyEdge>();
		this.triangles = new LinkedList<MyTriangle>();
		this.constraintsEdges = new ArrayList<MyEdge>();
		this.polygons = new LinkedList<MyPolygon>();

		this.maxx = 1200;
		this.maxy = 700;
		this.theBox = new MyBox();

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

	/**
	 * Tell if delaunay has been applied
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
		this.theBox.init();

		for (MyPoint aPoint : this.points) {
			this.theBox.alterBox(aPoint);
		}
		return this.theBox;
	}

	/**
	 * Get the current number of points in the Mesh
	 * @return NbPoints
	 */
	public int getNbPoints() {
		return this.points.size();
	}

	/**
	 * Get the current number of edges in the Mesh
	 * @return NbEdges
	 */
	public int getNbEdges() {
		return this.edges.size();
	}

	/**
	 * Get the current number of triangles in the Mesh
	 * @return NbTriangles
	 */
	public int getNbTriangles() {
		return this.triangles.size();
	}

	/**
	 * Get the points structure
	 * @return points
	 */
	public ArrayList<MyPoint> getPoints() {
		return this.points;
	}

	/**
	 * Set the points as the points of the array
	 * @param point
	 */
	public void setPoints(ArrayList<MyPoint> point) {
		this.points = new ArrayList<MyPoint>();
		for (MyPoint aPoint : point)
			this.points.add(aPoint);
	}

	/**
	 * Set the points as the points of the array
	 * @param point
	 */
	public void setPoints(LinkedList<MyPoint> point) {
		this.points = new ArrayList<MyPoint>();
		for (MyPoint aPoint : point)
			this.points.add(aPoint);
	}

	/**
	 * Set the points as the array
	 * @param point
	 */
	public void setPointsRef(ArrayList<MyPoint> point) {
		this.points = point;
	}

	/**
	 * Get the edges structure
	 * @return edges
	 */
	public ArrayList<MyEdge> getEdges() {
		return edges;
	}

	/**
	 * Set the edges as the edges of the ArrayList
	 * @param edges
	 */
	public void setConstraintEdges(ArrayList<MyEdge> edges) {
		this.constraintsEdges = new ArrayList<MyEdge>();
		for (MyEdge anEdge : edges)
			this.constraintsEdges.add(anEdge);
	}

	/**
	 * Set the edges as the edges of the ArrayList
	 * @param edges
	 */
	public void setEdges(ArrayList<MyEdge> edges) {
		setConstraintEdges(edges);
	}

	/**
	 * Set the edges as the edges of the LinkedList
	 * @param edges
	 */
	public void setConstraintEdges(LinkedList<MyEdge> edges) {
		this.constraintsEdges = new ArrayList<MyEdge>();
		for (MyEdge anEdge : edges)
			this.constraintsEdges.add(anEdge);
	}

	/**
	 * Set the edges as the edges of the ArrayList
	 * @param edges
	 */
	public void setEdges(LinkedList<MyEdge> edges) {
		setConstraintEdges(edges);
	}

	/**
	 * Set the edges as the LinkedList
	 * @param edges
	 */
	public void setConstraintEdgesRef(ArrayList<MyEdge> edges) {
		this.constraintsEdges = edges;
	}

	/**
	 * Set the edges as the LinkedList
	 * @param edges
	 */
	public void setEdgesRef(ArrayList<MyEdge> edges) {
		setConstraintEdgesRef(edges);
	}

	/**
	 * Get the complementary edges structure This structure emorize the edges
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
	public LinkedList<MyTriangle> getTriangles() {
		return triangles;
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
	 * Set refinement. Refinement value can be any value of :
	 * refinement_minArea = remove triangles with a too small area
	 * refinement_maxArea = split too large triangles refinement_minAngle =
	 * remove triangle with a too small angle
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
	public void setStartPoint() {
		startComputation = Calendar.getInstance().getTime().getTime();
	}

	/**
	 * End of timer - generate duration
	 */
	public void setEndPoint() {
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
		MyPoint aPoint = null;
		ListIterator<MyPoint> iterPoint = this.points.listIterator();
		while ((aPoint == null) && (iterPoint.hasNext())) {
			MyPoint vPoint = iterPoint.next();
			if (vPoint.getGID() == gid)
				aPoint = vPoint;
		}
		return aPoint;
	}

	/**
	 * get an edge from its GID
	 * 
	 * @param gid
	 * @return aPoint
	 */
	public MyEdge getEdgeFromGID(int gid) {
		MyEdge anEdge = null;
		ListIterator<MyEdge> iterEdge = this.edges.listIterator();
		while ((anEdge == null) && (iterEdge.hasNext())) {
			MyEdge vEdge = iterEdge.next();
			if (vEdge.getGID() == gid)
				anEdge = vEdge;
		}

		iterEdge = this.constraintsEdges.listIterator();
		while ((anEdge == null) && (iterEdge.hasNext())) {
			MyEdge vEdge = iterEdge.next();
			if (vEdge.getGID() == gid)
				anEdge = vEdge;
		}
		return anEdge;
	}

	/**
	 * search for a point
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	protected MyPoint searchPoint(double x, double y, double z) {
		boolean found = false;
		MyPoint aPoint = null;
		ListIterator<MyPoint> iterPoint = this.points.listIterator();
		while ((iterPoint.hasNext()) && (!found)) {
			aPoint = iterPoint.next();
			if (aPoint.squareDistance(x, y, z) < epsilon)
				found = true;
		}

		if (!found)
			aPoint = null;

		return aPoint;
	}

	/**
	 * search for a point
	 * 
	 * @param x
	 * @param y
	 */
	protected MyPoint searchPoint(double x, double y) {
		boolean found = false;
		MyPoint aPoint = null;
		ListIterator<MyPoint> iterPoint = points.listIterator();
		while ((iterPoint.hasNext()) && (!found)) {
			aPoint = iterPoint.next();
			if (aPoint.squareDistance(x, y) < epsilon)
				found = true;
		}

		if (!found)
			aPoint = null;

		return aPoint;
	}

	/**
	 * Get point, creates it if necessary
	 * @param x
	 * @param y
	 * @param z
	 */
	public MyPoint getPoint(double x, double y, double z) {
		MyPoint aPoint = searchPoint(x, y, z);

		if (aPoint == null)
			aPoint = new MyPoint(x, y, z);

		return aPoint;
	}

	/**
	 * Get point, creates it if necessary
	 * @param x
	 * @param y
	 */
	public MyPoint getPoint(double x, double y) {
		MyPoint aPoint = searchPoint(x, y);

		if (aPoint == null)
			aPoint = new MyPoint(x, y);

		return aPoint;
	}

	/**
	 * Add the bounding box to current data
	 */
	public void addBoundingBox() {
		getBoundingBox();

		// Add bounding Box
		MyPoint aPoint1 = new MyPoint(theBox.minx, theBox.miny);
		MyPoint aPoint2 = new MyPoint(theBox.minx, theBox.maxy);
		MyPoint aPoint3 = new MyPoint(theBox.maxx, theBox.maxy);
		MyPoint aPoint4 = new MyPoint(theBox.maxx, theBox.miny);

		points.add(aPoint1);
		points.add(aPoint2);
		points.add(aPoint3);
		points.add(aPoint4);

		// Generate lines, taking into account the fact there are points withe
		// the same x and y
		MyTools.quickSort_Points(points);
		MyPoint LastPoint;

		// Do not remove points order because it is linked to the order we chose
		// for the points
		// join points 1 and 2 - same x
		LastPoint = aPoint1;
		for (MyPoint aPoint : points) {
			if (aPoint.getX() == LastPoint.getX()) {
				constraintsEdges.add(new MyEdge(LastPoint, aPoint));
				LastPoint = aPoint;
			}
		}
		constraintsEdges.add(new MyEdge(LastPoint, aPoint2));

		// join points 2 and 3 - same y
		LastPoint = aPoint2;
		for (MyPoint aPoint : points) {
			if (aPoint.getY() == LastPoint.getY()) {
				constraintsEdges.add(new MyEdge(LastPoint, aPoint));
				LastPoint = aPoint;
			}
		}
		constraintsEdges.add(new MyEdge(LastPoint, aPoint3));

		// join points 1 and 4 - same y
		LastPoint = aPoint1;
		for (MyPoint aPoint : points) {
			if (aPoint.getY() == LastPoint.getY()) {
				constraintsEdges.add(new MyEdge(LastPoint, aPoint));
				LastPoint = aPoint;
			}
		}
		constraintsEdges.add(new MyEdge(LastPoint, aPoint4));

		// join points 4 and 3 - same x
		LastPoint = aPoint4;
		for (MyPoint aPoint : points) {
			if (aPoint.getX() == LastPoint.getX()) {
				constraintsEdges.add(new MyEdge(LastPoint, aPoint));
				LastPoint = aPoint;
			}
		}
		constraintsEdges.add(new MyEdge(LastPoint, aPoint3));
	}

	/**
	 * Create a new edge in the mesh
	 * @param aPoint1
	 * @param aPoint2
	 */
	public void createEdge(MyPoint aPoint1, MyPoint aPoint2) {
		if (!points.contains(aPoint1))
			points.add(aPoint1);
		if (!points.contains(aPoint2))
			points.add(aPoint2);
		MyEdge anEdge = new MyEdge(aPoint1, aPoint2);
		constraintsEdges.add(anEdge);
	}

	/**
	 * Create a new edge in the mesh as a copy of current edge
	 * @param aPoint1
	 * @param aPoint2
	 */
	public void createEdge(MyEdge anEdge) {
		MyPoint aPoint1 = anEdge.getStartPoint();
		MyPoint aPoint2 = anEdge.getEndPoint();
		createEdge(aPoint1, aPoint2);
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
	 */
	public void setRandomPoints(int _NbPoints) {
		for (int i = 0; i < _NbPoints; i++) {
			// Generate random coordinates
			double x = Math.random() * maxx;
			double y = Math.random() * maxy;
			double z = Math.random() * (maxx + maxy) / 20.0;

			MyPoint aPoint = new MyPoint(x, y, z);
			aPoint.setGID(i);

			points.add(aPoint);
		}
	}

	/**
	 * Generate random edges Can be applied only if points are created
	 * 
	 * @param _NbEdges
	 */
	public void setRandomEdges(int _NbEdges) {
		int NbPoints = points.size() - 1;
		if (NbPoints > 1) {
			for (int i = 0; i < _NbEdges; i++) {
				int start = (int) Math.round(Math.random() * NbPoints);
				int end = (int) Math.round(Math.random() * NbPoints);
				while (end == start)
					end = (int) Math.round(Math.random() * NbPoints);
				MyEdge anEdge = new MyEdge(points.get(start), points.get(end));
				anEdge.setGID(i);
				anEdge.marked = 1;
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
				setStartPoint();
				startedLocaly = true;
			}
			// general data structures
			badEdgesQueueList = new LinkedList<MyEdge>();
			boundaryEdges = new LinkedList<MyEdge>();

			// sort points
			if (verbose)
				System.out.println("Sorting points");
			sortAndSimplify();
			
			// we build a first triangle with the 3 first points we find
			if (verbose)
				System.out.println("Processing triangularization");
			MyTriangle aTriangle;
			MyPoint p1, p2, p3;
			MyEdge e1, e2, e3;
			p1 = p2 = p3 = null;

			ListIterator<MyPoint> iterPoint = points.listIterator();
			p1 = iterPoint.next();
			while (p1.isMarked())
				p1 = iterPoint.next();

			p2 = iterPoint.next();
			while (p2.isMarked())
				p2 = iterPoint.next();

			p3 = iterPoint.next();
			while (p3.isMarked())
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
			edges.add(e1);
			edges.add(e2);
			edges.add(e3);

			aTriangle = new MyTriangle(e1, e2, e3);
			triangles.add(aTriangle);

			// Then process the other points - order don't care
			boundaryEdges.add(e1);
			boundaryEdges.add(e2);
			boundaryEdges.add(e3);

			// flip-flop on a list of points
			int count = 0;
			while (iterPoint.hasNext()) {
				count++;
				MyPoint aPoint = iterPoint.next();
				if (!aPoint.isMarked()) {
					if (myInsertPoint(aPoint) == null)
						System.out.println("Erreur");
				}
			}

			meshComputed = true;

			// Add the edges in the edges array
			if (verbose)
				System.out.println("Adding edges");
			processEdges(constraintsEdges);

			// adding GIDs
			if (verbose)
				System.out.println("set GIDs");
			setAllGids();

			// It's fine, we computed the mesh
			if (verbose) {
				System.out.println("end processing");
				System.out.println("Triangularization end phase : ");
				System.out.println("  Points : " + points.size());
				System.out.println("  Edges : " + edges.size());
				System.out.println("  Triangles : " + triangles.size());
			}
			if (startedLocaly)
				setEndPoint();
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
		edges = new ArrayList<MyEdge>();
		triangles = new LinkedList<MyTriangle>();
		meshComputed = false;

		// Restart the process
		processDelaunay();
	}

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
			if (! points.contains(aPoint))
				points.add(aPoint);
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
	 * @throws DelaunayError
	 */
	public void addPoint(MyPoint aPoint) throws DelaunayError {
		// First we check if the point is in the points list
		boolean foundPoint = points.contains(aPoint);
		if (! foundPoint) {
			// We check if it is closed to another point
			foundPoint = false;
			ListIterator<MyPoint> iterPoint = points.listIterator();
			while ((iterPoint.hasNext()) && (! foundPoint)) {
				MyPoint anOldPoint = iterPoint.next();
				if (anOldPoint.closedTo(aPoint, precision)) {
					foundPoint = true;
				}
			}
		
			if (! foundPoint) {
				// First we find inside which triangle it is
				MyTriangle foundTriangle = getTriangle(aPoint);

				// Point is not in the mesh
				if (foundTriangle != null) {
					// the point is inside the foundTriangle triangle
					points.add(aPoint);
					addPointInsideTriangle(foundTriangle, aPoint);
				} else {
					// the point is outside the mesh
					// The boundary edge list is ok
					// We insert the point in the mesh
					points.add(aPoint);
					foundTriangle = myInsertPoint(aPoint);
				}
			}
		}
	}

	/**
	 * Get the triangle in which the point is
	 * 
	 * @param aPoint
	 */
	public MyTriangle getTriangle(MyPoint aPoint) {
		MyTriangle foundTriangle = null;
		ListIterator<MyTriangle> iterTriangle = triangles.listIterator();
		while ((iterTriangle.hasNext()) && (foundTriangle == null)) {
			MyTriangle aTriangle = iterTriangle.next();
			if (aTriangle.isInside(aPoint)) {
				foundTriangle = aTriangle;
			}
		}

		return foundTriangle;
	}

	/**
	 * Add a point inside a triangle
	 * The point is supposed to be in the points list
	 * 
	 * @param aTriangle
	 * @param aPoint
	 * @throws DelaunayError
	 */
	private void addPointInsideTriangle(MyTriangle aTriangle, MyPoint aPoint) {
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
		MyTriangle aTriangle2 = new MyTriangle();
		MyTriangle aTriangle3 = aTriangle;

		triangles.add(aTriangle1);
		triangles.add(aTriangle2);

		// Create 3 new edges
		MyEdge anEdge[] = new MyEdge[3];
		anEdge[0] = new MyEdge(secondPoint, aPoint);
		anEdge[1] = new MyEdge(aPoint, firstPoint);
		anEdge[2] = new MyEdge(aPoint, alterPoint);
		for (int i = 0; i < 3; i++) {
			edges.add(anEdge[i]);
		}

		// set edges
		// Triangle 1 : firstPoint, secondPoint, aPoint
		aTriangle1.edges[0] = oldEdge[0];
		aTriangle1.edges[1] = anEdge[0];
		aTriangle1.edges[2] = anEdge[1];

		// Triangle 2 : secondPoint, aPoint, alterPoint
		if ((secondPoint == oldEdge[1].getStartPoint()) || (secondPoint == oldEdge[1].getEndPoint()))
			aTriangle2.edges[0] = oldEdge[1];
		else
			aTriangle2.edges[0] = oldEdge[2];
		aTriangle2.edges[1] = anEdge[2];
		aTriangle2.edges[2] = anEdge[0];

		// Triangle 3 : firstPoint, aPoint, alterPoint
		if ((firstPoint == oldEdge[2].getStartPoint()) || (firstPoint == oldEdge[2].getEndPoint()))
			aTriangle3.edges[0] = oldEdge[2];
		else
			aTriangle3.edges[0] = oldEdge[1];
		aTriangle3.edges[1] = anEdge[1];
		aTriangle3.edges[2] = anEdge[2];
		
		// Link outside edges to triangles
		if (aTriangle1.edges[0].left == aTriangle)
			aTriangle1.edges[0].left = aTriangle1;
		else
			aTriangle1.edges[0].right = aTriangle1;
		if (aTriangle2.edges[0].left == aTriangle)
			aTriangle2.edges[0].left = aTriangle2;
		else
			aTriangle2.edges[0].right = aTriangle2;
		if (aTriangle3.edges[0].left == aTriangle)
			aTriangle3.edges[0].left = aTriangle3;
		else
			aTriangle3.edges[0].right = aTriangle3;
		
		// Link inside edges to triangles
		
		// anEdge[0] is connected to triangles 1 and 2
		// firstPoint is not in anEdge[0]
		// Triangle with anEdge[0] and firstPoint is Triangle 1
		if (anEdge[0].isLeft(firstPoint)) {
			anEdge[0].left = aTriangle1;
			anEdge[0].right = aTriangle2;
		}
		else {
			anEdge[0].right = aTriangle1;
			anEdge[0].left = aTriangle2;
		}

		// anEdge[1] is connected to triangles 1 and 3
		// alterPoint is not in anEdge[1]
		// Triangle with anEdge[1] and alterPoint is Triangle 3
		if (anEdge[1].isLeft(alterPoint)) {
			anEdge[1].left = aTriangle3;
			anEdge[1].right = aTriangle1;
		}
		else {
			anEdge[1].right = aTriangle3;
			anEdge[1].left = aTriangle1;
		}

		// anEdge[2] is connected to triangles 3 and 2
		// firstPoint is not in anEdge[2]
		// Triangle with anEdge[2] and firstPoint is Triangle 3
		if (anEdge[2].isLeft(firstPoint)) {
			anEdge[2].left = aTriangle3;
			anEdge[2].right = aTriangle2;
		}
		else {
			anEdge[2].right = aTriangle3;
			anEdge[2].left = aTriangle2;
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
					aTriangle1 = anEdge.left;
				else
					aTriangle1 = anEdge.right;
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
					else if (alterEdgeList_end[k].left == aTriangle1)
						alterTriangleList_end[k] = alterEdgeList_end[k].right;
					else
						alterTriangleList_end[k] = alterEdgeList_end[k].left;
				}
			}

			// then split anEdge
			remainEdge = new MyEdge(anEdge);
			remainEdge.setStartPoint(aPoint);
			anEdge.setEndPoint(aPoint);

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
					if (alterEdgeList_end[k].left == triangleList[k])
						alterEdgeList_end[k].left = new_triangleList[k];
					else
						alterEdgeList_end[k].right = new_triangleList[k];
				}
			}

			// change remainEdge connections
			for (int k = 0; k < 2; k++) {
				if (remainEdge != null) {
					if (remainEdge.left == triangleList[k])
						remainEdge.left = new_triangleList[k];
					if (remainEdge.right == triangleList[k])
						remainEdge.right = new_triangleList[k];
				}
			}

			// add connection for the newEdges
			for (int k = 0; k < 2; k++) {
				if (newEdges[k] != null) {
					if (newEdges[k].isLeft(end)) {
						newEdges[k].left = new_triangleList[k];
						newEdges[k].right = triangleList[k];
					} else {
						newEdges[k].left = triangleList[k];
						newEdges[k].right = new_triangleList[k];
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
			points.add(aPoint);

			// add the 3 new edges to the list
			newEdges[2] = remainEdge;
			for (int k = 0; k < 3; k++) {
				if (newEdges[k] != null) {
					edges.add(newEdges[k]);
					if (!isMeshComputed())
						if (!badEdgesQueueList.contains(newEdges[k]))
							badEdgesQueueList.add(newEdges[k]);
				}

			}

			// add the 2 new triangle to the list
			for (int k = 0; k < 2; k++) {
				if (new_triangleList[k] != null)
					triangles.add(new_triangleList[k]);
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
		else if (!points.contains(p1))
			throw new DelaunayError(DelaunayError.DelaunayError_pointNotFound);
		else if (!points.contains(p2))
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
		else if (!points.contains(anEdge.getStartPoint()))
			throw new DelaunayError(DelaunayError.DelaunayError_pointNotFound);
		else if (!points.contains(anEdge.getEndPoint()))
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
	 * @param aPolygon
	 */
	public void addPolygon(MyPolygon aPolygon)  throws DelaunayError {

		for (MyEdge anEdge : aPolygon.getEdges()) {
			anEdge.setMarked(false);
			points.add(anEdge.startPoint);
			points.add(anEdge.endPoint);
			edges.add(anEdge);
		}

		meshComputed = false;
		processDelaunay();

		for (MyTriangle atriangle : triangles) {
				if (aPolygon.getPolygon().contains(
						new GeometryFactory().createPoint(atriangle.getBarycenter()
								.getCoordinate())))
					atriangle.setProperty(aPolygon.getProperty());
		}
	}
	
	/**
	 * Refine mesh according to the type of refinement that has been defined in
	 * the refinement variable
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
					for (MyTriangle aTriangle : triangles) {
						double area = aTriangle.computeArea();
						if (area > maxArea) {
							badTrianglesList.add(aTriangle);
							
							// Generate barycenter
							MyPoint newPoint = aTriangle.getBarycenter();
							if (softInterpolate) {
								double ZValue = aTriangle.softInterpolateZ(newPoint);
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
					for (MyTriangle aTriangle : triangles) {
						if (aTriangle.badAngle(minAngle) >= 0)
							badTrianglesList.add(aTriangle);
					}

					// Try to flip-flap
					// We do not count modifications for this : if it fails we do not want to retry
					while (!badTrianglesList.isEmpty()) {
						MyTriangle aTriangle = badTrianglesList.getFirst();
						badTrianglesList.removeFirst();

						// try to flip-flap with the longest edge
						MyEdge longest = aTriangle.edges[0];
						double maxLength = longest.getSquared2DLength();
						for (int i = 1; i < 3; i++) {
							double length = aTriangle.edges[i].getSquared2DLength();;
							if (length > maxLength) {
								maxLength = length;
								longest = aTriangle.edges[i];
							}
						}

						// try to flip-flap
						tryFlipFlap(aTriangle, longest);
						
					}
				}
			} while (nbDone != 0);
		}
	}

	/**
	 * Add edges defined at the beginning of the process
	 * 
	 * @param constraintsEdges
	 */
	private void processEdges(ArrayList<MyEdge> constraintsEdges) {
		int nbEdges = edges.size();
		if (nbEdges > 0)
			MyTools.quickSort_Edges(edges, 0, nbEdges - 1, false);

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
		processOtherEdges(remain2);
	}

	/**
	 * Mark existing edges (constraintsEdges and edges are supposed to be sorted)
	 * 
	 * @param constraintsEdges
	 * @return list of remaining edges
	 */
	private ArrayList<MyEdge> processEdges_Step0(ArrayList<MyEdge> constraintsEdges) {
		ArrayList<MyEdge> remainEdges = new ArrayList<MyEdge>();

		// While there is still an edge to process
		for (MyEdge anEdge : constraintsEdges) {
			if (anEdge.outsideMesh != 0) {
				anEdge.marked = 1;
				edges.add(anEdge);
			} else {
				// To be connected
				remainEdges.add(anEdge);
			}
		}

		return remainEdges;
	}

	/**
	 * Mark existing edges (constraintsEdges and edges are supposed to be sorted)
	 * 
	 * @param constraintsEdges
	 * @return list of remaining edges
	 */
	private ArrayList<MyEdge> processEdges_Step1(ArrayList<MyEdge> constraintsEdges) {
		ArrayList<MyEdge> remainEdges = new ArrayList<MyEdge>();

		MyEdge currentEdge = null;
		MyEdge currentEdge2 = null;
		int index = 0;
		int maxIndex = edges.size();
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

			boolean found = false;
			boolean ended = false;
			int i = index;
			while ((!found) && (!ended) && (i < maxIndex)) {
				currentEdge2 = edges.get(i);
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
				currentEdge2.marked = 1;
				currentEdge2.setProperty(currentEdge.getProperty());
			} else {
				// Not found
				remainEdges.add(currentEdge);
			}
		}
		return remainEdges;
	}

	private MyEdge lookForSwap(MyEdge testEdge, MyPoint start, MyPoint end) {
		MyEdge canLink = null;
		int i = 0;

		while ((canLink == null) && (i < 2)) {
			MyTriangle aTriangle;
			if (i == 0)
				aTriangle = testEdge.left;
			else
				aTriangle = testEdge.right;

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
					if (possibleEdge.left == aTriangle)
						alterTriangle = possibleEdge.right;
					else
						alterTriangle = possibleEdge.left;

				// Check if the last point is end
				boolean match = false;
				if (alterTriangle != null)
					if (alterTriangle.belongsTo(end))
						match = true;

				// Check if we can swap that edge
				if (match) {
					if (possibleEdge.marked == 0)
						if (possibleEdge.getIntersection(start, end) != null)
							canLink = possibleEdge;
				}
			}
			i++;
		}

		return canLink;
	}

	/**
	 * Mark existing edges (constraintsEdges and edges are supposed to be sorted)
	 * 
	 * @param constraintsEdges
	 * @return list of remaining edges
	 */
	private ArrayList<MyEdge> processEdges_Step2(ArrayList<MyEdge> constraintsEdges) {
		ArrayList<MyEdge> remainEdges = new ArrayList<MyEdge>();
		ArrayList<MyEdge> EdgesToSwap = new ArrayList<MyEdge>();

		MyEdge currentEdge = null;
		MyEdge currentEdge2 = null;
		int index = 0;
		int maxIndex = edges.size();
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

			boolean ended = false;
			int i = index;
			while ((!found) && (!ended) && (i < maxIndex)) {
				currentEdge2 = edges.get(i);
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
							EdgesToSwap.add(swapEdge);
							swapEdge.setProperty(currentEdge.getProperty());
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

		// swap edges
		for (MyEdge anEdge : EdgesToSwap) {
			if (anEdge.marked == 0)
				swapTriangle(anEdge.left, anEdge.right, anEdge, true);
			anEdge.marked = 1;
		}

		return remainEdges;
	}

	/**
	 * Mark existing edges (constraintsEdges and edges are supposed to be sorted)
	 * 
	 * @param constraintsEdges
	 * @return list of remaining edges
	 */
	private void processOtherEdges(ArrayList<MyEdge> constraintsEdges) {
		// List of triangles that are created when there is an intersection
		MyEdge CurrentEdge = null;

		int iter = 0;
		int maxIter = constraintsEdges.size();

		if (verbose)
			System.out.println("Processing mesh intersection for " + maxIter
					+ " edges");

		// While there is still an edge to process
		ListIterator<MyEdge> iterEdge = constraintsEdges.listIterator();
		while (iterEdge.hasNext()) {
			iter++;
			if (verbose)
				System.out.println("Processing edge " + iter + " / " + maxIter);

			// Get first edge then remove it from the list
			CurrentEdge = iterEdge.next();

			// Compute edge intersection with the Mesh
			MyPoint p1 = CurrentEdge.getStartPoint();
			MyPoint p2 = CurrentEdge.getEndPoint();

			// Intersection points - this is an ArrayList because we need to
			// sort it
			ArrayList<MyPoint> addedPoints = new ArrayList<MyPoint>();
			ArrayList<MyEdge> IntersectedEdges = new ArrayList<MyEdge>();
			// Edges that can participate to p1 p2
			ArrayList<MyEdge> possibleEdges = new ArrayList<MyEdge>();

			// First we get all intersection points
			// We need then because we have to compare alterPoint with this list
			// of points

			for (MyEdge anEdge : edges) {
				MyPoint p3 = anEdge.getStartPoint();
				MyPoint p4 = anEdge.getEndPoint();

				// possible intersection
				MyPoint IntersectionPoint1 = null;
				MyPoint IntersectionPoint2 = null;
				MyEdge saveEdge = anEdge;

				int testIntersection = anEdge.intersects(p1, p2);
				switch (testIntersection) {
				case 0:
					// No intersection => don't care
					break;
				case 3:
					// There is an intersection point
					IntersectionPoint1 = anEdge.getIntersection(p1, p2);
					possibleEdges.add(anEdge);
					saveEdge = null;
					break;
				case 1:
					// There is an intersection point
					IntersectionPoint1 = anEdge.getIntersection(p1, p2);
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
								aTriangle1 = anEdge.left;
							else
								aTriangle1 = anEdge.right;

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
								aTriangle1 = anEdge.left;
							else
								aTriangle1 = anEdge.right;

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
				if (!CurrentEdge.isInside(aPoint)) {
					// Not between p1 and p2 => removed
					iterPoint.remove();
				} else
					aPoint.marked = 1;
			}

			// Then we mark all edges from p1 to p2
			int size = addedPoints.size();
			if (size > 2)
				MyTools.quickSort_Points(addedPoints);
			MyPoint LastPoint = p1;
			for (MyPoint p : addedPoints) {
				MyEdge anEdge = checkTwoPointsEdge(p, LastPoint, possibleEdges);
				if (anEdge != null) {
					anEdge.marked = 1;
					LastPoint.marked = 1;
					p.marked = 1;
					anEdge.setProperty(CurrentEdge.getProperty());

					// look for swapping edge
					if (anEdge.getEndPoint() == p)
						anEdge.swap();
				}
				LastPoint = p;
			}
		}

		// Then apply the flip-flop algorithm
		processBadEdges();
	}

	/**
	 * sort points, remove same points and reset points and edges
	 */
	private void sortAndSimplify() {
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
		for (MyEdge anEdge : edges) {
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

	}

	/**
	 * Insert o point to the current triangularization
	 * 
	 * @param aPoint
	 */
	private MyTriangle myInsertPoint(MyPoint aPoint) {
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
					edges.add(anEdge1);
					newEdges.add(anEdge1);
				} else {
					// second use of the edge => remove it from the list
					newEdges.remove(anEdge1);
				}

				// check if there is an edge between aPoint and p1
				anEdge2 = MyTools.checkTwoPointsEdge(aPoint, p1, newEdges);
				if (anEdge2 == null) {
					anEdge2 = new MyEdge(aPoint, p1);
					edges.add(anEdge2);
					newEdges.add(anEdge2);
				} else {
					// second use of the edge => remove it from the list
					newEdges.remove(anEdge2);
				}

				// create triangle : take care of the order : anEdge MUST be
				// first
				MyTriangle aTriangle = new MyTriangle(anEdge, anEdge1, anEdge2);
				triangles.add(aTriangle);

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
			if ((anEdge.left == null) || (anEdge.right == null))
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
						// Set points
						anEdge.setStartPoint(p3);
						anEdge.setEndPoint(p4);

						// Put it into triangles
						aTriangle1.edges[0] = anEdge10;
						aTriangle1.edges[1] = anEdge11;
						aTriangle1.edges[2] = anEdge12;

						aTriangle2.edges[0] = anEdge20;
						aTriangle2.edges[1] = anEdge21;
						aTriangle2.edges[2] = anEdge22;

						// We have to reconnect anEdge12 and anEdge22
						if (anEdge12.left == aTriangle2)
							anEdge12.left = aTriangle1;
						else
							anEdge12.right = aTriangle1;
						if (anEdge22.left == aTriangle1)
							anEdge22.left = aTriangle2;
						else
							anEdge22.right = aTriangle2;

						// The set right side for anEdge
						if (anEdge.isLeft(p1)) {
							anEdge.left = aTriangle1;
							anEdge.right = aTriangle2;
						} else {
							anEdge.left = aTriangle2;
							anEdge.right = aTriangle1;
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

				if (anEdge.marked == 1)
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
								if ((addEdge.left != null)
										&& (addEdge.right != null)) {
									if (addEdge != anEdge)
										if (!badEdgesQueueList
												.contains(addEdge))
											badEdgesQueueList.add(addEdge);
								}

								addEdge = aTriangle2.edges[j];
								if ((addEdge.left != null)
										&& (addEdge.right != null)) {
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
		for (MyTriangle aTestTriangle : triangles)
			aTestTriangle.checkTopology();
	}

	/**
	 * process a flat triangle
	 * 
	 * @param aTriangle
	 */
	private void changeFlatTriangle(MyTriangle aTriangle,
			LinkedList<MyPoint> addedPoints, LinkedList<MyPoint> impactPoints,
			LinkedList<Double> Factor) {
		// Save all possible (edges and triangles)
		MyEdge edgeToProcess[] = new MyEdge[3];
		MyTriangle trianglesToProcess[] = new MyTriangle[3];
		int nbElements = 0;
		for (int i = 0; i < 3; i++) {
			MyEdge anEdge = aTriangle.edges[i];
			MyTriangle alterTriangle;
			if (anEdge.left == aTriangle)
				alterTriangle = anEdge.right;
			else
				alterTriangle = anEdge.left;

			if (anEdge.marked == 0)
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
	public void removeFlatTriangles() throws DelaunayError {
		if (!isMeshComputed())
			throw new DelaunayError(DelaunayError.DelaunayError_notGenerated);
		else {
			// Check triangles to be removed
			int maxGID = 0;
			LinkedList<MyTriangle> badTrianglesList = new LinkedList<MyTriangle>();
			LinkedList<MyTriangle> veryBadTrianglesList = new LinkedList<MyTriangle>();
			for (MyTriangle aTriangle : triangles) {
				if (aTriangle.getGid() > maxGID)
					maxGID = aTriangle.getGid();
				if (aTriangle.isFlatSlope()) {
					// Check if we can remove flatness (there might be at least
					// one non-marked edge
					boolean canRemove = false;
					int i = 0;
					while ((!canRemove) && (i < 3)) {
						MyEdge anEdge = aTriangle.edges[i];
						if (anEdge.marked == 0)
							// it must not be on the mesh edge
							if ((anEdge.left != null) && (anEdge.right != null))
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
						if (anEdge.marked == 0) {
							// That edge can be tried
							if (anEdge.left == aTriangle) {
								// check if right triangle is not flat
								if (anEdge.right != null)
									if (anEdge.right.isFlatSlope()) {
										canChange = false;
									}
							} else {
								// check if right triangle is not flat
								if (anEdge.left != null)
									if (anEdge.left.isFlatSlope()) {
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
							if (anEdge.marked == 0) {
								// That edge can be tried
								if (anEdge.left == aTriangle) {
									// check if right triangle is not flat
									if (anEdge.right != null)
										if (!anEdge.right.isFlatSlope()) {
											canChange = true;
										}
								} else {
									// check if right triangle is not flat
									if (anEdge.left != null)
										if (!anEdge.left.isFlatSlope()) {
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
				while (!todoList.isEmpty()) {
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
		setAllGids();
	}

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

		boolean marked = removeEdge.isMarked();
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
				if (aTriangle.edges[i].left == aTriangle)
					aTriangle.edges[i].left = null;
				if (aTriangle.edges[i].right == aTriangle)
					aTriangle.edges[i].right = null;
			}

			// remove longest edge
			edges.remove(removeEdge);

			// and finally the triangle itself
			triangles.remove(aTriangle);
		}

		// mark the two saved edges and remove mark on longest if necessary
		if (marked) {
			remain[0].setMarked(true);
			remain[1].setMarked(true);
			removeEdge.setMarked(false);
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
			// First - check if every point belongs to at least one edge
			for (MyPoint aPoint : points) {
				boolean found = false;
				ListIterator<MyEdge> iterEdge = edges.listIterator();
				while ((iterEdge.hasNext()) && (!found)) {
					MyEdge anEdge = iterEdge.next();
					if (anEdge.getStartPoint() == aPoint)
						found = true;
					else if (anEdge.getEndPoint() == aPoint)
						found = true;
				}
				if (!found)
					throw new DelaunayError(
							DelaunayError.DelaunayError_nonInsertedPoint);
			}

			// Second - check topology
			for (MyTriangle aTriangle : triangles) {
				if (!aTriangle.checkTopology())
					throw new DelaunayError(
							DelaunayError.DelaunayError_incorrectTopology);
			}

			// Third - check delaunay
			for (MyTriangle aTriangle : triangles) {
				if (!aTriangle.checkDelaunay(points))
					throw new DelaunayError(
							DelaunayError.DelaunayError_incorrectTopology);
			}
		}
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
	/**
	 * Get point max GID
	 * 
	 * @return
	 */
	public int getMaxGID_Points() {
		SetAllGIDs(points);
		point_GID = points.size();
		return point_GID;
	}

	/**
	 * Get edges max GID
	 * 
	 * @return
	 */
	public int getMaxGID_Edges() {
		SetAllGIDs(edges);
		edge_GID = edges.size();
		return edge_GID;
	}

	/**
	 * Get triangles max GID
	 * 
	 * @return
	 */
	public int getMaxGID_Triangles() {
		SetAllGIDs_Triangle();
		triangle_GID = triangles.size();
		return triangle_GID;
	}

	// ----------------------------------------------------------------
	/**
	 * Draw Mesh in the JPanel : triangles and edges. If duration is positive,
	 * also display it Must be used only when using package drawing
	 * 
	 * @param g
	 */
	public void displayObject(Graphics g) {
		getBoundingBox();
		double scaleX, scaleY;
		double minX, minY;

		scaleX = 1200 / (theBox.maxx - theBox.minx);
		scaleY = 600 / (theBox.maxy - theBox.miny);
		if (scaleX > scaleY)
			scaleX = scaleY;
		else
			scaleY = scaleX;
		minX = theBox.minx;
//		minY = theBox.maxy;// coordinate 0 in Y is at top of screen (don't forget make change in sub method)
		minY = theBox.miny;// coordinate 0 in Y is at bottom of screen
		int decalageX = 10;
		int decalageY = 630;

		g.setColor(Color.white);
		g.fillRect(decalageX - 5, 640, decalageX - 5 + 1200, 100);

		g.setColor(Color.black);
		g.drawString(triangles.size() + " Triangles - " + edges.size()
				+ " Edges - " + points.size() + " Points", decalageX,
				30 + decalageY);
		if (duration > 0) {
			g.drawString("Computation time : " + duration + " ms", decalageX,
					45 + decalageY);
		}

		g.setColor(Color.white);
		g.fillRect(decalageX - 5, 30 - 5, decalageX - 5 + 1200, 30 - 5 + 600);

		// Draw triangles
		if (!triangles.isEmpty()) {
			for (MyTriangle aTriangle : triangles) {
				aTriangle.displayObject(g, decalageX, decalageY, minX, minY,
						scaleX, scaleY);
			}

			if (displayCircles)
				for (MyTriangle aTriangle : triangles) {
					aTriangle.displayObjectCircles(g, decalageX, decalageY);
				}
		}

		// Draw lines
		if (!constraintsEdges.isEmpty())
			for (MyEdge aVertex : constraintsEdges) {
				aVertex.displayObject(g, decalageX, decalageY, minX, minY,
						scaleX, scaleY);
			}

		if (!edges.isEmpty())
			for (MyEdge aVertex : edges) {
				if (aVertex.marked > 0) {
					aVertex.displayObject(g, decalageX, decalageY, minX, minY,
							scaleX, scaleY);
				}
			}

		int psize = points.size();
		if (psize > 0) {
			for (MyPoint aPoint : points) {
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
			for (MyPoint aPoint : points) {
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
			for (MyEdge anEdge : edges) {
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
				if (anEdge.left == null)
					writer.write("\t\t\t<Left>-1</Left>\n");
				else
					writer.write("\t\t\t<Left>" + anEdge.left.getGid()
							+ "</Left>\n");
				if (anEdge.right == null)
					writer.write("\t\t\t<Right>-1</Right>\n");
				else
					writer.write("\t\t\t<Right>" + anEdge.right.getGid()
							+ "</Right>\n");
				writer.write("\t\t</Segment>\n");
			}
			writer.write("\t</Edges>\n");
			writer.flush();

			// Write triangles
			writer.write("\t<Triangles>\n");
			for (MyTriangle aTriangle : triangles) {
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
			for (MyPoint aPoint : points) {
				writer.write(aPoint.getX() + "\t" + aPoint.getY() + "\t"
						+ aPoint.getZ() + "\t" + aPoint.gid + "\n");
			}

			writer.write("\n");
			/*
			 * for (MyEdge anEdge : edges) { writer.write(anEdge.getStartPoint().gid
			 * + "\t" + anEdge.getEndPoint().gid + "\t" + anEdge.gid + "\n"); }
			 */
			 for (MyEdge anEdge : constraintsEdges) {
				writer.write(anEdge.getStartPoint().gid + "\t" + anEdge.getEndPoint().gid
						+ "\t" + anEdge.gid + "\n");
			}

			writer.close();
		} catch (IOException e) {
		}
	}

	/**
	 * Read Mesh points from the file
	 */
	public void readMesh() {
		readMesh("Mesh.txt");
	}

	/**
	 * Read Mesh points from the file
	 */
	public void readMesh(String path) {
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
						points.add(aPoint);
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
			for (MyTriangle aTriangle : triangles) {
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
	 */
	public void VRMLexport() {
		VRMLexport("Mesh.wrl");
	}

	/**
	 * Export to VRML file
	 * 
	 * @param path
	 */
	public void VRMLexport(String path) {
		this.setAllGids();
		try {
			Writer writer = new FileWriter(path);
			processVRMLexport(writer);
			writer.close();
		} catch (IOException e) {
		}

	}

	public void processVRMLexport(Writer writer) {
		if (writer != null)
			try {
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
	protected void SetAllGIDs(ArrayList elements) {
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

	/**
	 * Set GIDs for triangles -- specific because triangle is not an ArrayList
	 */
	protected void SetAllGIDs_Triangle() {
		int maxGID = 0;
		for (MyTriangle aTriangle : triangles) {
			maxGID++;
			aTriangle.setGid(maxGID);
		}
	}

	/**
	 * Set missing GIDs for points, edges and triangles
	 */
	public void setAllGids() {
		// Process points
		SetAllGIDs(points);
		point_GID = points.size();

		// Process edges
		SetAllGIDs(edges);
		edge_GID = edges.size();

		// Process triangles
		SetAllGIDs_Triangle();
		triangle_GID = triangles.size();
	}
}
