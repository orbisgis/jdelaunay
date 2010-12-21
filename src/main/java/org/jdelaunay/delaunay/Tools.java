package org.jdelaunay.delaunay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public final class Tools {

	public static final double EPSILON = 0.0000001;
	public static final double EPSILON2 = EPSILON * EPSILON;

	/**
	 * Default constructor is private : it is not supposed to be used !
	 */
	private Tools(){
		
	}

	/**
	 * swap two elements
	 * 
	 * @param elements
	 * @param index1
	 * @param index2
	 */
	private static void swapElements(ArrayList elements, int index1,
			int index2) {
		Object anElement;
		anElement = elements.get(index1);
		elements.set(index1, elements.get(index2));
		elements.set(index2, anElement);
	}

	/**
	 * Quick sort a Element array ordered according to the GID
	 * 
	 * @param points
	 * @param minIndex
	 * @param maxIndex
	 */
	protected static void quickSortGID(ArrayList<? extends Element> elements,
			int minIndex, int maxIndex) {
		int i, j;
		int enregRef;
		double cleRef;
		boolean found;
		Element anElement;
		int valGid = -1;

		i = minIndex;
		j = maxIndex;
		enregRef = (maxIndex + minIndex) / 2;
		anElement = (Element)elements.get(enregRef);
		cleRef = anElement.getGid();
		do {
			// first : increasing index
			found = false;
			while (!found) {
				if (i >= maxIndex) {
					found = true;
				}
				else {
					anElement = (Element)elements.get(i);
					valGid = anElement.getGid();
					if (valGid > cleRef) {
						found = true;
					}
					else {
						i++;
					}
				}
			}
			// second : decreasing index
			found = false;
			while (!found) {
				if (minIndex >= j) {
					found = true;
				}
				else {
					anElement = (Element)elements.get(j);
					valGid = anElement.getGid();
					if (valGid <= cleRef) {
						found = true;
					}
					else {
						j--;
					}
				}
			}
			// exchange values
			if (i <= j) {
				// we can change values
				swapElements(elements, i, j);

				i++;
				j--;
			}
		} while (i <= j);

		// Recurrent calls
		if (minIndex < j) {
			// if left side is not empty
			quickSortGID(elements, minIndex, j);
		}
		if (maxIndex > i) {
			// if right side is not empty
			quickSortGID(elements, i, maxIndex);
		}
	}


	/**
	 * Quick sort on points Ordered according to x and y
	 * 
	 * @param points
	 * @param minIndex
	 * @param maxIndex
	 */
	public static void quickSortPoints(ArrayList<Point> points,
			int minIndex, int maxIndex) {
		int i, j;
		int enregRef;
		double cleRef1, cleRef2;
		boolean found;
		Point aPoint;

		i = minIndex;
		j = maxIndex;
		enregRef = (maxIndex + minIndex) / 2;
		aPoint = points.get(enregRef);
		cleRef1 = aPoint.getX();
		cleRef2 = aPoint.getY();
		do {
			// first : increasing index
			found = false;
			while (!found) {
				if (i >= maxIndex) {
					found = true;
				}
				else {
					aPoint = points.get(i);
					if (aPoint.getX() > cleRef1) {
						found = true;
					}
					else if ((aPoint.getX() == cleRef1)
							&& (aPoint.getY() >= cleRef2)) {
						found = true;
					}
					else {
						i++;
					}
				}
			}
			// second : decreasing index
			found = false;
			while (!found) {
				if (minIndex >= j) {
					found = true;
				}
				else {
					aPoint = points.get(j);
					if (aPoint.getX() < cleRef1) {
						found = true;
					}
					else if ((aPoint.getX() == cleRef1)
							&& (aPoint.getY() <= cleRef2)) {
						found = true;
					}
					else {
						j--;
					}
				}
			}
			// exchange values
			if (i <= j) {
				// we can change values
				swapElements(points, i, j);

				i++;
				j--;
			}
		} while (i <= j);

		// Recurrent calls
		if (minIndex < j) {
			// if left side is not empty
			quickSortPoints(points, minIndex, j);
		}
		if (maxIndex > i) {
			// if right side is not empty
			quickSortPoints(points, i, maxIndex);
		}
	}

	/**
	 * Quick sort on points Ordered according to x and y
	 * 
	 * @param points
	 */
	protected static void quickSortPoints(ArrayList<Point> points) {
		Tools.quickSortPoints(points, 0, points.size() - 1);
	}

	/**
	 * Quick sort on points Ordered according to minimum X, Y of both
	 * extremities
	 *
	 * @param minIndex
	 * @param maxIndex
	 */
	protected static void quickSortEdges(ArrayList<Edge> edges, int minIndex,
			int maxIndex, boolean switchPoints) {
		int i, j;
		int enregRef;
		double cleRef1, cleRef2, cleRef3, cleRef4;
		double cle1, cle2, cle3, cle4;
		double x;
		boolean found;

		i = minIndex;
		j = maxIndex;
		enregRef = (maxIndex + minIndex) / 2;
		Edge anEdge = edges.get(enregRef);
		cleRef1 = anEdge.getStartPoint().getX();
		cleRef2 = anEdge.getStartPoint().getY();
		cleRef3 = anEdge.getEndPoint().getX();
		cleRef4 = anEdge.getEndPoint().getY();
		if (cleRef3 < cleRef1) {
			x = cleRef3;
			cleRef3 = cleRef1;
			cleRef1 = x;

			x = cleRef4;
			cleRef4 = cleRef2;
			cleRef2 = x;
		} else if ((cleRef3 == cleRef1) && (cleRef4 < cleRef2)) {
			x = cleRef4;
			cleRef4 = cleRef2;
			cleRef2 = x;
		}
		if (switchPoints) {
			x = cleRef3;
			cleRef3 = cleRef1;
			cleRef1 = x;

			x = cleRef4;
			cleRef4 = cleRef2;
			cleRef2 = x;
		}
		do {
			// first : increasing index
			found = false;
			while (!found) {
				if (i >= maxIndex) {
					found = true;
				}
				else {
					anEdge = edges.get(i);
					cle1 = anEdge.getStartPoint().getX();
					cle2 = anEdge.getStartPoint().getY();
					cle3 = anEdge.getEndPoint().getX();
					cle4 = anEdge.getEndPoint().getY();
					if (cle3 < cle1) {
						x = cle3;
						cle3 = cle1;
						cle1 = x;

						x = cle4;
						cle4 = cle2;
						cle2 = x;
					} else if ((cle3 == cle1) && (cle4 < cle2)) {
						x = cle4;
						cle4 = cle2;
						cle2 = x;
					}
					if (switchPoints) {
						x = cle3;
						cle3 = cle1;
						cle1 = x;

						x = cle4;
						cle4 = cle2;
						cle2 = x;
					}

					if (cle1 > cleRef1) {
						found = true;
					}
					else if (cle1 < cleRef1) {
						i++;
					}
					else if (cle2 > cleRef2) {
						found = true;
					}
					else if (cle2 < cleRef2) {
						i++;
					}
					else if (cle3 > cleRef3) {
						found = true;
					}
					else if (cle3 < cleRef3) {
						i++;
					}
					else if (cle4 > cleRef4) {
						found = true;
					}
					else if (cle4 < cleRef4) {
						i++;
					}
					else {
						found = true;
					}
				}
			}

			// second : decreasing index
			found = false;
			while (!found) {
				if (minIndex >= j) {
					found = true;
				}
				else {
					anEdge = edges.get(j);
					cle1 = anEdge.getStartPoint().getX();
					cle2 = anEdge.getStartPoint().getY();
					cle3 = anEdge.getEndPoint().getX();
					cle4 = anEdge.getEndPoint().getY();
					if (cle3 < cle1) {
						x = cle3;
						cle3 = cle1;
						cle1 = x;

						x = cle4;
						cle4 = cle2;
						cle2 = x;
					} else if ((cle3 == cle1) && (cle4 < cle2)) {
						x = cle4;
						cle4 = cle2;
						cle2 = x;
					}
					if (switchPoints) {
						x = cle3;
						cle3 = cle1;
						cle1 = x;

						x = cle4;
						cle4 = cle2;
						cle2 = x;
					}

					if (cle1 < cleRef1) {
						found = true;
					}
					else if (cle1 > cleRef1) {
						j--;
					}
					else if (cle2 < cleRef2) {
						found = true;
					}
					else if (cle2 > cleRef2) {
						j--;
					}
					else if (cle3 < cleRef3) {
						found = true;
					}
					else if (cle3 > cleRef3) {
						j--;
					}
					else if (cle4 < cleRef4) {
						found = true;
					}
					else if (cle4 > cleRef4) {
						j--;
					}
					else {
						found = true;
					}
				}
			}

			// exchange values
			if (i <= j) {
				// we can change values
				swapElements(edges, i, j);

				i++;
				j--;
			}
		} while (i <= j);

		// Recurrent calls
		if (minIndex < j) {
			// if left side is not empty
			quickSortEdges(edges, minIndex, j, switchPoints);
		}
		if (maxIndex > i) {
			// if right side is not empty
			quickSortEdges(edges, i, maxIndex, switchPoints);
		}
	}

	/**
	 * Linear Z interpolation
	 * 
	 * @param p1
	 * @param p2
	 * @param aPoint
	 * @return
	 */
	public static double interpolateZ(Point p1, Point p2, Point aPoint) {
		double dist = p1.squareDistance(p2);
		double z = p2.getZ() - p1.getZ();

		double d = aPoint.squareDistance(p1);
		double factor = d / dist;
		return p1.getZ() + (factor * z);

	}

	/**
	 * Check if the edge already exists returns null if it doesn't
	 * 
	 * @param p1
	 * @param p2
	 * @param edgeList
	 * @return theEdge
	 */
	protected static Edge checkTwoPointsEdge(Point p1, Point p2,
			LinkedList<Edge> edgeList) {
		// Check if the two points already lead to an existing edge.
		// If the edge exists it must be in the non-processed edges
		Edge theEdge = null;
		Point test1, test2;
		ListIterator<Edge> iter1 = edgeList.listIterator();
		while (iter1.hasNext() && (theEdge == null)) {
			Edge anEdge = iter1.next();
			test1 = anEdge.getStartPoint();
			test2 = anEdge.getEndPoint();
			if (((test1.equals(p1)) && (test2.equals(p2)))
					|| ((test1.equals(p2)) && (test2.equals(p1)))) {
				theEdge = anEdge;
			}
		}
		return theEdge;
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


}
