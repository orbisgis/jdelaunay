package org.jdelaunay.delaunay;

/**
 * Delaunay Package.
 *
 * @author Jean-Yves MARTIN, Erwan BOCHER
 * @date 2009-01-12
 * @revision 2010-05-14
 * @version 1.1
 */

import java.awt.*;

public class MyEdge extends MyElement {
	protected MyPoint start, end;
	protected MyTriangle left, right;

	protected byte marked;
	protected byte outsideMesh;

	static final int UPSLOPE = -1;
	static final int DOWNSLOPE = 1;
	static final int FLATSLOPE = 0;

	/**
	 * Initialize data
	 */
	private void init() {
		this.start = null;
		this.end = null;
		this.left = null;
		this.right = null;
		this.marked = 0;
		this.outsideMesh = 0;
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
	 * @param start
	 * @param end
	 */
	public MyEdge(MyPoint start, MyPoint end) {
		super();
		init();
		this.start = start;
		this.end = end;
	}

	/**
	 * Generate an edge from another edge
	 *
	 * @param ed
	 */
	public MyEdge(MyEdge ed) {
		super((MyElement)ed);
		init();
		this.start = ed.start;
		this.end = ed.end;
		this.left = ed.left;
		this.right = ed.right;
		this.marked = ed.marked;
		this.outsideMesh = ed.outsideMesh;
	}

	/**
	 * @param i
	 * @return
	 */
	public MyPoint point(int i) {
		if (i == 0)
			return this.start;
		else
			return this.end;
	}

	/**
	 * Returned edge left triangle
	 *
	 * @return leftTriangle
	 */
	public MyTriangle getLeft() {
		return this.left;
	}

	/**
	 * Returned edge right triangle
	 *
	 * @return rightTriangle
	 */
	public MyTriangle getRight() {
		return this.right;
	}

	/**
	 * set edge left triangle
	 *
	 * @return leftTriangle
	 */
	public void setLeft(MyTriangle aTriangle) {
		this.left=aTriangle;
	}

	/**
	 * set edge right triangle
	 *
	 * @return rightTriangle
	 */
	public void setRight(MyTriangle aTriangle) {
		this.right=aTriangle;
	}
	
	/**
	 * Returned edge start point
	 *
	 * @return end
	 */
	public MyPoint getStart() {
		return this.start;
	}

	/**
	 * Returned edge end point
	 *
	 * @return end
	 */
	public MyPoint getEnd() {
		return this.end;
	}

	/**
	 * Set edge start point
	 *
	 * @param p
	 */
	public void setStart(MyPoint p) {
		this.start = p;
	}

	/**
	 * Set edge end point
	 *
	 * @param p
	 */
	public void setEnd(MyPoint p) {
		this.end = p;
	}

	/**
	 * get squared 2D length
	 */
	protected double getSquared2DLength() {
		return start.squareDistance_2D(end);
	}

	/**
	 * get 2D length
	 */
	public double get2DLength() {
		return Math.sqrt(getSquared2DLength());
	}

	/**
	 * get squared 3D length
	 */
	protected double getSquared3DLength() {
		return start.squareDistance(end);
	}

	/**
	 * get 3D length
	 */
	public double get3DLength(MyPoint p) {
		return Math.sqrt(getSquared3DLength());
	}

	/**
	 * get the mark of the edge
	 * @return marked
	 */
	public boolean isMarked() {
		return (this.marked != 0);
	}

	/**
	 * set the mark of the edge
	 * @param marked
	 */
	public void setMarked(boolean marked) {
		if (marked)
			this.marked = 1;
		else
			this.marked = 0;
	}

	
	/**
	 * check if edge is taken into account in the triangularization
	 * @return outsideMesh
	 */
	public boolean isOutsideMesh() {
		return (this.outsideMesh != 0);
	}

	/**
	 * set the edge in the triangularization or not
	 * @param outsideMesh
	 */
	public void setOutsideMesh(boolean outsideMesh) {
		if (outsideMesh)
			this.outsideMesh = 1;
		else
			this.outsideMesh = 0;
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
		MyPoint p3 = this.start;
		MyPoint p4 = this.end;
		// (x2 - x1) t1 - (x4 - x3) t2 = (x3 - x1)
		// (y2 - y1) t1 - (y4 - y3) t2 = (y3 - y1)

		double a1 = p2.getX() - p1.getX();
		double b1 = p4.getX() - p3.getX();
		double c1 = p3.getX() - p1.getX();
		double a2 = p2.getY() - p1.getY();
		double b2 = p4.getY() - p3.getY();
		double c2 = p3.getY() - p1.getY();
		double t1, t2;
		double epsilon = MyTools.epsilon;

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
			if (Math.abs(p2.getX() - p1.getX()) > epsilon)
				t1 = (p3.getX() - p1.getX()) / (p2.getX() - p1.getX());
			else
				t1 = (p3.getY() - p1.getY()) / (p2.getY() - p1.getY());

			if ((-epsilon > t1) || (t1 > 1 + epsilon)) {
				// Check if p4 is between p1 and p2
				if (Math.abs(p2.getX() - p1.getX()) > epsilon)
					t1 = (p4.getX() - p1.getX()) / (p2.getX() - p1.getX());
				else
					t1 = (p4.getY() - p1.getY()) / (p2.getY() - p1.getY());

				if ((-epsilon > t1) || (t1 > 1 + epsilon)) {
					// Check if p1 is between p3 and p4
					if (Math.abs(p4.getX() - p3.getX()) > epsilon)
						t1 = (p1.getX() - p3.getX()) / (p4.getX() - p3.getX());
					else
						t1 = (p1.getY() - p3.getY()) / (p4.getY() - p3.getY());

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
		MyPoint p3 = this.start;
		MyPoint p4 = this.end;

		// (x2 - x1) t1 - (x4 - x3) t2 = (x3 - x1)
		// (y2 - y1) t1 - (y4 - y3) t2 = (y3 - y1)

		double a1 = p2.getX() - p1.getX();
		double b1 = p4.getX() - p3.getX();
		double c1 = p3.getX() - p1.getX();
		double a2 = p2.getY() - p1.getY();
		double b2 = p4.getY() - p3.getY();
		double c2 = p3.getY() - p1.getY();
		double epsilon = MyTools.epsilon;

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
					double x = p2.getX() * t1 + (1 - t1) * p1.getX();
					double y = p2.getY() * t1 + (1 - t1) * p1.getY();
					// double z = p2.getZ() * t1 + (1 - t1) * p1.getZ();
					double z = p4.getZ() * t2 + (1 - t2) * p3.getZ();

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
		return getIntersection(anEdge.start, anEdge.end);
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

		MyPoint p1 = this.start;
		MyPoint p2 = this.end;

		// x = x2 t1 + (1 - t1) x1
		// y = y2 t1 + (1 - t1) y1
		// z = z2 t1 + (1 - t1) z1

		// (x2 - x1) t1 = (x - x1)
		// (y2 - y1) t1 = (y - y1)

		// t1 = (x - x1) / (x2 - x1)
		// t1 = (y - y1) / (y2 - y1)
		double t1, t2;

		double a1 = p2.getX() - p1.getX();
		double c1 = p.getX() - p1.getX();
		double a2 = p2.getY() - p1.getY();
		double c2 = p.getY() - p1.getY();
		double epsilon = MyTools.epsilon;

		if (Math.abs(a1) > epsilon) {
			t1 = c1 / a1;
			if ((-epsilon < t1) && (t1 < 1 + epsilon)) {
				// p.getX() is between p1.getX() and p2.getX()
				if (Math.abs(a2) > epsilon) {
					t2 = c2 / a2;
					if ((-epsilon < t2) && (t2 < 1 + epsilon)
							&& (Math.abs(t1 - t2) < epsilon))
						// same t value => ok
						isInside = true;
				} else if (Math.abs(c2) < epsilon) {
					// p1.getY(), p2.getY() and p.getY() are the same
					isInside = true;
				}
			}
		} else if (Math.abs(c1) < epsilon) {
			// p1.getX(), p2.getX() and p.getX() are the same
			if (Math.abs(a2) > epsilon) {
				t2 = c2 / a2;
				if ((-epsilon < t2) && (t2 < 1 + epsilon))
					isInside = true;
			} else if (Math.abs(c2) < epsilon) {
				// p1.getY(), p2.getY() and p.getY() are also the same
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

		if (this.start.squareDistance_2D(p) < MyTools.epsilon)
			isExtremity = true;
		else if (this.end.squareDistance_2D(p) < MyTools.epsilon)
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
		MyPoint p1 = this.start;
		MyPoint p2 = this.end;
		double ux = p2.getX() - p1.getX();
		double uy = p2.getY() - p1.getY();
		double vx = p.getX() - p1.getX();
		double vy = p.getY() - p1.getY();
		double res = ux * vy - uy * vx;

		return res > 0;
	}

	/**
	 * Check if the point p is on the right
	 *
	 * @param p
	 * @return
	 */
	public boolean isRight(MyPoint p) {
		MyPoint p1 = this.start;
		MyPoint p2 = this.end;
		double ux = p2.getX() - p1.getX();
		double uy = p2.getY() - p1.getY();
		double vx = p.getX() - p1.getX();
		double vy = p.getY() - p1.getY();
		double res = ux * vy - uy * vx;
		return res < 0;
	}

	/**
	 * Swap the 2 points of the edge
	 * also swap connected triangles
	 */
	public void swap() {
		// swap points
		MyPoint aPoint = this.end;
		this.end = this.start;
		this.start = aPoint;

		// swap triangles
		MyTriangle aTriangle = left;
		left = right;
		right = aTriangle;
	}

	/**
	 * Check if the edge is flat or not
	 *
	 * @return isFlat
	 */
	public boolean isFlatSlope() {
		boolean isFlat = true;
		if (Math.abs(this.start.getZ() - this.end.getZ()) > MyTools.epsilon)
			isFlat = false;
		return isFlat;
	}

	/**
	 * Get the barycenter of the triangle
	 *
	 * @return isFlat
	 */
	public MyPoint getBarycenter() {
		double x = (this.start.getX()+this.end.getX())/2.0;
		double y = (this.start.getY()+this.end.getY())/2.0;
		double z = (this.start.getZ()+this.end.getZ())/2.0;
		return new MyPoint(x, y, z);
	}

	/**
	 * Get edge hashCode as min hasCode of its points
	 *
	 * @param p
	 * @return
	 */
	public int hashCode() {
		MyPoint p1 = this.start;
		MyPoint p2 = this.end;
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
	protected void setColor(Graphics g) {
		((Graphics2D) g).setStroke(new BasicStroke(1));
		if (getProperty() != 0 ) {
			g.setColor(Color.red);
			((Graphics2D) g).setStroke(new BasicStroke(2));
		} else if (marked == 1) {
			g.setColor(Color.CYAN);
		} else if (marked == 2) {
			g.setColor(Color.pink);
		} else
			g.setColor(Color.black);
	}

	/**
 	 * Display the edge in a JPanel
	 * @param g
	 * @param decalageX
	 * @param decalageY
	 * @param minX
	 * @param minY
	 * @param scaleX
	 * @param scaleY
	 */
	protected void displayObject(Graphics g, int decalageX, int decalageY,
			double minX, double minY, double scaleX, double scaleY) {
		setColor(g);
		g.drawLine((int) ((this.start.getX() - minX) * scaleX + decalageX), decalageY
				+ (int) ((this.start.getY() - minY) * scaleY),
				(int) ((this.end.getX() - minX) * scaleX + decalageX), decalageY
						+ (int) ((this.end.getY() - minY) * scaleY));
		if (marked > 0) {
			this.start.displayObject(g, decalageX, decalageY, minX, minY, scaleX,
					scaleY);
			this.end.displayObject(g, decalageX, decalageY, minX, minY, scaleX,
					scaleY);
		}
	}
/*
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
		if (pente == null) {
			Coordinate d = new Coordinate();
			Geometry g = this.geom;
			d.getX() = g.getCoordinates()[1].getX() - g.getCoordinates()[0].getX();
			d.getY() = g.getCoordinates()[1].getY() - g.getCoordinates()[0].getY();
			d.getZ() = g.getCoordinates()[1].getZ() - g.getCoordinates()[0].getZ();
			double norme = Math.sqrt(d.getX() * d.getX() + d.getY() * d.getY() + d.getZ() * d.getZ());
			// normage du vecteur
			if (norme > 0) {
				d.getX() = d.getX() / norme;
				d.getY() = d.getY() / norme;
				d.getZ() = d.getZ() / norme;
			}
			pente = d;
		}
		return pente;
		return get3DVector;

	}
*/
	public int getGradient() {
		int gradient;
		if (getStart().getZ() > getEnd().getZ()) {
			gradient = MyEdge.DOWNSLOPE;
		} else if (getStart().getZ() < getEnd().getZ()) {
			gradient = MyEdge.UPSLOPE;
		} else {
			gradient = MyEdge.FLATSLOPE;
		}
		return gradient;
	}

}
