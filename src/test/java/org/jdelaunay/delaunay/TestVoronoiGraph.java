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

	/**
	 * Tests that a not flat triangle is stored when filling the graph
	 * @throws DelaunayError
	 */
	public void testRetrieveNotFlat() throws DelaunayError {
		List<DTriangle> tris = TestVoronoiNodes.getSampleTriangles();
		DTriangle tri = tris.get(9);
		DTriangle tri7 = tris.get(7);
		DTriangle tri6 = tris.get(6);
		List<DPoint> points = tri7.getPoints();
		for(DPoint p : points){
			if(p.equals(new DPoint(1,2,0))){
				p.setZ(8);
			}
		}
		VoronoiGraph vg = new  VoronoiGraph(tri);
		vg.fillUntilNotFlatFound();
		assertTrue(vg.getNotFlat().getParent().equals(tri7) || vg.getNotFlat().getParent().equals(tri6));
	}

	/**
	 * Tests that not-flat triangles are not all added to the graph.
	 * The graph must just contain the ones that are linked to a flat-node
	 * contained in the graph.
	 * @throws DelaunayError
	 */
	public void testProcessFlatGraph() throws DelaunayError {
		List<DTriangle> tris = TestVoronoiNodes.getSampleTriangles();
		DTriangle tri = tris.get(9);
		DTriangle tri7 = tris.get(7);
		DTriangle tri6 = tris.get(6);
		DTriangle tri5 = tris.get(5);
		List<DPoint> points = tri6.getPoints();
		for(DPoint p : points){
			if(p.equals(new DPoint(1,2,0))){
				p.setZ(8);
			}
			if(p.equals(new DPoint(4,0,0))){
				p.setZ(8);
			}
		}
		VoronoiGraph vg = new  VoronoiGraph(tri);
		vg.fillUntilNotFlatFound();
		assertTrue(vg.getNotFlat().getParent().equals(tri7) || vg.getNotFlat().getParent().equals(tri5));
		assertTrue(vg.getSortedNodes().size()==9);
		assertFalse(vg.getSortedNodes().contains(new VoronoiNode(tri6)));
	}

	public void testZComputation() throws DelaunayError {
		DPoint p1 = new DPoint(0,4,0);
		DPoint p2 = new DPoint(3,7,0);
		DPoint p3 = new DPoint(5,5,0);
		DPoint p4= new DPoint(3,0,10);
		DEdge e1 = new DEdge(p1, p2);
		e1.setLocked(true);
		DEdge e2 = new DEdge(p2, p3);
		e2.setLocked(true);
		DEdge e3 = new DEdge(p1, p3);
		DEdge e4 = new DEdge(p1, p4);
		DEdge e5 = new DEdge(p3, p4);
		DTriangle t1 = new DTriangle(e1, e2, e3);
		DTriangle t2 = new DTriangle(e4, e5, e3);
		VoronoiGraph vg = new VoronoiGraph(t1);
		vg.fillUntilNotFlatFound();
		vg.assignZValues();
		for(VoronoiNode vn : vg.getSortedNodes()){
			assertTrue(vn.getLocation().getZ() < 10);
			assertTrue(vn.getLocation().getZ() > 0);
		}
	}
}
