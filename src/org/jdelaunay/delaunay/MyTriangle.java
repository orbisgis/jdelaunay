package org.jdelaunay.delaunay;

/**
 * Delaunay Package.
 * 
 * @author Jean-Yves MARTIN
 * @date 2009-01-12
 * @version 1.0
 */

import java.awt.*;
import java.util.*;

public class MyTriangle {
	protected MyPoint[] points;
	protected MyEdge[] edges;

	private MyPoint center;
	private double radius;
	
	private static final double epsilon = 0.00001;
	private static final double epsilon2 = epsilon * epsilon;

	/**
	 * Initialize data structure
	 * This method is called by every constructor
	 */
	private void init() {
		points = new MyPoint[3];
		edges = new MyEdge[3];
		center = new MyPoint();
		radius = -1;
	}

	/**
	 * Create a new triangle with points and edges
	 * 
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param e1
	 * @param e2
	 * @param e3
	 */
	public MyTriangle() {
		init();
	}


	/**
	 * Create a new triangle with edges Add the points from the edges
	 * 
	 * @param e1
	 * @param e2
	 * @param e3
	 */
	public MyTriangle(MyEdge e1, MyEdge e2, MyEdge e3) {
		init();

		edges[0] = e1;
		edges[1] = e2;
		edges[2] = e3;

		points[0] = e1.start();
		points[1] = e1.end();
		if (e2.start() == points[1])
			points[2] = e2.end();
		else
			points[2] = e2.start();

		reconnectEdges();
		recomputeCenter();
		radius = center.squareDistance_2D(points[0]);
	}

	/**
	 * Create a new triangle with points and edges
	 * 
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param e1
	 * @param e2
	 * @param e3
	 */
	public MyTriangle(MyPoint p1, MyPoint p2, MyPoint p3, MyEdge e1, MyEdge e2,
			MyEdge e3) {
		init();

		points[0] = p1;
		points[1] = p2;
		points[2] = p3;

		edges[0] = e1;
		edges[1] = e2;
		edges[2] = e3;

		reconnectEdges();
		recomputeCenter();
		radius = center.squareDistance_2D(p1);
	}

	/**
	 * Create a Triangle from another triangle NB : it doesn't update edges
	 * connection
	 * 
	 * @param aTriangle
	 */
	public MyTriangle(MyTriangle aTriangle) {
		init();

		for (int i = 0; i < 3; i++) {
			points[i] = aTriangle.points[i];
			edges[i] = aTriangle.edges[i];
		}

		recomputeCenter();
		radius = center.squareDistance_2D(points[0]);
	}

	/**
	 * Get the ith points
	 * 
	 * @param i
	 * @return
	 */
	public MyPoint point(int i) {
		return points[i];
	}

	/**
	 * Get the center of the circle that joins the three points
	 * 
	 * @return
	 */
	public MyPoint center() {
		return center;
	}

	/**
	 * Get the radius of the circle
	 * 
	 * @return
	 */
	public double radius() {
		return Math.sqrt(radius);
	}

	/**
	 * Get the ith edge
	 * 
	 * @param i
	 * @return
	 */
	public MyEdge edge(int i) {
		return edges[i];
	}

	/**
	 * Recompute the center of the circle that joins the points
	 */
	public void recomputeCenter() {
		MyPoint p1 = points[0];
		MyPoint p2 = points[1];
		MyPoint p3 = points[2];

		double p1Sq = p1.xy[0] * p1.xy[0] + p1.xy[1] * p1.xy[1];
		double p2Sq = p2.xy[0] * p2.xy[0] + p2.xy[1] * p2.xy[1];
		double p3Sq = p3.xy[0] * p3.xy[0] + p3.xy[1] * p3.xy[1];

		double ux = p2.xy[0] - p1.xy[0];
		double uy = p2.xy[1] - p1.xy[1];
		double vx = p3.xy[0] - p1.xy[0];
		double vy = p3.xy[1] - p1.xy[1];

		double cp = ux * vy - uy * vx;
		double cx, cy, cz;
		cx = cy = cz = 0.0;

		if (cp != 0) {
			cx = (p1Sq * (p2.xy[1] - p3.xy[1]) + p2Sq * (p3.xy[1] - p1.xy[1]) + p3Sq
					* (p1.xy[1] - p2.xy[1]))
					/ (2 * cp);
			cy = (p1Sq * (p3.xy[0] - p2.xy[0]) + p2Sq * (p1.xy[0] - p3.xy[0]) + p3Sq
					* (p2.xy[0] - p1.xy[0]))
					/ (2 * cp);
			cz = 0.0;

			center.xy[0] = cx;
			center.xy[1] = cy;
			center.xy[2] = cz;

			radius = center.squareDistance_2D(points[0]);
		} else {
			center.xy[0] = 0.0;
			center.xy[1] = 0.0;
			center.xy[2] = 0.0;

			radius = -1;
		}

	}

