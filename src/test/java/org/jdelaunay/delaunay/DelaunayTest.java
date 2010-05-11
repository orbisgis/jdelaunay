package org.jdelaunay.delaunay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import org.jdelaunay.delaunay.Delaunay;
import org.jdelaunay.delaunay.DelaunayError;
import org.jdelaunay.delaunay.MyEdge;
import org.jdelaunay.delaunay.MyMesh;
import org.jdelaunay.delaunay.MyPoint;
import org.jdelaunay.delaunay.MyTriangle;

public class DelaunayTest extends BaseTest {

	public void testDelaunayRandomPoints() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		Delaunay testDelaunay = new Delaunay(aMesh);
		testDelaunay.setPrecision(1.0e-3);
		testDelaunay.setVerbose(true);
		aMesh.setRandomPoints(5000);
		aMesh.setStart();
		aMesh.setMax(1300, 700);
		testDelaunay.processDelaunay();
		aMesh.setEnd();
		show(aMesh);
		System.out.println();

	}

	public void testDelaunayPoints() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		Delaunay testDelaunay = new Delaunay(aMesh);
		testDelaunay.setPrecision(1.0e-3);
		testDelaunay.setVerbose(true);
		aMesh.setPoints(getPoints());
		aMesh.setStart();
		aMesh.setMax(1300, 700);
		testDelaunay.processDelaunay();
		aMesh.setEnd();
		show(aMesh);
		System.out.println();

	}

	public void testDelaunayPointsRefinementMaxArea() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		Delaunay testDelaunay = new Delaunay(aMesh);
		testDelaunay.setPrecision(1.0e-3);
		testDelaunay.setVerbose(true);
		aMesh.setPoints(getPoints());
		aMesh.setStart();
		testDelaunay.processDelaunay();
		testDelaunay.setMaxArea(1000);
		assertTrue(testDelaunay.getMaxArea() == 1000);
		testDelaunay.setRefinment(Delaunay.refinement_maxArea);
		testDelaunay.refineMesh();
		aMesh.setEnd();
		show(aMesh);

		LinkedList<MyTriangle> triangles = aMesh.getTriangles();

		for (MyTriangle myTriangle : triangles) {

			if (myTriangle.computeArea() > 1000) {
				assertTrue(false);
			}

		}

	}

	public void testDelaunayPointsRefinementMinArea() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		Delaunay testDelaunay = new Delaunay(aMesh);
		testDelaunay.setPrecision(1.0e-3);
		testDelaunay.setVerbose(true);
		aMesh.setPoints(getPoints());
		aMesh.setStart();
		testDelaunay.processDelaunay();
		testDelaunay.setMinArea(1000);
		assertTrue(testDelaunay.getMinArea() == 1000);
		testDelaunay.setRefinment(Delaunay.refinement_minArea);
		testDelaunay.refineMesh();
		aMesh.setEnd();

		LinkedList<MyTriangle> triangles = aMesh.getTriangles();

		for (MyTriangle myTriangle : triangles) {

			if (myTriangle.computeArea() < 1000) {
				assertTrue(false);
			}

		}

	}

	public void testDelaunayPointsRefinementMinAngle() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		Delaunay testDelaunay = new Delaunay(aMesh);
		testDelaunay.setPrecision(1.0e-3);
		testDelaunay.setVerbose(true);
		aMesh.setPoints(getPoints());
		aMesh.setStart();
		testDelaunay.processDelaunay();
		testDelaunay.setMinAngle(30);
		assertTrue(testDelaunay.getMinAngle() == 30);
		testDelaunay.setRefinment(Delaunay.refinement_minAngle);
		testDelaunay.refineMesh();
		aMesh.setEnd();

		LinkedList<MyTriangle> triangles = aMesh.getTriangles();

		for (MyTriangle myTriangle : triangles) {

			if (myTriangle.badAngle(30) >= 0) {
				assertTrue(false);
			}

		}

	}

	public void testDelaunayDuplicateXYZPoint() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		Delaunay testDelaunay = new Delaunay(aMesh);
		testDelaunay.setPrecision(1.0e-3);
		testDelaunay.setVerbose(true);
		ArrayList<MyPoint> pts = new ArrayList<MyPoint>();
		pts.add(new MyPoint(0, 0, 0));
		pts.add(new MyPoint(10, 0, 0));
		pts.add(new MyPoint(5, 5, 0));
		pts.add(new MyPoint(10, 5, 0));
		pts.add(new MyPoint(0.001, 0.001, 10));
		pts.add(new MyPoint(5.0001, 5.0001, 10));
		
		aMesh.setPoints(pts);
		aMesh.setStart();
		aMesh.setMax(1300, 700);
		testDelaunay.processDelaunay();
		aMesh.setEnd();
		aMesh.saveMeshAsVRML("/tmp/mesh.vrml");
	
		show(aMesh);
		
		assertEquals("aMesh.getNbPoints()== pts.size()-1?",aMesh.getNbPoints(), pts.size() - 1);
	}

	public void testDelaunayDuplicateXYPoint() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		Delaunay testDelaunay = new Delaunay(aMesh);
		testDelaunay.setPrecision(1.0e-3);
		testDelaunay.setVerbose(true);
		ArrayList<MyPoint> pts = getPoints();
		pts.add(new MyPoint(52, 100, 10));
		aMesh.setPoints(pts);
		aMesh.setStart();
		aMesh.setMax(1300, 700);
		testDelaunay.processDelaunay();
		aMesh.setEnd();
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
		Delaunay testDelaunay = new Delaunay(aMesh);
		testDelaunay.setPrecision(1.0e-3);
		testDelaunay.setVerbose(true);
		aMesh.setPoints(getPoints());
		aMesh.setStart();
		aMesh.setMax(1300, 700);
		testDelaunay.processDelaunay();
		aMesh.setEnd();
		testGID(aMesh);
	}

	public void testDelaunayDupplicateEdges() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		Delaunay testDelaunay = new Delaunay(aMesh);
		testDelaunay.setPrecision(1.0e-3);
		testDelaunay.setVerbose(true);

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
		aMesh.setStart();
		
		// process triangularization
		testDelaunay.processDelaunay();

		aMesh.setEnd();

		show(aMesh);

		int j=0;
		for (int i=0;i<aMesh.getEdges().size()-1;i++) {
			for (j=i+1;j<aMesh.getEdges().size();j++) {
//				System.out.println(aMesh.getEdges().get(i).getStart().getGid()+"->"+aMesh.getEdges().get(i).getEnd().getGid()+" "+aMesh.getEdges().get(j).getStart().getGid()+"->"+aMesh.getEdges().get(j).getEnd().getGid());
				
				assertFalse((aMesh.getEdges().get(i).getStart().getGid()==aMesh.getEdges().get(j).getStart().getGid()
				&& aMesh.getEdges().get(i).getEnd().getGid()==aMesh.getEdges().get(j).getEnd().getGid())
				|| (aMesh.getEdges().get(j).getStart().getGid()==aMesh.getEdges().get(i).getStart().getGid()
				&& aMesh.getEdges().get(j).getEnd().getGid()==aMesh.getEdges().get(i).getEnd().getGid()));

			}

		}
		
	}

}
