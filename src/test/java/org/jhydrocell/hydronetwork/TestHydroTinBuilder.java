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

	public void testMorphologicalQualifDeeper() throws DelaunayError {
		DEdge e1 = new DEdge(0,0,3,4,2,3);
		DEdge e2 = new DEdge(2,1,5,6,3,8);
		DEdge e3 = new DEdge(2,2,4,6,0,3);
		DEdge e4 = new DEdge(6,2,3,9,2,7);
		e1.setProperty(HydroProperties.WALL);
		e2.setProperty(HydroProperties.WALL);
		e3.setProperty(HydroProperties.WALL);
		e4.setProperty(HydroProperties.WALL);
		HydroTINBuilder mesh = new HydroTINBuilder();
		mesh.addConstraintEdge(e1);
		mesh.addConstraintEdge(e2);
		mesh.addConstraintEdge(e3);
		mesh.addConstraintEdge(e4);
		mesh.forceConstraintIntegrity();
		for(DEdge ed : mesh.getConstraintEdges()){
			assertTrue(ed.hasProperty(HydroProperties.WALL));
		}
		assertTrue(mesh.getConstraintEdges().size()==7);
		mesh.processDelaunay();
		int i =0;
		for(DEdge ed : mesh.getEdges()){
			if(mesh.getConstraintEdges().contains(ed)){
				assertTrue(ed.hasProperty(HydroProperties.WALL));
				i++;
			}
		}
		assertTrue(i==7);
		mesh.morphologicalQualification();
		i =0;
		for(DEdge ed : mesh.getEdges()){
			if(mesh.getConstraintEdges().contains(ed)){
				assertTrue(ed.hasProperty(HydroProperties.WALL));
				i++;
			}
		}
		assertTrue(i==7);
	}
}
