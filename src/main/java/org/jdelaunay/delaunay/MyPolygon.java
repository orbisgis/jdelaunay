package org.jdelaunay.delaunay;

/**
 * Delaunay Package.
 *
 * @author Erwan BOCHER, Adelin PIAU
 * @date 2010-05-20
 * @revision 2010-05-20
 * @version 2.0
 */

import java.util.ArrayList;

import com.vividsolutions.jts.geom.Polygon;

public class MyPolygon extends MyElement {
	
	private Polygon polygon;
	private ArrayList<MyEdge> edges;
	
	
	/**
	 * Generate a polygon.
	 * @param polygon
	 */
	public MyPolygon(Polygon polygon) {
		super();
		init(polygon);
	}

	/**
	 * Generate a polygon with property.
	 * @param polygon
	 * @param _property
	 */
	public MyPolygon(Polygon polygon, int _property) {
		super(_property);
		init(polygon);
	}
	

	/**
	 * Initialize data.
	 * @param polygon
	 */
	private void init(Polygon polygon) {		
		if (polygon.isEmpty())
			throw new IllegalArgumentException("Polygon is empty");
		
		this.polygon=polygon;
		
		edges=new ArrayList<MyEdge>();
		
		for( int i=1;i<polygon.getNumPoints();i++)
		{
			edges.add(new MyEdge(
				new MyPoint(polygon.getCoordinates()[i-1]),
				new MyPoint(polygon.getCoordinates()[i]) 
				));
		}
		
	}

	/**
	 * @return a polygon.
	 */
	public Polygon getPolygon() {
		return polygon;
	}
	
	
	/**
	 * Get the edges structure.
	 * @return edges
	 */
	public ArrayList<MyEdge> getEdges() {
		return edges;
	}
}
