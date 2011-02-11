/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdelaunay.delaunay;

import java.util.Collections;
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
		Edge constr = new Edge(0,3,0,8,3,0);
//		mesh.addConstraintEdge(constr);
//		mesh.addPoint(new Point(4,5,0));
//		mesh.addPoint(new Point(4,1,0));
//		mesh.processDelaunay();
////		show(mesh);
		DelaunayTriangle tri1 = new DelaunayTriangle(constr, new Edge(0,3,0,4,5,0), new Edge(4,5,0,8,3,0));
		DelaunayTriangle tri2 = new DelaunayTriangle(constr, new Edge(0,3,0,4,1,0), new Edge(4,1,0,8,3,0));
//		assertTrue(mesh.getTriangleList().contains(tri1));
//		assertTrue(mesh.getTriangleList().contains(tri2));
//		assertTrue(mesh.isMeshComputed());

		mesh = new ConstrainedMesh();
		constr = new Edge(3,0,0,3,6,0);
		mesh.addConstraintEdge(constr);
		mesh.addPoint(new Point(1,3,0));
		mesh.addPoint(new Point(5,1,0));
		mesh.processDelaunay();
//		show(mesh);
		tri1 = new DelaunayTriangle(constr, new Edge(1,3,0,3,6,0), new Edge(1,3,0,3,0,0));
		tri2 = new DelaunayTriangle(constr, new Edge(3,6,0,5,1,0), new Edge(5,1,0,3,0,0));
		assertTrue(mesh.getTriangleList().size()==2);
		assertTrue(mesh.getTriangleList().contains(tri1));
		assertTrue(mesh.getTriangleList().contains(tri2));
		assertTrue(mesh.isMeshComputed());
	}

	/**
	 * Another test case, with a flip-flap during the processing.
	 * The input contains one horizontal constraints with two points upper
	 * and two points lower than it
	 * @throws DelaunayError
	 */
	public void testOneConstraintFourPoints() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new Edge(0,3,0,8,3,0));
		mesh.addPoint(new Point(3,1,0));
		mesh.addPoint(new Point(5,0,0));
		mesh.addPoint(new Point(4,5,0));
		mesh.addPoint(new Point(6,4,0));
		mesh.processDelaunay();
//		show(mesh);
		List<DelaunayTriangle> triangles = mesh.getTriangleList();
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0,3,0,8,3,0), new Edge(8,3,0,3,1,0), new Edge(3,1,0,0,3,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(5,0,0,8,3,0), new Edge(8,3,0,3,1,0), new Edge(3,1,0,5,0,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0,3,0,4,5,0), new Edge(4,5,0,6,4,0), new Edge(6,4,0,0,3,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0,3,0,8,3,0), new Edge(8,3,0,6,4,0), new Edge(6,4,0,0,3,0))));
	}

	/**
	 * the same configuration as the previous test, with a larger input.
	 * @throws DelaunayError
	 */
	public void testOneConstraintFourPointsExtended() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new Edge(0,3,0,8,3,0));
		mesh.addConstraintEdge(new Edge(10,0,0,10,6,0));
		mesh.addPoint(new Point(3,1,0));
		mesh.addPoint(new Point(5,0,0));
		mesh.addPoint(new Point(4,5,0));
		mesh.addPoint(new Point(6,4,0));
//		mesh.addPoint(new Point(9,6,0));
		mesh.processDelaunay();
