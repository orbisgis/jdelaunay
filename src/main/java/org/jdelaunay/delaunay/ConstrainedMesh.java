package org.jdelaunay.delaunay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author alexis
 */
public class ConstrainedMesh {

	//The list of triangles during the triangulation process.
	//This list is sorted by using the implementation of Comparable in
	//DelaunayTriangle.
	private List<DelaunayTriangle> triangleList;
	//The list of edges.
	private List<Edge> edges;
	//The list of points used during the triangulation
	private List<Point> points;
	//The lis of constraints used during the triangulation
	private List<Edge> constraintEdges;
	//A list of polygons that will be emptied after the triangulation
	protected List<ConstraintPolygon> polygons;
	//
	private double precision;
	//The minimum distance between two distinct points
	private double tolerance;
	//The two following lists are used only during computation.
	//The bad edge queue list contains all the edges that coud be changed
	//during a flip-flap operation
	private LinkedList<Edge> badEdgesQueueList;
	//boundaryEdges contains the Envelope of the CURRENT geometry.
	private List<Edge> boundaryEdges;
	//Permits to know if the mesh has been computed or not
	private boolean meshComputed;
	//Is the debug level used ?
	private boolean verbose;

	// GIDs
	private int pointGID;
	private int edgeGID;
	private int triangleGID;
	// constants
	public static final double EPSILON = 0.00001;
	public static final int MAXITER = 5;
	public static final int REFINEMENT_MAX_AREA = 1;
	public static final int REFINEMENT_MIN_ANGLE = 2;
	public static final int REFINEMENT_SOFT_INTERPOLATE = 4;
	public static final int REFINEMENT_OBTUSE_ANGLE = 8;

	public ConstrainedMesh() {
		triangleList = new ArrayList<DelaunayTriangle>();
		edges = new ArrayList<Edge>();
		constraintEdges = new ArrayList<Edge>();
		points = new ArrayList<Point>();
		polygons = new ArrayList<ConstraintPolygon>();
		meshComputed = false;
		precision = 0;
		tolerance = 0.00001;

		pointGID = 0;
		edgeGID = 0;
		triangleGID = 0;


		badEdgesQueueList = new LinkedList<Edge>();
		boundaryEdges = new ArrayList<Edge>();
	}

	/**
	 * Get the list of edges that are to be processed by the flip flap algorithm
	 * @return
	 */
	public List<Edge> getBadEdgesQueueList() {
		return badEdgesQueueList;
	}

	/**
	 * Set the list of edges that are to be processed by the flip flap algorithm
	 * @param badEdgesQueueList
	 */
	public void setBadEdgesQueueList(LinkedList<Edge> badEdgesQueueList) {
		this.badEdgesQueueList = badEdgesQueueList;
	}

	/**
	 * Get the list of edges that form the current convex hull of the triangulation
	 * @return
	 */
	public List<Edge> getBoundaryEdges() {
		return boundaryEdges;
	}

	/**
	 * Set the list of edges that form the current convex hull of the triangulation
	 * @param boundaryEdges
	 */
	public void setBoundaryEdges(ArrayList<Edge> boundaryEdges) {
		this.boundaryEdges = boundaryEdges;
	}

	/**
	 * Get the list of edges that are used as constraints during triangulation
	 * @return
	 */
	public List<Edge> getConstraintEdges() {
		return constraintEdges;
	}

	/**
	 * Set the list of edges that are used as constraints during triangulation
	 * As we can't be sure the constraintEdges is already sorted, we sort it first
	 * and add all the corresponding points to the point list.
	 * @param constraintEdges
	 */
	public void setConstraintEdges(ArrayList<Edge> constraint) {
		this.constraintEdges = new ArrayList<Edge>();
		for (Edge e : constraint) {
			addPoint(e.getStart());
			addPoint(e.getEnd());
			e.setLocked(true);
			addConstraintEdge(e);
		}
	}

	/**
	 * Add an edge to the list of constraint edges.
	 * @param e
	 *	the edge we want to add
	 */
	public void addConstraintEdge(Edge e) {
		if (constraintEdges == null) {
			constraintEdges = new ArrayList<Edge>();
		}
		e.setLocked(true);
		addEdgeToLeftSortedList(constraintEdges, e);
		addPoint(e.getStart());
		addPoint(e.getEnd());
	}

	/**
	 * Get the list of edges
	 * @return
	 */
	public List<Edge> getEdges() {
		return edges;
	}

	/**
	 * Set the list of edges
	 * @param constraintEdges
	 */
	public void setEdges(ArrayList<Edge> constraint) {
		this.edges = new ArrayList<Edge>();
		for (Edge e : constraint) {
			addPoint(e.getStart());
			addPoint(e.getEnd());
			addEdge(e);
		}
	}

	/**
	 * Add an edge to the list of edges.
	 * @param e
	 *	the edge we want to add
	 */
	public void addEdge(Edge e) {
		if (edges == null) {
			edges = new ArrayList<Edge>();
		}
		addEdgeToLeftSortedList(edges, e);
		addPoint(e.getStart());
		addPoint(e.getEnd());
	}

