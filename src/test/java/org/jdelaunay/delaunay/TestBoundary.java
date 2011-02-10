package org.jdelaunay.delaunay;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests dedicated to the testing of boundary.
 * @author alexis
 */
public class TestBoundary extends BaseUtility {

	/**
	 * Tests that the list of BoundaryPart can't be set to null.
	 */
	public void testSetNullBoundary(){
		Boundary bound = new Boundary();
		assertNotNull(bound.getBoundary());
		bound.setBoundary(null);
		assertNotNull(bound.getBoundary());
	}

	/**
	 * Get the eligible parts for a point that is lower than all the parts of
	 * the boundary
	 */
	public void testGetEligiblePartsLower() throws DelaunayError{
		Boundary bound = getExampleBoundary();
		List<Integer> indices = bound.getEligibleParts(new Point(8,-1,0));
		List<BoundaryPart> elig = bound.getBoundary();
		assertTrue(indices.size()==1);
		Edge ed = elig.get(indices.get(0)).getBoundaryEdges().get(0);
		assertTrue(ed.equals(new Edge(0,1,0,3,2,0)));
		assertNull(elig.get(indices.get(0)).getConstraint());
	}

	/**
	 * Get the eligible parts for the right point of the lowest constraint linked to
	 * this boundary.
	 */
	public void testGetEligiblePartsLowerBis() throws DelaunayError{
		Boundary bound = getExampleBoundary();
		List<Integer> indices = bound.getEligibleParts(new Point(9,0,0));
		List<BoundaryPart> elig = bound.getBoundary();
		assertTrue(indices.size()==2);
		Edge ed = elig.get(indices.get(0)).getBoundaryEdges().get(0);
		assertTrue(ed.equals(new Edge(0,1,0,3,2,0)));
		assertNull(elig.get(indices.get(0)).getConstraint());
		assertTrue(elig.get(indices.get(1)).getBoundaryEdges().get(0).equals(new Edge(3,2,0,5,3,0)));
		assertTrue(elig.get(indices.get(1)).getBoundaryEdges().get(1).equals(new Edge(5,3,0,6,5,0)));
		assertTrue(elig.get(indices.get(1)).getConstraint().equals(new Edge(3,2,0,9,0,0)));
	}

	/**
	 * Get the eligible parts for a point that is upper than all the parts
	 * of the boundary.
	 */
	public void testGetEligiblePartsUpper() throws DelaunayError{
		Boundary bound = getExampleBoundary();
		List<Integer> indices = bound.getEligibleParts(new Point(8,13,0));
		List<BoundaryPart> elig = bound.getBoundary();
		assertTrue(indices.size()==1);
		Edge ed = elig.get(indices.get(0)).getBoundaryEdges().get(0);
		assertTrue(ed.equals(new Edge(0,13,0,3,12,0)));
		assertTrue(elig.get(indices.get(0)).getConstraint().equals(new Edge(3,12,0,9,12,0)));
	}

	/**
	 * Get the eligible parts for a point that is upper than all the parts
	 * of the boundary.
	 */
	public void testGetEligiblePartsUpperBis() throws DelaunayError{
		Boundary bound = getExampleBoundary();
		List<Integer> indices = bound.getEligibleParts(new Point(9,12,0));
		List<BoundaryPart> elig = bound.getBoundary();
		assertTrue(indices.size()==4);
		Edge ed = elig.get(indices.get(0)).getBoundaryEdges().get(0);
		assertTrue(ed.equals(new Edge(7,7,0,6,10,0)));
		assertTrue(elig.get(indices.get(0)).getConstraint().equals(new Edge(7,7,0,10,9,0)));
		ed = elig.get(indices.get(1)).getBoundaryEdges().get(0);
		assertTrue(ed.equals(new Edge(6,10,0,5,11,0)));
		assertTrue(elig.get(indices.get(1)).getConstraint().equals(new Edge(6,10,0,9,12,0)));
		ed = elig.get(indices.get(2)).getBoundaryEdges().get(0);
		assertTrue(ed.equals(new Edge(5,11,0,3,12,0)));
		assertTrue(elig.get(indices.get(2)).getConstraint().equals(new Edge(5,11,0,9,12,0)));
		ed = elig.get(indices.get(3)).getBoundaryEdges().get(0);
		assertTrue(ed.equals(new Edge(0,13,0,3,12,0)));
		assertTrue(elig.get(indices.get(3)).getConstraint().equals(new Edge(3,12,0,9,12,0)));
	}

