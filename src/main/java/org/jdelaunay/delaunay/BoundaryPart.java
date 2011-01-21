package org.jdelaunay.delaunay;

import java.util.ArrayList;
import java.util.List;

/**
 * A part of the boundary of the mesh. This class is built with :
 *  * A set E of edges, that are all part of the mesh.
 *  * An edge C that is a constraint used during the triangulation.
 *
 * The elements of E will be all the edges of the boundary that are upper than C,
 * and lower than the constraint Edge directly upper than C
 * @author alexis
 */
public class BoundaryPart {

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
	
}
