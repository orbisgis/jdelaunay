package org.jdelaunay.delaunay;

import java.util.*;

/**
 * Delaunay Package.
 * 
 * @author Jean-Yves MARTIN, Erwan BOCHER
 * @date 2009-01-12
 * @version 1.0
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
	 * Generate a QuadTree with a specified level
	 * 
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
	protected void add(T element, MyBox theBox, MyBox boundingBox) {
		int NbPointsInside = 0;
		int indexIntersect = -1;
		MyBox saveBox = new MyBox();

		// Check in which area of the QuadTree it can be
		// Elements at the frontier may be counted several times -> the will be inserted in the root
		for (int sector = 0; sector < 4; sector++) {
			MyBox testBox = getSector(sector, boundingBox);
			if ((((theBox.minx >= testBox.minx) && (theBox.minx <= testBox.maxx))
					|| ((theBox.maxx >= testBox.minx) && (theBox.maxx <= testBox.maxx)))
					&& (((theBox.miny >= testBox.miny) && (theBox.miny <= testBox.maxy))
							|| ((theBox.maxy >= testBox.miny) && (theBox.maxy <= testBox.maxy)))) {
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

			// test bounding box of the each subarea and search inside if it is in the
			// box
			int i = 0;
			while ((i < 4) && (anElement == null)) {
				MyBox testBox = getSector(i, boundingBox);
				if ((x >= testBox.minx) && (x <= testBox.maxx)
						&& (y >= testBox.miny) && (y <= testBox.maxy)) {
					// One point at least is inside => memorize it
					anElement = search(aPoint, testBox);
				}
				i++;
			}
		}

		return anElement;
	}

}
