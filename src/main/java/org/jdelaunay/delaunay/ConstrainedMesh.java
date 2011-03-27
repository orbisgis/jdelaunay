package org.jdelaunay.delaunay;

import com.vividsolutions.jts.geom.Envelope;
import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * This class is used to compute the constrained delaunay triangulation on a set of
 * points and constraint edges. The constraints can be validated before the triangulation.
 *
 * After the processing of the mesh with processDelaunay, the ConstrainedMesh can
 * be identified with three set of data, plus its input constraint edges. The three
 * sets are :
 *
 *   * The points of the mesh.
 *
 *   * The edges of the mesh.
 *
 *   * The triangles of the mesh.
 *
 * When processing elevation data, you can use the removeFlatTriangles method to
 * be sure that none of the triangles are horizontal. This can be useful when the
 * triangulation is supposed to be used for hydrology.
 *
 * @author alexis
 */
public class ConstrainedMesh implements Serializable {
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(ConstrainedMesh.class);
	//The list of triangles during the triangulation process.
	//This list is sorted by using the implementation of Comparable in
	//DTriangle.
	private List<DTriangle> triangleList;
	//The list of edges.
	private List<DEdge> edges;
	//The list of points used during the triangulation
	private List<DPoint> points;
	//The lis of constraints used during the triangulation
	private List<DEdge> constraintEdges;
	//A list of polygons that will be emptied after the triangulation
	private List<ConstraintPolygon> polygons;
	//
	private double precision;
	//The minimum distance between two distinct points
	private double tolerance;
	//The two following lists are used only during computation.
	//The bad edge queue list contains all the edges that coud be changed
	//during a flip-flap operation
	private transient List<DEdge> badEdgesQueueList;
	//Permits to know if the mesh has been computed or not
	private boolean meshComputed;
	//Is the debug level used ?
	private boolean verbose;
	// GIDs
	private int pointGID;
	private int edgeGID;
	private int triangleGID;
	//We need a hashmap to classify the weights of the edges, according to their
	//properties.
	private Map<Integer, Integer> weights;
	// constants
	public static final int MIN_POINTS_NUMBER = 3;
	public static final int MAXITER = 5;
	public static final int REFINEMENT_MAX_AREA = 1;
	public static final int REFINEMENT_MIN_ANGLE = 2;
	public static final int REFINEMENT_SOFT_INTERPOLATE = 4;
	public static final int REFINEMENT_OBTUSE_ANGLE = 8;

	//The two points that will be used to extend the mesh, and to reduce the number
	//of edges in the boundary. They will be removed when the mesh will be computed,
	//and the mesh will be fixed.
	private Double extMinX = null;
	private Double extMaxY = null;
	private Double extMinY = null;

	/**
	 * Build a new, empty, ConstrainedMesh. It does not conatin any information
	 * that could be used to build a triangulation. You must fill it with
	 * the points and edges you need before launching a processDelaunay() operation.
	 */
	public ConstrainedMesh() {
		triangleList = new ArrayList<DTriangle>();
		edges = new ArrayList<DEdge>();
		constraintEdges = new ArrayList<DEdge>();
		points = new ArrayList<DPoint>();
		polygons = new ArrayList<ConstraintPolygon>();
		meshComputed = false;
		precision = 0;
		tolerance = Tools.EPSILON;
		pointGID = 0;
		edgeGID = 0;
		triangleGID = 0;
		weights = new HashMap<Integer, Integer>();
		badEdgesQueueList = new LinkedList<DEdge>();
	}

	/**
	 * Get the list of edges that are to be processed by the flip flap algorithm
	 * @return
	 */
	public final List<DEdge> getBadEdgesQueueList() {
		return badEdgesQueueList;
	}

	/**
	 * Set the list of edges that are to be processed by the flip flap algorithm
	 * @param badEdgesQueueList
	 */
	public final void setBadEdgesQueueList(LinkedList<DEdge> badEdgesQueueList) {
		this.badEdgesQueueList = badEdgesQueueList;
	}

	/**
	 * Get the list of edges that are used as constraints during triangulation
	 * @return
	 */
	public final List<DEdge> getConstraintEdges() {
		return constraintEdges;
	}

	/**
	 * Set the list of edges that are used as constraints during triangulation
	 * @param constraintEdges
	 */
	public final void setConstraintEdges(ArrayList<DEdge> constraint) throws DelaunayError {
		this.constraintEdges = new ArrayList<DEdge>();
		for (DEdge e : constraint) {
			//We lock the edge. It will not be supposed to be switched
			//during a flip flap.
			e.setLocked(true);
			fixConstraintDirection(e);
			addConstraintEdge(e);
		}
	}

