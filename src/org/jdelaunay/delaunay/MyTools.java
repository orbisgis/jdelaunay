package org.jdelaunay.delaunay;

import java.util.ArrayList;

public class MyTools {

	/**
	 * Quick sort on points Ordered according to x and y
	 * 
	 * @param points
	 * @param min_index
	 * @param max_index
	 */
	public static void quickSort_Points(ArrayList<MyPoint> points, int min_index,
			int max_index) {
		int i, j;
		int enreg_ref;
		double cle_ref1, cle_ref2;
		boolean found;
		MyPoint aPoint;

		i = min_index;
		j = max_index;
		enreg_ref = (max_index + min_index) / 2;
		aPoint = points.get(enreg_ref);
		cle_ref1 = aPoint.getX();
		cle_ref2 = aPoint.getY();
		do {
			// first : increasing index
			found = false;
			while (!found) {
				if (i > max_index)
					found = true;
				else {
					aPoint = points.get(i);
					if (aPoint.getX() > cle_ref1)
						found = true;
					else if ((aPoint.getX() == cle_ref1)
							&& (aPoint.getY() >= cle_ref2))
						found = true;
					else
						i++;
				}
			}
			// second : decreasing index
			found = false;
			while (!found) {
				if (min_index > j)
					found = true;
				else {
					aPoint = points.get(j);
					if (aPoint.getX() < cle_ref1)
						found = true;
					else if ((aPoint.getX() == cle_ref1)
							&& (aPoint.getY() <= cle_ref2))
						found = true;
					else
						j--;
				}
			}
			// exchange values
			if (i <= j) {
				// we can change values
				aPoint = points.get(i);
				points.set(i, points.get(j));
				points.set(j, aPoint);

				i++;
				j--;
			}
		} while (i <= j);

		// Recurrent calls
		if (min_index < j) {
			// if left side is not empty
			quickSort_Points(points, min_index, j);
		}
		if (max_index > i) {
			// if right side is not empty
			quickSort_Points(points, i, max_index);
		}
	}

	/**
	 * Quick sort on points Ordered according to x and y
	 * 
	 * @param points
	 */
	public static void quickSort_Points(ArrayList<MyPoint> points) {
		MyTools.quickSort_Points(points, 0, points.size()-1);
	}
	
	/**
	 * Quick sort on points Ordered according to the GID
	 * 
	 * @param points
	 * @param min_index
	 * @param max_index
	 */
	protected static void quickSortGID_Points(ArrayList<MyPoint> points, int min_index,
			int max_index) {
		int i, j;
		int enreg_ref;
		int cle_ref;
		boolean found;
		MyPoint aPoint;

		i = min_index;
		j = max_index;
		enreg_ref = (max_index + min_index) / 2;
		aPoint = points.get(enreg_ref);
		cle_ref = aPoint.getGid();
		do {
			// first : increasing index
			found = false;
			while (!found) {
				if (i > max_index)
					found = true;
				else {
					aPoint = points.get(i);
					if (aPoint.getGid() >= cle_ref)
						found = true;
					else
						i++;
				}
			}
			// second : decreasing index
			found = false;
			while (!found) {
				if (min_index > j)
					found = true;
				else {
					aPoint = points.get(j);
					if (aPoint.getGid() <= cle_ref)
						found = true;
					else
						j--;
				}
			}
			// exchange values
			if (i <= j) {
				// we can change values
				aPoint = points.get(i);
				points.set(i, points.get(j));
				points.set(j, aPoint);

				i++;
				j--;
			}
		} while (i <= j);

		// Recurrent calls
		if (min_index < j)  {
			// if left side is not empty
			quickSortGID_Points(points, min_index, j);
		}
		if (max_index > i) {
			// if right side is not empty
			quickSortGID_Points(points, i, max_index);
		}
	}

	/**
	 * Quick sort on edges Ordered according to the GID
	 * 
	 * @param points
	 * @param min_index
	 * @param max_index
	 */
	protected static void quickSortGID_Edges(ArrayList<MyEdge> edges, int min_index,
			int max_index) {
		int i, j;
		int enreg_ref;
		int cle_ref;
		boolean found;
		MyEdge anEdge;

		i = min_index;
		j = max_index;
		enreg_ref = (max_index + min_index) / 2;
		anEdge = edges.get(enreg_ref);
		cle_ref = anEdge.getGid();
		do {
			// first : increasing index
			found = false;
			while (!found) {
				if (i > max_index)
					found = true;
				else {
					anEdge = edges.get(i);
					if (anEdge.getGid() >= cle_ref)
						found = true;
					else
						i++;
				}
			}
			// second : decreasing index
			found = false;
			while (!found) {
				if (min_index > j)
					found = true;
				else {
					anEdge = edges.get(j);
					if (anEdge.getGid() <= cle_ref)
						found = true;
					else
						j--;
				}
			}
			// exchange values
			if (i <= j) {
				// we can change values
				anEdge = edges.get(i);
				edges.set(i, edges.get(j));
				edges.set(j, anEdge);

				i++;
				j--;
			}
		} while (i <= j);

		// Recurrent calls
		if ((min_index < j) && (j < max_index)) {
			// if left side is not empty
			quickSortGID_Edges(edges, min_index, j);
		}
		if ((max_index > i) && (i > min_index)) {
			// if right side is not empty
			quickSortGID_Edges(edges, i, max_index);
		}
	}

	/**
	 * Quick sort on triangles Ordered according to the GID
	 * 
	 * @param points
	 * @param min_index
	 * @param max_index
	 */
	protected static void quickSortGID_Triangles(ArrayList<MyPoint> triangles, int min_index,
			int max_index) {
		int i, j;
		int enreg_ref;
		int cle_ref;
		boolean found;
		MyPoint aPoint;

		i = min_index;
		j = max_index;
		enreg_ref = (max_index + min_index) / 2;
		aPoint = triangles.get(enreg_ref);
		cle_ref = aPoint.getGid();
		do {
			// first : increasing index
			found = false;
			while (!found) {
				if (i > max_index)
					found = true;
				else {
					aPoint = triangles.get(i);
					if (aPoint.getGid() > cle_ref)
						found = true;
					else
						i++;
				}
			}
			// second : decreasing index
			found = false;
			while (!found) {
				if (min_index > j)
					found = true;
				else {
					aPoint = triangles.get(j);
					if (aPoint.getGid() < cle_ref)
						found = true;
					else
						j--;
				}
			}
			// exchange values
			if (i <= j) {
				// we can change values
				aPoint = triangles.get(i);
				triangles.set(i, triangles.get(j));
				triangles.set(j, aPoint);

				i++;
				j--;
			}
		} while (i <= j);

		// Recurrent calls
		if ((min_index < j) && (j < max_index)) {
			// if left side is not empty
			quickSortGID_Triangles(triangles, min_index, j);
		}
		if ((max_index > i) && (i > min_index)) {
			// if right side is not empty
			quickSortGID_Triangles(triangles, i, max_index);
		}
	}
}
