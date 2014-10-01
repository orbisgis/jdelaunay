/**
 *
 * jDelaunay is a library dedicated to the processing of Delaunay and constrained
 * Delaunay triangulations from PSLG inputs.
 *
 * This library is developed at French IRSTV institute as part of the AvuPur and Eval-PDU project,
 * funded by the French Agence Nationale de la Recherche (ANR) under contract
 * ANR-07-VULN-01 and ANR-08-VILL-0005-01 .
 *
 * jDelaunay is distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2010-2012 IRSTV FR CNRS 2488
 *
 * jDelaunay is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * jDelaunay is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * jDelaunay. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.jdelaunay.delaunay.geometries;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.util.LinkedList;
import java.util.Map;
import org.jdelaunay.delaunay.error.DelaunayError;
import org.jdelaunay.delaunay.tools.Tools;

/**
 * An edge in the tringulation. A DEdge is formed with two DPoint instances.
 *
 * A DEdge is linked to up to two DTriangle : one on its left, and one on its right.
 * This DEdge is an edge of these DTriangle. The left and right sides are relative
 * to the orientation of the edge.
 *
 * Indeed, a DEdge has a start and an end. If you want to know the leftmost point,
 * you can use getPointLeft (resp getPointRight for the rightmost point). The leftmost
 * point is not necessarily the start point.
 *
 * To swap the start and the end, you can use the swap() method.
 *
 * @author Adelin Piau
 * @author Jean-Yves Martin
 * @author Erwan Bocher
 */
