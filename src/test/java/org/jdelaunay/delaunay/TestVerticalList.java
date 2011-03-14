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
		vList.addEdge(new DEdge(0,0,0,4,4,4));
		vList.addEdge(new DEdge(3,3,4,6,6,4));
		vList.addEdge(new DEdge(3,1,0,6,1,4));
		vList.addEdge(new DEdge(2,5,0,5,8,4));
		vList.addEdge(new DEdge(4,3,0,6,1,0));
		assertTrue(vList.get(0).equals(new DEdge(3,1,0,6,1,4)));
		assertTrue(vList.get(1).equals(new DEdge(0,0,0,4,4,4)));
		assertTrue(vList.get(2).equals(new DEdge(3,3,4,6,6,4)));
		assertTrue(vList.get(3).equals(new DEdge(2,5,0,5,8,4)));
		assertTrue(vList.get(4).equals(new DEdge(4,3,0,6,1,0)));
		vList.setAbs(0);
		assertTrue(vList.get(2).equals(new DEdge(3,1,0,6,1,4)));
		assertTrue(vList.get(0).equals(new DEdge(0,0,0,4,4,4)));
		assertTrue(vList.get(1).equals(new DEdge(3,3,4,6,6,4)));
		assertTrue(vList.get(3).equals(new DEdge(2,5,0,5,8,4)));
		assertTrue(vList.get(4).equals(new DEdge(4,3,0,6,1,0)));
		vList.setAbs(new DPoint(5,4,0));
		assertTrue(vList.get(0).equals(new DEdge(3,1,0,6,1,4)));
		assertTrue(vList.get(2).equals(new DEdge(0,0,0,4,4,4)));
		assertTrue(vList.get(3).equals(new DEdge(3,3,4,6,6,4)));
		assertTrue(vList.get(4).equals(new DEdge(2,5,0,5,8,4)));
		assertTrue(vList.get(1).equals(new DEdge(4,3,0,6,1,0)));
	}

	/**
	 * Tests that a basic insertion keeps the list ordered.
	 * @throws DelaunayError
	 */
	public void testInsertElement() throws DelaunayError{
		VerticalList vList = new VerticalList(1);
		vList.setAbs(new DPoint(5,4,0));
		vList.addEdge(new DEdge(0,0,0,4,4,4));
		vList.addEdge(new DEdge(3,3,4,6,6,4));
		vList.addEdge(new DEdge(3,1,0,6,1,4));
		vList.addEdge(new DEdge(2,5,0,5,8,4));
		vList.addEdge(new DEdge(4,3,0,6,1,0));
		assertTrue(vList.get(0).equals(new DEdge(3,1,0,6,1,4)));
		assertTrue(vList.get(2).equals(new DEdge(0,0,0,4,4,4)));
		assertTrue(vList.get(3).equals(new DEdge(3,3,4,6,6,4)));
		assertTrue(vList.get(4).equals(new DEdge(2,5,0,5,8,4)));
		assertTrue(vList.get(1).equals(new DEdge(4,3,0,6,1,0)));
		assertTrue(vList.size()==5);
		vList.addEdge(new DEdge(6,3,0,8,1,0));
		assertTrue(vList.get(2).equals(new DEdge(6,3,0,8,1,0)));
		assertTrue(vList.size()==6);
	}

	/**
	 * Performs tests on the remove and removeEdge operations
	 * @throws DelaunayError
	 */
	public void testRemoveElement() throws DelaunayError{
		VerticalList vList = new VerticalList(1);
		vList.setAbs(new DPoint(5,4,0));
		vList.addEdge(new DEdge(0,0,0,4,4,4));
		vList.addEdge(new DEdge(3,3,4,6,6,4));
		vList.addEdge(new DEdge(3,1,0,6,1,4));
		vList.addEdge(new DEdge(2,5,0,5,8,4));
		vList.addEdge(new DEdge(4,3,0,6,1,0));
		assertTrue(vList.get(0).equals(new DEdge(3,1,0,6,1,4)));
		assertTrue(vList.get(2).equals(new DEdge(0,0,0,4,4,4)));
		assertTrue(vList.get(3).equals(new DEdge(3,3,4,6,6,4)));
		assertTrue(vList.get(4).equals(new DEdge(2,5,0,5,8,4)));
		assertTrue(vList.get(1).equals(new DEdge(4,3,0,6,1,0)));
		assertTrue(vList.size()==5);
		vList.removeEdge(new DEdge(4,3,0,6,1,0));
		assertTrue(vList.get(0).equals(new DEdge(3,1,0,6,1,4)));
		assertTrue(vList.get(1).equals(new DEdge(0,0,0,4,4,4)));
		assertTrue(vList.get(2).equals(new DEdge(3,3,4,6,6,4)));
		assertTrue(vList.get(3).equals(new DEdge(2,5,0,5,8,4)));
		assertTrue(vList.size()==4);
		vList.addEdge(new DEdge(4,3,0,6,1,0));
		vList.remove(1);
		assertTrue(vList.get(0).equals(new DEdge(3,1,0,6,1,4)));
		assertTrue(vList.get(1).equals(new DEdge(0,0,0,4,4,4)));
		assertTrue(vList.get(2).equals(new DEdge(3,3,4,6,6,4)));
		assertTrue(vList.get(3).equals(new DEdge(2,5,0,5,8,4)));
		assertTrue(vList.size()==4);
		
	}

	/**
	 * Basically tests the constructors
	 * @throws DelaunayError
	 */
	public void testVListCreation() throws DelaunayError{
		VerticalList vList = new VerticalList(1);
		assertTrue(vList.getAbs()==1);
		vList = new VerticalList(new DPoint(2,1,1));
		assertTrue(vList.getAbs()==2);

	}

	/**
	 * Tests the searchEdge method.
	 * @throws DelaunayError
	 */
	public void testSearchEdge() throws DelaunayError{
		VerticalList vList = new VerticalList(1);
		vList.setAbs(new DPoint(5,4,0));
		vList.addEdge(new DEdge(0,0,0,4,4,4));
		vList.addEdge(new DEdge(3,3,4,6,6,4));
		vList.addEdge(new DEdge(3,1,0,6,1,4));
		vList.addEdge(new DEdge(2,5,0,5,8,4));
		vList.addEdge(new DEdge(4,3,0,6,1,0));
		assertEquals(0, vList.searchEdge(new DEdge(3,1,0,6,1,4)));
		assertEquals(1, vList.searchEdge(new DEdge(4,3,0,6,1,0)));
		assertEquals(2, vList.searchEdge(new DEdge(0,0,0,4,4,4)));
		assertEquals(3, vList.searchEdge(new DEdge(3,3,4,6,6,4)));
		assertEquals(4, vList.searchEdge(new DEdge(2,5,0,5,8,4)));
		assertEquals(-3, vList.searchEdge(new DEdge(6,3,0,8,1,0)));
	}

        /**
         * Test the methods that searches the edge that is directly upper to the
         * specified point
         * @throws DelaunayError
         */
        public void testGetUpperEdge() throws DelaunayError{
		VerticalList vList = new VerticalList(1);
		vList.setAbs(new DPoint(5,4,0));
		vList.addEdge(new DEdge(0,0,0,4,4,4));
		vList.addEdge(new DEdge(3,3,4,6,6,4));
		vList.addEdge(new DEdge(3,1,0,6,1,4));
		vList.addEdge(new DEdge(2,5,0,5,8,4));
		vList.addEdge(new DEdge(4,3,0,6,1,0));
                DEdge upper = vList.getUpperEdge(new DPoint(4,2,0));
                assertTrue(upper.equals(new DEdge(4,3,0,6,1,0)));
                upper = vList.getUpperEdge(new DPoint(4,4,0));
                assertTrue(upper.equals(new DEdge(2,5,0,5,8,4)));
                upper = vList.getUpperEdge(new DPoint(4,15,0));
                assertNull(upper);
                upper = vList.getUpperEdge(new DPoint(4,7,0));
                assertNull(upper);

        }

        /**
         * Tests that the retrieval of the edge lower than a given point in a
         * vertical list works well.
         * @throws DelaunayError
         */
        public void testGetLowerEdge() throws DelaunayError{
		VerticalList vList = new VerticalList(1);
		vList.setAbs(new DPoint(5,4,0));
		vList.addEdge(new DEdge(0,0,0,4,4,4));
		vList.addEdge(new DEdge(3,3,4,6,6,4));
		vList.addEdge(new DEdge(3,1,0,6,1,4));
		vList.addEdge(new DEdge(2,5,0,5,8,4));
		vList.addEdge(new DEdge(4,3,0,6,1,0));
                DEdge lower = vList.getLowerEdge(new DPoint(4,2,0));
                assertTrue(lower.equals(new DEdge(3,1,0,6,1,4)));
                lower = vList.getLowerEdge(new DPoint(4,0,0));
                assertNull(lower);
                lower = vList.getLowerEdge(new DPoint(4,4,0));
                assertTrue(lower.equals(new DEdge(4,3,0,6,1,0)));
                lower = vList.getLowerEdge(new DPoint(4,15,0));
                assertTrue(lower.equals(new DEdge(2,5,0,5,8,4)));

        }

	/**
	 * tests the method that add a whole list of edges in the vertical list.
	 */
	public void testAddList() throws DelaunayError{
		VerticalList vList = new VerticalList(1);
		vList.setAbs(new DPoint(5,4,0));
		ArrayList<DEdge> edges = new ArrayList<DEdge>();
		edges.add(new DEdge(0,0,0,4,4,4));
		edges.add(new DEdge(3,3,4,6,6,4));
		edges.add(new DEdge(3,1,0,6,1,4));
		edges.add(new DEdge(2,5,0,5,8,4));
		edges.add(new DEdge(4,3,0,6,1,0));
		vList.addEdges(edges);
		assertTrue(vList.size()==5);
		assertTrue(vList.getVerticallySortedEdges().contains(new DEdge(0,0,0,4,4,4)));
		assertTrue(vList.getVerticallySortedEdges().contains(new DEdge(3,3,4,6,6,4)));
		assertTrue(vList.getVerticallySortedEdges().contains(new DEdge(3,1,0,6,1,4)));
		assertTrue(vList.getVerticallySortedEdges().contains(new DEdge(2,5,0,5,8,4)));
		assertTrue(vList.getVerticallySortedEdges().contains(new DEdge(4,3,0,6,1,0)));
	}

	/**
	 * checks if the method which search for intersection between an edge and
	 * the edges upper and lower than a point works well.
	 */
	public void testIntersectsUpperOrLower() throws DelaunayError{
		VerticalList vList = new VerticalList(1);
		vList.addEdge(new DEdge(0,0,0,4,4,4));
		vList.addEdge(new DEdge(4,4,4,6,6,4));
		vList.addEdge(new DEdge(3,1,0,6,1,4));
		vList.addEdge(new DEdge(2,5,0,5,8,4));
		vList.addEdge(new DEdge(4,3,0,6,1,0));
		DPoint pRef = new DPoint(5,3,0);
		assertTrue(vList.intersectsUpperOrLower(pRef, new DEdge(4,2,0,5,3,0)));
		assertTrue(vList.getLastUpperPt().equals(pRef));
		assertTrue(vList.getLastLowerPt().equals(pRef));
		assertTrue(vList.intersectsUpperOrLower(pRef, new DEdge(4,2,0,5,3,0)));
                //for the next edge, the upper edge is (0,0,0,4,4,4), not (4,4,4,6,6,6)
                //It the result of the vertical sort. Consequently, we don't have any
                //intersection between (4,6,0,5,3,0) and the edges directly upper and
                //lower than point
		assertFalse(vList.intersectsUpperOrLower(pRef, new DEdge(4,6,0,5,3,0)));
		assertFalse(vList.intersectsUpperOrLower(pRef, new DEdge(4,6,0,5,3,0)));
                //The intersection occurs with (0,0,0,4,4,4)
		assertTrue(vList.intersectsUpperOrLower(pRef, new DEdge(3,4,0,5,3,0)));

	}

	/**
	 * We check that the volatile attributes (lastUpperPt, etc...) are well
	 * removed when changing a valuable information in the list.
	 */
	public void testVolatileAttributes() throws DelaunayError{
		VerticalList vList = new VerticalList(1);
		vList.addEdge(new DEdge(0,0,0,4,4,4));
		vList.addEdge(new DEdge(4,4,4,6,6,4));
		vList.addEdge(new DEdge(3,1,0,6,1,4));
		vList.addEdge(new DEdge(2,5,0,5,8,4));
		vList.addEdge(new DEdge(4,3,0,6,1,0));
		DPoint pRef = new DPoint(5,3,0);
		assertTrue(vList.intersectsUpperOrLower(pRef, new DEdge(4,2,0,5,3,0)));
		assertTrue(vList.getLastUpperPt().equals(pRef));
		assertTrue(vList.getLastLowerPt().equals(pRef));
		vList.setAbs(0);
		assertNull(vList.getLastLowerEd());
		assertNull(vList.getLastUpperEd());
		assertNull(vList.getLastLowerPt());
		assertNull(vList.getLastLowerPt());
		vList.intersectsUpperOrLower(pRef, new DEdge(4,2,0,5,3,0));
		assertTrue(vList.getLastUpperPt().equals(pRef));
		assertTrue(vList.getLastLowerPt().equals(pRef));
		vList.addEdge(new DEdge(10,10,10,11,11,11));
		assertNull(vList.getLastLowerEd());
		assertNull(vList.getLastUpperEd());
		assertNull(vList.getLastLowerPt());
		assertNull(vList.getLastLowerPt());
		vList.intersectsUpperOrLower(pRef, new DEdge(4,2,0,5,3,0));
		assertTrue(vList.getLastUpperPt().equals(pRef));
		assertTrue(vList.getLastLowerPt().equals(pRef));
		vList.removeEdge(new DEdge(10,10,10,11,11,11));
		assertNull(vList.getLastLowerEd());
		assertNull(vList.getLastUpperEd());
		assertNull(vList.getLastLowerPt());
		assertNull(vList.getLastLowerPt());
		vList.intersectsUpperOrLower(pRef, new DEdge(4,2,0,5,3,0));
		assertTrue(vList.getLastUpperPt().equals(pRef));
		assertTrue(vList.getLastLowerPt().equals(pRef));
		ArrayList<DEdge> edgeList = new ArrayList<DEdge>();
		edgeList.add(new DEdge(10,10,10,11,11,11));
		edgeList.add(new DEdge(12,12,12,11,11,11));
		vList.addEdges(edgeList);
		assertNull(vList.getLastLowerEd());
		assertNull(vList.getLastUpperEd());
		assertNull(vList.getLastLowerPt());
		assertNull(vList.getLastLowerPt());
		vList.intersectsUpperOrLower(pRef, new DEdge(4,2,0,5,3,0));
		assertTrue(vList.getLastUpperPt().equals(pRef));
		assertTrue(vList.getLastLowerPt().equals(pRef));
	}

	/**
	 * Performs a simple test with a unique vertical constraint. We must be careful with
	 * this case, as a vertical edge will be reported as lower (resp. upper)
	 * than a point if it is on its left (resp. right) according to the
	 * vertical sort. This cases (that are false positive, and must be reported
	 * as negatives, consquently) must be especially managed.
	 */
	public void testVerticalConstraint() throws DelaunayError{
		VerticalList vList = new VerticalList();
		vList.addEdge(new DEdge(3,0,0,3,8,0));
		assertNull(vList.getLowerEdge(new DPoint(4,0,0)));
		assertNull(vList.getUpperEdge(new DPoint(4,0,0)));
		assertNull(vList.getLowerEdge(new DPoint(2,0,0)));
		assertNull(vList.getUpperEdge(new DPoint(2,0,0)));
	}

	/**
	 * Try to remove edges from a vertical list just by knowing their
	 * right most point.
	 * @throws DelaunayError
	 */
	public void testRemoveFromRightPoint() throws DelaunayError {
		VerticalList vList = new VerticalList();
		vList.addEdge(new DEdge(3,3,0,5,6,0));
		vList.addEdge(new DEdge(3,0,0,6,0,0));
		vList.addEdge(new DEdge(2,2,0,4,2,0));
		vList.addEdge(new DEdge(2,3,0,4,2,0));
		vList.addEdge(new DEdge(2,1,0,4,2,0));
		vList.removeEdgeFromRightPoint(new DPoint(4,2,0));
		assertTrue(vList.size()==2);
		assertTrue(vList.searchEdge(new DEdge(3,3,0,5,6,0))>=0);
		assertTrue(vList.searchEdge(new DEdge(3,0,0,6,0,0))>=0);
		vList = new VerticalList();
		vList.addEdge(new DEdge(2,2,0,4,2,0));
		vList.addEdge(new DEdge(2,3,0,4,2,0));
		vList.addEdge(new DEdge(2,1,0,4,2,0));
		vList.removeEdgeFromRightPoint(new DPoint(4,2,0));
		assertTrue(vList.size()==0);
		vList = new VerticalList();
		vList.addEdge(new DEdge(3,3,0,5,6,0));
		vList.addEdge(new DEdge(2,2,0,4,2,0));
		vList.addEdge(new DEdge(2,3,0,4,2,0));
		vList.addEdge(new DEdge(2,1,0,4,2,0));
		vList.removeEdgeFromRightPoint(new DPoint(4,2,0));
		assertTrue(vList.size()==1);
		assertTrue(vList.searchEdge(new DEdge(3,3,0,5,6,0))>=0);
		vList = new VerticalList();
		vList.addEdge(new DEdge(3,0,0,6,0,0));
		vList.addEdge(new DEdge(2,2,0,4,2,0));
		vList.addEdge(new DEdge(2,3,0,4,2,0));
		vList.addEdge(new DEdge(2,1,0,4,2,0));
		vList.removeEdgeFromRightPoint(new DPoint(4,2,0));
		assertTrue(vList.size()==1);
		assertTrue(vList.searchEdge(new DEdge(3,0,0,6,0,0))>=0);
	}

	/**
	 * Performs an edge removal on a vertical edge, from its right point,
	 * in a vertical list.
	 * @throws DelaunayError
	 */
	public void testRemoveVerticalEdgeFromRightPoint() throws DelaunayError {
		VerticalList vList = new VerticalList();
		vList.addEdge(new DEdge(0,4,0,6,8,0));
		vList.addEdge(new DEdge(4,2,0,4,5,0));
		vList.addEdge(new DEdge(0,2,0,6,1,0));
		vList.removeEdgeFromRightPoint(new DPoint(4,5,0));
		assertTrue(vList.size()==2);
		assertTrue(vList.get(0).equals(new DEdge(0,2,0,6,1,0)));
		assertTrue(vList.get(1).equals(new DEdge(0,4,0,6,8,0)));
		vList = new VerticalList();
		vList.addEdge(new DEdge(0,4,0,6,8,0));
		vList.addEdge(new DEdge(4,2,0,4,5,0));
		vList.addEdge(new DEdge(0,2,0,6,1,0));
		vList.removeEdgeFromRightPoint(new DPoint(4,2,0));
		assertTrue(vList.size()==3);
		assertTrue(vList.get(0).equals(new DEdge(0,2,0,6,1,0)));
		assertTrue(vList.get(2).equals(new DEdge(0,4,0,6,8,0)));

	}

	public void testVerticalSort() throws DelaunayError {
		VerticalComparator vc = new VerticalComparator(30.60612691466083);
		DEdge e1 = new DEdge(8.0, 21.0, 73.0,51.0, 50.0, 47.0);
		DEdge e2 = new DEdge(70.0, 74.0, 55.0,30.60612691466083, 32.288840262582056, 32.60256458544183);
		assertTrue(vc.compare(e1, e2)==1);
	}
}