	/**
	 * This method will sort the edges using the coordinates of the left point
	 * of the edges.
	 * @return
	 */
	public List<Edge> sortEdgesLeft(List<Edge> inputList) {
		ArrayList<Edge> outputList = new ArrayList<Edge>();
		for (Edge e : inputList) {
			addEdgeToLeftSortedList(outputList, e);
		}
		return outputList;
	}

	/**
	 * This method will insert an edge in an already sorted list, as described
	 * in sortEdgesLeft.
	 * The sorted list is not checked here, so be careful when using this method !
	 * If an edge is already present in the list, it is not added.
	 *
	 * if two edges have the same left point, they are sorted using the other one.
	 * @param sorted
	 * @param edge
	 */
	private void addEdgeToLeftSortedList(List<Edge> sorted, Edge edge) {
		if (sorted.isEmpty()) {
			sorted.add(edge);
			return;
		}
		int c;
		int s = sorted.size();
		c = edge.sortLeftRight(sorted.get(0));
		if (c < 1) {
			//left is on the left of the first edge in the list, we put it there
			if (c == -1) {
				sorted.add(0, edge);
			}
			return;
		}
		c = edge.sortLeftRight(sorted.get(s - 1));
		if (c > -1) {
			//left is on the right of the leftmost edge of the last element.
			if (c == 1) {
				sorted.add(edge);
			}
			return;
		}
		int i = s / 2;
		int delta = s / 2;
		boolean next = true;
		Edge other;
		while (next) {
			other = sorted.get(i);
			c = edge.sortLeftRight(other);
			switch (c) {
				case -1:
					other = sorted.get(i - 1);
					c = edge.sortLeftRight(other);
					switch (c) {
						case -1:
							delta = (delta / 2 > 0 ? delta / 2 : 1);
							i = i - delta;
							break;
						case 0:
							return;
						case 1:
							sorted.add(i, edge);
							return;
					}
					break;
				case 0:
					return;
				case 1:
					other = sorted.get(i + 1);
					c = edge.sortLeftRight(other);
					switch (c) {
						case -1:
							sorted.add(i + 1, edge);
							return;
						case 0:
							return;
						default:
							delta = (delta / 2 > 0 ? delta / 2 : 1);
							i = i + delta;
							break;
					}
			}
		}
	}

	/**
	 * Search an edge in the list of edges.
	 * @param edge
	 * @return
	 */
	public int searchEdge(Edge edge){
		return 0;
	}

	/**
	 * Get the precision
	 * @return
	 */
	public double getPrecision() {
		return precision;
	}

	/**
	 * Set the precision
	 * @param precision
	 */
	public void setPrecision(double precision) {
		this.precision = precision;
	}

	/**
	 * Get the value used to compute the minimum distance between two points
	 * @return
	 */
	public double getTolerance() {
		return tolerance;
	}

