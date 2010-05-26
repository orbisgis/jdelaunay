package org.jdelaunay.delaunay;

/**
 * Delaunay Package.
 * 
 * @author Jean-Yves MARTIN, Erwan BOCHER
 * @date 2009-01-12
 * @version 1.0
 */

public class MyQuadTreeMapper<T extends MyElement> {
	private MyBox myBoundingBox;
	private boolean usable;
	private MyQuadTree<T> myQuadTree;
	private static int maxLevel = 5;
	
	/**
	 * Constructor
	 */
	public MyQuadTreeMapper() {
		this.myBoundingBox = null;
		this.usable = false;
		this.myQuadTree = null;
	}
	
	/**
	 * Constructor
	 * @param theBox
	 */
	public MyQuadTreeMapper(MyBox theBox) {
		this.myBoundingBox = new MyBox(theBox);
		this.usable = true;
		this.myQuadTree = new MyQuadTree<T>(maxLevel);
	}

	/**
	 * Set QuadTree bounding box and allow insertion / searches
	 * @param theBox
	 */
	public void setBox(MyBox theBox) {
		if (! this.usable) {
			this.myBoundingBox = new MyBox(theBox);
			this.usable = true;
			this.myQuadTree = new MyQuadTree<T>(maxLevel);
		}
	}

	/**
	 * add an element in the QuadTree
	 * @param element
	 */
	public void add(T element) {
		if (this.usable) {
			MyBox theBox = element.getBoundingBox();
			myQuadTree.add(element, theBox, myBoundingBox);
		}
	}
	
	/**
	 * Search the element that contains the point
	 * @param aPoint
	 * @return anElement
	 */
	public T search(MyPoint aPoint) {
		T anElement = null;
		if (this.usable) {
			anElement = myQuadTree.search(aPoint, myBoundingBox);
		}
		return anElement;
	}
}