//		show(mesh);
		List<DelaunayTriangle> triangles = mesh.getTriangleList();
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0,3,0,8,3,0), new Edge(8,3,0,3,1,0), new Edge(3,1,0,0,3,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(5,0,0,8,3,0), new Edge(8,3,0,3,1,0), new Edge(3,1,0,5,0,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0,3,0,4,5,0), new Edge(4,5,0,6,4,0), new Edge(6,4,0,0,3,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0,3,0,8,3,0), new Edge(8,3,0,6,4,0), new Edge(6,4,0,0,3,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(5,0,0,8,3,0), new Edge(8,3,0,10,0,0), new Edge(10,0,0,5,0,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(10,6,0,8,3,0), new Edge(8,3,0,10,0,0), new Edge(10,0,0,10,6,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(10,6,0,8,3,0), new Edge(8,3,0,6,4,0), new Edge(6,4,0,10,6,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(10,6,0,4,5,0), new Edge(4,5,0,6,4,0), new Edge(6,4,0,10,6,0))));
		assertTrue(triangles.size()==8);
	}

	/**
	 * Performs a test with many constraints and input points.
	 * The input constraints does not intersect here.
	 */
	public void testManyConstraints() throws DelaunayError{
		ConstrainedMesh mesh = new ConstrainedMesh();
		Edge constr = new Edge(0,3,0,8,3,0);
		mesh.addConstraintEdge(constr);
		constr = new Edge(9,0,0,9,6,0);
		mesh.addConstraintEdge(constr);
		constr = new Edge(12,6,0,8,7,0);
		mesh.addConstraintEdge(constr);
		constr = new Edge(5,4,0,8,7,0);
		mesh.addConstraintEdge(constr);
		constr = new Edge(12,6,0,12,7,0);
		mesh.addConstraintEdge(constr);
		constr = new Edge(8,3,0,9,6,0);
		mesh.addConstraintEdge(constr);
		constr = new Edge(8,7,0,12,12,0);
		mesh.addConstraintEdge(constr);
		mesh.addPoint(new Point(4,5,0));
		mesh.addPoint(new Point(4,1,0));
		mesh.addPoint(new Point(10,3,0));
		mesh.addPoint(new Point(11,9,0));
		mesh.processDelaunay();
		List<DelaunayTriangle> triangles = mesh.getTriangleList();
//                show(mesh);
		assertTrue(triangles.contains(new DelaunayTriangle(
			new Edge(0,3,0,8,3,0),
			new Edge(0,3,0,5,4,0),
			new Edge(5,4,0,8,3,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(
			new Edge(0,3,0,8,3,0),
			new Edge(8,3,0,4,1,0),
			new Edge(4,1,0,0,3,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(
			new Edge(0,3,0,5,4,0),
			new Edge(5,4,0,4,5,0),
			new Edge(4,5,0,0,3,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(
			new Edge(0,3,0,12,12,0),
			new Edge(12,12,0,4,5,0),
			new Edge(4,5,0,0,3,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(
			new Edge(8,7,0,12,12,0),
			new Edge(12,12,0,4,5,0),
			new Edge(4,5,0,8,7,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(
			new Edge(8,7,0,5,4,0),
			new Edge(5,4,0,4,5,0),
			new Edge(4,5,0,8,7,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(
			new Edge(8,7,0,5,4,0),
			new Edge(5,4,0,8,3,0),
			new Edge(8,3,0,8,7,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(
			new Edge(8,7,0,9,6,0),
			new Edge(9,6,0,8,3,0),
			new Edge(8,3,0,8,7,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(
			new Edge(9,0,0,9,6,0),
			new Edge(9,6,0,8,3,0),
			new Edge(8,3,0,9,0,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(
			new Edge(9,0,0,9,6,0),
			new Edge(9,6,0,10,3,0),
			new Edge(10,3,0,9,0,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(
			new Edge(9,0,0,12,6,0),
			new Edge(12,6,0,10,3,0),
			new Edge(10,3,0,9,0,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(
			new Edge(9,6,0,12,6,0),
			new Edge(12,6,0,10,3,0),
			new Edge(10,3,0,9,6,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(
			new Edge(9,6,0,12,6,0),
			new Edge(12,6,0,8,7,0),
			new Edge(8,7,0,9,6,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(
			new Edge(12,7,0,12,6,0),
			new Edge(12,6,0,8,7,0),
			new Edge(8,7,0,12,7,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(
			new Edge(12,7,0,11,9,0),
			new Edge(11,9,0,12,12,0),
			new Edge(12,12,0,12,7,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(
			new Edge(12,12,0,11,9,0),
			new Edge(11,9,0,8,7,0),
			new Edge(8,7,0,12,12,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(
			new Edge(9,0,0,8,3,0),
			new Edge(8,3,0,4,1,0),
			new Edge(4,1,0,9,0,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(
			new Edge(11,9,0,8,7,0),
			new Edge(12,7,0,11,9,0),
			new Edge(8,7,0,12,7,0))));

	}

	/**
	 * Perform constrained triangulation on a set of edges which is designed
	 * to cause the use of the remove ghost algorithm in ConstrainedMesh.
	 * Check that unnecessary edges and triangles are well removed.
	 */
	public void testRemoveGhost() throws DelaunayError{
		ConstrainedMesh mesh = new ConstrainedMesh();
		Edge constr = new Edge(1,1,0,5,1,0);
		mesh.addConstraintEdge(constr);
		mesh.addPoint(new Point (3,0,0));
		mesh.addPoint(new Point (3,2,0));
		mesh.addPoint(new Point (3,4,0));
		mesh.addPoint(new Point (3,6,0));
		mesh.addPoint(new Point (3,8,0));
		mesh.addPoint(new Point (3,10,0));
		mesh.addPoint(new Point (3,12,0));
		mesh.addPoint(new Point (3,14,0));
		mesh.addPoint(new Point (3,16,0));
		mesh.processDelaunay();
//		show(mesh);
		assertFalse(mesh.getPoints().contains(new Point(0,0,0)));
		List<DelaunayTriangle> triangles = mesh.getTriangleList();
		for(DelaunayTriangle tri : triangles){
			assertFalse(tri.contains(new Point(0,0,0)));
		}
		for(Edge ed : mesh.getEdges()){
			assertFalse(ed.contains(new Point(0,0,0)));
		}
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(1,1,0,3,2,0), new Edge(3,2,0,3,4,0), new Edge(3,4,0,1,1,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(1,1,0,3,6,0), new Edge(3,6,0,3,4,0), new Edge(3,4,0,1,1,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(1,1,0,3,8,0), new Edge(3,8,0,3,6,0), new Edge(3,6,0,1,1,0))));
	}

	/**
	 * Checks that the objects (points and edges, to be accurate) are not duplicated
	 * in the mesh.
	 * @throws DelaunayError
	 */
	public void testObjectsUnicity() throws DelaunayError{
		ConstrainedMesh mesh = new ConstrainedMesh();
		Edge constr = new Edge(0,3,0,8,3,0);
		mesh.addConstraintEdge(constr);
		constr = new Edge(9,0,0,9,6,0);
		mesh.addConstraintEdge(constr);
		constr = new Edge(12,6,0,8,7,0);
		mesh.addConstraintEdge(constr);
		constr = new Edge(5,4,0,8,7,0);
		mesh.addConstraintEdge(constr);
		constr = new Edge(12,6,0,12,7,0);
		mesh.addConstraintEdge(constr);
		constr = new Edge(8,3,0,9,6,0);
		mesh.addConstraintEdge(constr);
		constr = new Edge(8,7,0,12,12,0);
		mesh.addConstraintEdge(constr);
		mesh.addPoint(new Point(4,5,0));
		mesh.addPoint(new Point(4,1,0));
		mesh.addPoint(new Point(10,3,0));
		mesh.addPoint(new Point(11,9,0));
		mesh.processDelaunay();
		List<DelaunayTriangle> triangles = mesh.getTriangleList();
		int index;
		Edge comp;
		List<Edge> edges = mesh.getEdges();
		List<Point> points = mesh.getPoints();
		Point pt;
		for(DelaunayTriangle tri : triangles){
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
		Edge constr1 = new Edge(0,2,0,5,6,0);
		Edge constr2 = new Edge(0,2,0,4,0,0);
		mesh.addConstraintEdge(constr2);
		mesh.addConstraintEdge(constr1);
		mesh.addPoint(new Point(2,3,0));
		mesh.addPoint(new Point(3,6,0));
		mesh.processDelaunay();
//		show(mesh);
		List<DelaunayTriangle> triangles=mesh.getTriangleList();
		assertTrue(triangles.contains(new DelaunayTriangle(constr1,new Edge(0,2,0,3,6,0),new Edge(3,6,0,5,6,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(constr1,new Edge(0,2,0,2,3,0),new Edge(2,3,0,5,6,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(constr2,new Edge(0,2,0,2,3,0),new Edge(2,3,0,4,0,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(2,3,0,5,6,0),new Edge(5,6,0,4,0,0),new Edge(2,3,0,4,0,0))));
		assertTrue(triangles.size()==4);
	}

	/**
	 * Tests that we can manage the case when two points give two degenerated
	 * edges during the processing.
	 */
	public void testTwoColinearDegeneratedEdges() throws DelaunayError{
		ConstrainedMesh mesh = new ConstrainedMesh();
		Edge constr1 = new Edge(2,2,0,7,4,0);
		Edge constr2 = new Edge (2,2,0,7,0,0);
		mesh.addConstraintEdge(constr2);
		mesh.addConstraintEdge(constr1);
		mesh.addPoint(new Point(1,1,0));
		mesh.addPoint(new Point(1,3,0));
		mesh.addPoint(new Point(4,2,0));
		mesh.addPoint(new Point(6,2,0));
		mesh.processDelaunay();
//		show(mesh);
		List<DelaunayTriangle> triangles = mesh.getTriangleList();
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(1,1,0,1,3,0), new Edge(2,2,0,1,3,0), new Edge(1,1,0,2,2,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(constr1, new Edge(2,2,0,1,3,0), new Edge(1,3,0,7,4,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(constr1, new Edge(2,2,0,4,2,0), new Edge(4,2,0,7,4,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(6,2,0,7,4,0), new Edge(7,4,0,7,0,0), new Edge(7,0,0,6,2,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(4,2,0,6,2,0), new Edge(6,2,0,7,4,0), new Edge(7,4,0,4,2,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(4,2,0,6,2,0), new Edge(6,2,0,7,0,0), new Edge(7,0,0,4,2,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(constr2, new Edge(2,2,0,4,2,0), new Edge(4,2,0,7,0,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(1,1,0,7,0,0), constr2, new Edge(1,1,0,2,2,0))));
		assertTrue(triangles.size()==8);
		List<Edge> edges = mesh.getEdges();
		assertTrue(edges.size()==14);

	}


	/**
	 * Performs a delaunay triangulation with an input without constraints.
	 */
	public void testSwapEdges() throws DelaunayError{
		ConstrainedMesh mesh = new ConstrainedMesh();
		Point p1=new Point(0,4,0);
		Point p2=new Point(2,8,0);
		Point p3=new Point(2,0,0);
		Point p4=new Point(4,4,0);
		mesh.addPoint(p1);
		mesh.addPoint(p2);
		mesh.addPoint(p3);
		mesh.addPoint(p4);
		mesh.processDelaunay();
		DelaunayTriangle tri1 = new DelaunayTriangle(new Edge(p1, p2), new Edge(p2, p4), new Edge(p4, p1));
		DelaunayTriangle tri2 = new DelaunayTriangle(new Edge(p1, p3), new Edge(p3, p4), new Edge(p4, p1));
		assertTrue(mesh.getTriangleList().contains(tri1));
		assertTrue(mesh.getTriangleList().contains(tri2));

	}


	/**
	 * Performs a delaunay triangulation with an input without constraints.
	 */
	public void testSwapEdgesBis() throws DelaunayError{
		ConstrainedMesh mesh = new ConstrainedMesh();
		Point p1=new Point(0,1,0);
		Point p3=new Point(4,0,0);
		Point p4=new Point(4,1,0);
		Point p2=new Point(3,3,0);
		mesh.addPoint(p1);
		mesh.addPoint(p2);
		mesh.addPoint(p3);
		mesh.addPoint(p4);
		mesh.processDelaunay();
//		show(mesh);
		DelaunayTriangle tri1 = new DelaunayTriangle(new Edge(p1, p2), new Edge(p2, p4), new Edge(p4, p1));
		DelaunayTriangle tri2 = new DelaunayTriangle(new Edge(p1, p3), new Edge(p3, p4), new Edge(p4, p1));
		assertTrue(mesh.getTriangleList().contains(tri1));
		assertTrue(mesh.getTriangleList().contains(tri2));
	}

	/**
	 * Make a classical triangulation on a random set of points. For benchmark purposes.
	 * @throws DelaunayError
	 */
	public void testDelaunayTriangulationRandomPoints() throws DelaunayError{
		List<Point> randomPoint = BaseUtility.getRandomPoints(1000);
		ConstrainedMesh mesh = new ConstrainedMesh();
		for(Point pt : randomPoint){
			mesh.addPoint(pt);
		}
		mesh.addConstraintEdge(new Edge(5,5,0,10,10,0));
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
		mesh.addConstraintEdge(new Edge(0,0,0,2,2,0));
		mesh.addConstraintEdge(new Edge(0,4,0,2,2,0));
		mesh.addConstraintEdge(new Edge(4,0,0,2,2,0));
		mesh.addConstraintEdge(new Edge(4,4,0,2,2,0));
		mesh.processDelaunay();
//		show(mesh);
		List<DelaunayTriangle> triangles = mesh.getTriangleList();
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0,0,0,2,2,0), new Edge(0,4,0,2,2,0), new Edge(0,0,0,0,4,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0,4,0,2,2,0), new Edge(4,4,0,2,2,0), new Edge(4,4,0,0,4,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0,0,0,2,2,0), new Edge(4,0,0,2,2,0), new Edge(0,0,0,4,0,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(4,4,0,2,2,0), new Edge(4,0,0,2,2,0), new Edge(4,4,0,4,0,0))));
	}

	/**
	 * Process a triangulation where the three first input points have the same
	 * x-coordinate, and are linked to constraints.
	 */
	public void test3HorizontalPoints() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new Edge(0,3,0,3,1,0));
		mesh.addConstraintEdge(new Edge(3,1,0,8,0,0));
		mesh.addConstraintEdge(new Edge(0,6,0,3,7,0));
		mesh.addConstraintEdge(new Edge(3,7,0,5,5,0));
		mesh.addConstraintEdge(new Edge(0,10,0,4,9,0));
		mesh.addConstraintEdge(new Edge(4,9,0,9,9,0));
		mesh.processDelaunay();
//		show(mesh);
		List<DelaunayTriangle> triangles = mesh.getTriangleList();
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0,3,0,3,1,0),new Edge(3,1,0,5,5,0),new Edge(5,5,0,0,3,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(5,5,0,0,3,0),new Edge(0,3,0,3,7,0),new Edge(3,7,0,5,5,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0,3,0,3,7,0),new Edge(3,7,0,0,6,0),new Edge(0,6,0,0,3,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(3,7,0,0,6,0),new Edge(0,6,0,0,10,0),new Edge(0,10,0,3,7,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0,10,0,3,7,0),new Edge(3,7,0,4,9,0),new Edge(4,9,0,0,10,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(4,9,0,0,10,0),new Edge(0,10,0,9,9,0),new Edge(9,9,0,4,9,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(9,9,0,4,9,0),new Edge(4,9,0,5,5,0),new Edge(5,5,0,9,9,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(4,9,0,5,5,0),new Edge(3,7,0,5,5,0),new Edge(3,7,0,4,9,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(5,5,0,9,9,0),new Edge(9,9,0,8,0,0),new Edge(8,0,0,5,5,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(8,0,0,5,5,0),new Edge(5,5,0,3,1,0),new Edge(3,1,0,8,0,0))));

	}

	public void test4verticalPoint() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addPoint(new Point(0,1,0));
		mesh.addPoint(new Point(0,4,0));
		mesh.addPoint(new Point(0,9,0));
		mesh.addPoint(new Point(0,12,0));
		mesh.addPoint(new Point(2,1,0));
		mesh.processDelaunay();
//		show(mesh);
		List<DelaunayTriangle> triangles = mesh.getTriangleList();
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0,1,0,2,1,0),new Edge(2,1,0,0,4,0),new Edge(0,4,0,0,1,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0,9,0,2,1,0),new Edge(2,1,0,0,4,0),new Edge(0,4,0,0,9,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0,9,0,2,1,0),new Edge(2,1,0,0,12,0),new Edge(0,12,0,0,9,0))));
	}
	/**
	 * Test case where we have three constraints 
	 * @throws DelaunayError
	 */
	public void test3VerticalConstraints() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new Edge(3,0,0,3,3,0));
		mesh.addConstraintEdge(new Edge(3,5,0,3,8,0));
		mesh.addConstraintEdge(new Edge(3,10,0,3,13,0));
		mesh.addPoint(new Point(0,1,0));
		mesh.addPoint(new Point(0,4,0));
		mesh.addPoint(new Point(0,9,0));
		mesh.addPoint(new Point(0,12,0));
		mesh.addPoint(new Point(6,6,0));
		mesh.addPoint(new Point(6,7,0));
//		show(mesh);
		mesh.processDelaunay();
//		show(mesh);
		List<DelaunayTriangle> triangles = mesh.getTriangleList();
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0,1,0,3,0,0),new Edge(3,0,0,3,3,0),new Edge(3,3,0,0,1,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0,1,0,0,4,0),new Edge(0,4,0,3,3,0),new Edge(3,3,0,0,1,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(3,5,0,0,4,0),new Edge(0,4,0,3,3,0),new Edge(3,3,0,3,5,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(3,5,0,0,4,0),new Edge(0,4,0,0,9,0),new Edge(0,9,0,3,5,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(3,5,0,3,8,0),new Edge(3,8,0,0,9,0),new Edge(0,9,0,3,5,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(3,10,0,3,8,0),new Edge(3,8,0,0,9,0),new Edge(0,9,0,3,10,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(3,10,0,0,12,0),new Edge(0,12,0,0,9,0),new Edge(0,9,0,3,10,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(3,10,0,0,12,0),new Edge(0,12,0,3,13,0),new Edge(3,13,0,3,10,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(3,10,0,6,7,0),new Edge(6,7,0,3,13,0),new Edge(3,13,0,3,10,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(3,10,0,6,7,0),new Edge(6,7,0,3,8,0),new Edge(3,8,0,3,10,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(6,6,0,6,7,0),new Edge(6,7,0,3,8,0),new Edge(3,8,0,6,6,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(6,6,0,3,5,0),new Edge(3,5,0,3,8,0),new Edge(3,8,0,6,6,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(6,6,0,3,5,0),new Edge(3,5,0,3,3,0),new Edge(3,3,0,6,6,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(6,6,0,3,0,0),new Edge(3,0,0,3,3,0),new Edge(3,3,0,6,6,0))));
	}

	/**
	 * A test case where the first triangle can't be built, and were the 2 first built edges
	 * will be p1p2 and p2p3
	 * @throws DelaunayError
	 */
	public void testTwoNeighbourConstraints() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new Edge(0,2,0,2,4,0));
		mesh.addConstraintEdge(new Edge(0,10,0,3,8,0));
		mesh.addConstraintEdge(new Edge(0,10,0,1,13,0));
		mesh.addConstraintEdge(new Edge(3,14,0,1,13,0));
		mesh.processDelaunay();
//		show(mesh);
		List<DelaunayTriangle> triangles = mesh.getTriangleList();
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0,2,0,2,4,0), new Edge(2,4,0,0,10,0), new Edge(0,10,0,0,2,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(3,8,0,2,4,0), new Edge(2,4,0,0,10,0), new Edge(0,10,0,3,8,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(3,8,0,1,13,0), new Edge(1,13,0,0,10,0), new Edge(0,10,0,3,8,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(3,8,0,1,13,0), new Edge(1,13,0,3,14,0), new Edge(3,14,0,3,8,0))));
		assertTrue(triangles.size()==4);
	}

	/**
	 * An extension of the previous case.
	 * @throws DelaunayError
	 */
	public void testTwistedConstraint() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new Edge(0,10,0,3,8,0));
		mesh.addConstraintEdge(new Edge(0,10,0,1,13,0));
		mesh.addConstraintEdge(new Edge(3,14,0,1,13,0));
		mesh.addConstraintEdge(new Edge(3,14,0,2,15,0));
		mesh.addConstraintEdge(new Edge(6,10,0,3,8,0));
		mesh.processDelaunay();
//		show(mesh);
		List<DelaunayTriangle> triangles = mesh.getTriangleList();
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0,10,0,3,8,0), new Edge(3,8,0,1,13,0), new Edge(1,13,0,0,10,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(6,10,0,3,8,0), new Edge(3,8,0,1,13,0), new Edge(1,13,0,6,10,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(6,10,0,3,14,0), new Edge(3,14,0,1,13,0), new Edge(1,13,0,6,10,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(2,15,0,3,14,0), new Edge(3,14,0,1,13,0), new Edge(1,13,0,2,15,0))));


	}

	public void testCommonLeftAndRightPoint() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
//		mesh.addConstraintEdge(new Edge(0,3,0,6,3,0));
		mesh.addConstraintEdge(new Edge(0,3,0,10,6,0));
		mesh.addConstraintEdge(new Edge(8,1,0,6,3,0));
		mesh.addConstraintEdge(new Edge(8,5,0,6,3,0));
		mesh.addPoint(new Point(0,4,0));
		mesh.addPoint(new Point(6,0,0));
		mesh.processDelaunay();
//		show(mesh);
		List<DelaunayTriangle> triangles = mesh.getTriangleList();
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0,3,0,6,3,0), new Edge(6,3,0,6,0,0), new Edge(6,0,0,0,3,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(8,1,0,6,3,0), new Edge(6,3,0,6,0,0), new Edge(6,0,0,8,1,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(8,1,0,6,3,0), new Edge(6,3,0,8,5,0), new Edge(8,5,0,8,1,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(8,1,0,10,6,0), new Edge(10,6,0,8,5,0), new Edge(8,5,0,8,1,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0,3,0,10,6,0), new Edge(10,6,0,8,5,0), new Edge(8,5,0,0,3,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0,3,0,10,6,0), new Edge(10,6,0,0,4,0), new Edge(0,4,0,0,3,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0,3,0,6,3,0), new Edge(6,3,0,8,5,0), new Edge(8,5,0,0,3,0))));

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
		mesh.addConstraintEdge(new Edge (0, -5, 80.0, 4, -4.0, 80.0));
		mesh.addConstraintEdge(new Edge (4, -4.0, 80.0, 6, 4.0, 80.0));
		mesh.addConstraintEdge(new Edge (6, 4.0, 80.0, 12.0, 7, 80.0));
		mesh.addConstraintEdge(new Edge (12.0, 7, 80.0, 16.0, 6, 80.0));
		
		mesh.addConstraintEdge(new Edge (6.0, 21.0, 80.0, 7, 16.0, 80.0));
		mesh.addConstraintEdge(new Edge (6.0, 21.0, 80.0, 10, 26.0, 80.0));
		mesh.addConstraintEdge(new Edge (7, 16.0, 80.0, 11.0, 14, 80.0));
		mesh.processDelaunay();
//		show(mesh);
		List<DelaunayTriangle> triangles = mesh.getTriangleList();
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0, -5, 80.0, 4, -4.0, 80.0), new Edge(4, -4.0, 80.0, 6, 4.0, 80.0), new Edge(6, 4.0, 80.0, 0, -5, 80.0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0, -5, 80.0, 7, 16.0, 80.0), new Edge(7, 16.0, 80.0, 6, 4.0, 80.0), new Edge(6, 4.0, 80.0, 0, -5, 80.0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0, -5, 80.0, 7, 16.0, 80.0), new Edge(7, 16.0, 80.0, 6.0, 21.0, 80.0), new Edge(6.0, 21.0, 80.0, 0, -5, 80.0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(11.0, 14, 80.0, 7, 16.0, 80.0), new Edge(7, 16.0, 80.0, 6.0, 21.0, 80.0), new Edge(6.0, 21.0, 80.0, 11.0, 14, 80.0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(11.0, 14, 80.0, 10, 26.0, 80.0), new Edge(10, 26.0, 80.0, 6.0, 21.0, 80.0), new Edge(6.0, 21.0, 80.0, 11.0, 14, 80.0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(11.0, 14, 80.0, 10, 26.0, 80.0), new Edge(10, 26.0, 80.0, 16.0, 6, 80.0), new Edge(16.0, 6, 80.0, 11.0, 14, 80.0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(11.0, 14, 80.0, 12.0, 7, 80.0), new Edge(12.0, 7, 80.0, 16.0, 6, 80.0), new Edge(16.0, 6, 80.0, 11.0, 14, 80.0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(6, 4.0, 80.0, 12.0, 7, 80.0), new Edge(12.0, 7, 80.0, 16.0, 6, 80.0), new Edge(16.0, 6, 80.0, 6, 4.0, 80.0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(6, 4.0, 80.0, 4, -4.0, 80.0), new Edge(4, -4.0, 80.0, 16.0, 6, 80.0), new Edge(16.0, 6, 80.0, 6, 4.0, 80.0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(6, 4.0, 80.0, 12.0, 7, 80.0), new Edge(12.0, 7, 80.0, 11.0, 14, 80.0), new Edge(11.0, 14, 80.0, 6, 4.0, 80.0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(6, 4.0, 80.0, 7, 16.0, 80.0), new Edge(7, 16.0, 80.0, 11.0, 14, 80.0), new Edge(11.0, 14, 80.0, 6, 4.0, 80.0))));

	}

	/**
	 * A second test whose configuration comes from the chezine data.
	 * It tests that the triangulation is well performed when the first three points are colinear.
	 */
	public void testFromChezineBis() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new Edge (0, 0.6, 10, 13, -4, 10));

		mesh.addConstraintEdge(new Edge (0, 63, 10.0, 17, 42, 10.0));

		mesh.addConstraintEdge(new Edge (0, 77, 10.0, 3.0, 92.0, 10.0));
		mesh.addConstraintEdge(new Edge (0, 118, 10.0, 13.0, 125, 10.0));
		mesh.addConstraintEdge(new Edge (3.0, 92.0, 10.0, 13.0, 99, 10.0));
		mesh.processDelaunay();
//		show(mesh);
		List<DelaunayTriangle> triangles = mesh.getTriangleList();
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0, 0.6, 10, 13, -4, 10), new Edge(13, -4, 10,17, 42, 10.0), new Edge(17, 42, 10.0,0, 0.6, 10))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0, 0.6, 10, 0, 63, 10.0), new Edge(0, 63, 10.0 ,17, 42, 10.0), new Edge(17, 42, 10.0,0, 0.6, 10))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0, 77, 10.0, 0, 63, 10.0), new Edge(0, 63, 10.0 ,17, 42, 10.0), new Edge(17, 42, 10.0, 0, 77, 10.0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0, 77, 10.0, 13.0, 99, 10.0), new Edge(13.0, 99, 10.0 ,17, 42, 10.0), new Edge(17, 42, 10.0, 0, 77, 10.0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0, 77, 10.0, 13.0, 99, 10.0), new Edge(13.0, 99, 10.0 ,3.0, 92.0, 10.0), new Edge(3.0, 92.0, 10.0, 0, 77, 10.0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0, 77, 10.0, 0, 118, 10.0), new Edge(0, 118, 10.0,3.0, 92.0, 10.0), new Edge(3.0, 92.0, 10.0, 0, 77, 10.0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(13.0, 99, 10.0, 0, 118, 10.0), new Edge(0, 118, 10.0,3.0, 92.0, 10.0), new Edge(3.0, 92.0, 10.0, 13.0, 99, 10.0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(13.0, 99, 10.0, 0, 118, 10.0), new Edge(0, 118, 10.0,13.0, 125, 10.0), new Edge(13.0, 125, 10.0, 13.0, 99, 10.0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(13.0, 99, 10.0, 17, 42, 10.0), new Edge(17, 42, 10.0,13.0, 125, 10.0), new Edge(13.0, 125, 10.0, 13.0, 99, 10.0))));

	}

	/**
	 * A test inherited from the former delaunay implementation.
	 * @throws DelaunayError
	 */
	public void testProcessStar() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addPoint(new Point(0, 0, 0));
		mesh.addPoint(new Point(10, 0, 0));
		mesh.addPoint(new Point(0, 10, 0));
		mesh.addPoint(new Point(10, 10, 0));
		mesh.addConstraintEdge(new Edge(1, 5, 2, 4, 6, 2));
		mesh.addConstraintEdge(new Edge(4, 6, 2, 5, 9, 2));
		mesh.addConstraintEdge(new Edge(5, 9, 2, 6, 6, 2));
		mesh.addConstraintEdge(new Edge(6, 6, 2, 9, 5, 2));
		mesh.addConstraintEdge(new Edge(9, 5, 2, 6, 4, 2));
		mesh.addConstraintEdge(new Edge(6, 4, 2, 5, 1, 2));
		mesh.addConstraintEdge(new Edge(5, 1, 2, 4, 4, 2));
		mesh.addConstraintEdge(new Edge(4, 4, 2, 1, 5, 2));
		mesh.processDelaunay();
//		show(mesh);
		List<DelaunayTriangle> triangles = mesh.getTriangleList();
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0,0,0,5,1,2), new Edge(5,1,2,10,0,0), new Edge(10,0,0,0,0,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0,0,0,5,1,2), new Edge(5,1,2,4,4,2), new Edge(4,4,2,0,0,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0,0,0,1,5,2), new Edge(1,5,2,4,4,2), new Edge(4,4,2,0,0,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(0,0,0,1,5,2), new Edge(1,5,2,0,10,0), new Edge(0,10,0,0,0,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(4,6,2,1,5,2), new Edge(1,5,2,0,10,0), new Edge(0,10,0,4,6,2))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(4,6,2,5,9,2), new Edge(5,9,2,0,10,0), new Edge(0,10,0,4,6,2))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(10,10,0,5,9,2), new Edge(5,9,2,0,10,0), new Edge(0,10,0,10,10,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(10,10,0,5,9,2), new Edge(5,9,2,6,6,2), new Edge(6,6,2,10,10,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(10,10,0,9,5,2), new Edge(9,5,2,6,6,2), new Edge(6,6,2,10,10,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(6,4,2,9,5,2), new Edge(9,5,2,10,0,0), new Edge(10,0,0,6,4,2))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(10,10,0,9,5,2), new Edge(9,5,2,10,0,0), new Edge(10,0,0,10,10,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(6,4,2,5,1,2), new Edge(5,1,2,10,0,0), new Edge(10,0,0,6,4,2))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(6,4,2,5,1,2), new Edge(5,1,2,4,4,2), new Edge(4,4,2,6,4,2))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(6,4,2,4,6,2), new Edge(4,6,2,4,4,2), new Edge(4,4,2,6,4,2))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(1,5,2,4,6,2), new Edge(4,6,2,4,4,2), new Edge(4,4,2,1,5,2))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(6,4,2,4,6,2), new Edge(4,6,2,6,6,2), new Edge(6,6,2,6,4,2))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(5,9,2,4,6,2), new Edge(4,6,2,6,6,2), new Edge(6,6,2,5,9,2))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(6,4,2,9,5,2), new Edge(9,5,2,6,6,2), new Edge(6,6,2,6,4,2))));
	}

	public void testBoundaryIntegrity() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addPoint(new Point(1,6,0));
		mesh.addPoint(new Point(2,3,0));
		mesh.addPoint(new Point(4,7,0));
		mesh.addPoint(new Point(6,1,0));
		mesh.addPoint(new Point(7,5,0));
		mesh.addPoint(new Point(7,9,0));
		mesh.addPoint(new Point(9,11,0));
		mesh.addPoint(new Point(10,2,0));
		mesh.addPoint(new Point(12,10,0));
		mesh.addPoint(new Point(13,4,0));
		mesh.addPoint(new Point(13,7,0));
		mesh.processDelaunay();
//		show(mesh);
		List<DelaunayTriangle> triangles = mesh.getTriangleList();
		assertTrue(triangles.size()==12);
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(1,6,0,2,3,0), new Edge(2,3,0,4,7,0),new Edge(4,7,0,1,6,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(1,6,0,7,9,0), new Edge(7,9,0,4,7,0),new Edge(4,7,0,1,6,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(1,6,0,7,9,0), new Edge(7,9,0,9,11,0),new Edge(9,11,0,1,6,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(12,10,0,7,9,0), new Edge(7,9,0,9,11,0),new Edge(9,11,0,12,10,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(12,10,0,7,9,0), new Edge(7,9,0,13,7,0),new Edge(13,7,0,12,10,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(7,5,0,7,9,0), new Edge(7,9,0,13,7,0),new Edge(13,7,0,7,5,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(7,5,0,13,4,0), new Edge(13,4,0,13,7,0),new Edge(13,7,0,7,5,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(7,5,0,13,4,0), new Edge(13,4,0,10,2,0),new Edge(10,2,0,7,5,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(7,5,0,6,1,0), new Edge(6,1,0,10,2,0),new Edge(10,2,0,7,5,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(7,5,0,4,7,0), new Edge(4,7,0,2,3,0),new Edge(2,3,0,7,5,0))));
		assertTrue(triangles.contains(new DelaunayTriangle(new Edge(7,5,0,4,7,0), new Edge(4,7,0,7,9,0),new Edge(7,9,0,7,5,0))));
	}

	public void testLongConstraintLine()throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new Edge (	44, 13, 40,
							44, 37, 40));
		mesh.addConstraintEdge(new Edge (	44, 13, 40,
							54, 0 , 40));
		mesh.addConstraintEdge(new Edge (	44, 37, 40,
							64, 36, 40));
		mesh.addConstraintEdge(new Edge	(	62, 60, 40,
							66, 40, 40));
		mesh.addConstraintEdge(new Edge (	64, 36, 40,
							66, 40, 40));
		mesh.addConstraintEdge(new Edge (	85, 40, 40,
							88, 20, 40));
		mesh.addConstraintEdge(new Edge (	0 , 60, 50,
							5 , 40, 50));
		mesh.addConstraintEdge(new Edge (	5 , 40, 50,
							24, 11, 50));
		mesh.processDelaunay();
//		show(mesh);
		List<DelaunayTriangle> tri = mesh.getTriangleList();
		assertTrue(true);

	}

	public void testChezineStress() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new Edge (	0, 15, 10,
							5, 13, 10));
		mesh.addConstraintEdge(new Edge (	4, 0 , 10,
							5, 0 , 10));
		mesh.addConstraintEdge(new Edge (	4, 0 , 10,
							10, 3, 10));
		mesh.addConstraintEdge(new Edge (	5, 13, 10,
							8, 10, 10));
		mesh.addConstraintEdge(new Edge (	8, 10, 10,
							10, 6, 10));
		mesh.addConstraintEdge(new Edge (	10, 3, 10,
							12, 0, 10));
		mesh.processDelaunay();
//		show(mesh);
		List<DelaunayTriangle> tri = mesh.getTriangleList();
		assertTrue(tri.size()==7);
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(4,0,10,5,13,10), new Edge(5,13,10,0,15,10), new Edge(0,15,10,4,0,10))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(4,0,10,5,13,10), new Edge(5,13,10,8,10,10), new Edge(8,10,10,4,0,10))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(4,0,10,10,6,10), new Edge(10,6,10,8,10,10), new Edge(8,10,10,4,0,10))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(4,0,10,10,6,10), new Edge(10,6,10,10,3,10), new Edge(10,3,10,4,0,10))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(4,0,10,5,0,10), new Edge(5,0,10,10,3,10), new Edge(10,3,10,4,0,10))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(12,0,10,5,0,10), new Edge(5,0,10,10,3,10), new Edge(10,3,10,12,0,10))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(12,0,10,10,6,10), new Edge(10,6,10,10,3,10), new Edge(10,3,10,12,0,10))));
		
	}

	/**
	 * A test whose configuration is deducted from the chezine data.
	 * @throws DelaunayError
	 */
	public void testChezineStressBis() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new Edge (	0, 6, 10,
							5, 12, 10));
		mesh.addConstraintEdge(new Edge (	2, 0 , 10,
							5, 0, 10));
		mesh.addConstraintEdge(new Edge (	3, 5, 10,
							5, 4, 10));
		mesh.addConstraintEdge(new Edge (	3, 5, 10,
							5, 7, 10));
		mesh.addConstraintEdge(new Edge (	5, 0 , 10,
							7, 0 , 10));
		mesh.addConstraintEdge(new Edge (	5, 4, 10,
							7, 0, 10));
		mesh.addConstraintEdge(new Edge (	5, 7, 10,
							6, 10, 10));
		mesh.addConstraintEdge(new Edge (	5, 12, 10,
							6, 10, 10));
		mesh.processDelaunay();
//		show(mesh);
		List<DelaunayTriangle> tri = mesh.getTriangleList();
		assertTrue(tri.size()==10);
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(0,6,10,2,0,10), new Edge(2,0,10,3,5,10), new Edge(3,5,10,0,6,10))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(0,6,10,5,7,10), new Edge(5,7,10,3,5,10), new Edge(3,5,10,0,6,10))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(0,6,10,5,7,10), new Edge(5,7,10,5,12,10), new Edge(5,12,10,0,6,10))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(6,10,10,5,7,10), new Edge(5,7,10,5,12,10), new Edge(5,12,10,6,10,10))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(6,10,10,5,7,10), new Edge(5,7,10,7,0,10), new Edge(7,0,10,6,10,10))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(5,4,10,5,7,10), new Edge(5,7,10,7,0,10), new Edge(7,0,10,5,4,10))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(5,4,10,5,0,10), new Edge(5,0,10,7,0,10), new Edge(7,0,10,5,4,10))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(5,4,10,5,0,10), new Edge(5,0,10,2,0,10), new Edge(2,0,10,5,4,10))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(5,4,10,5,7,10), new Edge(5,7,10,3,5,10), new Edge(3,5,10,5,4,10))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(2,0,10,5,4,10), new Edge(5,4,10,3,5,10), new Edge(3,5,10,2,0,10))));
	}

	/**
	 * A triangle was forgotten due to a bug when adding a constraint in the mesh.
	 * @throws DelaunayError
	 */
	public void testForgottenTriangle() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new Edge (	0  , 4, 60.0,
							5  , 7, 60.0));
		mesh.addConstraintEdge(new Edge (	4  , 10, 60.0,
							5  , 9, 60.0));
		mesh.addConstraintEdge(new Edge (	5  , 7, 60.0,
							10 , 7, 60.0));
		mesh.addConstraintEdge(new Edge (	5  , 10, 60.0,
							10 , 8, 60.0));
		mesh.addConstraintEdge(new Edge (	12 , 0  , 60.0,
							15 , 2 , 60.0));
		mesh.processDelaunay();
//		show(mesh);
		List<DelaunayTriangle> tri = mesh.getTriangleList();
		assertTrue(tri.size()==10);
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(0,4,60,4,10,60), new Edge(4,10,60,5,7,60), new Edge(5,7,60,0,4,60))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(5,9,60,4,10,60), new Edge(4,10,60,5,7,60), new Edge(5,7,60,5,9,60))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(5,9,60,10,8,60), new Edge(10,8,60,5,7,60), new Edge(5,7,60,5,9,60))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(10,7,60,10,8,60), new Edge(10,8,60,5,7,60), new Edge(5,7,60,10,7,60))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(5,9,60,4,10,60), new Edge(4,10,60,5,10,60), new Edge(5,10,60,5,9,60))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(5,9,60,10,8,60), new Edge(10,8,60,5,10,60), new Edge(5,10,60,5,9,60))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(10,7,60,10,8,60), new Edge(10,8,60,15,2,60), new Edge(15,2,60,10,7,60))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(10,7,60,12,0,60), new Edge(12,0,60,15,2,60), new Edge(15,2,60,10,7,60))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(10,7,60,12,0,60), new Edge(12,0,60,5,7,60), new Edge(5,7,60,10,7,60))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(0,4,60,12,0,60), new Edge(12,0,60,5,7,60), new Edge(5,7,60,0,4,60))));
		List<Edge> edges = mesh.getEdges();
		assertTrue(edges.size()==18);
		assertTrue(edges.contains(new Edge(0,4,60,4,10,60)));
		assertTrue(edges.contains(new Edge(0,4,60,12,0,60)));
		assertTrue(edges.contains(new Edge(0,4,60,5,7,60)));
		assertTrue(edges.contains(new Edge(4,10,60,5,7,60)));
		assertTrue(edges.contains(new Edge(12,0,60,5,7,60)));
		assertTrue(edges.contains(new Edge(4,10,60,5,9,60)));
		assertTrue(edges.contains(new Edge(5,9,60,5,7,60)));
		assertTrue(edges.contains(new Edge(10,8,60,5,7,60)));
		assertTrue(edges.contains(new Edge(10,7,60,5,7,60)));
		assertTrue(edges.contains(new Edge(5,10,60,4,10,60)));
		assertTrue(edges.contains(new Edge(5,10,60,5,9,60)));
		assertTrue(edges.contains(new Edge(5,10,60,10,8,60)));
		assertTrue(edges.contains(new Edge(5,9,60,10,8,60)));
		assertTrue(edges.contains(new Edge(5,7,60,10,8,60)));
		assertTrue(edges.contains(new Edge(10,7,60,10,8,60)));
		assertTrue(edges.contains(new Edge(10,7,60,15,2,60)));
		assertTrue(edges.contains(new Edge(10,7,60,12,0,60)));
		assertTrue(edges.contains(new Edge(15,2,60,12,0,60)));
	}

	/**
	 * This test caused problem, the generation of the start boundary did not work.
	 * @throws DelaunayError
	 */
	public void testBuildStartBound() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new Edge (	0  , 5, 0,
							1, 3, 0));
		mesh.addConstraintEdge(new Edge (	0  , 5, 0,
							7, 8, 0));
		mesh.addConstraintEdge(new Edge (	1, 3, 0,
							6  , 1, 0));
		mesh.addConstraintEdge(new Edge (	6  , 1, 0,
							8, 0 , 0));
		mesh.addConstraintEdge(new Edge (	7, 8, 0,
							12 , 10, 0));
		mesh.processDelaunay();
