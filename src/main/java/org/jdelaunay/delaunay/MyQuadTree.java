package org.jdelaunay.delaunay;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Delaunay Package.
 * 
 * @author Jean-Yves MARTIN, Erwan BOCHER, Adelin PIAU
 * @date 2009-01-12
 * @revision 2010-06-23
 * @version 1.2
 */

public class MyQuadTree<T extends MyElement> {
	private MyQuadTree<T> theQuadTree[];
	private LinkedList<T> theList;

	/**
	 * General initialization
	 */
	private void init() {
		this.theQuadTree = new MyQuadTree[4];
		for (int i = 0; i < 4; i++)
			this.theQuadTree[i] = null;

		this.theList = new LinkedList<T>();
	}

	/**
	 * Default constructor
	 */
	public MyQuadTree() {
		init();
	}

	/**
	 * Generate a empty QuadTree with a specified level.
	 * Generate all nodes.
	 * @param maxLevel
	 */
	public MyQuadTree(int maxLevel) {
		init();
		if (maxLevel > 0) {
			for (int i = 0; i < 4; i++) {
				theQuadTree[i] = new MyQuadTree<T>(maxLevel - 1);
			}
		}
	}

	
	/**
	 * Generate a QuadTree with a specified level and complete it with elements.
	 * Only not empty nodes are generated.
	 * @param maxLevel
	 */
	public MyQuadTree(int maxLevel, MyBox boundingBox, ArrayList<T> elements) {
		init();
		for(T element:elements)
			add(element, element.getBoundingBox(), boundingBox, maxLevel);
	}
	
	
	/**
	 * Get the 2D area of an element of the QuadTree
	 * 
	 * @param sector
	 * @param boundingBox
	 */
	private MyBox getSector(int sector, MyBox boundingBox) {
		MyBox aBox = new MyBox();

		switch (sector) {
		case 0:
			aBox.minx = boundingBox.minx;
			aBox.maxx = (boundingBox.minx + boundingBox.maxx) / 2.0;
			aBox.miny = boundingBox.miny;
			aBox.maxy = (boundingBox.miny + boundingBox.maxy) / 2.0;
			break;
		case 1:
			aBox.minx = boundingBox.minx;
			aBox.maxx = (boundingBox.minx + boundingBox.maxx) / 2.0;
			aBox.miny = (boundingBox.miny + boundingBox.maxy) / 2.0;
			aBox.maxy = boundingBox.maxy;
			break;
		case 2:
			aBox.minx = (boundingBox.minx + boundingBox.maxx) / 2.0;
			aBox.maxx = boundingBox.maxx;
			aBox.miny = boundingBox.miny;
			aBox.maxy = (boundingBox.miny + boundingBox.maxy) / 2.0;
			break;
		case 3:
			aBox.minx = (boundingBox.minx + boundingBox.maxx) / 2.0;
			aBox.maxx = boundingBox.maxx;
			aBox.miny = (boundingBox.miny + boundingBox.maxy) / 2.0;
			aBox.maxy = boundingBox.maxy;
			break;
		}
		return aBox;
	}

	
	/**
	 * Insert element in the QuadTree
	 * 
	 * @param element
	 * @param theBox
	 * @param boundingBox
	 */
	protected void add(T element, MyBox theBox, MyBox boundingBox, int maxLevel) {
		int NbPointsInside = 0;
		int indexIntersect = -1;
		MyBox saveBox = new MyBox();

		// Check in which area of the QuadTree it can be
		// Elements at the frontier may be counted several times -> the will be
		// inserted in the root
		for (int sector = 0; sector < 4; sector++) {
			MyBox testBox = getSector(sector, boundingBox);
			if ((((theBox.minx >= testBox.minx) && (theBox.minx <= testBox.maxx)) || ((theBox.maxx >= testBox.minx) && (theBox.maxx <= testBox.maxx)))
					&& (((theBox.miny >= testBox.miny) && (theBox.miny <= testBox.maxy)) || ((theBox.maxy >= testBox.miny) && (theBox.maxy <= testBox.maxy)))) {
				// One point at least is inside => memorize it
				indexIntersect = sector;
				NbPointsInside++;
				saveBox = new MyBox(testBox);
			}
		}

		
		// Insert it or go further
		if ((NbPointsInside == 0) || (NbPointsInside > 1))
			theList.add(element);
		else if (maxLevel-1<=0)
			theList.add(element);
		else
		{	if(theQuadTree[indexIntersect]==null)
				theQuadTree[indexIntersect]=new MyQuadTree<T>();
			theQuadTree[indexIntersect].add(element, theBox, saveBox, maxLevel-1);
		}
	}
	
	
	/**
	 * Insert element in the QuadTree
	 * 
	 * @param element
	 * @param theBox
	 * @param boundingBox
	 */
	protected void add(T element, MyBox theBox, MyBox boundingBox) {
		int NbPointsInside = 0;
		int indexIntersect = -1;
		MyBox saveBox = new MyBox();
		

		// Check in which area of the QuadTree it can be
		// Elements at the frontier may be counted several times -> the will be
		// inserted in the root
		for (int sector = 0; sector < 4; sector++) {
			MyBox testBox = getSector(sector, boundingBox);
			if ((((theBox.minx >= testBox.minx) && (theBox.minx <= testBox.maxx)) || ((theBox.maxx >= testBox.minx) && (theBox.maxx <= testBox.maxx)))
					&& (((theBox.miny >= testBox.miny) && (theBox.miny <= testBox.maxy)) || ((theBox.maxy >= testBox.miny) && (theBox.maxy <= testBox.maxy)))) {
				// One point at least is inside => memorize it
				indexIntersect = sector;
				NbPointsInside++;
				saveBox = new MyBox(testBox);
			}
		}


		
		// Insert it or go further
		if ((NbPointsInside == 0) || (NbPointsInside > 1))
			theList.add(element);
		else if (theQuadTree[indexIntersect] == null)
			theList.add(element);
		else
			theQuadTree[indexIntersect].add(element, theBox, saveBox);
	}

