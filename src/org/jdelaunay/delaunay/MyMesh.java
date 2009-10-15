package org.jdelaunay.delaunay;

/**
 * Delaunay Package.
 *
 * @author Jean-Yves MARTIN
 * @date 2009-01-12
 * @version 1.0
 */

import java.awt.*;
import java.io.*;
import java.util.*;
import com.vividsolutions.jts.geom.Coordinate;

public class MyMesh {
	// Vectors with points and edges
	protected ArrayList<MyPoint> points;
	protected ArrayList<MyEdge> edges;
	protected LinkedList<MyTriangle> triangles;
	protected ArrayList<MyEdge> compEdges;

	// bounding box
	protected int maxx, maxy;
	private MyBox theBox;

	// Display elements
	private MyDrawing affiche;
	private long duration;
	private long startComputation;
	private boolean displayCircles;
	private boolean meshComputed;
	protected Delaunay DelaunayReference;
	private LinkedList<MyPoint> listEntry;
	private LinkedList<MyPoint> listExit;
	private LinkedList<MyPoint> listIntermediate;
	private LinkedList<MyEdge> listEdges;
	private LinkedList<MyPoint> listPrepare;
	private String listDefinition;
	private boolean connectToSurface;

	// GIDs
	protected int point_GID;
	protected int edge_GID;
	protected int triangle_GID;

	protected static final double epsilon = 0.00001;

	/**
	 * Create an empty Mesh. Allocate data structures
	 *
	 */
	public MyMesh() {
		DelaunayReference = null;

		// Generate vectors
		points = new ArrayList<MyPoint>();
		edges = new ArrayList<MyEdge>();
		triangles = new LinkedList<MyTriangle>();
		compEdges = new ArrayList<MyEdge>();

		maxx = 1200;
		maxy = 700;

		duration = 0;
		theBox = new MyBox();

		affiche = null;
		displayCircles = false;
		meshComputed = false;

		point_GID = 0;
		edge_GID = 0;
		triangle_GID = 0;

		listEntry = new LinkedList<MyPoint>();
		listExit = new LinkedList<MyPoint>();
		listIntermediate = new LinkedList<MyPoint>();
		listEdges = new LinkedList<MyEdge>();
		listPrepare = new LinkedList<MyPoint>();
		listDefinition = null;
		connectToSurface = true;
	}

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
			double z = Math.random() * 1000.0;

