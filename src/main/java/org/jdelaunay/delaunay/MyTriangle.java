package org.jdelaunay.delaunay;

/**
 * Delaunay Package.
 *
 * @author Jean-Yves MARTIN, Erwan BOCHER
 * @date 2009-01-12
 * @revision 2010-05-16
 * @version 2.0
 */

import java.awt.*;
import java.util.*;

import com.vividsolutions.jts.geom.Coordinate;

public class MyTriangle extends MyElement {
	protected MyEdge[] edges;

	private double x_center, y_center;
	private double radius;

	/**
	 * Initialize data structure This method is called by every constructor
	 */
	private void init() {
		this.edges = new MyEdge[3];
		this.x_center = 0;
		this.y_center = 0;
		this.radius = -1;
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
		super();
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
		super();
		init();

		edges[0] = e1;
		edges[1] = e2;
		edges[2] = e3;
		
		connectEdges();
		recomputeCenter();
		radius = e1.getStartPoint().squareDistance_2D(x_center, y_center);
	}

	/**
	 * Create a Triangle from another triangle NB : it doesn't update edges
	 * connection
	 *
	 * @param aTriangle
	 */
	public MyTriangle(MyTriangle aTriangle) {
		super((MyElement)aTriangle);
		init();

		for (int i = 0; i < 3; i++) {
			edges[i] = aTriangle.edges[i];
		}

		x_center = aTriangle.x_center;
		y_center = aTriangle.y_center;
		radius = aTriangle.radius;
	}


	/**
	 * Get the ith point
	 * i must be equal to 0, 1 or 2.
	 *
	 * @param i
	 * @return aPoint
	 */
	public MyPoint getPoint(int i) {
		MyPoint p;
		if (i==0)
			p = edges[0].getStartPoint();
		else if (i==1)
			p = edges[0].getEndPoint();
		else {
			p = edges[1].getStartPoint();
			if ((p==edges[0].getStartPoint()) || (p==edges[0].getEndPoint()))
				p = edges[1].getEndPoint();
		}
		return p;
	}

	/**
	 * Get the ith edge
	 * i must be equal to 0, 1 or 2.
	 *
	 * @param i
	 * @return anEdge
	 */
	public MyEdge getEdge(int i) {
		if ((0<=i) && (i<=2))
			return edges[i];
		else
			return null;
	}

	/**
	 * Set the ith edge
	 *
	 * @param i
	 * @param anEdge
	 */
	public void setEdge(int i, MyEdge anEdge) {
		if ((0<=i) && (i<=2))
			edges[i] = anEdge;
	}

	/**
	 * Get the radius of the CircumCircle
	 *
	 * @return radius
	 */
	public double getRadius() {
		return Math.sqrt(radius);
	}

	/**
	 * Get the center of the CircumCircle
	 *
	 * @return
	 */
	public Coordinate getCircumCenter() {
		return new Coordinate(this.x_center, this.y_center, 0.0);
	}

	/* (non-Javadoc)
	 * @see org.jdelaunay.delaunay.MyElement#getBoundingBox()
	 */
	public MyBox getBoundingBox() {
		MyBox aBox = new MyBox();

		MyPoint p1,p2,p3;
		p1 = edges[0].getStartPoint();
		p2 = edges[0].getEndPoint();
		p3 = edges[1].getStartPoint();
		if ((p3==p1)||(p3==p2))
			p3 = edges[1].getEndPoint();
		aBox.alterBox( p1);
		aBox.alterBox( p2);
		aBox.alterBox( p3);
		
		return aBox;
	}

	/* (non-Javadoc)
	 * @see org.jdelaunay.delaunay.MyElement#contains(org.jdelaunay.delaunay.MyPoint)
	 */
	public boolean contains(MyPoint aPoint) {
		return isInside(aPoint);
	}

