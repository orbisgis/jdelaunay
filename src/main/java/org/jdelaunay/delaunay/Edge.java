package org.jdelaunay.delaunay;

/**
 * Delaunay Package.
 *
 * @author Jean-Yves MARTIN, Erwan BOCHER, Adelin PIAU
 * @date 2009-01-12
 * @revision 2010-11-08
 * @version 1.2
 */
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.vividsolutions.jts.geom.Coordinate;
import org.apache.log4j.Logger;

public class Edge extends Element implements Comparable<Edge> {

	//The logger supposed to report errors to the user.
	private static Logger log = Logger.getLogger(Edge.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Point startPoint, endPoint;
	private DelaunayTriangle left, right;
	private BoundaryBox aBox;
	//An edge is considered to be degenerated if it is connected to the boundary
	//of the mesh, but is not part of any triangle. It is the case when adding 
	//a point to the mesh that can't see any point of the boundary, because of
	//the existing constraints.
	private transient boolean degenerated;
	/**
	 * bit number  | function :
	 * 1			| isOutsideMesh / setOutsideMesh
	 * 2			| isLocked / setLocked
	 * 3			| isLevelEdge / setLevelEdge
	 * 4			| isUseByPolygon / setUseByPolygon
	 * 5			| isZUse / setUseZ
	 * 6 to 32		| isMarked / setMarked
	 */
	private int indicator;
	static final int UPSLOPE = -1;
	static final int DOWNSLOPE = 1;
	static final int FLATSLOPE = 0;
	//Intersection constants :
	/**
	 * Value returned by the intersects method when the two edges don't intersect
	 */
	public static final int NO_INTERSECTION=0;
	/**
	 * Value returned by the intersects method when the two edges intersect in one point
	 *
	 */
	public static final int INTERSECT=1;
	/**
	 * Value returned by the intersects method when the two edges are colinear and don't
	 * intersect
	 */
	public static final int COLINEAR=2;
	/**
	 * Value returned by the intersects method when the two edges intersect in one
	 * point that is an extremity for both of them.
	 */
	public static final int SHARE_EXTREMITY=3;
	/**
	 * Value returned by the intersects method when the two edges intersect in
	 * more than one point (ie they are colinear and do not just share their extremities).
	 */
	public static final int SHARE_EDGE_PART=4;


	/**
	 * Initialize data.
	 */
	private void init() {
		startPoint = null;
		endPoint = null;
		left = null;
		right = null;
		indicator = 0;
		degenerated=false;
//		aBox=null;
	}

	private void updateBox() {
		aBox = new BoundaryBox();
		aBox.alterBox(this.startPoint);
		aBox.alterBox(this.endPoint);
	}

	/**
	 * Generate a new edge.
	 */
	public Edge() {
		super();
		init();
	}

	/**
	 * Generate an edge from two points.
	 *
	 * @param startPoint
	 * @param endPoint
	 */
	public Edge(Point start, Point end) {
		super();
		init();
		this.startPoint = start;
		this.endPoint = end;
//		updateBox();
	}

	/**
	 * Generate an edge from another edge.
	 *
	 * @param ed
	 */
	public Edge(Edge ed) {
		super((Element) ed);
		init();
		this.startPoint = ed.startPoint;
		this.endPoint = ed.endPoint;
		this.left = ed.left;
		this.right = ed.right;
		this.indicator = ed.indicator;
		this.property = ed.property;
//		updateBox();
	}

	/**
	 * Create a new edge given the coordinates of its two extremities
	 * @param x
	 * @param y
	 * @param z
	 * @param u
	 * @param v
	 * @param w
	 */
	public Edge(double x, double y, double z, double u, double v, double w) {
		super();
		init();
		try {
			Point p1 = new Point(x, y, z);
			Point p2 = new Point(u, v, w);
			this.startPoint = p1;
			this.endPoint = p2;
		} catch (DelaunayError d) {
			log.error("A problem occured while building the points " + d.getMessage());
		}
	}

	/**
	 * @param i
	 * @return If i==0, return startPoint else return endPoint.
	 */
	public final Point point(int i) {
		if (i == 0) {
			return this.startPoint;
		} else {
			return this.endPoint;
		}
	}

	/**
	 * @return DelaunayTriangle at the left of edge.
	 */
	public final DelaunayTriangle getLeft() {
		return this.left;
	}

	/**
	 * @return DelaunayTriangle at the right of edge.
	 */
	public final DelaunayTriangle getRight() {
		return this.right;
	}

	/**
	 * Return the left triangle if tri is the right one, the right triangle
	 * if tri is the left one, and null otherwise.
	 * @param tri
	 * @return
	 */
	public final DelaunayTriangle getOtherTriangle(DelaunayTriangle tri){
		if(tri == null){
			return null;
		} else if(tri.equals(right)){
			return left;
		} else if(tri.equals(left)){
			return right;
		} else {
			return null;
		}
	}

	/**
	 * Set DelaunayTriangle at left of edge.
	 * 
	 * @param aTriangle A triangle at left of edge.
	 */
	public final void setLeft(DelaunayTriangle aTriangle) {
		this.left = aTriangle;
	}

	/**
	 * Set DelaunayTriangle at right of edge.
	 * 
	 * @param aTriangle A triangle at right of edge.
	 */
	public final void setRight(DelaunayTriangle aTriangle) {
		this.right = aTriangle;
	}

	/**
	 * @return start point of edge.
	 */
	public final Point getStart() {
		return this.startPoint;
	}

	/**
	 * @return start point of edge.
	 */
	public final Point getStartPoint() {
		return this.startPoint;
	}

	/**
	 * @return end point of edge.
	 */
	public final Point getEnd() {
		return this.endPoint;
	}

	/**
	 * @return end point of edge.
	 */
	public final Point getEndPoint() {
		return this.endPoint;
	}

	/**
	 * Checks if this edge is "degenerated" or not. An edge is marked as degenerated
	 * when connecting to the mesh, but not implied in the buildingof any triangle.
	 * @return
	 */
	public final boolean isDegenerated(){
		return degenerated;
	}

	/**
	 * Determines if this edge must be considered as degenerated or not.
	 * @param degen
	 */
	public final void setDegenerated(boolean degen){
		degenerated=degen;
	}

	/**
	 * Set edge start point.
	 *
	 * @param p Start point.
	 */
	public final void setStartPoint(Point p) {
		if (isUseByPolygon()) {
			p.setUseByPolygon(true);
		}

		this.startPoint = p;
		updateBox();
	}

	/**
	 * Set edge end point.
	 *
	 * @param p End point.
	 */
	public final void setEndPoint(Point p) {
		if (isUseByPolygon()) {
			p.setUseByPolygon(true);
		}

		this.endPoint = p;
		updateBox();
	}

	/**
	 * Get the point of this edge that is on the left from the other. 
	 * We use the order relation defined in Point. Consequently, with a vertical
	 * edge, this method return the minimum point, (so the one with the lowest
	 * y).
	 * @return
	 */
	public final Point getPointLeft() {
		int c = endPoint.compareTo2D(startPoint);
		switch (c) {
			case -1:
				return endPoint;
			default:
				return startPoint;
		}
	}

	/**
	 * Get the point of this edge that is on the left from the other.
	 * We use the order relation defined in Point.
	 * @return
	 */
	public final Point getPointRight() {
		int c = endPoint.compareTo2D(startPoint);
		switch (c) {
			case 1:
				return endPoint;
			default:
				return startPoint;
		}
	}

	/**
	 * get squared 2D length
	 */
	protected final double getSquared2DLength() {
		return startPoint.squareDistance2D(endPoint);
	}

	/**
	 * get 2D length
	 */
	public final double get2DLength() {
		return Math.sqrt(getSquared2DLength());
	}

	/**
	 * get squared 3D length
	 */
	protected final double getSquared3DLength() {
		return startPoint.squareDistance(endPoint);
	}

	/**
	 * get 3D length
	 */
	public final double get3DLength() {
		return Math.sqrt(getSquared3DLength());
	}

	@Override
	public final int getIndicator() {
		return indicator;
	}

	@Override
	public final int setIndicator(int indicator) {
		this.indicator = indicator;
		return 0;
	}

	@Override
	public final void removeIndicator() {
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
		} else {
			this.indicator = (this.indicator | test) - test;
		}

		startPoint.setMarkedByEdge(byteNumber, value);
		endPoint.setMarkedByEdge(byteNumber, value);
	}

