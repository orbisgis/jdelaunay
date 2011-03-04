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

	/**
	 * The same test as above, but we change our starting node.
	 * @throws DelaunayError
	 */
	public void testProcessGraphOtherStartNode() throws DelaunayError {
		List<DTriangle> tris = TestVoronoiNodes.getSampleTriangles();
		DTriangle tri = tris.get(2);
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

	/**
	 * We isolate a triangle and check we obtain a graph with a single node.
	 * @throws DelaunayError
	 */
	public void testProcessGraphIsolatedTriangle() throws DelaunayError {
		List<DTriangle> tris = TestVoronoiNodes.getSampleTriangles();
		DTriangle tri = tris.get(9);
		tri.getEdges()[0].setLocked(true);
		tri.getEdges()[1].setLocked(true);
		tri.getEdges()[2].setLocked(true);
		VoronoiGraph vg = new VoronoiGraph(tri);
		vg.fillUntilNotFlatFound();
		assertTrue(vg.getSortedNodes().size()==1);
		assertTrue(vg.getSortedNodes().contains(new VoronoiNode(new DTriangle(new DEdge(2,6,0,4,3,0),
					new DEdge(4,3,0,5,6,0), new DEdge(5,6,0,2,6,0)))));
	}

	/**
	 * We isolate a triangle and check we obtain a graph with a single node.
	 * @throws DelaunayError
	 */
	public void testProcessNotTotallyIsolatedTriangle() throws DelaunayError {
		List<DTriangle> tris = TestVoronoiNodes.getSampleTriangles();
		DTriangle tri = tris.get(9);
		tri.getEdges()[0].setLocked(true);
		tri.getEdges()[1].setLocked(true);
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

	/**
	 * We isolate two triangle and check we obtain a graph with 8 nodes.
	 * @throws DelaunayError
	 */
	public void testProcessTwoIsolatedTriangles() throws DelaunayError {
		List<DTriangle> tris = TestVoronoiNodes.getSampleTriangles();
		DTriangle tri1 = tris.get(2);
		tri1.getEdges()[0].setLocked(true);
		tri1.getEdges()[1].setLocked(true);
		tri1.getEdges()[2].setLocked(true);
		DTriangle tri2 = tris.get(3);
		tri2.getEdges()[0].setLocked(true);
		tri2.getEdges()[1].setLocked(true);
		tri2.getEdges()[2].setLocked(true);
		DTriangle tri = tris.get(9);
		VoronoiGraph vg = new VoronoiGraph(tri);
		vg.fillUntilNotFlatFound();
		assertTrue(vg.getSortedNodes().size()==8);
		for(VoronoiNode vn : vg.getSortedNodes()){
			boolean found = false;
			for(DTriangle dt : tris){
				if(vn.getParent().equals(dt) || vn.getParent().equals(tri1)||vn.getParent().equals(tri2)){
					found = true;
					break;
				}
			}
			if(!found){
				assertTrue(false);
			}
		}
	}

	/**
	 * test that we retrieve two isolated graphs when dividing the space in
	 * two parts with constrained edges.
	 * @throws DelaunayError
	 */
	public void testTwoInnerGraphs() throws DelaunayError {
		List<DTriangle> tris = TestVoronoiNodes.getSampleTriangles();
		DTriangle tri1 = tris.get(2);
		DTriangle tri2 = tris.get(3);
		for(int i=0; i<DTriangle.PT_NB;i++){
			if(tri1.getEdges()[i].equals(new DEdge(5,6,0,1,9,0))){
				tri1.getEdges()[i].setLocked(true);
			}
			if(tri2.getEdges()[i].equals(new DEdge(5,6,0,8,3,0))){
				tri2.getEdges()[i].setLocked(true);
			}
		}
		DTriangle tri = tris.get(9);
		VoronoiGraph vg = new VoronoiGraph(tri);
		vg.fillUntilNotFlatFound();
		assertTrue(vg.getSortedNodes().size()==8);
		for(VoronoiNode vn : vg.getSortedNodes()){
			boolean found = false;
			for(DTriangle dt : tris){
				if(vn.getParent().equals(dt) || vn.getParent().equals(tri1)||vn.getParent().equals(tri2)){
					found = true;
					break;
				}
			}
			if(!found){
				assertTrue(false);
			}
		}
		vg = new VoronoiGraph(tri1);
		vg.fillUntilNotFlatFound();
		assertTrue(vg.getSortedNodes().size()==2);
		assertTrue(vg.getSortedNodes().contains(new VoronoiNode(new DTriangle(new DEdge(1,9,0,5,6,0),
					new DEdge(5,6,0,8,6,0), new DEdge(8,6,0,1,9,0)))));
		assertTrue(vg.getSortedNodes().contains(new VoronoiNode(new DTriangle(new DEdge(8,3,0,5,6,0),
					new DEdge(5,6,0,8,6,0), new DEdge(8,6,0,8,3,0)))));
	}
}