			points.add(new MyPoint(x, y, z, i));
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
				MyEdge anEdge = new MyEdge(points.get(start), points.get(end),
						i);
				anEdge.marked = 1;
				compEdges.add(anEdge);
			}
		}
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
	 * Get Duration
	 *
	 * @return
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * @return
	 */
	public boolean isMeshComputed() {
		return meshComputed;
	}

	/**
	 * @param meshComputed
	 */
	public void setMeshComputed(boolean meshComputed) {
		this.meshComputed = meshComputed;
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
		theBox.init();

		for (MyPoint aPoint : points) {
			theBox.alterBox(aPoint);
		}
		return theBox;
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
			if (aPoint.x == LastPoint.x) {
				compEdges.add(new MyEdge(LastPoint, aPoint));
				LastPoint = aPoint;
			}
		}
		compEdges.add(new MyEdge(LastPoint, aPoint2));

		// join points 2 and 3 - same y
		LastPoint = aPoint2;
		for (MyPoint aPoint : points) {
			if (aPoint.y == LastPoint.y) {
				compEdges.add(new MyEdge(LastPoint, aPoint));
				LastPoint = aPoint;
			}
		}
		compEdges.add(new MyEdge(LastPoint, aPoint3));

		// join points 1 and 4 - same y
		LastPoint = aPoint1;
		for (MyPoint aPoint : points) {
			if (aPoint.y == LastPoint.y) {
				compEdges.add(new MyEdge(LastPoint, aPoint));
				LastPoint = aPoint;
			}
		}
		compEdges.add(new MyEdge(LastPoint, aPoint4));

		// join points 4 and 3 - same x
		LastPoint = aPoint4;
		for (MyPoint aPoint : points) {
			if (aPoint.x == LastPoint.x) {
				compEdges.add(new MyEdge(LastPoint, aPoint));
				LastPoint = aPoint;
			}
		}
		compEdges.add(new MyEdge(LastPoint, aPoint3));
	}

	/**
	 * Get the current number of points in the Mesh
	 *
	 * @return NbPoints
	 */
	public int getNbPoints() {
		return points.size();
	}

	/**
	 * Get the current number of edges in the Mesh
	 *
	 * @return NbEdges
	 */
	public int getNbEdges() {
		return edges.size();
	}

	/**
	 * Add an edge to the mesh
	 */
	public void addEdge(MyPoint aPoint1, MyPoint aPoint2) {
		if (!points.contains(aPoint1))
			points.add(aPoint1);
		if (!points.contains(aPoint2))
			points.add(aPoint2);
		MyEdge anEdge = new MyEdge(aPoint1, aPoint2);
		compEdges.add(anEdge);
	}

	/**
	 * Add an edge to the mesh
	 */
	public void addEdge(MyEdge anEdge) {
		MyPoint aPoint1 = anEdge.getStart();
		MyPoint aPoint2 = anEdge.getEnd();
		addEdge(aPoint1, aPoint2);
	}

	/**
	 * Get the current number of triangles in the Mesh
	 *
	 * @return NbTriangles
	 */
	public int getNbTriangles() {
		return triangles.size();
	}

	/**
	 * Get the points structure
	 *
	 * @return points
	 */
	public ArrayList<MyPoint> getPoints() {
		return points;
	}

	/**
	 * Set the points as the points of the array
	 *
	 * @param _point
	 */
	public void setPoints(ArrayList<MyPoint> _point) {
		points = new ArrayList<MyPoint>();
		for (MyPoint aPoint : _point)
			points.add(aPoint);
	}

	/**
	 * Set the points as the array
	 *
	 * @param _point
	 */
	public void setPointsRef(ArrayList<MyPoint> _point) {
		points = _point;
	}

	/**
	 * Get the edges structure
	 *
	 * @return edges
	 */
	public ArrayList<MyEdge> getEdges() {
		return edges;
	}

	/**
	 * Set the edges as the edges of the ArrayList
	 *
	 * @param _edges
	 */
	public void setEdges(ArrayList<MyEdge> _edges) {
		compEdges = new ArrayList<MyEdge>();
		for (MyEdge anEdge : _edges)
			compEdges.add(anEdge);
	}

	/**
	 * Set the edges as the edges of the LinkedList
	 *
	 * @param _edges
	 */
	public void setEdges(LinkedList<MyEdge> _edges) {
		compEdges = new ArrayList<MyEdge>();
		for (MyEdge anEdge : _edges)
			compEdges.add(anEdge);
	}

	/**
	 * Set the edges as the LinkedList
	 *
	 * @param _edges
	 */
	public void setEdgesRef(ArrayList<MyEdge> _edges) {
		compEdges = _edges;
	}

	/**
	 * Get the complementary edges structure This structure emorize the edges
	 * that have to be added to the triangularization
	 *
	 * @return edges
	 */
	public ArrayList<MyEdge> getCompEdges() {
		return compEdges;
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
	 * get a point from its GID
	 *
	 * @param gid
	 * @return aPoint
	 */
	public MyPoint getPointFromGID(int gid) {
		MyPoint aPoint = null;
		ListIterator<MyPoint> iterPoint = points.listIterator();
		while ((aPoint == null) && (iterPoint.hasNext())) {
			MyPoint vPoint = iterPoint.next();
			if (vPoint.gid == gid)
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
		ListIterator<MyEdge> iterEdge = edges.listIterator();
		while ((anEdge == null) && (iterEdge.hasNext())) {
			MyEdge vEdge = iterEdge.next();
			if (vEdge.gid == gid)
				anEdge = vEdge;
		}

		iterEdge = compEdges.listIterator();
		while ((anEdge == null) && (iterEdge.hasNext())) {
			MyEdge vEdge = iterEdge.next();
			if (vEdge.gid == gid)
				anEdge = vEdge;
		}
		return anEdge;
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

	// ----------------------------------------------------------------
	/**
	 * Defines a new branch type
	 *
	 * @param branchType
	 * @param connectToSurface
	 * @throws DelaunayError
	 */
	private void branchStart(String branchType, boolean connectToSurface)
			throws DelaunayError {
		this.listEntry = new LinkedList<MyPoint>();
		this.listExit = new LinkedList<MyPoint>();
		this.listIntermediate = new LinkedList<MyPoint>();
		this.listEdges = new LinkedList<MyEdge>();
		this.listDefinition = branchType;
		this.connectToSurface = connectToSurface;
	}

	/**
	 * Defines a new branch type on the surface
	 *
	 * @param branchType
	 * @throws DelaunayError
	 */
	private void branchStart(String branchType) throws DelaunayError {
		branchStart(branchType, true);
	}

	/**
	 * defines a new branch
	 *
	 * @param theList
	 * @throws DelaunayError
	 */
	private void setNewBranch(LinkedList theList) throws DelaunayError {
		MyPoint lastPoint = null;
		int count = theList.size();
		MyPoint aPoint = null;
		Coordinate aCoordinate = null;
		ListIterator iterList = theList.listIterator();
		while (iterList.hasNext()) {
			Object item = iterList.next();
			if (item instanceof MyPoint) {
				aPoint = (MyPoint) item;
			} else if (item instanceof Coordinate) {
				aCoordinate = (Coordinate) item;
				aPoint = new MyPoint(aCoordinate.x, aCoordinate.y,
						aCoordinate.z);
			} else
				aPoint = null;

			count--;
			if (aPoint != null) {
				if (lastPoint == null) {
					// First point of the list
					if (listIntermediate.contains(aPoint)) {
						// Already an intermediate point => do nothing
					} else if (listExit.contains(aPoint)) {
						// It is an exit
						// It is also an entry
						// => becomes an intermediate
						listExit.remove(aPoint);
						listIntermediate.add(aPoint);
					} else if (!listEntry.contains(aPoint)) {
						// New entry
						listEntry.add(aPoint);
					}
					// else it is in Entry
				} else {
					// Intermediate point
					if (listIntermediate.contains(aPoint)) {
						// Already an intermediate point => do nothing
					} else if (listExit.contains(aPoint)) {
						// It is an exit
						if (count > 0) {
							// and not the last point
							// => becomes an intermediate
							listExit.remove(aPoint);
							listIntermediate.add(aPoint);
						}
					} else if (listEntry.contains(aPoint)) {
						// It is an entry
						// => becomes an intermediate
						listEntry.remove(aPoint);
						listIntermediate.add(aPoint);
					} else if (count > 0) {
						// new point => add it to Intermediate
						listIntermediate.add(aPoint);
					} else {
						// new point and Last point => Exit
						listExit.add(aPoint);
					}

					// Link lastPoint to new point
					MyEdge anEdge = new MyEdge(lastPoint, aPoint,
							listDefinition);
					listEdges.add(anEdge);
				}
				// other informations
				aPoint.setPointType(listDefinition);

				lastPoint = aPoint;
			}
		}
	}

	/**
	 * Validate branch and end that branch type
	 *
	 * @throws DelaunayError
	 */
	private void branchValidate() throws DelaunayError {
		MyTriangle referenceTriangle = null;

		// add every entry point to the mesh
		for (MyPoint aPoint : listEntry) {
			if (points.contains(aPoint)) {
				// Already in the points list => do noting
			} else {
				aPoint.marked = true;
				referenceTriangle = DelaunayReference.getTriangle(aPoint);
				if (referenceTriangle != null) {
					// Connect it to the surface
					double ZValue = referenceTriangle.getSurfacePoint(aPoint);
					aPoint.z = ZValue;

					DelaunayReference.addPoint(referenceTriangle, aPoint);
				} else {
					referenceTriangle = DelaunayReference.addPoint(aPoint);

					if (referenceTriangle != null) {
						double ZValue = 0;
						for (int i = 0; i < 3; i++) {
							if (referenceTriangle.points[i] != aPoint)
								ZValue += referenceTriangle.points[i].z;
						}
						aPoint.z = ZValue / 2;
					}
				}
			}
		}

		// add every intermediate point to the point list
		// do not include them in the mesh
		for (MyPoint aPoint : listIntermediate) {
			if (points.contains(aPoint)) {
				// Already in the points list => do noting
			} else {
				points.add(aPoint);
				aPoint.marked = true;
				referenceTriangle = DelaunayReference.getTriangle(aPoint);
				if (referenceTriangle != null) {
					double ZValue = referenceTriangle.getSurfacePoint(aPoint);
					if (connectToSurface) {
						// Connect it to the surface
						aPoint.z = ZValue;

						DelaunayReference.addPoint(referenceTriangle, aPoint);
					} else {
						if (aPoint.z > ZValue)
							aPoint.z = ZValue - 1.0;
					}
				} else if (connectToSurface) {
					referenceTriangle = DelaunayReference.addPoint(aPoint);

					if (referenceTriangle != null) {
						double ZValue = 0;
						for (int i = 0; i < 3; i++) {
							if (referenceTriangle.points[i] != aPoint)
								ZValue += referenceTriangle.points[i].z;
						}
						aPoint.z = ZValue / 2;
					}
				}
			}
		}

		// add every exit point to the mesh
		for (MyPoint aPoint : listExit) {
			if (points.contains(aPoint)) {
				// Already in the points list => do noting
			} else {
				aPoint.marked = true;
				referenceTriangle = DelaunayReference.getTriangle(aPoint);
				if (referenceTriangle != null) {
					// Connect it to the surface
					double ZValue = referenceTriangle.getSurfacePoint(aPoint);
					aPoint.z = ZValue;

					DelaunayReference.addPoint(referenceTriangle, aPoint);
				} else {
					referenceTriangle = DelaunayReference.addPoint(aPoint);

					if (referenceTriangle != null) {
						double ZValue = 0;
						for (int i = 0; i < 3; i++) {
							if (referenceTriangle.points[i] != aPoint)
								ZValue += referenceTriangle.points[i].z;
						}
						aPoint.z = ZValue / 2;
					}
				}
			}
		}

		// add edges
		for (MyEdge anEdge : listEdges) {
			anEdge.marked = 1;
			if (connectToSurface)
				DelaunayReference.addEdge(anEdge);
			else {
				anEdge.outsideMesh = true;
				edges.add(anEdge);
			}
		}

		this.setAllGids();

		// Reset informations
		listEntry = new LinkedList<MyPoint>();
		listExit = new LinkedList<MyPoint>();
		listIntermediate = new LinkedList<MyPoint>();
		listEdges = new LinkedList<MyEdge>();
		listDefinition = null;
		connectToSurface = true;
	}

	// ----------------------------------------------------------------
	/**
	 * add a sewer entry
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @throws DelaunayError
	 */
	public void addSewerEntry(double x, double y) throws DelaunayError {
		// Search for the point
		MyPoint sewerPoint = getPoint(x, y);
		addSewerEntry(sewerPoint);
	}

	/**
	 * add a sewer entry
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @throws DelaunayError
	 */
	public void addSewerEntry(double x, double y, double z)
			throws DelaunayError {
		// Search for the point
		MyPoint sewerPoint = getPoint(x, y);
		addSewerEntry(sewerPoint);
	}

	/**
	 * add a sewer entry
	 *
	 * @param sewerPoint
	 * @throws DelaunayError
	 */
	public void addSewerEntry(MyPoint sewerPoint) throws DelaunayError {
		listPrepare = new LinkedList<MyPoint>();
		listPrepare.add(sewerPoint);
	}

	/**
	 * add a sewer exit
	 *
	 * @param x
	 * @param y
	 * @throws DelaunayError
	 */
	public void addSewerExit(double x, double y) throws DelaunayError {
		// Search for the point
		MyPoint sewerPoint = getPoint(x, y);
		addSewerExit(sewerPoint);
	}

	/**
	 * add a sewer exit
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @throws DelaunayError
	 */
	public void addSewerExit(double x, double y, double z) throws DelaunayError {
		// Search for the point
		MyPoint sewerPoint = getPoint(x, y, z);
		addSewerExit(sewerPoint);
	}

	/**
	 * add a sewer exit
	 *
	 * @param sewerPoint
	 * @throws DelaunayError
	 */
	public void addSewerExit(MyPoint sewerPoint) throws DelaunayError {
		listPrepare.add(sewerPoint);
		sewerSet(listPrepare);
		listPrepare = new LinkedList<MyPoint>();
	}

	/**
	 * add a sewer point (neither start or exit
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @throws DelaunayError
	 */
	public void addSewerPoint(double x, double y, double z)
			throws DelaunayError {
		// Search for the point
		MyPoint aPoint = getPoint(x, y, z);
		addSewerPoint(aPoint);
	}

	/**
	 * add a sewer point (neither start or exit
	 *
	 * @param sewerPoint
	 * @throws DelaunayError
	 */
	public void addSewerPoint(MyPoint sewerPoint) throws DelaunayError {
		listPrepare.add(sewerPoint);
	}

	/**
	 * use a sewer point to start a new branch
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @throws DelaunayError
	 */
	public void setSewerPoint(double x, double y, double z)
			throws DelaunayError {
		// Search for the point
		addSewerEntry(x, y, z);
	}

	/**
	 * use a sewer point to start a new branch
	 *
	 * @param sewerPoint
	 * @throws DelaunayError
	 */
	public void setSewerPoint(MyPoint sewerPoint) throws DelaunayError {
		addSewerEntry(sewerPoint);
	}

	// ----------------------------------------------------------------
	/**
	 * Start sewers definition
	 *
	 * @throws DelaunayError
	 */
	public void sewerStart() throws DelaunayError {
		branchStart(ConstraintType.SEWER, false);
	}

	/**
	 * define a new sewer branch
	 *
	 * @param sewerPoint
	 * @throws DelaunayError
	 */
	public void sewerSet(LinkedList<MyPoint> sewerList) throws DelaunayError {
		if (listDefinition == null)
			branchStart(ConstraintType.SEWER, false);
		else if (listDefinition != ConstraintType.SEWER) {
			branchValidate();
			branchStart(ConstraintType.SEWER, false);
		}
		setNewBranch(sewerList);
	}

	/**
	 * Validate and end sewer definition
	 *
	 * @throws DelaunayError
	 */
	public void sewerValidate() throws DelaunayError {
		if (listDefinition != null)
			branchValidate();
		listDefinition = null;
	}

	// ----------------------------------------------------------------
	/**
	 * Start ditches definition
	 *
	 * @throws DelaunayError
	 */
	public void ditchStart() throws DelaunayError {
		branchStart(ConstraintType.DITCH);
	}

	/**
	 * define a new ditch branch
	 *
	 * @param ditchList
	 * @throws DelaunayError
	 */
	public void ditchSet(LinkedList<MyPoint> ditchList) throws DelaunayError {
		if (listDefinition == null)
			branchStart(ConstraintType.DITCH);
		else if (listDefinition != ConstraintType.DITCH) {
			branchValidate();
			branchStart(ConstraintType.DITCH);
		}
		setNewBranch(ditchList);
	}

	/**
	 * Validate and end ditches definition
	 *
	 * @throws DelaunayError
	 */
	public void ditchValidate() throws DelaunayError {
		if (listDefinition != null)
			branchValidate();
		listDefinition = null;
	}

	// ----------------------------------------------------------------
	/**
	 * Start rivers definition
	 *
	 * @throws DelaunayError
	 */
	public void riverStart() throws DelaunayError {
		branchStart(ConstraintType.RIVER);
	}

	/**
	 * define a new river branch
	 *
	 * @param riverList
	 * @throws DelaunayError
	 */
	public void riverSet(LinkedList<MyPoint> riverList) throws DelaunayError {
		if (listDefinition == null)
			branchStart(ConstraintType.RIVER);
		else if (listDefinition != ConstraintType.RIVER) {
			branchValidate();
			branchStart(ConstraintType.RIVER);
		}
		setNewBranch(riverList);
	}

	/**
	 * Validate and end rivers definition
	 *
	 * @throws DelaunayError
	 */
	public void riverValidate() throws DelaunayError {
		if (listDefinition != null)
			branchValidate();
		listDefinition = null;
	}

	// ----------------------------------------------------------------
	/**
	 * Start walls definition
	 *
	 * @throws DelaunayError
	 */
	public void wallStart() throws DelaunayError {
		branchStart(ConstraintType.WALL);
	}

	/**
	 * define a new wall branch
	 *
	 * @param wallList
	 * @throws DelaunayError
	 */
	public void wallSet(LinkedList<MyPoint> wallList) throws DelaunayError {
		if (listDefinition == null)
			branchStart(ConstraintType.WALL);
		else if (listDefinition != ConstraintType.WALL) {
			branchValidate();
			branchStart(ConstraintType.WALL);
		}
		setNewBranch(wallList);
	}

	/**
	 * Validate and end walls definition
	 *
	 * @throws DelaunayError
	 */
	public void wallValidate() throws DelaunayError {
		if (listDefinition != null)
			branchValidate();
		listDefinition = null;
	}

	// ----------------------------------------------------------------

	// ----------------------------------------------------------------
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
		ListIterator<MyPoint> iterPoint = points.listIterator();
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
	 * get point, creates it if necessary
	 *
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
	 * get point, creates it if necessary
	 *
	 * @param x
	 * @param y
	 * @param z
	 */
	public MyPoint getPoint(double x, double y) {
		MyPoint aPoint = searchPoint(x, y);

		if (aPoint == null)
			aPoint = new MyPoint(x, y);

		return aPoint;
	}

	// ----------------------------------------------------------------
	/**
	 * Get point max GID
	 *
	 * @return
	 */
	public int getMaxGID_Points() {
		SetAllGIDs_Point();

		int maxGID = 0;
		for (MyPoint aPoint : points) {
			int theGID = aPoint.getGid();
			if (theGID > maxGID)
				maxGID = theGID;
		}

		point_GID = maxGID;
		return maxGID;
	}

	/**
	 * Get edges max GID
	 *
	 * @return
	 */
	public int getMaxGID_Edges() {
		SetAllGIDs_Edges();

		int maxGID = 0;
		for (MyEdge anEdge : edges) {
			int theGID = anEdge.getGid();
			if (theGID > maxGID)
				maxGID = theGID;
		}

		edge_GID = maxGID;
		return maxGID;
	}

	/**
	 * Get triangles max GID
	 *
	 * @return
	 */
	public int getMaxGID_Triangles() {
		SetAllGIDs_Edges();

		int maxGID = 0;
		for (MyTriangle aTriangle : triangles) {
			int theGID = aTriangle.getGid();
			if (theGID > maxGID)
				maxGID = theGID;
		}

		triangle_GID = maxGID;
		return maxGID;
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
		minY = theBox.maxy;
		int decalageX = 10;
		int decalageY = 630;

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
				aTriangle.setColor(g);
				aTriangle.displayObject(g, decalageX, decalageY, minX, minY,
						scaleX, scaleY);
			}

			if (displayCircles)
				for (MyTriangle aTriangle : triangles) {
					aTriangle.setColor(g);
					aTriangle.displayObjectCircles(g, decalageX, decalageY);
				}
		}

		// Draw lines
		if (false)
			if (!compEdges.isEmpty())
				for (MyEdge aVertex : compEdges) {
					aVertex.setColor(g);
					aVertex.displayObject(g, decalageX, decalageY, minX, minY,
							scaleX, scaleY);
				}

		if (!edges.isEmpty())
			for (MyEdge aVertex : edges) {
				if (aVertex.marked > 0) {
					aVertex.setColor(g);
					aVertex.displayObject(g, decalageX, decalageY, minX, minY,
							scaleX, scaleY);
				}
			}

		int psize = points.size();
		if ((psize > 0) && (psize < 100)) {
			for (MyPoint aPoint : points) {
				aPoint.setColor(g);
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
			writer.write("\t<Points>\n");
			for (MyPoint aPoint : points) {
				writer.write("\t\t<Point id=\"" + points.indexOf(aPoint)
						+ "\">\n");
				writer.write("\t\t\t<X>" + aPoint.x + "</X>\n");
				writer.write("\t\t\t<Y>" + aPoint.y + "</Y>\n");
				writer.write("\t\t\t<Z>" + aPoint.z + "</Z>\n");
				if (aPoint.type == null)
					writer.write("\t\t\t<Type />\n");
				else
					writer.write("\t\t\t<Type>" + aPoint.type + "</Type>\n");
				writer.write("\t\t</Point>\n");
			}
			writer.write("\t</Points>\n");

			writer.write("\t<Edges>\n");
			for (MyEdge anEdge : edges) {
				writer.write("\t\t<Segment id=\"" + edges.indexOf(anEdge)
						+ "\">\n");
				writer.write("\t\t\t<Start>" + points.indexOf(anEdge.point[0])
						+ "</Start>\n");
				writer.write("\t\t\t<End>" + points.indexOf(anEdge.point[1])
						+ "</End>\n");
				if (anEdge.type == null)
					writer.write("\t\t\t<Type />\n");
				else
					writer.write("\t\t\t<Type>" + anEdge.type + "</Type>\n");
				if (anEdge.left == null)
					writer.write("\t\t\t<Left>-1</Left>\n");
				else
					writer.write("\t\t\t<Left>"
							+ triangles.indexOf(anEdge.left) + "</Left>\n");
				if (anEdge.right == null)
					writer.write("\t\t\t<Right>-1</Right>\n");
				else
					writer.write("\t\t\t<Right>"
							+ triangles.indexOf(anEdge.right) + "</Right>\n");
				writer.write("\t\t</Segment>\n");
			}
			writer.write("\t</Edges>\n");

			writer.write("\t<Triangles>\n");
			for (MyTriangle aTriangle : triangles) {
				writer.write("\t\t<Triangle id=\""
						+ triangles.indexOf(aTriangle) + "\">\n");
				for (int i = 0; i < 3; i++)
					writer.write("\t\t\t<Edge>"
							+ edges.indexOf(aTriangle.edges[i]) + "</Edge>\n");
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
				writer.write(aPoint.x + "\t" + aPoint.y + "\t" + aPoint.z
						+ "\t" + aPoint.gid + "\n");
			}

			writer.write("\n");
			for (MyEdge anEdge : edges) {
				writer.write(anEdge.point[0].gid + "\t" + anEdge.point[1].gid
						+ "\t" + anEdge.gid + "\n");
			}
			for (MyEdge anEdge : compEdges) {
				writer.write(anEdge.point[0].gid + "\t" + anEdge.point[1].gid
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
							aPoint.x = Double.parseDouble(mot);
							break;
						case 1:
							aPoint.y = Double.parseDouble(mot);
							break;
						case 2:
							aPoint.z = Double.parseDouble(mot);
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
							anEdge.point[0] = getPointFromGID(gid);
							break;
						case 1:
							gid = Integer.parseInt(mot);
							anEdge.point[1] = getPointFromGID(gid);
							break;
						case 2:
							anEdge.gid = Integer.parseInt(mot);
							break;
						}
						i++;
					}
					if (i >= 1)
						compEdges.add(anEdge);
					else
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
					MyPoint aPoint = aTriangle.points[j];
					output.writeUTF(aPoint.x + "\t" + aPoint.y + "\t"
							+ aPoint.z);
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
				xmin = points.get(0).x;
				xmax = xmin;
				ymin = points.get(0).y;
				ymax = ymin;
				zmin = points.get(0).z;
				zmax = zmin;
				for (MyPoint aPoint : points) {
					if (xmax < aPoint.x)
						xmax = aPoint.x;
					if (xmin > aPoint.x)
						xmin = aPoint.x;
					if (ymax < aPoint.y)
						ymax = aPoint.y;
					if (ymin > aPoint.y)
						ymin = aPoint.y;
					if (zmax < aPoint.z)
						zmax = aPoint.z;
					if (zmin > aPoint.z)
						zmin = aPoint.z;
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
					writer.write(" " + (aPoint.x - dx) + " " + (aPoint.y - dy)
							+ " " + (aPoint.z - dz) + "\n");
				}
				// add points for walls
				for (MyEdge anEdge : compEdges) {
					if (anEdge.getType() == ConstraintType.WALL) {
						writer.write("#wall points\n");
						for (int i = 0; i < 2; i++) {
							MyPoint aPoint = anEdge.point[i];
							writer.write(" #Wall Point " + (aPoint.gid - 1)
									+ "\n");
							writer.write(" " + (aPoint.x - dx) + " "
									+ (aPoint.y - dy) + " "
									+ (aPoint.z - dz + 2) + "\n");
						}
					}
				}

				writer.write("] # end point\n");
				writer.write("} # end coord\n");
				writer.write("\n");
				writer.write("coordIndex [\n");
				for (MyTriangle aTriangle : triangles) {
					writer.write("#triangle " + (aTriangle.gid - 1) + "\n");
					for (int i = 0; i < 3; i++)
						writer.write((aTriangle.points[i].gid - 1) + "\t");
					writer.write("-1\n");
				}

				// add walls
				int index = points.size();
				for (MyEdge anEdge : compEdges) {
					if (anEdge.getType() == ConstraintType.WALL) {
						writer.write("#wall " + (anEdge.gid - 1) + "\n");
						writer.write((anEdge.point[0].gid - 1) + "\t");
						writer.write((anEdge.point[1].gid - 1) + "\t");
						writer.write((index + 1) + "\t");
						writer.write((index) + "\t");
						writer.write("-1\n");
						index += 2;
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
					writer.write("0 #triangle " + (aTriangle.gid - 1) + "\n");
				}
				for (MyEdge anEdge : compEdges) {
					if (anEdge.getType() == ConstraintType.WALL) {
						writer.write("1 #wall edge " + (anEdge.gid - 1) + "\n");
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
				String type = anEdge.getType();
				if (type != null) {
					double cx = 1, cy = 1, cz = 1;
					if (type == ConstraintType.SEWER) {
						cy = 0;
					} else if (type == ConstraintType.RIVER) {
						cx = 0;
						cy = 0;
					} else if (type == ConstraintType.DITCH) {
						cz = 0;
					}
					VRMLexport_line(writer, anEdge.getStart(), anEdge.getEnd(),
							dx, dy, dz, cx, cy, cz);
				}
			}

			writer.write("\n");
			writer.write("Viewpoint {\n");
			writer.write("description \"middle\"\n");
			writer.write("position 0 0 " + zcamera + "\n");
			writer.write("} # end viewpoint\n");
			writer.write("\n");

			writer.close();
		} catch (IOException e) {
		}

	}

	private void VRMLexport_line(Writer writer, MyPoint start, MyPoint end,
			double dx, double dy, double dz, double cx, double cy, double cz) {
		try {

			double x, y, z;
			x = (start.x + end.x) / 2;
			y = (start.y + end.y) / 2;
			z = (start.z + end.z) / 2;

			double length = Math.sqrt(start.squareDistance(end));

			double ux = (end.x - start.x) / length;
			double uy = (end.y - start.y) / length;
			double uz = (end.z - start.z) / length;
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
	 * Set missing GIDs for points
	 */
	protected void SetAllGIDs_Point() {
		// sort points
		MyTools.quickSortGID_Points(points, 0, points.size() - 1);

		// Values are orderer
		int maxGID = points.size();
		for (int i = 0; i < maxGID; i++) {
			points.get(i).setGid(i + 1);
		}

		point_GID = maxGID;
	}

	/**
	 * Set missing GIDs for edges
	 */
	protected void SetAllGIDs_Edges() {
		// sort edges
		MyTools.quickSortGID_Edges(edges, 0, edges.size() - 1);

		// Values are orderer
		int maxGID = edges.size();
		for (int i = 0; i < maxGID; i++) {
			edges.get(i).setGid(i + 1);
		}

		edge_GID = maxGID;
	}

	/**
	 * Set GIDs for triangles
	 */
	protected void SetAllGIDs_Triangle() {
		int maxGID = triangles.size();
		for (int i = 0; i < maxGID; i++) {
			triangles.get(i).setGid(i + 1);
		}

		triangle_GID = maxGID;
	}

	/**
	 * Set missing GIDs for points, edges and triangles
	 */
	public void setAllGids() {
		// Process points
		SetAllGIDs_Point();

		// Process edges
		SetAllGIDs_Edges();

		// Process triangles
		SetAllGIDs_Triangle();
	}
}
