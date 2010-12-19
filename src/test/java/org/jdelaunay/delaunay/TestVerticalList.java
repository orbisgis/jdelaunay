/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdelaunay.delaunay;

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

        }
}
