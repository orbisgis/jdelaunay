/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdelaunay.delaunay;

/**
 *
 * @author alexis
 */
public class TestEdges extends BaseUtility {

	/**
	 * test that  getPointfromItsX retrieves the points of absciss X from the
	 * line associated to the edge
	 */
	public void testGetPointsFromX() {
		MyEdge e = new MyEdge(0,0,0,1,1,1);
		MyEdge e2 = new MyEdge(0,0,0,0,10,0);
		try{
			assertTrue(e.getPointFromItsX(0.5).equals(new MyPoint(0.5, 0.5,0.5)));
			assertTrue(e.getPointFromItsX(0.1).equals(new MyPoint(0.1, 0.1,0.1)));
			assertTrue(e.getPointFromItsX(0.2).equals(new MyPoint(0.2, 0.2,0.2)));
			assertTrue(e.getPointFromItsX(0.7).equals(new MyPoint(0.7, 0.7,0.7)));
			assertNull(e2.getPointFromItsX(8));
		} catch (DelaunayError d){
			System.out.println(d.getMessage());
		}
	}

	/**
	 * This test checks the results returned by the method intersects of the class
	 * MyEdge.
         * The method also checks that the intersection points are well computed.
	 */
	public void testEdgesIntersection() throws DelaunayError{
		MyEdge e1 = new MyEdge(4,4,0,8,8,0);
		MyEdge e2 = new MyEdge(8,4,0,4,8,0);
                MyElement intersection;
		assertTrue(e1.intersects(e2)==1);
                intersection = e1.getIntersection(e2);
                assertTrue(intersection.equals(new MyPoint(6,6,0)));


		e2 = new MyEdge(4,4,0,2,2,0);
		assertTrue(e1.intersects(e2)==3);
                intersection = e1.getIntersection(e2);
                assertTrue(intersection.equals(new MyPoint(4,4,0)));


		e2 = new MyEdge(4,4,0,20,20,0);
		assertTrue(e1.intersects(e2)==4);
                intersection = e1.getIntersection(e2);
                assertTrue(intersection.equals(new MyEdge(4,4,0,8,8,0)));


		e2 = new MyEdge(10,10,0,6,6,0);
		assertTrue(e1.intersects(e2)==4);
                intersection = e1.getIntersection(e2);
                assertTrue(intersection.equals(new MyEdge(6,6,0,8,8,0)));


		e2 = new MyEdge(6,4,0,10,8,0);		
		assertTrue(e1.intersects(e2)==2);


		e2 = new MyEdge(0,0,0,1,-4,0);
		assertTrue(e1.intersects(e2)==0);
		assertNull(e1.getIntersection(e2));


		e2 = new MyEdge(8,8,0,1,-4,0);
		assertTrue(e1.intersects(e2)==3);
                intersection = e1.getIntersection(e2);
                assertTrue(intersection.equals(new MyPoint(8,8,0)));


		e2 = new MyEdge(6,6,0,1,-4,0);
		assertTrue(e1.intersects(e2)==1);

                //some tests for vertical edges
		e1 = new MyEdge(4,4,0,4,8,0);
		e2 = new MyEdge(4,4,0,4,2,0);
		assertTrue(e1.intersects(e2)==3);
		assertTrue(e1.getIntersection(e2).equals(new MyPoint(4,4,0)));


		e2 = new MyEdge(4,8,0,4,10,0);
		assertTrue(e1.intersects(e2)==3);


		e2 = new MyEdge(4,6,0,4,10,0);
		assertTrue(e1.intersects(e2)==4);
		assertTrue(e1.getIntersection(e2).equals(new MyEdge(4,6,0,4,8,0)));


		e2 = new MyEdge(2,4,0,2,10,0);
		assertTrue(e1.intersects(e2)==2);
		assertNull(e1.getIntersection(e2));


		e2 = new MyEdge(4,9,0,4,10,0);
		assertTrue(e1.intersects(e2)==2);


		e2 = new MyEdge(9,9,0,10,10,0);
		assertTrue(e1.intersects(e2)==0);
		assertNull(e1.getIntersection(e2));


		e2 = new MyEdge(4,0,0,4,10,0);
		assertTrue(e1.intersects(e2)==4);
		assertTrue(e1.getIntersection(e2).equals(new MyEdge(4,4,0,4,8,0)));


		e2 = new MyEdge(4,8,0,10,12,0);
		assertTrue(e1.intersects(e2)==3);


		e2 = new MyEdge(9,9,0,10,10,0);
		assertTrue(e1.intersects(e2)==0);


                //tests for horizontal edges
		e1 = new MyEdge(4,4,0,8,4,0);
		e2 = new MyEdge(4,4,0,4,2,0);
		assertTrue(e1.intersects(e2)==3);


		e2 = new MyEdge(5,3,0,5,8,0);
		assertTrue(e1.intersects(e2)==1);


		e2 = new MyEdge(9,4,0,0,4,0);
		assertTrue(e1.intersects(e2)==4);
		assertTrue(e1.getIntersection(e2).equals(new MyEdge(4,4,0,8,4,0)));

		e2 = new MyEdge(8,4,0,10,4,0);
		assertTrue(e1.intersects(e2)==3);


		e2 = new MyEdge(9,4,0,10,4,0);
		assertTrue(e1.intersects(e2)==2);


		e2 = new MyEdge(4,4,0,8,7,0);
		assertTrue(e1.intersects(e2)==3);


		e2 = new MyEdge(4,3,0,8,5,0);
		assertTrue(e1.intersects(e2)==1);


		e2 = new MyEdge(4,3,0,8,3,0);
		assertTrue(e1.intersects(e2)==2);
	}

	/**
	 * This method check that the vertical sort defined in MyEdge works well
	 */

	public void testVerticalSort() throws DelaunayError{
		MyEdge e1 = new MyEdge(0,0,0,2,2,2);
		MyEdge e2 = new MyEdge(0,0,0,2,2,2);
		assertTrue(e1.verticalSort(e2, 1)==0);
	}
        
}
