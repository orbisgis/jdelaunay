package org.jdelaunay.delaunay;

import com.vividsolutions.jts.geom.Envelope;
import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.apache.log4j.Logger;

/**
 * This class is used to compute the constrained delaunay triangulation on a set of
 * points and constraint edges. The constraints can be validated before the triangulation.
 * @author alexis
 */
public class ConstrainedMesh implements Serializable {
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(ConstrainedMesh.class);
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
	private List<ConstraintPolygon> polygons;
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

	//The two points that will be used to extend the mesh, and to reduce the number
	//of edges in the boundary. They will be removed when the mesh will be comuted,
	//and the mesh will be fixed.
	private Double extMinX = null;
	private Double extMaxY = null;
	private Double extMinY = null;

	public ConstrainedMesh() {
		triangleList = new ArrayList<DelaunayTriangle>();
		edges = new ArrayList<Edge>();
		constraintEdges = new ArrayList<Edge>();
		points = new ArrayList<Point>();
		polygons = new ArrayList<ConstraintPolygon>();
		meshComputed = false;
		precision = 0;
		tolerance = EPSILON;

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
	public final List<Edge> getBadEdgesQueueList() {
		return badEdgesQueueList;
	}

	/**
	 * Set the list of edges that are to be processed by the flip flap algorithm
	 * @param badEdgesQueueList
	 */
	public final void setBadEdgesQueueList(LinkedList<Edge> badEdgesQueueList) {
		this.badEdgesQueueList = badEdgesQueueList;
	}

	/**
	 * Get the list of edges that form the current convex hull of the triangulation
	 * @return
	 */
	public final List<Edge> getBoundaryEdges() {
		return boundaryEdges;
	}

	/**
	 * Set the list of edges that form the current convex hull of the triangulation
	 * @param boundaryEdges
	 */
	public final void setBoundaryEdges(ArrayList<Edge> boundaryEdges) {
		this.boundaryEdges = boundaryEdges;
	}

	/**
	 * Get the list of edges that are used as constraints during triangulation
	 * @return
	 */
	public final List<Edge> getConstraintEdges() {
		return constraintEdges;
	}

	/**
	 * Set the list of edges that are used as constraints during triangulation
	 * As we can't be sure the constraintEdges is already sorted, we sort it first
	 * and add all the corresponding points to the point list.
	 * @param constraintEdges
	 */
	public final void setConstraintEdges(ArrayList<Edge> constraint) throws DelaunayError {
		this.constraintEdges = new ArrayList<Edge>();
		for (Edge e : constraint) {
			//We lock the edge. It will not be supposed to be switched
			//during a flip flap.
			e.setLocked(true);
			if(e.getPointLeft().equals(e.getEndPoint())){
				e.swap();
			}
			addConstraintEdge(e);
		}
	}

	/**
	 * Add an edge to the list of constraint edges.
	 * @param e
	 *	the edge we want to add
	 */
	public final void addConstraintEdge(Edge e) throws DelaunayError {
		if (constraintEdges == null) {
			constraintEdges = new ArrayList<Edge>();
		}
		if(e.getPointLeft().equals(e.getEndPoint())){
			e.swap();
		}
		e.setLocked(true);
		addEdgeToLeftSortedList(constraintEdges, e);
		int index = Collections.binarySearch(points, e.getStartPoint());
		if(index < 0 ){
			updateExtensionPoints(e.getStartPoint());
			points.add(-index -1, e.getStartPoint());
		} else {
			e.setStartPoint(points.get(index));
		}
		index = Collections.binarySearch(points, e.getEndPoint());
		if(index < 0 ){
			updateExtensionPoints(e.getEndPoint());
			points.add(-index -1, e.getEndPoint());
		} else {
			e.setEndPoint(points.get(index));
		}
	}

	/**
	 * Get the list of edges
	 * @return
	 */
	public final List<Edge> getEdges() {
		return edges;
	}

	/**
	 * Set the list of edges
	 * @param constraintEdges
	 */
	public final void setEdges(List<Edge> constraint) throws DelaunayError {
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
	public final void addEdge(Edge e) {
		if (edges == null) {
			edges = new ArrayList<Edge>();
		}
		int constraintIndex = sortedListContains(constraintEdges, e);
		if (constraintIndex < 0) {
			addEdgeToLeftSortedList(edges, e);
		} else {
			addEdgeToLeftSortedList(edges, constraintEdges.get(constraintIndex));
		}
	}

	/**
	 * Remove an Edge from the list of edges.
	 * @param e
	 */
	public final void removeEdge(Edge e) {
		//edges is a sorted list, using the left right sort. We are supposed
		//to ensure unicity of objects in it, so we can use the binarysearch directly.
		int index = Collections.binarySearch(edges, e);
		//index will be positive if and only ifedges contains e (cf java API)
		if (index >= 0) {
			edges.remove(index);
		}
	}

	/**
	 * This method will sort the edges using the coordinates of the left point
	 * of the edges.
	 * @return
	 */
	public final List<Edge> sortEdgesLeft(List<Edge> inputList) {
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
		addToSortedList(edge, sorted, edgeGID);
	}

	/**
	 * Search an edge in the list of edges.
	 * @param edge
	 * @return
	 */
	public final int searchEdge(Edge edge) {
		return Collections.binarySearch(edges, edge);
	}

	/**
	 * Get the precision
	 * @return
	 */
	public final double getPrecision() {
		return precision;
	}

	/**
	 * Set the precision
	 * @param precision
	 */
	public final void setPrecision(double precision) {
		this.precision = precision;
	}

	/**
	 * Get the value used to compute the minimum distance between two points
	 * @return
	 */
	public final double getTolerance() {
		return tolerance;
	}

	/**
	 * Set the value used to compute the minimum distance between two points
	 * @param tolerance
	 */
	public final void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}

	/**
	 * Get the list of triangles already computed and added in this mesh
	 * @return
	 */
	public final List<DelaunayTriangle> getTriangleList() {
		return triangleList;
	}

	/**
	 * Set the list of triangles already computed in this mesh.
	 * @param triangleList
	 */
	public final void setTriangleList(List<DelaunayTriangle> triangleList) {
		Collections.sort(triangleList);
		this.triangleList = triangleList;
	}

	/**
	 * Add a triangle to the current constrained mesh
	 * @param triangle
	 */
	public final void addTriangle(DelaunayTriangle triangle) {
		triangleList.add(triangle);
		triangleGID++;
		triangle.setGID(triangleGID);
	}

	/**
	 * Return the index i of the triangle given in argument in the list of triangles
	 * or -i-1 if it is not in the list.
	 * @param tri
	 * @return
	 */
	public final int containsTriangle(DelaunayTriangle tri) {
		return sortedListContains(triangleList, tri);
	}

	/**
	 * Remove a triangle from the list of triangles
	 * @param tri
	 */
	public final void removeTriangle(DelaunayTriangle tri) {
//		//first we search it
		triangleList.remove(tri);
	}

	/**
	 * Get the points contained in this mesh
	 * @return
	 */
	public final List<Point> getPoints() {
		return points;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public final Point getPoint(double x, double y, double z) throws DelaunayError{
		Point pt = new Point(x,y,z);
		int c = listContainsPoint(pt);
		if(c<0){
			return null;
		} else {
			return points.get(c);
		}
	}

	/**
	 * Can be used to know if the mesh has been computed or not
	 * @return
	 */
	public final boolean isMeshComputed() {
		return meshComputed;
	}

	/**
	 * Change the status of this mesh computation
	 * @param comp
	 */
	public final void setMeshComputed(boolean comp) {
		meshComputed = comp;
	}

	/**
	 * Get the bounding box of this mesh.
	 * @return
	 */
	public final Envelope getBoundingBox() {
		Envelope env = new Envelope();
		for (Point p : points) {
			env.expandToInclude(p.getCoordinate());
		}
		return env;
	}

	/**
	 * Says if the verbose mode is activated or not
	 * @return
	 */
	public final boolean isVerbose() {
		return verbose;
	}

	/**
	 * Set the verbosity level
	 * @param verb
	 */
	public final void setVerbose(boolean verb) {
		verbose = verb;
	}

	/**
	 * Set the list of points to be used during the triangulation
	 * If using this method, it's up to you to be sure that you don't have
	 * any duplication in your set of points...
	 * @param points
	 */
	public final void setPoints(List<Point> pts) throws DelaunayError {
		if(pts == null){
			points = new ArrayList<Point>();
		} else {
			Collections.sort(pts);
			points = new ArrayList<Point>();
			extMaxY = null;
			extMinY = null;
			extMinX = null;
			for(Point pt : pts){
				updateExtensionPoints(pt);
			}
			this.points = pts;
		}
	}

	/**
	 * Add a new point in the list that will be used to perform the triangulation.
	 * The list of points is supposed to be sorted.
	 * @param point
	 */
	public final void addPoint(Point point) throws DelaunayError {
		if (points == null) {
			points = new ArrayList<Point>();
		}
		updateExtensionPoints(point);
		addToSortedList(point, points, pointGID);
	}

	/**
	 * Get the extension points that would be added to the mesh while computing
	 * DT or CDT with the current set of points.
	 * @return
	 *	A set of two points. They will share the same x-coordinate, that is
	 *	the max x-coordinate of the mesh minus 1.
	 *	The first one will have the max y-coordinate (ie the max y-coordinate of
	 *	the mesh plus 1), the second will have the min y-coordinate (ie the min
	 *	y-coordinate minux 1).
	 * @throws DelaunayError
	 */
	public final List<Point> getExtensionPoints() throws DelaunayError{
		ArrayList<Point> ret = new ArrayList();
		ret.add(new Point(extMinX, extMaxY, 0));
		ret.add(new Point(extMinX, extMinY, 0));
		return ret;
	}

	/**
	 * This method update the coordinates of the extension points that will be used
	 * during the triangulation
	 * @param pt
	 * @throws DelaunayError
	 */
	private void updateExtensionPoints(Point pt) throws DelaunayError {
		if(extMinX == null){
			if(!points.isEmpty()){
				throw new DelaunayError("we should have added this coordinate before !");
			}
			extMinX = pt.getX()-1;
		} else if(pt.getX() < extMinX+1) {
			extMinX = pt.getX() - 1;
		}
		if(extMinY == null){
			if(!points.isEmpty()){
				throw new DelaunayError("we should have added this coordinate before !");
			}
			extMinY = pt.getY()-1;
			extMaxY = pt.getY()+1;
		} else {
			if(pt.getY() > extMaxY - 1){
				extMaxY = pt.getY() + 1;
			} else if (pt.getY() < extMinY + 1){
				extMinY = pt.getY() - 1;
			}
		}
		
	}

	/**
	 * Add an element to the list. This method takes care to ensure that we don't
	 * insert duplicated items in the list.
	 * @param <T extends Element & Comparable<? super T>>
	 * @param elt
	 * @param sortedList
	 */
	private <T extends Element & Comparable<? super T>> void addToSortedList(T elt, List<T> sortedList, int gid) {
		//We make a binary search, as divides and conquers rules...
		int index = Collections.binarySearch(sortedList, elt);
		if (index < 0) {
			//The position where we want to insert elt is -index-1, as the
			//value retruned by binary search is equal to (-insertPos -1)
			//(cf java.util.Collections javadoc
			//we don't process the insertion if an element equals to elt
			//is already contained in the list.
			int insertPos = -index - 1;
			sortedList.add(insertPos, elt);
			gid++;
			elt.setGID(gid);
		}
	}

	/**
	 * This methods will search the point p in the list.
	 * @param p
	 * @return the index of p, -1 if it's not in the list
	 */
	public final int listContainsPoint(Point p) {
		return sortedListContains(points, p);
	}

	/**
	 * Search the element elt in the sorted list sortedList. You are supposed
	 * to be sure that sortedList is actually sorted ;-)
	 * @param <T>
	 * @param sortedList
	 * @param elt
	 * @return
	 */
	private <T extends Element & Comparable<T>> int sortedListContains(List<T> sortedList, T elt) {
		//We make a binary search, as divides and conquers rules...
		int index = Collections.binarySearch(sortedList, elt);
		//binarySearch will return the index of the element if it is found
		//(-insertPosition -1) otherwise. Consequently, if index > 0
		//we are sure that elt is in the list.
		return (index < 0 ? -1 : index);
	}

	/**
	 * This method will force the integrity of the constraints used to compute
	 * the delaunay triangulation. After execution :
	 *  * duplicates are removed
	 *  * intersection points are added to the mesh points
	 *  * secant edges are split
	 */
	public final void forceConstraintIntegrity() throws DelaunayError {
		//The event points are the extremities and intersections of the
		//constraint edges. This list is created empty, and filled to stay
		//sorted.
		ArrayList<Point> eventPoints = new ArrayList<Point>();
		//We fill the list.
		for (Edge edge : constraintEdges) {
			addToSortedList(edge.getStart(), eventPoints, -2);
			addToSortedList(edge.getEnd(), eventPoints, -2);
		}
		//we are about to perform the sweepline algorithm
		Point currentEvent = null;
		//edgeBuffer will contain the edges sorted vertically
		VerticalList edgeBuffer = new VerticalList(0);
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
			edgeBuffer.setAbs(abs);
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
					edgeBuffer.addEdge(currentMemEdge);
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
							if (newEvent.equals2D(currentEvent)) {
								//We process the intersection.
								if (!newEvent.equals2D(e2.getPointLeft()) && !newEvent.equals2D(e2.getPointRight())) {
									//newEvent lies on e2, and is not an extremity
									if(newEvent.equals2D(e1.getPointLeft())){
										newEvent = e1.getPointLeft();
									}
									if(newEvent.equals2D(e1.getPointRight())){
										newEvent = e1.getPointRight();
									}
									inter2 = new Edge(newEvent, e2.getPointLeft() );
									addConstraintEdge(inter2);
									rm = edgeBuffer.remove(j);
									if (!rm.equals(e2)) {
										throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_REMOVING_EDGE);
									}
									inter4 = new Edge(newEvent, e2.getPointRight());
									edgeBuffer.addEdge(inter4);
									rmCount++;
								} else if (newEvent.equals2D(e2.getPointRight())) {
									addConstraintEdge(e2);
									rm = edgeBuffer.remove(j);
									if (!rm.equals(e2)) {
										throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_REMOVING_EDGE);
									}
									rmCount++;
								}
								if (!newEvent.equals2D(e1.getPointLeft()) && !newEvent.equals2D(e1.getPointRight())) {
									if(newEvent.equals2D(e2.getPointLeft())){
										newEvent = e2.getPointLeft();
									}
									if(newEvent.equals2D(e2.getPointRight())){
										newEvent = e2.getPointRight();
									}
									inter1 = new Edge(e1.getPointLeft(), newEvent);
									addConstraintEdge(inter1);
									rm = edgeBuffer.remove(j - 1);
									if (!rm.equals(e1)) {
										throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_REMOVING_EDGE);
									}
									inter3 = new Edge(e1.getPointRight(), newEvent);
									edgeBuffer.addEdge(inter3);
									rmCount++;
								} else if (newEvent.equals2D(e1.getPointRight())) {
									addConstraintEdge(e1);
									rm = edgeBuffer.remove(j - 1);
									if (!rm.equals(e1)) {
										throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_REMOVING_EDGE);
									}
									rmCount++;
								}
								j = (j - rmCount < 0 ? 0 : j - rmCount);
							} else { // the intersection will be processed later.
								addToSortedList(newEvent, eventPoints, -2);
							}
						} else {
							//in this case, we have e1.isExtremity(newEvent) && e2.isExtremity(newEvent)
							if (e2.getPointRight().equals2D(currentEvent)) {
								addConstraintEdge(e2);
								rm = edgeBuffer.remove(j);
								if (!rm.equals(e2)) {
									throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_REMOVING_EDGE);
								}
								rmCount++;
							}
							if (e1.getPointRight().equals2D(currentEvent)) {
								addConstraintEdge(e1);
								rm = edgeBuffer.remove(j - 1);
								if (!rm.equals(e1)) {
									throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_REMOVING_EDGE);
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
								throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_REMOVING_EDGE);
							}
							rm = edgeBuffer.remove(j - 1);
							if (!rm.equals(e1)) {
								throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_REMOVING_EDGE);
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
									mem = edgeBuffer.addEdge(inter1);
									j = j <= mem ? j : mem;
								}
							}
							if (inter2.getPointRight().compareTo2D(currentEvent) == 1) {
								//inter2 has to be processed for further intersections
								mem = edgeBuffer.addEdge(inter2);
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
							j = j - 2 < 0 ? 0 : j - 2;
						} else {
							throw new DelaunayError("We should already be on this event point");
						}

					} else {
						if (e1.getPointRight().equals2D(currentEvent)) {
							addConstraintEdge(e1);
							rm = edgeBuffer.remove(j - 1);
							if (!rm.equals(e1)) {
								throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_REMOVING_EDGE);
							}
							j--;
							j = j - 1 < 0 ? 0 : j - 1;
						}
					}
					j++;
					if(j>=edgeBuffer.size()){
						e2 = edgeBuffer.get(edgeBuffer.size()-1);
						if(e2.getPointRight().equals(currentEvent)){
							edgeBuffer.remove(edgeBuffer.size()-1);
							addConstraintEdge(e2);
						}
					}
				}
			} else if (edgeBuffer.size() == 1 && edgeBuffer.get(0).getPointRight().equals2D(currentEvent)) {
				addConstraintEdge(edgeBuffer.get(0));
				edgeBuffer.remove(0);
			}
			i++;
		}
	}

	/**
	 * This method will vertically sort the edges in edgeList, using the absciss of the
	 * point p given in parameter.
	 * @param edgeList
	 * @param p
	 * @throws DelaunayError
	 */
	public final void sortEdgesVertically(List<Edge> edgeList, Point p) throws DelaunayError {
		sortEdgesVertically(edgeList, p.getX());
	}

	/**
	 * This method will sort the edges contained in the ArrayList list by considering
	 * their intersection point with the line of equation x=abs, where a is given
	 * in parameter.
	 * @param edgeList
	 * @param x
	 */
	public final void sortEdgesVertically(List<Edge> edgeList, double abs) throws DelaunayError {
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
	public final int insertEdgeVerticalList(Edge edge, List<Edge> edgeList, double abs) throws DelaunayError {
		if (edgeList.isEmpty()) {
			edgeList.add(edge);
		}
		VerticalComparator comparator = new VerticalComparator(abs);
		return Tools.addToSortedList(edge, edgeList, comparator);
	}

	/**
	 * Check if the list given in argument is vertically sorted or not.
	 * @param edgeList
	 * @return
	 */
	public final boolean isVerticallySorted(List<Edge> edgeList, double abs) {
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
				log.error(e.getCause());
			}
		}
		return true;
	}

	/**
	 * Checks that edge does not intersect the existing edges of the mesh.
	 * @param edge
	 * @return
	 * @throws DelaunayError
	 */
	public final boolean intersectsExistingEdges(Edge edge) throws DelaunayError {
		int inter;
		for (Edge ed : edges) {
			inter = ed.intersects(edge);
			if (inter == Edge.INTERSECT || inter == Edge.SHARE_EDGE_PART) {
				return true;
			}
		}
		return false;
	}
	/**
	 * This method simply travels the list given in argument. If edges edgelist.get(i)
	 * and edgeList.get(i+1) intersect, then we add the intersection point in
	 * the eventList.
	 * @param edgeList
	 */
	public final void addPointsFromNeighbourEdges(List<Edge> edgeList, List<Point> eventList) throws DelaunayError {
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

	/**
	 * Get the list of constraint edges whose left point is left.
	 * @param left
	 * @return
	 */
	public final List<Edge> getConstraintsFromLeftPoint(Point left) {
		//The edge left-left is the minimum edge whose leftpoint is left.
		List<Edge> retList = new ArrayList();
		if (constraintEdges == null || constraintEdges.isEmpty()) {
			return retList;
		}
		int size = constraintEdges.size();
		Edge leftSearch = new Edge(left, left);
		int index = Collections.binarySearch(constraintEdges, leftSearch);
		index = index < 0 ? -index - 1 : index;
		while (index < size && constraintEdges.get(index).getPointLeft().equals(left)) {
			retList.add(constraintEdges.get(index));
			index++;
		}
		return retList;
	}

	/**
	 * Get the list of constraint edges whose left point is left, vertically sorted.
	 * @param left
	 * @return
	 */
	public final List<Edge> getConstraintFromLPVertical(Point left){
		List<Edge> retList = getConstraintsFromLeftPoint(left);
		VerticalComparator vc = new VerticalComparator(left.getX());
		Collections.sort(retList, vc);
		//Vertical constraints are managed in a way that put the potential one
		//linked to left at the beginning of the list. It shoule be the last one.
		if(!retList.isEmpty() && retList.get(0).isVertical()){
			Edge tmp = retList.get(0);
			retList.remove(0);
			retList.add(tmp);
		}
		return retList;
	}
	// ------------------------------------------------------------------------------------------

	/**
	 * Generate the Delaunay's triangularization with a flip-flop algorithm.
	 * Mesh must have been set. Triangulation can only be done once.
	 * Otherwise call reprocessDelaunay
	 *
	 * @throws DelaunayError
	 */
	public final void processDelaunay() throws DelaunayError {
		if (isMeshComputed()) {
			throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_GENERATED);
		} else if (points.size() < 3) {
			throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_NOT_ENOUGH_POINTS_FOUND);
		} else {
			// general data structures
			badEdgesQueueList = new LinkedList<Edge>();
			edges = new ArrayList<Edge>();
			triangleList = new ArrayList<DelaunayTriangle>();
			boundaryEdges = new ArrayList<Edge>();

			// sort points
			if (verbose) {
				log.trace("Getting points");
			}
			ListIterator<Point> iterPoint = points.listIterator();

			Point p1 = iterPoint.next();
			Point p2 = iterPoint.next();
			Edge e1 = new Edge(p1, p2);
			e1 = replaceByConstraint(e1);
			List<Edge> fromLeft = getConstraintFromLPVertical(p1);
			//This operaton connects the two first points and their linked constraints.
			Boundary bound = buildStartBoundary(p1, e1, fromLeft, getConstraintFromLPVertical(p2));
			

			while(iterPoint.hasNext()){
				p2=iterPoint.next();
				fromLeft = getConstraintFromLPVertical(p2);
				//The insertion is performed here !
				try{
				triangleList.addAll(bound.insertPoint(p2, fromLeft));
				} catch (Exception e){
					break;
				}
				//We retrieve the edges that have been added to the mesh.
				edges.addAll(bound.getAddedEdges());
				//We retrieve the potential bad edges, and treat them.
				badEdgesQueueList = bound.getBadEdges();
				processBadEdges();
			}

			meshComputed = true;

			// It's fine, we computed the mesh
			if (verbose) {
				log.trace("End processing");
				log.trace("Triangularization end phase : ");
				log.trace("  Points : " + points.size());
				log.trace("  Edges : " + edges.size());
				log.trace("  Triangles : " + triangleList.size());
			}
		}
	}

	/**
	 * Build the boundary needed to begin the building of the mesh.
	 * @param p1
	 * @param e1
	 * @param constraintsP1
	 * @return
	 */
	final Boundary buildStartBoundary(Point p1, Edge e1, List<Edge> constraintsP1, List<Edge> constraintsP2){
		BoundaryPart bp;
		Boundary bound = new Boundary();
		List<Edge> boundEdges = new LinkedList<Edge>();
		boundEdges.add(e1);
		//we need two different lists to avoid causing ConcurrentModificationException
		List<Edge> boundEdgesBis = new LinkedList<Edge>();
		boundEdgesBis.add(e1);
		//If p2 is not linked to any constraint, then [p1 p2] will be degenerated.
		//If there are constraints linked to p2, then [p1 p2] will be shared between
		//the uppest constraint linked to p1 that is lower than [p1 p2] and
		//the uppest constraint linked to p2.
		if(constraintsP2.isEmpty()){
			e1.setDegenerated(true);
		} else {
			e1.setShared(true);
		}
		List<BoundaryPart> bps = new ArrayList<BoundaryPart>();
		if(constraintsP1 == null || constraintsP1.isEmpty()){
			//We don't have to manage with any constraint.
			bp=new BoundaryPart(boundEdges);
			bps.add(bp);
			boundEdges = new LinkedList<Edge>();
			boundEdges.add(e1);
			//We add the constraints linked to p2, that will form other boundary parts.
			fillWithP2Constraints(boundEdgesBis, constraintsP2, bps, e1);
			if(!bps.isEmpty()){
				boundEdges = new LinkedList<Edge>();
				boundEdges.add(e1);
				bps.get(bps.size()-1).setBoundaryEdges(boundEdges);
			}
			bound.setBoundary(bps);
		} else {
			Edge current = constraintsP1.get(0);
			ListIterator<Edge> iter = constraintsP1.listIterator();
			boolean direct = current.getEndPoint().equals(current.getPointRight());
			if((direct && current.isRight(e1.getPointRight())) || (!direct && current.isLeft(e1.getPointRight())) ){
				//We can create a boundary part without constraint, as 
				//p2 is under all the constraints linked to p1.
				bp=new BoundaryPart(boundEdges);
				bps.add(bp);
				//We add the constraints linked to p2, that will form other boundary parts.
				fillWithP2Constraints(boundEdgesBis, constraintsP2, bps, e1);
				//We add the constraints linked to p1.
				while(iter.hasNext()){
					current = iter.next();
					bps.add(new BoundaryPart(current));
				}
			} else {
				//set will be set to true when the constraints linked to p2
				//will have been added.
				boolean set = false;
				Edge mem = iter.next();
				current = null;
				while(iter.hasNext()){
					current = iter.next();
					if(!set && (current.isRight(e1.getPointRight()) || current.getPointRight().equals(e1.getEndPoint()))){
						//We must not instanciate a BP where the constraint is a boundary Edge
						if(mem.equals(e1)){
							bps.add(new BoundaryPart(boundEdges));
						} else {
							bps.add(new BoundaryPart(boundEdges, mem));
						}
						//We add the constraints linked to p2, that will form other boundary parts.
						fillWithP2Constraints(boundEdgesBis, constraintsP2, bps, e1);
						mem=current;
						set = true;
					}else {
						//We must not consider e1 as a constraint edge linked to the boundary :
						//it's already part of the boundary.
						if(!set && e1.equals(current)){
							bps.add(new BoundaryPart(boundEdges, mem));
						} else {
							bps.add(new BoundaryPart(mem));
						}
						mem=current;
					}
				}
				//We must process the last constraint edge linked to p1.
				if(current != null){
					//if current.isRight(p2), the BoundaryPart that
					//contains e1 has already been added.
					if(current.isRight(e1.getPointRight())){
						bps.add(new BoundaryPart(current));
					} else if(!current.equals(e1)){
					//We still have to add the BP with e1
						bps.add(new BoundaryPart(boundEdges, current));
					}
				}
				if(!set){
					mem = mem.equals(e1) ? null : mem;
					bps.add(new BoundaryPart(boundEdges, mem));
					//We add the constraints linked to p2, that will form other boundary parts.
					fillWithP2Constraints(boundEdgesBis, constraintsP2, bps, e1);
				}
			}
		}
		//We must add e1 to the list of edges.
		edges.add(e1);
		bound.setBoundary(bps);
		return bound;
	}

	/**
	 * Fill the list of boundary parts with the boundary parts infered from p2
	 * @param boundaryEdges
	 *		The boundary Edges that will be used to fill the last BP
	 * @param constraintsP2
	 *		The constraints that will be used to build the BPs
	 * @param bps
	 *		The list where we will add the BPs
	 * @param e1
	 *		The already created Edge
	 */
	private void fillWithP2Constraints(List<Edge> boundaryEdges, List<Edge> constraintsP2, List<BoundaryPart> bps, Edge e1){
		Edge ed;
		for(int i = 0; i<constraintsP2.size()-1; i++){
			ed = constraintsP2.get(i);
			if(!ed.equals(e1)){
				bps.add(new BoundaryPart(ed));
			}
		}
		if(!constraintsP2.isEmpty()){
			ed = constraintsP2.get(constraintsP2.size()-1);
			if(!ed.equals(e1)){
				bps.add(new BoundaryPart(boundaryEdges, ed));
			}
		}
		
	}

	/**
	 * This method checks if edge is present in the constraint edges, and return
	 * the corresponding constraint edge if it is found.
	 *
	 * It reorders the points as in the edge given in parameter.
	 *
	 * @param edge
	 */
	private Edge replaceByConstraint(Edge edge) {
		int index = sortedListContains(constraintEdges, edge);
		Edge tempEdge = edge;
		if (index >= 0) {
			tempEdge = constraintEdges.get(index);
			if(!edge.getStartPoint().equals(tempEdge.getStartPoint())){
				tempEdge.swap();
			}
		}
		return tempEdge;
	}
	
	/**
	 * Process the flip-flop algorithm on the list of triangles
	 */
	private void processBadEdges() {
		if (!isMeshComputed()) {
			LinkedList<Edge> alreadySeen = new LinkedList<Edge>();
			while (!badEdgesQueueList.isEmpty()) {
				Edge anEdge = badEdgesQueueList.get(0);
				badEdgesQueueList.remove(0);

				boolean doIt = true;

				if (anEdge.isLocked()) {
					doIt = false;
				} else if (alreadySeen.contains(anEdge)) {
					doIt = false;
				}

				if (doIt) {
					alreadySeen.add(anEdge);
					// We cannot process marked edges
					// We check if the two triangles around the edge are ok
					DelaunayTriangle aTriangle1 = anEdge.getLeft();
					DelaunayTriangle aTriangle2 = anEdge.getRight();
					if ((aTriangle1 != null) && (aTriangle2 != null)
						&& swapTriangle(aTriangle1, aTriangle2, anEdge)) {
						// Add the triangle"s edges to the bad edges list
						Edge addEdge;
						for (int j = 0; j < 3; j++) {
							addEdge = aTriangle1.edges[j];
							if ((addEdge.getLeft() != null)
								&& (addEdge.getRight() != null)
								&& !addEdge.equals(anEdge)
								&& !badEdgesQueueList.contains(addEdge)) {
								badEdgesQueueList.add(addEdge);
							}
							addEdge = aTriangle2.edges[j];
							if ((addEdge.getLeft() != null)
								&& (addEdge.getRight() != null)
								&& !addEdge.equals(anEdge)
								&& !badEdgesQueueList.contains(addEdge)) {
								badEdgesQueueList.add(addEdge);
							}
						}
					}
				}
			}
		} else {
			while (!badEdgesQueueList.isEmpty()) {
				badEdgesQueueList.remove(0);
			}
		}
	}

	/**
	 * Swap two neighbour triangles, whose common edge is anEdge
	 * @param aTriangle1
	 * @param aTriangle2
	 * @param anEdge
	 * @param forced
	 * @return
	 */
	private boolean swapTriangle(DelaunayTriangle aTriangle1, DelaunayTriangle aTriangle2,
		Edge anEdge) {

		boolean exchange = false;
		Edge anEdge10, anEdge11, anEdge12;
		Edge anEdge20, anEdge21, anEdge22;
		Point p1, p2, p3, p4;

		if ((aTriangle1 != null) && (aTriangle2 != null)) {
			p1 = anEdge.getStartPoint();
			p2 = anEdge.getEndPoint();

			p3 = null;
			p4 = null;

			// Test for each triangle if the remaining point of the
			// other triangle is inside or not
			// DelaunayTriangle 1 is p1, p2, p3 or p2, p1, p3
			p3 = aTriangle1.getAlterPoint(p1, p2);
			if (p3 != null && aTriangle2.inCircle(p3) == 1) {
				exchange = true;
			}

			// DelaunayTriangle 2 is p2, p1, p4 or p1, p2, p4
			p4 = aTriangle2.getAlterPoint(p1, p2);
			if (p4 != null && aTriangle1.inCircle(p4) == 1) {
				exchange = true;
			}

			if (p3 != p4 && exchange) {
				anEdge10 = anEdge;
				anEdge11 = checkTwoPointsEdge(p3, p1, aTriangle1.edges, 3);
				anEdge12 = checkTwoPointsEdge(p1, p4, aTriangle2.edges, 3);
				anEdge20 = anEdge;
				anEdge21 = checkTwoPointsEdge(p2, p4, aTriangle2.edges, 3);
				anEdge22 = checkTwoPointsEdge(p3, p2, aTriangle1.edges, 3);
				if ((anEdge11 == null) || (anEdge12 == null) || (anEdge21 == null) || (anEdge22 == null)) {
					log.error("ERROR");
				} else {
					anEdge.setStartPoint(p3);
					anEdge.setEndPoint(p4);
					edgeGID++;
					anEdge.setGID(edgeGID);
					aTriangle1.edges[0] = anEdge10;
					aTriangle1.edges[1] = anEdge11;
					aTriangle1.edges[2] = anEdge12;
					aTriangle2.edges[0] = anEdge20;
					aTriangle2.edges[1] = anEdge21;
					aTriangle2.edges[2] = anEdge22;
					if (anEdge12.getLeft() == aTriangle2) {
						anEdge12.setLeft(aTriangle1);
					} else {
						anEdge12.setRight(aTriangle1);
					}
					if (anEdge22.getLeft() == aTriangle1) {
						anEdge22.setLeft(aTriangle2);
					} else {
						anEdge22.setRight(aTriangle2);
					}
					if (anEdge.isLeft(p1)) {
						anEdge.setLeft(aTriangle1);
						anEdge.setRight(aTriangle2);
					} else {
						anEdge.setLeft(aTriangle2);
						anEdge.setRight(aTriangle1);
					}
					aTriangle1.recomputeCenter();
					aTriangle2.recomputeCenter();
				}
			}
		}
		return exchange;
	}

	/**
	 * Check if the edge already exists. Returns null if it doesn't
	 *
	 * @param p1
	 * @param p2
	 * @param edgeQueueList
	 * @param size
	 *
	 * @return
	 */
	private Edge checkTwoPointsEdge(Point p1, Point p2,
		Edge[] edgeQueueList, int size) {
		// Check if the two points already lead to an existing edge.
		// If the edge exists it must be in the non-processed edges
		Edge theEdge = null;
		int i = 0;
		int max = (edgeQueueList.length < size ? edgeQueueList.length : size);
		while ((i < max) && (theEdge == null)) {
			Edge anEdge = edgeQueueList[i];
			if (((anEdge.getStartPoint().equals(p1)) && (anEdge.getEndPoint().equals( p2)))
				|| ((anEdge.getStartPoint().equals(p2)) && (anEdge.getEndPoint().equals(p1)))) {
				theEdge = anEdge;
			} else {
				i++;
			}
		}
		return theEdge;
	}

	/**
	 * Draw Mesh in the JPanel : triangles and edges. If duration is positive,
	 * also display it Must be used only when using package drawing
	 *
	 * @param g
	 */
	public final void displayObject(Graphics g) {
		try {
			Envelope theBox = getBoundingBox();
			double scaleX, scaleY;
			double minX, minY;
			int xSize = 1200;
			int ySize = 600;
			int decalageX = 10;
			int decalageY = ySize + 30;
			int legende = ySize + 60;
			int bordure = 10;

			scaleX = xSize / (theBox.getMaxX() - theBox.getMinX());
			scaleY = ySize / (theBox.getMaxY() - theBox.getMinY());
			if (scaleX > scaleY) {
				scaleX = scaleY;
			} else {
				scaleY = scaleX;
			}
			minX = theBox.getMinX();
			// minY = theBox.maxy;// coordinate 0 in Y is at top of screen (don't
			// forget make change in sub method)
			minY = theBox.getMinY();// coordinate 0 in Y is at bottom of screen
			scaleY = -scaleY;

			g.setColor(Color.white);
			g.fillRect(decalageX - bordure, decalageY - ySize - bordure, 2
				* bordure + xSize, 2 * bordure + ySize);
			g.fillRect(decalageX - bordure, legende - bordure, 2 * bordure + xSize,
				2 * bordure + 50);

			g.setColor(Color.black);

			// Draw triangles
			if (!triangleList.isEmpty()) {
				for (DelaunayTriangle aTriangle : triangleList) {
					aTriangle.displayObject(g, decalageX, decalageY, minX, minY,
						scaleX, scaleY);
				}

//				if (displayCircles) {
//					for (DelaunayTriangle aTriangle : trianglesQuadTree.getAll()) {
//						aTriangle.displayObjectCircles(g, decalageX, decalageY);
//					}
//				}
			}

			// Draw lines
			if (!constraintEdges.isEmpty()) {
				for (Edge aVertex : constraintEdges) {
					aVertex.displayObject(g, decalageX, decalageY, minX, minY, scaleX, scaleY);
				}
			}

			if (!edges.isEmpty()) {
				for (Edge aVertex : edges) {
					aVertex.displayObject(g, decalageX, decalageY, minX, minY, scaleX, scaleY);
				}
			}

			if ((points.size() > 0) && (points.size() < 100)) {
				for (Point aPoint : points) {
					aPoint.displayObject(g, decalageX, decalageY, minX, minY,
						scaleX, scaleY);
				}
			}
		} catch (Exception e) {
			log.warn("Problem during rendering\n", e);
		}
	}
}
