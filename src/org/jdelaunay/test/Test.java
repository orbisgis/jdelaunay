package org.jdelaunay.test;

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
//		aMesh.setDisplayCircles(true);
//		aMesh.readMeshPoints();
		aMesh.setRandomPoints(10000);
		aMesh.setRandomEdges(200);
//		aMesh.saveMeshPoints();
//		aMesh.addBoundingBox();
		
		aMesh.setStart();
		try {
			// process triangularization
			testDelaunay.processDelaunay();

			// Refine Mesh
			testDelaunay.refineMesh();
//			testDelaunay.refineMeshAngles();
			
		} catch (DelaunayError e) {
			e.printStackTrace();
		}
		aMesh.setEnd();
	
		MyDrawing aff = new MyDrawing();
		aff.add(aMesh);
		aMesh.setAffiche(aff);

//		aMesh.saveMeshXML();
	}
}
