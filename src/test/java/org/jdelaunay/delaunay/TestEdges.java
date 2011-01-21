/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdelaunay.delaunay;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Tests some of the methods defined in Edge.
 * @author alexis
 */
public class TestEdges extends BaseUtility {

	/**
	 * test that  getPointfromItsX retrieves the points of absciss X from the
	 * line associated to the edge
	 */
	public void testGetPointsFromX() {
		Edge e = new Edge(0,0,0,1,1,1);
		Edge e2 = new Edge(0,0,0,0,10,0);
		try{
			assertTrue(e.getPointFromItsX(0.5).equals(new Point(0.5, 0.5,0.5)));
			assertTrue(e.getPointFromItsX(0.1).equals(new Point(0.1, 0.1,0.1)));
			assertTrue(e.getPointFromItsX(0.2).equals(new Point(0.2, 0.2,0.2)));
			assertTrue(e.getPointFromItsX(0.7).equals(new Point(0.7, 0.7,0.7)));
			assertNull(e2.getPointFromItsX(8));
		} catch (DelaunayError d){
			System.out.println(d.getMessage());
		}
	}

	/**
	 * This test checks the results returned by the method intersects of the class
	 * Edge.
         * The method also checks that the intersection points are well computed.
	 */
	public void testEdgesIntersection() throws DelaunayError{
		Edge e1 = new Edge(4,4,0,8,8,0);
		Edge e2 = new Edge(8,4,0,4,8,0);
                Element intersection;
		assertTrue(e1.intersects(e2)==1);
                intersection = e1.getIntersection(e2);
                assertTrue(intersection.equals(new Point(6,6,0)));


		e2 = new Edge(4,4,0,2,2,0);
		assertTrue(e1.intersects(e2)==3);
                intersection = e1.getIntersection(e2);
                assertTrue(intersection.equals(new Point(4,4,0)));


		e2 = new Edge(4,4,0,20,20,0);
		assertTrue(e1.intersects(e2)==4);
                intersection = e1.getIntersection(e2);
                assertTrue(intersection.equals(new Edge(4,4,0,8,8,0)));


		e2 = new Edge(10,10,0,6,6,0);
		assertTrue(e1.intersects(e2)==4);
                intersection = e1.getIntersection(e2);
                assertTrue(intersection.equals(new Edge(6,6,0,8,8,0)));


		e2 = new Edge(6,4,0,10,8,0);
		assertTrue(e1.intersects(e2)==2);


		e2 = new Edge(0,0,0,1,-4,0);
		assertTrue(e1.intersects(e2)==0);
		assertNull(e1.getIntersection(e2));


		e2 = new Edge(8,8,0,1,-4,0);
		assertTrue(e1.intersects(e2)==3);
                intersection = e1.getIntersection(e2);
                assertTrue(intersection.equals(new Point(8,8,0)));


		e2 = new Edge(6,6,0,1,-4,0);
		assertTrue(e1.intersects(e2)==1);

                //some tests for vertical edges
		e1 = new Edge(4,4,0,4,8,0);
		e2 = new Edge(4,4,0,4,2,0);
		assertTrue(e1.intersects(e2)==3);
		assertTrue(e1.getIntersection(e2).equals(new Point(4,4,0)));


		e2 = new Edge(4,8,0,4,10,0);
		assertTrue(e1.intersects(e2)==3);


		e2 = new Edge(4,6,0,4,10,0);
		assertTrue(e1.intersects(e2)==4);
		assertTrue(e1.getIntersection(e2).equals(new Edge(4,6,0,4,8,0)));


		e2 = new Edge(2,4,0,2,10,0);
		assertTrue(e1.intersects(e2)==2);
		assertNull(e1.getIntersection(e2));


		e2 = new Edge(4,9,0,4,10,0);
		assertTrue(e1.intersects(e2)==2);


		e2 = new Edge(9,9,0,10,10,0);
		assertTrue(e1.intersects(e2)==0);
		assertNull(e1.getIntersection(e2));


		e2 = new Edge(4,0,0,4,10,0);
		assertTrue(e1.intersects(e2)==4);
		assertTrue(e1.getIntersection(e2).equals(new Edge(4,4,0,4,8,0)));


		e2 = new Edge(4,8,0,10,12,0);
		assertTrue(e1.intersects(e2)==3);


		e2 = new Edge(9,9,0,10,10,0);
		assertTrue(e1.intersects(e2)==0);


                //tests for horizontal edges
		e1 = new Edge(4,4,0,8,4,0);
		e2 = new Edge(4,4,0,4,2,0);
		assertTrue(e1.intersects(e2)==3);


		e2 = new Edge(5,3,0,5,8,0);
		assertTrue(e1.intersects(e2)==1);


		e2 = new Edge(9,4,0,0,4,0);
		assertTrue(e1.intersects(e2)==4);
		assertTrue(e1.getIntersection(e2).equals(new Edge(4,4,0,8,4,0)));

		e2 = new Edge(8,4,0,10,4,0);
		assertTrue(e1.intersects(e2)==3);


		e2 = new Edge(9,4,0,10,4,0);
		assertTrue(e1.intersects(e2)==2);


		e2 = new Edge(4,4,0,8,7,0);
		assertTrue(e1.intersects(e2)==3);


		e2 = new Edge(4,3,0,8,5,0);
		assertTrue(e1.intersects(e2)==1);


		e2 = new Edge(4,3,0,8,3,0);
		assertTrue(e1.intersects(e2)==2);

		e1 = new Edge(90.63238154151456,43.20221085741341,41.857266329284386,91.81204048255302,43.45596499572569,38.49821580890875);
		e2 = new Edge(91.81349681622754,43.456278264827304,23.46029784229816,94.75494831679701,44.08900817086041,30.11835638671625);
		double d1 = (e1.getPointLeft().getY() - e1.getPointRight().getY())/(e1.getPointLeft().getX() - e1.getPointRight().getX());
		double d2 = (e2.getPointLeft().getY() - e2.getPointRight().getY())/(e2.getPointLeft().getX() - e2.getPointRight().getX());
		System.out.println("distance left - right : " + e1.getPointRight().squareDistance2D(e2.getPointLeft()));
		System.out.println("d1 : "+d1);
		System.out.println("d2 : "+d2);
		Element haha = e1.getIntersection(e2);
		System.out.println(haha);
		System.out.println("e1 inter e2 : "+e1.intersects(e2));
		System.out.println("e2 inter e1 : "+e2.intersects(e1));
		assertTrue(e1.intersects(e2)==0);
		assertTrue(e2.intersects(e1)==0);
	}

