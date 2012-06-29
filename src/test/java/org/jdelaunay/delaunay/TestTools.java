/**
 *
 * jDelaunay is a library dedicated to the processing of Delaunay and constrained
 * Delaunay triangulations from PSLG inputs.
 *
 * This library is developed at French IRSTV institute as part of the AvuPur and Eval-PDU project,
 * funded by the French Agence Nationale de la Recherche (ANR) under contract
 * ANR-07-VULN-01 and ANR-08-VILL-0005-01 .
 *
 * jDelaunay is distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2010-2012 IRSTV FR CNRS 2488
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
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.jdelaunay.delaunay;
import java.util.ArrayList;
import java.util.List;
import org.jdelaunay.delaunay.error.DelaunayError;
import org.jdelaunay.delaunay.geometries.DEdge;
import org.jdelaunay.delaunay.geometries.DPoint;
import org.jdelaunay.delaunay.tools.Tools;

/**
 *
 * @author Alexis Guéganno
 */
public class TestTools extends BaseUtility {
        
        public void testIsColinear() throws DelaunayError {
                DPoint p1 = new DPoint(2,2,0);
                DPoint p2 = new DPoint(3,3,0);
                assertTrue(Tools.isColinear(p1, p2));
                p2 = new DPoint(2,3,0);
                assertFalse(Tools.isColinear(p1, p2));
        }
        

	/**
	 * Test that the methods who checks that a list is vertically sorted works well
	 */
	public void testIsVerticallySorted() throws DelaunayError{
		List<DEdge> list = new ArrayList<DEdge>();
		list.add(new DEdge(0,0,0,2,2,2));
		list.add(new DEdge(0,1,0,2,3,2));
		list.add(new DEdge(0,2,0,2,4,2));
		list.add(new DEdge(0,3,0,2,5,2));
		list.add(new DEdge(0,4,0,2,6,2));
		assertTrue(Tools.isVerticallySorted(list, 1));
		list.add(new DEdge(0,-1,0,2,0,2));
		assertFalse(Tools.isVerticallySorted(list, 1));

	}
}
