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
