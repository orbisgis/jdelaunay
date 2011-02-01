package org.jdelaunay.delaunay;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * A part of the boundary of the mesh. This class is built with :
 *  * A set E of edges, that are all part of the mesh.
 *  * An edge C that is a constraint used during the triangulation.
 *
 * The elements of E will be all the edges of the boundary that are upper than C,
 * and lower than the constraint Edge directly upper than C
 * @author alexis
 */
final class BoundaryPart {

	//the section of the boundary contained in this BoundaryPart.
	private List<Edge> boundaryEdges;
	//The constraint that define the lower scope of this boundary part.
	//The upper scope will be defined by the next BoundaryPart in the Boundary class.
	private Edge constraint;
	//The list of edges that could be swapped during the flip-flap.
	private List<Edge> badEdges;
	//The list of newly added Edges
	private List<Edge> addedEdges;

	private void init(){
		badEdges = new ArrayList<Edge>();
		addedEdges = new ArrayList<Edge>();
	}

	/**
	 * default constructor is kept private.
	 */
	private BoundaryPart(){
	}

	/**
	 * Most common constructor. It's up to you to "sort" efficiently the edges of
	 * the boundary.
	 * @param bound
	 * @param cstr
	 */
	BoundaryPart(List<Edge> bound, Edge cstr){
		init();
		boundaryEdges = bound;
		constraint = cstr;
	}

	/**
	 * Constructor used when the constraint linked to this part of the boundary
	 * is null.
	 * 
	 * This can happen for the lowest part of the boundary, fo instance.
	 * @param bound
	 */
	BoundaryPart(List<Edge> bound){
		init();
		boundaryEdges = bound;
		constraint = null;
	}

	/**
	 * Constructor used when the part of boundary between this constraint and the
	 * next one is empty.
	 * @param cstr
	 */
	BoundaryPart(Edge cstr){
		init();
		setConstraint(cstr);
		boundaryEdges = new ArrayList<Edge>();
	}

	/**
	 * Get the list of edges associated to this part of the boundary.
	 * @return
	 */
	List<Edge> getBoundaryEdges() {
		return boundaryEdges;
	}

	/**
	 * Set the set of edges that are associated to this boundary part.
	 * @param boundaryEdges
	 */
	void setBoundaryEdges(List<Edge> boundaryEdges) {
		if(boundaryEdges == null){
			this.boundaryEdges = new ArrayList<Edge>();
		}else {
			this.boundaryEdges = boundaryEdges;
		}
	}

	/**
	 * Get the constraint that forms the lower limit of this part of the boundary.
	 * @return
	 */
	Edge getConstraint() {
		return constraint;
	}

	/**
	 * Set the constraint that determine the lower limit of this part of the
	 * boundary.
	 * @param constraint
	 */
	void setConstraint(Edge constraint) {
		if(constraint != null && constraint.getPointLeft().equals(constraint.getEndPoint())){
			constraint.swap();
		}
		this.constraint = constraint;
	}

	/**
	 * Get the edges added to the mesh during the last insertion of a point.
	 * @return
	 */
	List<Edge> getAddedEdges(){
		return addedEdges;
	}

	/**
	 * Gets the edges that will need to be processed by the flip-flap algorithm
	 * @return
	 */
	List<Edge> getBadEdges(){
		return badEdges;
	}

	/**
	 * Returns true if the point given in argument is lower than the constraint
	 * edge used to define this boundary part.
	 * @param point
	 * @return
	 */
	boolean pointIsLower(final Point point){
		return constraint.isRight(point);
	}

	/**
	 * Returns true if the point given in argument is upper than the constraint
	 * edge used to define this boundary part.
	 * @param point
	 * @return
	 */
	boolean pointIsUpper(final Point point){
		return constraint.isLeft(point);
	}

	/**
	 * Check if point is the right point of the constraint.
	 * @param point
	 * @return
	 */
	boolean isConstraintRightPoint(final Point point){
		return constraint.getPointRight().equals(point);
	}

	/**
	 * Check if bpo can be the next BoundaryPart of the boundary. It is true
	 * if bpo's left point lies on the last edge of this boundary part.
	 * @param bpo
	 * @return
	 */
	boolean canBeNext(BoundaryPart bpo) {
		Edge last = boundaryEdges.get(boundaryEdges.size()-1);
		Point left = bpo.getConstraint().getPointLeft();
		Point right = bpo.getConstraint().getPointRight();
		return (left.equals(last.getStartPoint())
			|| left.equals(last.getEndPoint()) )
			&& last.isRight(right)
			&& !constraint.getPointLeft().equals(left);

	}

