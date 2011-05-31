/*
 * jDelaunay is a library dedicated to the processing of Delaunay and constrained 
 * Delaunay triangulations from PSLG inputs.
 * 
 * This library is developed at French IRSTV institute as part of the AvuPur and Eval-PDU project, 
 * funded by the French Agence Nationale de la Recherche (ANR) under contract 
 * ANR-07-VULN-01 and ANR-08-VILL-0005-01 .
 * 
 * jDelaunay is distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Adelin PIAU, Jean-Yves MARTIN
 * Copyright (C) 2011 Alexis GUEGANNO, Jean-Yves MARTIN
 * 
 * jDelaunay is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * jDelaunay is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * jDelaunay. If not, see <http://www.gnu.org/licenses/>.
 * 
 * For more information, please consult: <http://trac.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.jdelaunay.delaunay;

import java.util.ArrayList;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Delaunay Package.
 *
 * @author Erwan BOCHER, Adelin PIAU, Alexis GUÉGANNO
 */
public final class ConstraintPolygon extends Element {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Polygon polygon;
	private ArrayList<DEdge> edges;
	private DTriangle refTriangle;

	
	/**
	 * True, if we want to set Z coordinate of polygon to new point else set an average of polygon and mesh Z coordinate.
	 */
	private boolean usePolygonZ;
	
	/**
	 * True, if we want to remove triangle who are inside the polygon.
	 */
	private boolean isEmpty;

	
	/**
	 * True, if we want to make new triangulation inside the polygon.
	 */
	private boolean mustBeTriangulated;
	
	/**
	 * Generate a polygon.
	 * 
	 * @param polygon
	 * @throws DelaunayError 
	 */
	public ConstraintPolygon(Polygon polygon) throws DelaunayError {
		super();
		this.usePolygonZ=false;
		this.isEmpty=false;
		init(polygon);
	}
	
	/**
	 * Generate a polygon.
	 * 
	 * @param polygon
	 * @param isEmpty True, if we remove triangle who are inside the polygon.
	 * @throws DelaunayError 
	 */
	public ConstraintPolygon(Polygon polygon, boolean isEmpty) throws DelaunayError {
		super();
		init(polygon);
		this.usePolygonZ=false;
		this.isEmpty=isEmpty;
	}

	/**
	 * Generate a polygon with property.
	 * 
	 * @param polygon
	 * @param property
	 * @throws DelaunayError 
	 */
	public ConstraintPolygon(Polygon polygon, int property) throws DelaunayError {
		super(property);
		this.usePolygonZ=false;
		this.isEmpty=false;
		init(polygon);
	}
	
	/**
	 * Generate a polygon with property.
	 * 
	 * @param polygon
	 * @param property
	 * @param usePolygonZ - 
         *      True, if we set Z coordinate of polygon to new point else set an average of polygon and mesh Z coordinate.
	 * @throws DelaunayError 
	 */
	public ConstraintPolygon(Polygon polygon, int property, boolean usePolygonZ) throws DelaunayError {
		super(property);
		this.usePolygonZ=usePolygonZ;
		this.isEmpty=false;
		init(polygon);
	}
	
	/**
	 * Generate a polygon with property.
	 * 
	 * @param polygon
	 * @param property
	 * @param usePolygonZ True, if we set Z coordinate of polygon to new point else set an average of polygon and mesh Z coordinate.
	 * @param isEmpty True, if we remove triangle who are inside the polygon.
	 * @throws DelaunayError 
	 */
	public ConstraintPolygon(Polygon polygon, int property, boolean usePolygonZ, boolean isEmpty) throws DelaunayError {
		super(property);
		this.usePolygonZ=usePolygonZ;
		this.isEmpty=isEmpty;
		init(polygon);
	}
	
	/**
	 * Initialize data.
	 * 
	 * @param polygon
	 * @throws DelaunayError 
	 */
	private void init(Polygon polygon) throws DelaunayError {
		if (polygon.isEmpty()) {
			throw new IllegalArgumentException("Polygon is empty");
		}
		else {
			this.polygon = polygon;
			
			refTriangle=null;
			
			// create edge list
			edges = new ArrayList<DEdge>();

			// add edges to the edge list
			// each point is created one
			int nbPoints = polygon.getNumPoints();
			
			if(Double.isNaN(polygon.getCoordinates()[0].z)) {
				polygon.getCoordinates()[0].z = 0;
			}
			DPoint lastPoint = new DPoint(polygon.getCoordinates()[0]);

			DPoint aPoint;
			DEdge aEdge;
			for (int i = 1; i < nbPoints; i++) {
				if(Double.isNaN(polygon.getCoordinates()[i].z)) {
					polygon.getCoordinates()[i].z = 0;
				}
				aPoint = new DPoint(polygon.getCoordinates()[i]);
				aEdge=new DEdge(lastPoint, aPoint);
				edges.add(aEdge);
				lastPoint = aPoint;
			}
		}
	}

