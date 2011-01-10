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
		Edge constr = new Edge(0,3,0,8,3,0);
		mesh.addConstraintEdge(constr);
		mesh.addPoint(new Point(4,5,0));
		mesh.addPoint(new Point(4,1,0));
		mesh.processDelaunay();
//		show(mesh);
		DelaunayTriangle tri1 = new DelaunayTriangle(constr, new Edge(0,3,0,4,5,0), new Edge(4,5,0,8,3,0));
		DelaunayTriangle tri2 = new DelaunayTriangle(constr, new Edge(0,3,0,4,1,0), new Edge(4,1,0,8,3,0));
		assertTrue(mesh.getTriangleList().contains(tri1));
		assertTrue(mesh.getTriangleList().contains(tri2));
		assertTrue(mesh.isMeshComputed());

		mesh = new ConstrainedMesh();
		constr = new Edge(3,0,0,3,6,0);
		mesh.addConstraintEdge(constr);
		mesh.addPoint(new Point(1,3,0));
		mesh.addPoint(new Point(5,1,0));
		mesh.processDelaunay();
		tri1 = new DelaunayTriangle(constr, new Edge(1,3,0,3,6,0), new Edge(1,3,0,3,0,0));
		tri2 = new DelaunayTriangle(constr, new Edge(3,6,0,5,1,0), new Edge(5,1,0,3,0,0));
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
			index = mesh.searchEdge(tri.getEdge(0));
			comp = edges.get(index) ;
			assertTrue(tri.getEdge(0)==comp);
			pt=points.get(points.indexOf(tri.getPoint(0)));
			assertTrue(tri.getPoint(0) == pt);
			index = mesh.searchEdge(tri.getEdge(1));
			comp = edges.get(index) ;
			assertTrue(tri.getEdge(1)==comp);
			pt=points.get(points.indexOf(tri.getPoint(1)));
			assertTrue(tri.getPoint(1) == pt);
			index = mesh.searchEdge(tri.getEdge(2));
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
		List<Point> randomPoint = BaseUtility.getRandomPoints(10000);
		ConstrainedMesh mesh = new ConstrainedMesh();
		for(Point pt : randomPoint){
			mesh.addPoint(pt);
		}
		double t = System.currentTimeMillis();
		mesh.processDelaunay();
		double t2 = System.currentTimeMillis();
		System.out.println("Needed time : "+(t2 - t));
		assertTrue(true);
	}

}
