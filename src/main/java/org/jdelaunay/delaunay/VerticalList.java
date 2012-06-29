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
import java.util.List;
import org.jdelaunay.delaunay.error.DelaunayError;
import org.jdelaunay.delaunay.geometries.DEdge;
import org.jdelaunay.delaunay.geometries.DPoint;
import org.jdelaunay.delaunay.tools.Tools;

/**
 * Store a list that is sorted "vertically", according to an attached
 * VerticalComparator
 * @author Alexis Guéganno
 */
public class VerticalList {

	//The list of constraints attached to this object
	private List<DEdge> constraintsList;
	//The comparator used by this VerticalList
	private VerticalComparator comp;

	/*****************************************************/
	/*The following attributes are used to prevent the   */
	/*library to perform many times the same comparisons */
	/*(getUpper and getLower)                            */
	/*They are here for efficiency reasons               */
	/*****************************************************/

	//The last point used for a getUpperEdge
	private DPoint lastUpperPt;
	//The last returned upper edge
	private DEdge lastUpperEd;
	//The last point used for a getLowerEdge
	private DPoint lastLowerPt;
	//The last returned lower edge
	private DEdge lastLowerEd;

	/**
	 * The default constructor. The inner comparator is instanciated with
	 * value 0.
	 */
	public VerticalList(){
		constraintsList = new ArrayList<DEdge>();
		comp = new VerticalComparator((0));
		resetVolatileAttributes();
	}

	/**
	 * Public constructor. It uses the double abs to instanciate the comparator
	 * @param abs
	 */
	public VerticalList(double abs){
		constraintsList = new ArrayList<DEdge>();
		comp=new VerticalComparator(abs);
		resetVolatileAttributes();
	}

	/**
	 * Public constructor. Uses the absciss of the point to instanciate the
	 * comparator.
	 * @param pt
	 */
	public VerticalList(DPoint pt){
		this(pt.getX());
	}

	/**
	 * get the absciss where the comparison is currently performed.
	 * @return
         *      the current evaluation abs.
	 */
	public final double getAbs(){
		return comp.getAbs();
	}

	/**
	 * Change the absciss where we want our edges to be sorted
	 * @param abs
	 */
	public final void setAbs(double abs) throws DelaunayError{
		resetVolatileAttributes();
		if(abs!=comp.getAbs()){
			comp.setAbs(abs);
			sort();
		}
	}

	/**
	 * Change the absciss where we want our edges to be sorted. For this task,
	 * we use the absciss of the point given in parameter.
	 * @param pt
	 */
	public final void setAbs(DPoint pt) throws DelaunayError{
		setAbs(pt.getX());
	}

	/**
	 * retrieve the element at position i in the list.
	 * @param i
	 * @return
         *      get the ith element in the list.
	 */
	public final DEdge get(int i){
		return constraintsList.get(i);
	}

	/**
	 * Add each of the edge in the list given in argument in this vertical
	 * list.
	 * @param constraints
	 */
	public final void addEdges(List<DEdge> constraints){
		resetVolatileAttributes();
		for(DEdge edge : constraints){
			Tools.addToSortedList(edge, constraintsList, comp);
		}
	}

	/**
	 * Add an edge to the list of constraints that are considered to be linked
	 * to the boundary of the current mesh.
	 * @param constraint
         * @return the index of constraint in the list.
	 */
	public final int addEdge(DEdge constraint){
		resetVolatileAttributes();
		if(constraintsList == null){
			constraintsList = new ArrayList<DEdge>();
		}
		return Tools.addToSortedList(constraint, constraintsList, comp);
	}

	/**
	 * Remove an edge in this vertical list. Do nothing if the edge is not present.
	 * @param constr
	 */
	public final void removeEdge(DEdge constr){
		resetVolatileAttributes();
		int index = Collections.binarySearch(constraintsList, constr, comp);
		if(index >= 0){
			constraintsList.remove(constr);
		}
	}

