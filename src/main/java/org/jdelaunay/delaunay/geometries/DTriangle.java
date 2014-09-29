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


import java.awt.Color;
import java.awt.Graphics;
import java.util.Arrays;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdelaunay.delaunay.error.DelaunayError;
import org.jdelaunay.delaunay.tools.Tools;

/**
 * This is the class representing a Triangle in the DelaunayTriangulation. A DTriangle
 * is made of three edges, each edge sharing a point with the other ones. Consequently,
 * a DTriangle can be identified thanks to its edges, or thanks to its associated DPoint
 * instances.
 *
 * @author Adelin Piau
 * @author Jean-Yves Martin
 * @author Erwan Bocher
 * @author Alexis Guéganno
 */
public class DTriangle extends Element implements Comparable<DTriangle>{

	
	private static final long serialVersionUID = 1L;

	public static final int PT_NB = 3;

	private static final int HASHBASE = 5;
	private static final int HASHMULT = 97;

	/**
	 * The array of edges that constitute this triangle
	 */
	private DEdge[] edges;
	//The coordinates of the center of the circle.
	private double xCenter, yCenter, zCenter;
	private double radius;

	private boolean seenForFlatRemoval;
        //This attribute will be used to manage triangles while refining the mesh
        private boolean processed;

	/**
	 * Initialize data structure This method is called by every constructor
	 */
	private void init() {
		this.edges = new DEdge[PT_NB];
		this.xCenter = 0;
		this.yCenter = 0;
		zCenter = 0;
		this.radius = -1;
		seenForFlatRemoval = false;
                processed = false;
	}

	/**
	 * Create a new triangle with the three given edges as a basis. </p><p>
         * An integrity check is processed while building the triangle. This constructor
         * is the best way to ensure that already existing edges will be linked to 
         * the good triangles, and that ther won't be any edge duplication in the 
         * data structures.
	 *
	 * @param e1
	 * @param e2
	 * @param e3
	 * @throws DelaunayError
         *      If there is at least two edges that don't share exactly a point.
	 */
	public DTriangle(DEdge e1, DEdge e2, DEdge e3) throws DelaunayError {
		super();
		init();

		//We check the integrity of the edges given to build this triangle
		boolean integrityE1E2 = (e1.isExtremity(e2.getStartPoint()) && ! e3.isExtremity((e2.getStartPoint())))
			|| (e1.isExtremity(e2.getEndPoint()) && !e3.isExtremity(e2.getEndPoint()));
		boolean integrityE1EptNb =  (e1.isExtremity(e3.getStartPoint()) && ! e2.isExtremity((e3.getStartPoint())))
			|| (e1.isExtremity(e3.getEndPoint()) && !e2.isExtremity(e3.getEndPoint()));
		boolean integrityEptNbE2= (e2.isExtremity(e3.getStartPoint()) && ! e1.isExtremity((e3.getStartPoint())))
			|| (e2.isExtremity(e3.getEndPoint()) && !e1.isExtremity(e3.getEndPoint()));

		if(integrityE1E2 && integrityE1EptNb && integrityEptNbE2){
			edges[0] = e1;
			edges[1] = e2;
			edges[2] = e3;

			connectEdges();
			computeCenter();
			radius = e1.getStartPoint().squareDistance2D(xCenter, yCenter);
		} else {
			throw new DelaunayError("Problem while generating the Triangle : "+
				integrityE1E2 +" "+ integrityE1EptNb+" "+integrityEptNbE2);
		}
	}
        
        /**
         * Create a new triangle with three input points.
         * 
         * @param p1
         * @param p2
         * @param p3
         * @throws DelaunayError 
         */
        public DTriangle(DPoint p1, DPoint p2, DPoint p3) throws DelaunayError{
		super();
		init();
                DEdge e1 = new DEdge(p1, p2);
                DEdge e2 = new DEdge(p2, p3);
                DEdge e3 = new DEdge(p3, p1);
                edges[0] = e1;
                edges[1] = e2;
                edges[2] = e3;
                connectEdges();
                computeCenter();
                radius = e1.getStartPoint().squareDistance2D(xCenter, yCenter);
                
        }

	/**
	 * Create a DTriangle from another triangle<br/>
         * NB : it doesn't update edges connection - topology is not preserved.
	 *
	 * @param aTriangle
	 */
	public DTriangle(DTriangle aTriangle) {
		super((Element)aTriangle);
		init();
		System.arraycopy(aTriangle.edges, 0, edges, 0, PT_NB);

		xCenter = aTriangle.xCenter;
		yCenter = aTriangle.yCenter;
		radius = aTriangle.radius;
	}

	/**
	 * Get a list of points containing the <code>DPoint</code> that define this triangle.</p><p>
         * This method is consistent with getPoint, ie getPoints().get(i)==getPoint(i).
	 * @return
         * the apex of the triangle, in a list.
	 */
	public final List<DPoint> getPoints(){
		List<DPoint> ret = new ArrayList<DPoint>();
		ret.add(edges[0].getStartPoint());
		ret.add(edges[0].getEndPoint());
		ret.add(getPoint(2));
		return ret;
	}

