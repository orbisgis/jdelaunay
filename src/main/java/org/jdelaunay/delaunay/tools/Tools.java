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
package org.jdelaunay.delaunay.tools;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.jdelaunay.delaunay.error.DelaunayError;
import org.jdelaunay.delaunay.geometries.DEdge;
import org.jdelaunay.delaunay.geometries.DPoint;
import org.jdelaunay.delaunay.geometries.DTriangle;
import org.jdelaunay.delaunay.geometries.Element;

/**
 * A set of utility methods
 *
 * @author Alexis Guéganno
 * @author Jean-Yves Martin
 * @author Erwan Bocher
 */

public final class Tools {

	public static final double EPSILON = 0.0000001;
	public static final double EPSILON2 = EPSILON * EPSILON;
	public static final int BIT_OUTSIDE = 1;
	public static final int BIT_LOCKED = 2;
	public static final int BIT_LEVEL = 3;
	public static final int BIT_POLYGON = 4;
	public static final int BIT_ZUSED = 5;
	public static final int BIT_MARKED = 6;
        
        public static final double PI_OVER_2 = Math.PI / 2.0;
        


	/**
	 * Default constructor is private : it is not supposed to be used !
	 */
	private Tools(){
		
	}

	/**
         * 
	 * Add an element to the list. This method takes care to ensure that we don't
	 * insert duplicated items in the list.
         * @param <T>
         * @param elt
         * @param sortedList
         * @param comp
         * @return 
         *      the position of the element in the list.
         */
	public static <T extends Element> int addToSortedList(T elt, List<T> sortedList, Comparator<T> comp){
		//We make a binary search, as divides and conquers rules...
		int index = Collections.binarySearch(sortedList, elt, comp);
		if(index < 0){
			//The position where we want to insert elt is -index-1, as the
			//value retruned by binary search is equal to (-insertPos -1)
			//(cf java.util.Collections javadoc)
			int insertPos = -index-1;
			sortedList.add(insertPos, elt);
		}
		return index;
	}

	/**
	 * Search the element elt in the sorted list sortedList. You are supposed
	 * to be sure that sortedList is actually sorted ;-)
	 * @param <T>
	 * @param sortedList
	 * @param elt
	 * @return
         *  the index of the element, or (-1-index) where index would be the index of the 
         *  element after insertion
	 */
	public static <T extends Element> int sortedListContains(List<T> sortedList, T elt, Comparator<T> comp) {
		//We make a binary search, as divides and conquers rules...
		int index = Collections.binarySearch(sortedList, elt, comp);
		//binarySearch will return the index of the element if it is found
		//(-insertPosition -1) otherwise. Consequently, if index > 0
		//we are sure that elt is in the list.
		return index;
	}

        /**
         * Compute the vector product from the vectors v1 and v2 represented with DPoint
         * @param v1
         * @param v2
         * @return
         *      The vector product as a DPoint.
         * @throws DelaunayError
         */
	public static DPoint vectorProduct(DPoint v1, DPoint v2) throws DelaunayError {
		DPoint v3 = new DPoint(0, 0, 0);
		v3.setX( v1.getY() * v2.getZ() - v1.getZ() * v2.getY());
		v3.setY( v1.getZ() * v2.getX() - v1.getX() * v2.getZ());
		v3.setZ( v1.getX() * v2.getY() - v1.getY() * v2.getX());
		return v3;
	}

        /**
	 * Checks that the vectors resulting from v1 and v2 are colinear.
	 * @param v1
	 * @param v2
	 * @return
         *      true if v1 and v2 are colinear.
	 */
	public static boolean isColinear(DPoint v1, DPoint v2) {
		double res = 0;
		res += Math.abs(v1.getY() * v2.getZ() - v1.getZ() * v2.getY());
		res += Math.abs(v1.getZ() * v2.getX() - v1.getX() * v2.getZ());
		res += Math.abs(v1.getX() * v2.getY() - v1.getY() * v2.getX());
		return res < EPSILON;
	}