	/**
	 * Remove the edge at index index in this vertical list.
	 * @param index
	 * @return the removed DEdge
	 */
	public final DEdge remove(int index){
		resetVolatileAttributes();
		return constraintsList.remove(index);
	}

	/**
	 * Search an edge in the constraints linked to the boundary.
	 * @param edge
	 */
	protected final int searchEdge(DEdge edge){
		return Tools.sortedListContains(constraintsList, edge, comp);
	}

	/**
	 * Get the list of constraints linked to the boundary of the current mesh.
	 * @return the list of constraints linked to the boundary of the current mesh.
	 */
	public final List<DEdge> getVerticallySortedEdges(){
		return constraintsList;
	}

	/**
	 * get the last evaluated lower edge
	 * @return the last evaluated lower edge
	 */
	public final DEdge getLastLowerEd() {
		return lastLowerEd;
	}

	/**
	 * get the last point evaluated to perform the getLowerPoint operation
	 * @return the last point evaluated to perform the getLowerPoint operation
	 */
	public final DPoint getLastLowerPt() {
		return lastLowerPt;
	}

	/**
	 * Get the last evaluated upper edge
	 * @return the last evaluated upper edge
	 */
	public final DEdge getLastUpperEd() {
		return lastUpperEd;
	}

	/**
	 * Get the last evaluated upper point
	 * @return
         * the last evaluated upper point
	 */
	public final DPoint getLastUpperPt() {
		return lastUpperPt;
	}

	/**
	 * Sort the list to the x-coordinate of rightPt, and remove the edges of the
	 * list whose right point is equal to rightPt.
	 * @param rightPt
	 */
	public final void removeEdgeFromRightPoint(DPoint rightPt) throws DelaunayError {
		setAbs(rightPt);
		DEdge ed = new DEdge(rightPt, rightPt);
		int index = searchEdge(ed);
		index = index < 0 ? -index -1: index ;
		int i = index;
		while (i < constraintsList.size()){
			if(constraintsList.get(i).getPointRight().equals(rightPt)){
				constraintsList.remove(i);
			} else {
				break;
			}
		}
		i = index - 1;
		while(i>=0){
			if(constraintsList.get(i).getPointRight().equals(rightPt)){
				constraintsList.remove(i);
				i--;
			} else {
				break;
			}

		}
	}

	/**
	 * This method will sort the list using the abs of the current comparator.
	 * It's a bubble sort, not a merge sort, as it will be more efficient in 
	 * most cases when using sweep line.
	 */
	protected final void sort() throws DelaunayError{
		resetVolatileAttributes();
		int s = constraintsList.size();
		int i = 0;
		int c = 0;
		DEdge e1;
		DEdge e2;
		while (i < s - 1) {
			e1 = constraintsList.get(i);
			e2 = constraintsList.get(i + 1);
			c = comp.compare(e1, e2);
			if (c == 1) {
				constraintsList.set(i, e2);
				constraintsList.set(i + 1, e1);
				i = i - 1 < 0 ? 0 : i - 1;
			} else {
				i++;
			}
		}
		
	}

	/**
	 * Gets the current size of this vertical list.
	 * @return
         * the size of the list.
	 */
	public final int size(){
		return this.constraintsList.size();
	}

	/**
	 * This method resets the attributes lastUpperPt, lastUpperEd, lastLowerPt
	 * and lastLowerEd. It must be called each time a change occurs in the
	 * underlying list (add or removal, new sorting absciss)
	 */
	private void resetVolatileAttributes(){
		lastUpperEd=null;
		lastUpperPt=null;
		lastLowerEd=null;
		lastLowerPt=null;
	}

