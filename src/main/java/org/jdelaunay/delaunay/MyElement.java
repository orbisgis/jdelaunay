package org.jdelaunay.delaunay;

import com.vividsolutions.jts.geom.Coordinate;


/**
 * Delaunay Package.
 *
 * @author Jean-Yves MARTIN, Erwan BOCHER, Adelin PIAU
 * @date 2009-01-12
 * @revision 2010-07-22
 * @version 2.0
 */

public abstract class MyElement {
	protected int gid;
	protected int property;

	/**
	 * Default initialization
	 */
	private void init() {
		this.gid = -1;
		this.property = 0;
	}
	/**
	 * default constructor
	 */
	public MyElement() {
		init();
	}
	
	/**
	 * Constructor
	 */
	public MyElement(MyElement element) {
		init();
		this.property = element.property;
	}

	/**
	 * Constructor
	 */
	public MyElement(int property) {
		init();
		this.property = property;
	}

	/**
	 * Constructor
	 */
	public MyElement(int property, int gid) {
		init();
		this.gid = gid;
		this.property = property;
	}

	/**
	 * set GID value
	 * @param gid
	 */
	public void setGID(int gid) {
		this.gid = gid;
	}
	
	/**
	 * set GID value
	 * @param gid
	 */
	public void setGid(int gid) {
		this.gid = gid;
	}

	/**
	 * get GID value
	 * @param gid
	 */
	public int getGID() {
		return this.gid;
	}

	/**
	 * get GID value
	 * @param gid
	 */
	public int getGid() {
		return this.gid;
	}

	/**
	 * set property value
	 * @param property
	 */
	public void setProperty(int property) {
		this.property = property;
	}

	/**
	 * add property value
	 * @param property
	 */
	public void addProperty(int property) {
		this.property |= property;
	}
	
	/**
	 * check for a specific type value
	 * @param type
	 */
	public boolean hasProperty(int property) {
		return ((this.property & property) != 0);
	}

	/**
	 * get property value
	 * @return property
	 */
	public int getProperty() {
		return this.property;
	}

	/**
	 * Remove all properties of the element
	 * @param type
	 */
	public void removeProperties() {
		this.property = 0;
	}
	
	/**
	 * Computed bounding box
	 * 
	 * @return
	 */
	public abstract MyBox getBoundingBox();
	
	/**
	 * Check if the point is inside the element
	 * 
	 * @param aPoint
	 * @return bool
	 */
	public abstract boolean contains(MyPoint aPoint);
	
	/**
	 * Check if the coordinate is inside the element
	 * 
	 * @param c
	 * @return bool
	 * @throws DelaunayError 
	 */
	public abstract boolean contains(Coordinate c) throws DelaunayError;
	
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
