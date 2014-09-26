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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jdelaunay.delaunay.error.DelaunayError;


/**
 * Abstract container for all the geometric elements that are used during the triangulation
 *
 * @author Adelin Piau
 * @author Jean-Yves Martin
 * @author Erwan Bocher
 */

public abstract class Element {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5437683478248244942L;
        /**
         * The list that will contains the property values that will be used during the intersection. Note that
         * it is up to you to fill the list with the property values you need. Property
         * values must be managed as described in addProperty.
         */
	public static final List<Integer> WEIGHTED_PROPERTIES = new ArrayList<Integer>();
	private int gid;
	private int property;
	//An identifier to use correspondance maps externally, to affect properties efficiently.
	private int externalGID;

	/**
	 * Default initialization
	 */
	private void init() {
		this.gid = -1;
		this.property = 0;
		externalGID = -1;
	}
	/**
	 * default constructor
	 */
	public Element() {
		init();
	}
	
	/**
	 * Constructor
         * @param element
	 */
	public Element(Element element) {
		init();
		this.property = element.property;
	}

	/**
	 * Constructor
         * @param property
	 */
	public Element(int property) {
		init();
		this.property = property;
	}

	/**
	 * set GID value
	 * @param gid
	 */
	public final void setGID(int gid) {
		this.gid = gid;
	}

	/**
	 * get GID value
         * @return 
	 */
	public final int getGID() {
		return this.gid;
	}

	/**
	 * set property value
	 * @param property
	 */
	public final void setProperty(int property) {
		this.property = property;
	}

	/**
	 * add property value. Properties are managed, with this method, by making
	 * a or operation on the bits of the underlying int.
	 *
	 * We are marking bits on an int, where each bit can be associated (externally)
	 * to a given property. To mark the bit number 5, for instance, you must add the
	 * property 16 (as the only bit set to 1 in 16 is the fifth one).
	 *
	 * If you add the property 25, you will set the bits 5, 3 and 2 to 1
	 *
	 * @param property
	 */
	public void addProperty(int property) {
		this.property |= property;
	}
	
	/**
	 * check for a specific type value
	 * @param property
	 */
	public final boolean hasProperty(int property) {
		return ((this.property & property) != 0);
	}

	/**
	 * get property value
	 * @return property
	 */
	public final int getProperty() {
		return this.property;
	}

	/**
	 * Remove all properties of the element
	 */
	public final void removeProperties() {
		this.property = 0;
	}


	/**
	 * Get the weight of this DEdge. This property will be used, fo instance,
	 * by the forceConstraintIntegrity in ConstrainedMesh, to decide which Z value
	 * to use when computing a new intersection.
	 * @param map
	 * @return
         * The maximal weight
	 */
	public final int getMaxWeight(Map<Integer, Integer> map){
		int weight = -1;
		for(Integer i : Element.WEIGHTED_PROPERTIES){
			if(hasProperty(i) && map.containsKey(i)){
				weight = weight < map.get(i) ? map.get(i) : weight;
			}
		}
		return weight;
	}
	/**
	 * Set an external GID, referencing this object for an external use, and 
	 * eventually make correspondances with an external attributes table.
	 * @param externalGID
	 */
	public final void setExternalGID(int externalGID) {
		this.externalGID = externalGID;
	}

	/**
	 * get the external GID associated to the object (-1 if it has not been set)
	 * @return
         *      The external gid for this element.
	 */
	public final int getExternalGID() {
		return externalGID;
	}

	
	/**
	 * Computed bounding box
	 * 
	 * @return
         *      The boundary box of this element.
         * @throws org.jdelaunay.delaunay.error.DelaunayError
	 */
	public abstract BoundaryBox getBoundingBox() throws DelaunayError;
	
	/**
	 * Check if the point is inside the element
	 * 
	 * @param aPoint
	 * @return bool
	 */
	public abstract boolean contains(DPoint aPoint);
	

}
