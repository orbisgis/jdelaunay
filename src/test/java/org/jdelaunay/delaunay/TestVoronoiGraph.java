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

	/**
	 * Makes tests on the computation of the ZValue of the location of the Vn.
	 * @throws DelaunayError
	 */
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

	/**
	 * Compute ZValues for a VoronoiGraph that contains only flat triangles
	 * and that is lower than its neighbours
	 * 
	 * @throws DelaunayError
	 */
	public void testZIsolatedPartLower() throws DelaunayError {
		List<DTriangle> tris = TestVoronoiNodes.getSampleTriangles();
		DTriangle tri = tris.get(9);
		tri.getEdges()[0].setLocked(true);
		tri.getEdges()[1].setLocked(true);
		tri.getEdges()[2].setLocked(true);
		tri = tris.get(1);
		DPoint dp = tri.getOppositePoint(new DEdge(2,6,0,5,6,0));
		dp.setZ(8);
		tri = tris.get(4);
		dp = tri.getOppositePoint(new DEdge(4,3,0,5,6,0));
		dp.setZ(6);
		tri = tris.get(8);
		dp = tri.getOppositePoint(new DEdge(2,6,0,4,3,0));
		dp.setZ(5);
		tri = tris.get(9);
		VoronoiGraph vg = new VoronoiGraph(tri);
		vg.assignZValues();
		assertTrue(vg.getSortedNodes().get(0).getLocation().getZ()<0);
	}

	/**
	 * Compute ZValues for a VoronoiGraph that contains only flat triangles
	 * and that is upper than its neighbours
	 *
	 * @throws DelaunayError
	 */
	public void testZIsolatedPartUpper() throws DelaunayError {
		List<DTriangle> tris = TestVoronoiNodes.getSampleTriangles();
		DTriangle tri = tris.get(9);
		tri.getEdges()[0].setLocked(true);
		tri.getEdges()[1].setLocked(true);
		tri.getEdges()[2].setLocked(true);
		tri = tris.get(1);
		DPoint dp = tri.getOppositePoint(new DEdge(2,6,0,5,6,0));
		dp.setZ(-8);
		tri = tris.get(4);
		dp = tri.getOppositePoint(new DEdge(4,3,0,5,6,0));
		dp.setZ(-6);
		tri = tris.get(8);
		dp = tri.getOppositePoint(new DEdge(2,6,0,4,3,0));
		dp.setZ(-5);
		tri = tris.get(9);
		VoronoiGraph vg = new VoronoiGraph(tri);
		vg.assignZValues();
		assertTrue(vg.getSortedNodes().get(0).getLocation().getZ()>0);
	}

	/**
	 * Compute ZValues for a VoronoiGraph that contains only flat triangles
	 * and that is upper than some of its neighbours lower than the others.
	 * The z coordinate of the location is not supposed to be changed, here.
	 *
	 * @throws DelaunayError
	 */
	public void testZIsolatedPartIntermediate() throws DelaunayError {
		List<DTriangle> tris = TestVoronoiNodes.getSampleTriangles();
		DTriangle tri = tris.get(9);
		tri.getEdges()[0].setLocked(true);
		tri.getEdges()[1].setLocked(true);
		tri.getEdges()[2].setLocked(true);
		tri = tris.get(1);
		DPoint dp = tri.getOppositePoint(new DEdge(2,6,0,5,6,0));
		dp.setZ(-8);
		tri = tris.get(4);
		dp = tri.getOppositePoint(new DEdge(4,3,0,5,6,0));
		dp.setZ(-6);
		tri = tris.get(8);
		dp = tri.getOppositePoint(new DEdge(2,6,0,4,3,0));
		dp.setZ(5);
		tri = tris.get(9);
		VoronoiGraph vg = new VoronoiGraph(tri);
		vg.assignZValues();
		assertTrue(vg.getSortedNodes().get(0).getLocation().getZ()==0);
	}

	public void testGraphDepth() throws DelaunayError {
		DPoint p1 = new DPoint(0,4,10);
		DPoint p2 = new DPoint(3,4,0);
		DPoint p3 = new DPoint(3,0,0);
		DPoint p4 = new DPoint(6,2,0);
		DPoint p5 = new DPoint(9,0,0);
		DEdge e1 = new DEdge(p1, p2);
		DEdge e2 = new DEdge(p1, p3);
		DEdge e3 = new DEdge(p2, p3);
		DEdge e4 = new DEdge(p2, p4);
		DEdge e5 = new DEdge(p3, p4);
		DEdge e6 = new DEdge(p3, p5);
		DEdge e7 = new DEdge(p4, p5);
		e4.setLocked(true);
		e6.setLocked(true);
		e7.setLocked(true);
		DTriangle t1 = new DTriangle(e1, e2, e3);
		DTriangle t2 = new DTriangle(e3, e4, e5);
		DTriangle t3 = new DTriangle(e5, e6, e7);
		VoronoiGraph vg = new VoronoiGraph(t3);
		vg.fillUntilNotFlatFound();
		int de = vg.getMaxDepth();
		assertTrue(de == 2);
	}

	/**
	 * a test on the length of a voronoi graph, where we have two branches in the graph.
	 * @throws DelaunayError
	 */
	public void testGraphDepthTwoBranches() throws DelaunayError {
		DPoint p1 = new DPoint(0,0,0);
		DPoint p2 = new DPoint(4,0,10);
		DPoint p3 = new DPoint(0,3,10);
		DPoint p4 = new DPoint(2,6,10);
		DPoint p5 = new DPoint(6,7,10);
		DPoint p6 = new DPoint(5,3,10);
		DPoint p7 = new DPoint(9,3,10);
		DEdge e1 = new DEdge(p1, p3);
		DEdge e2 = new DEdge(p1, p2);
		DEdge e3 = new DEdge(p3, p2);
		DEdge e4 = new DEdge(p3, p4);
		DEdge e5 = new DEdge(p4, p5);
		DEdge e6 = new DEdge(p4, p6);
		DEdge e7 = new DEdge(p5, p6);
		DEdge e8 = new DEdge(p6, p7);
		DEdge e9 = new DEdge(p2, p7);
		DEdge e10 = new DEdge(p6, p2);
		DEdge e11 = new DEdge(p3, p6);
		DTriangle dt1 = new DTriangle(e1, e2, e3);
		DTriangle dt2 = new DTriangle(e11, e10, e3);
		DTriangle dt3 = new DTriangle(e11, e4, e6);
		DTriangle dt4 = new DTriangle(e6, e5, e7);
		DTriangle dt5 = new DTriangle(e10, e8, e9);
		e4.setLocked(true);
		e5.setLocked(true);
		e7.setLocked(true);
		e8.setLocked(true);
		e9.setLocked(true);
		VoronoiGraph vg = new VoronoiGraph(dt5);
		vg.fillUntilNotFlatFound();
		int depth = vg.getMaxDepth();
		assertTrue(depth==3);
                dt1.setSeenForFlatRemoval(false);
                dt2.setSeenForFlatRemoval(false);
                dt3.setSeenForFlatRemoval(false);
                dt4.setSeenForFlatRemoval(false);
                dt5.setSeenForFlatRemoval(false);
		vg = new VoronoiGraph(dt4);
		vg.fillUntilNotFlatFound();
		depth = vg.getMaxDepth();
		assertTrue(depth==3);
	}

	/**
	 * A test on the graph depth computation with a loop.
	 * @throws DelaunayError
	 */
	public void testGraphDepthLoop() throws DelaunayError {
		DPoint p1 = new DPoint(0,0,10);
		DPoint p2 = new DPoint(4,0,0);
		DPoint p3 = new DPoint(0,3,0);
		DPoint p4 = new DPoint(4,4,0);
		DPoint p5 = new DPoint(5,7,0);
		DPoint p6 = new DPoint(7,2,0);
		DPoint p7 = new DPoint(7,4,0);
		DPoint p8 = new DPoint(9,6,0);
		DEdge e1 = new DEdge(p1, p2);
		DEdge e2 = new DEdge(p1, p3);
		DEdge e3 = new DEdge(p3, p2);
		DEdge e4 = new DEdge(p4, p3);
		DEdge e5 = new DEdge(p4, p2);
		DEdge e6 = new DEdge(p5, p2);
		DEdge e7 = new DEdge(p4, p5);
		DEdge e8 = new DEdge(p5, p7);
		DEdge e9 = new DEdge(p4, p7);
		DEdge e10 = new DEdge(p4, p6);
		DEdge e11 = new DEdge(p3, p6);
		DEdge e12 = new DEdge(p6, p7);
		DEdge e13 = new DEdge(p7, p8);
		DEdge e14 = new DEdge(p5, p8);
		DTriangle dt1 = new DTriangle (e1, e2, e3);
		DTriangle dt2 = new DTriangle (e4, e5, e3);
		DTriangle dt3 = new DTriangle (e5, e6, e7);
		DTriangle dt4 = new DTriangle (e7, e8, e9);
		DTriangle dt5 = new DTriangle (e10, e12, e9);
		DTriangle dt6 = new DTriangle (e10, e4, e11);
		DTriangle dt7 = new DTriangle (e8, e14, e13);
		e6.setLocked(true);
		e14.setLocked(true);
		e13.setLocked(true);
		e12.setLocked(true);
		e11.setLocked(true);
		VoronoiGraph vg = new VoronoiGraph(dt7);
		vg.fillUntilNotFlatFound();
		int dep = vg.getMaxDepth();
		assertTrue(dep==4 || dep==5);		
	}

	/**
	 * A test on the graph depth computation with a loop.
	 * @throws DelaunayError
	 */
	public void testGraphDepthLoopSetZ() throws DelaunayError {
		DPoint p1 = new DPoint(0,0,10);
		DPoint p2 = new DPoint(4,0,0);
		DPoint p3 = new DPoint(0,3,0);
		DPoint p4 = new DPoint(4,4,0);
		DPoint p5 = new DPoint(5,7,0);
		DPoint p6 = new DPoint(7,2,0);
		DPoint p7 = new DPoint(7,4,0);
		DPoint p8 = new DPoint(9,6,0);
		DEdge e1 = new DEdge(p1, p2);
		DEdge e2 = new DEdge(p1, p3);
		DEdge e3 = new DEdge(p3, p2);
		DEdge e4 = new DEdge(p4, p3);
		DEdge e5 = new DEdge(p4, p2);
		DEdge e6 = new DEdge(p5, p2);
		DEdge e7 = new DEdge(p4, p5);
		DEdge e8 = new DEdge(p5, p7);
		DEdge e9 = new DEdge(p4, p7);
		DEdge e10 = new DEdge(p4, p6);
		DEdge e11 = new DEdge(p3, p6);
		DEdge e12 = new DEdge(p6, p7);
		DEdge e13 = new DEdge(p7, p8);
		DEdge e14 = new DEdge(p5, p8);
		DTriangle dt1 = new DTriangle (e1, e2, e3);
		DTriangle dt2 = new DTriangle (e4, e5, e3);
		DTriangle dt3 = new DTriangle (e5, e6, e7);
		DTriangle dt4 = new DTriangle (e7, e8, e9);
		DTriangle dt5 = new DTriangle (e10, e12, e9);
		DTriangle dt6 = new DTriangle (e10, e4, e11);
		DTriangle dt7 = new DTriangle (e8, e14, e13);
		e6.setLocked(true);
		e14.setLocked(true);
		e13.setLocked(true);
		e12.setLocked(true);
		e11.setLocked(true);
		VoronoiGraph vg = new VoronoiGraph(dt7);
		vg.fillUntilNotFlatFound();
		vg.assignZValues();
		for(VoronoiNode vn : vg.getSortedNodes()){
			assertTrue(vn.getLocation().getZ()<10 && vn.getLocation().getZ()>0);
		}
	}
}
