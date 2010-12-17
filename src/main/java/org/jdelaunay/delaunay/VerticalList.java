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
}