	/**
	 * get the mark of the edge
	 * @param byteNumber
	 * @return marked
	 */
	public final boolean isMarked(int byteNumber) {
		return testBit(6 + byteNumber);
	}

	/**
	 * set the mark of the edge
	 * @param byteNumber
	 * @param marked
	 */
	public final void setMarked(int byteNumber, boolean marked) {
		setBit(6 + byteNumber, marked);
	}

	/**
	 * get the mark of the edge
	 * @return marked
	 */
	public final boolean isLocked() {
		return testBit(2);
	}

	/**
	 * set the mark of the edge
	 * @param marked
	 */
	public final void setLocked(boolean locked) {
		setBit(2, locked);
	}

	/**
	 * check if edge is taken into account in the triangularization
	 * @return outsideMesh
	 */
	public final boolean isOutsideMesh() {
		return testBit(1);
	}

	/**
	 * set the edge in the triangularization or not
	 * @param outsideMesh
	 */
	public final void setOutsideMesh(boolean outsideMesh) {
		setBit(1, outsideMesh);
	}

	/**
	 * check if edge is a level edge. 
	 * @return levelEdge
	 */
	public final boolean isLevelEdge() {
		return testBit(3);
	}

	/**
	 * set if edge is a level edge.
	 * @param levelEdge
	 */
	public final void setLevelEdge(boolean levelEdge) {
		setBit(3, levelEdge);
	}

	/**
	 * check if edge is use by a polygon
	 * @return useByPolygon
	 */
	@Override
	public final boolean isUseByPolygon() {
		return testBit(4);
	}

