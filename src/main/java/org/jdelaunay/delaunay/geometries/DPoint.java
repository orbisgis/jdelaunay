/*
 * jDelaunay is a library dedicated to the processing of Delaunay and constrained 
 * Delaunay triangulations from PSLG inputs.
 * 
 * This library is developed at French IRSTV institute as part of the AvuPur and Eval-PDU project, 
 * funded by the French Agence Nationale de la Recherche (ANR) under contract 
 * ANR-07-VULN-01 and ANR-08-VILL-0005-01 .
 * 
 * jDelaunay is distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Adelin PIAU, Jean-Yves MARTIN
 * Copyright (C) 2011 Alexis GUEGANNO, Jean-Yves MARTIN
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
 * For more information, please consult: <http://trac.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.jdelaunay.delaunay.geometries;

import java.awt.Color;
import java.awt.Graphics;
import com.vividsolutions.jts.geom.Coordinate;
import org.jdelaunay.delaunay.error.DelaunayError;
import org.jdelaunay.delaunay.tools.Tools;


/**
 * The smallest geometric element of a mesh.
 *
 * @author Jean-Yves MARTIN, Erwan BOCHER, Adelin PIAU
 */
public class DPoint extends Element implements Comparable<DPoint> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Coordinate coord;

	private static final int HASHBASE = 7;
	private static final int HASHMULT = 67;

	/**
	 * Initialize point 
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @throws DelaunayError If x, y or z is not set.
	 */
	private void init(double x, double y, double z) throws DelaunayError {
		if(Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z)) {
			throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_ERROR_POINT_XYZ);
		}
		
		this.coord = new Coordinate(x,y,z);

	}

	/**
	 * Build a point at the origin
	 * @throws DelaunayError  If x, y or z is not set.
	 */
	public DPoint() throws DelaunayError {
		super();
		init(0.0,0.0,0.0);
	}

	/**
	 * Build a point at coordinates x, y, z with no type
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @throws DelaunayError If x, y or z is not set.
	 */
	public DPoint(double x, double y, double z) throws DelaunayError {
		super();
		init(x,y,z);
	}

	/**
	 * Build a point as a copy of another point
	 * @throws DelaunayError DelaunayError
	 */
	public DPoint(DPoint pt) throws DelaunayError {
		super((Element) pt);
		init(pt.coord.x,pt.coord.y,pt.coord.z);
	}

	/**
	 * Build a point as a copy of jts Coordinates
	 * @throws DelaunayError If x, y or z is not set.
	 */
	public DPoint(Coordinate coord) throws DelaunayError {
		super();
		init(coord.x,coord.y,coord.z);
	}

	/**
	 * Get X coordinate
	 * @return x
	 */
	public final double getX() {
		return this.coord.x;
	}

	/**
	 * Get Y coordinate
	 * @return y
	 */
	public final double getY() {
		return this.coord.y;
	}

	/**
	 * Get Z coordinate
	 * @return z
	 */
	public final double getZ() {
		return this.coord.z;
	}

	/**
	 * Set X coordinate
	 * @param x
	 */
	public final void setX(double x) {
		this.coord.x = x;
	}
	/**
	 * Set Y coordinate
	 * @param y
	 */
	public final void setY(double y) {
		this.coord.y = y;
	}
	/**
	 * Set Z coordinate
	 * @param z
	 */
	public final void setZ(double z) {
		this.coord.z = z;
	}	
	
	/**
	 * return jts Coordinate
	 * @return
         * A representation of this DPoint as a JTS Coordinate.
	 */
	public final Coordinate getCoordinate() {
		return coord;
	}
        
	@Override
	public final BoundaryBox getBoundingBox() {
		BoundaryBox aBox = new BoundaryBox();
		aBox.alterBox(coord);
		return aBox;
	}

	/**
	 * Check if aPoint can be considered as equal to this.
	 * @param aPoint
	 * @return
         * true if this and aPoint are equal.
	 */
	@Override
	public final boolean contains(DPoint aPoint) {
		return squareDistance(aPoint) < Tools.EPSILON2;
	}
	
	@Override
	public final boolean contains(Coordinate c) {
		return squareDistance(c.x, c.y, c.z) < Tools.EPSILON2;
	}

	/**
	 * plane square distance to another point
	 * 
	 * @param aPoint
	 * @return distance
	 */
	protected final double squareDistance2D(DPoint aPoint) {
		return squareDistance(aPoint.coord.x, aPoint.coord.y);
	}

	/**
	 * plane square distance to another point
	 * 
	 * @param x
	 * @param y
	 * @return distance
	 */
	protected final double squareDistance2D(double x, double y) {
		return squareDistance(x, y);
	}

	/**
	 * square distance to another point
	 * 
	 * @param aPoint
	 * @return distance
	 */
	public final double squareDistance(DPoint aPoint) {
		return squareDistance(aPoint.coord.x, aPoint.coord.y, aPoint.coord.z);
	}

	/**
	 * plane square distance to another point
	 * 
	 * @param x
	 * @param y
	 * @return distance
	 */
	protected final double squareDistance(double x, double y) {
		return (x - this.coord.x) * (x - this.coord.x) + (y - this.coord.y) * (y - this.coord.y);
	}

	/**
	 * square distance to another point
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return distance
	 */
	protected final double squareDistance(double x, double y, double z) {
		return (x - this.coord.x) * (x - this.coord.x) + (y - this.coord.y) * (y - this.coord.y)
				+ (z - this.coord.z) * (z - this.coord.z);
	}

	/**
	 * Check if the point is closed to the current point, ie if the distance 
         * between this and aPoint is inferior to tolerence.
	 * 
	 * @param aPoint
	 * @param tolerence
	 * @return closedTo
	 */
	protected final boolean closedTo(DPoint aPoint, double tolerence) {
		return squareDistance(aPoint) < tolerence*tolerence;
	}

	/**
	 * Gives a string representation of this object.
	 * @return DPoint GID [x y z]
	 */
	@Override
	public final String toString() {
		return "Point "+getGID()+" [" + this.coord.x + " " + this.coord.y + " " + this.coord.z + "]";
	}

	/**
	 * We override the equals method, as two points can be said equal when their
	 * coordinate are exactly the same
	 * @param p
	 * @return
         * true if this and p are equal, in three dimensions.
	 */
	@Override
	public final boolean equals(Object p){
		if(p instanceof DPoint){
			DPoint y = (DPoint) p;
                        double dist = (getX() - y.getX())*(getX() - y.getX())+(getY() - y.getY())*(getY() - y.getY());
                        dist = dist + (getZ() - y.getZ())*(getZ() - y.getZ());
			return dist<Tools.EPSILON2;
		}else {
			return false;
		}
	}

	/**
	 * Generate an hashcode based on the coordinates of the point.
	 * @return
         * a hash
	 */
	@Override
	public final int hashCode() {
		int hash = HASHBASE;
		hash = HASHMULT * hash + (this.coord != null ? this.coord.hashCode() : 0);
		return hash;
	}

	/**
	 * Check if this==y, considering only the first two coordinates.
	 * @param y
	 * @return
         * true if this and y are equal in 2D.
	 */
	public final boolean equals2D(DPoint y){
		if(y==null){
			return false;
		} else {
                        return (((getX() - y.getX())*(getX() - y.getX())+(getY() - y.getY())*(getY() - y.getY()))<Tools.EPSILON2);
		}
	}

	/**
	 * Compare this and p in two dimensions.
	 * @param p
	 * @return
	 *	-1 : if this.x &lt; p.x || (this.x == p.x && this.y &lt; p.y)<br />
	 *	0 : if this.x == p.x && this.y == p.y<br />
	 *	1 otherwise.
	 */
	public final int compareTo2D(DPoint p){
                double dx = (getX() - p.getX());
                if(dx*dx*2 < Tools.EPSILON2){
                        double dy = (getY() - p.getY());
                        if(dx*dx+dy*dy<Tools.EPSILON2){
                                return 0;
                        } else if(getY()<p.getY()){
                                return -1;
                        } else {
                                return 1;
                        }
                } else if(getX()<p.getX()){
                        return -1;
                } else {
                        return 1;
                }
	}

	/**
	 * Compare this and p in two dimensions.
	 * @param p
	 * @return
	 *	-1 : if this.x &lt; p.x || (this.x == p.x && this.y &lt; p.y)<br />
	 *	0 : if this.x == p.x && this.y == p.y<br />
	 *	1 otherwise.
	 */
	@Override
	public final int compareTo(DPoint p){
		return compareTo2D(p);
	}
	
	/**
	 * Set the point color for the JFrame panel
	 * 
	 * @param g
	 */
	protected final void setColor(Graphics g) {
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
	public final void displayObject(Graphics g, int decalageX, int decalageY,
			double minX, double minY, double scaleX, double scaleY) {
		setColor(g);
		g.drawOval((int) ((this.coord.x - minX) * scaleX + decalageX) - 1,
				(int) ((this.coord.y - minY) * scaleY + decalageY) - 1, 1, 1);
	}
}
