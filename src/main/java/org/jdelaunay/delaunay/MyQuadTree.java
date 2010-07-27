package org.jdelaunay.delaunay;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Delaunay Package.
 * 
 * @author Jean-Yves MARTIN, Erwan BOCHER, Adelin PIAU
 * @date 2009-01-12
 * @revision 2010-07-27
 * @version 1.3
 */

public class MyQuadTree<T extends MyElement> {
	/**
	 * 0 : bottom left
	 * 1 : top left
	 * 2 : bottom right
	 * 3 : top left
	 */
	private MyQuadTree<T> theQuadTree[];
	
	/**
	 * Lists of elements.
	 * (see getSectorList())
	 */
	private LinkedList<T>[] theList;
	
	private int level=0; 

	/**
	 * General initialization
	 */
	private void init() {
		this.theQuadTree = new MyQuadTree[4];
		for (int i = 0; i < 4; i++)
			theQuadTree[i] = null;

		theList = new LinkedList[6];
		for (int i = 0; i < 6; i++)
			theList[i] = new LinkedList<T>();
	}

	/**
	 * Generate a empty QuadTree with a specified level.
	 * Generate all nodes.
	 * @param maxLevel
	 */
	public MyQuadTree(int maxLevel) {
		level=maxLevel;
		init();
	}

	
	/**
	 * Generate a QuadTree with a specified level and complete it with elements.
	 * Only not empty nodes are generated.
	 * @param maxLevel
	 */
	public MyQuadTree(int maxLevel, MyBox boundingBox, ArrayList<T> elements) {
		init();
		level=maxLevel;
		System.out.println("bip");
		for(T element:elements)
			add(element, element.getBoundingBox(), boundingBox, maxLevel);
	}
	
	
	/**
	 * Get the 2D area of an element of the QuadTree
	 * 
	 * @param sector
	 * @param boundingBox
	 */
	private MyBox getSectorNode(int sector, MyBox boundingBox) {
		MyBox aBox = new MyBox();

		switch (sector) {
		case 0: // bottom left
			aBox.minx = boundingBox.minx;
			aBox.maxx = (boundingBox.minx + boundingBox.maxx) / 2.0;
			aBox.miny = boundingBox.miny;
			aBox.maxy = (boundingBox.miny + boundingBox.maxy) / 2.0;
			break;
		case 1: //top left
			aBox.minx = boundingBox.minx;
			aBox.maxx = (boundingBox.minx + boundingBox.maxx) / 2.0;
			aBox.miny = (boundingBox.miny + boundingBox.maxy) / 2.0;
			aBox.maxy = boundingBox.maxy;
			break;
		case 2: //bottom right 
			aBox.minx = (boundingBox.minx + boundingBox.maxx) / 2.0;
			aBox.maxx = boundingBox.maxx;
			aBox.miny = boundingBox.miny;
			aBox.maxy = (boundingBox.miny + boundingBox.maxy) / 2.0;
			break;
		case 3: //top right
			aBox.minx = (boundingBox.minx + boundingBox.maxx) / 2.0;
			aBox.maxx = boundingBox.maxx;
			aBox.miny = (boundingBox.miny + boundingBox.maxy) / 2.0;
			aBox.maxy = boundingBox.maxy;
			break;
		}
		return aBox;
	}

	
	/**
	 * @param elementBoundingBox Bounding Box of an element.
	 * @param boundingBox Bounding Box of the node of quadtree.
	 * @return The number of the case of thelist who contain the good list of element.
	 */
	private int getSectorList(MyBox elementBoundingBox, MyBox boundingBox) {	
		//	 _______ _______ _______
		//	|		|		|		|
		//	|		|	2	|		|
		//	|		|_______|		|
		//	|		|		|		|	<--- boundingBox
		//	|	1	|	3	|	5	|
		//	|		|_______|		|
		//	|		|		|		|
		//	|		|	4	|		|	0 out of boundingBox
		//	|_______|_______|_______|

		int sector=0;
		double width=boundingBox.maxx-boundingBox.minx;
		
		if(elementBoundingBox.maxx<=boundingBox.minx+width/3)// left
		{
			sector=1;
		}
		else if(elementBoundingBox.minx>=boundingBox.minx +width*2/3)// right
		{
			sector=5;
		}
		else if(elementBoundingBox.minx>boundingBox.minx +width/3 && elementBoundingBox.maxx<boundingBox.minx +width*2/3)// middle
		{
			double height=boundingBox.maxy-boundingBox.miny;
			
			if(elementBoundingBox.miny>=boundingBox.miny+height*2/3)// middle top
			{
				sector=2;
			}
			else if(elementBoundingBox.maxy<=boundingBox.miny+height/3)// middle bottom
			{
				sector=4;
			}
			else if(elementBoundingBox.miny>boundingBox.miny+height/3 && elementBoundingBox.maxy<boundingBox.miny+height*2/3)// middle
			{
				sector=3;
			}
		}
			
		return sector;
	}

	
	/**
	 * @param sector Between 1 and 5 include.
	 * @param elementBoundingBox Bounding Box of an element.
	 * @param boundingBox Bounding Box of the node of quadtree.
	 * @return True is elementBoundingBox intersect the sector.
	 */
	private boolean isIntersectSectorList(int sector, MyBox elementBoundingBox, MyBox boundingBox) {	
		
		//	 _______ _______ _______
		//	|		|		|		|
		//	|		|	2	|		|
		//	|		|_______|		|
		//	|		|		|		|	<--- boundingBox
		//	|	1	|	3	|	5	|
		//	|		|_______|		|
		//	|		|		|		|
		//	|		|	4	|		|
		//	|_______|_______|_______|

		boolean intersect =false;
		double width=boundingBox.maxx-boundingBox.minx;
		double height=boundingBox.maxy-boundingBox.miny;
		
		switch (sector) {
			case 1:// left
				if(elementBoundingBox.minx<=boundingBox.minx+width/3)
					intersect =true;
				break;
				
			case 2:// middle top
				if(elementBoundingBox.minx<boundingBox.minx+width*2/3 && elementBoundingBox.maxx>boundingBox.minx+width/3
						&& elementBoundingBox.maxy>=boundingBox.miny+height*2/3)
					intersect =true;
				break;

			case 3:// middle
				if(elementBoundingBox.minx<boundingBox.minx+width*2/3 && elementBoundingBox.maxx>boundingBox.minx+width/3
						&& elementBoundingBox.maxy>boundingBox.miny+height/3 && elementBoundingBox.miny<boundingBox.miny+height*2/3)
					intersect =true;
				break;
				
			case 4:// middle bottom
				if(elementBoundingBox.minx<boundingBox.minx+width*2/3 && elementBoundingBox.maxx>boundingBox.minx+width/3
						&& elementBoundingBox.miny<=boundingBox.miny+height/3)
					intersect =true;
				break;
				
			case 5:// right
				if(elementBoundingBox.maxx>=boundingBox.minx+width*2/3)
					intersect =true;
				break;
		}
		return intersect;
	}
	
	
	/**
	 * Insert element in the QuadTree
	 * 
	 * @param element
	 * @param theBox
	 * @param boundingBox
	 */
	protected void add(T element, MyBox theBox, MyBox boundingBox, int maxLevel) {
		int indexIntersect = -1;
		MyBox saveBox = null;

		// Check in which area of the QuadTree it can be
		// Elements at the frontier may be counted several times -> the will be
		// inserted in the root
		for (int sector = 0; sector < 4; sector++) {
			MyBox testBox = getSectorNode(sector, boundingBox);
				if ( (theBox.minx >= testBox.minx) && (theBox.maxx <= testBox.maxx) 
				 &&  (theBox.miny >= testBox.miny) && (theBox.maxy <= testBox.maxy) ) {
				// One point at least is inside => memorize it
				indexIntersect = sector;
				saveBox = new MyBox(testBox);
			}
		}

		
		// Insert it or go further
		if( indexIntersect == -1)
			add(element, boundingBox);
		else if (maxLevel-1<=0)
			add(element, boundingBox);
		else
		{	
			if(theQuadTree[indexIntersect]==null)
				theQuadTree[indexIntersect]=new MyQuadTree<T>(maxLevel-1);
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
		int indexIntersect = -1;
		MyBox saveBox = null;

		// Check in which area of the QuadTree it can be
		// Elements at the frontier may be counted several times -> the will be
		// inserted in the root
		for (int sector = 0; sector < 4; sector++) {
			MyBox testBox = getSectorNode(sector, boundingBox);
				if ( (theBox.minx >= testBox.minx) && (theBox.maxx <= testBox.maxx) 
						 &&  (theBox.miny >= testBox.miny) && (theBox.maxy <= testBox.maxy) ) {

			// One point at least is inside => memorize it
				indexIntersect = sector;
				saveBox = new MyBox(testBox);
			}
		}

		// Insert it or go further
		if(indexIntersect == -1)
		{
			add(element, boundingBox);
		}
		else if (theQuadTree[indexIntersect] == null)
		{
			if(level>0)
			{
				theQuadTree[indexIntersect]=new MyQuadTree<T>(level-1);
				theQuadTree[indexIntersect].add(element, theBox, saveBox);
			}
			else
				add(element, boundingBox);
		}
		else
			theQuadTree[indexIntersect].add(element, theBox, saveBox);
	}


	
	
	/**
	 * Insert element in the QuadTree
	 * @param element
	 * @param boundingBo 
	 */
	private void add(T element, MyBox boundingBox)//FIXME too slow
	{
		int i, sector =getSectorList(element.getBoundingBox(), boundingBox);
		for(i=0;
				i<theList[sector].size() 
				&& ( theList[sector].get(i).getBoundingBox().maxx > element.getBoundingBox().maxx  
				);i++
			);

		theList[sector].add(i,element);

	}
	

	
	/**
	 * Search an element in the tree.
	 * 
	 * @param element
	 * @param boundingBox
	 * @return True if it's inside the tree. 
	 */
	protected boolean contains(T element, MyBox boundingBox) {
		boolean found = false;

		// test root list
		found=theList[getSectorList(element.getBoundingBox(), boundingBox)].contains(element);
			
		// if not found try QuadTree areas
		if (found== false) {
			double x = element.getBoundingBox().getMiddle().x;
			double y = element.getBoundingBox().getMiddle().y;

			// test bounding box of the each subarea and search inside if it is
			// in the box
			int i = 0;
			while ((i < 4) && (found == false)) {
				if (theQuadTree[i] != null) {
					MyBox testBox = getSectorNode(i, boundingBox);
					if ((x >= testBox.minx) && (x <= testBox.maxx)
							&& (y >= testBox.miny) && (y <= testBox.maxy)) {
						// One point at least is inside => memorize it
						found |= theQuadTree[i].contains(element, testBox);
					}
				}
				i++;
			}
		}
		return found;
	}
	
	
	/**
	 * Search an element in the tree
	 * 
	 * @param aPoint
	 * @param boundingBox
	 * @return
	 */
	protected T search(MyPoint aPoint, double precision, MyBox boundingBox) {
		T anElement = null;

		// test root list
		ListIterator<T>	iterList = theList[getSectorList(aPoint.getBoundingBox(), boundingBox)].listIterator();
			while ((iterList.hasNext()) && (anElement == null)) {
				T searchedElement = iterList.next();
			
				if(aPoint.getBoundingBox().minx>searchedElement.getBoundingBox().maxx)
					break;
				
				double a,b;
				if(searchedElement.getClass().getName().equals("org.jdelaunay.delaunay.MyPoint"))
				{	a=((MyPoint)searchedElement).getX() - aPoint.getX();
					b=((MyPoint)searchedElement).getY() - aPoint.getY();
				}
				else
				{
					a=searchedElement.getBoundingBox().getMiddle().x;
					b=searchedElement.getBoundingBox().getMiddle().y;
				}
				if (	a>=-precision &&
						a<=precision &&
						b>=-precision &&
						b<=precision)
					 {
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
					MyBox testBox = getSectorNode(i, boundingBox);
					if ((x >= testBox.minx) && (x <= testBox.maxx)
							&& (y >= testBox.miny) && (y <= testBox.maxy)) {
						// One point at least is inside => memorize it
						anElement = theQuadTree[i].search(aPoint,precision, testBox);
					}
				}
				i++;
			}
		}

		return anElement;
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
		ListIterator<T>	iterList = theList[0].listIterator();
		while ((iterList.hasNext()) && (anElement == null)) {
			T searchedElement = iterList.next();
			
			if (searchedElement.contains(aPoint)) {
				anElement = searchedElement;
			}
		}

		for(int i=1;i<6 && anElement==null;i++)
		{
				if(isIntersectSectorList(i, aPoint.getBoundingBox(), boundingBox))
				{
					iterList = theList[i].listIterator();
					while ((iterList.hasNext()) && (anElement == null)) {
						T searchedElement = iterList.next();
						
						if (searchedElement.contains(aPoint)) {
							anElement = searchedElement;
						}
					}
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
					MyBox testBox = getSectorNode(i, boundingBox);
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
	 * Search an element in the tree
	 * 
	 * @param aPoint
	 * @param boundingBox
	 * @return
	 */
	protected T searchGID(int gid, MyBox boundingBox) {
		T anElement = null;

		// test root list
		ListIterator<T> iterList;
		for(int i=0;i<6 && anElement==null;i++)
		{
			iterList = theList[i].listIterator();
			while ((iterList.hasNext()) && (anElement == null)) {
				T searchedElement = iterList.next();
				if (searchedElement.gid==gid) {
					anElement = searchedElement;
				}
			}
		}
		
		// if not found try QuadTree areas
		if (anElement == null) {
			// test bounding box of the each subarea and search inside if it is
			// in the box
			int i = 0;
			while ((i < 4) && (anElement == null)) {
				if (theQuadTree[i] != null) {
					MyBox testBox = getSectorNode(i, boundingBox);
						// One point at least is inside => memorize it
						anElement = theQuadTree[i].searchGID(gid, testBox);
				}
				i++;
			}
		}

		return anElement;
	}
	
	
	/**
	 * Search in which element middle of anElement is.
	 * @param anElement
	 * @param boundingBox
	 * @return The element that contain anElement.
	 * @throws DelaunayError 
	 */
	protected <E extends MyElement> T searchInWhichElementItIs(E anElement, MyBox boundingBox) throws DelaunayError {
		T  foudElement = null;
		
		// test root list
		ListIterator<T> iterList = theList[0].listIterator();
		while ((iterList.hasNext()) && foudElement==null) {
			T searchedElement = iterList.next();
			
			if(anElement.getBoundingBox().minx>searchedElement.getBoundingBox().maxx)
				break;
			
			if(searchedElement.contains(anElement.getBoundingBox().getMiddle()))
				foudElement=searchedElement;
		}
		
		for(int i=1;i<6 && foudElement==null;i++)	//TODO optimize me
		{
			if(isIntersectSectorList(i, anElement.getBoundingBox(), boundingBox))
			{
				iterList = theList[i].listIterator();
				while ((iterList.hasNext()) && foudElement==null) {
					T searchedElement = iterList.next();
					
					if(anElement.getBoundingBox().minx>searchedElement.getBoundingBox().maxx)
						break;
					
					if(searchedElement.contains(anElement.getBoundingBox().getMiddle()))
						foudElement=searchedElement;
				}
			}
		}
		
		if(foudElement==null)
		{
			// test bounding box of the each subarea and search inside if it is in the box
			int i = 0;
			MyBox testBox;
			while ((i < 4)) {
				if (theQuadTree[i] != null) {
					testBox = getSectorNode(i, boundingBox);
					
					if(anElement.getBoundingBox().minx <= testBox.maxx && anElement.getBoundingBox().maxx >= testBox.minx
						&& anElement.getBoundingBox().miny <= testBox.maxy && anElement.getBoundingBox().maxy >= testBox.miny)
					{
						foudElement=theQuadTree[i].searchInWhichElementItIs(anElement,testBox);
					}
				}
				i++;
			}
		}
		return foudElement;
	}


	
	/**
	 * @param anEdge
	 * @param searchBoundingBox Bounding box of anEdge.
	 * @param boundingBox
	 * @return ArrayList of Object[].<br/>
	 * Object[0] is an MyEdge<br/>
	 * Object[1] is an Integer ( The result of anEdge.intersects(...) ).
	 */
	protected ArrayList<Object[]> searchIntersection(MyEdge anEdge, MyBox searchBoundingBox, MyBox boundingBox) {//TODO optimize me !!!
		ArrayList<Object[]>  allElements = new ArrayList<Object[]>();
		int intersect=0;

		ListIterator<MyEdge> iterList = (ListIterator<MyEdge>) theList[0].listIterator();
		while ((iterList.hasNext())) {
			MyEdge searchedElement =  iterList.next();

			if(searchBoundingBox.minx>searchedElement.getBoundingBox().maxx)
				break;

			if(searchBoundingBox.minx<=searchedElement.getBoundingBox().maxx &&
					searchBoundingBox.miny<=searchedElement.getBoundingBox().maxy &&
					searchBoundingBox.maxy>=searchedElement.getBoundingBox().miny)
			{	intersect = anEdge.intersects(searchedElement.getStartPoint(), searchedElement.getEndPoint());
				if(0< intersect)
					allElements.add(new Object[]{searchedElement, intersect});
			}
		}
		
		for(int i=1;i<6;i++)
		{
			if(isIntersectSectorList(i, searchBoundingBox, boundingBox))
			{
				iterList = (ListIterator<MyEdge>) theList[i].listIterator();
				while ((iterList.hasNext())) {
					MyEdge searchedElement =  iterList.next();
	
					if(searchBoundingBox.minx>searchedElement.getBoundingBox().maxx)
						break;
					
					if(searchBoundingBox.minx<=searchedElement.getBoundingBox().maxx &&
							searchBoundingBox.miny<=searchedElement.getBoundingBox().maxy &&
							searchBoundingBox.maxy>=searchedElement.getBoundingBox().miny)
					{	intersect = anEdge.intersects(searchedElement.getStartPoint(), searchedElement.getEndPoint());
						if(0< intersect)
							allElements.add(new Object[]{searchedElement, intersect});
					}
				}
			}
		}
		
		int i = 0;
		while ((i < 4)) {
			if (theQuadTree[i] != null) {
				MyBox testBox = getSectorNode(i, boundingBox);
				if (searchBoundingBox.maxx >= testBox.minx && searchBoundingBox.minx <= testBox.maxx
					&& searchBoundingBox.miny <= testBox.maxy && searchBoundingBox.maxy >= testBox.miny )
				{
					allElements.addAll(theQuadTree[i].searchIntersection(anEdge, searchBoundingBox, testBox));
				}
			}
			i++;
		}
		return allElements;
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
		
		
		ListIterator<T> iterList;
		iterList = theList[0].listIterator();
		while ((iterList.hasNext())) {
			T searchedElement = iterList.next();
			MyBox b=searchedElement.getBoundingBox();
			
			if(searchBoundingBox.minx>b.maxx)
				break;
			
			if(
					b.maxx>=searchBoundingBox.minx &&
					b.maxy>=searchBoundingBox.miny &&
					b.minx<=searchBoundingBox.maxx &&
					b.miny<=searchBoundingBox.maxy
				)
				allElements.add(searchedElement);
		}
		
		for(int i=1;i<6;i++)	//TODO optimize me
		{
			if(isIntersectSectorList(i, searchBoundingBox, boundingBox))
			{
				iterList = theList[i].listIterator();
				while ((iterList.hasNext())) {
					T searchedElement = iterList.next();
					MyBox b=searchedElement.getBoundingBox();
					
					if(searchBoundingBox.minx>b.maxx)
						break;
					
					if(
							b.maxx>=searchBoundingBox.minx &&
							b.maxy>=searchBoundingBox.miny &&
							b.minx<=searchBoundingBox.maxx &&
							b.miny<=searchBoundingBox.maxy
						)
						allElements.add(searchedElement);
				}
			}
		}
		
		
		// test bounding box of the each subarea and search inside if it is
		// in the
		// box
		int i = 0;
		while ((i < 4)) {
			if (theQuadTree[i] != null) {
				MyBox testBox = getSectorNode(i, boundingBox);
				if (searchBoundingBox.maxx >= testBox.minx && searchBoundingBox.minx <= testBox.maxx
					&& searchBoundingBox.miny <= testBox.maxy && searchBoundingBox.maxy >= testBox.miny )
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
		ListIterator<T> iterList ;
		
		iterList = theList[0].listIterator();
		
		while ((iterList.hasNext())) {
			T searchedElement = iterList.next();

			MyBox b=searchedElement.getBoundingBox();
			
			if(searchBoundingBox.minx>b.maxx)
				break;
			

				if(		b.minx> searchBoundingBox.minx &&
						b.maxx< searchBoundingBox.maxx &&
						b.miny> searchBoundingBox.miny &&
						b.maxy< searchBoundingBox.maxy
				)
				{	
					allElements.add(searchedElement);
				}

		}
		
		for(int i=1;i<6;i++)	//TODO optimize me
		{
			if(isIntersectSectorList(i, searchBoundingBox, boundingBox))
			{
				iterList = theList[i].listIterator();
				
				while ((iterList.hasNext())) {
					T searchedElement = iterList.next();
		
					MyBox b=searchedElement.getBoundingBox();
					
					if(searchBoundingBox.minx>b.maxx)
						break;
					
		
						if(		b.minx> searchBoundingBox.minx &&
								b.maxx< searchBoundingBox.maxx &&
								b.miny> searchBoundingBox.miny &&
								b.maxy< searchBoundingBox.maxy
						)
						{	
							allElements.add(searchedElement);
						}
				}
			}
		}
		
		
		// test bounding box of the each subarea and search inside if it is
		// in the
		// box
		int i = 0;
		while ((i < 4)) {
			if (theQuadTree[i] != null) {
				MyBox testBox = getSectorNode(i, boundingBox);
				
//				if (!((searchBoundingBox.maxx < testBox.minx) || (searchBoundingBox.minx > testBox.maxx)
//				|| (searchBoundingBox.miny > testBox.maxy) || (searchBoundingBox.maxy < testBox.miny)) )
				if (searchBoundingBox.maxx >= testBox.minx && searchBoundingBox.minx <= testBox.maxx
					&& searchBoundingBox.miny <= testBox.maxy && searchBoundingBox.maxy >= testBox.miny )
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
	 * @return Number of elements removed.
	 */
	protected int removeAllStric(MyPolygon aPolygon, MyBox boundingBox) {
		
		return removeAllStric(aPolygon, aPolygon.getBoundingBox(),boundingBox);
	}
	
	/**
	 * Remove all elements strictly inside the polygon's area.
	 * 
	 * @param aPolygon Area of search.
	 * @param boundingBox Bounding box of quad tree.
	 * @return Number of elements removed.
	 */
	private int removeAllStric(MyPolygon aPolygon, MyBox searchBoundingBox, MyBox boundingBox) {

		int quadtreeSize=0;
		// test root list
		ListIterator<T> iterList;
		
		iterList = theList[0].listIterator();		
		while ((iterList.hasNext())) {
			T searchedElement = iterList.next();
			
			if(searchBoundingBox.minx>searchedElement.getBoundingBox().maxx)
				break;
			
			if(!searchedElement.isUseByPolygon() && aPolygon.contains(searchedElement.getBoundingBox().getMiddle()))
			{	
				iterList.remove();
				quadtreeSize++;
			}

		}
		for(int i=1;i<6;i++)
		{
			if(isIntersectSectorList(i, searchBoundingBox, boundingBox))
			{
				iterList = theList[i].listIterator();
				while ((iterList.hasNext())) {
					
					T searchedElement = iterList.next();
					if(searchBoundingBox.minx>searchedElement.getBoundingBox().maxx)
						break;
					

					if(!searchedElement.isUseByPolygon() && aPolygon.contains(searchedElement.getBoundingBox().getMiddle()))
					{	
						iterList.remove();
						quadtreeSize++;
					}
				}
			}
		}

		// test bounding box of the each subarea and search inside if it is in the box
		int i = 0;
		MyBox testBox;
		while ((i < 4)) {
			if (theQuadTree[i] != null) {
				testBox = getSectorNode(i, boundingBox);
				
				if(searchBoundingBox.minx <= testBox.maxx && searchBoundingBox.maxx >= testBox.minx
					&& searchBoundingBox.miny <= testBox.maxy && searchBoundingBox.maxy >= testBox.miny)
				{
					quadtreeSize+=theQuadTree[i].removeAllStric(aPolygon, searchBoundingBox,testBox);
					// Remove empty node
					if(theQuadTree[i].theList[0].size()<=0
							&& theQuadTree[i].theList[1].size()<=0
							&& theQuadTree[i].theList[2].size()<=0
							&& theQuadTree[i].theList[3].size()<=0
							&& theQuadTree[i].theList[4].size()<=0
							&& theQuadTree[i].theList[5].size()<=0
							&& theQuadTree[i].theQuadTree[0]==null
							&& theQuadTree[i].theQuadTree[1]==null
							&& theQuadTree[i].theQuadTree[2]==null
							&& theQuadTree[i].theQuadTree[3]==null
							)//FIXME not very good
						theQuadTree[i]=null;
				}


			}
			i++;
		}
		return quadtreeSize;
	}
	
	
	
	/**
	 * Remove element.
	 * @param element
	 * @return True if it done.
	 */
	protected int remove(T element, MyBox boundingBox) {
		int removeDone=0;
		// test root list

		
		if(element.getClass().getName().equals("org.jdelaunay.delaunay.MyEdge"))
		{
			ListIterator<T> iterList = theList[getSectorList(element.getBoundingBox(), boundingBox)].listIterator();
				while ((iterList.hasNext())) {
					T searchedElement = iterList.next();
					
					if(element.getBoundingBox().minx>searchedElement.getBoundingBox().maxx)
						break;
					
					if(searchedElement==element)
					{
						iterList.remove();
						removeDone++;
					}
					
				}	
	
	}
		else
		{
			ListIterator<T> iterList;
			for(int i=0;i<6;i++)	//TODO optimize me
			{
				iterList = theList[i].listIterator();
				while ((iterList.hasNext())) {
					T searchedElement = iterList.next();
				
					if(searchedElement==element)
					{
						iterList.remove();
						removeDone++;
						break;
					}
					
				}		
			}
	}
			
		// test bounding box of the each subarea and search inside if it is in the box
		int i = 0;
		MyBox testBox;
		while ((i < 4)) {
			if (theQuadTree[i] != null) {
				testBox = getSectorNode(i, boundingBox);
				if(element.getBoundingBox().maxx <= testBox.maxx && element.getBoundingBox().minx >= testBox.minx
						&& element.getBoundingBox().maxy <= testBox.maxy && element.getBoundingBox().miny >= testBox.miny)
				{
					removeDone+=theQuadTree[i].remove(element,testBox);
					if(theQuadTree[i].theList[0].size()<=0
							&& theQuadTree[i].theList[1].size()<=0
							&& theQuadTree[i].theList[2].size()<=0
							&& theQuadTree[i].theList[3].size()<=0
							&& theQuadTree[i].theList[4].size()<=0
							&& theQuadTree[i].theList[5].size()<=0
							&& theQuadTree[i].theQuadTree[0]==null
							&& theQuadTree[i].theQuadTree[1]==null
							&& theQuadTree[i].theQuadTree[2]==null
							&& theQuadTree[i].theQuadTree[3]==null
							)//FIXME not very good
						theQuadTree[i]=null;
				}
			}
			i++;
		}
		
		return removeDone;
	}
	
	
	
	/**
	 * Remove elements from the lists
	 */
	protected void removeData() {
		
		for(int i=0;i<6;i++)	//TODO optimize me
		{
			while (! theList[i].isEmpty()) {
				theList[i].removeFirst();
			}
		}
		
		// if not found try QuadTree areas
		for (int i=0; i<4; i++) {
			if (theQuadTree[i] != null) {
				theQuadTree[i].removeData();
			}
		}
	}
	
	
	
	/**
	 * @return All elements in the quadTree.
	 */
	protected ArrayList<T> getAll() {
		ArrayList<T>  allElements = new ArrayList<T>();
		
		// test root list
		ListIterator<T> iterList;
		for(int i=0;i<6;i++)	//TODO optimize me
		{
			iterList = theList[i].listIterator();
			while ((iterList.hasNext())) {
				T searchedElement = iterList.next();
				allElements.add(searchedElement);
			}
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