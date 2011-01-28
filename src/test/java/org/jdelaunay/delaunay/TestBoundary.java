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
		boundaryEdges = new ArrayList<Edge>();
		boundaryEdges.add(new Edge(3,2,0,5,3,0));
		boundaryEdges.add(new Edge(5,3,0,6,5,0));
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new Edge(6,5,0,10,4,0);
		boundaryEdges = new ArrayList<Edge>();
		boundaryEdges.add(new Edge(6,5,0,7,7,0));
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new Edge(7,7,0,10,7,0);
		boundaryEdges = new ArrayList<Edge>();
		bp = new BoundaryPart( cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new Edge(7,7,0,10,9,0);
		boundaryEdges = new ArrayList<Edge>();
		boundaryEdges.add(new Edge(7,7,0,6,10,0));
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new Edge(6,10,0,9,12,0);
		boundaryEdges = new ArrayList<Edge>();
		boundaryEdges.add(new Edge(6,10,0,5,11,0));
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new Edge(5,11,0,9,12,0);
		boundaryEdges = new ArrayList<Edge>();
		boundaryEdges.add(new Edge(5,11,0,3,12,0));
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new Edge(3,12,0,9,12,0);
		boundaryEdges = new ArrayList<Edge>();
		boundaryEdges.add(new Edge(3,12,0,0,13,0));
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);

		//We set the list of BoundaryPart in bound.
		bound.setBoundary(bpl);
		return bound;
	}

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
}
