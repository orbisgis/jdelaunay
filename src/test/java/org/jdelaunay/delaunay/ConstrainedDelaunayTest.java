package org.jdelaunay.delaunay;

import java.util.ArrayList;

import org.jdelaunay.delaunay.Delaunay;
import org.jdelaunay.delaunay.DelaunayError;
import org.jdelaunay.delaunay.MyEdge;
import org.jdelaunay.delaunay.MyMesh;
import org.jdelaunay.delaunay.MyPoint;

public class ConstrainedDelaunayTest extends BaseTest {

	public void testDelaunayRandomBreaklines() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		Delaunay testDelaunay = new Delaunay(aMesh);
		testDelaunay.setPrecision(1.0e-3);
		testDelaunay.setVerbose(true);
		aMesh.setRandomPoints(100);
		aMesh.setRandomEdges(50);
		aMesh.setStart();
		aMesh.setMax(1300, 700);
		testDelaunay.processDelaunay();
		aMesh.setEnd();
		show(aMesh);
		System.out.println();
	}

	public void testDelaunayBreaklines() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		Delaunay testDelaunay = new Delaunay(aMesh);
		testDelaunay.setPrecision(1.0e-3);
		testDelaunay.setVerbose(true);
		aMesh.setPoints(getPoints());
		aMesh.setEdges(getBreaklines());
		aMesh.setStart();
		aMesh.setMax(1300, 700);
		testDelaunay.processDelaunay();
		aMesh.setEnd();
		show(aMesh);
		System.out.println();
	}

	public void testGIDS() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		Delaunay testDelaunay = new Delaunay(aMesh);
		testDelaunay.setPrecision(1.0e-3);
		testDelaunay.setVerbose(true);
		aMesh.setPoints(getPoints());
		aMesh.setEdges(getBreaklines());
		aMesh.setStart();
		aMesh.setMax(1300, 700);
		testDelaunay.processDelaunay();
		aMesh.setEnd();
		testGID(aMesh);
	}

	public void testDuplicatesXYZBreakline() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		Delaunay testDelaunay = new Delaunay(aMesh);
		testDelaunay.setPrecision(1.0e-3);
		testDelaunay.setVerbose(true);
		aMesh.setPoints(getPoints());
		ArrayList<MyEdge> breaklines = getBreaklines();
		breaklines.add(new MyEdge(new MyPoint(120, 10, 2), new MyPoint(102, 10,
				1)));
		aMesh.setEdges(breaklines);
		aMesh.setStart();
		aMesh.setMax(1300, 700);
		testDelaunay.processDelaunay();
		aMesh.setEnd();
		show(aMesh);
		System.out.println();
	}
}
