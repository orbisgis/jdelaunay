/**
 *
 * jDelaunay is a library dedicated to the processing of Delaunay and constrained
 * Delaunay triangulations from PSLG inputs.
 *
 * This library is developed at French IRSTV institute as part of the AvuPur and Eval-PDU project,
 * funded by the French Agence Nationale de la Recherche (ANR) under contract
 * ANR-07-VULN-01 and ANR-08-VILL-0005-01 .
 *
 * jDelaunay is distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2010-2012 IRSTV FR CNRS 2488
 *
 * jDelaunay is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * jDelaunay is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * jDelaunay. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.jdelaunay.delaunay;

import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.jdelaunay.delaunay.evaluator.InsertionEvaluator;
import org.jdelaunay.delaunay.error.DelaunayError;
import org.jdelaunay.delaunay.geometries.BoundaryBox;
import org.jdelaunay.delaunay.geometries.DEdge;
import org.jdelaunay.delaunay.geometries.DPoint;
import org.jdelaunay.delaunay.geometries.DTriangle;
import org.jdelaunay.delaunay.geometries.Element;
import org.jdelaunay.delaunay.tools.Tools;

/**
 * This class is used to compute the constrained delaunay triangulation on a set of
 * points and constraint edges. The constraints can be validated before the triangulation.<p></p>
 *
 * After the processing of the mesh with processDelaunay, the ConstrainedMesh can
 * be identified with three set of data, plus its input constraint edges. The three
 * sets are :<p></p>
 *
 *   * The points of the mesh.<br />
 *
 *   * The edges of the mesh.<br/>
 *
 *   * The triangles of the mesh.<p></p>
 *
 * When processing elevation data, you can use the removeFlatTriangles method to
 * be sure that none of the triangles are horizontal. This can be useful when the
 * triangulation is supposed to be used for hydrology.
 *
 * @author Alexis Guéganno
 */
public class ConstrainedMesh implements Serializable {
	private static final long serialVersionUID = 2L;

