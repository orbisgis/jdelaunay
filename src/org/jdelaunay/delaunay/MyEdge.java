package org.jdelaunay.delaunay;
/**
 * Delaunay Package.
 * 
 * @author Jean-Yves MARTIN
 * @date 2009-01-12
 * @version 1.0
 */

import java.awt.*;

public class MyEdge
{
	/**
	 * 
	 */
	protected MyPoint[] point;
	protected MyTriangle left, right;
	protected String type;
	protected int marked;

	private static final double epsilon = 0.00001;

	/**
	 * Initialize data
	 */
	private void init() {
		point = new MyPoint[2];
		left=null;
		right=null;
		type = null;
		marked = 0;
	}

	/**
	 * Generate a new edge
	 */
	public MyEdge() {
		super();
		init();
	}

	/**
	 * Generate an edge from two points
	 * 
	 * @param s
	 * @param e
	 */
	public MyEdge(MyPoint s, MyPoint e) {
		init();
		point[0] = s;
		point[1] = e;
	}

	/**
	 * Generate an edge from another edge
	 * 
	 * @param _ed
	 */
	public MyEdge(MyEdge _ed) {
		init();
		for (int i = 0; i < 2; i++) {
			point[i] = _ed.point[i];
		}
		left=_ed.left;
		right=_ed.right;
		if (_ed.type != null)
			type = new String(_ed.type);
		marked = _ed.marked;
	}

	/**
	 * Generate a typed edge from two points
	 * 
	 * @param s
	 * @param e
	 * @param _type
	 */
	public MyEdge(MyPoint s, MyPoint e, String _type) {
		init();
		point[0] = s;
		point[1] = e;
		type = new String(_type);
	}

	public MyPoint point(int i) {
		if (i == 0)
			return point[0];
		else
			return point[1];
	}

	/**
	 * Returned edge left triangle
	 * 
	 * @return leftTriangle
	 */
	public MyTriangle getLeft() {
		return left;
	}

	/**
	 * Returned edge right triangle
	 * 
	 * @return rightTriangle
	 */
	public MyTriangle getRight() {
		return right;
	}

	/**
	 * Returned edge start point
	 * 
	 * @return end
	 */
	public MyPoint getStart() {
		return point[0];
	}

	/**
	 * Returned edge end point
	 * 
	 * @return end
	 */
	public MyPoint getEnd() {
		return point[1];
	}

	/**
	 * Set edge start point
	 * 
	 * @param p
	 */
	public void setStart(MyPoint p) {
		point[0] = p;
	}

	/**
	 * Set edge end point
	 * 
	 * @param p
	 */
	public void setEnd(MyPoint p) {
		point[1] = p;
	}

	/**
	 * Get edge type
	 * 
	 * @return
	 */
	public String getEdgeType() {
		return type;
	}

	/**
	 * Set edge type
	 * 
	 * @param type
	 */
	public void setEdgeType(String type) {
		this.type = type;
	}

	/**
	 * get the mark of the edge
	 * @return marked
	 */
	public int getMarked() {
		return marked;
	}

	/**
	 * set the mark of the edge
	 * @param marked
	 */
	public void setMarked(int marked) {
		this.marked = marked;
	}

	/**
	 * intersects two edges returns null if there is no intersection
	 * 
	 * @param p1
	 * @param p2
	 * @return intersection
	 */
	public int intersects(MyPoint p1, MyPoint p2) {
		int result = 0;
		MyPoint p3 = point[0];
		MyPoint p4 = point[1];
		// (x2 - x1) t1 - (x4 - x3) t2 = (x3 - x1)
		// (y2 - y1) t1 - (y4 - y3) t2 = (y3 - y1)

		double a1 = p2.x - p1.x;
		double b1 = p4.x - p3.x;
		double c1 = p3.x - p1.x;
		double a2 = p2.y - p1.y;
		double b2 = p4.y - p3.y;
		double c2 = p3.y - p1.y;
		double t1, t2;

		// d = (x4 - x3) (y2 - y1) - (x2 - x1) * (y4 - y3)
		double d = b1 * a2 - b2 * a1;
		if (d != 0) {
			// t1 = ((y3 - y1) (x4 - x3) - (x3 - x1) (y4 - y3)) / d
			// t2 = ((x2 - x1) (y3 - y1) - (y2 - y1) (x3 - x1)) / d

			t1 = (c2 * b1 - c1 * b2) / d;
			t2 = (a1 * c2 - a2 * c1) / d;

			if ((-epsilon <= t1) && (t1 <= 1 + epsilon) && (-epsilon <= t2)
					&& (t2 <= 1 + epsilon)) {
				result = 1;
			}
		} else {
			// Check if p3 is between p1 and p2
			if (Math.abs(p2.x - p1.x) > epsilon)
				t1 = (p3.x - p1.x) / (p2.x - p1.x);
			else
				t1 = (p3.y - p1.y) / (p2.y - p1.y);

			if ((-epsilon > t1) || (t1 > 1 + epsilon)) {
				// Check if p4 is between p1 and p2
				if (Math.abs(p2.x - p1.x) > epsilon)
					t1 = (p4.x - p1.x) / (p2.x - p1.x);
				else
					t1 = (p4.y - p1.y) / (p2.y - p1.y);

				if ((-epsilon > t1) || (t1 > 1 + epsilon)) {
					// Check if p1 is between p3 and p4
					if (Math.abs(p4.x - p3.x) > epsilon)
						t1 = (p1.x - p3.x) / (p4.x - p3.x);
					else
						t1 = (p1.y - p3.y) / (p4.y - p3.y);

					if ((-epsilon > t1) || (t1 > 1 + epsilon))
						// we do not check for p2 because it is now impossible
						result = 0;
					else
						result = 2;
				} else
					result = 2;

			} else
				result = 2;
		}

		return result;
	}

