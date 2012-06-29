/**
 *
 * jDelaunay is a library dedicated to the processing of Delaunay and constrained
 * Delaunay triangulations from PSLG inputs.
 *
 * This library is developed at French IRSTV institute as part of the AvuPur and Eval-PDU project,
 * funded by the French Agence Nationale de la Recherche (ANR) under contract
 * ANR-07-VULN-01 and ANR-08-VILL-0005-01 .
 *
 * jDelaunay is distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2010-2012 IRSTV FR CNRS 2488
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
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.jdelaunay.delaunay.geometries;

import com.vividsolutions.jts.geom.Coordinate;
import java.util.List;
import junit.framework.TestCase;
import org.jdelaunay.delaunay.error.DelaunayError;

/**
 *
 * @author Alexis Guéganno
 */
public class TestBoundaryBox extends TestCase {

	public void testGetPoints() throws DelaunayError{
		BoundaryBox bb = new BoundaryBox(0, 4, 0, 4, 7, 4);
		List<DPoint> list = bb.getPoints();
		assertTrue(list.contains(new DPoint(0,0,4)));
		assertTrue(list.contains(new DPoint(4,4,7)));
	}
        
        public void testExpandBox() throws DelaunayError {
                BoundaryBox bb = new BoundaryBox();
                assertTrue(bb.getPoints().isEmpty());
                bb.alterBox(new Coordinate(0,0,0));
		List<DPoint> list = bb.getPoints();
		assertTrue(list.get(0).equals(new DPoint(0,0,0)));
		assertTrue(list.get(1).equals(new DPoint(0,0,0)));
                bb.alterBox(new Coordinate(2,0,0));
		list = bb.getPoints();
		assertTrue(list.get(0).equals(new DPoint(0,0,0)));
		assertTrue(list.get(1).equals(new DPoint(2,0,0)));
                bb.alterBox(new Coordinate(-2,0,0));
		list = bb.getPoints();
		assertTrue(list.get(0).equals(new DPoint(-2,0,0)));
		assertTrue(list.get(1).equals(new DPoint(2,0,0)));
                bb.alterBox(new Coordinate(0,2,0));
		list = bb.getPoints();
		assertTrue(list.get(0).equals(new DPoint(-2,0,0)));
		assertTrue(list.get(1).equals(new DPoint(2,2,0)));
                bb.alterBox(new Coordinate(0,-2,0));
		list = bb.getPoints();
		assertTrue(list.get(0).equals(new DPoint(-2,-2,0)));
		assertTrue(list.get(1).equals(new DPoint(2,2,0)));
                bb.alterBox(new Coordinate(0,0,2));
		list = bb.getPoints();
		assertTrue(list.get(0).equals(new DPoint(-2,-2,0)));
		assertTrue(list.get(1).equals(new DPoint(2,2,2)));
                bb.alterBox(new Coordinate(0,0,-2));
		list = bb.getPoints();
		assertTrue(list.get(0).equals(new DPoint(-2,-2,-2)));
		assertTrue(list.get(1).equals(new DPoint(2,2,2)));
                
        }
        
        /**
         * We check that the empty attribute is well transmitted.
         * @throws DelaunayError 
         */
        public void testAlternativeConstructor() throws DelaunayError {
                BoundaryBox bb = new BoundaryBox();
                assertTrue(bb.getPoints().isEmpty());
                BoundaryBox bb2 = new BoundaryBox(bb);
                assertTrue(bb.getPoints().isEmpty());
                
        }
}