        /**
	 * Compute the difference between two vectors.
	 * @param v1
	 * @param v2
	 * @return v1 - v2
         * @throws DelaunayError
	 */
	public static DPoint vectorialDiff(DPoint v1, DPoint v2) throws DelaunayError {
		DPoint v3 = new DPoint(0, 0, 0);
		v3.setX( v1.getX() - v2.getX());
		v3.setY(v1.getY() - v2.getY());
		v3.setZ(v1.getZ() - v2.getZ());
		return v3;
	}

        /**
	 * Calcule le point d'intersection de 2 droites coplanaires
	 *
	 * @param p1
	 *            point de la premiere droite
	 * @param v1
	 *            vecteur directeur de la premiere droite
	 * @param p2
	 *            point de la seconde droite
	 * @param v2
	 *            vecteur directeur de la seconde droite
	 * @return coordonnees du point d'intersection des 2 droites ou null si les
	 *         droites sont parrallèles
         * @throws DelaunayError
	 */

	public static DPoint computeIntersection(DPoint p1, DPoint v1,
			DPoint p2, DPoint v2) throws DelaunayError {
		double delta;
		double k;
		DPoint i = null;
		// methode de Cramer pour determiner l'intersection de 2 droites du plan
		delta = v1.getX() * (-v2.getY()) - (-v1.getY()) * v2.getX();
		if (delta != 0) {
			k = ((p2.getX() - p1.getX()) * (-v2.getY()) - (p2.getY() - p1.getY()) * (-v2.getX())) / delta;
			i = new DPoint();
			i.setX(p1.getX() + k * v1.getX());
			i.setY(p1.getY() + k * v1.getY());
			i.setZ(p1.getZ() + k * v1.getZ());
		}
		return i;
	}



	/**
	 * Check if the list given in argument is vertically sorted or not.
	 * @param edgeList
	 * @return
         *      true if edgeList is vertically sorted.
         * @see org.jdelaunay.delaunay.VerticalComparator
	 */
	public static boolean isVerticallySorted(List<DEdge> edgeList, double abs) throws DelaunayError {
		DEdge e1, e2;
		e2 = edgeList.get(0);
		for (int i = 1; i < edgeList.size(); i++) {
			e1 = e2;
			e2 = edgeList.get(i);
                        if (e1.verticalSort(e2, abs) == 1) {
                                return false;
                        }
		}
		return true;
	}
        
    /**
     * Returns the angle of the vector from p0 to p1, relative to the positive
     * X-axis. The angle is normalized to be in the range [ -Pi, Pi ].
     *
     * @param p0
     * @param p1
     * @return the normalized angle (in radians) that p0-p1 makes with the
     * positive x-axis.
     */
    public static double angle(DPoint p0, DPoint p1) {
        double dx = p1.getX() - p0.getX();
        double dy = p1.getY() - p0.getY();
        return Math.atan2(dy, dx);
    }
    
    /**
     * Compute the projection of a DPoint onto a DEdge
     * @param p
     * @param dEdge
     * @return
     * @throws DelaunayError 
     */
    public static DPoint project(DPoint p, DEdge dEdge) throws DelaunayError {
        DPoint p0 = dEdge.getStartPoint();
        DPoint p1 = dEdge.getEndPoint();
        if (p.equals(p0) || p.equals(p1)) {
            return p;
        }        
        double dx = p1.getX() - p0.getX();
        double dy = p1.getY() - p0.getY();
        double dz = p1.getZ()-p0.getZ();
        double hypo = dx * dx + dy * dy;        
        double r = ((p.getX() - p0.getX()) * dx + (p.getY() - p0.getY()) * dy)/ hypo;
        double xPoint = p0.getX() + r * dx;
        double yPoint = p0.getY() + r * dy;
        double zPoint = p0.getZ()+ r * dz;        
        return new DPoint(xPoint, yPoint, zPoint);
    }
    
    /**
     * Return the perpendicular bisectors for a triangle.
     * 
     * @param dTriangle
     * @return
     * @throws DelaunayError 
     */
    public static DEdge[] getPerpendicularBisectors(DTriangle dTriangle) throws DelaunayError {
        DPoint centerPoint = dTriangle.getCircumCenter();
        DEdge[] bisectors = new DEdge[3];
        int i = 0;
        for (DEdge edge : dTriangle.getEdges()) {
            bisectors[i] = new DEdge(centerPoint, project(centerPoint, edge));
            i++;
        }
        return bisectors;
    }

}