	/**
	 * set if edge is use by a polygon.
	 * @param useByPolygon
	 */
	@Override
	public final void setUseByPolygon(boolean useByPolygon) {
		setBit(4, useByPolygon);
	}

	/**
	 * check if Z coordinate is use.
	 * @return useZ
	 */
	public final boolean isZUse() {
		return testBit(5);
	}

	/**
	 * set if Z coordinate is use.
	 * @param useByPolygon
	 */
	public final void setUseZ(boolean useZ) {
		setBit(5, useZ);
	}

	/* (non-Javadoc)
	 * @see org.jdelaunay.delaunay.Element#getBoundingBox()
	 */
	@Override
	public final BoundaryBox getBoundingBox() {

		BoundaryBox box = new BoundaryBox();
		box.alterBox(this.startPoint);
		box.alterBox(this.endPoint);

		return box;
	}

	/* (non-Javadoc)
	 * @see org.jdelaunay.delaunay.Element#contains(org.jdelaunay.delaunay.Point)
	 */
	@Override
	public final boolean contains(Point aPoint) {
		return contains(aPoint.getCoordinate());
	}

	@Override
	public final boolean contains(Coordinate c) {
		Point p1 = this.startPoint;
		Point p2 = this.endPoint;
		double ux = p2.getX() - p1.getX();
		double uy = p2.getY() - p1.getY();
		double vx = c.x - p1.getX();
		double vy = c.y - p1.getY();
		double res = ux * vy - uy * vx;
		boolean px = (ux >= 0 ? (p1.getX() - Tools.EPSILON <= c.x && c.x <= p2.getX() + Tools.EPSILON) :
			(p2.getX() - Tools.EPSILON <= c.x && c.x <= p1.getX() + Tools.EPSILON));/* px is in [p1x, p2x]*/
		boolean py = (uy >= 0 ? (p1.getY() - Tools.EPSILON <= c.y && c.y <= p2.getY() + Tools.EPSILON) : (
			p2.getY() - Tools.EPSILON <= c.y && c.y <= p1.getY() + Tools.EPSILON));/* py is in [p1y, p2y]*/
		return res <= Tools.EPSILON && res >= -Tools.EPSILON/* p is on p1, p2 line */
			&& px && py;
	}

	/**
	 * Check if this and other intersect.
	 * @param other
	 * @return intersection :<br/>
	 * 			0 = no intersection<br/>
	 * 			1 = intersects<br/>
	 * 			2 = co-linear<br/>
	 * 			3 = intersects at the extremity<br/>
	 *			4 = intersect in more than one point<br/>
	 * note that if on extremity of an edge lies inside the other edge, but
	 * is not one of the extremities of the other edge, this method
	 * returns 1
	 */
	public final int intersects(Edge other) throws DelaunayError{
		return intersects(other.getStart(), other.getEnd());
	}

	/**
	 * check if two edges intersect
	 *
	 * @param p1 the start point of the other edge
	 * @param p2 the end point of the other edge
	 * @return intersection :<br/>
	 * 			NO_INTERSECTION = no intersection<br/>
	 * 			INTERSECT = intersect<br/>
	 * 			COLINEAR = co-linear and don't intersect<br/>
	 * 			SHARE_EXTREMITY = intersects at the extremity<br/>
	 *			SHARE_EDGE_PART = intersect in more than one point<br/>
	 * note that if on extremity of an edge lies inside the other edge, but
	 * is not one of the extremities of the other edge, this method
	 * returns 1
	 */
	public final int intersects(Point p1, Point p2) throws DelaunayError {
		Point p3 = this.startPoint;
		Point p4 = this.endPoint;
		Element inter = getIntersection(p1, p2, false);
		if(inter==null){//there is no intersection, return 0 or 2
			// (x2 - x1) t1 - (x4 - x3) t2 = (x3 - x1)
			// (y2 - y1) t1 - (y4 - y3) t2 = (y3 - y1)
			double a1 = p2.getX() - p1.getX();
			double b1 = p4.getX() - p3.getX();
			double a2 = p2.getY() - p1.getY();
			double b2 = p4.getY() - p3.getY();

			// d = (x4 - x3) (y2 - y1) - (x2 - x1) * (y4 - y3)
			double d = b1 * a2 - b2 * a1;
			if(-Tools.EPSILON2 < d && d < Tools.EPSILON2){
				//the two edges are colinear
				return COLINEAR;
			}else {
				return NO_INTERSECTION;
			}
		} else if(inter instanceof Point){
			//intersection in one point,
			//return 1 or 3
			Point interPoint = (Point) inter;
			if((interPoint.squareDistance2D(p1)<Tools.EPSILON2 ||
				interPoint.squareDistance2D(p2)<Tools.EPSILON2)&&
				(interPoint.squareDistance2D(p3)<Tools.EPSILON2||
				interPoint.squareDistance2D(p4)<Tools.EPSILON2)){
				//intersection at an extremity of each edge.
				return SHARE_EXTREMITY;
			} else {
				return INTERSECT;
			}
			
		} else if(inter instanceof Edge){
			//intersection in more than
			//one point, return 4
			return SHARE_EDGE_PART;
		}
		return NO_INTERSECTION;
	}

