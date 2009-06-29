package org.jdelaunay.test;

import java.util.ArrayList;

import org.jdelaunay.delaunay.*;

public class Test {

	/**
	 * @param args
	 * @throws DelaunayError
	 */
	public static void main(String[] args) throws DelaunayError {
		MyMesh aMesh = new MyMesh();
		Delaunay testDelaunay = new Delaunay(aMesh);
		testDelaunay.setPrecision(1.0e-3);
		testDelaunay.setVerbose(true);

		aMesh.setMax(1300, 700);
		aMesh.readMesh("test.txt");
		// aMesh.setRandomPoints(5);
		// aMesh.setRandomEdges(2);
		// aMesh.setDisplayCircles(true);

		/*ArrayList<MyEdge> edges = new ArrayList<MyEdge>();
		MyEdge edge = new MyEdge(new MyPoint(50, 100, 10), new MyPoint(30, 20,
				10));

		edges.add(edge);*/
		// edges.add(edge2);
		// aMesh.setEdges(edges);

		aMesh.setStart();
		try {
			// process triangularization
			testDelaunay.processDelaunay();

			testDelaunay.removeFlatTriangles();

			testDelaunay.morphologicalQualification();

			// Refine Mesh
			// testDelaunay.setRefinment(Delaunay.refinement_minArea);
			// testDelaunay.refineMesh();

		} catch (DelaunayError e) {
			e.printStackTrace();
		}
		aMesh.setEnd();
		// aMesh.saveMesh();

		MyDrawing aff2 = new MyDrawing();
		aff2.add(aMesh);
		aMesh.setAffiche(aff2);

		aMesh.saveMeshXML();
		//aMesh.VRMLexport();
	}
}
