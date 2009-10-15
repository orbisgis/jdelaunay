package org.jdelaunay.test;

import java.util.ArrayList;
import java.util.LinkedList;

import org.jdelaunay.delaunay.MyDrawing;
import org.jdelaunay.delaunay.MyEdge;
import org.jdelaunay.delaunay.MyMesh;
import org.jdelaunay.delaunay.MyPoint;
import org.jdelaunay.delaunay.MyTriangle;

import junit.framework.TestCase;

public class BaseTest extends TestCase {

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

	public static ArrayList<MyEdge> getBreaklines() {

		ArrayList<MyEdge> edges = new ArrayList<MyEdge>();
		edges.add(new MyEdge(new MyPoint(12, 10, 2), new MyPoint(102, 100, 1)));
		edges.add(new MyEdge(new MyPoint(50, 10, 2), new MyPoint(10, 10, 1)));
		edges.add(new MyEdge(new MyPoint(120, 10, 2), new MyPoint(102, 10, 1)));

		return edges;
	}

	public static void show(MyMesh myMesh) {
		MyDrawing aff2 = new MyDrawing();
		aff2.add(myMesh);
		myMesh.setAffiche(aff2);
	}

	public static void testGID(MyMesh aMesh) {

		LinkedList<MyTriangle> triangles = aMesh.getTriangles();
		ArrayList<Integer> gids = new ArrayList<Integer>();
		for (MyTriangle myTriangle : triangles) {
			int gid = myTriangle.getGid();

			if (gids.contains(gid) && gid > 0) {
				assertTrue(false);
			} else {
				gids.add(gid);
			}
		}

		ArrayList<MyPoint> points = aMesh.getPoints();
		gids = new ArrayList<Integer>();

		for (MyPoint myPoint : points) {
			int gid = myPoint.getGid();

			if (gids.contains(gid) && gid > 0) {
				assertTrue(false);
			} else {
				gids.add(gid);
			}
		}

		ArrayList<MyEdge> edgs = aMesh.getEdges();
		gids = new ArrayList<Integer>();

		for (MyEdge myEdge : edgs) {
			int gid = myEdge.getGid();

			if (gids.contains(gid) && gid > 0) {
				assertTrue(false);
			} else {
				gids.add(gid);
			}
		}

	}
}
