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

public class MyEdge extends MyElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MyPoint startPoint, endPoint;
	private MyTriangle left, right;
	private MyBox aBox;
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

	/**
	 * Initialize data.
	 */
	private void init() {
		this.startPoint = null;
		this.endPoint = null;
		this.left = null;
		this.right = null;
		this.indicator = 0;
//		aBox=null;
	}

	private void updateBox() {
		aBox = new MyBox();
		aBox.alterBox(this.startPoint);
		aBox.alterBox(this.endPoint);
	}

	/**
	 * Generate a new edge.
	 */
	public MyEdge() {
		super();
		init();
	}

	/**
	 * Generate an edge from two points.
	 *
	 * @param startPoint
	 * @param endPoint
	 */
	public MyEdge(MyPoint start, MyPoint end) {
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
	public MyEdge(MyEdge ed) {
		super((MyElement) ed);
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
	public MyEdge(double x, double y, double z, double u, double v, double w) {
		super();
		init();
		try {
			MyPoint p1 = new MyPoint(x, y, z);
			MyPoint p2 = new MyPoint(u, v, w);
			this.startPoint = p1;
			this.endPoint = p2;
		} catch (DelaunayError d) {
			System.err.println("A problem occured while building the points " + d.getMessage());
		}
	}

	/**
	 * @param i
	 * @return If i==0, return startPoint else return endPoint.
	 */
	public MyPoint point(int i) {
		if (i == 0) {
			return this.startPoint;
		} else {
			return this.endPoint;
		}
	}

	/**
	 * @return Triangle at the left of edge.
	 */
	public MyTriangle getLeft() {
		return this.left;
	}

	/**
	 * @return Triangle at the right of edge.
	 */
	public MyTriangle getRight() {
		return this.right;
	}

	/**
	 * Set Triangle at left of edge.
	 * 
	 * @param aTriangle A triangle at left of edge.
	 */
	public void setLeft(MyTriangle aTriangle) {
		this.left = aTriangle;
	}

	/**
	 * Set Triangle at right of edge.
	 * 
	 * @param aTriangle A triangle at right of edge.
	 */
	public void setRight(MyTriangle aTriangle) {
		this.right = aTriangle;
	}

	/**
	 * @return start point of edge.
	 */
	public MyPoint getStart() {
		return this.startPoint;
	}

	/**
	 * @return start point of edge.
	 */
	public MyPoint getStartPoint() {
		return this.startPoint;
	}

	/**
	 * @return end point of edge.
	 */
	public MyPoint getEnd() {
		return this.endPoint;
	}

	/**
	 * @return end point of edge.
	 */
	public MyPoint getEndPoint() {
		return this.endPoint;
	}

	/**
	 * Set edge start point.
	 *
	 * @param p Start point.
	 */
	public void setStart(MyPoint p) {
		if (isUseByPolygon()) {
			p.setUseByPolygon(true);
		}

		this.startPoint = p;
		updateBox();
	}

	/**
	 * Set edge start point.
	 *
	 * @param p Start point.
	 */
	public void setStartPoint(MyPoint p) {
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
	public void setEnd(MyPoint p) {
		if (isUseByPolygon()) {
			p.setUseByPolygon(true);
		}

		this.endPoint = p;
		updateBox();
	}

	/**
	 * Set edge end point.
	 *
	 * @param p End point.
	 */
	public void setEndPoint(MyPoint p) {
		if (isUseByPolygon()) {
			p.setUseByPolygon(true);
		}

		this.endPoint = p;
		updateBox();
	}

	/**
	 * Get the point of this edge that is on the left from the other. 
	 * We use the order relation defined in MyPoint. Consequently, with a vertical
	 * edge, this method return the minimum point, (so the one with the lowest
	 * y).
	 * @return
	 */
	public MyPoint getPointLeft() {
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
	 * We use the order relation defined in MyPoint.
	 * @return
	 */
	public MyPoint getPointRight() {
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
	protected double getSquared2DLength() {
		return startPoint.squareDistance2D(endPoint);
	}

	/**
	 * get 2D length
	 */
	public double get2DLength() {
		return Math.sqrt(getSquared2DLength());
	}

	/**
	 * get squared 3D length
	 */
	protected double getSquared3DLength() {
		return startPoint.squareDistance(endPoint);
	}

	/**
	 * get 3D length
	 */
	public double get3DLength(MyPoint p) {
		return Math.sqrt(getSquared3DLength());
	}

	@Override
	public int getIndicator() {
		return indicator;
	}

	@Override
	public int setIndicator(int indicator) {
		this.indicator = indicator;
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
	public boolean isMarked(int byteNumber) {
		return testBit(6 + byteNumber);
	}

	/**
	 * set the mark of the edge
	 * @param byteNumber
	 * @param marked
	 */
	public void setMarked(int byteNumber, boolean marked) {
		setBit(6 + byteNumber, marked);
	}

	/**
	 * get the mark of the edge
	 * @return marked
	 */
	public boolean isLocked() {
		return testBit(2);
	}

	/**
	 * set the mark of the edge
	 * @param marked
	 */
	public void setLocked(boolean locked) {
		setBit(2, locked);
	}

	/**
	 * check if edge is taken into account in the triangularization
	 * @return outsideMesh
	 */
	public boolean isOutsideMesh() {
		return testBit(1);
	}

	/**
	 * set the edge in the triangularization or not
	 * @param outsideMesh
	 */
	public void setOutsideMesh(boolean outsideMesh) {
		setBit(1, outsideMesh);
	}

	/**
	 * check if edge is a level edge. 
	 * @return levelEdge
	 */
	public boolean isLevelEdge() {
		return testBit(3);
	}

	/**
	 * set if edge is a level edge.
	 * @param levelEdge
	 */
	public void setLevelEdge(boolean levelEdge) {
		setBit(3, levelEdge);
	}

	/**
	 * check if edge is use by a polygon
	 * @return useByPolygon
	 */
	@Override
	public boolean isUseByPolygon() {
		return testBit(4);
	}

	/**
	 * set if edge is use by a polygon.
	 * @param useByPolygon
	 */
	@Override
	public void setUseByPolygon(boolean useByPolygon) {
		setBit(4, useByPolygon);
	}

	/**
	 * check if Z coordinate is use.
	 * @return useZ
	 */
	public boolean isZUse() {
		return testBit(5);
	}

	/**
	 * set if Z coordinate is use.
	 * @param useByPolygon
	 */
	public void setUseZ(boolean useZ) {
		setBit(5, useZ);
	}

	/* (non-Javadoc)
	 * @see org.jdelaunay.delaunay.MyElement#getBoundingBox()
	 */
	@Override
	public MyBox getBoundingBox() {

		MyBox box = new MyBox();
		box.alterBox(this.startPoint);
		box.alterBox(this.endPoint);

		return box;
	}

	/* (non-Javadoc)
	 * @see org.jdelaunay.delaunay.MyElement#contains(org.jdelaunay.delaunay.MyPoint)
	 */
	@Override
	public boolean contains(MyPoint aPoint) {
		return contains(aPoint.getCoordinate());
//		if (intersects(aPoint, aPoint) > 0)
//			return true;
//		else
//			return false;
	}

	@Override
	public boolean contains(Coordinate c) {
		MyPoint p1 = this.startPoint;
		MyPoint p2 = this.endPoint;
		double ux = p2.getX() - p1.getX();
		double uy = p2.getY() - p1.getY();
		double vx = c.x - p1.getX();
		double vy = c.y - p1.getY();
		double res = ux * vy - uy * vx;
		boolean px = (ux >= 0 ? (p1.getX() <= c.x && c.x <= p2.getX()) : (p2.getX() <= c.x && c.x <= p1.getX()));/* px is in [p1x, p2x]*/
		boolean py = (uy >= 0 ? (p1.getY() <= c.y && c.y <= p2.getY()) : (p2.getY() <= c.y && c.y <= p1.getY()));/* py is in [p1y, p2y]*/
		return res <= MyTools.EPSILON && res >= -MyTools.EPSILON/* p is on p1, p2 line */
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
	public int intersects(MyEdge other) throws DelaunayError{
		return intersects(other.getStart(), other.getEnd());
	}

	/**
	 * check if two edges intersect
	 *
	 * @param p1 the start point of the other edge
	 * @param p2 the end point of the other edge
	 * @return intersection :<br/>
	 * 			0 = no intersection<br/>
	 * 			1 = intersect<br/>
	 * 			2 = co-linear and don't intersect<br/>
	 * 			3 = intersects at the extremity<br/>
	 *			4 = intersect in more than one point<br/>
	 * note that if on extremity of an edge lies inside the other edge, but
	 * is not one of the extremities of the other edge, this method
	 * returns 1
	 */
	public int intersects(MyPoint p1, MyPoint p2) throws DelaunayError {
		int result = 0;
		MyPoint p3 = this.startPoint;
		MyPoint p4 = this.endPoint;
		MyElement inter = getIntersection(p1, p2, false);
		if(inter==null){//there is no intersection, return 0 or 2
			// (x2 - x1) t1 - (x4 - x3) t2 = (x3 - x1)
			// (y2 - y1) t1 - (y4 - y3) t2 = (y3 - y1)
			double a1 = p2.getX() - p1.getX();
			double b1 = p4.getX() - p3.getX();
			double a2 = p2.getY() - p1.getY();
			double b2 = p4.getY() - p3.getY();

			// d = (x4 - x3) (y2 - y1) - (x2 - x1) * (y4 - y3)
			double d = b1 * a2 - b2 * a1;
			if(-MyTools.EPSILON2 < d && d < MyTools.EPSILON2){
				//the two edges are colinear
				return 2;
			}else {
				return 0;
			}
		} else if(inter instanceof MyPoint){
			//intersection in one point,
			//return 1 or 3
			MyPoint interPoint = (MyPoint) inter;
			if((interPoint.squareDistance2D(p1)<MyTools.EPSILON2 ||
				interPoint.squareDistance2D(p2)<MyTools.EPSILON2)&&
				(interPoint.squareDistance2D(p3)<MyTools.EPSILON2||
				interPoint.squareDistance2D(p4)<MyTools.EPSILON2)){
				//intersection at an extremity of each edge.
				return 3;
			} else {
				return 1;
			}
			
		} else if(inter instanceof MyEdge){
			//intersection in more than
			//one point, return 4
			return 4;
		}
		return result;
	}

	/**
	 * intersects two edges returns null if there is no intersection
	 *
	 * @param p1
	 * @param p2
	 * @return intersection
	 * @throws DelaunayError 
	 */
	public MyElement getIntersection(MyPoint p1, MyPoint p2) throws DelaunayError {
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
	public MyElement getIntersection(MyPoint point1, MyPoint point2, boolean useCoordZOfp1p2) throws DelaunayError {
		MyElement intersection = null;
		MyPoint p3 = getPointLeft();
		MyPoint p4 = getPointRight();
		MyPoint p1 = new MyPoint();
		MyPoint p2 = new MyPoint();
		switch(point1.compareTo2D(point2)){
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
		if (Math.abs(d) > MyTools.EPSILON2) {
			// t1 = ((y3 - y1) (x4 - x3) - (x3 - x1) (y4 - y3)) / d
			// t2 = ((x2 - x1) (y3 - y1) - (y2 - y1) (x3 - x1)) / d

			double t1 = (c2 * deltaXT - c1 * deltaYT) / d;
			double t2 = (deltaXO * c2 - deltaYO * c1) / d;

			if ((-MyTools.EPSILON <= t1) && (t1 <= 1 + MyTools.EPSILON) && (-MyTools.EPSILON <= t2)
				&& (t2 <= 1 + MyTools.EPSILON)) {
				// it intersects
				if (t2 <= MyTools.EPSILON) {
					intersection = p3;
				} else if (t2 >= 1 - MyTools.EPSILON) {
					intersection = p4;
				} else if (t1 <= MyTools.EPSILON) {
					intersection = p1;
				} else if (t1 >= 1 - MyTools.EPSILON) {
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
					intersection = new MyPoint(x, y, z);

				}
			}
		} else { //d==0 : the two edges are colinear
			double test;
			if (Math.abs(deltaXO) < MyTools.EPSILON) {
				test = c1 / deltaXT - c2 / deltaYT;
			} else {
				test = c1 / deltaXO - c2 / deltaYO;
			}
			if (Math.abs(test) > MyTools.EPSILON) {//the two supporting lines are different
				intersection = null;
			} else {//we have one supporting line
				//t13 is the position of the point three on the edge 1->2
				double t13, t14, t21, t22;
				if (Math.abs(deltaXO) < MyTools.EPSILON) {
					t13 = c2 / deltaYO;
					t14 = (p4.getY() - p1.getY()) / (deltaYO);
				} else {
					t13 = c1 / deltaXO;
					t14 = (p4.getX() - p1.getX()) / (deltaXO);
				}
				if (Math.abs(deltaXT) > MyTools.EPSILON) {
					t21 = -c1 / deltaXT;
					t22 = (p2.getX() - p3.getX()) / deltaXT;
				} else {
					t21 = -c2 / deltaYT;
					t22 = (p2.getY() - p3.getY()) / (deltaYT);
				}
				if (-MyTools.EPSILON < t13 && t13 < 1 + MyTools.EPSILON) {
					if (-MyTools.EPSILON < t14 && t14 < 1 + MyTools.EPSILON) {
                                                //p3 and p4 are both on the edge [p1 p2]
						intersection = new MyEdge(p3, p4);
					} else {
                                                //p4 is not on [p1 p2]
						if (p3.squareDistance2D(p1) < MyTools.EPSILON2) {
                                                        //p3 and p1 are equal
							if (-MyTools.EPSILON < t22 && t22 < 1 + MyTools.EPSILON) {
                                                                //p2 is on [p3 p4]
								intersection = new MyEdge(p1, p2);
							} else {
                                                                //p2 is not on [p3 p4], and p3 is not on [p1 p2]
								intersection = p3;
							}
						} else if (p3.squareDistance2D(p2) < MyTools.EPSILON2) {
                                                        //p3 and p2 are equals
							if (-MyTools.EPSILON < t21 && t21 < 1 + MyTools.EPSILON) {
                                                                //p1 is on [p3 p4]
								intersection = new MyEdge(p1, p2);
							} else {
                                                                //p1 is not on [p3 p4], and p3 is not on [p1 p2]
								intersection = p3;
							}

						} else if (-MyTools.EPSILON < t21 && t21 < 1 + MyTools.EPSILON) {
                                                        //p1 is on [p3 p4]
							intersection = new MyEdge(p1, p3);
						} else {
							intersection = new MyEdge(p2, p3);
						}
					}
				} else if (-MyTools.EPSILON < t14 && t14 < 1 + MyTools.EPSILON) {
                                //p3 is not on [p1 p2], but p4 is on it
					if (p4.squareDistance2D(p1) < MyTools.EPSILON2) {
                                                //p4 and p1 are equal
						if (-MyTools.EPSILON < t22 && t22 < 1 + MyTools.EPSILON) {
                                                        //p2 is on [p3 p4]
							intersection = new MyEdge(p1, p2);
						} else {
                                                        //p2 is not on [p3 p4] and p3 is not on [p1 p2]
							intersection = p4;
						}
					} else if (p4.squareDistance2D(p2) < MyTools.EPSILON2) {
                                                //p4 and p1 are equal
						if (-MyTools.EPSILON < t21 && t21 < 1 + MyTools.EPSILON) {
                                                        //p1  is on [p3 p4]
							intersection = new MyEdge(p1, p2);
						} else {
                                                        //p1 is not on [p3 p4] and p3 is not on [p1 p2]
							intersection = p4;
						}

					} else if (-MyTools.EPSILON < t21 && t21 < 1 + MyTools.EPSILON) {
                                                //p1 is on [p3 p4]
						intersection = new MyEdge(p1, p4);
					} else {
						intersection = new MyEdge(p2, p4);
					}
				} else if (MyTools.EPSILON < t21 && t21 < 1 - MyTools.EPSILON) {
                                        //p1 is on [p3 p4]. As we've seen, nor p4 neither p3 are
                                        // on [p1 p2], so we can conclude that the intersection is [p1 p2]
					intersection = new MyEdge(p1, p2);
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
	public MyElement getIntersection(MyEdge anEdge) throws DelaunayError {
		return getIntersection(anEdge.startPoint, anEdge.endPoint);
	}

	/**
	 * @param p
	 * @return Z coordinate of point p on the edge.
	 */
	public double getZOnEdge(MyPoint p) {
		MyPoint p1 = this.startPoint;
		MyPoint p2 = this.endPoint;

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
	public boolean isInside(MyPoint p) {
		boolean isInside = false;

		MyPoint p1 = this.startPoint;
		MyPoint p2 = this.endPoint;

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

		if (Math.abs(a1) > MyTools.EPSILON) {
			t1 = c1 / a1;
			if ((-MyTools.EPSILON < t1) && (t1 < 1 + MyTools.EPSILON)) {
				// p.getX() is between p1.getX() and p2.getX()
				if (Math.abs(a2) > MyTools.EPSILON) {
					t2 = c2 / a2;
					if ((-MyTools.EPSILON < t2) && (t2 < 1 + MyTools.EPSILON)
						&& (Math.abs(t1 - t2) < MyTools.EPSILON)) {
						isInside = true;
					}
				} else if (Math.abs(c2) < MyTools.EPSILON) {
					// p1.getY(), p2.getY() and p.getY() are the same
					isInside = true;
				}
			}
		} else if (Math.abs(c1) < MyTools.EPSILON) {
			// p1.getX(), p2.getX() and p.getX() are the same
			if (Math.abs(a2) > MyTools.EPSILON) {
				t2 = c2 / a2;
				if ((-MyTools.EPSILON < t2) && (t2 < 1 + MyTools.EPSILON)) {
					isInside = true;
				}
			} else if (Math.abs(c2) < MyTools.EPSILON) {
				// p1.getY(), p2.getY() and p.getY() are also the same
				isInside = true;
			}

		}

		return isInside;
	}

	/**
	 * check if the point is colinear to the edge in the XY plane
	 *
	 * @param p
	 * @return isColinear2D
	 */
	public boolean isColinear2D(MyPoint p) {
		boolean isColinear2D = false;

		MyPoint p1 = this.startPoint;
		MyPoint p2 = this.endPoint;

		double a1 = p2.getX() - p1.getX();
		double c1 = p.getX() - p1.getX();
		double a2 = p2.getY() - p1.getY();
		double c2 = p.getY() - p1.getY();
		double t = a1 * c2 - a2 * c1;
		if (Math.abs(t) < MyTools.EPSILON2) {
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
	public boolean isColinear(MyPoint p) {
		boolean isColinear = false;

		MyPoint p1 = this.startPoint;
		MyPoint p2 = this.endPoint;

		double a1 = p2.getX() - p1.getX();
		double c1 = p.getX() - p1.getX();
		double a2 = p2.getY() - p1.getY();
		double c2 = p.getY() - p1.getY();
		double a3 = p2.getZ() - p1.getZ();
		double c3 = p.getZ() - p1.getZ();
		double t1 = a1 * c2 - a2 * c1;
		double t2 = a1 * c3 - a3 * c1;
		double t3 = a3 * c2 - a2 * c3;
		if ((Math.abs(t1) < MyTools.EPSILON2) && (Math.abs(t2) < MyTools.EPSILON2) && (Math.abs(t3) < MyTools.EPSILON2)) {
			isColinear = true;
		}

		return isColinear;
	}

	/**
	 * Check if two edges have the same points.
	 * @param anEdge
	 * @return True if points are the same.
	 */
	public boolean haveSamePoint(MyEdge anEdge) {
		return (getStartPoint().equals(anEdge.getStartPoint())
			&& getEndPoint().equals(anEdge.getEndPoint()))
			|| (getStartPoint().equals(anEdge.getEndPoint())
			&& getEndPoint().equals(anEdge.getStartPoint()));
	}

	/**
	 * Check if two edges have the same points.
	 * @param anEdge
	 * @return True if points are the same.
	 */
	public boolean haveSamePoint(MyPoint p1, MyPoint p2) {
		return (getStartPoint().getCoordinate().equals(p1.getCoordinate())
			&& getEndPoint().getCoordinate().equals(p2.getCoordinate()))
			|| (getStartPoint().getCoordinate().equals(p2.getCoordinate())
			&& getEndPoint().getCoordinate().equals(p1.getCoordinate()));
	}

	/**
	 * check if the point is one of the extremities of the edge (on the
	 * xy-plane)
	 *
	 * @param p
	 * @return isInside
	 */
	public boolean isExtremity(MyPoint p) {
		return (startPoint.equals2D(p) ) || (endPoint.equals2D(p) );
	}

	/**
	 * Check if the point p is on the left
	 *
	 * @param p
	 * @return
	 */
	public boolean isLeft(MyPoint p) {
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
	public boolean isRight(MyPoint p) {
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
	public boolean isOnEdge(MyPoint p) {
		MyPoint p1 = this.startPoint;
		MyPoint p2 = this.endPoint;
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

		return res <= MyTools.EPSILON2 && res >= -MyTools.EPSILON2/* p is on p1, p2 line */

			&& b1	/* p2x < px < p1x ?*/

			&& b2; /* p2y < py < p1y ?*/
	}

	/**
	 * Returns true if the two points of this edge have the same x coordinate.
	 * @return
	 */
	public boolean isVertical() {
		double dx = (startPoint.getX() - endPoint.getX());
		double delta = (dx < 0 ? -dx : dx);
		return delta < MyTools.EPSILON;
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
	public MyPoint getPointFromItsX(double abs) throws DelaunayError {
		double deltaX = (startPoint.getX() - endPoint.getX());
		double dX = (deltaX < 0 ? -deltaX : deltaX);
		double p = (abs - startPoint.getX()) / (endPoint.getX() - startPoint.getX());
		if (dX < MyTools.EPSILON) {
			//the edge is vertical
			double delta = startPoint.getX() - abs;
			dX = ((delta < 0) ? -delta : delta);
			if (abs == startPoint.getX()) {//x is the absciss of every points in this edge
				//We return the minimum point.
				return ((endPoint.compareTo2D(startPoint) == -1) ? endPoint : startPoint);
			} else {//There is not any point of absciss X on this edge.
				return null;
			}
		} else {
			double y = startPoint.getY() + p * (endPoint.getY() - startPoint.getY());
			double z = startPoint.getZ() + p * (endPoint.getZ() - startPoint.getZ());
			return new MyPoint(abs, y, z);
		}
	}

	/**
	 * Swap the 2 points of the edge
	 * also swap connected triangles
	 */
	public void swap() {
		// swap points
		MyPoint aPoint = this.endPoint;
		this.endPoint = this.startPoint;
		this.startPoint = aPoint;

		// swap triangles
		MyTriangle aTriangle = left;
		left = right;
		right = aTriangle;
	}

	/**
	 * Check if the edge is flat or not
	 *
	 * @return isFlat
	 */
	public boolean isFlatSlope() {
		boolean isFlat = true;
		if (Math.abs(this.startPoint.getZ() - this.endPoint.getZ()) > MyTools.EPSILON) {
			isFlat = false;
		}
		return isFlat;
	}

	/**
	 * Get the barycenter of the triangle.
	 *
	 * @return barycenter point.
	 * @throws DelaunayError 
	 */
	public MyPoint getBarycenter() throws DelaunayError {
		double x = (this.startPoint.getX() + this.endPoint.getX()) / 2.0;
		double y = (this.startPoint.getY() + this.endPoint.getY()) / 2.0;
		double z = (this.startPoint.getZ() + this.endPoint.getZ()) / 2.0;
		return new MyPoint(x, y, z);
	}

	/**
	 * Two edges are supposed to be equals if the points they are defined by are the
	 * same.
	 * @param other
	 * @return
	 */
	@Override
	public boolean equals(Object other){
		if (other instanceof MyEdge){
			MyEdge otherEdge = (MyEdge) other;
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
	public int hashCode() {
		MyPoint p1 = this.startPoint;
		MyPoint p2 = this.endPoint;
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
	protected void setColor(Graphics g) {
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
	protected void displayObject(Graphics g, int decalageX, int decalageY,
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
	public int sortLeftRight(MyEdge edge) {
		MyPoint p1 = getPointLeft();
		MyPoint p2 = edge.getPointLeft();
		int c = p1.compareTo2D(p2);
		if (c == 0) {
			p1 = getPointRight();
			p2 = edge.getPointRight();
			c = p1.compareTo2D(p2);
		}
		return c;
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
	public int verticalSort(MyEdge edge, double abs) throws DelaunayError {
		MyPoint pThis = this.getPointFromItsX(abs);
		MyPoint pEdge = edge.getPointFromItsX(abs);
		if (pThis == null || pEdge == null) {
			throw new DelaunayError("You shouldn't try to sort vertical edges where x != abs !!!!");
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
				if(-MyTools.EPSILON < cT - cO && cT - cO < MyTools.EPSILON){
					c = getPointRight().compareTo2D(edge.getPointRight());
					if(c==0){
						c = getPointLeft().compareTo2D(edge.getPointLeft());
					}
				} else if(cT < cO){
					c = -1;
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
	public int getGradient() {
		int gradient;
		if (getStart().getZ() > getEnd().getZ()) {
			gradient = MyEdge.DOWNSLOPE;
		} else if (getStart().getZ() < getEnd().getZ()) {
			gradient = MyEdge.UPSLOPE;
		} else {
			gradient = MyEdge.FLATSLOPE;
		}
		return gradient;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Edge " + gid + " [" + startPoint + ", " + endPoint + "]";
	}
}
