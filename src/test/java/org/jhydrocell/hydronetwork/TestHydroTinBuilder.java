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
