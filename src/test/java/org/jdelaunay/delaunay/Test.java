package org.jdelaunay.delaunay;


public class Test {

	/**
	 * @param args
	 * @throws DelaunayError
	 */
	public static void main(String[] args) throws DelaunayError {
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);

		aMesh.setMax(1300, 700);
		// aMesh.readMesh();
		aMesh.setRandomPoints(20000);
		//aMesh.setRandomEdges(2);

		try {
			// process triangularization
			aMesh.processDelaunay();

			// testDelaunay.removeFlatTriangles();

			// Refine Mesh
			// testDelaunay.setRefinment(Delaunay.refinement_minArea);
			// testDelaunay.refineMesh();

		} catch (DelaunayError e) {
			e.printStackTrace();
		}
		// aMesh.saveMesh();

		MyDrawing aff2 = new MyDrawing();
		aff2.add(aMesh);
		aMesh.setAffiche(aff2);

		//aMesh.saveMesh();
		// aMesh.VRMLexport();
	}
}
