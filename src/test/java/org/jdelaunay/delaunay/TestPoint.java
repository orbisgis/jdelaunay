/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdelaunay.delaunay;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * This class is dedicated to the tests related to the point class.
 * @author alexis
 */
public class TestPoint extends BaseUtility {

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
