package org.jdelaunay.delaunay;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alexis
 */
public class ConstrainedMesh {

	//The list of triangles contained in the mesh
	private ArrayList<DelaunayTriangle> triangleList;
	//The lis of constraints used during the triangulation
	private ArrayList<Edge> constraintEdges;
	//The list of points used during the triangulation
	private ArrayList<Point> points;
	//
	private double precision;
	//The minimum distance between two distinct points
	private double tolerance;
	//The two following lists are used only during computation.
	//The bad edge queue list contains all the edges that coud be changed
	//during a flip-flap operation
	private List<Edge> badEdgesQueueList;
	//boundaryEdges contains the Envelope of the CURRENT geometry.
	private List<Edge> boundaryEdges;
	// constants
	public static final double EPSILON = 0.00001;
	public static final int MAXITER = 5;
	public static final int REFINEMENT_MAX_AREA = 1;
	public static final int REFINEMENT_MIN_ANGLE = 2;
	public static final int REFINEMENT_SOFT_INTERPOLATE = 4;
	public static final int REFINEMENT_OBTUSE_ANGLE = 8;

	public ConstrainedMesh() {
		triangleList = new ArrayList<DelaunayTriangle>();
		constraintEdges = new ArrayList<Edge>();
		points = new ArrayList<Point>();

		precision = 0;
		tolerance = 0.00001;

		badEdgesQueueList = new ArrayList<Edge>();
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
	public void setBadEdgesQueueList(ArrayList<Edge> badEdgesQueueList) {
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
	public ArrayList<Edge> getConstraintEdges() {
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
		addEdgeToLeftSortedList(constraintEdges, e);
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
	private void addEdgeToLeftSortedList(ArrayList<Edge> sorted, Edge edge) {
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
	public ArrayList<DelaunayTriangle> getTriangleList() {
		return triangleList;
	}

	/**y
	 * Set the list of triangles already computed in this mesh.
	 * @param triangleList
	 */
	public void setTriangleList(ArrayList<DelaunayTriangle> triangleList) {
		this.triangleList = triangleList;
	}

	/**
	 * Get the points contained in this mesh
	 * @return
	 */
	public ArrayList<Point> getPoints() {
		return points;
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
		addPointToSortedList(point, points);
	}

	/**
	 * Add a new point in a sorted list.
	 * @param point
	 * @param sortedList
	 */
	private void addPointToSortedList(Point point, List<Point> sortedList) {
		int s = sortedList.size();
		if (s == 0) {
			sortedList.add(point);
		} else {
			int p = s / 2;
			int delta = s / 2;
			int ret = -1;
			int c;
			//If the point is inferior to the first element of te list, we place it at the beginning
			if (point.compareTo2D(sortedList.get(0)) == -1) {
				p = 0;
				ret = 0;
				//If the point is superior to the last element of the list, we place it at the end
			} else if (point.compareTo2D(sortedList.get(s - 1)) == 1) {
				p = s;
				ret = s;
			}
			while (ret != p) {
				delta = (delta / 2 > 0 ? delta / 2 : 1);
				c = point.compareTo2D(sortedList.get(p));
				switch (c) {
					case -1:
						//point < points.get(p)
						//We must move left
						c = point.compareTo2D(sortedList.get(p - 1));
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
			addPointToSortedList(edge.getStart(), eventPoints);
			addPointToSortedList(edge.getEnd(), eventPoints);
		}
		//we are about to perform the sweepline algorithm
		Point currentEvent = null;
		//edgeBuffer will contain the edges sorted vertically
		ArrayList<Edge> edgeBuffer = new ArrayList<Edge>();
		//We keep a shallow copy of constraintEdges...
		ArrayList<Edge> edgeMemory = constraintEdges;
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
								addPointToSortedList(newEvent, eventPoints);
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
}
