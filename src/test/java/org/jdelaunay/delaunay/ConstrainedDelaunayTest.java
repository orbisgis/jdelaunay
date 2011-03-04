package org.jdelaunay.delaunay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConstrainedDelaunayTest extends BaseUtility {

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
			DEdge e1 = new DEdge(new DPoint(0, 0, 0), new DPoint(2, 2, 0));
			DEdge e2 = new DEdge(new DPoint(0, 2, 0), new DPoint(2, 0, 0));

			//We add the two points to the constraints of the mesh
			ArrayList<DEdge> edgeList= new ArrayList<DEdge>();
			edgeList.add(e2);
			edgeList.add(e1);
			mesh.setConstraintEdges(edgeList);
			mesh.forceConstraintIntegrity();
			assertTrue(true);

			//We check that all the five points are here.
			assertNotNull(mesh.listContainsPoint(new DPoint(0, 0, 0)));
			assertNotNull(mesh.listContainsPoint(new DPoint(1, 1, 0)));
			assertNotNull(mesh.listContainsPoint(new DPoint(2, 2, 0)));
			assertNotNull(mesh.listContainsPoint(new DPoint(2, 0, 0)));
			assertNotNull(mesh.listContainsPoint(new DPoint(0, 2, 0)));


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
			mesh.addPoint(new DPoint(1,1,0));
			mesh.addPoint(new DPoint(2,1,0));
			mesh.addPoint(new DPoint(7,1,0));
			mesh.addPoint(new DPoint(3,2,0));
			mesh.addPoint(new DPoint(3,0,0));
			mesh.addPoint(new DPoint(3,1,0));
			mesh.addPoint(new DPoint(1,0,0));
			mesh.addPoint(new DPoint(8,1,0));
			mesh.addPoint(new DPoint(1,3,0));
			mesh.addPoint(new DPoint(1,4,0));
			List<DPoint> list = mesh.getPoints();
			assertTrue(list.get(0).equals2D(new DPoint(1,0,0)));
			assertTrue(list.get(1).equals2D(new DPoint(1,1,0)));
			assertTrue(list.get(2).equals2D(new DPoint(1,3,0)));
			assertTrue(list.get(3).equals2D(new DPoint(1,4,0)));
			assertTrue(list.get(4).equals2D(new DPoint(2,1,0)));
			assertTrue(list.get(5).equals2D(new DPoint(3,0,0)));
			assertTrue(list.get(6).equals2D(new DPoint(3,1,0)));
			assertTrue(list.get(7).equals2D(new DPoint(3,2,0)));
			assertTrue(list.get(8).equals2D(new DPoint(7,1,0)));
			assertTrue(list.get(9).equals2D(new DPoint(8,1,0)));

		} catch (DelaunayError e ){
			System.out.println(e.getMessage());
		}
	}

	public void testListContainsPoint(){
		ConstrainedMesh mesh = new ConstrainedMesh();
		try {
			mesh.addPoint(new DPoint(1,1,0));
			mesh.addPoint(new DPoint(2,1,0));
			mesh.addPoint(new DPoint(7,1,0));
			mesh.addPoint(new DPoint(3,2,0));
			mesh.addPoint(new DPoint(3,0,0));
			mesh.addPoint(new DPoint(3,1,0));
			mesh.addPoint(new DPoint(1,0,0));
			mesh.addPoint(new DPoint(8,1,0));
			mesh.addPoint(new DPoint(1,3,0));
			mesh.addPoint(new DPoint(1,4,0));
			assertTrue(mesh.listContainsPoint(new DPoint(1,0,0))==0);
			assertTrue(mesh.listContainsPoint(new DPoint(1,1,0))==1);
			assertTrue(mesh.listContainsPoint(new DPoint(1,3,0))==2);
			assertTrue(mesh.listContainsPoint(new DPoint(1,4,0))==3);
			assertTrue(mesh.listContainsPoint(new DPoint(2,1,0))==4);
			assertTrue(mesh.listContainsPoint(new DPoint(3,0,0))==5);
			assertTrue(mesh.listContainsPoint(new DPoint(3,1,0))==6);
			assertTrue(mesh.listContainsPoint(new DPoint(3,2,0))==7);
			assertTrue(mesh.listContainsPoint(new DPoint(7,1,0))==8);
			assertTrue(mesh.listContainsPoint(new DPoint(8,1,0))==9);
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
			mesh.addPoint(new DPoint(1,1,0));
			mesh.addPoint(new DPoint(1,1,0));
			List<DPoint> list = mesh.getPoints();
			assertTrue(list.size()==1);
			assertTrue(list.get(0).equals2D(new DPoint(1,1,0)));
		} catch (DelaunayError e ){
			System.out.println(e.getMessage());
		}
	}

	/**
	 * This test chacks that a list of edge is properly ordered with the
	 * sortEdgesLeft method.
	 * It tests that duplicates are removed during the insertion
	 */
	public void testOrderEdgeList() throws DelaunayError {
		ArrayList<DEdge> list = new ArrayList<DEdge>();
		ConstrainedMesh mesh = new ConstrainedMesh();
		list.add(new DEdge(0,0,0,10,1,1));
		list.add(new DEdge(0.5,0,0,10,1,1));
		list.add(new DEdge(0,0.5,0,10,1,1));
		list.add(new DEdge(1,2,0,10,10,1));
		list.add(new DEdge(5,0,0,10,1,1));
		list.add(new DEdge(0,4,0,10,1,1));
		list.add(new DEdge(3,0,0,10,1,1));
		list.add(new DEdge(2,0,0,10,1,1));
		list.add(new DEdge(7,0,0,10,1,1));
		list.add(new DEdge(15,2,0,6,1,1));
		list.add(new DEdge(2,0,0,10,1,1));
		list.add(new DEdge(2,0,0,11,1,1));
		list.add(new DEdge(2,0,0,8,1,1));
		list.add(new DEdge(2,0,0,15,1,1));
		list.add(new DEdge(2,0,0,14,1,1));
		list.add(new DEdge(2,0,0,10,1,1));
		List<DEdge> sorted = mesh.sortEdgesLeft(list);
		DEdge e1 ;
		DEdge e2= sorted.get(0);
		for(int i=1; i<sorted.size(); i++){
			e1=e2;
			e2=sorted.get(i);
			int c=e1.getPointLeft().compareTo2D(e2.getPointLeft());
			assertTrue(c<=0);
		}
		assertTrue(sorted.size()==14);
		//We do the same thing but with random edges.
		List<DEdge> random = getRandomEdges(1000);
		sorted = mesh.sortEdgesLeft(random);
		e2= sorted.get(0);
		for(int i=1; i<sorted.size(); i++){
			e1=e2;
			e2=sorted.get(i);
			int c=e1.getPointLeft().compareTo2D(e2.getPointLeft());
			if(c>0){
				System.out.println(e1+" ------- "+e2);
			}
			assertTrue(c<=0);
		}
		random = getRandomEdges(1000);
		for(DEdge ed : random){
			mesh.addConstraintEdge(ed);
		}
		sorted = mesh.getConstraintEdges();
		e2= sorted.get(0);
		for(int i=1; i<sorted.size(); i++){
			e1=e2;
			e2=sorted.get(i);
			int c=e1.getPointLeft().compareTo2D(e2.getPointLeft());
			if(c>0){
				System.out.println(e1+" ------- "+e2);
			}
			assertTrue(c<=0);
		}
	}

	/**
	 * This test checks that edges are actually vertically sorted when using
	 * the sortEdgesVertically method.
	 * @throws DelaunayError
	 */
	public void testSortEdgeVertically() throws DelaunayError{
		ArrayList<DEdge> list = new ArrayList<DEdge>();
		ConstrainedMesh mesh = new ConstrainedMesh();
		list.add(new DEdge(0,0,0,10,4,1));
		list.add(new DEdge(0.5,0,0,10,1,1));
		list.add(new DEdge(0,0.5,0,10,1,1));
		list.add(new DEdge(1,2,0,10,10,1));
		list.add(new DEdge(5,0,0,10,1,1));
		list.add(new DEdge(0,4,0,10,6,1));
		list.add(new DEdge(3,0,0,10,1,1));
		list.add(new DEdge(2,0,0,10,1,1));
		list.add(new DEdge(7,0,0,10,8,1));
		list.add(new DEdge(15,2,0,6,1,1));
		list.add(new DEdge(2,0,0,10,1,1));
		list.add(new DEdge(2,0,0,11,1,1));
		list.add(new DEdge(2,0,0,8,1,1));
		list.add(new DEdge(2,0,0,15,1,1));
		list.add(new DEdge(2,0,0,14,1,1));
		list.add(new DEdge(2,0,0,10,1,1));
		list.add(new DEdge(8,-4,0,8,50,1));
		//We sort our edges (left-right), as we don't want to keep duplicates.
		List<DEdge> sortedLeft = mesh.sortEdgesLeft(list);
		//We sort the edges vertically
		mesh.sortEdgesVertically(sortedLeft, 8);
		DEdge e1 ;
		DEdge e2= sortedLeft.get(0);
		double d1, d2;
		for(int i=1; i<sortedLeft.size(); i++){
			e1=e2;
			e2=sortedLeft.get(i);
			d1=e1.getPointFromItsX(8).getY();
			d2=e2.getPointFromItsX(8).getY();
			assertTrue(d1<=d2);
		}
	}

	/**
	 * We want to check that we correctly insert new edges in an already
	 * "vertically sorted" list.
	 * @throws DelaunayError
	 */
	public void testInsertEdgeVertically() throws DelaunayError{
		ArrayList<DEdge> list = new ArrayList<DEdge>();
		ConstrainedMesh mesh = new ConstrainedMesh();
		list.add(new DEdge(0,0,0,10,4,1));
		list.add(new DEdge(0.5,0,0,10,1,1));
		list.add(new DEdge(0,0.5,0,10,1,1));
		list.add(new DEdge(1,2,0,10,10,1));
		list.add(new DEdge(5,0,0,10,1,1));
		list.add(new DEdge(0,4,0,10,6,1));
		list.add(new DEdge(3,0,0,10,1,1));
		list.add(new DEdge(2,0,0,10,1,1));
		list.add(new DEdge(7,0,0,10,8,1));
		list.add(new DEdge(15,2,0,6,1,1));
		list.add(new DEdge(2,0,0,10,1,1));
		list.add(new DEdge(2,0,0,11,1,1));
		list.add(new DEdge(2,0,0,8,1,1));
		list.add(new DEdge(2,0,0,15,1,1));
		list.add(new DEdge(2,0,0,14,1,1));
		list.add(new DEdge(2,0,0,10,1,1));
		list.add(new DEdge(8,-4,0,8,50,1));
		//We sort our edges (left-right), as we don't want to keep duplicates.
		List<DEdge> sortedLeft = mesh.sortEdgesLeft(list);
		//We sort the edges vertically
		mesh.sortEdgesVertically(sortedLeft, 8);
		mesh.insertEdgeVerticalList(new DEdge(4,0,0,12,1,1), sortedLeft, 8);
		mesh.insertEdgeVerticalList(new DEdge(4,0,0,12,6,1), sortedLeft, 8);
		mesh.insertEdgeVerticalList(new DEdge(5,2,0,12,7,1), sortedLeft, 8);
		mesh.insertEdgeVerticalList(new DEdge(5,0,0,19,5,1), sortedLeft, 8);
		//We add vertical edges to be sure they are well processed
		mesh.insertEdgeVerticalList(new DEdge(8,0,0,8,9,1), sortedLeft, 8);
		mesh.insertEdgeVerticalList(new DEdge(8,7,0,8,3,1), sortedLeft, 8);
		DEdge e1 ;
		DEdge e2= sortedLeft.get(0);
		double d1, d2;
		for(int i=1; i<sortedLeft.size(); i++){
			e1=e2;
			e2=sortedLeft.get(i);
			d1=e1.getPointFromItsX(8).getY();
			d2=e2.getPointFromItsX(8).getY();
			assertTrue(d1<=d2);
		}
		//We check that insertEdgesVerticalList doesn't keep duplicates
		sortedLeft = new ArrayList<DEdge>();
		mesh.insertEdgeVerticalList(new DEdge(4,0,0,12,1,1), sortedLeft, 8);
		mesh.insertEdgeVerticalList(new DEdge(4,0,0,12,6,1), sortedLeft, 8);
		mesh.insertEdgeVerticalList(new DEdge(5,2,0,12,7,1), sortedLeft, 8);
		mesh.insertEdgeVerticalList(new DEdge(5,0,0,19,5,1), sortedLeft, 8);
		mesh.insertEdgeVerticalList(new DEdge(26,0,0,12,1,1), sortedLeft, 8);
		mesh.insertEdgeVerticalList(new DEdge(23,0,0,12,6,1), sortedLeft, 8);
		mesh.insertEdgeVerticalList(new DEdge(23,2,0,12,7,1), sortedLeft, 8);
		mesh.insertEdgeVerticalList(new DEdge(16,0,0,19,5,1), sortedLeft, 8);
		mesh.insertEdgeVerticalList(new DEdge(8,0,0,8,9,1), sortedLeft, 8);
		mesh.insertEdgeVerticalList(new DEdge(8,0,0,8,9,1), sortedLeft, 8);
		assertTrue(sortedLeft.size()==9);

	}

	/**
	 * Checks the efficiency of the vertical sorting algorithm with random sets of edges
	 * @throws DelaunayError
	 */
	public void testVerticalSortRandom() throws DelaunayError{
		ConstrainedMesh mesh = new ConstrainedMesh();
		List<DEdge> constraints =  getRandomEdges(500);
		mesh.sortEdgesVertically(constraints, 8);
		DEdge e1 = constraints.get(0);
		DEdge e2;
		for(int i=1; i<constraints.size();i++){
			e2=e1;
			e1=constraints.get(i);
			assertTrue(e2.verticalSort(e1, 8)<1);
		}
		List<DEdge> toBeAdded = getRandomEdges(500);
		for(DEdge edge : toBeAdded){
			mesh.insertEdgeVerticalList(edge, constraints, 8);
			e1 = constraints.get(0);
			for(int i=1; i<constraints.size();i++){
				e2=e1;
				e1=constraints.get(i);
				assertTrue(e2.verticalSort(e1, 8)<1);
			}
		}

	}

	/**
	 * We check that event points can be added efficiently from secant edges
	 *
	 */
	public void testAddIntersectionPoints() throws DelaunayError{
		ArrayList<DEdge> list = new ArrayList<DEdge>();
		ConstrainedMesh mesh = new ConstrainedMesh();
		ArrayList<DPoint> events = new ArrayList<DPoint>();
		//We first check with only two edges which intersect in three
		//dimensions
		list.add(new DEdge(0,0,0,5,5,5));
		list.add(new DEdge(0,5,0,5,0,5));
		mesh.addPointsFromNeighbourEdges(list, events);
		assertTrue(events.size()==1);
		DPoint inter = events.get(0);
		assertTrue(inter.equals2D(new DPoint(2.5,2.5,0)));
		assertTrue(inter.equals(new DPoint(2.5,2.5,2.5)));
		//We check with two edges which intersect in only two dimensions
		list = new ArrayList<DEdge>();
		events = new ArrayList<DPoint>();
		list.add(new DEdge(0,0,0,5,5,5));
		list.add(new DEdge(0,5,0,5,0,0));
		mesh.addPointsFromNeighbourEdges(list, events);
		assertTrue(events.size()==1);
		inter = events.get(0);
		assertTrue(inter.equals2D(new DPoint(2.5,2.5,0)));
		assertTrue(inter.equals(new DPoint(2.5,2.5,2.5)));
	}
	/**
	 * This test checks that edges and their points are properly added to the mesh
	 */
	public void testAddConstraintEdge(){
		ConstrainedMesh mesh = new ConstrainedMesh();
		try{
			mesh.addConstraintEdge(new DEdge(new DPoint(0,0,0), new DPoint(3,3,0)));
			mesh.addConstraintEdge(new DEdge(new DPoint(2,3,0), new DPoint(5,3,0)));
			//We check that the points are in the points list
			List<DPoint> list = mesh.getPoints();
			assertTrue(list.get(0).equals2D(new DPoint(0,0,0)));
			assertTrue(list.get(1).equals2D(new DPoint(2,3,0)));
			assertTrue(list.get(2).equals2D(new DPoint(3,3,0)));
			assertTrue(list.get(3).equals2D(new DPoint(5,3,0)));

			//We check that the edges are in the list
			DEdge m1 = new DEdge(new DPoint(0,0,0), new DPoint(3,3,0));
			DEdge m2 = new DEdge(new DPoint(2,3,0), new DPoint(5,3,0));

			List<DEdge> listConst = mesh.getConstraintEdges();
			assertTrue(listConst.get(0).haveSamePoint(m1));
			assertTrue(listConst.get(1).haveSamePoint(m2));
		} catch(DelaunayError d){
			System.out.println(d.getMessage());
		}

	}

	/**
	 * This test checks that intersections are well processed by the sweep line
	 * algorithm.
	 * It obviously directly depends on the previous tests, and on the algorithms checked
	 * in these tests.
	 */
	public void testProcessIntersections() throws DelaunayError {
		//two crossing edges
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge(new DPoint(0,0,0), new DPoint(2,2,0)));
		mesh.addConstraintEdge(new DEdge(new DPoint(0,2,0), new DPoint(2,0,0)));
		mesh.forceConstraintIntegrity();
		List<DEdge> edgeList = mesh.getConstraintEdges();
		assertTrue(edgeList.size()==4);
		assertTrue(mesh.listContainsPoint(new DPoint(1,1,0))>-1);

		//(1,1,0) lies on the first edge and is an extremity of the second one.
		mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge(new DPoint(0,0,0), new DPoint(2,2,0)));
		mesh.addConstraintEdge(new DEdge(new DPoint(1,1,0), new DPoint(2,0,0)));
		mesh.forceConstraintIntegrity();edgeList = mesh.getConstraintEdges();
		assertTrue(edgeList.size()==3);
		assertTrue(mesh.listContainsPoint(new DPoint(1,1,0))>-1);

		//idem, but changing the edges order
		mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge(new DPoint(1,1,0), new DPoint(2,2,0)));
		mesh.addConstraintEdge(new DEdge(new DPoint(0,2,0), new DPoint(2,0,0)));
		mesh.forceConstraintIntegrity();
		edgeList = mesh.getConstraintEdges();
		assertTrue(edgeList.size()==3);
		assertTrue(mesh.listContainsPoint(new DPoint(1,1,0))>-1);

		//The two edges don't intersect, they just share an extremity.
		mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge(new DPoint(1,1,0), new DPoint(2,2,0)));
		mesh.addConstraintEdge(new DEdge(new DPoint(1,1,0), new DPoint(2,0,0)));
		mesh.forceConstraintIntegrity();
		edgeList = mesh.getConstraintEdges();
		assertTrue(edgeList.size()==2);
		assertTrue(mesh.listContainsPoint(new DPoint(1,1,0))>-1);

		//one extremity of an edge relies the other, which is vertical
		mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge(new DPoint(1,1,0), new DPoint(2,2,0)));
		mesh.addConstraintEdge(new DEdge(new DPoint(1,2,0), new DPoint(1,0,0)));
		mesh.forceConstraintIntegrity();
		edgeList = mesh.getConstraintEdges();
		assertTrue(edgeList.size()==3);
		assertTrue(mesh.listContainsPoint(new DPoint(1,1,0))>-1);

		//two vertical overlapping edges
		mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge(new DPoint(2,1,0), new DPoint(2,2,0)));
		mesh.addConstraintEdge(new DEdge(new DPoint(2,4,0), new DPoint(2,0,0)));
		mesh.forceConstraintIntegrity();
		edgeList = mesh.getConstraintEdges();
		assertTrue(edgeList.size()==3);

		//the third edge is "between" the two first, which intersect and
		//whose intersection lies after the right point of the third one.
		mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge(new DPoint(2,1,0), new DPoint(3,6,0)));
		mesh.addConstraintEdge(new DEdge(new DPoint(1,4,0), new DPoint(7,2,0)));
		mesh.addConstraintEdge(new DEdge(new DPoint(0,2,0), new DPoint(2,3,0)));
		mesh.forceConstraintIntegrity();
		edgeList = mesh.getConstraintEdges();
		assertTrue(edgeList.size()==5);
                DEdge e1 = edgeList.get(0);
                DEdge e2;
                for(int i = 1; i<edgeList.size();i++){
                        e2 = e1;
                        e1 = edgeList.get(i);
                        assertTrue(e2.sortLeftRight(e1)==-1);
                }

		//two "crosses", one left from the first
		mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge(new DPoint(4,1,0), new DPoint(5,6,0)));
		mesh.addConstraintEdge(new DEdge(new DPoint(4,4,0), new DPoint(10,2,0)));
		mesh.addConstraintEdge(new DEdge(new DPoint(0,2,0), new DPoint(2,3,0)));
		mesh.addConstraintEdge(new DEdge(new DPoint(0,2,0), new DPoint(2,3,0)));
		mesh.addConstraintEdge(new DEdge(new DPoint(0,4,0), new DPoint(2,1,0)));
		mesh.forceConstraintIntegrity();
		edgeList = mesh.getConstraintEdges();
		assertTrue(edgeList.size()==8);
                e1 = edgeList.get(0);
