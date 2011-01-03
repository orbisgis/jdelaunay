package org.jdelaunay.delaunay;

import java.util.ArrayList;

public class Test {

	public static ArrayList<Point> getPoints() throws DelaunayError {
		ArrayList<Point> points = new ArrayList<Point>();
		points.add(new Point(12, 10, 2));
		points.add(new Point(120, 10, 20));
		points.add(new Point(12, 100, 12));
		points.add(new Point(102, 100, 1));
		points.add(new Point(52, 100, 1));
		points.add(new Point(10, 50, 5));
		points.add(new Point(50, 50, 1));
		points.add(new Point(150, 50, 11));
		points.add(new Point(50, 150, 2));
		points.add(new Point(5, 50, 3));
		points.add(new Point(5, 5, 10));

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
		
		aMesh.addRefinment(MyMesh.REFINEMENT_MAX_AREA);
		aMesh.addRefinment(MyMesh.REFINEMENT_SOFT_INTERPOLATE);
		aMesh.refineMesh();
		
//		MeshDrawer aff2 = new MeshDrawer();
//		aff2.add(aMesh);
//		aMesh.setAffiche(aff2);

		aMesh.VRMLexport();
		System.out.println("I'm in your main !");
		// aMesh.saveMesh();
	}
}
