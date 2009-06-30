package org.jdelaunay.delaunay;

/**
 * Delaunay Package.
 *
 * @author Jean-Yves MARTIN
 * @date 2009-01-12
 * @version 1.0
 */

import java.awt.Color;
import java.awt.Graphics;

public class MyPoint {
	public double x, y, z;
	public String type;
	public int gid;
	protected boolean marked;
	private boolean talweg;
	private String topoType;

	private void init() {
		x = 0;
		y = 0;
		z = 0;
		type = null;
		gid = -1;
		marked = false;
	}

	/**
	 * Build a point at the origin
	 */
	public MyPoint() {
		init();
	}

	/**
	 * Build a point at coordinates _x, _y, 0.0 with no type
	 *
	 * @param _x
	 * @param _y
	 */
	public MyPoint(double _x, double _y) {
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
	public MyPoint(double _x, double _y, double _z, String _type) {
		init();
		x = _x;
		y = _y;
		z = _z;
		type = _type;
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
	public MyPoint(double _x, double _y, double _z, String _type, int _gid) {
		init();
		x = _x;
		y = _y;
		z = _z;
		type = _type;
		gid = _gid;
	}

	/**
	 * Build a point at coordinates _x, _y, _z with a gid
	 *
	 * @param _x
	 * @param _y
	 * @param _z
	 * @param _type
	 * @param _gid
	 */
	public MyPoint(double _x, double _y, double _z, int _gid) {
		init();
		x = _x;
		y = _y;
		z = _z;
		gid = _gid;
	}

	/**
	 * Build a point as a copy of another point
	 */
	public MyPoint(MyPoint _pt) {
		init();
		x = _pt.x;
		y = _pt.y;
		z = _pt.z;
		type = _pt.type;
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
	 * Set Type
	 *
	 *@param _type
	 * @return Type
	 */
	public void setPointType(String _type) {
		type = _type;
	}

	/**
	 * Get Type
	 *
	 * @return Type
	 */
	public String getPointType() {
		return type;
	}

	/**
	 * get GID
	 *
	 * @return
	 */
	public int getGid() {
		return gid;
	}

	/**
	 * set GID
	 *
	 * @param gid
	 */
	public void setGid(int gid) {
		this.gid = gid;
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
		return (x - aPoint.x) * (x - aPoint.x) + (y - aPoint.y)
				* (y - aPoint.y);
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
	 * square distance to another point
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return distance
	 */
	public double squareDistance(double x, double y, double z) {
		return (x - this.x) * (x - this.x) + (y - this.y)
				* (y - this.y) + (z - this.z) * (z - this.z);
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
	public void setColor(Graphics g) {
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
	public void displayObject(Graphics g, int decalageX, int decalageY, double minX, double minY, double scaleX, double scaleY) {
		g.drawOval((int) ((x-minX)*scaleX + decalageX) - 1,
				(int) ((y-minY)*scaleY + decalageY) - 1, 3, 3);

		if (false)
		if (gid > 0) {
			g.drawString(""+gid, (int) ((x-minX)*scaleX + decalageX),
					(int) ((y-minY)*scaleY + decalageY) - 1);
		}
	}

	public void setTopoType(String topoType) {
		this.topoType = topoType;

	}
	public String getTopoType() {
		return  topoType;

	}



}
