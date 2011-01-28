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
		List<BoundaryPart> elig = bound.getEligibleParts(new Point(8,-1,0));
		assertTrue(elig.size()==1);
		Edge ed = elig.get(0).getBoundaryEdges().get(0);
		assertTrue(ed.equals(new Edge(0,1,0,3,2,0)));
		assertNull(elig.get(0).getConstraint());
	}

	/**
	 * Get the eligible parts for a point that is upper than all the parts
	 * of the boundary.
	 */
	public void testGetEligiblePartsUpper() throws DelaunayError{
		Boundary bound = getExampleBoundary();
		List<BoundaryPart> elig = bound.getEligibleParts(new Point(8,13,0));
		assertTrue(elig.size()==1);
		Edge ed = elig.get(0).getBoundaryEdges().get(0);
		assertTrue(ed.equals(new Edge(0,13,0,3,12,0)));
		assertTrue(elig.get(0).getConstraint().equals(new Edge(3,12,0,9,12,0)));
	}

	/**
	 * Get the eligible parts for a point that is upper than all the parts
	 * of the boundary.
	 */
	public void testGetEligiblePartsUpperBis() throws DelaunayError{
		Boundary bound = getExampleBoundary();
		List<BoundaryPart> elig = bound.getEligibleParts(new Point(9,12,0));
		assertTrue(elig.size()==4);
		Edge ed = elig.get(0).getBoundaryEdges().get(0);
		assertTrue(ed.equals(new Edge(7,7,0,6,10,0)));
		assertTrue(elig.get(0).getConstraint().equals(new Edge(7,7,0,10,9,0)));
		ed = elig.get(1).getBoundaryEdges().get(0);
		assertTrue(ed.equals(new Edge(6,10,0,5,11,0)));
		assertTrue(elig.get(1).getConstraint().equals(new Edge(6,10,0,9,12,0)));
		ed = elig.get(2).getBoundaryEdges().get(0);
		assertTrue(ed.equals(new Edge(5,11,0,3,12,0)));
		assertTrue(elig.get(2).getConstraint().equals(new Edge(5,11,0,9,12,0)));
		ed = elig.get(3).getBoundaryEdges().get(0);
		assertTrue(ed.equals(new Edge(0,13,0,3,12,0)));
		assertTrue(elig.get(3).getConstraint().equals(new Edge(3,12,0,9,12,0)));
	}

	/**
	 * Tests getEligiblePart between two BP that share the same left point.
	 * @throws DelaunayError
	 */
	public void testGetEligiblePartDegenerated() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		List<BoundaryPart> elig = bound.getEligibleParts(new Point(9,8,0));
		assertTrue(elig.size()==1);
		assertTrue(elig.get(0).getBoundaryEdges().isEmpty());
		assertTrue(elig.get(0).getConstraint().equals(new Edge(7,7,0,10,7,0)));
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
		cstr = new Edge(6,5,0,5,10,0);
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
}