//		System.out.println(e1);
                for(int i = 1; i<edgeList.size();i++){
                        e2 = e1;
                        e1 = edgeList.get(i);
//			System.out.println(e1);
                        assertTrue(e2.sortLeftRight(e1)==-1);
                }
//		System.out.println();

		//two "crosses", one under the other.
		mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge(new DPoint(4,1,0), new DPoint(8,6,0)));
		mesh.addConstraintEdge(new DEdge(new DPoint(4,4,0), new DPoint(10,2,0)));
		mesh.addConstraintEdge(new DEdge(new DPoint(4,8,0), new DPoint(10,10,0)));
		mesh.addConstraintEdge(new DEdge(new DPoint(5,10,0), new DPoint(9,9,0)));
		mesh.forceConstraintIntegrity();
		edgeList = mesh.getConstraintEdges();
		assertTrue(edgeList.size()==8);
                e1 = edgeList.get(0);
//		System.out.println(e1);
                for(int i = 1; i<edgeList.size();i++){
                        e2 = e1;
                        e1 = edgeList.get(i);
//			System.out.println(e1);
                        assertTrue(e2.sortLeftRight(e1)==-1);
                }
//		System.out.println();

		//three edges intersect and form a triangle
		mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge(new DPoint(0,3,0), new DPoint(5,3,0)));
		mesh.addConstraintEdge(new DEdge(new DPoint(1,4,0), new DPoint(3,1,0)));
		mesh.addConstraintEdge(new DEdge(new DPoint(1,0,0), new DPoint(5,4,0)));
		mesh.forceConstraintIntegrity();
		edgeList = mesh.getConstraintEdges();
		assertTrue(edgeList.size()==9);
                e1 = edgeList.get(0);