	/**
	 * This method check that the vertical sort defined in Edge works well
	 */
	public void testVerticalSort() throws DelaunayError{
		Edge e1 = new Edge(0,0,0,2,2,2);
		Edge e2 = new Edge(0,0,0,2,2,2);
		assertTrue(e1.verticalSort(e2, 1)==0);

		e1 = new Edge(0,0,0,2,3,2);
		e2 = new Edge(0,0,0,2,2,2);
		assertTrue(e1.verticalSort(e2, 1)==1);
		assertTrue(e1.verticalSort(e2, 0)==1);

		e1 = new Edge(1,8,0,2,3,2);
		e2 = new Edge(0,0,0,2,2,2);
		assertTrue(e1.verticalSort(e2, 1)==1);
		assertTrue(e1.verticalSort(e2, 0)==1);

		e1 = new Edge(0,0,0,3,3,2);
		e2 = new Edge(0,0,0,2,2,2);
		assertTrue(e1.verticalSort(e2, 1)==1);
		assertTrue(e1.verticalSort(e2, 0)==1);

		e1 = new Edge(-1,-1,0,2,2,2);
		e2 = new Edge(0,0,0,2,2,2);
		assertTrue(e1.verticalSort(e2, 1)==-1);
		assertTrue(e1.verticalSort(e2, 0)==-1);
	}

