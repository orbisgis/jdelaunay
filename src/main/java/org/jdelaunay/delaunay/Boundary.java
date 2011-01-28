package org.jdelaunay.delaunay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The boundary of the mesh. During the computation of the mesh, the boundary is
 * considered as a set of parts (BoundaryPart instance) that are sorted vertically.
 * When adding a point, we will check to which part of the boundary it can be linked,
 * by using the vertical sort that has been made before.
 * @author alexis
 */
final class Boundary {

        //The boundary, as a list of BoundaryPart instances.
        private List<BoundaryPart> boundary;

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
         * Connect a new Point to the boundary. This operation will alter the
         * boundary, by potentially add or remove some boundary parts. Moreover,
         * in every cases, at least one BoundaryPart will be modified.
         * @param pt
         */
        List<DelaunayTriangle> insertPoint(final Point pt){
		List<BoundaryPart> parts = getEligibleParts(pt);
                throw new UnsupportedOperationException();
        }

	/**
	 *
	 * @param point
	 * @param constraint
	 * @return
	 * @throws DelaunayError
	 */
	List<DelaunayTriangle> inserPointWithConstraint(final Point point, final Edge constraint) throws DelaunayError{
		if(!point.equals(constraint.getPointLeft())){
			throw new DelaunayError(106, "the point and the constraint do not match.");
		}
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param point
	 * @return
	 */
	List<BoundaryPart> getEligibleParts(final Point point){
		//we treat the cases where the list contains one or none element.
		if(boundary.size() <= 1){
			return new ArrayList<BoundaryPart>();
		}
		int size = boundary.size();
		ArrayList<BoundaryPart> ret = new ArrayList<BoundaryPart>();
		//We first check the extremities.
		if(boundary.get(1).isConstraintRightPoint(point)){
			ret.add(boundary.get(0));
			ret.addAll(getUpperSameRightPoint(0, boundary.get(0)));
			return ret;
		}
		if(boundary.get(1).pointIsLower(point)){
			ret.add(boundary.get(0));
			return ret;
		}
		if(boundary.get(size -1).isConstraintRightPoint(point)){
			ret.addAll(getLowerSameRightPoint(size-1, boundary.get(size-1)));
			ret.add(boundary.get(size-1));
			return ret;
		}
		if(boundary.get(size-1).pointIsUpper(point)){
			ret.add(boundary.get(size-1));
			return ret;
		}

		BoundaryPart bp = boundary.get(0);
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
					ret.add(bp);
					return ret;
				} else if(bpo.isConstraintRightPoint(point)){
					//We've got a positive match, let's use it.
					ret.add(bp);
					ret.addAll(getUpperSameRightPoint(index+1, bpo));
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
				ret.add(bp);
				//... and finally those that are upper. We'll travel through them
				//in the right order, so.
				ret.addAll(getUpperSameRightPoint(index, bp));
				return ret;
			} else {
				//...and finally lower.
				bpo = boundary.get(index-1);
				if(bpo.pointIsUpper(point)){
					//We've found the two flanking boundary part for this point
					ret.add(bpo);
					return ret;
				} else if(bpo.isConstraintRightPoint(point)){
					//We've got a positive match, let's use it.
					ret.add(bp);
					ret.addAll(getUpperSameRightPoint(index-1, bpo));
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
	 * Retrieve the boundarypart that share the same right point as orig,
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
	private List<BoundaryPart> getUpperSameRightPoint(int index, BoundaryPart orig){
		Point point = orig.getConstraint().getPointRight();
		List<BoundaryPart> ret = new ArrayList<BoundaryPart>();
		BoundaryPart bp;
		for(int i = index+1; i < boundary.size();i++){
			bp=boundary.get(i);
			if(bp.getConstraint().getPointRight().equals(point)){
				ret.add(bp);
			} else {
				break;
			}
		}
		return ret;
	}

	/**
	 * Retrieve the boundarypart that share the same right point as orig,
	 * starting at index in the boundary list, and going in reverse order.
	 * Does NOT include orig in the returned set.
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
	private List<BoundaryPart> getLowerSameRightPoint(int index, BoundaryPart orig){
		Point point = orig.getConstraint().getPointRight();
		List<BoundaryPart> ret = new ArrayList<BoundaryPart>();
		BoundaryPart bp;
		for(int i = index-1; i >=0;i--){
			bp=boundary.get(i);
			if(bp.getConstraint().getPointRight().equals(point)){
				ret.add(bp);
			} else {
				//We add this last BP, as it is eligible for a connection
				//with the new Point.
				ret.add(bp);
				break;
			}
		}
		//We've traveled through the list in reverse order, we must fix that
		//before giving the list to the calling method
		Collections.reverse(ret);
		return ret;
	}
}
