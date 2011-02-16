/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdelaunay.delaunay;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

/**
 *
 * @author alexis
 */
public class TestConstraintPolygon extends BaseUtility {

	public void testInstanciation() throws DelaunayError {
		Coordinate[] pgc = new Coordinate[10];
		pgc[0] = new Coordinate(7,7);
		pgc[1] = new Coordinate(6,9);
		pgc[2] = new Coordinate(6,11);
		pgc[3] = new Coordinate(7,12);
		pgc[4] = new Coordinate(9,11);
		pgc[5] = new Coordinate(11,12);
		pgc[6] = new Coordinate(13,11);
		pgc[7] = new Coordinate(13,9);
		pgc[8] = new Coordinate(11,7);
		pgc[9] = new Coordinate(7,7);
		GeometryFactory geomFac = new GeometryFactory();
		LinearRing rgG = geomFac.createLinearRing(pgc);
		Polygon    pgG = geomFac.createPolygon(rgG,null);
		ConstraintPolygon poly = new ConstraintPolygon(pgG);
		assertFalse(poly.isUsePolygonZ());
		assertFalse(poly.isEmpty());
		poly = new ConstraintPolygon(pgG,1);
		assertFalse(poly.isUsePolygonZ());
		assertFalse(poly.isEmpty());
		assertTrue(poly.getProperty()==1);
		poly = new ConstraintPolygon(pgG,true);
		assertFalse(poly.isUsePolygonZ());
		assertTrue(poly.isEmpty());
		assertTrue(poly.getProperty()==0);
		poly = new ConstraintPolygon(pgG,1,true);
		assertTrue(poly.isUsePolygonZ());
		assertFalse(poly.isEmpty());
		assertTrue(poly.getProperty()==1);
		poly = new ConstraintPolygon(pgG,1,true,true);
		assertTrue(poly.isUsePolygonZ());
		assertTrue(poly.isEmpty());
		assertTrue(poly.getProperty()==1);


	}

}
