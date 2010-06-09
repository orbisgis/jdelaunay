package org.jdelaunay.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import org.jdelaunay.delaunay.*;

public class DelaunayTest extends BaseTest {

	public void testDelaunayRandomPoints() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		aMesh.setRandomPoints(5000);
		aMesh.setMax(1300, 700);
		aMesh.processDelaunay();
		show(aMesh);
		System.out.println();

	}

	public void testDelaunayPoints() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		aMesh.setPoints(getPoints());
		aMesh.setMax(1300, 700);
		aMesh.processDelaunay();
		show(aMesh);
		System.out.println();

	}

	public void testDelaunayPointsRefinementMaxArea() throws DelaunayError, IOException {

		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		aMesh.setPoints(getPoints());
		aMesh.processDelaunay();
		aMesh.setMinArea(1000);
		assertTrue(aMesh.getMinArea() == 1000);
		aMesh.setRefinment(aMesh.refinement_maxArea);

		aMesh.refineMesh();


		show(aMesh);
		
		ArrayList<MyTriangle> triangles = aMesh.getTriangles();

		for (MyTriangle myTriangle : triangles) {

			if (myTriangle.computeArea() > 1000) {
				assertTrue(false);
			}

		}

	}

	public void testDelaunayDuplicateXYZPoint() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		ArrayList<MyPoint> pts = getPoints();
		pts.add(new MyPoint(52, 100, 1));
		aMesh.setPoints(pts);
		aMesh.setMax(1300, 700);
		aMesh.processDelaunay();
		assertTrue(aMesh.getNbPoints() == pts.size() - 1);

	}

	public void testDelaunayDuplicateXYPoint() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		ArrayList<MyPoint> pts = getPoints();
		pts.add(new MyPoint(52, 100, 10));
		aMesh.setPoints(pts);
		aMesh.setMax(1300, 700);
		aMesh.processDelaunay();
		show(aMesh);
		assertTrue(aMesh.getNbPoints() == pts.size() - 1);

	}

	/**
	 * GID must be unique and greater than 0
	 *
	 * @throws DelaunayError
	 */
	public void testGIDS() throws DelaunayError {
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		aMesh.setPoints(getPoints());
		aMesh.setMax(1300, 700);
		aMesh.processDelaunay();
		assertGIDUnicity(aMesh);
	}

	public void testDelaunayDupplicateEdges() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);

		ArrayList<MyPoint> points = new ArrayList<MyPoint>();

		points.add(new MyPoint(50, 100, 10));
		points.add(new MyPoint(30, 20, 10));
		points.add(new MyPoint(30, 10, 1));
		points.add(new MyPoint(100, 10, 5));
		points.add(new MyPoint(50, 10, 5));

		ArrayList<MyEdge> edges = new ArrayList<MyEdge>();
		MyEdge edge1 = new MyEdge(new MyPoint(50, 100, 10), new MyPoint(30, 20,
				10));
		MyEdge edge2 = new MyEdge(new MyPoint(50, 100, 10), new MyPoint(100,
				10, 5));

		edges.add(edge1);
		edges.add(edge2);

		aMesh.setEdges(edges);
		aMesh.setPoints(points);

		// process triangularization
		aMesh.processDelaunay();

		int j=0;
		for (int i=0;i<aMesh.getEdges().size()-1;i++) {
			for (j=i+1;j<aMesh.getEdges().size();j++) {			
				assertFalse((aMesh.getEdges().get(i).getStart().getGID()==aMesh.getEdges().get(j).getStart().getGID()
								&& aMesh.getEdges().get(i).getEnd().getGID()==aMesh.getEdges().get(j).getEnd().getGID())
								|| (aMesh.getEdges().get(j).getStart().getGID()==aMesh.getEdges().get(i).getStart().getGID()
								&& aMesh.getEdges().get(j).getEnd().getGID()==aMesh.getEdges().get(i).getEnd().getGID()));

			}

		}

	}

}
