package org.jdelaunay.delaunay;

import java.util.ArrayList;
import java.util.LinkedList;

import org.jhydrocell.hydronetwork.MyHydroNetwork;


public class DelaunayForHydroTest extends BaseTest {

	/**
	 * After removing flat triangles the triangulation is not conform to the
	 * delaunay criterion
	 *
	 * @throws DelaunayError
	 */
	public void testRemoveFlatTriangles() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		aMesh.setPoints(getPoints());
		aMesh.setStart();
		aMesh.setMax(1300, 700);
		aMesh.processDelaunay();

		LinkedList<MyTriangle> triangles = aMesh.getTriangles();

		int nbFlat = 0;
		for (MyTriangle myTriangle : triangles) {

			if (myTriangle.isFlatSlope()) {
				nbFlat++;
			}
		}

		assertTrue(nbFlat > 0);
		aMesh.removeFlatTriangles();
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
		MyHydroNetwork HydroNetwork = new MyHydroNetwork(aMesh);
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		aMesh.setPoints(getPoints());
		aMesh.setStart();
		aMesh.setMax(1300, 700);
		aMesh.processDelaunay();
		
		
		
		HydroNetwork.morphologicalQualification();

		ArrayList<MyEdge> edges = aMesh.getEdges();

		for (MyEdge myEdge : edges) {

			myEdge.getProperty();
		}

	}
}
