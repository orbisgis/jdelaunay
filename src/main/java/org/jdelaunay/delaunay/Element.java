package org.jdelaunay.delaunay;

import com.vividsolutions.jts.geom.Coordinate;


/**
 * Abstract container for all the geometric elements that are used during the triangulation
 *
 * @author Jean-Yves MARTIN, Erwan BOCHER, Adelin PIAU
 * @date 2009-01-12
 * @revision 2010-11-08
 * @version 2.0
 */

public abstract class Element {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5437683478248244942L;
	public static final int WEIGHT_CLASSIFICATION_NUMBER = 10;
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
	 */
	public Element(Element element) {
		init();
		this.property = element.property;
	}

	/**
	 * Constructor
	 */
	public Element(int property) {
		init();
		this.property = property;
	}

	/**
	 * Constructor
	 */
	public Element(int property, int gid) {
		init();
		this.gid = gid;
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
	 * @param gid
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
	public final void addProperty(int property) {
		this.property |= property;
	}
	
	/**
	 * check for a specific type value
	 * @param type
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
	 * @param type
	 */
	public final void removeProperties() {
		this.property = 0;
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
	 */
	public final int getExternalGID() {
		return externalGID;
	}

	/**
	 * Computed bounding box
	 * 
	 * @return
	 */
	public abstract BoundaryBox getBoundingBox();
	
	/**
	 * Check if the point is inside the element
	 * 
	 * @param aPoint
	 * @return bool
	 */
	public abstract boolean contains(DPoint aPoint);
	
	/**
	 * Check if the coordinate is inside the element
	 * 
	 * @param c
	 * @return bool
	 * @throws DelaunayError 
	 */
	public abstract boolean contains(Coordinate c) throws DelaunayError;
	
	/**
	 * @return Get all markers in indicator.
	 */
	public abstract int getIndicator();
	
	/**
	 * Change all marker.
	 * Use only to transfer all indicators of an element to a second element!
	 * @param indicator A new indicator.
	 * @return
	 */
	public abstract int setIndicator(int indicator);
	
	/**
	 *  Remove all indicator.
	 */
	public abstract void removeIndicator();
	
	/**
	 * check if it is use by a polygon
	 * @return useByPolygon
	 */
	public abstract boolean isUseByPolygon();
	
	/**
	 * set if it is use by a polygon.
	 * @param useByPolygon
	 */
	public abstract void setUseByPolygon(boolean useByPolygon);
	
	
	

}
