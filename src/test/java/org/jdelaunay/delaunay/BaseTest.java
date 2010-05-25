package org.jdelaunay.test;

import java.util.ArrayList;
import java.util.ListIterator;

import org.jdelaunay.delaunay.*;

import junit.framework.TestCase;

public class BaseTest extends TestCase {

	// ---------------------------------------------------------------------------
	// Utilities
	/**
	 * Generate an array of points
	 * @return
	 */
	public static ArrayList<MyPoint> getPoints() {
		ArrayList<MyPoint> points = new ArrayList<MyPoint>();
		points.add(new MyPoint(12, 10, 2));
		points.add(new MyPoint(120, 10, 20));
		points.add(new MyPoint(12, 100, 12));
		points.add(new MyPoint(102, 100, 1));
		points.add(new MyPoint(52, 100, 1));
		points.add(new MyPoint(10, 50, 5));
		points.add(new MyPoint(50, 50, 1));
		points.add(new MyPoint(150, 50, 11));
		points.add(new MyPoint(50, 150, 2));
		points.add(new MyPoint(5, 50, 3));
		points.add(new MyPoint(5, 5, 10));

		return points;
	}

	/**
	 * Generate an array of edges
	 * @return
	 */
	public static ArrayList<MyEdge> getBreaklines() {
		ArrayList<MyEdge> edges = new ArrayList<MyEdge>();
		edges.add(new MyEdge(new MyPoint(12, 10, 2), new MyPoint(102, 100, 1)));
		edges.add(new MyEdge(new MyPoint(50, 10, 2), new MyPoint(10, 10, 1)));
		edges.add(new MyEdge(new MyPoint(120, 10, 2), new MyPoint(102, 10, 1)));

		return edges;
	}

	/**
	 * show Mesh in 2D
	 * @param myMesh
	 */
	public static void show(MyMesh myMesh) {
		MyDrawing aff2 = new MyDrawing();
		aff2.add(myMesh);
		myMesh.setAffiche(aff2);
	}

	// ---------------------------------------------------------------------------
	// Assertions
	/**
	 * Check coherence
	 * An edge is made of 2 different points
	 * A Triangle is made of 3 different edges
	 * @param aMesh
	 */
	public void assertCoherence(MyMesh aMesh) {
		// Assert edges correctly defined
		boolean correct = true;
		MyEdge myEdge;
		ListIterator<MyEdge> iterEdge = aMesh.getEdges().listIterator();
		while ((correct) && (iterEdge.hasNext())) {
			myEdge = iterEdge.next();
			if (myEdge.getStart() == null) {
				correct = false;
			} else if (myEdge.getEnd() == null) {
				correct = false;
			} else if (myEdge.getStart() == myEdge.getEnd()) {
				correct = false;
			}
			assertTrue(correct);
		}

		// Assert triangles correctly defined
		MyTriangle myTriangle;
		ListIterator<MyTriangle> iterTriangle = aMesh.getTriangles().listIterator();
		while ((correct) && (iterTriangle.hasNext())) {
			myTriangle = iterTriangle.next();
			for (int i=0; i<3; i++) {
				if (myTriangle.getEdge(i) == null) {
					correct = false;
				}
			}
			assertTrue(correct);
		}
		
		iterTriangle = aMesh.getTriangles().listIterator();
		while ((correct) && (iterTriangle.hasNext())) {
			myTriangle = iterTriangle.next();
			for (int i=0; i<3; i++) {
				for (int j=0; j<3; j++) {
					if (j != i)
						if (myTriangle.getEdge(i) == myTriangle.getEdge(j)) {
							correct = false;
						}
				}
			}
			assertTrue(correct);
		}
	}

	/**
	 * Check if each point is used in the Mesh
	 * Check that a point belongs to one edge
	 * @param aMesh
	 */
	public void assertUseEachPoint(MyMesh aMesh) {
		// Assert
		for (MyPoint aPoint : aMesh.getPoints()) {
			// point must belong to an edge
			int GID = aPoint.getGid();
			
			boolean found = false;
			MyEdge myEdge;
			ListIterator<MyEdge> iterEdge = aMesh.getEdges().listIterator();
			while ((! found) && (iterEdge.hasNext())) {
				myEdge = iterEdge.next();
				if (GID == myEdge.getStart().getGid()) {
					found = true;
				} else if (GID == myEdge.getEnd().getGid()) {
					found = true;
				}
			}
			assertTrue(found);
		}
	}

	/**
	 * Check if each edge is used in the Mesh
	 * Check that an edge belongs to one triangle
	 * @param aMesh
	 */
	public void assertUseEachEdge(MyMesh aMesh)  {
		// Assert
		for (MyEdge anEdge : aMesh.getEdges()) {
			// point must belong to an edge
			int GID = anEdge.getGid();
			
			boolean found = false;
			MyTriangle myTriangle;
			ListIterator<MyTriangle> iterTriangle = aMesh.getTriangles().listIterator();
			while ((! found) && (iterTriangle.hasNext())) {
				myTriangle = iterTriangle.next();
				for (int i=0; i<3; i++) {
					if (GID == myTriangle.getEdge(i).getGid()) {
						found = true;
					}
				}
			}
			assertTrue(found);
		}
	}

	/**
	 * Check if 2 points are linked by two different edges 
	 * @param aMesh
	 */
	public void assertDupplicateEdges(MyMesh aMesh) {
	}
	
	/**
	 * test GIDs
	 * Check GIDs for Point, Edges and Triangles
	 * GIDs must exist for each element (GID >= 0).
	 * GIDs are unique for each kind of element.
	 *
	 * @param aMesh
	 */
	public void assertGIDUnicity(MyMesh aMesh) {
		ArrayList<Integer> gids;
		
		// Test points
		gids = new ArrayList<Integer>();
		for (MyPoint myPoint : aMesh.getPoints()) {
			int gid = myPoint.getGid();

			if ((gids.contains(gid)) || (gid < 0)) {
				assertTrue(false);
			} else {
				gids.add(gid);
			}
		}

		// Test edges
		gids = new ArrayList<Integer>();
		for (MyEdge myEdge : aMesh.getEdges()) {
			int gid = myEdge.getGid();

			if ((gids.contains(gid)) || (gid < 0)) {
				assertTrue(false);
			} else {
				gids.add(gid);
			}
		}

		// Test triangles
		gids= new ArrayList<Integer>();
		for (MyTriangle myTriangle : aMesh.getTriangles()) {
			int gid = myTriangle.getGid();

			if ((gids.contains(gid)) || (gid < 0)) {
				assertTrue(false);
			} else {
				gids.add(gid);
			}
		}
	}

}
