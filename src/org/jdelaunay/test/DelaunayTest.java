package org.jdelaunay.test;

import java.util.ArrayList;
import java.util.Iterator;

import org.jdelaunay.delaunay.Delaunay;
import org.jdelaunay.delaunay.DelaunayError;
import org.jdelaunay.delaunay.MyDrawing;
import org.jdelaunay.delaunay.MyEdge;
import org.jdelaunay.delaunay.MyMesh;
import org.jdelaunay.delaunay.MyPoint;

import junit.framework.TestCase;

public class DelaunayTest extends TestCase {

	public void testDelaunayDupplicateEDges() {

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
		aMesh.setMax(1300, 700);
		aMesh.setStart();

		try {
			// process triangularization
			testDelaunay.processDelaunay();

			testDelaunay.removeFlatTriangles();

			testDelaunay.morphologicalQualification();

		} catch (DelaunayError e) {
			e.printStackTrace();
		}
		aMesh.setEnd();

		for (MyPoint pt : points) {

			int ptGID = pt.getGid();

			for (MyEdge myEdge : edges) {

				if (ptGID == myEdge.getStart().getGid()) {
					assertTrue(true);
				} else if (ptGID == myEdge.getEnd().getGid()) {
					assertTrue(true);
				} else {
					assertTrue(false);
				}

			}
		}



	}
}
