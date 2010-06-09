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
		System.out.println(aMesh.setPropertyToTriangleInPolygon(aMesh.getPolygon(0), aMesh.getPolygon(0).getRefTriangle()));
		System.out.println("fin 01");
	}
	
	public void testPolygon_01_2() throws DelaunayError, IOException, ParseException {
		
		WKTReader reader = new WKTReader();
		Polygon polygon = (Polygon) reader.read("POLYGON((55 80 10, 55 90 10, 35 90 10, 40 120 10, 75 110 10, 55 80 10))");
		MyPolygon aPolygon = new MyPolygon(polygon, 500);
		 
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		aMesh.setPoints(getPoints());
		aMesh.setMax(1300, 700);
		aMesh.processDelaunay();
		
		aPolygon.setEmpty(true);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		show(aMesh);
		System.out.println(aMesh.setPropertyToTriangleInPolygon(aMesh.getPolygon(0), aMesh.getPolygon(0).getRefTriangle()));
		System.out.println("fin 01");
	}
	
	public void testPolygon_01bis() throws DelaunayError, IOException, ParseException {
		
		WKTReader reader = new WKTReader();
		Polygon polygon = (Polygon) reader.read("POLYGON((55 80 10, 55 90 10, 35 90 10, 40 120 10, 75 110 10, 55 80 10))");
		MyPolygon aPolygon = new MyPolygon(polygon, 500);
		 
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		aMesh.setPoints(getPoints());
		aMesh.setMax(1300, 700);
		aMesh.processDelaunay();
		
		aPolygon.setEmpty(true);
		aMesh.addPolygon(aPolygon);
		
		show(aMesh);
		System.out.println("fin 01bis");
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
	
	public void testPolygon_02bis() throws DelaunayError, IOException, ParseException {
		
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
		aMesh.removeTriangleInPolygon(aMesh.getPolygon(0), aMesh.getPolygon(0).getRefTriangle());
		
		show(aMesh);
	}
	
	public void testPolygon_03() throws DelaunayError, IOException, ParseException {
		
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

		aMesh.addPolygon(aPolygon2);
		aMesh.addPolygon(aPolygon);

		aMesh.processDelaunay();
		
		show(aMesh);
		System.out.println(aMesh.setPropertyToTriangleInPolygon(aMesh.getPolygon(0), aMesh.getPolygon(0).getRefTriangle()));
		System.out.println("end");
	}
	
	public void testPolygon_04() throws DelaunayError, IOException, ParseException {
		
		WKTReader reader = new WKTReader();
		Polygon polygon = (Polygon) reader.read("POLYGON((55 80 10, 55 90 10, 35 90 10, 40 120 10, 75 110 10, 55 80 10))");
		MyPolygon aPolygon = new MyPolygon(polygon, 500);
		
		Polygon polygon2 = (Polygon) reader.read("POLYGON((20 20 10, 80 20 10, 80 80 10, 20 80 10, 20 20 10))");
		MyPolygon aPolygon2 = new MyPolygon(polygon2,16750080);
		
		Polygon polygon3 = (Polygon) reader.read("POLYGON((80 20 10, 80 80 10, 120 80 30, 120 20 30, 80 20 10))");
		MyPolygon aPolygon3 = new MyPolygon(polygon3,5210967);
		 
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		aMesh.setPoints(getPoints());
		aMesh.setMax(1300, 700);

		aMesh.addPolygon(aPolygon2);
		aMesh.addPolygon(aPolygon);
		aPolygon3.setEmpty(true);
		aMesh.addPolygon(aPolygon3);

		aMesh.processDelaunay();
		
		show(aMesh);
		aMesh.VRMLexport();
		System.out.println("end");
	}
	
	public void testPolygon_05() throws DelaunayError, IOException, ParseException {
		
		WKTReader reader = new WKTReader();
		Polygon polygon = (Polygon) reader.read("POLYGON((55 80 10, 55 90 10, 35 90 10, 40 120 10, 75 110 10, 55 80 10))");
		MyPolygon aPolygon = new MyPolygon(polygon, 500);
		
		Polygon polygon2 = (Polygon) reader.read("POLYGON((20 20 10, 80 20 10, 80 80 10, 20 80 10, 20 20 10))");
		MyPolygon aPolygon2 = new MyPolygon(polygon2,16750080);
		
		Polygon polygon3 = (Polygon) reader.read("POLYGON((80 20 10, 80 80 10, 120 80 30, 120 20 30, 80 20 10))");
		MyPolygon aPolygon3 = new MyPolygon(polygon3,5210967);
		
		Polygon polygon4 = (Polygon) reader.read("POLYGON((45 70 30, 45 100 30, 70 100 30, 70 60 30, 45 70 30))");
		MyPolygon aPolygon4 = new MyPolygon(polygon4,13606732);
		 
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		aMesh.setPoints(getPoints());
		aMesh.setMax(1300, 700);

		aMesh.addPolygon(aPolygon2);
		aMesh.addPolygon(aPolygon);
		aMesh.addPolygon(aPolygon3);
	aMesh.addPolygon(aPolygon4);	

		aMesh.processDelaunay();
		
		show(aMesh);
		System.out.println("end");
	}
	
	public void testPolygon_06() throws DelaunayError, IOException, ParseException {
		
		WKTReader reader = new WKTReader();
		Polygon polygon = (Polygon) reader.read("POLYGON((55 80 10, 55 90 10, 35 90 10, 40 120 10, 75 110 10, 55 80 10))");
		MyPolygon aPolygon = new MyPolygon(polygon, 500);
		
		Polygon polygon2 = (Polygon) reader.read("POLYGON((20 20 10, 80 20 10, 80 80 10, 20 80 10, 20 20 10))");
		MyPolygon aPolygon2 = new MyPolygon(polygon2,16750080);
		
		Polygon polygon3 = (Polygon) reader.read("POLYGON((80 20 10, 80 80 10, 120 80 30, 120 20 30, 80 20 10))");
		MyPolygon aPolygon3 = new MyPolygon(polygon3,5210967);
		
		Polygon polygon4 = (Polygon) reader.read("POLYGON((45 70 30, 45 100 30, 70 100 30, 70 60 30, 45 70 30))");
		MyPolygon aPolygon4 = new MyPolygon(polygon4,13606732);
		 
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		aMesh.setPoints(getPoints());
		aMesh.setMax(1300, 700);

		aMesh.addPolygon(aPolygon2);
		aMesh.addPolygon(aPolygon);
		aMesh.addPolygon(aPolygon3);


		aMesh.processDelaunay();
		aMesh.addPolygon(aPolygon4);
		
		show(aMesh);
		System.out.println("end");
	}

	
	
	public void testPolygon_07() throws DelaunayError, IOException, ParseException {
		
		WKTReader reader = new WKTReader();
		Polygon polygon = (Polygon) reader.read("POLYGON((55 80 10, 55 90 10, 35 90 10, 40 120 10, 75 110 10, 55 80 10))");
		MyPolygon aPolygon = new MyPolygon(polygon, 500);
		
		Polygon polygon2 = (Polygon) reader.read("POLYGON((20 20 10, 80 20 10, 80 80 10, 20 80 10, 20 20 10))");
		MyPolygon aPolygon2 = new MyPolygon(polygon2,16750080);
		
		Polygon polygon3 = (Polygon) reader.read("POLYGON((80 20 10, 80 80 10, 120 80 30, 120 20 30, 80 20 10))");
		MyPolygon aPolygon3 = new MyPolygon(polygon3,5210967);
		
		Polygon polygon4 = (Polygon) reader.read("POLYGON((70 55 5, 70 45 5, 110 45 5, 110 55 5, 70 55 5))");
		MyPolygon aPolygon4 = new MyPolygon(polygon4,13606732);
		 
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		aMesh.setPoints(getPoints());
		aMesh.setMax(1300, 700);

		aMesh.addPolygon(aPolygon2);
		aMesh.addPolygon(aPolygon);
		
		aPolygon3.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon3);

		aPolygon4.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon4);
		
		aMesh.processDelaunay();

		
		show(aMesh);
		aMesh.VRMLexport("testPolygon_07-2.wrl");
		System.out.println("end");
	}
}
