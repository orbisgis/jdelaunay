
package org.jdelaunay.delaunay;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides tests that run on the BoundaryPart class.
 * @author alexis
 */
public class TestBoundaryPart extends BaseUtility {

	/**
	 * test if a boundary part can bee right next another one in the boundary.
	 */
	public void testCanBeNext(){
		BoundaryPart bp1 = new BoundaryPart(new Edge(0,0,0,2,2,0));
		List<Edge> be = new ArrayList<Edge>();
		be.add(new Edge(0,0,0,1,4,0));
		bp1.setBoundaryEdges(be);
		BoundaryPart bp2 = new BoundaryPart(new Edge(1,4,0,2,2,0));
		assertTrue(bp1.canBeNext(bp2));
		bp2 = new BoundaryPart(new Edge(8,4,0,2,2,0));
		assertFalse(bp1.canBeNext(bp2));
		bp2 = new BoundaryPart(new Edge(0,0,0,2,2,0));
		assertFalse(bp1.canBeNext(bp2));
	}

        /**
         * tests that we  are able to connect a single point to a bassic boundary part.
         */
        public void testConnectSinglePoint() throws DelaunayError{
                //First we fill an empty boundary part
                List<Edge> bps = new ArrayList<Edge>();
		Edge ed = new Edge(0,0,0,1,3,0);
		ed.setDegenerated(false);
                bps.add(ed);
		ed = new Edge(1,3,0,1,5,0);
		ed.setDegenerated(false);
                bps.add(ed);
		ed = new Edge(1,5,0,0,8,0);
		ed.setDegenerated(false);
                bps.add(ed);
                BoundaryPart part = new BoundaryPart(bps);
		//We connect the point and retrieve the resulting triangles
                List<DelaunayTriangle> tri = part.connectPoint(new Point(3,4,0));
		//And we perform our tests. First on the boundary edges.
                bps = part.getBoundaryEdges();
                assertTrue(bps.get(0).equals(new Edge(0,0,0,3,4,0)));
                assertTrue(bps.get(1).equals(new Edge(0,8,0,3,4,0)));
                assertTrue(bps.size()==2);
		//next on the resulting triangles.
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(0,0,0,1,3,0),new Edge(1,3,0,3,4,0), new Edge(3,4,0,0,0,0))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(1,5,0,1,3,0),new Edge(1,3,0,3,4,0), new Edge(3,4,0,1,5,0))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(1,5,0,0,8,0),new Edge(0,8,0,3,4,0), new Edge(3,4,0,1,5,0))));
		assertTrue(tri.size()==3);
        }

	/**
	 * We make a more complicated test, were the point to be added is not on the right
	 * of every edges in the boundary part.
	 * @throws DelaunayError
	 */
	public void testConnectionPartialVisibility() throws DelaunayError{
                //First we fill an empty boundary part
                List<Edge> bps = new ArrayList<Edge>();
		bps.add(new Edge(1,0,0,3,1,0));
		bps.add(new Edge(3,1,0,4,4,0));
		bps.add(new Edge(4,4,0,4,7,0));
		bps.add(new Edge(4,7,0,3,9,0));
		bps.add(new Edge(3,9,0,0,10,0));
                BoundaryPart part = new BoundaryPart(bps);
		//We connect the point and retrieve the resulting triangles
                List<DelaunayTriangle> tri = part.connectPoint(new Point(8,5,0));
		//And we perform our tests. First on the boundary edges.
                bps = part.getBoundaryEdges();
                assertTrue(bps.get(0).equals(new Edge(1,0,0,3,1,0)));
                assertTrue(bps.get(1).equals(new Edge(3,1,0,8,5,0)));
                assertTrue(bps.get(2).equals(new Edge(8,5,0,3,9,0)));
                assertTrue(bps.get(3).equals(new Edge(3,9,0,0,10,0)));
                assertTrue(bps.size()==4);
		//next on the resulting triangles.
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(3,1,0,8,5,0), new Edge(8,5,0,4,4,0), new Edge(4,4,0,3,1,0))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(4,7,0,8,5,0), new Edge(8,5,0,4,4,0), new Edge(4,4,0,4,7,0))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(4,7,0,8,5,0), new Edge(8,5,0,3,9,0), new Edge(3,9,0,4,7,0))));
		assertTrue(tri.size()==3);
	}

	/**
	 * In some cases, we are not able to build a triangle with the point we
	 * want to add and the boundary edges of the BoundaryPart. Consequently,
	 * we must add a degenerated edge to the list of edges.
	 * @throws DelaunayError
	 */
	public void testConnectionNoVisibility() throws DelaunayError{
                //First we fill an empty boundary part
                List<Edge> bps = new ArrayList<Edge>();
		//We make our test with only one edge
		bps.add(new Edge(3,6,0,0,8,0));
		Edge cstr = new Edge(3,6,0,6,0,0);
		BoundaryPart part = new BoundaryPart(bps, cstr);
		//We connect the point and retrieve the resulting triangles
		List<DelaunayTriangle> tri = part.connectPoint(new Point(5,4,0));
		//And we perform our tests. First on the boundary edges.
                bps = part.getBoundaryEdges();
                assertTrue(bps.get(0).equals(new Edge(3,6,0,5,4,0)));
                assertTrue(bps.get(1).equals(new Edge(3,6,0,0,8,0)));
                assertTrue(bps.get(0).isDegenerated());
                assertTrue(bps.size()==2);
		//next on the resulting triangles.
		assertTrue(tri.isEmpty());

	}

	/**
	 * We work on the same basis as in testConnectionNovisibility, but here we will add 
	 * another point, and test the triangle and boundary edges we obtain.
	 * In this test, the originial boundaryEdge sees the second point we will add.
	 */
	public void testConnectionNoVisibilityFurther() throws DelaunayError {
                //First we fill an empty boundary part
                List<Edge> bps = new ArrayList<Edge>();
		//We make our test with only one edge
		bps.add(new Edge(3,6,0,0,8,0));
		Edge cstr = new Edge(3,6,0,6,0,0);
		BoundaryPart part = new BoundaryPart(bps, cstr);
		//We connect the point and retrieve the resulting triangles
		List<DelaunayTriangle> tri = part.connectPoint(new Point(5,4,0));
		//And we perform our tests. First on the boundary edges.
                bps = part.getBoundaryEdges();
                assertTrue(bps.get(0).equals(new Edge(3,6,0,5,4,0)));
                assertTrue(bps.get(1).equals(new Edge(3,6,0,0,8,0)));
                assertTrue(bps.get(0).isDegenerated());
                assertTrue(bps.size()==2);
		//next on the resulting triangles.
		assertTrue(tri.isEmpty());
		//And we check that we properly build triangles after that.
		tri = part.connectPoint(new Point(5,7,0));
		//And we perform our tests. First on the boundary edges.
                bps = part.getBoundaryEdges();
//                assertTrue(bps.get(0).equals(new Edge(3,6,0,5,4,0)));
//                assertTrue(bps.get(1).equals(new Edge(5,4,0,5,7,0)));
//                assertTrue(bps.get(2).equals(new Edge(5,7,0,0,8,0)));

	}

	/**
	 * Test the insertion of a point in a boundary part were the boundaryEdges
	 * list is empty. The resulting list will contain one degenerated Edge;
	 * This test checks that we throw an exception if we try to connect a point
	 * to a BoundaryPart that have nor a boundaryEdges list neither a constraint Edge.
	 * @throws DelaunayError
	 */
	public void testAddPointEmptyBoundary() throws DelaunayError {
		//We don't feed the boundary part, as we want to obtain a degenerated edge.
		List<Edge> bps = new ArrayList<Edge>();
		BoundaryPart part = new BoundaryPart(bps);
		//We connect the point and retrieve the resulting triangles
		List<DelaunayTriangle> tri;
		try{
			//We test the exception throw
			tri = part.connectPoint(new Point(5,4,0));
			assertTrue(false);
		} catch (DelaunayError d){
		}
		part.setConstraint(new Edge(0,0,0,4,4,0));
		tri = part.connectPoint(new Point(2,4,0));
		//And we perform our tests. First on the boundary edges.
                bps = part.getBoundaryEdges();
                assertTrue(bps.get(0).equals(new Edge(0,0,0,2,4,0)));
		assertTrue(tri.isEmpty());

		
		
	}

	/**
	 * On the same basis than the testAddPointEmptyBoundary test, we add a point
	 * to build a triangle from a degenerated Edge.
	 * @throws DelaunayError
	 */
	public void testAddPointEmptyBoundaryFurther() throws DelaunayError {
		//We don't feed the boundary part, as we want to obtain a degenerated edge.
		List<Edge> bps = new ArrayList<Edge>();
		BoundaryPart part = new BoundaryPart(bps);
		//We connect the point and retrieve the resulting triangles
		part.setConstraint(new Edge(0,0,0,4,1,0));
		List<DelaunayTriangle> tri = part.connectPoint(new Point(2,2,0));
		//And we perform our tests. First on the boundary edges.
                bps = part.getBoundaryEdges();
                assertTrue(bps.get(0).equals(new Edge(0,0,0,2,2,0)));
		tri = part.connectPoint(new Point(3,2,0));
		
	}

	/**
	 * Simple test that checks we can't have a null boundaryEdges list.
	 */
	public void testSetNullBoundaries(){
		BoundaryPart part = new BoundaryPart(new Edge());
		part.setBoundaryEdges(null);
		List<Edge>  edg = part.getBoundaryEdges();
		assertNotNull(edg);
		edg = new ArrayList<Edge>();
		part.setBoundaryEdges(edg);
		assertTrue(edg == part.getBoundaryEdges());
	}
}
