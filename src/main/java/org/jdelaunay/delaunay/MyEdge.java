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
import java.io.Serializable;

import com.vividsolutions.jts.geom.Coordinate;

public class MyEdge extends MyElement implements Serializable {

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
	
	private void updateBox()
	{
		aBox=new MyBox();
		aBox.alterBox( this.startPoint);
		aBox.alterBox( this.endPoint);
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
		super((MyElement)ed);
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
	public MyEdge(double x, double y , double z, double u, double v, double w){
		super();
		init();
		try{
			MyPoint p1 = new MyPoint(x,y,z);
			MyPoint p2 = new MyPoint(u,v,w);
			this.startPoint = p1;
			this.endPoint = p2;
		}catch (DelaunayError d){
			System.out.println("A problem occured while building the points "+d.getMessage());
		}
	}

	/**
	 * @param i
	 * @return If i==0, return startPoint else return endPoint.
	 */
	public MyPoint point(int i) {
		if (i == 0)
			return this.startPoint;
		else
			return this.endPoint;
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
		this.left=aTriangle;
	}

	/**
	 * Set Triangle at right of edge.
	 * 
	 * @param aTriangle A triangle at right of edge.
	 */
	public void setRight(MyTriangle aTriangle) {
		this.right=aTriangle;
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
		if(isUseByPolygon())
			p.setUseByPolygon(true);
		
		this.startPoint = p;
		updateBox();
	}

	/**
	 * Set edge start point.
	 *
	 * @param p Start point.
	 */
	public void setStartPoint(MyPoint p) {
		if(isUseByPolygon())
			p.setUseByPolygon(true);
		
		this.startPoint = p;
		updateBox();
	}
	
	/**
	 * Set edge end point.
	 *
	 * @param p End point.
	 */
	public void setEnd(MyPoint p) {
		if(isUseByPolygon())
			p.setUseByPolygon(true);
		
		this.endPoint = p;
		updateBox();
	}

	/**
	 * Set edge end point.
	 *
	 * @param p End point.
	 */
	public void setEndPoint(MyPoint p) {
		if(isUseByPolygon())
			p.setUseByPolygon(true);
		
		this.endPoint = p;
		updateBox();
	}

	/**
	 * Get the point of this edge that is on the left from the other. 
	 * We use the order relation defined in MyPoint.
	 * @return
	 */
	public MyPoint getPointLeft(){
		int c = endPoint.compareTo2D(startPoint);
		switch(c){
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
	public MyPoint getPointRight(){
		int c = endPoint.compareTo2D(startPoint);
		switch(c){
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
		return startPoint.squareDistance_2D(endPoint);
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
		if (value)
			this.indicator = (this.indicator | test);
		else
			this.indicator = (this.indicator | test) - test;
		
		startPoint.setMarkedByEdge(byteNumber, value);
		endPoint.setMarkedByEdge(byteNumber, value);
	}
	
	/**
	 * get the mark of the edge
	 * @param byteNumber
	 * @return marked
	 */
	public boolean isMarked(int byteNumber) {
		return testBit(6+byteNumber);
	}

	/**
	 * set the mark of the edge
	 * @param byteNumber
	 * @param marked
	 */
	public void setMarked(int byteNumber, boolean marked) {
		setBit(6+byteNumber, marked);
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
	public boolean isLevelEdge(){
		return testBit(3);
	}
	
	/**
	 * set if edge is a level edge.
	 * @param levelEdge
	 */
	public void setLevelEdge(boolean levelEdge){
		setBit(3, levelEdge);
	}
	
	/**
	 * check if edge is use by a polygon
	 * @return useByPolygon
	 */
	@Override
	public boolean isUseByPolygon(){
		return testBit(4);
	}
	
	/**
	 * set if edge is use by a polygon.
	 * @param useByPolygon
	 */
	@Override
	public void setUseByPolygon(boolean useByPolygon){
		setBit(4, useByPolygon);
	}

	
	/**
	 * check if Z coordinate is use.
	 * @return useZ
	 */
	public boolean isZUse(){
		return testBit(5);
	}
	
	/**
	 * set if Z coordinate is use.
	 * @param useByPolygon
	 */
	public void setUseZ(boolean useZ){
		setBit(5, useZ);
	}
	
	
	/* (non-Javadoc)
	 * @see org.jdelaunay.delaunay.MyElement#getBoundingBox()
	 */
	@Override
	public MyBox getBoundingBox() {
		
		MyBox box=new MyBox();
		box.alterBox( this.startPoint);
		box.alterBox( this.endPoint);
		
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
		return 	res <= MyTools.epsilon && res >= - MyTools.epsilon/* p is on p1, p2 line */ 
		&&(ux>=0?(p1.getX()<=c.x && c.x<= p2.getX()):(p2.getX()<=c.x && c.x<= p1.getX())) /* px is in [p1x, p2x]*/
		&&(uy>=0?(p1.getY()<=c.y && c.y<= p2.getY()):(p2.getY()<=c.y && c.y<= p1.getY()));/* py is in [p1y, p2y]*/
	}
	
	/**
	 * check if two edges intersects
	 *
	 * @param p1
	 * @param p2
	 * @return intersection :<br/>
	 * 			0 = no intersection<br/>
	 * 			1 = intersects<br/>
	 * 			2 = co-linear<br/>
	 * 			3 = intersects at the extremity
	 */
	public int intersects(MyPoint p1, MyPoint p2) {
		int result = 0;
		MyPoint p3 = this.startPoint;
		MyPoint p4 = this.endPoint;
		// (x2 - x1) t1 - (x4 - x3) t2 = (x3 - x1)
		// (y2 - y1) t1 - (y4 - y3) t2 = (y3 - y1)

		double a1 = p2.getX() - p1.getX();
		double b1 = p4.getX() - p3.getX();
		double c1 = p3.getX() - p1.getX();
		double a2 = p2.getY() - p1.getY();
		double b2 = p4.getY() - p3.getY();
		double c2 = p3.getY() - p1.getY();
		double t1, t2;
		double epsilon = MyTools.epsilon;

		// d = (x4 - x3) (y2 - y1) - (x2 - x1) * (y4 - y3)
		double d = b1 * a2 - b2 * a1;
		if (d != 0) {
			// t1 = ((y3 - y1) (x4 - x3) - (x3 - x1) (y4 - y3)) / d
			// t2 = ((x2 - x1) (y3 - y1) - (y2 - y1) (x3 - x1)) / d

			t1 = (c2 * b1 - c1 * b2) / d;
			t2 = (a1 * c2 - a2 * c1) / d;

			if ((-epsilon <= t1) && (t1 <= 1 + epsilon) && (-epsilon <= t2)
					&& (t2 <= 1 + epsilon))
				if (((-epsilon <= t1) && (t1 <= epsilon))
						|| ((1 - epsilon <= t1) && (t1 <= 1 + epsilon)))
					result = 3;//intersects at the extremity
				else
					result = 1;//intersects

		} else {
			// Check if p3 is between p1 and p2
			if (Math.abs(a1) > epsilon)
				t1 = (c1) / (a1);
			else
				t1 = (c2) / (a2);

			if ((-epsilon > t1) || (t1 > 1 + epsilon)) {
				// Check if p4 is between p1 and p2
				if (Math.abs(p2.getX() - p1.getX()) > epsilon)
					t1 = (p4.getX() - p1.getX()) / (p2.getX() - p1.getX());
				else
					t1 = (p4.getY() - p1.getY()) / (p2.getY() - p1.getY());

				if ((-epsilon > t1) || (t1 > 1 + epsilon)) {
					// Check if p1 is between p3 and p4
					if (Math.abs(p4.getX() - p3.getX()) > epsilon)
						t1 = (p1.getX() - p3.getX()) / (p4.getX() - p3.getX());
					else
						t1 = (p1.getY() - p3.getY()) / (p4.getY() - p3.getY());

					if ((-epsilon > t1) || (t1 > 1 + epsilon))
						// we do not check for p2 because it is now impossible
						result = 0;//no intersection
					else
					{
						result = 2;//co-linear
					}
				} else
				{
					result = 2;//co-linear
				}

			} else
			{
				result = 2;//co-linear
			}
			
			if(result==2)
			{
					if(isOnEdge(p1) || isOnEdge(p2) || new MyEdge(p1, p2).isOnEdge(p3) || new MyEdge(p1, p2).isOnEdge(p4))
					{
						result=1;//co-linear and intersects
					}
					
			}
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
	public MyPoint getIntersection(MyPoint p1, MyPoint p2) throws DelaunayError {
		return getIntersection(p1, p2, false);
	}
	
	/**
	 * intersects two edges returns null if there is no intersection
	 *
	 * @param p1
	 * @param p2
	 * @param useCoordZOfp1p2 If true, the coordinate of intersection get in Z the average of p1 and p2 Z. Don't care of p3 and, p4 Z.
	 * Else if false, the coordinate of intersection get in Z the average of p1, p2, p3 and p4 Z.
	 * @return intersection
	 * @throws DelaunayError 
	 */
	public MyPoint getIntersection(MyPoint p1, MyPoint p2, boolean useCoordZOfp1p2) throws DelaunayError {
		MyPoint intersection = null;
		MyPoint p3 = this.startPoint;
		MyPoint p4 = this.endPoint;

		// (x2 - x1) t1 - (x4 - x3) t2 = (x3 - x1)
		// (y2 - y1) t1 - (y4 - y3) t2 = (y3 - y1)

		double a1 = p2.getX() - p1.getX();
		double b1 = p4.getX() - p3.getX();
		double c1 = p3.getX() - p1.getX();
		double a2 = p2.getY() - p1.getY();
		double b2 = p4.getY() - p3.getY();
		double c2 = p3.getY() - p1.getY();
		double epsilon = MyTools.epsilon;

		// d = (x4 - x3) (y2 - y1) - (x2 - x1) * (y4 - y3)
		double d = b1 * a2 - b2 * a1;
		if (d != 0) {
			// t1 = ((y3 - y1) (x4 - x3) - (x3 - x1) (y4 - y3)) / d
			// t2 = ((x2 - x1) (y3 - y1) - (y2 - y1) (x3 - x1)) / d

			double t1 = (c2 * b1 - c1 * b2) / d;
			double t2 = (a1 * c2 - a2 * c1) / d;

			if ((-epsilon <= t1) && (t1 <= 1 + epsilon) && (-epsilon <= t2)
					&& (t2 <= 1 + epsilon)) {
				// it intersects
				if (t2 <= epsilon)
					intersection = p3;
				else if (t2 >= 1 - epsilon)
					intersection = p4;
				else if (t1 <= epsilon)
					intersection = p1;
				else if (t1 >= 1 - epsilon)
					intersection = p2;
				else {
					// x = x2 t1 + (1 - t1) x1
					// y = y2 t1 + (1 - t1) y1
					// z = z2 t1 + (1 - t1) z1
					// z = z4 t2 + (1 - t2) z3
					double x = p2.getX() * t1 + (1 - t1) * p1.getX();
					double y = p2.getY() * t1 + (1 - t1) * p1.getY();
					

					double z=0;
					if(useCoordZOfp1p2)
					{
						// Average of p1 and p2 Z. Don't care of p3 and p4 Z.
						z = p2.getZ() * t1 + (1 - t1) * p1.getZ();
					}else
					{
						// Average of p1, p2, p3 and p4 Z.
						z= p4.getZ() * t2 + (1 - t2) * p3.getZ();
					}
					intersection = new MyPoint(x, y, z);

					// Last verification
					if (p1.squareDistance_2D(intersection) < epsilon)
						intersection = p1;
					else if (p2.squareDistance_2D(intersection) < epsilon)
						intersection = p2;
					else if (p3.squareDistance_2D(intersection) < epsilon)
						intersection = p3;
					else if (p4.squareDistance_2D(intersection) < epsilon)
						intersection = p4;
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
	public MyPoint getIntersection(MyEdge anEdge) throws DelaunayError {
		return getIntersection(anEdge.startPoint, anEdge.endPoint);
	}


	
	/**
	 * @param p
	 * @return Z coordinate of point p on the edge.
	 */
	public double getZOnEdge(MyPoint p)
	{
		MyPoint p1 = this.startPoint;
		MyPoint p2 = this.endPoint;

		if(p2.getX()== p1.getX())
		{
			return (( p2.getX() - p.getX() )  * (p2.getZ() - p1.getZ())) / (p2.getX() - p1.getX()) ;
		}
		
		if(p2.getY()== p1.getY())
		{
			return (( p2.getY() - p.getY() )  * (p2.getZ() - p1.getZ())) / (p2.getY() - p1.getY()) ;
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
		double epsilon = MyTools.epsilon;

		if (Math.abs(a1) > epsilon) {
			t1 = c1 / a1;
			if ((-epsilon < t1) && (t1 < 1 + epsilon)) {
				// p.getX() is between p1.getX() and p2.getX()
				if (Math.abs(a2) > epsilon) {
					t2 = c2 / a2;
					if ((-epsilon < t2) && (t2 < 1 + epsilon)
							&& (Math.abs(t1 - t2) < epsilon))
						// same t value => ok
						isInside = true;
				} else if (Math.abs(c2) < epsilon) {
					// p1.getY(), p2.getY() and p.getY() are the same
					isInside = true;
				}
			}
		} else if (Math.abs(c1) < epsilon) {
			// p1.getX(), p2.getX() and p.getX() are the same
			if (Math.abs(a2) > epsilon) {
				t2 = c2 / a2;
				if ((-epsilon < t2) && (t2 < 1 + epsilon))
					isInside = true;
			} else if (Math.abs(c2) < epsilon) {
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
		double epsilon = MyTools.epsilon;
		double t = a1*c2 - a2*c1;
		if (Math.abs(t) < epsilon)
			isColinear2D = true;
		
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
		double epsilon = MyTools.epsilon;
		double t1 = a1*c2 - a2*c1;
		double t2 = a1*c3 - a3*c1;
		double t3 = a3*c2 - a2*c3;
		if ((Math.abs(t1) < epsilon) && (Math.abs(t2) < epsilon) && (Math.abs(t3) < epsilon))
			isColinear = true;

		return isColinear;
	}
	
	
	
	/**
	 * Check if two edges have the same points.
	 * @param anEdge
	 * @return True if points are the same.
	 */
	public boolean haveSamePoint(MyEdge anEdge)
	{
		return ( getStartPoint().equals(anEdge.getStartPoint()) &&
				 getEndPoint().equals(anEdge.getEndPoint()))
				|| ( getStartPoint().equals(anEdge.getEndPoint()) &&
				 getEndPoint().equals(anEdge.getStartPoint()) );
	}
	
	
	/**
	 * Check if two edges have the same points.
	 * @param anEdge
	 * @return True if points are the same.
	 */
	public boolean haveSamePoint(MyPoint p1, MyPoint p2)
	{
		return ( getStartPoint().getCoordinate().equals(p1.getCoordinate()) &&
				 getEndPoint().getCoordinate().equals(p2.getCoordinate())
				)
				||
				( getStartPoint().getCoordinate().equals(p2.getCoordinate()) &&
				 getEndPoint().getCoordinate().equals(p1.getCoordinate()) );
	}

	/**
	 * check if the point is one of the extremities of the edge (on the
	 * xy-plane)
	 *
	 * @param p
	 * @return isInside
	 */
	public boolean isExtremity(MyPoint p) {
		boolean isExtremity = false;

		if (this.startPoint.squareDistance_2D(p) < MyTools.epsilon)
			isExtremity = true;
		else if (this.endPoint.squareDistance_2D(p) < MyTools.epsilon)
			isExtremity = true;
		return isExtremity;
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
		
		return 	res <= MyTools.epsilon && res >= - MyTools.epsilon/* p is on p1, p2 line */ 
		&&															/* px is in [p1x, p2x]*/
			(ux==0? 													/*p2x == p1x ?*/
				(p1.getX()==p.getX())									/* p2x == p1x == px ?*/
				:(ux>0? 												/*p2x > p1x ?*/
						(p1.getX()<p.getX() && p.getX()< p2.getX())		/* p2x > px > p1x ?*/
						:(p2.getX()<p.getX() && p.getX()< p1.getX())))	/* p2x < px < p1x ?*/
		&&  														/* py is in [p1y, p2y]*/
			(uy==0? 													/* p2y == p1y ?*/
				(p1.getY()==p.getY())									/* p2y == p1y == py ?*/
				:(uy>0? 												/* p2y > p1y ?*/
						(p1.getY()<p.getY() && p.getY()< p2.getY())		/* p2y > py > p1y ?*/
						:(p2.getY()<p.getY() && p.getY()< p1.getY()))); /* p2y < py < p1y ?*/
	}

	public MyPoint getPointFromItsX(double x) throws DelaunayError{
		double deltaX = (startPoint.getX() - endPoint.getX());
		double deltaY = (startPoint.getY() - endPoint.getY());
		double dX = (deltaX < 0 ? -deltaX : deltaX);
		double p = (x-startPoint.getX())/(endPoint.getX() - startPoint.getX());
		if(dX < MyTools.epsilon ){
			//the edge is vertical
			return endPoint;
		} else {
			double y = startPoint.getY()+p*(endPoint.getY()-startPoint.getY());
			double z = startPoint.getZ()+p*(endPoint.getZ()-startPoint.getZ());
			return new MyPoint(x,y,z);
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
		if (Math.abs(this.startPoint.getZ() - this.endPoint.getZ()) > MyTools.epsilon)
			isFlat = false;
		return isFlat;
	}

	/**
	 * Get the barycenter of the triangle.
	 *
	 * @return barycenter point.
	 * @throws DelaunayError 
	 */
	public MyPoint getBarycenter() throws DelaunayError {
		double x = (this.startPoint.getX()+this.endPoint.getX())/2.0;
		double y = (this.startPoint.getY()+this.endPoint.getY())/2.0;
		double z = (this.startPoint.getZ()+this.endPoint.getZ())/2.0;
		return new MyPoint(x, y, z);
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
		if (v1 < v2)
			hashValue = v1;
		else
			hashValue = v2;

		return hashValue;
	}

	/**
	 * Set the edge color for the JFrame panel
	 *
	 * @param g
	 */
	protected void setColor(Graphics g) {
		((Graphics2D) g).setStroke(new BasicStroke(1));
		if (getProperty() != 0 ) {
			g.setColor(Color.red);
			((Graphics2D) g).setStroke(new BasicStroke(2));
		} else if (isLocked()) {
			g.setColor(Color.CYAN);
		} else if (isOutsideMesh()) {
			g.setColor(Color.pink);
		} else
			g.setColor(Color.black);
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
	 * This method will be used to "order" the edges using the following strategy.
	 * If we note leftP and rightP the leftmost and rightmost point of this
	 * this < edge if this.leftP < edge.leftP or (this.leftP == edge.leftP and this.rightP < edge.rightP)
	 * this == edge if this.leftP == edge.leftP and this.rightP == edge.rightP
	 * this > edge otherwise.
	 * @param edge
	 * @return
	 */
	public int sortLeftRight(MyEdge edge){
		MyPoint p1 = getPointLeft();
		MyPoint p2 = edge.getPointLeft();
		int c = p1.compareTo2D(p2);
		if(c==0){
			p1 = getPointRight();
			p2 = edge.getPointRight();
			c = p1.compareTo2D(p2);

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
	public String toString()
	{
		return "Edge "+gid+" ["+startPoint+", "+endPoint+"]";
	}
	
}