	/**
	 * intersects two edges returns null if there is no intersection
	 *
	 * @param p1
	 * @param p2
	 * @return intersection
	 * @throws DelaunayError 
	 */
	public final Element getIntersection(Point p1, Point p2) throws DelaunayError {
		return getIntersection(p1, p2, false);
	}

	/**
	 * intersects two edges returns null if there is no intersection
	 * if the two edgse are colinear, returns the minimum intersection point,
	 * if such a point exists.
	 * @param p1
	 * @param p2
	 * @param useCoordZOfp1p2 If true, the coordinate of intersection get in Z the average of p1 and p2 Z. Don't care of p3 and, p4 Z.
	 * Else if false, the coordinate of intersection get in Z the average of p1, p2, p3 and p4 Z.
	 * @return intersection
	 * @throws DelaunayError 
	 */
	public final Element getIntersection(Point point1, Point point2, boolean useCoordZOfp1p2) throws DelaunayError {
		Element intersection = null;
		Point p3 = getPointLeft();
		Point p4 = getPointRight();
		Point p1 ;
		Point p2 ;
		switch(point1.compareTo2D(point2)){
			//we put the leftmost point in p1, and the rightmost in p2
			case 1 :
				p1=point2;
				p2=point1;
				break;
			default:
				p1=point1;
				p2=point2;
		}

		// (x2 - x1) t1 - (x4 - x3) t2 = (x3 - x1)
		// (y2 - y1) t1 - (y4 - y3) t2 = (y3 - y1)

		double deltaXO = p2.getX() - p1.getX();
		double deltaXT = p4.getX() - p3.getX();
		double c1 = p3.getX() - p1.getX();
		double deltaYO = p2.getY() - p1.getY();
		double deltaYT = p4.getY() - p3.getY();
		double c2 = p3.getY() - p1.getY();

		// d = (x4 - x3) (y2 - y1) - (x2 - x1) * (y4 - y3)
		double d = deltaXT * deltaYO - deltaYT * deltaXO;
		if (Math.abs(d) > Tools.EPSILON2) {
			//The two edges are not colinear.
			if(p1.compareTo2D(p4)==1 || p3.compareTo2D(p2)==1){
				return null;
			}
			// t1 = ((y3 - y1) (x4 - x3) - (x3 - x1) (y4 - y3)) / d
			// t2 = ((x2 - x1) (y3 - y1) - (y2 - y1) (x3 - x1)) / d

			double t1 = (c2 * deltaXT - c1 * deltaYT) / d;
			double t2 = (deltaXO * c2 - deltaYO * c1) / d;

			if ((-Tools.EPSILON2 <= t1) && (t1 <= 1 + Tools.EPSILON2) && (-Tools.EPSILON2 <= t2)
				&& (t2 <= 1 + Tools.EPSILON2)) {
				// it intersects
				if (t2 <= Tools.EPSILON2) {
					intersection = p3;
				} else if (t2 >= 1 - Tools.EPSILON2) {
					intersection = p4;
				} else if (t1 <= Tools.EPSILON2) {
					intersection = p1;
				} else if (t1 >= 1 - Tools.EPSILON2) {
					intersection = p2;
				} else {
					// x = x2 t1 + (1 - t1) x1
					// y = y2 t1 + (1 - t1) y1
					// z = z2 t1 + (1 - t1) z1
					// z = z4 t2 + (1 - t2) z3
					double x = p2.getX() * t1 + (1 - t1) * p1.getX();
					double y = p2.getY() * t1 + (1 - t1) * p1.getY();


					double z = 0;
					if (useCoordZOfp1p2) {
						// Average of p1 and p2 Z. Don't care of p3 and p4 Z.
						z = p2.getZ() * t1 + (1 - t1) * p1.getZ();
					} else {
						// Average of p1, p2, p3 and p4 Z.
						z = p4.getZ() * t2 + (1 - t2) * p3.getZ();
					}
					intersection = new Point(x, y, z);

				}
			}
		} else { //d==0 : the two edges are colinear
			double test;
			if (Math.abs(deltaXO) < Tools.EPSILON2) {
				test = c1 / deltaXT - c2 / deltaYT;
			} else {
				test = c1 / deltaXO - c2 / deltaYO;
			}
			if (Math.abs(test) > Tools.EPSILON2) {//the two supporting lines are different
				intersection = null;
			} else {//we have one supporting line
				//t13 is the position of the point three on the edge 1->2
				double t13, t14, t21, t22;
				if (Math.abs(deltaXO) < Tools.EPSILON2) {
					t13 = c2 / deltaYO;
					t14 = (p4.getY() - p1.getY()) / (deltaYO);
				} else {
					t13 = c1 / deltaXO;
					t14 = (p4.getX() - p1.getX()) / (deltaXO);
				}
				if (Math.abs(deltaXT) > Tools.EPSILON2) {
					t21 = -c1 / deltaXT;
					t22 = (p2.getX() - p3.getX()) / deltaXT;
				} else {
					t21 = -c2 / deltaYT;
					t22 = (p2.getY() - p3.getY()) / (deltaYT);
				}
				if (-Tools.EPSILON2 < t13 && t13 < 1 + Tools.EPSILON2) {
					if (-Tools.EPSILON2 < t14 && t14 < 1 + Tools.EPSILON2) {
                                                //p3 and p4 are both on the edge [p1 p2]
						intersection = new Edge(p3, p4);
					} else {
                                                //p4 is not on [p1 p2]
						if (p3.squareDistance2D(p1) < Tools.EPSILON2) {
                                                        //p3 and p1 are equal
							if (-Tools.EPSILON2 < t22 && t22 < 1 + Tools.EPSILON2) {
                                                                //p2 is on [p3 p4]
								intersection = new Edge(p1, p2);
							} else {
                                                                //p2 is not on [p3 p4], and p3 is not on [p1 p2]
								intersection = p3;
							}
						} else if (p3.squareDistance2D(p2) < Tools.EPSILON2) {
                                                        //p3 and p2 are equals
							if (-Tools.EPSILON2 < t21 && t21 < 1 + Tools.EPSILON2) {
                                                                //p1 is on [p3 p4]
								intersection = new Edge(p1, p2);
							} else {
                                                                //p1 is not on [p3 p4], and p3 is not on [p1 p2]
								intersection = p3;
							}

						} else if (-Tools.EPSILON2 < t21 && t21 < 1 + Tools.EPSILON2) {
                                                        //p1 is on [p3 p4]
							intersection = new Edge(p1, p3);
						} else {
							intersection = new Edge(p2, p3);
						}
					}
				} else if (-Tools.EPSILON2 < t14 && t14 < 1 + Tools.EPSILON2) {
                                //p3 is not on [p1 p2], but p4 is on it
					if (p4.squareDistance2D(p1) < Tools.EPSILON2) {
                                                //p4 and p1 are equal
						if (-Tools.EPSILON2 < t22 && t22 < 1 + Tools.EPSILON2) {
                                                        //p2 is on [p3 p4]
							intersection = new Edge(p1, p2);
						} else {
                                                        //p2 is not on [p3 p4] and p3 is not on [p1 p2]
							intersection = p4;
						}
					} else if (p4.squareDistance2D(p2) < Tools.EPSILON2) {
                                                //p4 and p1 are equal
						if (-Tools.EPSILON2 < t21 && t21 < 1 + Tools.EPSILON2) {
                                                        //p1  is on [p3 p4]
							intersection = new Edge(p1, p2);
						} else {
                                                        //p1 is not on [p3 p4] and p3 is not on [p1 p2]
							intersection = p4;
						}

					} else if (-Tools.EPSILON2 < t21 && t21 < 1 + Tools.EPSILON2) {
                                                //p1 is on [p3 p4]
						intersection = new Edge(p1, p4);
					} else {
						intersection = new Edge(p2, p4);
					}
				} else if (Tools.EPSILON2 < t21 && t21 < 1 - Tools.EPSILON2) {
                                        //p1 is on [p3 p4]. As we've seen, nor p4 neither p3 are
                                        // on [p1 p2], so we can conclude that the intersection is [p1 p2]
					intersection = new Edge(p1, p2);
				}

			}

		}
		return intersection;
	}

