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
import org.jdelaunay.delaunay.error.DelaunayError;
import org.jdelaunay.delaunay.geometries.DEdge;
import org.jdelaunay.delaunay.geometries.DPoint;
import org.jdelaunay.delaunay.geometries.DTriangle;

/**
 * The boundary of the mesh. During the computation of the mesh, the boundary is
 * considered as a set of parts (BoundaryPart instance) that are sorted vertically.
 * When adding a point, we will check to which part of the boundary it can be linked,
 * by using the vertical sort that has been made before.
 * @author Alexis Guéganno
 */
final class Boundary {

        //The boundary, as a list of BoundaryPart instances.
        private List<BoundaryPart> boundary;
	//The bad edges resulting of the last point insertion.
	private List<DEdge> badEdges;
	//The edges added to the mesh during the last point insertion.
	private List<DEdge> addedEdges;

        Boundary(){
                boundary = new ArrayList<BoundaryPart>();
        }

        /**
         * Retrieve the boundary of the mesh as a list of boundary parts.
         * @return
         */
        List<BoundaryPart> getBoundary(){
                return boundary;
        }

	/**
	 * Set the list of BoundaryPart.
	 * @param bound
	 */
        void setBoundary(final List<BoundaryPart> bound){
		if(bound == null){
			boundary = new ArrayList<BoundaryPart>();
		} else {
			boundary = bound;
		}
        }

	/**
	 * Set the list of added Edges. Does not need to be publc, clearly.
	 * @param edges
	 */
	private void setAddedEdges(List<DEdge> edges){
		addedEdges = edges;
	}

	/**
	 * Get the edges added during the last insertion of a point in the mesh.
	 * @return
	 */
	List<DEdge> getAddedEdges(){
		return addedEdges;
	}

	/**
	 * set the list of bad edges resulting of the last insertion.
	 * @param edges
	 */
	private void setBadEdges(List<DEdge> edges){
		badEdges = edges;
	}

	/**
	 * Get the list of edges that must be tested with the flip flap-algorithm
	 * because of the last insertion of a point.
	 * @return
	 */
	List<DEdge> getBadEdges(){
		return badEdges;
	}