	/**
	 * Add an edge to the list of constraint edges.
	 * @param e
	 *	the edge we want to add
	 */
	public final void addConstraintEdge(DEdge e) throws DelaunayError {
		if (constraintEdges == null) {
			constraintEdges = new ArrayList<DEdge>();
		}
		fixConstraintDirection(e);
		int index = Collections.binarySearch(points, e.getStartPoint());
		if(index < 0 ){
			updateExtensionPoints(e.getStartPoint());
			points.add(-index -1, e.getStartPoint());
			pointGID++;
			e.getStartPoint().setGID(pointGID);
		} else {
			e.setStartPoint(points.get(index));
		}
		if(e.getStartPoint().equals(e.getEndPoint())){
			return;
		}
		e.setLocked(true);
		addEdgeToLeftSortedList(constraintEdges, e);
		index = Collections.binarySearch(points, e.getEndPoint());
		if(index < 0 ){
			updateExtensionPoints(e.getEndPoint());
			points.add(-index -1, e.getEndPoint());
			pointGID++;
			e.getEndPoint().setGID(pointGID);
		} else {
			e.setEndPoint(points.get(index));
		}
	}

	/**
	 * Get the list of edges
	 * @return
	 */
	public final List<DEdge> getEdges() {
		return edges;
	}

