/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdelaunay.delaunay;

import java.util.List;

/**
 * This class checks that the constrained triangulation is well performed.
 * @author alexis
 */
public class TestConstrainedMesh extends BaseUtility {

	/**
	 * Test the generation of a constrained mesh on a really simple configuration
	 * We have one constraint edge and two other points (4 points, so), and
	 * the constraint is supposed to prevent the execution of the flip flap
	 * algorithm.
	 * @throws DelaunayError
	 */
	public void testSimpleConstraint() throws DelaunayError{
		ConstrainedMesh mesh = new ConstrainedMesh();
		DEdge constr = new DEdge(0,3,0,8,3,0);
		mesh.addConstraintEdge(constr);
		mesh.addPoint(new DPoint(4,5,0));
		mesh.addPoint(new DPoint(4,1,0));
		mesh.processDelaunay();
//		show(mesh);
		DTriangle tri1 = new DTriangle(constr, new DEdge(0,3,0,4,5,0), new DEdge(4,5,0,8,3,0));
		DTriangle tri2 = new DTriangle(constr, new DEdge(0,3,0,4,1,0), new DEdge(4,1,0,8,3,0));
		assertTrue(mesh.getTriangleList().contains(tri1));
		assertTrue(mesh.getTriangleList().contains(tri2));
//		assertTrue(mesh.isMeshComputed());

		mesh = new ConstrainedMesh();
		constr = new DEdge(3,0,0,3,6,0);
		mesh.addConstraintEdge(constr);
		mesh.addPoint(new DPoint(1,3,0));
		mesh.addPoint(new DPoint(5,1,0));
		mesh.processDelaunay();
//		show(mesh);
		tri1 = new DTriangle(constr, new DEdge(1,3,0,3,6,0), new DEdge(1,3,0,3,0,0));
		tri2 = new DTriangle(constr, new DEdge(3,6,0,5,1,0), new DEdge(5,1,0,3,0,0));
		assertTrue(mesh.getTriangleList().size()==2);
		assertTrue(mesh.getTriangleList().contains(tri1));
		assertTrue(mesh.getTriangleList().contains(tri2));
		assertTrue(mesh.isMeshComputed());
		assertGIDUnicity(mesh);
		assertUseEachEdge(mesh);
		assertUseEachPoint(mesh);
	}

	/**
	 * Another test case, with a flip-flap during the processing.
	 * The input contains one horizontal constraints with two points upper
	 * and two points lower than it
	 * @throws DelaunayError
	 */
	public void testOneConstraintFourPoints() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge(0,3,0,8,3,0));
		mesh.addPoint(new DPoint(3,1,0));
		mesh.addPoint(new DPoint(5,0,0));
		mesh.addPoint(new DPoint(4,5,0));
		mesh.addPoint(new DPoint(6,4,0));
		mesh.processDelaunay();
