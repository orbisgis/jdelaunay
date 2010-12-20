/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdelaunay.delaunay;

import java.util.ArrayList;

/**
 * Perform tests on the VerticalList class
 * @author alexis
 */
public class TestVerticalList extends BaseUtility{

	/**
	 * Tests that we change the absciss used to sort the list efficiently.
	 * @throws DelaunayError
	 */
	public void testChangeAbs() throws DelaunayError{
		VerticalList vList = new VerticalList(1);
		vList.addEdge(new Edge(0,0,0,4,4,4));
		vList.addEdge(new Edge(3,3,4,6,6,4));
		vList.addEdge(new Edge(3,1,0,6,1,4));
		vList.addEdge(new Edge(2,5,0,5,8,4));
		vList.addEdge(new Edge(4,3,0,6,1,0));
		assertTrue(vList.get(0).equals(new Edge(3,1,0,6,1,4)));
		assertTrue(vList.get(1).equals(new Edge(0,0,0,4,4,4)));
		assertTrue(vList.get(2).equals(new Edge(3,3,4,6,6,4)));
		assertTrue(vList.get(3).equals(new Edge(2,5,0,5,8,4)));
		assertTrue(vList.get(4).equals(new Edge(4,3,0,6,1,0)));
		vList.setAbs(0);
		assertTrue(vList.get(2).equals(new Edge(3,1,0,6,1,4)));
		assertTrue(vList.get(0).equals(new Edge(0,0,0,4,4,4)));
		assertTrue(vList.get(1).equals(new Edge(3,3,4,6,6,4)));
		assertTrue(vList.get(3).equals(new Edge(2,5,0,5,8,4)));
		assertTrue(vList.get(4).equals(new Edge(4,3,0,6,1,0)));
		vList.setAbs(new Point(5,4,0));
		assertTrue(vList.get(0).equals(new Edge(3,1,0,6,1,4)));
		assertTrue(vList.get(2).equals(new Edge(0,0,0,4,4,4)));
		assertTrue(vList.get(3).equals(new Edge(3,3,4,6,6,4)));
		assertTrue(vList.get(4).equals(new Edge(2,5,0,5,8,4)));
		assertTrue(vList.get(1).equals(new Edge(4,3,0,6,1,0)));
	}

	/**
	 * Tests that a basic insertion keeps the list ordered.
	 * @throws DelaunayError
	 */
	public void testInsertElement() throws DelaunayError{
		VerticalList vList = new VerticalList(1);
		vList.setAbs(new Point(5,4,0));
		vList.addEdge(new Edge(0,0,0,4,4,4));
		vList.addEdge(new Edge(3,3,4,6,6,4));
		vList.addEdge(new Edge(3,1,0,6,1,4));
		vList.addEdge(new Edge(2,5,0,5,8,4));
		vList.addEdge(new Edge(4,3,0,6,1,0));
		assertTrue(vList.get(0).equals(new Edge(3,1,0,6,1,4)));
		assertTrue(vList.get(2).equals(new Edge(0,0,0,4,4,4)));
		assertTrue(vList.get(3).equals(new Edge(3,3,4,6,6,4)));
		assertTrue(vList.get(4).equals(new Edge(2,5,0,5,8,4)));
		assertTrue(vList.get(1).equals(new Edge(4,3,0,6,1,0)));
		assertTrue(vList.size()==5);
		vList.addEdge(new Edge(6,3,0,8,1,0));
		assertTrue(vList.get(2).equals(new Edge(6,3,0,8,1,0)));
		assertTrue(vList.size()==6);
	}

	/**
	 * Performs tests on the remove and removeEdge operations
	 * @throws DelaunayError
	 */
	public void testRemoveElement() throws DelaunayError{
		VerticalList vList = new VerticalList(1);
		vList.setAbs(new Point(5,4,0));
		vList.addEdge(new Edge(0,0,0,4,4,4));
		vList.addEdge(new Edge(3,3,4,6,6,4));
		vList.addEdge(new Edge(3,1,0,6,1,4));
		vList.addEdge(new Edge(2,5,0,5,8,4));
		vList.addEdge(new Edge(4,3,0,6,1,0));
		assertTrue(vList.get(0).equals(new Edge(3,1,0,6,1,4)));
		assertTrue(vList.get(2).equals(new Edge(0,0,0,4,4,4)));
		assertTrue(vList.get(3).equals(new Edge(3,3,4,6,6,4)));
		assertTrue(vList.get(4).equals(new Edge(2,5,0,5,8,4)));
		assertTrue(vList.get(1).equals(new Edge(4,3,0,6,1,0)));
		assertTrue(vList.size()==5);
		vList.removeEdge(new Edge(4,3,0,6,1,0));
		assertTrue(vList.get(0).equals(new Edge(3,1,0,6,1,4)));
		assertTrue(vList.get(1).equals(new Edge(0,0,0,4,4,4)));
		assertTrue(vList.get(2).equals(new Edge(3,3,4,6,6,4)));
		assertTrue(vList.get(3).equals(new Edge(2,5,0,5,8,4)));
		assertTrue(vList.size()==4);
		vList.addEdge(new Edge(4,3,0,6,1,0));
		vList.remove(1);
		assertTrue(vList.get(0).equals(new Edge(3,1,0,6,1,4)));
		assertTrue(vList.get(1).equals(new Edge(0,0,0,4,4,4)));
		assertTrue(vList.get(2).equals(new Edge(3,3,4,6,6,4)));
		assertTrue(vList.get(3).equals(new Edge(2,5,0,5,8,4)));
		assertTrue(vList.size()==4);
		
	}

