package org.jdelaunay.delaunay;

import java.io.IOException;

import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class TestQuadTree extends BaseTest  {
	
	public void testQuadTree_01() throws DelaunayError, IOException, ParseException {
		
		
		WKTReader reader = new WKTReader();
		Polygon polygon = (Polygon) reader.read("POLYGON((55 80 10, 55 90 10, 35 90 10, 40 120 10, 75 110 10, 55 80 10))");
		MyPolygon aPolygon = new MyPolygon(polygon, 500);
		
		Polygon polygon2 = (Polygon) reader.read("POLYGON((20 20 10, 80 20 10, 80 80 10, 20 80 10, 20 20 10))");
		MyPolygon aPolygon2 = new MyPolygon(polygon2,16750080);
		
		
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		aMesh.setPoints(getPoints());
		aMesh.setMax(1300, 700);
		

		aMesh.addPolygon(aPolygon);
		
		aMesh.processDelaunay();
				aMesh.addPolygon(aPolygon2);
		
		
		show(aMesh);
//		System.out.println("points :\n"+aMesh.points+"\n");
		System.out.println("\npoint inside aPolygon's bounding box :\n"+aMesh.quadTree.searchAllStric(aPolygon.getBoundingBox()));//new MyBox(0, 130, 0, 30, 0, 40)
		System.out.println("\npoint inside aPolygon2's bounding box :\n"+aMesh.quadTree.searchAllStric(aPolygon2.getBoundingBox()));
		System.out.println("fin 01\n");
	}
	
	
	public void testQuadTree_02() throws DelaunayError, IOException, ParseException {
		
		
		WKTReader reader = new WKTReader();
		Polygon polygon = (Polygon) reader.read("POLYGON((55 80 10, 55 90 10, 35 90 10, 40 120 10, 75 110 10, 55 80 10))");
		MyPolygon aPolygon = new MyPolygon(polygon, 500);
		
		Polygon polygon2 = (Polygon) reader.read("POLYGON((20 20 10, 80 20 10, 80 80 10, 20 80 10, 20 20 10))");
		MyPolygon aPolygon2 = new MyPolygon(polygon2,16750080);
		
		
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		aMesh.setPoints(getPoints());
		aMesh.setMax(1300, 700);
		

		aMesh.addPolygon(aPolygon);
		
		aPolygon2.setEmpty(true);
		aMesh.addPolygon(aPolygon2);
		
		aMesh.processDelaunay();
		

		
		
		show(aMesh);

		System.out.println("\npoint inside aPolygon's bounding box :\n"+aMesh.quadTree.searchAllStric(aPolygon.getBoundingBox()));
		System.out.println("\npoint inside aPolygon2's bounding box :\n"+aMesh.quadTree.searchAllStric(aPolygon2.getBoundingBox()));
		System.out.println("fin 02\n");
	}
	
	public void testQuadTree_03() throws DelaunayError, IOException, ParseException {
		
		
		WKTReader reader = new WKTReader();
		Polygon polygon = (Polygon) reader.read("POLYGON((55 80 10, 55 90 10, 35 90 10, 40 120 10, 75 110 10, 55 80 10))");
		MyPolygon aPolygon = new MyPolygon(polygon, 500);
		
		Polygon polygon2 = (Polygon) reader.read("POLYGON((20 20 10, 80 20 10, 80 80 10, 20 80 10, 20 20 10))");
		MyPolygon aPolygon2 = new MyPolygon(polygon2,16750080);
		
		
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		aMesh.setPoints(getPoints());
		aMesh.setMax(1300, 700);
		

		aMesh.addPolygon(aPolygon);
		aMesh.processDelaunay();
		
		aPolygon2.setEmpty(true);
		aMesh.addPolygon(aPolygon2);

		
		
		show(aMesh);

		System.out.println("\npoint inside aPolygon's bounding box :\n"+aMesh.quadTree.searchAllStric(aPolygon.getBoundingBox()));
		System.out.println("\npoint inside aPolygon2's bounding box :\n"+aMesh.quadTree.searchAllStric(aPolygon2.getBoundingBox()));
		System.out.println("fin 03\n");
	}

}
