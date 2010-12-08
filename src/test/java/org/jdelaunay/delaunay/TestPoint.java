/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdelaunay.delaunay;

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
}
