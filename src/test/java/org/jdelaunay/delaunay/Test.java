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

}
