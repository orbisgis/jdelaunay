package org.jdelaunay.test;

import java.util.*;
import org.jdelaunay.delaunay.*;

public class ConstrainedDelaunayTest extends BaseTest {

	public void testDelaunayRandomBreaklines() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		
		aMesh.setMax(1300, 700);
		aMesh.setRandomPoints(1000);
		aMesh.setRandomEdges(50);

		aMesh.processDelaunay();

		show(aMesh);
		System.out.println();
		assertTrue(true);
	}

	public void testDelaunayBreaklines() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		
		aMesh.setPoints(getPoints());
		aMesh.setConstraintEdges(getBreaklines());
		
		aMesh.processDelaunay();

		show(aMesh);
		System.out.println();
		assertTrue(true);
	}

	public void testGIDS() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		
		aMesh.setPoints(getPoints());
		aMesh.setConstraintEdges(getBreaklines());
		
		aMesh.processDelaunay();

		assertGIDUnicity(aMesh);
	}

	public void testDuplicatesXYZBreakline() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		
		aMesh.setPoints(getPoints());
		ArrayList<MyEdge> breaklines = getBreaklines();
		breaklines.add(new MyEdge(new MyPoint(120, 10, 2), new MyPoint(102, 10,
				1)));
		aMesh.setConstraintEdges(breaklines);

		aMesh.processDelaunay();

		show(aMesh);
		System.out.println();
		assertTrue(true);	}
}
