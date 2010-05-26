package org.jdelaunay.delaunay;

import java.io.IOException;

import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class TestPolygon  extends BaseTest  {
	
	public void testPolygon_01() throws DelaunayError, IOException, ParseException {
	
		WKTReader reader = new WKTReader();
		Polygon polygon = (Polygon) reader.read("POLYGON((55 80 10, 55 90 10, 35 90 10, 40 120 10, 75 110 10, 55 80 10))");
		MyPolygon aPolygon = new MyPolygon(polygon, 500);
		 
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		aMesh.setPoints(getPoints());
		aMesh.setMax(1300, 700);
		aMesh.processDelaunay();
		
		aMesh.addPolygon(aPolygon);
		
		show(aMesh);
		
	}
	
	
	public void testPolygon_02() throws DelaunayError, IOException, ParseException {
		
		WKTReader reader = new WKTReader();
		Polygon polygon = (Polygon) reader.read("POLYGON((55 80 10, 55 90 10, 35 90 10, 40 120 10, 75 110 10, 55 80 10))");
		MyPolygon aPolygon = new MyPolygon(polygon, 500);
		 
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		aMesh.setPoints(getPoints());
		aMesh.setMax(1300, 700);

		
		aMesh.addPolygon(aPolygon);
		aMesh.processDelaunay();
		
		show(aMesh);
	}
	
	public void testPolygon_03() throws DelaunayError, IOException, ParseException {
		
		WKTReader reader = new WKTReader();
		Polygon polygon = (Polygon) reader.read("POLYGON((55 80 10, 55 90 10, 35 90 10, 40 120 10, 75 110 10, 55 80 10))");
		MyPolygon aPolygon = new MyPolygon(polygon, 500);
		
		Polygon polygon2 = (Polygon) reader.read("POLYGON((20 20 10, 80 20 10, 80 80 10, 20 80 10, 20 20 10))");
		MyPolygon aPolygon2 = new MyPolygon(polygon2, 200);
		 
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		aMesh.setPoints(getPoints());
		aMesh.setMax(1300, 700);

		
		aMesh.addPolygon(aPolygon);
		aMesh.addPolygon(aPolygon2);
		aMesh.processDelaunay();
		
		show(aMesh);
		System.out.println("end");
	}

}
