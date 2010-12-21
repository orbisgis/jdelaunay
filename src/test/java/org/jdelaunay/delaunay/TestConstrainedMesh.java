/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdelaunay.delaunay;

import java.util.List;

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
	}

}
