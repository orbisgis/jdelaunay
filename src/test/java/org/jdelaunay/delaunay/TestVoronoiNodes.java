package org.jdelaunay.delaunay;

import java.util.ArrayList;
import java.util.List;

/**
 * This class performs miscellaneous tests on the VoronoiNode mechanisms.
 * @author alexis
 */
public class TestVoronoiNodes extends BaseUtility {

	/**
	 * Test the creation of a new VoronoiNode.
	 * @throws DelaunayError
	 */
	public void testCreation()throws DelaunayError {
		DTriangle tri = new DTriangle(new DEdge(0,0,0,4,0,0), new DEdge(4,0,0,0,4,0) , new DEdge(0,4,0,0,0,0));
		VoronoiNode vn = new VoronoiNode(tri);
		assertTrue(vn.getLinkedNodes().isEmpty());
		assertTrue(vn.getLocation().equals(new DPoint(2,2,0)));
		assertTrue(vn.getParent()==tri);
	}

	/**
	 * Instanciate a voronoi node and change its parent. Check that everything went well.
	 * @throws DelaunayError
	 */
	public void testUpdateParent() throws DelaunayError {
		DTriangle tri = new DTriangle(new DEdge(0,0,0,4,0,0), new DEdge(4,0,0,0,4,0) , new DEdge(0,4,0,0,0,0));
		VoronoiNode vn = new VoronoiNode(tri);
		tri = new DTriangle(new DEdge(0,0,0,5,0,0), new DEdge(5,0,0,0,5,0) , new DEdge(0,5,0,0,0,0));
		vn.setParent(tri);
		assertTrue(vn.getParent().equals(tri));
		assertTrue(vn.getParent() == tri);
		assertTrue(vn.getLocation().equals(new DPoint(2.5, 2.5, 0)));
	}

	/**
	 * performs a equality test.
	 * @throws DelaunayError
	 */
	public void testEquality() throws DelaunayError {
		DTriangle tri = new DTriangle(new DEdge(0,0,0,4,0,0), new DEdge(4,0,0,0,4,0) , new DEdge(0,4,0,0,0,0));
		VoronoiNode vn = new VoronoiNode(tri);
		DTriangle tri2 = new DTriangle(new DEdge(0,0,0,4,0,0), new DEdge(4,0,0,0,4,0) , new DEdge(0,4,0,0,0,0));
		VoronoiNode vn2 = new VoronoiNode(tri2);
		assertTrue(vn.equals(vn2));
		assertTrue(vn.hashCode() == vn2.hashCode());
	}

	/**
	 * Tests that we can't set a null parent to a voronoi node.
	 * @throws DelaunayError
	 */
	public void testSetNullParent() throws DelaunayError {
		DTriangle tri = new DTriangle(new DEdge(0,0,0,4,0,0), new DEdge(4,0,0,0,4,0) , new DEdge(0,4,0,0,0,0));
		VoronoiNode vn = new VoronoiNode(tri);
		try{
			vn.setParent(null);
			assertTrue(false);
		} catch(DelaunayError d){
			assertTrue(true);
		}
	}

	/**
	 * Try to retrieve the neighbours of a VN. Its parent has three neighbour
	 * triangles, and none of the parent's edges are constraints.
	 * @throws DelaunayError
	 */
	public void testRetrieveNeighbours() throws DelaunayError {
		List<DTriangle> tris = getSampleTriangles();
		DTriangle tri = tris.get(9);
		assertTrue(tri.equals(new DTriangle(new DEdge(2,6,0,4,3,0), new DEdge(4,3,0,5,6,0), new DEdge(5,6,0,2,6,0))));
		VoronoiNode vn = new VoronoiNode(tri);
		List<VoronoiNode> neigh = vn.getNeighbourNodes();
		List<DTriangle> parents = new ArrayList<DTriangle>();
		for(VoronoiNode v : neigh){
			parents.add(v.getParent());
		}
		assertTrue(neigh.size()==3);
		assertTrue(parents.contains(new DTriangle(new DEdge(2,6,0,5,6,0), new DEdge(5,6,0,1,9,0), new DEdge(1,9,0,2,6,0))));
		assertTrue(parents.contains(new DTriangle(new DEdge(2,6,0,4,3,0), new DEdge(4,3,0,0,5,0), new DEdge(0,5,0,2,6,0))));
		assertTrue(parents.contains(new DTriangle(new DEdge(4,3,0,5,6,0), new DEdge(5,6,0,8,3,0), new DEdge(8,3,0,4,3,0))));
	}

