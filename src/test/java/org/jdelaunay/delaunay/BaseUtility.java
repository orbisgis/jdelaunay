package org.jdelaunay.delaunay;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdelaunay.delaunay.display.MeshDrawer;
import java.util.ArrayList;
import java.util.ListIterator;

import junit.framework.TestCase;

public class BaseUtility extends TestCase {

	// ---------------------------------------------------------------------------
	// Utilities
	/**
	 * Generate an array of points
	 * @return
	 * @throws DelaunayError 
	 */
	public static ArrayList<Point> getPoints() throws DelaunayError {
		ArrayList<Point> points = new ArrayList<Point>();
		points.add(new Point(12, 10, 2));
		points.add(new Point(120, 10, 20));
		points.add(new Point(12, 100, 12));
		points.add(new Point(102, 100, 1));
		points.add(new Point(52, 100, 1));
		points.add(new Point(10, 50, 5));
		points.add(new Point(50, 50, 1));
		points.add(new Point(150, 50, 11));
		points.add(new Point(50, 150, 2));
		points.add(new Point(5, 50, 3));
		points.add(new Point(5, 5, 10));

		return points;
	}

	/**
	 * Generate an array of edges
	 * @return
	 * @throws DelaunayError 
	 */
	public static ArrayList<Edge> getBreaklines() throws DelaunayError {
		ArrayList<Edge> edges = new ArrayList<Edge>();
		edges.add(new Edge(new Point(12, 10, 2), new Point(102, 100, 1)));
		edges.add(new Edge(new Point(50, 10, 2), new Point(10, 10, 1)));
		edges.add(new Edge(new Point(120, 10, 2), new Point(102, 10, 1)));

		return edges;
	}

	/**
	 * show Mesh in 2D
	 * @param myMesh
	 */
	public static void show(ConstrainedMesh myMesh) {
		MeshDrawer aff2 = new MeshDrawer();
		aff2.add(myMesh);
                aff2.setVisible(true);
                try {
                        System.in.read();
                } catch (IOException ex) {
                        Logger.getLogger(BaseUtility.class.getName()).log(Level.WARNING, null, ex);
                }
	}

	// ---------------------------------------------------------------------------
	// Assertions
	/**
	 * Check coherence
	 * An edge is made of 2 different points
	 * A DelaunayTriangle is made of 3 different edges
	 * @param aMesh
	 */
	public void assertCoherence(MyMesh aMesh) {
		// Assert edges correctly defined
		boolean correct = true;
		Edge myEdge;
		ListIterator<Edge> iterEdge = aMesh.getEdges().listIterator();
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
		DelaunayTriangle myTriangle;
		ListIterator<DelaunayTriangle> iterTriangle = aMesh.getTriangles().listIterator();
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
					if (j != i && myTriangle.getEdge(i) == myTriangle.getEdge(j)) {
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
		for (Point aPoint : aMesh.getPoints()) {
			// point must belong to an edge
			int GID = aPoint.getGid();
			
			boolean found = false;
			Edge myEdge;
			ListIterator<Edge> iterEdge = aMesh.getEdges().listIterator();
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
		for (Edge anEdge : aMesh.getEdges()) {
			// point must belong to an edge
			int GID = anEdge.getGid();
			
			boolean found = false;
			DelaunayTriangle myTriangle;
			ListIterator<DelaunayTriangle> iterTriangle = aMesh.getTriangles().listIterator();
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
		for (Point myPoint : aMesh.getPoints()) {
			int gid = myPoint.getGid();

			if ((gids.contains(gid)) || (gid < 0)) {
				assertTrue(false);
			} else {
				gids.add(gid);
			}
		}

		// Test edges
		gids = new ArrayList<Integer>();
		for (Edge myEdge : aMesh.getEdges()) {
			int gid = myEdge.getGid();

			if ((gids.contains(gid)) || (gid < 0)) {
				assertTrue(false);
			} else {
				gids.add(gid);
			}
		}

		// Test triangles
		gids= new ArrayList<Integer>();
		for (DelaunayTriangle myTriangle : aMesh.getTriangles()) {
			int gid = myTriangle.getGid();

			if ((gids.contains(gid)) || (gid < 0)) {
				assertTrue(false);
			} else {
				gids.add(gid);
			}
		}
	}

}