        /**
         * Search the edge that will be just upper to the point in the sorted list.
         * The list is sorted according to the abscissa of point. Consequently,
         * this method is able to change the sorting absciss of the list.
         *
         * Note that we don't use the vertical sort here : an DEdge edge is said to be
         * "upper" than point if and only if edge.getPointFromItsX(point.getX())>point.getY()
         *
         * This method is used to determine which points of the mesh boundary are
         * visible from the point to be added.
         *
         * Be careful that the edge returned is the one found using thee verticl sort.
         * It may not be "vertically upper" than point : we can't be sure that there
         * is a point of x-coordinate point.getX() on this edge.
         *
         * @param point
         * @return The edge of which the ordinate is directly greater that the one
         * of point. Null if such an edge does not exist.
         */
        public final DEdge getUpperEdge(DPoint point) throws DelaunayError{
                if(constraintsList == null || constraintsList.isEmpty()){
			lastUpperEd = null;
                        return null;
                }
		if(lastUpperPt != null && lastUpperPt.equals(point)){
			return lastUpperEd;
		}
                int size = constraintsList.size();
                double abs = point.getX();
                if(Tools.EPSILON < Math.abs(abs-getAbs())){
                        //We must change the x-coordinate where we are working
                        setAbs(abs);
                }
		lastUpperPt=point;
                DEdge search = new DEdge(point, new DPoint(point.getX()+1, point.getY(), point.getZ()));
                int index = Collections.binarySearch(constraintsList, search, comp);
                index = (index < 0 ? -index -1 : index);
		//if index == size, there is no edge upper than pRef
                if(index == size){
			lastUpperEd = null;
                        return null;
                }
                //We've checked that the list is neither null nor empty. We can perform
                //the operation in the loop at least once.
                double edgeOrd;
                do{
                        edgeOrd = constraintsList.get(index).getPointFromItsX(abs).getY();
                        index ++;
                } while(Math.abs(edgeOrd - point.getY())<Tools.EPSILON && index < size);
                //We've gone one place too far
                index --;
                edgeOrd = constraintsList.get(index).getPointFromItsX(abs).getY();
                //We must check that the last edge is really upper than point, ie that
                //point and constraintsEdge.get(size -1) are not colinear.
                //If they are, we return null
                if(index < size && Math.abs(edgeOrd - point.getY())>=Tools.EPSILON){
			search = constraintsList.get(index);
			if(index < size -1){
				//The vertical sort is designed for the intersection algorithm.
				//More accurately, , it is designed to detect intersections that
				//occur after the processing x-coordinate.
				//For our goals here, we need to travel through the list when
				//some edges have the same getPointFromItsX. Indeed, we want
				//here to detect intersections that occur before the current
				//x-coordinate.
				DEdge next;
				DPoint rightSearch;
				DPoint rightNext;
				do{
					next = constraintsList.get(index+1);
					rightSearch = search.getPointFromItsX(abs);
					rightNext = next.getPointFromItsX(abs);
					if(rightSearch.equals(rightNext)){
						search = next;
						index++;
					} else {
						break;
					}
				} while(index < size - 1);
			}
			lastUpperEd = search;
                        return lastUpperEd;
                } else {
			lastUpperEd = null;
                        return null;
                }
        }

