package org.jdelaunay.delaunay;

import java.util.ArrayList;
import java.util.List;

import org.jhydrocell.hydronetwork.HydroNetwork;


public class DelaunayForHydroTest extends BaseUtility {

	/**
	 * After removing flat triangles the triangulation is not conform to the
	 * delaunay criterion
	 *
	 * @throws DelaunayError
	 */
	public void testRemoveFlatTriangles() throws DelaunayError {

		ConstrainedMesh aMesh = new ConstrainedMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		aMesh.setPoints(getPoints());
		aMesh.processDelaunay();

		List<DelaunayTriangle> triangles = aMesh.getTriangleList();

		int nbFlat = 0;
		for (DelaunayTriangle myTriangle : triangles) {

			if (myTriangle.isFlatSlope()) {
				nbFlat++;
			}
		}

		assertTrue(nbFlat > 0);
//		aMesh.removeFlatTriangles();
		triangles = aMesh.getTriangleList();

		nbFlat = 0;
		for (DelaunayTriangle myTriangle : triangles) {

			if (myTriangle.isFlatSlope()) {
				nbFlat++;
			}
		}
//		assertTrue(nbFlat == 0);
	}

	public void testEdgesMorphologicalClassification() throws DelaunayError {

		ConstrainedMesh aMesh = new ConstrainedMesh();
		HydroNetwork HydroNetwork = new HydroNetwork(aMesh);
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		aMesh.setPoints(getPoints());
//		aMesh.setMax(1300, 700);
		aMesh.processDelaunay();
		
		
		
		HydroNetwork.morphologicalQualification();

		List<Edge> edges = aMesh.getEdges();

		for (Edge myEdge : edges) {

			myEdge.getProperty();
		}
		
	}
}
