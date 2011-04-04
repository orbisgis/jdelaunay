/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jhydrocell.hydronetwork;

import org.jdelaunay.delaunay.BaseUtility;
import org.jdelaunay.delaunay.DEdge;
import org.jdelaunay.delaunay.DelaunayError;

/**
 *
 * @author alexis
 */
public class TestHydroTinBuilder extends BaseUtility{


	public void testMorphologicalQualification() throws DelaunayError {
		DEdge ed = new DEdge(0,0,10,3,5,5);
		ed.setProperty(HydroProperties.WALL);
		DEdge e1 = new DEdge(3,5,5,4,0,3);
		e1.setProperty(HydroProperties.RIVER);
		DEdge e2 = new DEdge(4,0,3,6,6,10);
		e2.setProperty(HydroProperties.LEVEL);
		HydroTINBuilder mesh = new HydroTINBuilder();
		mesh.addConstraintEdge(e2);
		mesh.addConstraintEdge(e1);
		mesh.addConstraintEdge(ed);
		mesh.processDelaunay();
		mesh.morphologicalQualification();
		assertTrue(mesh.getEdges().get(0).hasProperty(HydroProperties.WALL));
		assertTrue(mesh.getEdges().get(0).isLocked());
		assertTrue(mesh.getEdges().get(2).hasProperty(HydroProperties.RIVER));
		assertTrue(mesh.getEdges().get(2).hasProperty(HydroProperties.TALWEG));
		String prop = HydroProperties.toString(mesh.getEdges().get(2).getProperty());
		assertTrue(prop.contentEquals("river,talweg")||prop.contentEquals("talweg,river"));
		assertTrue(mesh.getEdges().get(2).getProperty()==(HydroProperties.RIVER+HydroProperties.TALWEG));
	}
}