	/**
	 * this test retrieves the neighbour of a triangle that have only two. Also check
	 * that there is not any duplications between triangles during the graph
	 * computation.
	 * @throws DelaunayError
	 */
	public void testRetrieveNeighEmptySide() throws DelaunayError {
		List<DTriangle> tris = getSampleTriangles();
		DTriangle tri = tris.get(5);
		assertTrue(tri.equals(new DTriangle(new DEdge(8,3,0,4,3,0), new DEdge(4,3,0,4,0,0), new DEdge(4,0,0,8,3,0))));
		VoronoiNode vn = new VoronoiNode(tri);
		List<VoronoiNode> neigh = vn.getNeighbourNodes();
		List<DTriangle> parents = new ArrayList<DTriangle>();
		for(VoronoiNode v : neigh){
			parents.add(v.getParent());
		}
		assertTrue(neigh.size()==2);
		assertTrue(parents.contains(tris.get(4)));
		assertTrue(parents.contains(tris.get(6)));
		assertTrue(parents.get(0)==tris.get(4) || parents.get(0)==tris.get(6));
		assertTrue(parents.get(1)==tris.get(5) || parents.get(1)==tris.get(6));
	}

	/**
	 * Retrieves the neighbours of a triangles that is connected to three other
	 * ones, but that is protected from one of them with a constraint.
	 * @throws DelaunayError
	 */
	public void testRetrieveNeighOneConstraint() throws DelaunayError {
		List<DTriangle> tris = getSampleTriangles();
		DTriangle tri = tris.get(9);
		DEdge[] edges = tri.getEdges();
		for(int i = 0; i<DTriangle.PT_NB; i++){
			if(edges[i].equals(new DEdge(2,6,0,4,3,0))){
				edges[i].setLocked(true);
				break;
			}
		}
		VoronoiNode vn = new VoronoiNode(tri);
		List<VoronoiNode> neigh = vn.getNeighbourNodes();
		List<DTriangle> parents = new ArrayList<DTriangle>();
		for(VoronoiNode v : neigh){
			parents.add(v.getParent());
		}
		assertTrue(neigh.size()==2);
		assertTrue(parents.contains(new DTriangle(new DEdge(2,6,0,5,6,0), new DEdge(5,6,0,1,9,0), new DEdge(1,9,0,2,6,0))));
		assertTrue(parents.contains(new DTriangle(new DEdge(4,3,0,5,6,0), new DEdge(5,6,0,8,3,0), new DEdge(8,3,0,4,3,0))));
	}

	/**
	 * Retrieves the neighbours of a triangles that is connected to three other
	 * ones, but that is protected from one of them with two constraints.
	 * @throws DelaunayError
	 */
	public void testRetrieveNeighTwoConstraints() throws DelaunayError {
		List<DTriangle> tris = getSampleTriangles();
		DTriangle tri = tris.get(9);
		DEdge[] edges = tri.getEdges();
		for(int i = 0; i<DTriangle.PT_NB; i++){
			if(edges[i].equals(new DEdge(2,6,0,4,3,0))){
				edges[i].setLocked(true);
				break;
			}
		}
		for(int i = 0; i<DTriangle.PT_NB; i++){
			if(edges[i].equals(new DEdge(2,6,0,5,6,0))){
				edges[i].setLocked(true);
				break;
			}
		}
		VoronoiNode vn = new VoronoiNode(tri);
		List<VoronoiNode> neigh = vn.getNeighbourNodes();
		List<DTriangle> parents = new ArrayList<DTriangle>();
		for(VoronoiNode v : neigh){
			parents.add(v.getParent());
		}
		assertTrue(neigh.size()==1);
		assertTrue(parents.contains(new DTriangle(new DEdge(4,3,0,5,6,0), new DEdge(5,6,0,8,3,0), new DEdge(8,3,0,4,3,0))));
	}