	private static final Logger LOG = Logger.getLogger(ConstrainedMesh.class);
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
        //we need to store triangles in a map, temporarily. These maps are here, and are not 
        //intended to accessed externally - Don't search for accessors !
        private transient Map<Integer, DTriangle> processed = null;
        private transient Map<Integer, DTriangle> remaining = null;
        private transient Map<Integer, DTriangle> buffer = null;
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
	 * Build a new, empty, ConstrainedMesh. It does not contain any information
	 * that could be used to build a triangulation. You must fill it with
	 * the points and edges you need before launching a processDelaunay() operation.
	 */
	public ConstrainedMesh() {
		triangleList = new ArrayList<DTriangle>();
		edges = new ArrayList<DEdge>();
		constraintEdges = new ArrayList<DEdge>();
		points = new ArrayList<DPoint>();
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
	 * Get the list of edges that are used as constraints during the triangulation
	 * @return
         *      The constraint edges, in a List.
	 */
	public final List<DEdge> getConstraintEdges() {
		return constraintEdges;
	}

	/**
	 * Set the list of edges that are used as constraints during triangulation
	 * @param constraint
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
         *      The edges of the mesh in a List.
	 */
	public final List<DEdge> getEdges() {
		return edges;
	}

	/**
	 * Set the list of edges
	 * @param inEdges
	 */
	public final void setEdges(List<DEdge> inEdges) throws DelaunayError {
		this.edges = new ArrayList<DEdge>();
		for (DEdge e : inEdges) {
			addPoint(e.getStartPoint());
			addPoint(e.getEndPoint());
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
         *      A let sorted list.
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
         *      The index of the edge in the list of edges, or (-1-index) if edge is 
         *      not in the list, and where index would be the index of the edge if 
         *      it was in the list
	 */
	public final int searchEdge(DEdge edge) {
		return Collections.binarySearch(edges, edge);
	}

	/**
	 * Get the precision
	 * @return
         *      precision
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
	 * @return tolerance.
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
         *      The triancle packaged in a List.
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
	 * Return true if tri is one of the triangles of this mesh.</p><p>
         * <strong>BE EXTREMELY CAREFUL !</strong> : this method will be completely
         * inefficient, as the triangle's data structure is not kept sorted. To obtain
         * a data structure that can be queried efficiently, sort the triangle's data
         * structure (as triangle are sortable, cf DTriangle), or put them in an
         * external spatial index (RTree/R+Tree)</p><p>
         * 
	 * @param tri
	 * @return
         *  true if the mesh contains the triangle tri.
	 */
	public final boolean containsTriangle(DTriangle tri) {
		return triangleList.contains(tri);
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
         *      The points of the mesh packaged in a List.
	 */
	public final List<DPoint> getPoints() {
		return points;
	}

	/**
	 * Try to retrieve the point (x,y,z) in the points' structure.
	 * @param x
	 * @param y
	 * @param z
	 * @return
         *      The corresponding DPoint instance, if it exists in the mesh, null otherwise.
         * @throws org.jdelaunay.delaunay.error.DelaunayError
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
         *      true if the mesh has already been computed.
	 */
	public final boolean isMeshComputed() {
		return meshComputed;
	}

	/**
	 * Change the status of this mesh computation
	 * @param comp
	 */
	private void setMeshComputed(boolean comp) {
		meshComputed = comp;
	}

	/**
	 * Get the bounding box of this mesh.
	 * @return
         *      The bounding box, as a JTS Envelope instance.
         * @throws org.jdelaunay.delaunay.error.DelaunayError
	 */
	public final BoundaryBox getBoundingBox() throws DelaunayError {
		BoundaryBox env = new BoundaryBox();
		for (DPoint p : points) {
			env.alterBox(p);
		}
		return env;
	}

	/**
	 * Says if the verbose mode is activated or not
	 * @return
         *      true if the verbosity has been activated.
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
	 * If using this method. All the exisiting points are removed, and replaced by
         * the pts list. The input list is sorted, and the duplicates are removed silently.
	 * @param pts
	 */
	public final void setPoints(List<DPoint> pts) throws DelaunayError {
		if(pts == null){
			points = new ArrayList<DPoint>();
		} else {
			Collections.sort(pts);
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
		ArrayList<DPoint> ret = new ArrayList<DPoint>();
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
         *      The weights currently used in the mesh.
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
	public final void setWeights(Map<Integer, Integer> weights) {
		if(weights == null){
			this.weights = new HashMap<Integer, Integer>();
		} else {
                        this.weights = weights;
                }
	}

	/**
	 * Search the element elt in the sorted list sortedList. You are supposed
	 * to be sure that sortedList is actually sorted ;-)
	 * @param <T>
	 * @param sortedList
	 * @param elt
	 * @return
         *      The index of the element in the list, or -(insertPosition -1) if it 
         * isn't in it.
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
                //We don't need to do anything if we don't have any constraint edge
                if(constraintEdges.size()<1){
                        return;
                }
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
							inter2 = edgeEvent;
							if (leftMost.compareTo2D(newEvent) == -1) {
								inter1 = new DEdge(leftMost, newEvent);
								if(e1.getPointLeft().equals(leftMost)){
									inter1.addProperty(e1.getProperty());
								}else if(e2.getPointLeft().equals(leftMost)){
									inter2.addProperty(e2.getProperty());
								}
							}
							inter2.addProperty(e1.getProperty());
							inter2.addProperty(e2.getProperty());
							if (rightMost.compareTo2D(edgeEvent.getPointRight()) == 1) {
								inter3 = new DEdge(edgeEvent.getPointRight(), rightMost);
								if(e1.getPointRight().equals(rightMost)){
									inter3.addProperty(e1.getProperty());
								} else if(e2.getPointRight().equals(rightMost)){
									inter3.addProperty(e2.getProperty());
								}
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
		if(e.getStartPoint().equals2D(p)){
			e.getStartPoint().setX(p.getX());
			e.getStartPoint().setY(p.getY());
		}
		if(e.getEndPoint().equals2D(p)){
			e.getEndPoint().setX(p.getX());
			e.getEndPoint().setY(p.getY());
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
	 * @param abs
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
	 * Checks that edge does not intersect the existing edges of the mesh.
	 * @param edge
	 * @return
         *      <code>true</code> if edge intersects an edge of the mesh.
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
	 * Get the list of constraint edges whose left point is left.
	 * @param left
	 * @return
         *      Get the constraint of the mesh for which the leftmost point is left.
	 */
	public final List<DEdge> getConstraintsFromLeftPoint(DPoint left) {
		//The edge left-left is the minimum edge whose leftpoint is left.
		List<DEdge> retList = new ArrayList<DEdge>();
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
         *      All the DEdge for which left is the leftmost point. The DEdge are 
         *      vertically sorted.
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
				LOG.trace("Getting points");
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
				LOG.trace("End processing");
				LOG.trace("Triangularization end phase : ");
				LOG.trace("  Points : " + points.size());
				LOG.trace("  Edges : " + edges.size());
				LOG.trace("  Triangles : " + triangleList.size());
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
	 * Refine the mesh, using the Ruppert's algorithm.
	 * @param minLength
	 *		The minimum length of an edge that could be inserted during the refinement.
	 * @throws DelaunayError
         * @throws IllegalArgumentException if <code>minLength</code> is inferior or equal to 0
	 */
	public final void refineMesh(double minLength, InsertionEvaluator ev) throws DelaunayError {
                if(minLength <=0){
                        throw new IllegalArgumentException("The minimum length must be strictly positive !");
                }
		edgeSplitting(minLength);
                triangleRefinement(minLength, ev);
	}

	/**
	 * Refine the mesh, using a derivate of the Ruppert algorithm. We won't split any constraint
         * edges here.
	 * @param minLength
	 *		The minimum length of an edge that could be inserted during the refinement.
	 * @throws DelaunayError
         * @throws IllegalArgumentException if <code>minLength</code> is inferior or equal to 0
	 */
	public final void refineTriangles(double minLength, InsertionEvaluator ev) throws DelaunayError {
                if(minLength <=0){
                        throw new IllegalArgumentException("The minimum length must be strictly positive !");
                }
                processed = new HashMap<Integer, DTriangle>(triangleList.size());
                remaining = new HashMap<Integer, DTriangle>(triangleList.size());
                fillRemainingFromTriangles();
                Set<Map.Entry<Integer, DTriangle>> treatSet = remaining.entrySet();
                while(!treatSet.isEmpty()) {
                        Map.Entry<Integer, DTriangle> entry = treatSet.iterator().next();
                        DTriangle dt = entry.getValue();
                        if(ev.evaluate(dt)){
                                buffer = new HashMap<Integer, DTriangle>();
                                DEdge ret = insertTriangleCircumCenter(dt, true, minLength);
                                putInProcessed(dt);
                                if(ret == null){
                                        fillRemainingFromTriangles();
                                }
                        }else {
                                putInProcessed(dt);
                        }
                }
                triangleList = new LinkedList<DTriangle>(processed.values());
                processed = null;
                remaining = null;
                buffer = null;
	}
        
        /**
         * Edges are split if encroached.
	 * @param minLength
	 *		The minimum length of an edge that could be inserted during the refinement.
	 * @throws DelaunayError
         */
        final void edgeSplitting(double minLength) throws DelaunayError {
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
         * We refine the triangles here.
         * @param minLength
         * @param ev
         * @throws DelaunayError 
         */
        final void triangleRefinement(double minLength, InsertionEvaluator ev) throws DelaunayError {
                DTriangle dt;
                DEdge ret;
                //We will use three maps to process our triangles efficietly. The first one 
                //contains the triangles that are to be treated. The seconf one contains
                //The triangles that have been treated. The last one is used to 
                //retrieve in the treated triangles the one that need to be (potentially)
                //processed again.
                processed = new HashMap<Integer, DTriangle>(triangleList.size());
                remaining = new HashMap<Integer, DTriangle>(triangleList.size());
                fillRemainingFromTriangles();
                //triangleList is still alive, but empty. Consequently, it can still be used
                //in the following steps - in splitEncroachedEdge, for instance.
                Set<Map.Entry<Integer, DTriangle>> treatSet = remaining.entrySet();
                while(!treatSet.isEmpty()) {
                        Map.Entry<Integer, DTriangle> entry = treatSet.iterator().next();
                        dt = entry.getValue();
                        if(ev.evaluate(dt)){
                                buffer = new HashMap<Integer, DTriangle>();
                                ret = insertTriangleCircumCenter(dt, true, minLength);
                                if(ret != null && ret.get2DLength()>2*minLength){
                                                splitEncroachedEdge(ret, minLength);
                                                fillRemainingFromTriangles();
                                        } else {
                                                putInProcessed(dt);
                                                fillRemainingFromTriangles();
                                        }
                        }else { 
                                putInProcessed(dt);
                        }
                }
                triangleList = new LinkedList<DTriangle>(processed.values());
                processed = null;
                remaining = null;
                buffer = null;
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
                LinkedList<DEdge> li = new LinkedList<DEdge>();
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
			last1 = left.getOppositeEdge(ed.getEndPoint());
			startOp1 = left.getOppositeEdge(ed.getStartPoint());
			other1 = new DTriangle(ed1, secondHalf, startOp1);
		}
		DEdge ed2 = null;
		DEdge last2 = null;
		DEdge startOp2 = null;
		DTriangle other2 = null;
		if(right != null){
			ed2 = new DEdge(middle, right.getOppositePoint(ed));
			last2 = right.getOppositeEdge(ed.getEndPoint());
			startOp2 = right.getOppositeEdge(ed.getStartPoint());
			other2 = new DTriangle(ed2, secondHalf, startOp2);
		}
		//this new edge is locked if ed was.
		secondHalf.setLocked(ed.isLocked());
		//We must set a new end to ed.
		ed.setEndPoint(middle);
		//We try to process the left triangle of the encroached edge
		if(left != null){
			//we must replace an edge of left
			int indexExc = left.getEdgeIndex(startOp1);
			left.setEdge(indexExc, ed1);
                        left.computeCenter();
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
			other1.setGID(++triangleGID);
			triangleList.add(other1);
			//We fill the bad edges queue.
			li.add(last1);
			li.add(ed1);
			li.add(startOp1);
                        //we add the edge to the list of edges
			ed1.setGID(++edgeGID);
			edges.add(ed1);
		}
		//We try to process the right triangle of the encroached edge
		if(right != null){
			//we must replace an edge of right
			int indexExc = right.getEdgeIndex(startOp2);
			right.setEdge(indexExc, ed2);
                        right.computeCenter();
			//We set the right and left triangles of each edge properly
			ed2.setRight(right);
			ed2.setLeft(other2);
			if(startOp2.isRight(middle)){
				startOp2.setRight(other2);
			} else {
				startOp2.setLeft(other2);
			}
			secondHalf.setRight(other2);
			//We add the new triangle to the list of triangles.
			other2.setGID(++triangleGID);
			triangleList.add(other2);
			//We fill the bad edges queue.
			li.add(last2);
			li.add(ed2);
			li.add(startOp2);
                        //we add the edge to the list of edges
			ed2.setGID(++edgeGID);
			edges.add(ed2);
		}
		//We perform the filap flap operations.
		revertibleSwapping(li, new LinkedList<DEdge>() , middle, false);
		//The algorithm is recursive, let's continue !
                if(ed.isLocked()){
                        constraintEdges.add(secondHalf);
                }
		edges.add(secondHalf);
		if(ed.isEncroached()){
			splitEncroachedEdge(ed, minLength);
		}
		if(secondHalf.isEncroached()){
			splitEncroachedEdge(secondHalf, minLength);
		}
                if(right != null){
                        if (last2.isEncroached()){
                                splitEncroachedEdge(last2, minLength);
                        }
                        if(startOp2.isEncroached()){
                                splitEncroachedEdge(startOp2, minLength);
                        }
                }
                if(left != null){
                        if (last1.isEncroached()){
                                splitEncroachedEdge(last1, minLength);
                        }
                        if(startOp1.isEncroached()){
                                splitEncroachedEdge(startOp1, minLength);
                        }
                }
	}

        /**
         * Insert the circumcenter of the given triangle in the mesh. This method 
         * is part of the Ruppert refinement algorithm. Consequently, it stops if it 
         * sees it will create a new encroached edge. <br/>
         * The circumcenter won't be inserted if it is too close from another point of the mesh. Moreover,
         * the z-coordinate of the circumcenter is re-computed to ensure the topologic
         * coherence of the mesh.
         * @param tri
         * @param revertible
         *      If set to true, the insertion won't be performed if it creates a new encroached 
         *      edge in the mesh.
         * @param minLength
         *      If the distance between the circumcenter and the closest point of the 
         * containing triangle is inferior to minLength, the circumcenter is not inserted.
         * @return
         *      The encroached edge created by this insertion, if any, or null, if 
         *      a constraint hides the circum center of its triangle, or if the insertion
         *      has been performed.
         * @throws DelaunayError 
         */
        public final DEdge insertTriangleCircumCenter(DTriangle tri, boolean revertible, double minLength) throws DelaunayError {
                Element container = tri.getCircumCenterContainerSafe();
                DPoint cc = new DPoint(tri.getCircumCenter());
                if(container instanceof DEdge ){
                        return (DEdge) container;
                } else if ( container == null){
                        return null;
                }
                //We must interpolate the z value of the circumcenter.
                double z = ((DTriangle) container).interpolateZ(cc);
                cc.setZ(z);
                DPoint pt = new DPoint(cc);
                // Set container property to point
                pt.setProperty(((DTriangle) container).getPoint(0).getProperty());
                if(revertible){
                        return insertIfNotEncroached(pt,(DTriangle) container, minLength);
                } else {
                        insertPointInTriangle(pt, (DTriangle) container, minLength);
                        return null;
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
	 * Process the flip-flap algorithm on the list of triangles
	 */
	private void processBadEdges() throws DelaunayError {
                LinkedList<DEdge> alreadySeen = new LinkedList<DEdge>();
                while (!badEdgesQueueList.isEmpty()) {
                        DEdge anEdge = badEdgesQueueList.remove(0);
                        boolean doIt = !anEdge.isLocked() && !alreadySeen.contains(anEdge);
                        if (doIt) {
                                alreadySeen.add(anEdge);
                                // We cannot process marked edges
                                // We check if the two triangles around the edge are ok
                                if (swapTriangle(anEdge)) {
                                        // Add the triangle's edges to the bad edges list
                                        LinkedList<DEdge> others = new LinkedList<DEdge>();
                                        DTriangle aTriangle1 = anEdge.getLeft();
                                        DTriangle aTriangle2 = anEdge.getRight();
                                        others.add(aTriangle1.getOppositeEdge(anEdge.getStartPoint()));
                                        others.add(aTriangle1.getOppositeEdge(anEdge.getEndPoint()));
                                        others.add(aTriangle2.getOppositeEdge(anEdge.getStartPoint()));
                                        others.add(aTriangle2.getOppositeEdge(anEdge.getEndPoint()));
                                        for(DEdge ed : others){
                                                if(ed.getLeft() != null && ed.getRight() != null
                                                        && !badEdgesQueueList.contains(ed)){
                                                        badEdgesQueueList.add(ed);
                                                }
                                        }
                                }
                        }
                }
	}
        
        /**
         * This implementation of the flip flap algorithm has two main differences 
         * with processBadEdges() : <br/>
         *  * It stores all the swap operations it does in the deque swapMemory<br/>
         *  * It stops when an encroached edge is found.
         * @param badEdges
         * @param swapMemory
         * @param pt the point we're trying to insert.
         * @param revert true if we want to stop when we find an encroached edge.
         * @return
         *      The found encroached DEdge, if any. If there is one, the operation 
         *      stops, and will be reverted later (in the ruppert algorithm).
         * @throws DelaunayError 
         */
        private DEdge revertibleSwapping(LinkedList<DEdge> badEdges, Deque<DEdge> swapMemory,
                                DPoint pt, boolean revert) throws DelaunayError {
                LinkedList<DEdge> alreadySeen = new LinkedList<DEdge>();
                while(!badEdges.isEmpty()){
                        DEdge ed = badEdges.removeFirst();
                        boolean cont = !ed.isLocked() && !alreadySeen.contains(ed);
                        if(cont){
                                alreadySeen.add(ed);
                                if(swapTriangle(ed)){
                                        DTriangle left = ed.getLeft();
                                        DTriangle right = ed.getRight();
                                        swapMemory.addLast(ed);
                                        putInBuffer(left);
                                        putInBuffer(right);
                                        LinkedList<DEdge> others = new LinkedList<DEdge>();
                                        others.add(left.getOppositeEdge(ed.getStartPoint()));
                                        others.add(left.getOppositeEdge(ed.getEndPoint()));
                                        others.add(right.getOppositeEdge(ed.getStartPoint()));
                                        others.add(right.getOppositeEdge(ed.getEndPoint()));
                                        for(DEdge edge : others){
                                                if(revert && edge.isEncroachedBy(pt)){
                                                      return edge;  
                                                }else if(edge.getLeft() != null && edge.getRight() != null
                                                        && !badEdges.contains(edge)){
                                                        badEdges.add(edge);
                                                }
                                        }
                                }
                        }
                }
                return null;
        }

	/**
	 * Swap two neighbour triangles, whose common edge is anEdge<br/>
         * 
	 * @param aTriangle1
	 * @param aTriangle2
	 * @param ed
         *      The edge where we're going to process the flip-flap. This edge will disapear,
         *      or rather it will be replaced by a new one in the data structure that contains 
         *      the edges.
	 * @param forced
	 * @return
         *      True if the flip-flap has been performed. Moreover, if this method 
         *      returns true, we can be sure that ed.left and ed.right are not null.
	 */
	final boolean swapTriangle(DEdge ed) throws DelaunayError {
                DTriangle left = ed.getLeft();
                DTriangle right = ed.getRight();

		boolean exchange = false;
		DPoint p1, p2, p3, p4;

		if (left != null && right != null) {
			p1 = ed.getStartPoint();
			p2 = ed.getEndPoint();

			// Test for each triangle if the remaining point of the
			// other triangle is inside or not
			// DTriangle 1 is p1, p2, p3 or p2, p1, p3
			p3 = left.getAlterPoint(p1, p2);
			if (p3 != null && right.inCircle(p3) == 1) {
				exchange = true;
			}

			// DTriangle 2 is p2, p1, p4 or p1, p2, p4
			p4 = right.getAlterPoint(p1, p2);
			if (p4 != null && left.inCircle(p4) == 1) {
				exchange = true;
			}
			if (p3 != p4 && exchange ) {
                                if( canSwap(ed)){
                                        flipFlap(ed);
                                } else {
                                        exchange = false;
                                }
			}
		}
		return exchange;
	}
        
        /**
         * Put the given triangle in the buffer map. We use the processed attribute of the 
         * triangle to know ig it is in the good strucutre, ie in processed. Keep
         * it coherent, so...
         * @param tri 
         */
        private void putInBuffer(DTriangle tri) {
                if(buffer != null && tri.isProcessed()){
                        //While refining the mesh, all the triangles
                        //that change during the swap operations must be 
                        //added to the buffer - If they are not in the 
                        //processed map anymore.
                        buffer.put(tri.getGID(), tri);
                }
        }
        
        /**
         * Transfer all the entries that are present in buffer in remaining.
         */
        private void fromBufferToRemaining() {
                Set<Map.Entry<Integer, DTriangle>> entries = buffer.entrySet();
                Iterator<Map.Entry<Integer, DTriangle>> iter = entries.iterator();
                while(iter.hasNext()){
                        Map.Entry<Integer, DTriangle> ent = iter.next();
                        ent.getValue().setProcessed(false);
                        remaining.put(ent.getKey(), ent.getValue());
                        processed.remove(ent.getKey());
                }
        }
        
        /**
         * If tri is in remaining, remove it from this map and put it in processed.
         * This method set the <code>processed</code> property of the triangle to true.
         * @param tri 
         */
        private void putInProcessed(DTriangle tri) {
                int gid = tri.getGID();
                if(remaining.remove(gid) != null){
                        processed.put(gid, tri);
                        tri.setProcessed(true);
                }
        }
        
        /**
         * Take (and remove) all the trinalges in triangleList to feed the Map remaining.
         */
        private void fillRemainingFromTriangles(){
                while(!triangleList.isEmpty()){
                        DTriangle tem = triangleList.remove(0);
                        remaining.put(tem.getGID(), tem);
                }
        }
        
        /**
         * This method check that triangles associated to ed can be swapped.
         * @param ed
         * @return 
         */
        private boolean canSwap(DEdge ed){
                DTriangle left = ed.getLeft();
                DTriangle right = ed.getRight();
                DPoint p1 = ed.getStartPoint();
                DPoint p2 = ed.getEndPoint();
                DPoint p4 = right.getAlterPoint(p1, p2);
                final DEdge anEdge11 = left.getOppositeEdge(p2);
                final DEdge anEdge22 = left.getOppositeEdge(p1);
                boolean err1 = (anEdge11.isLeft(p4) && anEdge11.isLeft(p2)) || (anEdge11.isRight(p4) && anEdge11.isRight(p2));
                boolean err2 = (anEdge22.isLeft(p4) && anEdge22.isLeft(p1)) || (anEdge22.isRight(p4) && anEdge22.isRight(p1));
                return err1 && err2;
        }
        
        /**
         * Makes a flip-flap on an edge without any test.
         * @param ed
         * @throws DelaunayError 
         */
        final void flipFlap(DEdge ed) throws DelaunayError {
                DTriangle left = ed.getLeft();
                DTriangle right = ed.getRight();
                DPoint p1 = ed.getStartPoint();
                DPoint p2 = ed.getEndPoint();
                DPoint p3 = left.getAlterPoint(p1, p2);
                DPoint p4 = right.getAlterPoint(p1, p2);
                final DEdge anEdge11 = left.getOppositeEdge(p2);
                final DEdge anEdge12 = right.getOppositeEdge(p2);
                final DEdge anEdge21 = right.getOppositeEdge(p1);
                final DEdge anEdge22 = left.getOppositeEdge(p1);
                if (anEdge11==null || anEdge12==null || anEdge21==null || anEdge22==null) {
                        throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_MISC, "Couldn't swap the triangles.");
                } else {
                        ed.setStartPoint(p3);
                        ed.setEndPoint(p4);
                        left.setEdge(0, ed);
                        left.setEdge(1, anEdge11);
                        left.setEdge(2, anEdge12);
                        right.setEdge(0, ed);
                        right.setEdge(1, anEdge21);
                        right.setEdge(2, anEdge22);
                        if (anEdge12.getLeft() == right) {
                                anEdge12.setLeft(left);
                        } else {
                                anEdge12.setRight(left);
                        }
                        if (anEdge22.getLeft() == left) {
                                anEdge22.setLeft(right);
                        } else {
                                anEdge22.setRight(right);
                        }
                        ed.setLeft(right);
                        ed.setRight(left);
                        left.computeCenter();
                        right.computeCenter();
                }
        }
        /**
         * The operation that "revert" a flip flap. Flip-flaps are supposed to be 
         * symetric, but the implementation given in flip-flap has border effects on
         * address of the objects, and on the GIDs. We can't bear such effects while refining
         * the mesh, because they could lead to incoherences in the data structures (map) we use
         * then.
         * @param ed
         * @throws DelaunayError 
         */
        final void revertFlipFlap(DEdge ed) throws DelaunayError {
                //We retrieve all the needed objects.
                DTriangle left = ed.getLeft();
                DTriangle right = ed.getRight();
                DPoint p1 = ed.getStartPoint();
                DPoint p2 = ed.getEndPoint();
                DPoint p3 = left.getAlterPoint(p1, p2);
                DPoint p4 = right.getAlterPoint(p1, p2);
                final DEdge anEdge11 = left.getOppositeEdge(p2);
                final DEdge anEdge12 = right.getOppositeEdge(p2);
                final DEdge anEdge21 = right.getOppositeEdge(p1);
                final DEdge anEdge22 = left.getOppositeEdge(p1);
                if (anEdge11==null || anEdge12==null || anEdge21==null || anEdge22==null) {
                        throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_MISC, "Couldn't swap the triangles.");
                } else {
                        ed.setStartPoint(p3);
                        ed.setEndPoint(p4);
                        right.setEdge(0, ed);
                        right.setEdge(1, anEdge11);
                        right.setEdge(2, anEdge12);
                        left.setEdge(0, ed);
                        left.setEdge(1, anEdge21);
                        left .setEdge(2, anEdge22);
                        if (anEdge21.getLeft() == right) {
                                anEdge21.setLeft(left);
                        } else {
                                anEdge21.setRight(left);
                        }
                        if (anEdge11.getLeft() == left) {
                                anEdge11.setLeft(right);
                        } else {
                                anEdge11.setRight(right);
                        }
                        left.computeCenter();
                        right.computeCenter();
                }
                
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
         * remove edges from a list of points using equivalences in ReplacePoints
         * @param replacePoints
         * @param theList
         */
        private void changeUnqualifiedEdges(Map<DPoint, DPoint> replacePoints, List<DEdge> theList) {
                ArrayList<DEdge> edgeToRemove = new ArrayList<DEdge>();
                for (DEdge anEdge : theList) {
                        DPoint aPoint1 = anEdge.getStartPoint();
                        DPoint replaced1 = aPoint1;
                        if (replacePoints.containsKey(aPoint1)) {
                                replaced1 = replacePoints.get(aPoint1);
                                anEdge.setStartPoint(replaced1);
                        }
                        DPoint aPoint2 = anEdge.getEndPoint();
                        DPoint replaced2 = aPoint2;
                        if (replacePoints.containsKey(aPoint2)) {
                                replaced2 = replacePoints.get(aPoint2);
                                anEdge.setEndPoint(replaced2);
                        }
                        // Ensure the two points are not equal
                        if (replaced1.equals(replaced2)) {
                                edgeToRemove.add(anEdge);
                        }
                }
                // Remove bad edges
                for (DEdge anEdge : edgeToRemove) {
                        theList.remove(anEdge);
                }

        }

        /**
         * Do a flip-flap on all the edges that can be accessed from the it iterator,
         * moving straight ahead. There are not any check on the delaunay criterium, here.<br/>
         * Note that we are using revertFlipFlap rather than flipFlap in this method.
         * Indeed, this method is first intended to be sued during the reversion of point
         * insertion. Flip-flap operations have been made, we must be sure to revert
         * them the right way, to keep our structures coherent.
         * @param it
         * @throws DelaunayError 
         */
        final void proceedSwaps(Iterator<DEdge> it) throws DelaunayError {
                while(it.hasNext()){
                        DEdge ed = it.next();
                        ed.swap();
                        revertFlipFlap(ed);
                }
        }
        
        /**
         * Insert pt in container only if it does not create a new encroached edge 
         * in the mesh.<br/>
         * if the insertion of the new point would cause such a creation, the point
         * is not inserted, and all the operations associated to its insertion
         * are reverted.
         * @param pt
         * @param container
         * @param minLength
         *      The minimum length between the new point and the point of the triangle
         *      that is the closest to it. If this distance is less than minLength,
         *      the point is not inserted.
         * @return
         *      The encroached edge that has caused the stop of the insertion, if any.
         * @throws DelaunayError 
         */
        public final DEdge insertIfNotEncroached(final DPoint pt, DTriangle container, double minLength) 
                        throws DelaunayError{
                if(!container.isInside(pt)){
                        throw new DelaunayError(0, "you must search for the containing triangle"
                                + " before to proceed to the insertion.");
                } 
                if(container.isCloser(pt, minLength)){
                        return null;
                }
                boolean onEdge = container.isOnAnEdge(pt);
                DEdge ret;
                if(onEdge){
                        ret = insertOnEdgeRevertible(container, pt);
                } else {
                        ret = insertInTriangleRevertible(container, pt);
                }
                return ret;
                
        }
        
        /**
         * This method performs the revertible insertion of a point on an edge of the mesh.
         * It first remember all the elemnts that could be needed to revert all
         * the operations.<br/>
         * Our goal here is to fail fast : as soon as we find an encroached edge,
         * we stop our process and we revert our operations.
         * @param container
         * @param pt
         * @return
         * @throws DelaunayError 
         */
        private DEdge insertOnEdgeRevertible(DTriangle container, DPoint pt) throws DelaunayError {
                LinkedList<DEdge> badEdges = new LinkedList<DEdge>();
                Deque<DEdge> swapMem = new LinkedList<DEdge> ();
                DEdge ret;
                DEdge contEdge = container.getContainingEdge(pt);
                //if contEdge is a border or a constraint, we return it as it would be 
                //necessary encroached by the insertion. This way, we avoid useless complicated tests.
                if(contEdge.isLocked() || contEdge.getLeft()==null || contEdge.getRight() == null){
                        return contEdge;
                }
                DTriangle left = contEdge.getLeft();
                DEdge memleft = null;
                DPoint memExt = contEdge.getEndPoint();
                //We must remember what our original data was.
                if(left != null){
                        memleft = left.getOppositeEdge(contEdge.getStartPoint());
                }
                DTriangle right = contEdge.getRight();
                DEdge memright = null;
                if(right != null){
                        memright = right.getOppositeEdge(contEdge.getStartPoint());
                }
                //We make the first step : insertion of the point on the edge.
                ret = initPointOnEdge(pt, contEdge, badEdges);
                //If the returned value is not null, we come back.
                if(ret != null){
                        revertPointOnEdgeInsertion(contEdge, pt, memExt, memleft, memright);
                        //We can return here, as we've already found en encroached edge.
                        return ret;
                }
                //Second step : we proceed to our revertible swap.
                ret = revertibleSwapping(badEdges, swapMem, pt, true);
                //If the returned value is not null, we come back.
                if(ret != null){
                        Iterator<DEdge> it = swapMem.descendingIterator();
                        proceedSwaps(it);
                        if(contEdge.getStartPoint().equals(pt)){
                                contEdge.swap();
                        }
                        revertPointOnEdgeInsertion(contEdge, pt, memExt, memleft, memright);
                }
                return ret;
        }
        
        /**
         * This method performs the revertible insertion of a point in a triangle of the mesh.
         * It first remember all the elements that will be needed to finalize the 
         * reversion of the insertion, and then try it.<br/>
         * Our goal here is to fail fast : as soon as we find an encroached edge,
         * we stop our process and we revert our operations.
         * @param container
         *      The container of the point. It must have been searched before.
         * @param pt
         *      The point to insert
         * @return
         *  The encroached edge that provoked the reversion of the process.
         * @throws DelaunayError 
         */
        private DEdge insertInTriangleRevertible(DTriangle container, DPoint pt) throws DelaunayError{
                LinkedList<DEdge> badEdges = new LinkedList<DEdge>();
                Deque<DEdge> swapMem = new LinkedList<DEdge> ();
                DEdge ret;
                //We must remember what our original data was.
                DEdge eMem0 = container.getEdge(0);
                DEdge eMem1 = container.getEdge(1);
                DEdge eMem2 = container.getEdge(2);
                DPoint mem = container.getOppositePoint(eMem0);
                ret = initPointInTriangle(pt, container, badEdges);
                if(ret != null){
                        revertPointInTriangleInsertion(container, pt, mem, eMem1, eMem2);
                        //we must stop here, that's why we've already revert our process.
                        return ret;
                }
                //
                ret = revertibleSwapping(badEdges, swapMem, pt, true);
                if(ret != null){
                        Iterator<DEdge> it = swapMem.descendingIterator();
                        proceedSwaps(it);
                        //Due to the swap operations, the container reference may not contain
                        //pt anymore. We search for our triangle back.
                        DTriangle actualContainer = container;
                        if(eMem0.getLeft() != null && eMem0.getLeft().belongsTo(pt)){
                                actualContainer = eMem0.getLeft();
                        } else if(eMem0.getRight() != null && eMem0.getRight().belongsTo(pt)){
                                actualContainer = eMem0.getRight();
                        }
                        revertPointInTriangleInsertion(actualContainer, pt, mem, eMem1, eMem2);
                }
                return ret;
        }
        
        /**
         * Method that revert a point insertion in a triangle (but not on one of its edges).
         * We need the reference to the triangle, the inserted point, and the former
         * third point of the triangle.<br/>
         * Note that we need to decrease the edge, triangle and point GIDs, and 
         * to remove the elements we don't need from each data structure.
         * @param dt
         *      The triangle that must return in its original state
         * @param forget
         *      The point that will finally not be inserted
         * @param apex 
         *      The last point of dt in its original state
         * @param o1
         *      One of the missing edges of dt
         * @param o2
         *      One of the missing edges of dt
         */
        final void revertPointInTriangleInsertion(DTriangle dt, DPoint forget, DPoint apex, DEdge o1, DEdge o2)
                        throws DelaunayError {
                DEdge perm = dt.getOppositeEdge(forget);
                DEdge mod = dt.getOppositeEdge(perm.getStartPoint());
                int index = dt.getEdgeIndex(mod);
                int index2 = dt.getEdgeIndex(dt.getOppositeEdge(perm.getEndPoint()));
                if(o1.contains(perm.getStartPoint())){
                        dt.setEdge(index, o2);
                        dt.setEdge(index2, o1);
                } else {
                        dt.setEdge(index, o1);
                        dt.setEdge(index2, o2);
                }
                dt.computeCenter();
                //remove the point :
                points.remove(points.size()-1);
                //remove the edges
                edges.remove(edges.size()-1);
                edges.remove(edges.size()-1);
                edges.remove(edges.size()-1);
                //remove the triangles.
                triangleList.remove(triangleList.size()-1);
                triangleList.remove(triangleList.size()-1);
                //Reset a unique value for the triangles equal to dt and linked to its
                //edges.
                forceCoherence(dt);
        }
        
        /**
         * This method reverts the first step of a point insertion in the mesh, when
         * this insertion occurs on an edge of a triangle. <br/>
         * Note that we need to decrease the edge, triangle and point GIDs, and 
         * to remove the elements we don't need from each data structure.
         * @param ed
         * @param forget
         * @param extremity
         * @param leftLast
         * @param rightLast
         * @throws DelaunayError 
         */
        final void revertPointOnEdgeInsertion(DEdge ed, DPoint forget, DPoint extremity, DEdge leftLast, 
                        DEdge rightLast) throws DelaunayError {
                DTriangle left = ed.getLeft();
                DTriangle right = ed.getRight();
                if(triangleList.size()>1){
                        checkMemEdges(leftLast, forget, triangleList.get(triangleList.size() -1), 
                                triangleList.get(triangleList.size() -2));
                        checkMemEdges(rightLast, forget, triangleList.get(triangleList.size() -1), 
                                triangleList.get(triangleList.size() -2));
                }
                DPoint st;
                //We push back the original extremity of ed.
                ed.setEndPoint(extremity);
                st = ed.getStartPoint();
                //If left is not null, we reset it and remove the elements added from it.
                if(left != null){
                        rebuildTriangleOEI(left, st, leftLast);
                        edges.remove(edges.size()-1);
                        triangleList.remove(triangleList.size()-1);
                        forceCoherence(left);
                }
                edges.remove(edges.size()-1);
                //If right is not null, we reset it and remove the elements added from it.
                if(right != null ){
                        rebuildTriangleOEI(right, st, rightLast);
                        edges.remove(edges.size()-1);
                        triangleList.remove(triangleList.size()-1);
                        forceCoherence(right);
                }
                points.remove(points.size()-1);
        }
        
        /**
         * After each insertion or rejected insertion, we must return to a coherent state.
         * This method is intended ot achieve such a goal, by forcing the triangle 
         * in parameter and its edges to be effectively linked after the call to it.
         * @param dt 
         */
        private void forceCoherence(DTriangle dt ){
                dt.forceCoherenceWithEdges();
                for(DEdge ed : dt.getEdges()){
                        ed.forceTriangleSide();
                }
        }
        
        /**
         * Check that forget is not a point of l1, if mem is an edge of l1, and idem for l2 
         * if it is not the case for l1. If it is, we process deep swaps on mem.
         * @param mem
         * @param forget
         * @param l1
         * @param l2 
         */
        private void checkMemEdges(DEdge mem, DPoint forget, DTriangle l1, DTriangle l2){
                if(l1!=null && l1.isEdgeOf(mem) && !l1.belongsTo(forget)){
                        mem.deepSwap();
                } else if(l2!=null && l2.isEdgeOf(mem) && ! l2.belongsTo(forget)){
                        mem.deepSwap();
                }
        }
        
        /**
         * This method rebuild the original triangles, before the canceled insertion of a point
         * on an edge.
         * @param tri
         * @param op
         * @param ed
         * @throws DelaunayError 
         */
        private void rebuildTriangleOEI(DTriangle tri, DPoint op, DEdge ed) throws DelaunayError {
                DEdge rep = tri.getOppositeEdge(op);
                int i = tri.getEdgeIndex(rep);
                tri.setEdge(i, ed);
                tri.computeCenter();
        }
        /**
         * Insert the point pt in the triangle container.<br/>
         * This method does not check if there are any new encroached edge
         * in the mesh. It just goes straight ahead. Moreover, it does not reaffect
         * the z-coordinate of the inserted point.
         * @param pt
         *      The point to be inserted.
         * @param container
         *      The triangle of the mesh that contains pt.
         * @param minLength
         *      The minimum length between the new point and the point of the triangle
         *      that is the closest to it. If this distance is less than minLength,
         *      the point is not inserted.
         * @throws DelaunayError if pt is not inside container. If a search is needed,
         *      it must be made before trying to insert the point.
         */
        public final void insertPointInTriangle(final DPoint pt, DTriangle container, double minLength) 
                        throws DelaunayError{
                if(!container.isInside(pt)){
                        throw new DelaunayError(0, "you must search for the containing triangle"
                                + "before to proceed to the insertion.");
                } 
                if(container.isCloser(pt, minLength)){
                        return ;
                }
                LinkedList<DEdge> badEdges = new LinkedList<DEdge>();
                boolean onEdge = container.isOnAnEdge(pt);
                if(onEdge){
                        DEdge contEdge = container.getContainingEdge(pt);
                        initPointOnEdge(pt, contEdge, badEdges);
                        badEdgesQueueList = badEdges;
                        processBadEdges();
                } else {
                        initPointInTriangle(pt, container, badEdges);
                        badEdgesQueueList = badEdges;
                        processBadEdges();
                }
        }
        
        /**
         * When inserting a point in the already processed mesh, we must generate the
         * needed edges and triangles, and then process the flip-flap operations. 
         * This method generates the needed element and update the local existing ones.
         * DEdge instances that could have become "bad edges" are stored in the
         * <code>Deque</code> badEdges. This Deque can be used for the flip flap 
         * algorithms. <br/>
         * The method returns a DEdge. It is the first encroached DEdge created by
         * the insertion of this point, at the local scale (ie before the flip-flap
         * processing). It can be used to know if the point insertion must be reverted.
         * @param pt
         * @param container
         * @param badEdges
         * @return The first DEdge that is encroached after the insertion, if any, null otherwise.
         * @throws DelaunayError 
         */
        final DEdge initPointInTriangle(final DPoint pt, DTriangle container, Deque<DEdge> badEdges) throws DelaunayError {
                DEdge eMem0 = container.getEdge(0);
                DEdge eMem1 = container.getEdge(1);
                DEdge eMem2 = container.getEdge(2);
                badEdges.add(eMem0);
                badEdges.add(eMem1);
                badEdges.add(eMem2);
                DEdge e1 = new DEdge(pt, eMem1.getStartPoint());
                DEdge e2 = new DEdge(pt, eMem1.getEndPoint());
                edgeGID++;
                e1.setGID(edgeGID);
                edgeGID++;
                e2.setGID(edgeGID);
                //We instanciate the first triangle
                DTriangle tri1 = new DTriangle(eMem1, e1, e2);
                addTriangle(tri1);
                //we must prepare the third edge, that will be used in the two other triangles.
                //e3 is shared between container and tri2
                DEdge e3 = new DEdge(pt, container.getOppositePoint(eMem1));
                edgeGID++;
                e3.setGID(edgeGID);
                //We must instanciate the second triangle.
                DTriangle tri2;
                if(eMem2.isExtremity(eMem1.getStartPoint())){
                        tri2 = new DTriangle(e1, e3, eMem2);
                        container.setEdge(1, e2);
                        container.setEdge(2, e3);
                } else {
                        tri2 = new DTriangle(e2, e3, eMem2);
                        container.setEdge(1, e1);
                        container.setEdge(2, e3);
                }
                container.forceCoherenceWithEdges();
                addTriangle(tri2);
                edges.add(e1);
                edges.add(e2);
                edges.add(e3);
                pointGID++;
                pt.setGID(pointGID);
                points.add(pt);
                //e1, e2 and e3 can't be encroached, as they are not locked, and they
                //can't be on the boundary of the mesh.
                //they can't be bad (yet) either.
                if(eMem0.isEncroached()){
                        return eMem0;
                } else if(eMem1.isEncroached()){
                        return eMem1;
                } else if(eMem2.isEncroached()){
                        return eMem2;
                }
                return null;
        }
        
        /**
         * When inserting a point in the already processed mesh, we must determine 
         * if we are in a triangle or on an edge. Here, we are on an edge.<br/>
         * <code>contEdge</code> will be split in two edges using <code>pt</code>. The new point,
         * new edges and new triangles will be added to the mesh. The edges that could be 
         * eligible for a flip-flap are stored in <code>badEdges</code>
         * The method return a DEdge, which is the first edge of the triangles
         * linked (left or right) to the original edge that becomes encroached because
         * of the insertion, if any.
         * @param pt
         * @param contEdge
         * @param badEdges
         * @return
         * @throws DelaunayError 
         */
        final DEdge initPointOnEdge(final DPoint pt, DEdge contEdge, Deque<DEdge> badEdges) throws DelaunayError {
                DTriangle left = contEdge.getLeft();
                DTriangle right = contEdge.getRight();
                DEdge r1 = null;
                DEdge r2 = null;
                DEdge l1 = null;
                DEdge l2 = null;
                //We must split the edge before building the triangles
                //otherPart is built to have the same orientation as contEdge.
                DEdge otherPart = new DEdge(pt, contEdge.getEndPoint());
                if(left != null){
                        //we retrieve the two other edges from the left triangle.
                        l1 = left.getOppositeEdge(contEdge.getEndPoint());
                        l2 = left.getOppositeEdge(contEdge.getStartPoint());
                        badEdges.add(l1);
                        badEdges.add(l2);
                        //We retrieve the point opposite to contEdge in left
                        DPoint opLeft = left.getOppositePoint(contEdge);
                        //we build the missing edge
                        DEdge lastLeft = new DEdge(pt, opLeft);
                        DTriangle otl = new DTriangle(l2, otherPart, lastLeft);
                        //We change an edge in left.
                        //left is not coherent anymore
                        left.setEdge(left.getEdgeIndex(l2), lastLeft);
                        lastLeft.setLeft(left);
                        //We add the newly created triangle and edge to the corresponding lists.
                        edgeGID++;
                        lastLeft.setGID(edgeGID);
                        edges.add(lastLeft);
                        //and now the triangle
                        addTriangle(otl);
                }
                if(right != null){
                        //we retrieve the two other edges from the right triangle.
                        r1 = right.getOppositeEdge(contEdge.getEndPoint());
                        r2 = right.getOppositeEdge(contEdge.getStartPoint());
                        badEdges.add(r1);
                        badEdges.add(r2);
                        //We retrieve the point opposite to contEdge in right
                        DPoint opRight = right.getOppositePoint(contEdge);
                        //We build the missing edge.
                        DEdge lastRight = new DEdge(pt, opRight);
                        DTriangle otr = new DTriangle(r2, otherPart, lastRight);
                        //We change an ede in right.
                        //right is not coherent anymore.
                        right.setEdge(right.getEdgeIndex(r2), lastRight);
                        lastRight.setRight(right);
                        //We add the newly created triangle and edge to the corresponding lists.
                        edgeGID++;
                        lastRight.setGID(edgeGID);
                        edges.add(lastRight);
                        //and now the triangle
                        addTriangle(otr);
                }
                contEdge.setEndPoint(pt);
                //Don't forget to add the new point..
                pointGID++;
                pt.setGID(pointGID);
                points.add(pt);
                //...and the other part of the input edge
                edgeGID++;
                otherPart.setGID(edgeGID);
                edges.add(otherPart);
                //At this stage, left and right are not valid triangles anymore.
                contEdge.setEndPoint(pt);
                //We must still return the first encroached edge we find, if any.
                //Analyze left first.
                if(left != null){
                        left.computeCenter();
                        if(l1.isEncroached()){
                                return l1;
                        } else if(l2.isEncroached()){
                                return l2;
                        }
                } 
                //Then analyze right.
                if(right != null){
                        right.computeCenter();
                        if(r1.isEncroached()){
                                return r1;
                        } else if(r2.isEncroached()){
                                return r2;
                        }
                }
                return null;
        }
        
        /**
         * Ensure points are at least at epsilon from other points
         * NB : points are supposed to be already sorted
         * @param epsilon
         */
        public final void dataQualification(double epsilon) throws DelaunayError {
                if (isMeshComputed()) {
                        throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_GENERATED);
                } else if (points == null || edges == null || constraintEdges == null) {
                        throw new DelaunayError("Structures not defined");
                } else if (epsilon <= 0) {
                        throw new DelaunayError("Epsilon must be positive");
                } else {
                        HashMap<DPoint, DPoint> replacePoints = new HashMap<DPoint, DPoint>();
                        double epsilon2 = epsilon * epsilon;

                        int index = -1;
                        for (DPoint aPoint : points) {
                                index++;
                                if (!replacePoints.containsKey(aPoint)){
                                        // get all points at less than epsilon and put them in ReplacePoints
                                        double x1 = aPoint.getX();
                                        double y1 = aPoint.getY();

                                        ListIterator<DPoint> iter = points.listIterator(index);
                                        boolean ended = false;
                                        while ((iter.hasNext()) && (!ended)) {
                                                DPoint nextPoint = iter.next();
                                                if (nextPoint != aPoint) {
                                                        // We check if it is the same object, not the same point
                                                        double x2 = nextPoint.getX();
                                                        double y2 = nextPoint.getY();

                                                        if ((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1) <= epsilon2) {
                                                                // too close to aPoint
                                                                replacePoints.put(nextPoint, aPoint);
                                                        }
                                                        if (x2 > x1 + epsilon) {
                                                                // not possible to have another point
                                                                ended = true;
                                                        }
                                                }
                                        }
                                }
                        }

                        // Points are processed, we remove all points in the list that are in the HashMap
                        ListIterator<DPoint> iterPts = points.listIterator();
                        while (iterPts.hasNext()) {
                                DPoint aPoint = iterPts.next();
                                if (replacePoints.containsKey(aPoint)) {
                                        // point to be replaces
                                        iterPts.remove();
                                }
                        }

                        // Then replace points in other structures
                        //      - edges
                        //      - constraintEdges
                        //      - polygons

                        changeUnqualifiedEdges(replacePoints, edges);
                        changeUnqualifiedEdges(replacePoints, constraintEdges);                        
                }
        }

	/**
	 * Draw Mesh in the JPanel : triangles and edges. If duration is positive,
	 * also display it Must be used only when using package drawing
	 *
	 * @param g
         * @throws org.jdelaunay.delaunay.error.DelaunayError
	 */
	//NO-SONAR
	public final void displayObject(Graphics g) throws DelaunayError {
		try {
                        BoundaryBox theBox = getBoundingBox();
			double scaleX, scaleY;
			double minX, minY;
			final int xSize = 1200;
			final int ySize = 600;
			final int decalageX = 10;
			final int decalageY = 630;
			final int legende = 660;
			final int bordure = 10;
			final int maxDisplayedPoints = 100;

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

			if ((points.size() > 0) && (points.size() < maxDisplayedPoints)) {
				for (DPoint aPoint : points) {
					aPoint.displayObject(g, decalageX, decalageY, minX, minY,
						scaleX, scaleY);
				}
			}
		} catch (RuntimeException e) {
			LOG.warn("Problem during rendering\n", e);
		}
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		// our "pseudo-constructor"
		in.defaultReadObject();
		badEdgesQueueList = new LinkedList<DEdge>();
	}
}