	/**
	 * intersects two edges returns null if there is no intersection
	 *
	 * @param anEdge
	 * @return intersection
	 * @throws DelaunayError 
	 */
	public final Element getIntersection(Edge anEdge) throws DelaunayError {
		return getIntersection(anEdge.startPoint, anEdge.endPoint);
	}

	/**
	 * @param p
	 * @return Z coordinate of point p on the edge.
	 */
	public final double getZOnEdge(Point p) {
		Point p1 = this.startPoint;
		Point p2 = this.endPoint;

		if (p2.getX() == p1.getX()) {
			return ((p2.getX() - p.getX()) * (p2.getZ() - p1.getZ())) / (p2.getX() - p1.getX());
		}

		if (p2.getY() == p1.getY()) {
			return ((p2.getY() - p.getY()) * (p2.getZ() - p1.getZ())) / (p2.getY() - p1.getY());
		}

		return p1.getZ();
	}

	/**
	 * check if the point is between the extremities of the edge (on the
	 * xy-plane)
	 *
	 * @param p
	 * @return isInside
	 */
	public final boolean isInside(Point p) {
		boolean isInside = false;

		Point p1 = this.startPoint;
		Point p2 = this.endPoint;

		// x = x2 t1 + (1 - t1) x1
		// y = y2 t1 + (1 - t1) y1
		// z = z2 t1 + (1 - t1) z1

		// (x2 - x1) t1 = (x - x1)
		// (y2 - y1) t1 = (y - y1)

		// t1 = (x - x1) / (x2 - x1)
		// t1 = (y - y1) / (y2 - y1)
		double t1, t2;

		double a1 = p2.getX() - p1.getX();
		double c1 = p.getX() - p1.getX();
		double a2 = p2.getY() - p1.getY();
		double c2 = p.getY() - p1.getY();

		if (Math.abs(a1) > Tools.EPSILON) {
			t1 = c1 / a1;
			if ((-Tools.EPSILON < t1) && (t1 < 1 + Tools.EPSILON)) {
				// p.getX() is between p1.getX() and p2.getX()
				if (Math.abs(a2) > Tools.EPSILON) {
					t2 = c2 / a2;
					if ((-Tools.EPSILON < t2) && (t2 < 1 + Tools.EPSILON)
						&& (Math.abs(t1 - t2) < Tools.EPSILON)) {
						isInside = true;
					}
				} else if (Math.abs(c2) < Tools.EPSILON) {
					// p1.getY(), p2.getY() and p.getY() are the same
					isInside = true;
				}
			}
		} else if (Math.abs(c1) < Tools.EPSILON) {
			// p1.getX(), p2.getX() and p.getX() are the same
			if (Math.abs(a2) > Tools.EPSILON) {
				t2 = c2 / a2;
				if ((-Tools.EPSILON < t2) && (t2 < 1 + Tools.EPSILON)) {
					isInside = true;
				}
			} else if (Math.abs(c2) < Tools.EPSILON) {
				// p1.getY(), p2.getY() and p.getY() are also the same
				isInside = true;
			}

		}

		return isInside;
	}

