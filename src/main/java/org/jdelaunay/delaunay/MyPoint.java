package org.jdelaunay.delaunay;

/**
 * Delaunay Package.
 *
 * @author Jean-Yves MARTIN, Erwan BOCHER
 * @date 2009-01-12
 * @revision 2010-05-14
 * @version 2.0
 */

import java.awt.*;
import com.vividsolutions.jts.geom.Coordinate;

public class MyPoint extends MyElement  {
	protected Coordinate coord;
	protected byte marked;

	/**
	 * Initialize point 
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	private void init(double x, double y, double z) {
		this.coord = new Coordinate(x,y,z);
		marked = 0;
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
	 * return true if the point is marked, false otherwise
	 * @return marked
	 */
	public boolean isMarked() {
		return (marked != 0);
	}

	/**
	 * mark, unmark the point
	 * @param marked
	 */
	public void setMarked(boolean marked) {
		if (marked)
			this.marked = 1;
		else
			this.marked = 0;
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
		if (squareDistance(aPoint) < 0.000001)
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
//		g.drawOval((int) ((this.coord.x - minX) * scaleX + decalageX) - 1,
//				(int) ((this.coord.y - minY) * scaleY + decalageY) - 1, 1, 1);// coordinate 0 in Y is at top of screen (don't forget make change in sub method)

		g.drawOval((int) ((this.coord.x - minX) * scaleX + decalageX) - 1,
				(int) (decalageY - (this.coord.y - minY) * scaleY) - 1, 1, 1);// coordinate 0 in Y is at bottom of screen
		
		/*
		 * if (gid > 0) { g.drawString("" + gid, (int) ((x - minX) * scaleX +
		 * decalageX), (int) ((y - minY) * scaleY + decalageY) - 1); }
		 */}

}
