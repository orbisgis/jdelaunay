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
		aMesh.setRandomPoints(100000);
//	aMesh.setRandomEdges(500);
	//	aMesh.addEdge(aMesh.getPoints().get(5), aMesh.getPoints().get(9));
//		aMesh.saveMeshPoints();
//		aMesh.addBoundingBox();

		aMesh.setStart();
		try {
			// process triangularization
			testDelaunay.processDelaunay();

			//testDelaunay.setMinAngle(30);

			// Refine Mesh
			testDelaunay.refineMesh();
//			testDelaunay.refineMeshAngles();

			double x,y,z;
			x=y=z=0.0;
			for (int i=0; i<3; i++) {
				x+= aMesh.getPoints().get(i).getX();
				y+= aMesh.getPoints().get(i).getY();
				z+= aMesh.getPoints().get(i).getZ();
			}
			MyPoint aPoint = new MyPoint(x/3,y/3,z/3);
			testDelaunay.addPoint(aPoint);

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
