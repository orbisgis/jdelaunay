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