//		System.out.println(e1);
                for(int i = 1; i<edgeList.size();i++){
                        e2 = e1;
                        e1 = edgeList.get(i);
//			System.out.println(e1);
                        assertTrue(e2.sortLeftRight(e1)==-1);
                }
//		System.out.println();

		//
		mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge(new DPoint(2,1,0), new DPoint(2,2,0)));
		mesh.addConstraintEdge(new DEdge(new DPoint(2,4,0), new DPoint(2,0,0)));
		mesh.forceConstraintIntegrity();
		edgeList = mesh.getConstraintEdges();
		assertTrue(edgeList.size()==3);

		mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge(new DPoint(1,3,0), new DPoint(10,6,0)));
		mesh.addConstraintEdge(new DEdge(new DPoint(7,5,0), new DPoint(13,7,0)));
		mesh.addConstraintEdge(new DEdge(new DPoint(7,7,0), new DPoint(13,5,0)));
		mesh.forceConstraintIntegrity();
		edgeList = mesh.getConstraintEdges();
		assertTrue(edgeList.size()==5);

		mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge(new DPoint(1,3,0), new DPoint(10,6,0)));
		mesh.addConstraintEdge(new DEdge(new DPoint(7,5,0), new DPoint(13,7,0)));
		mesh.addConstraintEdge(new DEdge(new DPoint(7,7,0), new DPoint(10,3,0)));
		mesh.forceConstraintIntegrity();
		edgeList = mesh.getConstraintEdges();
		assertTrue(edgeList.size()==6);

		mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge(23.754617414248024,25.03430079155673,8.321899736147756,29.974500344589934,23.130254996554104,8.9565816678153));
		mesh.addConstraintEdge(new DEdge(23.754617414248024,25.03430079155673,8.321899736147756,30.606126914660827,32.288840262582056,15.237518756619197));
		mesh.forceConstraintIntegrity();
		edgeList = mesh.getConstraintEdges();
		assertTrue(edgeList.size()==2);

		mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge(new DPoint(2,4,0), new DPoint(2,2,0)));
		mesh.addConstraintEdge(new DEdge(new DPoint(2,1,0), new DPoint(2,3,0)));
		mesh.forceConstraintIntegrity();
		edgeList = mesh.getConstraintEdges();
		assertTrue(edgeList.size()==3);
		assertTrue(edgeList.get(0).equals(new DEdge(2,1,0,2,2,0)));
		assertTrue(edgeList.get(1).equals(new DEdge(2,2,0,2,3,0)));
		assertTrue(edgeList.get(2).equals(new DEdge(2,3,0,2,4,0)));

		mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge(new DPoint(2,4,0), new DPoint(2,3,0)));
		mesh.addConstraintEdge(new DEdge(new DPoint(2,1,0), new DPoint(2,3,0)));
		mesh.forceConstraintIntegrity();
		edgeList = mesh.getConstraintEdges();
		assertTrue(edgeList.size()==2);

		mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge(new DPoint(1,1,0), new DPoint(2,2,0)));
		mesh.addConstraintEdge(new DEdge(new DPoint(0,2,0), new DPoint(2,0,0)));
		mesh.addConstraintEdge(new DEdge(new DPoint(0,0,0), new DPoint(2,1,0)));
		mesh.forceConstraintIntegrity();
		edgeList = mesh.getConstraintEdges();
		assertTrue(edgeList.size()==6);
		assertTrue(mesh.listContainsPoint(new DPoint(1,1,0))>-1);
                assertTrue(sillyCheckIntersection(edgeList));
                e1 = edgeList.get(0);
                for(int i = 1; i<edgeList.size();i++){
                        e2 = e1;
                        e1 = edgeList.get(i);
                        assertTrue(e2.sortLeftRight(e1)==-1);
                }

		mesh = new ConstrainedMesh();
		DEdge edge1 = new DEdge(29.04662741160085,52.16572027299656,68.38018218763128 , 21.70784635428322,52.702506064941865,70.26548339515645);
		mesh.addConstraintEdge(edge1);
		DEdge edge2 = new DEdge(32.696545765031715,62.25043024404333,48.051049255488714 , 27.630378535764756,51.60370887400286,81.41914742448961);
		mesh.addConstraintEdge(edge2);
		mesh.forceConstraintIntegrity();
		edgeList = mesh.getConstraintEdges();
		assertTrue(edgeList.size()==4);

		mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge(50, 17, 11,1 ,32, 6));
		mesh.addConstraintEdge(new DEdge(97, 30, 99,46, 1, 98));
		mesh.addConstraintEdge(new DEdge(63, 17, 56,91, 26, 35));
		mesh.addConstraintEdge(new DEdge(59, 12, 96,47, 35, 24));
		mesh.addConstraintEdge(new DEdge(44, 44, 10,72, 7, 27));
		mesh.addConstraintEdge(new DEdge(29, 9, 35,33, 67 ,39));
		mesh.addConstraintEdge(new DEdge(4, 5, 18,89, 12, 17));
		mesh.addConstraintEdge(new DEdge(38, 81, 70,33, 35, 36));
		mesh.addConstraintEdge(new DEdge(70, 74, 55,2, 2, 64));
		mesh.addConstraintEdge(new DEdge(51, 50, 47,8, 21, 73));
		edgeList = mesh.getConstraintEdges();
                e1 = edgeList.get(0);
                for(int i = 1; i<edgeList.size();i++){
                        e2 = e1;
                        e1 = edgeList.get(i);
                        assertTrue(e2.sortLeftRight(e1)==-1);
                }

		mesh.forceConstraintIntegrity();
		edgeList = mesh.getConstraintEdges();
                assertTrue(sillyCheckIntersection(edgeList));

