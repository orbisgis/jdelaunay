/*
 * jDelaunay is a library dedicated to the processing of Delaunay and constrained 
 * Delaunay triangulations from PSLG inputs.
 * 
 * This library is developed at French IRSTV institute as part of the AvuPur and Eval-PDU project, 
 * funded by the French Agence Nationale de la Recherche (ANR) under contract 
 * ANR-07-VULN-01 and ANR-08-VILL-0005-01 .
 * 
 * jDelaunay is distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Adelin PIAU, Jean-Yves MARTIN
 * Copyright (C) 2011 Alexis GUEGANNO, Jean-Yves MARTIN
 * 
 * jDelaunay is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * jDelaunay is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * jDelaunay. If not, see <http://www.gnu.org/licenses/>.
 * 
 * For more information, please consult: <http://trac.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
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
		List<Integer> indices = bound.getEligibleParts(new DPoint(8,-1,0));
		List<BoundaryPart> elig = bound.getBoundary();
		assertTrue(indices.size()==1);
		DEdge ed = elig.get(indices.get(0)).getBoundaryEdges().get(0);
		assertTrue(ed.equals(new DEdge(0,1,0,3,2,0)));
		assertNull(elig.get(indices.get(0)).getConstraint());
	}

	/**
	 * Get the eligible parts for the right point of the lowest constraint linked to
	 * this boundary.
	 */
	public void testGetEligiblePartsLowerBis() throws DelaunayError{
		Boundary bound = getExampleBoundary();
		List<Integer> indices = bound.getEligibleParts(new DPoint(9,0,0));
		List<BoundaryPart> elig = bound.getBoundary();
		assertTrue(indices.size()==2);
		DEdge ed = elig.get(indices.get(0)).getBoundaryEdges().get(0);
		assertTrue(ed.equals(new DEdge(0,1,0,3,2,0)));
		assertNull(elig.get(indices.get(0)).getConstraint());
		assertTrue(elig.get(indices.get(1)).getBoundaryEdges().get(0).equals(new DEdge(3,2,0,5,3,0)));
		assertTrue(elig.get(indices.get(1)).getBoundaryEdges().get(1).equals(new DEdge(5,3,0,6,5,0)));
		assertTrue(elig.get(indices.get(1)).getConstraint().equals(new DEdge(3,2,0,9,0,0)));
	}

	/**
	 * Get the eligible parts for a point that is upper than all the parts
	 * of the boundary.
	 */
	public void testGetEligiblePartsUpper() throws DelaunayError{
		Boundary bound = getExampleBoundary();
		List<Integer> indices = bound.getEligibleParts(new DPoint(8,13,0));
		List<BoundaryPart> elig = bound.getBoundary();
		assertTrue(indices.size()==1);
		DEdge ed = elig.get(indices.get(0)).getBoundaryEdges().get(0);
		assertTrue(ed.equals(new DEdge(0,13,0,3,12,0)));
		assertTrue(elig.get(indices.get(0)).getConstraint().equals(new DEdge(3,12,0,9,12,0)));
	}

	/**
	 * Get the eligible parts for a point that is upper than all the parts
	 * of the boundary.
	 */
	public void testGetEligiblePartsUpperBis() throws DelaunayError{
		Boundary bound = getExampleBoundary();
		List<Integer> indices = bound.getEligibleParts(new DPoint(9,12,0));
		List<BoundaryPart> elig = bound.getBoundary();
		assertTrue(indices.size()==4);
		DEdge ed = elig.get(indices.get(0)).getBoundaryEdges().get(0);
		assertTrue(ed.equals(new DEdge(7,7,0,6,10,0)));
		assertTrue(elig.get(indices.get(0)).getConstraint().equals(new DEdge(7,7,0,10,9,0)));
		ed = elig.get(indices.get(1)).getBoundaryEdges().get(0);
		assertTrue(ed.equals(new DEdge(6,10,0,5,11,0)));
		assertTrue(elig.get(indices.get(1)).getConstraint().equals(new DEdge(6,10,0,9,12,0)));
		ed = elig.get(indices.get(2)).getBoundaryEdges().get(0);
		assertTrue(ed.equals(new DEdge(5,11,0,3,12,0)));
		assertTrue(elig.get(indices.get(2)).getConstraint().equals(new DEdge(5,11,0,9,12,0)));
		ed = elig.get(indices.get(3)).getBoundaryEdges().get(0);
		assertTrue(ed.equals(new DEdge(0,13,0,3,12,0)));
		assertTrue(elig.get(indices.get(3)).getConstraint().equals(new DEdge(3,12,0,9,12,0)));
	}

	/**
	 * Tests getEligiblePart between two BP that share the same left point.
	 * @throws DelaunayError
	 */
	public void testGetEligiblePartDegenerated() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		List<Integer> indices = bound.getEligibleParts(new DPoint(9,8,0));
		List<BoundaryPart> elig = bound.getBoundary();
		assertTrue(indices.size()==1);
		assertTrue(elig.get(indices.get(0)).getBoundaryEdges().isEmpty());
		assertTrue(elig.get(indices.get(0)).getConstraint().equals(new DEdge(7,7,0,10,7,0)));
	}

	/**
	 * A simple try to retrieve a boundary part.
	 * @throws DelaunayError
	 */
	public void testGetEligiblePartSimple() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		List<Integer> indices = bound.getEligibleParts(new DPoint(9,6,0));
		List<BoundaryPart> elig = bound.getBoundary();
		assertTrue(indices.size()==1);
		assertTrue(elig.get(indices.get(0)).getBoundaryEdges().size()==1);
		assertTrue(elig.get(indices.get(0)).getBoundaryEdges().get(0).equals(new DEdge(6,5,0,7,7,0)));
		assertTrue(elig.get(indices.get(0)).getConstraint().equals(new DEdge(6,5,0,10,4,0)));
	}

	/**
	 * A test with a set of BP that have the same right point for their constraint.
	 */
	public void testGetEligiblePartCommonRightPoint() throws DelaunayError {
		Boundary bound = getExampleboundaryBis();
		List<Integer> indices = bound.getEligibleParts(new DPoint(7,7,0));
		List<BoundaryPart> elig = bound.getBoundary();
		assertTrue(indices.size()==5);
		//We test the BoundaryParts.
		assertTrue(elig.get(indices.get(0)).getBoundaryEdges().size()==1);
		assertTrue(elig.get(indices.get(0)).getBoundaryEdges().get(0).equals(new DEdge(0,4,0,0,6,0)));
		assertTrue(elig.get(indices.get(0)).getConstraint().equals(new DEdge(0,4,0,11,1,0)));
		//We test the BoundaryParts.
		assertTrue(elig.get(indices.get(1)).getBoundaryEdges().size()==1);
		assertTrue(elig.get(indices.get(1)).getBoundaryEdges().get(0).equals(new DEdge(0,6,0,0,8,0)));
		assertTrue(elig.get(indices.get(1)).getConstraint().equals(new DEdge(0,6,0,7,7,0)));
		//We test the BoundaryParts.
		assertTrue(elig.get(indices.get(2)).getBoundaryEdges().size()==1);
		assertTrue(elig.get(indices.get(2)).getBoundaryEdges().get(0).equals(new DEdge(0,8,0,0,10,0)));
		assertTrue(elig.get(indices.get(2)).getConstraint().equals(new DEdge(0,8,0,7,7,0)));
		//We test the BoundaryParts.
		assertTrue(elig.get(indices.get(3)).getBoundaryEdges().size()==1);
		assertTrue(elig.get(indices.get(3)).getBoundaryEdges().get(0).equals(new DEdge(0,10,0,0,12,0)));
		assertTrue(elig.get(indices.get(3)).getConstraint().equals(new DEdge(0,10,0,7,7,0)));
		//We test the BoundaryParts
		assertTrue(elig.get(indices.get(4)).getBoundaryEdges().size()==1);
		assertTrue(elig.get(indices.get(4)).getBoundaryEdges().get(0).equals(new DEdge(0,12,0,0,14,0)));
		assertTrue(elig.get(indices.get(4)).getConstraint().equals(new DEdge(0,12,0,7,7,0)));
		
	}

	/**
	 * Some other test to ensure a good code coverage. We don't want to let bugs
	 * behind us...
	 * @throws DelaunayError
	 */
	public void testGetEligiblePart() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		List<Integer> indices = bound.getEligibleParts(new DPoint(7,11,0));
		List<BoundaryPart> elig = bound.getBoundary();
		assertTrue(indices.size()==1);
		assertTrue(elig.get(indices.get(0)).getBoundaryEdges().size()==1);
		assertTrue(elig.get(indices.get(0)).getBoundaryEdges().get(0).equals(new DEdge(6,10,0,5,11,0)));
		assertTrue(elig.get(indices.get(0)).getConstraint().equals(new DEdge(6,10,0,9,12,0)));
		//Once again
		bound = getExampleBoundary();
		indices = bound.getEligibleParts(new DPoint(7,10,0));
		assertTrue(indices.size()==1);
		assertTrue(elig.get(indices.get(0)).getBoundaryEdges().size()==1);
		assertTrue(elig.get(indices.get(0)).getBoundaryEdges().get(0).equals(new DEdge(6,10,0,7,7,0)));
		assertTrue(elig.get(indices.get(0)).getConstraint().equals(new DEdge(7,7,0,10,9,0)));
		
	}

	public void testGetEligiblePartRightPoint() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		List<Integer> indices = bound.getEligibleParts(new DPoint(10,4,0));
		List<BoundaryPart> elig = bound.getBoundary();
		assertTrue(indices.size()==2);
		assertTrue(elig.get(indices.get(0)).getBoundaryEdges().size()==2);
		assertTrue(elig.get(indices.get(0)).getBoundaryEdges().get(0).equals(new DEdge(3,2,0,5,3,0)));
		assertTrue(elig.get(indices.get(0)).getBoundaryEdges().get(1).equals(new DEdge(5,3,0,6,5,0)));
		assertTrue(elig.get(indices.get(0)).getConstraint().equals(new DEdge(3,2,0,9,0,0)));
		assertTrue(elig.get(indices.get(1)).getBoundaryEdges().size()==1);
		assertTrue(elig.get(indices.get(1)).getBoundaryEdges().get(0).equals(new DEdge(6,5,0,7,7,0)));
		assertTrue(elig.get(indices.get(1)).getConstraint().equals(new DEdge(6,5,0,10,4,0)));
		//Once again
		bound = getExampleBoundary();
		indices = bound.getEligibleParts(new DPoint(10,7,0));
		assertTrue(indices.size()==2);
		assertTrue(elig.get(indices.get(0)).getBoundaryEdges().size()==1);
		assertTrue(elig.get(indices.get(0)).getBoundaryEdges().get(0).equals(new DEdge(6,5,0,7,7,0)));
		assertTrue(elig.get(indices.get(0)).getConstraint().equals(new DEdge(6,5,0,10,4,0)));
		assertTrue(elig.get(indices.get(1)).getBoundaryEdges().isEmpty());
		assertTrue(elig.get(indices.get(1)).getConstraint().equals(new DEdge(7,7,0,10,7,0)));

	}

	/**
	 * Insert a point that is flanked with two constraint edges , and that does not lie
	 * on a constraint.
	 * @throws DelaunayError
	 */
	public void testInsertFlankedPoint() throws DelaunayError{
		Boundary bound = getExampleBoundary();
		List<DTriangle> tri = bound.insertPoint(new DPoint(9,6,0));
		BoundaryPart bp = bound.getBoundary().get(2);
		assertTrue(tri.contains(new DTriangle(new DEdge(6,5,0,9,6,0), new DEdge(9,6,0,7,7,0), new DEdge(7,7,0,6,5,0))));
		assertTrue(tri.size()==1);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new DEdge(6,5,0,9,6,0)));
		assertTrue(bp.getBoundaryEdges().get(1).equals(new DEdge(9,6,0,7,7,0)));
		assertTrue(bp.getBoundaryEdges().size()==2);
		assertTrue(bp.getConstraint().equals(new DEdge(6,5,0,10,4,0)));
	}

	/**
	 * Insert a point that is upper than all the constraint edges linked to the mesh.
	 * This point does not lie on any constraint.
	 * @throws DelaunayError
	 */
	public void testInsertUpperPoint() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		List<DTriangle> tri = bound.insertPoint(new DPoint(8,13,0));
		BoundaryPart bp = bound.getBoundary().get(7);
		assertTrue(tri.contains(new DTriangle(new DEdge(3,12,0,8,13,0), new DEdge(8,13,0,0,13,0), new DEdge(0,13,0,3,12,0))));
		assertTrue(tri.size()==1);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new DEdge(3,12,0,8,13,0)));
		assertTrue(bp.getBoundaryEdges().get(1).equals(new DEdge(8,13,0,0,13,0)));
		assertTrue(bp.getBoundaryEdges().size()==2);
		assertTrue(bp.getConstraint().equals(new DEdge(3,12,0,9,12,0)));
	}

	/**
	 * Tests that we retrieve the good bad and added edges when inserting a point
	 * that is upper than all the constraints linked to the mesh.
	 * @throws DelaunayError
	 */
	public void testInsUpPtAddedAndBadEdges() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		bound.insertPoint(new DPoint(8,13,0));
		List<DEdge> added = bound.getAddedEdges();
		List<DEdge> bad = bound.getBadEdges();
		assertTrue(added.size()==2);
		assertTrue(added.contains(new DEdge(0,13,0,8,13,0)));
		assertTrue(added.contains(new DEdge(8,13,0,3,12,0)));
		assertTrue(bad.size()==1);
		assertTrue(bad.contains(new DEdge(0,13,0,3,12,0)));
	}

	/**
	 * Insert a point that is upper than all the constraint edges linked to the mesh.
	 * This point does not lie on any constraint.
	 * @throws DelaunayError
	 */
	public void testInsertLowerPoint() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		List<DTriangle> tri = bound.insertPoint(new DPoint(8,-1,0));
		BoundaryPart bp = bound.getBoundary().get(0);
		assertTrue(tri.contains(new DTriangle(new DEdge(0,1,0,8,-1,0), new DEdge(8,-1,0,3,2,0), new DEdge(3,2,0,0,1,0))));
		assertTrue(tri.size()==1);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new DEdge(0,1,0,8,-1,0)));
		assertTrue(bp.getBoundaryEdges().get(1).equals(new DEdge(8,-1,0,3,2,0)));
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
		bound.insertPoint(new DPoint(8,-1,0));
		List<DEdge> added = bound.getAddedEdges();
		assertTrue(added.size()==2);
		assertTrue(added.contains(new DEdge(0,1,0,8,-1,0)));
		assertTrue(added.contains(new DEdge(3,2,0,8,-1,0)));
		List<DEdge> bad = bound.getBadEdges();
		assertTrue(bad.size()==1);
		assertTrue(bad.contains(new DEdge(0,1,0,3,2,0)));
	}

	/**
	 * insert a point that must create a degenerated edge.
	 * @throws DelaunayError
	 */
	public void testInsertPointDegenerated() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		List<DTriangle> tri = bound.insertPoint(new DPoint(9,8,0));
		BoundaryPart bp = bound.getBoundary().get(3);
		assertTrue(tri.isEmpty());
		assertTrue(bp.getBoundaryEdges().get(0).equals(new DEdge(7,7,0,9,8,0)));
		assertTrue(bp.getBoundaryEdges().get(0).isDegenerated());
		assertTrue(bp.getBoundaryEdges().size()==1);
		assertTrue(bp.getConstraint().equals(new DEdge(7,7,0,10,7,0)));
	}

	/**
	 * tests that we retrieve the good bad and added edges when inserting a point
	 * that create a single degenerated edge.
	 * @throws DelaunayError
	 */
	public void testInsPtDegenAddedAndBadEdges() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		bound.insertPoint(new DPoint(9,8,0));
		List<DEdge> added = bound.getAddedEdges();
		assertTrue(added.size()==1);
		assertTrue(added.get(0).equals(new DEdge(7,7,0,9,8,0)));
		List<DEdge> bad = bound.getBadEdges();
		assertTrue(bad.isEmpty());
	}

	/**
	 * Tests the insertion of the point when more than one edges of the boundary
	 * are affected.
	 * @throws DelaunayError
	 */
	public void testInsertTwoTriangles() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		List<DTriangle> tri = bound.insertPoint(new DPoint(7,3,0));
		assertTrue(tri.size()==2);
		assertTrue(tri.contains(new DTriangle(new DEdge(3,2,0,7,3,0), new DEdge(7,3,0,5,3,0),  new DEdge(5,3,0,3,2,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(6,5,0,7,3,0), new DEdge(7,3,0,5,3,0),  new DEdge(5,3,0,6,5,0))));
		List<DEdge> added = bound.getAddedEdges();
		List<DEdge> bad = bound.getBadEdges();
		assertTrue(added.size()==3);
		assertTrue(added.contains(new DEdge(3,2,0,7,3,0)));
		assertTrue(added.contains(new DEdge(5,3,0,7,3,0)));
		assertTrue(added.contains(new DEdge(6,5,0,7,3,0)));
		assertTrue(bad.size()==2);
		assertTrue(bad.contains(new DEdge(3,2,0,5,3,0)));
		assertTrue(bad.contains(new DEdge(6,5,0,5,3,0)));

	}

	/**
	 * This (complete) test checks that we insert correctly the right points
	 * of the constraints.
	 * @throws DelaunayError
	 */
	public void testInsertConstraintRightPoint() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		List<DTriangle> tri = bound.insertPoint(new DPoint(10,4,0));
		//we test the added triangles.
		assertTrue(tri.size()==3);
		assertTrue(tri.contains(new DTriangle(new DEdge(3,2,0,10,4,0), new DEdge(10,4,0,5,3,0), new DEdge(5,3,0,3,2,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(6,5,0,10,4,0), new DEdge(10,4,0,5,3,0), new DEdge(5,3,0,6,5,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(6,5,0,10,4,0), new DEdge(10,4,0,7,7,0), new DEdge(7,7,0,6,5,0))));
		//we check that the constraint contained in some triangles is not a duplicated edge.
		int index  = tri.indexOf(new DTriangle(new DEdge(6,5,0,10,4,0), new DEdge(10,4,0,5,3,0), new DEdge(5,3,0,6,5,0)));
		DEdge ed = tri.get(index).getOppositeEdge(new DPoint(5,3,0));
		assertTrue(ed.isLocked());
		index  = tri.indexOf(new DTriangle(new DEdge(6,5,0,10,4,0), new DEdge(10,4,0,7,7,0), new DEdge(7,7,0,6,5,0)));
		ed = tri.get(index).getOppositeEdge(new DPoint(7,7,0));
		assertTrue(ed.isLocked());
		List<DEdge> added = bound.getAddedEdges();
		List<DEdge> bad = bound.getBadEdges();
		//we check the added edges.
		assertTrue(added.size()==4);
		assertTrue(added.contains(new DEdge(3,2,0,10,4,0)));
		assertTrue(added.contains(new DEdge(5,3,0,10,4,0)));
		assertTrue(added.contains(new DEdge(6,5,0,10,4,0)));
		assertTrue(added.contains(new DEdge(7,7,0,10,4,0)));
		//we check that the added edge that is a constraint is not a duplicate.
		index = added.indexOf(new DEdge(6,5,0,10,4,0));
		ed = added.get(index);
		assertTrue(ed.isLocked());
		//we check the badEdges.
		assertTrue(bad.size()==3);
		assertTrue(bad.contains(new DEdge(3,2,0,5,3,0)));
		assertTrue(bad.contains(new DEdge(5,3,0,6,5,0)));
		assertTrue(bad.contains(new DEdge(6,5,0,7,7,0)));
		//We check the boundary state.
		assertTrue(bound.getBoundary().size()==7);
		BoundaryPart bp = bound.getBoundary().get(1);
		assertTrue(bp.getConstraint().equals(new DEdge(3,2,0,9,0,0)));

	}

	/**
	 * This test performs an insertion of a point that is the right point of
	 * many constraints.
	 * @throws DelaunayError
	 */
	public void testInsertCstrRightPtManyBP() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		List<DTriangle> tri = bound.insertPoint(new DPoint(9,12,0));
		//we test the added triangles.
		assertTrue(tri.size()==4);
		assertTrue(tri.contains(new DTriangle(new DEdge(7,7,0,9,12,0), new DEdge(9,12,0,6,10,0), new DEdge(6,10,0,7,7,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(5,11,0,9,12,0), new DEdge(9,12,0,6,10,0), new DEdge(6,10,0,5,11,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(5,11,0,9,12,0), new DEdge(9,12,0,3,12,0), new DEdge(3,12,0,5,11,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(0,13,0,9,12,0), new DEdge(9,12,0,3,12,0), new DEdge(3,12,0,0,13,0))));
		//we check that the constraints contained in some triangles are not  duplicated edges.
		int index  = tri.indexOf(new DTriangle(new DEdge(7,7,0,9,12,0), new DEdge(9,12,0,6,10,0), new DEdge(6,10,0,7,7,0)));
		DEdge ed = tri.get(index).getOppositeEdge(new DPoint(7,7,0));
		assertTrue(ed.isLocked());
		index  = tri.indexOf(new DTriangle(new DEdge(5,11,0,9,12,0), new DEdge(9,12,0,6,10,0), new DEdge(6,10,0,5,11,0)));
		ed = tri.get(index).getOppositeEdge(new DPoint(6,10,0));
		assertTrue(ed.isLocked());
		index  = tri.indexOf(new DTriangle(new DEdge(5,11,0,9,12,0), new DEdge(9,12,0,3,12,0), new DEdge(3,12,0,5,11,0)));
		ed = tri.get(index).getOppositeEdge(new DPoint(5,11,0));
		assertTrue(ed.isLocked());
		index  = tri.indexOf(new DTriangle(new DEdge(0,13,0,9,12,0), new DEdge(9,12,0,3,12,0), new DEdge(3,12,0,0,13,0)));
		ed = tri.get(index).getOppositeEdge(new DPoint(0,13,0));
		assertTrue(ed.isLocked());
		List<DEdge> added = bound.getAddedEdges();
		List<DEdge> bad = bound.getBadEdges();
		//we check the added edges.
		assertTrue(added.size()==5);
		assertTrue(added.contains(new DEdge(6,10,0,9,12,0)));
		assertTrue(added.contains(new DEdge(7,7,0,9,12,0)));
		assertTrue(added.contains(new DEdge(5,11,0,9,12,0)));
		assertTrue(added.contains(new DEdge(3,12,0,9,12,0)));
		assertTrue(added.contains(new DEdge(0,13,0,9,12,0)));
		//we check the bad edges
		assertTrue(bad.size()==4);
		assertTrue(bad.contains(new DEdge(7,7,0,6,10,0)));
		assertTrue(bad.contains(new DEdge(5,11,0,6,10,0)));
		assertTrue(bad.contains(new DEdge(5,11,0,3,12,0)));
		assertTrue(bad.contains(new DEdge(0,13,0,3,12,0)));
		//And we check the whole boundary
		assertTrue(bound.getBoundary().size()==5);
		BoundaryPart bp = bound.getBoundary().get(4);
		assertTrue(bp.getConstraint().equals(new DEdge(7,7,0,10,9,0)));
		assertTrue(bp.getBoundaryEdges().get(0).equals(new DEdge(7,7,0,9,12,0)));
		assertTrue(bp.getBoundaryEdges().get(0).getStartPoint().equals(new DPoint(7,7,0)));
		assertTrue(bp.getBoundaryEdges().get(1).equals(new DEdge(0,13,0,9,12,0)));
		assertTrue(bp.getBoundaryEdges().get(1).getStartPoint().equals(new DPoint(9,12,0)));
	}

	/**
	 * We add the right point of the lowest constraint and check that everything's ok.
	 * @throws DelaunayError
	 */
	public void testAddRightPtLowestCstr() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		List<DTriangle> tri = bound.insertPoint(new DPoint(9,0,0));
		//we test the added triangles.
		assertTrue(tri.size()==3);
		assertTrue(tri.contains(new DTriangle(new DEdge(0,1,0,9,0,0), new DEdge(9,0,0,3,2,0), new DEdge(3,2,0,0,1,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(5,3,0,9,0,0), new DEdge(9,0,0,3,2,0), new DEdge(3,2,0,5,3,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(5,3,0,9,0,0), new DEdge(9,0,0,6,5,0), new DEdge(6,5,0,5,3,0))));
		List<DEdge> added = bound.getAddedEdges();
		List<DEdge> bad = bound.getBadEdges();
		//we check the added edges.
		assertTrue(added.size()==4);
		assertTrue(added.contains(new DEdge(0,1,0,9,0,0)));
		assertTrue(added.contains(new DEdge(3,2,0,9,0,0)));
		assertTrue(added.contains(new DEdge(5,3,0,9,0,0)));
		assertTrue(added.contains(new DEdge(6,5,0,9,0,0)));
		//we check the bad edges
		assertTrue(bad.size()==3);
		assertTrue(bad.contains(new DEdge(0,1,0,3,2,0)));
		assertTrue(bad.contains(new DEdge(5,3,0,3,2,0)));
		assertTrue(bad.contains(new DEdge(5,3,0,6,5,0)));
		//And we check the whole boundary
		assertTrue(bound.getBoundary().size()==7);
		BoundaryPart bp = bound.getBoundary().get(0);
		assertNull(bp.getConstraint());
		assertTrue(bp.getBoundaryEdges().size()==2);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new DEdge(0,1,0,9,0,0)));
		assertTrue(bp.getBoundaryEdges().get(1).equals(new DEdge(6,5,0,9,0,0)));
	}

	/**
	 * checks that we manage properly the case where uppest boundary part does not
	 * contain any boundary DEdge.
	 * @throws DelaunayError
	 */
	public void testInsertRightPtDegenUppestCstr() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		assertTrue(bound.getBoundary().size()==8);
		List<DTriangle> tri = bound.insertPoint(new DPoint(10,7,0));
		//we test the added triangles.
		assertTrue(tri.size()==1);
		assertTrue(tri.contains(new DTriangle(new DEdge(6,5,0,10,7,0), new DEdge(10,7,0,7,7,0), new DEdge(7,7,0,6,5,0))));
		List<DEdge> added = bound.getAddedEdges();
		List<DEdge> bad = bound.getBadEdges();
		//we check the added edges.
		assertTrue(added.size()==2);
		assertTrue(added.contains(new DEdge(6,5,0,10,7,0)));
		assertTrue(added.contains(new DEdge(7,7,0,10,7,0)));
		//we check the bad edges
		assertTrue(bad.size()==1);
		assertTrue(bad.contains(new DEdge(6,5,0,7,7,0)));
		//And we check the whole boundary
		assertTrue(bound.getBoundary().size()==7);
		BoundaryPart bp = bound.getBoundary().get(2);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new DEdge(6,5,0,10,7,0)));
		assertTrue(bp.getBoundaryEdges().get(1).equals(new DEdge(7,7,0,10,7,0)));
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
		DEdge cstr;
		List<DEdge> boundaryEdges;
		Boundary bound = new Boundary();
		//We fill a boundary part, and put it in bpl
		cstr = null;
		boundaryEdges = new ArrayList<DEdge>();
		boundaryEdges.add(new DEdge(0,0,0,1,3,0));
		bp = new BoundaryPart(boundaryEdges);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new DEdge(1,3,0,6,1,0);
		cstr.setLocked(true);
		boundaryEdges = new ArrayList<DEdge>();
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new DEdge(1,3,0,5,4,0);
		cstr.setLocked(true);
		boundaryEdges = new ArrayList<DEdge>();
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new DEdge(1,3,0,6,6,0);
		cstr.setLocked(true);
		boundaryEdges = new ArrayList<DEdge>();
		boundaryEdges.add(new DEdge(1,3,0,0,6,0));
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		bound.setBoundary(bpl);
		//We insert the point
		List<DTriangle> tri = bound.insertPoint(new DPoint(5,4,0));
		assertTrue(tri.isEmpty());
		assertTrue(bound.getBoundary().size()==3);
		assertTrue(bound.getBoundary().get(1).getConstraint().equals(new DEdge(1,3,0,6,1,0)));
		assertTrue(bound.getBoundary().get(1).getBoundaryEdges().get(0).equals(new DEdge(1,3,0,5,4,0)));
		assertTrue(bound.getBoundary().get(1).getBoundaryEdges().get(0).isLocked());
		assertTrue(bound.getBoundary().get(1).getBoundaryEdges().get(0).isDegenerated());
		
	}

	public void testInsertRightPtDegenLowestCstr() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		assertTrue(bound.getBoundary().size()==8);
		List<DTriangle> tri = bound.insertPoint(new DPoint(10,9,0));
		//we test the added triangles.
		assertTrue(tri.size()==1);
		assertTrue(tri.contains(new DTriangle(new DEdge(6,10,0,10,9,0), new DEdge(10,9,0,7,7,0), new DEdge(7,7,0,6,10,0))));
		List<DEdge> added = bound.getAddedEdges();
		List<DEdge> bad = bound.getBadEdges();
		//we check the added edges.
		assertTrue(added.size()==2);
		assertTrue(added.contains(new DEdge(6,10,0,10,9,0)));
		assertTrue(added.contains(new DEdge(7,7,0,10,9,0)));
		//we check the bad edges
		assertTrue(bad.size()==1);
		assertTrue(bad.contains(new DEdge(6,10,0,7,7,0)));
		//And we check the whole boundary
		assertTrue(bound.getBoundary().size()==7);
		BoundaryPart bp = bound.getBoundary().get(3);
		assertTrue(bp.getBoundaryEdges().get(1).equals(new DEdge(6,10,0,10,9,0)));
		assertTrue(bp.getBoundaryEdges().get(0).equals(new DEdge(7,7,0,10,9,0)));
		assertTrue(bp.getBoundaryEdges().get(0).isLocked());
		assertFalse(bp.getBoundaryEdges().get(0).isDegenerated());

	}

	/**
	 * Performs an insertion with a simple split.
	 * @throws DelaunayError
	 */
	public void testInsertAndSplit() throws DelaunayError {
		Boundary bound = getExampleBoundary();
		List<DEdge> cstrList = new ArrayList<DEdge>();
		cstrList.add(new DEdge(7,3,0,12,2,0));
		List<DTriangle> tri = bound.insertPoint(new DPoint(7,3,0), cstrList);
		assertTrue(tri.size()==2);
		assertTrue(tri.contains(new DTriangle(new DEdge(3,2,0,7,3,0), new DEdge(7,3,0,5,3,0),  new DEdge(5,3,0,3,2,0))));
		assertTrue(tri.contains(new DTriangle(new DEdge(6,5,0,7,3,0), new DEdge(7,3,0,5,3,0),  new DEdge(5,3,0,6,5,0))));
		List<DEdge> added = bound.getAddedEdges();
		List<DEdge> bad = bound.getBadEdges();
		//we check the added edges.
		assertTrue(added.size()==3);
		assertTrue(added.contains(new DEdge(3,2,0,7,3,0)));
		assertTrue(added.contains(new DEdge(5,3,0,7,3,0)));
		assertTrue(added.contains(new DEdge(6,5,0,7,3,0)));
		//we check the bad edges
		assertTrue(bad.size()==2);
		assertTrue(bad.contains(new DEdge(3,2,0,5,3,0)));
		assertTrue(bad.contains(new DEdge(6,5,0,5,3,0)));
		//And we check the whole boundary
		assertTrue(bound.getBoundary().size()==9);
		BoundaryPart bp = bound.getBoundary().get(1);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new DEdge(3,2,0,7,3,0)));
		assertTrue(bp.getConstraint().equals(new DEdge(3,2,0,9,0,0)));
		bp = bound.getBoundary().get(2);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new DEdge(7,3,0,6,5,0)));
		assertTrue(bp.getConstraint().equals(new DEdge(7,3,0,12,2,0)));
		bp = bound.getBoundary().get(3);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new DEdge(6,5,0,7,7,0)));
		assertTrue(bp.getConstraint().equals(new DEdge(6,5,0,10,4,0)));
		
	}

	/**
	 * test a split performed with degen edges
	 * @throws DelaunayError
	 */
	public void testInsertDegenEdgesSplit() throws DelaunayError {
		Boundary bound = getExampleDegenEdges();
		List<DEdge> cstrList = new ArrayList<DEdge>();
		cstrList.add(new DEdge(6,4,0,8,4,0));
		List<DTriangle> tri = bound.insertPoint(new DPoint(6,4,0), cstrList);
		List<DEdge> added = bound.getAddedEdges();
		List<DEdge> bad = bound.getBadEdges();
		//we test the added triangles.
		assertTrue(tri.isEmpty());
		//we check the bad edges
		assertTrue(bad.isEmpty());
		//we check the added edges.
		assertTrue(added.size()==1);
		assertTrue(added.get(0).equals(new DEdge(4,4,0,6,4,0)));
		//And we check the whole boundary
		assertTrue(bound.getBoundary().size()==3);
		BoundaryPart bp = bound.getBoundary().get(0);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new DEdge(0,4,0,2,4,0)));
		assertTrue(bp.getBoundaryEdges().get(1).equals(new DEdge(4,4,0,2,4,0)));
		assertTrue(bp.getBoundaryEdges().get(2).equals(new DEdge(4,4,0,6,4,0)));
		assertTrue(bp.getConstraint().equals(new DEdge(0,4,0,8,0,0)));
		bp = bound.getBoundary().get(1);
		assertTrue(bp.getBoundaryEdges().get(2).equals(new DEdge(0,4,0,2,4,0)));
		assertTrue(bp.getBoundaryEdges().get(1).equals(new DEdge(4,4,0,2,4,0)));
		assertTrue(bp.getBoundaryEdges().get(0).equals(new DEdge(4,4,0,6,4,0)));
		assertTrue(bp.getConstraint().equals(new DEdge(6,4,0,8,4,0)));
	}

	/**
	 * This test is built on the same scheme than testInsertDegenEdgesSplit, but
	 * we go one step further by connecting another point to the mesh
	 * @throws DelaunayError
	 */
	public void testInsertDESplitNextStep() throws DelaunayError {
		Boundary bound = getExampleDegenEdges();
		List<DEdge> cstrList = new ArrayList<DEdge>();
		cstrList.add(new DEdge(6,4,0,8,4,0));
		List<DTriangle> tri = bound.insertPoint(new DPoint(6,4,0), cstrList);
		tri = bound.insertPoint(new DPoint(6,6,0));
		//we test the added triangles.
		assertTrue(tri.size()==3);

	}

	/**
	 * A more complete example, with many degenerated edges.
	 * @throws DelaunayError
	 */
	public void testManySharedEdges() throws DelaunayError {
		DEdge cstr = new DEdge(0,13,0,19,0,0);
		BoundaryPart bp = new BoundaryPart(cstr);
		Boundary bound = new Boundary();
		List<BoundaryPart> bpl = new ArrayList<BoundaryPart>();
		List<DEdge> cstrList;
		bpl.add(bp);
		bound.setBoundary(bpl);
		//3,13,0   19,2,0
		DPoint ptToAdd = new DPoint(3,13,0);
		cstrList = new ArrayList<DEdge>();
		cstrList.add(new DEdge(3,13,0,19,2,0));
		bound.insertPoint(ptToAdd, cstrList);
		assertTrue(bound.getBoundary().size()==2);
		//6,13,0   19,4,0
		ptToAdd = new DPoint(6,13,0);
		cstrList = new ArrayList<DEdge>();
		cstrList.add(new DEdge(6,13,0,19,4,0));
		bound.insertPoint(ptToAdd, cstrList);
		//9,13,0   19,6,0
		ptToAdd = new DPoint(9,13,0);
		cstrList = new ArrayList<DEdge>();
		cstrList.add(new DEdge(9,13,0,19,6,0));
		bound.insertPoint(ptToAdd, cstrList);
		//12,13,0
		ptToAdd = new DPoint(12,13,0);
		cstrList = new ArrayList<DEdge>();
		bound.insertPoint(ptToAdd, cstrList);
		//15,13,0
		ptToAdd = new DPoint(15,13,0);
		cstrList = new ArrayList<DEdge>();
		bound.insertPoint(ptToAdd, cstrList);
		assertTrue(bound.getBoundary().size()==4);
		assertTrue(bound.getAddedEdges().contains(new DEdge(12,13,0,15,13,0)));
		/***************************************
		 * And now we can perform our tests.
		 **************************************/
		//On the first BP in the boundary
		bp = bound.getBoundary().get(0);
		assertTrue(bp.getBoundaryEdges().size()==1);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new DEdge(0,13,0,3,13,0)));
		assertTrue(bp.getBoundaryEdges().get(0).isShared());
		//On the second BP in the boundary
		bp = bound.getBoundary().get(1);
		assertTrue(bp.getBoundaryEdges().size()==1);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new DEdge(6,13,0,3,13,0)));
		assertTrue(bp.getBoundaryEdges().get(0).isShared());
		//On the third BP in the boundary
		bp = bound.getBoundary().get(2);
		assertTrue(bp.getBoundaryEdges().size()==1);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new DEdge(6,13,0,9,13,0)));
		assertTrue(bp.getBoundaryEdges().get(0).isShared());
		//On the third BP in the boundary
		bp = bound.getBoundary().get(3);
		assertTrue(bp.getBoundaryEdges().size()==5);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new DEdge(12,13,0,9,13,0)));
		assertTrue(bp.getBoundaryEdges().get(0).isDegenerated());
		assertTrue(bp.getBoundaryEdges().get(1).equals(new DEdge(12,13,0,15,13,0)));
		assertTrue(bp.getBoundaryEdges().get(1).isDegenerated());
		assertTrue(bp.getBoundaryEdges().get(2).equals(new DEdge(6,13,0,9,13,0)));
		assertTrue(bp.getBoundaryEdges().get(2).isShared());
		assertTrue(bp.getBoundaryEdges().get(3).equals(new DEdge(6,13,0,3,13,0)));
		assertTrue(bp.getBoundaryEdges().get(3).isShared());
		assertTrue(bp.getBoundaryEdges().get(4).equals(new DEdge(0,13,0,3,13,0)));
		assertTrue(bp.getBoundaryEdges().get(4).isShared());
	}

	/**
	 * A more complete example, with many degenerated edges. we insert a point.
	 * @throws DelaunayError
	 */
	public void testManySharedEdgesInsertion() throws DelaunayError {
		DEdge cstr = new DEdge(0,13,0,19,0,0);
		BoundaryPart bp = new BoundaryPart(cstr);
		Boundary bound = new Boundary();
		List<BoundaryPart> bpl = new ArrayList<BoundaryPart>();
		List<DEdge> cstrList;
		bpl.add(bp);
		bound.setBoundary(bpl);
		//3,13,0   19,2,0
		DPoint ptToAdd = new DPoint(3,13,0);
		cstrList = new ArrayList<DEdge>();
		cstrList.add(new DEdge(3,13,0,19,2,0));
		bound.insertPoint(ptToAdd, cstrList);
		assertTrue(bound.getBoundary().size()==2);
		//6,13,0   19,4,0
		ptToAdd = new DPoint(6,13,0);
		cstrList = new ArrayList<DEdge>();
		cstrList.add(new DEdge(6,13,0,19,4,0));
		bound.insertPoint(ptToAdd, cstrList);
		//9,13,0   19,6,0
		ptToAdd = new DPoint(9,13,0);
		cstrList = new ArrayList<DEdge>();
		cstrList.add(new DEdge(9,13,0,19,6,0));
		bound.insertPoint(ptToAdd, cstrList);
		//12,13,0
		ptToAdd = new DPoint(12,13,0);
		cstrList = new ArrayList<DEdge>();
		bound.insertPoint(ptToAdd, cstrList);
		//15,13,0
		ptToAdd = new DPoint(15,13,0);
		cstrList = new ArrayList<DEdge>();
		bound.insertPoint(ptToAdd, cstrList);
		assertTrue(bound.getBoundary().size()==4);
		assertTrue(bound.getAddedEdges().contains(new DEdge(12,13,0,15,13,0)));
		//We insert a point.
		List<DTriangle> tri = bound.insertPoint(new DPoint(16,16,0));
		/***************************************
		 * And now we can perform our tests.
		 **************************************/
		 assertTrue(tri.size()==5);
		 assertTrue(tri.contains(new DTriangle(new DEdge(12,13,0,15,13,0), new DEdge(15,13,0,16,16,0), new DEdge(16,16,0,12,13,0))));
		 assertTrue(tri.contains(new DTriangle(new DEdge(12,13,0,9,13,0), new DEdge(9,13,0,16,16,0), new DEdge(16,16,0,12,13,0))));
		 assertTrue(tri.contains(new DTriangle(new DEdge(6,13,0,9,13,0), new DEdge(9,13,0,16,16,0), new DEdge(16,16,0,6,13,0))));
		 assertTrue(tri.contains(new DTriangle(new DEdge(6,13,0,3,13,0), new DEdge(3,13,0,16,16,0), new DEdge(16,16,0,6,13,0))));
		 assertTrue(tri.contains(new DTriangle(new DEdge(0,13,0,3,13,0), new DEdge(3,13,0,16,16,0), new DEdge(16,16,0,0,13,0))));
		 for(BoundaryPart bopa : bound.getBoundary()){
			 assertFalse(bopa.getBoundaryEdges().get(0).isShared());
		 }
		 bp = bound.getBoundary().get(3);
		 assertTrue(bp.getBoundaryEdges().size()==4);
		 assertTrue(bp.getBoundaryEdges().get(0).equals(new DEdge(9,13,0,12,13,0)));
		 assertTrue(bp.getBoundaryEdges().get(0).getStartPoint().equals(new DPoint(9,13,0)));
		 assertTrue(bp.getBoundaryEdges().get(1).equals(new DEdge(15,13,0,12,13,0)));
		 assertTrue(bp.getBoundaryEdges().get(1).getStartPoint().equals(new DPoint(12,13,0)));
		 assertTrue(bp.getBoundaryEdges().get(2).equals(new DEdge(15,13,0,16,16,0)));
		 assertTrue(bp.getBoundaryEdges().get(2).getStartPoint().equals(new DPoint(15,13,0)));
		 assertTrue(bp.getBoundaryEdges().get(3).equals(new DEdge(0,13,0,16,16,0)));
		 assertTrue(bp.getBoundaryEdges().get(3).getStartPoint().equals(new DPoint(16,16,0)));
		 assertTrue(bp.getAddedEdges().size()==6);
		 assertTrue(bp.getAddedEdges().contains(new DEdge(15,13,0,16,16,0)));
		 assertTrue(bp.getAddedEdges().contains(new DEdge(12,13,0,16,16,0)));
		 assertTrue(bp.getAddedEdges().contains(new DEdge(9,13,0,16,16,0)));
		 assertTrue(bp.getAddedEdges().contains(new DEdge(6,13,0,16,16,0)));
		 assertTrue(bp.getAddedEdges().contains(new DEdge(3,13,0,16,16,0)));
		 assertTrue(bp.getAddedEdges().contains(new DEdge(0,13,0,16,16,0)));
		
	}

	/**
	 * A more complete example, with many degenerated edges. we insert a point.
	 * @throws DelaunayError
	 */
	public void testManySharedEdgesInsertionBis() throws DelaunayError {
		DEdge cstr = new DEdge(0,13,0,19,0,0);
		BoundaryPart bp = new BoundaryPart(cstr);
		Boundary bound = new Boundary();
		List<BoundaryPart> bpl = new ArrayList<BoundaryPart>();
		List<DEdge> cstrList;
		bpl.add(bp);
		bound.setBoundary(bpl);
		//3,13,0   19,2,0
		DPoint ptToAdd = new DPoint(3,13,0);
		cstrList = new ArrayList<DEdge>();
		cstrList.add(new DEdge(3,13,0,19,2,0));
		bound.insertPoint(ptToAdd, cstrList);
		assertTrue(bound.getBoundary().size()==2);
		//6,13,0   19,4,0
		ptToAdd = new DPoint(6,13,0);
		cstrList = new ArrayList<DEdge>();
		cstrList.add(new DEdge(6,13,0,19,4,0));
		bound.insertPoint(ptToAdd, cstrList);
		//9,13,0   19,6,0
		ptToAdd = new DPoint(9,13,0);
		cstrList = new ArrayList<DEdge>();
		cstrList.add(new DEdge(9,13,0,19,6,0));
		bound.insertPoint(ptToAdd, cstrList);
		//12,13,0
		ptToAdd = new DPoint(12,13,0);
		cstrList = new ArrayList<DEdge>();
		bound.insertPoint(ptToAdd, cstrList);
		//15,13,0
		ptToAdd = new DPoint(15,13,0);
		cstrList = new ArrayList<DEdge>();
		bound.insertPoint(ptToAdd, cstrList);
		assertTrue(bound.getBoundary().size()==4);
		assertTrue(bound.getAddedEdges().contains(new DEdge(12,13,0,15,13,0)));
		//We insert a point.
		List<DTriangle> tri = bound.insertPoint(new DPoint(16,5,0));
		bp = bound.getBoundary().get(1);
		assertTrue(bp.getBoundaryEdges().size()==2);
		assertTrue(bp.getBoundaryEdges().get(0).equals(new DEdge(3,13,0,16,5,0)));
		assertTrue(bp.getBoundaryEdges().get(0).getStartPoint().equals(new DPoint(3,13,0)));
		assertTrue(bp.getBoundaryEdges().get(1).equals(new DEdge(6,13,0,16,5,0)));
		assertTrue(bp.getBoundaryEdges().get(1).getStartPoint().equals(new DPoint(16,5,0)));
		bp = bound.getBoundary().get(3);
		assertFalse(bp.getBoundaryEdges().get(3).isShared());
		//We insert another point 
		tri = bound.insertPoint(new DPoint(16,16,0));
		/***************************************
		 * And now we can perform our tests.
		 **************************************/
		 assertTrue(tri.size()==5);
		 assertTrue(tri.contains(new DTriangle(new DEdge(12,13,0,15,13,0), new DEdge(15,13,0,16,16,0), new DEdge(16,16,0,12,13,0))));
		 assertTrue(tri.contains(new DTriangle(new DEdge(12,13,0,9,13,0), new DEdge(9,13,0,16,16,0), new DEdge(16,16,0,12,13,0))));
		 assertTrue(tri.contains(new DTriangle(new DEdge(6,13,0,9,13,0), new DEdge(9,13,0,16,16,0), new DEdge(16,16,0,6,13,0))));
		 assertTrue(tri.contains(new DTriangle(new DEdge(6,13,0,3,13,0), new DEdge(3,13,0,16,16,0), new DEdge(16,16,0,6,13,0))));
		 assertTrue(tri.contains(new DTriangle(new DEdge(0,13,0,3,13,0), new DEdge(3,13,0,16,16,0), new DEdge(16,16,0,0,13,0))));
		 for(BoundaryPart bopa : bound.getBoundary()){
			 assertFalse(bopa.getBoundaryEdges().get(0).isShared());
		 }
		 bp = bound.getBoundary().get(3);
		 assertTrue(bp.getBoundaryEdges().size()==4);
		 assertTrue(bp.getBoundaryEdges().get(0).equals(new DEdge(9,13,0,12,13,0)));
		 assertTrue(bp.getBoundaryEdges().get(0).getStartPoint().equals(new DPoint(9,13,0)));
		 assertTrue(bp.getBoundaryEdges().get(1).equals(new DEdge(15,13,0,12,13,0)));
		 assertTrue(bp.getBoundaryEdges().get(1).getStartPoint().equals(new DPoint(12,13,0)));
		 assertTrue(bp.getBoundaryEdges().get(2).equals(new DEdge(15,13,0,16,16,0)));
		 assertTrue(bp.getBoundaryEdges().get(2).getStartPoint().equals(new DPoint(15,13,0)));
		 assertTrue(bp.getBoundaryEdges().get(3).equals(new DEdge(0,13,0,16,16,0)));
		 assertTrue(bp.getBoundaryEdges().get(3).getStartPoint().equals(new DPoint(16,16,0)));
		 assertTrue(bp.getAddedEdges().size()==6);
		 assertTrue(bp.getAddedEdges().contains(new DEdge(15,13,0,16,16,0)));
		 assertTrue(bp.getAddedEdges().contains(new DEdge(12,13,0,16,16,0)));
		 assertTrue(bp.getAddedEdges().contains(new DEdge(9,13,0,16,16,0)));
		 assertTrue(bp.getAddedEdges().contains(new DEdge(6,13,0,16,16,0)));
		 assertTrue(bp.getAddedEdges().contains(new DEdge(3,13,0,16,16,0)));
		 assertTrue(bp.getAddedEdges().contains(new DEdge(0,13,0,16,16,0)));
		 assertTrue(bp.getBadEdges().size()==1);
	}

	public void testBuildFirstBoundary() throws DelaunayError {
		DEdge constr = new DEdge(3,0,0,3,6,0);
		constr.setLocked(true);
		List<BoundaryPart> bpl = new ArrayList<BoundaryPart>();
		DPoint p1 = new DPoint(1,3,0);
		DPoint p2 = new DPoint(3,0,0);
		DEdge e1 = new DEdge(p1, p2);
		e1.setShared(true);
		List<DEdge> edList = new ArrayList<DEdge>();
		edList.add(e1);
		BoundaryPart bp = new BoundaryPart(edList);
		bpl.add(bp);
		edList = new ArrayList<DEdge>();
		edList.add(e1);
		bp = new BoundaryPart(edList, constr);
		bpl.add(bp);
		Boundary bound = new Boundary();
		bound.setBoundary(bpl);
		List<DTriangle> tri = bound.insertPoint(new DPoint(3,6,0));
		assertTrue(tri.size()==1);
		assertTrue(bound.getBoundary().size()==1);
		assertTrue(bound.getBoundary().get(0).getBoundaryEdges().size()==3);
		assertTrue(bound.getBoundary().get(0).getBoundaryEdges().get(0).equals(new DEdge(1,3,0,3,0,0)));
		assertTrue(bound.getBoundary().get(0).getBoundaryEdges().get(1).equals(new DEdge(3,0,0,3,6,0)));
		assertTrue(bound.getBoundary().get(0).getBoundaryEdges().get(2).equals(new DEdge(3,6,0,1,3,0)));
	}

	public void testBuildFirstBoundaryBis() throws DelaunayError {
		//We build the boundary part.
		DEdge constrBis = new DEdge(0,2,0,6,4,0);
		constrBis.setLocked(true);
		BoundaryPart bp = new BoundaryPart(constrBis);
		List<BoundaryPart> edList = new ArrayList<BoundaryPart>();
		edList.add(bp);
		//we build and fill the boundary
		Boundary bound = new Boundary();
		bound.setBoundary(edList);
		//we prepare the constraint.
		DEdge constr = new DEdge(4,0,0,6,4,0);
		constr.setLocked(true);
		List<DEdge> cstrList = new ArrayList<DEdge>();
		cstrList.add(constr);
		bound.insertPoint(new DPoint(4,0,0), cstrList);
		assertTrue(bound.getBoundary().size()==3);
		assertTrue(bound.getBoundary().get(2).getConstraint().equals(new DEdge(0,2,0,6,4,0)));
		assertTrue(bound.getBoundary().get(1).getConstraint().equals(new DEdge(4,0,0,6,4,0)));
		assertTrue(bound.getBoundary().get(0).getConstraint()==null);
		assertTrue(bound.getBoundary().get(0).getBoundaryEdges().get(0).equals(new DEdge(0,2,0,4,0,0)));
		assertTrue(bound.getBoundary().get(1).getBoundaryEdges().get(0).equals(new DEdge(0,2,0,4,0,0)));
		assertTrue(bound.getBoundary().get(2).getBoundaryEdges().isEmpty());
	}

	/**
	 * Performs an insertion on a boundary wher a vertical constraint is shared.
	 * @throws DelaunayError
	 */
	public void testBuildSharedVerticalConstraint () throws DelaunayError {
		List<BoundaryPart> bpList = new ArrayList<BoundaryPart>();
		List<DEdge> edList = new ArrayList<DEdge>();
		//We build the first boundary part and add it to the list of BP
		DEdge constr = new DEdge(0,6,0,6,0,0);
		constr.setLocked(true);
		DEdge ed = new DEdge(0,6,0,3,6,0);
		ed.setShared(true);
		edList.add(ed);
		BoundaryPart bp = new BoundaryPart(edList, constr);
		bpList.add(bp);
		//We build the second boundary part and add it to the list of BP
		edList = new ArrayList<DEdge>();
		edList.add(ed);
		DEdge constrBis = new DEdge(3,6,0,4,9,0);
		constrBis.setLocked(true);
		bp = new BoundaryPart(edList,constrBis);
		bpList.add(bp);
		//we bulid and fill the boundary.
		Boundary bound = new Boundary();
		bound.setBoundary(bpList);
		//We perform the insertion
		List<DEdge> cstrList = new ArrayList<DEdge>();
		List<DTriangle> tri = bound.insertPoint(new DPoint(4,9,0), cstrList);
		//We perform our tests
		assertTrue(tri.size()==1);
		assertTrue(bound.getBoundary().size()==1);
		assertTrue(bound.getBoundary().get(0).getBoundaryEdges().size()==3);
	}

	public void testLowestIsConstraint() throws DelaunayError {
		List<BoundaryPart> bpList = new ArrayList<BoundaryPart>();
		List<DEdge> edList = new ArrayList<DEdge>();
		//we build the first (and only) BoundaryPart. It is built with a constraint
		//and an edge. The constraint is lower thant the edge.
		DEdge constraint = new DEdge(0,0,0,5,3,0);
		constraint.setLocked(true);
		DEdge ed = new DEdge(0,0,0,2,3,0);
		edList.add(ed);
		BoundaryPart bp = new BoundaryPart(edList, constraint);
		bpList.add(bp);
		constraint = new DEdge(0,0,0,5,13,0);
		constraint.setLocked(true);
		bp = new BoundaryPart(constraint);
		bpList.add(bp);
		Boundary bound = new Boundary();
		bound.setBoundary(bpList);
		constraint = new DEdge(3,0,0,5,1,0);
		constraint.setLocked(true);
		edList = new ArrayList<DEdge>();
		edList.add(constraint);
		//And now we add a point that is lower than the constraint DEdge.
		//A constraint begins at this point.
		//It must result in a boundary with three boundary parts :
		// - The lowest without constraint, with one boundary DEdge
		// - the second with the new constraint, that shares the boundary edge with the lowest.
		// - the uppest identical as the one already present.
		List<DTriangle> tri = bound.insertPoint(new DPoint(3,0,0),edList);
		assertTrue(tri.isEmpty());
		assertTrue(bound.getBoundary().size()==4);
		assertNull(bound.getBoundary().get(0).getConstraint());
		assertTrue(bound.getBoundary().get(0).getBoundaryEdges().get(0).equals(new DEdge(0,0,0,3,0,0)));
		assertTrue(bound.getBoundary().get(1).getConstraint().equals(new DEdge(3,0,0,5,1,0)));
		assertTrue(bound.getBoundary().get(1).getBoundaryEdges().get(0).equals(new DEdge(3,0,0,0,0,0)));
		assertTrue(bound.getBoundary().get(1).getBoundaryEdges().get(0).isShared());
		assertFalse(bound.getBoundary().get(1).getBoundaryEdges().get(0).isDegenerated());
		assertTrue(bound.getBoundary().get(2).getConstraint().equals(new DEdge(0,0,0,5,3,0)));
		assertTrue(bound.getBoundary().get(2).getBoundaryEdges().get(0).equals(new DEdge(0,0,0,2,3,0)));
		assertTrue(bound.getBoundary().get(3).getConstraint().equals(new DEdge(0,0,0,5,13,0)));
		
	}

	/**
	 * This test matches TestConstrainedMesh.testCross, and performs the first
	 * point insertion of the mesh building.
	 * @throws DelaunayError
	 */
	public void testCross() throws DelaunayError {
		List<BoundaryPart> bpList = new ArrayList<BoundaryPart>();
		List<DEdge> edList = new ArrayList<DEdge>();
		DEdge ed = new DEdge(0,0,0,0,4,0);
		DEdge constraint = new DEdge(0,0,0,2,2,0);
		constraint.setLocked(true);
		ed.setShared(true);
		edList.add(ed);
		//We build the first boundarypart
		BoundaryPart bp = new BoundaryPart(edList, constraint);
		Boundary bound = new Boundary();
		bpList.add(bp);
		//we build the second BoundaryPart
		constraint = new DEdge(0,4,0,2,2,0);
		constraint.setLocked(true);
		edList = new ArrayList<DEdge>();
		edList.add(ed);
		bp=new BoundaryPart(edList, constraint);
		bpList.add(bp);
		//We fill the boundary
		bound.setBoundary(bpList);
		//We prepare the point insertion.
		edList = new ArrayList<DEdge>();
		constraint = new DEdge(2,2,0,4,0,0);
		constraint.setLocked(true);
		edList.add(constraint);
		constraint = new DEdge(2,2,0,4,4,0);
		constraint.setLocked(true);
		edList.add(constraint);
		//we perform the insertion.
		List<DTriangle> tri = bound.insertPoint(new DPoint(2,2,0), edList);
		assertTrue(tri.size()==1);
		assertTrue(bound.getBoundary().size()==3);
		assertNull(bound.getBoundary().get(0).getConstraint());
		assertTrue(bound.getBoundary().get(0).getBoundaryEdges().get(0).equals(new DEdge(0,0,0,2,2,0)));
		assertTrue(bound.getBoundary().get(1).getConstraint().equals(new DEdge(2,2,0,4,0,0)));
		assertTrue(bound.getBoundary().get(1).getBoundaryEdges().isEmpty());
		assertTrue(bound.getBoundary().get(2).getConstraint().equals(new DEdge(2,2,0,4,4,0)));
		assertTrue(bound.getBoundary().get(2).getBoundaryEdges().size()==2);
		assertTrue(bound.getBoundary().get(2).getBoundaryEdges().get(0).equals(new DEdge(2,2,0,0,4,0)));
		assertTrue(bound.getBoundary().get(2).getBoundaryEdges().get(0).getStartPoint().equals(new DPoint(2,2,0)));
	}

	/**
	 * Get a boundary ready to be tested.
	 * @return
	 */
	private Boundary getExampleBoundary() throws DelaunayError{
		List<BoundaryPart> bpl = new ArrayList<BoundaryPart>();
		BoundaryPart bp;
		DEdge cstr;
		List<DEdge> boundaryEdges;
		Boundary bound = new Boundary();
		//We fill a boundary part, and put it in bpl
		cstr = null;
		boundaryEdges = new ArrayList<DEdge>();
		boundaryEdges.add(new DEdge(0,1,0,3,2,0));
		bp = new BoundaryPart(boundaryEdges);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new DEdge(3,2,0,9,0,0);
		cstr.setLocked(true);
		boundaryEdges = new ArrayList<DEdge>();
		boundaryEdges.add(new DEdge(3,2,0,5,3,0));
		boundaryEdges.add(new DEdge(5,3,0,6,5,0));
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new DEdge(6,5,0,10,4,0);
		cstr.setLocked(true);
		boundaryEdges = new ArrayList<DEdge>();
		boundaryEdges.add(new DEdge(6,5,0,7,7,0));
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new DEdge(7,7,0,10,7,0);
		cstr.setLocked(true);
		boundaryEdges = new ArrayList<DEdge>();
		bp = new BoundaryPart( cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new DEdge(7,7,0,10,9,0);
		cstr.setLocked(true);
		boundaryEdges = new ArrayList<DEdge>();
		boundaryEdges.add(new DEdge(7,7,0,6,10,0));
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new DEdge(6,10,0,9,12,0);
		cstr.setLocked(true);
		boundaryEdges = new ArrayList<DEdge>();
		boundaryEdges.add(new DEdge(6,10,0,5,11,0));
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new DEdge(5,11,0,9,12,0);
		cstr.setLocked(true);
		boundaryEdges = new ArrayList<DEdge>();
		boundaryEdges.add(new DEdge(5,11,0,3,12,0));
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new DEdge(3,12,0,9,12,0);
		cstr.setLocked(true);
		boundaryEdges = new ArrayList<DEdge>();
		boundaryEdges.add(new DEdge(3,12,0,0,13,0));
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
	private Boundary getExampleboundaryBis() throws DelaunayError{
		List<BoundaryPart> bpl = new ArrayList<BoundaryPart>();
		BoundaryPart bp;
		DEdge cstr;
		List<DEdge> boundaryEdges;
		Boundary bound = new Boundary();
		//We fill a boundary part, and put it in bpl
		cstr = null;
		boundaryEdges = new ArrayList<DEdge>();
		boundaryEdges.add(new DEdge(0,0,0,0,4,0));
		bp = new BoundaryPart(boundaryEdges);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new DEdge(0,4,0,11,1,0);
		boundaryEdges = new ArrayList<DEdge>();
		boundaryEdges.add(new DEdge(0,4,0,0,6,0));
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new DEdge(0,6,0,7,7,0);
		boundaryEdges = new ArrayList<DEdge>();
		boundaryEdges.add(new DEdge(0,6,0,0,8,0));
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new DEdge(0,8,0,7,7,0);
		boundaryEdges = new ArrayList<DEdge>();
		boundaryEdges.add(new DEdge(0,8,0,0,10,0));
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new DEdge(0,10,0,7,7,0);
		boundaryEdges = new ArrayList<DEdge>();
		boundaryEdges.add(new DEdge(0,10,0,0,12,0));
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new DEdge(0,12,0,7,7,0);
		boundaryEdges = new ArrayList<DEdge>();
		boundaryEdges.add(new DEdge(0,12,0,0,14,0));
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new DEdge(0,14,0,10,16,0);
		boundaryEdges = new ArrayList<DEdge>();
		boundaryEdges.add(new DEdge(0,14,0,0,16,0));
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
	private Boundary getExampleDegenEdges() throws DelaunayError{
		List<BoundaryPart> bpl = new ArrayList<BoundaryPart>();
		BoundaryPart bp;
		DEdge cstr;
		DEdge ed;
		List<DEdge> boundaryEdges;
		Boundary bound = new Boundary();
		//We fill a boundary part, and put it in bpl
		cstr = new DEdge(0,4,0,8,0,0);
		boundaryEdges = new ArrayList<DEdge>();
		ed = new DEdge(0,4,0,2,4,0);
		ed.setDegenerated(true);
		boundaryEdges.add(ed);
		ed = new DEdge(2,4,0,4,4,0);
		ed.setDegenerated(true);
		boundaryEdges.add(ed);
		bp = new BoundaryPart(boundaryEdges, cstr);
		bpl.add(bp);
		//We fill a boundary part, and put it in bpl
		cstr = new DEdge(0,4,0,8,10,0);
		bp = new BoundaryPart(cstr);
		bpl.add(bp);

		//We set the list of BoundaryPart in bound.
		bound.setBoundary(bpl);
		return bound;
	}
}