	/**
	 * Get the ith point. i must be equal to 0, 1 or 2. </p><p>
         * This method is consistent with getPoints, ie getPoints().get(i)==getPoint(i).
	 * 
	 * @param i
	 * @return aPoint
         *      On of the apex if i = 0, 1 or 2, null otherwise.
	 */
	public final DPoint getPoint(int i) {
		DPoint p = null;
		if (i==0) {
			p = edges[0].getStartPoint();
		} else if (i==1) {
			p = edges[0].getEndPoint();
		} else if(i==2){
			p = edges[1].getStartPoint();
			if ((p.equals(edges[0].getStartPoint())) || (p.equals(edges[0].getEndPoint()))) {
				p = edges[1].getEndPoint();
			}
		}
		return p;
	}

	/**
	 * Get the ith edge
	 * i must be equal to 0, 1 or 2.
	 *
	 * @param i
	 * @return anEdge
	 */
	public final DEdge getEdge(int i) {
		if ((0<=i) && (i<=2)) {
			return edges[i];
		} else {
			return null;
		}
	}

	/**
	 * Return the edges that form this triangle in an array.</p><p>
         * This method is consistent with getEdge(i), ie getEdge(i)==getEdges()[i]
	 * @return
         * The edges in an array.
	 */
	public final DEdge[] getEdges(){
		DEdge[] ret = new DEdge[PT_NB];
		System.arraycopy(this.edges, 0, ret, 0, PT_NB);
		return ret;
	}

	/**
	 * Get the index of the edge in the triangle.
	 * @param ed
	 * @return
	 * The index of the edge in its array of edges, -1 if ed is not an
         * edge of this triangle.
         */
	public final int getEdgeIndex(DEdge ed){
		for(int i=0; i<PT_NB; i++){
			if(edges[i].equals(ed)){
				return i;
			}
		}
		return -1;
	}

	/**
	 * Set the ith edge. If i&lt;0 or i&gt;2, this method returns quietly without
         * doing anything.
	 *
	 * @param i
	 * @param anEdge
         * @return
         *      <code>true</code> if anEdge has been successfully inserted, false otherwise.
	 */
	public final boolean setEdge(int i, DEdge anEdge) {
		if (0<=i && i<=2) {
			edges[i] = anEdge;
                        return true;
		}
                return false;
	}

	/**
	 * Get the radius of the CircumCircle
	 *
	 * @return radius
	 */
	public final double getRadius() {
		return Math.sqrt(radius);
	}

	/**
	 * Get the center of the CircumCircle
	 *
	 * @return
         *      The circumcenter of the triangle, as a JTS Coordinate.
         * @throws org.jdelaunay.delaunay.error.DelaunayError
	 */
	public final DPoint getCircumCenter() throws DelaunayError {
		return new DPoint(this.xCenter, this.yCenter, zCenter);
	}

	/**
	 * check if this triangle has already been encountered (and marked if flat)
	 * during the flat removal operation.
	 * @return
         * true if this triangle has already been encountered.
	 */
	public final boolean isSeenForFlatRemoval() {
		return seenForFlatRemoval;
	}

	/**
	 * Set the value of the seenForFlatRemoval attribute, that is used to
	 * process flat triangles only once during the flat tiangles removal operation.
	 * @param seenForFlatRemoval
	 */
	public final void setSeenForFlatRemoval(boolean seenForFlatRemoval) {
		this.seenForFlatRemoval = seenForFlatRemoval;
	}
        
        /**
         * If true, the triangle is supposed to be in the structure that stores the 
         * already processed triangles.
         * @return 
         */
        public final boolean isProcessed() {
                return processed;
        }
        
        /**
         * Set the processed attribute.
         * @param pro 
         */
        public final void setProcessed(boolean pro) {
                processed = pro;
        }
        
	@Override
	public final BoundaryBox getBoundingBox() throws DelaunayError {
		BoundaryBox aBox = new BoundaryBox();

		DPoint p1,p2,pptNb;
		p1 = edges[0].getStartPoint();
		p2 = edges[0].getEndPoint();
		pptNb = edges[1].getStartPoint();
		if ((pptNb.equals(p1))||(pptNb.equals(p2))) {
			pptNb = edges[1].getEndPoint();
		}
		aBox.alterBox( p1);
		aBox.alterBox( p2);
		aBox.alterBox( pptNb);
		
		return aBox;
	}

	/**
	 * Get the leftmost point of this triangle.
	 * @return
         * the leftmost point of this triangle.
	 */
	public final DPoint getLeftMost(){
		DPoint p1 = edges[0].getPointLeft();
		DPoint p2 = edges[1].getPointLeft();
		return p1.compareTo(p2) < 1 ? p1 : p2;
	}

