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

public class MyPoint extends MyElement {
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
	public MyPoint() throws DelaunayError {
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
	public MyPoint(double x, double y, double z) throws DelaunayError {
		super();
		init(x,y,z);
	}

	/**
	 * Build a point as a copy of another point
	 * @throws DelaunayError DelaunayError
	 */
	public MyPoint(MyPoint pt) throws DelaunayError {
		super((MyElement) pt);
		init(pt.coord.x,pt.coord.y,pt.coord.z);
	}

	/**
	 * Build a point as a copy of jts Coordinates
	 * @throws DelaunayError If x, y or z is not set.
	 */
	public MyPoint(Coordinate coord) throws DelaunayError {
		super();
		init(coord.x,coord.y,coord.z);
	}

	/**
	 * Get X coordinate
	 * @return x
	 */
	public double getX() {
		return this.coord.x;
	}

	/**
	 * Get Y coordinate
	 * @return y
	 */
	public double getY() {
		return this.coord.y;
	}

	/**
	 * Get Z coordinate
	 * @return z
	 */
	public double getZ() {
		return this.coord.z;
	}

	/**
	 * Set X coordinate
	 * @param z
	 */
	public void setX(double x) {
		this.coord.x = x;
	}
	/**
	 * Set Y coordinate
	 * @param z
	 */
	public void setY(double y) {
		this.coord.y = y;
	}
	/**
	 * Set Z coordinate
	 * @param z
	 */
	public void setZ(double z) {
		this.coord.z = z;
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
	 * get the mark of the point
	 * @param byteNumber
	 * @return marked
	 */
	public boolean isMarked(int byteNumber) {
		return testBit(6+byteNumber);
	}

	/**
	 * set the mark of the point
	 * @param byteNumber
	 * @param marked
	 */
	public void setMarked(int byteNumber, boolean marked) {
		setBit(6+byteNumber, marked);
	}
	
	
	/**
	 * Should be only use by MyEdge.
	 * Set the mark of the point.
	 * @param byteNumber
	 * @param marked
	 */
	public void setMarkedByEdge(int byteNumber, boolean marked) {
		setBit(byteNumber, marked);
	}

	/**
	 * get the mark of the point
	 * @return marked
	 */
	public boolean isLocked() {
		return testBit(2);
	}

	/**
	 * set the mark of the point
	 * @param marked
	 */
	public void setLocked(boolean locked) {
		setBit(2, locked);
	}
	
	
	/**
	 * check if point is use by a level edge.
	 * @return useByLevelEdge
	 */
	public boolean isUseByLevelEdge(){
		return testBit(3);
	}
	
	/**
	 * set if point is use by a level edge.
	 * @param useByLevelEdge
	 */
	public void setUseByLevelEdge(boolean useByLevelEdge){
		setBit(3, useByLevelEdge);
	}
	
	/**
	 * check if point is use by a polygon.
	 * @return useByPolygon
	 */
	@Override
	public boolean isUseByPolygon(){
		return testBit(4);
	}
	
	/**
	 * set if point is use by a polygon.
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
	
	
	/**
	 * return jts Coordinate
	 * @return
	 */
	public Coordinate getCoordinate() {
		return coord;
	}

	/* (non-Javadoc)
	 * @see org.jdelaunay.delaunay.MyElement#getBoundingBox()
	 */
	@Override
	public MyBox getBoundingBox() {
		MyBox aBox = new MyBox();
		aBox.alterBox( this);
		
		return aBox;
	}

	/* (non-Javadoc)
	 * @see org.jdelaunay.delaunay.MyElement#contains(org.jdelaunay.delaunay.MyPoint)
	 */
	@Override
	public boolean contains(MyPoint aPoint) {
		return squareDistance(aPoint) < MyTools.EPSILON2;
	}
	
	@Override
	public boolean contains(Coordinate c) {
		return squareDistance(c.x, c.y, c.z) < MyTools.EPSILON2;
	}
	
	/**
	 * linear square distance to another point
	 * 
	 * @param aPoint
	 * @return distance
	 */
	protected double squareDistance1D(MyPoint aPoint) {
		return (coord.x - aPoint.coord.x) * (coord.x - aPoint.coord.x);
	}

	/**
	 * plane square distance to another point
	 * 
	 * @param aPoint
	 * @return distance
	 */
	protected double squareDistance2D(MyPoint aPoint) {
		return squareDistance(aPoint.coord.x, aPoint.coord.y);
	}

	/**
	 * plane square distance to another point
	 * 
	 * @param x
	 * @param y
	 * @return distance
	 */
	protected double squareDistance2D(double x, double y) {
		return squareDistance(x, y);
	}

	/**
	 * square distance to another point
	 * 
	 * @param aPoint
	 * @return distance
	 */
	protected double squareDistance(MyPoint aPoint) {
		return squareDistance(aPoint.coord.x, aPoint.coord.y, aPoint.coord.z);
	}

	/**
	 * plane square distance to another point
	 * 
	 * @param x
	 * @param y
	 * @return distance
	 */
	protected double squareDistance(double x, double y) {
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
	protected double squareDistance(double x, double y, double z) {
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
	protected boolean closedTo(MyPoint aPoint, double tolerence) {
		return (squareDistance(aPoint) < tolerence*tolerence);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Point "+gid+" [" + this.coord.x + " " + this.coord.y + " " + this.coord.z + "]";
	}

	/**
	 * We override the equals method, as two points can be said equal when their
	 * coordinate are exactly the same
	 * @param y
	 * @return
	 */
	@Override
	public boolean equals(Object p){
		if(p instanceof MyPoint){
			MyPoint y = (MyPoint) p;
                        double dist = coord.distance(y.getCoordinate()) + (coord.z - y.getZ())*coord.z - y.getZ();
			return dist<MyTools.EPSILON2;
		}else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 67 * hash + (this.coord != null ? this.coord.hashCode() : 0);
		return hash;
	}

	/**
	 * Check if this==y, considering only the first two coordinates.
	 * @param y
	 * @return
	 */
	public boolean equals2D(MyPoint y){
		if(y==null){
			return false;
		} else {
                        return (coord.distance(y.getCoordinate())<MyTools.EPSILON);
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
	public int compareTo2D(MyPoint p){
		return coord.compareTo(p.getCoordinate());
	}
	/**
	 * Set the point color for the JFrame panel
	 * 
	 * @param g
	 */
	protected void setColor(Graphics g) {
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
	protected void displayObject(Graphics g, int decalageX, int decalageY,
			double minX, double minY, double scaleX, double scaleY) {
		setColor(g);
		g.drawOval((int) ((this.coord.x - minX) * scaleX + decalageX) - 1,
				(int) ((this.coord.y - minY) * scaleY + decalageY) - 1, 1, 1);
	}
}