	/**
	 * intersects two edges returns null if there is no intersection
	 * 
	 * @param p1
	 * @param p2
	 * @return intersection
	 */
	public MyPoint getIntersection(MyPoint p1, MyPoint p2) {
		MyPoint intersection = null;
		MyPoint p3 = point[0];
		MyPoint p4 = point[1];

		// (x2 - x1) t1 - (x4 - x3) t2 = (x3 - x1)
		// (y2 - y1) t1 - (y4 - y3) t2 = (y3 - y1)

		double a1 = p2.x - p1.x;
		double b1 = p4.x - p3.x;
		double c1 = p3.x - p1.x;
		double a2 = p2.y - p1.y;
		double b2 = p4.y - p3.y;
		double c2 = p3.y - p1.y;

		// d = (x4 - x3) (y2 - y1) - (x2 - x1) * (y4 - y3)
		double d = b1 * a2 - b2 * a1;
		if (d != 0) {
			// t1 = ((y3 - y1) (x4 - x3) - (x3 - x1) (y4 - y3)) / d
			// t2 = ((x2 - x1) (y3 - y1) - (y2 - y1) (x3 - x1)) / d

			double t1 = (c2 * b1 - c1 * b2) / d;
			double t2 = (a1 * c2 - a2 * c1) / d;

			if ((-epsilon <= t1) && (t1 <= 1 + epsilon) && (-epsilon <= t2)
					&& (t2 <= 1 + epsilon)) {
				// it intersects
				if (t2 <= epsilon)
					intersection = p3;
				else if (t2 >= 1 - epsilon)
					intersection = p4;
				else if (t1 <= epsilon)
					intersection = p1;
				else if (t1 >= 1 - epsilon)
					intersection = p2;
				else {
					// x = x2 t1 + (1 - t1) x1
					// y = y2 t1 + (1 - t1) y1
					// z = z2 t1 + (1 - t1) z1
					double x = p2.x * t1 + (1 - t1) * p1.x;
					double y = p2.y * t1 + (1 - t1) * p1.y;
					double z = p2.z * t1 + (1 - t1) * p1.z;

					intersection = new MyPoint(x, y, z);

					// Last verification
					if (p1.squareDistance_2D(intersection) < epsilon)
						intersection = p1;
					else if (p2.squareDistance_2D(intersection) < epsilon)
						intersection = p2;
					else if (p3.squareDistance_2D(intersection) < epsilon)
						intersection = p3;
					else if (p4.squareDistance_2D(intersection) < epsilon)
						intersection = p4;
				}
			}
		}

		return intersection;
	}

	/**
	 * intersects two edges returns null if there is no intersection
	 * 
	 * @param anEdge
	 * @return intersection
	 */
	public MyPoint getIntersection(MyEdge anEdge) {
		return getIntersection(anEdge.point[0], anEdge.point[1]);
	}

	/**
	 * Check if the point p is on the left
	 * 
	 * @param p
	 * @return
	 */
	public boolean isLeft(MyPoint p) {
		MyPoint p1 = point[0];
		MyPoint p2 = point[1];
		double ux = p2.x - p1.x;
		double uy = p2.y - p1.y;
		double vx = p.x - p1.x;
		double vy = p.y - p1.y;

		return ux * vy - uy * vx > 0;
	}

	/**
	 * Check if the point p is on the right
	 * 
	 * @param p
	 * @return
	 */
	public boolean isRight(MyPoint p) {
		MyPoint p1 = point[0];
		MyPoint p2 = point[1];
		double ux = p2.x - p1.x;
		double uy = p2.y - p1.y;
		double vx = p.x - p1.x;
		double vy = p.y - p1.y;

		return ux * vy - uy * vx < 0;
	}

	/**
	 * Set the edge color for the JFrame panel
	 * 
	 * @param g
	 */
	public void setColor(Graphics g) {
		if (marked == 1)
			g.setColor(Color.blue);
		else if (marked == 2)
			g.setColor(Color.pink);
		else if ((left == null) && (right == null))
			g.setColor(Color.red);
		else if ((left == null) || (right == null))
			g.setColor(Color.orange);
		else
			g.setColor(Color.black);
	}

	/**
	 * Display the edge in a JPanel
	 * 
	 * @param g
	 * @param decalageX
	 * @param decalageY
	 */
	public void displayObject(Graphics g, int decalageX, int decalageY) {
		g.drawLine((int) (point[0].x + decalageX), decalageY
				- (int) (point[0].y), (int) (point[1].x + decalageX),
				decalageY - (int) (point[1].y));
		if (marked > 0) {
			point[0].displayObject(g, decalageX, decalageY);
			point[1].displayObject(g, decalageX, decalageY);
		}
	}
}
