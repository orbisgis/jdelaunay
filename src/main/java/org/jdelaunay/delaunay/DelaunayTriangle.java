package org.jdelaunay.delaunay;

/**
 * Delaunay Package.
 *
 * @author Jean-Yves MARTIN, Erwan BOCHER, Adelin PIAU, Alexis GUEGANNO
 * @date 2009-01-12
 * @revision 2010-12-20
 * @version 2.12-16
 */

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.ListIterator;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * This is the class representing a Triangle in th DelaunayTriangulation.
 * @author alexis
 */
public class DelaunayTriangle extends Element implements Comparable<DelaunayTriangle>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The array of edges that constitute this triangle
	 */
	protected Edge[] edges;

	private double xCenter, yCenter;
	private double radius;
	
	private int indicator;


	/**
	 * Initialize data structure This method is called by every constructor
	 */
	private void init() {
		this.edges = new Edge[3];
		this.xCenter = 0;
		this.yCenter = 0;
		this.radius = -1;
		this.indicator = 0;
	}

	/**
	 * Create a new triangle with points and edges
	 *
	 */
	public DelaunayTriangle() {
		super();
		init();
	}

	/**
	 * Create a new triangle with edges Add the points from the edges
	 *
	 * @param e1
	 * @param e2
	 * @param e3
	 * @throws DelaunayError
	 */
	public DelaunayTriangle(Edge e1, Edge e2, Edge e3) throws DelaunayError {
		super();
		init();

		//We check the integrity of the edges given to build this triangle
		boolean integrityE1E2 = (e1.isExtremity(e2.getStart()) && ! e3.isExtremity((e2.getStart())))
			|| (e1.isExtremity(e2.getEnd()) && !e3.isExtremity(e2.getEnd()));
		boolean integrityE1E3 =  (e1.isExtremity(e3.getStart()) && ! e2.isExtremity((e3.getStart())))
			|| (e1.isExtremity(e3.getEnd()) && !e2.isExtremity(e3.getEnd()));
		boolean integrityE3E2= (e2.isExtremity(e3.getStart()) && ! e1.isExtremity((e3.getStart())))
			|| (e2.isExtremity(e3.getEnd()) && !e1.isExtremity(e3.getEnd()));

		if(integrityE1E2 && integrityE1E3 && integrityE3E2){
			edges[0] = e1;
			edges[1] = e2;
			edges[2] = e3;

			connectEdges();
			recomputeCenter();
			radius = e1.getStartPoint().squareDistance2D(xCenter, yCenter);
		} else {
			throw new DelaunayError("Problem while generating the Triangle");
		}
	}

	/**
	 * Create a DelaunayTriangle from another triangle NB : it doesn't update edges
	 * connection
	 *
	 * @param aTriangle
	 */
	public DelaunayTriangle(DelaunayTriangle aTriangle) {
		super((Element)aTriangle);
		init();
		System.arraycopy(aTriangle.edges, 0, edges, 0, 3);

		xCenter = aTriangle.xCenter;
		yCenter = aTriangle.yCenter;
		radius = aTriangle.radius;
	}


	/**
	 * Get the ith point
	 * i must be equal to 0, 1 or 2.
	 *
	 * @param i
	 * @return aPoint
	 */
	public Point getPoint(int i) {
		Point p;
		if (i==0) {
			p = edges[0].getStartPoint();
		}
		else if (i==1) {
			p = edges[0].getEndPoint();
		}
		else {
			p = edges[1].getStartPoint();
			if ((p==edges[0].getStartPoint()) || (p==edges[0].getEndPoint())) {
				p = edges[1].getEndPoint();
			}
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
	public Edge getEdge(int i) {
		if ((0<=i) && (i<=2)) {
			return edges[i];
		}
		else {
			return null;
		}
	}

	/**
	 * Return the edges that form this triangle in an array.
	 * @return
	 */
	public Edge[] getEdges(){
		return this.edges;
	}

	/**
	 * Set the ith edge
	 *
	 * @param i
	 * @param anEdge
	 */
	public void setEdge(int i, Edge anEdge) {
		if ((0<=i) && (i<=2)) {
			edges[i] = anEdge;
		}
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
		return new Coordinate(this.xCenter, this.yCenter, 0.0);
	}
	
	@Override
	public int getIndicator() {
		return indicator;
	}
	
	@Override
	public int setIndicator(int indicator) {
		this.indicator=indicator;
		return 0;
	}
	
	@Override
	public void removeIndicator() {
		indicator = 0;
	}
	
	
	/**
	 * get the value of a specific bit
	 * @param byteNumber
	 * @return marked
	 */
	private boolean testBit(int byteNumber) {
		return ((this.indicator & (1 << byteNumber)) != 0);
	}

	/**
	 * set the value of a specific bit
	 * @param byteNumber
	 * @param value
	 */
	private void setBit(int byteNumber, boolean value) {
		int test = (1 << byteNumber);
		if (value) {
			this.indicator = (this.indicator | test);
		}
		else {
			this.indicator = (this.indicator | test) - test;
		}
	}
	
	/**
	 * get the mark of the edge
	 * @param byteNumber
	 * @return marked
	 */
	public boolean isMarked(int byteNumber) {
		return testBit(3+byteNumber);
	}

	/**
	 * set the mark of the edge
	 * @param byteNumber
	 * @param marked
	 */
	public void setMarked(int byteNumber, boolean marked) {
		setBit(3+byteNumber, marked);
	}

	/* (non-Javadoc)
	 * @see org.jdelaunay.delaunay.Element#getBoundingBox()
	 */
	@Override
	public BoundaryBox getBoundingBox() {
		BoundaryBox aBox = new BoundaryBox();

		Point p1,p2,p3;
		p1 = edges[0].getStartPoint();
		p2 = edges[0].getEndPoint();
		p3 = edges[1].getStartPoint();
		if ((p3.equals(p1))||(p3.equals(p2))) {
			p3 = edges[1].getEndPoint();
		}
		aBox.alterBox( p1);
		aBox.alterBox( p2);
		aBox.alterBox( p3);
		
		return aBox;
	}

	/* (non-Javadoc)
	 * @see org.jdelaunay.delaunay.Element#contains(org.jdelaunay.delaunay.Point)
	 */
	@Override
	public boolean contains(Point aPoint) {
		return isInside(aPoint);
	}
	
	@Override
	public boolean contains(Coordinate c) throws DelaunayError {
		return isInside(new Point(c));
	}

	/**
	 * Recompute the center of the circle that joins the 3 points : the CircumCenter
	 */
	protected final void recomputeCenter() {
		Point p1,p2,p3;
		p1 = edges[0].getStartPoint();
		p2 = edges[0].getEndPoint();
		p3 = edges[1].getStartPoint();
		if ((p3.equals(p1))||(p3.equals(p2))) {
			p3 = edges[1].getEndPoint();
		}

		double p1Sq = p1.getX() * p1.getX() + p1.getY() * p1.getY();
		double p2Sq = p2.getX() * p2.getX() + p2.getY() * p2.getY();
		double p3Sq = p3.getX() * p3.getX() + p3.getY() * p3.getY();

		double ux = p2.getX() - p1.getX();
		double uy = p2.getY() - p1.getY();
		double vx = p3.getX() - p1.getX();
		double vy = p3.getY() - p1.getY();

		double cp = ux * vy - uy * vx;
		double cx, cy;

		if (cp != 0) {
			cx = (p1Sq * (p2.getY() - p3.getY()) + p2Sq * (p3.getY() - p1.getY()) + p3Sq
					* (p1.getY() - p2.getY()))
					/ (2.0 * cp);
			cy = (p1Sq * (p3.getX() - p2.getX()) + p2Sq * (p1.getX() - p3.getX()) + p3Sq
					* (p2.getX() - p1.getX()))
					/ (2.0 * cp);

			xCenter = cx;
			yCenter = cy;

			radius = p1.squareDistance2D(xCenter, yCenter);
		} else {
			xCenter = 0.0;
			yCenter = 0.0;
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
			Point aPoint = this.getAlterPoint(edges[i]);
			if (edges[i].isLeft(aPoint)) {
				if (edges[i].getLeft() == null) {
					edges[i].setLeft(this);
				}
				else {
					edges[i].setRight( this );
				}
			}
			else {
				if (edges[i].getRight() == null) {
					edges[i].setRight(this);
				}
				else {
					edges[i].setLeft( this );
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
	public int inCircle(Point aPoint) {
		// default is outside the circle
		int returnedValue = 0;

		// double distance = squareDistance(Center, aPoint);
		double ux = aPoint.getX() - xCenter;
		double uy = aPoint.getY() - yCenter;
		double distance = ux * ux + uy * uy;
		if (distance < radius - Tools.EPSILON2) {
			returnedValue = 1;
		}
		else if (distance < radius + Tools.EPSILON2) {
			returnedValue = 2;
		}

		return returnedValue;
	}

	/**
	 * Check if the point is inside the triangle
	 *
	 * @param aPoint
	 * @return isInside
	 */
	public boolean isInside(Point aPoint) {
		boolean isInside = true;

		int k = 0;
		while ((k < 3) && (isInside)) {
			Edge theEdge = edges[k];

			if (theEdge.getLeft() == this) {
				if (theEdge.isRight(aPoint)) {
					isInside = false;
				}
			} else {
				if (theEdge.isLeft(aPoint)) {
					isInside = false;
				}
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
	public double interpolateZ(Point aPoint) {
		double zValue = 0;

		Point p1,p2,p3;
		p1 = edges[0].getStartPoint();
		p2 = edges[0].getEndPoint();
		p3 = edges[1].getStartPoint();
		if ((p3.equals(p1))||(p3.equals(p2))) {
			p3 = edges[1].getEndPoint();
		}

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

		if (Math.abs(c) > Tools.EPSILON) {
			// Non vertical triangle
			zValue = (-a * aPoint.getX() - b * aPoint.getY() - d) / c;
		}

		return zValue;
	}

	/**
	 * Get Z value of a specific point in the triangle
	 * Take into account triangles connected to the edge
	 *
	 * @param aPoint
	 * @return ZValue
	 */
	public double softInterpolateZ(Point aPoint) {
		double weight = 3.0;
		double zValue = interpolateZ(aPoint) * weight;
		
		// Process connected edges
		for (int i=0; i<3; i++) {
			Edge anEdge = edges[i];
			DelaunayTriangle aTriangle = null;
			if (anEdge != null) {
				if (anEdge.getLeft() == this) {
					aTriangle = anEdge.getRight();
				} else {
					aTriangle = anEdge.getLeft();
				}
			}
			if (aTriangle != null) {
				weight += 1.0;
				zValue += aTriangle.interpolateZ(aPoint);
			}
		}
		// Define new Z value
		zValue /= weight;

		return zValue;
	}

	/**
	 * Compute triangle area
	 *
	 * @return area
	 */
	public double computeArea() {
		Point p1,p2,p3;
		p1 = edges[0].getStartPoint();
		p2 = edges[0].getEndPoint();
		p3 = edges[1].getStartPoint();
		if ((p3.equals(p1))||(p3.equals(p2))) {
			p3 = edges[1].getEndPoint();
		}

		double area = ((p3.getX()-p1.getX())*(p2.getY()-p1.getY())-(p2.getX()-p1.getX())*(p3.getY()-p1.getY()))/2;

		return area<0 ? -area : area ;
	}

	/**
	 * check if one of the triangle's angle is less than minimum
	 *
	 * @param tolarance
	 * @return minAngle
	 */
	protected int badAngle(double tolarance) {
		double minAngle = 400;
		int returndeValue = -1;
		for (int k = 0; k < 3; k++) {
			int k1 = (k + 1) % 3;
			int k2 = (k1 + 1) % 3;

			Point p1 = this.getPoint(k);
			Point p2 = this.getPoint(k1);
			Point p3 = this.getPoint(k2);

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
				if (minAngle < tolarance) {
					returndeValue = k;
				}
			}
		}
		return returndeValue;
	}

	/**
	 * check if one of the triangle's angle is less than minimum
	 *
	 * @return maxAngle
	 */
	protected double getMaxAngle() {
		double maxAngle = 0;
		for (int k = 0; k < 3; k++) {
			int k1 = (k + 1) % 3;
			int k2 = (k1 + 1) % 3;

			Point p1 = this.getPoint(k);
			Point p2 = this.getPoint(k1);
			Point p3 = this.getPoint(k2);

			double ux = p2.getX() - p1.getX();
			double uy = p2.getY() - p1.getY();
			double vx = p3.getX() - p1.getX();
			double vy = p3.getY() - p1.getY();

			double dp = ux * vx + uy * vy;

			double angle = Math.acos(Math.sqrt(((dp * dp))
					/ ((ux * ux + uy * uy) * (vx * vx + vy * vy))))
					* (180d / Math.PI);
			if (angle > maxAngle) {
				maxAngle = angle;
			}
		}
		return maxAngle;
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
				if (edges[i] == edges[j]) {
					foundEdge++;
				}
			}
			if (foundEdge != 1) {
				correct = false;
			}
			i++;
		}

		// check if each edge is connected to the triangle
		i = 0;
		while ((i < 3) && (correct)) {
			int foundEdge = 0;
			if (edges[i].getLeft() == this) {
				foundEdge++;
			}
			if (edges[i].getRight() == this) {
				foundEdge++;
			}

			if (foundEdge != 1) {
				correct = false;
			}
			i++;
		}

		// Check if each point in the edges is referenced 2 times
		Point aPoint;
		i = 0;
		while ((i < 3) && (correct)) {
			Edge anEdge = edges[i];
			for (j = 0; j < 2; j++) {
				if (j == 0) {
					aPoint = anEdge.getStartPoint();
				}
				else {
					aPoint = anEdge.getEndPoint();
				}
				int foundPoint = 0;
				for (k = 0; k < 3; k++) {
					if (edges[k].getStartPoint() == aPoint) {
						foundPoint++;
					}
					if (edges[k].getEndPoint() == aPoint) {
						foundPoint++;
					}
				}
				if (foundPoint != 2) {
					correct = false;
				}
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
	public boolean checkDelaunay(ArrayList<Point> thePoints) {
		boolean correct = true;
		ListIterator<Point> iterPoint = thePoints.listIterator();
		while (iterPoint.hasNext() && correct) {
			Point aPoint = iterPoint.next();
			if (inCircle(aPoint) == 1 && !aPoint.equals(getPoint(0))
                                        && !aPoint.equals(getPoint(1))
                                        && !aPoint.equals(getPoint(2))) {
                                // Check if it is one of the points of the triangle
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
			if (!edges[i].isFlatSlope()) {
				isFlat = false;
			}
			else {
				i++;
			}
		}
		return isFlat;
	}

	/**
	 * Get the point of the triangle that is not belong to the edge
	 *
	 * @param anEdge
	 * @return alterPoint
	 */
	public final Point getAlterPoint(Edge anEdge) {
		Point start = anEdge.getStartPoint();
		Point end = anEdge.getEndPoint();
		return getAlterPoint(start, end);
	}

	/**
	 * Get the point of the triangle that is not one of the 2 points given
	 * in argument.
	 * If one of these argument is not part of this triangle, this method will
	 * return null.
	 *
	 * @param p1
	 * @param p2
	 * @return alterPoint
	 */
	protected final Point getAlterPoint(Point p1, Point p2) {
		Point alterPoint = null;

		if (p1 == edges[0].getStartPoint()) {
			if (p2 == edges[0].getEndPoint()) {
				alterPoint = edges[1].getStartPoint();
				if ((alterPoint.equals(p1)) || (alterPoint.equals(p2))) {
					alterPoint = edges[1].getEndPoint();
				}
			}
			else {
				alterPoint = edges[0].getEndPoint();
			}
		}
		else if (p2 == edges[0].getStartPoint()) {
			if (p1 == edges[0].getEndPoint()) {
				alterPoint = edges[1].getStartPoint();
				if ((alterPoint.equals(p1)) || (alterPoint.equals(p2))) {
					alterPoint = edges[1].getEndPoint();
				}
			}
			else {
				alterPoint = edges[0].getEndPoint();
			}
		}
		else {
			alterPoint = edges[0].getStartPoint();
		}

		return alterPoint;
	}

	/**
	 * Get the edge of the triangle that includes the two point
	 *
	 * @param p1
	 * @param p2
	 * @return alterEdge
	 */
	protected Edge getEdgeFromPoints(Point p1, Point p2) {
		Edge alterEdge = null;
		Point test1, test2;
		int i = 0;
		while ((i < 3) && (alterEdge == null)) {
			Edge testEdge = edges[i];
			test1 = testEdge.getStartPoint();
			test2 = testEdge.getEndPoint();
			if ((test1.equals(p1)) && (test2.equals(p2))) {
				alterEdge = testEdge;
			}
			else if ((test1.equals(p2)) && (test2.equals(p1))) {
				alterEdge = testEdge;
			}
			else {
				i++;
			}
		}

		return alterEdge;
	}
	
	/**
	 * Get the position of edge of the triangle that includes the two point
	 *
	 * @param p1 
	 * @param p2
	 * @return postion -1 if not found.
	 */
	protected int getEdgePositionFromPoints(Point p1, Point p2) {
		int edgePosition = -1;
		Point test1, test2;
		int i = 0;
		while ((i < 3) && (edgePosition == -1)) {
			Edge testEdge = edges[i];
			test1 = testEdge.getStartPoint();
			test2 = testEdge.getEndPoint();
			if ((test1.equals(p1)) && (test2.equals(p2))) {
				edgePosition = i;
			}
			else if ((test1.equals(p2)) && (test2.equals(p1))) {
				edgePosition = i;
			}
			else {
				i++;
			}
		}

		return edgePosition;
	}

	/**
	 * Check if the point belongs to the triangle
	 *
	 * @param aPoint
	 * @return belongs
	 */
	public boolean belongsTo(Point aPoint) {
		boolean belongs = false;
		Edge anEdge = this.getEdge(0);
		if (anEdge.getStartPoint() == aPoint) {
			belongs = true;
		}
		else if (anEdge.getEndPoint() == aPoint) {
			belongs = true;
		}
		else {
			anEdge = this.getEdge(1);
			if (anEdge.getStartPoint() == aPoint) {
				belongs = true;
			}
			else if (anEdge.getEndPoint() == aPoint) {
				belongs = true;
			}
		}

		return belongs;
	}

	/**
	 * Check if the point is closed to one of the triangle's points
	 *
	 * @param aPoint
	 * @return closedTo
	 */
	public boolean isClosedFromPoints(Point aPoint) {
		boolean closedTo = false;
		Edge anEdge = this.getEdge(0);
		if (anEdge.getStartPoint() == aPoint) {
			closedTo = true;
		}
		else if (anEdge.getEndPoint() == aPoint) {
			closedTo = true;
		}
		else {
			anEdge = this.getEdge(1);
			if (anEdge.getStartPoint() == aPoint) {
				closedTo = true;
			}
			else if (anEdge.getEndPoint() == aPoint) {
				closedTo = true;
			}
		}

		return closedTo;
	}
	
	/**
	 * Get the barycenter of the triangle
	 *
	 * @return isFlat
	 * @throws DelaunayError 
	 */
	public Point getBarycenter() throws DelaunayError {
		double x = 0, y = 0, z = 0;
		Point aPoint;
		for (int i = 0; i < 3; i++) {
			aPoint = getPoint(i);

			x += aPoint.getX();
			y += aPoint.getY();
			z += aPoint.getZ();
		}
		x /= 3.0;
		y /= 3.0;
		z /= 3.0;
		
		return new Point(x, y, z);
	}

	/**
	 * Gives a rperesentation of this triangle as a String.
	 * @return Triangle "+gid+": ["+getPoint(0).toString()+", "+getPoint(1).toString()+", "+getPoint(2).toString()+"]
	 */
	@Override
	public String toString() {
		return "Triangle "+gid+": ["+getPoint(0).toString()+", "+getPoint(1).toString()+", "+getPoint(2).toString()+"]";
	}

	/**
	 * Set the edge color Must be used only when using package drawing
	 *
	 * @param g
	 */
	protected void setColor(Graphics g) {
		if(property>0) {
			g.setColor(new Color(property));
		}
		else if (isFlatSlope()) {
			g.setColor(Color.green);
		}
		else {
			g.setColor(Color.yellow);
		}
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
		Point p1, p2, p3;
		p1 = getPoint(0);
		p2 = getPoint(1);
		p3 = getPoint(2);

		xPoints[0] = (int) ((p1.getX() - minX) * scaleX + decalageX);
		xPoints[1] = (int) ((p2.getX() - minX) * scaleX + decalageX);
		xPoints[2] = (int) ((p3.getX() - minX) * scaleX + decalageX);

		yPoints[0] = (int) ((p1.getY() - minY) * scaleY + decalageY);
		yPoints[1] = (int) ((p2.getY() - minY) * scaleY + decalageY);
		yPoints[2] = (int) ((p3.getY() - minY) * scaleY + decalageY);

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
		g.drawOval((int) (xCenter) + decalageX, decalageY - (int) (yCenter),
				1, 1);//FIXME not good position
		g.drawOval((int) (xCenter - r) + decalageX, decalageY
				- (int) (yCenter + r), (int) r * 2, (int) r * 2);//FIXME not good position
	}

	@Override
	public boolean isUseByPolygon() {
		return testBit(4);
	}

	@Override
	public void setUseByPolygon(boolean useByPolygon) {
		setBit(4, useByPolygon);
	}

	/**
	 * implements the Comparable interface. The triangles will be sorted according
	 * the middle of their bounding box.
	 * As we work on a triangulation where triangles' intersection can only be an edge, a point
	 * or void, the Bounding boxes are unique.
	 * @param t
	 * @return
	 */
	@Override
	public int compareTo(DelaunayTriangle t) {
		Coordinate midT = getBoundingBox().getMiddle();
		Coordinate midO = t.getBoundingBox().getMiddle();
		return midT.compareTo(midO);
	}


}
