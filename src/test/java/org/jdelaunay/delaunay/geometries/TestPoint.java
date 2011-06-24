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

package org.jdelaunay.delaunay.geometries;

import com.vividsolutions.jts.geom.Coordinate;
import junit.framework.TestCase;
import org.jdelaunay.delaunay.error.DelaunayError;

/**
 * This class is dedicated to the tests related to the point class.
 * @author alexis
 */
public class TestPoint extends TestCase {

        /**
         * Tests the use of MyTools.Epsilon when comparing to points, or testing 
         * their equality.
         */
        public void testPrecision() throws DelaunayError{
                DPoint p1 = new DPoint(22,8,0);
                DPoint p2 = new DPoint(22.00000005,8.00000005,0.00000005);
                assertTrue(p1.equals(p2));
                assertTrue(p1.compareTo2D(p2)==0);
                p2 = new DPoint(22.0000001,8.0000001,0.0000001);
                assertFalse(p1.equals(p2));
                assertTrue(p1.compareTo2D(p2)==-1);
                assertFalse(p1.equals(new Integer(0)));
        }

        public void testClosedTo() throws DelaunayError {
                DPoint p1 = new DPoint(22,8,0);
                assertTrue(p1.closedTo(new DPoint(22.0000001,8,0), 0.0001));
                assertFalse(p1.closedTo(new DPoint(22.001,8,0), 0.0001));
        }
        
        public void testContains() throws DelaunayError {
                DPoint p1 = new DPoint(0,0,0);
                assertTrue(p1.contains(new DPoint(0,0,0.00000001)));
                assertTrue(p1.contains(new Coordinate(0,0,0.00000001)));
                assertFalse(p1.contains(new DPoint(0,0,0.001)));
                assertFalse(p1.contains(new Coordinate(0,0,0.001)));
        }
        
        /**
         * Tests the comparison methods between two points in two dimensions.
         */
        public void testComparison() throws DelaunayError {
                DPoint p1 = new DPoint(22,8,0);
                DPoint p2 = new DPoint(22,8,0);
                assertTrue(p1.compareTo2D(p2)==0);
                p2=new DPoint(15, 6, 4);
                assertTrue(p1.compareTo2D(p2)==1);
                p2=new DPoint(35, 6, 4);
                assertTrue(p1.compareTo2D(p2)==-1);
                p2 = new DPoint(22,5,0);
                assertTrue(p1.compareTo2D(p2)==1);
                p2 = new DPoint(22,10,0);
                assertTrue(p1.compareTo2D(p2)==-1);
        }

	/**
	 * Checks that we throw the expected exception when instanciating a point
	 * with a NaN value.
	 */
	public void testNotANumber() {
		DPoint pt;
		try{
			pt = new DPoint(0, 0, Double.NaN);
			assertTrue(false);
		} catch(DelaunayError d){
		}
		try{
			pt = new DPoint( Double.NaN,0, 0);
			assertTrue(false);
		} catch(DelaunayError d){
		}
		try{
			pt = new DPoint(0, Double.NaN, 0);
			assertTrue(false);
		} catch(DelaunayError d){
		}
		assertTrue(true);
	}

	/**
	 * Tests the point instanciation.
	 * @throws DelaunayError
	 */
	public void testInstanciation() throws DelaunayError {
		DPoint pt = new DPoint(new Coordinate(1,2,3));
		assertTrue(pt.getX()==1);
		assertTrue(pt.getY()==2);
		assertTrue(pt.getZ()==3);
		pt.setX(5);
		assertTrue(pt.getX()==5);
		pt.setY(6);
		assertTrue(pt.getY()==6);
		pt.setZ(7);
		assertTrue(pt.getZ()==7);

	}

	/**
	 * Checks that a point is not equal to null.
	 * @throws DelaunayError
	 */
	public void testNullEquality() throws DelaunayError {
		DPoint pt = new DPoint(0,0,0);
		assertFalse(pt.equals2D(null));
	}
}
