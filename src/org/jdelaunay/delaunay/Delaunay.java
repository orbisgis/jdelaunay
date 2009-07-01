package org.jdelaunay.delaunay;

/**
 * Delaunay Package.
 *
 * @author Jean-Yves MARTIN
 * @date 2009-01-12
 * @version 1.1
 */

import java.util.*;

import org.jdelaunay.utilities.HydroLineUtil;
import org.jdelaunay.utilities.HydroPolygonUtil;
import org.jdelaunay.utilities.MathUtil;

/**
 * @author kwyhr
 *
 */
public class Delaunay {
	// the Mesh
	protected MyMesh theMesh;

	// Parameters
	protected double precision;
	protected double tolarence;
	protected double minArea, maxArea;
	protected double minAngle;
	protected int refinement;
	private boolean verbose;

	// constants
	public static final int refinement_minArea = 1;
	public static final int refinement_maxArea = 2;
	public static final int refinement_minAngle = 4;
	public static final int maxIter = 5;

	// Mesh data access
	private ArrayList<MyPoint> points;
	private ArrayList<MyEdge> edges;
	private LinkedList<MyTriangle> triangles;

	// Working index vector
	private LinkedList<MyEdge> badEdgesQueueList;
	private LinkedList<MyEdge> boundaryEdges;

	// GIDs
	private int point_GID;
	private int edge_GID;
	private int triangle_GID;

	/**
	 * Generate empty Delaunay Structure.
	 */
	private void init() {
		theMesh = null;
		precision = 0.0;
		tolarence = 0.000001;
		maxArea = 600;
		minArea = 1;
		minAngle = 5;
		refinement = 0;
		verbose = false;
	}

	/**
	 * Generate empty Delaunay Structure.
	 */
	public Delaunay() {
		init();
		point_GID = 0;
		edge_GID = 0;
		triangle_GID = 0;
	}

	/**
	 * Generate empty Delaunay Structure for a Mesh.
	 */
	public Delaunay(MyMesh aMesh) {
		init();
		theMesh = aMesh;
		aMesh.DelaunayReference = this;
	}

	/**
	 * Return associated Mesh.
	 *
	 * @return
	 */
	public MyMesh getMesh() {
		return theMesh;
	}

	/**
	 * Set current Mesh
	 *
	 * @param _theMesh
	 */
	public void setMesh(MyMesh _theMesh) {
		this.theMesh = _theMesh;
		_theMesh.DelaunayReference = this;
	}

	/**
	 * Set precision for proximity.
	 *
	 * @param _precision
	 */
	public void setPrecision(double _precision) {
		precision = _precision;
	}

	/**
	 * Get precision for proximity.
	 *
	 * @return
	 */
	public double getPrecision() {
		return precision;
	}

