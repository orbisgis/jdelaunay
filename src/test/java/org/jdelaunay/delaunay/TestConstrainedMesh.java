/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdelaunay.delaunay;

/**
 * This class chacks that the constrained triangulation is well performed.
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
		mesh.addPoint(new Point(4,5,0));
		mesh.addPoint(new Point(4,1,0));
		mesh.addPoint(new Point(10,3,0));
		mesh.addPoint(new Point(12,12,0));
		mesh.processDelaunay();
                show(mesh);
		assertTrue(true);

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

}
