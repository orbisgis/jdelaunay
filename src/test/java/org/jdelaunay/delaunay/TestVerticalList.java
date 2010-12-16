/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdelaunay.delaunay;

/**
 *
 * @author alexis
 */
public class TestVerticalList extends BaseUtility{

	public void testChangeAbs() throws DelaunayError{
		VerticalList vList = new VerticalList(1);
		vList.addEdge(new Edge(0,0,0,4,4,4));
		vList.addEdge(new Edge(3,3,4,6,6,4));
		vList.addEdge(new Edge(3,1,0,6,1,4));
		vList.addEdge(new Edge(2,5,0,5,8,4));
		assertTrue(vList.get(0).equals(new Edge(3,1,0,6,1,4)));
		assertTrue(vList.get(1).equals(new Edge(0,0,0,4,4,4)));
		assertTrue(vList.get(2).equals(new Edge(3,3,4,6,6,4)));
		assertTrue(vList.get(3).equals(new Edge(2,5,0,5,8,4)));
		vList.setAbs(0);
		assertTrue(vList.get(2).equals(new Edge(3,1,0,6,1,4)));
		assertTrue(vList.get(0).equals(new Edge(0,0,0,4,4,4)));
		assertTrue(vList.get(1).equals(new Edge(3,3,4,6,6,4)));
		assertTrue(vList.get(3).equals(new Edge(2,5,0,5,8,4)));
	}

}
