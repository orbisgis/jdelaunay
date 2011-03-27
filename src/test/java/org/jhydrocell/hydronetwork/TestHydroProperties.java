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
        
}
