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

}