	/**
	 * Recompute the center of the circle that joins the 3 points : the CircumCenter
	 */
	protected void recomputeCenter() {
		MyPoint p1,p2,p3;
		p1 = edges[0].getStartPoint();
		p2 = edges[0].getEndPoint();
		p3 = edges[1].getStartPoint();
		if ((p3==p1)||(p3==p2))
			p3 = edges[1].getEndPoint();

		double p1Sq = p1.getX() * p1.getX() + p1.getY() * p1.getY();
		double p2Sq = p2.getX() * p2.getX() + p2.getY() * p2.getY();
		double p3Sq = p3.getX() * p3.getX() + p3.getY() * p3.getY();

		double ux = p2.getX() - p1.getX();
		double uy = p2.getY() - p1.getY();
		double vx = p3.getX() - p1.getX();
		double vy = p3.getY() - p1.getY();

		double cp = ux * vy - uy * vx;
		double cx, cy;
		cx = cy = 0.0;

		if (cp != 0) {
			cx = (p1Sq * (p2.getY() - p3.getY()) + p2Sq * (p3.getY() - p1.getY()) + p3Sq
					* (p1.getY() - p2.getY()))
					/ (2.0 * cp);
			cy = (p1Sq * (p3.getX() - p2.getX()) + p2Sq * (p1.getX() - p3.getX()) + p3Sq
					* (p2.getX() - p1.getX()))
					/ (2.0 * cp);

			x_center = cx;
			y_center = cy;

			radius = p1.squareDistance_2D(x_center, y_center);
		} else {
			x_center = 0.0;
			y_center = 0.0;
			radius = -1;
		}

	}

	/**
	 * Connect triangle edges to build topology
	 */
	private void connectEdges() {
		// we connect edges to the triangle
		for (int i=0; i<3; i++) {
			// Start point should be start
			MyPoint aPoint = this.getAlterPoint(edges[i]);
			if (edges[i].isLeft(aPoint)) {
				if (edges[i].left == null)
					edges[i].left = this;
				else {
					edges[i].right = this;
				}
			}
			else {
				if (edges[i].right == null)
					edges[i].right = this;
				else {
					edges[i].left = this;
				}
			}
		}
	}

	/**
	 * Check if the point is in or on the Circle 0 = outside 1 = inside 2 = on
	 * the circle
	 *
	 * @param aPoint
	 * @return position 0 = outside 1 = inside 2 = on the circle
	 */
	public int inCircle(MyPoint aPoint) {
		// default is outside the circle
		int returnedValue = 0;

		// double distance = squareDistance(Center, aPoint);
		double ux = aPoint.getX() - x_center;
		double uy = aPoint.getY() - y_center;
		double distance = ux * ux + uy * uy;
		if (distance < radius - MyTools.epsilon2)
			// in the circle
			returnedValue = 1;
		else if (distance < radius + MyTools.epsilon2)
			// on the circle
			returnedValue = 2;

		return returnedValue;
	}

	/**
	 * Check if the point is inside the triangle
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
			} else {
				if (theEdge.isLeft(aPoint))
					isInside = false;
			}
			k++;
		}

		return isInside;
	}

	/**
	 * Get Z value of a specific point in the triangle
	 *
	 * @param aPoint
	 * @return ZValue
	 */
	public double interpolateZ(MyPoint aPoint) {
		double ZValue = 0;

		MyPoint p1,p2,p3;
		p1 = edges[0].getStartPoint();
		p2 = edges[0].getEndPoint();
		p3 = edges[1].getStartPoint();
		if ((p3==p1)||(p3==p2))
			p3 = edges[1].getEndPoint();

		double ux = p2.getX() - p1.getX();
		double uy = p2.getY() - p1.getY();
		double uz = p2.getZ() - p1.getZ();
		double vx = p3.getX() - p1.getX();
		double vy = p3.getY() - p1.getY();
		double vz = p3.getZ() - p1.getZ();

		double a = uy * vz - uz * vy;
		double b = uz * vx - ux * vz;
		double c = ux * vy - uy * vx;
		double d = -a * p1.getX() - b * p1.getY() - c * p1.getZ();

		if (Math.abs(c) > MyTools.epsilon) {
			// Non vertical triangle
			ZValue = (-a * aPoint.getX() - b * aPoint.getY() - d) / c;
		}

		return ZValue;
	}