	/**
	 * This method check that the vertical sort defined in Edge works well,
	 * using the VerticalComarator class.
	 */
	public void testVerticalComparison() throws DelaunayError{
		VerticalComparator comp = new VerticalComparator(1);
		assertTrue(comp.getAbs()==1);
		Edge e1 = new Edge(0,0,0,2,2,2);
		Edge e2 = new Edge(0,0,0,2,2,2);
		assertTrue(comp.compare(e1, e2)==0);

		e1 = new Edge(0,0,0,2,3,2);
		e2 = new Edge(0,0,0,2,2,2);
		comp.setAbs(1);
		assertTrue(comp.compare(e1, e2)==1);
		comp.setAbs(0);
		assertTrue(comp.getAbs()==0);
		assertTrue(comp.compare(e1, e2)==1);
		assertTrue(comp.compare(e2, e1)==-1);

		e1 = new Edge(1,8,0,2,3,2);
		e2 = new Edge(0,0,0,2,2,2);
		comp.setAbs(1);
		assertTrue(comp.compare(e1, e2)==1);
		comp.setAbs(0);
		assertTrue(comp.compare(e1, e2)==1);
		assertTrue(comp.compare(e2, e1)==-1);

		e1 = new Edge(0,0,0,3,3,2);
		e2 = new Edge(0,0,0,2,2,2);
		comp.setAbs(1);
		assertTrue(comp.compare(e1, e2)==1);
		comp.setAbs(0);
		assertTrue(comp.compare(e1, e2)==1);
		assertTrue(comp.compare(e2, e1)==-1);

		e1 = new Edge(-1,-1,0,2,2,2);
		e2 = new Edge(0,0,0,2,2,2);
		comp.setAbs(1);
		assertTrue(comp.compare(e1, e2)==-1);
		assertTrue(comp.compare(e2, e1)==1);
		comp.setAbs(0);
		assertTrue(comp.compare(e1, e2)==-1);


		e2 = new Edge(8,0,0,8,2,2);
		assertTrue(comp.compare(e1, e2)==-2);
	}
        
	/**
	 * Checks whether edges are vertical or not.
	 */
	public void testVertical(){
		Edge e1 = new Edge(14.815045543900215,70.25119472533194,33.92748531499323,14.815093546085139,70.25101901938166,33.92739651606882);
		assertFalse(e1.isVertical());

		e1=new Edge(0,0,0,0,5,0);
		assertTrue(e1.isVertical());

		e1=new Edge(0,0,0,0.00000009,5,0);
		assertTrue(e1.isVertical());
	}

	/**
	 * tests the initialization of an edge.
	 */
	public void testInit(){
		Edge edge = new Edge();
		assertNull(edge.getStart());
		assertNull(edge.getEnd());
		assertNull(edge.getLeft());
		assertNull(edge.getRight());
		assertTrue(edge.getIndicator()==0);
	}

	/**
	 * Tests the methods who retrieve the points of the edge.
	 */
	public void testGetPoint() throws DelaunayError{
		Edge edge = new Edge(1,2,0,4,9,5);
		assertTrue(edge.point(0).equals(new Point(1,2,0)));
		assertTrue(edge.point(1).equals(new Point(4,9,5)));
		assertTrue(edge.point(42).equals(new Point(4,9,5)));

	}

	/**
	 * Tests that we are able to effectively set start and end point of an edge
	 */
	public void testSetStartEnd() throws DelaunayError{
		Edge edge = new Edge(0,0,0,2,2,5);
		edge.setStartPoint(new Point(0.5,0.5,0));
		assertEquals(edge, new Edge(0.5,0.5,0,2,2,5));
		edge.setStartPoint(new Point(0,0,0));
		assertEquals(edge, new Edge(0,0,0,2,2,5));
		edge.setEndPoint(new Point(3,3,4));
		assertEquals(edge, new Edge(0,0,0,3,3,4));
		edge.setEndPoint(new Point(4,4,5));
		assertEquals(edge, new Edge(0,0,0,4,4,5));
	}

	/**
	 * Test the computing of this edge's length
	 */
	public void testLength() throws DelaunayError{
		Edge edge = new Edge(0,0,0,2,2,2);
		assertEquals(edge.getSquared2DLength(),8.0);
		assertEquals(edge.getSquared3DLength(),12.0);
		assertEquals(edge.get2DLength(),2*Math.sqrt(2), Tools.EPSILON);
		assertEquals(edge.get3DLength(),2*Math.sqrt(3),Tools.EPSILON);
	}

	/**
	 * Tests the methods which check that a point or a coordinate lies on
	 * an edge or not.
	 * @throws DelaunayError
	 */
	public void testContainsPoint() throws DelaunayError{
		Edge edge = new Edge(0,0,0,4,0,0);
		Point pt = new Point(2,0,0);
		assertTrue(edge.contains(pt));
		assertTrue(edge.contains(new Coordinate(2,0,0)));
		assertTrue(edge.contains(new Point(2,0.000000001,0)));
		assertFalse(edge.contains(new Point(2,2,2)));
	}