	/**
	 * Set the value used to compute the minimum distance between two points
	 * @param tolerance
	 */
	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}

	/**
	 * Get the list of triangles already computed and added in this mesh
	 * @return
	 */
	public List<DelaunayTriangle> getTriangleList() {
		return triangleList;
	}

	/**y
	 * Set the list of triangles already computed in this mesh.
	 * @param triangleList
	 */
	public void setTriangleList(List<DelaunayTriangle> triangleList) {
		Collections.sort(triangleList);
		this.triangleList = triangleList;
	}

	public void addTriangle(DelaunayTriangle triangle){
		addToSortedList(triangle, triangleList);
	}

	/**
	 * Get the points contained in this mesh
	 * @return
	 */
	public List<Point> getPoints() {
		return points;
	}

	/**
	 * Can be used to know if the mesh has been computed or not
	 * @return
	 */
	public boolean isMeshComputed(){
		return meshComputed;
	}

	/**
	 * Change the status of this mesh computation
	 * @param comp
	 */
	public void setMeshComputed(boolean comp){
		meshComputed = comp;
	}

	/**
	 * Says if the verbose mode is activated or not
	 * @return
	 */
	public boolean isVerbose(){
		return verbose;
	}

	/**
	 * Set the verbosity level
	 * @param verb
	 */
	public void setVerbose(boolean verb){
		verbose = verb;
	}
	/**
	 * Set the list of points to be used during the triangulation
	 * @param points
	 */
	public void setPoints(ArrayList<Point> points) {
		this.points = points;
	}

	/**
	 * Add a new point in the list that will be used to perform the triangulation.
	 * The list of points is supposed to be sorted.
	 * @param point
	 */
	public void addPoint(Point point) {
		addToSortedList(point, points);
	}

	/**
	 *
	 * @param <T extends Element & Comparable<? super T>>
	 * @param point
	 * @param sortedList
	 */
	private <T extends Element & Comparable<? super T>> void addToSortedList(T point, List<T> sortedList){
		int s = sortedList.size();
		if (s == 0) {
			sortedList.add(point);
		} else {
			int p = s / 2;
			int delta = s / 2;
			int ret = -1;
			int c;
			//If the point is inferior to the first element of te list, we place it at the beginning
			if (point.compareTo(sortedList.get(0)) == -1) {
				p = 0;
				ret = 0;
				//If the point is superior to the last element of the list, we place it at the end
			} else if (point.compareTo(sortedList.get(s - 1)) == 1) {
				p = s;
				ret = s;
			}
			while (ret != p) {
				delta = (delta / 2 > 0 ? delta / 2 : 1);
				c = point.compareTo(sortedList.get(p));
				switch (c) {
					case -1:
						//point < points.get(p)
						//We must move left
						c = point.compareTo(sortedList.get(p - 1));
						switch (c) {
							case -1:
								p = p - delta;
								break;
							case 0:
								p = -1;
								break;
							default:
								ret = p;
						}
						break;
					case 0:
						p = -1;
						break;
					default:
						p = p + delta;
				}
			}
			if (p != -1) {
				sortedList.add(p, point);
			}
		}
	}

	/**
	 * This methods will search the point p in the list.
	 * @param p
	 * @return the index of p, -1 if it's not in the list
	 */
	public int listContainsPoint(Point p) {
		int s = points.size();
		int c = p.compareTo2D(points.get(0));
		if (c == -1) {	//p<first, p is not in the sorted list
			return -1;
		} else if (c == 0) {
			return 0;
		}
		c = p.compareTo2D(points.get(s - 1));
		if (c == 1) {	//p>last, p is not in the sorted list
			return -1;
		} else if (c == 0) {
			return points.size() - 1;
		}
		//p is ppotentially in the list, and is not one of the extremities.
		int delta = points.size() / 2;
		int i = points.size() / 2;
		while (delta > 0) {
			c = p.compareTo2D(points.get(i));
			switch (c) {
				case -1://p is on the left of points(i)...
					if (i == 0) {
						return -1;
					}
					c = p.compareTo2D(points.get(i - 1));
					switch (c) {
						case 1://...and on the right of points(i-1), so it's no in the list
							return -1;
						case 0:
							return i - 1;
						default://...and on the left of points(i-1), we continue
							delta = delta / 2;
							i = i - delta;
					}
					break;
				case 0:
					return i;
				case 1://p is on the right of points(i)
					if (i == points.size() - 1) {
						return -1;
					}
					c = p.compareTo2D(points.get(i + 1));
					switch (c) {
						case -1://...and on the left of points(i-1), so it's no in the list
							return -1;
						case 0:
							return i + 1;
						default://...and on the right of points(i-1), we continue
							delta = delta / 2;
							i = i + delta;
					}

			}
		}
		return -1;
	}

	/**
	 * This method will force the integrity of the constraints used to compute
	 * the delaunay triangulation. After execution :
	 *  * duplicates are removed
	 *  * intersection points are added to the mesh points
	 *  * secant edges are split
	 */
	public void forceConstraintIntegrity() throws DelaunayError {
		//The event points are the extremities and intersections of the
		//constraint edges. This list is created empty, and filled to stay
		//sorted.
		ArrayList<Point> eventPoints = new ArrayList<Point>();
		//We fill the list.
		for (Edge edge : constraintEdges) {
			addToSortedList(edge.getStart(), eventPoints);
			addToSortedList(edge.getEnd(), eventPoints);
		}
		//we are about to perform the sweepline algorithm
		Point currentEvent = null;
		//edgeBuffer will contain the edges sorted vertically
		ArrayList<Edge> edgeBuffer = new ArrayList<Edge>();
		//We keep a shallow copy of constraintEdges...
		List<Edge> edgeMemory = constraintEdges;
		//...and we empty it
		constraintEdges = new ArrayList<Edge>();
		//The absciss where we search the intersections
		double abs;
		//Used in the  loop...
		int i = 0;//The first while
		int j = 0;//the inner while
		Edge e1, e2; //the edges that will be compared in the for loop
		Edge inter1 = null;// the edges resulting of the intersection.
		Edge inter2 = null;
		Edge inter3 = null;
		Edge inter4 = null;
		Point newEvent = null;//the event that will be added to the eventList
		Edge edgeEvent = null;//used when the intersection is an edge
		Point leftMost = null;
		Point rightMost = null;
		Element intersection = null;
		Edge currentMemEdge = null;
		Edge rm;
		int memoryPos = 0;
		int rmCount;
		int mem;
		while (i < eventPoints.size()) {
			//We retrieve the event about to be processed.
			currentEvent = eventPoints.get(i);
			//We retrieve the absciss of the current event
			abs = currentEvent.getX();
			//We've reached a new event, we must be sure that our vertical
			//list is still sorted.
			sortEdgesVertically(edgeBuffer, abs);
			if (currentMemEdge == null) {
				currentMemEdge = edgeMemory.get(0);
			}
			//We add the edges that can be associated to this event.
			for (; memoryPos < edgeMemory.size(); memoryPos++) {
				//As you can see, the first argument of this for loop is not used.
				//We want here to go straight forward in the list, but depending on the
				//value of eventPoints.get(i)
				currentMemEdge = edgeMemory.get(memoryPos);
				if (currentEvent.equals2D(currentMemEdge.getPointLeft())) {
					insertEdgeVerticalList(currentMemEdge, edgeBuffer, abs);
				} else {
					break;
				}
			}
			//we search for intersections only if we have at least two edges...
			if (edgeBuffer.size() > 1) {
				e2 = edgeBuffer.get(0);
				j = 1;
				while (j < edgeBuffer.size()) {
					//We walk through our buffer
					j = j < 1 ? 1 : j;
					e1 = edgeBuffer.get(j - 1);
					e2 = edgeBuffer.get(j);
					intersection = e1.getIntersection(e2);
					rmCount = 0;
					if (intersection instanceof Point) {
						//We have a single intersection point.
						//We must check it's not at an extremity.
						newEvent = (Point) intersection;
						if (!e1.isExtremity(newEvent) || !e2.isExtremity(newEvent)) {
							//We've found an intersection between two non-colinear edges
							//We must check that this intersection point is not
							//the current event point. If it is, we must process the
							//intersection.
							if (newEvent.equals2D(currentEvent)) {//We process the intersection.
								if (!newEvent.equals2D(e2.getPointLeft()) && !newEvent.equals2D(e2.getPointRight())) {
									//newEvent lies on e2, and is not an extremity
									inter2 = new Edge(e2.getPointLeft(), newEvent);
									addConstraintEdge(inter2);
									rm = edgeBuffer.remove(j);
									if (!rm.equals(e2)) {
										throw new RuntimeException("problem while removing an edge");
									}
									inter4 = new Edge(e2.getPointRight(), newEvent);
									insertEdgeVerticalList(inter4, edgeBuffer, abs);
									rmCount++;
								} else if (newEvent.equals2D(e2.getPointRight())) {
									addConstraintEdge(e2);
									rm = edgeBuffer.remove(j);
									if (!rm.equals(e2)) {
										throw new RuntimeException("problem while removing an edge");
									}
									rmCount++;
								}
								if (!newEvent.equals2D(e1.getPointLeft()) && !newEvent.equals2D(e1.getPointRight())) {
									inter1 = new Edge(e1.getPointLeft(), newEvent);
									addConstraintEdge(inter1);
									rm = edgeBuffer.remove(j - 1);
									if (!rm.equals(e1)) {
										throw new RuntimeException("problem while removing an edge");
									}
									inter3 = new Edge(e1.getPointRight(), newEvent);
									insertEdgeVerticalList(inter3, edgeBuffer, abs);
									rmCount++;
								} else if (newEvent.equals2D(e1.getPointRight())) {
									addConstraintEdge(e1);
									rm = edgeBuffer.remove(j - 1);
									if (!rm.equals(e1)) {
										throw new RuntimeException("problem while removing an edge");
									}
									rmCount++;
								}
								j = (j - rmCount < 0 ? 0 : j - rmCount);
							} else { // the intersection will be processed later.
								addToSortedList(newEvent, eventPoints);
							}
						} else {
							//in this case, we have e1.isExtremity(newEvent) && e2.isExtremity(newEvent)
							if (e2.getPointRight().equals2D(currentEvent)) {
								addConstraintEdge(e2);
								rm = edgeBuffer.remove(j);
								if (!rm.equals(e2)) {
									throw new RuntimeException("problem while removing an edge");
								}
								rmCount++;
							}
							if (e1.getPointRight().equals2D(currentEvent)) {
								addConstraintEdge(e1);
								rm = edgeBuffer.remove(j - 1);
								if (!rm.equals(e1)) {
									throw new RuntimeException("problem while removing an edge");
								}
								rmCount++;
							}
							j = (j - rmCount < 0 ? 0 : j - rmCount);
						}
					} else if (intersection instanceof Edge) {
						//The intersection is an edge. There are two possible cases :
						//The left point of the intersection is at the extremity of e1 and e2 : we
						//register the right point as an event
						//The left point is the extremity of e1 OR (exclusive) of e2. It is an event,
						//and certainly the current one.
						edgeEvent = (Edge) intersection;
						newEvent = edgeEvent.getPointLeft();
						//the intersection point is inside one of the edges.
						//We are supposed to be on it..
						//inter1 will be the lowest part of the intersection,
						//inter2 the middle one and inter3 the highest.
						if (newEvent.equals2D(currentEvent)) {
							leftMost = (e1.getPointLeft().compareTo2D(e2.getPointLeft()) < 1
								? e1.getPointLeft()
								: e2.getPointLeft());
							rightMost = (e1.getPointRight().compareTo2D(e2.getPointRight()) < 1
								? e2.getPointRight()
								: e1.getPointRight());
							inter1 = null;
							inter2 = null;
							inter3 = null;
							//we remove the two edges we are analyzing,
							//new edges will be inserted if necessary.
							rm = edgeBuffer.remove(j);
							if (!rm.equals(e2)) {
								throw new RuntimeException("problem while removing an edge");
							}
							rm = edgeBuffer.remove(j - 1);
							if (!rm.equals(e1)) {
								throw new RuntimeException("problem while removing an edge");
							}
							j--;
							if (leftMost.compareTo2D(newEvent) == -1) {
								inter1 = new Edge(leftMost, newEvent);
							}
							inter2 = edgeEvent;
							if (rightMost.compareTo2D(edgeEvent.getPointRight()) == 1) {
								inter3 = new Edge(edgeEvent.getPointRight(), rightMost);
							}
							if (inter1 != null) {
								if (inter1.getPointRight().compareTo2D(currentEvent) == 1) {
									addConstraintEdge(inter1);
								} else {
									mem = insertEdgeVerticalList(inter1, edgeBuffer, abs);
									j = j <= mem ? j : mem;
								}
							}
							if (inter2.getPointRight().compareTo2D(currentEvent) == 1) {
								//inter2 has to be processed for further intersections
								mem = insertEdgeVerticalList(inter2, edgeBuffer, abs);
								j = j <= mem ? j : mem;

							} else {
								//inter2 can't be implied in other intersections
								addConstraintEdge(inter2);
							}
							if (inter3 != null) {
								//inter3 must be processed further.
								addEdgeToLeftSortedList(edgeMemory, inter3);
//								mem = insertEdgeVerticalList(inter3, edgeBuffer, abs);
//								j = j <= mem ? j : mem;
							}
							j = j-2 < 0 ? 0 : j-2 ;
						} else {
							throw new DelaunayError("We should already be on this event point");
						}

					} else {
						if (e1.getPointRight().equals2D(currentEvent)) {
							addConstraintEdge(e1);
							rm = edgeBuffer.remove(j - 1);
							if (!rm.equals(e1)) {
								throw new RuntimeException("problem while removing an edge");
							}
							j--;
							j = j-1 < 0 ? 0 : j-1;
						}
					}
					j++;
				}
			} else if (edgeBuffer.size() == 1 && edgeBuffer.get(0).getPointRight().equals2D(currentEvent)) {
				addConstraintEdge(edgeBuffer.get(0));
				edgeBuffer.remove(0);
			}
			i++;
		}
	}

	/**
	 * This method will sort the edges contained in the ArrayList list by considering
	 * their intersection point with the line of equation x=abs, where a is given
	 * in parameter.
	 * @param edgeList
	 * @param x
	 */
	public void sortEdgesVertically(List<Edge> edgeList, double abs) throws DelaunayError {
		int s = edgeList.size();
		int i = 0;
		int c = 0;
		Edge e1;
		Edge e2;
		while (i < s - 1) {
			e1 = edgeList.get(i);
			e2 = edgeList.get(i + 1);
			c = e1.verticalSort(e2, abs);
			if (c == 1) {
				edgeList.set(i, e2);
				edgeList.set(i + 1, e1);
				i = i - 1 < 0 ? 0 : i - 1;
			} else {
				i++;
			}
		}
	}

	/**
	 * This method will insert a new Edge in a vertically sorted list, as described in
	 * sortEdgesVertically.
	 * Be careful when using this method. In fact, you must use the same absciss
	 * here that the one which has been used when sorting the list.
	 * @param edge
	 * @param edgeList
	 */
	public int insertEdgeVerticalList(Edge edge, List<Edge> edgeList, double abs) throws DelaunayError {
		if (edgeList == null || edgeList.isEmpty()) {
			edgeList.add(edge);
		}
		int s = edgeList.size();
		int compare = edge.verticalSort(edgeList.get(0), abs);
		if (compare == -1) {
			edgeList.add(0, edge);
			return 0;
		}
		compare = edge.verticalSort(edgeList.get(s - 1), abs);
		if (compare == 1) {
			edgeList.add(s, edge);
			return s;
		}
		int delta = s / 2;
		int i = s / 2;
		while (delta > 0) {
			compare = edge.verticalSort(edgeList.get(i), abs);
			switch (compare) {
				case -1:
					compare = edge.verticalSort(edgeList.get(i - 1), abs);
					switch (compare) {
						case -1:
							delta = delta / 2;
							i = i - delta;
							break;
						case 1:
							edgeList.add(i, edge);
							return i;
						case 0:
							return i - 1;
					}
					break;
				case 0:
					return i;
				case 1:
					compare = edge.verticalSort(edgeList.get(i + 1), abs);
					switch (compare) {
						case 1:
							delta = delta / 2;
							i = i + delta;
							break;
						case -1:
							edgeList.add(i + 1, edge);
							return i + 1;
						case 0:
							return i + 1;

					}
					break;
			}
		}
		return -1;
	}

	/**
	 * Check if the list given in argument is vertically sorted or not.
	 * @param edgeList
	 * @return
	 */
	public boolean isVerticallySorted(List<Edge> edgeList, double abs) {
		Edge e1, e2;
		e2 = edgeList.get(0);
		for (int i = 1; i < edgeList.size(); i++) {
			e1 = e2;
			e2 = edgeList.get(i);
			try {
				if (e1.verticalSort(e2, abs) == 1) {
					return false;
				}
			} catch (DelaunayError e) {
				System.err.println(e.getCause());
			}
		}

		return true;
	}

	/**
	 * This method simply travels the list given in argument. If edges edgelist.get(i)
	 * and edgeList.get(i+1) intersect, then we add the intersection point in
	 * the eventList.
	 * @param edgeList
	 */
	public void addPointsFromNeighbourEdges(List<Edge> edgeList, List<Point> eventList) throws DelaunayError {
		Edge e1;
		Edge e2;
		Element inter = null;
		//we check that our paremeters are not null, and that our edge list contains
		//at least two edges, because they couldn't be intersections otherwise.
		if (edgeList == null || eventList == null || edgeList.size() < 2) {
			return;
		} else {
			for (int i = 0; i < edgeList.size() - 1; i++) {
				e1 = edgeList.get(i);
				e2 = edgeList.get(i + 1);
				inter = e1.getIntersection(e2);
				if (inter != null) {
					if (inter instanceof Point) {
						eventList.add((Point) inter);
					} else {
						eventList.add(((Edge) inter).getPointLeft());
					}
				}
			}
		}
	}

