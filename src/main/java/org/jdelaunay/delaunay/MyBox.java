package org.jdelaunay.delaunay;

import java.io.Serializable;
import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Delaunay Package.
 * 
 * @author Jean-Yves MARTIN, Erwan BOCHER, Adelin PIAU
 * @date 2009-01-12
 * @revision 2010-11-08
 * @version 1.1
 */

public class MyBox implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5019273961990097490L;
	protected double minx, maxx;
	protected double miny, maxy;
	protected double minz, maxz;
	private Coordinate middle;
	private boolean empty;

	/**
	 *  initialization method
	 */
	public void init() {
		minx = 0.0;
                maxx = 0.0;
                miny = 0.0;
                maxy = 0.0;
                minz = 0.0;
                maxz = 0.0;
		empty = true;
		middle=new Coordinate(0, 0, 0);
	}

	/**
	 * generate an empty box centered on 0,0,0
	 */
	public MyBox() {
		init();
	}

	/**
	 * set a box according to coordinates
	 * @param pminx
	 * @param pmaxx
	 * @param pminy
	 * @param pmaxy
	 * @param pminz
	 * @param pmaxz
	 */
	public MyBox(double pminx, double pmaxx, double pminy, double pmaxy,
			double pminz, double pmaxz) {
		init();

		setBox(pminx, pmaxx, pminy, pmaxy, pminz, pmaxz);
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
	 * @param pminx
	 * @param pmaxx
	 * @param pminy
	 * @param pmaxy
	 * @param pminz
	 * @param pmaxz
	 */
	public void setBox(double pminx, double pmaxx, double pminy, double pmaxy,
			double pminz, double pmaxz) {
		minx = pminx;
		maxx = pmaxx;
		miny = pminy;
		maxy = pmaxy;
		minz = pminz;
		maxz = pmaxz;
		empty = false;
		updateMiddle();
	}
	
	
	/**
	 * Update middle coordinate. 
	 */
	private void updateMiddle()
	{
		double mx = maxx-minx;
		double my = maxy-miny;
		double mz = maxz-minz;
		middle=new Coordinate(minx+(mx!=0?mx/2:0), miny+(my!=0?my/2:0), minz+(mz!=0?mz/2:0));
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
			if (minx > x) {
				minx = x;
			}
			else if (maxx < x) {
				maxx = x;
			}
			if (miny > y) {
				miny = y;
			}
			else if (maxy < y) {
				maxy = y;
			}
			if (minz > z) {
				minz = z;
			}
			else if (maxz < z) {
				maxz = z;
			}
		}
		updateMiddle();
	}

	/**
	 * alter box coordinates according to the new point
	 * 
	 * @param aPoint
	 */
	public void alterBox(Point aPoint) {
		double x = aPoint.getX();
		double y = aPoint.getY();
		double z = aPoint.getZ();
		alterBox(x,y,z);
	}
	
	
	/**
	 * @return Middle coordinate of box.
	 */
	public Coordinate getMiddle()
	{
		return middle;
	}
	
        @Override
	public String toString()
	{
		return "min x["+minx+"] y["+miny+"] z["+minz+"] | max x["+maxx+"] y["+maxy+"] z["+maxz+"]";
	}

	public ArrayList<Point> getPoints() throws DelaunayError {
		ArrayList<Point> points = new ArrayList<Point>();
		points.add(new Point(minx, miny, 0));
		points.add(new Point(minx, maxy, 0));
		points.add(new Point(maxx, miny, 0));
		points.add(new Point(maxx, maxy, 0));
		return points;
	}
}