	/**
	 * Get the last edge that form, with e1 and e2, this triangle. If e1 or e2
	 * do not belong to this triangle, return null.
	 * @param e1
	 * @param e2
	 * @return
         *  the edge of the triangle that is not e1 or e2. Null if e1 or e2 is not an edge
         *  of the triangle.
	 */
	public final DEdge getLastEdge(DEdge e1, DEdge e2){
		if(e1.equals(edges[0])){
			if(e2.equals(edges[1])){
				return edges[2];
			} else if(e2.equals(edges[2])){
				return edges[1];
			}
		} else if(e1.equals(edges[1])){
			if(e2.equals(edges[0])){
				return edges[2];
			} else if(e2.equals(edges[2])){
				return edges[0];
			}
		}else if(e1.equals(edges[2])){
			if(e2.equals(edges[0])){
				return edges[1];
			} else if(e2.equals(edges[1])){
				return edges[0];
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jdelaunay.delaunay.Element#contains(org.jdelaunay.delaunay.DPoint)
	 */
	@Override
	public final boolean contains(DPoint aPoint) {
		return isInside(aPoint);
	}	
	

	/**
	 * Determines if pt lies on one of the edges of this triangle.
	 * @param pt
	 * @return
	 *		true if it does.
	 */
	public final boolean isOnAnEdge(DPoint pt) {
		for(int i = 0; i<PT_NB;i++){
			if(edges[i].contains(pt)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Recompute the center of the circle that joins the ptNb points : the CircumCenter
	 * @throws DelaunayError
	 */
	public final void computeCenter() throws DelaunayError {
		DPoint p1,p2,pptNb;
		p1 = edges[0].getStartPoint();
		p2 = edges[0].getEndPoint();
		pptNb = edges[1].getStartPoint();
		if ((pptNb.equals(p1))||(pptNb.equals(p2))) {
			pptNb = edges[1].getEndPoint();
		}

		double p1Sq = p1.getX() * p1.getX() + p1.getY() * p1.getY();
		double p2Sq = p2.getX() * p2.getX() + p2.getY() * p2.getY();
		double pptNbSq = pptNb.getX() * pptNb.getX() + pptNb.getY() * pptNb.getY();

		double ux = p2.getX() - p1.getX();
		double uy = p2.getY() - p1.getY();
		double vx = pptNb.getX() - p1.getX();
		double vy = pptNb.getY() - p1.getY();

		double cp = ux * vy - uy * vx;
		double cx, cy;

		if (cp != 0) {
			cx = (p1Sq * (p2.getY() - pptNb.getY()) + p2Sq * (pptNb.getY() - p1.getY()) + pptNbSq
					* (p1.getY() - p2.getY()))
					/ (2.0 * cp);
			cy = (p1Sq * (pptNb.getX() - p2.getX()) + p2Sq * (p1.getX() - pptNb.getX()) + pptNbSq
					* (p2.getX() - p1.getX()))
					/ (2.0 * cp);

			xCenter = cx;
			yCenter = cy;
			zCenter = interpolateZ(new DPoint(cx, cy, 0));

			radius = p1.squareDistance2D(xCenter, yCenter);
		} else {
			xCenter = 0.0;
			yCenter = 0.0;
			radius = -1;
		}

	}

	/**
	 * Connect the edges of this triangle to it, ie put this in the left or right
         * attribute of each edge, according to the edge orientation and the triangle location.<p></p>
         * Note that if this triangle is on the right (resp. left) of one edge that 
         * already owns a right(resp. right) triangle, the other triangle will be replaced
         * by this one. Use with care, so !
         * 
	 */
	private void connectEdges() {
		// we connect edges to the triangle
		for (int i=0; i<PT_NB; i++) {
			// Start point should be start
			DPoint aPoint = this.getOppositePoint(edges[i]);
			if (edges[i].isLeft(aPoint)) {
                                edges[i].setLeft(this);
			} else {
                                edges[i].setRight(this);
			}
		}
	}

	/**
	 * Check if the aPoint is in or on the circumcircle of this triangle.
	 *
	 * @param aPoint
	 * @return position : <br/>
         *  * 0 = outside <br/>
         *  * 1 = inside <br/>
         *  * 2 = on the circle
	 */
	public final int inCircle(DPoint aPoint) {
		// default is outside the circle
		int returnedValue = 0;

		double ux = aPoint.getX() - xCenter;
		double uy = aPoint.getY() - yCenter;
		double distance = ux * ux + uy * uy;
		if (distance < radius - Tools.EPSILON2) {
			returnedValue = 1;
		}
		else if (distance < radius + Tools.EPSILON2) {
			returnedValue = 2;
		}

		return returnedValue;
	}

	/**
	 * Check if the point is inside the triangle
	 *
	 * @param aPoint
	 * @return isInside
	 */
	public final boolean isInside(DPoint aPoint) {
		boolean isInside = true;

		int k = 0;
		while ((k < PT_NB) && (isInside)) {
			DEdge theEdge = edges[k];

			if (theEdge.getLeft() == this) {
				if (theEdge.isRight(aPoint)) {
					isInside = false;
				}
			} else {
				if (theEdge.isLeft(aPoint)) {
					isInside = false;
				}
			}
			k++;
		}
		return isInside;
	}

	/**
	 * Get Z value of a specific point in the triangle
	 *
	 * @param aPoint
	 * @return ZValue
	 */
	public final double interpolateZ(DPoint aPoint) {
		double zValue = 0;

		DPoint p1,p2,p3;
		p1 = edges[0].getStartPoint();
		p2 = edges[0].getEndPoint();
		p3 = edges[1].getStartPoint();
		if ((p3.equals(p1))||(p3.equals(p2))) {
			p3 = edges[1].getEndPoint();
		}

		double ux = p2.getX() - p1.getX();
		double uy = p2.getY() - p1.getY();
		double uz = p2.getZ() - p1.getZ();
		double vx = p3.getX() - p1.getX();
		double vy = p3.getY() - p1.getY();
		double vz = p3.getZ() - p1.getZ();

		double a = uy * vz - uz * vy;
		double b = uz * vx - ux * vz;
		double c = ux * vy - uy * vx;
		double d = -a * p1.getX() - b * p1.getY() - c * p1.getZ();

		if (Math.abs(c) > Tools.EPSILON) {
			// Non vertical triangle
			zValue = (-a * aPoint.getX() - b * aPoint.getY() - d) / c;
		}

		return zValue;
	}

	/**
	 * Get Z value of a specific point in the triangle
	 * Take into account triangles connected to the edge
	 *
	 * @param aPoint
	 * @return ZValuei
	 */
	public final double softInterpolateZ(DPoint aPoint) {
		double weight = (double) PT_NB;
		double zValue = interpolateZ(aPoint) * weight;
		
		// Process connected edges
		for (int i=0; i<PT_NB; i++) {
			DEdge anEdge = edges[i];
			DTriangle aTriangle = null;
			if (anEdge != null) {
				if (anEdge.getLeft() == this) {
					aTriangle = anEdge.getRight();
				} else {
					aTriangle = anEdge.getLeft();
				}
			}
			if (aTriangle != null) {
				weight += 1.0;
				zValue += aTriangle.interpolateZ(aPoint);
			}
		}
		// Define new Z value
		zValue /= weight;

		return zValue;
	}

	/**
	 * Compute triangle area
	 *
	 * @return area
	 */
	public final double getArea() {
		DPoint p1,p2,pptNb;
		p1 = edges[0].getStartPoint();
		p2 = edges[0].getEndPoint();
		pptNb = edges[1].getStartPoint();
		if ((pptNb.equals(p1))||(pptNb.equals(p2))) {
			pptNb = edges[1].getEndPoint();
		}

		double area = ((pptNb.getX()-p1.getX())*(p2.getY()-p1.getY())-(p2.getX()-p1.getX())*(pptNb.getY()-p1.getY()))/2;

		return area<0 ? -area : area ;
	}
        
        /**
         * Computes the 3D area of a triangle based on the same approach of JTS
         * @return 
         */
        public final double getArea3D() {
            DPoint p1,p2,pptNb;
		p1 = edges[0].getStartPoint();
		p2 = edges[0].getEndPoint();
		pptNb = edges[1].getStartPoint();
		if ((pptNb.equals(p1))||(pptNb.equals(p2))) {
			pptNb = edges[1].getEndPoint();
		}
            /**
             * Uses the formula 1/2 * | u x v | where u,v are the side vectors
             * of the triangle x is the vector cross-product
             */
            // side vectors u and v
            double ux = p2.getX() - p1.getX();
            double uy = p2.getY() - p1.getY();
            double uz = p2.getZ() - p1.getZ();

            double vx = pptNb.getX() - p1.getX();
            double vy = pptNb.getY() - p1.getY();
            double vz = pptNb.getZ() - p1.getZ();

            // cross-product = u x v
            double crossx = uy * vz - uz * vy;
            double crossy = uz * vx - ux * vz;
            double crossz = ux * vy - uy * vx;

            // tri area = 1/2 * | u x v |
            double absSq = crossx * crossx + crossy * crossy + crossz * crossz;
            return Math.sqrt(absSq) / 2;
        }

	/**
	 * Get the normal vector to this triangle, of length 1.
	 * @return
         * Get the vector normal to the triangle.
	 * @throws DelaunayError
	 */
	public final DPoint getNormalVector() throws DelaunayError {
		//We first perform a vectorial product between two of the edges
		double dx1 = edges[0].getStartPoint().getX() - edges[0].getEndPoint().getX();
		double dy1 = edges[0].getStartPoint().getY() - edges[0].getEndPoint().getY();
		double dz1 = edges[0].getStartPoint().getZ() - edges[0].getEndPoint().getZ();
		double dx2 = edges[1].getStartPoint().getX() - edges[1].getEndPoint().getX();
		double dy2 = edges[1].getStartPoint().getY() - edges[1].getEndPoint().getY();
		double dz2 = edges[1].getStartPoint().getZ() - edges[1].getEndPoint().getZ();
		DPoint vec = new DPoint(dy1*dz2 - dz1*dy2, dz1 * dx2 - dx1 * dz2, dx1 * dy2 - dy1 * dx2);
		double length = Math.sqrt(vec.squareDistance(new DPoint(0,0,0)));
		vec.setX(vec.getX()/length);
		vec.setY(vec.getY()/length);
		vec.setZ(vec.getZ()/length);
		return vec;
	}

	/**
	 * Get the vector with the highest down slope in the plan associated to this triangle.
	 * @return
         * return the steepest vector.
	 * @throws DelaunayError
	 */
	public final DPoint getSteepestVector() throws DelaunayError {
		DPoint normal = getNormalVector();
		if(Math.abs(normal.getX())<Tools.EPSILON && Math.abs(normal.getY())<Tools.EPSILON){
			return new DPoint(0,0,0);
		}
		DPoint pente;
		if (Math.abs(normal.getX())<Tools.EPSILON) {
			pente = new DPoint(0, 1, - normal.getY() /  normal.getZ());
		} else if (Math.abs(normal.getY())<Tools.EPSILON) {
			pente = new DPoint(1, 0, -normal.getX() / normal.getZ());
		} else {
			pente = new DPoint(normal.getX() / normal.getY(), 1, -1 / normal.getZ() *
				(normal.getX() * normal.getX() / normal.getY() + normal.getY()));
		}
		//We want the vector to be low-oriented.
		if(pente.getZ()>Tools.EPSILON){
			pente.setX(-pente.getX());
			pente.setY(-pente.getY());
			pente.setZ(-pente.getZ());
		}
		//We normalize it
		double length = Math.sqrt(pente.squareDistance(new DPoint(0,0,0)));
		if(length > Tools.EPSILON){
			pente.setX(pente.getX()/length);
			pente.setY(pente.getY()/length);
			pente.setZ(pente.getZ()/length);
		}
		return pente;
	}


	/**
	 * Get the slope of this DTriangle. Be careful, as the returned value will be negative
	 * @return
         *      the slope of this triangle, in rads
	 * @throws DelaunayError
	 */
	public final double getSlope() throws DelaunayError {
		DPoint steep = getSteepestVector();
		DEdge ed = new DEdge(new DPoint(0,0,0), steep);
		return ed.getSlope();
	}

	/**
	 * get the slope of this DTriangle, in degrees. Be careful, as the returned value will be negative.
	 * @return
         *      the slope, in degrees.
	 * @throws DelaunayError
	 */
	public final double getSlopeInDegree() throws DelaunayError {
		DPoint steep = getSteepestVector();
		DEdge ed = new DEdge(new DPoint(0,0,0), steep);
		return ed.getSlopeInDegree();
	}

        /**
         * Return the maximal angle og this triangle.
         * @return 
         *      the minimal angle of this triangle.
         */
        public final double getMinAngle() {
                double min = Double.POSITIVE_INFINITY;
                for(int i=0; i<PT_NB;i++){
                        double cur = getAngle(i);
                        min = cur < min ? cur : min;
                }
                return min;
        }
        
	/**
	 * Return the maximal angle of this triangle.
	 *
	 * @return maxAngle
	 */
	public final double getMaxAngle() {
		double maxAngle = 0;
		for (int k = 0; k < PT_NB; k++) {
			double angle = getAngle(k);
			if (angle > maxAngle) {
				maxAngle = angle;
			}
		}
		return maxAngle;
	}

	/**
	 * Check if triangle topology is correct or not
	 *
	 * @return correct
	 */
	public final boolean checkTopology() {
		boolean correct = true;

		// check if each edge is connected to the triangle
		
		for(int i=0; i < PT_NB; i++) {
			if (!(edges[i].getLeft() == this ||  edges[i].getRight() == this)) {
				return false;
			}
		}
                //We must be sure that we have exactly three points in the triangle
                for (int l = 0; l < PT_NB; l++) {
                        DPoint pt = getPoint(l);
                        if(!sharedByTwoEdge(pt)){
                                return false;
                        }
                }
                

		return correct;
	}
        
        /**
         * test if pt is an apex of this triangle. It must be an extremity of 
         * two of the edges, and must not be an extremity of the last one.
         * @param pt
         * @return 
         *      true if pt is an apex of the triangle.
         */
        public final boolean sharedByTwoEdge(DPoint pt){
                if(edges[0].isExtremity(pt)){
                        return (edges[1].isExtremity(pt) && !edges[2].isExtremity(pt)) ||
                                (!edges[1].isExtremity(pt) && edges[2].isExtremity(pt));
                } else {
                        return edges[1].isExtremity(pt) && edges[2].isExtremity(pt);
                }
        }

	/**
	 * Check if the triangle is flat or not.
	 * In other words, this method checks if the 3 points have the same Z.
	 *
	 * @return isFlat
	 */
	public final boolean isFlatSlope() {
		boolean isFlat = true;
		int i = 0;
		while ((i < PT_NB) && (isFlat)) {
			if (!edges[i].isFlatSlope()) {
				isFlat = false;
			}
			else {
				i++;
			}
		}
		return isFlat;
	}

	/**
	 * Get the point of the triangle that does not belong to the edge
	 *
	 * @param ed
	 * @return alterPoint
	 */
	public final DPoint getOppositePoint(DEdge ed) {
		DPoint start = ed.getStartPoint();
		DPoint end = ed.getEndPoint();
		return getAlterPoint(start, end);
	}

	/**
	 * Return the edge that is not linked to pt, or null if pt is not a
	 * point of this triangle.
	 * @param pt
         * @return 
	 */
	public final DEdge getOppositeEdge(DPoint pt){
		if(!belongsTo(pt)){
			return null;
		}
		if(!edges[0].contains(pt)){
			return edges[0];
		} else if(!edges[1].contains(pt)){
			return edges[1];
		} else {
			return edges[2];
		} 

	}

	/**
	 * Get the point of the triangle that is not one of the 2 points given
	 * in argument.
	 * If one of these argument is not part of this triangle, this method will
	 * return null.
	 *
	 * @param p1
	 * @param p2
	 * @return alterPoint
	 */
	public final DPoint getAlterPoint(DPoint p1, DPoint p2) {
		DPoint t1 = getPoint(0);
		DPoint t2 = getPoint(1);
		DPoint t3 = getPoint(2);
		if(p1.equals(t1)){
			if(p2.equals(t2)){
				return t3;
			} else if(p2.equals(t3)){
				return t2;
			}
		} else if(p1.equals(t2)) {
			if(p2.equals(t1)){
				return t3;
			} else if (p2.equals(t3)) {
				return t1;
			}
		} else if(p1.equals(t3)) {
			if(p2.equals(t1)){
				return t2;
			} else if (p2.equals(t2)) {
				return t1;
			}
		}
		return null;
	}

	/**
	 * Get the edge of the triangle that includes the two point
	 *
	 * @param p1
	 * @param p2
	 * @return alterEdge
	 */
	protected final DEdge getEdgeFromPoints(DPoint p1, DPoint p2) {
		DEdge alterEdge = null;
		DPoint test1, test2;
		int i = 0;
		while (i < PT_NB && alterEdge == null) {
			DEdge testEdge = edges[i];
			test1 = testEdge.getStartPoint();
			test2 = testEdge.getEndPoint();
			if ((test1.equals(p1)) && (test2.equals(p2))) {
				alterEdge = testEdge;
			}
			else if ((test1.equals(p2)) && (test2.equals(p1))) {
				alterEdge = testEdge;
			}
			else {
				i++;
			}
		}

		return alterEdge;
	}

	/**
	 * Check if the point is an apex of the triangle
	 *
	 * @param aPoint
	 * @return belongs
	 */
	public final boolean belongsTo(DPoint aPoint) {
		boolean belongs = false;
		DEdge anEdge = this.getEdge(0);
		if (anEdge.getStartPoint().equals(aPoint)) {
			belongs = true;
		} else if (anEdge.getEndPoint().equals(aPoint)) {
			belongs = true;
		} else {
			anEdge = this.getEdge(1);
			if (anEdge.getStartPoint().equals(aPoint)) {
				belongs = true;
			} else if (anEdge.getEndPoint().equals(aPoint)) {
				belongs = true;
			}
		}

		return belongs;
	}
	
	/**
	 * Get the barycenter of the triangle as a DPoint
	 *
	 * @return isFlat
	 * @throws DelaunayError 
	 */
	public final DPoint getBarycenter() throws DelaunayError {
		double x = 0, y = 0, z = 0;
		DPoint aPoint;
		for (int i = 0; i < PT_NB; i++) {
			aPoint = getPoint(i);

			x += aPoint.getX();
			y += aPoint.getY();
			z += aPoint.getZ();
		}
		x /= (double) PT_NB;
		y /= (double) PT_NB;
		z /= (double) PT_NB;
		
		return new DPoint(x, y, z);
	}

	/**
	 * Gives a rperesentation of this triangle as a String.
	 * @return Triangle "+gid+": ["+getPoint(0).toString()+", "+getPoint(1).toString()+", "+getPoint(2).toString()+"]
	 */
	@Override
	public final String toString() {
		return "Triangle "+getGID()+": ["+getPoint(0).toString()+", "+getPoint(1).toString()+", "+getPoint(2).toString()+"]";
	}

	/**
	 * Set the edge color Must be used only when using package drawing
	 *
	 * @param g
	 */
	private void setColor(Graphics g) {
		if(getProperty()>0) {
			g.setColor(new Color(getProperty()));
		}
		else if (isFlatSlope()) {
			g.setColor(Color.green);
		}
		else {
			g.setColor(Color.yellow);
		}
	}

	/**
	 * Display the triangle in a JPanel Must be used only when using package
	 * drawing
	 * 
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
		int[] xPoints, yPoints;
		xPoints = new int[PT_NB];
		yPoints = new int[PT_NB];
		DPoint p1, p2, pptNb;
		p1 = getPoint(0);
		p2 = getPoint(1);
		pptNb = getPoint(2);

		xPoints[0] = (int) ((p1.getX() - minX) * scaleX + decalageX);
		xPoints[1] = (int) ((p2.getX() - minX) * scaleX + decalageX);
		xPoints[2] = (int) ((pptNb.getX() - minX) * scaleX + decalageX);

		yPoints[0] = (int) ((p1.getY() - minY) * scaleY + decalageY);
		yPoints[1] = (int) ((p2.getY() - minY) * scaleY + decalageY);
		yPoints[2] = (int) ((pptNb.getY() - minY) * scaleY + decalageY);

		setColor(g);
		g.fillPolygon(xPoints, yPoints, PT_NB);

		for (int i = 0; i < PT_NB; i++) {
			edges[i].displayObject(g, decalageX, decalageY, minX, minY, scaleX,
					scaleY);
		}
	}

	/**
	 * Display the triangle in a JPanel Must be used only when using package
	 * drawing
	 *
	 * @param g
	 * @param decalageX
	 * @param decalageY
	 */
	protected final void displayObjectCircles(Graphics g, int decalageX, int decalageY) {
		double r = Math.sqrt(radius);
		g.setColor(Color.red);
		g.drawOval((int) (xCenter) + decalageX, decalageY - (int) (yCenter),
				1, 1);//FIXME not good position
		g.drawOval((int) (xCenter - r) + decalageX, decalageY
				- (int) (yCenter + r), (int) r * 2, (int) r * 2);//FIXME not good position
	}

	/**
	 * Used to check if this is equal to other.
	 * This and other are equal if and only if other is an instance of DTriangle
	 * and their points are the same (whatever their order in the triangle).
	 * @param other
	 * @return
         * true if this and other are equal
	 */
	@Override
	public final boolean equals(Object other){
		if(other instanceof DTriangle){
			DTriangle otherTri = (DTriangle) other;
			boolean ret = belongsTo(otherTri.getPoint(0)) && belongsTo(otherTri.getPoint(1))
				&& belongsTo(otherTri.getPoint(2));
			return ret;
		} else {
			return false;
		}
	}

	@Override
	public final int hashCode() {
		return HASHMULT * HASHBASE + Arrays.deepHashCode(this.edges);
	}

	/**
	 * Implements the Comparable interface. The triangles will be sorted according
	 * the middle of their bounding box.
	 * As we work on a triangulation where triangles' intersection can only be an edge, a point
	 * or void, the Bounding boxes are unique.
	 *
	 * BE CAREFUL : this method is not consistent with equals ! We are making a comparison
	 * on the bounding box of two triangles, they could be equal even if the triangle
	 * are different !!!
	 * @param t
	 * @return
         * -1, 0 or 1, using the point comparison on the center of the bounding boxes
	 */
	@Override
	public final int compareTo(DTriangle t) {
            try {
                DPoint midT = getBoundingBox().getMiddle();
                DPoint midO = t.getBoundingBox().getMiddle();
                int c = midT.compareTo(midO);
                if (c == 0) {
                    try {
                        c = getBarycenter().compareTo(t.getBarycenter());
                    } catch (DelaunayError ex) {
                        Logger.getLogger(DTriangle.class.getName()).log(Level.WARNING, null, ex);
                    }
                }
                return c;
            } catch (DelaunayError e) {
                throw new IllegalArgumentException(e.getLocalizedMessage(), e);
            }
	}

	/**
	 * Retrieve the angle, in degrees, at vertex number k.
	 * @param k
	 * @return
         * The angle at the ith point.
	 */
	public final double getAngle(int k){
		int k1 = (k + 1) % PT_NB;
		int k2 = (k1 + 1) % PT_NB;
		final double degreesPI = 180d;

		DPoint p1 = this.getPoint(k);
		DPoint p2 = this.getPoint(k1);
		DPoint pptNb = this.getPoint(k2);

		double ux = p2.getX() - p1.getX();
		double uy = p2.getY() - p1.getY();
		double vx = pptNb.getX() - p1.getX();
		double vy = pptNb.getY() - p1.getY();

		double dp = ux * vx + uy * vy;

		return Math.acos(Math.sqrt(((dp * dp))
				/ ((ux * ux + uy * uy) * (vx * vx + vy * vy))))
				* (degreesPI / Math.PI);
	}

     

        /**
	 * Compute the slope of the triangle in percent
	 *
	 * @return the slope of the triangle, in percent.
         * @throws DelaunayError
	 */
	public final double getSlopeInPercent() throws DelaunayError {
		return Math.abs(getSlope()) * 100;
	}


        /**
	 * Compute the azimut of the triangle in degrees between north and steeepest vector.
         * Aspect is measured clockwise in degrees from 0, due north, to 360, again due north, coming full circle.
         * @return the aspect of the slope of this triangle.
         * @throws DelaunayError
	 */
	public final double getSlopeAspect() throws DelaunayError {
		double orientationPente;
		DPoint c1 = new DPoint(0.0, 0.0, 0.0);
		DPoint c2 = getSteepestVector();
		if (c2.getZ() > 0.0) {
			c2.setX(-c2.getX());
                        c2.setY(-c2.getY());
                        c2.setZ(-c2.getZ());
		}
		// l'ordre des coordonnees correspond a l'orientation de l'arc
		// "sommet haut vers sommet bas"
		double angleAxeXrad = Tools.angle(c1, c2);
		// on considere que l'axe nord correspond a l'axe Y positif
		double angleAxeNordrad = Tools.PI_OVER_2 - angleAxeXrad;
		double angleAxeNorddeg = Math.toDegrees(angleAxeNordrad);
		// on renvoie toujours une valeur d'angle >= 0
		orientationPente = angleAxeNorddeg < 0.0 ? 360.0 + angleAxeNorddeg
			: angleAxeNorddeg;
		return orientationPente;
	}

        /**
	 * Returns true if the triangle is turned toward the edge ed.
	 * @param ed
	 * @return
         *      true if this is pouring into ed.
         * @throws org.jdelaunay.delaunay.error.DelaunayError
	 */
	public final boolean isTopoOrientedToEdge(DEdge ed) throws DelaunayError {
		// on determine les sommets A,B et C du triangle et on calle AB (ou BA)
		// sur e
		DPoint a = ed.getStartPoint();
		DPoint b = ed.getEndPoint();
                if(!this.belongsTo(a) || !belongsTo(b)){
                        throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_OUTSIDE_TRIANGLE);
                }


		DPoint c = getOppositePoint(ed);
		DPoint ab = Tools.vectorialDiff(b, a);
		DPoint ac = Tools.vectorialDiff(c, a);
		// orientation CCW
		if (Tools.vectorProduct(ab, ac).getZ() < 0) {
			// echange A et B
			DPoint d = a;
			a = b;
			b = d;
			ab = Tools.vectorialDiff(b, a);
		}
		// test d'intersection entre AB et P
		DPoint p =getSteepestVector();		
		return Tools.vectorProduct(ab, p).getZ() < 0;
	}


        /**
         * Compute the intersection point according the steepest vector.
         * We assume that the point is in the Triangle
         * @param dPoint
         * @return DPoint
         * @throws DelaunayError
         */
        public final DPoint getSteepestIntersectionPoint(DPoint dPoint) throws DelaunayError{
                if (isInside(dPoint)){
                for (DEdge dEdge : edges) {
                       if (isTopoOrientedToEdge(dEdge)) {
                               DPoint pt = Tools.computeIntersection(dEdge.getStartPoint(), 
							dEdge.getDirectionVector(),
							dPoint, getSteepestVector());
                               if (dEdge.contains(pt)){
					return pt;
                               }
                                       
                        }
                }
                }
                return null;

        }

	/**
	 * Compute the intersection point according to the vector opposite to the steepest
	 * vector. If dp is outside the triangle, we return null.
	 * @param dp
	 * @return
         * The point pt of the triangle's boundary for which (dp pt) is colinear to 
         * the steepest vector.
	 * @throws DelaunayError
	 */
	public final DPoint getCounterSteepestIntersection(DPoint dp) throws DelaunayError {
		if(isInside(dp) || isOnAnEdge(dp)){
			for(DEdge ed : edges){
				if(!isTopoOrientedToEdge(ed)){
					DPoint counterSteep = getSteepestVector();
					counterSteep.setX(-counterSteep.getX());
					counterSteep.setY(-counterSteep.getY());
					counterSteep.setZ(-counterSteep.getZ());
                               DPoint pt = Tools.computeIntersection(ed.getStartPoint(),
							ed.getDirectionVector(),
							dp, counterSteep);
				       if (ed.contains(pt)){
						return pt;
				       }
				}
			}
		}
		return null;
	}
        
        /**
         * Return the triangle of the mesh that contains the center of this DTriangle.
         * @return
         *      The DTriangle that contains the circumcenter of this. The last DEdge seen,
         *      if the circumcenter is not in the mesh.
         * @throws DelaunayError 
         */
        public final Element getCircumCenterContainer() throws DelaunayError{
                DPoint cc = new DPoint(getCircumCenter());
                return searchPointContainer(cc);
        }
        
        /**
         * Return the triangle of the mesh that contains the center of this DTriangle.
         * @return
         *      The DTriangle that contains the circumcenter of this.<br />
         *      The last DEdge seen, if the circumcenter is not in the mesh. <br />
         *      <code>null<code> if a constraint is crossed while searching for the circumcenter.
         * @throws DelaunayError 
         */
        public final Element getCircumCenterContainerSafe() throws DelaunayError{
                DPoint cc = new DPoint(getCircumCenter());
                return searchPointImpl(cc, true);
        }
        
        /**
         * This method recursively search for pt in the mesh. If it's in this, this is
         * returned. Else, we search in the adjacent triangles.
         * @param pt
         * @return
         *      The triangle that contains the triangle, or the last edge visited
         *      if the point is outside the mesh.
         * @throws DelaunayError 
         */
        public final Element searchPointContainer(final DPoint pt) throws DelaunayError {
                return searchPointImpl(pt, false);
        }
        
        /**
         * Common implementation for the search operations.
         * @param pt
         * @param safe
         * @return
         * @throws DelaunayError 
         */
        private Element searchPointImpl(final DPoint pt, final boolean safe) throws DelaunayError {
                Element ret = null;
                if(contains(pt)){
                        return this;
                } else {
                        for(DEdge ed : edges){
                                DPoint op = getOppositePoint(ed);
                                if(ed.isRight(pt) && ed.isLeft(op)){
                                        if(ed.isLocked() && safe){
                                                return null;
                                        } else if(ed.getRight() != null){
                                                return ed.getRight().searchPointContainer(pt );
                                        } else {
                                                ret = ed;
                                        }
                                } else if(ed.isLeft(pt) && ed.isRight(op)){
                                        if(ed.isLocked() && safe){
                                                return null;
                                        } else if(ed.getLeft() != null){
                                                return ed.getLeft().searchPointContainer(pt);
                                        } else {
                                                ret = ed;
                                        }
                                }
                        }
                }             
                return ret;
                
        }
        
        /**
         * Get the edge that contains pt (if any, and the first found if pt is an apex)
         * @param pt
         * @return 
         *      The containing edge, if any, null otherwise.
         */
        public final DEdge getContainingEdge(DPoint pt){
                if(isOnAnEdge(pt)){
                        for (DEdge edge : edges) {
                                if (edge.contains(pt)) {
                                        return edge;
                                }
                        }
                }
                return null;                
        }
        
        /**
         * Returns true if ed is equals to one of the edges that form this triangle.
         * @param ed
         * @return 
         *  true if ed is an edge of this.
         */
        public final boolean isEdgeOf(DEdge ed){
                for(DEdge e : edges){
                        if(e.equals(ed)){
                                return true;
                        }
                }
                return false;
        }
        
        /**
         * Return the square of the minimal distance between pt and the apex
         * of this triangle.
         * @param pt
         * @return 
         * the square of the minimal distance between pt and the apex
         * of this triangle.
         */
        public final double getMinSquareDistance(DPoint pt) {
                double min = Double.POSITIVE_INFINITY;
                for(int i = 0; i<PT_NB; i++){
                        double dist = pt.squareDistance(getPoint(i));
                        min = dist < min ? dist : min;
                }
                return min;
        }
        
        /**
         * Returns true if the minimal distance between pt and one of the apex
         * is smaller than threshold.
         * @param pt
         * @param threshold
         * @return the minimal distance between pt and one of the apex
         * is smaller than threshold.
         */
        public final boolean isCloser(DPoint pt, double threshold) {
                double min = getMinSquareDistance(pt);
                return min < threshold * threshold;
        }
        
        /**
         * This method force the link between this and its edges, and ensure that
         * edges are not pointing to duplicates of this triangle.
         */
        public final void forceCoherenceWithEdges(){
                for(DEdge edg : edges){
                        DTriangle tri = edg.getLeft();
                        DTriangle tri2 = edg.getRight();
                        if(equals(tri)){
                                edg.setLeft(this);
                        } else if(equals(tri2)){
                                edg.setRight(this);
                        } else {
                                DPoint op = getOppositePoint(edg);
                                if(op!=null){
                                        if(edg.isLeft(op)){
                                                edg.setLeft(this);
                                        } else {
                                                edg.setRight(this);
                                        }
                                }
                        }
                }
        }
}
