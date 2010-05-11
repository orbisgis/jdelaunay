package org.jdelaunay.delaunay;

/**
 * Delaunay Package.
 *
 * @author Jean-Yves MARTIN, Erwan BOCHER
 * @date 2009-01-12
 * @version 1.1
 */

public abstract class MyElement {

	protected int gid;
	protected int property_type;
	protected int property_topo;
	
	/**
	 * default constructor
	 */
	public MyElement() {
		this.gid = -1;
		this.property_type = 0;
		this.property_topo = 0;
	}
	
	/**
	 * Constructor
	 */
	public MyElement(int property_type) {
		this.gid = -1;
		this.property_type = property_type;
		this.property_topo = 0;
	}

	/**
	 * Constructor
	 */
	public MyElement(int property_type, int gid) {
		this.gid = gid;
		this.property_type = property_type;
		this.property_topo = 0;
	}

	/**
	 * Constructor
	 */
	public MyElement(MyElement element) {
		this.gid = element.gid;
		this.property_type = element.property_type;
		this.property_topo = element.property_topo;
	}

	/**
	 * set GID value
	 * @param gid
	 */
	public void setGID(int gid) {
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
	 * set type value
	 * @param type
	 */
	public void setType(int type) {
		this.property_type = type;
	}

	/**
	 * add type value
	 * @param type
	 */
	public void addType(int type) {
		this.property_type |= type;
	}
	
	/**
	 * check for a specific type value
	 * @param type
	 */
	public boolean isType(int type) {
		return ((this.property_type & type) != 0);
	}

	/**
	 * get type value
	 * @param type
	 */
	public int getType() {
		return this.property_type;
	}

	/**
	 * set type value
	 * @param type
	 */
	public void setTopo(int topo) {
		this.property_topo = topo;
	}
	
	/**
	 * check for a specific type value
	 * @param type
	 */
	public boolean isTopo(int topo) {
		return ((this.property_topo & topo) != 0);
	}

	/**
	 * get type value
	 * @param type
	 */
	public int getTopo() {
		return this.property_topo;
	}
}
