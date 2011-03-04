/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdelaunay.delaunay;

/**
 * Class that performs test on the voronoi graph
 * @author alexis
 */
public class TestVoronoiGraph extends BaseUtility{

	/**
	 * Create a simple VoronoiGraph.
	 * @throws DelaunayError
	 */
	public void testCreation() throws DelaunayError {
		DTriangle tri = new DTriangle(new DEdge(0,0,0,4,0,0), new DEdge(4,0,0,0,4,0) , new DEdge(0,4,0,0,0,0));
		VoronoiGraph vg = new VoronoiGraph(tri);
		assertTrue(vg.getSortedNodes().size()==1);
		assertTrue(vg.getSortedNodes().contains(new VoronoiNode(tri)));
		assertTrue(vg.getStartNode().equals(new VoronoiNode(tri)));
		
	}

}
