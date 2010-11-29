package org.jdelaunay.delaunay;

import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.StringTokenizer;

/**
 *
 * @author alexis
 */
public class ConstrainedMesh {

	//The list of triangles contained in the mesh
	private ArrayList<MyTriangle> triangleList;
	//The lis of constraints used during the triangulation
	private ArrayList<MyEdge> constraintEdges;
	//The list of points used during the triangulation
	private ArrayList<MyPoint> points;
	//
	private double precision;
	//The minimum distance between two distinct points
	private double tolerance;
	//The two following lists are used only during computation.
	//The bad edge queue list contains all the edges that coud be changed
	//during a flip-flap operation
	private LinkedList<MyEdge> badEdgesQueueList;
	//boundaryEdges contains the Envelope of the CURRENT geometry.
	private LinkedList<MyEdge> boundaryEdges;
	// constants
	public static final double epsilon = 0.00001;
	public static final int maxIter = 5;
	public static final int refinement_maxArea = 1;
	public static final int refinement_minAngle = 2;
	public static final int refinement_softInterpolate = 4;
	public static final int refinement_obtuseAngle = 8;

	public ConstrainedMesh() {
		triangleList = new ArrayList<MyTriangle>();
		constraintEdges = new ArrayList<MyEdge>();
		points = new ArrayList<MyPoint>();

		precision = 0;
		tolerance = 0.00001;

		badEdgesQueueList = new LinkedList<MyEdge>();
		boundaryEdges = new LinkedList<MyEdge>();
	}

	/**
	 * Get the list of edges that are to be processed by the flip flap algorithm
	 * @return
	 */
	public LinkedList<MyEdge> getBadEdgesQueueList() {
		return badEdgesQueueList;
	}

	/**
	 * Set the list of edges that are to be processed by the flip flap algorithm
	 * @param badEdgesQueueList
	 */
	public void setBadEdgesQueueList(LinkedList<MyEdge> badEdgesQueueList) {
		this.badEdgesQueueList = badEdgesQueueList;
	}

	/**
	 * Get the list of edges that form the current convex hull of the triangulation
	 * @return
	 */
	public LinkedList<MyEdge> getBoundaryEdges() {
		return boundaryEdges;
	}

	/**
	 * Set the list of edges that form the current convex hull of the triangulation
	 * @param boundaryEdges
	 */
	public void setBoundaryEdges(LinkedList<MyEdge> boundaryEdges) {
		this.boundaryEdges = boundaryEdges;
	}

	/**
	 * Get the list of edges that are used as constraints during triangulation
	 * @return
	 */
	public ArrayList<MyEdge> getConstraintEdges() {
		return constraintEdges;
	}

	/**
	 * Set the list of edges that are used as constraints during triangulation
	 * As we can't be sure the constraintEdges is already sorted, we sort it first
	 * and add all the corresponding points to the point list.
	 * @param constraintEdges
	 */
	public void setConstraintEdges(ArrayList<MyEdge> constraint) {
		this.constraintEdges = new ArrayList<MyEdge>();
		for (MyEdge e : constraint) {
			addPoint(e.getStart());
			addPoint(e.getEnd());
			addConstraintEdge(e);
		}
	}

	public void addConstraintEdge(MyEdge e) {
		if (constraintEdges == null) {
			constraintEdges = new ArrayList<MyEdge>();
		}
		addEdgeToLeftSortedList(constraintEdges, e);
	}

	/**
	 * This method will sort the edges using the coordinates of the left point
	 * of the edges.
	 * @return
	 */
	public ArrayList<MyEdge> sortEdgesLeft(ArrayList<MyEdge> inputList) {
		ArrayList<MyEdge> outputList = new ArrayList<MyEdge>();
		for (MyEdge e : inputList) {
			addEdgeToLeftSortedList(outputList, e);
		}
		return outputList;
	}

	/**
	 * This method will insert an edge in an already sorted list, as described
	 * in sortEdgesLeft.
	 * The sorted list is not checked here, so be careful when using this method !
	 * If an edge is already present in the list, it is not added.
	 *
	 * if two edges have the same left point, they are sorted using the other one.
	 * @param sorted
	 * @param edge
	 */
	private void addEdgeToLeftSortedList(ArrayList<MyEdge> sorted, MyEdge edge) {
		if (sorted.isEmpty()) {
			sorted.add(edge);
			return;
		}
		MyEdge temp = sorted.get(0);
		MyPoint left = edge.getPointLeft();
		MyPoint right;
		int s = sorted.size();
		if (left.compareTo2D(temp.getPointLeft()) == -1 || left.compareTo2D(temp.getPointLeft()) == 0) {
			//left is on the left of the first edge in the list, we put it there
			sorted.add(0, edge);
			return;
		}
		temp = sorted.get(s - 1);
		if (temp.getPointLeft().compareTo2D(left) == -1 || temp.getPointLeft().compareTo2D(left) == 0) {
			//left is on the right of the leftmost edge of the last element.
			sorted.add(edge);
			return;
		}
		int c;
		int i = s / 2;
		int delta = s / 2;
		boolean next = true;
		MyEdge other;
		while (next) {
			other = sorted.get(i);
			c = edge.sortLeftRight(other);
			switch(c){
				case -1:
					other = sorted.get(i-1);
					c = edge.sortLeftRight(other);
					switch(c){
						case -1:
							delta = (delta / 2 >0 ? delta/2 : 1);
							i= i - delta;
							break;
						case 0:
							return;
						case 1:
							sorted.add(i,edge);
							return;
					}
					break;
				case 0:
					return;
				case 1:
					other = sorted.get(i-1);
					c = edge.sortLeftRight(other);
					switch(c){
						case -1:
							sorted.add(i+1, edge);
							return;
						case 0:
							return;
						default:
							delta = (delta / 2 >0 ? delta/2 : 1);
							i=i+delta;
							break;
					}
			}
		}
	}