	/**
	 * Reconnect triangle edges to rebuild topology
	 */
	public void reconnectEdges() {
		for (int j = 0; j < 3; j++) {
			MyPoint start = edges[j].point[0];
			MyPoint end = edges[j].point[1];
			for (int k = 0; k < 3; k++) {
				if ((start != points[k]) && (end != points[k]))
					if (edges[j].isLeft(points[k]))
						edges[j].left = this;
					else
						edges[j].right = this;
			}
		}
	}

	/**
	 * Check if the point is in or on the Circle 0 = outside 1 = inside 2 = on
	 * the circle
	 * 
	 * @param aPoint
	 * @return position
	 * 0 = outside
	 * 1 = iside
	 * 2 = on the circle
	 */
	public int inCircle(MyPoint aPoint) {
		// default is outside the circle
		int returnedValue = 0;

		// double distance = squareDistance(Center, aPoint);
		double ux = aPoint.xy[0] - center.xy[0];
		double uy = aPoint.xy[1] - center.xy[1];
		double distance = ux * ux + uy * uy;
		if (distance < radius - epsilon2)
			// in the circle
			returnedValue = 1;
		else if (distance < radius + epsilon2)
			// on the circle
			returnedValue = 2;

		return returnedValue;
	}

	/**
	 * Check if the point is inside the triangle / not
	 * 
	 * @param aPoint
	 * @return isInside
	 */
	public boolean isInside(MyPoint aPoint) {
		boolean isInside = true;
		
		int k = 0;
		while ((k < 3) && (isInside)) {
			MyEdge theEdge = edges[k];
			if (theEdge.left == this) {
				if (theEdge.isRight(aPoint))
					isInside = false;
			}
			else {
				if (theEdge.isLeft(aPoint))
					isInside = false;
			}
			k++;
		}

		return isInside;
	}

	/**
	 * compute triangle area
	 * 
	 * @return area
	 */
	public double computeArea() {
		MyPoint p1 = points[0];
		MyPoint p2 = points[1];
		MyPoint p3 = points[2];
		
		double ux = p2.xy[0] - p1.xy[0];
		double uy = p2.xy[1] - p1.xy[1];
		double vx = p3.xy[0] - p1.xy[0];
		double vy = p3.xy[1] - p1.xy[1];
		double wx = p3.xy[0] - p2.xy[0];
		double wy = p3.xy[1] - p3.xy[1];
		
		double a = Math.sqrt(ux*ux+uy*uy);
		double b = Math.sqrt(vx*vx+vy*vy);
		double c = Math.sqrt(wx*wx+wy*wy);
		
		double area = Math.sqrt((a+b+c)*(b+c-a)*(c+a-b)*(a+b-c));
		
		return area;
	}

	/**
	 * check if one of the triangle's angle is less than minimum
	 * 
	 * @return minAngle
	 */
	protected int badAngle(double tolarance) {
		double minAngle = 400;
		int returndeValue = -1;
		for (int k = 0; k < 3; k++) {
			int k1 = (k + 1) % 3;
			int k2 = (k1 + 1) % 3;

			MyPoint p1 = points[k];
			MyPoint p2 = points[k1];
			MyPoint p3 = points[k2];

			double ux = p2.xy[0] - p1.xy[0];
			double uy = p2.xy[1] - p1.xy[1];
			double vx = p3.xy[0] - p1.xy[0];
			double vy = p3.xy[1] - p1.xy[1];

			double dp = ux * vx + uy * vy;

			double angle = Math.acos(Math.sqrt(((dp * dp))
					/ ((ux * ux + uy * uy) * (vx * vx + vy * vy))))
					* (180d / Math.PI);
			if (angle < minAngle) {
				minAngle = angle;
				if (minAngle < tolarance)
					returndeValue = k;
			}
		}
		return returndeValue;
	}