	/**
	 * Split this BoundaryPart in two. The edge given in argument will be used
	 * as the constraint for the new BoundaryEdge. Its leftmost point will be
	 * searched in the boundary Edges of this BoundaryPart. 
	 * @param cstr
	 * @return
	 */
	BoundaryPart split(Edge cstr) throws DelaunayError {
		if(boundaryEdges.isEmpty()){
			//We can't split anything if we don't even have a boundary edge !
			throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_CAN_NOT_SPLIT_BP);
		}
		//The point where we'll perform the split
		Point split = cstr.getPointLeft();
		//We can instanciate ret, as we know its constraint.
		BoundaryPart ret = new BoundaryPart(cstr);
		if(split.equals(boundaryEdges.get(0).getStartPoint())){
			//ret will starve this of all its boundary Edge.
			ret.setBoundaryEdges(boundaryEdges);
			this.setBoundaryEdges(new LinkedList<Edge>());
			return ret;
		}
		if(boundaryEdges.get(0).isDegenerated()){
			
		}
		LinkedList<Edge> futureBoundary = new LinkedList<Edge>();
		List<Edge> otherBoundary = boundaryEdges;
		
		ListIterator<Edge> iter = otherBoundary.listIterator();
		Edge course;
		boolean success = false;
		while(iter.hasNext()){
			course = iter.next();
			futureBoundary.add(course);
			iter.remove();
			if(course.getEndPoint().equals(split)){
				success = true;
				break;
			}
		}
		if(!success){
			throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_CAN_NOT_SPLIT_BP);
		} else {
			this.setBoundaryEdges(futureBoundary);
			return new BoundaryPart(otherBoundary, cstr);
		}
		
	}

        /**
         * Connect a single point to this boundary part. Travels through the boundary
         * edges and try to build triangles from it. The boundary is, of course,
         * updated.
         * @param point
         * @return
         */
        List<DelaunayTriangle> connectPoint(Point point, Edge nextCstr) throws DelaunayError{
		if(boundaryEdges.isEmpty() && constraint==null){
			throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_CAN_NOT_CONNECT_POINT);
		}
		badEdges = new ArrayList<Edge>();
		addedEdges = new ArrayList<Edge>();
                ListIterator<Edge> iter = boundaryEdges.listIterator();
		Edge firstFound = null;
		Edge mem = null;
		Edge memBis = null;
		Edge current;
		boolean rightDeg = false;
		//If the boundaryEdges list is empty, we must add a degenerated edge.
		if(boundaryEdges.isEmpty()){
			firstFound = new Edge(constraint.getPointLeft(),point);
			firstFound.setDegenerated(true);
			if(firstFound.equals(constraint)){
				firstFound = constraint;
				if(constraint.getLeft()==null && constraint.getRight()==null){
					constraint.setDegenerated(true);
				}
			} else {
				firstFound = firstFound.equals(nextCstr) ? nextCstr : firstFound;
			}
			addedEdges.add(firstFound);
			boundaryEdges.add(firstFound);
			return new ArrayList<DelaunayTriangle>();
		}
		List<DelaunayTriangle> triList = new ArrayList<DelaunayTriangle>();
		DelaunayTriangle temp = null;
		while(iter.hasNext()){
			current = iter.next();
			//We must put current the right direction if it is degenerated.
			if(current.isDegenerated()){
				iter.previous();
				mem = connectToDegenerated(iter, point, triList, mem);
				rightDeg = mem==null;
				if(mem != null && mem.isDegenerated()){
					return new ArrayList<DelaunayTriangle>();
				}
			} else {
				if(current.isRight(point)){
					//Current is not degenerated, so it will become
					//an inner Edge of the mesh. We must process the flip
					//flap on it if necessary.
					badEdges.add(current);
					//we can build a triangle.
					if(mem == null){
						//if not already set, we must instanciate mem
						mem = new Edge(current.getStartPoint(),point);
						//We must check that we're not about to duplicate the
						//constraint Edge
						mem = mem.equals(constraint) ? constraint : mem;
						//We will add an Edge in the mesh.
						addedEdges.add(mem);
						//We must insert this new edge at the right position in
						//the boundaryEdges list. For that we come one step
						//back and add it with the listIterator.
						iter.previous();
						iter.add(mem);
						//Then we must move one step forward to continue
						//our processing, in order not to process the
						//same edge twice.
						iter.next();
					}
					//We build the last Edge of the triangle we are about to add.
					memBis = new Edge(point, current.getEndPoint());
					if(nextCstr != null){
						memBis = memBis.equals(nextCstr) ? nextCstr : memBis;
					}
					//We will add an Edge in the mesh.
					addedEdges.add(memBis);
					//we can build the triangle...
					temp = new DelaunayTriangle(current, mem, memBis);
					//...and add it to the list we'll return.
					triList.add(temp);
					//memBis is the last created Edge - we put it in mem.
					mem = memBis;
					//and we can remove the current edge, as it is not
					//part of the boundary anymore.
					iter.remove();
				} else {
					if(mem != null){
						//the local envelope is supposed to be convex,
						//so we can stop here.
						//Before that, we insert this last edge
						//in the boundaryEdges list.
						iter.previous();
						iter.add(mem);
						return triList;
					}
				}
			}
		}
		//If we're here, the point was still visible from the last Edge of
		//the list. We must add the last generated edge.
		//We check that mem is not null. It can be null if point can't be seen
		//from any edges of this boundary part
		if(mem==null){
			//in all cases, we are sure we can connect point to the
			//constraint edge's left point or to the last edge's right point.
			//We must determine what to do...
			if(!boundaryEdges.isEmpty() && boundaryEdges.get(0).getStartPoint().equals(boundaryEdges.get(0).getPointLeft())){
				mem = new Edge(boundaryEdges.get(boundaryEdges.size()-1).getPointRight(), point);
				mem.setDegenerated(true);
			} else {
				mem = new Edge(constraint.getStartPoint(), point);
				mem.setDegenerated(true);
				iter=boundaryEdges.listIterator();
			}
		}
		if(!rightDeg){
			iter.add(mem);
		}
		return triList;
        }

	/**
	 * Connect a point to a degenerated edge, or to a set of linked degenerated
	 * edges.
	 * @param iter
	 * @param point
	 * @param tri
	 * @param prevAdded
	 * @return
	 *	An edge that could be shared in another triangle with another edge
	 * and point. null if such an edge can't exist.
	 * @throws DelaunayError
	 */
	private Edge connectToDegenerated(ListIterator<Edge> iter, Point point,
				List<DelaunayTriangle> tri, Edge prevAdded) throws DelaunayError {
		Edge current = iter.next();
		Edge ret = null;
		Edge mem = null;
		Edge memBis = null;
		LinkedList<Edge> llMem = new LinkedList<Edge>();
		//We manage the case where we'll just add a new Degenerated edge.
		if(current.isColinear(point)){
			while(current.isColinear(point) && iter.hasNext()){
				current = iter.next();
			}
			//If we've been stop because current.isColinear(point) is false,
			//we must go one step back.
			if(!current.isColinear(point)){
				current = iter.previous();
				current = iter.previous();
				ret = new Edge(current.getEndPoint(),point);
				ret.setDegenerated(true);
				addedEdges.add(ret);
				iter.next();
				iter.add(ret);
				return ret;
			} else {
				ret = new Edge(current.getEndPoint(),point);
				ret.setDegenerated(true);
				addedEdges.add(ret);
				iter.add(ret);
				return ret;
			}
		}
		//We are going to build triangles ! Ready... let's go !
		boolean nextReached = false;
		boolean upper = current.isLeft(point);
		iter.previous();
		while (iter.hasNext()) {
			current=iter.next();
			if(!current.isDegenerated()){
				//We stop the loop.
				nextReached = true;
				break;
			}
			if(upper){
				//We can let the current degenerated edges in the order they are.
				if(mem == null){
					ret = new Edge(point, current.getStartPoint());
					mem = ret;
				}
				//we build the edge we don't know yet
				memBis = new Edge(current.getEndPoint(), point);
				//We build the triangle and add it to the list.
				tri.add(new DelaunayTriangle(current, memBis, mem));
				//We store memBis in mem in order not to loose it
				mem = memBis;
				//current is not degenerated anymore
				current.setDegenerated(false);
			} else {
				//We must swap all the degenerated edges(as they are supposed to be
				//oriented the same way) and reverse their order in the boundaryEdges list.
				//We store the degenerated edges in
				llMem.addFirst(current);
				current.swap();
				//We can remove current from the boundaryEdges list, as it will be
				//added back in the end.
				iter.remove();
				//We check that the first edge does not already exist in the boundary:
				if(mem == null){
					//Be careful, we've swapped the edge !
					mem = new Edge(current.getEndPoint(),point);
					if(prevAdded != null && mem.equals(prevAdded)){
						//We avoid edge duplication here.
						mem=prevAdded;
					} else {
						addedEdges.add(mem);
						iter.add(mem);
					}
				}
				//We build the Edge we don't know yet
				memBis = new Edge(point, current.getStartPoint());
				addedEdges.add(memBis);
				//And we add the new Triangle to the list.
				tri.add(new DelaunayTriangle(mem, memBis, current));
				mem=memBis;
				//current is not degenerated anymore
				current.setDegenerated(false);
			}
			
		}
		//We must finalize our process :
		if(upper){
			if(nextReached){
				iter.previous();
			}
			iter.add(mem);
		} else {
			if(nextReached){
				iter.previous();
			}
			iter.add(mem);
			for(Edge ed : llMem){
				iter.add(ed);
			}
		}
		return ret;
	}

	/**
	 * Connect a single point to this boundary part.
	 * @param point
	 * @return
	 * @throws DelaunayError
	 */
        List<DelaunayTriangle> connectPoint(Point point) throws DelaunayError{
		return connectPoint(point, null);
        }
}
