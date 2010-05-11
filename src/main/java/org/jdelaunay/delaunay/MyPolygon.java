package org.jdelaunay.delaunay;

import com.vividsolutions.jts.geom.Polygon;

public class MyPolygon extends MyElement {

	private Polygon polygon;

	public MyPolygon(Polygon polygon) {
		super();
		init(polygon);
	}

	public MyPolygon(Polygon polygon, int _type) {
		super(_type);
		init(polygon);
	}
	
	public void init(Polygon polygon) {		
		if (!polygon.isEmpty())
			throw new IllegalArgumentException("Polygon is empty");
	}

	public Polygon getPolygon() {
		return polygon;
	}

}