	/**
	 * Get Z value of a specific point in the triangle
	 * Take into account triangles connected to the edge
	 *
	 * @param aPoint
	 * @return ZValue
	 */
	public double softInterpolateZ(MyPoint aPoint) {
		double weight = 3.0;
		double ZValue = interpolateZ(aPoint) * weight;
		
		// Process connected edges
		for (int i=0; i<3; i++) {
			MyEdge anEdge = edges[i];
			MyTriangle aTriangle = null;
			if (anEdge != null)
				if (anEdge.left == this)
					aTriangle = anEdge.right;
				else
					aTriangle = anEdge.left;
			if (aTriangle != null) {
				weight += 1.0;
				ZValue += aTriangle.interpolateZ(aPoint);
			}
		}
		// Define new Z value
		ZValue /= weight;

		return ZValue;
	}

	/**
	 * Compute triangle area
	 *
	 * @return area
	 */
	public double computeArea() {
		MyPoint p1,p2,p3;
		p1 = edges[0].getStartPoint();
		p2 = edges[0].getEndPoint();
		p3 = edges[1].getStartPoint();
		if ((p3==p1)||(p3==p2))
			p3 = edges[1].getEndPoint();

		double ux = p2.getX() - p1.getX();
		double uy = p2.getY() - p1.getY();
		double vx = p3.getX() - p1.getX();
		double vy = p3.getY() - p1.getY();
		double wx = p3.getX() - p2.getX();
		double wy = p3.getY() - p2.getY();

		double a = Math.sqrt(ux * ux + uy * uy);
		double b = Math.sqrt(vx * vx + vy * vy);
		double c = Math.sqrt(wx * wx + wy * wy);

		double area = Math.sqrt((a + b + c) * (b + c - a) * (c + a - b)
				* (a + b - c));

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

			MyPoint p1 = this.getPoint(k);
			MyPoint p2 = this.getPoint(k1);
			MyPoint p3 = this.getPoint(k2);

			double ux = p2.getX() - p1.getX();
			double uy = p2.getY() - p1.getY();
			double vx = p3.getX() - p1.getX();
			double vy = p3.getY() - p1.getY();

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
		int i, j, k;

		// check if we do not have an edge twice
		i = 0;
		while ((i < 3) && (correct)) {
			int foundEdge = 0;
			for (j = 0; j < 3; j++) {
				if (edges[i] == edges[j])
					foundEdge++;
			}
			if (foundEdge != 1)
				correct = false;
			i++;
		}

		// check if each edge is connected to the triangle
		i = 0;
		while ((i < 3) && (correct)) {
			int foundEdge = 0;
			if (edges[i].left == this)
				foundEdge++;
			if (edges[i].right == this)
				foundEdge++;

			if (foundEdge != 1)
				correct = false;
			i++;
		}

		// Check if each point in the edges is referenced 2 times
		MyPoint aPoint;
		i = 0;
		while ((i < 3) && (correct)) {
			MyEdge anEdge = edges[i];
			for (j = 0; j < 2; j++) {
				if (j == 0)
					aPoint = anEdge.getStartPoint();
				else
					aPoint = anEdge.getEndPoint();
				int foundPoint = 0;
				for (k = 0; k < 3; k++) {
					if (edges[k].getStartPoint() == aPoint)
						foundPoint++;
					if (edges[k].getEndPoint() == aPoint)
						foundPoint++;
				}
				if (foundPoint != 2)
					correct = false;
			}
			i++;
		}

		return correct;
	}