//                mesh = new ConstrainedMesh();
//                List<DEdge> randomEdges = getRandomEdges(100);
//                for(DEdge edge : randomEdges){
//                        mesh.addConstraintEdge(edge);
//                }
//		double t1 = System.currentTimeMillis();
//                mesh.forceConstraintIntegrity();
//		edgeList = mesh.getConstraintEdges();
//		boolean bool;
//		System.out.println("number of intersections : " +edgeList.size());
////                mesh.forceConstraintIntegrity();
//		double t2 = System.currentTimeMillis();
//		System.out.println("time used : "+(t2 - t1) );
//		edgeList = mesh.getConstraintEdges();
//                mesh.forceConstraintIntegrity();
//		System.out.println("number of intersections : " +edgeList.size());
////		edgeList = mesh.getConstraintEdges();
////		System.out.println("number of intersections : " +edgeList.size());
//                e1 = edgeList.get(0);
//		t1 = System.currentTimeMillis();
//		bool = sillyCheckIntersection(edgeList);
//		t2 = System.currentTimeMillis();
//                for(int i = 1; i<edgeList.size();i++){
//                        e2 = e1;
//                        e1 = edgeList.get(i);
//                        assertTrue(e2.sortLeftRight(e1)<1);
//                }
//		System.out.println("time used : "+(t2 - t1) );
//		assertTrue(bool);
	}


	/**
	 * Tests the intersectsExistingEdges method
	 * @throws DelaunayError
	 */
	public void testIntersectsExistingEdges() throws DelaunayError{
		ConstrainedMesh mesh = new ConstrainedMesh();
		ArrayList<DEdge> list = new ArrayList<DEdge>();
		list.add(new DEdge(0,0,0,5,5,0));
		list.add(new DEdge(5,5,0,6,1,0));
		mesh.setEdges(list);
		//intersects the first edge
		assertTrue(mesh.intersectsExistingEdges(new DEdge(4,2,0,3,3,0)));
		//intersects and colinear to the first edge
		assertTrue(mesh.intersectsExistingEdges(new DEdge(8,8,0,3,3,0)));
		//one common point with the first edge and intersects the second edge
		assertTrue(mesh.intersectsExistingEdges(new DEdge(0,0,0,6,3,0)));
		//One common extremity with the first edge. does not intersect.
		assertFalse(mesh.intersectsExistingEdges(new DEdge(0,0,0,4,3,0)));
		//One extremity common to both edges. Does not intersect
		assertFalse(mesh.intersectsExistingEdges(new DEdge(5,5,0,6,6,0)));
		//Does not intersect with any of the edges.
		assertFalse(mesh.intersectsExistingEdges(new DEdge(0,8,0,3,8,0)));
		

	}

        /**
         * This test represents a special configuration with 4 edges. They all intersect
         * in a really small area, and that can cause some problems when computing
         * thee intersection.
         * @throws DelaunayError
         */
        public void testAnotherIntersection() throws DelaunayError{
		ConstrainedMesh mesh = new ConstrainedMesh();
		ArrayList<DEdge> edges = new ArrayList<DEdge>();
                edges.add(new DEdge(3.055669878287226, 73.03246145782423, 0, 43.70557626947108, 35.35995458365234, 0));
                edges.add(new DEdge(3.2192640691458885, 98.57692790324268, 0, 19.81947056963683, 13.613537224055394, 0));
                edges.add(new DEdge(5.981788531239529, 17.432917460384022, 0, 9.21484373199296, 90.49887456765843, 0));
                edges.add(new DEdge(6.399806805909236, 67.72788939942218, 0, 74.23296927832122, 86.61091383261046, 0));

                mesh.setConstraintEdges(edges);
//                show(mesh);
                mesh.forceConstraintIntegrity();
                List<DEdge> edgesB = mesh.getConstraintEdges();
//			System.out.println("INSERT INTO edgester VALUES (ST_GEOMFROMTEXT('LINESTRING ("+ptL.getX()+" "+ptL.getY()+" 0,"+ptR.getX()+" "+ptR.getY()+" 0)'));");
		
		assertTrue(edgesB.size()==16);

        }

        /**
         * A test where three edges intersect in a common point, and then are cut
         * again, with a vertical edge.
         * @throws DelaunayError
         */
        public void testIntersection() throws DelaunayError{
                ConstrainedMesh mesh = new ConstrainedMesh();
                ArrayList<DEdge> cstr = new ArrayList<DEdge>();
                cstr.add(new DEdge(0,0,0,4,6,0));
                cstr.add(new DEdge(0,6,0,4,0,0));
                cstr.add(new DEdge(0,3,0,4,3,0));
                cstr.add(new DEdge(3,0,0,3,6,0));
                mesh.setConstraintEdges(cstr);
                mesh.forceConstraintIntegrity();
//                show(mesh);
                List<DEdge> edges = mesh.getConstraintEdges();
                assertTrue(edges.contains(new DEdge(0,0,0,2,3,0)));
                assertTrue(edges.contains(new DEdge(0,6,0,2,3,0)));
                assertTrue(edges.contains(new DEdge(0,3,0,2,3,0)));
                assertTrue(edges.contains(new DEdge(2,3,0,3,4.5,0)));
                assertTrue(edges.contains(new DEdge(3,3,0,3,4.5,0)));
                assertTrue(edges.contains(new DEdge(3,6,0,3,4.5,0)));
                assertTrue(edges.contains(new DEdge(2,3,0,3,3,0)));
                assertTrue(edges.contains(new DEdge(2,3,0,3,1.5,0)));
                assertTrue(edges.contains(new DEdge(3,0,0,3,1.5,0)));
                assertTrue(edges.contains(new DEdge(3,3,0,3,1.5,0)));
                assertTrue(edges.contains(new DEdge(3,3,0,4,3,0)));
                assertTrue(edges.contains(new DEdge(3,1.5,0,4,0,0)));
                assertTrue(edges.contains(new DEdge(3,4.5,0,4,6,0)));
                assertTrue(edges.size()==13);

        }

	/**
	 * When adding a new DEdge to the list of edges of the triangulation, we must
	 * check before that this edge is not already referenced as a constraint. If
	 * it is, we must add the constraint to the edge, not a new, not locked, edge.
	 */
	public void testAddExistingConstraintToEdges() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		DEdge e1 = new DEdge(0,0,0,2,2,0);
		e1.setLocked(true);
		mesh.addConstraintEdge(e1);
		DEdge e2 = new DEdge(0,0,0,2,2,0);
		mesh.addEdge(e2);
		assertTrue(mesh.getEdges().get(0)==e1);
	}

	/**
	 * Test that the methods who checks that a list is vertically sorted works well
	 */
	public void testIsVerticallySorted(){
		List<DEdge> list = new ArrayList<DEdge>();
		ConstrainedMesh mesh = new ConstrainedMesh();
		list.add(new DEdge(0,0,0,2,2,2));
		list.add(new DEdge(0,1,0,2,3,2));
		list.add(new DEdge(0,2,0,2,4,2));
		list.add(new DEdge(0,3,0,2,5,2));
		list.add(new DEdge(0,4,0,2,6,2));
		assertTrue(mesh.isVerticallySorted(list, 1));
		list.add(new DEdge(0,-1,0,2,0,2));
		assertFalse(mesh.isVerticallySorted(list, 1));

	}

	/**
	 * Checks that the searchEdge method works well.
	 */
	public void testSearchEdge() throws DelaunayError{
		List<DEdge> list = new ArrayList<DEdge>();
		ConstrainedMesh mesh = new ConstrainedMesh();
		list.add(new DEdge(0,0,0,2,2,2));
		list.add(new DEdge(0,1,0,2,3,2));
		list.add(new DEdge(0,2,0,2,4,2));
		list.add(new DEdge(0,3,0,2,5,2));
		list.add(new DEdge(0,4,0,2,6,2));
		mesh.setEdges(list);
		Collections.sort(list);
		assertTrue(mesh.searchEdge(new DEdge(0,0,0,2,2,2))==0);
		assertTrue(mesh.searchEdge(new DEdge(0,1,0,2,3,2))==1);
		assertTrue(mesh.searchEdge(new DEdge(0,2,0,2,4,2))==2);
		assertTrue(mesh.searchEdge(new DEdge(-1,0,0,-1,1,0))<0);
	}

	/**
	 * Checks that the searchEdge method works well.
	 */
	public void testRemoveEdge() throws DelaunayError{
		List<DEdge> list = new ArrayList<DEdge>();
		ConstrainedMesh mesh = new ConstrainedMesh();
		list.add(new DEdge(0,0,0,2,2,2));
		list.add(new DEdge(0,1,0,2,3,2));
		list.add(new DEdge(0,2,0,2,4,2));
		list.add(new DEdge(0,3,0,2,5,2));
		list.add(new DEdge(0,4,0,2,6,2));
		mesh.setEdges(list);
		Collections.sort(list);
		mesh.removeEdge(new DEdge(0,0,0,2,2,2));
		assertTrue(mesh.searchEdge(new DEdge(0,0,0,2,2,2))<0);
		assertTrue(mesh.searchEdge(new DEdge(0,1,0,2,3,2))==0);
		assertTrue(mesh.searchEdge(new DEdge(0,2,0,2,4,2))==1);
	}

	/**
	 * Checks that we are able to ad a point to the point list even if it has
	 * not been instanciated.
	 */
	public void testAddPointNullList() throws DelaunayError{
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.setPoints(null);
		mesh.addPoint(new DPoint(0,0,0));
		assertTrue(mesh.listContainsPoint(new DPoint(0,0,0))>=0);
	}

	/**
	 * Tests that we are able to retrieve the list of points whose left point
	 * is the one given in parameter
	 */
	public void testGetEdgeFromLeftPoint() throws DelaunayError{
		ConstrainedMesh mesh = new ConstrainedMesh();
		List<DEdge> fromLeft;
		mesh.addConstraintEdge(new DEdge(0,0,0,2,2,0));
		mesh.addConstraintEdge(new DEdge(2,4,0,2,2,0));
		mesh.addConstraintEdge(new DEdge(4,2,0,2,2,0));
		mesh.addConstraintEdge(new DEdge(2,0,0,2,2,0));
		mesh.addConstraintEdge(new DEdge(0,-1,0,3,-1,0));
		mesh.addConstraintEdge(new DEdge(3,3,0,5,4,0));
		fromLeft = mesh.getConstraintsFromLeftPoint(new DPoint(0,0,0));
		assertTrue(fromLeft.contains(new DEdge(0,0,0,2,2,0)));
		assertTrue(fromLeft.size()==1);
		fromLeft = mesh.getConstraintsFromLeftPoint(new DPoint(2,2,0));
		assertTrue(fromLeft.contains(new DEdge(2,4,0,2,2,0)));
		assertTrue(fromLeft.contains(new DEdge(4,2,0,2,2,0)));
		assertTrue(fromLeft.size()==2);
		fromLeft = mesh.getConstraintsFromLeftPoint(new DPoint(10,10,0));
		assertTrue(fromLeft.isEmpty());
		mesh.setConstraintEdges(new ArrayList<DEdge>());
		fromLeft = mesh.getConstraintsFromLeftPoint(new DPoint(2,2,0));
		assertTrue(fromLeft.isEmpty());
	}

	public void testUpdateExtensionPoints() throws DelaunayError{
		ConstrainedMesh mesh = new ConstrainedMesh();
		DPoint ext1, ext2;
		List<DPoint> extensions;
		mesh.addPoint(new DPoint(0,0,0));
		extensions = mesh.getExtensionPoints();
		ext1 = extensions.get(0);
		ext2 = extensions.get(1);
		assertTrue(ext1.equals(new DPoint(-1,1,0)));
		assertTrue(ext2.equals(new DPoint(-1,-1,0)));
		mesh.addPoint(new DPoint(0,0.5,0));
		extensions = mesh.getExtensionPoints();
		ext1 = extensions.get(0);
		ext2 = extensions.get(1);
		assertTrue(ext1.equals(new DPoint(-1,1.5,0)));
		assertTrue(ext2.equals(new DPoint(-1,-1,0)));
		mesh.addPoint(new DPoint(0,-0.5,0));
		extensions = mesh.getExtensionPoints();
		ext1 = extensions.get(0);
		ext2 = extensions.get(1);
		assertTrue(ext1.equals(new DPoint(-1,1.5,0)));
		assertTrue(ext2.equals(new DPoint(-1,-1.5,0)));
		mesh.addPoint(new DPoint(0,-0.25,0));
		extensions = mesh.getExtensionPoints();
		ext1 = extensions.get(0);
		ext2 = extensions.get(1);
		assertTrue(ext1.equals(new DPoint(-1,1.5,0)));
		assertTrue(ext2.equals(new DPoint(-1,-1.5,0)));
		mesh.addPoint(new DPoint(10,-0.25,0));
		extensions = mesh.getExtensionPoints();
		ext1 = extensions.get(0);
		ext2 = extensions.get(1);
		assertTrue(ext1.equals(new DPoint(-1,1.5,0)));
		assertTrue(ext2.equals(new DPoint(-1,-1.5,0)));
		mesh.addPoint(new DPoint(-2,-0.25,0));
		extensions = mesh.getExtensionPoints();
		ext1 = extensions.get(0);
		ext2 = extensions.get(1);
		assertTrue(ext1.equals(new DPoint(-3,1.5,0)));
		assertTrue(ext2.equals(new DPoint(-3,-1.5,0)));
		mesh.addPoint(new DPoint(-3,3,0));
		extensions = mesh.getExtensionPoints();
		ext1 = extensions.get(0);
		ext2 = extensions.get(1);
		System.out.println(ext1);
		assertTrue(ext1.equals(new DPoint(-4,4,0)));
		assertTrue(ext2.equals(new DPoint(-4,-1.5,0)));
		mesh.addPoint(new DPoint(-4,-3,0));
		extensions = mesh.getExtensionPoints();
		ext1 = extensions.get(0);
		ext2 = extensions.get(1);
		assertTrue(ext1.equals(new DPoint(-5,4,0)));
		assertTrue(ext2.equals(new DPoint(-5,-4,0)));
		
		
	}

	public void testIntersectionFromCatalunya() throws DelaunayError {
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
		mesh.forceConstraintIntegrity();
		List<DEdge> constraint = mesh.getConstraintEdges();
		assertTrue(constraint.size()==15);
		assertConstraintsAreLocked(mesh);
	}

	/**
	 * performs an intersection between an edge which is almost vertical and another one.
	 * @throws DelaunayError
	 */
	public void testIntersectAlmostVertical() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge(296317.8, 2258359.5, 0,296317.79999999993, 2258360.8, 0));
		mesh.addConstraintEdge(new DEdge(296313.2, 2258359.7, 0,296320.29999999993, 2258359.6999999983, 0));
		mesh.addConstraintEdge(new DEdge(296312.7, 2258363.1999999993, 0,296317.79999999993, 2258360.8, 0));
		mesh.addConstraintEdge(new DEdge(296317.8, 2258359.5, 0,296320.99999999994, 2258359.0999999978, 0));
		mesh.forceConstraintIntegrity();
