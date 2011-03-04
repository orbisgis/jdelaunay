/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdelaunay.delaunay;

import java.util.List;

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

	/**
	 * Test the setStartNode method.
	 * @throws DelaunayError
	 */
	public void testSetStartNode()throws DelaunayError {
		DTriangle tri = new DTriangle(new DEdge(0,0,0,4,0,0), new DEdge(4,0,0,0,4,0) , new DEdge(0,4,0,0,0,0));
		VoronoiGraph vg = new VoronoiGraph(tri);
		tri = new DTriangle(new DEdge(0,0,0,5,0,0), new DEdge(5,0,0,0,4,0) , new DEdge(0,4,0,0,0,0));
		VoronoiNode vn = new VoronoiNode(tri);
		vg.setStartNode(vn);
		assertTrue(vg.getStartNode().equals(vn));
		assertTrue(vg.getStartNode()==vn);
		tri = new DTriangle(new DEdge(0,0,0,5,0,0), new DEdge(5,0,0,0,6,0) , new DEdge(0,6,0,0,0,0));
		vg.setStartNode(tri);
		assertTrue(vg.getStartNode().equals(new VoronoiNode(tri)));
	}

	/**
	 * Compute a Voronoi graph using the sample triangles provided by TestVoronoiNode.
	 * @throws DelaunayError
	 */
	public void testProcessGraph() throws DelaunayError {
		List<DTriangle> tris = TestVoronoiNodes.getSampleTriangles();
		DTriangle tri = tris.get(9);
		VoronoiGraph vg = new VoronoiGraph(tri);
		vg.fillUntilNotFlatFound();
		assertTrue(vg.getSortedNodes().size()==10);
		for(VoronoiNode vn : vg.getSortedNodes()){
			boolean found = false;
			for(DTriangle dt : tris){
				if(vn.getParent().equals(dt)){
					found = true;
					break;
				}
			}
			if(!found){
				assertTrue(false);
			}
		}
	}
}
