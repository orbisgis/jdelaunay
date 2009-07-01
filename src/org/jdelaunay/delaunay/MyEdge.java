package org.jdelaunay.delaunay;

/**
 * Delaunay Package.
 *
 * @author Jean-Yves MARTIN
 * @date 2009-01-12
 * @version 1.0
 */

import java.awt.*;

import javax.print.DocFlavor.STRING;

import com.vividsolutions.jts.geom.Coordinate;

public class MyEdge {
	/**
	 *
	 */
	protected MyPoint[] point;
	protected MyTriangle left, right;
	protected String type;
	protected int gid;
	protected int marked;
	protected boolean outsideMesh;
	private boolean talweg = false;
	private boolean ridge = false;
	private double slopeInDegree;
	private Coordinate get3DVector;
	private String topoType;

	private static final double epsilon = 0.00001;
	static final int UPSLOPE = -1;
	final static int DOWNSLOPE = 1;
	final static int FLATSLOPE = 0;

	/**
	 * Initialize data
	 */
	private void init() {
		point = new MyPoint[2];
		left = null;
		right = null;
		type = null;
		gid = -1;
		marked = 0;
		outsideMesh = false;
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
		left = _ed.left;
		right = _ed.right;
		type = _ed.type;
		marked = _ed.marked;
		outsideMesh = _ed.outsideMesh;
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
		type = _type;
	}

	/**
	 * Generate a typed edge from two points
	 *
	 * @param s
	 * @param e
	 * @param _type
	 * @param _gid
	 */
	public MyEdge(MyPoint s, MyPoint e, String _type, int _gid) {
		init();
		point[0] = s;
		point[1] = e;
		type = _type;
		gid = _gid;
	}

	/**
	 * Generate a typed edge from two points
	 *
	 * @param s
	 * @param e
	 * @param _gid
	 */
	public MyEdge(MyPoint s, MyPoint e, int _gid) {
		init();
		point[0] = s;
		point[1] = e;
		gid = _gid;
	}

	/**
	 * @param i
	 * @return
	 */
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
	 *
	 * @return marked
	 */
	public int getMarked() {
		return marked;
	}

	/**
	 * set the mark of the edge
	 *
	 * @param marked
	 */
	public void setMarked(int marked) {
		this.marked = marked;
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
	 * check if two edges intersects
	 *
	 * @param p1
	 * @param p2
	 * @return intersection 0 = no intersection 1 = intersects 2 = co-linear 3 =
	 *         intersects at the extremity
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
					&& (t2 <= 1 + epsilon))
				if (((-epsilon <= t1) && (t1 <= epsilon))
						|| ((1 - epsilon <= t1) && (t1 <= 1 + epsilon)))
					result = 3;
				else
					result = 1;

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
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
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
					// z = z4 t2 + (1 - t2) z3
					double x = p2.x * t1 + (1 - t1) * p1.x;
					double y = p2.y * t1 + (1 - t1) * p1.y;
//					double z = p2.z * t1 + (1 - t1) * p1.z;
					double z = p4.z * t2 + (1 - t2) * p3.z;

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
	 * check if the point is between the extremities of the edge (on the
	 * xy-plane)
	 *
	 * @param p
	 * @return isInside
	 */
	public boolean isInside(MyPoint p) {
		boolean isInside = false;

		MyPoint p1 = point[0];
		MyPoint p2 = point[1];

		// x = x2 t1 + (1 - t1) x1
		// y = y2 t1 + (1 - t1) y1
		// z = z2 t1 + (1 - t1) z1

		// (x2 - x1) t1 = (x - x1)
		// (y2 - y1) t1 = (y - y1)

		// t1 = (x - x1) / (x2 - x1)
		// t1 = (y - y1) / (y2 - y1)
		double t1, t2;

		double a1 = p2.x - p1.x;
		double c1 = p.x - p1.x;
		double a2 = p2.y - p1.y;
		double c2 = p.y - p1.y;

		if (Math.abs(a1) > epsilon) {
			t1 = c1 / a1;
			if ((-epsilon < t1) && (t1 < 1 + epsilon)) {
				// p.x is between p1.x and p2.x
				if (Math.abs(a2) > epsilon) {
					t2 = c2 / a2;
					if ((-epsilon < t2) && (t2 < 1 + epsilon)
							&& (Math.abs(t1 - t2) < epsilon))
						// same t value => ok
						isInside = true;
				} else if (Math.abs(c2) < epsilon) {
					// p1.y, p2.y and p.y are the same
					isInside = true;
				}
			}
		} else if (Math.abs(c1) < epsilon) {
			// p1.x, p2.x and p.x are the same
			if (Math.abs(a2) > epsilon) {
				t2 = c2 / a2;
				if ((-epsilon < t2) && (t2 < 1 + epsilon))
					isInside = true;
			} else if (Math.abs(c2) < epsilon) {
				// p1.y, p2.y and p.y are also the same
				isInside = true;
			}

		}

		return isInside;
	}