	/**
	 * Retrieves the neighbours of a triangles that is connected to three other
	 * ones, but that is protected from them with three constraints.
	 * @throws DelaunayError
	 */
	public void testRetrieveNeighThreeConstraints() throws DelaunayError {
		List<DTriangle> tris = getSampleTriangles();
		DTriangle tri = tris.get(9);
		DEdge[] edges = tri.getEdges();
		for(int i = 0; i<DTriangle.PT_NB; i++){
			if(edges[i].equals(new DEdge(2,6,0,4,3,0))){
				edges[i].setLocked(true);
			}
			if(edges[i].equals(new DEdge(2,6,0,5,6,0))){
				edges[i].setLocked(true);
			}
			if(edges[i].equals(new DEdge(4,3,0,5,6,0))){
				edges[i].setLocked(true);
			}
		}
		VoronoiNode vn = new VoronoiNode(tri);
		List<VoronoiNode> neigh = vn.getNeighbourNodes();
		List<DTriangle> parents = new ArrayList<DTriangle>();
		for(VoronoiNode v : neigh){
			parents.add(v.getParent());
		}
		assertTrue(neigh.isEmpty());
	}

	/**
	 * Build a sample of connected triangles that share the exact same edges and points.
	 * @return
	 * @throws DelaunayError
	 */
	private List<DTriangle> getSampleTriangles() throws DelaunayError {
		List<DTriangle> ret = new ArrayList<DTriangle>();
		DPoint p1 = new DPoint(0,5,0);
		DPoint p2 = new DPoint(1,2,0);
		DPoint p3 = new DPoint(1,9,0);
		DPoint p4 = new DPoint(2,6,0);
		DPoint p5 = new DPoint(4,0,0);
		DPoint p6 = new DPoint(4,3,0);
		DPoint p7 = new DPoint(5,6,0);
		DPoint p8 = new DPoint(8,3,0);
		DPoint p9 = new DPoint(8,6,0);
		DEdge e1 = new DEdge(p1, p2);
		DEdge e2 = new DEdge(p1, p3);
		DEdge e3 = new DEdge(p1, p4);
		DEdge e4 = new DEdge(p1, p6);
		DEdge e5 = new DEdge(p2, p5);
		DEdge e6 = new DEdge(p2, p6);
		DEdge e7 = new DEdge(p3, p4);
		DEdge e8 = new DEdge(p3, p7);
		DEdge e9 = new DEdge(p3, p9);
		DEdge e10 = new DEdge(p4, p6);
		DEdge e11 = new DEdge(p4, p7);
		DEdge e12 = new DEdge(p5, p6);
		DEdge e13 = new DEdge(p5, p8);
		DEdge e14 = new DEdge(p6, p7);
		DEdge e15 = new DEdge(p6, p8);
		DEdge e16 = new DEdge(p7, p8);
		DEdge e17 = new DEdge(p7, p9);
		DEdge e18 = new DEdge(p8, p9);
		DTriangle t1 = new DTriangle(e2, e3, e7);
		DTriangle t2 = new DTriangle(e11, e8, e7);
		DTriangle t3 = new DTriangle(e8, e17, e9);
		DTriangle t4 = new DTriangle(e16, e17, e18);
		DTriangle t5 = new DTriangle(e14, e15, e16);
		DTriangle t6 = new DTriangle(e15, e12, e13);
		DTriangle t7 = new DTriangle(e5, e12, e6);
		DTriangle t8 = new DTriangle(e1, e4, e6);
		DTriangle t9 = new DTriangle(e10, e4, e3);
		DTriangle t10 = new DTriangle(e10, e11, e14);
		ret.add(t1);
		ret.add(t2);
		ret.add(t3);
		ret.add(t4);
		ret.add(t5);
		ret.add(t6);
		ret.add(t7);
		ret.add(t8);
		ret.add(t9);
		ret.add(t10);
		return ret;
	}

}