	/**
	 * check if the point is colinear to the edge in the XY plane, ie if it lies
	 * on the line defined by this edge.
	 *
	 * @param p
	 * @return isColinear2D
	 */
	public final boolean isColinear2D(Point p) {
		boolean isColinear2D = false;

		Point p1 = this.startPoint;
		Point p2 = this.endPoint;

		double a1 = p2.getX() - p1.getX();
		double c1 = p.getX() - p1.getX();
		double a2 = p2.getY() - p1.getY();
		double c2 = p.getY() - p1.getY();
		double t = a1 * c2 - a2 * c1;
		if (Math.abs(t) < Tools.EPSILON2) {
			isColinear2D = true;
		}

		return isColinear2D;
	}

	/**
	 * check if the point is colinear to the edge
	 *
	 * @param p
	 * @return isColinear2D
	 */
	public final boolean isColinear(Point p) {
		boolean isColinear = false;

		Point p1 = this.startPoint;
		Point p2 = this.endPoint;

		double a1 = p2.getX() - p1.getX();
		double c1 = p.getX() - p1.getX();
		double a2 = p2.getY() - p1.getY();
		double c2 = p.getY() - p1.getY();
		double a3 = p2.getZ() - p1.getZ();
		double c3 = p.getZ() - p1.getZ();
		double t1 = a1 * c2 - a2 * c1;
		double t2 = a1 * c3 - a3 * c1;
		double t3 = a3 * c2 - a2 * c3;
		if ((Math.abs(t1) < Tools.EPSILON2) && (Math.abs(t2) < Tools.EPSILON2) && (Math.abs(t3) < Tools.EPSILON2)) {
			isColinear = true;
		}

		return isColinear;
	}

	/**
	 * Check if two edges have the same points.
	 * @param anEdge
	 * @return True if points are the same.
	 */
	public final boolean haveSamePoint(Edge anEdge) {
		return (getStartPoint().equals(anEdge.getStartPoint())
			&& getEndPoint().equals(anEdge.getEndPoint()))
			|| (getStartPoint().equals(anEdge.getEndPoint())
			&& getEndPoint().equals(anEdge.getStartPoint()));
	}

	/**
	 * check if the point is one of the extremities of the edge (on the
	 * xy-plane)
	 *
	 * @param p
	 * @return isInside
	 */
	public final boolean isExtremity(Point p) {
		return (startPoint.equals2D(p) ) || (endPoint.equals2D(p) );
	}

	/**
	 * Check if the point p is on the left
	 *
	 * @param p
	 * @return
	 */
	public final boolean isLeft(Point p) {
		double ux = this.endPoint.getX() - this.startPoint.getX();
		double uy = this.endPoint.getY() - this.startPoint.getY();
		double vx = p.getX() - this.startPoint.getX();
		double vy = p.getY() - this.startPoint.getY();

		return ux * vy - uy * vx > 0;
	}

	/**
	 * Check if the point p is on the right
	 *
	 * @param p
	 * @return
	 */
	public final boolean isRight(Point p) {
		double ux = this.endPoint.getX() - this.startPoint.getX();
		double uy = this.endPoint.getY() - this.startPoint.getY();
		double vx = p.getX() - this.startPoint.getX();
		double vy = p.getY() - this.startPoint.getY();

		return ux * vy - uy * vx < 0;
	}