	/**
	 * Check if the triangles respects Delaunay's constraints. the parameter
	 * thePoints is the list of points of the mesh
	 *
	 * @param thePoints
	 * @return
	 */
	public boolean checkDelaunay(ArrayList<MyPoint> thePoints) {
		boolean correct = true;
		ListIterator<MyPoint> iterPoint = thePoints.listIterator();
		while ((iterPoint.hasNext()) && (correct)) {
			MyPoint aPoint = iterPoint.next();
			if (inCircle(aPoint) == 1) {
				// Check if it is one of the points of the triangle
				if ((aPoint != getPoint(0))
						&& (aPoint != getPoint(1))
						&& (aPoint != getPoint(2)))
					correct = false;
			}
		}

		return correct;
	}

	/**
	 * Check if the triangle is flat or not.
	 * Check if the 3 points have the same Z.
	 *
	 * @return isFlat
	 */
	public boolean isFlatSlope() {
		boolean isFlat = true;
		int i = 0;
		while ((i < 3) && (isFlat)) {
			if (!edges[i].isFlatSlope())
				isFlat = false;
			else
				i++;
		}
		return isFlat;
	}

	/**
	 * Get the point of the triangle that does not belong to the edge
	 *
	 * @return isFlat
	 */
	protected MyPoint getAlterPoint(MyEdge anEdge) {
		MyPoint start = anEdge.getStartPoint();
		MyPoint end = anEdge.getEndPoint();
		return getAlterPoint(start, end);
	}

	/**
	 * Get the point of the triangle that is not one of the 2 points
	 *
	 * @return alterPoint
	 */
	protected MyPoint getAlterPoint(MyPoint p1, MyPoint p2) {
		MyPoint alterPoint = null;

		if (p1 == edges[0].getStartPoint()) {
			if (p2 == edges[0].getEndPoint()) {
				alterPoint = edges[1].getStartPoint();
				if ((alterPoint== p1) || (alterPoint==p2))
					alterPoint = edges[1].getEndPoint();
			}
			else {
				alterPoint = edges[0].getEndPoint();
			}
		}
		else if (p2 == edges[0].getStartPoint()) {
			if (p1 == edges[0].getEndPoint()) {
				alterPoint = edges[1].getStartPoint();
				if ((alterPoint== p1) || (alterPoint==p2))
					alterPoint = edges[1].getEndPoint();
			}
			else {
				alterPoint = edges[0].getEndPoint();
			}
		}
		else
			alterPoint = edges[0].getStartPoint();

		return alterPoint;
	}

	/**
	 * Get the edge of the triangle that includes the two point
	 *
	 * @return alterEdge
	 */
	protected MyEdge getEdgeFromPoints(MyPoint p1, MyPoint p2) {
		MyEdge alterEdge = null;
		MyPoint test1, test2;
		int i = 0;
		while ((i < 3) && (alterEdge == null)) {
			MyEdge testEdge = edges[i];
			test1 = testEdge.getStartPoint();
			test2 = testEdge.getEndPoint();
			if ((test1 == p1) && (test2 == p2))
				alterEdge = testEdge;
			else if ((test1 == p2) && (test2 == p1))
				alterEdge = testEdge;
			else
				i++;
		}

		return alterEdge;
	}

	/**
	 * Check if the point belongs to the triangle
	 *
	 * @return belongs
	 */
	public boolean belongsTo(MyPoint aPoint) {
		boolean belongs = false;
		MyEdge anEdge = this.getEdge(0);
		if (anEdge.getStartPoint() == aPoint)
			belongs = true;
		else if (anEdge.getEndPoint() == aPoint)
			belongs = true;
		else {
			anEdge = this.getEdge(1);
			if (anEdge.getStartPoint() == aPoint)
				belongs = true;
			else if (anEdge.getEndPoint() == aPoint)
				belongs = true;
		}

		return belongs;
	}

