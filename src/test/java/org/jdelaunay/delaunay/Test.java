package org.jdelaunay.delaunay;

import java.util.ArrayList;

public class Test {

	public static ArrayList<MyPoint> getPoints() {
		ArrayList<MyPoint> points = new ArrayList<MyPoint>();
		points.add(new MyPoint(12, 10, 2));
		points.add(new MyPoint(120, 10, 20));
		points.add(new MyPoint(12, 100, 12));
		points.add(new MyPoint(102, 100, 1));
		points.add(new MyPoint(52, 100, 1));
		points.add(new MyPoint(10, 50, 5));
		points.add(new MyPoint(50, 50, 1));
		points.add(new MyPoint(150, 50, 11));
		points.add(new MyPoint(50, 150, 2));
		points.add(new MyPoint(5, 50, 3));
		points.add(new MyPoint(5, 5, 10));

		return points;
	}
	/**
	 * @param args
	 * @throws DelaunayError
	 */
	public static void main(String[] args) throws DelaunayError {
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		
		// aMesh.setMax(1300, 700);
		// aMesh.readMesh();
		// aMesh.setRandomPoints(200000);
		// aMesh.setRandomEdges(2);
		aMesh.setPoints(getPoints());
		
		aMesh.processDelaunay();
		
		aMesh.addRefinment(MyMesh.refinement_maxArea);
		aMesh.addRefinment(MyMesh.refinement_softInterpolate);
		aMesh.refineMesh();
		
		MyDrawing aff2 = new MyDrawing();
		aff2.add(aMesh);
		aMesh.setAffiche(aff2);

		aMesh.VRMLexport();
		// aMesh.saveMesh();
	}
}