	/**
	 * Test the method isColinear
	 * @throws DelaunayError
	 */
	public void testIsColinear() throws DelaunayError {
		Edge edge = new Edge (0,0,0,2,2,0);
		Point pt = new Point (3,3,0);
		assertTrue(edge.isColinear(pt));
		pt = new Point (3,3.000000000000001,0);
		assertTrue(edge.isColinear(pt));
		pt = new Point (3,8,0);
		assertFalse(edge.isColinear(pt));

	}

	/**
	 * Tests that we are able to retrieve the triangle linked to an edge that
	 * is opposite to the one we already know.
	 * @throws DelaunayError
	 */
	public void testGetOhterTriangle() throws DelaunayError{
		Edge edgeb = new Edge (4,4,0,2,2,0);
		Edge et11b = new Edge(4,4,0,0,2,0);
		Edge et12b = new Edge(2,2,0,0,2,0);
		Edge edge = new Edge(0,0,0,2,2,0);
		DelaunayTriangle t1b = new DelaunayTriangle(edgeb, et11b, et12b);
		assertNull(edge.getOtherTriangle(t1b));
		assertNull(edge.getOtherTriangle(null));
		Edge et11 = new Edge(0,0,0,0,2,0);
		Edge et12 = new Edge(2,2,0,0,2,0);
		DelaunayTriangle t1 = new DelaunayTriangle(edge, et11, et12);
		Edge et21 = new Edge(0,0,0,2,0,0);
		Edge et22 = new Edge(2,2,0,2,0,0);
		DelaunayTriangle t2 = new DelaunayTriangle(edge, et21, et22);
		edge.setLeft(t2);
		edge.setRight(t1);
		assertTrue(edge.getLeft().equals(new DelaunayTriangle(edge, et21, et22)));
		assertTrue(edge.getRight().equals(new DelaunayTriangle(edge, et11, et12)));
		assertTrue(edge.getOtherTriangle(t2).equals(t1));
		assertTrue(edge.getOtherTriangle(t1).equals(t2));
		assertNull(edge.getOtherTriangle(t1b));
		assertNull(edge.getOtherTriangle(null));
	}

	/**
	 * Tests that the swap operation on edges works well.
	 * @throws DelaunayError
	 */
	public void testSwapPoints() throws DelaunayError{
		Edge ed = new Edge(0,4,0,4,4,0);
		DelaunayTriangle tri = new DelaunayTriangle(ed, new Edge(4,4,0,2,0,0), new Edge(2,0,0,0,4,0));
		DelaunayTriangle tribis = new DelaunayTriangle(ed, new Edge(4,4,0,2,8,0), new Edge(2,8,0,0,4,0));
		assertTrue(ed.getStartPoint().equals(new Point(0,4,0)));
		assertTrue(ed.getEndPoint().equals(new Point(4,4,0)));
		assertTrue(tri.equals(ed.getRight()));
		assertTrue(tribis.equals(ed.getLeft()));
		ed.swap();
		assertTrue(ed.getEndPoint().equals(new Point(0,4,0)));
		assertTrue(ed.getStartPoint().equals(new Point(4,4,0)));
		assertTrue(tribis.equals(ed.getRight()));
		assertTrue(tri.equals(ed.getLeft()));
	}

	/**
	 * Basic test to check if a point is inside an Edge or not.
	 * @throws DelaunayError
	 */
	public void testIsInside() throws DelaunayError {
		Edge ed = new Edge(0,0,0,4,4,0);
		Point p = new Point(2,2,0);
		assertTrue(ed.isInside(p));
		p = new Point(8,3,0);
		assertFalse(ed.isInside(p));
		
	}

	/**
	 * Test if two edges share the same points.
	 * @throws DelaunayError
	 */
	public void testHaveSamePoint() throws DelaunayError {
		Edge ed = new Edge(0,0,0,4,4,0);
		assertTrue(ed.haveSamePoint(new Edge(0,0,0,4,4,0)));
		assertFalse(ed.haveSamePoint(new Edge(0,4,0,4,4,0)));
		assertFalse(ed.haveSamePoint(new Edge(0,0,0,0,4,0)));
		assertFalse(ed.haveSamePoint(new Edge(0,4,0,0,3,0)));
	}

	public void testGetBarycenter() throws DelaunayError {
		Edge ed = new Edge(0,0,0,4,6,8);
		Point pt = new Point (2,3,4);
		assertTrue(pt.equals(ed.getBarycenter()));
	}
}