	/**
	 * @return True, if we set Z coordinate of polygon to new point else set an average of polygon and mesh Z coordinate.
	 */
	public boolean isUsePolygonZ() {
		return usePolygonZ;
	}
	
	/**
	 * @return True, if we remove triangle who are inside the polygon.
	 */
	public boolean isEmpty() { 
		return isEmpty;
	}

	/**
	 * True, if we remove triangle who are inside the polygon.
	 * @param isEmpty
	 */
	public void setEmpty(boolean isEmpty) { 
		this.isEmpty = isEmpty;
	}
	
	
	/**
	 * @return True, if we make new triangulation inside the polygon.
	 */
	public boolean mustBeTriangulated()
	{
		return mustBeTriangulated;
	}
	
	/**
	 * True, if we want to make new triangulation inside the polygon.
	 */
	public void setMustBeTriangulated(boolean mustBeTriangulated)
	{
		this.mustBeTriangulated=mustBeTriangulated;
		setEmpty(mustBeTriangulated);
	}
	
	
	/**
	 * @return The reference triangle.
	 */
	public DTriangle getRefTriangle() {
		return refTriangle;
	}

	/**
	 * Set the reference triangle.
	 * @param refTriangle
	 */
	public void setRefTriangle(DTriangle refTriangle) {
		this.refTriangle = refTriangle;
	}

	/**
	 * @return a polygon.
	 */
	public Polygon getPolygon() {
		return polygon;
	}

	/**
	 * Get the edges structure.
	 * 
	 * @return edges
	 */
	public ArrayList<DEdge> getEdges() {
		return edges;
	}
	
	/**
	 * Get points.
	 * 
	 * @return points
	 * @throws DelaunayError 
	 */
	public ArrayList<DPoint> getPoints() throws DelaunayError {
		ArrayList<DPoint> points= new ArrayList<DPoint>();
		DPoint aPoint;
		for (int i = 0; i < polygon.getNumPoints()-1; i++)
		{	
			aPoint=new DPoint(polygon.getCoordinates()[i]);
			points.add(aPoint);
		}
		return points;
	}
	
	/**
	 * Get the minimum boundary of this element.
	 * @return
         *      The bounding box of the polygon.
	 */
	@Override
	public BoundaryBox getBoundingBox() {
		BoundaryBox aBox = new BoundaryBox();

		int nbPoints = polygon.getNumPoints();
		for (int i = 0; i < nbPoints; i++) {
			Coordinate pt = polygon.getCoordinates()[i];
			aBox.alterBox( pt.x, pt.y, pt.z );
		}
		
		return aBox;
	}

	/**
	 * Check if this polygon contain the point given in argument.
	 * @param aPoint
	 * @return
         *      true if aPoint is in this.
	 */
	@Override
	public boolean contains(DPoint aPoint) {
		return polygon.contains(new GeometryFactory().createPoint(aPoint.getCoordinate()));
	}
	
	public boolean contains(DEdge anEdge) throws DelaunayError { //FIXME make better code
		DPoint aPoint = anEdge.getBarycenter();
		boolean intersectPolygon=polygon.contains(new GeometryFactory().createPoint(aPoint.getCoordinate()));
		boolean edgeColinear=false, intersectEdge=false;
		for(int i=0;i<edges.size();i++ )
		{
			if(edges.get(i).haveSamePoint(anEdge)) {
				edgeColinear = true;
			}
			else if(edges.get(i).intersects(anEdge.getStartPoint(), anEdge.getEndPoint())==DEdge.INTERSECT) {
				intersectEdge = true;
			}
		}
		return (intersectPolygon && !intersectEdge) || (!intersectPolygon && edgeColinear && !intersectEdge);
	}
	
	/**
	 * check if this polygon contain the point given in argument.
	 * @param coordinate
	 * @return
         *      true if coordinate is in this
	 * @throws DelaunayError
	 */
	@Override
	public boolean contains(Coordinate coordinate) throws DelaunayError {
		return polygon.contains(new GeometryFactory().createPoint(coordinate));

	}

	/**
	 *
	 * @param anEdge
	 * @return      
         *      true if anEdge intersects this.
	 * @throws DelaunayError
	 */
	public boolean isIntersect(DEdge anEdge) throws DelaunayError{
		boolean intersect=false;
		for(int i=0;i<edges.size() && !intersect;i++ )
		{
			intersect=edges.get(i).intersects(anEdge.getStartPoint(),anEdge.getEndPoint()) == DEdge.INTERSECT;
		}
			
		return intersect;
	}
	
}