//	// ------------------------------------------------------------------------------------------
//	/**
//	 * Generate the Delaunay's triangularization with a flip-flop algorithm.
//	 * Mesh must have been set. Triangulation can only be done once.
//	 * Otherwise call reprocessDelaunay
//	 *
//	 * @throws DelaunayError
//	 */
//	public void processDelaunay() throws DelaunayError {
//		if (isMeshComputed()) {
//			throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_GENERATED);
//		}
//		else if (points.size() < 3) {
//			throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_NOT_ENOUGH_POINTS_FOUND);
//		}
//		else {
//			// general data structures
//			badEdgesQueueList = new LinkedList<Edge>();
//			boundaryEdges = new ArrayList<Edge>();
//			LinkedList<Point> badPointList = new LinkedList<Point>();
//
//			// sort points
//			if (verbose){
//				System.out.println("Sorting points");
//                        }
//			ListIterator<Point> iterPoint = points.listIterator();
//
//
//			// we build a first triangle with the 3 first points we find
//			if (verbose){
//				System.out.println("Processing triangularization");
//                        }
//			DelaunayTriangle aTriangle;
//			Point p1, p2, p3;
//			Edge e1, e2, e3;
//			p1 = p2 = p3 = null;
//
//
//			p1 = iterPoint.next();
//			while (p1.isLocked()){
//				p1 = iterPoint.next();
//                        }
//
//			p2 = iterPoint.next();
//			while (p2.isLocked()){
//				p2 = iterPoint.next();
//                        }
//			e1 = new Edge(p1, p2);
//
//			// The 3 points MUST NOT be colinear
//			p3 = iterPoint.next();
//			while (p3.isLocked()){
//				p3 = iterPoint.next();
//                        }
//
//			while ((e1.isColinear2D(p3)) && (iterPoint.hasNext())) {
//				badPointList.add(p3);
//
//				p3 = iterPoint.next();
//				while (p3.isLocked()){
//					p3 = iterPoint.next();
//                                }
//			}
//
//			// The triangle's edges MUST be in the right direction
//			if (e1.isLeft(p3)) {
//				e2 = new Edge(p2, p3);
//				e3 = new Edge(p3, p1);
//			} else {
//				e1.setStartPoint(p2);
//				e1.setEndPoint(p1);
//
//				e2 = new Edge(p1, p3);
//				e3 = new Edge(p3, p2);
//			}
//
//			addEdge(e1);
//			addEdge(e2);
//			addEdge(e3);
//
//
//			aTriangle = new DelaunayTriangle(e1, e2, e3);
//			addTriangle(aTriangle);
//
//			// Then process the other points - order don't care
//			boundaryEdges.add(e1);
//			boundaryEdges.add(e2);
//			boundaryEdges.add(e3);
//
//			// flip-flop on a list of points
//			boolean ended = false;
//			Point aPoint=null;
//			Point LastTestedPoint=null;
//			int count = 0;
//			while (! ended) {
//				boolean hasGotPoint = false;
//				if (! badPointList.isEmpty()) {
//					aPoint = badPointList.getFirst();
//					if (LastTestedPoint != aPoint) {
//						badPointList.removeFirst();
//						hasGotPoint = true;
//					}
//				}
//
//				if (! hasGotPoint) {
//					if (iterPoint.hasNext()) {
//						count++;
//						aPoint = iterPoint.next();
//					} else {
//						ended = true;
//						aPoint = null;
//					}
//				}
//				LastTestedPoint = aPoint;
//
//				if (aPoint!= null && aPoint.isLocked() && myInsertPoint(aPoint) == null) {
//					badPointList.addFirst(aPoint);
//                                }
//
//			}
//
//
//			meshComputed = true;
//
//			if(polygons.size()>0)
//			{
//				if (verbose){
//					System.out.println("Processing edges of "+polygons.size()+" polygon"+(polygons.size()>1?"s":""));
//                                }
////				processPolygons();
//			}
//
//			// It's fine, we computed the mesh
//			if (verbose) {
//				System.out.println("End processing");
//				System.out.println("Triangularization end phase : ");
//				System.out.println("  Points : " + points.size());
//				System.out.println("  Edges : " + edges.size());
//				System.out.println("  Triangles : " + triangleList.size());
//			}
//		}
//	}
//
//	/**
//	 * Insert a point to the current triangulation
//	 *
//	 * @param aPoint
//	 * @param property Property for the new triangle.
//	 * @throws DelaunayError
//	 */
//	private DelaunayTriangle insertPoint(Point aPoint, int property) throws DelaunayError {//test
//		DelaunayTriangle foundTriangle = null;
//		// We build triangles with all boundary edges for which the point is on
//		// the left
//		Point p1, p2;
//		Edge anEdge1, anEdge2;
//		LinkedList<Edge> oldEdges = new LinkedList<Edge>();
//		LinkedList<Edge> newEdges = new LinkedList<Edge>();
//
//
//		for(int i=0; i<boundaryEdges.size(); i++){
//			Edge anEdge=boundaryEdges.get(i);
//
//
//			// as the boundary edge anEdge already exists, we check if the
//			// point is on the left for the reverse order of the edge
//			// So, the point must be on the right of the BoundaryEdge
//			boolean test = false;
//			p1 = null;
//			p2 = null;
//			anEdge1=anEdge2=null;
//			test = anEdge.isRight(aPoint);
//			if (test) {
//				// We have the edge and the 2 point, in reverse order
//				p2 = anEdge.getStartPoint();
//				p1 = anEdge.getEndPoint();
//
//				// triangle points order is p1, p2, aPoint
//				// check if there is an edge between p2 and aPoint
//
//
//				anEdge1 = Tools.checkTwoPointsEdge(p2, aPoint, newEdges);
//
//
//				if (anEdge1 == null) {
//					anEdge1 = new Edge(p2, aPoint);
//					if(!edgesQuadTree.isIntersect(anEdge1)){
//						addEdgeToLeftSortedList(edges,anEdge1);
//						newEdges.add(anEdge1);
//					}
//					else {
//						test = false;
//					}
//				}
//
//				if(test)
//				{
//					// check if there is an edge between aPoint and p1
//
//					anEdge2 = Tools.checkTwoPointsEdge(aPoint, p1, newEdges);
//
//
//					if (anEdge2 == null) {
//						anEdge2 = new Edge(aPoint, p1);
//
//						if(!edgesQuadTree.isIntersect(anEdge2)){
//							addEdgeToLeftSortedList(edges,anEdge2);
//							newEdges.add(anEdge2);
//						}
//						else {
//							test = false;
//						}
//					}
//
//
//					if(test)
//					{
//
//						// create triangle : take care of the order : anEdge MUST be
//						// first
//						DelaunayTriangle aTriangle = new DelaunayTriangle(anEdge, anEdge1, anEdge2);
//						aTriangle.setProperty(property);
//						addTriangle(aTriangle);
//
//						// We say we founded a first triangle
//						if (foundTriangle == null) {
//							foundTriangle = aTriangle;
//						}
//
//						// Mark the edge to be removed
//						oldEdges.add(anEdge);
//
//						// add the edges to the bad edges list
//						if (!isMeshComputed()) {
//							if (!badEdgesQueueList.contains(anEdge)) {
//								badEdgesQueueList.add(anEdge);
//							}
//							if (!badEdgesQueueList.contains(anEdge1)) {
//								badEdgesQueueList.add(anEdge1);
//							}
//							if (!badEdgesQueueList.contains(anEdge2)) {
//								badEdgesQueueList.add(anEdge2);
//							}
//						}
//
//					}
//				}
//			}
//		}
//
//
//
//		// remove old edges
//		for (Edge anEdge : oldEdges){
//			boundaryEdges.remove(anEdge);
//                }
//
//		// add the newEdges to the boundary list
//		for (Edge anEdge : newEdges){
//			if ((anEdge.getLeft() == null) || (anEdge.getRight() == null))
//			{
//				boundaryEdges.add(anEdge);
//			}
//                }
//
//
//		// Process badTriangleQueueList
//		processBadEdges();
//
//
//		return foundTriangle;
//	}
//
//	/**
//	 * Process the flip-flop algorithm on the list of triangles
//	 */
//	private void processBadEdges() {
//		if (!isMeshComputed()) {
//			LinkedList<Edge> alreadySeen = new LinkedList<Edge>();
//			while (!badEdgesQueueList.isEmpty()) {
//				Edge anEdge = badEdgesQueueList.getFirst();
//				badEdgesQueueList.removeFirst();
//
//				boolean doIt = true;
//
//				if (anEdge.isLocked()) {
//					doIt = false;
//				}
//				else if (alreadySeen.contains(anEdge)) {
//					doIt = false;
//				}
//
//				if (doIt) {
//					alreadySeen.add(anEdge);
//					// We cannot process marked edges
//					// We check if the two triangles around the edge are ok
//					DelaunayTriangle aTriangle1 = anEdge.getLeft();
//					DelaunayTriangle aTriangle2 = anEdge.getRight();
//					if ((aTriangle1 != null) && (aTriangle2 != null)) {
//
//						if (swapTriangle(aTriangle1, aTriangle2, anEdge, false)) {
//							// Add the triangle"s edges to the bad edges list
//							Edge addEdge;
//							for (int j = 0; j < 3; j++) {
//								addEdge = aTriangle1.edges[j];
//								if ((addEdge.getLeft() != null)
//										&& (addEdge.getRight() != null)) {
//									if (addEdge != anEdge){
//										if (!badEdgesQueueList.contains(addEdge)){
//											badEdgesQueueList.add(addEdge);
//                                                                                }
//                                                                        }
//								}
//								addEdge = aTriangle2.edges[j];
//								if ((addEdge.getLeft() != null)
//										&& (addEdge.getRight() != null)) {
//									if (addEdge != anEdge){
//										if (!badEdgesQueueList.contains(addEdge)){
//											badEdgesQueueList.add(addEdge);
//                                                                                }
//                                                                        }
//								}
//							}
//						}
//					}
//				}
//			}
//		} else {
//			while (!badEdgesQueueList.isEmpty()) {
//				badEdgesQueueList.removeFirst();
//			}
//		}
//	}
//
//	/**
//	 * Insert a point to the current triangularization
//	 *
//	 * @param aPoint
//	 * @throws DelaunayError
//	 */
//	private DelaunayTriangle myInsertPoint(Point aPoint) throws DelaunayError {
//		return insertPoint(aPoint, 0);
//	}
//
//	/**
//	 * Swap two neighbour triangles, whose common edge is anEdge
//	 * @param aTriangle1
//	 * @param aTriangle2
//	 * @param anEdge
//	 * @param forced
//	 * @return
//	 */
//	private boolean swapTriangle(DelaunayTriangle aTriangle1, DelaunayTriangle aTriangle2,
//			Edge anEdge, boolean forced) {
//
//		boolean exchange = false;
//		Edge anEdge10, anEdge11, anEdge12;
//		Edge anEdge20, anEdge21, anEdge22;
//		Point p1, p2, p3, p4;
//
//		if ((aTriangle1 != null) && (aTriangle2 != null)) {
//			p1 = anEdge.getStartPoint();
//			p2 = anEdge.getEndPoint();
//
//			p3 = p4 = null;
//
//			// Test for each triangle if the remaining point of the
//			// other triangle is inside or not
//			// DelaunayTriangle 1 is p1, p2, p3 or p2, p1, p3
//			p3 = aTriangle1.getAlterPoint(p1, p2);
//			if (p3 != null) {
//				if (aTriangle2.inCircle(p3) == 1) {
//					exchange = true;
//				}
//			}
//
//			// DelaunayTriangle 2 is p2, p1, p4 or p1, p2, p4
//			p4 = aTriangle2.getAlterPoint(p1, p2);
//			if (p4 != null) {
//				if (aTriangle1.inCircle(p4) == 1) {
//					exchange = true;
//				}
//			}
//
//			if (p3 != p4) {
//				if ((exchange) || (forced)) {
//					anEdge10 = anEdge;
//					anEdge11 = checkTwoPointsEdge(p3, p1, aTriangle1.edges, 3);
//					anEdge12 = checkTwoPointsEdge(p1, p4, aTriangle2.edges, 3);
//					anEdge20 = anEdge;
//					anEdge21 = checkTwoPointsEdge(p2, p4, aTriangle2.edges, 3);
//					anEdge22 = checkTwoPointsEdge(p3, p2, aTriangle1.edges, 3);
//					if ((anEdge11 == null) || (anEdge12 == null) || (anEdge21 == null) || (anEdge22 == null)) {
//						System.out.println("ERREUR");
//					} else {
//						edgesQuadTree.remove(anEdge);
//						anEdge.setStartPoint(p3);
//						anEdge.setEndPoint(p4);
//						edgeGID++;
//						anEdge.setGID(edgeGID);
//						addEdgeToLeftSortedList(edges, anEdge);
//						aTriangle1.edges[0] = anEdge10;
//						aTriangle1.edges[1] = anEdge11;
//						aTriangle1.edges[2] = anEdge12;
//						aTriangle2.edges[0] = anEdge20;
//						aTriangle2.edges[1] = anEdge21;
//						aTriangle2.edges[2] = anEdge22;
//						if (anEdge12.getLeft() == aTriangle2) {
//							anEdge12.setLeft(aTriangle1);
//						} else {
//							anEdge12.setRight(aTriangle1);
//						}
//						if (anEdge22.getLeft() == aTriangle1) {
//							anEdge22.setLeft(aTriangle2);
//						} else {
//							anEdge22.setRight(aTriangle2);
//						}
//						if (anEdge.isLeft(p1)) {
//							anEdge.setLeft(aTriangle1);
//							anEdge.setRight(aTriangle2);
//						} else {
//							anEdge.setLeft(aTriangle2);
//							anEdge.setRight(aTriangle1);
//						}
//						aTriangle1.recomputeCenter();
//						aTriangle2.recomputeCenter();
//					}
//				}
//			}
//		}
//		return exchange;
//	}
//
//	/**
//	 * Check if the edge already exists. Returns null if it doesn't
//	 *
//	 * @param p1
//	 * @param p2
//	 * @param edgeQueueList
//	 * @param size
//	 *
//	 * @return
//	 */
//	private Edge checkTwoPointsEdge(Point p1, Point p2,
//			Edge[] edgeQueueList, int size) {
//		// Check if the two points already lead to an existing edge.
//		// If the edge exists it must be in the non-processed edges
//		Edge theEdge = null;
//		int i = 0;
//		int max = (edgeQueueList.length < size ? edgeQueueList.length : size);
//		while ((i < max) && (theEdge == null)) {
//			Edge anEdge = edgeQueueList[i];
//			if (((anEdge.getStartPoint() == p1) && (anEdge.getEndPoint() == p2))
//					|| ((anEdge.getStartPoint() == p2) && (anEdge.getEndPoint() == p1))) {
//				theEdge = anEdge;
//			}
//			else {
//				i++;
//			}
//		}
//		return theEdge;
//	}
}
