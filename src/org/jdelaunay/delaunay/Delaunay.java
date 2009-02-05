package org.jdelaunay.delaunay;

/**
 * Delaunay Package.
 * 
 * @author Jean-Yves MARTIN
 * @date 2009-01-12
 * @version 1.0
 */

import java.util.*;

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

	// constants
	public static final int refinement_minArea = 1;
	public static final int refinement_maxArea = 2;
	public static final int refinement_minAngle = 4;
	public static final int maxIter = 5;

	// Mesh data access
	private ArrayList<MyPoint> points;
	private LinkedList<MyEdge> edges;
	private LinkedList<MyTriangle> triangles;

	// Working index vector
	private LinkedList<MyEdge> badEdgesQueueList;
	private LinkedList<MyEdge> boundaryEdges;
	private boolean meshComputed;

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
		meshComputed = false;
	}

	/**
	 * Generate empty Delaunay Structure.
	 */
	public Delaunay() {
		init();
	}

	/**
	 * Generate empty Delaunay Structure for a Mesh.
	 */
	public Delaunay(MyMesh aMesh) {
		init();
		theMesh = aMesh;
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
	 * Generate the Delaunay triangularization with a flip-flop algorithm. Mesh
	 * must have been set. Triangularization can only be done once. Otherwise
	 * call reprocessDelaunay
	 * 
	 * @throws DelaunayError
	 */
	public void processDelaunay() throws DelaunayError {
		if (theMesh == null)
			throw new DelaunayError(DelaunayError.DelaunayError_noMesh);
		else if (meshComputed)
			throw new DelaunayError(DelaunayError.DelaunayError_Generated);
		else if (theMesh.getNbPoints() < 3)
			throw new DelaunayError(
					DelaunayError.DelaunayError_notEnoughPointsFound);
		else {
			// general data structures
			badEdgesQueueList = new LinkedList<MyEdge>();
			boundaryEdges = new LinkedList<MyEdge>();

			// sort points
			sortAndSimplify();

			// we build a first triangle with the 3 first points we find
			MyTriangle aTriangle;
			MyPoint p1, p2, p3;
			MyEdge e1, e2, e3;
			p1 = p2 = p3 = null;

			ListIterator<MyPoint> iterPoint = points.listIterator();
			p1 = iterPoint.next();
			p2 = iterPoint.next();
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
				InsertPoint(aPoint);
			}

			// remove flat triangles
			removeFlatTriangles();

			// Add the edges in the edges array
			processOtherEdges(theMesh.compEdges);
			removeFlatTriangles();

			// It's fine, we computed the mesh
			meshComputed = true;
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
			edges = new LinkedList<MyEdge>();
			triangles = new LinkedList<MyTriangle>();
			meshComputed = false;

			// Restart the process
			processDelaunay();
		}
	}

	/**
	 * Add a point in the mesh and rebuild triangularization
	 * 
	 * @param aPoint
	 * @throws DelaunayError
	 */
	public void addPoint(MyPoint aPoint) throws DelaunayError {
		if (theMesh == null)
			throw new DelaunayError(DelaunayError.DelaunayError_noMesh);
		else {
			// First we check if the point is in the points list
			boolean pointAlreadyExists = points.contains(aPoint);

			if (!pointAlreadyExists) {
				// First we find the point's location.
				MyTriangle foundTriangle = null;
				ListIterator<MyTriangle> iterTriangle = triangles
						.listIterator();
				while ((iterTriangle.hasNext()) && (foundTriangle == null)) {
					MyTriangle aTriangle = iterTriangle.next();
					if (aTriangle.isInside(aPoint)) {
						foundTriangle = aTriangle;
					}
				}

				if (foundTriangle != null) {
					// the point is inside the foundTriangle triangle
					addPoint(foundTriangle, aPoint);

				} else {
					// the point is outside the mesh
					// The boundary edge list is ok
					// We insert the point in the mesh
					InsertPoint(aPoint);
				}
			}
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
			MyEdge anEdge[] = new MyEdge[6];
			for (int i = 0; i < 3; i++) {
				anEdge[i] = new MyEdge(aTriangle.points[i], aPoint);
				anEdge[i + 3] = anEdge[i];
				edges.add(anEdge[i]);
			}

			// We build a triangle with each edge

			// build first triangle
			MyTriangle aTriangle1 = new MyTriangle();
			aTriangle1.edges[0] = aTriangle.edges[0];
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
			for (int i = 0; i < 3; i++) {
				if (!badEdgesQueueList.contains(aTriangle.edges[i]))
					badEdgesQueueList.add(aTriangle.edges[i]);
				if (!badEdgesQueueList.contains(aTriangle1.edges[i]))
					badEdgesQueueList.add(aTriangle1.edges[i]);
				if (!badEdgesQueueList.contains(aTriangle2.edges[i]))
					badEdgesQueueList.add(aTriangle2.edges[i]);
			}

			// Process badTriangleQueueList
			processBadEdges();
		}

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
		else if (!meshComputed)
			throw new DelaunayError(DelaunayError.DelaunayError_notGenerated);
		else if (p1.squareDistance(p2) < tolarence)
			throw new DelaunayError(DelaunayError.DelaunayError_proximity);
		else if (!points.contains(p1))
			throw new DelaunayError(DelaunayError.DelaunayError_pointNotFound);
		else if (!points.contains(p2))
			throw new DelaunayError(DelaunayError.DelaunayError_pointNotFound);
		else {
			badEdgesQueueList = new LinkedList<MyEdge>();
			processEdgeIntersection(p1, p2);
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
		else if (!meshComputed)
			throw new DelaunayError(DelaunayError.DelaunayError_notGenerated);
		else if (anEdge.getStart().squareDistance(anEdge.getEnd()) < tolarence)
			throw new DelaunayError(DelaunayError.DelaunayError_proximity);
		else if (!points.contains(anEdge.getStart()))
			throw new DelaunayError(DelaunayError.DelaunayError_pointNotFound);
		else if (!points.contains(anEdge.getEnd()))
			throw new DelaunayError(DelaunayError.DelaunayError_pointNotFound);
		else {
			badEdgesQueueList = new LinkedList<MyEdge>();
			processEdgeIntersection(anEdge.getStart(), anEdge.getEnd());
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
		else if (!meshComputed)
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
	private void processOtherEdges(LinkedList<MyEdge> compEdges) {
		MyEdge currentEdge;
		// While there is still an edge to process
		ListIterator<MyEdge> iterEdge = compEdges.listIterator();
		while (iterEdge.hasNext()) {
			// Get first edge then remove it from the list
			currentEdge = iterEdge.next();

			// Compute edge intersection with the Mesh
			MyPoint p1 = currentEdge.getStart();
			MyPoint p2 = currentEdge.getEnd();

			processEdgeIntersection(p1, p2);
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
			quickSort(points, 0, NbPoints - 1);

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

		// Then remove points from the list
		for (MyPoint aPoint : Replace.keySet()) {
			points.remove(aPoint);
		}

	}

	/**
	 * Quick sort on points Ordered according to x and y
	 * 
	 * @param min_index
	 * @param max_index
	 */
	private void quickSort(ArrayList<MyPoint> points, int min_index,
			int max_index) {
		int i, j;
		int enreg_ref;
		double cle_ref1, cle_ref2;
		boolean found;

		i = min_index;
		j = max_index;
		enreg_ref = (max_index + min_index) / 2;
		cle_ref1 = points.get(enreg_ref).getX();
		cle_ref2 = points.get(enreg_ref).getY();
		do {
			// first : increasing index
			found = false;
			while (!found) {
				if (i > max_index)
					found = true;
				else if (points.get(i).getX() > cle_ref1)
					found = true;
				else if ((points.get(i).getX() == cle_ref1)
						&& (points.get(i).getY() >= cle_ref2))
					found = true;
				else
					i++;
			}

			// second : decreasing index
			found = false;
			while (!found) {
				if (min_index > j)
					found = true;
				else if (points.get(j).getX() < cle_ref1)
					found = true;
				else if ((points.get(j).getX() == cle_ref1)
						&& (points.get(j).getY() <= cle_ref2))
					found = true;
				else
					j--;
			}

			// exchange values
			if (i <= j) {
				// we can change values
				MyPoint aPoint = points.get(i);
				points.set(i, points.get(j));
				points.set(j, aPoint);

				i++;
				j--;
			}
		} while (i <= j);

		// Recurrent calls
		if (min_index < j) {
			// if left side is not empty
			quickSort(points, min_index, j);
		}
		if (max_index > i) {
			// if right side is not empty
			quickSort(points, i, max_index);
		}
	}

	/**
	 * Insert o point to the current triangularization
	 * 
	 * @param aPoint
	 */
	private void InsertPoint(MyPoint aPoint) {
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

				// Mark the edge to be removed
				oldEdges.add(anEdge);

				// add the edges to the bad edges list
				if (!badEdgesQueueList.contains(anEdge))
					badEdgesQueueList.add(anEdge);
				if (!badEdgesQueueList.contains(anEdge1))
					badEdgesQueueList.add(anEdge1);
				if (!badEdgesQueueList.contains(anEdge2))
					badEdgesQueueList.add(anEdge2);
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
						|| (anEdge21 == null) || (anEdge22 == null))
					System.out.println("ERREUR");

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
		return exchange;
	}

	/**
	 * Process the flip-flop algorithm on the list of triangles
	 */
	private void processBadEdges() {
		while (!badEdgesQueueList.isEmpty()) {
			MyEdge anEdge = badEdgesQueueList.getFirst();
			badEdgesQueueList.removeFirst();

			if (anEdge.marked != 1) {
				// We cannot process marked edges
				// We check if the two triangles around the edge are ok
				MyTriangle aTriangle1 = anEdge.getLeft();
				MyTriangle aTriangle2 = anEdge.getRight();
				if ((aTriangle1 != null)
						&& (aTriangle2 != null)) {

					if (swapTriangle(aTriangle1, aTriangle2, anEdge, false)) {
						// Add the edges to the bad edges list
						for (int j = 0; j < 3; j++) {
							if (aTriangle1.edges[j] != anEdge)
								if (!badEdgesQueueList
										.contains(aTriangle1.edges[j]))
									badEdgesQueueList.add(aTriangle1.edges[j]);
							if (aTriangle2.edges[j] != anEdge)
								if (!badEdgesQueueList
										.contains(aTriangle2.edges[j]))
									badEdgesQueueList.add(aTriangle2.edges[j]);
						}
					}
				}
			}
		}
	}

	public void checkTopology() {
		for (MyTriangle aTestTriangle : triangles)
			aTestTriangle.checkTopology();
	}

	private void removeFlatTriangles() {
		// Check triangles to be removed
		ListIterator<MyTriangle> iterTriangle = triangles.listIterator();
		LinkedList<MyTriangle> badTrianglesList = new LinkedList<MyTriangle>();
		while (iterTriangle.hasNext()) {
			MyTriangle aTriangle = iterTriangle.next();
			if (aTriangle.isFlatTriangle())
				badTrianglesList.add(aTriangle);
		}

		// Remove all bas triangles
		while (!badTrianglesList.isEmpty()) {
			MyTriangle aTriangle = badTrianglesList.getFirst();
			badTrianglesList.removeFirst();

			// Remove it
			removeTriangle(aTriangle);
		}
	}

	private void removeTriangle(MyTriangle aTriangle) {
		// get longest edge
		MyEdge longest = aTriangle.edges[0];
		double maxLength = longest.getStart().squareDistance_2D(longest.getEnd());
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
	private void processEdgeIntersection(MyPoint p1, MyPoint p2) {
		// List of triangles that are created when there is an intersection
		MyTriangle TriangleList[] = new MyTriangle[4];
		// Edges added during the process
		ArrayList<MyEdge> addedEdges = new ArrayList<MyEdge>();
		// Intersection points - this is an ArrayList because we need to sort it
		ArrayList<MyPoint> addedPoints = new ArrayList<MyPoint>();
		ArrayList<MyEdge> IntersectedEdges = new ArrayList<MyEdge>();
		// Edges that can participate to p1 p2
		ArrayList<MyEdge> possibleEdges = new ArrayList<MyEdge>();

		// First we get all intersection points
		// We need then because we have to compare alterPoint with this list of
		// points
		for (MyEdge anEdge : edges) {
			MyPoint start = anEdge.getStart();
			MyPoint end = anEdge.getEnd();

			switch (anEdge.intersects(p1, p2)) {
			case 0:
				// No intersection => don't care
				break;
			case 1:
				// There is an intersection point
				MyPoint IntersectionPoint = anEdge.getIntersection(p1, p2);
				if (IntersectionPoint != null) {
					if (!addedPoints.contains(IntersectionPoint)) {
						addedPoints.add(IntersectionPoint);
						IntersectedEdges.add(anEdge);
					}
				}
				break;
			case 2:
				// points are on the same line and intersects

				// p1 and p2 cannot be inside the edge because they participate
				// to the mesh
				// so, start and end MUST be inside p1-p2
				if (!addedPoints.contains(start)) {
					addedPoints.add(start);
					IntersectedEdges.add(anEdge);
				}
				if (!addedPoints.contains(end)) {
					addedPoints.add(end);
					IntersectedEdges.add(anEdge);
				}
				possibleEdges.add(anEdge);
				break;
			}
		}

		// Intersect p1-p2 with all found edges
		ListIterator<MyEdge> intersect1 = IntersectedEdges.listIterator();
		ListIterator<MyPoint> intersect2 = addedPoints.listIterator();
		while (intersect1.hasNext()) {
			MyEdge anEdge = intersect1.next();
			MyPoint IntersectionPoint = intersect2.next();

			if (! points.contains(IntersectionPoint))
			if (anEdge != null) {
				MyPoint start = anEdge.getStart();
				MyPoint end = anEdge.getEnd();

				// if the intersection point is one of the start or end
				// points, do nothing
				if ((IntersectionPoint != start) && (IntersectionPoint != end)) {
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

							// Get the point of the triangle that is neither
							// start or end
							MyPoint alterPoint = null;
							for (int j = 0; j < 3; j++) {
								if ((aTriangle1.points[j] != start)
										&& (aTriangle1.points[j] != end))
									alterPoint = aTriangle1.points[j];
							}

//							if (!addedPoints.contains(alterPoint)) {
								// The alterPoint is not an intersection point

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
									// Create an new edge from IntersectionPoint
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
									// start, IntersectionPoint and alterPoint
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
//							}
						}
					}
				}
			}
		}

		// Then we mark all edges from p1 to p2
		int size = addedPoints.size();
		quickSort(addedPoints, 0, size - 1);
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
		else if (!meshComputed)
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
}
