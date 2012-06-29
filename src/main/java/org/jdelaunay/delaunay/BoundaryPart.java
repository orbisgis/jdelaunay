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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.jdelaunay.delaunay.error.DelaunayError;
import org.jdelaunay.delaunay.geometries.DEdge;
import org.jdelaunay.delaunay.geometries.DPoint;
import org.jdelaunay.delaunay.geometries.DTriangle;

/**
 * A part of the boundary of the mesh. This class is built with :
 *  * A set E of edges, that are all part of the mesh.
 *  * An edge C that is a constraint used during the triangulation.
 *
 * The elements of E will be all the edges of the boundary that are upper than C,
 * and lower than the constraint DEdge directly upper than C
 * @author Alexis Guéganno
 */
final class BoundaryPart {

	//the section of the boundary contained in this BoundaryPart.
	private LinkedList<DEdge> boundaryEdges;
	//The constraint that define the lower scope of this boundary part.
	//The upper scope will be defined by the next BoundaryPart in the Boundary class.
	private DEdge constraint;
	//The list of edges that could be swapped during the flip-flap.
	private List<DEdge> badEdges;
	//The list of newly added Edges
	private List<DEdge> addedEdges;

	private DEdge splitMem;

	private void init(){
		badEdges = new LinkedList<DEdge>();
		addedEdges = new ArrayList<DEdge>();
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
	BoundaryPart(List<DEdge> bound, DEdge cstr){
		init();
		boundaryEdges = bound instanceof LinkedList ? (LinkedList) bound : new LinkedList(bound);
		constraint = cstr;
	}

	/**
	 * Constructor used when the constraint linked to this part of the boundary
	 * is null.
	 *
	 * This can happen for the lowest part of the boundary, fo instance.
	 * @param bound
	 */
	BoundaryPart(List<DEdge> bound){
		init();
		boundaryEdges = bound instanceof LinkedList ? (LinkedList) bound : new LinkedList(bound);
		constraint = null;
	}

	/**
	 * Constructor used when the part of boundary between this constraint and the
	 * next one is empty.
	 * @param cstr
	 */
	BoundaryPart(DEdge cstr){
		init();
		setConstraint(cstr);
		boundaryEdges = new LinkedList<DEdge>();
	}

	/**
	 * Get the list of edges associated to this part of the boundary.
	 * @return
	 */
	List<DEdge> getBoundaryEdges() {
		return boundaryEdges;
	}

	/**
	 * Set the set of edges that are associated to this boundary part.
	 * @param boundaryEdges
	 */
	void setBoundaryEdges(List<DEdge> bound) {
		if(bound == null){
			this.boundaryEdges = new LinkedList<DEdge>();
		}else {
			boundaryEdges = bound instanceof LinkedList ? (LinkedList) bound : new LinkedList(bound);
		}
	}

	/**
	 * Get the constraint that forms the lower limit of this part of the boundary.
	 * @return
	 */
	DEdge getConstraint() {
		return constraint;
	}

	/**
	 * Set the constraint that determine the lower limit of this part of the
	 * boundary.
	 * @param constraint
	 */
	void setConstraint(DEdge constraint) {
		if(constraint != null && constraint.getPointLeft().equals(constraint.getEndPoint())){
			constraint.swap();
		}
		this.constraint = constraint;
	}

	/**
	 * Get the edges added to the mesh during the last insertion of a point.
	 * @return
	 */
	List<DEdge> getAddedEdges(){
		return addedEdges;
	}

	/**
	 * Gets the edges that will need to be processed by the flip-flap algorithm
	 * @return
	 */
	List<DEdge> getBadEdges(){
		return badEdges;
	}

	/**
	 * Returns true if the point given in argument is lower than the constraint
	 * edge used to define this boundary part.
	 * @param point
	 * @return
	 */
	boolean pointIsLower(final DPoint point){
		return constraint.isRight(point);
	}

	/**
	 * Returns true if the point given in argument is upper than the constraint
	 * edge used to define this boundary part.
	 * @param point
	 * @return
	 */
	boolean pointIsUpper(final DPoint point){
		return constraint.isLeft(point);
	}

	/**
	 * Check if point is the right point of the constraint.
	 * @param point
	 * @return
	 */
	boolean isConstraintRightPoint(final DPoint point){
		return constraint.getPointRight().equals(point);
	}

	/**
	 * Check if bpo can be the next BoundaryPart of the boundary. It is true
	 * if bpo's left point lies on the last edge of this boundary part.
	 * @param bpo
	 * @return
	 */
	boolean canBeNext(BoundaryPart bpo) {
		DEdge last = boundaryEdges.get(boundaryEdges.size()-1);
		DPoint left = bpo.getConstraint().getPointLeft();
		DPoint right = bpo.getConstraint().getPointRight();
		return (left.equals(last.getStartPoint())
			|| left.equals(last.getEndPoint()) )
			&& last.isRight(right)
			&& !constraint.getPointLeft().equals(left);

	}

	/**
	 * Split this BoundaryPart in two. The edge given in argument will be used
	 * as the constraint for the new BoundaryEdge. Its leftmost point will be
	 * searched in the boundary Edges of this BoundaryPart.
	 *
	 * It's up to you to use this method the right way. It's not public API, and
	 * shouldn't be used out of the mesh production.
	 *
	 * @param cstr
	 * @return
	 */
	BoundaryPart split(DEdge cstr) throws DelaunayError {
		if(boundaryEdges.isEmpty() || cstr == null){
			//We can't split anything if we don't even have a boundary edge !
			throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_CAN_NOT_SPLIT_BP);
		}
		//The point where we'll perform the split
		DPoint split = cstr.getPointLeft();
		//We can instanciate ret, as we know its constraint.
		BoundaryPart ret = new BoundaryPart(cstr);
		if(split.equals(boundaryEdges.get(0).getStartPoint())){
			//ret will starve this of all its boundary DEdge.
			ret.setBoundaryEdges(boundaryEdges);
			this.setBoundaryEdges(new LinkedList<DEdge>());
			return ret;
		}
		LinkedList<DEdge> futureBoundary = new LinkedList<DEdge>();
		LinkedList<DEdge> otherBoundary = boundaryEdges;
		LinkedList<DEdge> degen = new LinkedList();
		ListIterator<DEdge> iter = otherBoundary.listIterator();
		DEdge course;
		boolean success = false;
		while(iter.hasNext()){
			//Next step
			course = iter.next();
			if(course.isDegenerated()){
				//We fill our memory of degenerated edges, that will be used
				//if we end our course on one (the last, hopefully...) of them.
				degen.add(course);
			}
			//The current edge will still be part of this BP's boundary edges...
			futureBoundary.add(course);
			//...so we can remove it of the boundary edges of the future BP.
			//If it is a degen edge, it will be added back in the end.
			iter.remove();
			if(course.getEndPoint().equals(split)){
				//We have ended our course on a degen DEdge. The degenerated
				//edges that are part of this BP will be duplicated (or rather,
				//their references will be duplicated).
				if(course.isDegenerated()){
					//We must reverse the order of the degen edges
					//in the newly created BP
					Collections.reverse(degen);
					//the Edges in degen are not degenerated anymore,
					//they are shared.
					for(DEdge edge : degen){
						edge.setDegenerated(false);
						edge.setShared(true);
					}
					degen.addAll(otherBoundary);
					otherBoundary = degen;
					//We must remember what is the next constraint
					splitMem = cstr;
				}
				success = true;
				break;
			}
		}
		if(!success){
			//we've failed at finding a boundary edge that own the right
			//point of cstr.
			throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_CAN_NOT_SPLIT_BP);
		} else {
			//We replace this boundary edges by the shorter set computed here.
			this.setBoundaryEdges(futureBoundary);
			//We return the new BoundaryPart
			return new BoundaryPart(otherBoundary, cstr);
		}
	}

        /**
         * Connect a single point to this boundary part. Travels through the boundary
         * edges and try to build triangles from it. The boundary is, of course,
         * updated.
	 * @param point
	 * @param nextCstr
	 * @return
	 * @throws DelaunayError
	 */
        List<DTriangle> connectPoint(DPoint point, DEdge nextCstr) throws DelaunayError{
		if(boundaryEdges==null || (boundaryEdges.isEmpty() && constraint==null)){
			throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_CAN_NOT_CONNECT_POINT);
		}
		badEdges = new ArrayList<DEdge>();
		addedEdges = new ArrayList<DEdge>();
		//This boolean will be used to travel through the degenerated edges in
		//the right way, when processing an BP that shares some degen edges
		//with another BP
		boolean revertDir = !boundaryEdges.isEmpty() && constraint != null ?
			boundaryEdges.get(0).getEndPoint().equals(constraint.getStartPoint()) :
			false;
		boolean removeDegen = !boundaryEdges.isEmpty() && splitMem != null ?
			boundaryEdges.get(boundaryEdges.size()-1).getEndPoint().equals(splitMem.getStartPoint()) :
			false;
		revertDir = removeDegen || revertDir;
                ListIterator<DEdge> iter = boundaryEdges.listIterator();
		DEdge mem = null;
		DEdge memBis = null;
		boolean endShared = false;
		DEdge current;
		boolean rightDeg = false;
		//If the boundaryEdges list is empty, we must add a degenerated edge.
		if(boundaryEdges.isEmpty()){
			return buildFirstDegen(point, nextCstr);
		}
		List<DTriangle> triList = new ArrayList<DTriangle>();
		DTriangle temp = null;
		while(iter.hasNext()){
			current = iter.next();
			//We must put current the right direction if it is degenerated.
			if(current.isDegenerated()){
				iter.previous();
				mem = connectToDegenerated(iter, point, triList, mem, revertDir, nextCstr);
				rightDeg = mem==null;
				if(mem != null && mem.isDegenerated()){
					//if we've built an DEdge that is degenerated, we can stop here
					//and return an empty list of triangles.
					return new ArrayList<DTriangle>();
				}
			} else if(current.isShared()){
				mem = connectToShared(iter, current, point, triList, mem, nextCstr);
				endShared = mem == null ? false : mem.equals(memBis);
				if(endShared){
					break;
				}
				memBis = mem;
			} else {
				if(current.isRight(point)){
					//Current is not degenerated, so it will become
					//an inner DEdge of the mesh. We must process the flip
					//flap on it if necessary.
					badEdges.add(current);
					//we can build a triangle.
					if(mem == null){
						//if not already set, we must instanciate mem
						mem = new DEdge(current.getStartPoint(),point);
						//We must check that we're not about to duplicate the
						//constraint DEdge
						mem = replaceByCstr(mem, nextCstr);
						if(mem.isShared()){
							mem.setShared(false);
						}
						if(mem.isDegenerated()){
							mem.setDegenerated(false);
						}
						//We will add an DEdge in the mesh.
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
					//We build the last DEdge of the triangle we are about to add.
					memBis = new DEdge(point, current.getEndPoint());
					memBis = replaceByCstr(memBis, nextCstr);
					//We will add an DEdge in the mesh.
					addedEdges.add(memBis);
					//we can build the triangle...
					temp = new DTriangle(current, mem, memBis);
					//...and add it to the list we'll return.
					triList.add(temp);
					//memBis is the last created DEdge - we put it in mem.
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
		//If we're here, the point was still visible from the last DEdge of
		//the list. We must add the last generated edge.
		//We check that mem is not null. It can be null if point can't be seen
		//from any edges of this boundary part
		if(mem==null && triList.isEmpty()){
			//in all cases, we are sure we can connect point to the
			//constraint edge's left point or to the last edge's right point.
			//We must determine what to do...
			connectDegenOrphan(point, iter, nextCstr);
		} else if(!rightDeg && !endShared){
			iter.add(mem);
		}
		return triList;
        }

	/**
	 * if we haven't found any triangle to build, or a set of degenerated edges
	 * to complete, we must add one degenerated edge.
	 * @param point
	 * @return
	 */
	private void connectDegenOrphan(DPoint point, ListIterator<DEdge> iter, DEdge nextCstr){
		DEdge mem = null;
		ListIterator<DEdge> iterBis = iter;
		//in all cases, we are sure we can connect point to the
		//constraint edge's left point or to the last edge's right point.
		//We must determine what to do...
		if(constraint == null ){
			if(!boundaryEdges.get(boundaryEdges.size()-1).getPointRight().equals(point)){
				mem = new DEdge(boundaryEdges.get(boundaryEdges.size()-1).getPointRight(), point);
			} else {
				return;
			}
		} else if(constraint.getLeft()!=null && constraint.getPointRight().equals(point)){
			//In this case, we are dealing with a BP where the constraint has already
			//been added in a Triangle.
			//We don't do anything.
			boundaryEdges.add(0, constraint);
			return;
		} else if (!boundaryEdges.isEmpty()) {
			//The list is not empty
			if(boundaryEdges.get(0).isShared()){
				//The first edge in the list is shared. if it is connected to the
				//constraint by its left, we must connect the new edge to the end of the list.
				if(boundaryEdges.get(0).getStartPoint().equals(constraint.getStartPoint())){
					mem = new DEdge(boundaryEdges.get(boundaryEdges.size()-1).getPointRight(), point);
				} else {
					mem = new DEdge(constraint.getStartPoint(), point);
					iterBis=boundaryEdges.listIterator();
				}
			} else {
				if(boundaryEdges.get(0).getStartPoint().equals(boundaryEdges.get(0).getPointLeft())){
					mem = new DEdge(boundaryEdges.get(boundaryEdges.size()-1).getPointRight(), point);
				} else {
					mem = new DEdge(constraint.getStartPoint(), point);
					iterBis=boundaryEdges.listIterator();
				}
			}
		} else {
			mem = new DEdge(constraint.getStartPoint(), point);
		}
		mem = replaceByCstr(mem, nextCstr);
		mem.setDegenerated(true);
		addedEdges.add(mem);
		iterBis.add(mem);
	}

	/**
	 * Perform the connection of a point to a shared edge.
	 * @return
	 */
	private DEdge connectToShared(ListIterator<DEdge> iter, DEdge share, DPoint point,
			List<DTriangle> tri, DEdge prev, DEdge nextCstr) throws DelaunayError {
		DEdge ret = null;
		//The shared DEdge is supposed to be oriented with the start point on
		//the right. We must determine if we need to consider it as in
		//reverse order.
		DEdge prevAdd = prev;
		boolean reverse = false;
		//In seom cases, when we must connect a new point to the mesh, but if we don't
		//have added any edge, we must be able to study the configuration a little deeper.
		//Indeed, if the edge is not connected to the constraint of the boundary part,
		//but must be considered in direct order, there are cases where we can
		//create triangles that are forgotten otherwise.
		boolean prevUsed = false;
		boolean connectedToPrev = false;
		connectedToPrev = prevAdd != null &&
				(share.isExtremity(prevAdd.getStartPoint())
				|| share.isExtremity(prevAdd.getEndPoint()));
		boolean connectedToConstraint = false;
		if(constraint != null){
			reverse = share.isLeft(constraint.getPointLeft());
			connectedToConstraint = share.isExtremity(constraint.getStartPoint())
					|| share.isExtremity(constraint.getEndPoint());
			if(!connectedToConstraint){
				//We check that the current shared edge is connected to the previous
				//edge in the boundary in direct order. If it is, we must
				//consider to connect the point to this shared edge.
				iter.previous();
				if(iter.hasPrevious()){
					DEdge temp = iter.previous();
					if(temp.getEndPoint().equals(share.getStartPoint())){
						prevAdd = temp;
						//We are in direct order. We consider that the share edge
						//is connected to the constraint, and we set prevUsed to true,
						//in order not to return a false mem
						//value in the end.
						connectedToConstraint=true;
						prevUsed = true;
					}
					//We come back
					iter.next();
				}
				//We come back
				iter.next();
			}
			if(!connectedToConstraint && !connectedToPrev){
				return prevAdd;
			}
			if(connectedToPrev){
				//If the previously added edge is connected to the end point
				//of share, we must go in reverse order.
				reverse = share.getEndPoint().equals(prevAdd.getStartPoint())
					|| share.getEndPoint().equals(prevAdd.getEndPoint());
			} else {
				//If prevAdd is not connected or null, we perform our test with
				//constraint
				reverse = share.getEndPoint().equals(constraint.getStartPoint())
					|| share.getEndPoint().equals(constraint.getEndPoint());
			}
		} else if(prevAdd != null && !connectedToPrev){
			return prevAdd;
		} else if(prevAdd != null){
				//If the previously added edge is connected to the end point
				//of share, we must go in reverse order.
				reverse = share.getEndPoint().equals(prevAdd.getStartPoint())
					|| share.getEndPoint().equals(prevAdd.getEndPoint());
		}
		//And we can perform the connection
		if(reverse && share.isLeft(point)){
			ret = new DEdge(point, share.getStartPoint());
			ret = replaceByCstr(ret, nextCstr);
			if(connectedToPrev){
				//We must go in reverse order, and share and prevAdd are connected.
				//Consequently, share and prevAdd share the endPoint of share.
				//The other point of prevAdd is point. If it is not, we'll
				//receive a DelaunayError.
				tri.add(new DTriangle(share, prevAdd, ret));
				//We must remove the current shared edge from the
				//list of boundaryEdges
				iter.remove();
			} else {
				//We must go in reverse order, and share and prevAdd are not
				//connected. We create the other needed edge.
				DEdge e = new DEdge(share.getEndPoint(), point);
				e = replaceByCstr(e, nextCstr);
				e.setDegenerated(false);
				tri.add(new DTriangle(share, e, ret));
				//We must remove the current shared edge from the
				//list of boundaryEdges
				iter.remove();
				//we must add e to the list of boundaryEdges
				iter.add(e);
				addedEdges.add(e);
			}
			addedEdges.add(ret);
			//we save this new Triangle.
			//share is not shared anymore !
			share.setShared(false);
		} else if(!reverse && share.isRight(point)){
			ret = new DEdge(point, share.getEndPoint());
			ret = replaceByCstr(ret, nextCstr);
			if(connectedToPrev){
				//We travel in the direct order, shared and prevAdd
				//are connected. Consequently, share and prevAdd share
				//the startPoint of share.
				//The other point of prevAdd is point. If it is not,
				//we'll receive a DelaunayError when creating the triangle.
				tri.add(new DTriangle(share, prevAdd, ret));
				//We must remove the current shared edge from the
				//list of boundaryEdges
				iter.remove();
			} else {
				//We travel in the direct order, and shared and prevAdd
				//are not connected. We create the other needed edge.
				DEdge e = new DEdge(share.getStartPoint(), point);
				e = replaceByCstr(e, nextCstr);
				tri.add(new DTriangle(share, e, ret));
				//We must remove the current shared edge from the
				//list of boundaryEdges
				iter.remove();
				//we must add e to the list of boundaryEdges
				iter.add(e);
				addedEdges.add(e);
			}
			addedEdges.add(ret);
			//share is not shared anymore !
			share.setShared(false);
			//share must be swapped.
			share.swap();
		} else if(prevAdd != null && !prevUsed){
			//we must add the prevAdd edge, as it is now part of the boundary.
			iter.previous();
			iter.add(prevAdd);
			iter.next();
		}
		//we don't want to return prevAdd if prevUsed has been set to true,
		//as in this case there is not really an edge previously connected to the mesh.
		return ret == null && !prevUsed ? prevAdd : ret;
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
	private DEdge connectToDegenerated(ListIterator<DEdge> iter, DPoint point,
				List<DTriangle> tri, DEdge prevAdded, boolean revertDir, DEdge nextCstr) throws DelaunayError {
		DEdge current = iter.next();
		DEdge ret = null;
		DEdge mem = null;
		DEdge memBis = null;
		LinkedList<DEdge> llMem = new LinkedList<DEdge>();
		//We manage the case where we'll just add a new degenerated DEdge.
		if(current.isColinear(point)){
			while(current.isColinear(point) && iter.hasNext() && current.isDegenerated() && !current.isShared()){
				current = iter.next();
			}
			//If we've been stopped because current.isColinear(point) is false,
			//or because it's not degenerated, we must go one step back.
			if(!current.isColinear(point) || !current.isDegenerated()){
				current = iter.previous();
				current = iter.previous();
				ret = new DEdge(current.getEndPoint(),point);
				ret = replaceByCstr(ret, nextCstr);
				ret.setDegenerated(true);
				addedEdges.add(ret);
				iter.next();
				iter.add(ret);
				return ret;
			} else {
				if(!current.getEndPoint().equals(point)){
					ret = new DEdge(current.getEndPoint(),point);
					ret = replaceByCstr(ret, nextCstr);
					ret.setDegenerated(true);
					addedEdges.add(ret);
					iter.add(ret);
				}
				return ret;
			}
		}
		//We are going to build triangles ! Ready... let's go !
		boolean nextReached = false;
		boolean isUpper = current.isLeft(point);
		iter.previous();
		while (iter.hasNext()) {
			current=iter.next();
			if(!current.isDegenerated()){
				//We stop the loop.
				nextReached = true;
				break;
			}
			if(isUpper){
				//we must check that edges will be processed in the right order...
				if(revertDir){
					//the degenerated edges will keep their orientation, but ret will
					//be the last one to be generated.
					if(mem == null){
						mem = new DEdge(current.getEndPoint(), point);
						mem = replaceByCstr(mem, nextCstr);
						ret = mem;
						addedEdges.add(mem);
					}
					//we build the edge we don't know yet
					memBis = new DEdge(point, current.getStartPoint());
					memBis = replaceByCstr(memBis, nextCstr);
					addedEdges.add(memBis);
					//We build the triangle and add it to the list.
					tri.add(new DTriangle(current, memBis, mem));
					//We store memBis in mem in order not to loose it
					mem = memBis;
					//current is not degenerated anymore
					current.setDegenerated(false);
					//The edge must not be duplicated anymore
					iter.remove();
				} else {
					//We can let the current degenerated edges in the order they are.
					if(mem == null){
						ret = new DEdge(point, current.getStartPoint());
						ret = replaceByCstr(ret, nextCstr);
						mem = ret;
						addedEdges.add(mem);
					}
					//we build the edge we don't know yet
					memBis = new DEdge(current.getEndPoint(), point);
					memBis = replaceByCstr(memBis, nextCstr);
					addedEdges.add(memBis);
					//We build the triangle and add it to the list.
					tri.add(new DTriangle(current, memBis, mem));
					//We store memBis in mem in order not to loose it
					mem = memBis;
					//current is not degenerated anymore
					current.setDegenerated(false);
				}
			} else {
				//We must swap all the degenerated edges(as they are supposed to be
				//oriented the same way) and reverse their order in the boundaryEdges list.
				//We store the degenerated edges in
				if(!revertDir){
					llMem.addFirst(current);
				}
				current.swap();
				//We can remove current from the boundaryEdges list, as it will be
				//added back in the end, unless we are treating an BP that shares
				//boundary Edges with the next one.
				iter.remove();
				//We check that the first edge does not already exist in the boundary:
				if(mem == null){
					//Be careful, we've swapped the edge !
					mem = new DEdge(current.getEndPoint(),point);
					if(mem.equals(prevAdded)){
						//We avoid edge duplication here.
						mem=prevAdded;
					} else {
						mem = replaceByCstr(mem, nextCstr);
						addedEdges.add(mem);
						iter.add(mem);
					}
				}
				//We build the DEdge we don't know yet
				memBis = new DEdge(point, current.getStartPoint());
				memBis = replaceByCstr(memBis, nextCstr);
				addedEdges.add(memBis);
				//And we add the new Triangle to the list.
				tri.add(new DTriangle(mem, memBis, current));
				mem=memBis;
				//current is not degenerated anymore
				current.setDegenerated(false);
			}

		}
		//We must finalize our process :
		if(isUpper){
			if(nextReached){
				iter.previous();
			}
			if(revertDir){
				iter.add(ret);
				ret=mem;
			} else {
				iter.add(mem);
			}
		} else {
			if(nextReached){
				iter.previous();
			}
			iter.add(mem);
			for(DEdge ed : llMem){
				iter.add(ed);
			}
		}
		return ret;
	}

	/**
	 * mem is replaced by cstr if they are equal, or by this constraint
	 * DEdge if they are equal. DPoint order is kept.
	 * @param mem
	 * @param cstr
	 * @return
	 */
	private DEdge replaceByCstr(DEdge mem, DEdge cstr){
		DEdge memRet = mem.equals(cstr) ? cstr : mem;
		if(mem.equals(constraint)){
			memRet = constraint;
			constraint = null;
		}
		if(memRet.getStartPoint().equals(mem.getEndPoint())){
			memRet.swap();
		}
		return memRet;
	}

	/**
	 * Build the first degenerated edge linked to this BoundaryPart
	 * @param point
	 * @param nextCstr
	 * @return
	 */
	private List<DTriangle> buildFirstDegen(DPoint point, DEdge nextCstr){
		DEdge mem = new DEdge(constraint.getPointLeft(),point);
		mem.setDegenerated(true);
		if(mem.equals(constraint)){
			mem = constraint;
			if(mem.getLeft()==null && mem.getRight()==null){
				mem.setDegenerated(true);
			}
		} else {
			mem = mem.equals(nextCstr) ? nextCstr : mem;
		}
		addedEdges.add(mem);
		boundaryEdges.add(mem);
		return new ArrayList<DTriangle>();
	}

	/**
	 * Connect a single point to this boundary part.
	 * @param point
	 * @return
	 * @throws DelaunayError
	 */
        List<DTriangle> connectPoint(DPoint point) throws DelaunayError{
		return connectPoint(point, null);
        }
}
