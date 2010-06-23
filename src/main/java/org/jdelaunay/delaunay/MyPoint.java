package org.jdelaunay.delaunay;

/**
 * Delaunay Package.
 *
 * @author Jean-Yves MARTIN, Erwan BOCHER, Adelin PIAU
 * @date 2009-01-12
 * @revision 2010-06-23
 * @version 2.0
 */

import java.awt.*;
import com.vividsolutions.jts.geom.Coordinate;

public class MyPoint extends MyElement  {
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
	 */
	private void init(double x, double y, double z) {
		this.coord = new Coordinate(x,y,z);
		this.indicator = 0;

	}

	/**
	 * Build a point at the origin
	 */
	public MyPoint() {
		super();
		init(0.0,0.0,0.0);
	}

	/**
	 * Build a point at coordinates x, y, 0.0 with no type
	 * 
	 * @param x
	 * @param y
	 */
	public MyPoint(double x, double y) {
		super();
		init(x, y, 0.0);
	}

	/**
	 * Build a point at coordinates x, y, z with no type
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public MyPoint(double x, double y, double z) {
		super();
		init(x,y,z);
	}

	/**
	 * Build a point as a copy of another point
	 */
	public MyPoint(MyPoint pt) {
		super((MyElement) pt);
		init(pt.coord.x,pt.coord.y,pt.coord.z);
	}

	/**
	 * Build a point as a copy of jts Coordinates
	 */
	public MyPoint(Coordinate coord) {
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
	public boolean isUseByPolygon(){
		return testBit(4);
	}
	
	/**
	 * set if point is use by a polygon.
	 * @param useByPolygon
	 */
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
	public MyBox getBoundingBox() {
		MyBox aBox = new MyBox();
		aBox.alterBox( this);
		
		return aBox;
	}

	/* (non-Javadoc)
	 * @see org.jdelaunay.delaunay.MyElement#contains(org.jdelaunay.delaunay.MyPoint)
	 */
	public boolean contains(MyPoint aPoint) {
		if (squareDistance(aPoint) < MyTools.epsilon2)
			return true;
		else
			return false;
	}
	/**
	 * linear square distance to another point
	 * 
	 * @param aPoint
	 * @return distance
	 */
	protected double squareDistance_1D(MyPoint aPoint) {
		return (coord.x - aPoint.coord.x) * (coord.x - aPoint.coord.x);
	}

	/**
	 * plane square distance to another point
	 * 
	 * @param aPoint
	 * @return distance
	 */
	protected double squareDistance_2D(MyPoint aPoint) {
		return squareDistance(aPoint.coord.x, aPoint.coord.y);
	}

	/**
	 * plane square distance to another point
	 * 
	 * @param x
	 * @param y
	 * @return distance
	 */
	protected double squareDistance_2D(double x, double y) {
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
	protected boolean closedTo(MyPoint aPoint, double tolarence) {
		return (squareDistance(aPoint) < tolarence*tolarence);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Point [" + this.coord.x + " " + this.coord.y + " " + this.coord.z + "]";
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

		/*
		 * if (gid > 0) { g.drawString("" + gid, (int) ((x - minX) * scaleX +
		 * decalageX), (int) ((y - minY) * scaleY + decalageY) - 1); }
		 */}

}