	/**
	 * Tests getEligiblePart between two BP that share the same left point.
	 * @throws DelaunayError
	 */
	public void testGetEligiblePartDegenerated() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		List<Integer> indices = bound.getEligibleParts(new Point(9,8,0));
		List<BoundaryPart> elig = bound.getBoundary();
		assertTrue(indices.size()==1);
		assertTrue(elig.get(indices.get(0)).getBoundaryEdges().isEmpty());
		assertTrue(elig.get(indices.get(0)).getConstraint().equals(new Edge(7,7,0,10,7,0)));
	}

	/**
	 * A simple try to retrieve a boundary part.
	 * @throws DelaunayError
	 */
	public void testGetEligiblePartSimple() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		List<Integer> indices = bound.getEligibleParts(new Point(9,6,0));
		List<BoundaryPart> elig = bound.getBoundary();
		assertTrue(indices.size()==1);
		assertTrue(elig.get(indices.get(0)).getBoundaryEdges().size()==1);
		assertTrue(elig.get(indices.get(0)).getBoundaryEdges().get(0).equals(new Edge(6,5,0,7,7,0)));
		assertTrue(elig.get(indices.get(0)).getConstraint().equals(new Edge(6,5,0,10,4,0)));
	}

	/**
	 * A test with a set of BP that have the same right point for their constraint.
	 */
	public void testGetEligiblePartCommonRightPoint() throws DelaunayError {
		Boundary bound = getExampleboundaryBis();
		List<Integer> indices = bound.getEligibleParts(new Point(7,7,0));
		List<BoundaryPart> elig = bound.getBoundary();
		assertTrue(indices.size()==5);
		//We test the BoundaryParts.
		assertTrue(elig.get(indices.get(0)).getBoundaryEdges().size()==1);
		assertTrue(elig.get(indices.get(0)).getBoundaryEdges().get(0).equals(new Edge(0,4,0,0,6,0)));
		assertTrue(elig.get(indices.get(0)).getConstraint().equals(new Edge(0,4,0,11,1,0)));
		//We test the BoundaryParts.
		assertTrue(elig.get(indices.get(1)).getBoundaryEdges().size()==1);
		assertTrue(elig.get(indices.get(1)).getBoundaryEdges().get(0).equals(new Edge(0,6,0,0,8,0)));
		assertTrue(elig.get(indices.get(1)).getConstraint().equals(new Edge(0,6,0,7,7,0)));
		//We test the BoundaryParts.
		assertTrue(elig.get(indices.get(2)).getBoundaryEdges().size()==1);
		assertTrue(elig.get(indices.get(2)).getBoundaryEdges().get(0).equals(new Edge(0,8,0,0,10,0)));
		assertTrue(elig.get(indices.get(2)).getConstraint().equals(new Edge(0,8,0,7,7,0)));
		//We test the BoundaryParts.
		assertTrue(elig.get(indices.get(3)).getBoundaryEdges().size()==1);
		assertTrue(elig.get(indices.get(3)).getBoundaryEdges().get(0).equals(new Edge(0,10,0,0,12,0)));
		assertTrue(elig.get(indices.get(3)).getConstraint().equals(new Edge(0,10,0,7,7,0)));
		//We test the BoundaryParts
		assertTrue(elig.get(indices.get(4)).getBoundaryEdges().size()==1);
		assertTrue(elig.get(indices.get(4)).getBoundaryEdges().get(0).equals(new Edge(0,12,0,0,14,0)));
		assertTrue(elig.get(indices.get(4)).getConstraint().equals(new Edge(0,12,0,7,7,0)));
		
	}

	/**
	 * Some other test to ensure a good code coverage. We don't want to let bugs
	 * behind us...
	 * @throws DelaunayError
	 */
	public void testGetEligiblePart() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		List<Integer> indices = bound.getEligibleParts(new Point(7,11,0));
		List<BoundaryPart> elig = bound.getBoundary();
		assertTrue(indices.size()==1);
		assertTrue(elig.get(indices.get(0)).getBoundaryEdges().size()==1);
		assertTrue(elig.get(indices.get(0)).getBoundaryEdges().get(0).equals(new Edge(6,10,0,5,11,0)));
		assertTrue(elig.get(indices.get(0)).getConstraint().equals(new Edge(6,10,0,9,12,0)));
		//Once again
		bound = getExampleBoundary();
		indices = bound.getEligibleParts(new Point(7,10,0));
		assertTrue(indices.size()==1);
		assertTrue(elig.get(indices.get(0)).getBoundaryEdges().size()==1);
		assertTrue(elig.get(indices.get(0)).getBoundaryEdges().get(0).equals(new Edge(6,10,0,7,7,0)));
		assertTrue(elig.get(indices.get(0)).getConstraint().equals(new Edge(7,7,0,10,9,0)));
		
	}

	public void testGetEligiblePartRightPoint() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		List<Integer> indices = bound.getEligibleParts(new Point(10,4,0));
		List<BoundaryPart> elig = bound.getBoundary();
		assertTrue(indices.size()==2);
		assertTrue(elig.get(indices.get(0)).getBoundaryEdges().size()==2);
		assertTrue(elig.get(indices.get(0)).getBoundaryEdges().get(0).equals(new Edge(3,2,0,5,3,0)));
		assertTrue(elig.get(indices.get(0)).getBoundaryEdges().get(1).equals(new Edge(5,3,0,6,5,0)));
		assertTrue(elig.get(indices.get(0)).getConstraint().equals(new Edge(3,2,0,9,0,0)));
		assertTrue(elig.get(indices.get(1)).getBoundaryEdges().size()==1);
		assertTrue(elig.get(indices.get(1)).getBoundaryEdges().get(0).equals(new Edge(6,5,0,7,7,0)));
		assertTrue(elig.get(indices.get(1)).getConstraint().equals(new Edge(6,5,0,10,4,0)));
		//Once again
		bound = getExampleBoundary();
		indices = bound.getEligibleParts(new Point(10,7,0));
		assertTrue(indices.size()==2);
		assertTrue(elig.get(indices.get(0)).getBoundaryEdges().size()==1);
		assertTrue(elig.get(indices.get(0)).getBoundaryEdges().get(0).equals(new Edge(6,5,0,7,7,0)));
		assertTrue(elig.get(indices.get(0)).getConstraint().equals(new Edge(6,5,0,10,4,0)));
		assertTrue(elig.get(indices.get(1)).getBoundaryEdges().isEmpty());
		assertTrue(elig.get(indices.get(1)).getConstraint().equals(new Edge(7,7,0,10,7,0)));

	}

	/**
	 * Insert a point that is flanked with two constraint edges , and that does not lie
	 * on a constraint.
	 * @throws DelaunayError
	 */
	public void testInsertFlankedPoint() throws DelaunayError{
		Boundary bound = getExampleBoundary();
		List<DelaunayTriangle> tri = bound.insertPoint(new Point(9,6,0));
		BoundaryPart bp = bound.getBoundary().get(2);
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(6,5,0,9,6,0), new Edge(9,6,0,7,7,0), new Edge(7,7,0,6,5,0))));
		assertTrue(tri.size()==1);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new Edge(6,5,0,9,6,0)));
		assertTrue(bp.getBoundaryEdges().get(1).equals(new Edge(9,6,0,7,7,0)));
		assertTrue(bp.getBoundaryEdges().size()==2);
		assertTrue(bp.getConstraint().equals(new Edge(6,5,0,10,4,0)));
	}

	/**
	 * Insert a point that is upper than all the constraint edges linked to the mesh.
	 * This point does not lie on any constraint.
	 * @throws DelaunayError
	 */
	public void testInsertUpperPoint() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		List<DelaunayTriangle> tri = bound.insertPoint(new Point(8,13,0));
		BoundaryPart bp = bound.getBoundary().get(7);
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(3,12,0,8,13,0), new Edge(8,13,0,0,13,0), new Edge(0,13,0,3,12,0))));
		assertTrue(tri.size()==1);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new Edge(3,12,0,8,13,0)));
		assertTrue(bp.getBoundaryEdges().get(1).equals(new Edge(8,13,0,0,13,0)));
		assertTrue(bp.getBoundaryEdges().size()==2);
		assertTrue(bp.getConstraint().equals(new Edge(3,12,0,9,12,0)));
	}

	/**
	 * Tests that we retrieve the good bad and added edges when inserting a point
	 * that is upper than all the constraints linked to the mesh.
	 * @throws DelaunayError
	 */
	public void testInsUpPtAddedAndBadEdges() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		bound.insertPoint(new Point(8,13,0));
		List<Edge> added = bound.getAddedEdges();
		List<Edge> bad = bound.getBadEdges();
		assertTrue(added.size()==2);
		assertTrue(added.contains(new Edge(0,13,0,8,13,0)));
		assertTrue(added.contains(new Edge(8,13,0,3,12,0)));
		assertTrue(bad.size()==1);
		assertTrue(bad.contains(new Edge(0,13,0,3,12,0)));
	}

	/**
	 * Insert a point that is upper than all the constraint edges linked to the mesh.
	 * This point does not lie on any constraint.
	 * @throws DelaunayError
	 */
	public void testInsertLowerPoint() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		List<DelaunayTriangle> tri = bound.insertPoint(new Point(8,-1,0));
		BoundaryPart bp = bound.getBoundary().get(0);
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(0,1,0,8,-1,0), new Edge(8,-1,0,3,2,0), new Edge(3,2,0,0,1,0))));
		assertTrue(tri.size()==1);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new Edge(0,1,0,8,-1,0)));
		assertTrue(bp.getBoundaryEdges().get(1).equals(new Edge(8,-1,0,3,2,0)));
		assertTrue(bp.getBoundaryEdges().size()==2);
		assertNull(bp.getConstraint());
	}

	/**
	 * Tests that we retrieve the good bad and added edges when inserting a point
	 * that is lower than all the constraints linked to the mesh.
	 * @throws DelaunayError
	 */
	public void testInsLowPtAddedAndBadEdges() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		bound.insertPoint(new Point(8,-1,0));
		List<Edge> added = bound.getAddedEdges();
		assertTrue(added.size()==2);
		assertTrue(added.contains(new Edge(0,1,0,8,-1,0)));
		assertTrue(added.contains(new Edge(3,2,0,8,-1,0)));
		List<Edge> bad = bound.getBadEdges();
		assertTrue(bad.size()==1);
		assertTrue(bad.contains(new Edge(0,1,0,3,2,0)));
	}

	/**
	 * insert a point that must create a degenerated edge.
	 * @throws DelaunayError
	 */
	public void testInsertPointDegenerated() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		List<DelaunayTriangle> tri = bound.insertPoint(new Point(9,8,0));
		BoundaryPart bp = bound.getBoundary().get(3);
		assertTrue(tri.isEmpty());
		assertTrue(bp.getBoundaryEdges().get(0).equals(new Edge(7,7,0,9,8,0)));
		assertTrue(bp.getBoundaryEdges().get(0).isDegenerated());
		assertTrue(bp.getBoundaryEdges().size()==1);
		assertTrue(bp.getConstraint().equals(new Edge(7,7,0,10,7,0)));
	}

	/**
	 * tests that we retrieve the good bad and added edges when inserting a point
	 * that create a single degenerated edge.
	 * @throws DelaunayError
	 */
	public void testInsPtDegenAddedAndBadEdges() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		bound.insertPoint(new Point(9,8,0));
		List<Edge> added = bound.getAddedEdges();
		assertTrue(added.size()==1);
		assertTrue(added.get(0).equals(new Edge(7,7,0,9,8,0)));
		List<Edge> bad = bound.getBadEdges();
		assertTrue(bad.isEmpty());
	}

	/**
	 * Tests the insertion of the point when more than one edges of the boundary
	 * are affected.
	 * @throws DelaunayError
	 */
	public void testInsertTwoTriangles() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		List<DelaunayTriangle> tri = bound.insertPoint(new Point(7,3,0));
		assertTrue(tri.size()==2);
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(3,2,0,7,3,0), new Edge(7,3,0,5,3,0),  new Edge(5,3,0,3,2,0))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(6,5,0,7,3,0), new Edge(7,3,0,5,3,0),  new Edge(5,3,0,6,5,0))));
		List<Edge> added = bound.getAddedEdges();
		List<Edge> bad = bound.getBadEdges();
		assertTrue(added.size()==3);
		assertTrue(added.contains(new Edge(3,2,0,7,3,0)));
		assertTrue(added.contains(new Edge(5,3,0,7,3,0)));
		assertTrue(added.contains(new Edge(6,5,0,7,3,0)));
		assertTrue(bad.size()==2);
		assertTrue(bad.contains(new Edge(3,2,0,5,3,0)));
		assertTrue(bad.contains(new Edge(6,5,0,5,3,0)));

	}

	/**
	 * This (complete) test checks that we insert correctly the right points
	 * of the constraints.
	 * @throws DelaunayError
	 */
	public void testInsertConstraintRightPoint() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		List<DelaunayTriangle> tri = bound.insertPoint(new Point(10,4,0));
		//we test the added triangles.
		assertTrue(tri.size()==3);
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(3,2,0,10,4,0), new Edge(10,4,0,5,3,0), new Edge(5,3,0,3,2,0))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(6,5,0,10,4,0), new Edge(10,4,0,5,3,0), new Edge(5,3,0,6,5,0))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(6,5,0,10,4,0), new Edge(10,4,0,7,7,0), new Edge(7,7,0,6,5,0))));
		//we check that the constraint contained in some triangles is not a duplicated edge.
		int index  = tri.indexOf(new DelaunayTriangle(new Edge(6,5,0,10,4,0), new Edge(10,4,0,5,3,0), new Edge(5,3,0,6,5,0)));
		Edge ed = tri.get(index).getOppositeEdge(new Point(5,3,0));
		assertTrue(ed.isLocked());
		index  = tri.indexOf(new DelaunayTriangle(new Edge(6,5,0,10,4,0), new Edge(10,4,0,7,7,0), new Edge(7,7,0,6,5,0)));
		ed = tri.get(index).getOppositeEdge(new Point(7,7,0));
		assertTrue(ed.isLocked());
		List<Edge> added = bound.getAddedEdges();
		List<Edge> bad = bound.getBadEdges();
		//we check the added edges.
		assertTrue(added.size()==4);
		assertTrue(added.contains(new Edge(3,2,0,10,4,0)));
		assertTrue(added.contains(new Edge(5,3,0,10,4,0)));
		assertTrue(added.contains(new Edge(6,5,0,10,4,0)));
		assertTrue(added.contains(new Edge(7,7,0,10,4,0)));
		//we check that the added edge that is a constraint is not a duplicate.
		index = added.indexOf(new Edge(6,5,0,10,4,0));
		ed = added.get(index);
		assertTrue(ed.isLocked());
		//we check the badEdges.
		assertTrue(bad.size()==3);
		assertTrue(bad.contains(new Edge(3,2,0,5,3,0)));
		assertTrue(bad.contains(new Edge(5,3,0,6,5,0)));
		assertTrue(bad.contains(new Edge(6,5,0,7,7,0)));
		//We check the boundary state.
		assertTrue(bound.getBoundary().size()==7);
		BoundaryPart bp = bound.getBoundary().get(1);
		assertTrue(bp.getConstraint().equals(new Edge(3,2,0,9,0,0)));

	}

	/**
	 * This test performs an insertion of a point that is the right point of
	 * many constraints.
	 * @throws DelaunayError
	 */
	public void testInsertCstrRightPtManyBP() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		List<DelaunayTriangle> tri = bound.insertPoint(new Point(9,12,0));
		//we test the added triangles.
		assertTrue(tri.size()==4);
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(7,7,0,9,12,0), new Edge(9,12,0,6,10,0), new Edge(6,10,0,7,7,0))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(5,11,0,9,12,0), new Edge(9,12,0,6,10,0), new Edge(6,10,0,5,11,0))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(5,11,0,9,12,0), new Edge(9,12,0,3,12,0), new Edge(3,12,0,5,11,0))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(0,13,0,9,12,0), new Edge(9,12,0,3,12,0), new Edge(3,12,0,0,13,0))));
		//we check that the constraints contained in some triangles are not  duplicated edges.
		int index  = tri.indexOf(new DelaunayTriangle(new Edge(7,7,0,9,12,0), new Edge(9,12,0,6,10,0), new Edge(6,10,0,7,7,0)));
		Edge ed = tri.get(index).getOppositeEdge(new Point(7,7,0));
		assertTrue(ed.isLocked());
		index  = tri.indexOf(new DelaunayTriangle(new Edge(5,11,0,9,12,0), new Edge(9,12,0,6,10,0), new Edge(6,10,0,5,11,0)));
		ed = tri.get(index).getOppositeEdge(new Point(6,10,0));
		assertTrue(ed.isLocked());
		index  = tri.indexOf(new DelaunayTriangle(new Edge(5,11,0,9,12,0), new Edge(9,12,0,3,12,0), new Edge(3,12,0,5,11,0)));
		ed = tri.get(index).getOppositeEdge(new Point(5,11,0));
		assertTrue(ed.isLocked());
		index  = tri.indexOf(new DelaunayTriangle(new Edge(0,13,0,9,12,0), new Edge(9,12,0,3,12,0), new Edge(3,12,0,0,13,0)));
		ed = tri.get(index).getOppositeEdge(new Point(0,13,0));
		assertTrue(ed.isLocked());
		List<Edge> added = bound.getAddedEdges();
		List<Edge> bad = bound.getBadEdges();
		//we check the added edges.
		assertTrue(added.size()==5);
		assertTrue(added.contains(new Edge(6,10,0,9,12,0)));
		assertTrue(added.contains(new Edge(7,7,0,9,12,0)));
		assertTrue(added.contains(new Edge(5,11,0,9,12,0)));
		assertTrue(added.contains(new Edge(3,12,0,9,12,0)));
		assertTrue(added.contains(new Edge(0,13,0,9,12,0)));
		//we check the bad edges
		assertTrue(bad.size()==4);
		assertTrue(bad.contains(new Edge(7,7,0,6,10,0)));
		assertTrue(bad.contains(new Edge(5,11,0,6,10,0)));
		assertTrue(bad.contains(new Edge(5,11,0,3,12,0)));
		assertTrue(bad.contains(new Edge(0,13,0,3,12,0)));
		//And we check the whole boundary
		assertTrue(bound.getBoundary().size()==5);
		BoundaryPart bp = bound.getBoundary().get(4);
		assertTrue(bp.getConstraint().equals(new Edge(7,7,0,10,9,0)));
		assertTrue(bp.getBoundaryEdges().get(0).equals(new Edge(7,7,0,9,12,0)));
		assertTrue(bp.getBoundaryEdges().get(0).getStartPoint().equals(new Point(7,7,0)));
		assertTrue(bp.getBoundaryEdges().get(1).equals(new Edge(0,13,0,9,12,0)));
		assertTrue(bp.getBoundaryEdges().get(1).getStartPoint().equals(new Point(9,12,0)));
	}

	/**
	 * We add the right point of the lowest constraint and check that everything's ok.
	 * @throws DelaunayError
	 */
	public void testAddRightPtLowestCstr() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		List<DelaunayTriangle> tri = bound.insertPoint(new Point(9,0,0));
		//we test the added triangles.
		assertTrue(tri.size()==3);
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(0,1,0,9,0,0), new Edge(9,0,0,3,2,0), new Edge(3,2,0,0,1,0))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(5,3,0,9,0,0), new Edge(9,0,0,3,2,0), new Edge(3,2,0,5,3,0))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(5,3,0,9,0,0), new Edge(9,0,0,6,5,0), new Edge(6,5,0,5,3,0))));
		List<Edge> added = bound.getAddedEdges();
		List<Edge> bad = bound.getBadEdges();
		//we check the added edges.
		assertTrue(added.size()==4);
		assertTrue(added.contains(new Edge(0,1,0,9,0,0)));
		assertTrue(added.contains(new Edge(3,2,0,9,0,0)));
		assertTrue(added.contains(new Edge(5,3,0,9,0,0)));
		assertTrue(added.contains(new Edge(6,5,0,9,0,0)));
		//we check the bad edges
		assertTrue(bad.size()==3);
		assertTrue(bad.contains(new Edge(0,1,0,3,2,0)));
		assertTrue(bad.contains(new Edge(5,3,0,3,2,0)));
		assertTrue(bad.contains(new Edge(5,3,0,6,5,0)));
		//And we check the whole boundary
		assertTrue(bound.getBoundary().size()==7);
		BoundaryPart bp = bound.getBoundary().get(0);
		assertNull(bp.getConstraint());
		assertTrue(bp.getBoundaryEdges().size()==2);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new Edge(0,1,0,9,0,0)));
		assertTrue(bp.getBoundaryEdges().get(1).equals(new Edge(6,5,0,9,0,0)));
	}

	/**
	 * checks that we manage properly the case where uppest boundary part does not
	 * contain any boundary Edge.
	 * @throws DelaunayError
	 */
	public void testInsertRightPtDegenUppestCstr() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		assertTrue(bound.getBoundary().size()==8);
		List<DelaunayTriangle> tri = bound.insertPoint(new Point(10,7,0));
		//we test the added triangles.
		assertTrue(tri.size()==1);
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(6,5,0,10,7,0), new Edge(10,7,0,7,7,0), new Edge(7,7,0,6,5,0))));
		List<Edge> added = bound.getAddedEdges();
		List<Edge> bad = bound.getBadEdges();
		//we check the added edges.
		assertTrue(added.size()==2);
		assertTrue(added.contains(new Edge(6,5,0,10,7,0)));
		assertTrue(added.contains(new Edge(7,7,0,10,7,0)));
		//we check the bad edges
		assertTrue(bad.size()==1);
		assertTrue(bad.contains(new Edge(6,5,0,7,7,0)));
		//And we check the whole boundary
		assertTrue(bound.getBoundary().size()==7);
		BoundaryPart bp = bound.getBoundary().get(2);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new Edge(6,5,0,10,7,0)));
		assertTrue(bp.getBoundaryEdges().get(1).equals(new Edge(7,7,0,10,7,0)));
		assertTrue(bp.getBoundaryEdges().get(1).isLocked());
		assertFalse(bp.getBoundaryEdges().get(1).isDegenerated());
	}

	/**
	 * Tests that the property of a constraints are well kept when adding its
	 * right point to the mesh, when the insertion create (or rather use, as we 
	 * don't create any duplicate edges in the mesh) a degenerated (and locked)
	 * edge.
	 * @throws DelaunayError
	 */
	public void testDegeneratedConstraint() throws DelaunayError {
		List<BoundaryPart> bpl = new ArrayList<BoundaryPart>();
		BoundaryPart bp;
		Edge cstr;
		List<Edge> boundaryEdges;
		Boundary bound = new Boundary();
		//We fill a boundary part, and put it in bpl
		cstr = null;
		boundaryEdges = new ArrayList<Edge>();
		boundaryEdges.add(new Edge(0,0,0,1,3,0));
		bp = new BoundaryPart(boundaryEdges);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new Edge(1,3,0,6,1,0);
		cstr.setLocked(true);
		boundaryEdges = new ArrayList<Edge>();
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new Edge(1,3,0,5,4,0);
		cstr.setLocked(true);
		boundaryEdges = new ArrayList<Edge>();
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new Edge(1,3,0,6,6,0);
		cstr.setLocked(true);
		boundaryEdges = new ArrayList<Edge>();
		boundaryEdges.add(new Edge(1,3,0,0,6,0));
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		bound.setBoundary(bpl);
		//We insert the point
		List<DelaunayTriangle> tri = bound.insertPoint(new Point(5,4,0));
		assertTrue(tri.isEmpty());
		assertTrue(bound.getBoundary().size()==3);
		assertTrue(bound.getBoundary().get(1).getConstraint().equals(new Edge(1,3,0,6,1,0)));
		assertTrue(bound.getBoundary().get(1).getBoundaryEdges().get(0).equals(new Edge(1,3,0,5,4,0)));
		assertTrue(bound.getBoundary().get(1).getBoundaryEdges().get(0).isLocked());
		assertTrue(bound.getBoundary().get(1).getBoundaryEdges().get(0).isDegenerated());
		
	}

	public void testInsertRightPtDegenLowestCstr() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		assertTrue(bound.getBoundary().size()==8);
		List<DelaunayTriangle> tri = bound.insertPoint(new Point(10,9,0));
		//we test the added triangles.
		assertTrue(tri.size()==1);
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(6,10,0,10,9,0), new Edge(10,9,0,7,7,0), new Edge(7,7,0,6,10,0))));
		List<Edge> added = bound.getAddedEdges();
		List<Edge> bad = bound.getBadEdges();
		//we check the added edges.
		assertTrue(added.size()==2);
		assertTrue(added.contains(new Edge(6,10,0,10,9,0)));
		assertTrue(added.contains(new Edge(7,7,0,10,9,0)));
		//we check the bad edges
		assertTrue(bad.size()==1);
		assertTrue(bad.contains(new Edge(6,10,0,7,7,0)));
		//And we check the whole boundary
		assertTrue(bound.getBoundary().size()==7);
		BoundaryPart bp = bound.getBoundary().get(3);
		assertTrue(bp.getBoundaryEdges().get(1).equals(new Edge(6,10,0,10,9,0)));
		assertTrue(bp.getBoundaryEdges().get(0).equals(new Edge(7,7,0,10,9,0)));
		assertTrue(bp.getBoundaryEdges().get(0).isLocked());
		assertFalse(bp.getBoundaryEdges().get(0).isDegenerated());

	}

	/**
	 * Performs an insertion with a simple split.
	 * @throws DelaunayError
	 */
	public void testInsertAndSplit() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		List<Edge> cstrList = new ArrayList<Edge>();
		cstrList.add(new Edge(7,3,0,12,2,0));
		List<DelaunayTriangle> tri = bound.insertPoint(new Point(7,3,0), cstrList);
		assertTrue(tri.size()==2);
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(3,2,0,7,3,0), new Edge(7,3,0,5,3,0),  new Edge(5,3,0,3,2,0))));
		assertTrue(tri.contains(new DelaunayTriangle(new Edge(6,5,0,7,3,0), new Edge(7,3,0,5,3,0),  new Edge(5,3,0,6,5,0))));
		List<Edge> added = bound.getAddedEdges();
		List<Edge> bad = bound.getBadEdges();
		//we check the added edges.
		assertTrue(added.size()==3);
		assertTrue(added.contains(new Edge(3,2,0,7,3,0)));
		assertTrue(added.contains(new Edge(5,3,0,7,3,0)));
		assertTrue(added.contains(new Edge(6,5,0,7,3,0)));
		//we check the bad edges
		assertTrue(bad.size()==2);
		assertTrue(bad.contains(new Edge(3,2,0,5,3,0)));
		assertTrue(bad.contains(new Edge(6,5,0,5,3,0)));
		//And we check the whole boundary
		assertTrue(bound.getBoundary().size()==9);
		BoundaryPart bp = bound.getBoundary().get(1);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new Edge(3,2,0,7,3,0)));
		assertTrue(bp.getConstraint().equals(new Edge(3,2,0,9,0,0)));
		bp = bound.getBoundary().get(2);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new Edge(7,3,0,6,5,0)));
		assertTrue(bp.getConstraint().equals(new Edge(7,3,0,12,2,0)));
		bp = bound.getBoundary().get(3);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new Edge(6,5,0,7,7,0)));
		assertTrue(bp.getConstraint().equals(new Edge(6,5,0,10,4,0)));
		
	}

	/**
	 * test a split performed with degen edges
	 * @throws DelaunayError
	 */
	public void testInsertDegenEdgesSplit() throws DelaunayError {
		Boundary bound = getExampleDegenEdges();
		List<Edge> cstrList = new ArrayList<Edge>();
		cstrList.add(new Edge(6,4,0,8,4,0));
		List<DelaunayTriangle> tri = bound.insertPoint(new Point(6,4,0), cstrList);
		List<Edge> added = bound.getAddedEdges();
		List<Edge> bad = bound.getBadEdges();
		//we test the added triangles.
		assertTrue(tri.isEmpty());
		//we check the bad edges
		assertTrue(bad.isEmpty());
		//we check the added edges.
		assertTrue(added.size()==1);
		assertTrue(added.get(0).equals(new Edge(4,4,0,6,4,0)));
		//And we check the whole boundary
		assertTrue(bound.getBoundary().size()==3);
		BoundaryPart bp = bound.getBoundary().get(0);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new Edge(0,4,0,2,4,0)));
		assertTrue(bp.getBoundaryEdges().get(1).equals(new Edge(4,4,0,2,4,0)));
		assertTrue(bp.getBoundaryEdges().get(2).equals(new Edge(4,4,0,6,4,0)));
		assertTrue(bp.getConstraint().equals(new Edge(0,4,0,8,0,0)));
		bp = bound.getBoundary().get(1);
		assertTrue(bp.getBoundaryEdges().get(2).equals(new Edge(0,4,0,2,4,0)));
		assertTrue(bp.getBoundaryEdges().get(1).equals(new Edge(4,4,0,2,4,0)));
		assertTrue(bp.getBoundaryEdges().get(0).equals(new Edge(4,4,0,6,4,0)));
		assertTrue(bp.getConstraint().equals(new Edge(6,4,0,8,4,0)));
	}

	/**
	 * This test is built on the same scheme than testInsertDegenEdgesSplit, but
	 * we go one step further by connecting another point to the mesh
	 * @throws DelaunayError
	 */
	public void testInsertDESplitNextStep() throws DelaunayError {
		Boundary bound = getExampleDegenEdges();
		List<Edge> cstrList = new ArrayList<Edge>();
		cstrList.add(new Edge(6,4,0,8,4,0));
		List<DelaunayTriangle> tri = bound.insertPoint(new Point(6,4,0), cstrList);
		tri = bound.insertPoint(new Point(6,6,0));
		//we test the added triangles.
		assertTrue(tri.size()==3);

	}

	/**
	 * A more complete example, with many degenerated edges.
	 * @throws DelaunayError
	 */
	public void testManySharedEdges() throws DelaunayError {
		Edge cstr = new Edge(0,13,0,19,0,0);
		BoundaryPart bp = new BoundaryPart(cstr);
		Boundary bound = new Boundary();
		List<BoundaryPart> bpl = new ArrayList<BoundaryPart>();
		List<Edge> cstrList;
		bpl.add(bp);
		bound.setBoundary(bpl);
		//3,13,0   19,2,0
		Point ptToAdd = new Point(3,13,0);
		cstrList = new ArrayList<Edge>();
		cstrList.add(new Edge(3,13,0,19,2,0));
		bound.insertPoint(ptToAdd, cstrList);
		assertTrue(bound.getBoundary().size()==2);
		//6,13,0   19,4,0
		ptToAdd = new Point(6,13,0);
		cstrList = new ArrayList<Edge>();
		cstrList.add(new Edge(6,13,0,19,4,0));
		bound.insertPoint(ptToAdd, cstrList);
		//9,13,0   19,6,0
		ptToAdd = new Point(9,13,0);
		cstrList = new ArrayList<Edge>();
		cstrList.add(new Edge(9,13,0,19,6,0));
		bound.insertPoint(ptToAdd, cstrList);
		//12,13,0
		ptToAdd = new Point(12,13,0);
		cstrList = new ArrayList<Edge>();
		bound.insertPoint(ptToAdd, cstrList);
		//15,13,0
		ptToAdd = new Point(15,13,0);
		cstrList = new ArrayList<Edge>();
		bound.insertPoint(ptToAdd, cstrList);
		assertTrue(bound.getBoundary().size()==4);
		assertTrue(bound.getAddedEdges().contains(new Edge(12,13,0,15,13,0)));
		/***************************************
		 * And now we can perform our tests.
		 **************************************/
		//On the first BP in the boundary
		bp = bound.getBoundary().get(0);
		assertTrue(bp.getBoundaryEdges().size()==1);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new Edge(0,13,0,3,13,0)));
		assertTrue(bp.getBoundaryEdges().get(0).isShared());
		//On the second BP in the boundary
		bp = bound.getBoundary().get(1);
		assertTrue(bp.getBoundaryEdges().size()==1);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new Edge(6,13,0,3,13,0)));
		assertTrue(bp.getBoundaryEdges().get(0).isShared());
		//On the third BP in the boundary
		bp = bound.getBoundary().get(2);
		assertTrue(bp.getBoundaryEdges().size()==1);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new Edge(6,13,0,9,13,0)));
		assertTrue(bp.getBoundaryEdges().get(0).isShared());
		//On the third BP in the boundary
		bp = bound.getBoundary().get(3);
		assertTrue(bp.getBoundaryEdges().size()==5);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new Edge(12,13,0,9,13,0)));
		assertTrue(bp.getBoundaryEdges().get(0).isDegenerated());
		assertTrue(bp.getBoundaryEdges().get(1).equals(new Edge(12,13,0,15,13,0)));
		assertTrue(bp.getBoundaryEdges().get(1).isDegenerated());
		assertTrue(bp.getBoundaryEdges().get(2).equals(new Edge(6,13,0,9,13,0)));
		assertTrue(bp.getBoundaryEdges().get(2).isShared());
		assertTrue(bp.getBoundaryEdges().get(3).equals(new Edge(6,13,0,3,13,0)));
		assertTrue(bp.getBoundaryEdges().get(3).isShared());
		assertTrue(bp.getBoundaryEdges().get(4).equals(new Edge(0,13,0,3,13,0)));
		assertTrue(bp.getBoundaryEdges().get(4).isShared());
	}

	/**
	 * A more complete example, with many degenerated edges. we insert a point.
	 * @throws DelaunayError
	 */
	public void testManySharedEdgesInsertion() throws DelaunayError {
		Edge cstr = new Edge(0,13,0,19,0,0);
		BoundaryPart bp = new BoundaryPart(cstr);
		Boundary bound = new Boundary();
		List<BoundaryPart> bpl = new ArrayList<BoundaryPart>();
		List<Edge> cstrList;
		bpl.add(bp);
		bound.setBoundary(bpl);
		//3,13,0   19,2,0
		Point ptToAdd = new Point(3,13,0);
		cstrList = new ArrayList<Edge>();
		cstrList.add(new Edge(3,13,0,19,2,0));
		bound.insertPoint(ptToAdd, cstrList);
		assertTrue(bound.getBoundary().size()==2);
		//6,13,0   19,4,0
		ptToAdd = new Point(6,13,0);
		cstrList = new ArrayList<Edge>();
		cstrList.add(new Edge(6,13,0,19,4,0));
		bound.insertPoint(ptToAdd, cstrList);
		//9,13,0   19,6,0
		ptToAdd = new Point(9,13,0);
		cstrList = new ArrayList<Edge>();
		cstrList.add(new Edge(9,13,0,19,6,0));
		bound.insertPoint(ptToAdd, cstrList);
		//12,13,0
		ptToAdd = new Point(12,13,0);
		cstrList = new ArrayList<Edge>();
		bound.insertPoint(ptToAdd, cstrList);
		//15,13,0
		ptToAdd = new Point(15,13,0);
		cstrList = new ArrayList<Edge>();
		bound.insertPoint(ptToAdd, cstrList);
		assertTrue(bound.getBoundary().size()==4);
		assertTrue(bound.getAddedEdges().contains(new Edge(12,13,0,15,13,0)));
		//We insert a point.
		List<DelaunayTriangle> tri = bound.insertPoint(new Point(16,16,0));
		/***************************************
		 * And now we can perform our tests.
		 **************************************/
		 assertTrue(tri.size()==5);
		 assertTrue(tri.contains(new DelaunayTriangle(new Edge(12,13,0,15,13,0), new Edge(15,13,0,16,16,0), new Edge(16,16,0,12,13,0))));
		 assertTrue(tri.contains(new DelaunayTriangle(new Edge(12,13,0,9,13,0), new Edge(9,13,0,16,16,0), new Edge(16,16,0,12,13,0))));
		 assertTrue(tri.contains(new DelaunayTriangle(new Edge(6,13,0,9,13,0), new Edge(9,13,0,16,16,0), new Edge(16,16,0,6,13,0))));
		 assertTrue(tri.contains(new DelaunayTriangle(new Edge(6,13,0,3,13,0), new Edge(3,13,0,16,16,0), new Edge(16,16,0,6,13,0))));
		 assertTrue(tri.contains(new DelaunayTriangle(new Edge(0,13,0,3,13,0), new Edge(3,13,0,16,16,0), new Edge(16,16,0,0,13,0))));
		 for(BoundaryPart bopa : bound.getBoundary()){
			 assertFalse(bopa.getBoundaryEdges().get(0).isShared());
		 }
		 bp = bound.getBoundary().get(3);
		 assertTrue(bp.getBoundaryEdges().size()==4);
		 assertTrue(bp.getBoundaryEdges().get(0).equals(new Edge(9,13,0,12,13,0)));
		 assertTrue(bp.getBoundaryEdges().get(0).getStartPoint().equals(new Point(9,13,0)));
		 assertTrue(bp.getBoundaryEdges().get(1).equals(new Edge(15,13,0,12,13,0)));
		 assertTrue(bp.getBoundaryEdges().get(1).getStartPoint().equals(new Point(12,13,0)));
		 assertTrue(bp.getBoundaryEdges().get(2).equals(new Edge(15,13,0,16,16,0)));
		 assertTrue(bp.getBoundaryEdges().get(2).getStartPoint().equals(new Point(15,13,0)));
		 assertTrue(bp.getBoundaryEdges().get(3).equals(new Edge(0,13,0,16,16,0)));
		 assertTrue(bp.getBoundaryEdges().get(3).getStartPoint().equals(new Point(16,16,0)));
		 assertTrue(bp.getAddedEdges().size()==6);
		 assertTrue(bp.getAddedEdges().contains(new Edge(15,13,0,16,16,0)));
		 assertTrue(bp.getAddedEdges().contains(new Edge(12,13,0,16,16,0)));
		 assertTrue(bp.getAddedEdges().contains(new Edge(9,13,0,16,16,0)));
		 assertTrue(bp.getAddedEdges().contains(new Edge(6,13,0,16,16,0)));
		 assertTrue(bp.getAddedEdges().contains(new Edge(3,13,0,16,16,0)));
		 assertTrue(bp.getAddedEdges().contains(new Edge(0,13,0,16,16,0)));
		
	}

	/**
	 * A more complete example, with many degenerated edges. we insert a point.
	 * @throws DelaunayError
	 */
	public void testManySharedEdgesInsertionBis() throws DelaunayError {
		Edge cstr = new Edge(0,13,0,19,0,0);
		BoundaryPart bp = new BoundaryPart(cstr);
		Boundary bound = new Boundary();
		List<BoundaryPart> bpl = new ArrayList<BoundaryPart>();
		List<Edge> cstrList;
		bpl.add(bp);
		bound.setBoundary(bpl);
		//3,13,0   19,2,0
		Point ptToAdd = new Point(3,13,0);
		cstrList = new ArrayList<Edge>();
		cstrList.add(new Edge(3,13,0,19,2,0));
		bound.insertPoint(ptToAdd, cstrList);
		assertTrue(bound.getBoundary().size()==2);
		//6,13,0   19,4,0
		ptToAdd = new Point(6,13,0);
		cstrList = new ArrayList<Edge>();
		cstrList.add(new Edge(6,13,0,19,4,0));
		bound.insertPoint(ptToAdd, cstrList);
		//9,13,0   19,6,0
		ptToAdd = new Point(9,13,0);
		cstrList = new ArrayList<Edge>();
		cstrList.add(new Edge(9,13,0,19,6,0));
		bound.insertPoint(ptToAdd, cstrList);
		//12,13,0
		ptToAdd = new Point(12,13,0);
		cstrList = new ArrayList<Edge>();
		bound.insertPoint(ptToAdd, cstrList);
		//15,13,0
		ptToAdd = new Point(15,13,0);
		cstrList = new ArrayList<Edge>();
		bound.insertPoint(ptToAdd, cstrList);
		assertTrue(bound.getBoundary().size()==4);
		assertTrue(bound.getAddedEdges().contains(new Edge(12,13,0,15,13,0)));
		//We insert a point.
		List<DelaunayTriangle> tri = bound.insertPoint(new Point(16,5,0));
		bp = bound.getBoundary().get(1);
		assertTrue(bp.getBoundaryEdges().size()==2);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new Edge(3,13,0,16,5,0)));
		assertTrue(bp.getBoundaryEdges().get(0).getStartPoint().equals(new Point(3,13,0)));
		assertTrue(bp.getBoundaryEdges().get(1).equals(new Edge(6,13,0,16,5,0)));
		assertTrue(bp.getBoundaryEdges().get(1).getStartPoint().equals(new Point(16,5,0)));
		bp = bound.getBoundary().get(3);
		assertFalse(bp.getBoundaryEdges().get(3).isShared());
		//We insert another point 
		tri = bound.insertPoint(new Point(16,16,0));
		/***************************************
		 * And now we can perform our tests.
		 **************************************/
		 assertTrue(tri.size()==5);
		 assertTrue(tri.contains(new DelaunayTriangle(new Edge(12,13,0,15,13,0), new Edge(15,13,0,16,16,0), new Edge(16,16,0,12,13,0))));
		 assertTrue(tri.contains(new DelaunayTriangle(new Edge(12,13,0,9,13,0), new Edge(9,13,0,16,16,0), new Edge(16,16,0,12,13,0))));
		 assertTrue(tri.contains(new DelaunayTriangle(new Edge(6,13,0,9,13,0), new Edge(9,13,0,16,16,0), new Edge(16,16,0,6,13,0))));
		 assertTrue(tri.contains(new DelaunayTriangle(new Edge(6,13,0,3,13,0), new Edge(3,13,0,16,16,0), new Edge(16,16,0,6,13,0))));
		 assertTrue(tri.contains(new DelaunayTriangle(new Edge(0,13,0,3,13,0), new Edge(3,13,0,16,16,0), new Edge(16,16,0,0,13,0))));
		 for(BoundaryPart bopa : bound.getBoundary()){
			 assertFalse(bopa.getBoundaryEdges().get(0).isShared());
		 }
		 bp = bound.getBoundary().get(3);
		 assertTrue(bp.getBoundaryEdges().size()==4);
		 assertTrue(bp.getBoundaryEdges().get(0).equals(new Edge(9,13,0,12,13,0)));
		 assertTrue(bp.getBoundaryEdges().get(0).getStartPoint().equals(new Point(9,13,0)));
		 assertTrue(bp.getBoundaryEdges().get(1).equals(new Edge(15,13,0,12,13,0)));
		 assertTrue(bp.getBoundaryEdges().get(1).getStartPoint().equals(new Point(12,13,0)));
		 assertTrue(bp.getBoundaryEdges().get(2).equals(new Edge(15,13,0,16,16,0)));
		 assertTrue(bp.getBoundaryEdges().get(2).getStartPoint().equals(new Point(15,13,0)));
		 assertTrue(bp.getBoundaryEdges().get(3).equals(new Edge(0,13,0,16,16,0)));
		 assertTrue(bp.getBoundaryEdges().get(3).getStartPoint().equals(new Point(16,16,0)));
		 assertTrue(bp.getAddedEdges().size()==6);
		 assertTrue(bp.getAddedEdges().contains(new Edge(15,13,0,16,16,0)));
		 assertTrue(bp.getAddedEdges().contains(new Edge(12,13,0,16,16,0)));
		 assertTrue(bp.getAddedEdges().contains(new Edge(9,13,0,16,16,0)));
		 assertTrue(bp.getAddedEdges().contains(new Edge(6,13,0,16,16,0)));
		 assertTrue(bp.getAddedEdges().contains(new Edge(3,13,0,16,16,0)));
		 assertTrue(bp.getAddedEdges().contains(new Edge(0,13,0,16,16,0)));
		 assertTrue(bp.getBadEdges().size()==1);
	}

	public void testBuildFirstBoundary() throws DelaunayError {
		Edge constr = new Edge(3,0,0,3,6,0);
		constr.setLocked(true);
		List<BoundaryPart> bpl = new ArrayList<BoundaryPart>();
		Point p1 = new Point(1,3,0);
		Point p2 = new Point(3,0,0);
		Edge e1 = new Edge(p1, p2);
		e1.setShared(true);
		List<Edge> edList = new ArrayList<Edge>();
		edList.add(e1);
		BoundaryPart bp = new BoundaryPart(edList);
		bpl.add(bp);
		edList = new ArrayList<Edge>();
		edList.add(e1);
		bp = new BoundaryPart(edList, constr);
		bpl.add(bp);
		Boundary bound = new Boundary();
		bound.setBoundary(bpl);
		List<DelaunayTriangle> tri = bound.insertPoint(new Point(3,6,0));
		assertTrue(tri.size()==1);
		assertTrue(bound.getBoundary().size()==1);
		assertTrue(bound.getBoundary().get(0).getBoundaryEdges().size()==3);
		assertTrue(bound.getBoundary().get(0).getBoundaryEdges().get(0).equals(new Edge(1,3,0,3,0,0)));
		assertTrue(bound.getBoundary().get(0).getBoundaryEdges().get(1).equals(new Edge(3,0,0,3,6,0)));
		assertTrue(bound.getBoundary().get(0).getBoundaryEdges().get(2).equals(new Edge(3,6,0,1,3,0)));
	}

	public void testBuildFirstBoundaryBis() throws DelaunayError {
		//We build the boundary part.
		Edge constrBis = new Edge(0,2,0,6,4,0);
		constrBis.setLocked(true);
		BoundaryPart bp = new BoundaryPart(constrBis);
		List<BoundaryPart> edList = new ArrayList<BoundaryPart>();
		edList.add(bp);
		//we build and fill the boundary
		Boundary bound = new Boundary();
		bound.setBoundary(edList);
		//we prepare the constraint.
		Edge constr = new Edge(4,0,0,6,4,0);
		constr.setLocked(true);
		List<Edge> cstrList = new ArrayList<Edge>();
		cstrList.add(constr);
		bound.insertPoint(new Point(4,0,0), cstrList);
		assertTrue(bound.getBoundary().size()==3);
		assertTrue(bound.getBoundary().get(2).getConstraint().equals(new Edge(0,2,0,6,4,0)));
		assertTrue(bound.getBoundary().get(1).getConstraint().equals(new Edge(4,0,0,6,4,0)));
		assertTrue(bound.getBoundary().get(0).getConstraint()==null);
		assertTrue(bound.getBoundary().get(0).getBoundaryEdges().get(0).equals(new Edge(0,2,0,4,0,0)));
		assertTrue(bound.getBoundary().get(1).getBoundaryEdges().get(0).equals(new Edge(0,2,0,4,0,0)));
		assertTrue(bound.getBoundary().get(2).getBoundaryEdges().isEmpty());
	}

	public void testBuildSharedVerticalConstraint () throws DelaunayError {
		List<BoundaryPart> bpList = new ArrayList<BoundaryPart>();
		List<Edge> edList = new ArrayList<Edge>();
		//We build the first boundary part and add it to the list of BP
		Edge constr = new Edge(0,6,0,6,0,0);
		constr.setLocked(true);
		Edge ed = new Edge(0,6,0,3,6,0);
		ed.setShared(true);
		edList.add(ed);
		BoundaryPart bp = new BoundaryPart(edList, constr);
		bpList.add(bp);
		//We build the second boundary part and add it to the list of BP
		edList = new ArrayList<Edge>();
		edList.add(ed);
		Edge constrBis = new Edge(3,6,0,4,9,0);
		constrBis.setLocked(true);
		bp = new BoundaryPart(edList,constrBis);
		bpList.add(bp);
		//we bulid and fill the boundary.
		Boundary bound = new Boundary();
		bound.setBoundary(bpList);
		//We perform the insertion
		List<Edge> cstrList = new ArrayList<Edge>();
		List<DelaunayTriangle> tri = bound.insertPoint(new Point(4,9,0), cstrList);
		//We perform our tests
		assertTrue(tri.size()==1);
		assertTrue(bound.getBoundary().size()==1);
		assertTrue(bound.getBoundary().get(0).getBoundaryEdges().size()==3);
	}

	/**
	 * Get a boundary ready to be tested.
	 * @return
	 */
	private Boundary getExampleBoundary(){
		List<BoundaryPart> bpl = new ArrayList<BoundaryPart>();
		BoundaryPart bp;
		Edge cstr;
		List<Edge> boundaryEdges;
		Boundary bound = new Boundary();
		//We fill a boundary part, and put it in bpl
		cstr = null;
		boundaryEdges = new ArrayList<Edge>();
		boundaryEdges.add(new Edge(0,1,0,3,2,0));
		bp = new BoundaryPart(boundaryEdges);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new Edge(3,2,0,9,0,0);
		cstr.setLocked(true);
		boundaryEdges = new ArrayList<Edge>();
		boundaryEdges.add(new Edge(3,2,0,5,3,0));
		boundaryEdges.add(new Edge(5,3,0,6,5,0));
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new Edge(6,5,0,10,4,0);
		cstr.setLocked(true);
		boundaryEdges = new ArrayList<Edge>();
		boundaryEdges.add(new Edge(6,5,0,7,7,0));
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new Edge(7,7,0,10,7,0);
		cstr.setLocked(true);
		boundaryEdges = new ArrayList<Edge>();
		bp = new BoundaryPart( cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new Edge(7,7,0,10,9,0);
		cstr.setLocked(true);
		boundaryEdges = new ArrayList<Edge>();
		boundaryEdges.add(new Edge(7,7,0,6,10,0));
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new Edge(6,10,0,9,12,0);
		cstr.setLocked(true);
		boundaryEdges = new ArrayList<Edge>();
		boundaryEdges.add(new Edge(6,10,0,5,11,0));
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new Edge(5,11,0,9,12,0);
		cstr.setLocked(true);
		boundaryEdges = new ArrayList<Edge>();
		boundaryEdges.add(new Edge(5,11,0,3,12,0));
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new Edge(3,12,0,9,12,0);
		cstr.setLocked(true);
		boundaryEdges = new ArrayList<Edge>();
		boundaryEdges.add(new Edge(3,12,0,0,13,0));
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);

		//We set the list of BoundaryPart in bound.
		bound.setBoundary(bpl);
		return bound;
	}

	/**
	 * Get a boundary ready to be tested.
	 * @return
	 */
	private Boundary getExampleboundaryBis(){
		List<BoundaryPart> bpl = new ArrayList<BoundaryPart>();
		BoundaryPart bp;
		Edge cstr;
		List<Edge> boundaryEdges;
		Boundary bound = new Boundary();
		//We fill a boundary part, and put it in bpl
		cstr = null;
		boundaryEdges = new ArrayList<Edge>();
		boundaryEdges.add(new Edge(0,0,0,0,4,0));
		bp = new BoundaryPart(boundaryEdges);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new Edge(0,4,0,11,1,0);
		boundaryEdges = new ArrayList<Edge>();
		boundaryEdges.add(new Edge(0,4,0,0,6,0));
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new Edge(0,6,0,7,7,0);
		boundaryEdges = new ArrayList<Edge>();
		boundaryEdges.add(new Edge(0,6,0,0,8,0));
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new Edge(0,8,0,7,7,0);
		boundaryEdges = new ArrayList<Edge>();
		boundaryEdges.add(new Edge(0,8,0,0,10,0));
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new Edge(0,10,0,7,7,0);
		boundaryEdges = new ArrayList<Edge>();
		boundaryEdges.add(new Edge(0,10,0,0,12,0));
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new Edge(0,12,0,7,7,0);
		boundaryEdges = new ArrayList<Edge>();
		boundaryEdges.add(new Edge(0,12,0,0,14,0));
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new Edge(0,14,0,10,16,0);
		boundaryEdges = new ArrayList<Edge>();
		boundaryEdges.add(new Edge(0,14,0,0,16,0));
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);

		//We set the list of BoundaryPart in bound.
		bound.setBoundary(bpl);
		return bound;
	}

	/**
	 * Get a boundary ready to be tested.
	 * @return
	 */
	private Boundary getExampleDegenEdges(){
		List<BoundaryPart> bpl = new ArrayList<BoundaryPart>();
		BoundaryPart bp;
		Edge cstr;
		Edge ed;
		List<Edge> boundaryEdges;
		Boundary bound = new Boundary();
		//We fill a boundary part, and put it in bpl
		cstr = new Edge(0,4,0,8,0,0);
		boundaryEdges = new ArrayList<Edge>();
		ed = new Edge(0,4,0,2,4,0);
		ed.setDegenerated(true);
		boundaryEdges.add(ed);
		ed = new Edge(2,4,0,4,4,0);
		ed.setDegenerated(true);
		boundaryEdges.add(ed);
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new Edge(0,4,0,8,10,0);
		bp = new BoundaryPart(cstr);
		bpl.add(bp);

		//We set the list of BoundaryPart in bound.
		bound.setBoundary(bpl);
		return bound;
	}
}
