package org.jdelaunay.delaunay;

/**
 * Delaunay Package.
 *
 * @author Jean-Yves MARTIN, Erwan BOCHER
 * @date 2009-01-12
 * @version 1.0
 */

import java.awt.*;
import com.vividsolutions.jts.geom.Coordinate;

public class MyPoint extends MyElement {
	protected double x, y, z;
	protected byte marked;

	private void init() {
		x = 0;
		y = 0;
		z = 0;
		marked = 0;
	}

	/**
	 * Build a point at the origin
	 */
	public MyPoint() {
		super();
		init();
	}

	/**
	 * Build a point at coordinates _x, _y, 0.0 with no type
	 * 
	 * @param _x
	 * @param _y
	 */
	public MyPoint(double _x, double _y) {
		super();
		init();
		x = _x;
		y = _y;
	}

	/**
	 * Build a point at coordinates _x, _y, _z with no type
	 * 
	 * @param _x
	 * @param _y
	 * @param _z
	 */
	public MyPoint(double _x, double _y, double _z) {
		super();
		init();
		x = _x;
		y = _y;
		z = _z;
	}

	/**
	 * Build a point at coordinates _x, _y, _z with a type
	 * 
	 * @param _x
	 * @param _y
	 * @param _z
	 * @param _type
	 */
	public MyPoint(double _x, double _y, double _z, int _type) {
		super(_type);
		init();
		x = _x;
		y = _y;
		z = _z;
	}

	/**
	 * Build a point at coordinates _x, _y, _z with a type and a gid
	 * 
	 * @param _x
	 * @param _y
	 * @param _z
	 * @param _type
	 * @param _gid
	 */
	public MyPoint(double _x, double _y, double _z, int _type, int _gid) {
		super(_type, _gid);
		init();
		x = _x;
		y = _y;
		z = _z;
	}

	/**
	 * Build a point as a copy of another point
	 */
	public MyPoint(MyPoint _pt) {
		super((MyElement) _pt);
		init();
		x = _pt.x;
		y = _pt.y;
		z = _pt.z;
	}

	public MyPoint(Coordinate coord) {
		super();
		init();
		x = coord.x;
		y = coord.y;
		z = coord.z;
	}

	/**
	 * Get X coordinate
	 * 
	 * @return x
	 */
	public double getX() {
		return x;
	}

	/**
	 * Get Y coordinate
	 * 
	 * @return y
	 */
	public double getY() {
		return y;
	}

	/**
	 * Get Z coordinate
	 * 
	 * @return z
	 */
	public double getZ() {
		return z;
	}

	/**
	 * Set Z coordinate
	 * 
	 * @param z
	 */
	public void setZ(double z) {
		this.z = z;
	}

	/**
	 * return true if the point is marked, false otherwise
	 * 
	 * @return marked
	 */
	public boolean isMarked() {
		return (marked != 0);
	}

	/**
	 * mark, unmark the point
	 * 
	 * @param marked
	 */
	public void setMarked(boolean marked) {
		if (marked)
			this.marked = 1;
		else
			this.marked = 0;
	}

	/**
	 * Translate to Coordinate
	 * 
	 * @return
	 */
	public Coordinate getCoordinate() {
		return new Coordinate(x, y, z);
	}

	/**
	 * linear square distance to another point
	 * 
	 * @param aPoint
	 * @return distance
	 */
	public double squareDistance_1D(MyPoint aPoint) {
		return (x - aPoint.x) * (x - aPoint.x);
	}

	/**
	 * plane square distance to another point
	 * 
	 * @param aPoint
	 * @return distance
	 */
	public double squareDistance_2D(MyPoint aPoint) {
		return squareDistance(aPoint.x, aPoint.y);
	}

	/**
	 * plane square distance to another point
	 * 
	 * @param x
	 * @param y
	 * @return distance
	 */
	public double squareDistance_2D(double x, double y) {
		return squareDistance(x, y);
	}

	/**
	 * square distance to another point
	 * 
	 * @param aPoint
	 * @return distance
	 */
	public double squareDistance(MyPoint aPoint) {
		return squareDistance(aPoint.x, aPoint.y, aPoint.z);
	}

	/**
	 * plane square distance to another point
	 * 
	 * @param x
	 * @param y
	 * @return distance
	 */
	public double squareDistance(double x, double y) {
		return (x - this.x) * (x - this.x) + (y - this.y) * (y - this.y);
	}

	/**
	 * square distance to another point
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return distance
	 */
	public double squareDistance(double x, double y, double z) {
		return (x - this.x) * (x - this.x) + (y - this.y) * (y - this.y)
				+ (z - this.z) * (z - this.z);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Point [" + x + " " + y + " " + z + "]";
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
		g.drawOval((int) ((x - minX) * scaleX + decalageX) - 1,
				(int) ((y - minY) * scaleY + decalageY) - 1, 1, 1);

		/*
		 * if (gid > 0) { g.drawString("" + gid, (int) ((x - minX) * scaleX +
		 * decalageX), (int) ((y - minY) * scaleY + decalageY) - 1); }
		 */}

}
