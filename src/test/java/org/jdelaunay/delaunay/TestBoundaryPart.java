
package org.jdelaunay.delaunay;

import java.util.ArrayList;
import java.util.LinkedList;
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
		BoundaryPart bp1 = new BoundaryPart(new DEdge(0,0,0,2,2,0));
		List<DEdge> be = new ArrayList<DEdge>();
		be.add(new DEdge(0,0,0,1,4,0));
		bp1.setBoundaryEdges(be);
		BoundaryPart bp2 = new BoundaryPart(new DEdge(1,4,0,2,2,0));
		assertTrue(bp1.canBeNext(bp2));
		bp2 = new BoundaryPart(new DEdge(8,4,0,2,2,0));
		assertFalse(bp1.canBeNext(bp2));
		bp2 = new BoundaryPart(new DEdge(0,0,0,2,2,0));
		assertFalse(bp1.canBeNext(bp2));
	}

	/**
	 * When we set the constraint edge, we must be sure it is in the right sense.
	 * @throws DelaunayError
	 */
	public void testSwapConstraint() throws DelaunayError {
		BoundaryPart bp = new BoundaryPart(new ArrayList<DEdge>());
		bp.setConstraint(new DEdge(2,2,0,0,0,0));
		assertTrue(bp.getConstraint().getStartPoint().equals(new DPoint(0,0,0)));
		assertTrue(bp.getConstraint().getEndPoint().equals(new DPoint(2,2,0)));
	}

        /**
         * tests that we  are able to connect a single point to a bassic boundary part.
         */
        public void testConnectSinglePoint() throws DelaunayError{
                //First we fill an empty boundary part
                List<DEdge> bps = new ArrayList<DEdge>();
		DEdge ed = new DEdge(0,0,0,1,3,0);
		ed.setDegenerated(false);
                bps.add(ed);
		ed = new DEdge(1,3,0,1,5,0);
		ed.setDegenerated(false);
                bps.add(ed);
		ed = new DEdge(1,5,0,0,8,0);
		ed.setDegenerated(false);
                bps.add(ed);
                BoundaryPart part = new BoundaryPart(bps);
		//We connect the point and retrieve the resulting triangles
                List<DTriangle> tri = part.connectPoint(new DPoint(3,4,0));
		//And we perform our tests. First on the boundary edges.
                bps = part.getBoundaryEdges();
                assertTrue(bps.get(0).equals(new DEdge(0,0,0,3,4,0)));
                assertTrue(bps.get(1).equals(new DEdge(0,8,0,3,4,0)));
                assertTrue(bps.size()==2);
		//next on the resulting triangles.
		assertTrue(tri.contains(new DTriangle(new DEdge(0,0,0,1,3,0),new DEdge(1,3,0,3,4,0), new DEdge(3,4,0,0,0,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(1,5,0,1,3,0),new DEdge(1,3,0,3,4,0), new DEdge(3,4,0,1,5,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(1,5,0,0,8,0),new DEdge(0,8,0,3,4,0), new DEdge(3,4,0,1,5,0))));
		assertTrue(tri.size()==3);
        }

	/**
	 * We make a more complicated test, were the point to be added is not on the right
	 * of every edges in the boundary part.
	 * @throws DelaunayError
	 */
	public void testConnectionPartialVisibility() throws DelaunayError{
                //First we fill an empty boundary part
                List<DEdge> bps = new ArrayList<DEdge>();
		bps.add(new DEdge(1,0,0,3,1,0));
		bps.add(new DEdge(3,1,0,4,4,0));
		bps.add(new DEdge(4,4,0,4,7,0));
		bps.add(new DEdge(4,7,0,3,9,0));
		bps.add(new DEdge(3,9,0,0,10,0));
                BoundaryPart part = new BoundaryPart(bps);
		//We connect the point and retrieve the resulting triangles
                List<DTriangle> tri = part.connectPoint(new DPoint(8,5,0));
		//And we perform our tests. First on the boundary edges.
                bps = part.getBoundaryEdges();
                assertTrue(bps.get(0).equals(new DEdge(1,0,0,3,1,0)));
                assertTrue(bps.get(1).equals(new DEdge(3,1,0,8,5,0)));
                assertTrue(bps.get(2).equals(new DEdge(8,5,0,3,9,0)));
                assertTrue(bps.get(3).equals(new DEdge(3,9,0,0,10,0)));
                assertTrue(bps.size()==4);
		//next on the resulting triangles.
		assertTrue(tri.contains(new DTriangle(new DEdge(3,1,0,8,5,0), new DEdge(8,5,0,4,4,0), new DEdge(4,4,0,3,1,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(4,7,0,8,5,0), new DEdge(8,5,0,4,4,0), new DEdge(4,4,0,4,7,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(4,7,0,8,5,0), new DEdge(8,5,0,3,9,0), new DEdge(3,9,0,4,7,0))));
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
                List<DEdge> bps = new ArrayList<DEdge>();
		//We make our test with only one edge
		bps.add(new DEdge(3,6,0,0,8,0));
		DEdge cstr = new DEdge(3,6,0,6,0,0);
		BoundaryPart part = new BoundaryPart(bps, cstr);
		//We connect the point and retrieve the resulting triangles
		List<DTriangle> tri = part.connectPoint(new DPoint(5,4,0));
		//And we perform our tests. First on the boundary edges.
                bps = part.getBoundaryEdges();
                assertTrue(bps.get(0).equals(new DEdge(3,6,0,5,4,0)));
                assertTrue(bps.get(1).equals(new DEdge(3,6,0,0,8,0)));
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
                List<DEdge> bps = new ArrayList<DEdge>();
		//We make our test with only one edge
		bps.add(new DEdge(3,6,0,0,8,0));
		DEdge cstr = new DEdge(3,6,0,6,0,0);
		BoundaryPart part = new BoundaryPart(bps, cstr);
		//We connect the point and retrieve the resulting triangles
		List<DTriangle> tri = part.connectPoint(new DPoint(5,4,0));
		//And we perform our tests. First on the boundary edges.
                bps = part.getBoundaryEdges();
                assertTrue(bps.get(0).equals(new DEdge(3,6,0,5,4,0)));
                assertTrue(bps.get(1).equals(new DEdge(3,6,0,0,8,0)));
                assertTrue(bps.get(0).isDegenerated());
                assertTrue(bps.size()==2);
		//next on the resulting triangles.
		assertTrue(tri.isEmpty());
		//And we check that we properly build triangles after that.
		tri = part.connectPoint(new DPoint(5,7,0));
		//And we perform our tests. First on the boundary edges.
                bps = part.getBoundaryEdges();
                assertTrue(bps.get(0).equals(new DEdge(3,6,0,5,4,0)));
                assertTrue(bps.get(1).equals(new DEdge(5,4,0,5,7,0)));
                assertTrue(bps.get(2).equals(new DEdge(5,7,0,0,8,0)));
		assertTrue(bps.size()==3);
		assertTrue(tri.contains(new DTriangle(new DEdge(3,6,0,5,4,0), new DEdge(5,4,0,5,7,0), new DEdge(5,7,0,3,6,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(3,6,0,0,8,0), new DEdge(0,8,0,5,7,0), new DEdge(5,7,0,3,6,0))));
		assertTrue(tri.size()==2);

	}

	/**
	 * We work on the same basis as in testConnectionNovisibility, but here we will two
	 * points that will form two degenerated edges, and we will connect another point
	 * to build triangles.
	 */
	public void testConnectiontwoDegenEdges() throws DelaunayError {
                //First we fill an empty boundary part
                List<DEdge> bps = new ArrayList<DEdge>();
		//We make our test with only one edge
		bps.add(new DEdge(3,6,0,0,8,0));
		DEdge cstr = new DEdge(3,6,0,6,0,0);
		BoundaryPart part = new BoundaryPart(bps, cstr);
		//We connect the point and retrieve the resulting triangles
		List<DTriangle> tri = part.connectPoint(new DPoint(5,4,0));
		tri = part.connectPoint(new DPoint(7,2,0));
		//And we perform our tests. First on the boundary edges.
                bps = part.getBoundaryEdges();
                assertTrue(bps.get(0).equals(new DEdge(3,6,0,5,4,0)));
                assertTrue(bps.get(1).equals(new DEdge(5,4,0,7,2,0)));
                assertTrue(bps.get(2).equals(new DEdge(3,6,0,0,8,0)));
                assertTrue(bps.get(0).isDegenerated());
                assertTrue(bps.get(1).isDegenerated());
                assertTrue(bps.size()==3);
		//next on the resulting triangles.
		assertTrue(tri.isEmpty());
		//And we check that we properly build triangles after that.
		tri = part.connectPoint(new DPoint(5,7,0));
		//And we perform our tests. First on the boundary edges.
                bps = part.getBoundaryEdges();
                assertTrue(bps.get(0).equals(new DEdge(3,6,0,5,4,0)));
                assertTrue(bps.get(1).equals(new DEdge(5,4,0,7,2,0)));
                assertTrue(bps.get(2).equals(new DEdge(7,2,0,5,7,0)));
                assertTrue(bps.get(3).equals(new DEdge(5,7,0,0,8,0)));
		assertTrue(bps.size()==4);
	}

	/**
	 * We work on the same basis as in testConnectionNovisibility, but here we will two
	 * points that will form two degenerated edges, and we will connect another point
	 * to build triangles.
	 * In this test, one edge
	 */
	public void testConnectDegenEdges() throws DelaunayError {
                //First we fill an empty boundary part
                List<DEdge> bps = new ArrayList<DEdge>();
		//We make our test with only one edge
		bps.add(new DEdge(3,6,0,0,8,0));
		DEdge cstr = new DEdge(3,6,0,6,0,0);
		BoundaryPart part = new BoundaryPart(bps, cstr);
		//We connect the point and retrieve the resulting triangles
		List<DTriangle> tri = part.connectPoint(new DPoint(5,4,0));
		tri = part.connectPoint(new DPoint(7,2,0));
		//And we perform our tests. First on the boundary edges.
                bps = part.getBoundaryEdges();
                assertTrue(bps.get(0).equals(new DEdge(3,6,0,5,4,0)));
                assertTrue(bps.get(1).equals(new DEdge(5,4,0,7,2,0)));
                assertTrue(bps.get(2).equals(new DEdge(3,6,0,0,8,0)));
                assertTrue(bps.get(0).isDegenerated());
                assertTrue(bps.get(1).isDegenerated());
                assertTrue(bps.size()==3);
		//next on the resulting triangles.
		assertTrue(tri.isEmpty());
		//And we check that we properly build triangles after that.
		tri = part.connectPoint(new DPoint(7,3,0));
		//And we perform our tests. First on the boundary edges.
                bps = part.getBoundaryEdges();
                assertTrue(bps.get(0).equals(new DEdge(3,6,0,5,4,0)));
                assertTrue(bps.get(1).equals(new DEdge(5,4,0,7,2,0)));
                assertTrue(bps.get(2).equals(new DEdge(7,2,0,7,3,0)));
                assertTrue(bps.get(3).equals(new DEdge(7,3,0,3,6,0)));
                assertTrue(bps.get(4).equals(new DEdge(3,6,0,0,8,0)));
		assertTrue(bps.size()==5);
	}

	/**
	 * We work on the same basis as in testConnectionNovisibility, but here we will two
	 * points that will form two degenerated edges, and we will connect another point
	 * to build triangles.
	 * In this test, one edge
	 */
	public void testConnectDegenEdgesBis() throws DelaunayError {
                //First we fill an empty boundary part
                List<DEdge> bps = new ArrayList<DEdge>();
		//We make our test with only one edge
		bps.add(new DEdge(3,6,0,0,8,0));
		DEdge cstr = new DEdge(3,6,0,6,0,0);
		BoundaryPart part = new BoundaryPart(bps, cstr);
		//We connect the point and retrieve the resulting triangles
		List<DTriangle> tri = part.connectPoint(new DPoint(5,4,0));
		tri = part.connectPoint(new DPoint(7,2,0));
		//And we perform our tests. First on the boundary edges.
                bps = part.getBoundaryEdges();
		//next on the resulting triangles.
		assertTrue(tri.isEmpty());
		//And we check that we properly build triangles after that.
		tri = part.connectPoint(new DPoint(8,0,0));
		//And we perform our tests. First on the boundary edges.
                bps = part.getBoundaryEdges();
                assertTrue(bps.get(0).equals(new DEdge(3,6,0,8,0,0)));
                assertTrue(bps.get(1).equals(new DEdge(8,0,0,7,2,0)));
                assertTrue(bps.get(2).equals(new DEdge(7,2,0,5,4,0)));
                assertTrue(bps.get(3).equals(new DEdge(5,4,0,3,6,0)));
                assertTrue(bps.get(4).equals(new DEdge(3,6,0,0,8,0)));
		assertTrue(bps.size()==5);
	}

	/**
	 * We add colinear points to a boundary part without boundary edges in it.
	 * We should obtain 3 degenerated edges.
	 * @throws DelaunayError
	 */
	public void testConnectThreeColinearDegen() throws DelaunayError {
                //First we fill an empty boundary part
                List<DEdge> bps = new ArrayList<DEdge>();
		//We make our test with only one edge
		DEdge cstr = new DEdge(3,6,0,6,0,0);
		BoundaryPart part = new BoundaryPart(bps, cstr);
		//We connect the point and retrieve the resulting triangles
		List<DTriangle> tri = part.connectPoint(new DPoint(5,4,0));
		tri = part.connectPoint(new DPoint(7,2,0));
		tri = part.connectPoint(new DPoint(9,0,0));
		//And we perform our tests. First on the boundary edges.
                bps = part.getBoundaryEdges();
                assertTrue(bps.get(0).equals(new DEdge(3,6,0,5,4,0)));
                assertTrue(bps.get(1).equals(new DEdge(5,4,0,7,2,0)));
                assertTrue(bps.get(2).equals(new DEdge(7,2,0,9,0,0)));
                assertTrue(bps.size()==3);
		assertTrue(tri.isEmpty());
	}

	public void testInvisiblePoint() throws DelaunayError{
		//We fill the boundary part :
		List<DEdge> bps = new ArrayList<DEdge>();
		bps.add(new DEdge(0,4,0,4,5,0));
		//We create the constraint.
		DEdge cstr = new DEdge(0,4,0,10,0,0);
		//We create the boundary part.
		BoundaryPart part = new BoundaryPart(bps, cstr);
		//We connect the point and retrieve the resulting triangles.
		List<DTriangle> tri = part.connectPoint(new DPoint(6,7,0));
		//And we perform our tests. First on the boundary edges.
                bps = part.getBoundaryEdges();
                assertTrue(bps.get(0).equals(new DEdge(0,4,0,4,5,0)));
                assertTrue(bps.get(1).equals(new DEdge(4,5,0,6,7,0)));
                assertTrue(bps.size()==2);
		assertTrue(tri.isEmpty());

	}

	/**
	 * Test the insertion of a point in a boundary part were the boundaryEdges
	 * list is empty. The resulting list will contain one degenerated DEdge;
	 * This test checks that we throw an exception if we try to connect a point
	 * to a BoundaryPart that have nor a boundaryEdges list neither a constraint DEdge.
	 * @throws DelaunayError
	 */
	public void testAddPointEmptyBoundary() throws DelaunayError {
		//We don't feed the boundary part, as we want to obtain a degenerated edge.
		List<DEdge> bps = new ArrayList<DEdge>();
		BoundaryPart part = new BoundaryPart(bps);
		//We connect the point and retrieve the resulting triangles
		List<DTriangle> tri;
		try{
			//We test the exception throw whent trying to connect a point to a totally
			//empty boundaryPart.
			tri = part.connectPoint(new DPoint(5,4,0));
			assertTrue(false);
		} catch (DelaunayError d){
		}
		part.setConstraint(new DEdge(0,0,0,4,4,0));
		tri = part.connectPoint(new DPoint(2,4,0));
		//And we perform our tests. First on the boundary edges.
                bps = part.getBoundaryEdges();
                assertTrue(bps.get(0).equals(new DEdge(0,0,0,2,4,0)));
		//next on the resulting triangles.
		assertTrue(tri.isEmpty());

		
	}

	/**
	 * On the same basis than the testAddPointEmptyBoundary test, we add a point
	 * to build a triangle from a degenerated DEdge.
	 * @throws DelaunayError
	 */
	public void testAddPointEmptyBoundaryFurther() throws DelaunayError {
		//We don't feed the boundary part, as we want to obtain a degenerated edge.
		List<DEdge> bps = new ArrayList<DEdge>();
		BoundaryPart part = new BoundaryPart(bps);
		//We connect the point and retrieve the resulting triangles
		part.setConstraint(new DEdge(0,0,0,4,1,0));
		List<DTriangle> tri = part.connectPoint(new DPoint(2,2,0));
		//And we perform our tests. First on the boundary edges.
                bps = part.getBoundaryEdges();
                assertTrue(bps.get(0).equals(new DEdge(0,0,0,2,2,0)));
		//next on the resulting triangles.
		assertTrue(tri.isEmpty());
		tri = part.connectPoint(new DPoint(3,2,0));
		//And we perform our tests. First on the boundary edges.
                bps = part.getBoundaryEdges();
		assertTrue(bps.get(0).equals(new DEdge(0,0,0,3,2,0)));
		assertTrue(bps.get(1).equals(new DEdge(3,2,0,2,2,0)));
		assertTrue(bps.get(2).equals(new DEdge(2,2,0,0,0,0)));
	}

	/**
	 * The BoundaryPart stores the edges that was added to the mesh during the
	 * last insertion of a point. We check we retrieve all the desired edges here.
	 * 
	 * @throws DelaunayError
	 */
	public void testRetrieveAddedEdges() throws DelaunayError {
		List<DEdge> bps = new ArrayList<DEdge>();
		bps.add(new DEdge(7,0,0,6,2,0));
		bps.add(new DEdge(6,2,0,4,4,0));
		bps.add(new DEdge(4,4,0,0,5,0));
		BoundaryPart part = new BoundaryPart(bps);
		part.setBoundaryEdges(bps);
		List<DTriangle> tri = part.connectPoint(new DPoint(6,5,0));
		List<DEdge> added = part.getAddedEdges();
		assertTrue(added.size()==4);
		assertTrue(added.contains(new DEdge(7,0,0,6,5,0)));
		assertTrue(added.contains(new DEdge(6,2,0,6,5,0)));
		assertTrue(added.contains(new DEdge(4,4,0,6,5,0)));
		assertTrue(added.contains(new DEdge(0,5,0,6,5,0)));
	}

	/**
	 * The BoundaryPart stores the edges that must be added in the badEdgeQueueList
	 * due to the last insertion of a point. We check that we retrieve all the expected.
	 * bad edges
	 * @throws DelaunayError
	 */
	public void testRetrieveBadEdges() throws DelaunayError {
		List<DEdge> bps = new ArrayList<DEdge>();
		bps.add(new DEdge(7,0,0,6,2,0));
		bps.add(new DEdge(6,2,0,4,4,0));
		bps.add(new DEdge(4,4,0,0,5,0));
		BoundaryPart part = new BoundaryPart(bps);
		part.setBoundaryEdges(bps);
		List<DTriangle> tri = part.connectPoint(new DPoint(6,5,0));
		List<DEdge> bad = part.getBadEdges();
		assertTrue(bad.size()==3);
		assertTrue(bad.contains(new DEdge(7,0,0,6,2,0)));
		assertTrue(bad.contains(new DEdge(6,2,0,4,4,0)));
		assertTrue(bad.contains(new DEdge(4,4,0,0,5,0)));
		
	}

	/**
	 * We check that we retrieve the expected bad and added edges when 
	 * inserting a point that create a degenerated edge.
	 * @throws DelaunayError
	 */
	public void testRetrieveBadAddedEdgesDegen() throws DelaunayError{
		List<DEdge> bps = new ArrayList<DEdge>();
		DEdge cstr = new DEdge(0,0,0,8,1,0);
		BoundaryPart part = new BoundaryPart(bps, cstr);
		List<DTriangle> tri = part.connectPoint(new DPoint(2,2,0));
		assertTrue(tri.isEmpty());
		List<DEdge> bad = part.getBadEdges();
		assertTrue(bad.isEmpty());
		List<DEdge> added = part.getAddedEdges();
		assertTrue(added.size()==1);
		assertTrue(added.contains(new DEdge(0,0,0,2,2,0)));
		tri = part.connectPoint(new DPoint(4,4,0));
		assertTrue(tri.isEmpty());
		bad = part.getBadEdges();
		assertTrue(bad.isEmpty());
		added = part.getAddedEdges();
		assertTrue(added.size()==1);
		assertTrue(added.contains(new DEdge(2,2,0,4,4,0)));
		//We add a point that will build two triangles and three edges, but
		//no bad edges.
		tri = part.connectPoint(new DPoint(5,3,0));
		assertTrue(tri.size()==2);
		assertTrue(tri.contains(new DTriangle(new DEdge(0,0,0,5,3,0), new DEdge(5,3,0,2,2,0), new DEdge(2,2,0,0,0,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(4,4,0,5,3,0), new DEdge(5,3,0,2,2,0), new DEdge(2,2,0,4,4,0))));
		bad = part.getBadEdges();
		assertTrue(bad.isEmpty());
		added = part.getAddedEdges();
		assertTrue(added.size()==3);
		assertTrue(added.contains(new DEdge(0,0,0,5,3,0)));
		assertTrue(added.contains(new DEdge(2,2,0,5,3,0)));
		assertTrue(added.contains(new DEdge(5,3,0,4,4,0)));
		//We add a point that will build three triangles and four edges, and
		//three bad edges.
		tri = part.connectPoint(new DPoint(5,6,0));
		assertTrue(tri.size()==3);
		assertTrue(tri.contains(new DTriangle(new DEdge(0,0,0,5,6,0), new DEdge(5,6,0,2,2,0), new DEdge(2,2,0,0,0,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(4,4,0,5,6,0), new DEdge(5,6,0,2,2,0), new DEdge(2,2,0,4,4,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(4,4,0,5,6,0), new DEdge(5,6,0,5,3,0), new DEdge(5,3,0,4,4,0))));
		bad = part.getBadEdges();
		assertTrue(bad.size()==3);
		assertTrue(bad.contains(new DEdge(5,3,0,4,4,0)));
		assertTrue(bad.contains(new DEdge(2,2,0,4,4,0)));
		assertTrue(bad.contains(new DEdge(0,0,0,2,2,0)));
		added = part.getAddedEdges();
		assertTrue(added.size()==4);
		assertTrue(added.contains(new DEdge(5,6,0,5,3,0)));
		assertTrue(added.contains(new DEdge(2,2,0,5,6,0)));
		assertTrue(added.contains(new DEdge(5,6,0,4,4,0)));
		assertTrue(added.contains(new DEdge(5,6,0,0,0,0)));

	}

	/**
	 * Simple test that checks we can't have a null boundaryEdges list.
	 */
	public void testSetNullBoundaries(){
		BoundaryPart part = new BoundaryPart(new DEdge(0,0,0,1,1,0));
		part.setBoundaryEdges(null);
		LinkedList<DEdge>  edg = (LinkedList) part.getBoundaryEdges();
		assertNotNull(edg);
		edg = new LinkedList<DEdge>();
		part.setBoundaryEdges(edg);
		assertTrue(edg == part.getBoundaryEdges());
	}

	/**
	 * Tests the pointIsLower method.
	 */
	public void testIsLower() throws DelaunayError{
		BoundaryPart part = new BoundaryPart(new DEdge(0,0,0,4,1,0));
		assertTrue(part.pointIsLower(new DPoint(3,0,0)));
		assertFalse(part.pointIsLower(new DPoint(3,2,0)));
	}

	/**
	 * Tests the pointIsUpper method.
	 */
	public void testIsUpper() throws DelaunayError {
		BoundaryPart part = new BoundaryPart(new DEdge(0,0,0,4,1,0));
		assertFalse(part.pointIsUpper(new DPoint(3,0,0)));
		assertTrue(part.pointIsUpper(new DPoint(3,2,0)));
	}

	/**
	 * Tests the pointIsUpper method.
	 */
	public void testIsConstraintRightPoint() throws DelaunayError {
		BoundaryPart part = new BoundaryPart(new DEdge(0,0,0,4,1,0));
		assertFalse(part.isConstraintRightPoint(new DPoint(3,0,0)));
		assertFalse(part.isConstraintRightPoint(new DPoint(0,0,0)));
		assertTrue(part.isConstraintRightPoint(new DPoint(4,1,0)));
	}

	/**
	 * Test that we add the constraint of the BoundaryPart, and not a random duplicate,
	 * when connecting its right point
	 *
	 * @throws DelaunayError
	 */
	public void testAddConstraintRightPoint() throws DelaunayError {
		DEdge cstr = new DEdge(0,0,0,7,2,0);
		List<DEdge> bps = new ArrayList<DEdge>();
		bps.add(new DEdge(0,0,0,2,3,0));
		bps.add(new DEdge(2,3,0,2,6,0));
		BoundaryPart bp = new BoundaryPart(bps, cstr);
		List<DTriangle> tri = bp.connectPoint(new DPoint(7,2,0));
		bps = bp.getBoundaryEdges();
		//We check the boundary edges.
		assertTrue(bps.size()==2);
		assertTrue(bps.get(0).equals(new DEdge(0,0,0,7,2,0)));
		assertTrue(bps.get(1).equals(new DEdge(7,2,0,2,6,0)));
		assertTrue(bps.get(0)==cstr);
		//And we test the triangles, because we like it.
		assertTrue(tri.size()==2);
		assertTrue(tri.contains(new DTriangle(new DEdge(0,0,0,7,2,0), new DEdge(7,2,0,2,3,0), new DEdge(2,3,0,0,0,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(2,6,0,7,2,0), new DEdge(7,2,0,2,3,0), new DEdge(2,3,0,2,6,0))));
		
	}

	/**
	 * Tests that we add the other edge if it is eligible to be added and
	 * given in argument to connectPoint.
	 * @throws DelaunayError
	 */
	public void testAddPointAndEdge()throws DelaunayError {
		DEdge cstr = new DEdge(0,0,0,7,2,0);
		List<DEdge> bps = new ArrayList<DEdge>();
		bps.add(new DEdge(0,0,0,2,3,0));
		bps.add(new DEdge(2,3,0,2,6,0));
		BoundaryPart bp = new BoundaryPart(bps, cstr);
		DEdge other = new DEdge(7,2,0,2,6,0);
		List<DTriangle> tri = bp.connectPoint(new DPoint(7,2,0), other);
		bps = bp.getBoundaryEdges();
		//We check the boundary edges.
		assertTrue(bps.size()==2);
		assertTrue(bps.get(0).equals(new DEdge(0,0,0,7,2,0)));
		assertTrue(bps.get(1).equals(new DEdge(7,2,0,2,6,0)));
		assertTrue(bps.get(0)==cstr);
		assertTrue(bps.get(1)==other);
		//And we test the triangles, because we like it.
		assertTrue(tri.size()==2);
		assertTrue(tri.contains(new DTriangle(new DEdge(0,0,0,7,2,0), new DEdge(7,2,0,2,3,0), new DEdge(2,3,0,0,0,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(2,6,0,7,2,0), new DEdge(7,2,0,2,3,0), new DEdge(2,3,0,2,6,0))));
		//Let's try again, but this time other and cstr won't share a point.
		bps = new LinkedList<DEdge>();
		bps.add(new DEdge(0,0,0,2,3,0));
		bps.add(new DEdge(2,3,0,2,6,0));
		bp = new BoundaryPart(bps, cstr);
		other = new DEdge(7,5,0,2,6,0);
		tri = bp.connectPoint(new DPoint(7,5,0), other);
		//We check the boundary edges.
		assertTrue(bps.size()==2);
		assertTrue(bps.get(0).equals(new DEdge(0,0,0,7,5,0)));
		assertTrue(bps.get(1).equals(new DEdge(7,5,0,2,6,0)));
		assertTrue(bps.get(1)==other);
		//And we test the triangles, because we like it.
		assertTrue(tri.size()==2);
		assertTrue(tri.contains(new DTriangle(new DEdge(0,0,0,7,5,0), new DEdge(7,5,0,2,3,0), new DEdge(2,3,0,0,0,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(2,6,0,7,5,0), new DEdge(7,5,0,2,3,0), new DEdge(2,3,0,2,6,0))));
	}

	/**
	 * Performs a simple split on a boundary DEdge.
	 * @throws DelaunayError
	 */
	public void testSimpleSplit() throws DelaunayError {
		BoundaryPart bp;
		List<DEdge> bounds = new ArrayList<DEdge>();
		//We prepare the first BP
		DEdge cstr = new DEdge(0,0,0,5,1,0);
		cstr.setLocked(true);
		bounds.add(new DEdge(0,0,0,2,2,0));
		bounds.add(new DEdge(2,2,0,2,4,0));
		bounds.add(new DEdge(2,4,0,0,6,0));
		bp = new BoundaryPart(bounds, cstr);
		//We split the first bp
		BoundaryPart res = bp.split(new DEdge(2,2,0,5,2,0));
		//and we make our tests.
		//On the result
		assertTrue(res.getConstraint().equals(new DEdge(2,2,0,5,2,0)));
		assertTrue(res.getBoundaryEdges().size()==2);
		assertTrue(res.getBoundaryEdges().get(0).equals(new DEdge(2,2,0,2,4,0)));
		assertTrue(res.getBoundaryEdges().get(1).equals(new DEdge(2,4,0,0,6,0)));
		//On the first BP
		assertTrue(bp.getConstraint().equals(new DEdge(0,0,0,5,1,0)));
		assertTrue(bp.getBoundaryEdges().size()==1);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new DEdge(0,0,0,2,2,0)));
	}

	/**
	 * Performs a split on the start point of the first boundary edge of the BP
	 * @throws DelaunayError
	 */
	public void testSplitStartMostPoint() throws DelaunayError{
		BoundaryPart bp;
		List<DEdge> bounds = new ArrayList<DEdge>();
		//We prepare the first BP
		DEdge cstr = new DEdge(0,0,0,5,1,0);
		cstr.setLocked(true);
		bounds.add(new DEdge(0,0,0,2,2,0));
		bounds.add(new DEdge(2,2,0,2,4,0));
		bounds.add(new DEdge(2,4,0,0,6,0));
		bp = new BoundaryPart(bounds, cstr);
		//We split the first bp
		BoundaryPart res = bp.split(new DEdge(0,0,0,5,2,0));
		//and we make our tests.
		//On the result
		assertTrue(res.getConstraint().equals(new DEdge(0,0,0,5,2,0)));
		assertTrue(res.getBoundaryEdges().size()==3);
		assertTrue(res.getBoundaryEdges().get(0).equals(new DEdge(0,0,0,2,2,0)));
		assertTrue(res.getBoundaryEdges().get(1).equals(new DEdge(2,2,0,2,4,0)));
		assertTrue(res.getBoundaryEdges().get(2).equals(new DEdge(2,4,0,0,6,0)));
		//On the first BP
		assertTrue(bp.getConstraint().equals(new DEdge(0,0,0,5,1,0)));
		assertTrue(bp.getBoundaryEdges().isEmpty());
		
	}

	/**
	 * Performs a split on the start point of the first boundary edge of the BP
	 * @throws DelaunayError
	 */
	public void testSplitEndMostPoint() throws DelaunayError{
		BoundaryPart bp;
		List<DEdge> bounds = new ArrayList<DEdge>();
		//We prepare the first BP
		DEdge cstr = new DEdge(0,0,0,5,1,0);
		cstr.setLocked(true);
		bounds.add(new DEdge(0,0,0,2,2,0));
		bounds.add(new DEdge(2,2,0,2,4,0));
		bounds.add(new DEdge(2,4,0,0,6,0));
		bp = new BoundaryPart(bounds, cstr);
		//We split the first bp
		BoundaryPart res = bp.split(new DEdge(0,6,0,5,2,0));
		//and we make our tests.
		//On the result
		assertTrue(res.getConstraint().equals(new DEdge(0,6,0,5,2,0)));
		assertTrue(res.getBoundaryEdges().isEmpty());
		//On the first BP
		assertTrue(bp.getConstraint().equals(new DEdge(0,0,0,5,1,0)));
		assertTrue(bp.getBoundaryEdges().size()==3);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new DEdge(0,0,0,2,2,0)));
		assertTrue(bp.getBoundaryEdges().get(1).equals(new DEdge(2,2,0,2,4,0)));
		assertTrue(bp.getBoundaryEdges().get(2).equals(new DEdge(2,4,0,0,6,0)));
	}

	/**
	 * Test that an exception is throws when trying to split a BP with an edge that
	 * is not linked to it.
	 * @throws DelaunayError
	 */
	public void testSplitException() throws DelaunayError {
		BoundaryPart bp;
		List<DEdge> bounds = new ArrayList<DEdge>();
		//We prepare the first BP
		DEdge cstr = new DEdge(0,0,0,5,1,0);
		cstr.setLocked(true);
		bounds.add(new DEdge(0,0,0,2,2,0));
		bounds.add(new DEdge(2,2,0,2,4,0));
		bounds.add(new DEdge(2,4,0,0,6,0));
		bp = new BoundaryPart(bounds, cstr);
		//We split the first bp, an exception is supposed to be thrown
		try{
			bp.split(new DEdge(8,6,0,5,2,0));
			assertTrue(false);
		}catch (DelaunayError d){
		}
		assertTrue(true);
	}

	/**
	 * Tests that an exception is thrown when using a null (sic ! ) constraint
	 * in split.
	 * @throws DelaunayError
	 */
	public void testSplitExceptionEmptyCstr() throws DelaunayError {
		BoundaryPart bp;
		List<DEdge> bounds = new ArrayList<DEdge>();
		//We prepare the first BP
		DEdge cstr = new DEdge(0,0,0,5,1,0);
		cstr.setLocked(true);
		bounds.add(new DEdge(0,0,0,2,2,0));
		bounds.add(new DEdge(2,2,0,2,4,0));
		bounds.add(new DEdge(2,4,0,0,6,0));
		bp = new BoundaryPart(bounds, cstr);
		//We split the first bp, an exception is supposed to be thrown
		try{
			bp.split(null);
			assertTrue(false);
		}catch (DelaunayError d){
		}
		assertTrue(true);
		
	}

	/**
	 * Test that an exception is thrown when trying to split a BP without any boundary edge.
	 * @throws DelaunayError
	 */
	public void testSplitExceptionEmpty() throws DelaunayError {
		BoundaryPart bp;
		List<DEdge> bounds = new ArrayList<DEdge>();
		//We prepare the first BP
		DEdge cstr = new DEdge(0,0,0,5,1,0);
		cstr.setLocked(true);
		bp = new BoundaryPart(bounds, cstr);
		//We split the first bp, an exception is supposed to be thrown
		try{
			bp.split(new DEdge(0,0,0,5,2,0));
			assertTrue(false);
		}catch (DelaunayError d){
		}
		try{
			bp.split(new DEdge(10,0,0,5,2,0));
			assertTrue(false);
		}catch (DelaunayError d){
		}
		assertTrue(true);
	}

	/**
	 * Performs a split on a BP with degen edges.
	 * @throws DelaunayError
	 */
	public void testSplitDegen() throws DelaunayError {
		BoundaryPart bp;
		DEdge deg;
		List<DEdge> bounds = new ArrayList<DEdge>();
		//We prepare the first BP
		DEdge cstr = new DEdge(6,12,0,15,0,0);
		deg = new DEdge(6,12,0,8,11,0);
		deg.setDegenerated(true);
		bounds.add(deg);
		deg = new DEdge(8,11,0,10,10,0);
		deg.setDegenerated(true);
		bounds.add(deg);
		deg = new DEdge(6,12,0,0,13,0);
		bounds.add(deg);
		bp = new BoundaryPart(bounds, cstr);
		//We split the first bp
		BoundaryPart res = bp.split(new DEdge(10,10,0,12,9,0));
		//and we make our tests.
		//On the result
		assertTrue(res.getConstraint().equals(new DEdge(12,9,0,10,10,0)));
		assertTrue(res.getBoundaryEdges().size()==3);
		assertTrue(res.getBoundaryEdges().get(0).equals(new DEdge(10,10,0,8,11,0)));
		assertTrue(res.getBoundaryEdges().get(0).isShared());
		assertTrue(res.getBoundaryEdges().get(1).equals(new DEdge(8,11,0,6,12,0)));
		assertTrue(res.getBoundaryEdges().get(1).isShared());
		assertTrue(res.getBoundaryEdges().get(2).equals(new DEdge(6,12,0,0,13,0)));
		//On the first BP
		assertTrue(bp.getConstraint().equals(new DEdge(6,12,0,15,0,0)));
		assertTrue(bp.getBoundaryEdges().size()==2);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new DEdge(8,11,0,6,12,0)));
		assertTrue(bp.getBoundaryEdges().get(0).isShared());
		assertTrue(bp.getBoundaryEdges().get(1).equals(new DEdge(10,10,0,8,11,0)));
		assertTrue(bp.getBoundaryEdges().get(1).isShared());
	}

	/**
	 * 
	 * @throws DelaunayError
	 */
	public void testSplitDegenInsertPoint() throws DelaunayError {
		BoundaryPart bp;
		DEdge deg, deg2;
		List<DEdge> bounds = new ArrayList<DEdge>();
		//We prepare the first BP
		DEdge cstr = new DEdge(6,12,0,15,0,0);
		deg = new DEdge(6,12,0,8,11,0);
		deg.setDegenerated(true);
		deg2 = deg;
		bounds.add(deg);
		deg = new DEdge(8,11,0,10,10,0);
		deg.setDegenerated(true);
		bounds.add(deg);
		deg = new DEdge(6,12,0,0,13,0);
		bounds.add(deg);
		bp = new BoundaryPart(bounds, cstr);
		//We split the first bp
		BoundaryPart res = bp.split(new DEdge(10,10,0,12,9,0));
		List<DTriangle> tri = res.connectPoint(new DPoint(10,11,0));
		//we test the tiangles
		assertTrue(tri.size()==2);
		assertTrue(tri.contains(new DTriangle(new DEdge(10,10,0,10,11,0), new DEdge(10,11,0,8,11,0), new DEdge(8,11,0,10,10,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(6,12,0,10,11,0), new DEdge(10,11,0,8,11,0), new DEdge(8,11,0,6,12,0))));
		//The added and bad edges, now.
		List<DEdge> added = res.getAddedEdges();
		List<DEdge> bad = res.getBadEdges();
		//The added edges
		assertTrue(added.size()==3);
		assertTrue(added.contains(new DEdge(6,12,0,10,11,0)));
		assertTrue(added.contains(new DEdge(8,11,0,10,11,0)));
		assertTrue(added.contains(new DEdge(10,10,0,10,11,0)));
		//The bad edges
		assertTrue(bad.isEmpty());
		assertFalse(deg2.isDegenerated());
		//And the boundary
		List<DEdge> bList = res.getBoundaryEdges();
		assertTrue(bList.size()==3);
		assertTrue(bList.get(0).equals(new DEdge(10,10,0,10,11,0)));
		assertTrue(bList.get(1).equals(new DEdge(10,11,0,6,12,0)));
		assertTrue(bList.get(2).equals(new DEdge(6,12,0,0,13,0)));
	}

	/**
	 *
	 * @throws DelaunayError
	 */
	public void testSplitDegenInsertPointBis() throws DelaunayError {
		BoundaryPart bp;
		DEdge deg, deg2;
		List<DEdge> bounds = new ArrayList<DEdge>();
		//We prepare the first BP
		DEdge cstr = new DEdge(6,12,0,15,0,0);
		deg = new DEdge(6,12,0,8,11,0);
		deg.setDegenerated(true);
		deg2 = deg;
		bounds.add(deg);
		deg = new DEdge(8,11,0,10,10,0);
		deg.setDegenerated(true);
		bounds.add(deg);
		deg = new DEdge(6,12,0,0,13,0);
		bounds.add(deg);
		bp = new BoundaryPart(bounds, cstr);
		//We split the first bp
		BoundaryPart res = bp.split(new DEdge(10,10,0,12,9,0));
		List<DTriangle> tri = res.connectPoint(new DPoint(10,14,0));
		//we test the tiangles
		assertTrue(tri.size()==3);
		assertTrue(tri.contains(new DTriangle(new DEdge(10,10,0,10,14,0), new DEdge(10,14,0,8,11,0), new DEdge(8,11,0,10,10,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(6,12,0,10,14,0), new DEdge(10,14,0,8,11,0), new DEdge(8,11,0,6,12,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(6,12,0,10,14,0), new DEdge(10,14,0,0,13,0), new DEdge(0,13,0,6,12,0))));
		//The added and bad edges, now.
		List<DEdge> added = res.getAddedEdges();
		List<DEdge> bad = res.getBadEdges();
		//The added edges
		assertTrue(added.size()==4);
		assertTrue(added.contains(new DEdge(6,12,0,10,14,0)));
		assertTrue(added.contains(new DEdge(8,11,0,10,14,0)));
		assertTrue(added.contains(new DEdge(10,10,0,10,14,0)));
		assertTrue(added.contains(new DEdge(0,13,0,10,14,0)));
		//The bad edges
		assertTrue(bad.size()==1);
		assertTrue(bad.contains(new DEdge(0,13,0,6,12,0)));
		assertFalse(deg2.isDegenerated());
		//And the boundary
		List<DEdge> bList = res.getBoundaryEdges();
		assertTrue(bList.size()==2);
		assertTrue(bList.get(0).equals(new DEdge(10,10,0,10,14,0)));
		assertTrue(bList.get(0).getStartPoint().equals(new DPoint(10,10,0)));
		assertTrue(bList.get(1).equals(new DEdge(10,14,0,0,13,0)));
		assertTrue(bList.get(1).getStartPoint().equals(new DPoint(10,14,0)));
	}

	/**
	 *
	 * @throws DelaunayError
	 */
	public void testSplitDegenInsertPointLower() throws DelaunayError {
		BoundaryPart bp;
		DEdge deg, deg2;
		List<DEdge> bounds = new ArrayList<DEdge>();
		//We prepare the first BP
		DEdge cstr = new DEdge(6,12,0,15,0,0);
		deg = new DEdge(6,12,0,8,11,0);
		deg.setDegenerated(true);
		deg2 = deg;
		bounds.add(deg);
		deg = new DEdge(8,11,0,10,10,0);
		deg.setDegenerated(true);
		bounds.add(deg);
		deg = new DEdge(6,12,0,0,13,0);
		bounds.add(deg);
		bp = new BoundaryPart(bounds, cstr);
		//We split the first bp
		BoundaryPart res = bp.split(new DEdge(10,10,0,12,9,0));
		List<DTriangle> tri = bp.connectPoint(new DPoint(11,8,0));
		//we test the tiangles
		assertTrue(tri.size()==2);
		assertTrue(tri.contains(new DTriangle(new DEdge(10,10,0,11,8,0), new DEdge(11,8,0,8,11,0), new DEdge(8,11,0,10,10,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(6,12,0,11,8,0), new DEdge(11,8,0,8,11,0), new DEdge(8,11,0,6,12,0))));
		//The added and bad edges, now.
		List<DEdge> added = bp.getAddedEdges();
		List<DEdge> bad = bp.getBadEdges();
		//The added edges
		assertTrue(added.size()==3);
		assertTrue(added.contains(new DEdge(8,11,0,11,8,0)));
		assertTrue(added.contains(new DEdge(10,10,0,11,8,0)));
		assertTrue(added.contains(new DEdge(6,12,0,11,8,0)));
		//The bad edges
		assertTrue(bad.isEmpty());
		assertFalse(deg2.isDegenerated());
		//And the boundary
		List<DEdge> bList = bp.getBoundaryEdges();
		assertTrue(bList.size()==2);
		assertTrue(bList.get(0).equals(new DEdge(6,12,0,11,8,0)));
		assertTrue(bList.get(0).getStartPoint().equals(new DPoint(6,12,0)));
		assertTrue(bList.get(1).equals(new DEdge(11,8,0,10,10,0)));
		assertTrue(bList.get(1).getStartPoint().equals(new DPoint(11,8,0)));
	}

	/**
	 *
	 * @throws DelaunayError
	 */
	public void testSplitDegenInsertPointLowerWorse() throws DelaunayError {
		BoundaryPart bp;
		DEdge deg, deg2;
		List<DEdge> bounds = new ArrayList<DEdge>();
		//We prepare the first BP
		DEdge cstr = new DEdge(6,12,0,15,0,0);
		deg = new DEdge(6,12,0,8,11,0);
		deg.setDegenerated(true);
		deg2 = deg;
		bounds.add(deg);
		deg = new DEdge(8,11,0,10,10,0);
		deg.setDegenerated(true);
		bounds.add(deg);
		deg = new DEdge(6,12,0,0,13,0);
		bounds.add(deg);
		bp = new BoundaryPart(bounds, cstr);
		//We split the first bp
		bp.split(new DEdge(10,10,0,12,10,0));
		List<DTriangle> tri = bp.connectPoint(new DPoint(11,8,0));
		//we test the tiangles
		assertTrue(tri.size()==2);
		assertTrue(tri.contains(new DTriangle(new DEdge(10,10,0,11,8,0), new DEdge(11,8,0,8,11,0), new DEdge(8,11,0,10,10,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(6,12,0,11,8,0), new DEdge(11,8,0,8,11,0), new DEdge(8,11,0,6,12,0))));
		//The added and bad edges, now.
		List<DEdge> added = bp.getAddedEdges();
		List<DEdge> bad = bp.getBadEdges();
		//The added edges
		assertTrue(added.size()==3);
		assertTrue(added.contains(new DEdge(8,11,0,11,8,0)));
		assertTrue(added.contains(new DEdge(10,10,0,11,8,0)));
		assertTrue(added.contains(new DEdge(6,12,0,11,8,0)));
		//The bad edges
		assertTrue(bad.isEmpty());
		assertFalse(deg2.isDegenerated());
		//And the boundary
		List<DEdge> bList = bp.getBoundaryEdges();
		assertTrue(bList.size()==2);
		assertTrue(bList.get(0).equals(new DEdge(6,12,0,11,8,0)));
		assertTrue(bList.get(0).getStartPoint().equals(new DPoint(6,12,0)));
		assertTrue(bList.get(1).equals(new DEdge(11,8,0,10,10,0)));
		assertTrue(bList.get(1).getStartPoint().equals(new DPoint(11,8,0)));
	}

	/**
	 * Performs a split on a BP that contains only degenerated edges.
	 * @throws DelaunayError
	 */
	public void testSplitDegenDEOnly() throws DelaunayError {
		BoundaryPart bp;
		DEdge deg;
		List<DEdge> bounds = new ArrayList<DEdge>();
		//We prepare the first BP
		DEdge cstr = new DEdge(6,12,0,15,0,0);
		deg = new DEdge(6,12,0,8,11,0);
		deg.setDegenerated(true);
		bounds.add(deg);
		deg = new DEdge(8,11,0,10,10,0);
		deg.setDegenerated(true);
		bounds.add(deg);
		bp = new BoundaryPart(bounds, cstr);
		//We split the first bp
		BoundaryPart res = bp.split(new DEdge(10,10,0,12,9,0));
		//and we make our tests.
		//On the result
		assertTrue(res.getConstraint().equals(new DEdge(12,9,0,10,10,0)));
		assertTrue(res.getBoundaryEdges().size()==2);
		assertTrue(res.getBoundaryEdges().get(0).equals(new DEdge(10,10,0,8,11,0)));
		assertTrue(res.getBoundaryEdges().get(1).equals(new DEdge(8,11,0,6,12,0)));
		//On the first BP
		assertTrue(bp.getConstraint().equals(new DEdge(6,12,0,15,0,0)));
		assertTrue(bp.getBoundaryEdges().size()==2);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new DEdge(8,11,0,6,12,0)));
		assertTrue(bp.getBoundaryEdges().get(1).equals(new DEdge(10,10,0,8,11,0)));
	}

	/**
	 * Performs a split on a BP with degen edges.
	 * @throws DelaunayError
	 */
	public void testSplitDegenBis() throws DelaunayError {
		BoundaryPart bp;
		DEdge deg;
		List<DEdge> bounds = new ArrayList<DEdge>();
		//We prepare the first BP
		DEdge cstr = new DEdge(0,6,0,12,0,0);
		deg = new DEdge(0,6,0,4,7,0);
		bounds.add(deg);
		deg = new DEdge(4,7,0,6,9,0);
		deg.setDegenerated(true);
		bounds.add(deg);
		deg = new DEdge(6,9,0,8,11,0);
		deg.setDegenerated(true);
		bounds.add(deg);
		bp = new BoundaryPart(bounds, cstr);
		//We split the first bp
		BoundaryPart res = bp.split(new DEdge(8,11,0,10,13,0));
		//and we make our tests.
		//On the result
		assertTrue(res.getConstraint().equals(new DEdge(8,11,0,10,13,0)));
		assertTrue(res.getBoundaryEdges().size()==2);
		assertTrue(res.getBoundaryEdges().get(0).equals(new DEdge(6,9,0,8,11,0)));
		assertTrue(res.getBoundaryEdges().get(1).equals(new DEdge(4,7,0,6,9,0)));
		//On the first BP
		assertTrue(bp.getConstraint().equals(new DEdge(0,6,0,12,0,0)));
		assertTrue(bp.getBoundaryEdges().size()==3);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new DEdge(0,6,0,4,7,0)));
		assertTrue(bp.getBoundaryEdges().get(1).equals(new DEdge(4,7,0,6,9,0)));
		assertTrue(bp.getBoundaryEdges().get(2).equals(new DEdge(6,9,0,8,11,0)));
	}

	/**
	 * performs the connection of a point to a boundary part that contains only
	 * edges shared with another one.
	 * @throws DelaunayError
	 */
	public void testConnectToSplitDegenDEOnly () throws DelaunayError {
		BoundaryPart bp;
		DEdge deg;
		List<DEdge> bounds = new ArrayList<DEdge>();
		//We prepare the first BP
		DEdge cstr = new DEdge(0,6,0,12,0,0);
		deg = new DEdge(0,6,0,4,7,0);
		bounds.add(deg);
		deg = new DEdge(4,7,0,6,9,0);
		deg.setDegenerated(true);
		bounds.add(deg);
		deg = new DEdge(6,9,0,8,11,0);
		deg.setDegenerated(true);
		bounds.add(deg);
		bp = new BoundaryPart(bounds, cstr);
		//We split the first bp
		BoundaryPart res = bp.split(new DEdge(8,11,0,10,13,0));
		//we connect a point to the newly generated BP
		List<DTriangle> tri = res.connectPoint(new DPoint(9,13,0));
		assertTrue(tri.size()==2);
		assertTrue(tri.contains(new DTriangle(new DEdge(8,11,0,9,13,0), new DEdge(9,13,0,6,9,0), new DEdge(6,9,0,8,11,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(4,7,0,9,13,0), new DEdge(9,13,0,6,9,0), new DEdge(6,9,0,4,7,0))));
		//We check the boundary
		List<DEdge> boundEd = res.getBoundaryEdges();
		assertTrue(boundEd.size()==2);
		assertTrue(boundEd.contains(new DEdge(8,11,0,9,13,0)));
		assertTrue(boundEd.contains(new DEdge(4,7,0,9,13,0)));
		List<DEdge> bad = res.getBadEdges();
		List<DEdge> added = res.getAddedEdges();
		//we test the added edges.
		assertTrue(added.size()==3);
		assertTrue(added.contains(new DEdge(4,7,0,9,13,0)));
		assertTrue(added.contains(new DEdge(6,9,0,9,13,0)));
		assertTrue(added.contains(new DEdge(8,11,0,9,13,0)));
		assertTrue(bad.isEmpty());
	}

	/**
	 * Test that edges are well shared when splitting a BP with degenerated edges.
	 * @throws DelaunayError
	 */
	public void testSharing() throws DelaunayError {
		BoundaryPart bp;
		DEdge deg;
		List<DEdge> bounds = new ArrayList<DEdge>();
		//We prepare the first BP
		DEdge cstr = new DEdge(6,12,0,15,0,0);
		deg = new DEdge(6,12,0,8,11,0);
		deg.setDegenerated(true);
		bounds.add(deg);
		deg = new DEdge(8,11,0,10,10,0);
		deg.setDegenerated(true);
		bounds.add(deg);
		deg = new DEdge(6,12,0,0,13,0);
		bounds.add(deg);
		bp = new BoundaryPart(bounds, cstr);
		//We split the first bp
		BoundaryPart res = bp.split(new DEdge(10,10,0,12,9,0));
		//We make some tests on the original BP
		assertTrue(bp.getBoundaryEdges().get(0).isShared());
		assertTrue(bp.getBoundaryEdges().get(1).isShared());
		//We make some tests on the new BP
		assertTrue(res.getBoundaryEdges().get(0).isShared());
		assertTrue(res.getBoundaryEdges().get(1).isShared());
		assertFalse(res.getBoundaryEdges().get(2).isShared());
	}

	/**
	 * Tests that sthe sharing property is well removed when adding a point.
	 * @throws DelaunayError
	 */
	public void testSharingRemoval() throws DelaunayError {
		BoundaryPart bp;
		DEdge deg;
		List<DEdge> bounds = new ArrayList<DEdge>();
		//We prepare the first BP
		DEdge cstr = new DEdge(6,12,0,15,0,0);
		deg = new DEdge(6,12,0,8,11,0);
		deg.setDegenerated(true);
		bounds.add(deg);
		deg = new DEdge(8,11,0,10,10,0);
		deg.setDegenerated(true);
		bounds.add(deg);
		deg = new DEdge(6,12,0,0,13,0);
		bounds.add(deg);
		bp = new BoundaryPart(bounds, cstr);
		//We split the first bp
		BoundaryPart res = bp.split(new DEdge(10,10,0,12,9,0));
		bp.connectPoint(new DPoint(11,8,0));
		//We make some tests on the new BP
		assertFalse(res.getBoundaryEdges().get(0).isShared());
		assertFalse(res.getBoundaryEdges().get(1).isShared());
		assertFalse(res.getBoundaryEdges().get(2).isShared());
		bounds = new ArrayList<DEdge>();
		//We prepare the first BP
		cstr = new DEdge(6,12,0,15,0,0);
		deg = new DEdge(6,12,0,8,11,0);
		deg.setDegenerated(true);
		bounds.add(deg);
		deg = new DEdge(8,11,0,10,10,0);
		deg.setDegenerated(true);
		bounds.add(deg);
		deg = new DEdge(6,12,0,0,13,0);
		bounds.add(deg);
		bp = new BoundaryPart(bounds, cstr);
		//We split the first bp
		res = bp.split(new DEdge(10,10,0,12,9,0));
		res.connectPoint(new DPoint(11,14,0));
		//We make some tests on the original BP
		assertFalse(bp.getBoundaryEdges().get(0).isShared());
		assertFalse(bp.getBoundaryEdges().get(1).isShared());
	}

	/**
	 * Inserts a new degenerated edge in a BP that already
	 * contains two shared edges.
	 * @throws DelaunayError
	 */
	public void testSharingInsertNewDegen() throws DelaunayError {
		BoundaryPart bp;
		DEdge deg;
		List<DEdge> bounds = new ArrayList<DEdge>();
		//We prepare the first BP
		DEdge cstr = new DEdge(6,12,0,15,0,0);
		deg = new DEdge(6,12,0,8,11,0);
		deg.setDegenerated(true);
		bounds.add(deg);
		deg = new DEdge(8,11,0,10,10,0);
		deg.setDegenerated(true);
		bounds.add(deg);
		deg = new DEdge(6,12,0,0,13,0);
		bounds.add(deg);
		bp = new BoundaryPart(bounds, cstr);
		//We split the first bp
		BoundaryPart res = bp.split(new DEdge(10,10,0,12,11,0));
		assertTrue(res.getBoundaryEdges().size()==3);
		bp.connectPoint(new DPoint (12,10,0));
		assertTrue(bp.getBoundaryEdges().size()==3);
		assertTrue(bp.getBoundaryEdges().get(0).isShared());
		assertTrue(bp.getBoundaryEdges().get(1).isShared());
		assertTrue(bp.getBoundaryEdges().get(2).isDegenerated());
	}

	/**
	 * Inserts a new degenerated edge in a BP that already
	 * contains two shared edges, and create a new triangle with this degenerated
	 * edge, with a point that can't be seen from the shared edges
	 * @throws DelaunayError
	 */
	public void testSharingInsertNewDegenFurther() throws DelaunayError {
		BoundaryPart bp;
		DEdge deg;
		List<DEdge> bounds = new ArrayList<DEdge>();
		//We prepare the first BP
		DEdge cstr = new DEdge(6,12,0,15,0,0);
		deg = new DEdge(6,12,0,8,11,0);
		deg.setDegenerated(true);
		bounds.add(deg);
		deg = new DEdge(8,11,0,10,10,0);
		deg.setDegenerated(true);
		bounds.add(deg);
		deg = new DEdge(6,12,0,0,13,0);
		bounds.add(deg);
		bp = new BoundaryPart(bounds, cstr);
		//We split the first bp
		BoundaryPart res = bp.split(new DEdge(10,10,0,12,11,0));
		assertTrue(res.getBoundaryEdges().size()==3);
		List<DTriangle> tri = bp.connectPoint(new DPoint (12,10,0));
		assertTrue(tri.isEmpty());
		tri = bp.connectPoint(new DPoint (13,11,0));
		assertTrue(bp.getBoundaryEdges().size()==5);
		assertTrue(bp.getBoundaryEdges().get(0).isShared());
		assertTrue(bp.getBoundaryEdges().get(0).equals(new DEdge(6,12,0,8,11,0)));
		assertTrue(bp.getBoundaryEdges().get(1).isShared());
		assertTrue(bp.getBoundaryEdges().get(1).equals(new DEdge(8,11,0,10,10,0)));
		assertFalse(bp.getBoundaryEdges().get(2).isDegenerated());
		assertTrue(bp.getBoundaryEdges().get(2).equals(new DEdge(10,10,0,12,10,0)));
		assertTrue(bp.getBoundaryEdges().get(3).equals(new DEdge(13,11,0,12,10,0)));
		assertTrue(bp.getBoundaryEdges().get(4).equals(new DEdge(10,10,0,13,11,0)));
		assertTrue(tri.size()==1);
	}

	/**
	 * Inserts a new degenerated edge in a BP that already
	 * contains two shared edges.
	 * @throws DelaunayError
	 */
	public void testSharingSwapOrder() throws DelaunayError {
		BoundaryPart bp;
		DEdge deg;
		List<DEdge> bounds = new ArrayList<DEdge>();
		//We prepare the first BP
		DEdge cstr = new DEdge(6,12,0,15,0,0);
		deg = new DEdge(6,12,0,8,11,0);
		deg.setDegenerated(true);
		bounds.add(deg);
		deg = new DEdge(8,11,0,10,10,0);
		deg.setDegenerated(true);
		bounds.add(deg);
		deg = new DEdge(6,12,0,0,13,0);
		bounds.add(deg);
		bp = new BoundaryPart(bounds, cstr);
		//We split the first bp
		BoundaryPart res = bp.split(new DEdge(10,10,0,12,6,0));
		assertTrue(res.getBoundaryEdges().size()==3);
		List<DTriangle> tri = res.connectPoint(new DPoint (12,8,0));
		assertTrue(tri.isEmpty());
		tri = res.connectPoint(new DPoint (14,6,0));
		assertTrue(tri.isEmpty());
		bp=res.split(new DEdge(14,6,0,15,3,0));
		assertTrue(bp.getBoundaryEdges().size()==5);
		assertTrue(bp.getBoundaryEdges().get(0).isShared());
		assertTrue(bp.getBoundaryEdges().get(0).equals(new DEdge(12,8,0,14,6,0)));
		assertTrue(bp.getBoundaryEdges().get(1).isShared());
		assertTrue(bp.getBoundaryEdges().get(1).equals(new DEdge(12,8,0,10,10,0)));
		assertTrue(bp.getBoundaryEdges().get(2).isShared());
		assertTrue(bp.getBoundaryEdges().get(2).equals(new DEdge(10,10,0,8,11,0)));
		assertTrue(bp.getBoundaryEdges().get(3).isShared());
		assertTrue(bp.getBoundaryEdges().get(3).equals(new DEdge(8,11,0,6,12,0)));
		assertTrue(bp.getBoundaryEdges().get(4).equals(new DEdge(0,13,0,6,12,0)));
	}
}
