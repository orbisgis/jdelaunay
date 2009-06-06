package org.jdelaunay.test;

import java.awt.Point;

import org.jdelaunay.delaunay.*;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MyMesh aMesh = new MyMesh();
		Delaunay testDelaunay = new Delaunay(aMesh);
		testDelaunay.setPrecision(1.0e-3);

		aMesh.setMax(1300, 700);
		// aMesh.setDisplayCircles(true);
		// aMesh.readMeshPoints();

		for (int i = 0; i < 10; i++) {

			aMesh.addEdge(new MyEdge(new MyPoint(i, i+10, i), new MyPoint(i+20, i +5, i)));
		}
		//aMesh.setRandomEdges(2);
		// aMesh.saveMeshPoints();
		// aMesh.addBoundingBox();

		aMesh.setStart();
		try {
			// process triangularization
			testDelaunay.processDelaunay();

/*
			for (int i = 0; i < 100; i++) {


				testDelaunay.addPoint(new MyPoint(i+ 5,10, i +12));
			}*/


			// Refine Mesh
			//testDelaunay.refineMesh();
			// testDelaunay.refineMeshAngles();

		} catch (DelaunayError e) {
			e.printStackTrace();
		}
		aMesh.setEnd();

		MyDrawing aff = new MyDrawing();
		aff.add(aMesh);
		aMesh.setAffiche(aff);

		aMesh.saveMesh("/tmp/text.text");
	}
}
