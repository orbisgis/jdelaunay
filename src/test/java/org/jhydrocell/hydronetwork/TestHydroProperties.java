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
public class TestHydroProperties extends BaseUtility {
        
        public void testPropertiesOnEdge() throws DelaunayError {
                DEdge ed = new DEdge(0,0,0,3,3,3);
                ed.setProperty(HydroProperties.SEWER);
                assertTrue(ed.hasProperty(HydroProperties.SEWER));
                String str = HydroProperties.toString(ed.getProperty());
                assertTrue(str.contentEquals("sewer"));
        }

	public void testPropValues() throws DelaunayError {
		assertTrue(HydroProperties.WALL == 1);
		assertTrue(HydroProperties.SEWER == 2);
	}

	public void testAddNoneProp() throws DelaunayError {
		DEdge ed = new DEdge(0,0,0,3,5,0);
		ed.addProperty(HydroProperties.WALL);
		ed.addProperty(HydroProperties.NONE);
		assertTrue(ed.hasProperty(HydroProperties.WALL));
	}

	public void testAddAnyProp() throws DelaunayError {
		DEdge ed = new DEdge(0,0,0,6,5,0);
		ed.addProperty(HydroProperties.WALL);
		ed.addProperty(HydroProperties.ANY);
		assertTrue(ed.hasProperty(HydroProperties.WALL));
	}

	public void testAnyProperty() throws DelaunayError {
		DEdge ed = new DEdge(0,0,0,5,6,0);
		ed.addProperty(HydroProperties.ANY);
		assertTrue(ed.hasProperty(HydroProperties.WALL));
		assertTrue(ed.hasProperty(HydroProperties.SEWER));
		assertTrue(ed.hasProperty(HydroProperties.SEWER_INPUT));
		assertTrue(ed.hasProperty(HydroProperties.SEWER_OUTPUT));
		assertTrue(ed.hasProperty(HydroProperties.DITCH));
		assertTrue(ed.hasProperty(HydroProperties.LEFTCOLINEAR));
	}
        
}
