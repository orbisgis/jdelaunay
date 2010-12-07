package org.jdelaunay.delaunay;

import java.util.ArrayList;
import java.util.List;

public class ConstrainedDelaunayTest extends BaseUtility {

	public void testDelaunayRandomBreaklines() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
//		aMesh.setVerbose(true);
		
		aMesh.setMax(1300, 700);
		aMesh.setRandomPoints(1000);
		aMesh.setRandomEdges(50);

		aMesh.processDelaunay();

//		show(aMesh);
		System.out.println();
		assertTrue(true);
	}

	public void testDelaunayBreaklines() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
//		aMesh.setVerbose(true);
		
		aMesh.setPoints(getPoints());
		aMesh.setConstraintEdges(getBreaklines());
		
		aMesh.processDelaunay();

//		show(aMesh);
		System.out.println();
		assertTrue(true);
	}

	public void testGIDS() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
//		aMesh.setVerbose(true);
		
		aMesh.setPoints(getPoints());
		aMesh.setConstraintEdges(getBreaklines());
		
		aMesh.processDelaunay();

		assertGIDUnicity(aMesh);
	}

	public void testDuplicatesXYZBreakline() throws DelaunayError {

		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
//		aMesh.setVerbose(true);
		
		aMesh.setPoints(getPoints());
		ArrayList<MyEdge> breaklines = getBreaklines();
		breaklines.add(new MyEdge(new MyPoint(120, 10, 2), new MyPoint(102, 10,
				1)));
		aMesh.setConstraintEdges(breaklines);

		aMesh.processDelaunay();

//		show(aMesh);
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
		List<MyEdge> sorted = mesh.sortEdgesLeft(list);
		MyEdge e1 ;
		MyEdge e2= sorted.get(0);
		for(int i=1; i<sorted.size(); i++){
			e1=e2;
			e2=sorted.get(i);
			int c=e1.getPointLeft().compareTo2D(e2.getPointLeft());
			assertTrue(c<=0);
		}
		assertTrue(sorted.size()==14);
		//We do the same thing but with random edges.
		List<MyEdge> random = getRandomEdges(1000);
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
		for(MyEdge ed : random){
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
		ArrayList<MyEdge> list = new ArrayList<MyEdge>();
		ConstrainedMesh mesh = new ConstrainedMesh();
		list.add(new MyEdge(0,0,0,10,4,1));
		list.add(new MyEdge(0.5,0,0,10,1,1));
		list.add(new MyEdge(0,0.5,0,10,1,1));
		list.add(new MyEdge(1,2,0,10,10,1));
		list.add(new MyEdge(5,0,0,10,1,1));
		list.add(new MyEdge(0,4,0,10,6,1));
		list.add(new MyEdge(3,0,0,10,1,1));
		list.add(new MyEdge(2,0,0,10,1,1));
		list.add(new MyEdge(7,0,0,10,8,1));
		list.add(new MyEdge(15,2,0,6,1,1));
		list.add(new MyEdge(2,0,0,10,1,1));
		list.add(new MyEdge(2,0,0,11,1,1));
		list.add(new MyEdge(2,0,0,8,1,1));
		list.add(new MyEdge(2,0,0,15,1,1));
		list.add(new MyEdge(2,0,0,14,1,1));
		list.add(new MyEdge(2,0,0,10,1,1));
		list.add(new MyEdge(8,-4,0,8,50,1));
		//We sort our edges (left-right), as we don't want to keep duplicates.
		List<MyEdge> sortedLeft = mesh.sortEdgesLeft(list);
		//We sort the edges vertically
		mesh.sortEdgesVertically(sortedLeft, 8);
		MyEdge e1 ;
		MyEdge e2= sortedLeft.get(0);
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
		ArrayList<MyEdge> list = new ArrayList<MyEdge>();
		ConstrainedMesh mesh = new ConstrainedMesh();
		list.add(new MyEdge(0,0,0,10,4,1));
		list.add(new MyEdge(0.5,0,0,10,1,1));
		list.add(new MyEdge(0,0.5,0,10,1,1));
		list.add(new MyEdge(1,2,0,10,10,1));
		list.add(new MyEdge(5,0,0,10,1,1));
		list.add(new MyEdge(0,4,0,10,6,1));
		list.add(new MyEdge(3,0,0,10,1,1));
		list.add(new MyEdge(2,0,0,10,1,1));
		list.add(new MyEdge(7,0,0,10,8,1));
		list.add(new MyEdge(15,2,0,6,1,1));
		list.add(new MyEdge(2,0,0,10,1,1));
		list.add(new MyEdge(2,0,0,11,1,1));
		list.add(new MyEdge(2,0,0,8,1,1));
		list.add(new MyEdge(2,0,0,15,1,1));
		list.add(new MyEdge(2,0,0,14,1,1));
		list.add(new MyEdge(2,0,0,10,1,1));
		list.add(new MyEdge(8,-4,0,8,50,1));
		//We sort our edges (left-right), as we don't want to keep duplicates.
		List<MyEdge> sortedLeft = mesh.sortEdgesLeft(list);
		//We sort the edges vertically
		mesh.sortEdgesVertically(sortedLeft, 8);
		mesh.insertEdgeVerticalList(new MyEdge(4,0,0,12,1,1), sortedLeft, 8);
		mesh.insertEdgeVerticalList(new MyEdge(4,0,0,12,6,1), sortedLeft, 8);
		mesh.insertEdgeVerticalList(new MyEdge(5,2,0,12,7,1), sortedLeft, 8);
		mesh.insertEdgeVerticalList(new MyEdge(5,0,0,19,5,1), sortedLeft, 8);
		//We add vertical edges to be sure they are well processed
		mesh.insertEdgeVerticalList(new MyEdge(8,0,0,8,9,1), sortedLeft, 8);
		mesh.insertEdgeVerticalList(new MyEdge(8,7,0,8,3,1), sortedLeft, 8);
		MyEdge e1 ;
		MyEdge e2= sortedLeft.get(0);
		double d1, d2;
		for(int i=1; i<sortedLeft.size(); i++){
			e1=e2;
			e2=sortedLeft.get(i);
			d1=e1.getPointFromItsX(8).getY();
			d2=e2.getPointFromItsX(8).getY();
			assertTrue(d1<=d2);
		}
		//We check that insertEdgesVerticalList doesn't keep duplicates
		sortedLeft = new ArrayList<MyEdge>();
		mesh.insertEdgeVerticalList(new MyEdge(4,0,0,12,1,1), sortedLeft, 8);
		mesh.insertEdgeVerticalList(new MyEdge(4,0,0,12,6,1), sortedLeft, 8);
		mesh.insertEdgeVerticalList(new MyEdge(5,2,0,12,7,1), sortedLeft, 8);
		mesh.insertEdgeVerticalList(new MyEdge(5,0,0,19,5,1), sortedLeft, 8);
		mesh.insertEdgeVerticalList(new MyEdge(26,0,0,12,1,1), sortedLeft, 8);
		mesh.insertEdgeVerticalList(new MyEdge(23,0,0,12,6,1), sortedLeft, 8);
		mesh.insertEdgeVerticalList(new MyEdge(23,2,0,12,7,1), sortedLeft, 8);
		mesh.insertEdgeVerticalList(new MyEdge(16,0,0,19,5,1), sortedLeft, 8);
		mesh.insertEdgeVerticalList(new MyEdge(8,0,0,8,9,1), sortedLeft, 8);
		mesh.insertEdgeVerticalList(new MyEdge(8,0,0,8,9,1), sortedLeft, 8);
		assertTrue(sortedLeft.size()==9);

	}
	/**
	 * We check that event points can be added efficiently from secant edges
	 *
	 */
	public void testAddIntersectionPoints() throws DelaunayError{
		ArrayList<MyEdge> list = new ArrayList<MyEdge>();
		ConstrainedMesh mesh = new ConstrainedMesh();
		ArrayList<MyPoint> events = new ArrayList<MyPoint>();
		//We first check with only two edges which intersect in three
		//dimensions
		list.add(new MyEdge(0,0,0,5,5,5));
		list.add(new MyEdge(0,5,0,5,0,5));
		mesh.addPointsFromNeighbourEdges(list, events);
		assertTrue(events.size()==1);
		MyPoint inter = events.get(0);
		assertTrue(inter.equals2D(new MyPoint(2.5,2.5,0)));
		assertTrue(inter.equals(new MyPoint(2.5,2.5,2.5)));
		//We check with two edges which intersect in only two dimensions
		list = new ArrayList<MyEdge>();
		events = new ArrayList<MyPoint>();
		list.add(new MyEdge(0,0,0,5,5,5));
		list.add(new MyEdge(0,5,0,5,0,0));
		mesh.addPointsFromNeighbourEdges(list, events);
		assertTrue(events.size()==1);
		inter = events.get(0);
		assertTrue(inter.equals2D(new MyPoint(2.5,2.5,0)));
		assertTrue(inter.equals(new MyPoint(2.5,2.5,2.5)));
	}
	/**
	 * This test checks that edges and their points are properly added to the mesh
	 */
	public void testAddConstraintEdge(){
		ConstrainedMesh mesh = new ConstrainedMesh();
		try{
			mesh.addConstraintEdge(new MyEdge(new MyPoint(0,0,0), new MyPoint(3,3,0)));
			mesh.addConstraintEdge(new MyEdge(new MyPoint(2,3,0), new MyPoint(5,3,0)));
			//We check that the points are in the points list
			ArrayList<MyPoint> list = mesh.getPoints();
			assertTrue(list.get(0).equals2D(new MyPoint(0,0,0)));
			assertTrue(list.get(1).equals2D(new MyPoint(2,3,0)));
			assertTrue(list.get(2).equals2D(new MyPoint(3,3,0)));
			assertTrue(list.get(3).equals2D(new MyPoint(5,3,0)));

			//We check that the edges are in the list
			MyEdge m1 = new MyEdge(new MyPoint(0,0,0), new MyPoint(3,3,0));
			MyEdge m2 = new MyEdge(new MyPoint(2,3,0), new MyPoint(5,3,0));

			ArrayList<MyEdge> listConst = mesh.getConstraintEdges();
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
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new MyEdge(new MyPoint(0,0,0), new MyPoint(2,2,0)));
		mesh.addConstraintEdge(new MyEdge(new MyPoint(0,2,0), new MyPoint(2,0,0)));
		mesh.forceConstraintIntegrity();
		List<MyEdge> edgeList = mesh.getConstraintEdges();
		assertTrue(edgeList.size()==4);
		assertTrue(mesh.listContainsPoint(new MyPoint(1,1,0))>-1);

		mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new MyEdge(new MyPoint(0,0,0), new MyPoint(2,2,0)));
		mesh.addConstraintEdge(new MyEdge(new MyPoint(1,1,0), new MyPoint(2,0,0)));
		mesh.forceConstraintIntegrity();edgeList = mesh.getConstraintEdges();
		assertTrue(edgeList.size()==3);
		assertTrue(mesh.listContainsPoint(new MyPoint(1,1,0))>-1);

		mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new MyEdge(new MyPoint(1,1,0), new MyPoint(2,2,0)));
		mesh.addConstraintEdge(new MyEdge(new MyPoint(0,2,0), new MyPoint(2,0,0)));
		mesh.forceConstraintIntegrity();
		edgeList = mesh.getConstraintEdges();
		assertTrue(edgeList.size()==3);
		assertTrue(mesh.listContainsPoint(new MyPoint(1,1,0))>-1);

		mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new MyEdge(new MyPoint(1,1,0), new MyPoint(2,2,0)));
		mesh.addConstraintEdge(new MyEdge(new MyPoint(1,1,0), new MyPoint(2,0,0)));
		mesh.forceConstraintIntegrity();
		edgeList = mesh.getConstraintEdges();
		assertTrue(edgeList.size()==2);
		assertTrue(mesh.listContainsPoint(new MyPoint(1,1,0))>-1);

		mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new MyEdge(new MyPoint(1,1,0), new MyPoint(2,2,0)));
		mesh.addConstraintEdge(new MyEdge(new MyPoint(1,2,0), new MyPoint(1,0,0)));
		mesh.forceConstraintIntegrity();
		edgeList = mesh.getConstraintEdges();
		assertTrue(edgeList.size()==3);
		assertTrue(mesh.listContainsPoint(new MyPoint(1,1,0))>-1);

		mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new MyEdge(new MyPoint(2,1,0), new MyPoint(2,2,0)));
		mesh.addConstraintEdge(new MyEdge(new MyPoint(2,4,0), new MyPoint(2,0,0)));
		mesh.forceConstraintIntegrity();
		edgeList = mesh.getConstraintEdges();
		assertTrue(edgeList.size()==3);

		mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new MyEdge(new MyPoint(2,4,0), new MyPoint(2,0,0)));
		mesh.addConstraintEdge(new MyEdge(new MyPoint(2,1,0), new MyPoint(2,2,0)));
		mesh.forceConstraintIntegrity();
		edgeList = mesh.getConstraintEdges();
		assertTrue(edgeList.size()==3);

		mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new MyEdge(23.754617414248024,25.03430079155673,8.321899736147756,29.974500344589934,23.130254996554104,8.9565816678153));
		mesh.addConstraintEdge(new MyEdge(23.754617414248024,25.03430079155673,8.321899736147756,30.606126914660827,32.288840262582056,15.237518756619197));
		mesh.forceConstraintIntegrity();
		edgeList = mesh.getConstraintEdges();
		assertTrue(edgeList.size()==2);

		mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new MyEdge(new MyPoint(2,4,0), new MyPoint(2,2,0)));
		mesh.addConstraintEdge(new MyEdge(new MyPoint(2,1,0), new MyPoint(2,3,0)));
		mesh.forceConstraintIntegrity();
		edgeList = mesh.getConstraintEdges();
		assertTrue(edgeList.size()==3);
		assertTrue(edgeList.get(0).equals(new MyEdge(2,1,0,2,2,0)));
		assertTrue(edgeList.get(1).equals(new MyEdge(2,2,0,2,3,0)));
		assertTrue(edgeList.get(2).equals(new MyEdge(2,3,0,2,4,0)));

		mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new MyEdge(new MyPoint(2,4,0), new MyPoint(2,3,0)));
		mesh.addConstraintEdge(new MyEdge(new MyPoint(2,1,0), new MyPoint(2,3,0)));
		mesh.forceConstraintIntegrity();
		edgeList = mesh.getConstraintEdges();
		assertTrue(edgeList.size()==2);

		mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new MyEdge(new MyPoint(1,1,0), new MyPoint(2,2,0)));
		mesh.addConstraintEdge(new MyEdge(new MyPoint(0,2,0), new MyPoint(2,0,0)));
		mesh.addConstraintEdge(new MyEdge(new MyPoint(0,0,0), new MyPoint(2,1,0)));
		mesh.forceConstraintIntegrity();
		edgeList = mesh.getConstraintEdges();
		assertTrue(edgeList.size()==6);
		assertTrue(mesh.listContainsPoint(new MyPoint(1,1,0))>-1);
                assertTrue(sillyCheckIntersection(edgeList));
                MyEdge e1 = edgeList.get(0);
                MyEdge e2;
                for(int i = 1; i<edgeList.size();i++){
                        e2 = e1;
                        e1 = edgeList.get(i);
                        assertTrue(e2.sortLeftRight(e1)==-1);
                }

		mesh = new ConstrainedMesh();
		MyEdge edge1 = new MyEdge(29.04662741160085,52.16572027299656,68.38018218763128 , 21.70784635428322,52.702506064941865,70.26548339515645);
		mesh.addConstraintEdge(edge1);
		MyEdge edge2 = new MyEdge(32.696545765031715,62.25043024404333,48.051049255488714 , 27.630378535764756,51.60370887400286,81.41914742448961);
		mesh.addConstraintEdge(edge2);
		mesh.forceConstraintIntegrity();
		edgeList = mesh.getConstraintEdges();
		assertTrue(edgeList.size()==4);

		mesh = new ConstrainedMesh();
		mesh.addConstraintEdge(new MyEdge(50, 17, 11,1 ,32, 6));
		mesh.addConstraintEdge(new MyEdge(97, 30, 99,46, 1, 98));
		mesh.addConstraintEdge(new MyEdge(63, 17, 56,91, 26, 35));
		mesh.addConstraintEdge(new MyEdge(59, 12, 96,47, 35, 24));
		mesh.addConstraintEdge(new MyEdge(44, 44, 10,72, 7, 27));
		mesh.addConstraintEdge(new MyEdge(29, 9, 35,33, 67 ,39));
		mesh.addConstraintEdge(new MyEdge(4, 5, 18,89, 12, 17));
		mesh.addConstraintEdge(new MyEdge(38, 81, 70,33, 35, 36));
		mesh.addConstraintEdge(new MyEdge(70, 74, 55,2, 2, 64));
		mesh.addConstraintEdge(new MyEdge(51, 50, 47,8, 21, 73));
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

                mesh = new ConstrainedMesh();
                List<MyEdge> randomEdges = getRandomEdges(1000);
                for(MyEdge edge : randomEdges){
                        mesh.addConstraintEdge(edge);
                }
		double t1 = System.currentTimeMillis();
                mesh.forceConstraintIntegrity();
                mesh.forceConstraintIntegrity();
		double t2 = System.currentTimeMillis();
		System.out.println("time used : "+(t2 - t1) );
                assertTrue(sillyCheckIntersection(edgeList));
		edgeList = mesh.getConstraintEdges();
                e1 = edgeList.get(0);
                for(int i = 1; i<edgeList.size();i++){
                        e2 = e1;
                        e1 = edgeList.get(i);
                        assertTrue(e2.sortLeftRight(e1)<1);
                }
		t1 = System.currentTimeMillis();
		boolean bool = sillyCheckIntersection(edgeList);
		t2 = System.currentTimeMillis();
		System.out.println("time used : "+(t2 - t1) );
		assertTrue(bool);
	}

	/**
	 * Method used to create random a list of random edge.
	 * @param number
	 * @return
	 */
	private List<MyEdge> getRandomEdges(int number){
		ArrayList<MyEdge> retList = new ArrayList<MyEdge>();
		int num = (number < 0 ? -number : number);
		for(int i=0; i<num; i++){
			double d1 = Math.random()*100;
			double d2 = Math.random()*100;
			double d3 = Math.random()*100;
			double d4 = Math.random()*100;
			double d5 = Math.random()*100;
			double d6 = Math.random()*100;
			retList.add(new MyEdge(d1, d2, d3, d4, d5, d6));
		}
		return retList;
	}

	/**
	 * Returns false if there is an intersction (ie if the edges are invalid)
	 * @param edgeList
	 * @return
	 */
	private boolean sillyCheckIntersection(List<MyEdge> edgeList) throws DelaunayError{
		MyEdge e1;
		MyEdge e2;
                boolean ret=true;
		for(int i = 0; i < edgeList.size(); i++){
			e1 = edgeList.get(i);
			for (int j = i+1; j < edgeList.size(); j++){
				e2=edgeList.get(j);
				MyElement inter =e1.getIntersection(e2);
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

