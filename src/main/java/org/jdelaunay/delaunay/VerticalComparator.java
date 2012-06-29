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
package org.jdelaunay.delaunay;

import java.io.Serializable;
import java.util.Comparator;
import org.apache.log4j.Logger;
import org.jdelaunay.delaunay.error.DelaunayError;
import org.jdelaunay.delaunay.geometries.DEdge;
import org.jdelaunay.delaunay.geometries.DPoint;
import org.jdelaunay.delaunay.tools.Tools;

/**
 * The VerticalComparator class will be used to perform sorting and searching operations
 * in vertically sorted lists. We can't use directly the Comparable interface in DEdge,
 * as it is already used for the "left-right" method.
 * @author Alexis Guéganno
 */
public class VerticalComparator implements Comparator<DEdge>, Serializable {
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(VerticalComparator.class);

	//The absciss where we are going to make the comparison.
	private double abs;

	/**
	 * Vertical comparator constructor.
	 * @param x
	 */
	public VerticalComparator(double x){
		abs = x;
	}

	/**
	 * Set the absciss where we are going to work.
	 * @param x
	 */
	public final void setAbs(double x){
		abs = x;
	}

	/**
	 * get the absciss where we are currently working.
	 * @return
         *      The value currently used for comparison
	 */
	public final double getAbs(){
		return abs;
	}

	/**
	 * This comparison method is a vertical sorting test :
	 * Sort two edges (edge1 and edge2, indeed), and sort them according to their intersection point
	 * with the line l of equation x=abs.
	 * if p1 (p2) is the intersection between l and the line defined by edge1 (edge2),
	 * this method returns :
	 *  * -1 if p1 &lt; p2 or ( p1 == p2 and this is "under" edge)
	 *  * 0 if p1 == p2 and (this and edge are colinear)
	 *  * 1 if p1 &gt; p2 or (p1 == p2 and edge is under this)
	 *
	 * In our case, we will return -2 if one of the edge is vertical, and has absciss
	 * other than abs.
	 * @param edge1
	 * @param edge2
	 * @return
         * -1 if edge1 &lt; edge2<br />
         * 0 if edge1 == edge2<br />
         * 1 otherwise.
	 */
	@Override
	public final int compare(DEdge edge1, DEdge edge2) {
		int c;
		DPoint pEdge1 = null;
		DPoint pEdge2 = null;
		//If the two edges are equal, we return fast
		if(edge1.equals(edge2)){
			return 0;
		}
		try{
			//We retrieve the points that must be used to perform the comparison.
			pEdge1 = edge1.getPointFromItsX(abs);
			pEdge2 = edge2.getPointFromItsX(abs);
		} catch (DelaunayError e){
			LOG.error("Problem while processing the points from their absciss !");
		}
		if (pEdge1 == null || pEdge2 == null) {
			c=-2;
		} else {
			//We can perform the comparison.
			c = pEdge1.getY()<pEdge2.getY() ? -1 : 1;
			c = pEdge1.getY()==pEdge2.getY() ? 0 : c;
			if (c == 0) {
				//We have an intersection. (pEdge1 and pEdge2 are equal)
				if(edge1.isVertical()){
					if(edge2.isVertical()){
						double yr1 = edge1.getPointRight().getY();
						double yr2 = edge2.getPointRight().getY();
						return yr1 > yr2 ? 1: -1;
					}
					c = comparePoints(edge1.getPointRight(),edge2.getPointRight());
					if(edge1.getPointLeft().equals(pEdge1)){
						c = 1;
					} else if(edge1.getPointRight().equals(pEdge1)){
						c = -1;
					}
				} else if(edge2.isVertical()){
					c = comparePoints(edge2.getPointRight(),edge1.getPointRight());
					if(edge2.getPointLeft().equals(pEdge2)){
						c = -1;
					} else if(edge2.getPointRight().equals(pEdge2)){
						c = 1;
					}
				} else {
					double deltaXT = edge1.getPointRight().getX()-edge1.getPointLeft().getX();
					double deltaYT = edge1.getPointRight().getY()-edge1.getPointLeft().getY();
					double deltaXO = edge2.getPointRight().getX()-edge2.getPointLeft().getX();
					double deltaYO = edge2.getPointRight().getY()-edge2.getPointLeft().getY();
					double cT = deltaYT / deltaXT;
					double cO = deltaYO / deltaXO;
					if(-Tools.EPSILON < cT - cO && cT - cO < Tools.EPSILON){
						c = comparePoints(edge1.getPointRight(),edge2.getPointRight());
						if(c==0){
							c = comparePoints(edge1.getPointLeft(),edge2.getPointLeft());
						}
					} else if(cT < cO){
						c = -1;
					} else {
						c=1;
					}
				}
			}
		}
		return c;
	}

	/**
	 * Inner method of comparison between two points. we don't use any epsilon here
	 * to take in account the loss of precision we face when using double values.
	 * @param p1
	 * @param p2
	 * @return
	 */
	private int comparePoints(DPoint p1, DPoint p2){
		double x1 = p1.getX();
		double x2 = p2.getX();
		if(x1 < x2){
			return -1;
		} else if(x1 > x2){
			return 1;
		} else {
			double y1 = p1.getY();
			double y2 = p2.getY();
			if(y1 < y2){
				return -1;
			} else if(y1 > y2){
				return 1;
			} else {
				return 0;
			}
		}
	}
}