        /**
         * Connect a new DPoint to the boundary. This operation will alter the
         * boundary, by potentially adding or removing some boundary parts. Moreover,
         * in every cases, at least one BoundaryPart will be modified.
	 *
	 * @param pt
	 * @param constraints
	 * @return
	 * @throws DelaunayError
	 */
        List<DTriangle> insertPoint(final DPoint pt, final List<DEdge> constraints) throws DelaunayError {
		if(constraints != null && !constraints.isEmpty() && !pt.equals(constraints.get(0).getPointLeft())){
			throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_CAN_NOT_CONNECT_POINT, 
                                "the point and the constraint do not match.");
		}
		List<Integer> indices = getEligibleParts(pt);
		if(indices.isEmpty()){
			throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_CAN_NOT_CONNECT_POINT);
		}
		List<DTriangle> addedTri;
		List<BoundaryPart> tmpBd ;
		BoundaryPart bp;
		BoundaryPart splitBp;
		List<BoundaryPart> splitList = new ArrayList<BoundaryPart>();
		List<DEdge> bad;
		List<DEdge> added;
		List<DEdge> tmpAdded;
		if(indices.size()==1){
			//parts contain only one BoundaryPart : the point is not the right
			//extremity of a constraint linked to the edge, unless it is the righ extremity
			//of the constraint of the lowest BP.
			//We can connect it directly : it won't provoke the
			//removal of any BoundaryPart
			int index = indices.get(0);
			if(index == -1){
				//if we come here, then we are working on the lowest BP of this boundary.
				//We must create a degenerated edge, use it to create a new BP
				//and add this bp to the boundary.
				index = 0;
				DEdge ed = new DEdge(boundary.get(0).getConstraint().getPointLeft(),pt);
				if(constraints != null && !constraints.isEmpty()){
					for(DEdge con : constraints){
						ed = ed.equals(con) ? con : ed;
					}
				}
				ed.setDegenerated(true);
				List<DEdge> led= new LinkedList<DEdge> ();
				led.add(ed);
				bp=new BoundaryPart(led);
				boundary.add(0, bp);
				addedTri = bp.connectPoint(pt);
				bp.getAddedEdges().add(ed);
			} else {
				bp = boundary.get(index);
				addedTri = bp.connectPoint(pt);
				//If we've added the right point of the constraint of the lowest boundary,
				//we must set the said constraint to null.
				if(index==0 && bp.getConstraint() != null && bp.getConstraint().getPointRight().equals(pt)){
					bp.setConstraint(null);
				}
			}
			setBadEdges(bp.getBadEdges());
			setAddedEdges(bp.getAddedEdges());
			if(constraints != null && !constraints.isEmpty()){
				//We must split bp into two or more boundary parts.
				splitBp = bp.split(constraints.get(constraints.size()-1));
				for(int i = 0; i<constraints.size()-1; i++) {
					splitList.add(new BoundaryPart(constraints.get(i)));
				}
				splitList.add(splitBp);
				//We insert the newly obtained BP in the boundary
				boundary.addAll(index+1, splitList);
			}
		} else {
			//We retrieve the informations of the connection of the point
			//to the lowest BoundaryPart
			//We are going to replace two or more boundary parts with
			//one or two boundary parts.
			//Let's go.

			//we retrieve the first bp of the list.
			bp = boundary.get(indices.get(0));
			//We prepare the BP we will add in the end.
			BoundaryPart newBP = indices.get(0) == 0 && bp.getConstraint()!=null
							&& pt.equals(bp.getConstraint().getPointRight()) ?
					new BoundaryPart(new ArrayList<DEdge>()) :
					new BoundaryPart(new ArrayList<DEdge>(), bp.getConstraint());
			//We must know the constraint that bound the next BP to avoid the
			//creation of duplicates.
			DEdge nextCstr = boundary.get(indices.get(1)).getConstraint();
			//we start the connection.
			addedTri = bp.connectPoint(pt, nextCstr);
			bad = bp.getBadEdges();
			added = bp.getAddedEdges();
			//We start to fill the edge we'll add in the end.
			int bpSize = bp.getBoundaryEdges().size();
			DEdge ed = bp.getBoundaryEdges().get(bpSize-1);
			if(bpSize == 1 ||(ed.isLocked() && ed.isDegenerated())){
				newBP.setBoundaryEdges(bp.getBoundaryEdges());
			} else {
				newBP.setBoundaryEdges(bp.getBoundaryEdges().subList(0, bpSize-1));
			}
			//And now we can process the other edges.
			for(int i = 1; i<indices.size(); i++){
				//we don't want to go too far.
				if(i+1<indices.size()){
					nextCstr = boundary.get(indices.get(i+1)).getConstraint();
				} else {
					nextCstr = null;
				}
				bp = boundary.get(indices.get(i));
				addedTri.addAll(bp.connectPoint(pt, nextCstr));
				bad.addAll(bp.getBadEdges());
				tmpAdded = bp.getAddedEdges();
				if(tmpAdded.size()>1) {
					added.get(added.size()-1).setDegenerated(false);
					added.addAll(tmpAdded.subList(1, tmpAdded.size()));
				}
			}
			//We must use the last altered BP to retrieve the boundary
			//edges
			List<DEdge> tmpLast = newBP.getBoundaryEdges();
			DEdge ed0 = bp.getBoundaryEdges().get(0);
			if(bp.getBoundaryEdges().size()==1 && bp.getBoundaryEdges().get(0).equals(bp.getConstraint())){
				//We are on the right point of a constraint. The Boundary
				//Part that is associated to it does not contain any
				//BoundaryEdge, consequently we must add the constraint
				//edge to the boundaryEdges of newBP
				if(!tmpLast.isEmpty() && !tmpLast.get(tmpLast.size()-1).equals(bp.getConstraint())){
					tmpLast.add(bp.getConstraint());
				//We must swap the edge if necessary.
					if(bp.getConstraint().getPointLeft().equals(bp.getConstraint().getStartPoint())){
						bp.getConstraint().swap();
					}
				}
			} else if((ed0.getRight()==null || ed0.getLeft()==null)
					&& !ed0.equals(tmpLast.get(0)) && !ed0.equals(tmpLast.get(tmpLast.size()-1))){
				//We check if bp.getBoundaryEdges.get(0) has an empty side, ie if it is linked to
				//one triangle or less.
				tmpLast.addAll(bp.getBoundaryEdges());
			} else {
				tmpLast.addAll(bp.getBoundaryEdges().subList(1, bp.getBoundaryEdges().size()));
			}
			newBP.setBoundaryEdges(tmpLast);
			setAddedEdges(added);
			setBadEdges(bad);
			//We must replace the eligible parts with the one we've just
			//created.
			if(constraints != null && !constraints.isEmpty()){
				splitBp = newBP.split(constraints.get(constraints.size()-1));
				for(int i = 0; i<constraints.size()-1; i++) {
					splitList.add(new BoundaryPart(constraints.get(i)));
				}
				splitList.add(splitBp);
				tmpBd = new ArrayList<BoundaryPart>();
				tmpBd.addAll(boundary);
				boundary = new ArrayList<BoundaryPart>();
				boundary.addAll(tmpBd.subList(0, indices.get(0)));
				//We must not forget newBP !
				boundary.add(newBP);
				boundary.addAll(splitList);
				boundary.addAll(tmpBd.subList(indices.get(indices.size()-1)+1, tmpBd.size()));
			} else {
				boundary.set(indices.get(0), newBP);
				tmpBd = new ArrayList<BoundaryPart>();
				tmpBd.addAll(boundary);
				boundary = new ArrayList<BoundaryPart>();
				boundary.addAll(tmpBd.subList(0, indices.get(0)+1));
				boundary.addAll(tmpBd.subList(indices.get(indices.size()-1)+1, tmpBd.size()));
			}
		}
		return addedTri;
        }

	/**
	 * Insert a point in the mesh, without any constraint linked to it.
	 * @param point
	 * @param constraint
	 * @return
	 * @throws DelaunayError
	 */
	List<DTriangle> insertPoint(final DPoint point) throws DelaunayError{
		return insertPoint(point, null);
	}

	/**
	 * Retrieve the parts of the boundary that can be used for the insertion of 
         * the <code>DPoint</code> point.
	 * @param point
	 * @return
	 */
	List<Integer> getEligibleParts(final DPoint point){
		//we treat the cases where the list contains one or none element.
		ArrayList<Integer> ret = new ArrayList<Integer>();
		if(boundary.size() <= 1){
			//There is only one boundary part, or boundary is empty.
			if(boundary.get(0).getConstraint() != null && boundary.get(0).getConstraint().isRight(point) ){
				//we are going to add a new Boundary Part to the boundary.
				ret.add(-1);
			}else {
				//We can work with the boundary part that already exists.
				ret.add(0);
			}
			return ret;
		}
		int size = boundary.size();
		//In some cases, the lowest BoundaryPart can contain a constraint DEdge.
		//We must manage this case.
		if(boundary.get(0).getConstraint() != null && boundary.get(0).getConstraint().isRight(point)){
			//we are going to add a new Boundary Part to the boundary.
			ret.add(-1);
			return ret;

		}
		if(boundary.get(0).getConstraint() != null && boundary.get(0).isConstraintRightPoint(point)){
			ret.add(0);
			ret.addAll(getUpperSameRightPoint(0, boundary.get(0)));
			return ret;
		}
		//We first check the extremities.
		if(boundary.get(1).isConstraintRightPoint(point)){
			ret.add(0);
			ret.add(1);
			ret.addAll(getUpperSameRightPoint(1, boundary.get(1)));
			return ret;
		}
		if(boundary.get(1).pointIsLower(point)){
			ret.add(0);
			return ret;
		}
		if(boundary.get(size -1).isConstraintRightPoint(point)){
			ret.addAll(getLowerSameRightPoint(size-1, boundary.get(size-1)));
			ret.add(size-1);
			return ret;
		}
		if(boundary.get(size-1).pointIsUpper(point)){
			ret.add(size-1);
			return ret;
		}

		BoundaryPart bp;
		BoundaryPart bpo ;
		int index = boundary.size()/2;
		int delta = index ;
		boolean next = true;
		//We will make a binary search in the list of boundaries.
		while(next){
			bp=boundary.get(index);
			if(bp.pointIsUpper(point)){
				//First we check upper...
				bpo = boundary.get(index+1);
				if(bpo.pointIsLower(point)){
					//We've found the two flanking boundary part for this point
					ret.add(index);
					return ret;
				} else if(bpo.isConstraintRightPoint(point)){
					//We've got a positive match, let's use it.
					ret.add(index);
					ret.addAll(getUpperSameRightPoint(index, bpo));
					return ret;
				} else {
					delta = delta/2 > 0 ? delta/2 : 1;
					index = index+delta;
				}
			} else if(bp.isConstraintRightPoint(point)){
				//...then on...
				//We add the parts that are lower,
				ret.addAll(getLowerSameRightPoint(index, bp));
				//... then bp...
				ret.add(index);
				//... and finally those that are upper. We'll travel through them
				//in the right order, so.
				ret.addAll(getUpperSameRightPoint(index, bp));
				return ret;
			} else {
				//...and finally lower.
				bpo = boundary.get(index-1);
				if(bpo.pointIsUpper(point)){
					//We've found the two flanking boundary part for this point
					ret.add(index - 1);
					return ret;
				} else if(bpo.isConstraintRightPoint(point)){
					//We've got a positive match, let's use it.
					ret.addAll(getLowerSameRightPoint(index-1, bpo));
					ret.add(index - 1);
					return ret;
				} else {
					delta = delta/2 > 0 ? delta/2 : 1;
					index = index-delta;
				}
			}
		}
		return ret;
	}

	/**
	 * Retrieve the BoundaryPart that share the same right point as orig,
	 * starting at index in the boundary list.
	 * DOES NOT include orig in the returned set.
	 * DOES NOT return the first boundarypart that does not share the same right
	 * point as orig and that is upper than it in the boundary.
	 *
	 * The returned parts come in the order we would find them if we iterated
	 * overt the BoundaryParts of this boundary.
	 *
	 * @param index
	 * @param orig
	 * @return
	 */
	private List<Integer> getUpperSameRightPoint(int index, BoundaryPart orig){
		DPoint point = orig.getConstraint().getPointRight();
		List<Integer> ret = new ArrayList<Integer>();
		BoundaryPart bp;
		for(int i = index+1; i < boundary.size();i++){
			bp=boundary.get(i);
			if(bp.getConstraint().getPointRight().equals(point)){
				ret.add(i);
			} else {
				break;
			}
		}
		return ret;
	}

	/**
	 * Retrieve the boundarypart that share the same right point as orig,
	 * starting at index in the boundary list, and going in reverse order.
	 * DOES NOT include orig in the returned set.
	 * DOES return the first boundarypart that does not share the same right
	 * point as orig and that is lower than it in the boundary.
	 *
	 * The returned parts come in the order we would find them if we iterated
	 * overt the BoundaryParts of this boundary.
	 *
	 * @param index
	 * @param orig
	 * @return
	 */
	private List<Integer> getLowerSameRightPoint(int index, BoundaryPart orig){
		DPoint point = orig.getConstraint().getPointRight();
		List<Integer> ret = new ArrayList<Integer>();
		BoundaryPart bp;
		for(int i = index-1; i >=0;i--){
			bp=boundary.get(i);
			if(bp.getConstraint().getPointRight().equals(point)){
				ret.add(i);
			} else {
				//We add this last BP, as it is eligible for a connection
				//with the new DPoint.
				ret.add(i);
				break;
			}
		}
		//We've traveled through the list in reverse order, we must fix that
		//before giving the list to the calling method
		Collections.reverse(ret);
		return ret;
	}
}
