package org.jdelaunay.delaunay;

import java.util.*;
import org.jdelaunay.delaunay.*;
import org.jhydrocell.hydronetwork.*;


public class DelaunayForHydroTest extends BaseTest {

	/**
	 * After removing flat triangles the triangulation is not conform to the
	 * delaunay criterion
	 *
	 * @throws DelaunayError
	 */
	public void testRemoveFlatTriangles() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		Delaunay testDelaunay = new Delaunay(aMesh);
		testDelaunay.setPrecision(1.0e-3);
		testDelaunay.setVerbose(true);
		aMesh.setPoints(getPoints());
		aMesh.setStart();
		aMesh.setMax(1300, 700);
		testDelaunay.processDelaunay();

		LinkedList<MyTriangle> triangles = aMesh.getTriangles();

		int nbFlat = 0;
		for (MyTriangle myTriangle : triangles) {

			if (myTriangle.isFlatSlope()) {
				nbFlat++;
			}
		}

		assertTrue(nbFlat > 0);
		testDelaunay.removeFlatTriangles();
		triangles = aMesh.getTriangles();

		nbFlat = 0;
		for (MyTriangle myTriangle : triangles) {

			if (myTriangle.isFlatSlope()) {
				nbFlat++;
			}
		}
		assertTrue(nbFlat == 0);
		aMesh.setEnd();

	}

	public void testEdgesMorphologicalClassification() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		Delaunay testDelaunay = new Delaunay(aMesh);
		MyHydroNetwork HydroNetwork = new MyHydroNetwork(testDelaunay);
		testDelaunay.setPrecision(1.0e-3);
		testDelaunay.setVerbose(true);
		aMesh.setPoints(getPoints());
		aMesh.setStart();
		aMesh.setMax(1300, 700);
		testDelaunay.processDelaunay();
		
		
		
		HydroNetwork.morphologicalQualification();

		ArrayList<MyEdge> edges = aMesh.getEdges();

		for (MyEdge myEdge : edges) {

			myEdge.getType();
		}

	}
}
