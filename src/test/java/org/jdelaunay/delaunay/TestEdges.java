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
import java.util.HashMap;

/**
 * Tests some of the methods defined in DEdge.
 * @author alexis
 */
public class TestEdges extends BaseUtility {

        public void testAlternativeConstructor() throws DelaunayError {
              DEdge e1 = new DEdge(0,0,0,5,3,0);
              e1.setProperty(665);
              e1.setLocked(true);
              DEdge e2 = new DEdge(e1);
              assertTrue(e2.equals(new DEdge(0,0,0,5,3,0)));
              assertTrue(e2.isLocked());
              assertTrue(e2.getProperty()==665);
        }
        
        /**
         * Some tests related to the getZOnEdge method
         * @throws DelaunayError 
         */
        public void testZInterpolation() throws DelaunayError {
                DEdge e1 = new DEdge(0,0,0,6,0,6);
                double ret = e1.getZOnEdge(new DPoint(3,0,0));
                assertEquals(3.0, ret);
                e1 = new DEdge(0,0,0,6,6,6);
                ret = e1.getZOnEdge(new DPoint(3,3,0));
                assertEquals(3.0, ret);
                e1 = new DEdge(0,0,0,0,6,6);
                ret = e1.getZOnEdge(new DPoint(0,3,0));
                assertEquals(3.0, ret);
                ret = e1.getZOnEdge(new DPoint(3,3,0));
                assertTrue(Double.isNaN(ret));
        }
        
	/**
	 * test that  getPointfromItsX retrieves the points of absciss X from the
	 * line associated to the edge
	 */
	public void testGetPointsFromX()  throws DelaunayError{
		DEdge e = new DEdge(0,0,0,1,1,1);
		DEdge e2 = new DEdge(0,0,0,0,10,0);
		try{
			assertTrue(e.getPointFromItsX(0.5).equals(new DPoint(0.5, 0.5,0.5)));
			assertTrue(e.getPointFromItsX(0.1).equals(new DPoint(0.1, 0.1,0.1)));
			assertTrue(e.getPointFromItsX(0.2).equals(new DPoint(0.2, 0.2,0.2)));
			assertTrue(e.getPointFromItsX(0.7).equals(new DPoint(0.7, 0.7,0.7)));
			assertNull(e2.getPointFromItsX(8));
                        assertEquals(new DPoint(0,0,0), e2.getPointFromItsX(0));
		} catch (DelaunayError d){
			System.out.println(d.getMessage());
		}
	}

