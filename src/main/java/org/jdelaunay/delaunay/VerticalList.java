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

	/**
	 * Public constructor. It uses the double abs to instanciate the comparator
	 * @param abs
	 */
	public VerticalList(double abs){
		constraintsList = new ArrayList<Edge>();
		comp=new VerticalComparator(abs);
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
	public double getAbs(){
		return comp.getAbs();
	}

	/**
	 * Change the absciss where we want our edges to be sorted
	 * @param abs
	 */
	public void setAbs(double abs) throws DelaunayError{
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
	public void setAbs(Point pt) throws DelaunayError{
		setAbs(pt.getX());
	}

	/**
	 * retrieve the element at position i in the list.
	 * @param i
	 * @return
	 */
	public Edge get(int i){
		return constraintsList.get(i);
	}


	/**
	 * Add an edge to the list of constraints that are considered to be linked
	 * to the boundary of the current mesh.
	 * @param constraint
	 */
	public int addEdge(Edge constraint){
		if(constraintsList == null){
			constraintsList = new ArrayList<Edge>();
		}
		return Tools.addToSortedList(constraint, constraintsList, comp);
	}

	/**
	 * Remove an edge in this vertical list. Do nothing if the edge is not present.
	 * @param constr
	 */
	public void removeEdge(Edge constr){
		int index = Collections.binarySearch(constraintsList, constr, comp);
		if(index >= 0){
			constraintsList.remove(constr);
		}
	}

	public Edge remove(int index){
		return constraintsList.remove(index);
	}

	/**
	 * Search an edge in the constraints linked to the boundary.
	 * @param edge
	 * @param abs
	 */
	protected int searchEdge(Edge edge){
		return Tools.sortedListContains(constraintsList, edge, comp);
	}

	/**
	 * Get the list of constraints linked to the boundary of the current mesh.
	 * @return
	 */
	public List<Edge> getVerticallySortedEdges(){
		return constraintsList;
	}
	
	/**
	 * This method will sort the list using the abs of the current comparator.
	 * It's a bubble sort, not a merge sort, as it will be more efficient in 
	 * most cases when using sweep line.
	 */
	protected void sort() throws DelaunayError{
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
	public int size(){
		return this.constraintsList.size();
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
         * @param point
         * @return The edge of which the ordinate is directly greater that the one
         * of point. Null if such an edge does not exist.
         */
        public Edge getUpperEdge(Point point) throws DelaunayError{
                if(constraintsList == null || constraintsList.isEmpty()){
                        return null;
                }
                int size = constraintsList.size();
                double abs = point.getX();
                if(Tools.EPSILON < Math.abs(abs-getAbs())){
                        //We must change the x-coordinate where we are working
                        setAbs(abs);
                }
                Edge search = new Edge(point, new Point(point.getX()+1, point.getY(), point.getZ()));
                int index = Collections.binarySearch(constraintsList, search, comp);
                index = (index < 0 ? -index -1 : index);
                //We've checked that the list is neither null nor empty. We can perform
                //the operation in the loop at least once.
                double edgeOrd;
                if(index == size){
                        return null;
                }
                edgeOrd = constraintsList.get(index).getPointFromItsX(abs).getY();
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
                        return constraintsList.get(index);
                } else {
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
         * @param point
         * @return The edge of which the ordinate is directly greater that the one
         * of point. Null if such an edge does not exist.
         */
        public Edge getLowerEdge(Point point) throws DelaunayError{
                if(constraintsList == null || constraintsList.isEmpty()){
                        return null;
                }
                int size = constraintsList.size();
                double abs = point.getX();
                if(Tools.EPSILON < Math.abs(abs-getAbs())){
                        //We must change the x-coordinate where we are working
                        setAbs(abs);
                }
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
                if(index < 0){
                        return null;
                }
                do{
                        edgeOrd = constraintsList.get(index).getPointFromItsX(abs).getY();
                        index --;
                } while(Math.abs(edgeOrd - point.getY())<Tools.EPSILON && index >=0);
                //We've gone one place too far
                index ++;
                edgeOrd = constraintsList.get(index).getPointFromItsX(abs).getY();
                //We must check that the last edge is really lower than point, ie that
                //point and constraintsEdge.get(size -1) are not colinear.
                //If they are, we return null
                if(index >=0 && Math.abs(edgeOrd - point.getY())>=Tools.EPSILON){
                        return constraintsList.get(index);
                } else {
                        return null;
                }
        }
}
