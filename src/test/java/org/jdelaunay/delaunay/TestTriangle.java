/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdelaunay.delaunay;

import com.vividsolutions.jts.geom.Coordinate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

	/**
	 * Checks that the center of the triangle (or rather, the center of its circumcircle)
	 * is well computed.
	 * @throws DelaunayError
	 */
	public void testGetAndRecomputeCenter() throws DelaunayError{
		Point p1 = new Point(0,0,0);
		Point p2 = new Point(0,3,0);
		Point p3 = new Point(3,0,0);
		Edge e1 = new Edge(p1,p2);
		Edge e2 = new Edge(p2,p3);
		Edge e3 = new Edge(p3,p1);
		DelaunayTriangle t = null;
		t= new DelaunayTriangle(e1,e2,e3);
		Coordinate center = t.getCircumCenter();
		assertTrue(center.equals(new Coordinate(1.5,1.5,0)));
	}

	/**
	 * Test the sort used to classify the triangles, which is based on the center
	 * of their bounding box
	 */
	public void testTriangleSort() throws DelaunayError{
		Point p1 = new Point(0,0,0);
		Point p2 = new Point(4,5,0);
		Point p3 = new Point(3,2,0);
		Point p4 = new Point(8,0,0);
		Point p5 = new Point(7,9,0);
		Point p6 = new Point(3,7,0);
		Edge e1 = new Edge(p1,p2);
		Edge e2 = new Edge(p2,p3);
		Edge e3 = new Edge(p3,p1);
		DelaunayTriangle t1 = new DelaunayTriangle(e1, e2, e3);
		Edge e4 = new Edge(p1,p2);
		Edge e5 = new Edge(p2,p3);
		Edge e6 = new Edge(p3,p1);
		DelaunayTriangle t2 = new DelaunayTriangle(e4, e5, e6);
		assertTrue(t1.compareTo(t2)==0);
		e4 = new Edge(p4, p5);
		e5 = new Edge(p5, p6);
		e6 = new Edge(p6, p4);
		t2 = new DelaunayTriangle(e4, e5, e6);
		assertTrue(t1.compareTo(t2)==-1);
		assertTrue(t2.compareTo(t1)==1);
		e4 = new Edge(p3, p4);
		e5 = new Edge(p2, p4);
		t2 = new DelaunayTriangle(e2, e4, e5);
		assertTrue(t1.compareTo(t2)==-1);
		assertTrue(t2.compareTo(t1)==1);
	}

	/**
	 * Checks the efficiency of the methods supposed to sort a list of triangle,
	 * and to insert a new element in it while keeping it sorted.
	 * Note that two different triangles may be reported as equal...
	 * This case is supposed not to happen in the delaunay triangulation, as
	 * triangles can't intersect in it but on common edges or points.
	 */
	public void testListTriangleSort() throws DelaunayError{
		List<DelaunayTriangle> triangleList = new ArrayList<DelaunayTriangle>();
		Point p1 = new Point(0,0,0);
		Point p2 = new Point(4,5,0);
		Point p3 = new Point(3,2,0);
		Point p4 = new Point(8,0,0);
		Point p5 = new Point(7,9,0);
		Point p6 = new Point(3,7,0);
		Point p7 = new Point(10,5,0);
		Point p8 = new Point(3,9,0);
		Point p9 = new Point(2.2,5.02,0);
		Point p10 = new Point(4,14,0);
		Point p11 = new Point(12,6,0);
		Point p12 = new Point(3.5,24,0);
		Edge e1 = new Edge(p1,p2);
		Edge e2 = new Edge(p2,p3);
		Edge e3 = new Edge(p3,p1);
		triangleList.add(new DelaunayTriangle(e1, e2, e3));
		e1 = new Edge(p1,p5);
		e2 = new Edge(p5,p3);
		e3 = new Edge(p3,p1);
		triangleList.add(new DelaunayTriangle(e1, e2, e3));
		e1 = new Edge(p1,p5);
		e2 = new Edge(p5,p3);
		e3 = new Edge(p3,p1);
		triangleList.add(new DelaunayTriangle(e1, e2, e3));
		e1 = new Edge(p7,p5);
		e2 = new Edge(p5,p6);
		e3 = new Edge(p6,p7);
		triangleList.add(new DelaunayTriangle(e1, e2, e3));
		e1 = new Edge(p8,p10);
		e2 = new Edge(p10,p11);
		e3 = new Edge(p11,p8);
		triangleList.add(new DelaunayTriangle(e1, e2, e3));
		e1 = new Edge(p10,p11);
		e2 = new Edge(p11,p4);
		e3 = new Edge(p4,p10);
		triangleList.add(new DelaunayTriangle(e1, e2, e3));
		e1 = new Edge(p4,p6);
		e2 = new Edge(p6,p8);
		e3 = new Edge(p4,p8);
		triangleList.add(new DelaunayTriangle(e1, e2, e3));
		e1 = new Edge(p7,p1);
		e2 = new Edge(p1,p8);
		e3 = new Edge(p8,p7);
		triangleList.add(new DelaunayTriangle(e1, e2, e3));
		e1 = new Edge(p9,p5);
		e2 = new Edge(p5,p4);
		e3 = new Edge(p4,p9);
		triangleList.add(new DelaunayTriangle(e1, e2, e3));
		e1 = new Edge(p12,p2);
		e2 = new Edge(p2,p8);
		e3 = new Edge(p8,p12);
		triangleList.add(new DelaunayTriangle(e1, e2, e3));
		Collections.sort(triangleList);
		isTriangleListSorted(triangleList);
		e1 = new Edge(p5,p7);
		e2 = new Edge(p7,p9);
		e3 = new Edge(p9,p5);
		DelaunayTriangle t1=new DelaunayTriangle(e1, e2, e3);
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.setTriangleList(triangleList);
		mesh.addTriangle(t1);
		isTriangleListSorted(triangleList);
	}

	private void isTriangleListSorted(List<DelaunayTriangle> list){
		if(list !=null && !list.isEmpty()){
			DelaunayTriangle previous;
			DelaunayTriangle current = list.get(0);
			for(int i=1; i<list.size();i++){
				previous = current;
				current = list.get(i);
				assertTrue(previous.compareTo(current)<1);
			}
		} else {
			assertTrue(false);
		}
	}

	/**
	 * Test the isInside method.
	 * @throws DelaunayError
	 */
	public void testIsInside() throws DelaunayError{
		Point p1 = new Point(0,0,0);
		Point p2 = new Point(4,5,0);
		Point p3 = new Point(3,0,0);
		Edge e1 = new Edge(p1,p2);
		Edge e2 = new Edge(p2,p3);
		Edge e3 = new Edge(p3,p1);
		DelaunayTriangle t1 =new DelaunayTriangle(e1, e2, e3);
		assertTrue(t1.isInside(p1));
		assertTrue(t1.isInside(p2));
		assertTrue(t1.isInside(p3));
		assertTrue(t1.isInside(new Point(2,2,0)));
		assertTrue(t1.isInside(new Point(1,0,0)));

	}

	/**
	 * Performs equality tests between triangles.
	 */
	public void testTrianglesEquality() throws DelaunayError{
		Point p1 = new Point(0,0,0);
		Point p2 = new Point(4,5,0);
		Point p3 = new Point(3,0,0);
		Edge e1 = new Edge(p1,p2);
		Edge e2 = new Edge(p2,p3);
		Edge e3 = new Edge(p3,p1);
		DelaunayTriangle t1 =new DelaunayTriangle(e1, e2, e3);
		DelaunayTriangle t2 =new DelaunayTriangle(e1, e2, e3);
		assertEquals(t1,t2);
		t2 =new DelaunayTriangle(e2, e1, e3);
		assertEquals(t1,t2);
		t2 =new DelaunayTriangle(e2, e1, e3);
		assertEquals(t1,t2);
		t2 =new DelaunayTriangle(e2, e3, e1);
		assertEquals(t1,t2);
		t2 =new DelaunayTriangle(e1, e3, e2);
		assertEquals(t1,t2);
		t2 =new DelaunayTriangle(e3, e2, e1);
		assertEquals(t1,t2);
		t2 =new DelaunayTriangle(e3, e1, e2);
		assertEquals(t1,t2);
		Point p4 = new Point(8,0,0);
		Edge e4 = new Edge(p4,p1);
		Edge e5 = new Edge(p4,p2);
		t2 =new DelaunayTriangle(e4, e1, e5);
		assertFalse(t2.equals(t1));
	}

	/**
	 * The equality between two triangles in the mesh can't be determined just by
	 * using the envelope of two triangles. this test presents a case where
	 * two triangles sharing an edge would be reported as equals if we performed
	 * our equality operation just by using the envelopes. But they are different...
	 */
	public void testTooCloseTrianglesEquality() throws DelaunayError{
		Edge common = new Edge(0,2,0,5,6,0);
		DelaunayTriangle t1 = new DelaunayTriangle(common, new Edge(0,2,0,3,6,0), new Edge(3,6,0,5,6,0));
		DelaunayTriangle t2 = new DelaunayTriangle(common, new Edge(0,2,0,2,3,0), new Edge(2,3,0,5,6,0));
		assertFalse(t1.equals(t2));
		assertTrue(t1.compareTo(t2)!=0);
		
	}

	/**
	 * tests that we effectively retrieve the leftmost point of a triangle.
	 * @throws DelaunayError
	 */
	public void testGetLeftMost() throws DelaunayError{
		Point p1 = new Point(0,0,0);
		Point p2 = new Point(4,5,0);
		Point p3 = new Point(3,0,0);
		Edge e1 = new Edge(p1,p2);
		Edge e2 = new Edge(p2,p3);
		Edge e3 = new Edge(p3,p1);
		DelaunayTriangle t1 =new DelaunayTriangle(e1, e2, e3);
		Point left = t1.getLeftMost();
		assertTrue(left.equals(new Point(0,0,0)));
		p2 = new Point(0,3,0);
		e1 = new Edge(p1,p2);
		e2 = new Edge(p2,p3);
		e3 = new Edge(p3,p1);
		t1 =new DelaunayTriangle(e3, e2, e1);
		left = t1.getLeftMost();
		assertTrue(left.equals(new Point(0,0,0)));
		p2 = new Point(-1,3,0);
		e1 = new Edge(p1,p2);
		e2 = new Edge(p2,p3);
		e3 = new Edge(p3,p1);
		t1 =new DelaunayTriangle(e3, e2, e1);
		left = t1.getLeftMost();
		assertTrue(left.equals(new Point(-1,3,0)));

	}

	/**
	 * Tests that we retrieve the good edge when searching the edge opposed
	 * to a point in a triangle.
	 * @throws DelaunayError
	 */
	public void testGetOppositeEdge() throws DelaunayError{
		Point p1 = new Point(0,0,0);
		Point p2 = new Point(4,5,0);
		Point p3 = new Point(3,0,0);
		Edge e1 = new Edge(p1,p2);
		Edge e2 = new Edge(p2,p3);
		Edge e3 = new Edge(p3,p1);
		DelaunayTriangle t1 =new DelaunayTriangle(e1, e2, e3);
		Edge oppos = t1.getOppositeEdge(p1);
		assertTrue(oppos.equals(new Edge(p2,p3)));
		oppos = t1.getOppositeEdge(p2);
		assertTrue(oppos.equals(new Edge(p3,p1)));
		oppos = t1.getOppositeEdge(p3);
		assertTrue(oppos.equals(new Edge(p1,p2)));
		oppos = t1.getOppositeEdge(new Point(8,8,8));
		assertNull(oppos);

	}

	/**
	 * Test that we know how to retrieve an edge from a triangle, given the two
	 * other ones that form it.
	 * @throws DelaunayError
	 */
	public void testGetLastEdge() throws DelaunayError{
		Point p1 = new Point(0,0,0);
		Point p2 = new Point(4,5,0);
		Point p3 = new Point(3,0,0);
		Edge e1 = new Edge(p1,p2);
		Edge e2 = new Edge(p2,p3);
		Edge e3 = new Edge(p3,p1);
		DelaunayTriangle t1 =new DelaunayTriangle(e1, e2, e3);
		Edge last;
		last = t1.getLastEdge(e1, e2);
		assertTrue(last.equals(e3));
		last = t1.getLastEdge(e1, e3);
		assertTrue(last.equals(e2));
		last = t1.getLastEdge(e3, e1);
		assertTrue(last.equals(e2));
		last = t1.getLastEdge(e3, e2);
		assertTrue(last.equals(e1));
		last = t1.getLastEdge(e3, new Edge(5,5,5,4,3,0));
		assertNull(last);
		last = t1.getLastEdge(new Edge(5,5,5,4,3,0), e3);
		assertNull(last);
		last = t1.getLastEdge(new Edge(5,5,5,4,3,0), new Edge(9,8,7,6,5,4));
		assertNull(last);

	}

	/**
	 * Tests that we are able to retrieve triangle in a sorted list. The input points
	 * are the same that those used in TestConstrainedMesh.testManyConstraints().
	 * @throws DelaunayError
	 */
	public void testLargerTriangleSort() throws DelaunayError{
		List<DelaunayTriangle> triangleList = new ArrayList<DelaunayTriangle>();
		triangleList.add(new DelaunayTriangle(
			new Edge(0,3,0,8,3,0),
			new Edge(0,3,0,5,4,0),
			new Edge(5,4,0,8,3,0)));
		triangleList.add(new DelaunayTriangle(
			new Edge(0,3,0,8,3,0),
			new Edge(8,3,0,4,1,0),
			new Edge(4,1,0,0,3,0)));
		triangleList.add(new DelaunayTriangle(
			new Edge(0,3,0,5,4,0),
			new Edge(5,4,0,4,5,0),
			new Edge(4,5,0,0,3,0)));
		triangleList.add(new DelaunayTriangle(
			new Edge(0,3,0,12,12,0),
			new Edge(12,12,0,4,5,0),
			new Edge(4,5,0,0,3,0)));
		triangleList.add(new DelaunayTriangle(
			new Edge(8,7,0,12,12,0),
			new Edge(12,12,0,4,5,0),
			new Edge(4,5,0,8,7,0)));
		triangleList.add(new DelaunayTriangle(
			new Edge(8,7,0,5,4,0),
			new Edge(5,4,0,4,5,0),
			new Edge(4,5,0,8,7,0)));
		triangleList.add(new DelaunayTriangle(
			new Edge(8,7,0,9,6,0),
			new Edge(9,6,0,8,3,0),
			new Edge(8,3,0,8,7,0)));
		triangleList.add(new DelaunayTriangle(
			new Edge(8,7,0,5,4,0),
			new Edge(5,4,0,8,3,0),
			new Edge(8,3,0,8,7,0)));
		triangleList.add(new DelaunayTriangle(
			new Edge(9,0,0,9,6,0),
			new Edge(9,6,0,8,3,0),
			new Edge(8,3,0,9,0,0)));
		triangleList.add(new DelaunayTriangle(
			new Edge(9,0,0,9,6,0),
			new Edge(9,6,0,10,3,0),
			new Edge(10,3,0,9,0,0)));
		triangleList.add(new DelaunayTriangle(
			new Edge(9,0,0,12,6,0),
			new Edge(12,6,0,10,3,0),
			new Edge(10,3,0,9,0,0)));
		triangleList.add(new DelaunayTriangle(
			new Edge(9,6,0,12,6,0),
			new Edge(12,6,0,10,3,0),
			new Edge(10,3,0,9,6,0)));
		triangleList.add(new DelaunayTriangle(
			new Edge(9,6,0,12,6,0),
			new Edge(12,6,0,8,7,0),
			new Edge(8,7,0,9,6,0)));
		triangleList.add(new DelaunayTriangle(
			new Edge(12,7,0,12,6,0),
			new Edge(12,6,0,8,7,0),
			new Edge(8,7,0,12,7,0)));
		triangleList.add(new DelaunayTriangle(
			new Edge(12,7,0,11,9,0),
			new Edge(11,9,0,12,12,0),
			new Edge(12,12,0,12,7,0)));
		triangleList.add(new DelaunayTriangle(
			new Edge(12,12,0,11,9,0),
			new Edge(11,9,0,8,7,0),
			new Edge(8,7,0,12,12,0)));
		triangleList.add(new DelaunayTriangle(
			new Edge(9,0,0,8,3,0),
			new Edge(8,3,0,4,1,0),
			new Edge(4,1,0,9,0,0)));
		triangleList.add(new DelaunayTriangle(
			new Edge(11,9,0,8,7,0),
			new Edge(12,7,0,11,9,0),
			new Edge(8,7,0,12,7,0)));
		ArrayList<DelaunayTriangle> sorted = new ArrayList<DelaunayTriangle>(triangleList);
		Collections.sort(sorted);
		for(DelaunayTriangle tri : triangleList){
			assertTrue(Collections.binarySearch(sorted, tri)>=0);
		}
	}
}