	/**
	 * This test checks the results returned by the method intersects of the class
	 * DEdge.
         * The method also checks that the intersection points are well computed.
	 */
	public void testEdgesIntersection() throws DelaunayError{
		DEdge e1 = new DEdge(4,4,0,8,8,0);
		DEdge e2 = new DEdge(8,4,0,4,8,0);
                Element intersection;
		assertTrue(e1.intersects(e2)==1);
                intersection = e1.getIntersection(e2);
                assertTrue(intersection.equals(new DPoint(6,6,0)));


		e2 = new DEdge(4,4,0,2,2,0);
		assertTrue(e1.intersects(e2)==3);
                intersection = e1.getIntersection(e2);
                assertTrue(intersection.equals(new DPoint(4,4,0)));


		e2 = new DEdge(4,4,0,20,20,0);
		assertTrue(e1.intersects(e2)==4);
                intersection = e1.getIntersection(e2);
                assertTrue(intersection.equals(new DEdge(4,4,0,8,8,0)));


		e2 = new DEdge(10,10,0,6,6,0);
		assertTrue(e1.intersects(e2)==4);
                intersection = e1.getIntersection(e2);
                assertTrue(intersection.equals(new DEdge(6,6,0,8,8,0)));


		e2 = new DEdge(6,4,0,10,8,0);
		assertTrue(e1.intersects(e2)==2);


		e2 = new DEdge(0,0,0,1,-4,0);
		assertTrue(e1.intersects(e2)==0);
		assertNull(e1.getIntersection(e2));


		e2 = new DEdge(8,8,0,1,-4,0);
		assertTrue(e1.intersects(e2)==3);
                intersection = e1.getIntersection(e2);
                assertTrue(intersection.equals(new DPoint(8,8,0)));


		e2 = new DEdge(6,6,0,1,-4,0);
		assertTrue(e1.intersects(e2)==1);

                //some tests for vertical edges
		e1 = new DEdge(4,4,0,4,8,0);
		e2 = new DEdge(4,4,0,4,2,0);
		assertTrue(e1.intersects(e2)==3);
		assertTrue(e1.getIntersection(e2).equals(new DPoint(4,4,0)));


		e2 = new DEdge(4,8,0,4,10,0);
		assertTrue(e1.intersects(e2)==3);


		e2 = new DEdge(4,6,0,4,10,0);
		assertTrue(e1.intersects(e2)==4);
		assertTrue(e1.getIntersection(e2).equals(new DEdge(4,6,0,4,8,0)));


		e2 = new DEdge(2,4,0,2,10,0);
		assertTrue(e1.intersects(e2)==2);
		assertNull(e1.getIntersection(e2));


		e2 = new DEdge(4,9,0,4,10,0);
		assertTrue(e1.intersects(e2)==2);


		e2 = new DEdge(9,9,0,10,10,0);
		assertTrue(e1.intersects(e2)==0);
		assertNull(e1.getIntersection(e2));


		e2 = new DEdge(4,0,0,4,10,0);
		assertTrue(e1.intersects(e2)==4);
		assertTrue(e1.getIntersection(e2).equals(new DEdge(4,4,0,4,8,0)));


		e2 = new DEdge(4,8,0,10,12,0);
		assertTrue(e1.intersects(e2)==3);


		e2 = new DEdge(9,9,0,10,10,0);
		assertTrue(e1.intersects(e2)==0);


                //tests for horizontal edges
		e1 = new DEdge(4,4,0,8,4,0);
		e2 = new DEdge(4,4,0,4,2,0);
		assertTrue(e1.intersects(e2)==3);


		e2 = new DEdge(5,3,0,5,8,0);
		assertTrue(e1.intersects(e2)==1);


		e2 = new DEdge(9,4,0,0,4,0);
		assertTrue(e1.intersects(e2)==4);
		assertTrue(e1.getIntersection(e2).equals(new DEdge(4,4,0,8,4,0)));

		e2 = new DEdge(8,4,0,10,4,0);
		assertTrue(e1.intersects(e2)==3);


		e2 = new DEdge(9,4,0,10,4,0);
		assertTrue(e1.intersects(e2)==2);


		e2 = new DEdge(4,4,0,8,7,0);
		assertTrue(e1.intersects(e2)==3);


		e2 = new DEdge(4,3,0,8,5,0);
		assertTrue(e1.intersects(e2)==1);


		e2 = new DEdge(4,3,0,8,3,0);
		assertTrue(e1.intersects(e2)==2);

		e1 = new DEdge(90.63238154151456,43.20221085741341,41.857266329284386,91.81204048255302,43.45596499572569,38.49821580890875);
		e2 = new DEdge(91.81349681622754,43.456278264827304,23.46029784229816,94.75494831679701,44.08900817086041,30.11835638671625);
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
	 * This method check that the vertical sort defined in DEdge works well
	 */
	public void testVerticalSort() throws DelaunayError{
		DEdge e1 = new DEdge(0,0,0,2,2,2);
		DEdge e2 = new DEdge(0,0,0,2,2,2);
		assertTrue(e1.verticalSort(e2, 1)==0);

		e1 = new DEdge(0,0,0,2,3,2);
		e2 = new DEdge(0,0,0,2,2,2);
		assertTrue(e1.verticalSort(e2, 1)==1);
		assertTrue(e1.verticalSort(e2, 0)==1);

		e1 = new DEdge(1,8,0,2,3,2);
		e2 = new DEdge(0,0,0,2,2,2);
		assertTrue(e1.verticalSort(e2, 1)==1);
		assertTrue(e1.verticalSort(e2, 0)==1);

		e1 = new DEdge(0,0,0,3,3,2);
		e2 = new DEdge(0,0,0,2,2,2);
		assertTrue(e1.verticalSort(e2, 1)==1);
		assertTrue(e1.verticalSort(e2, 0)==1);

		e1 = new DEdge(-1,-1,0,2,2,2);
		e2 = new DEdge(0,0,0,2,2,2);
		assertTrue(e1.verticalSort(e2, 1)==-1);
		assertTrue(e1.verticalSort(e2, 0)==-1);
	}

	/**
	 * This method check that the vertical sort defined in DEdge works well,
	 * using the VerticalComarator class.
	 */
	public void testVerticalComparison() throws DelaunayError{
		VerticalComparator comp = new VerticalComparator(1);
		assertTrue(comp.getAbs()==1);
		DEdge e1 = new DEdge(0,0,0,2,2,2);
		DEdge e2 = new DEdge(0,0,0,2,2,2);
		assertTrue(comp.compare(e1, e2)==0);

		e1 = new DEdge(0,0,0,2,3,2);
		e2 = new DEdge(0,0,0,2,2,2);
		comp.setAbs(1);
		assertTrue(comp.compare(e1, e2)==1);
		comp.setAbs(0);
		assertTrue(comp.getAbs()==0);
		assertTrue(comp.compare(e1, e2)==1);
		assertTrue(comp.compare(e2, e1)==-1);

		e1 = new DEdge(1,8,0,2,3,2);
		e2 = new DEdge(0,0,0,2,2,2);
		comp.setAbs(1);
		assertTrue(comp.compare(e1, e2)==1);
		comp.setAbs(0);
		assertTrue(comp.compare(e1, e2)==1);
		assertTrue(comp.compare(e2, e1)==-1);

		e1 = new DEdge(0,0,0,3,3,2);
		e2 = new DEdge(0,0,0,2,2,2);
		comp.setAbs(1);
		assertTrue(comp.compare(e1, e2)==1);
		comp.setAbs(0);
		assertTrue(comp.compare(e1, e2)==1);
		assertTrue(comp.compare(e2, e1)==-1);

		e1 = new DEdge(-1,-1,0,2,2,2);
		e2 = new DEdge(0,0,0,2,2,2);
		comp.setAbs(1);
		assertTrue(comp.compare(e1, e2)==-1);
		assertTrue(comp.compare(e2, e1)==1);
		comp.setAbs(0);
		assertTrue(comp.compare(e1, e2)==-1);


		e2 = new DEdge(8,0,0,8,2,2);
		assertTrue(comp.compare(e1, e2)==-2);
	}
        
	/**
	 * Checks whether edges are vertical or not.
	 */
	public void testVertical() throws DelaunayError{
		DEdge e1 = new DEdge(14.815045543900215,70.25119472533194,33.92748531499323,14.815093546085139,70.25101901938166,33.92739651606882);
		assertFalse(e1.isVertical());

		e1=new DEdge(0,0,0,0,5,0);
		assertTrue(e1.isVertical());

		e1=new DEdge(0,0,0,0.00000009,5,0);
		assertTrue(e1.isVertical());
	}

	/**
	 * tests the initialization of an edge.
	 */
	public void testInit(){
		DEdge edge = new DEdge();
		assertNull(edge.getStart());
		assertNull(edge.getEnd());
		assertNull(edge.getLeft());
		assertNull(edge.getRight());
	}

	/**
	 * Tests that we are able to effectively set start and end point of an edge
	 */
	public void testSetStartEnd() throws DelaunayError{
		DEdge edge = new DEdge(0,0,0,2,2,5);
		edge.setStartPoint(new DPoint(0.5,0.5,0));
		assertEquals(edge, new DEdge(0.5,0.5,0,2,2,5));
		edge.setStartPoint(new DPoint(0,0,0));
		assertEquals(edge, new DEdge(0,0,0,2,2,5));
		edge.setEndPoint(new DPoint(3,3,4));
		assertEquals(edge, new DEdge(0,0,0,3,3,4));
		edge.setEndPoint(new DPoint(4,4,5));
		assertEquals(edge, new DEdge(0,0,0,4,4,5));
	}

	/**
	 * Test the computing of this edge's length
	 */
	public void testLength() throws DelaunayError{
		DEdge edge = new DEdge(0,0,0,2,2,2);
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
		DEdge edge = new DEdge(0,0,0,4,0,0);
		DPoint pt = new DPoint(2,0,0);
		assertTrue(edge.contains(pt));
		assertTrue(edge.contains(new Coordinate(2,0,0)));
		assertTrue(edge.contains(new DPoint(2,0.000000001,0)));
		assertFalse(edge.contains(new DPoint(2,2,2)));
	}

	/**
	 * Test the method isColinear
	 * @throws DelaunayError
	 */
	public void testIsColinear() throws DelaunayError {
		DEdge edge = new DEdge (0,0,0,2,2,0);
		DPoint pt = new DPoint (3,3,0);
		assertTrue(edge.isColinear(pt));
		pt = new DPoint (3,3.000000000000001,0);
		assertTrue(edge.isColinear(pt));
		pt = new DPoint (3,8,0);
		assertFalse(edge.isColinear(pt));
                pt = new DPoint (3,3,50);
		assertTrue(edge.isColinear2D(pt));
		pt = new DPoint (3,3.000000000000001,50);
		assertTrue(edge.isColinear2D(pt));
		pt = new DPoint (3,8,50);
		assertFalse(edge.isColinear2D(pt));
                

	}

	/**
	 * Tests that we are able to retrieve the triangle linked to an edge that
	 * is opposite to the one we already know.
	 * @throws DelaunayError
	 */
	public void testGetOtherTriangle() throws DelaunayError{
		DEdge edgeb = new DEdge (4,4,0,2,2,0);
		DEdge et11b = new DEdge(4,4,0,0,2,0);
		DEdge et12b = new DEdge(2,2,0,0,2,0);
		DEdge edge = new DEdge(0,0,0,2,2,0);
		DTriangle t1b = new DTriangle(edgeb, et11b, et12b);
		assertNull(edge.getOtherTriangle(t1b));
		assertNull(edge.getOtherTriangle(null));
		DEdge et11 = new DEdge(0,0,0,0,2,0);
		DEdge et12 = new DEdge(2,2,0,0,2,0);
		DTriangle t1 = new DTriangle(edge, et11, et12);
		DEdge et21 = new DEdge(0,0,0,2,0,0);
		DEdge et22 = new DEdge(2,2,0,2,0,0);
		DTriangle t2 = new DTriangle(edge, et21, et22);
		edge.setLeft(t2);
		edge.setRight(t1);
		assertTrue(edge.getLeft().equals(
                        new DTriangle(
                                new DEdge(0,0,0,2,2,0),
                                new DEdge(0,0,0,2,0,0),
                                new DEdge(2,2,0,2,0,0))));
		assertTrue(edge.getRight().equals(
                        new DTriangle(
                                new DEdge(0,0,0,2,2,0), 
                                new DEdge(0,0,0,0,2,0), 
                                new DEdge(2,2,0,0,2,0))));
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
		DEdge ed = new DEdge(0,4,0,4,4,0);
		DTriangle tri = new DTriangle(ed, new DEdge(4,4,0,2,0,0), new DEdge(2,0,0,0,4,0));
		DTriangle tribis = new DTriangle(ed, new DEdge(4,4,0,2,8,0), new DEdge(2,8,0,0,4,0));
		assertTrue(ed.getStartPoint().equals(new DPoint(0,4,0)));
		assertTrue(ed.getEndPoint().equals(new DPoint(4,4,0)));
		assertTrue(tri.equals(ed.getRight()));
		assertTrue(tribis.equals(ed.getLeft()));
		ed.swap();
		assertTrue(ed.getEndPoint().equals(new DPoint(0,4,0)));
		assertTrue(ed.getStartPoint().equals(new DPoint(4,4,0)));
		assertTrue(tribis.equals(ed.getRight()));
		assertTrue(tri.equals(ed.getLeft()));
	}

	/**
	 * Basic test to check if a point is inside an DEdge or not.
	 * @throws DelaunayError
	 */
	public void testIsInside() throws DelaunayError {
		DEdge ed = new DEdge(0,0,0,4,4,0);
		DPoint p = new DPoint(2,2,0);
		assertTrue(ed.isInside(p));
		p = new DPoint(8,3,0);
		assertFalse(ed.isInside(p));
                ed = new DEdge(2,0,0,2,8,0);
                assertTrue(ed.isInside(new DPoint(2,4,0)));
                assertFalse(ed.isInside(new DPoint(2,15,0)));
                assertFalse(ed.isInside(new DPoint(3,4,0)));
                ed = new DEdge(2,0,0,4,0,0);
                assertTrue(ed.isInside(new DPoint(3,0,0)));
		
	}

	/**
	 * Test if two edges share the same points.
	 * @throws DelaunayError
	 */
	public void testHaveSamePoint() throws DelaunayError {
		DEdge ed = new DEdge(0,0,0,4,4,0);
		assertTrue(ed.haveSamePoint(new DEdge(0,0,0,4,4,0)));
		assertFalse(ed.haveSamePoint(new DEdge(0,4,0,4,4,0)));
		assertFalse(ed.haveSamePoint(new DEdge(0,0,0,0,4,0)));
		assertFalse(ed.haveSamePoint(new DEdge(0,4,0,0,3,0)));
	}

	public void testGetBarycenter() throws DelaunayError {
		DEdge ed = new DEdge(0,0,0,4,6,8);
		DPoint pt = new DPoint (2,3,4);
		assertTrue(pt.equals(ed.getBarycenter()));
	}

	public void testShared() throws DelaunayError{
		DEdge ed = new DEdge(0,0,0,2,2,0);
		assertFalse(ed.isShared());
		ed.setShared(true);
		assertTrue(ed.isShared());
	}

	public void testBoundingBox() throws DelaunayError{
		DEdge ed = new DEdge(0,0,0,2,2,0);
		BoundaryBox bb = ed.getBoundingBox();
		assertTrue(bb.getPoints().contains(new DPoint(0,0,0)));
		assertTrue(bb.getPoints().contains(new DPoint(2,2,0)));
	}

	public void testGetUpperPoint() throws DelaunayError {
		DPoint p1 = new DPoint(0,5,7);
		DPoint p2 = new DPoint(9,6,3);
		DEdge d = new DEdge(p2, p1);
		assertTrue(d.getUpperPoint().equals(p1));
		assertTrue(d.getUpperPoint()==p1);
	}

	public void testGetMiddle() throws DelaunayError {
		DPoint p1 = new DPoint(2,4,10);
		DPoint p2 = new DPoint(8,0,-2);
		DEdge d1 = new DEdge(p1, p2);
		DEdge d2 = new DEdge(p2, p1);
		assertTrue(d1.getMiddle().equals(new DPoint(5,2,4)));
		assertTrue(d1.getMiddle().equals(d2.getMiddle()));
	}

	public void testIntersectionAlmostVerticalEdges() throws DelaunayError {
		DEdge e2 = new DEdge (300641.29999999993, 2260093.5, 0.0, 300641.3, 2260119.5, 0.0);
		DEdge e1 = new DEdge (300641.29999999993, 2260093.5, 0.0, 300641.3, 2260113.9000000004, 0.0);
		Element elt = e2.getIntersection(e1);
		assertTrue(elt instanceof DEdge);
		assertTrue(((DEdge) elt).getPointLeft().getX()==300641.29999999993);
		assertTrue(((DEdge) elt).getPointLeft().getY()==2260093.5);
		assertTrue(((DEdge) elt).getPointRight().getX()==300641.3);
		assertTrue(((DEdge) elt).getPointRight().getY()==2260113.9000000004);
	}

	public void testPrecisionNearPoint() throws DelaunayError {
		DEdge e1 = new DEdge (300641.29999999993, 2260093.5, 0.0, 300641.70000000007, 2260059.000000001, 0.0);
		DEdge e2 = new DEdge (300640.3, 2260085.2, 0.0, 300641.4000000001, 2260085.4000000013, 0.0);
		Element  elt = e1.getIntersection(e2);
		assertTrue(elt instanceof DPoint);
	}

	public void testIntersectionParallel() throws DelaunayError {
		DEdge e1 = new DEdge(0,0,0,5,0,0);
		DEdge e2 = new DEdge(0,0,0,4,0.0000000001,0);
		Element elt = e2.getIntersection(e1);
		assertTrue(elt instanceof DEdge);
	}

	public void testIsEncroached() throws DelaunayError {
		DPoint p1 = new DPoint(0,3,0);
		DPoint p2 = new DPoint(6,0,0);
		DPoint p3 = new DPoint(3,0,0);
		DEdge e1 = new DEdge(p1, p2);
		DEdge e2 = new DEdge(p2, p3);
		DEdge e3 = new DEdge(p3, p1);
		e1.setLocked(true);
		e2.setLocked(true);
		e3.setLocked(true);
		DTriangle t = new DTriangle(e1, e2, e3);
		assertTrue(e1.isEncroached());
		assertFalse(e2.isEncroached());
		assertFalse(e3.isEncroached());
		DTriangle t3 = new DTriangle(e1, new DEdge(6,0,0,4,3,0), new DEdge(4,3,0,0,3,0));
		assertTrue(e1.isEncroached());
		e1.setLocked(false);
		assertFalse(e1.isEncroached());
		DPoint p4 = new DPoint(1,1,0);
		DEdge e4 = new DEdge(p1, p4);
		DEdge e5 = new DEdge(p3, p4);
		DTriangle t2 = new DTriangle(e3, e4, e5);
		assertTrue(e3.isEncroached());
                DEdge ed = new DEdge(8,8,8,6,8,9);
                assertFalse(ed.isEncroached());
	}
        
        public void testIsEncroachedBy() throws DelaunayError {
                DEdge ed = new DEdge(0,3,0,8,3,0);
                assertFalse(ed.isEncroachedBy(new DPoint(0,2,0)));
                assertTrue(ed.isEncroachedBy(new DPoint(4,4,0)));
        }

	public void testNotSeenOverlap() throws DelaunayError {
		DEdge e2 = new DEdge (311785.4, 2251488.2, 25.7, 311792.0, 2251487.1, 25.9);
		DEdge e1 = new DEdge (311784.2, 2251488.4, 25.7, 311792.0, 2251487.1, 25.9);
		Element inter = e1.getIntersection(e2);
		assertNotNull(inter);
	}

	public void testProblematicIntersectionFusion() throws DelaunayError {
		DEdge e1 = new DEdge (299371.8, 2258651.5, 52.4, 299374.2, 2258651.2, 52.4);
		DEdge e2 = new DEdge (299373.0, 2258651.200000001, 0.0, 299379.9, 2258651.200000001, 0.0);
		Element inter = e1.getIntersection(e2);
		assertNotNull(inter);
		e2 = new DEdge (299374.152991453, 2258645.700000001, 0.0, 299374.2, 2258651.2, 52.4);
		inter = e1.getIntersection(e2);
		assertNotNull(inter);
	}

	/**
	 * Test the getDistance method.
	 * @throws DelaunayError
	 */
	public void testGetDistance() throws DelaunayError {
		DEdge e1 = new DEdge (0,0,0,2,2,2);
		DPoint p = new DPoint(1,2,0);
		assertTrue(Math.abs(e1.getDistance2D(p) - Math.sqrt(2)/2)<Tools.EPSILON);
		e1 = new DEdge(0,0,0,0,2,0);
		assertTrue(e1.getDistance2D(p)==1);
	}

	public void testHeight() throws DelaunayError {
		DEdge e1 = new DEdge(0,0,0,5,5,5);
		assertTrue(e1.getHeight()==0);
		e1.setHeight(8);
		assertTrue(e1.getHeight()==8);
	}

	public void testForceTopographicOrientation() throws DelaunayError {
		DEdge e1 = new DEdge(0,0,0,5,5,5);
		assertTrue(e1.getStartPoint().equals(new DPoint(0,0,0)));
		DTriangle d1 = new DTriangle(e1, new DEdge(5,5,5,4,1,0), new DEdge(4,1,0,0,0,0));
		DTriangle d2 = new DTriangle(e1, new DEdge(5,5,5,1,3,0), new DEdge(1,3,0,0,0,0));
		e1.forceTopographicOrientation();
		assertTrue(e1.getEndPoint().equals(new DPoint(0,0,0)));
		assertTrue(e1.getRight().equals(new DTriangle(e1, new DEdge(5,5,5,1,3,0), new DEdge(1,3,0,0,0,0))));
		e1.forceTopographicOrientation();
		assertTrue(e1.getEndPoint().equals(new DPoint(0,0,0)));
		assertTrue(e1.getRight().equals(new DTriangle(e1, new DEdge(5,5,5,1,3,0), new DEdge(1,3,0,0,0,0))));


		
	}

	public void testGetSlope() throws DelaunayError {
		DEdge e = new DEdge(0,0,0,5,0,5);
		assertEquals(e.getSlope(), 1, Tools.EPSILON);
		assertEquals(e.getSlopeInDegree(), 45, Tools.EPSILON);
		assertTrue(e.getDirectionVector().equals(new DPoint(5/Math.sqrt(50),0,5/Math.sqrt(50))));
		e = new DEdge(0,0,0,0,0,5);
		assertTrue(Double.isNaN(e.getSlope()));
	}

        public void testRetrieveGoodZ() throws DelaunayError {
                DEdge e1 = new DEdge(0,0,0,5,5,0);
                DEdge e2 = new DEdge(0,5,5,5,0,9);
		HashMap<Integer,Integer> weights = new HashMap<Integer,Integer>();
		weights.put(2, 2);
		weights.put(8, 4);
		weights.put(16, 6);
                e1.setProperty(2);
                e2.setProperty(8);
                DPoint inter = (DPoint) e1.getIntersection(e2,weights);
                assertTrue(inter.getZ()==7);
                e1.setProperty(16);
                inter = (DPoint) e1.getIntersection(e2);
                assertTrue(inter.getZ()==0);
        }

	public void testGetMaxWeight() throws DelaunayError {
		DEdge e1 = new DEdge(0,0,0,2,2,5);
		e1.addProperty(1);
		e1.addProperty(2);
		e1.addProperty(4);
		e1.addProperty(8);
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		map.put(1, 5);
		map.put(2, 10);
		map.put(4, 40);
		map.put(8, 70);
		map.put(16, 20);
		map.put(32, 30);
		map.put(64, 40);
		assertEquals(e1.getMaxWeight(map), 70);
		map.remove(1);
		map.put(1, 80);
		assertEquals(e1.getMaxWeight(map), 80);

	}
        
        public void testDeepSwap() throws DelaunayError {
                DEdge ed = new DEdge(0,0,0,5,5,0);
                DTriangle t1 = new DTriangle(ed,
                        new DEdge(0,0,0,1,5,0),
                        new DEdge(5,5,0,1,5,0));
                DTriangle t2 = new DTriangle(ed,
                        new DEdge(0,0,0,3,-1,0),
                        new DEdge(5,5,0,3,-1,0));
                assertTrue(ed.getStartPoint().equals(new DPoint(0,0,0)));
                assertTrue(ed.getLeft()==t1);
                assertTrue(ed.getRight()==t2);
                ed.deepSwap();
                assertTrue(ed.getStartPoint().equals(new DPoint(0,0,0)));
                assertTrue(ed.getLeft()==t2);
                assertTrue(ed.getRight()==t1);
                assertTrue(ed.getLeft().belongsTo(new DPoint(1,5,0)));
                assertTrue(ed.getRight().belongsTo(new DPoint(3,-1,0)));
        }
        
        public void testForceTriangleSide() throws DelaunayError {
                DEdge ed = new DEdge(0,0,0,5,5,0);
                DTriangle t1 = new DTriangle(ed,
                        new DEdge(0,0,0,1,5,0),
                        new DEdge(5,5,0,1,5,0));
                assertTrue(ed.getLeft() == t1 && ed.getRight()==null);
                t1.setEdge(1, new DEdge(0,0,0,5,1,0));
                t1.setEdge(2, new DEdge(5,5,0,5,1,0));
                ed.forceTriangleSide();
                assertTrue(ed.getLeft() == null && ed.getRight()==t1);
                t1.setEdge(1, new DEdge(0,0,0,1,5,0));
                t1.setEdge(2, new DEdge(5,5,0,1,5,0));
                ed.forceTriangleSide();
                assertTrue(ed.getLeft() == t1 && ed.getRight()==null);
        }
        
        public void testIsOnEdge() throws DelaunayError {
                DEdge ed = new DEdge(0,0,0,5,5,0);
                assertTrue(ed.isOnEdge(new DPoint(3,3,0)));
                assertTrue(ed.isOnEdge(new DPoint(3,3,40)));
                assertFalse(ed.isOnEdge(new DPoint(3,5,0)));
                
        }
        
        public void testTrianglesGoToEdge() throws DelaunayError {
                DEdge e1 = new DEdge(0,0,2,5,5,2);
                DEdge e2 = new DEdge(5,5,2,1,4,5);
                DEdge e3 = new DEdge(1,4,5,0,0,2);
                DEdge e4 = new DEdge(5,5,2,4,1,4);
                DEdge e5 = new DEdge(4,1,4,0,0,2);
                DTriangle t1 = new DTriangle(e1, e2, e3);
                DTriangle t2 = new DTriangle(e1, e4, e5);
                assertTrue(e1.isLeftTriangleGoToEdge());
                assertTrue(e1.isRightTriangleGoToEdge());
                DPoint pt = new DPoint(1,4,0);
                e2.setEndPoint(pt);
                e3.setStartPoint(pt);
                DPoint p = new DPoint(4,1,0);
                e4.setEndPoint(p);
                e5.setStartPoint(p);
                assertFalse(e1.isLeftTriangleGoToEdge());
                assertFalse(e1.isRightTriangleGoToEdge());
                DEdge ed = new DEdge(0,0,8,6,3,4);
                assertFalse(ed.isRightTriangleGoToEdge());
                assertFalse(ed.isLeftTriangleGoToEdge());
        }
        
        public void testSlope() throws DelaunayError {
                DEdge e1 = new DEdge(0,0,2,5,5,2);
                assertTrue(e1.getGradient() == DEdge.FLATSLOPE);
                e1.setEndPoint(new DPoint(5,5,0));
                assertTrue(e1.getGradient() == DEdge.DOWNSLOPE);
                e1.setEndPoint(new DPoint(5,5,4));
                assertTrue(e1.getGradient() == DEdge.UPSLOPE);
        }
        
        public void testAspectSlope() throws DelaunayError {
                DEdge e1 = new DEdge(0,0,0,4,0,0);
                assertEquals(e1.getSlopeAspect(), 90.0, 0.0001);
        }
        
        public void testColinearityCoherence() throws DelaunayError {
                DEdge ed = new DEdge(0,0,0,4,0,0);
                double add = 0.0000000001;
                for(int i =0; i<11; i++){
                        DPoint pt = new DPoint(5,add * i,0);
                        assertTrue(ed.isColinear(pt) == !ed.isLeft(pt));
                }
        }
}