	/**
	 * Get maximum area for refinement.
	 *
	 * @return maxArea
	 */
	public double getMaxArea() {
		return maxArea;
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
		return minArea;
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
	 * Set refinement. Refinement value can be any combinaison of :
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
	 * @param verbose
	 *            mode
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * Generate the Delaunay triangularization with a flip-flop algorithm. Mesh
	 * must have been set. Triangularization can only be done once. Otherwise
	 * call reprocessDelaunay
	 *
	 * @throws DelaunayError
	 */
	public void processDelaunay() throws DelaunayError {
		if (theMesh == null)
			throw new DelaunayError(DelaunayError.DelaunayError_noMesh);
		else if (theMesh.isMeshComputed())
			throw new DelaunayError(DelaunayError.DelaunayError_Generated);
		else if (theMesh.getNbPoints() < 3)
			throw new DelaunayError(
					DelaunayError.DelaunayError_notEnoughPointsFound);
		else {
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
			while (p1.marked)
				p1 = iterPoint.next();

			p2 = iterPoint.next();
			while (p2.marked)
				p2 = iterPoint.next();

			p3 = iterPoint.next();
			while (p3.marked)
				p3 = iterPoint.next();

			// The triangle's edges MUST be in the right direction
			e1 = new MyEdge(p1, p2);
			if (e1.isLeft(p3)) {
				e2 = new MyEdge(p2, p3);
				e3 = new MyEdge(p3, p1);
			} else {
				e1 = new MyEdge(p2, p1);
				e2 = new MyEdge(p1, p3);
				e3 = new MyEdge(p3, p2);
			}

			edges.add(e1);
			edges.add(e2);
			edges.add(e3);

			aTriangle = new MyTriangle(p1, p2, p3, e1, e2, e3);
			triangles.add(aTriangle);

			// Then process the other points - order don't care
			boundaryEdges.add(e1);
			boundaryEdges.add(e2);
			boundaryEdges.add(e3);

			// flip-flop on a list of points
			while (iterPoint.hasNext()) {
				MyPoint aPoint = iterPoint.next();
				if (!aPoint.marked)
					myInsertPoint(aPoint);
			}

			if (verbose) {
				System.out.println("Triangularization phase : ");
				System.out.println("  Points : " + points.size());
				System.out.println("  Edges : " + edges.size());
				System.out.println("  Triangles : " + triangles.size());
			}

			theMesh.setMeshComputed(true);

			// remove flat triangles
			// removeFlatTriangles();

			// Add the edges in the edges array
			if (verbose)
				System.out.println("Adding edges");
			processEdges(theMesh.compEdges);
			// removeFlatTriangles();

			// adding GIDs
			if (verbose)
				System.out.println("set GIDs");
			theMesh.setAllGids();
			/*
			 * point_GID = 0; for (MyPoint aPoint : points) { point_GID++;
			 * aPoint.setGid(point_GID); }
			 *
			 * edge_GID = 0; for (MyEdge anEdge : edges) { edge_GID++;
			 * anEdge.setGid(edge_GID); }
			 *
			 * triangle_GID = 0; for (MyTriangle aTriangle1 : triangles) {
			 * triangle_GID++; aTriangle1.setGid(triangle_GID); }
			 */
			// It's fine, we computed the mesh
			if (verbose)
				System.out.println("end processing");

			if (verbose) {
				System.out.println("Triangularization end phase : ");
				System.out.println("  Points : " + points.size());
				System.out.println("  Edges : " + edges.size());
				System.out.println("  Triangles : " + triangles.size());
			}
		}
	}

	/**
	 * Re-Generate the Delaunay triangularization with a flip-flop algorithm.
	 * Mesh must have been set. Every triangle and edge is removed to restart
	 * the process.
	 *
	 * @throws DelaunayError
	 */
	public void reprocessDelaunay() throws DelaunayError {
		if (theMesh == null)
			throw new DelaunayError(DelaunayError.DelaunayError_noMesh);
		else {
			edges = new ArrayList<MyEdge>();
			triangles = new LinkedList<MyTriangle>();
			theMesh.setMeshComputed(false);

			// Restart the process
			processDelaunay();
		}
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
		if (theMesh == null)
			throw new DelaunayError(DelaunayError.DelaunayError_noMesh);
		else if (!aTriangle.isInside(aPoint))
			throw new DelaunayError(DelaunayError.DelaunayError_outsideTriangle);
		else {
			// add point in the triangle
			points.add(aPoint);
			processAddPoint(aTriangle, aPoint);

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
		if (theMesh == null)
			throw new DelaunayError(DelaunayError.DelaunayError_noMesh);
		else if (!anEdge.isInside(aPoint))
			throw new DelaunayError(DelaunayError.DelaunayError_outsideTriangle);
		else {
			// Add point
			processAddPoint(anEdge, aPoint);

			// Then apply the flip-flop algorithm
			processBadEdges();
		}
	}

	/**
	 * Add a point in the mesh and rebuild triangularization
	 *
	 * @param aPoint
	 * @throws DelaunayError
	 */
	public MyTriangle addPoint(MyPoint aPoint) throws DelaunayError {
		MyTriangle foundTriangle = null;
		if (theMesh == null)
			throw new DelaunayError(DelaunayError.DelaunayError_noMesh);
		else {
			// First we check if the point is in the points list
			boolean pointAlreadyExists = points.contains(aPoint);

			// First we find the point's location.
			ListIterator<MyTriangle> iterTriangle = triangles.listIterator();
			while ((iterTriangle.hasNext()) && (foundTriangle == null)) {
				MyTriangle aTriangle = iterTriangle.next();
				if (aTriangle.isInside(aPoint)) {
					foundTriangle = aTriangle;
				}
			}

			if (!pointAlreadyExists) {
				if (foundTriangle != null) {
					// the point is inside the foundTriangle triangle
					addPoint(foundTriangle, aPoint);
				} else {
					// the point is outside the mesh
					// The boundary edge list is ok
					// We insert the point in the mesh
					points.add(aPoint);
					foundTriangle = myInsertPoint(aPoint);
				}
			}
		}

		return foundTriangle;
	}

	/**
	 * Get the triangle where the point is
	 *
	 * @param aPoint
	 * @throws DelaunayError
	 */
	public MyTriangle getTriangle(MyPoint aPoint) throws DelaunayError {
		MyTriangle foundTriangle = null;
		if (theMesh == null)
			throw new DelaunayError(DelaunayError.DelaunayError_noMesh);
		else {
			ListIterator<MyTriangle> iterTriangle = triangles.listIterator();
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
	 * Add a point inside a triangle
	 *
	 * @param aTriangle
	 * @param aPoint
	 * @throws DelaunayError
	 */
	private void processAddPoint(MyTriangle aTriangle, MyPoint aPoint) {
		// We build 3 edges from the point to each point of the triangle
		MyEdge anEdge[] = new MyEdge[6];
		MyEdge oldEdge[] = new MyEdge[3];
		for (int i = 0; i < 3; i++) {
			anEdge[i] = new MyEdge(aTriangle.points[i], aPoint);
			anEdge[i + 3] = anEdge[i];
			oldEdge[i] = aTriangle.edges[i];
			edges.add(anEdge[i]);
		}

		// We build a triangle with each edge

		// build first triangle
		MyTriangle aTriangle1 = new MyTriangle();
		aTriangle1.edges[0] = aTriangle.edges[0];
		if (aTriangle1.edges[0].left == aTriangle)
			aTriangle1.edges[0].left = aTriangle1;
		else
			aTriangle1.edges[0].right = aTriangle1;
		aTriangle1.points[0] = aTriangle1.edges[0].getStart();
		aTriangle1.points[1] = aTriangle1.edges[0].getEnd();
		aTriangle1.points[2] = aPoint;
		int k = 1;
		for (int i = 0; i < 3; i++) {
			if (anEdge[i] != null)
				if (aTriangle1.points[0] == anEdge[i].getStart()) {
					aTriangle1.edges[k] = anEdge[i];
					anEdge[i] = null;
					k++;
				} else if (aTriangle1.points[1] == anEdge[i].getStart()) {
					aTriangle1.edges[k] = anEdge[i];
					anEdge[i] = null;
					k++;
				}
		}

		triangles.add(aTriangle1);

		// Second triangle
		MyTriangle aTriangle2 = new MyTriangle();
		aTriangle2.edges[0] = aTriangle.edges[1];
		if (aTriangle2.edges[0].left == aTriangle)
			aTriangle2.edges[0].left = aTriangle2;
		else
			aTriangle2.edges[0].right = aTriangle2;
		aTriangle2.points[0] = aTriangle2.edges[0].getStart();
		aTriangle2.points[1] = aTriangle2.edges[0].getEnd();
		aTriangle2.points[2] = aPoint;
		k = 1;
		for (int i = 3; i < 6; i++) {
			if (anEdge[i] != null)
				if (aTriangle2.points[0] == anEdge[i].getStart()) {
					aTriangle2.edges[k] = anEdge[i];
					anEdge[i] = null;
					k++;
				} else if (aTriangle2.points[1] == anEdge[i].getStart()) {
					aTriangle2.edges[k] = anEdge[i];
					anEdge[i] = null;
					k++;
				}
		}
		triangles.add(aTriangle2);

		// change current triangle
		// Replace the two first edges by the two remaining edges
		// and keep the last edge
		k = 0;
		for (int i = 0; i < 6; i++) {
			if (anEdge[i] != null) {
				aTriangle.edges[k] = anEdge[i];
				anEdge[i] = null;
				k++;
			}
		}
		// Add the points
		aTriangle.points[0] = aTriangle.edges[2].getStart();
		aTriangle.points[1] = aTriangle.edges[2].getEnd();
		aTriangle.points[2] = aPoint;

		// Rebuild all topologies
		aTriangle.reconnectEdges();
		aTriangle1.reconnectEdges();
		aTriangle2.reconnectEdges();

		aTriangle.recomputeCenter();
		aTriangle1.recomputeCenter();
		aTriangle2.recomputeCenter();

		// Add edges to the bad edges list
		if (!theMesh.isMeshComputed())
			for (int i = 0; i < 3; i++) {
				if (!badEdgesQueueList.contains(aTriangle.edges[i]))
					badEdgesQueueList.add(aTriangle.edges[i]);
				if (!badEdgesQueueList.contains(aTriangle1.edges[i]))
					badEdgesQueueList.add(aTriangle1.edges[i]);
				if (!badEdgesQueueList.contains(aTriangle2.edges[i]))
					badEdgesQueueList.add(aTriangle2.edges[i]);
			}
		checkTopology();
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
			MyPoint start = anEdge.getStart();

			MyPoint end = anEdge.getEnd();

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
			remainEdge.point[0] = aPoint;
			anEdge.point[1] = aPoint;

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

					// change start
					i = 0;
					found = false;
					while ((i < 3) && (!found)) {
						if (new_triangleList[k].points[i] == start)
							found = true;
						else
							i++;
					}
					if (found)
						new_triangleList[k].points[i] = aPoint;

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
					// change end
					int i = 0;
					boolean found = false;
					while ((i < 3) && (!found)) {
						if (triangleList[k].points[i] == end)
							found = true;
						else
							i++;
					}
					if (found)
						triangleList[k].points[i] = aPoint;

					// change alterEdgeList_end
					i = 0;
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
					if (!theMesh.isMeshComputed())
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
		if (theMesh == null)
			throw new DelaunayError(DelaunayError.DelaunayError_noMesh);
		else if (!theMesh.isMeshComputed())
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
		if (theMesh == null)
			throw new DelaunayError(DelaunayError.DelaunayError_noMesh);
		else if (!theMesh.isMeshComputed())
			throw new DelaunayError(DelaunayError.DelaunayError_notGenerated);
		else if (anEdge.getStart().squareDistance(anEdge.getEnd()) < tolarence)
			throw new DelaunayError(DelaunayError.DelaunayError_proximity);
		else if (!points.contains(anEdge.getStart()))
			throw new DelaunayError(DelaunayError.DelaunayError_pointNotFound);
		else if (!points.contains(anEdge.getEnd()))
			throw new DelaunayError(DelaunayError.DelaunayError_pointNotFound);
		else {
			badEdgesQueueList = new LinkedList<MyEdge>();
			ArrayList<MyEdge> theList = new ArrayList<MyEdge>();
			theList.add(anEdge);
			processEdges(theList);
		}
	}

	/**
	 * Process a triangle with a too small area : - merge the three point as a
	 * new point and remove the three points - remove the three edges - process
	 * the neighbors
	 *
	 * @param aTriangle
	 */
	private void processSmallAreaTriangle(MyTriangle aTriangle,
			LinkedList<MyTriangle> toBeRemoved) {
		double x, y, z;
		x = y = z = 0;
		for (int i = 0; i < 3; i++) {
			x += aTriangle.points[i].x;
			y += aTriangle.points[i].y;
			z += aTriangle.points[i].z;
		}
		x /= 3;
		y /= 3;
		z /= 3;

		MyPoint newPoint = new MyPoint(x, y, z);
		points.add(newPoint);

		// replace each reference to one of the points of the
		// triangle by a reference to this point
		MyPoint aPoint;
		for (int i = 0; i < 3; i++) {
			MyEdge anEdge = aTriangle.edges[i];

			int countModify = 0;
			for (int k = 0; k < 2; k++) {
				// In the edge
				boolean thereIsOne = false;
				if (k == 0)
					aPoint = anEdge.getStart();
				else
					aPoint = anEdge.getEnd();
				for (int j = 0; j < 3; j++)
					if (aPoint == aTriangle.points[j]) {
						if (k == 0)
							anEdge.setStart(newPoint);
						else
							anEdge.setEnd(newPoint);
						thereIsOne = true;
						countModify++;
					}

				// if edge is touched, the triangles must be
				if (thereIsOne) {
					if (anEdge.left != null)
						for (int j = 0; j < 3; j++) {
							if (anEdge.left.points[j] == aPoint)
								anEdge.left.points[j] = newPoint;
						}
					if (anEdge.right != null)
						for (int j = 0; j < 3; j++) {
							if (anEdge.right.points[j] == aPoint)
								anEdge.right.points[j] = newPoint;
						}
				}
			}

			// On the other side of the edge, there can be
			// something to
			// change
			if (countModify == 2) {
				// we modified it twice, so the edge have to be
				// deleted.
				MyTriangle otherTriangle = anEdge.left;
				if (otherTriangle == aTriangle)
					otherTriangle = anEdge.right;

				// That means that the two other edges MUST
				// merge
				MyEdge changeEdge = null;
				for (int k = 0; k < 3; k++) {
					if (otherTriangle.edges[k] != anEdge) {
						if (changeEdge == null) {

						}

					}
				}
			}
		}
		// Then we remove the edges of the Triangle

	}

	/**
	 * Process a triangle with a too large area : - add a new point in the
	 * middle - generate three triangles in place of the current one That mean
	 * we generate two more triangles and we replace the current one Then we
	 * rebuild the delaunay triangularization
	 *
	 * @param aTriangle
	 */
	private void processLargeAreaTriangle(MyTriangle aTriangle)
			throws DelaunayError {
		double x, y, z;
		x = y = z = 0;
		for (int i = 0; i < 3; i++) {
			x += aTriangle.points[i].x;
			y += aTriangle.points[i].y;
			z += aTriangle.points[i].z;
		}
		x /= 3;
		y /= 3;
		z /= 3;

		MyPoint newPoint = new MyPoint(x, y, z);
		addPoint(aTriangle, newPoint);
	}

	/**
	 * Process a triangle with a too small angle :
	 *
	 * @param aTriangle
	 */
	private void processSmallAngleTriangle(MyTriangle aTriangle,
			LinkedList<MyTriangle> toBeRemoved) {
		int badVertice = -1;
		double minDistance = -1;
		for (int i = 0; i < 3; i++) {
			MyEdge anEdge = aTriangle.edges[i];
			double dist = anEdge.getStart().squareDistance_2D(anEdge.getEnd());
			if ((badVertice == -1) || (dist < minDistance)) {
				minDistance = dist;
				badVertice = i;
			}
		}
	}

	/**
	 * Refine mesh according to the type of refinement that has been defined in
	 * the refinement variable
	 */
	public void refineMesh() throws DelaunayError {
		if (theMesh == null)
			throw new DelaunayError(DelaunayError.DelaunayError_noMesh);
		else if (!theMesh.isMeshComputed())
			throw new DelaunayError(DelaunayError.DelaunayError_notGenerated);
		else {
			// check all triangle to save all the ones with a bad area criteria
			LinkedList<MyTriangle> badTrianglesList = new LinkedList<MyTriangle>();
			LinkedList<MyTriangle> toBeRemoved = new LinkedList<MyTriangle>();

			int iterDone = 0;
			int nbDone = 0;
			do {
				iterDone++;
				nbDone = 0;

				if ((refinement & refinement_maxArea) != 0) {
					for (MyTriangle aTriangle : triangles) {
						double area = aTriangle.computeArea();
						if (area > maxArea)
							badTrianglesList.add(aTriangle);
					}

					// Process the triangle :
					// if it is too big, we split it
					while (!badTrianglesList.isEmpty()) {
						MyTriangle aTriangle = badTrianglesList.getFirst();
						badTrianglesList.removeFirst();
						nbDone++;

						// build a new point inside
						processLargeAreaTriangle(aTriangle);
					}
				}

				if ((refinement & refinement_minArea) != 0) {
					for (MyTriangle aTriangle : triangles) {
						double area = aTriangle.computeArea();
						if (area < minArea)
							badTrianglesList.add(aTriangle);
					}

					// Process the triangle :
					// if it is too small, we remove it
					while (!badTrianglesList.isEmpty()) {
						MyTriangle aTriangle = badTrianglesList.getFirst();
						badTrianglesList.removeFirst();
						nbDone++;

						// build a new point inside
						processSmallAreaTriangle(aTriangle, toBeRemoved);
					}

					while (!toBeRemoved.isEmpty()) {
						MyTriangle aTriangle = toBeRemoved.getFirst();
						toBeRemoved.removeFirst();

						triangles.remove(aTriangle);
					}
				}

				if ((refinement & refinement_minAngle) != 0) {
					for (MyTriangle aTriangle : triangles) {
						if (aTriangle.badAngle(minAngle) >= 0)
							badTrianglesList.add(aTriangle);
					}

					// Process the triangle :
					// if it has a too small, we remove it
					while (!badTrianglesList.isEmpty()) {
						MyTriangle aTriangle = badTrianglesList.getFirst();
						badTrianglesList.removeFirst();
						nbDone++;

						// build a new point inside
						processSmallAngleTriangle(aTriangle, toBeRemoved);
					}

					while (!toBeRemoved.isEmpty()) {
						MyTriangle aTriangle = toBeRemoved.getFirst();
						toBeRemoved.removeFirst();

						triangles.remove(aTriangle);
					}
				}
			} while ((nbDone != 0) && (iterDone < maxIter));
		}
	}

	/**
	 * Add edges defined at the beginning of the process
	 *
	 * @param compEdges
	 */
	private void processEdges(ArrayList<MyEdge> compEdges) {
		int nbEdges = edges.size();
		if (nbEdges > 0)
			quickSort_Edges(edges, 0, nbEdges - 1, false);

		int nbEdges2 = compEdges.size();
		if (nbEdges2 > 0)
			quickSort_Edges(compEdges, 0, nbEdges2 - 1, false);

		// Process unconnected edges
		ArrayList<MyEdge> remain0 = processEdges_Step0(compEdges);
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
			quickSort_Edges(remain2, 0, nbEdges4 - 1, true);
		processOtherEdges(remain2);

		// Post process some edges
		postProcessEdges();

	}

	/**
	 * Mark existing edges (compEdges and edges are supposed to be sorted)
	 *
	 * @param compEdges
	 * @return list of remaining edges
	 */
	private ArrayList<MyEdge> processEdges_Step0(ArrayList<MyEdge> compEdges) {
		ArrayList<MyEdge> remainEdges = new ArrayList<MyEdge>();

		// While there is still an edge to process
		for (MyEdge anEdge : compEdges) {
			if (anEdge.outsideMesh) {
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
	 * Mark existing edges (compEdges and edges are supposed to be sorted)
	 *
	 * @param compEdges
	 * @return list of remaining edges
	 */
	private ArrayList<MyEdge> processEdges_Step1(ArrayList<MyEdge> compEdges) {
		ArrayList<MyEdge> remainEdges = new ArrayList<MyEdge>();

		MyEdge currentEdge = null;
		MyEdge currentEdge2 = null;
		int index = 0;
		int maxIndex = edges.size();
		double cle_ref1, cle_ref2, cle_ref3, cle_ref4;
		double cle1, cle2, cle3, cle4;
		double x;

		// While there is still an edge to process
		ListIterator<MyEdge> iterEdge = compEdges.listIterator();
		while (iterEdge.hasNext()) {
			// Get first edge then remove it from the list
			currentEdge = iterEdge.next();

			// Compute edge intersection with the Mesh
			MyPoint p1 = currentEdge.getStart();
			MyPoint p2 = currentEdge.getEnd();

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
				MyPoint p3 = currentEdge2.getStart();
				MyPoint p4 = currentEdge2.getEnd();
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
					if ((anEdge.getStart() != start)
							&& (anEdge.getEnd() != start))
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
					for (int j = 0; j < 3; j++) {
						if (alterTriangle.points[j] == end)
							match = true;
					}

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
	 * Mark existing edges (compEdges and edges are supposed to be sorted)
	 *
	 * @param compEdges
	 * @return list of remaining edges
	 */
	private ArrayList<MyEdge> processEdges_Step2(ArrayList<MyEdge> compEdges) {
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
		ListIterator<MyEdge> iterEdge = compEdges.listIterator();
		while (iterEdge.hasNext()) {
			// Get first edge then remove it from the list
			currentEdge = iterEdge.next();
			boolean found = false;

			// Compute edge intersection with the Mesh
			MyPoint p1 = currentEdge.getStart();
			MyPoint p2 = currentEdge.getEnd();

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
				MyPoint p3 = currentEdge2.getStart();
				MyPoint p4 = currentEdge2.getEnd();
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
							found = true;

							// look for swapping edge
							if (p1 == swapEdge.getEnd())
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
	 * Mark existing edges (compEdges and edges are supposed to be sorted)
	 *
	 * @param compEdges
	 * @return list of remaining edges
	 */
	private void processOtherEdges(ArrayList<MyEdge> compEdges) {
		// List of triangles that are created when there is an intersection
		MyEdge CurrentEdge = null;

		int iter = 0;
		int maxIter = compEdges.size();

		if (verbose)
			System.out.println("Processing mesh intersection for " + maxIter
					+ " edges");

		// While there is still an edge to process
		ListIterator<MyEdge> iterEdge = compEdges.listIterator();
		while (iterEdge.hasNext()) {
			iter++;
			if (verbose)
				System.out.println("Processing edge " + iter + " / " + maxIter);

			// Get first edge then remove it from the list
			CurrentEdge = iterEdge.next();

			// Compute edge intersection with the Mesh
			MyPoint p1 = CurrentEdge.getStart();
			MyPoint p2 = CurrentEdge.getEnd();

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
				MyPoint p3 = anEdge.getStart();
				MyPoint p4 = anEdge.getEnd();

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
					MyPoint start = anEdge.getStart();
					MyPoint end = anEdge.getEnd();

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
					aPoint.marked = true;
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
					LastPoint.marked = true;
					p.marked = true;
					anEdge.setType(CurrentEdge.getType());

					// look for swapping edge
					if (anEdge.getEnd() == p)
						anEdge.swap();
				}
				LastPoint = p;
			}
		}

		// Then apply the flip-flop algorithm
		processBadEdges();
	}

	/**
	 * post process the edges according to their type
	 */
	private void postProcessEdges() {
		LinkedList<MyEdge> addedEdges = new LinkedList<MyEdge>();
		for (MyEdge anEdge : edges) {
			if (anEdge.getType() == ConstaintType.WALL) {
				// Process wall : duplicate edge and changes connections
				if ((anEdge.left != null) && (anEdge.right != null)) {
					// Something to do if and only if there are two triangles
					// connected
					MyEdge newEdge = new MyEdge(anEdge);

					// Changes left triangle connection
					MyTriangle aTriangle = anEdge.left;
					for (int i = 0; i < 3; i++) {
						if (aTriangle.edges[i] == anEdge)
							aTriangle.edges[i] = newEdge;
					}

					// Changes edges connections
					newEdge.right = null;
					anEdge.left = null;

					// add the new edge
					addedEdges.add(newEdge);
				}
			}
		}

		// add edges to the structure
		for (MyEdge anEdge : addedEdges) {
			edges.add(anEdge);
		}
	}

	/**
	 * sort points, remove same points and reset points and edges
	 */
	private void sortAndSimplify() {
		points = theMesh.getPoints();
		edges = theMesh.getEdges();
		triangles = theMesh.getTriangles();
		int NbPoints = theMesh.getNbPoints();
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
			for (int i = 0; i < 2; i++) {
				MyPoint aPoint = anEdge.point[i];
				if (Replace.containsKey(aPoint)) {
					anEdge.point[i] = Replace.get(aPoint);
				}
			}
		}

		for (MyEdge anEdge : theMesh.compEdges) {
			for (int i = 0; i < 2; i++) {
				MyPoint aPoint = anEdge.point[i];
				if (Replace.containsKey(aPoint)) {
					anEdge.point[i] = Replace.get(aPoint);
				}
			}
		}

		// Then remove points from the list
		for (MyPoint aPoint : Replace.keySet()) {
			points.remove(aPoint);
		}

	}

	/**
	 * Quick sort on points Ordered according to minimum X, Y of both
	 * extremities
	 *
	 * @param min_index
	 * @param max_index
	 */
	private void quickSort_Edges(ArrayList<MyEdge> edges, int min_index,
			int max_index, boolean switchPoints) {
		int i, j;
		int enreg_ref;
		double cle_ref1, cle_ref2, cle_ref3, cle_ref4;
		double cle1, cle2, cle3, cle4;
		double x;
		boolean found;

		i = min_index;
		j = max_index;
		enreg_ref = (max_index + min_index) / 2;
		MyEdge anEdge = edges.get(enreg_ref);
		cle_ref1 = anEdge.getStart().getX();
		cle_ref2 = anEdge.getStart().getY();
		cle_ref3 = anEdge.getEnd().getX();
		cle_ref4 = anEdge.getEnd().getY();
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
		if (switchPoints) {
			x = cle_ref3;
			cle_ref3 = cle_ref1;
			cle_ref1 = x;

			x = cle_ref4;
			cle_ref4 = cle_ref2;
			cle_ref2 = x;
		}
		do {
			// first : increasing index
			found = false;
			while (!found) {
				if (i > max_index)
					found = true;
				else {
					anEdge = edges.get(i);
					cle1 = anEdge.getStart().getX();
					cle2 = anEdge.getStart().getY();
					cle3 = anEdge.getEnd().getX();
					cle4 = anEdge.getEnd().getY();
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
					if (switchPoints) {
						x = cle3;
						cle3 = cle1;
						cle1 = x;

						x = cle4;
						cle4 = cle2;
						cle2 = x;
					}

					if (cle1 > cle_ref1)
						found = true;
					else if (cle1 < cle_ref1)
						i++;
					else if (cle2 > cle_ref2)
						found = true;
					else if (cle2 < cle_ref2)
						i++;
					else if (cle3 > cle_ref3)
						found = true;
					else if (cle3 < cle_ref3)
						i++;
					else if (cle4 > cle_ref4)
						found = true;
					else if (cle4 < cle_ref4)
						i++;
					else
						found = true;
				}
			}

			// second : decreasing index
			found = false;
			while (!found) {
				if (min_index > j)
					found = true;
				else {
					anEdge = edges.get(j);
					cle1 = anEdge.getStart().getX();
					cle2 = anEdge.getStart().getY();
					cle3 = anEdge.getEnd().getX();
					cle4 = anEdge.getEnd().getY();
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
					if (switchPoints) {
						x = cle3;
						cle3 = cle1;
						cle1 = x;

						x = cle4;
						cle4 = cle2;
						cle2 = x;
					}

					if (cle1 < cle_ref1)
						found = true;
					else if (cle1 > cle_ref1)
						j--;
					else if (cle2 < cle_ref2)
						found = true;
					else if (cle2 > cle_ref2)
						j--;
					else if (cle3 < cle_ref3)
						found = true;
					else if (cle3 > cle_ref3)
						j--;
					else if (cle4 < cle_ref4)
						found = true;
					else if (cle4 > cle_ref4)
						j--;
					else
						found = true;
				}
			}

			// exchange values
			if (i <= j) {
				// we can change values
				anEdge = edges.get(i);
				edges.set(i, edges.get(j));
				edges.set(j, anEdge);

				i++;
				j--;
			}
		} while (i <= j);

		// Recurrent calls
		if (min_index < j) {
			// if left side is not empty
			quickSort_Edges(edges, min_index, j, switchPoints);
		}
		if (max_index > i) {
			// if right side is not empty
			quickSort_Edges(edges, i, max_index, switchPoints);
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
			if (anEdge.isRight(aPoint)) {
				p1 = anEdge.getEnd();
				p2 = anEdge.getStart();

				// triangle points order is p1, p2, aPoint

				// check if there is an edge between p2 and aPoint
				anEdge1 = checkTwoPointsEdge(p2, aPoint, newEdges);
				if (anEdge1 == null) {
					anEdge1 = new MyEdge(p2, aPoint);
					edges.add(anEdge1);
					newEdges.add(anEdge1);
				} else {
					// second use of the edge => remove it from the list
					newEdges.remove(anEdge1);
				}

				// check if there is an edge between aPoint and p1
				anEdge2 = checkTwoPointsEdge(aPoint, p1, newEdges);
				if (anEdge2 == null) {
					anEdge2 = new MyEdge(aPoint, p1);
					edges.add(anEdge2);
					newEdges.add(anEdge2);
				} else {
					// second use of the edge => remove it from the list
					newEdges.remove(anEdge2);
				}

				// create triangle
				MyTriangle aTriangle = new MyTriangle(p1, p2, aPoint, anEdge,
						anEdge1, anEdge2);
				triangles.add(aTriangle);
				if (foundTriangle == null)
					foundTriangle = aTriangle;

				// Mark the edge to be removed
				oldEdges.add(anEdge);

				// add the edges to the bad edges list
				if (!theMesh.isMeshComputed()) {
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
		if ((aTriangle1 != null) && (aTriangle2 != null)) {
			MyPoint p1 = anEdge.getStart();
			MyPoint p2 = anEdge.getEnd();
			MyPoint p3, p4;

			p3 = p4 = null;

			// Test for each triangle if the remaining point of the
			// other triangle is inside or not
			// Triangle 1 is p1, p2, p3 or p2, p1, p3
			for (int j = 0; j < 3; j++) {
				if ((aTriangle1.points[j] != p1)
						&& (aTriangle1.points[j] != p2)) {
					p3 = aTriangle1.points[j];
				}
			}
			if (p3 != null)
				if (aTriangle2.inCircle(p3) == 1)
					exchange = true;

			// Triangle 2 is p2, p1, p4 or p1, p2, p4
			for (int j = 0; j < 3; j++)
				if ((aTriangle2.points[j] != p1)
						&& (aTriangle2.points[j] != p2)) {
					p4 = aTriangle2.points[j];
				}
			if (p4 != null)
				if (aTriangle1.inCircle(p4) == 1)
					exchange = true;

			if (p3 != p4)
				if ((exchange) || (forced)) {
					// We need to exchange points of the triangles

					// rebuild the two triangles
					MyEdge anEdge10, anEdge11, anEdge12;
					MyEdge anEdge20, anEdge21, anEdge22;

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
						// System.out.println("ERREUR");
					} else {
						// Set points
						anEdge.point[0] = p3;
						anEdge.point[1] = p4;

						// First triangle becomes p3,p4,p1
						// Second triangle becomes p4,p3,p2
						aTriangle1.points[0] = p3;
						aTriangle1.points[1] = p4;
						aTriangle1.points[2] = p1;

						aTriangle2.points[0] = p4;
						aTriangle2.points[1] = p3;
						aTriangle2.points[2] = p2;

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
		if (!theMesh.isMeshComputed()) {
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
							// Add the edges to the bad edges list
							for (int j = 0; j < 3; j++) {
								if (aTriangle1.edges[j] != anEdge)
									if (!badEdgesQueueList
											.contains(aTriangle1.edges[j]))
										badEdgesQueueList
												.add(aTriangle1.edges[j]);
								if (aTriangle2.edges[j] != anEdge)
									if (!badEdgesQueueList
											.contains(aTriangle2.edges[j]))
										badEdgesQueueList
												.add(aTriangle2.edges[j]);
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
			LinkedList<MyPoint> addedPoints, LinkedList<MyPoint> impactPoints) {
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
		for (int i = 0; i < nbElements; i++) {
			MyEdge anEdge = edgeToProcess[i];

			MyTriangle alterTriangle = trianglesToProcess[i];
			MyPoint alterPoint = alterTriangle.getAlterPoint(anEdge);
			double basicZ = anEdge.point[0].z;
			double altZ = alterPoint.z;

			// Split the edge in the middle
			MyPoint middle = anEdge.getBarycenter();
			LinkedList<MyTriangle> impactedTriangles = processAddPoint(anEdge,
					middle);

			// Move middle
			middle.z = (3 * basicZ + altZ) / 4;

			// Recompute all centers because it one point moved
			for (MyTriangle aTriangle1 : impactedTriangles) {
				aTriangle1.recomputeCenter();
			}

			addedPoints.add(middle);
			impactPoints.add(alterPoint);

			int iter = 5;
			while ((Math.abs(middle.z - basicZ) < MyTriangle.epsilon)
					&& (iter > 0)) {
				// too flat, change altitudes
				LinkedList<MyPoint> todo = new LinkedList<MyPoint>();
				todo.add(middle);
				while (!todo.isEmpty()) {
					MyPoint thePoint = todo.getFirst();
					todo.removeFirst();

					ListIterator<MyPoint> iter0 = addedPoints.listIterator();
					ListIterator<MyPoint> iter1 = impactPoints.listIterator();
					while (iter0.hasNext()) {
						MyPoint nextPoint = iter0.next();
						MyPoint nextAlter = iter1.next();
						if (nextPoint == thePoint) {
							thePoint.z = (3 * thePoint.z + nextAlter.z) / 4;
							todo.add(nextAlter);
						}
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
		if (theMesh == null)
			throw new DelaunayError(DelaunayError.DelaunayError_noMesh);
		else if (!theMesh.isMeshComputed())
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

					changeFlatTriangle(aTriangle, addedPoints, impactPoints);
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
		theMesh.setAllGids();
	}

	private void removeTriangle(MyTriangle aTriangle) {
		// get longest edge
		MyEdge longest = aTriangle.edges[0];
		double maxLength = longest.getStart().squareDistance_2D(
				longest.getEnd());
		for (int i = 1; i < 3; i++) {
			double length = aTriangle.edges[i].getStart().squareDistance_2D(
					aTriangle.edges[i].getEnd());
			if (length > maxLength) {
				maxLength = length;
				longest = aTriangle.edges[i];
			}
		}

		// remove it
		removeTriangle(aTriangle, longest);
	}

	private void removeTriangle(MyTriangle aTriangle, MyEdge removeEdge) {
		// save the two other edges
		int k = 0;
		MyEdge[] remain = new MyEdge[2];
		for (int i = 0; i < 3; i++) {
			if (aTriangle.edges[i] != removeEdge) {
				remain[k] = aTriangle.edges[i];
				k++;
			}
		}

		// Use the flip-flop algorithm on the longest edge
		int marked = removeEdge.marked;
		String type = removeEdge.type;
		MyTriangle aTriangle1 = removeEdge.getLeft();
		MyTriangle aTriangle2 = removeEdge.getRight();
		if ((aTriangle1 != null) && (aTriangle2 != null)) {
			// Flip-flop the two triangle - so keep the same number of triangle
			// but rearrange them
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
		if (marked > 0) {
			removeEdge.marked = 0;

			for (int i = 0; i < 2; i++) {
				if (remain[k].marked == 0)
					remain[k].marked = marked;
			}
		}

		if (type != null) {
			removeEdge.type = null;

			for (int i = 0; i < 2; i++) {
				if (remain[k].type == null)
					remain[k].type = type;
			}
		}
	}

	/**
	 * Intersect the edge that started at p1 and ended at p2 with the whole mesh
	 *
	 * @param p1
	 * @param p2
	 */
	private void processEdgeIntersection(ArrayList<MyEdge> compEdges) {
		// List of triangles that are created when there is an intersection
		MyTriangle TriangleList[] = new MyTriangle[4];

		ListIterator<MyEdge> iterEdge = compEdges.listIterator();
		while (iterEdge.hasNext()) {
			MyEdge CurrentEdge = iterEdge.next();

			MyPoint p1 = CurrentEdge.getStart();
			MyPoint p2 = CurrentEdge.getEnd();

			// Edges added during the process
			ArrayList<MyEdge> addedEdges = new ArrayList<MyEdge>();
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
				MyPoint start = anEdge.getStart();
				MyPoint end = anEdge.getEnd();

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
					saveEdge = null;
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
					IntersectionPoint1 = start;
					IntersectionPoint2 = end;
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
			if (false)
				while (intersect1.hasNext()) {
					MyEdge anEdge = intersect1.next();
					MyPoint IntersectionPoint = intersect2.next();

					if (anEdge != null) {
						MyPoint start = anEdge.getStart();
						MyPoint end = anEdge.getEnd();

						// if the intersection point is one of the start or end
						// points, do nothing
						if ((IntersectionPoint != start)
								&& (IntersectionPoint != end)) {
							// add the intersection point to the list of points
							// the order don't care
							points.add(IntersectionPoint);

							// split the edge at the intersection point
							MyEdge alterEdge = new MyEdge(anEdge);
							anEdge.point[1] = IntersectionPoint;
							alterEdge.point[0] = IntersectionPoint;
							addedEdges.add(alterEdge);

							// split the two triangles around that edge
							MyTriangle aTriangleLeft = anEdge.left;
							MyTriangle aTriangleRight = anEdge.right;

							// Do the same thing right and left
							for (int i = 0; i < 2; i++) {
								// Get base triangle
								MyTriangle aTriangle1 = null;
								if (i == 0)
									aTriangle1 = aTriangleLeft;
								else
									aTriangle1 = aTriangleRight;

								TriangleList[i * 2] = aTriangle1;
								TriangleList[i * 2 + 1] = null;

								if (aTriangle1 != null) {
									// There is a triangle => process that side

									// Get the point of the triangle that is
									// neither
									// start or end
									MyPoint alterPoint = null;
									for (int j = 0; j < 3; j++) {
										if ((aTriangle1.points[j] != start)
												&& (aTriangle1.points[j] != end))
											alterPoint = aTriangle1.points[j];
									}

									// if (!addedPoints.contains(alterPoint)) {
									// The alterPoint is not an intersection
									// point

									// Get alterEdge1 as the edge connected from
									// start to alterPoint
									MyEdge alterEdge1 = null;
									for (int j = 0; j < 3; j++) {
										MyEdge testEdge = aTriangle1.edges[j];
										if ((testEdge.getStart() == start)
												&& (testEdge.getEnd() == alterPoint))
											alterEdge1 = testEdge;
										else if ((testEdge.getStart() == alterPoint)
												&& (testEdge.getEnd() == start))
											alterEdge1 = testEdge;
									}

									// Get alterEdge2 as the edge connected from
									// end to alterPoint
									MyEdge alterEdge2 = null;
									for (int j = 0; j < 3; j++) {
										MyEdge testEdge = aTriangle1.edges[j];
										if ((testEdge.getStart() == end)
												&& (testEdge.getEnd() == alterPoint))
											alterEdge2 = testEdge;
										else if ((testEdge.getStart() == alterPoint)
												&& (testEdge.getEnd() == end))
											alterEdge2 = testEdge;
									}

									if (alterPoint == IntersectionPoint)
										System.out.println("ERREUR");
									else if ((alterEdge1 == null)
											|| (alterEdge2 == null))
										System.out.println("ERREUR");
									else {
										// Create an new edge from
										// IntersectionPoint
										// to alterPoint
										MyEdge newEdge = new MyEdge(
												IntersectionPoint, alterPoint);
										addedEdges.add(newEdge);
										possibleEdges.add(newEdge);

										// Creates a new triangle
										MyTriangle aTriangle2 = new MyTriangle();
										triangles.add(aTriangle2);
										TriangleList[i * 2 + 1] = aTriangle2;

										// Triangle 1 will be made of points
										// start, IntersectionPoint and
										// alterPoint
										aTriangle1.points[0] = start;
										aTriangle1.points[1] = IntersectionPoint;
										aTriangle1.points[2] = alterPoint;

										// Triangle 2 will be made of points
										// end, IntersectionPoint and alterPoint
										aTriangle2.points[0] = end;
										aTriangle2.points[1] = IntersectionPoint;
										aTriangle2.points[2] = alterPoint;

										// Triangle 1 will be connected to edges
										// anEdge, newEdge, alterEdge1
										aTriangle1.edges[0] = anEdge;
										aTriangle1.edges[1] = newEdge;
										aTriangle1.edges[2] = alterEdge1;

										// Triangle 2 will be made of
										// alterEdge, newEdge, alterEdge2
										aTriangle2.edges[0] = alterEdge;
										aTriangle2.edges[1] = newEdge;
										aTriangle2.edges[2] = alterEdge2;

										// Connect newEdge edges
										if (newEdge.isLeft(end)) {
											newEdge.left = aTriangle2;
											newEdge.right = aTriangle1;
										} else {
											newEdge.left = aTriangle1;
											newEdge.right = aTriangle2;
										}

										// Reconnect alterEdge
										if (alterEdge.left == aTriangle1)
											alterEdge.left = aTriangle2;
										else
											alterEdge.right = aTriangle2;

										// Reconnect alterEdge2
										if (alterEdge2.left == aTriangle1)
											alterEdge2.left = aTriangle2;
										else
											alterEdge2.right = aTriangle2;

										// Reset center and radius
										aTriangle1.recomputeCenter();
										aTriangle2.recomputeCenter();

										// add the newEdge to the queue
										badEdgesQueueList.add(newEdge);

										// add all edges
										for (int j = 0; j < 3; j++) {
											if (!badEdgesQueueList
													.contains(aTriangle1.edges[j]))
												badEdgesQueueList
														.add(aTriangle2.edges[j]);
											if (!badEdgesQueueList
													.contains(aTriangle2.edges[j]))
												badEdgesQueueList
														.add(aTriangle2.edges[j]);
										}
									}
									// }
								}
							}
						}
					}
				}

			// Then we mark all edges from p1 to p2
			int size = addedPoints.size();
			if (size > 2)
				MyTools.quickSort_Points(addedPoints);
			MyPoint LastPoint = null;
			for (MyPoint p : addedPoints) {
				MyEdge anEdge = checkTwoPointsEdge(p, LastPoint, possibleEdges);
				if (anEdge != null) {
					anEdge.marked = 1;
				}

				LastPoint = p;
			}

			// Add the edges that where created during the process
			for (MyEdge anEdge : addedEdges)
				edges.add(anEdge);
		}

		// Then apply the flip-flop algorithm
		processBadEdges();
	}

	/**
	 * Check if the current mesh triangularization is correct or not
	 *
	 * @return NbError
	 * @throws DelaunayError
	 */
	public void checkTriangularization() throws DelaunayError {
		if (theMesh == null)
			throw new DelaunayError(DelaunayError.DelaunayError_noMesh);
		else if (!theMesh.isMeshComputed())
			throw new DelaunayError(DelaunayError.DelaunayError_notGenerated);
		else {
			// First - check if every point belongs to at least one edge
			for (MyPoint aPoint : points) {
				boolean found = false;
				ListIterator<MyEdge> iterEdge = edges.listIterator();
				while ((iterEdge.hasNext()) && (!found)) {
					MyEdge anEdge = iterEdge.next();
					if (anEdge.getStart() == aPoint)
						found = true;
					else if (anEdge.getEnd() == aPoint)
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
			LinkedList<MyEdge> EdgeList) {
		// Check if the two points already lead to an existing edge.
		// If the edge exists it must be in the non-processed edges
		MyEdge theEdge = null;
		ListIterator<MyEdge> iter1 = EdgeList.listIterator();
		while (iter1.hasNext() && (theEdge == null)) {
			MyEdge anEdge = iter1.next();
			if (((anEdge.point[0] == p1) && (anEdge.point[1] == p2))
					|| ((anEdge.point[0] == p2) && (anEdge.point[1] == p1)))
				theEdge = anEdge;
		}
		return theEdge;
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
			if (((anEdge.point[0] == p1) && (anEdge.point[1] == p2))
					|| ((anEdge.point[0] == p2) && (anEdge.point[1] == p1)))
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
			if (((anEdge.point[0] == p1) && (anEdge.point[1] == p2))
					|| ((anEdge.point[0] == p2) && (anEdge.point[1] == p1)))
				theEdge = anEdge;
			else
				i++;
		}
		return theEdge;
	}

	/**
	 * Morphological qualification
	 *
	 * @throws DelaunayError
	 */
	public void morphologicalQualification() throws DelaunayError {

		if (theMesh == null)
			throw new DelaunayError(DelaunayError.DelaunayError_noMesh);
		else if (!theMesh.isMeshComputed())
			throw new DelaunayError(DelaunayError.DelaunayError_notGenerated);
		else {

			/**
			 * Edges : topographic qualifications
			 */

			for (MyEdge edge : theMesh.edges) {

				HydroLineUtil hydroLineUtil = new HydroLineUtil(edge);

				edge.setSlopeInDegree(hydroLineUtil.getSlopeInDegree());
				edge.setSlope(hydroLineUtil.get3DVector());
				HydroPolygonUtil hydroPolygonUtil = null;

				MyTriangle aTriangleLeft = edge.getLeft();

				MyTriangle aTriangleRight = edge.getRight();

				boolean rightTtoEdge = false;
				boolean rightTColinear = false;
				boolean righTFlat = false;
				boolean leftTtoEdge = false;
				boolean leftTColinear = false;
				boolean leftTFlat = false;
				boolean rightBorder = false;
				boolean leftBorder = false;

				// Qualification des triangles
				if (aTriangleRight != null) {

					hydroPolygonUtil = new HydroPolygonUtil(aTriangleRight);
					boolean pointeVersEdge = hydroPolygonUtil
							.getPenteVersEdge(edge);
					aTriangleRight.setSlopeInDegree(hydroPolygonUtil
							.getSlopeInDegree());
					aTriangleRight.setSlope(hydroPolygonUtil.get3DVector());

					if (pointeVersEdge) {
						rightTtoEdge = true;

					} else if (hydroPolygonUtil.getSlope() > 0) {
						if (MathUtil.IsColinear(hydroLineUtil.get3DVector(),
								hydroPolygonUtil.get3DVector())) {

							rightTColinear = true;
						}
					} else if (hydroPolygonUtil.getSlope() == 0) {

						righTFlat = true;
					}
				}

				else {
					rightBorder = true;
				}

				if (aTriangleLeft != null) {

					hydroPolygonUtil = new HydroPolygonUtil(aTriangleLeft);
					boolean pointeVersEdge = hydroPolygonUtil
							.getPenteVersEdge(edge);
					aTriangleLeft.setSlopeInDegree(hydroPolygonUtil
							.getSlopeInDegree());
					aTriangleLeft.setSlope(hydroPolygonUtil.get3DVector());

					if (pointeVersEdge) {

						leftTtoEdge = true;
					} else if (hydroPolygonUtil.getSlope() > 0) {
						if (MathUtil.IsColinear(hydroLineUtil.get3DVector(),
								hydroPolygonUtil.get3DVector())) {

							leftTColinear = true;
						}
					} else if (hydroPolygonUtil.getSlope() == 0) {

						leftTFlat = true;
					}

				} else {
					leftBorder = true;
				}

				// Recupération des noeuds associés à l'edge

				// Qualification de la pente de l'edge parcouru
				int edgeGradient = edge.getGradient();

				if (!leftBorder && !rightBorder) {

					// Traitement des ridges
					if ((!rightTtoEdge && !leftTtoEdge)
							&& (!righTFlat && !leftTFlat)) {

						edge.setTopoType(TopoType.RIDGE);

					}

					// Cas des talwegs
					else if (rightTtoEdge && leftTtoEdge) {

						edge.setTopoType(TopoType.TALWEG);
						edge.getStart().setTopoType(TopoType.TALWEG);
						edge.getEnd().setTopoType(TopoType.TALWEG);

					}

					// Le triangle de gauche pointe sur l'edge mais pas le
					// triangle de droite
					else if ((leftTtoEdge && !rightTtoEdge) && !righTFlat) {

						edge.setTopoType(TopoType.RIGHTSLOPE);

					}

					// Le triangle de droite pointe sur l'edge mais pas le
					// triangle de gauche
					else if ((rightTtoEdge && !leftTtoEdge) && (!leftTFlat)) {

						edge.setTopoType(TopoType.LEFTTSLOPE);

					}

					// Traitement du rebord droit
					else if ((!rightTtoEdge && !leftTtoEdge)
							&& (!leftTFlat && righTFlat)) {
						edge.setTopoType(TopoType.LEFTSIDE);
					}

					// Traitement du rebord gauche

					else if ((!leftTtoEdge && !rightTtoEdge)
							&& (!righTFlat && leftTFlat)) {
						edge.setTopoType(TopoType.RIGHTSIDE);
					}

					// Traitement du fond gauche
					else if ((rightTtoEdge && !leftTtoEdge)
							&& (leftTFlat && !righTFlat)) {

						edge.setTopoType(TopoType.LEFTWELL);
					}

					// Traitement du fond droit
					else if ((!rightTtoEdge && leftTtoEdge)
							&& (!leftTFlat && righTFlat)) {

						edge.setTopoType(TopoType.RIGHTWELL);
					}

					// Cas particulier des talwegs colineaires

					// Talweg colineaire gauche

					else if ((!leftTtoEdge && rightTtoEdge) && leftTColinear) {

						edge.setTopoType(TopoType.LEFTCOLINEAR);
						edge.getStart().setTopoType(TopoType.TALWEG);
						edge.getEnd().setTopoType(TopoType.TALWEG);

					}

					// Talweg colineaire droit

					else if ((leftTtoEdge && !rightTtoEdge) && rightTColinear) {

						edge.setTopoType(TopoType.RIGHTCOLINEAR);
						edge.getStart().setTopoType(TopoType.TALWEG);
						edge.getEnd().setTopoType(TopoType.TALWEG);

					}

					// Les deux triangles sont colineaires

					else if ((!leftTtoEdge && !rightTtoEdge)
							&& (rightTColinear && leftTColinear)) {

						edge.setTopoType(TopoType.DOUBLECOLINEAR);

						edge.getStart().setTopoType(TopoType.TALWEG);
						edge.getEnd().setTopoType(TopoType.TALWEG);

					}

					// Le reste est plat
					else {

						edge.setTopoType(TopoType.FLAT);

					}

				}

				// Traitement des bords plats
				else {
					edge.setTopoType(TopoType.BORDER);
				}

			}

		}
	}

	public void talwegBuilder() {

		/**
		 * The code below is used to insert new talweg in the TIN
		 */

		ArrayList<MyPoint> listPointAtraiter = new ArrayList<MyPoint>();
		ArrayList<MyTriangle> listTriangles = new ArrayList<MyTriangle>();

		for (MyEdge edge : theMesh.edges) {

			// Edge talweg
			if ((edge.getTopoType() != TopoType.TALWEG)
					|| (edge.getTopoType() != TopoType.LEFTCOLINEAR)
					|| (edge.getTopoType() != TopoType.RIGHTCOLINEAR)
					|| (edge.getTopoType() != TopoType.DOUBLECOLINEAR)) {

				MyPoint uperPoint = findUperPoint(edge);

				if (uperPoint.getTopoType() == TopoType.TALWEG) {
					if (!listPointAtraiter.contains(uperPoint)) {
						listPointAtraiter.add(uperPoint);

					}
				}
			}
		}

		theMesh.setAllGids();
	}

	public MyPoint findUperPoint(MyEdge edge) {

		MyPoint p1 = edge.getStart();
		MyPoint p2 = edge.getEnd();
		if (p1.z > p2.z) {
			return p1;
		} else if (p1.z > p2.z) {
			return p2;
		} else {
			return p1;
		}

	}

}
