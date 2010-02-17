package org.jdelaunay.delaunay;

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
		// aMesh.readMesh("test.txt");
		aMesh.setRandomPoints(90000);

//		aMesh.setRandomEdges(2);
		// aMesh.setDisplayCircles(true);

		aMesh.setStart();
		try {
			// process triangularization
			testDelaunay.processDelaunay();

			// testDelaunay.removeFlatTriangles();

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
		// aMesh.VRMLexport();
	}
}