	/**
	 * Basically tests the constructors
	 * @throws DelaunayError
	 */
	public void testVListCreation() throws DelaunayError{
		VerticalList vList = new VerticalList(1);
		assertTrue(vList.getAbs()==1);
		vList = new VerticalList(new Point(2,1,1));
		assertTrue(vList.getAbs()==2);

	}

	/**
	 * Tests the searchEdge method.
	 * @throws DelaunayError
	 */
	public void testSearchEdge() throws DelaunayError{
		VerticalList vList = new VerticalList(1);
		vList.setAbs(new Point(5,4,0));
		vList.addEdge(new Edge(0,0,0,4,4,4));
		vList.addEdge(new Edge(3,3,4,6,6,4));
		vList.addEdge(new Edge(3,1,0,6,1,4));
		vList.addEdge(new Edge(2,5,0,5,8,4));
		vList.addEdge(new Edge(4,3,0,6,1,0));
		assertEquals(0, vList.searchEdge(new Edge(3,1,0,6,1,4)));
		assertEquals(1, vList.searchEdge(new Edge(4,3,0,6,1,0)));
		assertEquals(2, vList.searchEdge(new Edge(0,0,0,4,4,4)));
		assertEquals(3, vList.searchEdge(new Edge(3,3,4,6,6,4)));
		assertEquals(4, vList.searchEdge(new Edge(2,5,0,5,8,4)));
		assertEquals(-3, vList.searchEdge(new Edge(6,3,0,8,1,0)));
	}

        /**
         * Test the methods that searches the edge that is directly upper to the
         * specified point
         * @throws DelaunayError
         */
        public void testGetUpperEdge() throws DelaunayError{
		VerticalList vList = new VerticalList(1);
		vList.setAbs(new Point(5,4,0));
		vList.addEdge(new Edge(0,0,0,4,4,4));
		vList.addEdge(new Edge(3,3,4,6,6,4));
		vList.addEdge(new Edge(3,1,0,6,1,4));
		vList.addEdge(new Edge(2,5,0,5,8,4));
		vList.addEdge(new Edge(4,3,0,6,1,0));
                Edge upper = vList.getUpperEdge(new Point(4,2,0));
                assertTrue(upper.equals(new Edge(4,3,0,6,1,0)));
                upper = vList.getUpperEdge(new Point(4,4,0));
                assertTrue(upper.equals(new Edge(2,5,0,5,8,4)));
                upper = vList.getUpperEdge(new Point(4,15,0));
                assertNull(upper);
                upper = vList.getUpperEdge(new Point(4,7,0));
                assertNull(upper);

        }

        /**
         * Tests that the retrieval of the edge lower than a given point in a
         * vertical list works well.
         * @throws DelaunayError
         */
        public void testGetLowerEdge() throws DelaunayError{
		VerticalList vList = new VerticalList(1);
		vList.setAbs(new Point(5,4,0));
		vList.addEdge(new Edge(0,0,0,4,4,4));
		vList.addEdge(new Edge(3,3,4,6,6,4));
		vList.addEdge(new Edge(3,1,0,6,1,4));
		vList.addEdge(new Edge(2,5,0,5,8,4));
		vList.addEdge(new Edge(4,3,0,6,1,0));
                Edge lower = vList.getLowerEdge(new Point(4,2,0));
                assertTrue(lower.equals(new Edge(3,1,0,6,1,4)));
                lower = vList.getLowerEdge(new Point(4,0,0));
                assertNull(lower);
                lower = vList.getLowerEdge(new Point(4,4,0));
                assertTrue(lower.equals(new Edge(4,3,0,6,1,0)));
                lower = vList.getLowerEdge(new Point(4,15,0));
                assertTrue(lower.equals(new Edge(2,5,0,5,8,4)));

        }

	/**
	 * tests the method that add a whole list of edges in the vertical list.
	 */
	public void testAddList() throws DelaunayError{
		VerticalList vList = new VerticalList(1);
		vList.setAbs(new Point(5,4,0));
		ArrayList<Edge> edges = new ArrayList<Edge>();
		edges.add(new Edge(0,0,0,4,4,4));
		edges.add(new Edge(3,3,4,6,6,4));
		edges.add(new Edge(3,1,0,6,1,4));
		edges.add(new Edge(2,5,0,5,8,4));
		edges.add(new Edge(4,3,0,6,1,0));
		vList.addEdges(edges);
		assertTrue(vList.size()==5);
		assertTrue(vList.getVerticallySortedEdges().contains(new Edge(0,0,0,4,4,4)));
		assertTrue(vList.getVerticallySortedEdges().contains(new Edge(3,3,4,6,6,4)));
		assertTrue(vList.getVerticallySortedEdges().contains(new Edge(3,1,0,6,1,4)));
		assertTrue(vList.getVerticallySortedEdges().contains(new Edge(2,5,0,5,8,4)));
		assertTrue(vList.getVerticallySortedEdges().contains(new Edge(4,3,0,6,1,0)));
	}

