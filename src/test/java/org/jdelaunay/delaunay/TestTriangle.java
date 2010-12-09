/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdelaunay.delaunay;

/**
 * This class checks the reliability of the methods written in DelaunayTriangle
 * @author alexis
 */
public class TestTriangle extends BaseUtility {

	/**
	 * Checks that computeArea works well. The test is basic, but important.
	 */
	public void testComputeArea() throws DelaunayError{
		Point p1 = new Point(0,0,0);
		Point p2 = new Point(4,0,0);
		Point p3 = new Point(3,2,0);
		Edge e1 = new Edge(p1,p2);
		Edge e2 = new Edge(p2,p3);
		Edge e3 = new Edge(p3,p1);
		DelaunayTriangle t = new DelaunayTriangle(e1,e2,e3);
		double d = t.computeArea();
		assertTrue(d==4);
	}

	/**
	 * Checks that the constructor of DelaunayTriangle throws an exception
	 * when edges used for building the triangle are not linked
	 * @throws DelaunayError
	 */
	public void testEdgesIntegrity() throws DelaunayError{
		Point p1 = new Point(0,0,0);
		Point p2 = new Point(4,5,0);
		Point p3 = new Point(3,2,0);
		Point p4 = new Point(8,0,0);
		Point p5 = new Point(7,9,0);
		Point p6 = new Point(3,7,0);
		Edge e1 = new Edge(p1,p4);
		Edge e2 = new Edge(p2,p5);
		Edge e3 = new Edge(p3,p6);
		DelaunayTriangle t = null;
		try{
			t= new DelaunayTriangle(e1,e2,e3);
		} catch (DelaunayError d){
		}
		assertNull(t);
		p1 = new Point(0,0,0);
		p2 = new Point(4,5,0);
		p3 = new Point(3,2,0);
		p4 = new Point(8,0,0);
		p5 = new Point(7,9,0);
		e1 = new Edge(p1,p4);
		e2 = new Edge(p2,p5);
		e3 = new Edge(p3,p4);
		t = null;
		try{
			t= new DelaunayTriangle(e1,e2,e3);
		} catch (DelaunayError d){
		}
		assertNull(t);
		p1 = new Point(0,0,0);
		p2 = new Point(4,5,0);
		p3 = new Point(3,2,0);
		p4 = new Point(8,0,0);
		e1 = new Edge(p1,p4);
		e2 = new Edge(p2,p4);
		e3 = new Edge(p3,p4);
		t = null;
		try{
			t= new DelaunayTriangle(e1,e2,e3);
		} catch (DelaunayError d){
		}
		assertNull(t);
		t = null;
		try{
			t= new DelaunayTriangle(e1,e3,e2);
		} catch (DelaunayError d){
		}
		assertNull(t);
		t = null;
		try{
			t= new DelaunayTriangle(e2,e1,e3);
		} catch (DelaunayError d){
		}
		assertNull(t);
		p1 = new Point(0,0,0);
		p2 = new Point(4,5,0);
		p3 = new Point(3,2,0);
		p4 = new Point(8,0,0);
		e1 = new Edge(p1,p4);
		e2 = new Edge(p2,p4);
		e3 = new Edge(p3,p2);
		t = null;
		try{
			t= new DelaunayTriangle(e1,e2,e3);
		} catch (DelaunayError d){
		}
		assertNull(t);

	}
}