	/**
	 * Check if the point is closed to one of the triangle's points
	 *
	 * @return closedTo
	 */
	public boolean isClosedFromPoints(MyPoint aPoint) {
		boolean closedTo = false;
		MyEdge anEdge = this.getEdge(0);
		if (anEdge.getStartPoint() == aPoint)
			closedTo = true;
		else if (anEdge.getEndPoint() == aPoint)
			closedTo = true;
		else {
			anEdge = this.getEdge(1);
			if (anEdge.getStartPoint() == aPoint)
				closedTo = true;
			else if (anEdge.getEndPoint() == aPoint)
				closedTo = true;
		}

		return closedTo;
	}
	
	/**
	 * Get the barycenter of the triangle
	 *
	 * @return isFlat
	 */
	public MyPoint getBarycenter() {
		double x = 0, y = 0, z = 0;
		MyPoint aPoint;
		for (int i = 0; i < 3; i++) {
			aPoint = getPoint(i);

			x += aPoint.getX();
			y += aPoint.getY();
			z += aPoint.getZ();
		}
		x /= 3.0;
		y /= 3.0;
		z /= 3.0;
		
		return new MyPoint(x, y, z);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String myString = new String("Triangle : \n");
		for (int i = 0; i < 3; i++)
			myString += getPoint(i).toString() + "\n";
		return myString;
	}

	/**
	 * Set the edge color Must be used only when using package drawing
	 *
	 * @param g
	 */
	protected void setColor(Graphics g) {
		if (isFlatSlope())
			g.setColor(Color.green);
		else
			g.setColor(Color.yellow);
	}

	/**
	 * Display the triangle in a JPanel Must be used only when using package
	 * drawing
	 * 
	 * @param g
	 * @param decalageX
	 * @param decalageY
	 * @param minX
	 * @param minY
	 * @param scaleX
	 * @param scaleY
	 */
	protected void displayObject(Graphics g, int decalageX, int decalageY,
			double minX, double minY, double scaleX, double scaleY) {
		int[] xPoints, yPoints;
		xPoints = new int[3];
		yPoints = new int[3];
		MyPoint p1, p2, p3;
		p1 = getPoint(0);
		p2 = getPoint(1);
		p3 = getPoint(2);

		xPoints[0] = (int) ((p1.getX() - minX) * scaleX + decalageX);
		xPoints[1] = (int) ((p2.getX() - minX) * scaleX + decalageX);
		xPoints[2] = (int) ((p3.getX() - minX) * scaleX + decalageX);

//		yPoints[0] = (int) ((p1.getY() - minY) * scaleY + decalageY);// coordinate 0 in Y is at top of screen (don't forget make change in sub method)
//		yPoints[1] = (int) ((p2.getY() - minY) * scaleY + decalageY);
//		yPoints[2] = (int) ((p3.getY() - minY) * scaleY + decalageY);
//		
		yPoints[0] = (int) (decalageY - (p1.getY() - minY) * scaleY);// coordinate 0 in Y is at bottom of screen
		yPoints[1] = (int) (decalageY - (p2.getY() - minY) * scaleY);
		yPoints[2] = (int) (decalageY - (p3.getY() - minY) * scaleY);

		setColor(g);
		g.fillPolygon(xPoints, yPoints, 3);

		for (int i = 0; i < 3; i++) {
			edges[i].displayObject(g, decalageX, decalageY, minX, minY, scaleX,
					scaleY);
		}
	}

	/**
	 * Display the triangle in a JPanel Must be used only when using package
	 * drawing
	 *
	 * @param g
	 * @param decalageX
	 * @param decalageY
	 */
	protected void displayObjectCircles(Graphics g, int decalageX, int decalageY) {
		double r = Math.sqrt(radius);
		g.setColor(Color.red);
		g.drawOval((int) (x_center) + decalageX, decalageY - (int) (y_center),
				1, 1);
		g.drawOval((int) (x_center - r) + decalageX, decalageY
				- (int) (y_center + r), (int) r * 2, (int) r * 2);
	}
}
