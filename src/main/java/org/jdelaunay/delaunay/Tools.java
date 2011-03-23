package org.jdelaunay.delaunay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A set of utility methods
 *
 * @author Jean-Yves MARTIN, Alexis GUEGANNO
 * @date 2009-01-12
 * @revision 2011-11-01
 * @version 2.1
 */

public final class Tools {

	public static final double EPSILON = 0.0000001;
	public static final double EPSILON2 = EPSILON * EPSILON;
	public static final int BIT_OUTSIDE = 1;
	public static final int BIT_LOCKED = 2;
	public static final int BIT_LEVEL = 3;
	public static final int BIT_POLYGON = 4;
	public static final int BIT_ZUSED = 5;
	public static final int BIT_MARKED = 6;
        


	/**
	 * Default constructor is private : it is not supposed to be used !
	 */
	private Tools(){
		
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
		cleRef = anElement.getGID();
		do {
			// first : increasing index
			found = false;
			while (!found) {
				if (i >= maxIndex) {
					found = true;
				}
				else {
					anElement = (Element)elements.get(i);
					valGid = anElement.getGID();
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
					valGid = anElement.getGID();
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
				Collections.swap(elements, i, j);

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
	public static void quickSortPoints(ArrayList<DPoint> points,
			int minIndex, int maxIndex) {
		int i, j;
		int enregRef;
		double cleRef1, cleRef2;
		boolean found;
		DPoint aPoint;

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
					else if ((Math.abs(aPoint.getX() - cleRef1)<EPSILON)
							&& (aPoint.getY() - cleRef2 >= -EPSILON )) {
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
					else if ((Math.abs(aPoint.getX() - cleRef1)<EPSILON)
							&& (aPoint.getY() - cleRef2 <= EPSILON )) {
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
				Collections.swap(points, i, j);

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
	protected static void quickSortPoints(ArrayList<DPoint> points) {
		Tools.quickSortPoints(points, 0, points.size() - 1);
	}

	/**
	 * Quick sort on points Ordered according to minimum X, Y of both
	 * extremities
	 *
	 * @param minIndex
	 * @param maxIndex
	 */
	protected static void quickSortEdges(ArrayList<DEdge> edges, int minIndex,
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
		DEdge anEdge = edges.get(enregRef);
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
		} else if ((Math.abs(cleRef3-cleRef1)<EPSILON) && (cleRef4 < cleRef2)) {
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
					} else if ((Math.abs(cle3 - cle1)<EPSILON) && (cle4 < cle2)) {
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
					} else if ((Math.abs(cle3-cle1)<EPSILON) && (cle4 < cle2)) {
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
				Collections.swap(edges, i, j);

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
	public static double interpolateZ(DPoint p1, DPoint p2, DPoint aPoint) {
		double dist = p1.squareDistance(p2);
		double z = p2.getZ() - p1.getZ();

		double d = aPoint.squareDistance(p1);
		if (Math.abs(dist) > Tools.EPSILON2) {
			double factor = d / dist;
			return p1.getZ() + (factor * z);
		}
		else {
			return (p2.getZ() + p1.getZ()) / 2.0;
		}
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

        /**
         * Compute the vector product from the vectors v1 and v2 represented with DPoint
         * @param v1
         * @param v2
         * @return
         * @throws DelaunayError
         */
	public static DPoint vectorProduct(DPoint v1, DPoint v2) throws DelaunayError {
		DPoint v3 = new DPoint(0, 0, 0);
		v3.setX( v1.getY() * v2.getZ() - v1.getZ() * v2.getY());
		v3.setY( v1.getZ() * v2.getX() - v1.getX() * v2.getZ());
		v3.setZ( v1.getX() * v2.getY() - v1.getY() * v2.getX());
		return v3;
	}

        /**
	 * Checks that the vectors resulting from v1 and v2 are colinear.
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static boolean isColinear(DPoint v1, DPoint v2) {
		double res = 0;
		res += Math.abs(v1.getY() * v2.getZ() - v1.getZ() * v2.getY());
		res += Math.abs(v1.getZ() * v2.getX() - v1.getX() * v2.getZ());
		res += Math.abs(v1.getX() * v2.getY() - v1.getY() * v2.getX());
		return res < EPSILON;
	}

        /**
	 * Compute the difference between two vectors.
	 * @param v1
	 * @param v2
	 * @return v1 - v2
         * @throws 
	 */
	public static DPoint vectorialDiff(DPoint v1, DPoint v2) throws DelaunayError {
		DPoint v3 = new DPoint(0, 0, 0);
		v3.setX( v1.getX() - v2.getX());
		v3.setY(v1.getY() - v2.getY());
		v3.setZ(v1.getZ() - v2.getZ());
		return v3;
	}


}