//		show(mesh);
		List<DTriangle> triangles = mesh.getTriangleList();
		assertTrue(triangles.contains(new DTriangle(new DEdge(0,3,0,8,3,0), new DEdge(8,3,0,3,1,0), new DEdge(3,1,0,0,3,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(5,0,0,8,3,0), new DEdge(8,3,0,3,1,0), new DEdge(3,1,0,5,0,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(0,3,0,4,5,0), new DEdge(4,5,0,6,4,0), new DEdge(6,4,0,0,3,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(0,3,0,8,3,0), new DEdge(8,3,0,6,4,0), new DEdge(6,4,0,0,3,0))));
		assertGIDUnicity(mesh);
		assertUseEachEdge(mesh);
		assertUseEachPoint(mesh);
	}

	/**
	 * the same configuration as the previous test, with a larger input.
	 * @throws DelaunayError
	 */
	public void testOneConstraintFourPointsExtended() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge(0,3,0,8,3,0));
		mesh.addConstraintEdge(new DEdge(10,0,0,10,6,0));
		mesh.addPoint(new DPoint(3,1,0));
		mesh.addPoint(new DPoint(5,0,0));
		mesh.addPoint(new DPoint(4,5,0));
		mesh.addPoint(new DPoint(6,4,0));
//		mesh.addPoint(new DPoint(9,6,0));
		mesh.processDelaunay();
//		show(mesh);
		List<DTriangle> triangles = mesh.getTriangleList();
		assertTrue(triangles.contains(new DTriangle(new DEdge(0,3,0,8,3,0), new DEdge(8,3,0,3,1,0), new DEdge(3,1,0,0,3,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(5,0,0,8,3,0), new DEdge(8,3,0,3,1,0), new DEdge(3,1,0,5,0,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(0,3,0,4,5,0), new DEdge(4,5,0,6,4,0), new DEdge(6,4,0,0,3,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(0,3,0,8,3,0), new DEdge(8,3,0,6,4,0), new DEdge(6,4,0,0,3,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(5,0,0,8,3,0), new DEdge(8,3,0,10,0,0), new DEdge(10,0,0,5,0,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(10,6,0,8,3,0), new DEdge(8,3,0,10,0,0), new DEdge(10,0,0,10,6,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(10,6,0,8,3,0), new DEdge(8,3,0,6,4,0), new DEdge(6,4,0,10,6,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(10,6,0,4,5,0), new DEdge(4,5,0,6,4,0), new DEdge(6,4,0,10,6,0))));
		assertTrue(triangles.size()==8);
		assertGIDUnicity(mesh);
		assertUseEachEdge(mesh);
		assertUseEachPoint(mesh);
	}

	/**
	 * Performs a test with many constraints and input points.
	 * The input constraints does not intersect here.
	 */
	public void testManyConstraints() throws DelaunayError{
		ConstrainedMesh mesh = new ConstrainedMesh();
		DEdge constr = new DEdge(0,3,0,8,3,0);
		mesh.addConstraintEdge(constr);
		constr = new DEdge(9,0,0,9,6,0);
		mesh.addConstraintEdge(constr);
		constr = new DEdge(12,6,0,8,7,0);
		mesh.addConstraintEdge(constr);
		constr = new DEdge(5,4,0,8,7,0);
		mesh.addConstraintEdge(constr);
		constr = new DEdge(12,6,0,12,7,0);
		mesh.addConstraintEdge(constr);
		constr = new DEdge(8,3,0,9,6,0);
		mesh.addConstraintEdge(constr);
		constr = new DEdge(8,7,0,12,12,0);
		mesh.addConstraintEdge(constr);
		mesh.addPoint(new DPoint(4,5,0));
		mesh.addPoint(new DPoint(4,1,0));
		mesh.addPoint(new DPoint(10,3,0));
		mesh.addPoint(new DPoint(11,9,0));
		mesh.processDelaunay();
		List<DTriangle> triangles = mesh.getTriangleList();
//                show(mesh);
		assertTrue(triangles.contains(new DTriangle(
			new DEdge(0,3,0,8,3,0),
			new DEdge(0,3,0,5,4,0),
			new DEdge(5,4,0,8,3,0))));
		assertTrue(triangles.contains(new DTriangle(
			new DEdge(0,3,0,8,3,0),
			new DEdge(8,3,0,4,1,0),
			new DEdge(4,1,0,0,3,0))));
		assertTrue(triangles.contains(new DTriangle(
			new DEdge(0,3,0,5,4,0),
			new DEdge(5,4,0,4,5,0),
			new DEdge(4,5,0,0,3,0))));
		assertTrue(triangles.contains(new DTriangle(
			new DEdge(0,3,0,12,12,0),
			new DEdge(12,12,0,4,5,0),
			new DEdge(4,5,0,0,3,0))));
		assertTrue(triangles.contains(new DTriangle(
			new DEdge(8,7,0,12,12,0),
			new DEdge(12,12,0,4,5,0),
			new DEdge(4,5,0,8,7,0))));
		assertTrue(triangles.contains(new DTriangle(
			new DEdge(8,7,0,5,4,0),
			new DEdge(5,4,0,4,5,0),
			new DEdge(4,5,0,8,7,0))));
		assertTrue(triangles.contains(new DTriangle(
			new DEdge(8,7,0,5,4,0),
			new DEdge(5,4,0,8,3,0),
			new DEdge(8,3,0,8,7,0))));
		assertTrue(triangles.contains(new DTriangle(
			new DEdge(8,7,0,9,6,0),
			new DEdge(9,6,0,8,3,0),
			new DEdge(8,3,0,8,7,0))));
		assertTrue(triangles.contains(new DTriangle(
			new DEdge(9,0,0,9,6,0),
			new DEdge(9,6,0,8,3,0),
			new DEdge(8,3,0,9,0,0))));
		assertTrue(triangles.contains(new DTriangle(
			new DEdge(9,0,0,9,6,0),
			new DEdge(9,6,0,10,3,0),
			new DEdge(10,3,0,9,0,0))));
		assertTrue(triangles.contains(new DTriangle(
			new DEdge(9,0,0,12,6,0),
			new DEdge(12,6,0,10,3,0),
			new DEdge(10,3,0,9,0,0))));
		assertTrue(triangles.contains(new DTriangle(
			new DEdge(9,6,0,12,6,0),
			new DEdge(12,6,0,10,3,0),
			new DEdge(10,3,0,9,6,0))));
		assertTrue(triangles.contains(new DTriangle(
			new DEdge(9,6,0,12,6,0),
			new DEdge(12,6,0,8,7,0),
			new DEdge(8,7,0,9,6,0))));
		assertTrue(triangles.contains(new DTriangle(
			new DEdge(12,7,0,12,6,0),
			new DEdge(12,6,0,8,7,0),
			new DEdge(8,7,0,12,7,0))));
		assertTrue(triangles.contains(new DTriangle(
			new DEdge(12,7,0,11,9,0),
			new DEdge(11,9,0,12,12,0),
			new DEdge(12,12,0,12,7,0))));
		assertTrue(triangles.contains(new DTriangle(
			new DEdge(12,12,0,11,9,0),
			new DEdge(11,9,0,8,7,0),
			new DEdge(8,7,0,12,12,0))));
		assertTrue(triangles.contains(new DTriangle(
			new DEdge(9,0,0,8,3,0),
			new DEdge(8,3,0,4,1,0),
			new DEdge(4,1,0,9,0,0))));
		assertTrue(triangles.contains(new DTriangle(
			new DEdge(11,9,0,8,7,0),
			new DEdge(12,7,0,11,9,0),
			new DEdge(8,7,0,12,7,0))));
		assertGIDUnicity(mesh);
		assertUseEachEdge(mesh);
		assertUseEachPoint(mesh);

	}

	/**
	 * Perform constrained triangulation on a set of edges which is designed
	 * to cause the use of the remove ghost algorithm in ConstrainedMesh.
	 * Check that unnecessary edges and triangles are well removed.
	 */
	public void testRemoveGhost() throws DelaunayError{
		ConstrainedMesh mesh = new ConstrainedMesh();
		DEdge constr = new DEdge(1,1,0,5,1,0);
		mesh.addConstraintEdge(constr);
		mesh.addPoint(new DPoint (3,0,0));
		mesh.addPoint(new DPoint (3,2,0));
		mesh.addPoint(new DPoint (3,4,0));
		mesh.addPoint(new DPoint (3,6,0));
		mesh.addPoint(new DPoint (3,8,0));
		mesh.addPoint(new DPoint (3,10,0));
		mesh.addPoint(new DPoint (3,12,0));
		mesh.addPoint(new DPoint (3,14,0));
		mesh.addPoint(new DPoint (3,16,0));
		mesh.processDelaunay();
//		show(mesh);
		assertFalse(mesh.getPoints().contains(new DPoint(0,0,0)));
		List<DTriangle> triangles = mesh.getTriangleList();
		for(DTriangle tri : triangles){
			assertFalse(tri.contains(new DPoint(0,0,0)));
		}
		for(DEdge ed : mesh.getEdges()){
			assertFalse(ed.contains(new DPoint(0,0,0)));
		}
		assertTrue(triangles.contains(new DTriangle(new DEdge(1,1,0,3,2,0), new DEdge(3,2,0,3,4,0), new DEdge(3,4,0,1,1,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(1,1,0,3,6,0), new DEdge(3,6,0,3,4,0), new DEdge(3,4,0,1,1,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(1,1,0,3,8,0), new DEdge(3,8,0,3,6,0), new DEdge(3,6,0,1,1,0))));
		assertGIDUnicity(mesh);
		assertUseEachEdge(mesh);
		assertUseEachPoint(mesh);
	}

	/**
	 * Checks that the objects (points and edges, to be accurate) are not duplicated
	 * in the mesh.
	 * @throws DelaunayError
	 */
	public void testObjectsUnicity() throws DelaunayError{
		ConstrainedMesh mesh = new ConstrainedMesh();
		DEdge constr = new DEdge(0,3,0,8,3,0);
		mesh.addConstraintEdge(constr);
		constr = new DEdge(9,0,0,9,6,0);
		mesh.addConstraintEdge(constr);
		constr = new DEdge(12,6,0,8,7,0);
		mesh.addConstraintEdge(constr);
		constr = new DEdge(5,4,0,8,7,0);
		mesh.addConstraintEdge(constr);
		constr = new DEdge(12,6,0,12,7,0);
		mesh.addConstraintEdge(constr);
		constr = new DEdge(8,3,0,9,6,0);
		mesh.addConstraintEdge(constr);
		constr = new DEdge(8,7,0,12,12,0);
		mesh.addConstraintEdge(constr);
		mesh.addPoint(new DPoint(4,5,0));
		mesh.addPoint(new DPoint(4,1,0));
		mesh.addPoint(new DPoint(10,3,0));
		mesh.addPoint(new DPoint(11,9,0));
		mesh.processDelaunay();
		List<DTriangle> triangles = mesh.getTriangleList();
		int index;
		DEdge comp;
		List<DEdge> edges = mesh.getEdges();
		List<DPoint> points = mesh.getPoints();
		DPoint pt;
		for(DTriangle tri : triangles){
			index = edges.indexOf(tri.getEdge(0));
			comp = edges.get(index) ;
			assertTrue(tri.getEdge(0)==comp);
			pt=points.get(points.indexOf(tri.getPoint(0)));
			assertTrue(tri.getPoint(0) == pt);
			index = edges.indexOf(tri.getEdge(1));
			comp = edges.get(index) ;
			assertTrue(tri.getEdge(1)==comp);
			pt=points.get(points.indexOf(tri.getPoint(1)));
			assertTrue(tri.getPoint(1) == pt);
			index = edges.indexOf(tri.getEdge(2));
			comp = edges.get(index) ;
			assertTrue(tri.getEdge(2)==comp);
			pt=points.get(points.indexOf(tri.getPoint(2)));
			if(tri.getPoint(2) == pt){
				assertTrue(true);
			} else {
				System.out.println(pt);
				System.out.println(tri.getPoint(2));
				assertTrue(false);
			}
		}
	}

	/**
	 * Performs a test on an input where the two first points can't be used
	 * to build a triangle with a "ghost point" which would be placed before
	 * the first point of the input. Indeed, the second point can't be seen
	 * from this ghost point, because of the constraints given in input.
	 * @throws DelaunayError
	 */
	public void testProtectedSecondPoint() throws DelaunayError{
		ConstrainedMesh mesh = new ConstrainedMesh();
		DEdge constr1 = new DEdge(0,2,0,5,6,0);
		DEdge constr2 = new DEdge(0,2,0,4,0,0);
		mesh.addConstraintEdge(constr2);
		mesh.addConstraintEdge(constr1);
		mesh.addPoint(new DPoint(2,3,0));
		mesh.addPoint(new DPoint(3,6,0));
		mesh.processDelaunay();
//		show(mesh);
		List<DTriangle> triangles=mesh.getTriangleList();
		assertTrue(triangles.contains(new DTriangle(constr1,new DEdge(0,2,0,3,6,0),new DEdge(3,6,0,5,6,0))));
		assertTrue(triangles.contains(new DTriangle(constr1,new DEdge(0,2,0,2,3,0),new DEdge(2,3,0,5,6,0))));
		assertTrue(triangles.contains(new DTriangle(constr2,new DEdge(0,2,0,2,3,0),new DEdge(2,3,0,4,0,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(2,3,0,5,6,0),new DEdge(5,6,0,4,0,0),new DEdge(2,3,0,4,0,0))));
		assertTrue(triangles.size()==4);
	}

	/**
	 * Tests that we can manage the case when two points give two degenerated
	 * edges during the processing.
	 */
	public void testTwoColinearDegeneratedEdges() throws DelaunayError{
		ConstrainedMesh mesh = new ConstrainedMesh();
		DEdge constr1 = new DEdge(2,2,0,7,4,0);
		DEdge constr2 = new DEdge (2,2,0,7,0,0);
		mesh.addConstraintEdge(constr2);
		mesh.addConstraintEdge(constr1);
		mesh.addPoint(new DPoint(1,1,0));
		mesh.addPoint(new DPoint(1,3,0));
		mesh.addPoint(new DPoint(4,2,0));
		mesh.addPoint(new DPoint(6,2,0));
		mesh.processDelaunay();
//		show(mesh);
		List<DTriangle> triangles = mesh.getTriangleList();
		assertTrue(triangles.contains(new DTriangle(new DEdge(1,1,0,1,3,0), new DEdge(2,2,0,1,3,0), new DEdge(1,1,0,2,2,0))));
		assertTrue(triangles.contains(new DTriangle(constr1, new DEdge(2,2,0,1,3,0), new DEdge(1,3,0,7,4,0))));
		assertTrue(triangles.contains(new DTriangle(constr1, new DEdge(2,2,0,4,2,0), new DEdge(4,2,0,7,4,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(6,2,0,7,4,0), new DEdge(7,4,0,7,0,0), new DEdge(7,0,0,6,2,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(4,2,0,6,2,0), new DEdge(6,2,0,7,4,0), new DEdge(7,4,0,4,2,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(4,2,0,6,2,0), new DEdge(6,2,0,7,0,0), new DEdge(7,0,0,4,2,0))));
		assertTrue(triangles.contains(new DTriangle(constr2, new DEdge(2,2,0,4,2,0), new DEdge(4,2,0,7,0,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(1,1,0,7,0,0), constr2, new DEdge(1,1,0,2,2,0))));
		assertTrue(triangles.size()==8);
		List<DEdge> edges = mesh.getEdges();
		assertTrue(edges.size()==14);
		assertGIDUnicity(mesh);
		assertUseEachEdge(mesh);
		assertUseEachPoint(mesh);

	}


	/**
	 * Performs a delaunay triangulation with an input without constraints.
	 */
	public void testSwapEdges() throws DelaunayError{
		ConstrainedMesh mesh = new ConstrainedMesh();
		DPoint p1=new DPoint(0,4,0);
		DPoint p2=new DPoint(2,8,0);
		DPoint p3=new DPoint(2,0,0);
		DPoint p4=new DPoint(4,4,0);
		mesh.addPoint(p1);
		mesh.addPoint(p2);
		mesh.addPoint(p3);
		mesh.addPoint(p4);
		mesh.processDelaunay();
		DTriangle tri1 = new DTriangle(new DEdge(p1, p2), new DEdge(p2, p4), new DEdge(p4, p1));
		DTriangle tri2 = new DTriangle(new DEdge(p1, p3), new DEdge(p3, p4), new DEdge(p4, p1));
		assertTrue(mesh.getTriangleList().contains(tri1));
		assertTrue(mesh.getTriangleList().contains(tri2));
		assertGIDUnicity(mesh);
		assertUseEachEdge(mesh);
		assertUseEachPoint(mesh);
	}


	/**
	 * Performs a delaunay triangulation with an input without constraints.
	 */
	public void testSwapEdgesBis() throws DelaunayError{
		ConstrainedMesh mesh = new ConstrainedMesh();
		DPoint p1=new DPoint(0,1,0);
		DPoint p3=new DPoint(4,0,0);
		DPoint p4=new DPoint(4,1,0);
		DPoint p2=new DPoint(3,3,0);
		mesh.addPoint(p1);
		mesh.addPoint(p2);
		mesh.addPoint(p3);
		mesh.addPoint(p4);
		mesh.processDelaunay();
//		show(mesh);
		DTriangle tri1 = new DTriangle(new DEdge(p1, p2), new DEdge(p2, p4), new DEdge(p4, p1));
		DTriangle tri2 = new DTriangle(new DEdge(p1, p3), new DEdge(p3, p4), new DEdge(p4, p1));
		assertTrue(mesh.getTriangleList().contains(tri1));
		assertTrue(mesh.getTriangleList().contains(tri2));
	}

	/**
	 * Make a classical triangulation on a random set of points. For benchmark purposes.
	 * @throws DelaunayError
	 */
	public void testDelaunayTriangulationRandomPoints() throws DelaunayError{
		List<DPoint> randomPoint = BaseUtility.getRandomPoints(1000);
		ConstrainedMesh mesh = new ConstrainedMesh();
		for(DPoint pt : randomPoint){
			mesh.addPoint(pt);
		}
		mesh.addConstraintEdge(new DEdge(5,5,0,10,10,0));
		double t = System.currentTimeMillis();
		mesh.processDelaunay();
//		show(mesh);
		double t2 = System.currentTimeMillis();
		System.out.println("Needed time : "+(t2 - t));
		assertTrue(true);
	}

	/**
	 * try tro triangulate a simple cross.
	 */
	public void testCross() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge(0,0,0,2,2,0));
		mesh.addConstraintEdge(new DEdge(0,4,0,2,2,0));
		mesh.addConstraintEdge(new DEdge(4,0,0,2,2,0));
		mesh.addConstraintEdge(new DEdge(4,4,0,2,2,0));
		mesh.processDelaunay();
//		show(mesh);
		List<DTriangle> triangles = mesh.getTriangleList();
		assertTrue(triangles.contains(new DTriangle(new DEdge(0,0,0,2,2,0), new DEdge(0,4,0,2,2,0), new DEdge(0,0,0,0,4,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(0,4,0,2,2,0), new DEdge(4,4,0,2,2,0), new DEdge(4,4,0,0,4,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(0,0,0,2,2,0), new DEdge(4,0,0,2,2,0), new DEdge(0,0,0,4,0,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(4,4,0,2,2,0), new DEdge(4,0,0,2,2,0), new DEdge(4,4,0,4,0,0))));
	}

	/**
	 * Process a triangulation where the three first input points have the same
	 * x-coordinate, and are linked to constraints.
	 */
	public void test3HorizontalPoints() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge(0,3,0,3,1,0));
		mesh.addConstraintEdge(new DEdge(3,1,0,8,0,0));
		mesh.addConstraintEdge(new DEdge(0,6,0,3,7,0));
		mesh.addConstraintEdge(new DEdge(3,7,0,5,5,0));
		mesh.addConstraintEdge(new DEdge(0,10,0,4,9,0));
		mesh.addConstraintEdge(new DEdge(4,9,0,9,9,0));
		mesh.processDelaunay();
//		show(mesh);
		List<DTriangle> triangles = mesh.getTriangleList();
		assertTrue(triangles.contains(new DTriangle(new DEdge(0,3,0,3,1,0),new DEdge(3,1,0,5,5,0),new DEdge(5,5,0,0,3,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(5,5,0,0,3,0),new DEdge(0,3,0,3,7,0),new DEdge(3,7,0,5,5,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(0,3,0,3,7,0),new DEdge(3,7,0,0,6,0),new DEdge(0,6,0,0,3,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(3,7,0,0,6,0),new DEdge(0,6,0,0,10,0),new DEdge(0,10,0,3,7,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(0,10,0,3,7,0),new DEdge(3,7,0,4,9,0),new DEdge(4,9,0,0,10,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(4,9,0,0,10,0),new DEdge(0,10,0,9,9,0),new DEdge(9,9,0,4,9,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(9,9,0,4,9,0),new DEdge(4,9,0,5,5,0),new DEdge(5,5,0,9,9,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(4,9,0,5,5,0),new DEdge(3,7,0,5,5,0),new DEdge(3,7,0,4,9,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(5,5,0,9,9,0),new DEdge(9,9,0,8,0,0),new DEdge(8,0,0,5,5,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(8,0,0,5,5,0),new DEdge(5,5,0,3,1,0),new DEdge(3,1,0,8,0,0))));

	}

	public void test4verticalPoint() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addPoint(new DPoint(0,1,0));
		mesh.addPoint(new DPoint(0,4,0));
		mesh.addPoint(new DPoint(0,9,0));
		mesh.addPoint(new DPoint(0,12,0));
		mesh.addPoint(new DPoint(2,1,0));
		mesh.processDelaunay();
//		show(mesh);
		List<DTriangle> triangles = mesh.getTriangleList();
		assertTrue(triangles.contains(new DTriangle(new DEdge(0,1,0,2,1,0),new DEdge(2,1,0,0,4,0),new DEdge(0,4,0,0,1,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(0,9,0,2,1,0),new DEdge(2,1,0,0,4,0),new DEdge(0,4,0,0,9,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(0,9,0,2,1,0),new DEdge(2,1,0,0,12,0),new DEdge(0,12,0,0,9,0))));
	}
	/**
	 * Test case where we have three constraints 
	 * @throws DelaunayError
	 */
	public void test3VerticalConstraints() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge(3,0,0,3,3,0));
		mesh.addConstraintEdge(new DEdge(3,5,0,3,8,0));
		mesh.addConstraintEdge(new DEdge(3,10,0,3,13,0));
		mesh.addPoint(new DPoint(0,1,0));
		mesh.addPoint(new DPoint(0,4,0));
		mesh.addPoint(new DPoint(0,9,0));
		mesh.addPoint(new DPoint(0,12,0));
		mesh.addPoint(new DPoint(6,6,0));
		mesh.addPoint(new DPoint(6,7,0));
//		show(mesh);
		mesh.processDelaunay();
//		show(mesh);
		List<DTriangle> triangles = mesh.getTriangleList();
		assertTrue(triangles.contains(new DTriangle(new DEdge(0,1,0,3,0,0),new DEdge(3,0,0,3,3,0),new DEdge(3,3,0,0,1,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(0,1,0,0,4,0),new DEdge(0,4,0,3,3,0),new DEdge(3,3,0,0,1,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(3,5,0,0,4,0),new DEdge(0,4,0,3,3,0),new DEdge(3,3,0,3,5,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(3,5,0,0,4,0),new DEdge(0,4,0,0,9,0),new DEdge(0,9,0,3,5,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(3,5,0,3,8,0),new DEdge(3,8,0,0,9,0),new DEdge(0,9,0,3,5,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(3,10,0,3,8,0),new DEdge(3,8,0,0,9,0),new DEdge(0,9,0,3,10,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(3,10,0,0,12,0),new DEdge(0,12,0,0,9,0),new DEdge(0,9,0,3,10,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(3,10,0,0,12,0),new DEdge(0,12,0,3,13,0),new DEdge(3,13,0,3,10,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(3,10,0,6,7,0),new DEdge(6,7,0,3,13,0),new DEdge(3,13,0,3,10,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(3,10,0,6,7,0),new DEdge(6,7,0,3,8,0),new DEdge(3,8,0,3,10,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(6,6,0,6,7,0),new DEdge(6,7,0,3,8,0),new DEdge(3,8,0,6,6,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(6,6,0,3,5,0),new DEdge(3,5,0,3,8,0),new DEdge(3,8,0,6,6,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(6,6,0,3,5,0),new DEdge(3,5,0,3,3,0),new DEdge(3,3,0,6,6,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(6,6,0,3,0,0),new DEdge(3,0,0,3,3,0),new DEdge(3,3,0,6,6,0))));
	}

	/**
	 * A test case where the first triangle can't be built, and were the 2 first built edges
	 * will be p1p2 and p2p3
	 * @throws DelaunayError
	 */
	public void testTwoNeighbourConstraints() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge(0,2,0,2,4,0));
		mesh.addConstraintEdge(new DEdge(0,10,0,3,8,0));
		mesh.addConstraintEdge(new DEdge(0,10,0,1,13,0));
		mesh.addConstraintEdge(new DEdge(3,14,0,1,13,0));
		mesh.processDelaunay();
//		show(mesh);
		List<DTriangle> triangles = mesh.getTriangleList();
		assertTrue(triangles.contains(new DTriangle(new DEdge(0,2,0,2,4,0), new DEdge(2,4,0,0,10,0), new DEdge(0,10,0,0,2,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(3,8,0,2,4,0), new DEdge(2,4,0,0,10,0), new DEdge(0,10,0,3,8,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(3,8,0,1,13,0), new DEdge(1,13,0,0,10,0), new DEdge(0,10,0,3,8,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(3,8,0,1,13,0), new DEdge(1,13,0,3,14,0), new DEdge(3,14,0,3,8,0))));
		assertTrue(triangles.size()==4);
	}

	/**
	 * An extension of the previous case.
	 * @throws DelaunayError
	 */
	public void testTwistedConstraint() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge(0,10,0,3,8,0));
		mesh.addConstraintEdge(new DEdge(0,10,0,1,13,0));
		mesh.addConstraintEdge(new DEdge(3,14,0,1,13,0));
		mesh.addConstraintEdge(new DEdge(3,14,0,2,15,0));
		mesh.addConstraintEdge(new DEdge(6,10,0,3,8,0));
		mesh.processDelaunay();
//		show(mesh);
		List<DTriangle> triangles = mesh.getTriangleList();
		assertTrue(triangles.contains(new DTriangle(new DEdge(0,10,0,3,8,0), new DEdge(3,8,0,1,13,0), new DEdge(1,13,0,0,10,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(6,10,0,3,8,0), new DEdge(3,8,0,1,13,0), new DEdge(1,13,0,6,10,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(6,10,0,3,14,0), new DEdge(3,14,0,1,13,0), new DEdge(1,13,0,6,10,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(2,15,0,3,14,0), new DEdge(3,14,0,1,13,0), new DEdge(1,13,0,2,15,0))));


	}

	public void testCommonLeftAndRightPoint() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
//		mesh.addConstraintEdge(new DEdge(0,3,0,6,3,0));
		mesh.addConstraintEdge(new DEdge(0,3,0,10,6,0));
		mesh.addConstraintEdge(new DEdge(8,1,0,6,3,0));
		mesh.addConstraintEdge(new DEdge(8,5,0,6,3,0));
		mesh.addPoint(new DPoint(0,4,0));
		mesh.addPoint(new DPoint(6,0,0));
		mesh.processDelaunay();
//		show(mesh);
		List<DTriangle> triangles = mesh.getTriangleList();
		assertTrue(triangles.contains(new DTriangle(new DEdge(0,3,0,6,3,0), new DEdge(6,3,0,6,0,0), new DEdge(6,0,0,0,3,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(8,1,0,6,3,0), new DEdge(6,3,0,6,0,0), new DEdge(6,0,0,8,1,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(8,1,0,6,3,0), new DEdge(6,3,0,8,5,0), new DEdge(8,5,0,8,1,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(8,1,0,10,6,0), new DEdge(10,6,0,8,5,0), new DEdge(8,5,0,8,1,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(0,3,0,10,6,0), new DEdge(10,6,0,8,5,0), new DEdge(8,5,0,0,3,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(0,3,0,10,6,0), new DEdge(10,6,0,0,4,0), new DEdge(0,4,0,0,3,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(0,3,0,6,3,0), new DEdge(6,3,0,8,5,0), new DEdge(8,5,0,0,3,0))));

	}

	/**
	 * This test contains a set of data that have been obtained from the chezine data.
	 * The points have been modified, in order to simplify the coordinate and
	 * help the understanding of the test.
	 * @throws DelaunayError
	 */
	public void testFromChezine() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		/*
		 * The points we use are, sorted :
		 * 0, -5, 80.0
		 * 4, -4.0, 80.0
		 * 6, 4.0, 80.0
		 * 6.0, 21.0, 80.0
		 * 7, 16.0, 80.0
		 * 10, 26.0, 80.0
		 * 11.0, 14, 80.0
		 * 12.0, 7, 80.0
		 * 16.0, 6, 80.0
		 */
		mesh.addConstraintEdge(new DEdge (0, -5, 80.0, 4, -4.0, 80.0));
		mesh.addConstraintEdge(new DEdge (4, -4.0, 80.0, 6, 4.0, 80.0));
		mesh.addConstraintEdge(new DEdge (6, 4.0, 80.0, 12.0, 7, 80.0));
		mesh.addConstraintEdge(new DEdge (12.0, 7, 80.0, 16.0, 6, 80.0));
		
		mesh.addConstraintEdge(new DEdge (6.0, 21.0, 80.0, 7, 16.0, 80.0));
		mesh.addConstraintEdge(new DEdge (6.0, 21.0, 80.0, 10, 26.0, 80.0));
		mesh.addConstraintEdge(new DEdge (7, 16.0, 80.0, 11.0, 14, 80.0));
		mesh.processDelaunay();
//		show(mesh);
		List<DTriangle> triangles = mesh.getTriangleList();
		assertTrue(triangles.contains(new DTriangle(new DEdge(0, -5, 80.0, 4, -4.0, 80.0), new DEdge(4, -4.0, 80.0, 6, 4.0, 80.0), new DEdge(6, 4.0, 80.0, 0, -5, 80.0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(0, -5, 80.0, 7, 16.0, 80.0), new DEdge(7, 16.0, 80.0, 6, 4.0, 80.0), new DEdge(6, 4.0, 80.0, 0, -5, 80.0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(0, -5, 80.0, 7, 16.0, 80.0), new DEdge(7, 16.0, 80.0, 6.0, 21.0, 80.0), new DEdge(6.0, 21.0, 80.0, 0, -5, 80.0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(11.0, 14, 80.0, 7, 16.0, 80.0), new DEdge(7, 16.0, 80.0, 6.0, 21.0, 80.0), new DEdge(6.0, 21.0, 80.0, 11.0, 14, 80.0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(11.0, 14, 80.0, 10, 26.0, 80.0), new DEdge(10, 26.0, 80.0, 6.0, 21.0, 80.0), new DEdge(6.0, 21.0, 80.0, 11.0, 14, 80.0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(11.0, 14, 80.0, 10, 26.0, 80.0), new DEdge(10, 26.0, 80.0, 16.0, 6, 80.0), new DEdge(16.0, 6, 80.0, 11.0, 14, 80.0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(11.0, 14, 80.0, 12.0, 7, 80.0), new DEdge(12.0, 7, 80.0, 16.0, 6, 80.0), new DEdge(16.0, 6, 80.0, 11.0, 14, 80.0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(6, 4.0, 80.0, 12.0, 7, 80.0), new DEdge(12.0, 7, 80.0, 16.0, 6, 80.0), new DEdge(16.0, 6, 80.0, 6, 4.0, 80.0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(6, 4.0, 80.0, 4, -4.0, 80.0), new DEdge(4, -4.0, 80.0, 16.0, 6, 80.0), new DEdge(16.0, 6, 80.0, 6, 4.0, 80.0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(6, 4.0, 80.0, 12.0, 7, 80.0), new DEdge(12.0, 7, 80.0, 11.0, 14, 80.0), new DEdge(11.0, 14, 80.0, 6, 4.0, 80.0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(6, 4.0, 80.0, 7, 16.0, 80.0), new DEdge(7, 16.0, 80.0, 11.0, 14, 80.0), new DEdge(11.0, 14, 80.0, 6, 4.0, 80.0))));

	}

	/**
	 * A second test whose configuration comes from the chezine data.
	 * It tests that the triangulation is well performed when the first three points are colinear.
	 */
	public void testFromChezineBis() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge (0, 0.6, 10, 13, -4, 10));

		mesh.addConstraintEdge(new DEdge (0, 63, 10.0, 17, 42, 10.0));

		mesh.addConstraintEdge(new DEdge (0, 77, 10.0, 3.0, 92.0, 10.0));
		mesh.addConstraintEdge(new DEdge (0, 118, 10.0, 13.0, 125, 10.0));
		mesh.addConstraintEdge(new DEdge (3.0, 92.0, 10.0, 13.0, 99, 10.0));
		mesh.processDelaunay();
//		show(mesh);
		List<DTriangle> triangles = mesh.getTriangleList();
		assertTrue(triangles.contains(new DTriangle(new DEdge(0, 0.6, 10, 13, -4, 10), new DEdge(13, -4, 10,17, 42, 10.0), new DEdge(17, 42, 10.0,0, 0.6, 10))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(0, 0.6, 10, 0, 63, 10.0), new DEdge(0, 63, 10.0 ,17, 42, 10.0), new DEdge(17, 42, 10.0,0, 0.6, 10))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(0, 77, 10.0, 0, 63, 10.0), new DEdge(0, 63, 10.0 ,17, 42, 10.0), new DEdge(17, 42, 10.0, 0, 77, 10.0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(0, 77, 10.0, 13.0, 99, 10.0), new DEdge(13.0, 99, 10.0 ,17, 42, 10.0), new DEdge(17, 42, 10.0, 0, 77, 10.0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(0, 77, 10.0, 13.0, 99, 10.0), new DEdge(13.0, 99, 10.0 ,3.0, 92.0, 10.0), new DEdge(3.0, 92.0, 10.0, 0, 77, 10.0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(0, 77, 10.0, 0, 118, 10.0), new DEdge(0, 118, 10.0,3.0, 92.0, 10.0), new DEdge(3.0, 92.0, 10.0, 0, 77, 10.0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(13.0, 99, 10.0, 0, 118, 10.0), new DEdge(0, 118, 10.0,3.0, 92.0, 10.0), new DEdge(3.0, 92.0, 10.0, 13.0, 99, 10.0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(13.0, 99, 10.0, 0, 118, 10.0), new DEdge(0, 118, 10.0,13.0, 125, 10.0), new DEdge(13.0, 125, 10.0, 13.0, 99, 10.0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(13.0, 99, 10.0, 17, 42, 10.0), new DEdge(17, 42, 10.0,13.0, 125, 10.0), new DEdge(13.0, 125, 10.0, 13.0, 99, 10.0))));

	}

	/**
	 * A test inherited from the former delaunay implementation.
	 * @throws DelaunayError
	 */
	public void testProcessStar() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addPoint(new DPoint(0, 0, 0));
		mesh.addPoint(new DPoint(10, 0, 0));
		mesh.addPoint(new DPoint(0, 10, 0));
		mesh.addPoint(new DPoint(10, 10, 0));
		mesh.addConstraintEdge(new DEdge(1, 5, 2, 4, 6, 2));
		mesh.addConstraintEdge(new DEdge(4, 6, 2, 5, 9, 2));
		mesh.addConstraintEdge(new DEdge(5, 9, 2, 6, 6, 2));
		mesh.addConstraintEdge(new DEdge(6, 6, 2, 9, 5, 2));
		mesh.addConstraintEdge(new DEdge(9, 5, 2, 6, 4, 2));
		mesh.addConstraintEdge(new DEdge(6, 4, 2, 5, 1, 2));
		mesh.addConstraintEdge(new DEdge(5, 1, 2, 4, 4, 2));
		mesh.addConstraintEdge(new DEdge(4, 4, 2, 1, 5, 2));
		mesh.processDelaunay();
//		show(mesh);
		List<DTriangle> triangles = mesh.getTriangleList();
		assertTrue(triangles.contains(new DTriangle(new DEdge(0,0,0,5,1,2), new DEdge(5,1,2,10,0,0), new DEdge(10,0,0,0,0,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(0,0,0,5,1,2), new DEdge(5,1,2,4,4,2), new DEdge(4,4,2,0,0,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(0,0,0,1,5,2), new DEdge(1,5,2,4,4,2), new DEdge(4,4,2,0,0,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(0,0,0,1,5,2), new DEdge(1,5,2,0,10,0), new DEdge(0,10,0,0,0,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(4,6,2,1,5,2), new DEdge(1,5,2,0,10,0), new DEdge(0,10,0,4,6,2))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(4,6,2,5,9,2), new DEdge(5,9,2,0,10,0), new DEdge(0,10,0,4,6,2))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(10,10,0,5,9,2), new DEdge(5,9,2,0,10,0), new DEdge(0,10,0,10,10,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(10,10,0,5,9,2), new DEdge(5,9,2,6,6,2), new DEdge(6,6,2,10,10,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(10,10,0,9,5,2), new DEdge(9,5,2,6,6,2), new DEdge(6,6,2,10,10,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(6,4,2,9,5,2), new DEdge(9,5,2,10,0,0), new DEdge(10,0,0,6,4,2))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(10,10,0,9,5,2), new DEdge(9,5,2,10,0,0), new DEdge(10,0,0,10,10,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(6,4,2,5,1,2), new DEdge(5,1,2,10,0,0), new DEdge(10,0,0,6,4,2))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(6,4,2,5,1,2), new DEdge(5,1,2,4,4,2), new DEdge(4,4,2,6,4,2))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(6,4,2,4,6,2), new DEdge(4,6,2,4,4,2), new DEdge(4,4,2,6,4,2))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(1,5,2,4,6,2), new DEdge(4,6,2,4,4,2), new DEdge(4,4,2,1,5,2))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(6,4,2,4,6,2), new DEdge(4,6,2,6,6,2), new DEdge(6,6,2,6,4,2))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(5,9,2,4,6,2), new DEdge(4,6,2,6,6,2), new DEdge(6,6,2,5,9,2))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(6,4,2,9,5,2), new DEdge(9,5,2,6,6,2), new DEdge(6,6,2,6,4,2))));
	}

	public void testBoundaryIntegrity() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addPoint(new DPoint(1,6,0));
		mesh.addPoint(new DPoint(2,3,0));
		mesh.addPoint(new DPoint(4,7,0));
		mesh.addPoint(new DPoint(6,1,0));
		mesh.addPoint(new DPoint(7,5,0));
		mesh.addPoint(new DPoint(7,9,0));
		mesh.addPoint(new DPoint(9,11,0));
		mesh.addPoint(new DPoint(10,2,0));
		mesh.addPoint(new DPoint(12,10,0));
		mesh.addPoint(new DPoint(13,4,0));
		mesh.addPoint(new DPoint(13,7,0));
		mesh.processDelaunay();
//		show(mesh);
		List<DTriangle> triangles = mesh.getTriangleList();
		assertTrue(triangles.size()==12);
		assertTrue(triangles.contains(new DTriangle(new DEdge(1,6,0,2,3,0), new DEdge(2,3,0,4,7,0),new DEdge(4,7,0,1,6,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(1,6,0,7,9,0), new DEdge(7,9,0,4,7,0),new DEdge(4,7,0,1,6,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(1,6,0,7,9,0), new DEdge(7,9,0,9,11,0),new DEdge(9,11,0,1,6,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(12,10,0,7,9,0), new DEdge(7,9,0,9,11,0),new DEdge(9,11,0,12,10,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(12,10,0,7,9,0), new DEdge(7,9,0,13,7,0),new DEdge(13,7,0,12,10,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(7,5,0,7,9,0), new DEdge(7,9,0,13,7,0),new DEdge(13,7,0,7,5,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(7,5,0,13,4,0), new DEdge(13,4,0,13,7,0),new DEdge(13,7,0,7,5,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(7,5,0,13,4,0), new DEdge(13,4,0,10,2,0),new DEdge(10,2,0,7,5,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(7,5,0,6,1,0), new DEdge(6,1,0,10,2,0),new DEdge(10,2,0,7,5,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(7,5,0,4,7,0), new DEdge(4,7,0,2,3,0),new DEdge(2,3,0,7,5,0))));
		assertTrue(triangles.contains(new DTriangle(new DEdge(7,5,0,4,7,0), new DEdge(4,7,0,7,9,0),new DEdge(7,9,0,7,5,0))));
	}

	public void testLongConstraintLine()throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge (	44, 13, 40,
							44, 37, 40));
		mesh.addConstraintEdge(new DEdge (	44, 13, 40,
							54, 0 , 40));
		mesh.addConstraintEdge(new DEdge (	44, 37, 40,
							64, 36, 40));
		mesh.addConstraintEdge(new DEdge	(	62, 60, 40,
							66, 40, 40));
		mesh.addConstraintEdge(new DEdge (	64, 36, 40,
							66, 40, 40));
		mesh.addConstraintEdge(new DEdge (	85, 40, 40,
							88, 20, 40));
		mesh.addConstraintEdge(new DEdge (	0 , 60, 50,
							5 , 40, 50));
		mesh.addConstraintEdge(new DEdge (	5 , 40, 50,
							24, 11, 50));
		mesh.processDelaunay();
//		show(mesh);
		List<DTriangle> tri = mesh.getTriangleList();
		assertTrue(true);

	}

	public void testChezineStress() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge (	0, 15, 10,
							5, 13, 10));
		mesh.addConstraintEdge(new DEdge (	4, 0 , 10,
							5, 0 , 10));
		mesh.addConstraintEdge(new DEdge (	4, 0 , 10,
							10, 3, 10));
		mesh.addConstraintEdge(new DEdge (	5, 13, 10,
							8, 10, 10));
		mesh.addConstraintEdge(new DEdge (	8, 10, 10,
							10, 6, 10));
		mesh.addConstraintEdge(new DEdge (	10, 3, 10,
							12, 0, 10));
		mesh.processDelaunay();
//		show(mesh);
		List<DTriangle> tri = mesh.getTriangleList();
		assertTrue(tri.size()==7);
		assertTrue(tri.contains(new DTriangle(new DEdge(4,0,10,5,13,10), new DEdge(5,13,10,0,15,10), new DEdge(0,15,10,4,0,10))));
		assertTrue(tri.contains(new DTriangle(new DEdge(4,0,10,5,13,10), new DEdge(5,13,10,8,10,10), new DEdge(8,10,10,4,0,10))));
		assertTrue(tri.contains(new DTriangle(new DEdge(4,0,10,10,6,10), new DEdge(10,6,10,8,10,10), new DEdge(8,10,10,4,0,10))));
		assertTrue(tri.contains(new DTriangle(new DEdge(4,0,10,10,6,10), new DEdge(10,6,10,10,3,10), new DEdge(10,3,10,4,0,10))));
		assertTrue(tri.contains(new DTriangle(new DEdge(4,0,10,5,0,10), new DEdge(5,0,10,10,3,10), new DEdge(10,3,10,4,0,10))));
		assertTrue(tri.contains(new DTriangle(new DEdge(12,0,10,5,0,10), new DEdge(5,0,10,10,3,10), new DEdge(10,3,10,12,0,10))));
		assertTrue(tri.contains(new DTriangle(new DEdge(12,0,10,10,6,10), new DEdge(10,6,10,10,3,10), new DEdge(10,3,10,12,0,10))));
		
	}

	/**
	 * A test whose configuration is deducted from the chezine data.
	 * @throws DelaunayError
	 */
	public void testChezineStressBis() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge (	0, 6, 10,
							5, 12, 10));
		mesh.addConstraintEdge(new DEdge (	2, 0 , 10,
							5, 0, 10));
		mesh.addConstraintEdge(new DEdge (	3, 5, 10,
							5, 4, 10));
		mesh.addConstraintEdge(new DEdge (	3, 5, 10,
							5, 7, 10));
		mesh.addConstraintEdge(new DEdge (	5, 0 , 10,
							7, 0 , 10));
		mesh.addConstraintEdge(new DEdge (	5, 4, 10,
							7, 0, 10));
		mesh.addConstraintEdge(new DEdge (	5, 7, 10,
							6, 10, 10));
		mesh.addConstraintEdge(new DEdge (	5, 12, 10,
							6, 10, 10));
		mesh.processDelaunay();
//		show(mesh);
		List<DTriangle> tri = mesh.getTriangleList();
		assertTrue(tri.size()==10);
		assertTrue(tri.contains(new DTriangle(new DEdge(0,6,10,2,0,10), new DEdge(2,0,10,3,5,10), new DEdge(3,5,10,0,6,10))));
		assertTrue(tri.contains(new DTriangle(new DEdge(0,6,10,5,7,10), new DEdge(5,7,10,3,5,10), new DEdge(3,5,10,0,6,10))));
		assertTrue(tri.contains(new DTriangle(new DEdge(0,6,10,5,7,10), new DEdge(5,7,10,5,12,10), new DEdge(5,12,10,0,6,10))));
		assertTrue(tri.contains(new DTriangle(new DEdge(6,10,10,5,7,10), new DEdge(5,7,10,5,12,10), new DEdge(5,12,10,6,10,10))));
		assertTrue(tri.contains(new DTriangle(new DEdge(6,10,10,5,7,10), new DEdge(5,7,10,7,0,10), new DEdge(7,0,10,6,10,10))));
		assertTrue(tri.contains(new DTriangle(new DEdge(5,4,10,5,7,10), new DEdge(5,7,10,7,0,10), new DEdge(7,0,10,5,4,10))));
		assertTrue(tri.contains(new DTriangle(new DEdge(5,4,10,5,0,10), new DEdge(5,0,10,7,0,10), new DEdge(7,0,10,5,4,10))));
		assertTrue(tri.contains(new DTriangle(new DEdge(5,4,10,5,0,10), new DEdge(5,0,10,2,0,10), new DEdge(2,0,10,5,4,10))));
		assertTrue(tri.contains(new DTriangle(new DEdge(5,4,10,5,7,10), new DEdge(5,7,10,3,5,10), new DEdge(3,5,10,5,4,10))));
		assertTrue(tri.contains(new DTriangle(new DEdge(2,0,10,5,4,10), new DEdge(5,4,10,3,5,10), new DEdge(3,5,10,2,0,10))));
	}

	/**
	 * A triangle was forgotten due to a bug when adding a constraint in the mesh.
	 * @throws DelaunayError
	 */
	public void testForgottenTriangle() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge (	0  , 4, 60.0,
							5  , 7, 60.0));
		mesh.addConstraintEdge(new DEdge (	4  , 10, 60.0,
							5  , 9, 60.0));
		mesh.addConstraintEdge(new DEdge (	5  , 7, 60.0,
							10 , 7, 60.0));
		mesh.addConstraintEdge(new DEdge (	5  , 10, 60.0,
							10 , 8, 60.0));
		mesh.addConstraintEdge(new DEdge (	12 , 0  , 60.0,
							15 , 2 , 60.0));
		mesh.processDelaunay();
//		show(mesh);
		List<DTriangle> tri = mesh.getTriangleList();
		assertTrue(tri.size()==10);
		assertTrue(tri.contains(new DTriangle(new DEdge(0,4,60,4,10,60), new DEdge(4,10,60,5,7,60), new DEdge(5,7,60,0,4,60))));
		assertTrue(tri.contains(new DTriangle(new DEdge(5,9,60,4,10,60), new DEdge(4,10,60,5,7,60), new DEdge(5,7,60,5,9,60))));
		assertTrue(tri.contains(new DTriangle(new DEdge(5,9,60,10,8,60), new DEdge(10,8,60,5,7,60), new DEdge(5,7,60,5,9,60))));
		assertTrue(tri.contains(new DTriangle(new DEdge(10,7,60,10,8,60), new DEdge(10,8,60,5,7,60), new DEdge(5,7,60,10,7,60))));
		assertTrue(tri.contains(new DTriangle(new DEdge(5,9,60,4,10,60), new DEdge(4,10,60,5,10,60), new DEdge(5,10,60,5,9,60))));
		assertTrue(tri.contains(new DTriangle(new DEdge(5,9,60,10,8,60), new DEdge(10,8,60,5,10,60), new DEdge(5,10,60,5,9,60))));
		assertTrue(tri.contains(new DTriangle(new DEdge(10,7,60,10,8,60), new DEdge(10,8,60,15,2,60), new DEdge(15,2,60,10,7,60))));
		assertTrue(tri.contains(new DTriangle(new DEdge(10,7,60,12,0,60), new DEdge(12,0,60,15,2,60), new DEdge(15,2,60,10,7,60))));
		assertTrue(tri.contains(new DTriangle(new DEdge(10,7,60,12,0,60), new DEdge(12,0,60,5,7,60), new DEdge(5,7,60,10,7,60))));
		assertTrue(tri.contains(new DTriangle(new DEdge(0,4,60,12,0,60), new DEdge(12,0,60,5,7,60), new DEdge(5,7,60,0,4,60))));
		List<DEdge> edges = mesh.getEdges();
		assertTrue(edges.size()==18);
		assertTrue(edges.contains(new DEdge(0,4,60,4,10,60)));
		assertTrue(edges.contains(new DEdge(0,4,60,12,0,60)));
		assertTrue(edges.contains(new DEdge(0,4,60,5,7,60)));
		assertTrue(edges.contains(new DEdge(4,10,60,5,7,60)));
		assertTrue(edges.contains(new DEdge(12,0,60,5,7,60)));
		assertTrue(edges.contains(new DEdge(4,10,60,5,9,60)));
		assertTrue(edges.contains(new DEdge(5,9,60,5,7,60)));
		assertTrue(edges.contains(new DEdge(10,8,60,5,7,60)));
		assertTrue(edges.contains(new DEdge(10,7,60,5,7,60)));
		assertTrue(edges.contains(new DEdge(5,10,60,4,10,60)));
		assertTrue(edges.contains(new DEdge(5,10,60,5,9,60)));
		assertTrue(edges.contains(new DEdge(5,10,60,10,8,60)));
		assertTrue(edges.contains(new DEdge(5,9,60,10,8,60)));
		assertTrue(edges.contains(new DEdge(5,7,60,10,8,60)));
		assertTrue(edges.contains(new DEdge(10,7,60,10,8,60)));
		assertTrue(edges.contains(new DEdge(10,7,60,15,2,60)));
		assertTrue(edges.contains(new DEdge(10,7,60,12,0,60)));
		assertTrue(edges.contains(new DEdge(15,2,60,12,0,60)));
	}

	/**
	 * This test caused problem, the generation of the start boundary did not work.
	 * @throws DelaunayError
	 */
	public void testBuildStartBound() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge (	0  , 5, 0,
							1, 3, 0));
		mesh.addConstraintEdge(new DEdge (	0  , 5, 0,
							7, 8, 0));
		mesh.addConstraintEdge(new DEdge (	1, 3, 0,
							6  , 1, 0));
		mesh.addConstraintEdge(new DEdge (	6  , 1, 0,
							8, 0 , 0));
		mesh.addConstraintEdge(new DEdge (	7, 8, 0,
							12 , 10, 0));
		mesh.processDelaunay();
//		show(mesh);
		List<DTriangle> tri = mesh.getTriangleList();
		assertTrue(tri.size()==5);
		assertTrue(tri.contains(new DTriangle(new DEdge(7,8,0,0,5,0), new DEdge(0,5,0,1,3,0), new DEdge(1,3,0,7,8,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(7,8,0,6,1,0), new DEdge(6,1,0,1,3,0), new DEdge(1,3,0,7,8,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(7,8,0,6,1,0), new DEdge(6,1,0,8,0,0), new DEdge(8,0,0,7,8,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(7,8,0,12,10,0), new DEdge(12,10,0,8,0,0), new DEdge(8,0,0,7,8,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(1,3,0,6,1,0), new DEdge(6,1,0,8,0,0), new DEdge(8,0,0,1,3,0))));
		assertTrue(mesh.getEdges().size()==10);
	}

	/**
	 * A test with a vertical constraint that share its left point with another one.
	 * @throws DelaunayError
	 */
	public void testVerticalConstraintLinked() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge (	6, 2, 0,
							6, 7, 0));
		mesh.addConstraintEdge(new DEdge (	6, 2, 0,
							8, 0 , 0));
		mesh.addConstraintEdge(new DEdge (	6, 7, 0,
							11, 6, 0));
		mesh.addConstraintEdge(new DEdge (	0 , 12, 0,
							2 , 8, 0));
		mesh.processDelaunay();
//		show(mesh);
		List<DTriangle> tri = mesh.getTriangleList();
		assertTrue(tri.size()==5);
		assertTrue(tri.contains(new DTriangle(new DEdge(6,7,0,11,6,0), new DEdge(11,6,0,0,12,0), new DEdge(0,12,0,6,7,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(6,7,0,2,8,0), new DEdge(2,8,0,0,12,0), new DEdge(0,12,0,6,7,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(6,7,0,2,8,0), new DEdge(2,8,0,6,2,0), new DEdge(6,2,0,6,7,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(6,7,0,11,6,0), new DEdge(11,6,0,6,2,0), new DEdge(6,2,0,6,7,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(8,0,0,11,6,0), new DEdge(11,6,0,6,2,0), new DEdge(6,2,0,8,0,0))));
		assertTrue(mesh.getEdges().size()==10);
	}

	/**
	 * A test with raw data from the catalunya level lines
	 * DPoint order :
	 * This tests may cause null pointer exception.
	 *
	 * @throws DelaunayError
	 */
	public void testFromCatalunya() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge (	1.5, 1, 0,
							6  , 0, 0));
		mesh.addConstraintEdge(new DEdge (	2, 8, 0,
							10 , 5, 0));
		mesh.addConstraintEdge(new DEdge (	2.5, 9, 0,
							19 , 5, 0));
		mesh.addConstraintEdge(new DEdge (	3, 13, 0,
							5  , 12, 0));
		mesh.processDelaunay();
//		show(mesh);
		List<DTriangle> tri = mesh.getTriangleList();
		assertTrue(tri.size()==8);
		assertTrue(tri.contains(new DTriangle(new DEdge(2.5,9,0,2,8,0), new DEdge(2,8,0,3,13,0), new DEdge(3,13,0,2.5,9,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(2.5,9,0,5,12,0), new DEdge(5,12,0,3,13,0), new DEdge(3,13,0,2.5,9,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(2.5,9,0,5,12,0), new DEdge(5,12,0,19,5,0), new DEdge(19,5,0,2.5,9,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(2.5,9,0,10,5,0), new DEdge(10,5,0,19,5,0), new DEdge(19,5,0,2.5,9,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(2.5,9,0,10,5,0), new DEdge(10,5,0,2,8,0), new DEdge(2,8,0,2.5,9,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(6,0,0,10,5,0), new DEdge(10,5,0,2,8,0), new DEdge(2,8,0,6,0,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(6,0,0,10,5,0), new DEdge(10,5,0,19,5,0), new DEdge(19,5,0,6,0,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(6,0,0,1.5,1,0), new DEdge(1.5,1,0,2,8,0), new DEdge(2,8,0,6,0,0))));
	}

	public void testFromCatalunyaRicher() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge (	2.3, 5, 0,
							8, 9, 0));
		mesh.addConstraintEdge(new DEdge (	2.4, 48, 0,
							7, 46, 0));
		mesh.addConstraintEdge(new DEdge (	2.45, 62, 0,
							11, 58, 0));
		mesh.addConstraintEdge(new DEdge (	2.55, 66, 0,
							20, 57, 0));
		mesh.addConstraintEdge(new DEdge (	2.6, 90, 0,
							5, 82, 0));
//		show(mesh);
		mesh.processDelaunay();
//		show(mesh);
		List<DTriangle> tri = mesh.getTriangleList();
		assertTrue(tri.size()==12);
		assertTrue(tri.contains(new DTriangle(new DEdge(2.6,90,0,2.55,66,0), new DEdge(2.55,66,0,2.45,62,0), new DEdge(2.45,62,0,2.6,90,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(2.6,90,0,2.55,66,0), new DEdge(2.55,66,0,5,82,0), new DEdge(5,82,0,2.6,90,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(2.6,90,0,20,57,0), new DEdge(20,57,0,5,82,0), new DEdge(5,82,0,2.6,90,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(2.55,66,0,20,57,0), new DEdge(20,57,0,5,82,0), new DEdge(5,82,0,2.55,66,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(2.55,66,0,20,57,0), new DEdge(20,57,0,11,58,0), new DEdge(11,58,0,2.55,66,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(7,46,0,20,57,0), new DEdge(20,57,0,11,58,0), new DEdge(11,58,0,7,46,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(8,9,0,20,57,0), new DEdge(20,57,0,7,46,0), new DEdge(7,46,0,8,9,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(2.45,62,0,2.4,48,0), new DEdge(2.4,48,0,11,58,0), new DEdge(11,58,0,2.45,62,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(2.45,62,0,2.55,66,0), new DEdge(2.55,66,0,11,58,0), new DEdge(11,58,0,2.45,62,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(7,46,0,2.4,48,0), new DEdge(2.4,48,0,11,58,0), new DEdge(11,58,0,7,46,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(7,46,0,2.4,48,0), new DEdge(2.4,48,0,8,9,0), new DEdge(8,9,0,7,46,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(2.3,5,0,2.4,48,0), new DEdge(2.4,48,0,8,9,0), new DEdge(8,9,0,2.3,5,0))));
		int index = mesh.getEdges().indexOf(new DEdge(2.3,5,0,8,9,0));
		assertTrue(mesh.getEdges().size()==21);
		assertTrue(mesh.getEdges().get(index).isLocked());

	}

	public void testLowestConstraintManagement() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge (298508.5, 2258710.7, 0.0, 298541.30000000005, 2258672.200000001, 0.0));
		mesh.addConstraintEdge(new DEdge (298508.5, 2258710.7, 0.0, 298542.5, 2258759.5999999996, 0.0));
		mesh.addConstraintEdge(new DEdge (298509.10000000003, 2258861.1000000006, 0.0, 298516.9, 2258927.7, 0.0));
		mesh.addConstraintEdge(new DEdge (298509.10000000003, 2258861.1000000006, 0.0, 298540.29999999993, 2258842.299999999, 0.0));
		mesh.addConstraintEdge(new DEdge (298569.0999999999, 2258616.299999999, 0.0, 298571.99999999994, 2258623.999999999, 0.0));
		mesh.processDelaunay();
		assertTrue(mesh.getTriangleList().size()==8);
		assertTrue(mesh.getTriangleList().contains(new DTriangle(
							new DEdge(298508.5, 2258710.7, 0.0,
								298541.30000000005, 2258672.200000001, 0.0),
							new DEdge(298541.30000000005, 2258672.200000001, 0.0,
								298542.5, 2258759.5999999996, 0.0),
							new DEdge(298542.5, 2258759.5999999996, 0.0,
								298508.5, 2258710.7, 0.0))));
		assertTrue(mesh.getTriangleList().contains(new DTriangle(
							new DEdge(298508.5, 2258710.7, 0.0,
								298509.10000000003, 2258861.1000000006, 0.0),
							new DEdge(298509.10000000003, 2258861.1000000006, 0.0,
								298542.5, 2258759.5999999996, 0.0),
							new DEdge(298542.5, 2258759.5999999996, 0.0,
								298508.5, 2258710.7, 0.0))));
		assertTrue(mesh.getTriangleList().contains(new DTriangle(
							new DEdge(298540.29999999993, 2258842.299999999, 0.0,
								298509.10000000003, 2258861.1000000006, 0.0),
							new DEdge(298509.10000000003, 2258861.1000000006, 0.0,
								298542.5, 2258759.5999999996, 0.0),
							new DEdge(298542.5, 2258759.5999999996, 0.0,
								298540.29999999993, 2258842.299999999, 0.0))));
		assertTrue(mesh.getTriangleList().contains(new DTriangle(
							new DEdge(298540.29999999993, 2258842.299999999, 0.0,
								298571.99999999994, 2258623.999999999, 0.0),
							new DEdge(298571.99999999994, 2258623.999999999, 0.0,
								298542.5, 2258759.5999999996, 0.0),
							new DEdge(298542.5, 2258759.5999999996, 0.0,
								298540.29999999993, 2258842.299999999, 0.0))));
		assertTrue(mesh.getTriangleList().contains(new DTriangle(
							new DEdge(298541.30000000005, 2258672.200000001, 0.0,
								298571.99999999994, 2258623.999999999, 0.0),
							new DEdge(298571.99999999994, 2258623.999999999, 0.0,
								298542.5, 2258759.5999999996, 0.0),
							new DEdge(298542.5, 2258759.5999999996, 0.0,
								298541.30000000005, 2258672.200000001, 0.0))));
		assertTrue(mesh.getTriangleList().contains(new DTriangle(
							new DEdge(298541.30000000005, 2258672.200000001, 0.0,
								298571.99999999994, 2258623.999999999, 0.0),
							new DEdge(298571.99999999994, 2258623.999999999, 0.0,
								298569.0999999999, 2258616.299999999, 0.0),
							new DEdge(298569.0999999999, 2258616.299999999, 0.0,
								298541.30000000005, 2258672.200000001, 0.0))));
		assertTrue(mesh.getTriangleList().contains(new DTriangle(
							new DEdge(298541.30000000005, 2258672.200000001, 0.0,
								298508.5, 2258710.7, 0.0),
							new DEdge(298508.5, 2258710.7, 0.0,
								298569.0999999999, 2258616.299999999, 0.0),
							new DEdge(298569.0999999999, 2258616.299999999, 0.0,
								298541.30000000005, 2258672.200000001, 0.0))));
		assertTrue(mesh.getTriangleList().contains(new DTriangle(
							new DEdge(298540.29999999993, 2258842.299999999, 0.0,
								298509.10000000003, 2258861.1000000006, 0.0),
							new DEdge(298509.10000000003, 2258861.1000000006, 0.0,
								298516.9, 2258927.7, 0.0),
							new DEdge(298516.9, 2258927.7, 0.0,
								298540.29999999993, 2258842.299999999, 0.0))));
	}

	public void testVerticalEdgePrecisionProblem() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge (300638.4000000001, 2260120.0, 0.0, 300641.3, 2260119.5, 0.0));
		mesh.addConstraintEdge(new DEdge (300641.29999999993, 2260093.5, 0.0, 300641.3, 2260113.9000000004, 0.0));
		mesh.addConstraintEdge(new DEdge (300641.29999999993, 2260093.5, 0.0, 300641.3, 2260119.5, 0.0));
		mesh.addConstraintEdge(new DEdge (300641.29999999993, 2260093.5, 0.0, 300641.4000000001, 2260085.4000000013, 0.0));
		mesh.addConstraintEdge(new DEdge (300641.29999999993, 2260093.5, 0.0, 300671.9, 2260092.5999999996, 0.0));
		mesh.addConstraintEdge(new DEdge (300641.3, 2260113.9000000004, 0.0, 300641.3, 2260119.5, 0.0));
		mesh.forceConstraintIntegrity();
		mesh.processDelaunay();
//		show(mesh);
		assertTrue(mesh.getTriangleList().size()==6);
	}

	/**
	 * This configuration, that comes from the Nantes landuse, caused problems in the triangulation
	 * computation.
	 * @throws DelaunayError
	 */
	public void testParcellaireExcerpt() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge (300640.3, 2260085.2, 0.0, 300641.4000000001, 2260085.4000000013, 0.0));
		mesh.addConstraintEdge(new DEdge (300641.10000000003, 2259945.6000000006, 0.0, 300641.3, 2259944.8000000007, 0.0));
		mesh.addConstraintEdge(new DEdge (300641.29999999993, 2260093.5, 0.0, 300641.4000000001, 2260085.4000000013, 0.0));
		mesh.addConstraintEdge(new DEdge (300641.29999999993, 2260093.5, 0.0, 300641.70000000007, 2260059.000000001, 0.0));
		mesh.addConstraintEdge(new DEdge (300641.3, 2260113.9000000004, 0.0, 300641.3, 2260119.5, 0.0));
		mesh.addConstraintEdge(new DEdge (300641.70000000007, 2260059.000000001, 0.0, 300670.80000000005, 2260054.4000000013, 0.0));
		mesh.forceConstraintIntegrity();
		mesh.processDelaunay();
		List<DTriangle> triangles = mesh.getTriangleList();
		assertTrue(triangles.size()==13);
//		show(mesh);
	}

	/**
	 * This configuration caused some problems, as an intersection was not seen by the
	 * intersection algorithm.
	 * @throws DelaunayError
	 */
	public void testMistyIntersection() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge (296448.7, 2254716.8, 0.0, 296449.60000000003, 2254721.9000000013, 0.0));
		mesh.addConstraintEdge(new DEdge (296448.8, 2254721.9000000004, 0.0, 296449.0999999999, 2254720.5999999987, 0.0));
		mesh.addConstraintEdge(new DEdge (296449.0999999999, 2254720.5999999987, 0.0, 296450.9, 2254714.3, 0.0));
		mesh.forceConstraintIntegrity();
		assertTrue(mesh.getConstraintEdges().size()==5);
		mesh.processDelaunay();
//		show(mesh);
		List<DTriangle> triangles = mesh.getTriangleList();
		assertTrue(triangles.size()==6);

	}

	/**
	 * A point were missing, due to a problem when managing vertical constraints.
	 * @throws DelaunayError
	 */
	public void testMissingIntersectionPoint() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge (296458.5, 2254707.7, 0.0, 296459.50000000006, 2254696.6000000006, 0.0));
		mesh.addConstraintEdge(new DEdge (296459.00000000006, 2254700.6000000006, 0.0, 296459.0, 2254703.700000001, 0.0));
		mesh.addConstraintEdge(new DEdge (296459.00000000006, 2254700.6000000006, 0.0, 296459.3, 2254697.5, 0.0));
		mesh.forceConstraintIntegrity();
		assertTrue(mesh.getConstraintEdges().size()==5);
		mesh.processDelaunay();
//		show(mesh);
	}

	/**
	 * A problematic configuration from buildings of the Nantes area. Two overlapping
	 * constraint edges were not merged during the processing of the constraints intersections.
	 * @throws DelaunayError
	 */
	public void testProblemConfigBati() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge (311784.2, 2251488.4, 25.7, 311792.0, 2251487.1, 25.9));
		mesh.addConstraintEdge(new DEdge (311784.9, 2251484.8, 27.0, 311785.4, 2251488.2, 25.7));
		mesh.addConstraintEdge(new DEdge (311784.9, 2251484.8, 27.0, 311791.9, 2251483.8, 27.9));
		mesh.addConstraintEdge(new DEdge (311785.4, 2251488.2, 25.7, 311792.0, 2251487.1, 25.9));
		mesh.forceConstraintIntegrity();
		mesh.processDelaunay();
		assertTrue(mesh.getTriangleList().size()==3);
		assertTrue(mesh.getConstraintEdges().size()==4);
		assertTrue(mesh.getConstraintEdges().contains(new DEdge(311784.2, 2251488.4, 25.7,311785.4, 2251488.2, 25.7)));
		assertTrue(mesh.getConstraintEdges().contains(new DEdge(311785.4, 2251488.2, 25.7,311792.0, 2251487.1, 25.9)));
		assertTrue(mesh.getConstraintEdges().contains(new DEdge(311785.4, 2251488.2, 25.7,311784.9, 2251484.8, 27.0)));
		assertTrue(mesh.getConstraintEdges().contains(new DEdge(311784.9, 2251484.8, 27.0,311791.9, 2251483.8, 27.9)));
//		show(mesh);
	}

	/**
	 * This configuration caused a problem because of a bad insertion in the boundary.
	 * @throws DelaunayError
	 */
	public void testCantBuildTriangle() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge (	0.97, 1, 10,
							7.10, 3.5, 11));
		mesh.addConstraintEdge(new DEdge (	1.27, 73, 20,
							5.55, 71, 21));
		mesh.addConstraintEdge(new DEdge (	1.45, 115, 30,
							4.22, 107, 31));
		mesh.addConstraintEdge(new DEdge (	1.49, 125, 40,
							2.81, 135, 41));
		mesh.addConstraintEdge(new DEdge (	5.19, 1024, 50,
							7.87, 1024, 51));
//		show(mesh);
		try{
			mesh.processDelaunay();
			assertTrue(true);
		} catch (DelaunayError d){
			assertFalse(true);
		}
//		show(mesh);
	}

	/**
	 * This test and the next two ones have been created because the configurations
	 * they contain caused DelaunayErrors or NullPointerExceptions. We don't check
	 * the content of the generated mesh, but just check we don't catch
	 * any delaunay error.
	 * @throws DelaunayError
	 */
	public void testCantBuildTriangleBis() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge (	0.0, 0.0, 0,
							4.04, -3.89, 0));
		mesh.addConstraintEdge(new DEdge (	0.83, 199,0,
							5.77, 202,0));
		mesh.addConstraintEdge(new DEdge (	0.97, 235, 0,
							7.10, 238, 0));
		mesh.addConstraintEdge(new DEdge (	1.27, 307, 0,
							5.55, 306,0));
		mesh.addConstraintEdge(new DEdge (	1.35, 326, 0,
							18.41, 317, 0));
		mesh.addConstraintEdge(new DEdge (	1.45, 350, 0,
							4.22, 342, 0));
		mesh.addConstraintEdge(new DEdge (	1.49, 360, 0,
							2.81, 370, 0));
//		show(mesh);
		try{
			mesh.processDelaunay();
			assertTrue(true);
		} catch (DelaunayError d){
			assertFalse(true);
		}
//		show(mesh);
	}

	public void testCantBuildTriangleTer() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge (	0.0, 0.0, 0,
							4.04, -3.89, 0));
		mesh.addConstraintEdge(new DEdge (	0.83, 199, 0,
							5.77, 203, 0));
		mesh.addConstraintEdge(new DEdge (	0.97, 235.18, 0,
							7.10, 239, 0));
		mesh.addConstraintEdge(new DEdge (	1.27, 307.82, 0,
							5.55, 306, 0));
		mesh.addConstraintEdge(new DEdge (	1.49, 360, 0,
							2.81, 370, 0));
		mesh.addConstraintEdge(new DEdge (	1.78, 430, 0,
							3.75, 427, 0));
		mesh.addConstraintEdge(new DEdge (	2.22, 537, 0,
							14.17, 533, 0));
		mesh.addConstraintEdge(new DEdge (	2.45, 593, 0,
							9.41, 592, 0));
		try{
			mesh.processDelaunay();
			assertTrue(true);
		} catch (DelaunayError d){
			assertFalse(true);
		}
		
	}

	public void testCantBuildTriangleQuattro() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge (	0.27000000001862645, 218.3300000000745, 660.0,
							1.7199999999720603, 213.97000000067055, 660.0));
		mesh.addConstraintEdge(new DEdge (	0.8400000000256114, 150.29000000003725, 660.0,
							2.7899999999790452, 152.97000000067055, 660.0));
		mesh.addConstraintEdge(new DEdge (	0.9299999999930151, 312.910000000149, 710.0,
							3.2100000000209548, 310.3500000005588, 710.0));
		mesh.addConstraintEdge(new DEdge (	1.7199999999720603, 213.97000000067055, 660.0,
							5.349999999976717, 191.30000000074506, 660.0));
		mesh.addConstraintEdge(new DEdge (	2.0200000000186265, 91.84000000078231, 680.0,
							9.940000000002328, 104.0100000007078, 680.0));
		mesh.addConstraintEdge(new DEdge (	2.650000000023283, 0.0, 720.0,
							3.070000000006985, 24.12000000011176, 720.0));
		try{
			mesh.processDelaunay();
//			show(mesh);
			assertTrue(true);
		} catch (DelaunayError d){
			assertFalse(true);
		}
		List<DTriangle> tri = mesh.getTriangleList();
		assertTrue(tri.size()==14);
		assertTrue(tri.contains(new DTriangle(	new DEdge(0.27000000001862645, 218.3300000000745, 660.0,
									5.349999999976717, 191.30000000074506, 660.0),
								new DEdge(5.349999999976717, 191.30000000074506, 660.0,
									0.8400000000256114, 150.29000000003725, 660.0),
								new DEdge(0.8400000000256114, 150.29000000003725, 660.0,
									0.27000000001862645, 218.3300000000745, 660.0))));
		assertTrue(tri.contains(new DTriangle(	new DEdge(0.27000000001862645, 218.3300000000745, 660.0,
									3.2100000000209548, 310.3500000005588, 710.0),
								new DEdge(3.2100000000209548, 310.3500000005588, 710.0,
									1.7199999999720603, 213.97000000067055, 660.0),
								new DEdge(1.7199999999720603, 213.97000000067055, 660.0,
									0.27000000001862645, 218.3300000000745, 660.0))));
		assertTrue(tri.contains(new DTriangle(	new DEdge(2.0200000000186265, 91.84000000078231, 680.0,
									2.650000000023283, 0.0, 720.0),
								new DEdge(2.650000000023283, 0.0, 720.0,
									0.8400000000256114, 150.29000000003725, 660.0),
								new DEdge(0.8400000000256114, 150.29000000003725, 660.0,
									2.0200000000186265, 91.84000000078231, 680.0))));

	}

	/**
	 * A test designed with cross constraints, from girona level lines.
	 * x order :
	 *
	 * 0.0
	 * 0.77
	 * 1.19
	 * 1.45
	 * 1.82
	 * 2.58
	 * 3.20
	 * 3.70
	 * 4.88
	 * 5.97
	 * 6.45
	 * 6.65
	 * 6.89
	 * 7.76
	 * 9.5
	 * 
	 * @throws DelaunayError
	 */
	public void testCrossedConstraints() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge (	0.0, 4.18, 770.0,
							2.58, 8.06, 770.0));
		mesh.addConstraintEdge(new DEdge (	0.77, 5.33, 770.0,
							1.19, 9.87, 763.0));
		mesh.addConstraintEdge(new DEdge (	0.77, 5.33, 770.0,
							1.45, 4.79, 773.0));
		mesh.addConstraintEdge(new DEdge (	1.19, 9.87, 763.0,
							6.65, 13.48, 763.0));
		mesh.addConstraintEdge(new DEdge (	1.45, 4.79, 773.0,
							3.70, 2.91, 780.0));
		mesh.addConstraintEdge(new DEdge (	1.82, 0.0, 780.0,
							3.20, 1.5, 780.0));
		mesh.addConstraintEdge(new DEdge (	3.20, 1.5, 780.0,
							4.88, 6.25, 780.0));
		mesh.addConstraintEdge(new DEdge (	3.70, 2.91, 780.0,
							5.97, 3.29, 787.0));
		mesh.addConstraintEdge(new DEdge (	5.97, 3.29, 787.0,
							6.89, 5.47, 787.0));
		mesh.addConstraintEdge(new DEdge (	6.45, 2.04, 790.0,
							7.76, 5.12, 790.0));
		mesh.addConstraintEdge(new DEdge (	6.89, 5.47, 787.0,
							9.5 , 8.04, 787.0));
//		show(mesh);
		mesh.forceConstraintIntegrity();
//		show(mesh);
		try{
			mesh.processDelaunay();
			assertTrue(true);
		} catch (DelaunayError d){
			assertFalse(true);
		}
//		show(mesh);
	}

	/**
	 * Process a single encroached DEdge in a mesh.
	 * @throws DelaunayError
	 */
	public void testSplitEncroachedEdges() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		DEdge e1 = new DEdge(0,3,0,8,3,0);
		mesh.addConstraintEdge(e1);
		mesh.addPoint(new DPoint(3, 0, 0));
		mesh.addPoint(new DPoint(2, 4.5, 0));
		mesh.processDelaunay();
		mesh.splitEncroachedEdge(e1,0.1);
//		show(mesh);
		assertTrue(mesh.getTriangleList().size()==6);
		assertTrue(mesh.getConstraintEdges().size()==3);
		assertTrue(mesh.getConstraintEdges().contains(new DEdge(0,3,0,2,3,0)));
		assertTrue(mesh.getConstraintEdges().contains(new DEdge(4,3,0,2,3,0)));
		assertTrue(mesh.getConstraintEdges().contains(new DEdge(4,3,0,8,3,0)));
		assertTrue(mesh.getTriangleList().contains(new DTriangle(
						new DEdge(0,3,0,3,0,0),
						new DEdge(3,0,0,2,3,0),
						new DEdge(2,3,0,0,3,0))));
		assertTrue(mesh.getTriangleList().contains(new DTriangle(
						new DEdge(4,3,0,3,0,0),
						new DEdge(3,0,0,2,3,0),
						new DEdge(2,3,0,4,3,0))));
		assertTrue(mesh.getTriangleList().contains(new DTriangle(
						new DEdge(4,3,0,3,0,0),
						new DEdge(3,0,0,8,3,0),
						new DEdge(8,3,0,4,3,0))));
		assertTrue(mesh.getTriangleList().contains(new DTriangle(
						new DEdge(4,3,0,2,4.5,0),
						new DEdge(2,4.5,0,8,3,0),
						new DEdge(8,3,0,4,3,0))));
		assertTrue(mesh.getTriangleList().contains(new DTriangle(
						new DEdge(4,3,0,2,4.5,0),
						new DEdge(2,4.5,0,2,3,0),
						new DEdge(2,3,0,4,3,0))));
		assertTrue(mesh.getTriangleList().contains(new DTriangle(
						new DEdge(0,3,0,2,4.5,0),
						new DEdge(2,4.5,0,2,3,0),
						new DEdge(2,3,0,0,3,0))));
		assertTrue(mesh.getEdges().size()==11);
		assertTrue(mesh.getPoints().size()==6);
		assertTrue(mesh.getPoints().contains(new DPoint(3,0,0)));
		assertTrue(mesh.getPoints().contains(new DPoint(2,4.5,0)));
		assertTrue(mesh.getPoints().contains(new DPoint(2,3,0)));
		assertTrue(mesh.getPoints().contains(new DPoint(4,3,0)));
		assertTrue(mesh.getPoints().contains(new DPoint(8,3,0)));
		assertTrue(mesh.getPoints().contains(new DPoint(0,3,0)));
		assertGIDUnicity(mesh);
	}

	/**
	 * Test the removal of an encroached DEdge, with a watershed that will
	 * block a split.
	 * @throws DelaunayError
	 */
	public void testEncroachedThreshold()  throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		DEdge e1 = new DEdge(0,3,0,8,3,0);
		mesh.addConstraintEdge(e1);
		mesh.addPoint(new DPoint(3, 0, 0));
		mesh.addPoint(new DPoint(1, 3.5, 0));
		mesh.processDelaunay();
		mesh.splitEncroachedEdge(e1,1.5);
//		show(mesh);
		assertTrue(mesh.getTriangleList().size()==6);
		assertTrue(mesh.getConstraintEdges().size()==3);
		assertTrue(mesh.getConstraintEdges().contains(new DEdge(0,3,0,2,3,0)));
		assertTrue(mesh.getConstraintEdges().contains(new DEdge(4,3,0,2,3,0)));
		assertTrue(mesh.getConstraintEdges().contains(new DEdge(4,3,0,8,3,0)));
		assertTrue(mesh.getTriangleList().contains(new DTriangle(
						new DEdge(0,3,0,3,0,0),
						new DEdge(3,0,0,2,3,0),
						new DEdge(2,3,0,0,3,0))));
		assertTrue(mesh.getTriangleList().contains(new DTriangle(
						new DEdge(4,3,0,3,0,0),
						new DEdge(3,0,0,2,3,0),
						new DEdge(2,3,0,4,3,0))));
		assertTrue(mesh.getTriangleList().contains(new DTriangle(
						new DEdge(4,3,0,3,0,0),
						new DEdge(3,0,0,8,3,0),
						new DEdge(8,3,0,4,3,0))));
		assertTrue(mesh.getTriangleList().contains(new DTriangle(
						new DEdge(4,3,0,1,3.5,0),
						new DEdge(1,3.5,0,8,3,0),
						new DEdge(8,3,0,4,3,0))));
		assertTrue(mesh.getTriangleList().contains(new DTriangle(
						new DEdge(4,3,0,1,3.5,0),
						new DEdge(1,3.5,0,2,3,0),
						new DEdge(2,3,0,4,3,0))));
		assertTrue(mesh.getTriangleList().contains(new DTriangle(
						new DEdge(0,3,0,1,3.5,0),
						new DEdge(1,3.5,0,2,3,0),
						new DEdge(2,3,0,0,3,0))));
		assertTrue(mesh.getEdges().size()==11);
		assertTrue(mesh.getEdges().contains(new DEdge(1,3.5,0,0,3,0)));
		assertTrue(mesh.getEdges().contains(new DEdge(1,3.5,0,2,3,0)));
		assertTrue(mesh.getEdges().contains(new DEdge(1,3.5,0,4,3,0)));
		assertTrue(mesh.getEdges().contains(new DEdge(1,3.5,0,8,3,0)));
		assertTrue(mesh.getEdges().contains(new DEdge(0,3,0,2,3,0)));
		assertTrue(mesh.getEdges().contains(new DEdge(4,3,0,2,3,0)));
		assertTrue(mesh.getEdges().contains(new DEdge(4,3,0,8,3,0)));
		assertTrue(mesh.getEdges().contains(new DEdge(3,0,0,0,3,0)));
		assertTrue(mesh.getEdges().contains(new DEdge(3,0,0,2,3,0)));
		assertTrue(mesh.getEdges().contains(new DEdge(3,0,0,4,3,0)));
		assertTrue(mesh.getEdges().contains(new DEdge(3,0,0,8,3,0)));
		assertTrue(mesh.getPoints().size()==6);
		assertTrue(mesh.getPoints().contains(new DPoint(3,0,0)));
		assertTrue(mesh.getPoints().contains(new DPoint(1,3.5,0)));
		assertTrue(mesh.getPoints().contains(new DPoint(2,3,0)));
		assertTrue(mesh.getPoints().contains(new DPoint(4,3,0)));
		assertTrue(mesh.getPoints().contains(new DPoint(8,3,0)));
		assertTrue(mesh.getPoints().contains(new DPoint(0,3,0)));
		assertGIDUnicity(mesh);
	}

	public void testEncroachedEdgeTwoRecursiveCalls() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		DEdge e1 = new DEdge(0,3,0,8,3,0);
		mesh.addConstraintEdge(e1);
		mesh.addPoint(new DPoint(3, 0, 0));
		mesh.addPoint(new DPoint(3, 3.5, 0));
		mesh.processDelaunay();
		mesh.splitEncroachedEdge(e1,0.01);
//		show(mesh);
		assertTrue(mesh.getTriangleList().size()==8);
		assertTrue(mesh.getConstraintEdges().size()==4);
		assertTrue(mesh.getConstraintEdges().contains(new DEdge(0,3,0,2,3,0)));
		assertTrue(mesh.getConstraintEdges().contains(new DEdge(3,3,0,2,3,0)));
		assertTrue(mesh.getConstraintEdges().contains(new DEdge(4,3,0,3,3,0)));
		assertTrue(mesh.getConstraintEdges().contains(new DEdge(4,3,0,8,3,0)));
		assertTrue(mesh.getTriangleList().contains(new DTriangle(
						new DEdge(0,3,0,3,0,0),
						new DEdge(3,0,0,2,3,0),
						new DEdge(2,3,0,0,3,0))));
		assertTrue(mesh.getTriangleList().contains(new DTriangle(
						new DEdge(3,3,0,3,0,0),
						new DEdge(3,0,0,2,3,0),
						new DEdge(2,3,0,3,3,0))));
		assertTrue(mesh.getTriangleList().contains(new DTriangle(
						new DEdge(3,3,0,3,0,0),
						new DEdge(3,0,0,4,3,0),
						new DEdge(4,3,0,3,3,0))));
		assertTrue(mesh.getTriangleList().contains(new DTriangle(
						new DEdge(4,3,0,3,0,0),
						new DEdge(3,0,0,8,3,0),
						new DEdge(8,3,0,4,3,0))));
		assertTrue(mesh.getTriangleList().contains(new DTriangle(
						new DEdge(4,3,0,3,3.5,0),
						new DEdge(3,3.5,0,8,3,0),
						new DEdge(8,3,0,4,3,0))));
		assertTrue(mesh.getTriangleList().contains(new DTriangle(
						new DEdge(4,3,0,3,3.5,0),
						new DEdge(3,3.5,0,3,3,0),
						new DEdge(3,3,0,4,3,0))));
		assertTrue(mesh.getTriangleList().contains(new DTriangle(
						new DEdge(2,3,0,3,3.5,0),
						new DEdge(3,3.5,0,3,3,0),
						new DEdge(3,3,0,2,3,0))));
		assertTrue(mesh.getTriangleList().contains(new DTriangle(
						new DEdge(0,3,0,3,3.5,0),
						new DEdge(3,3.5,0,2,3,0),
						new DEdge(2,3,0,0,3,0))));
		assertTrue(mesh.getEdges().size()==14);
		assertTrue(mesh.getEdges().contains(new DEdge(3,3.5,0,0,3,0)));
		assertTrue(mesh.getEdges().contains(new DEdge(3,3.5,0,2,3,0)));
		assertTrue(mesh.getEdges().contains(new DEdge(3,3.5,0,3,3,0)));
		assertTrue(mesh.getEdges().contains(new DEdge(3,3.5,0,4,3,0)));
		assertTrue(mesh.getEdges().contains(new DEdge(3,3.5,0,8,3,0)));
		assertTrue(mesh.getEdges().contains(new DEdge(0,3,0,2,3,0)));
		assertTrue(mesh.getEdges().contains(new DEdge(3,3,0,2,3,0)));
		assertTrue(mesh.getEdges().contains(new DEdge(4,3,0,3,3,0)));
		assertTrue(mesh.getEdges().contains(new DEdge(4,3,0,8,3,0)));
		assertTrue(mesh.getEdges().contains(new DEdge(3,0,0,0,3,0)));
		assertTrue(mesh.getEdges().contains(new DEdge(3,0,0,2,3,0)));
		assertTrue(mesh.getEdges().contains(new DEdge(3,0,0,3,3,0)));
		assertTrue(mesh.getEdges().contains(new DEdge(3,0,0,4,3,0)));
		assertTrue(mesh.getEdges().contains(new DEdge(3,0,0,8,3,0)));
		assertTrue(mesh.getPoints().size()==7);
		assertTrue(mesh.getPoints().contains(new DPoint(3,0,0)));
		assertTrue(mesh.getPoints().contains(new DPoint(3,3.5,0)));
		assertTrue(mesh.getPoints().contains(new DPoint(2,3,0)));
		assertTrue(mesh.getPoints().contains(new DPoint(3,3,0)));
		assertTrue(mesh.getPoints().contains(new DPoint(4,3,0)));
		assertTrue(mesh.getPoints().contains(new DPoint(8,3,0)));
		assertTrue(mesh.getPoints().contains(new DPoint(0,3,0)));
	}
}