	/**
	 * Get the precision
	 * @return
	 */
	public double getPrecision() {
		return precision;
	}

	/**
	 * Set the precision
	 * @param precision
	 */
	public void setPrecision(double precision) {
		this.precision = precision;
	}

	/**
	 * Get the value used to compute the minimum distance between two points
	 * @return
	 */
	public double getTolerance() {
		return tolerance;
	}

	/**
	 * Set the value used to compute the minimum distance between two points
	 * @param tolerance
	 */
	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}

	/**
	 * Get the list of triangles already computed and added in this mesh
	 * @return
	 */
	public ArrayList<MyTriangle> getTriangleList() {
		return triangleList;
	}

	/**y
	 * Set the list of triangles already computed in this mesh.
	 * @param triangleList
	 */
	public void setTriangleList(ArrayList<MyTriangle> triangleList) {
		this.triangleList = triangleList;
	}

	/**
	 * Get the points contained in this mesh
	 * @return
	 */
	public ArrayList<MyPoint> getPoints() {
		return points;
	}

	/**
	 * Set the list of points to be used during the triangulation
	 * @param points
	 */
	public void setPoints(ArrayList<MyPoint> points) {
		this.points = points;
	}

	public void addPoint(MyPoint point) {
		int s = points.size();
		if (s == 0) {
			points.add(point);
		} else {
			int p = s / 2;
			int delta = s / 2;
			int ret = -1;
			int c;
			//If the point is inferior to the first element of te list, we place it at the beginning
			if (point.compareTo2D(points.get(0)) == -1) {
				p = 0;
				ret = 0;
				//If the point is superior to the last element of the list, we place it at the end
			} else if (point.compareTo2D(points.get(s - 1)) == 1) {
				p = s;
				ret = s;
			}
			while (ret != p) {
				delta = (delta / 2 > 0 ? delta / 2 : 1);
				c = point.compareTo2D(points.get(p));
				if (c == -1) {
					//point < points.get(p)
					//We must move left
					c = point.compareTo2D(points.get(p - 1));
					if (c == -1) {
						p = p - delta;
					} else if (c == 0) {
						p = -1;
					} else {
						ret = p;
					}
				} else if (c == 0) {
					p = -1;
				} else {
					p = p + delta;
				}
			}
			if (p != -1) {
				points.add(p, point);
			}
		}
	}

	/**
	 * This methods will search the point p in the list.
	 * @param p
	 * @return the index of p, -1 if it's not in the list
	 */
	public int listContainsPoint(MyPoint p) {
		int s = points.size();
		int c = p.compareTo2D(points.get(0));
		if (c == -1) {	//p<first, p is not in the sorted list
			return -1;
		} else if (c == 0) {
			return 0;
		}
		c = p.compareTo2D(points.get(s - 1));
		if (c == 1) {	//p>last, p is not in the sorted list
			return -1;
		} else if (c == 0) {
			return points.size() - 1;
		}
		//p is ppotentially in the list, and is not one of the extremities.
		int delta = points.size() / 2;
		int i = points.size() / 2;
		while (delta > 0) {
			c = p.compareTo2D(points.get(i));
			if (c == -1) {
				//p is on the left of points(i)...
				if (i == 0) {
					return -1;
				}
				c = p.compareTo2D(points.get(i - 1));
				if (c == 1) {//...and on the right of points(i-1), so it's no in the list
					return -1;
				} else if (c == 0) {
					return i - 1;
				} else {//...and on the left of points(i-1), we continue
					delta = delta / 2;
					i = i - delta;
				}
			} else if (c == 0) {
				return i;
			} else {
				//p is on the right of points(i)
				if (i == points.size() - 1) {
					return -1;
				}
				c = p.compareTo2D(points.get(i + 1));
				if (c == -1) {//...and on the left of points(i-1), so it's no in the list
					return -1;
				} else if (c == 0) {
					return i + 1;
				} else {//...and on the right of points(i-1), we continue
					delta = delta / 2;
					i = i + delta;
				}
			}
		}
		return -1;
	}

//	public boolean listContainsPoint(MyPoint point){
//		int p = points.size()/2;
//		int q=-1;
//		while(q!=p){
//			q=p;
//		}
//	}
	public void forceConstraintIntegrity() {
	}

	/**
	 * This method will sort the edges contained in the ArrayList list by considering
	 * their intersection point with the line of equation x=a, where a is given
	 * in parameter.
	 * @param list
	 * @param x
	 */
	private void sortEdgesVertically(ArrayList list, double a) {
	}
}
