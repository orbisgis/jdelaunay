package org.jdelaunay.delaunay;

/**
 * Delaunay Package.
 * 
 * @author Jean-Yves MARTIN, Erwan BOCHER
 * @date 2009-01-12
 * @version 1.0
 */

public class MyBox {
	protected double minx, maxx;
	protected double miny, maxy;
	protected double minz, maxz;
	private boolean empty;

	/**
	 *  initialization method
	 */
	public void init() {
		minx = maxx = miny = maxy = minz = maxz = 0.0;
		empty = true;
	}

	/**
	 * generate an empty box centered on 0,0,0
	 */
	public MyBox() {
		init();
	}

	/**
	 * set a box according to coordinates
	 * @param _minx
	 * @param _maxx
	 * @param _miny
	 * @param _maxy
	 * @param _minz
	 * @param _maxz
	 */
	public MyBox(double _minx, double _maxx, double _miny, double _maxy,
			double _minz, double _maxz) {
		init();

		setBox(_minx, _maxx, _miny, _maxy, _minz, _maxz);
	}

	/**
	 * set a box according to coordinates
	 * @param aBox
	 */
	public MyBox(MyBox aBox) {
		init();

		setBox(aBox.minx,aBox.maxx, aBox.miny, aBox.maxy, aBox.minz, aBox.maxz);
	}

	/**
	 * set box coordinates
	 * 
	 * @param _minx
	 * @param _maxx
	 * @param _miny
	 * @param _maxy
	 * @param _minz
	 * @param _maxz
	 */
	public void setBox(double _minx, double _maxx, double _miny, double _maxy,
			double _minz, double _maxz) {
		minx = _minx;
		maxx = _maxx;
		miny = _miny;
		maxy = _maxy;
		minz = _minz;
		maxz = _maxz;
		empty = false;
	}

	/**
	 * alter box coordinates according to the new point
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void alterBox(double x, double y, double z) {
		if (empty) {
			minx = maxx = x;
			miny = maxy = y;
			minz = maxz = z;
			empty = false;
		}
		else {
			if (minx > x)
				minx = x;
			else if (maxx < x)
				maxx = x;
			if (miny > y)
				miny = y;
			else if (maxy < y)
				maxy = y;
			if (minz > z)
				minz = z;
			else if (maxz < z)
				maxz = z;
		}
	}

	/**
	 * alter box coordinates according to the new point
	 * 
	 * @param aPoint
	 */
	public void alterBox(MyPoint aPoint) {
		double x = aPoint.getX();
		double y = aPoint.getY();
		double z = aPoint.getZ();
		alterBox(x,y,z);
	}
	
	
	public String toString()
	{
		return "min x["+minx+"] y["+miny+"] z["+minz+"] | max x["+maxx+"] y["+maxy+"] z["+maxz+"]";
	}
}
