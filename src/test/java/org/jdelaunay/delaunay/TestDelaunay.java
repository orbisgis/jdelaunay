package org.jdelaunay.delaunay;

import java.util.ArrayList;
import java.util.ListIterator;

public class TestDelaunay extends BaseTest {
	/**
	 * Test random generation of points
	 * @throws DelaunayError
	 */
	public void testDelaunayRandomPoints() throws DelaunayError {
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		aMesh.setMax(1300, 700);
		aMesh.setRandomPoints(5000);

		aMesh.processDelaunay();
		show(aMesh);
		System.out.println();
		assertTrue(true);
	}

	/**
	 * Use identified set of points
	 * @throws DelaunayError
	 */
	public void testDelaunayPoints() throws DelaunayError {
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		aMesh.setPoints(getPoints());

		aMesh.processDelaunay();

		show(aMesh);
		System.out.println();
		assertTrue(true);
	}

	/**
	 * test GIDs
	 * Check GIDs for Point, Edges and Triangles
	 * GIDs must exist for each element (GID >= 0).
	 * GIDs are unique for each kind of element.
	 *
	 * @throws DelaunayError
	 */
	public void testGIDS() throws DelaunayError {
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		aMesh.setPoints(getPoints());
		aMesh.processDelaunay();
		
		assertGIDUnicity(aMesh);
	}

	/**
	 * Test points at the same location in 3D
	 * Use a predefined set of points and add the first one
	 * The final set of points must be decremented by 1
	 * 
	 * @throws DelaunayError
	 */
	public void testDelaunayDuplicateXYZPoint() throws DelaunayError {
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		
		ArrayList<MyPoint> pts = getPoints();
		MyPoint addedPoint = new MyPoint(pts.get(1));
		pts.add(addedPoint);
		int PtsSize = pts.size();
		
		aMesh.setPoints(pts);
		aMesh.processDelaunay();

		assertTrue(aMesh.getNbPoints() == (PtsSize - 1));
	}

	/**
	 * Test points at the same location in 2D
	 * Use a predefined set of points and add the first one
	 * The final set of points must be decremented by 1
	 * @throws DelaunayError
	 */
	public void testDelaunayDuplicateXYPoint() throws DelaunayError {
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		
		ArrayList<MyPoint> pts = getPoints();
		MyPoint addedPoint = new MyPoint(pts.get(1));
		addedPoint.setZ(addedPoint.getZ() + 10);
		pts.add(addedPoint);
		int PtsSize = pts.size();
		
		aMesh.setPoints(pts);
		aMesh.processDelaunay();

		assertTrue(aMesh.getNbPoints() == (PtsSize - 1));
	}

	/**
	 * Check coherence
	 * An edge is made of 2 different points
	 * A Triangle is made of 3 different edges
	 * @throws DelaunayError
	 */
	public void testCoherence() throws DelaunayError {
		// Generate Mesh
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);

		aMesh.setMax(1300, 700);
		aMesh.setRandomPoints(5000);

		// process triangularization
		aMesh.processDelaunay();

		// Assert edges correctly defined
		assertCoherence(aMesh);
	}

	/**
	 * Check if each point and each edge is used at least once
	 * Check that each point belongs to an edge
	 * Check that each edge belongs to a triangle
	 * @throws DelaunayError
	 */
	public void testUseEachElement() throws DelaunayError {
		// Generate Mesh
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);

		aMesh.setMax(1300, 700);
		aMesh.setRandomPoints(5000);

		// process triangularization
		aMesh.processDelaunay();

		// Assert
		assertUseEachPoint(aMesh);
		assertUseEachEdge(aMesh);
	}

	/**
	 * Check if 2 points are linked by two different edges 
	 * @throws DelaunayError
	 */
	public void testDelaunayDupplicateEdges() throws DelaunayError {
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);

		aMesh.setMax(1300, 700);
		aMesh.setRandomPoints(5000);

		// process triangularization
		aMesh.processDelaunay();

		boolean correct = true;
		ArrayList<MyEdge> edgeList = aMesh.getEdges();
		
		for (MyEdge anEdge:edgeList) {
			MyEdge myEdge;
			MyPoint start = anEdge.getStart();
			MyPoint end = anEdge.getEnd();
			
			ListIterator<MyEdge> iterEdge = edgeList.listIterator();
			while ((correct) && (iterEdge.hasNext())) {
				myEdge = iterEdge.next();
				if (anEdge != myEdge) {
					if ((start == myEdge.getStart()) && (end == myEdge.getEnd())) {
						correct = false;
					} else if ((end == myEdge.getStart()) && (start == myEdge.getEnd())) {
						correct = false;
					}
				}
			}
			assertTrue(correct);
		}
	}

	/**
	 * Refine Mesh - test triangles area
	 * @throws DelaunayError
	 */
	public void testDelaunayPointsRefinementMaxArea() throws DelaunayError {
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		
		aMesh.setPoints(getPoints());
		
		aMesh.processDelaunay();
		
		aMesh.setMaxArea(1000);
		assertTrue(aMesh.getMaxArea() == 1000);
		
		aMesh.setRefinment(MyMesh.refinement_maxArea);
		aMesh.refineMesh();
		show(aMesh);

		for (MyTriangle myTriangle : aMesh.getTriangles()) {
			if (myTriangle.computeArea() > 1000) {
				assertTrue(false);
			}
		}
	}
}