	/**
	 * Check if the point p is on edge.
	 * @param p
	 * @return True if the point is on edge.
	 */
	public final boolean isOnEdge(Point p) {
		Point p1 = this.startPoint;
		Point p2 = this.endPoint;
		double ux = p2.getX() - p1.getX();
		double uy = p2.getY() - p1.getY();
		double vx = p.getX() - p1.getX();
		double vy = p.getY() - p1.getY();
		double res = ux * vy - uy * vx;

		boolean b1 = /* px is in [p1x, p2x]*/ (ux == 0
			? /*p2x == p1x ?*/ (p1.getX() == p.getX()) /* p2x == p1x == px ?*/
			: (ux > 0
			? /*p2x > p1x ?*/ (p1.getX() < p.getX() && p.getX() < p2.getX()) /* p2x > px > p1x ?*/
			: (p2.getX() < p.getX() && p.getX() < p1.getX())));

		boolean b2 = /* py is in [p1y, p2y]*/ (uy == 0
			? /* p2y == p1y ?*/ (p1.getY() == p.getY()) /* p2y == p1y == py ?*/
			: (uy > 0
			? /* p2y > p1y ?*/ (p1.getY() < p.getY() && p.getY() < p2.getY()) /* p2y > py > p1y ?*/
			: (p2.getY() < p.getY() && p.getY() < p1.getY())));

		return res <= Tools.EPSILON2 && res >= -Tools.EPSILON2/* p is on p1, p2 line */

			&& b1	/* p2x < px < p1x ?*/

			&& b2; /* p2y < py < p1y ?*/
	}

	/**
	 * Returns true if the two points of this edge have the same x coordinate.
	 * @return
	 */
	public final boolean isVertical() {
		double dx = (startPoint.getX() - endPoint.getX());
		double delta = (dx < 0 ? -dx : dx);
		return delta < Tools.EPSILON;
	}

	/**
	 * This method retrieves the point that would stand on the line defined by
	 * this edge, and whose absciss is abs.
	 * if this is a vertical edge, the method returns :
	 *	the minimum point if this edge stand on the line x=abs
	 *	null otherwise
	 * @param abs
	 * @return
	 * @throws DelaunayError
	 */
	public final Point getPointFromItsX(double abs) throws DelaunayError {
		double deltaX = (startPoint.getX() - endPoint.getX());
		double dX = (deltaX < 0 ? -deltaX : deltaX);
		double p = (abs - startPoint.getX()) / (endPoint.getX() - startPoint.getX());
		if (dX < Tools.EPSILON) {
			//the edge is vertical
			if (abs == startPoint.getX()) {//x is the absciss of every points in this edge
				//We return the minimum point.
				return getPointLeft();
			} else {//There is not any point of absciss X on this edge.
				return null;
			}
		} else {
			double y = startPoint.getY() + p * (endPoint.getY() - startPoint.getY());
			double z = startPoint.getZ() + p * (endPoint.getZ() - startPoint.getZ());
			return new Point(abs, y, z);
		}
	}

	/**
	 * Swap the 2 points of the edge
	 * also swap connected triangles
	 */
	public final void swap() {
		// swap points
		Point aPoint = this.endPoint;
		this.endPoint = this.startPoint;
		this.startPoint = aPoint;

		// swap triangles
		DelaunayTriangle aTriangle = left;
		left = right;
		right = aTriangle;
	}

	/**
	 * Check if the edge is flat or not
	 *
	 * @return isFlat
	 */
	public final boolean isFlatSlope() {
		boolean isFlat = true;
		if (Math.abs(this.startPoint.getZ() - this.endPoint.getZ()) > Tools.EPSILON) {
			isFlat = false;
		}
		return isFlat;
	}

	/**
	 * Get the barycenter of the Edge.
	 *
	 * @return barycenter point.
	 * @throws DelaunayError 
	 */
	public final Point getBarycenter() throws DelaunayError {
		double x = (this.startPoint.getX() + this.endPoint.getX()) / 2.0;
		double y = (this.startPoint.getY() + this.endPoint.getY()) / 2.0;
		double z = (this.startPoint.getZ() + this.endPoint.getZ()) / 2.0;
		return new Point(x, y, z);
	}

	/**
	 * Two edges are supposed to be equals if the points they are defined by are the
	 * same.
	 * @param other
	 * @return
	 */
	@Override
	public final boolean equals(Object other){
		if (other instanceof Edge){
			Edge otherEdge = (Edge) other;
			return (endPoint.equals(otherEdge.getEnd()) && startPoint.equals(otherEdge.getStart()))
				|| (endPoint.equals(otherEdge.getStart()) && startPoint.equals(otherEdge.getEnd()));
		}else{
			return false;
		}
	}

	/**
	 * Get edge hashCode as min hasCode of its points
	 *
	 * @param p
	 * @return
	 */
	@Override
	public final int hashCode() {
		Point p1 = this.startPoint;
		Point p2 = this.endPoint;
		int hashValue = 0;

		int v1 = p1.hashCode();
		int v2 = p2.hashCode();
		if (v1 < v2) {
			hashValue = v1;
		} else {
			hashValue = v2;
		}

		return hashValue;
	}

	/**
	 * Set the edge color for the JFrame panel
	 *
	 * @param g
	 */
	protected final void setColor(Graphics g) {
		((Graphics2D) g).setStroke(new BasicStroke(1));
		if (getProperty() != 0) {
			g.setColor(Color.red);
			((Graphics2D) g).setStroke(new BasicStroke(2));
		} else if (isLocked()) {
			g.setColor(Color.CYAN);
		} else if (isOutsideMesh()) {
			g.setColor(Color.pink);
		} else {
			g.setColor(Color.black);
		}
	}

