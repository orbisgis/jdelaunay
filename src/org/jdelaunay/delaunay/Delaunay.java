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

			// Add the edges in the edges array
			processOtherEdges(theMesh.compEdges);

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
			aTriangle1.points[0] = aTriangle1.edges[0].start();
			aTriangle1.points[1] = aTriangle1.edges[0].end();
			aTriangle1.points[2] = aPoint;
			int k = 1;
			for (int i = 0; i < 3; i++) {
				if (anEdge[i] != null)
					if (aTriangle1.points[0] == anEdge[i].start()) {
						aTriangle1.edges[k] = anEdge[i];
						anEdge[i] = null;
						k++;
					} else if (aTriangle1.points[1] == anEdge[i].start()) {
						aTriangle1.edges[k] = anEdge[i];
						anEdge[i] = null;
						k++;
					}
			}
			triangles.add(aTriangle1);

			// Second triangle
			MyTriangle aTriangle2 = new MyTriangle();
			aTriangle2.edges[0] = aTriangle.edges[1];
			aTriangle2.points[0] = aTriangle2.edges[0].start();
			aTriangle2.points[1] = aTriangle2.edges[0].end();
			aTriangle2.points[2] = aPoint;
			k = 1;
			for (int i = 3; i < 6; i++) {
				if (anEdge[i] != null)
					if (aTriangle2.points[0] == anEdge[i].start()) {
						aTriangle2.edges[k] = anEdge[i];
						anEdge[i] = null;
						k++;
					} else if (aTriangle2.points[1] == anEdge[i].start()) {
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
			aTriangle.points[0] = aTriangle.edges[2].start();
			aTriangle.points[1] = aTriangle.edges[2].end();
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
		else if (anEdge.start().squareDistance(anEdge.end()) < tolarence)
			throw new DelaunayError(DelaunayError.DelaunayError_proximity);
		else if (!points.contains(anEdge.start()))
			throw new DelaunayError(DelaunayError.DelaunayError_pointNotFound);
		else if (!points.contains(anEdge.end()))
			throw new DelaunayError(DelaunayError.DelaunayError_pointNotFound);
		else {
			badEdgesQueueList = new LinkedList<MyEdge>();
			processEdgeIntersection(anEdge.start(), anEdge.end());
		}
	}

	/**
	 * Process a triangle with a too small area :
	 * - merge the three point as a new point and remove the three points
	 * - remove the three edges
	 * - process the neighbors
	 * 
	 * @param aTriangle
	 */
	private void processSmallAreaTriangle(MyTriangle aTriangle, LinkedList<MyTriangle> toBeRemoved) {
		double x, y, z;
		x = y = z = 0;
		for (int i = 0; i < 3; i++) {
			x += aTriangle.points[i].xy[0];
			y += aTriangle.points[i].xy[1];
			z += aTriangle.points[i].xy[2];
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
					aPoint = anEdge.start();
				else
					aPoint = anEdge.end();
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
	 * Process a triangle with a too large area :
	 * - add a new point in the middle
	 * - generate three triangles in place of the current one
	 *   That mean we generate two more triangles and we replace the current one
	 * Then we rebuild the delaunay triangularization
	 * 
	 * @param aTriangle
	 */
	private void processLargeAreaTriangle(MyTriangle aTriangle)
			throws DelaunayError {
		double x, y, z;
		x = y = z = 0;
		for (int i = 0; i < 3; i++) {
			x += aTriangle.points[i].xy[0];
			y += aTriangle.points[i].xy[1];
			z += aTriangle.points[i].xy[2];
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
	private void processSmallAngleTriangle(MyTriangle aTriangle, LinkedList<MyTriangle> toBeRemoved) {
		int badVertice = -1;
		double minDistance = -1;
		for (int i = 0; i < 3; i++) {
			MyEdge anEdge = aTriangle.edges[i];
			double dist = anEdge.start()
					.squareDistance_2D(anEdge.end());
			if ((badVertice == -1) || (dist < minDistance)) {
				minDistance = dist;
				badVertice = i;
			}
		}
	}

	/**
	 * Refine mesh according to the type of refinement that has been
	 * defined in the refinement variable
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
					
					while (! toBeRemoved.isEmpty()) {
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

					while (! toBeRemoved.isEmpty()) {
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
			MyPoint p1 = currentEdge.start();
			MyPoint p2 = currentEdge.end();
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
				p1 = anEdge.end();
				p2 = anEdge.start();

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

	/**
	 * Process the flip-flop algorithm on the list of triangles
	 */
	private void processBadEdges() {
		MyPoint p1, p2, p3, p4;
		while (!badEdgesQueueList.isEmpty()) {
			MyEdge anEdge = badEdgesQueueList.getFirst();
			badEdgesQueueList.removeFirst();

			if (anEdge.marked != 1) {
				// We cannot process marked edges
				p1 = anEdge.start();
				p2 = anEdge.end();
				// We check if the two triangles around the edge are ok
				if ((anEdge.leftTriangle() != null)
						&& (anEdge.rightTriangle() != null)) {
					MyTriangle aTriangle1 = anEdge.leftTriangle();
					MyTriangle aTriangle2 = anEdge.rightTriangle();

					boolean exchange = false;
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
					if (aTriangle2.inCircle(p3) == 1)
						exchange = true;

					// Triangle 2 is p2, p1, p4 or p1, p2, p4
					for (int j = 0; j < 3; j++)
						if ((aTriangle2.points[j] != p1)
								&& (aTriangle2.points[j] != p2)) {
							p4 = aTriangle2.points[j];
						}
					if (aTriangle1.inCircle(p4) == 1)
						exchange = true;

					if (exchange) {
						// We need to exchange points of the triangles

						// rebuild the two triangles
						MyEdge anEdge10, anEdge11, anEdge12;
						MyEdge anEdge20, anEdge21, anEdge22;

						// Triangle 1 is p1, p2, p3 or p2, p1, p3
						// Triangle 2 is p2, p1, p4 or p1, p2, p4
						anEdge10 = anEdge;
						anEdge11 = checkTwoPointsEdge(p3, p1, aTriangle1.edges,
								3);
						if (anEdge11 == null)
							anEdge10 = anEdge;
						anEdge12 = checkTwoPointsEdge(p1, p4, aTriangle2.edges,
								3);
						if (anEdge12 == null)
							anEdge10 = anEdge;

						anEdge20 = anEdge;
						anEdge21 = checkTwoPointsEdge(p2, p4, aTriangle2.edges,
								3);
						if (anEdge21 == null)
							anEdge10 = anEdge;
						anEdge22 = checkTwoPointsEdge(p3, p2, aTriangle1.edges,
								3);
						if (anEdge22 == null)
							anEdge10 = anEdge;

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

						// Reconnect all edges
						aTriangle1.reconnectEdges();
						aTriangle2.reconnectEdges();

						// do not forget to recompute circles
						aTriangle1.recomputeCenter();
						aTriangle2.recomputeCenter();

						// checkTriangle();

						// and add the edges to the bad edges list
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
		LinkedList<MyEdge> addedEdges = new LinkedList<MyEdge>();
		// Intersection points - this is an ArrayList because we need to sort it
		ArrayList<MyPoint> addedPoints = new ArrayList<MyPoint>();
		// Edges that can participate to p1 p2
		LinkedList<MyEdge> possibleEdges = new LinkedList<MyEdge>();

		// Intersect p1-p2 with all edges
		for (MyEdge anEdge : edges) {
			MyPoint start = anEdge.start();
			MyPoint end = anEdge.end();

			switch (anEdge.intersects(p1, p2)) {
			case 0:
				// No intersection => don't care
				break;
			case 1:
				// There is an intersection point
				MyPoint IntersectionPoint = anEdge.getIntersection(p1, p2);
				if (IntersectionPoint != null) {
					addedPoints.add(IntersectionPoint);

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
						MyTriangle aTriangle1;
						for (int i = 0; i < 2; i++) {
							if (i == 0)
								aTriangle1 = anEdge.left;
							else
								aTriangle1 = anEdge.right;
							TriangleList[i * 2] = aTriangle1;
							TriangleList[i * 2 + 1] = null;

							if (aTriangle1 != null) {
								// Find the point of the triangle that is not
								// start or end of the edge
								MyPoint alterPoint = null;
								int j = 0;
								while ((j < 3) && (alterPoint == null)) {
									MyPoint aPoint = aTriangle1.points[j];
									if ((aPoint != start) && (aPoint != end))
										alterPoint = aPoint;
									else
										j++;
								}

								// Create the new edge that split the triangle
								MyEdge newEdge = new MyEdge(IntersectionPoint,
										alterPoint);
								addedEdges.add(newEdge);
								possibleEdges.add(newEdge);

								// Create a new Triangle
								MyTriangle aTriangle2 = new MyTriangle(
										aTriangle1);
								triangles.add(aTriangle2);
								TriangleList[i * 2 + 1] = aTriangle2;

								// Change the points
								for (j = 0; j < 3; j++) {
									if (aTriangle1.points[j] == end)
										aTriangle1.points[j] = IntersectionPoint;
									if (aTriangle2.points[j] == start)
										aTriangle2.points[j] = IntersectionPoint;
								}

								// reconnect edges
								// change the edge of triangle 2
								for (j = 0; j < 3; j++) {
									if (aTriangle2.edges[j] == anEdge)
										aTriangle2.edges[j] = alterEdge;
								}
								for (j = 0; j < 3; j++) {
									// change for the common edge
									if (((aTriangle1.edges[j].start() == end) && (aTriangle1.edges[j]
											.end() == alterPoint))
											|| ((aTriangle1.edges[j].start() == alterPoint) && (aTriangle1.edges[j]
													.end() == end)))
										aTriangle1.edges[j] = newEdge;
									if (((aTriangle2.edges[j].start() == start) && (aTriangle2.edges[j]
											.end() == alterPoint))
											|| ((aTriangle2.edges[j].start() == alterPoint) && (aTriangle2.edges[j]
													.end() == start)))
										aTriangle2.edges[j] = newEdge;
								}

								aTriangle1.recomputeCenter();
								aTriangle2.recomputeCenter();

								// add the newEdge to the queue
								badEdgesQueueList.add(newEdge);

								// add all edges except alterEdge and anEdge
								for (j = 0; j < 3; j++) {
									if (aTriangle1.edges[j] != anEdge)
										if (!badEdgesQueueList
												.contains(aTriangle1.edges[j]))
											badEdgesQueueList
													.add(aTriangle2.edges[j]);
									if (aTriangle2.edges[j] != alterEdge)
										if (!badEdgesQueueList
												.contains(aTriangle2.edges[j]))
											badEdgesQueueList
													.add(aTriangle2.edges[j]);
								}

							}
						}

						// Rebuild connections
						for (int i = 0; i < 4; i++) {
							MyTriangle aTriangle = TriangleList[i];
							if (aTriangle != null) {
								aTriangle.reconnectEdges();
							}
						}

						// add the split edge and the alter edge
						badEdgesQueueList.add(alterEdge);
						if (!badEdgesQueueList.contains(anEdge))
							badEdgesQueueList.add(anEdge);

					}
				}
				break;
			case 2:
				// points are on the same line and intersects

				// p1 and p2 cannot be inside the edge because they participate
				// to the mesh
				// so, start and end MUST be inside p1-p2
				addedPoints.add(start);
				addedPoints.add(end);
				possibleEdges.add(anEdge);
				break;
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
					if (anEdge.start() == aPoint)
						found = true;
					else if (anEdge.end() == aPoint)
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