	/**
	 * Check if triangle topology is correct / not
	 * 
	 * @return correct
	 */
	public boolean checkTopology() {
		boolean correct = true;
		int j = 0;
		while ((j < 3) && (correct)) {
			MyEdge aEdge = edges[j];
			int foundPoint = 0;
			for (int k = 0; k < 3; k++) {
				if (aEdge.start() == points[k])
					foundPoint++;
				else if (aEdge.end() == points[k])
					foundPoint++;
			}
			if (foundPoint != 2)
				correct = false;

			if (correct) {
				MyPoint start = edges[j].start();
				MyPoint end = edges[j].end();
				boolean found = false;
				int k = 0;
				while ((k < 3) && (correct) && (!found)) {
					if ((start != points[k]) && (end != points[k])) {
						if (edges[j].isLeft(points[k])) {
							if (edges[j].left != this)
								correct = false;
							if (edges[j].right == this)
								correct = false;
						} else {
							if (edges[j].right != this)
								correct = false;
							if (edges[j].left == this)
								correct = false;
						}
						found = true;
					}
					k++;
				}
			}
			j++;
		}
		return correct;
	}

	/**
	 * Check if the triangles respects Delaunay constraints.
	 * the parameter thePoints is the list of points of the mesh
	 * 
	 * @param thePoints
	 * @return
	 */
	public boolean checkDelaunay(ArrayList<MyPoint> thePoints) {
		boolean correct = true;
		ListIterator<MyPoint> iterPoint = thePoints.listIterator();
		while ((iterPoint.hasNext()) && (correct)) {
			MyPoint aPoint = iterPoint.next();
			if ((aPoint != points[0]) && (aPoint != points[1])
					&& (aPoint != points[2])) {
				if (inCircle(aPoint) == 1)
					correct = false;
			}
		}

		return correct;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String myString = new String("Triangle : \n");
		for (int i = 1; i < 3; i++)
			myString += points[i] + "\n";
		return myString;
	}

	/**
	 * Set the edge color
	 * Must be used only when using package drawing
	 * 
	 * @param g
	 */
	public void setColor(Graphics g) {
		g.setColor(Color.cyan);
	}

	/**
	 * Display the triangle in a JPanel
	 * Must be used only when using package drawing
	 * 
	 * @param g
	 * @param decalageX
	 * @param decalageY
	 */
	public void displayObject(Graphics g, int decalageX, int decalageY) {
		int[] xPoints, yPoints;
		xPoints = new int[3];
		yPoints = new int[3];

		xPoints[0] = (int) points[0].xy[0] + decalageX;
		xPoints[1] = (int) points[1].xy[0] + decalageX;
		xPoints[2] = (int) points[2].xy[0] + decalageX;

		yPoints[0] = decalageY - (int) points[0].xy[1];
		yPoints[1] = decalageY - (int) points[1].xy[1];
		yPoints[2] = decalageY - (int) points[2].xy[1];

		g.fillPolygon(xPoints, yPoints, 3);

		for (int i = 0; i < 3; i++) {
			edges[i].setColor(g);
			edges[i].displayObject(g, decalageX, decalageY);
		}
	}

	/**
	 * Display the triangle in a JPanel
	 * Must be used only when using package drawing
	 * 
	 * @param g
	 * @param decalageX
	 * @param decalageY
	 */
	public void displayObjectCircles(Graphics g, int decalageX, int decalageY) {
		double r = Math.sqrt(radius);
		g.setColor(Color.red);
		g.drawOval((int) (center.xy[0]) + decalageX, decalageY
				- (int) (center.xy[1]), 1, 1);
		g.drawOval((int) (center.xy[0] - r) + decalageX, decalageY
				- (int) (center.xy[1] + r), (int) r * 2, (int) r * 2);
	}
}