	/**
	 * check if the point is one of the extremities of the edge (on the
	 * xy-plane)
	 *
	 * @param p
	 * @return isInside
	 */
	public boolean isExtremity(MyPoint p) {
		boolean isExtremity = false;

		if (point[0].squareDistance_2D(p) < epsilon)
			isExtremity = true;
		else if (point[1].squareDistance_2D(p) < epsilon)
			isExtremity = true;
		return isExtremity;
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
	 * swap the 2 points of the edge
	 */
	public void swap() {
		// swap points
		MyPoint aPoint = point[1];
		point[1] = point[0];
		point[0] = aPoint;

		// swap triangles
		MyTriangle aTriangle = left;
		left = right;
		right = aTriangle;
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
	 * Check if the edge is flat or not
	 *
	 * @return isFlat
	 */
	public boolean isFlatSlope() {
		boolean isFlat = true;
		if (Math.abs(point[0].z - point[1].z) > epsilon)
			isFlat = false;
		return isFlat;
	}

	/**
	 * Get the barycenter of the triangle
	 *
	 * @return isFlat
	 */
	public MyPoint getBarycenter() {
		double x = 0, y = 0, z = 0;
		for (int i = 0; i < 2; i++) {
			x += point[i].x;
			y += point[i].y;
			z += point[i].z;
		}
		x /= 2;
		y /= 2;
		z /= 2;
		return new MyPoint(x, y, z);
	}

	/**
	 * Check if the point p is on the right
	 *
	 * @param p
	 * @return
	 */
	public int hashCode() {
		MyPoint p1 = point[0];
		MyPoint p2 = point[1];
		int hashValue = 0;

		int v1 = p1.hashCode();
		int v2 = p2.hashCode();
		if (v1 < v2)
			hashValue = v1;
		else
			hashValue = v2;

		return hashValue;
	}

	/**
	 * Set the edge color for the JFrame panel
	 *
	 * @param g
	 */
	public void setColor(Graphics g) {
		((Graphics2D) g).setStroke(new BasicStroke(1));
		if (type == ConstaintType.SEWER) {
			g.setColor(Color.orange);
			((Graphics2D) g).setStroke(new BasicStroke(2));
		}
		else if (type == ConstaintType.RIVER) {
			g.setColor(Color.blue);
			((Graphics2D) g).setStroke(new BasicStroke(2));
		}
		else if (type == ConstaintType.DITCH) {
			g.setColor(Color.green);
			((Graphics2D) g).setStroke(new BasicStroke(2));
		}
		else if (type != null) {
			g.setColor(Color.yellow);
			((Graphics2D) g).setStroke(new BasicStroke(2));
		}
		else if (talweg) {
			g.setColor(Color.GRAY);
			((Graphics2D) g).setStroke(new BasicStroke(2));
		}
		else if (ridge) {
			g.setColor(Color.CYAN);
		}
		else if (marked == 1) {
			g.setColor(Color.red);
		}
		else if (marked == 2) {
			g.setColor(Color.pink);
		}
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
	public void displayObject(Graphics g, int decalageX, int decalageY,
			double minX, double minY, double scaleX, double scaleY) {
		g.drawLine((int) ((point[0].x - minX) * scaleX + decalageX), decalageY
				+ (int) ((point[0].y - minY) * scaleY),
				(int) ((point[1].x - minX) * scaleX + decalageX), decalageY
						+ (int) ((point[1].y - minY) * scaleY));
		if (marked > 0) {
			point[0].displayObject(g, decalageX, decalageY, minX, minY, scaleX,
					scaleY);
			point[1].displayObject(g, decalageX, decalageY, minX, minY, scaleX,
					scaleY);
		}
	}

	public void setSlopeInDegree(double slopeInDegree) {
		this.slopeInDegree = slopeInDegree;

	}

	public double getSlopeInDegree() {
		return slopeInDegree;
	}

	public void setSlope(Coordinate get3DVector) {
		this.get3DVector = get3DVector;

	}

	public Coordinate getSlope() {
		return get3DVector;

	}

	public void setTopoType(String topoType) {
		this.topoType = topoType;

	}

	public String getTopoType() {
		return topoType;

	}

	public int getGradient() {
		int gradient;
		if (getStart().z > getEnd().z) {
			gradient = MyEdge.DOWNSLOPE;
		} else if (getStart().z < getEnd().z) {
			gradient = MyEdge.UPSLOPE;
		} else {
			gradient = MyEdge.FLATSLOPE;
		}
		return gradient;
	}

}
