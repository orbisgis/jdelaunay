package org.jdelaunay.delaunay;
/**
 * Delaunay Package.
 * 
 * @author Jean-Yves MARTIN
 * @date 2009-01-12
 * @version 1.0
 */

import java.awt.Graphics;

public class MyPoint {
	protected double[] xy;
	protected String type;

	/**
	 * Build a point at the origin
	 * This method must be called by every constructor
	 */
	private void init() {
		xy = new double[3];
		for (int i = 0; i < 3; i++)
			xy[i] = 0;
		type = null;
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
		xy[0] = _x;
		xy[1] = _y;
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
		xy[0] = _x;
		xy[1] = _y;
		xy[2] = _z;
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
		xy[0] = _x;
		xy[1] = _y;
		xy[2] = _z;
		if (_type != null)
			type = new String(_type);
	}

	/**
	 * Build a point as a copy of another point
	 */
	public MyPoint(MyPoint _pt) {
		init();
		for (int i = 0; i < 3; i++)
			xy[i] = _pt.xy[i];
		if (_pt.type != null)
			type = new String(_pt.type);
	}

	/**
	 * Get X coordinate
	 * 
	 * @return x
	 */
	public double getX() {
		return xy[0];
	}

	/**
	 * Get Y coordinate
	 * 
	 * @return y
	 */
	public double getY() {
		return xy[1];
	}

	/**
	 * Get Z coordinate
	 * 
	 * @return z
	 */
	public double getZ() {
		return xy[2];
	}

	/**
	 * Set Type
	 * 
	 * @return Type
	 */
	public void setType(String _gid) {
		type = new String(_gid);
	}

	/**
	 * Get Type
	 * 
	 * @return Type
	 */
	public String getType() {
		return type;
	}

	/**
	 * linear square distance to another point
	 * 
	 * @param aPoint
	 * @return distance
	 */
	public double squareDistance_1D(MyPoint aPoint) {
		return (xy[0] - aPoint.xy[0]) * (xy[0] - aPoint.xy[0]);
	}

	/**
	 * plane square distance to another point
	 * 
	 * @param aPoint
	 * @return distance
	 */
	public double squareDistance_2D(MyPoint aPoint) {
		return (xy[0] - aPoint.xy[0]) * (xy[0] - aPoint.xy[0])
				+ (xy[1] - aPoint.xy[1]) * (xy[1] - aPoint.xy[1]);
	}

	/**
	 * square distance to another point
	 * 
	 * @param aPoint
	 * @return distance
	 */
	public double squareDistance(MyPoint aPoint) {
		return (xy[0] - aPoint.xy[0]) * (xy[0] - aPoint.xy[0])
				+ (xy[1] - aPoint.xy[1]) * (xy[1] - aPoint.xy[1])
				+ (xy[2] - aPoint.xy[2]) * (xy[2] - aPoint.xy[2]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Point " + xy[0] + " " + xy[1] + " " + xy[2];
	}

	/**
	 * Display the point in a JPanel
	 * Must be used only when using package drawing
	 * 
	 * @param g
	 * @param decalageX
	 * @param decalageY
	 */
	public void displayObject(Graphics g, int decalageX, int decalageY) {
		g.drawOval((int) (xy[0] + decalageX)-1, decalageY - (int) (xy[1])-1, 3, 3);
	}

}