//		show(mesh);
		List<DelaunayTriangle> tri = mesh.getTriangleList();
		assertTrue(tri.size()==5);
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(7,8,0,0,5,0), new Edge(0,5,0,1,3,0), new Edge(1,3,0,7,8,0))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(7,8,0,6,1,0), new Edge(6,1,0,1,3,0), new Edge(1,3,0,7,8,0))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(7,8,0,6,1,0), new Edge(6,1,0,8,0,0), new Edge(8,0,0,7,8,0))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(7,8,0,12,10,0), new Edge(12,10,0,8,0,0), new Edge(8,0,0,7,8,0))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(1,3,0,6,1,0), new Edge(6,1,0,8,0,0), new Edge(8,0,0,1,3,0))));
		assertTrue(mesh.getEdges().size()==10);
	}

	/**
	 * A test with a vertical constraint that share its left point with another one.
	 * @throws DelaunayError
	 */
	public void testVerticalConstraintLinked() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new Edge (	6, 2, 0,
							6, 7, 0));
		mesh.addConstraintEdge(new Edge (	6, 2, 0,
							8, 0 , 0));
		mesh.addConstraintEdge(new Edge (	6, 7, 0,
							11, 6, 0));
		mesh.addConstraintEdge(new Edge (	0 , 12, 0,
							2 , 8, 0));
		mesh.processDelaunay();
//		show(mesh);
		List<DelaunayTriangle> tri = mesh.getTriangleList();
		assertTrue(tri.size()==5);
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(6,7,0,11,6,0), new Edge(11,6,0,0,12,0), new Edge(0,12,0,6,7,0))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(6,7,0,2,8,0), new Edge(2,8,0,0,12,0), new Edge(0,12,0,6,7,0))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(6,7,0,2,8,0), new Edge(2,8,0,6,2,0), new Edge(6,2,0,6,7,0))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(6,7,0,11,6,0), new Edge(11,6,0,6,2,0), new Edge(6,2,0,6,7,0))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(8,0,0,11,6,0), new Edge(11,6,0,6,2,0), new Edge(6,2,0,8,0,0))));
		assertTrue(mesh.getEdges().size()==10);
	}
}