	/**
	 * Display the edge in a JPanel
	 * @param g
	 * @param decalageX
	 * @param decalageY
	 * @param minX
	 * @param minY
	 * @param scaleX
	 * @param scaleY
	 */
	protected final void displayObject(Graphics g, int decalageX, int decalageY,
		double minX, double minY, double scaleX, double scaleY) {
		setColor(g);
		g.drawLine((int) ((this.startPoint.getX() - minX) * scaleX + decalageX),
			decalageY + (int) ((this.startPoint.getY() - minY) * scaleY),
			(int) ((this.endPoint.getX() - minX) * scaleX + decalageX), decalageY
			+ (int) ((this.endPoint.getY() - minY) * scaleY)); // coordinate 0 in Y is at bottom of screen
		if (isLocked()) {
			this.startPoint.displayObject(g, decalageX, decalageY, minX, minY, scaleX,
				scaleY);
			this.endPoint.displayObject(g, decalageX, decalageY, minX, minY, scaleX,
				scaleY);
		}
	}

	/**
	 * This method will be used to sort the edges using the following strategy.
	 * If we note leftP and rightP the leftmost and rightmost point of this
	 * this < edge if this.leftP < edge.leftP or (this.leftP == edge.leftP and this.rightP < edge.rightP)
	 * this == edge if this.leftP == edge.leftP and this.rightP == edge.rightP
	 * this > edge otherwise.
	 * @param edge
	 * @return
	 */
	public final int sortLeftRight(Edge edge) {
		Point p1 = getPointLeft();
		Point p2 = edge.getPointLeft();
		int c = p1.compareTo2D(p2);
		if (c == 0) {
			p1 = getPointRight();
			p2 = edge.getPointRight();
			c = p1.compareTo2D(p2);
		}
		return c;
	}

	/**
	 * Realization of Compare. We use sortLeftRight here to sort our edges,
	 * not a vertical sort or something else...
	 * @param edge
	 * @return
	 */
	@Override
	public final int compareTo(Edge edge){
		return sortLeftRight(edge);
	}

	/**
	 * Sort two edges (this and edge, indeed), and sort them according to their intersection point
	 * with the line l of equation x=abs.
	 * if p1 (p2) is the intersection between l and the line defined by this (edge),
	 * this method returns :
	 *  * -1 if p1 < p2 or ( p1 == p2 and this is "under" edge)
	 *  * 0 if p1 == p2 and (this and edge are colinear)
	 *  * 1 if p1 > p2 or (p1 == p2 and edge is under this)
	 * @param edge
	 * @return
	 */
	public final int verticalSort(Edge edge, double abs) throws DelaunayError {
		Point pThis = this.getPointFromItsX(abs);
		Point pEdge = edge.getPointFromItsX(abs);
		if (pThis == null ) {
			throw new DelaunayError("Trying to sort vertical edge, edge : "+this+", abs : "+abs);
		}
		if( pEdge == null){
			throw new DelaunayError("Trying to sort vertical edge, edge : "+edge+", abs : "+abs);
		}
		int c = pThis.compareTo2D(pEdge);
		if (c == 0) {
			if(this.isVertical()){
				c = this.getPointRight().compareTo2D(edge.getPointRight());
			} else if(edge.isVertical()){
				c = edge.getPointRight().compareTo2D(this.getPointRight());
			} else {
				double deltaXT = getPointRight().getX()-getPointLeft().getX();
				double deltaYT = getPointRight().getY()-getPointLeft().getY();
				double deltaXO = edge.getPointRight().getX()-edge.getPointLeft().getX();
				double deltaYO = edge.getPointRight().getY()-edge.getPointLeft().getY();
				double cT = deltaYT / deltaXT;
				double cO = deltaYO / deltaXO;
				if(-Tools.EPSILON < cT - cO && cT - cO < Tools.EPSILON){
					c = getPointRight().compareTo2D(edge.getPointRight());
					if(c==0){
						c = getPointLeft().compareTo2D(edge.getPointLeft());
					}
				} else if(cT < cO){
		//We are in the case where the two edges intersect at the given X-coordinate.
		//The one with the higher determinant has been the lower one until now, unless
		//we are only seeing its lef point.
					c=-1;
				} else {
					c=1;
				}
			}
		}
		return c;
	}

	/**
	 * @return gradient
	 */
	public final int getGradient() {
		int gradient;
		if (getStart().getZ() > getEnd().getZ()) {
			gradient = Edge.DOWNSLOPE;
		} else if (getStart().getZ() < getEnd().getZ()) {
			gradient = Edge.UPSLOPE;
		} else {
			gradient = Edge.FLATSLOPE;
		}
		return gradient;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String toString() {
		return "Edge " + gid + " [Start : " + startPoint + ", End : " + endPoint + "]";
	}
}
