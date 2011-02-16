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
                Point p1 = new Point(22,8,0);
                Point p2 = new Point(22.00000005,8.00000005,0.00000005);
                assertTrue(p1.equals(p2));
                assertTrue(p1.compareTo2D(p2)==0);
                p2 = new Point(22.0000001,8.0000001,0.0000001);
                assertFalse(p1.equals(p2));
                assertTrue(p1.compareTo2D(p2)==-1);
        }

        /**
         * Tests the comparison methods between two points in two dimensions.
         */
        public void testComparison() throws DelaunayError {
                Point p1 = new Point(22,8,0);
                Point p2 = new Point(22,8,0);
                assertTrue(p1.compareTo2D(p2)==0);
                p2=new Point(15, 6, 4);
                assertTrue(p1.compareTo2D(p2)==1);
                p2=new Point(35, 6, 4);
                assertTrue(p1.compareTo2D(p2)==-1);
                p2 = new Point(22,5,0);
                assertTrue(p1.compareTo2D(p2)==1);
                p2 = new Point(22,10,0);
                assertTrue(p1.compareTo2D(p2)==-1);
        }

	/**
	 * Checks that we throw the expected exception when instanciating a point
	 * with a NaN value.
	 */
	public void testNotANumber() {
		Point pt;
		try{
			pt = new Point(0, 0, Double.NaN);
			assertTrue(false);
		} catch(DelaunayError d){
		}
		try{
			pt = new Point( Double.NaN,0, 0);
			assertTrue(false);
		} catch(DelaunayError d){
		}
		try{
			pt = new Point(0, Double.NaN, 0);
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
		Point pt = new Point(new Coordinate(1,2,3));
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
	 * Performs operation on the point indicator.
	 * @throws DelaunayError
	 */
	public void testIndicator() throws DelaunayError {
		Point pt = new Point(0,0,0);
		pt.setMarked(1, true);
		assertTrue(pt.isMarked(1));
		pt.setLocked(true);
		assertTrue(pt.isLocked());
		pt.setUseByLevelEdge(true);
		assertTrue(pt.isUseByLevelEdge());
		pt.setUseByPolygon(true);
		assertTrue(pt.isUseByPolygon());
		pt.setUseZ(true);
		assertTrue(pt.isZUse());

		pt.setMarked(1, false);
		assertFalse(pt.isMarked(1));
		pt.setLocked(false);
		assertFalse(pt.isLocked());
		pt.setUseByLevelEdge(false);
		assertFalse(pt.isUseByLevelEdge());
		pt.setUseByPolygon(false);
		assertFalse(pt.isUseByPolygon());
		pt.setUseZ(false);
		assertFalse(pt.isZUse());
	}

	/**
	 * Checks that a point is not equal to null.
	 * @throws DelaunayError
	 */
	public void testNullEquality() throws DelaunayError {
		Point pt = new Point(0,0,0);
		assertFalse(pt.equals2D(null));
	}
}
