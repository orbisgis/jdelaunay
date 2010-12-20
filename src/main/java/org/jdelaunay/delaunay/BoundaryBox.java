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

public class BoundaryBox implements Serializable {
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
	private void init() {
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
	public BoundaryBox() {
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
	public BoundaryBox(double pminx, double pmaxx, double pminy, double pmaxy,
			double pminz, double pmaxz) {
		init();

		setBox(pminx, pmaxx, pminy, pmaxy, pminz, pmaxz);
	}

	/**
	 * set a box according to coordinates
	 * @param aBox
	 */
	public BoundaryBox(BoundaryBox aBox) {
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
	public final void setBox(double pminx, double pmaxx, double pminy, double pmaxy,
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
	private void updateMiddle(){
		double mx = (maxx-minx)/2;
		double my = (maxy-miny)/2;
		double mz = (maxz-minz)/2;
		middle=new Coordinate(minx+mx, miny+my, minz+mz);
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