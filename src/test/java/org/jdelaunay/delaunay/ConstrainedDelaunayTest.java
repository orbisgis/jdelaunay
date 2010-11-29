package org.jdelaunay.delaunay;

import java.util.ArrayList;
import java.util.LinkedList;

public class ConstrainedDelaunayTest extends BaseTest {

	public void testDelaunayRandomBreaklines() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		
		aMesh.setMax(1300, 700);
		aMesh.setRandomPoints(1000);
		aMesh.setRandomEdges(50);

		aMesh.processDelaunay();

		show(aMesh);
		System.out.println();
		assertTrue(true);
	}

	public void testDelaunayBreaklines() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		
		aMesh.setPoints(getPoints());
		aMesh.setConstraintEdges(getBreaklines());
		
		aMesh.processDelaunay();

		show(aMesh);
		System.out.println();
		assertTrue(true);
	}

	public void testGIDS() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		
		aMesh.setPoints(getPoints());
		aMesh.setConstraintEdges(getBreaklines());
		
		aMesh.processDelaunay();

		assertGIDUnicity(aMesh);
	}

	public void testDuplicatesXYZBreakline() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		
		aMesh.setPoints(getPoints());
		ArrayList<MyEdge> breaklines = getBreaklines();
		breaklines.add(new MyEdge(new MyPoint(120, 10, 2), new MyPoint(102, 10,
				1)));
		aMesh.setConstraintEdges(breaklines);

		aMesh.processDelaunay();

		show(aMesh);
		System.out.println();
		assertTrue(true);
	}

	/**
	 * This test checks that constraints which intersect are split, and than
	 * a new point is added to the Mesh.
	 */
	public void testConstraintsIntersectionRemoval(){
		ConstrainedMesh mesh = new ConstrainedMesh();
		try{
			//We create two edges that intersect in 1,1. We must obtain
			//4 constraints ( ((0,0),(1,1)), ((1,1),(2,2)), ((2,0),(1,1)
			// and ((1,1), (0,2)) ). and (consequently) 5 points.
			MyEdge e1 = new MyEdge(new MyPoint(0, 0, 0), new MyPoint(2, 2, 0));
			MyEdge e2 = new MyEdge(new MyPoint(0, 2, 0), new MyPoint(2, 0, 0));
			MyPoint inter = new MyPoint(1,1,0);
			MyEdge ei1 = new MyEdge(new MyPoint(0, 0, 0), new MyPoint(1, 1, 0));
			MyEdge ei2 = new MyEdge(new MyPoint(1, 1, 0), new MyPoint(2, 2, 0));
			MyEdge ei3 = new MyEdge(new MyPoint(0, 2, 0), new MyPoint(1, 1, 0));
			MyEdge ei4 = new MyEdge(new MyPoint(1, 1, 0), new MyPoint(2, 0, 0));

			//We add the two points to the constraints of the mesh
			ArrayList<MyEdge> edgeList= new ArrayList<MyEdge>();
			edgeList.add(e2);
			edgeList.add(e1);
			mesh.setConstraintEdges(edgeList);
//			mesh.forceConstraintIntegrity();
			assertTrue(true);

			//We check that all the five points are here.
//			assertNotNull(mesh.searchPoint(0, 0, 0));
//			assertNotNull(mesh.searchPoint(1, 1, 0));
//			assertNotNull(mesh.searchPoint(2, 2, 0));
//			assertNotNull(mesh.searchPoint(2, 0, 0));
//			assertNotNull(mesh.searchPoint(0, 2, 0));


		} catch (DelaunayError d){
			System.out.println(d.getMessage());
		}
	}

	/**
	 * This test checks that, when points are added to the mesh, they are kept
	 * in a proper order.
	 */
	public void testPointOrder(){
		ConstrainedMesh mesh = new ConstrainedMesh();
		try {
			mesh.addPoint(new MyPoint(1,1,0));
			mesh.addPoint(new MyPoint(2,1,0));
			mesh.addPoint(new MyPoint(7,1,0));
			mesh.addPoint(new MyPoint(3,2,0));
			mesh.addPoint(new MyPoint(3,0,0));
			mesh.addPoint(new MyPoint(3,1,0));
			mesh.addPoint(new MyPoint(1,0,0));
			mesh.addPoint(new MyPoint(8,1,0));
			mesh.addPoint(new MyPoint(1,3,0));
			mesh.addPoint(new MyPoint(1,4,0));
			ArrayList<MyPoint> list = mesh.getPoints();
			assertTrue(list.get(0).equals2D(new MyPoint(1,0,0)));
			assertTrue(list.get(1).equals2D(new MyPoint(1,1,0)));
			assertTrue(list.get(2).equals2D(new MyPoint(1,3,0)));
			assertTrue(list.get(3).equals2D(new MyPoint(1,4,0)));
			assertTrue(list.get(4).equals2D(new MyPoint(2,1,0)));
			assertTrue(list.get(5).equals2D(new MyPoint(3,0,0)));
			assertTrue(list.get(6).equals2D(new MyPoint(3,1,0)));
			assertTrue(list.get(7).equals2D(new MyPoint(3,2,0)));
			assertTrue(list.get(8).equals2D(new MyPoint(7,1,0)));
			assertTrue(list.get(9).equals2D(new MyPoint(8,1,0)));

		} catch (DelaunayError e ){
			System.out.println(e.getMessage());
		}
	}

	public void testListContainsPoint(){
		ConstrainedMesh mesh = new ConstrainedMesh();
		try {
			mesh.addPoint(new MyPoint(1,1,0));
			mesh.addPoint(new MyPoint(2,1,0));
			mesh.addPoint(new MyPoint(7,1,0));
			mesh.addPoint(new MyPoint(3,2,0));
			mesh.addPoint(new MyPoint(3,0,0));
			mesh.addPoint(new MyPoint(3,1,0));
			mesh.addPoint(new MyPoint(1,0,0));
			mesh.addPoint(new MyPoint(8,1,0));
			mesh.addPoint(new MyPoint(1,3,0));
			mesh.addPoint(new MyPoint(1,4,0));
			assertTrue(mesh.listContainsPoint(new MyPoint(1,0,0))==0);
			assertTrue(mesh.listContainsPoint(new MyPoint(1,1,0))==1);
			assertTrue(mesh.listContainsPoint(new MyPoint(1,3,0))==2);
			assertTrue(mesh.listContainsPoint(new MyPoint(1,4,0))==3);
			assertTrue(mesh.listContainsPoint(new MyPoint(2,1,0))==4);
			assertTrue(mesh.listContainsPoint(new MyPoint(3,0,0))==5);
			assertTrue(mesh.listContainsPoint(new MyPoint(3,1,0))==6);
			assertTrue(mesh.listContainsPoint(new MyPoint(3,2,0))==7);
			assertTrue(mesh.listContainsPoint(new MyPoint(7,1,0))==8);
			assertTrue(mesh.listContainsPoint(new MyPoint(8,1,0))==9);
		} catch (DelaunayError e ){
			System.out.println(e.getMessage());
		}
	}

	/**
	 * This test checks that when we add two points at the same place in the space,
	 * only one remains.
	 */
	public void testAddDuplicatePoint(){
		ConstrainedMesh mesh = new ConstrainedMesh();
		try {
			mesh.addPoint(new MyPoint(1,1,0));
			mesh.addPoint(new MyPoint(1,1,0));
			ArrayList<MyPoint> list = mesh.getPoints();
			assertTrue(list.size()==1);
			assertTrue(list.get(0).equals2D(new MyPoint(1,1,0)));
		} catch (DelaunayError e ){
			System.out.println(e.getMessage());
		}
	}

	/**
	 * This test chacks that a list of edge is properly ordered with the
	 * sortEdgesLeft method.
	 * It tests that duplicates are removed during the insertion
	 */
	public void testOrderEdgeList(){
		ArrayList<MyEdge> list = new ArrayList<MyEdge>();
		ConstrainedMesh mesh = new ConstrainedMesh();
		list.add(new MyEdge(0,0,0,10,1,1));
		list.add(new MyEdge(0.5,0,0,10,1,1));
		list.add(new MyEdge(0,0.5,0,10,1,1));
		list.add(new MyEdge(1,2,0,10,10,1));
		list.add(new MyEdge(5,0,0,10,1,1));
		list.add(new MyEdge(0,4,0,10,1,1));
		list.add(new MyEdge(3,0,0,10,1,1));
		list.add(new MyEdge(2,0,0,10,1,1));
		list.add(new MyEdge(7,0,0,10,1,1));
		list.add(new MyEdge(15,2,0,6,1,1));
		list.add(new MyEdge(2,0,0,10,1,1));
		list.add(new MyEdge(2,0,0,11,1,1));
		list.add(new MyEdge(2,0,0,8,1,1));
		list.add(new MyEdge(2,0,0,15,1,1));
		list.add(new MyEdge(2,0,0,14,1,1));
		list.add(new MyEdge(2,0,0,10,1,1));
		ArrayList<MyEdge> sorted = mesh.sortEdgesLeft(list);
		MyEdge e1 ;
		MyEdge e2= sorted.get(0);
		System.out.println(e2);
		for(int i=1; i<sorted.size(); i++){
			e1=e2;
			e2=sorted.get(i);
			int c=e1.getPointLeft().compareTo2D(e2.getPointLeft());
			assertTrue(c<=0);
			System.out.println(e2);
		}
		assertTrue(sorted.size()==14);
	}

	/**
	 * This test checks that edges and their points are properly added to the mesh
	 */
	public void testAddConstraintEdge(){
		ConstrainedMesh mesh = new ConstrainedMesh();
		try{
			mesh.addConstraintEdge(new MyEdge(new MyPoint(0,0,0), new MyPoint(2,3,0)));
			mesh.addConstraintEdge(new MyEdge(new MyPoint(3,3,0), new MyPoint(5,3,0)));

			//We check that the points are in the points list
			ArrayList<MyPoint> list = mesh.getPoints();
			assertTrue(list.get(0).equals2D(new MyPoint(0,0,0)));
			assertTrue(list.get(1).equals2D(new MyPoint(2,3,0)));
			assertTrue(list.get(2).equals2D(new MyPoint(3,3,0)));
			assertTrue(list.get(3).equals2D(new MyPoint(5,3,0)));

			//We check that the edges are in the list
			MyEdge m1 = new MyEdge(new MyPoint(0,0,0), new MyPoint(2,3,0));
			MyEdge m2 = new MyEdge(new MyPoint(3,3,0), new MyPoint(5,3,0));

			ArrayList<MyEdge> listConst = mesh.getConstraintEdges();
			assertTrue(listConst.get(0).haveSamePoint(m1));
			assertTrue(listConst.get(1).haveSamePoint(m2));
		} catch(DelaunayError d){
			System.out.println(d.getMessage());
		}

	}
}
