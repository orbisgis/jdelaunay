/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdelaunay.delaunay;

/**
 * Tests some of the methods defined in MyEdge.
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
		int c = e1.intersects(e2);
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

		e1 = new MyEdge(26.605142485395294,45.996587159903655,14.80314043832656,26.60521489309339,45.99657676794573,14.803141832441982);
		e2 = new MyEdge(26.605142485395294,45.996587159903655,14.80314043832656,26.60522657816091,45.996594194945246,14.80313187802681);
		double d1 = (e1.getPointLeft().getY() - e1.getPointRight().getY())/(e1.getPointLeft().getX() - e1.getPointRight().getX());
		double d2 = (e2.getPointLeft().getY() - e2.getPointRight().getY())/(e2.getPointLeft().getX() - e2.getPointRight().getX());
		System.out.println("d1 : "+d1);
		System.out.println("d2 : "+d2);
		MyElement haha = e1.getIntersection(e2);
		System.out.println(haha);
		System.out.println("e1 inter e2 : "+e1.intersects(e2));
		System.out.println("e2 inter e1 : "+e2.intersects(e1));
		assertTrue(e1.intersects(e2)==3);
		assertTrue(e2.intersects(e1)==3);
	}

	/**
	 * This method check that the vertical sort defined in MyEdge works well
	 */
	public void testVerticalSort() throws DelaunayError{
		MyEdge e1 = new MyEdge(0,0,0,2,2,2);
		MyEdge e2 = new MyEdge(0,0,0,2,2,2);
		assertTrue(e1.verticalSort(e2, 1)==0);

		e1 = new MyEdge(0,0,0,2,3,2);
		e2 = new MyEdge(0,0,0,2,2,2);
		assertTrue(e1.verticalSort(e2, 1)==1);
		assertTrue(e1.verticalSort(e2, 0)==1);

		e1 = new MyEdge(1,8,0,2,3,2);
		e2 = new MyEdge(0,0,0,2,2,2);
		assertTrue(e1.verticalSort(e2, 1)==1);
		assertTrue(e1.verticalSort(e2, 0)==1);

		e1 = new MyEdge(0,0,0,3,3,2);
		e2 = new MyEdge(0,0,0,2,2,2);
		assertTrue(e1.verticalSort(e2, 1)==1);
		assertTrue(e1.verticalSort(e2, 0)==1);

		e1 = new MyEdge(-1,-1,0,2,2,2);
		e2 = new MyEdge(0,0,0,2,2,2);
		assertTrue(e1.verticalSort(e2, 1)==-1);
		assertTrue(e1.verticalSort(e2, 0)==-1);
	}
        
	/**
	 * Checks whether edges are vertical or not.
	 */
	public void testVertical(){
		MyEdge e1 = new MyEdge(14.815045543900215,70.25119472533194,33.92748531499323,14.815093546085139,70.25101901938166,33.92739651606882);
		assertFalse(e1.isVertical());

		e1=new MyEdge(0,0,0,0,5,0);
		assertTrue(e1.isVertical());

		e1=new MyEdge(0,0,0,0.00000009,5,0);
		assertTrue(e1.isVertical());
	}
}
