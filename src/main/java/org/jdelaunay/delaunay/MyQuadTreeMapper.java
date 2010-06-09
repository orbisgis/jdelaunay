package org.jdelaunay.delaunay;

import java.util.ArrayList;

/**
 * Delaunay Package.
 * 
 * @author Jean-Yves MARTIN, Erwan BOCHER, Adelin PIAU
 * @date 2009-01-12
 * @revision 2010-06-9
 * @version 1.2
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
	 * Constructor.
	 * Generate a empty QuadTree with a specified level.
	 * Generate all nodes.
	 * @param theBox
	 */
	public MyQuadTreeMapper(MyBox theBox) {
		this.myBoundingBox = new MyBox(theBox);
		this.usable = true;
		this.myQuadTree = new MyQuadTree<T>(maxLevel);
	}
	
	/**
	 * Constructor
	 * Generate a QuadTree with a specified level and complete it with elements.
	 * Only not empty nodes are generated.
	 * @param theBox
	 */
	public MyQuadTreeMapper(MyBox theBox, ArrayList<T> elements) {
		this.myBoundingBox = new MyBox(theBox);
		this.usable = true;
		this.myQuadTree = new MyQuadTree<T>(maxLevel, myBoundingBox, elements);
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
	 * add all elements in the QuadTree
	 * @param elements
	 */
	public void addAll(ArrayList<T> elements) {
		if (this.usable) {
			MyBox theBox;
			for(T element:elements)
			{
				theBox = element.getBoundingBox();
				myQuadTree.add(element, theBox, myBoundingBox);
			}
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
	
	/**
	 * Search the element that contains the point
	 * @param aPoint
	 * @return anElement
	 */
	public T search(double x, double y) {
		T anElement = null;
		if (this.usable) {
			anElement = myQuadTree.search(new MyPoint(x,y), myBoundingBox);
		}
		return anElement;
	}
	
	/**
	 * Search the element that contains the point
	 * @param aPoint
	 * @return anElement
	 */
	public T search(double x, double y, double z) {
		T anElement = null;
		if (this.usable) {
			anElement = myQuadTree.search(new MyPoint(x,y, z), myBoundingBox);
		}
		return anElement;
	}
	
	
	/**
	 * Search all elements inside or on the area searchBoundingBox.
	 * @param searchBoundingBox Area of search.
	 * @return All elements inside or on the area searchBoundingBox.
	 */
	public ArrayList<T> searchAll(MyBox searchBoundingBox) {
		ArrayList<T>  allElements = null;
		if (this.usable) {
			allElements = myQuadTree.searchAll(searchBoundingBox, myBoundingBox);
		}
		return allElements;
	}
	
	
	/**
	 * Search all elements strictly inside the area searchBoundingBox.
	 * @param searchBoundingBox Area of search.
	 * @return All elements strictly inside the area searchBoundingBox.
	 */
	public ArrayList<T> searchAllStric(MyBox searchBoundingBox) {
		ArrayList<T>  allElements = null;
		if (this.usable) {
			allElements = myQuadTree.searchAllStric(searchBoundingBox, myBoundingBox);
		}
		return allElements;
	}
	
	
	/**
	 * Remove all elements strictly inside the polygon's area.
	 * @param aPolygon Area of search.
	 * @return All elements in the quadtree with out elements strictly inside the area searchBoundingBox.
	 */
	public ArrayList<T> removeAllStric(MyPolygon aPolygon) {
		ArrayList<T>  allElements = null;
		if (this.usable) {
			allElements = myQuadTree.removeAllStric(aPolygon, myBoundingBox);
		}
		return allElements;
	}
	
	public ArrayList<T> getAll()
	{
		return myQuadTree.getAll();
	}


}
