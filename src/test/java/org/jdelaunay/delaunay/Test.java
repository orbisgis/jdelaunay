package org.jdelaunay.delaunay;

import java.util.ArrayList;

public class Test {

	public static ArrayList<DPoint> getPoints() throws DelaunayError {
		ArrayList<DPoint> points = new ArrayList<DPoint>();
		points.add(new DPoint(12, 10, 2));
		points.add(new DPoint(120, 10, 20));
		points.add(new DPoint(12, 100, 12));
		points.add(new DPoint(102, 100, 1));
		points.add(new DPoint(52, 100, 1));
		points.add(new DPoint(10, 50, 5));
		points.add(new DPoint(50, 50, 1));
		points.add(new DPoint(150, 50, 11));
		points.add(new DPoint(50, 150, 2));
		points.add(new DPoint(5, 50, 3));
		points.add(new DPoint(5, 5, 10));

		return points;
	}

}
