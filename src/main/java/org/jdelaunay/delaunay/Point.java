package org.jdelaunay.delaunay;

/**
 * Delaunay Package.
 *
 * @author Jean-Yves MARTIN, Erwan BOCHER, Adelin PIAU
 * @date 2009-01-12
 * @revision 2010-11-08
 * @version 2.1
 */

import java.awt.Color;
import java.awt.Graphics;

import com.vividsolutions.jts.geom.Coordinate;
import java.io.Serializable;

public class Point extends Element implements Comparable<Point>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Coordinate coord;
	
	/**
	 * byte number  | function :
	 * 1			| isOutsideMesh / setOutsideMesh
	 * 2			| isLocked / setLocked
	 * 3			| isUseByLevelEdge / setUseByLevelEdge
	 * 4			| isUseByPolygon / setUseByPolygon
	 * 5			| isZUse / setUseZ
	 * 6 to 32		| isMarked / setMarked
	 */
	private int indicator;

	/**
	 * Initialize point 
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @throws DelaunayError If x, y or z is not set.
	 */
	private void init(double x, double y, double z) throws DelaunayError {
		if(Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z)) {
			throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_ERROR_POINT_XYZ);
		}
		
		this.coord = new Coordinate(x,y,z);
		this.indicator = 0;

	}

	/**
	 * Build a point at the origin
	 * @throws DelaunayError  If x, y or z is not set.
	 */
	public Point() throws DelaunayError {
		super();
		init(0.0,0.0,0.0);
	}

	/**
	 * Build a point at coordinates x, y, z with no type
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @throws DelaunayError If x, y or z is not set.
	 */
	public Point(double x, double y, double z) throws DelaunayError {
		super();
		init(x,y,z);
	}

	/**
	 * Build a point as a copy of another point
	 * @throws DelaunayError DelaunayError
	 */
	public Point(Point pt) throws DelaunayError {
		super((Element) pt);
		init(pt.coord.x,pt.coord.y,pt.coord.z);
	}

	/**
	 * Build a point as a copy of jts Coordinates
	 * @throws DelaunayError If x, y or z is not set.
	 */
	public Point(Coordinate coord) throws DelaunayError {
		super();
		init(coord.x,coord.y,coord.z);
	}

	/**
	 * Get X coordinate
	 * @return x
	 */
	public final double getX() {
		return this.coord.x;
	}

	/**
	 * Get Y coordinate
	 * @return y
	 */
	public final double getY() {
		return this.coord.y;
	}

	/**
	 * Get Z coordinate
	 * @return z
	 */
	public final double getZ() {
		return this.coord.z;
	}

	/**
	 * Set X coordinate
	 * @param z
	 */
	public final void setX(double x) {
		this.coord.x = x;
	}
	/**
	 * Set Y coordinate
	 * @param z
	 */
	public final void setY(double y) {
		this.coord.y = y;
	}
	/**
	 * Set Z coordinate
	 * @param z
	 */
	public final void setZ(double z) {
		this.coord.z = z;
	}


	@Override
	public final int getIndicator() {
		return indicator;
	}
	
	@Override
	public final int setIndicator(int indicator) {
		this.indicator=indicator;
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
	public final boolean isMarked(int byteNumber) {
		return testBit(Tools.BIT_MARKED + byteNumber);
	}

	/**
	 * set the mark of the edge
	 * @param byteNumber
	 * @param marked
	 */
	public final void setMarked(int byteNumber, boolean marked) {
		setBit(Tools.BIT_MARKED + byteNumber, marked);
	}
	
	
	/**
	 * Should be only use by MyEdge.
	 * Set the mark of the point.
	 * @param byteNumber
	 * @param marked
	 */
	public final void setMarkedByEdge(int byteNumber, boolean marked) {
		setBit(byteNumber, marked);
	}

	/**
	 * get the mark of the edge
	 * @return marked
	 */
	public final boolean isLocked() {
		return testBit(Tools.BIT_LOCKED);
	}

	/**
	 * set the mark of the edge
	 * @param marked
	 */
	public final void setLocked(boolean locked) {
		setBit(Tools.BIT_LOCKED, locked);
	}
	
	
	/**
	 * check if point is use by a level edge.
	 * @return useByLevelEdge
	 */
	public final boolean isUseByLevelEdge(){
		return testBit(3);
	}
	
	/**
	 * set if point is use by a level edge.
	 * @param useByLevelEdge
	 */
	public final void setUseByLevelEdge(boolean useByLevelEdge){
		setBit(3, useByLevelEdge);
	}
	
		/**
	 * check if edge is use by a polygon
	 * @return useByPolygon
	 */
	@Override
	public final boolean isUseByPolygon() {
		return testBit(Tools.BIT_POLYGON);
	}

	/**
	 * set if edge is use by a polygon.
	 * @param useByPolygon
	 */
	@Override
	public final void setUseByPolygon(boolean useByPolygon) {
		setBit(Tools.BIT_POLYGON, useByPolygon);
	}

	/**
	 * check if Z coordinate is use.
	 * @return useZ
	 */
	public final boolean isZUse() {
		return testBit(Tools.BIT_ZUSED);
	}

	/**
	 * set if Z coordinate is use.
	 * @param useByPolygon
	 */
	public final void setUseZ(boolean useZ) {
		setBit(Tools.BIT_ZUSED, useZ);
	}
	
	
	/**
	 * return jts Coordinate
	 * @return
	 */
	public final Coordinate getCoordinate() {
		return coord;
	}

	/* (non-Javadoc)
	 * @see org.jdelaunay.delaunay.Element#getBoundingBox()
	 */
	@Override
	public final BoundaryBox getBoundingBox() {
		BoundaryBox aBox = new BoundaryBox();
		aBox.alterBox( this);
		
		return aBox;
	}

	/* (non-Javadoc)
	 * @see org.jdelaunay.delaunay.Element#contains(org.jdelaunay.delaunay.Point)
	 */
	@Override
	public final boolean contains(Point aPoint) {
		return squareDistance(aPoint) < Tools.EPSILON2;
	}
	
	@Override
	public final boolean contains(Coordinate c) {
		return squareDistance(c.x, c.y, c.z) < Tools.EPSILON2;
	}
	
	/**
	 * linear square distance to another point
	 * 
	 * @param aPoint
	 * @return distance
	 */
	protected final double squareDistance1D(Point aPoint) {
		return (coord.x - aPoint.coord.x) * (coord.x - aPoint.coord.x);
	}

	/**
	 * plane square distance to another point
	 * 
	 * @param aPoint
	 * @return distance
	 */
	protected final double squareDistance2D(Point aPoint) {
		return squareDistance(aPoint.coord.x, aPoint.coord.y);
	}

	/**
	 * plane square distance to another point
	 * 
	 * @param x
	 * @param y
	 * @return distance
	 */
	protected final double squareDistance2D(double x, double y) {
		return squareDistance(x, y);
	}

	/**
	 * square distance to another point
	 * 
	 * @param aPoint
	 * @return distance
	 */
	protected final double squareDistance(Point aPoint) {
		return squareDistance(aPoint.coord.x, aPoint.coord.y, aPoint.coord.z);
	}

	/**
	 * plane square distance to another point
	 * 
	 * @param x
	 * @param y
	 * @return distance
	 */
	protected final double squareDistance(double x, double y) {
		return (x - this.coord.x) * (x - this.coord.x) + (y - this.coord.y) * (y - this.coord.y);
	}

	/**
	 * square distance to another point
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return distance
	 */
	protected final double squareDistance(double x, double y, double z) {
		return (x - this.coord.x) * (x - this.coord.x) + (y - this.coord.y) * (y - this.coord.y)
				+ (z - this.coord.z) * (z - this.coord.z);
	}

	/**
	 * Check if the point is closed to the current point
	 * 
	 * @param aPoint
	 * @param tolarence
	 * @return closedTo
	 */
	protected final boolean closedTo(Point aPoint, double tolerence) {
		return (squareDistance(aPoint) < tolerence*tolerence);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String toString() {
		return "Point "+getGID()+" [" + this.coord.x + " " + this.coord.y + " " + this.coord.z + "]";
	}

	/**
	 * We override the equals method, as two points can be said equal when their
	 * coordinate are exactly the same
	 * @param y
	 * @return
	 */
	@Override
	public final boolean equals(Object p){
		if(p instanceof Point){
			Point y = (Point) p;
                        double dist = (getX() - y.getX())*(getX() - y.getX())+(getY() - y.getY())*(getY() - y.getY());
                        dist = dist + (getZ() - y.getZ())*(getZ() - y.getZ());
			return dist<Tools.EPSILON2;
		}else {
			return false;
		}
	}

	@Override
	public final int hashCode() {
		int hash = 7;
		hash = 67 * hash + (this.coord != null ? this.coord.hashCode() : 0);
		return hash;
	}

	/**
	 * Check if this==y, considering only the first two coordinates.
	 * @param y
	 * @return
	 */
	public final boolean equals2D(Point y){
		if(y==null){
			return false;
		} else {
                        return (((getX() - y.getX())*(getX() - y.getX())+(getY() - y.getY())*(getY() - y.getY()))<Tools.EPSILON2);
		}
	}

	/**
	 * Compare this and p in two dimensions.
	 * @param y
	 * @return
	 *	-1 : if this.x < p.x || (this.x == p.x && this.y < p.y)
	 *	0 : if this.x == p.x && this.y == p.y
	 *	1 otherwise.
	 */
	public final int compareTo2D(Point p){
                double dx = (getX() - p.getX());
                if(dx*dx*2 < Tools.EPSILON2){
                        double dy = (getY() - p.getY());
                        if(dx*dx+dy*dy<Tools.EPSILON2){
                                return 0;
                        } else if(getY()<p.getY()){
                                return -1;
                        } else {
                                return 1;
                        }
                } else if(getX()<p.getX()){
                        return -1;
                } else {
                        return 1;
                }
	}

	/**
	 * Compare this and p in two dimensions.
	 * @param y
	 * @return
	 *	-1 : if this.x < p.x || (this.x == p.x && this.y < p.y)
	 *	0 : if this.x == p.x && this.y == p.y
	 *	1 otherwise.
	 */
	@Override
	public final int compareTo(Point p){
		return compareTo2D(p);
	}
	
	/**
	 * Set the point color for the JFrame panel
	 * 
	 * @param g
	 */
	protected final void setColor(Graphics g) {
		g.setColor(Color.black);
	}

	/**
	 * Display the point in a JPanel Must be used only when using package
	 * drawing
	 * 
	 * @param g
	 * @param decalageX
	 * @param decalageY
	 */
	protected final void displayObject(Graphics g, int decalageX, int decalageY,
			double minX, double minY, double scaleX, double scaleY) {
		setColor(g);
		g.drawOval((int) ((this.coord.x - minX) * scaleX + decalageX) - 1,
				(int) ((this.coord.y - minY) * scaleY + decalageY) - 1, 1, 1);
	}
}
