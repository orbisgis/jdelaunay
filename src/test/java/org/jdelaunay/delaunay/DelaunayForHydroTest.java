package org.jdelaunay.delaunay;

import java.util.ArrayList;

import org.jhydrocell.hydronetwork.MyHydroNetwork;


public class DelaunayForHydroTest extends BaseUtility {

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
		aMesh.setMax(1300, 700);
		aMesh.processDelaunay();

		ArrayList<MyTriangle> triangles = aMesh.getTriangles();

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
//		MyDrawing aff2 = new MyDrawing();
//		aff2.add(aMesh);
//		aMesh.setAffiche(aff2);
	}

	public void testEdgesMorphologicalClassification() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		MyHydroNetwork HydroNetwork = new MyHydroNetwork(aMesh);
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		aMesh.setPoints(getPoints());
		aMesh.setMax(1300, 700);
		aMesh.processDelaunay();
		
		
		
		HydroNetwork.morphologicalQualification();

		ArrayList<MyEdge> edges = aMesh.getEdges();

		for (MyEdge myEdge : edges) {

			myEdge.getProperty();
		}
		
		
//		MyDrawing aff2 = new MyDrawing();
//		aff2.add(aMesh);
//		aMesh.setAffiche(aff2);
	}
}