	/**
	 * Search an element in the tree
	 * 
	 * @param aPoint
	 * @param boundingBox
	 * @return
	 */
	protected T search(MyPoint aPoint, MyBox boundingBox) {
		T anElement = null;

		// test root list
		ListIterator<T> iterList = theList.listIterator();
		while ((iterList.hasNext()) && (anElement == null)) {
			T searchedElement = iterList.next();
			if (searchedElement.contains(aPoint)) {
				anElement = searchedElement;
			}
		}

		// if not found try QuadTree areas
		if (anElement == null) {
			double x = aPoint.getX();
			double y = aPoint.getY();

			// test bounding box of the each subarea and search inside if it is
			// in the box
			int i = 0;
			while ((i < 4) && (anElement == null)) {
				if (theQuadTree[i] != null) {
					MyBox testBox = getSector(i, boundingBox);
					if ((x >= testBox.minx) && (x <= testBox.maxx)
							&& (y >= testBox.miny) && (y <= testBox.maxy)) {
						// One point at least is inside => memorize it
						anElement = theQuadTree[i].search(aPoint, testBox);
					}
				}
				i++;
			}
		}

		return anElement;
	}

	/**
	 * Remove elements from the lists
	 */
	protected void removeData() {
		while (! theList.isEmpty()) {
			theList.removeFirst();
		}

		// if not found try QuadTree areas
		for (int i=0; i<4; i++) {
			if (theQuadTree[i] != null) {
				theQuadTree[i].removeData();
			}
		}
	}
	
	/**
	 * Search all elements inside or on the area searchBoundingBox.
	 * 
	 * @param searchBoundingBox Area of search.
	 * @param boundingBox Bounding box of quad tree.
	 * @return All elements inside or on the area searchBoundingBox.
	 */
	protected ArrayList<T> searchAll(MyBox searchBoundingBox, MyBox boundingBox) {
		ArrayList<T>  allElements = new ArrayList<T>();
		
		// test root list
		ListIterator<T> iterList = theList.listIterator();
		while ((iterList.hasNext())) {
			T searchedElement = iterList.next();
			if(searchedElement.getClass().getName().equals("org.jdelaunay.delaunay.MyPoint"))
			{
				if(		((MyPoint)searchedElement).getX()>= searchBoundingBox.minx &&
						((MyPoint)searchedElement).getX()<= searchBoundingBox.maxx &&
						((MyPoint)searchedElement).getY()>= searchBoundingBox.miny &&
						((MyPoint)searchedElement).getY()<= searchBoundingBox.maxy
				)
				{	
					allElements.add(searchedElement);
				}
			}
			else
				allElements.add(searchedElement);
		}

		// test bounding box of the each subarea and search inside if it is
		// in the
		// box
		int i = 0;
		while ((i < 4)) {
			if (theQuadTree[i] != null) {
				MyBox testBox = getSector(i, boundingBox);
				
				if (!((searchBoundingBox.maxx <= testBox.minx) || (searchBoundingBox.minx >= testBox.maxx)
				|| (searchBoundingBox.miny >= testBox.maxy) || (searchBoundingBox.maxy <= testBox.miny)) )
				{
					allElements.addAll(theQuadTree[i].searchAll(searchBoundingBox, testBox));
				}
			}
			i++;
		}

		return allElements;
	}
	