	/**
	 * Set the list of edges
	 * @param constraintEdges
	 */
	public final void setEdges(List<DEdge> constraint) throws DelaunayError {
		this.edges = new ArrayList<DEdge>();
		for (DEdge e : constraint) {
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
	public final void addEdge(DEdge e) {
		if (edges == null) {
			edges = new ArrayList<DEdge>();
		}
		int constraintIndex = sortedListContains(constraintEdges, e);
		if (constraintIndex < 0) {
			addEdgeToLeftSortedList(edges, e);
			edgeGID++;
			e.setGID(edgeGID);
		} else {
			addEdgeToLeftSortedList(edges, constraintEdges.get(constraintIndex));
		}
	}

	/**
	 * Remove an DEdge from the list of edges.
	 * @param e
	 */
	public final void removeEdge(DEdge e) {
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
	public final List<DEdge> sortEdgesLeft(List<DEdge> inputList) {
		ArrayList<DEdge> outputList = new ArrayList<DEdge>();
		for (DEdge e : inputList) {
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
	private boolean addEdgeToLeftSortedList(List<DEdge> sorted, DEdge edge) {
		return addToSortedList(edge, sorted);
	}

	/**
	 * Search an edge in the list of edges.
	 * @param edge
	 * @return
	 */
	public final int searchEdge(DEdge edge) {
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
	public final List<DTriangle> getTriangleList() {
		return triangleList;
	}

	/**
	 * Add a triangle to the current constrained mesh
	 * @param triangle
	 */
	public final void addTriangle(DTriangle triangle) {
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
	public final int containsTriangle(DTriangle tri) {
		return sortedListContains(triangleList, tri);
	}

	/**
	 * Remove a triangle from the list of triangles
	 * @param tri
	 */
	public final void removeTriangle(DTriangle tri) {
//		//first we search it
		triangleList.remove(tri);
	}

	/**
	 * Get the points contained in this mesh
	 * @return
	 */
	public final List<DPoint> getPoints() {
		return points;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public final DPoint getPoint(double x, double y, double z) throws DelaunayError{
		DPoint pt = new DPoint(x,y,z);
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
		for (DPoint p : points) {
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
	public final void setPoints(List<DPoint> pts) throws DelaunayError {
		if(pts == null){
			points = new ArrayList<DPoint>();
		} else {
			Collections.sort(pts);
			points = new ArrayList<DPoint>();
			extMaxY = null;
			extMinY = null;
			extMinX = null;
			for(DPoint pt : pts){
				updateExtensionPoints(pt);
			}
			this.points = pts;
			//We must be sure that we don't have duplicates in the list
			ListIterator<DPoint> iter = points.listIterator();
			if(iter.hasNext()){
				DPoint e1 = iter.next();
				DPoint e2;
				while(iter.hasNext()){
					e2=e1;
					e1=iter.next();
					if(e1.equals(e2)){
						iter.remove();
					}
				}
			}
		}
	}

	/**
	 * Add a new point in the list that will be used to perform the triangulation.
	 * The list of points is supposed to be sorted.
	 * @param point
	 */
	public final void addPoint(DPoint point) throws DelaunayError {
		if (points == null) {
			points = new ArrayList<DPoint>();
		}
		updateExtensionPoints(point);
		boolean res = addToSortedList(point, points);
		if(res){
			pointGID++;
			point.setGID(pointGID);
		}
	}

	/**
	 * Get the extension points that would be added to the mesh while computing
	 * DT or CDT with the current set of points.
	 * @return
	 *	A set of two points. They will share the same x-coordinate, that is
	 *	the max x-coordinate of the mesh minus 1.
	 *	The first one will have the max y-coordinate (ie the max y-coordinate of
	 *	the mesh plus 1), the second will have the min y-coordinate (ie the min
	 *	y-coordinate minus 1).
	 * @throws DelaunayError
	 */
	public final List<DPoint> getExtensionPoints() throws DelaunayError{
		ArrayList<DPoint> ret = new ArrayList();
		ret.add(new DPoint(extMinX, extMaxY, 0));
		ret.add(new DPoint(extMinX, extMinY, 0));
		return ret;
	}

	/**
	 * This method update the coordinates of the extension points that will be used
	 * during the triangulation
	 * @param pt
	 * @throws DelaunayError
	 */
	private void updateExtensionPoints(DPoint pt) throws DelaunayError {
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
	private <T extends Element & Comparable<? super T>> boolean addToSortedList(T elt, List<T> sortedList) {
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
			return true;
		}
		return false;
	}

	/**
	 * This methods will search the point p in the list.
	 * @param p
	 * @return the index of p, -1 if it's not in the list
	 */
	public final int listContainsPoint(DPoint p) {
		return sortedListContains(points, p);
	}

	/**
	 * Get the table that currently contains the weights used to attribute the
	 * Z value when processing the intersection.
	 * @return
	 */
	public final Map<Integer, Integer> getWeights() {
		return weights;
	}

	/**
	 * Set the table of weights that are used when processing the intersection between
	 * edges, during the call to <code>forceConstraintsIntegrity()</code>.
	 *
	 * Keys of the map match the property of each edge. Note that we are working on bits,
	 * and than the expected wlues will certainly be power of 2 (ie 1, 2, 4, 8...).
	 *
	 * Check the <code>Element</code> javadoc for more information about
	 * <code>property</code>
	 *
	 * @param weights
	 */
	public final void setWeights(HashMap<Integer, Integer> weights) {
		if(weights == null){
			weights = new HashMap<Integer, Integer>();
		}
		this.weights = weights;
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
         * 
         * This methos is supposed to be used just before a call to processDelaunay().
         * If you use it after, you will break the unicity of edges GID.
	 */
	public final void forceConstraintIntegrity() throws DelaunayError {
                //We will repopulate the list of constraint edges
                edgeGID = 0;
		//The event points are the extremities and intersections of the
		//constraint edges. This list is created empty, and filled to stay
		//sorted.
		List<DPoint> eventPoints = points;
		//we are about to perform the sweepline algorithm
		DPoint currentEvent = null;
		//edgeBuffer will contain the edges sorted vertically
		VerticalList edgeBuffer = new VerticalList(0);
		//We keep a shallow copy of constraintEdges...
		List<DEdge> edgeMemory = constraintEdges;
		//...and we empty it
		constraintEdges = new ArrayList<DEdge>();
		//The absciss where we search the intersections
		double abs;
		//Used in the  loop...
		int i = 0;//The first while
		int j = 0;//the inner while
		DEdge e1, e2; //the edges that will be compared in the for loop
		DEdge inter1 = null;// the edges resulting of the intersection.
		DEdge inter2 = null;
		DEdge inter3 = null;
		DEdge inter4 = null;
		DPoint newEvent = null;//the event that will be added to the eventList
		DEdge edgeEvent = null;//used when the intersection is an edge
		DPoint leftMost = null;
		DPoint rightMost = null;
		Element intersection = null;
		DEdge currentMemEdge = null;
		DEdge rm;
		int memoryPos = 0;
		int rmCount;
		int mem;
		while (i < eventPoints.size()) {
			//The max weight used to compute the current intersection
			int maxWeight = Integer.MIN_VALUE;
			//We must remember what the z value was.
			Double z = Double.NaN;
			int w1;
			int w2;
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
					intersection = e1.getIntersection(e2,weights);
					rmCount = 0;
					if (intersection instanceof DPoint) {
						//We have a single intersection point.
						//We must check it's not at an extremity.
						newEvent = (DPoint) intersection;
						if (!e1.isExtremity(newEvent) || !e2.isExtremity(newEvent)) {
							//We've found an intersection between two non-colinear edges
							//We must check that this intersection point is not
							//the current event point. If it is, we must process the
							if (newEvent.equals2D(currentEvent)) {
								//intersection.
								if(!weights.isEmpty()){
									w1 = e1.getMaxWeight(weights);
									w2 = e2.getMaxWeight(weights);
									if(w1<maxWeight && w2<maxWeight){
										if(Double.isNaN(z)){
											throw new DelaunayError("you're not supposed to have a NaN here !");
										}
										newEvent.setZ(z);
									}else{
										maxWeight=Math.max(w1,w2);
										z = newEvent.getZ();
                                                                                //We want to keep the z value in the actual event,
                                                                                //as it won't be erased by the new one.
                                                                                currentEvent.setZ(z);
									}
								}
								//We process the intersection.
								newEvent.setX(abs);
								List<DEdge> toBeInsert = new ArrayList<DEdge>();
								if (!newEvent.equals2D(e2.getPointLeft()) && !newEvent.equals2D(e2.getPointRight())) {
									//newEvent lies on e2, and is not an extremity
									if(newEvent.equals2D(e1.getPointLeft())){
										newEvent = e1.getPointLeft();
									}
									if(newEvent.equals2D(e1.getPointRight())){
										newEvent = e1.getPointRight();
									}
									inter2 = new DEdge(newEvent, e2.getPointLeft() );
									inter2.setProperty(e2.getProperty());
									addConstraintEdge(inter2);
									rm = edgeBuffer.remove(j);
									if (!rm.equals(e2)) {
										throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_REMOVING_EDGE);
									}
									inter4 = new DEdge(newEvent, e2.getPointRight());
									inter4.setProperty(e2.getProperty());
									toBeInsert.add(inter4);
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
									inter1 = new DEdge(e1.getPointLeft(), newEvent);
									inter1.setProperty(e1.getProperty());
									addConstraintEdge(inter1);
									rm = edgeBuffer.remove(j - 1);
									if (!rm.equals(e1)) {
										throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_REMOVING_EDGE);
									}
									inter3 = new DEdge(e1.getPointRight(), newEvent);
									inter3.setProperty(e1.getProperty());
									toBeInsert.add(inter3);
									rmCount++;
								} else if (newEvent.equals2D(e1.getPointRight())) {
									addConstraintEdge(e1);
									rm = edgeBuffer.remove(j - 1);
									if (!rm.equals(e1)) {
										throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_REMOVING_EDGE);
									}
									rmCount++;
								}
								for(DEdge yed : toBeInsert){
									edgeBuffer.addEdge(yed);
								}
								j = (j - rmCount < 0 ? 0 : j - rmCount);
							} else { // the intersection will be processed later.
								ensurePointPosition(e2, newEvent);
								ensurePointPosition(e1, newEvent);
								addToSortedList(newEvent, eventPoints);
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
							} else if (e1.getPointRight().equals2D(currentEvent)) {
								//We must not remove two edges in the same move.
								addConstraintEdge(e1);
								rm = edgeBuffer.remove(j - 1);
								if (!rm.equals(e1)) {
									throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_REMOVING_EDGE);
								}
								rmCount++;
								j--;
							}
							j = (j - rmCount < 0 ? 0 : j - rmCount);
						}
					} else if (intersection instanceof DEdge) {
						//The intersection is an edge. There are two possible cases :
						//The left point of the intersection is at the extremity of e1 and e2 : we
						//register the right point as an event
						//The left point is the extremity of e1 OR (exclusive) of e2. It is an event,
						//and certainly the current one.
						edgeEvent = (DEdge) intersection;
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
								inter1 = new DEdge(leftMost, newEvent);
							}
							inter2 = edgeEvent;
							if (rightMost.compareTo2D(edgeEvent.getPointRight()) == 1) {
								inter3 = new DEdge(edgeEvent.getPointRight(), rightMost);
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

					} else if(e1.contains(currentEvent) && !e1.isExtremity(currentEvent)){
                                                DEdge inter = new DEdge(e1.getPointLeft(),currentEvent);
                                                inter.setProperty(e1.getProperty());
                                                inter.setLocked(e1.isLocked());
                                                addConstraintEdge(inter);
                                                if(e1.getStartPoint().equals(e1.getPointLeft()) ){
                                                        e1.setStartPoint(currentEvent);
                                                } else {
                                                        e1.setEndPoint(currentEvent);
                                                }
                                                if(!weights.isEmpty()){
                                                        int w = e1.getMaxWeight(weights);
                                                        if(w>maxWeight){
                                                                maxWeight = w;
                                                        }
                                                }
                                        } else {
						//if the current event is the right point of e1, we
						//can remove e1 from the buffer and add it to
						//the constraints.
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
					if(edgeBuffer.size()>0 && j>=edgeBuffer.size()){
						e2 = edgeBuffer.get(edgeBuffer.size()-1);
                                                if(e2.contains(currentEvent) && !e2.isExtremity(currentEvent)){
                                                        DEdge temp = new DEdge(e2.getPointLeft(), currentEvent);
                                                        temp.setLocked(e2.isLocked());
                                                        temp.setProperty(e2.getProperty());
                                                        addConstraintEdge(temp);
                                                        if(e2.getStartPoint().equals(e2.getPointLeft())){
                                                                e2.setStartPoint(currentEvent);
                                                        } else {
                                                                e2.setEndPoint(currentEvent);
                                                        }
                                                        
                                                }
						if(e2.getPointRight().equals(currentEvent)){
							edgeBuffer.remove(edgeBuffer.size()-1);
							addConstraintEdge(e2);
						}
					}
				}
                                //If we have only one constraint edge in the buffer, and the
                                //event is its right point, then we remove it from the buffer
                                //and add it to the list of constraints.
			} else if (edgeBuffer.size() == 1){ 
                                DEdge e0 = edgeBuffer.get(0);
                                if(e0.contains(currentEvent) && !e0.isExtremity(currentEvent)){
                                        DEdge temp = new DEdge(e0.getPointLeft(), currentEvent);
                                        temp.setLocked(e0.isLocked());
                                        temp.setProperty(e0.getProperty());
                                        addConstraintEdge(temp);
                                        if(e0.getStartPoint().equals(e0.getPointLeft())){
                                                e0.setStartPoint(currentEvent);
                                        } else {
                                                e0.setEndPoint(currentEvent);
                                        }
                                } else if( e0.getPointRight().equals2D(currentEvent)) {
                                        addConstraintEdge(edgeBuffer.get(0));
                                        edgeBuffer.remove(0);
                                }
			}
			i++;
		}
	}

        /**
         * Ensure that we don't create duplicate points during the intersection processing.
         * If an event is found to be at a distance inferior to EPSILON from an existing
         * point, then the existing point is moved to become exactly the said event.
         * @param e
         * @param p 
         */
	private void ensurePointPosition(DEdge e, DPoint p) {
		if(e.getStart().equals2D(p)){
			e.getStart().setX(p.getX());
			e.getStart().setY(p.getY());
		}
		if(e.getEnd().equals2D(p)){
			e.getEnd().setX(p.getX());
			e.getEnd().setY(p.getY());
		}
	}

	/**
	 * This method will vertically sort the edges in edgeList, using the absciss of the
	 * point p given in parameter.
	 * @param edgeList
	 * @param p
	 * @throws DelaunayError
	 */
	public final void sortEdgesVertically(List<DEdge> edgeList, DPoint p) throws DelaunayError {
		sortEdgesVertically(edgeList, p.getX());
	}

	/**
	 * This method will sort the edges contained in the ArrayList list by considering
	 * their intersection point with the line of equation x=abs, where a is given
	 * in parameter.
	 * @param edgeList
	 * @param x
	 */
	public final void sortEdgesVertically(List<DEdge> edgeList, double abs) throws DelaunayError {
		int s = edgeList.size();
		int i = 0;
		int c = 0;
		DEdge e1;
		DEdge e2;
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
	 * This method will insert a new DEdge in a vertically sorted list, as described in
	 * sortEdgesVertically.
	 * Be careful when using this method. In fact, you must use the same absciss
	 * here that the one which has been used when sorting the list.
	 * @param edge
	 * @param edgeList
	 */
	public final int insertEdgeVerticalList(DEdge edge, List<DEdge> edgeList, double abs) throws DelaunayError {
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
	public final boolean isVerticallySorted(List<DEdge> edgeList, double abs) {
		DEdge e1, e2;
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
	public final boolean intersectsExistingEdges(DEdge edge) throws DelaunayError {
		int inter;
		for (DEdge ed : edges) {
			inter = ed.intersects(edge);
			if (inter == DEdge.INTERSECT || inter == DEdge.SHARE_EDGE_PART) {
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
	public final void addPointsFromNeighbourEdges(List<DEdge> edgeList, List<DPoint> eventList) throws DelaunayError {
		DEdge e1;
		DEdge e2;
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
					if (inter instanceof DPoint) {
						eventList.add((DPoint) inter);
					} else {
						eventList.add(((DEdge) inter).getPointLeft());
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
	public final List<DEdge> getConstraintsFromLeftPoint(DPoint left) {
		//The edge left-left is the minimum edge whose leftpoint is left.
		List<DEdge> retList = new ArrayList();
		if (constraintEdges == null || constraintEdges.isEmpty()) {
			return retList;
		}
		int size = constraintEdges.size();
		DEdge leftSearch = new DEdge(left, left);
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
	public final List<DEdge> getConstraintFromLPVertical(DPoint left){
		List<DEdge> retList = getConstraintsFromLeftPoint(left);
		VerticalComparator vc = new VerticalComparator(left.getX());
		Collections.sort(retList, vc);
		//Vertical constraints are managed in a way that put the potential one
		//linked to left at the beginning of the list. It shoule be the last one.
		if(!retList.isEmpty() && retList.get(0).isVertical()){
			DEdge tmp = retList.get(0);
			retList.remove(0);
			retList.add(tmp);
		}
		return retList;
	}
	// ------------------------------------------------------------------------------------------

	/**
	 * Generate the Delaunay's triangularization with a flip-flap algorithm.
	 * Mesh must have been set. Triangulation can only be done once.
	 * Otherwise call reprocessDelaunay
	 *
	 * @throws DelaunayError
	 */
	public final void processDelaunay() throws DelaunayError {
		if (isMeshComputed()) {
			throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_GENERATED);
		} else if (points.size() < MIN_POINTS_NUMBER) {
			throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_NOT_ENOUGH_POINTS_FOUND);
		} else {
                        pointGID=0;
                        for(DPoint pt : points){
                                pt.setGID(++pointGID);
                        }
                        //We will repopulate the list of triangles. 
                        triangleGID=0;
			// general data structures
			badEdgesQueueList = new LinkedList<DEdge>();
			edges = new ArrayList<DEdge>();
			triangleList = new ArrayList<DTriangle>();

			// sort points
			if (verbose) {
				log.trace("Getting points");
			}
			ListIterator<DPoint> iterPoint = points.listIterator();

			DPoint p1 = iterPoint.next();
			DPoint p2 = iterPoint.next();
			DEdge e1 = new DEdge(p1, p2);
			e1 = replaceByConstraint(e1);
			List<DEdge> fromLeft = getConstraintFromLPVertical(p1);
			//This operaton connects the two first points and their linked constraints.
			Boundary bound = buildStartBoundary(p1, e1, fromLeft, getConstraintFromLPVertical(p2));
			List<DEdge> added ;
			List<DTriangle> tri;
			while(iterPoint.hasNext()){
				p2=iterPoint.next();
				fromLeft = getConstraintFromLPVertical(p2);
				//The insertion is performed here !
				tri = bound.insertPoint(p2, fromLeft);
				for(DTriangle t : tri){
					triangleGID++;
					t.setGID(triangleGID);
				}
				triangleList.addAll(tri);

				//We retrieve the edges that have been added to the mesh.
				added = bound.getAddedEdges();
				for(DEdge e : added){
					edgeGID++;
					e.setGID(edgeGID);
				}
				edges.addAll(added);
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
	 * This operation remove the flat triangles by inserting new points in the mesh,
	 * that come from the skeleton of the already computed mesh.
	 * This method must be used after a previous call to processDelaunay().
	 * This method will compute a triangulation again - the insertion is not incremental.
	 * @throws DelaunayError
	 */
	public final void removeFlatTriangles() throws DelaunayError {
		//if the mesh has not been computed, we throw an exception.
		if(!meshComputed){
			throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_GENERATED);
		}
		if((!triangleList.isEmpty() && triangleList.get(0).isSeenForFlatRemoval())){
			for(DTriangle tri : triangleList){
				tri.setSeenForFlatRemoval(false);
			}
		}
		List<DPoint> newPoints = new ArrayList<DPoint>();
		VoronoiGraph vg;
		for(DTriangle tri : triangleList){
			if(!tri.isSeenForFlatRemoval()){
				if(tri.isFlatSlope()){
					vg = new VoronoiGraph(tri);
					vg.fillUntilNotFlatFound();
					vg.assignZValues();
					if(vg.isUseful()){
						newPoints.addAll(vg.getSkeletonPoints());
					}
				} else {
					tri.setSeenForFlatRemoval(true);
				}
			}
		}
		for(DPoint pt : newPoints){
			pt.setGID(++pointGID);
		}
		points.addAll(newPoints);
		Collections.sort(points);
		setMeshComputed(false);
		triangleList = new ArrayList<DTriangle>();
		for(DEdge e : constraintEdges){
			e.setLeft(null);
			e.setRight(null);
			fixConstraintDirection(e);
		}
		processDelaunay();
	}

	/**
	 * Refine the mesh, using the Rupper's algorithm.
	 * @param minLength
	 *		The minimum length of an edge that could be inserted during the refinement.
	 * @throws DelaunayError
	 */
	public final void refineMesh(double minLength) throws DelaunayError {
		int sizeEdges = edges.size();
		DEdge ed;
		for(int i = 0; i< sizeEdges; i++){
			ed = edges.get(i);
			if(ed.isEncroached()){
				splitEncroachedEdge(ed, minLength);
			}
		}
	}

	/**
	 * Split the edges that have benn found to be encroached.
	 * @param ed
	 *		The edge to be split.
	 * @param minLength
	 *		The threshold used to determine the minimum length of an
	 *		edge that could be added by splitting an encroached edge.
	 * @throws DelaunayError
	 */
	final void splitEncroachedEdge(DEdge ed, double minLength) throws DelaunayError {
		//We must try to avoid creation of new objects. Rather use getters and setters
		//instead, as we will not be forced to use sorted sets this way.
		DTriangle left = ed.getLeft();
		DTriangle right = ed.getRight();
		DPoint middle = ed.getMiddle();
		//The newly generated edge.
		DEdge secondHalf = new DEdge(middle, ed.getEndPoint());
		if(secondHalf.getSquared2DLength() < minLength*minLength){
			return;
		}
		middle.setGID(++pointGID);
		points.add(middle);
		secondHalf.setGID(++edgeGID);
		DEdge ed1 = null;
		DEdge last1 = null;
		DEdge startOp1 = null;
		DTriangle other1 = null;
		//We prepare the objects that will be added after ed will have been split.
		if(left != null){
			ed1 = new DEdge(middle, left.getOppositePoint(ed));
			ed1.setGID(++edgeGID);
			last1 = left.getOppositeEdge(ed.getEndPoint());
			startOp1 = left.getOppositeEdge(ed.getStartPoint());
			other1 = new DTriangle(ed1, secondHalf, startOp1);
			other1.setGID(++triangleGID);
		}
		DEdge ed2 = null;
		DEdge last2 = null;
		DEdge startOp2 = null;
		DTriangle other2 = null;
		if(right != null){
			ed2 = new DEdge(middle, right.getOppositePoint(ed));
			ed2.setGID(++edgeGID);
			last2 = right.getOppositeEdge(ed.getEndPoint());
			startOp2 = right.getOppositeEdge(ed.getStartPoint());
			other2 = new DTriangle(ed2, secondHalf, startOp2);
			other2.setGID(++triangleGID);
		}
		//this new edge is locked if ed was.
		secondHalf.setLocked(ed.isLocked());
		//We must set a new end to ed.
		ed.setEndPoint(middle);
		badEdgesQueueList = new LinkedList<DEdge>();
		//We try to process the left triangle of the encroached edge
		if(left != null){
			//we must replace an edge of left
			int indexExc = left.getEdgeIndex(startOp1);
			left.setEdge(indexExc, ed1);
			//We set the right and left triangles of each edge properly
			ed1.setLeft(left);
			ed1.setRight(other1);
			if(startOp1.isRight(middle)){
				startOp1.setRight(other1);
			} else {
				startOp1.setLeft(other1);
			}
			secondHalf.setLeft(other1);
			//We add the new triangle to the list of triangles.
			triangleList.add(other1);
			//We fill the bad edges queue.
			badEdgesQueueList.add(last1);
			badEdgesQueueList.add(ed1);
			badEdgesQueueList.add(startOp1);
			edges.add(ed1);
		}
		//We try to process the right triangle of the encroached edge
		if(right != null){
			//we must replace an edge of right
			int indexExc = right.getEdgeIndex(startOp2);
			right.setEdge(indexExc, ed2);
			//We set the right and left triangles of each edge properly
			ed2.setRight(left);
			ed2.setLeft(other2);
			if(startOp2.isRight(middle)){
				startOp2.setRight(other2);
			} else {
				startOp2.setLeft(other2);
			}
			secondHalf.setRight(other2);
			//We add the new triangle to the list of triangles.
			triangleList.add(other2);
			//We fill the bad edges queue.
			badEdgesQueueList.add(last2);
			badEdgesQueueList.add(ed2);
			badEdgesQueueList.add(startOp2);
			edges.add(ed2);
		}
		//We perform the filap flap operations.
		processBadEdges();
		//The algorithm is recursive, let's continue !
		constraintEdges.add(secondHalf);
		edges.add(secondHalf);
		if(ed.isEncroached()){
			splitEncroachedEdge(ed, minLength);
		}
		if(secondHalf.isEncroached()){
			splitEncroachedEdge(secondHalf, minLength);
		}
	}

	/**
	 * Build the boundary needed to begin the building of the mesh.
	 * @param p1
	 * @param e1
	 * @param constraintsP1
	 * @return
	 */
	final Boundary buildStartBoundary(DPoint p1, DEdge e1, List<DEdge> constraintsP1, List<DEdge> constraintsP2){
		BoundaryPart bp;
		Boundary bound = new Boundary();
		List<DEdge> boundEdges = new LinkedList<DEdge>();
		boundEdges.add(e1);
		//we need two different lists to avoid causing ConcurrentModificationException
		List<DEdge> boundEdgesBis = new LinkedList<DEdge>();
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
			boundEdges = new LinkedList<DEdge>();
			boundEdges.add(e1);
			//We add the constraints linked to p2, that will form other boundary parts.
			fillWithP2Constraints(boundEdgesBis, constraintsP2, bps, e1);
			if(!bps.isEmpty()){
				boundEdges = new LinkedList<DEdge>();
				boundEdges.add(e1);
				bps.get(bps.size()-1).setBoundaryEdges(boundEdges);
			}
			bound.setBoundary(bps);
		} else {
			DEdge current = constraintsP1.get(0);
			ListIterator<DEdge> iter = constraintsP1.listIterator();
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
					if(!current.equals(e1)){
						bps.add(new BoundaryPart(current));
					}
				}
			} else {
				//set will be set to true when the constraints linked to p2
				//will have been added.
				boolean set = false;
				DEdge mem = iter.next();
				current = null;
				while(iter.hasNext()){
					current = iter.next();
					if(!set && (current.isRight(e1.getPointRight()) || current.getPointRight().equals(e1.getEndPoint()))){
						//We must not instanciate a BP where the constraint is a boundary DEdge
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
						//We add the constraints linked to p2, that will form other boundary parts.
						fillWithP2Constraints(boundEdgesBis, constraintsP2, bps, e1);
						set = true;
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
		edgeGID++;
		e1.setGID(edgeGID);
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
	 *		The already created DEdge
	 */
	private void fillWithP2Constraints(List<DEdge> boundaryEdges, List<DEdge> constraintsP2, List<BoundaryPart> bps, DEdge e1){
		DEdge ed;
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
	private DEdge replaceByConstraint(DEdge edge) {
		int index = sortedListContains(constraintEdges, edge);
		DEdge tempEdge = edge;
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
	private void processBadEdges() throws DelaunayError {
		if (!isMeshComputed()) {
			LinkedList<DEdge> alreadySeen = new LinkedList<DEdge>();
			while (!badEdgesQueueList.isEmpty()) {
				DEdge anEdge = badEdgesQueueList.get(0);
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
					DTriangle aTriangle1 = anEdge.getLeft();
					DTriangle aTriangle2 = anEdge.getRight();
					if ((aTriangle1 != null) && (aTriangle2 != null)
						&& swapTriangle(aTriangle1, aTriangle2, anEdge)) {
						// Add the triangle"s edges to the bad edges list
						DEdge addEdge;
						for (int j = 0; j < DTriangle.PT_NB; j++) {
							addEdge = aTriangle1.getEdge(j);
							if ((addEdge.getLeft() != null)
								&& (addEdge.getRight() != null)
								&& !addEdge.equals(anEdge)
								&& !badEdgesQueueList.contains(addEdge)) {
								badEdgesQueueList.add(addEdge);
							}
							addEdge = aTriangle2.getEdge(j);
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
	private boolean swapTriangle(DTriangle aTriangle1, DTriangle aTriangle2,
		DEdge anEdge) throws DelaunayError {

		boolean exchange = false;
		DEdge anEdge10, anEdge11, anEdge12;
		DEdge anEdge20, anEdge21, anEdge22;
		DPoint p1, p2, p3, p4;

		if ((aTriangle1 != null) && (aTriangle2 != null)) {
			p1 = anEdge.getStartPoint();
			p2 = anEdge.getEndPoint();

			p3 = null;
			p4 = null;

			// Test for each triangle if the remaining point of the
			// other triangle is inside or not
			// DTriangle 1 is p1, p2, p3 or p2, p1, p3
			p3 = aTriangle1.getAlterPoint(p1, p2);
			if (p3 != null && aTriangle2.inCircle(p3) == 1) {
				exchange = true;
			}

			// DTriangle 2 is p2, p1, p4 or p1, p2, p4
			p4 = aTriangle2.getAlterPoint(p1, p2);
			if (p4 != null && aTriangle1.inCircle(p4) == 1) {
				exchange = true;
			}

			if (p3 != p4 && exchange) {
				anEdge10 = anEdge;
				anEdge11 = checkTwoPointsEdge(p3, p1, aTriangle1.getEdges(), DTriangle.PT_NB);
				anEdge12 = checkTwoPointsEdge(p1, p4, aTriangle2.getEdges(), DTriangle.PT_NB);
				anEdge20 = anEdge;
				anEdge21 = checkTwoPointsEdge(p2, p4, aTriangle2.getEdges(), DTriangle.PT_NB);
				anEdge22 = checkTwoPointsEdge(p3, p2, aTriangle1.getEdges(), DTriangle.PT_NB);
				if ((anEdge11 == null) || (anEdge12 == null) || (anEdge21 == null) || (anEdge22 == null)) {
					log.error("ERROR");
				} else {
					anEdge.setStartPoint(p3);
					anEdge.setEndPoint(p4);
					edgeGID++;
					anEdge.setGID(edgeGID);
					aTriangle1.setEdge(0, anEdge10);
					aTriangle1.setEdge(1, anEdge11);
					aTriangle1.setEdge(2, anEdge12);
					aTriangle2.setEdge(0, anEdge20);
					aTriangle2.setEdge(1, anEdge21);
					aTriangle2.setEdge(2, anEdge22);
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
	private DEdge checkTwoPointsEdge(DPoint p1, DPoint p2,
		DEdge[] edgeQueueList, int size) {
		// Check if the two points already lead to an existing edge.
		// If the edge exists it must be in the non-processed edges
		DEdge theEdge = null;
		int i = 0;
		int max = (edgeQueueList.length < size ? edgeQueueList.length : size);
		while ((i < max) && (theEdge == null)) {
			DEdge anEdge = edgeQueueList[i];
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
	 * We must be sure that the start point of the constraint is its left point
	 * before beginning the triangulation computation.
	 * @param ed
	 */
	private void fixConstraintDirection(DEdge ed){
		if(ed.getPointRight().equals(ed.getStartPoint())){
			ed.swap();
		}
	}

	/**
	 * Draw Mesh in the JPanel : triangles and edges. If duration is positive,
	 * also display it Must be used only when using package drawing
	 *
	 * @param g
	 */
	//NO-SONAR
	public final void displayObject(Graphics g) {
		try {
			Envelope theBox = getBoundingBox();
			double scaleX, scaleY;
			double minX, minY;
			final int xSize = 1200;
			final int ySize = 600;
			final int decalageX = 10;
			final int decalageY = 630;
			final int legende = 660;
			final int bordure = 10;

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
				for (DTriangle aTriangle : triangleList) {
					aTriangle.displayObject(g, decalageX, decalageY, minX, minY,
						scaleX, scaleY);
				}
			}

			// Draw lines
			if (!constraintEdges.isEmpty()) {
				for (DEdge aVertex : constraintEdges) {
					aVertex.displayObject(g, decalageX, decalageY, minX, minY, scaleX, scaleY);
				}
			}

			if (!edges.isEmpty()) {
				for (DEdge aVertex : edges) {
					aVertex.displayObject(g, decalageX, decalageY, minX, minY, scaleX, scaleY);
				}
			}

			if ((points.size() > 0) && (points.size() < 100)) {
				for (DPoint aPoint : points) {
					aPoint.displayObject(g, decalageX, decalageY, minX, minY,
						scaleX, scaleY);
				}
			}
		} catch (RuntimeException e) {
			log.warn("Problem during rendering\n", e);
		}
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		// our "pseudo-constructor"
		in.defaultReadObject();
		badEdgesQueueList = new LinkedList<DEdge>();
	}
}