public class DEdge extends Element implements Comparable<DEdge> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DPoint startPoint, endPoint;
	private DTriangle left, right;
	//An edge is considered to be degenerated if it is connected to the boundary
	//of the mesh, but is not part of any triangle. It is the case when adding 
	//a point to the mesh that can't see any point of the boundary, because of
	//the existing constraints.
	private transient boolean degenerated = false;
	//A DEdge is said to be shared when it is used by two differents BoundaryParts
	//during the building of the mesh.
	private transient boolean shared = false;
	/**
	 * bit number  | function :
	 * 1			| isOutsideMesh / setOutsideMesh
	 * 2			| isLocked / setLocked
	 * 3			| isLevelEdge / setLevelEdge
	 * 4			| isUseByPolygon / setUseByPolygon
	 * 6 to 32		| isMarked / setMarked
	 */
	private boolean indicator;
	public static final int UPSLOPE = -1;
	public static final int DOWNSLOPE = 1;
	public static final int FLATSLOPE = 0;
	//Intersection constants :
	/**
	 * Value returned by the intersects method when the two edges don't intersect
	 */
	public static final int NO_INTERSECTION=0;
	/**
	 * Value returned by the intersects method when the two edges intersect in one point
	 *
	 */
	public static final int INTERSECT=1;
	/**
	 * Value returned by the intersects method when the two edges are colinear and don't
	 * intersect
	 */
	public static final int COLINEAR=2;
	/**
	 * Value returned by the intersects method when the two edges intersect in one
	 * point that is an extremity for both of them.
	 */
	public static final int SHARE_EXTREMITY=3;
	/**
	 * Value returned by the intersects method when the two edges intersect in
	 * more than one point (ie they are colinear and do not just share their extremities).
	 */
	public static final int SHARE_EDGE_PART=4;


	/**
	 * Initialize data.
	 */
	private void init() {
		startPoint = null;
		endPoint = null;
		left = null;
		right = null;
		indicator = false;
	}

	/**
	 * Generate a new edge. The two points that define the DEdge are set to null,
	 * it's up to you to use another constructor or to fill the extremities with
	 * actual values.
	 */
	public DEdge() {
		super();
		init();
	}

	/**
	 * Generate an edge from two points.
	 *
	 * @param start
	 * @param end
	 */
	public DEdge(DPoint start, DPoint end) {
		super();
		init();
		this.startPoint = start;
		this.endPoint = end;
	}

	/**
	 * Generate an edge from another edge.
	 *
	 * @param ed
	 */
	public DEdge(DEdge ed) {
		super((Element) ed);
		init();
		this.startPoint = ed.startPoint;
		this.endPoint = ed.endPoint;
		this.left = ed.left;
		this.right = ed.right;
		this.indicator = ed.indicator;
		setProperty(ed.getProperty());
	}

	/**
	 * Create a new edge given the coordinates of its two extremities
	 * @param x
	 * @param y
	 * @param z
	 * @param u
	 * @param v
	 * @param w
	 */
	public DEdge(double x, double y, double z, double u, double v, double w) throws DelaunayError {
		super();
		init();
                DPoint p1 = new DPoint(x, y, z);
                DPoint p2 = new DPoint(u, v, w);
                this.startPoint = p1;
                this.endPoint = p2;
	}

	/**
	 * Overrides the default method. The properties will be added to the points
	 * that define this edge.
	 * @param prop
	 */
	@Override
	public final void addProperty(int prop){
		super.addProperty(prop);
		startPoint.addProperty(prop);
		endPoint.addProperty(prop);
	}

	/**
	 * @return DTriangle at the left of edge.
	 */
	public final DTriangle getLeft() {
		return this.left;
	}

	/**
	 * @return DTriangle at the right of edge.
	 */
	public final DTriangle getRight() {
		return this.right;
	}

	/**
	 * Return the left triangle if tri is the right one, the right triangle
	 * if tri is the left one, and null otherwise.
	 * @param tri
	 * @return
         *      The left (resp. right) triangle of this edge if tri is its right (resp. left)
         *      triangle, null otherwise.
	 */
	public final DTriangle getOtherTriangle(DTriangle tri){
		if(tri == null){
			return null;
		} else if(tri.equals(right)){
			return left;
		} else if(tri.equals(left)){
			return right;
		} else {
			return null;
		}
	}

	/**
	 * Set DTriangle at left of edge.
	 * 
	 * @param aTriangle A triangle at left of edge.
	 */
	public final void setLeft(DTriangle aTriangle) {
		this.left = aTriangle;
	}

	/**
	 * Set DTriangle at right of edge.
	 * 
	 * @param aTriangle A triangle at right of edge.
	 */
	public final void setRight(DTriangle aTriangle) {
		this.right = aTriangle;
	}

	/**
	 * @return start point of edge.
	 */
	public final DPoint getStartPoint() {
		return this.startPoint;
	}

	/**
	 * @return end point of edge.
	 */
	public final DPoint getEndPoint() {
		return this.endPoint;
	}

	/**
	 * Checks if this edge is "degenerated" or not. An edge is marked as degenerated
	 * when connecting to the mesh, but not implied in the building of any triangle.
	 * @return
         *      <code>true</code> if this is marked as degenerated.
	 */
	public final boolean isDegenerated(){
		return degenerated;
	}

	/**
	 * Determines if this edge must be considered as degenerated or not.
	 * @param degen
	 */
	public final void setDegenerated(boolean degen){
		degenerated=degen;
	}

	/**
	 * An edge is shared when in use by two boundary parts.
	 * @return
         *      <code>true</code> if this is shared between two boundary parts.
	 */
	public final boolean isShared(){
		return shared;
	}

	/**
	 * set the shared status of this DEdge.
	 * @param share
	 */
	public final void setShared(boolean share){
		shared = share;
	}

	/**
	 * Set edge start point.
	 *
	 * @param p Start point.
	 */
	public final void setStartPoint(DPoint p) {

		this.startPoint = p;
	}

	/**
	 * Set edge end point.
	 *
	 * @param p End point.
	 */
	public final void setEndPoint(DPoint p) {
		this.endPoint = p;
	}

	/**
	 * Get the point of this edge that is on the left from the other. 
	 * We use the order relation defined in DPoint. Consequently, with a vertical
	 * edge, this method return the minimum point, (so the one with the lowest
	 * y).
	 * @return
         * the leftmost point of this edge.
	 */
	public final DPoint getPointLeft() {
		int c = endPoint.compareTo2D(startPoint);
		switch (c) {
			case -1:
				return endPoint;
			default:
				return startPoint;
		}
	}

	/**
	 * Get the point of this edge that is on the left from the other.
	 * We use the order relation defined in DPoint.
	 * @return
         * Rhe rightmost point of this edge.
	 */
	public final DPoint getPointRight() {
		int c = endPoint.compareTo2D(startPoint);
		switch (c) {
			case 1:
				return endPoint;
			default:
				return startPoint;
		}
	}

	/**
	 * get squared 2D length
         * @return the square of the 2D-length of this edge.
	 */
	public final double getSquared2DLength() {
		return startPoint.squareDistance2D(endPoint);
	}

	/**
	 * get 2D length
         * @return The length of this edge in two dimensions.
         * 
	 */
	public final double get2DLength() {
		return Math.sqrt(getSquared2DLength());
	}

	/**
	 * get squared 3D length
         * @return the square of the 3D-length of this edge.
	 */
	protected final double getSquared3DLength() {
		return startPoint.squareDistance(endPoint);
	}

	/**
	 * get 3D length
         * @return The length of this edge in three dimensions.
	 */
	public final double get3DLength() {
		return Math.sqrt(getSquared3DLength());
	}
	/**
	 * get the mark of the edge
	 * @return true if this edge is locked.
         *      
	 */
	public final boolean isLocked() {
		return indicator;
	}

	/**
	 * set the mark of the edge
	 * @param locked
	 */
	public final void setLocked(boolean locked) {
		indicator = locked;
	}

	@Override
	public final BoundaryBox getBoundingBox() throws DelaunayError {

		BoundaryBox box = new BoundaryBox();
		box.alterBox(this.startPoint);
		box.alterBox(this.endPoint);

		return box;
	}

	@Override
	public final boolean contains(DPoint aPoint) {
		DPoint p1 = this.startPoint;
		DPoint p2 = this.endPoint;
		double ux = p2.getX() - p1.getX();
		double uy = p2.getY() - p1.getY();
		double vx = aPoint.getX() - p1.getX();
		double vy = aPoint.getY() - p1.getY();
		double res = ux * vy - uy * vx;
		boolean px = (ux >= 0 ? (p1.getX() - Tools.EPSILON <= aPoint.getX() && aPoint.getX() <= p2.getX() + Tools.EPSILON) :
			(p2.getX() - Tools.EPSILON <= aPoint.getX() && aPoint.getX() <= p1.getX() + Tools.EPSILON));/* px is in [p1x, p2x]*/
		boolean py = (uy >= 0 ? (p1.getY() - Tools.EPSILON <= aPoint.getY() && aPoint.getY() <= p2.getY() + Tools.EPSILON) : (
			p2.getY() - Tools.EPSILON <= aPoint.getY() && aPoint.getY() <= p1.getY() + Tools.EPSILON));/* py is in [p1y, p2y]*/
		return res <= Tools.EPSILON && res >= -Tools.EPSILON/* p is on p1, p2 line */
			&& px && py;
	}

	/**
	 * Get the euclidean distance between p and the line defined by this edge.
	 * @param p
	 * @return
         *      the minimal distance between this edge and p.
	 */
	public final double getDistance2D(DPoint p){
		if(this.isVertical()){
			return Math.abs(p.getX() - startPoint.getX());
		} else {
			double a = (endPoint.getY() - startPoint.getY())/(endPoint.getX()-startPoint.getX());
			double b = endPoint.getY() - a * endPoint.getX();
			return Math.abs(a * p.getX() - p.getY() + b)/Math.sqrt(1+a*a);
		}
	}

	/**
	 * Check if this and other intersect.
	 * @param other
	 * @return intersection :<br/>
	 * 			0 = no intersection<br/>
	 * 			1 = intersects<br/>
	 * 			2 = co-linear<br/>
	 * 			3 = intersects at the extremity<br/>
	 *			4 = intersect in more than one point<br/>
	 * note that if on extremity of an edge lies inside the other edge, but
	 * is not one of the extremities of the other edge, this method
	 * returns 1
	 */
	public final int intersects(DEdge other) throws DelaunayError{
		return intersects(other.getStartPoint(), other.getEndPoint());
	}

	/**
	 * check if two edges intersect
	 *
	 * @param p1 the start point of the other edge
	 * @param p2 the end point of the other edge
	 * @return intersection :<br/>
	 * 			NO_INTERSECTION = no intersection<br/>
	 * 			INTERSECT = intersect<br/>
	 * 			COLINEAR = co-linear and don't intersect<br/>
	 * 			SHARE_EXTREMITY = intersects at the extremity<br/>
	 *			SHARE_EDGE_PART = intersect in more than one point<br/>
	 * note that if on extremity of an edge lies inside the other edge, but
	 * is not one of the extremities of the other edge, this method
	 * returns 1
	 */
	public final int intersects(DPoint p1, DPoint p2) throws DelaunayError {
		DPoint p3 = this.startPoint;
		DPoint p4 = this.endPoint;
		Element inter = getIntersection(p1, p2, false);
		if(inter==null){//there is no intersection, return 0 or 2
			// (x2 - x1) t1 - (x4 - x3) t2 = (x3 - x1)
			// (y2 - y1) t1 - (y4 - y3) t2 = (y3 - y1)
			double a1 = p2.getX() - p1.getX();
			double b1 = p4.getX() - p3.getX();
			double a2 = p2.getY() - p1.getY();
			double b2 = p4.getY() - p3.getY();

			// d = (x4 - x3) (y2 - y1) - (x2 - x1) * (y4 - y3)
			double d = b1 * a2 - b2 * a1;
			if(-Tools.EPSILON2 < d && d < Tools.EPSILON2){
				//the two edges are colinear
				return COLINEAR;
			}else {
				return NO_INTERSECTION;
			}
		} else if(inter instanceof DPoint){
			//intersection in one point,
			//return 1 or 3
			DPoint interPoint = (DPoint) inter;
			if((interPoint.squareDistance2D(p1)<Tools.EPSILON2 ||
				interPoint.squareDistance2D(p2)<Tools.EPSILON2)&&
				(interPoint.squareDistance2D(p3)<Tools.EPSILON2||
				interPoint.squareDistance2D(p4)<Tools.EPSILON2)){
				//intersection at an extremity of each edge.
				return SHARE_EXTREMITY;
			} else {
				return INTERSECT;
			}
			
		} else if(inter instanceof DEdge){
			//intersection in more than
			//one point, return 4
			return SHARE_EDGE_PART;
		}
		return NO_INTERSECTION;
	}

	/**
	 * intersects two edges returns null if there is no intersection
	 *
	 * @param p1
	 * @param p2
	 * @return the intersection, as an element instance. it can be an DEdge, a DPoint
         * or null.
	 * @throws DelaunayError 
	 */
	public final Element getIntersection(DPoint p1, DPoint p2) throws DelaunayError {
		return getIntersection(p1, p2, false);
	}

	/**
	 * intersects two edges returns null if there is no intersection
	 * if the two edgse are colinear, returns the minimum intersection point,
	 * if such a point exists.
	 * @param point1
	 * @param point2
	 * @param useCoordZOfp1p2 If true, the coordinate of intersection get in Z the average of p1 and p2 Z. Don't care of p3 and, p4 Z.
	 * Else if false, the coordinate of intersection get in Z the average of p1, p2, p3 and p4 Z.
	 * @return the intersection, as an element instance. it can be an DEdge, a DPoint
         * or null.
	 * @throws DelaunayError 
	 */
	public final Element getIntersection(DPoint point1, DPoint point2, boolean useCoordZOfp1p2) throws DelaunayError {
		Element intersection = null;
		DPoint p3 = getPointLeft();
		DPoint p4 = getPointRight();
		DPoint p1 ;
		DPoint p2 ;
		switch(point1.compareTo2D(point2)){
			//we put the leftmost point in p1, and the rightmost in p2
			case 1 :
				p1=point2;
				p2=point1;
				break;
			default:
				p1=point1;
				p2=point2;
		}

		// (x2 - x1) t1 - (x4 - x3) t2 = (x3 - x1)
		// (y2 - y1) t1 - (y4 - y3) t2 = (y3 - y1)

		double deltaXO = p2.getX() - p1.getX();
		double deltaXT = p4.getX() - p3.getX();
		double c1 = p3.getX() - p1.getX();
		double deltaYO = p2.getY() - p1.getY();
		double deltaYT = p4.getY() - p3.getY();
		double c2 = p3.getY() - p1.getY();

		// d = (x4 - x3) (y2 - y1) - (x2 - x1) * (y4 - y3)
		double d = deltaXT * deltaYO - deltaYT * deltaXO;
		if (Math.abs(d) > Tools.EPSILON) {
			//The two edges are not colinear.
			if(p1.compareTo2D(p4)==1 || p3.compareTo2D(p2)==1){
				return null;
			}
			// t1 = ((y3 - y1) (x4 - x3) - (x3 - x1) (y4 - y3)) / d
			// t2 = ((x2 - x1) (y3 - y1) - (y2 - y1) (x3 - x1)) / d

			double t1 = (c2 * deltaXT - c1 * deltaYT) / d;
			double t2 = (deltaXO * c2 - deltaYO * c1) / d;

			if ((-Tools.EPSILON2 <= t1) && (t1 <= 1 + Tools.EPSILON2) && (-Tools.EPSILON2 <= t2)
				&& (t2 <= 1 + Tools.EPSILON2)) {
				// it intersects
				if (t2 <= Tools.EPSILON2) {
					intersection = p3;
					if(useCoordZOfp1p2){
						double z = p2.getZ() * t1 + (1 - t1) * p1.getZ();
						p3.setZ(z);
					}
				} else if (t2 >= 1 - Tools.EPSILON2) {
					intersection = p4;
					if(useCoordZOfp1p2){
						double z = p2.getZ() * t1 + (1 - t1) * p1.getZ();
						p4.setZ(z);
					}
				} else if (t1 <= Tools.EPSILON2) {
					intersection = p1;
					if(!useCoordZOfp1p2){
						double z = p4.getZ() * t2 + (1 - t2) * p3.getZ();
						p1.setZ(z);
					}
				} else if (t1 >= 1 - Tools.EPSILON2) {
					intersection = p2;
					if(!useCoordZOfp1p2){
						double z = p4.getZ() * t2 + (1 - t2) * p3.getZ();
						p2.setZ(z);
					}
				} else {
					// x = x2 t1 + (1 - t1) x1
					// y = y2 t1 + (1 - t1) y1
					// z = z2 t1 + (1 - t1) z1
					// z = z4 t2 + (1 - t2) z3
					double x = p2.getX() * t1 + (1 - t1) * p1.getX();
					double y = p2.getY() * t1 + (1 - t1) * p1.getY();


					double z = 0;
					if (useCoordZOfp1p2) {
						// Average of p1 and p2 Z. Don't care of p3 and p4 Z.
						z = p2.getZ() * t1 + (1 - t1) * p1.getZ();
					} else {
						// Average of p1, p2, p3 and p4 Z.
						z = p4.getZ() * t2 + (1 - t2) * p3.getZ();
					}
					intersection = new DPoint(x, y, z);
                    intersection.setProperty(getProperty());
				}
			} else if ((-Tools.EPSILON <= t1) && (t1 <= 1 + Tools.EPSILON) && (-Tools.EPSILON <= t2)
				&& (t2 <= 1 + Tools.EPSILON)) {
				if(getDistance2D(p1)<Tools.EPSILON){
					return p1;
				}
				if(getDistance2D(p2)<Tools.EPSILON){
					return p2;
				}
				DEdge other = new DEdge(p1, p2);
				if(other.getDistance2D(p3)<Tools.EPSILON){
					return p3;
				}
				if(other.getDistance2D(p4)<Tools.EPSILON){
					return p4;
				}
			}
		} else { //d==0 : the two edges are colinear
			double test;
			if (Math.abs(deltaXO) < Tools.EPSILON2) {
				test = c1 / deltaXT - c2 / deltaYT;
			} else {
				test = c1 / deltaXO - c2 / deltaYO;
			}
			if (Math.abs(test) > Tools.EPSILON) {//the two supporting lines are different
				intersection = null;
			} else {//we have one supporting line
				//t13 is the position of the point three on the edge 1->2
				double t13, t14, t21, t22;
				if (Math.abs(deltaXO) < Tools.EPSILON) {
					t13 = c2 / deltaYO;
					t14 = (p4.getY() - p1.getY()) / (deltaYO);
				} else {
					t13 = c1 / deltaXO;
					t14 = (p4.getX() - p1.getX()) / (deltaXO);
				}
				if (Math.abs(deltaXT) > Tools.EPSILON) {
					t21 = -c1 / deltaXT;
					t22 = (p2.getX() - p3.getX()) / deltaXT;
				} else {
					t21 = -c2 / deltaYT;
					t22 = (p2.getY() - p3.getY()) / (deltaYT);
				}
				if (-Tools.EPSILON2 < t13 && t13 < 1 + Tools.EPSILON2) {
					if (-Tools.EPSILON2 < t14 && t14 < 1 + Tools.EPSILON2) {
                                                //p3 and p4 are both on the edge [p1 p2]
						if(!useCoordZOfp1p2){
							double z = p2.getZ() * t14 + (1 - t14) * p1.getZ();
							p4.setZ(z);
							z = p2.getZ() * t13 + (1 - t13) * p1.getZ();
							p3.setZ(z);
						}
						intersection = new DEdge(p3, p4);
					} else {
                                                //p4 is not on [p1 p2]
						if (p3.squareDistance2D(p1) < Tools.EPSILON2) {
                                                        //p3 and p1 are equal
							if (-Tools.EPSILON2 < t22 && t22 < 1 + Tools.EPSILON2) {
                                                                //p2 is on [p3 p4]
								intersection = new DEdge(p1, p2);
							} else {
                                                                //p2 is not on [p3 p4], and p3 is not on [p1 p2]
								intersection = p3;
							}
						} else if (p3.squareDistance2D(p2) < Tools.EPSILON2) {
                                                        //p3 and p2 are equals
							if (-Tools.EPSILON2 < t21 && t21 < 1 + Tools.EPSILON2) {
                                                                //p1 is on [p3 p4]
								intersection = new DEdge(p1, p2);
							} else {
                                                                //p1 is not on [p3 p4], and p3 is not on [p1 p2]
								intersection = p3;
							}

						} else if (-Tools.EPSILON2 < t21 && t21 < 1 + Tools.EPSILON2) {
                                                        //p1 is on [p3 p4]
							intersection = new DEdge(p1, p3);
						} else {
							intersection = new DEdge(p2, p3);
						}
					}
				} else if (-Tools.EPSILON2 < t14 && t14 < 1 + Tools.EPSILON2) {
                                //p3 is not on [p1 p2], but p4 is on it
					if (p4.squareDistance2D(p1) < Tools.EPSILON2) {
                                                //p4 and p1 are equal
						if (-Tools.EPSILON2 < t22 && t22 < 1 + Tools.EPSILON2) {
                                                        //p2 is on [p3 p4]
							intersection = new DEdge(p1, p2);
						} else {
                                                        //p2 is not on [p3 p4] and p3 is not on [p1 p2]
							intersection = p4;
						}
					} else if (p4.squareDistance2D(p2) < Tools.EPSILON2) {
                                                //p4 and p1 are equal
						if (-Tools.EPSILON2 < t21 && t21 < 1 + Tools.EPSILON2) {
                                                        //p1  is on [p3 p4]
							intersection = new DEdge(p1, p2);
						} else {
                                                        //p1 is not on [p3 p4] and p3 is not on [p1 p2]
							intersection = p4;
						}

					} else if (-Tools.EPSILON2 < t21 && t21 < 1 + Tools.EPSILON2) {
                                                //p1 is on [p3 p4]
						intersection = new DEdge(p1, p4);
					} else {
						intersection = new DEdge(p2, p4);
					}
				} else if (Tools.EPSILON2 < t21 && t21 < 1 - Tools.EPSILON2) {
                                        //p1 is on [p3 p4]. As we've seen, nor p4 neither p3 are
                                        // on [p1 p2], so we can conclude that the intersection is [p1 p2]
					intersection = new DEdge(p1, p2);
				}

			}

		}
		return intersection;
	}

	/**
	 * intersects two edges returns null if there is no intersection
	 *
	 * @param ed
	 * @return intersection
	 * @throws DelaunayError 
	 */
	public final Element getIntersection(DEdge ed) throws DelaunayError {
		return getIntersection(ed.startPoint, ed.endPoint);
	}

	/**
	 * Get the intersection, using the weights given in argument to compute the z :
	 * we will use the z from the edge with the highest weight.
	 * @param ed
	 * @param weights
	 * @return
         *      The intersection, as an element instance. It can be a DPoint, a DEdge, or null.
	 * @throws DelaunayError
	 */
	public final Element getIntersection(DEdge ed, Map<Integer,Integer> weights) throws DelaunayError {
		if(weights.isEmpty()){
			return getIntersection(ed.startPoint, ed.endPoint, false);
		}
		int wt = getMaxWeight(weights);
		int wo = ed.getMaxWeight(weights);
		if(wo>wt){
			return getIntersection(ed.startPoint, ed.endPoint,true);
		} else {
			return getIntersection(ed.startPoint, ed.endPoint, false);
		}
	}

	/**
         * Make a linear interpolation for the points that lie on the edge.
	 * @param p
	 * @return Z coordinate of the point p, if it is on the edge. It it is outside
         *  the edge, <code>Double.NaN</code> is returned instead.
	 */
	public final double getZOnEdge(DPoint p) {
                if(!isOnEdge(p)){
                        return Double.NaN;
                }
		DPoint p1 = this.startPoint;
		DPoint p2 = this.endPoint;
		if (Math.abs(p2.getX()-p1.getX())<Tools.EPSILON) {
			return (p2.getY() - p.getY()) * (p2.getZ() - p1.getZ()) / (p2.getY() - p1.getY());
		} else {
			return (p2.getX() - p.getX()) * (p2.getZ() - p1.getZ()) / (p2.getX() - p1.getX());
		}
	}

	/**
	 * Get the slope of the edge
	 * @return
         *      The slope of the edge, in radians.
	 */
	public final double getSlope() {
		double dz = endPoint.getZ() - startPoint.getZ();
                if(dz==0){
                    return 0;
                }
		double projSize = get2DLength();
		if(Math.abs(projSize)<Tools.EPSILON){
			return Double.NaN;
		} else {
			return dz / projSize;
		}
	}

	/**
	 * Get the slope of the edge in degree.
	 * @return
         *      The slope of the edge, in degrees.
	 */
	public final double getSlopeInDegree() {
		return Math.toDegrees(Math.atan(getSlope()));
	}

	/**
	 * Get the direction vector of the associated line.
	 * @return
         *      a DPoint that represents the direction vector of the associated line.
	 * @throws DelaunayError
	 */
	public final DPoint getDirectionVector() throws DelaunayError {
		double x = endPoint.getX() - startPoint.getX();
		double y = endPoint.getY() - startPoint.getY();
		double z = endPoint.getZ() - startPoint.getZ();
		double size = get3DLength();
		return new DPoint(x/size, y/size, z/size);
	}

	/**
	 * check if the point is between the extremities of the edge (on the
	 * xy-plane)
	 *
	 * @param p
	 * @return isInside
	 */
	public final boolean isInside(DPoint p) {
		boolean isInside = false;

		DPoint p1 = this.startPoint;
		DPoint p2 = this.endPoint;

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

		if (Math.abs(a1) > Tools.EPSILON) {
			t1 = c1 / a1;
			if ((-Tools.EPSILON < t1) && (t1 < 1 + Tools.EPSILON)) {
				// p.getX() is between p1.getX() and p2.getX()
				if (Math.abs(a2) > Tools.EPSILON) {
					t2 = c2 / a2;
					if ((-Tools.EPSILON < t2) && (t2 < 1 + Tools.EPSILON)
						&& (Math.abs(t1 - t2) < Tools.EPSILON)) {
						isInside = true;
					}
				} else if (Math.abs(c2) < Tools.EPSILON) {
					// p1.getY(), p2.getY() and p.getY() are the same
					isInside = true;
				}
			}
		} else if (Math.abs(c1) < Tools.EPSILON) {
			// p1.getX(), p2.getX() and p.getX() are the same
			if (Math.abs(a2) > Tools.EPSILON) {
				t2 = c2 / a2;
				if ((-Tools.EPSILON < t2) && (t2 < 1 + Tools.EPSILON)) {
					isInside = true;
				}
			} else if (Math.abs(c2) < Tools.EPSILON) {
				// p1.getY(), p2.getY() and p.getY() are also the same
				isInside = true;
			}

		}

		return isInside;
	}

	/**
	 * check if the point is colinear to the edge in the XY plane, ie if it lies
	 * on the line defined by this edge.
	 *
	 * @param p
	 * @return isColinear2D
	 */
	public final boolean isColinear2D(DPoint p) {
		boolean isColinear2D = false;

		DPoint p1 = this.startPoint;
		DPoint p2 = this.endPoint;

		double a1 = p2.getX() - p1.getX();
		double c1 = p.getX() - p1.getX();
		double a2 = p2.getY() - p1.getY();
		double c2 = p.getY() - p1.getY();
		double t = a1 * c2 - a2 * c1;
		if (Math.abs(t) < Tools.EPSILON) {
			isColinear2D = true;
		}

		return isColinear2D;
	}

	/**
	 * check if the point is colinear to the edge
	 *
	 * @param p
	 * @return isColinear2D
	 */
	public final boolean isColinear(DPoint p) {
		boolean isColinear = false;

		DPoint p1 = this.startPoint;
		DPoint p2 = this.endPoint;

		double a1 = p2.getX() - p1.getX();
		double c1 = p.getX() - p1.getX();
		double a2 = p2.getY() - p1.getY();
		double c2 = p.getY() - p1.getY();
		double a3 = p2.getZ() - p1.getZ();
		double c3 = p.getZ() - p1.getZ();
		double t1 = a1 * c2 - a2 * c1;
		double t2 = a1 * c3 - a3 * c1;
		double t3 = a3 * c2 - a2 * c3;
		if ((Math.abs(t1) < Tools.EPSILON) && (Math.abs(t2) < Tools.EPSILON) && (Math.abs(t3) < Tools.EPSILON)) {
			isColinear = true;
		}

		return isColinear;
	}

	/**
	 * Check if two edges have the same points.
	 * @param anEdge
	 * @return True if points are the same.
	 */
	public final boolean haveSamePoint(DEdge anEdge) {
		return (getStartPoint().equals(anEdge.getStartPoint())
			&& getEndPoint().equals(anEdge.getEndPoint()))
			|| (getStartPoint().equals(anEdge.getEndPoint())
			&& getEndPoint().equals(anEdge.getStartPoint()));
	}

	/**
	 * check if the point is one of the extremities of the edge (on the
	 * xy-plane)
	 *
	 * @param p
	 * @return isInside
	 */
	public final boolean isExtremity(DPoint p) {
		return (startPoint.equals2D(p) ) || (endPoint.equals2D(p) );
	}

	/**
	 * Check if the point p is on the left
	 *
	 * @param p
	 * @return
         *      true if p is on the left (strictly) of this point.
	 */
	public final boolean isLeft(DPoint p) {
		double ux = this.endPoint.getX() - this.startPoint.getX();
		double uy = this.endPoint.getY() - this.startPoint.getY();
		double vx = p.getX() - this.startPoint.getX();
		double vy = p.getY() - this.startPoint.getY();

		return ux * vy - uy * vx > Tools.EPSILON;
	}

	/**
	 * Check if the point p is on the right
	 *
	 * @param p
	 * @return
         *      true if p is on the right (strictly) of this point.
	 */
	public final boolean isRight(DPoint p) {
		double ux = this.endPoint.getX() - this.startPoint.getX();
		double uy = this.endPoint.getY() - this.startPoint.getY();
		double vx = p.getX() - this.startPoint.getX();
		double vy = p.getY() - this.startPoint.getY();

		return ux * vy - uy * vx < -Tools.EPSILON;
	}

	/**
	 * Check if the point p is on edge. Computation is made in two dimensions.
	 * @param p
	 * @return True if the point is on edge.
	 */
	public final boolean isOnEdge(DPoint p) {
		DPoint p1 = this.startPoint;
		DPoint p2 = this.endPoint;
		double ux = p2.getX() - p1.getX();
		double uy = p2.getY() - p1.getY();
		double vx = p.getX() - p1.getX();
		double vy = p.getY() - p1.getY();
		double res = ux * vy - uy * vx;

		boolean b1 = /* px is in [p1x, p2x]*/ (ux == 0
			? /*p2x == p1x ?*/ (p1.getX() == p.getX()) /* p2x == p1x == px ?*/
			: (ux > 0
			? /*p2x > p1x ?*/ (p1.getX() < p.getX() && p.getX() < p2.getX()) /* p2x > px > p1x ?*/
			: (p2.getX() < p.getX() && p.getX() < p1.getX())));

		boolean b2 = /* py is in [p1y, p2y]*/ (uy == 0
			? /* p2y == p1y ?*/ (p1.getY() == p.getY()) /* p2y == p1y == py ?*/
			: (uy > 0
			? /* p2y > p1y ?*/ (p1.getY() < p.getY() && p.getY() < p2.getY()) /* p2y > py > p1y ?*/
			: (p2.getY() < p.getY() && p.getY() < p1.getY())));

		return Math.abs(res) <= Tools.EPSILON/* p is on p1, p2 line */

			&& b1	/* p2x < px < p1x ?*/

			&& b2; /* p2y < py < p1y ?*/
	}

	/**
	 * Returns true if the two points of this edge have the same x coordinate.
	 * @return
         *      True if this is vertical
	 */
	public final boolean isVertical() {
		double dx = (startPoint.getX() - endPoint.getX());
		double delta = (dx < 0 ? -dx : dx);
		return delta < Tools.EPSILON;
	}

	/**
	 * This method retrieves the point that would stand on the line defined by
	 * this edge, and whose absciss is abs.
	 * if this is a vertical edge, the method returns :
	 *	the minimum point if this edge stand on the line x=abs
	 *	null otherwise
	 * @param abs
	 * @return
         *      The intersection of this with the line of absciss abs.
	 * @throws DelaunayError
         *      If this is vertical and this.x != abs.
	 */
	public final DPoint getPointFromItsX(double abs) throws DelaunayError {
		//We don't want to approximate accidentally our extremities !
		if(Math.abs(getPointLeft().getX() - abs)<Tools.EPSILON){
			return getPointLeft();
		} else if(Math.abs(getPointRight().getX() - abs)<Tools.EPSILON){
			return getPointRight();
		}
		double deltaX = (startPoint.getX() - endPoint.getX());
		double dX = (deltaX < 0 ? -deltaX : deltaX);
		double p = (abs - startPoint.getX()) / (endPoint.getX() - startPoint.getX());
		if (dX < Tools.EPSILON) {
			//the edge is vertical
			if (abs == startPoint.getX()) {//x is the absciss of every points in this edge
				//We return the minimum point.
				return getPointLeft();
			} else {//There is not any point of absciss X on this edge.
				return null;
			}
		} else {
			double y = startPoint.getY() + p * (endPoint.getY() - startPoint.getY());
			double z = startPoint.getZ() + p * (endPoint.getZ() - startPoint.getZ());
            DPoint dPoint = new DPoint(abs, y, z);
            dPoint.setProperty(getProperty());
			return dPoint;
		}
	}

	/**
	 * return the point of the edge that have the greatest Z coordinate.
	 * @return
         *      The uppest extremiety of this edge.
	 */
	public final DPoint getUpperPoint() {
		return startPoint.getZ() > endPoint.getZ() ? startPoint : endPoint;
	}

	/**
	 * Get the middle of the segment, in 3 dimensions.
	 * @return
         *      The middle of the edge, as a DPoint.
	 * @throws DelaunayError
	 */
	public final DPoint getMiddle() throws DelaunayError {
		double dx = endPoint.getX() - startPoint.getX();
		double dy = endPoint.getY() - startPoint.getY();
		double dz = endPoint.getZ() - startPoint.getZ();
        DPoint dPoint = new DPoint(startPoint);
        dPoint.setX(startPoint.getX()+dx/2);
        dPoint.setY(startPoint.getY()+dy/2);
        dPoint.setZ(startPoint.getZ()+dz/2);
		return dPoint;
	}

	/**
	 * An edge is said to be encroached in a mesh if :<br/>
         *   (The edge is a constraint OR an edge of the mesh' boundary)<br/>
         *   AND there is a point lying in the circle it is the diameter of.
	 * @return
	 *		true if there is such a point.
	 * @throws DelaunayError
	 */
	public final boolean isEncroached() throws DelaunayError{
		if(!isLocked() && left != null && right != null){
			return false;
		}
		DPoint middle = getMiddle();
		double length = getSquared2DLength()/4.0;
		DPoint other ;
		if(left!=null){
			other = left.getOppositePoint(this);
			if(other.squareDistance2D(middle)<length){
				return true;
                        }
                }
		if(right!=null){
			other = right.getOppositePoint(this);
			if(other.squareDistance2D(middle)<length){
				return true;
                        }
                }
                return false;        
	}
        
        /**
         * Test if this edge is encroached by the given point.
         * @param pt
         * @return
         *      true if pt encroaches this.
         * @throws DelaunayError 
         */
        public final boolean isEncroachedBy(DPoint pt) throws DelaunayError {
		if(!isLocked() && left != null && right != null){
			return false;
		}
		DPoint middle = getMiddle();
		double length = getSquared2DLength()/4.0;
                if(pt.squareDistance2D(middle)<length){
                        return true;
                }
                return false;
        }

	/**
	 * Swap the 2 points of the edge
	 * also swap connected triangles
	 */
	public final void swap() {
		// swap points
		DPoint aPoint = this.endPoint;
		this.endPoint = this.startPoint;
		this.startPoint = aPoint;

		// swap triangles
		DTriangle aTriangle = left;
		left = right;
		right = aTriangle;
	}

	/**
	 * Check if the edge is flat or not
	 *
	 * @return isFlat
	 */
	public final boolean isFlatSlope() {
		return !(Math.abs(this.startPoint.getZ() - this.endPoint.getZ()) > Tools.EPSILON);
	}

	/**
	 * Get the barycenter of the DEdge.
	 *
	 * @return barycenter point.
	 * @throws DelaunayError 
	 */
	public final DPoint getBarycenter() throws DelaunayError {
		double x = (this.startPoint.getX() + this.endPoint.getX()) / 2.0;
		double y = (this.startPoint.getY() + this.endPoint.getY()) / 2.0;
		double z = (this.startPoint.getZ() + this.endPoint.getZ()) / 2.0;
		return new DPoint(x, y, z);
	}

	/**
	 * Two edges are supposed to be equals if the points they are defined by are the
	 * same.
	 * @param other
	 * @return
         *      true if this and other are equal.
	 */
	@Override
	public final boolean equals(Object other){
		if (other instanceof DEdge){
			DEdge otherEdge = (DEdge) other;
			return (endPoint.equals(otherEdge.getEndPoint()) && startPoint.equals(otherEdge.getStartPoint()))
				|| (endPoint.equals(otherEdge.getStartPoint()) && startPoint.equals(otherEdge.getEndPoint()));
		}else{
			return false;
		}
	}

	/**
	 * Get edge hashCode as min hashCode of its points
	 *
	 * @return
         *  a hashcode.
	 */
	@Override
	public final int hashCode() {
		DPoint p1 = this.startPoint;
		DPoint p2 = this.endPoint;
		int hashValue = 0;
		int v1 = p1.hashCode();
		int v2 = p2.hashCode();
		if (v1 < v2) {
			hashValue = v1;
		} else {
			hashValue = v2;
		}

		return hashValue;
	}

	/**
	 * Set the edge color for the JFrame panel
	 *
	 * @param g
	 */
	protected final void setColor(Graphics g) {
		((Graphics2D) g).setStroke(new BasicStroke(1));
		if (getProperty() != 0) {
			g.setColor(Color.red);
			((Graphics2D) g).setStroke(new BasicStroke(2));
		} else if (isLocked()) {
			g.setColor(Color.CYAN);
		} else {
			g.setColor(Color.black);
		}
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
	public final void displayObject(Graphics g, int decalageX, int decalageY,
		double minX, double minY, double scaleX, double scaleY) {
		setColor(g);
		g.drawLine((int) ((this.startPoint.getX() - minX) * scaleX + decalageX),
			decalageY + (int) ((this.startPoint.getY() - minY) * scaleY),
			(int) ((this.endPoint.getX() - minX) * scaleX + decalageX), decalageY
			+ (int) ((this.endPoint.getY() - minY) * scaleY)); // coordinate 0 in Y is at bottom of screen
		if (isLocked()) {
			this.startPoint.displayObject(g, decalageX, decalageY, minX, minY, scaleX,
				scaleY);
			this.endPoint.displayObject(g, decalageX, decalageY, minX, minY, scaleX,
				scaleY);
		}
	}

	/**
	 * This method will be used to sort the edges using the following strategy.
	 * If we note leftP and rightP the leftmost and rightmost point of this
	 * this < edge if this.leftP < edge.leftP or (this.leftP == edge.leftP and this.rightP < edge.rightP)
	 * this == edge if this.leftP == edge.leftP and this.rightP == edge.rightP
	 * this > edge otherwise.
	 * @param edge
	 * @return
         *  -1 if this is inferior to edge, 0 if they are equal, 1 otherwise.
	 */
	public final int sortLeftRight(DEdge edge) {
		DPoint p1 = getPointLeft();
		DPoint p2 = edge.getPointLeft();
		int c = p1.compareTo2D(p2);
		if (c == 0) {
			p1 = getPointRight();
			p2 = edge.getPointRight();
			c = p1.compareTo2D(p2);
		}
		return c;
	}

	/**
	 * This method wil force the point marked as the end of the edge to be the point
	 * with the lower altitude.
	 */
	public final void forceTopographicOrientation() {
		double zEnd = endPoint.getZ();
		double zStart = startPoint.getZ();
		if(zStart < zEnd){
			swap();
		}
	}

	/**
	 * Realization of Compare. We use sortLeftRight here to sort our edges,
	 * not a vertical sort or something else...
	 * @param edge
	 * @return
         *  -1 if this is inferior to edge, 0 if they are equal, 1 otherwise.
	 */
	@Override
	public final int compareTo(DEdge edge){
		return sortLeftRight(edge);
	}

	/**
	 * Sort two edges (this and edge, indeed), and sort them according to their intersection point
	 * with the line l of equation x=abs.
	 * if p1 (p2) is the intersection between l and the line defined by this (edge),
	 * this method returns :
	 *  * -1 if p1 &lt; p2 or ( p1 == p2 and this is "under" edge)<br/>
	 *  * 0 if p1 == p2 and (this and edge are colinear)<br/>
	 *  * 1 if p1 &gt; p2 or (p1 == p2 and edge is under this)<br/>	 
         * 
	 *  * -1 if p1 &lt; p2 or ( p1 == p2 and this is "under" edge)<br/>
	 *  * 0 if p1 == p2 and (this and edge are colinear)<br/>
	 *  * 1 if p1 &gt; p2 or (p1 == p2 and edge is under this)<br/>
         * @param edge
         * @param abs
	 * @return
         * @throws org.jdelaunay.delaunay.error.DelaunayError
	 */
	public final int verticalSort(DEdge edge, double abs) throws DelaunayError {
		DPoint pThis = this.getPointFromItsX(abs);
		DPoint pEdge = edge.getPointFromItsX(abs);
		if (pThis == null ) {
			throw new DelaunayError("Trying to sort vertical edge, edge : "+this+", abs : "+abs);
		}
		if( pEdge == null){
			throw new DelaunayError("Trying to sort vertical edge, edge : "+edge+", abs : "+abs);
		}
		int c = pThis.compareTo2D(pEdge);
		if (c == 0) {
			if(this.isVertical()){
				c = this.getPointRight().compareTo2D(edge.getPointRight());
			} else if(edge.isVertical()){
				c = edge.getPointRight().compareTo2D(this.getPointRight());
			} else {
				double deltaXT = getPointRight().getX()-getPointLeft().getX();
				double deltaYT = getPointRight().getY()-getPointLeft().getY();
				double deltaXO = edge.getPointRight().getX()-edge.getPointLeft().getX();
				double deltaYO = edge.getPointRight().getY()-edge.getPointLeft().getY();
				double cT = deltaYT / deltaXT;
				double cO = deltaYO / deltaXO;
				if(-Tools.EPSILON < cT - cO && cT - cO < Tools.EPSILON){
					c = getPointRight().compareTo2D(edge.getPointRight());
					if(c==0){
						c = getPointLeft().compareTo2D(edge.getPointLeft());
					}
				} else if(cT < cO){
		//We are in the case where the two edges intersect at the given X-coordinate.
		//The one with the higher determinant has been the lower one until now, unless
		//we are only seeing its lef point.
					c=-1;
				} else {
					c=1;
				}
			}
		}
		return c;
	}

	/**
	 * @return gradient
	 */
	public final int getGradient() {
		int gradient;
		if (getStartPoint().getZ() > getEndPoint().getZ()) {
			gradient = DEdge.DOWNSLOPE;
		} else if (getStartPoint().getZ() < getEndPoint().getZ()) {
			gradient = DEdge.UPSLOPE;
		} else {
			gradient = DEdge.FLATSLOPE;
		}
		return gradient;
	}

        /**
	 * Compute the aspect of an edge.
         * Aspect is measured clockwise in degrees from 0, due north, to 360, again due north, coming full circle.
	 * @return 
         *      The aspect of this edge.
	 */
	public final double getSlopeAspect() {
			final double circleDegrees = 360.0;
			// l'ordre des coordonnees correspond a l'orientation de l'arc
			// "sommet haut vers sommet bas"
			double angleAxeXrad = startPoint.getZ() >= endPoint.getZ() ? Tools.angle(startPoint, endPoint) : Tools
					.angle(endPoint, startPoint);
			// on considere que l'axe nord correspond a l'axe Y positif
			double angleAxeNordrad = Tools.PI_OVER_2 - angleAxeXrad;
			double angleAxeNorddeg = Math.toDegrees(angleAxeNordrad);
			// on renvoie toujours une valeur d'angle >= 0		
		
		return angleAxeNorddeg < 0.0 ? circleDegrees + angleAxeNorddeg
					: angleAxeNorddeg;
	}

	/**
	 * Returns true if the triangle connected to the left of the edge is pouring
	 * into it.
	 * @return
         *      True if the left triangle is pouring to this.
         * @throws DelaunayError
	 */
	public final boolean isLeftTriangleGoToEdge() throws DelaunayError {
		if (left != null) {
			DPoint p = left.getOppositePoint(this);
			if (p.getZ() < startPoint.getZ() && p.getZ() < endPoint.getZ()) {
				return false;
			}

			return left.isTopoOrientedToEdge(this);
		}
		return false;
	}

	/**
	 * Returns true if the triangle connected to the right of the edge is pouring
	 * into it.
	 * @return
         *      True if the right triangle is pouring to this.
         * @throws DelaunayError
	 */
	public final boolean isRightTriangleGoToEdge() throws DelaunayError {
		if (right != null) {
			DPoint p = right.getOppositePoint(this);
			if (p.getZ() < startPoint.getZ() && p.getZ() < endPoint.getZ()) {
				return false;
			}

			return right.isTopoOrientedToEdge(this);
		}
		return false;
	}
	/**
	 * Gives a string representation of this object.
	 * @return "DEdge GID [Start : startPoint, End : endPoint]
	 */
	@Override
	public final String toString() {
		return "Edge " + getGID() + " [Start : " + startPoint + ", End : " + endPoint + "]";
	}
        
        /**
         * Perform a "deep swap" on this edge. The content of the left and right
         * triangles are inverted, then the references to the triangles are inverted too.<br/>
         * It can be useful when needing to invert two elements in a list without
         * knowing the indices of the elements, or in a non random access list.<br/>
         * This method is useful only when both the right and left triangles associated
         * to this edge are not null.
         */
        public final void deepSwap() {
                if(left != null && right != null){
                        DEdge el0 = left.getEdge(0);
                        DEdge el1 = left.getEdge(1);
                        DEdge el2 = left.getEdge(2);
                        LinkedList<DEdge> ll = new LinkedList<DEdge>();
                        ll.add(el0);
                        ll.add(el1);
                        ll.add(el2);
                        int gid = left.getGID();
                        int prop = left.getProperty();
                        left.setEdge(0, right.getEdge(0));
                        left.setEdge(1, right.getEdge(1));
                        left.setEdge(2, right.getEdge(2));
                        left.setGID(right.getGID());
                        left.setProperty(right.getProperty());
                        LinkedList<DEdge> rl = new LinkedList<DEdge>();
                        rl.add(right.getEdge(0));
                        rl.add(right.getEdge(1));
                        rl.add(right.getEdge(2));
                        right.setEdge(0, el0);
                        right.setEdge(1, el1);
                        right.setEdge(2, el2);
                        right.setGID(gid);
                        right.setProperty(prop);
                        for(DEdge ed : ll){
                                if(ed != this){
                                        if(ed.getLeft()==left ){
                                                ed.setLeft(right);
                                        } else {
                                                ed.setRight(right);
                                        }
                                }
                        }
                        for(DEdge ed : rl){
                                if(ed != this){
                                        if(ed.getLeft()==right && ed != this){
                                                ed.setLeft(left);
                                        } else {
                                                ed.setRight(left);
                                        }
                                }
                        }
                        DTriangle mem = left;
                        left = right;
                        right = mem;
                }
        }
        
        /**
         * This method takes care to ensure that the left and right triangles are
         * actually on the left and on the right, and not on the right and on the left.
         */
        public final void forceTriangleSide(){
                if(left != null){
                        DPoint pt = left.getOppositePoint(this);
                        if(isRight(pt)){
                                DTriangle dt = left;
                                left = right;
                                right = dt;
                        }
                } else if(right != null){
                        DPoint pt = right.getOppositePoint(this);
                        if (isLeft(pt)){
                                DTriangle dt = left;
                                left = right;
                                right = dt;
                        }
                }
        }
}