	/**
	 * Search all elements strictly inside the area searchBoundingBox.
	 * 
	 * @param searchBoundingBox Area of search.
	 * @param boundingBox Bounding box of quad tree.
	 * @return All elements strictly inside the area searchBoundingBox.
	 */
	protected ArrayList<T> searchAllStric(MyBox searchBoundingBox, MyBox boundingBox) {
		ArrayList<T>  allElements = new ArrayList<T>();
		
		// test root list
		ListIterator<T> iterList = theList.listIterator();
		while ((iterList.hasNext())) {
			T searchedElement = iterList.next();
			if(searchedElement.getClass().getName().equals("org.jdelaunay.delaunay.MyPoint"))
			{
				if(		((MyPoint)searchedElement).getX()> searchBoundingBox.minx &&
						((MyPoint)searchedElement).getX()< searchBoundingBox.maxx &&
						((MyPoint)searchedElement).getY()> searchBoundingBox.miny &&
						((MyPoint)searchedElement).getY()< searchBoundingBox.maxy
				)
				{	
					allElements.add(searchedElement);
				}
			}
			else
				allElements.add(searchedElement);
		}

		// test bounding box of the each subarea and search inside if it is
		// in the
		// box
		int i = 0;
		while ((i < 4)) {
			if (theQuadTree[i] != null) {
				MyBox testBox = getSector(i, boundingBox);
				
				if (!((searchBoundingBox.maxx < testBox.minx) || (searchBoundingBox.minx > testBox.maxx)
				|| (searchBoundingBox.miny > testBox.maxy) || (searchBoundingBox.maxy < testBox.miny)) )
				{
					allElements.addAll(theQuadTree[i].searchAllStric(searchBoundingBox, testBox));
				}
			}
			i++;
		}

		return allElements;
	}
	
	
	
	
	
	/**
	 * Remove all elements strictly inside the polygon's area.
	 * 
	 * @param aPolygon Area of search.
	 * @param boundingBox Bounding box of quad tree.
	 * @return All elements strictly inside the area searchBoundingBox.
	 */
	protected ArrayList<T> removeAllStric(MyPolygon aPolygon, MyBox boundingBox) {
		
		return removeAllStric(aPolygon, aPolygon.getBoundingBox(),boundingBox);
	}
	
	/**
	 * Remove all elements strictly inside the polygon's area.
	 * 
	 * @param aPolygon Area of search.
	 * @param boundingBox Bounding box of quad tree.
	 * @return All elements in the quadtree with out elements strictly inside the area searchBoundingBox.
	 */
	private ArrayList<T> removeAllStric(MyPolygon aPolygon, MyBox searchBoundingBox, MyBox boundingBox) {
		ArrayList<T>  allElements = new ArrayList<T>();
		
		// test root list
		ListIterator<T> iterList = theList.listIterator();
		while ((iterList.hasNext())) {
			T searchedElement = iterList.next();
			
			if(searchedElement.getClass().getName().equals("org.jdelaunay.delaunay.MyPoint") && aPolygon.contains((MyPoint)searchedElement) && !((MyPoint)searchedElement).isUseByPolygon())
			{	
				iterList.remove();
			}
			else if(searchedElement.getClass().getName().equals("org.jdelaunay.delaunay.MyEdge") && !((MyEdge)searchedElement).isUseByPolygon() && ( aPolygon.contains(((MyEdge)searchedElement).getStartPoint()) || aPolygon.contains(((MyEdge)searchedElement).getEndPoint()) )	)
			{	
				iterList.remove();
			}
			else if(searchedElement.getClass().getName().equals("org.jdelaunay.delaunay.MyTriangle") && aPolygon.contains(((MyTriangle)searchedElement).getBarycenter()))
			{	
				iterList.remove();
			}
			else
				allElements.add(searchedElement);
		}
		

		// test bounding box of the each subarea and search inside if it is in the box
		int i = 0;
		MyBox testBox;
		while ((i < 4)) {
			if (theQuadTree[i] != null) {
				testBox = getSector(i, boundingBox);
				
//				if(searchBoundingBox.minx <= testBox.maxx && searchBoundingBox.maxx >= testBox.minx
//					&& searchBoundingBox.miny <= testBox.maxy && searchBoundingBox.maxy >= testBox.miny)
//				{
					allElements.addAll(theQuadTree[i].removeAllStric(aPolygon, searchBoundingBox,testBox));
					// Remove empty node
					if(theQuadTree[i].theList.size()<=0 && theQuadTree[i].theQuadTree[0]==null
														&& theQuadTree[i].theQuadTree[1]==null
														&& theQuadTree[i].theQuadTree[2]==null
														&& theQuadTree[i].theQuadTree[3]==null)
						theQuadTree[i]=null;
//				}


			}
			i++;
		}
		return allElements;
	}
	
	
	protected ArrayList<T> getAll() {
		ArrayList<T>  allElements = new ArrayList<T>();
		
		// test root list
		ListIterator<T> iterList = theList.listIterator();
		while ((iterList.hasNext())) {
			T searchedElement = iterList.next();
			allElements.add(searchedElement);
		}
		

		// test bounding box of the each subarea and search inside if it is
		// in the
		// box
		int i = 0;
		while ((i < 4)) {
			if (theQuadTree[i] != null) {
					allElements.addAll(theQuadTree[i].getAll());

			}
			i++;
		}

		return allElements;
	}
}
