package org.jdelaunay.delaunay;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

public class MyTools {

	protected static final double EPSILON = 0.0000001;
	protected static final double EPSILON2 = EPSILON * EPSILON;

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
	 * Quick sort a MyElement array ordered according to the GID
	 * 
	 * @param points
	 * @param minIndex
	 * @param maxIndex
	 */
	protected static void quickSortGID(ArrayList<? extends MyElement> elements,
			int minIndex, int maxIndex) {
		int i, j;
		int enreg_ref;
		double cle_ref;
		boolean found;
		MyElement anElement;
		int valGid = -1;

		i = minIndex;
		j = maxIndex;
		enreg_ref = (maxIndex + minIndex) / 2;
		anElement = (MyElement)elements.get(enreg_ref);
		cle_ref = anElement.getGid();
		do {
			// first : increasing index
			found = false;
			while (!found) {
				if (i >= maxIndex) {
					found = true;
				}
				else {
					anElement = (MyElement)elements.get(i);
					valGid = anElement.getGid();
					if (valGid > cle_ref) {
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
					anElement = (MyElement)elements.get(j);
					valGid = anElement.getGid();
					if (valGid <= cle_ref) {
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
	public static void quickSort_Points(ArrayList<MyPoint> points,
			int minIndex, int maxIndex) {
		int i, j;
		int enreg_ref;
		double cle_ref1, cle_ref2;
		boolean found;
		MyPoint aPoint;

		i = minIndex;
		j = maxIndex;
		enreg_ref = (maxIndex + minIndex) / 2;
		aPoint = points.get(enreg_ref);
		cle_ref1 = aPoint.getX();
		cle_ref2 = aPoint.getY();
		do {
			// first : increasing index
			found = false;
			while (!found) {
				if (i >= maxIndex) {
					found = true;
				}
				else {
					aPoint = points.get(i);
					if (aPoint.getX() > cle_ref1) {
						found = true;
					}
					else if ((aPoint.getX() == cle_ref1)
							&& (aPoint.getY() >= cle_ref2)) {
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
					if (aPoint.getX() < cle_ref1) {
						found = true;
					}
					else if ((aPoint.getX() == cle_ref1)
							&& (aPoint.getY() <= cle_ref2)) {
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
			quickSort_Points(points, minIndex, j);
		}
		if (maxIndex > i) {
			// if right side is not empty
			quickSort_Points(points, i, maxIndex);
		}
	}

	/**
	 * Quick sort on points Ordered according to x and y
	 * 
	 * @param points
	 */
	protected static void quickSort_Points(ArrayList<MyPoint> points) {
		MyTools.quickSort_Points(points, 0, points.size() - 1);
	}

	/**
	 * Quick sort on points Ordered according to minimum X, Y of both
	 * extremities
	 *
	 * @param minIndex
	 * @param maxIndex
	 */
	protected static void quickSort_Edges(ArrayList<MyEdge> edges, int minIndex,
			int maxIndex, boolean switchPoints) {
		int i, j;
		int enreg_ref;
		double cle_ref1, cle_ref2, cle_ref3, cle_ref4;
		double cle1, cle2, cle3, cle4;
		double x;
		boolean found;

		i = minIndex;
		j = maxIndex;
		enreg_ref = (maxIndex + minIndex) / 2;
		MyEdge anEdge = edges.get(enreg_ref);
		cle_ref1 = anEdge.getStartPoint().getX();
		cle_ref2 = anEdge.getStartPoint().getY();
		cle_ref3 = anEdge.getEndPoint().getX();
		cle_ref4 = anEdge.getEndPoint().getY();
		if (cle_ref3 < cle_ref1) {
			x = cle_ref3;
			cle_ref3 = cle_ref1;
			cle_ref1 = x;

			x = cle_ref4;
			cle_ref4 = cle_ref2;
			cle_ref2 = x;
		} else if ((cle_ref3 == cle_ref1) && (cle_ref4 < cle_ref2)) {
			x = cle_ref4;
			cle_ref4 = cle_ref2;
			cle_ref2 = x;
		}
		if (switchPoints) {
			x = cle_ref3;
			cle_ref3 = cle_ref1;
			cle_ref1 = x;

			x = cle_ref4;
			cle_ref4 = cle_ref2;
			cle_ref2 = x;
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

					if (cle1 > cle_ref1) {
						found = true;
					}
					else if (cle1 < cle_ref1) {
						i++;
					}
					else if (cle2 > cle_ref2) {
						found = true;
					}
					else if (cle2 < cle_ref2) {
						i++;
					}
					else if (cle3 > cle_ref3) {
						found = true;
					}
					else if (cle3 < cle_ref3) {
						i++;
					}
					else if (cle4 > cle_ref4) {
						found = true;
					}
					else if (cle4 < cle_ref4) {
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

					if (cle1 < cle_ref1) {
						found = true;
					}
					else if (cle1 > cle_ref1) {
						j--;
					}
					else if (cle2 < cle_ref2) {
						found = true;
					}
					else if (cle2 > cle_ref2) {
						j--;
					}
					else if (cle3 < cle_ref3) {
						found = true;
					}
					else if (cle3 > cle_ref3) {
						j--;
					}
					else if (cle4 < cle_ref4) {
						found = true;
					}
					else if (cle4 > cle_ref4) {
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
			quickSort_Edges(edges, minIndex, j, switchPoints);
		}
		if (maxIndex > i) {
			// if right side is not empty
			quickSort_Edges(edges, i, maxIndex, switchPoints);
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
	public static double interpolateZ(MyPoint p1, MyPoint p2, MyPoint aPoint) {
		double D = p1.squareDistance(p2);
		double Z = p2.getZ() - p1.getZ();

		double d = aPoint.squareDistance(p1);
		double factor = d / D;
		return p1.getZ() + (factor * Z);

	}

	/**
	 * Check if the edge already exists returns null if it doesn't
	 * 
	 * @param p1
	 * @param p2
	 * @param EdgeList
	 * @return theEdge
	 */
	protected static MyEdge checkTwoPointsEdge(MyPoint p1, MyPoint p2,
			LinkedList<MyEdge> EdgeList) {
		// Check if the two points already lead to an existing edge.
		// If the edge exists it must be in the non-processed edges
		MyEdge theEdge = null;
		MyPoint test1, test2;
		ListIterator<MyEdge> iter1 = EdgeList.listIterator();
		while (iter1.hasNext() && (theEdge == null)) {
			MyEdge anEdge = iter1.next();
			test1 = anEdge.getStartPoint();
			test2 = anEdge.getEndPoint();
			if (((test1 == p1) && (test2 == p2))
					|| ((test1 == p2) && (test2 == p1))) {
				theEdge = anEdge;
			}
		}
		return theEdge;
	}

}
