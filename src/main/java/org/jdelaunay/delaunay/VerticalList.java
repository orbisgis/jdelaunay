package org.jdelaunay.delaunay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Store a list that is sorted "vertically", according to an attached
 * VerticalComparator
 * @author alexis
 */
public class VerticalList {

	//The list of constraints attached to this object
	private List<Edge> constraintsList;
	//The comparator used by this VerticalList
	private VerticalComparator comp;

	/*****************************************************/
	/*The following attributes are used to prevent the   */
	/*library to perform many times the same comparisons */
	/*(getUpper and getLower)                            */
	/*They are here for efficiency reasons               */
	/*****************************************************/

	//The last point used for a getUpperEdge
	private Point lastUpperPt;
	//The last returned upper edge
	private Edge lastUpperEd;
	//The last point used for a getLowerEdge
	private Point lastLowerPt;
	//The last returned lower edge
	private Edge lastLowerEd;

	/**
	 * The default constructor. The inner comparator is instanciated with
	 * value 0.
	 */
	public VerticalList(){
		constraintsList = new ArrayList<Edge>();
		comp = new VerticalComparator((0));
		resetVolatileAttributes();
	}

	/**
	 * Public constructor. It uses the double abs to instanciate the comparator
	 * @param abs
	 */
	public VerticalList(double abs){
		constraintsList = new ArrayList<Edge>();
		comp=new VerticalComparator(abs);
		resetVolatileAttributes();
	}

	/**
	 * Public constructor. Uses the absciss of the point to instanciate the
	 * comparator.
	 * @param pt
	 */
	public VerticalList(Point pt){
		this(pt.getX());
	}

	/**
	 * get the absciss where the comparison is currently performed.
	 * @return
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
		if(Math.abs(abs-comp.getAbs())>Tools.EPSILON){
			comp.setAbs(abs);
			sort();
		}
	}

	/**
	 * Change the absciss where we want our edges to be sorted. For this task,
	 * we use the absciss of the point given in parameter.
	 * @param abs
	 */
	public final void setAbs(Point pt) throws DelaunayError{
		setAbs(pt.getX());
	}

	/**
	 * retrieve the element at position i in the list.
	 * @param i
	 * @return
	 */
	public final Edge get(int i){
		return constraintsList.get(i);
	}

	/**
	 * Add each of the edge in the list given in argument in this vertical
	 * list.
	 * @param constraints
	 */
	public final void addEdges(List<Edge> constraints){
		resetVolatileAttributes();
		for(Edge edge : constraints){
			Tools.addToSortedList(edge, constraintsList, comp);
		}
	}

	/**
	 * Add an edge to the list of constraints that are considered to be linked
	 * to the boundary of the current mesh.
	 * @param constraint
	 */
	public final int addEdge(Edge constraint){
		resetVolatileAttributes();
		if(constraintsList == null){
			constraintsList = new ArrayList<Edge>();
		}
		return Tools.addToSortedList(constraint, constraintsList, comp);
	}

	/**
	 * Remove an edge in this vertical list. Do nothing if the edge is not present.
	 * @param constr
	 */
	public final void removeEdge(Edge constr){
		resetVolatileAttributes();
		int index = Collections.binarySearch(constraintsList, constr, comp);
		if(index >= 0){
			constraintsList.remove(constr);
		}
	}

	/**
	 * Remove the edge at index index in this vertical list.
	 * @param index
	 * @return
	 */
	public final Edge remove(int index){
		resetVolatileAttributes();
		return constraintsList.remove(index);
	}

	/**
	 * Search an edge in the constraints linked to the boundary.
	 * @param edge
	 * @param abs
	 */
	protected final int searchEdge(Edge edge){
		return Tools.sortedListContains(constraintsList, edge, comp);
	}

	/**
	 * Get the list of constraints linked to the boundary of the current mesh.
	 * @return
	 */
	public final List<Edge> getVerticallySortedEdges(){
		return constraintsList;
	}

	/**
	 * get the last evaluated lower edge
	 * @return
	 */
	public final Edge getLastLowerEd() {
		return lastLowerEd;
	}

	/**
	 * get the last point evaluate to perform et getLowerPoint operation
	 * @return
	 */
	public final Point getLastLowerPt() {
		return lastLowerPt;
	}

	/**
	 * Get the last evaluated upper edge
	 * @return
	 */
	public final Edge getLastUpperEd() {
		return lastUpperEd;
	}

	/**
	 * Get the last evaluated upper point
	 * @return
	 */
	public final Point getLastUpperPt() {
		return lastUpperPt;
	}

	/**
	 * Sort the list to the x-coordinate of rightPt, and remove the edges of the
	 * list whose right point is equal to rightPt.
	 * @param rightPt
	 */
	public final void removeEdgeFromRightPoint(Point rightPt) throws DelaunayError {
		setAbs(rightPt);
		Edge ed = new Edge(rightPt, rightPt);
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
		Edge e1;
		Edge e2;
		while (i < s - 1) {
			e1 = constraintsList.get(i);
			e2 = constraintsList.get(i + 1);
			c = e1.verticalSort(e2, comp.getAbs());
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
         * Note that we don't use the vertical sort here : an Edge edge is said to be
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
        public final Edge getUpperEdge(Point point) throws DelaunayError{
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
                Edge search = new Edge(point, new Point(point.getX()+1, point.getY(), point.getZ()));
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
			lastUpperEd = constraintsList.get(index);
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
         * Note that we don't use the vertical sort here : an Edge edge is said to be
         * "lower" than point if and only if edge.getPointFromItsX(point.getX())<point.getY()
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
        public final Edge getLowerEdge(Point point) throws DelaunayError{
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
                Edge search = new Edge(point, new Point(point.getX()+1, point.getY(), point.getZ()));
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
		Edge edgeTmp;
		Point pointTmp;
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
	 * @throws DelaunayError
	 */
	public final boolean intersectsUpperOrLower(Point pRef, Edge ed) throws DelaunayError{
		setAbs(pRef);
		Edge upper = getUpperEdge(pRef);
		int inter;
		if(upper!=null){
			inter = upper.intersects(ed);
			if(inter == 1){
				return true;
			}
		}
		Edge lower = getLowerEdge(pRef);
		if(lower!=null){
			inter = lower.intersects(ed);
			if(inter == 1){
				return true;
			}
		}
		return false;
	}
}
