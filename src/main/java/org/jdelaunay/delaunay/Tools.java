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
package org.jdelaunay.delaunay;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A set of utility methods
 *
 * @author Jean-Yves MARTIN, Alexis GUEGANNO
 * @date 2009-01-12
 * @revision 2011-11-01
 * @version 2.1
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
        


	/**
	 * Default constructor is private : it is not supposed to be used !
	 */
	private Tools(){
		
	}

	/**
	 * Linear Z interpolation
	 * 
	 * @param p1
	 * @param p2
	 * @param aPoint
	 * @return
	 */
	public static double interpolateZ(DPoint p1, DPoint p2, DPoint aPoint) {
		double dist = p1.squareDistance(p2);
		double z = p2.getZ() - p1.getZ();

		double d = aPoint.squareDistance(p1);
		if (Math.abs(dist) > Tools.EPSILON2) {
			double factor = d / dist;
			return p1.getZ() + (factor * z);
		}
		else {
			return (p2.getZ() + p1.getZ()) / 2.0;
		}
	}

	/**
	 * Add an element to the list. This method takes care to ensure that we don't
	 * insert duplicated items in the list.
	 * @param <T extends Element & Comparable<? super T>>
	 * @param elt
	 * @param sortedList
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
         * @throws 
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



}
