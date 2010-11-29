/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdelaunay.delaunay;

/**
 *
 * @author alexis
 */
public class TestEdges extends BaseTest {

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

}