        /**
         * Get the edge that is directly lower to the point in the sorted list.
         * The list is sorted according to the abscissa of point. Consequently,
         * this method is able to change the sorting absciss of the list.
         *
         * Note that we don't use the vertical sort here : an DEdge edge is said to be
         * "lower" than point if and only if edge.getPointFromItsX(point.getX())&lt;point.getY()
         *
         * This method is used to determine which points of the mesh boundary are
         * visible from the point to be added.
         *
         * Be careful that the edge returned is the one found using the vertical sort.
         * It may not be "vertically upper" than point : we can't be sure that there
         * is a point of x-coordinate point.getX() on this edge.
         *
         * @param point
         * @return The edge of which the ordinate is directly greater that the one
         * of point. Null if such an edge does not exist.
         */
        public final DEdge getLowerEdge(DPoint point) throws DelaunayError{
                if(constraintsList == null || constraintsList.isEmpty()){
			lastLowerEd=null;
                        return null;
                }
		if(lastLowerPt != null && lastLowerPt.equals(point)){
			return lastLowerEd;
		}
                double abs = point.getX();
                if(Tools.EPSILON < Math.abs(abs-getAbs())){
                        //We must change the x-coordinate where we are working
                        setAbs(abs);
                }
		lastLowerPt=point;
                DEdge search = new DEdge(point, new DPoint(point.getX()+1, point.getY(), point.getZ()));
                int index = Collections.binarySearch(constraintsList, search, comp);
                index = (index < 0 ? -index -1 : index);
                //we are searching for the edge that is lower. The insertionPoint is
                //the place where we would put the searchEdge, so the first potentially
                //lower edge is the one we find just befor it in the list.
                index--;
                //We've checked that the list is neither null nor empty. We can perform
                //the operation in the loop at least once.
                double edgeOrd;
		 //if index<0, there is no edge lower than point.
		//It can be <0 if the index returned by our research was 0 (or rather -1)
                if(index < 0){
			lastLowerEd=null;
                        return null;
                }
		DEdge edgeTmp;
		DPoint pointTmp;
		//we need to handle a boolean to be able to manage vertical edges
		boolean cont = true;
                do{
			edgeTmp = constraintsList.get(index);
			pointTmp = edgeTmp.getPointFromItsX(abs);
			if(pointTmp == null){
				//We are dealing with a vertical edge.
				cont=true;
			} else {
				edgeOrd = pointTmp.getY();
				cont = Math.abs(edgeOrd - point.getY())<Tools.EPSILON;
			}
                        index --;
                } while(cont && index >=0);
		//We've gone one place too far
		index ++;
		pointTmp = constraintsList.get(index).getPointFromItsX(abs);
		if(pointTmp == null){
			lastLowerEd=null;
			return null;
		} else {
			edgeOrd = pointTmp.getY();
			//We must check that the last edge is really lower than point, ie that
			//point and constraintsEdge.get(size -1) are not colinear.
			//If they are, we return null
			if(index >=0 && Math.abs(edgeOrd - point.getY())>=Tools.EPSILON){
				if(index >0){
					//The vertical sort is designed for the intersection algorithm.
					//More accurately, , it is designed to detect intersections that
					//occur after the processing x-coordinate.
					//For our goals here, we need to travel through the list when
					//some edges have the same getPointFromItsX. Indeed, we want
					//here to detect intersections that occur before the current
					//x-coordinate.
					DEdge prev;
					DPoint rightSearch;
					DPoint rightPrev;
					do{
						prev = constraintsList.get(index-1);
						rightSearch = search.getPointFromItsX(abs);
						rightPrev = prev.getPointFromItsX(abs);
						if(rightSearch.equals(rightPrev)){
							search = prev;
							index--;
						} else {
							break;
						}
					} while(index > 0);
				}
				lastLowerEd=constraintsList.get(index);
				return lastLowerEd;
			} else {
				lastLowerEd=null;
				return null;
			}
		}
	}

	/**
	 * Checks if the edges that are upper and lower than pRef in the list of
	 * constraints that are linked to the boundary intersect the edge ed given
	 * in parameter.
	 * Intersection must not be an extremity point of the two evaluated edges.
         * We don't deal with edges that intersect and that are colinear.
	 * @param pRef
	 * @param ed
	 * @return
         * true if ed intersects with the directly lower or directly upper point.
	 * @throws DelaunayError
	 */
	public final boolean intersectsUpperOrLower(DPoint pRef, DEdge ed) throws DelaunayError{
		setAbs(pRef);
		DEdge upper = getUpperEdge(pRef);
		int inter;
		if(upper!=null){
			inter = upper.intersects(ed);
			if(inter == DEdge.INTERSECT){
				return true;
			}
		}
		DEdge lower = getLowerEdge(pRef);
		if(lower!=null){
			inter = lower.intersects(ed);
			if(inter == DEdge.INTERSECT){
				return true;
			}
		}
		return false;
	}
}