	/**
	 * checks if the method which search for intersection between an edge and
	 * the edges upper and lower than a point works well.
	 */
	public void testIntersectsUpperOrLower() throws DelaunayError{
		VerticalList vList = new VerticalList(1);
		vList.addEdge(new Edge(0,0,0,4,4,4));
		vList.addEdge(new Edge(4,4,4,6,6,4));
		vList.addEdge(new Edge(3,1,0,6,1,4));
		vList.addEdge(new Edge(2,5,0,5,8,4));
		vList.addEdge(new Edge(4,3,0,6,1,0));
		Point pRef = new Point(5,3,0);
		assertTrue(vList.intersectsUpperOrLower(pRef, new Edge(4,2,0,5,3,0)));
		assertTrue(vList.getLastUpperPt().equals(pRef));
		assertTrue(vList.getLastLowerPt().equals(pRef));
		assertTrue(vList.intersectsUpperOrLower(pRef, new Edge(4,2,0,5,3,0)));
                //for the next edge, the upper edge is (0,0,0,4,4,4), not (4,4,4,6,6,6)
                //It the result of the vertical sort. Consequently, we don't have any
                //intersection between (4,6,0,5,3,0) and the edges directly upper and
                //lower than point
		assertFalse(vList.intersectsUpperOrLower(pRef, new Edge(4,6,0,5,3,0)));
		assertFalse(vList.intersectsUpperOrLower(pRef, new Edge(4,6,0,5,3,0)));
                //The intersection occurs with (0,0,0,4,4,4)
		assertTrue(vList.intersectsUpperOrLower(pRef, new Edge(3,4,0,5,3,0)));

	}

	/**
	 * We check that the volatile attributes (lastUpperPt, etc...) are well
	 * removed when changing a valuable information in the list.
	 */
	public void testVolatileAttributes() throws DelaunayError{
		VerticalList vList = new VerticalList(1);
		vList.addEdge(new Edge(0,0,0,4,4,4));
		vList.addEdge(new Edge(4,4,4,6,6,4));
		vList.addEdge(new Edge(3,1,0,6,1,4));
		vList.addEdge(new Edge(2,5,0,5,8,4));
		vList.addEdge(new Edge(4,3,0,6,1,0));
		Point pRef = new Point(5,3,0);
		assertTrue(vList.intersectsUpperOrLower(pRef, new Edge(4,2,0,5,3,0)));
		assertTrue(vList.getLastUpperPt().equals(pRef));
		assertTrue(vList.getLastLowerPt().equals(pRef));
		vList.setAbs(0);
		assertNull(vList.getLastLowerEd());
		assertNull(vList.getLastUpperEd());
		assertNull(vList.getLastLowerPt());
		assertNull(vList.getLastLowerPt());
		vList.intersectsUpperOrLower(pRef, new Edge(4,2,0,5,3,0));
		assertTrue(vList.getLastUpperPt().equals(pRef));
		assertTrue(vList.getLastLowerPt().equals(pRef));
		vList.addEdge(new Edge(10,10,10,11,11,11));
		assertNull(vList.getLastLowerEd());
		assertNull(vList.getLastUpperEd());
		assertNull(vList.getLastLowerPt());
		assertNull(vList.getLastLowerPt());
		vList.intersectsUpperOrLower(pRef, new Edge(4,2,0,5,3,0));
		assertTrue(vList.getLastUpperPt().equals(pRef));
		assertTrue(vList.getLastLowerPt().equals(pRef));
		vList.removeEdge(new Edge(10,10,10,11,11,11));
		assertNull(vList.getLastLowerEd());
		assertNull(vList.getLastUpperEd());
		assertNull(vList.getLastLowerPt());
		assertNull(vList.getLastLowerPt());
		vList.intersectsUpperOrLower(pRef, new Edge(4,2,0,5,3,0));
		assertTrue(vList.getLastUpperPt().equals(pRef));
		assertTrue(vList.getLastLowerPt().equals(pRef));
		ArrayList<Edge> edgeList = new ArrayList<Edge>();
		edgeList.add(new Edge(10,10,10,11,11,11));
		edgeList.add(new Edge(12,12,12,11,11,11));
		vList.addEdges(edgeList);
		assertNull(vList.getLastLowerEd());
		assertNull(vList.getLastUpperEd());
		assertNull(vList.getLastLowerPt());
		assertNull(vList.getLastLowerPt());
		vList.intersectsUpperOrLower(pRef, new Edge(4,2,0,5,3,0));
		assertTrue(vList.getLastUpperPt().equals(pRef));
		assertTrue(vList.getLastLowerPt().equals(pRef));
	}
}
