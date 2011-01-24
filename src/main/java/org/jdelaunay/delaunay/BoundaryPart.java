package org.jdelaunay.delaunay;

import java.util.ArrayList;
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
class BoundaryPart {

	//the section of the boundary contained in this BoundaryPart.
	List<Edge> boundaryEdges;
	//The constraint that define the lower scope of this boundary part.
	//The upper scope will be defined by the next BoundaryPart in the Boundary class.
	Edge constraint;

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
	public BoundaryPart(List<Edge> bound, Edge cstr){
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
	public BoundaryPart(List<Edge> bound){
		boundaryEdges = bound;
		constraint = null;
	}

	/**
	 * Constructor used when the part of boundary between this constraint and the
	 * next one is empty.
	 * @param cstr
	 */
	public BoundaryPart(Edge cstr){
		constraint = cstr;
		boundaryEdges = new ArrayList<Edge>();
	}

	/**
	 * Get the list of edges associated to this part of the boundary.
	 * @return
	 */
	public List<Edge> getBoundaryEdges() {
		return boundaryEdges;
	}

	/**
	 * Set the set of edges that are associated to this boundary part.
	 * @param boundaryEdges
	 */
	public void setBoundaryEdges(List<Edge> boundaryEdges) {
		this.boundaryEdges = boundaryEdges;
	}

	/**
	 * Get the constraint that forms the lower limit of this part of the boundary.
	 * @return
	 */
	public Edge getConstraint() {
		return constraint;
	}

	/**
	 * Set the constraint that determine the lower limit of this part of the
	 * boundary.
	 * @param constraint
	 */
	public void setConstraint(Edge constraint) {
		this.constraint = constraint;
	}

	/**
	 * Check if bpo can be the next BoundaryPart of the boundary. It is true
	 * if bpo's left point lies on the last edge of this boundary part.
	 * @param bpo
	 * @return
	 */
	public boolean canBeNext(BoundaryPart bpo) {
		Edge last = boundaryEdges.get(boundaryEdges.size()-1);
		Point left = bpo.getConstraint().getPointLeft();
		Point right = bpo.getConstraint().getPointRight();
		return (left.equals(last.getStartPoint())
			|| left.equals(last.getEndPoint()) )
			&& last.isRight(right)
			&& !constraint.getPointLeft().equals(left);

	}

        /**
         * Connect a single point to this boundary part. Travels through the boundary
         * edges and try to build triangles from it. The boundary is, of course,
         * updated.
         * @param point
         * @return
         */
        public  List<DelaunayTriangle> connectPoint(Point point) throws DelaunayError{
                ListIterator<Edge> iter = boundaryEdges.listIterator();
		Edge firstFound = null;
		Edge mem = null;
		Edge memBis = null;
		Edge current;
		//If the boundaryEdges list is empty, we must add a degenerated edge.
		if(boundaryEdges.isEmpty()){
			firstFound = new Edge(constraint.getPointLeft(),point);
			firstFound.setDegenerated(true);
			boundaryEdges.add(firstFound);
			return new ArrayList<DelaunayTriangle>();
		}
		List<DelaunayTriangle> triList = new ArrayList<DelaunayTriangle>();
		DelaunayTriangle temp = null;
		while(iter.hasNext()){
			current = iter.next();
			if(current.isRight(point)){
				//we can build a triangle.
				if(mem == null){
					//if not already set, we must instanciate mem
					mem = new Edge(current.getStartPoint(),point);
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
		//If we're here, the point was still visible from the last Edge of
		//the list. We must add the last generated edge.
		iter.add(mem);
		return triList;
        }

        public List<DelaunayTriangle> connectPoint(Point point, Edge nextCstr){
                throw new UnsupportedOperationException();
                
        }
}