//		show(mesh);
		assertTrue(mesh.getConstraintEdges().size()==6);
	}

	public void testProblematicConfig() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new DEdge(295881.6,           2260893.7,          0, 295882.89999999997, 2260893.6999999993, 0));
		mesh.addConstraintEdge(new DEdge(295881.6,           2260893.7,          0, 295886.5999999999,  2260893.6999999993, 0));
//		mesh.addConstraintEdge(new DEdge(295882.89999999997, 2260893.6999999993, 0, 295884.4,           2260900.7, 0));
//		mesh.addConstraintEdge(new DEdge(295882.89999999997, 2260893.6999999993, 0, 295886.5999999999,  2260893.6999999993, 0));
		mesh.forceConstraintIntegrity();
//		show(mesh);
		assertTrue(mesh.getConstraintEdges().size()==2);
	}

	/**
	 * Method used to create random a list of random edge.
	 * @param number
	 * @return
	 */
	private List<DEdge> getRandomEdges(int number){
		ArrayList<DEdge> retList = new ArrayList<DEdge>();
		int num = (number < 0 ? -number : number);
		for(int i=0; i<num; i++){
			double d1 = Math.random()*100;
			double d2 = Math.random()*100;
			double d3 = Math.random()*100;
			double d4 = Math.random()*100;
			double d5 = Math.random()*100;
			double d6 = Math.random()*100;
			retList.add(new DEdge(d1, d2, d3, d4, d5, d6));
		}
		return retList;
	}
	
	/**
	 * Returns false if there is an intersction (ie if the edges are invalid)
	 * @param edgeList
	 * @return
	 */
	private boolean sillyCheckIntersection(List<DEdge> edgeList) throws DelaunayError{
		DEdge e1;
		DEdge e2;
                boolean ret=true;
		for(int i = 0; i < edgeList.size(); i++){
			e1 = edgeList.get(i);
			for (int j = i+1; j < edgeList.size(); j++){
				e2=edgeList.get(j);
				Element inter =e1.getIntersection(e2);
				int c = e1.intersects(e2);
                                if((c==1 || c==4)&& !e1.equals(e2)){
                                        System.out.println("intersection : "+inter);
					System.out.println();
					System.out.println(i+" = "+e1.getPointLeft()+" : "+e1.getPointRight());
					System.out.println(j+" = "+e2.getPointLeft()+" : "+e2.getPointRight());
					System.out.println();
					for(int k=i;k<=j;k++){
						e1 = edgeList.get(k);
						System.out.println(k+" = "+e1.getPointLeft().getX()+","+e1.getPointLeft().getY()+","+e1.getPointLeft().getZ()+","
						+e1.getPointRight().getX()+","+e1.getPointRight().getY()+","+e1.getPointRight().getZ());
					}
                                        ret=false;
                                        break;
                                }

			}
                        if(!ret){
                                break;
                        }
		}
		return ret;
	}
}

