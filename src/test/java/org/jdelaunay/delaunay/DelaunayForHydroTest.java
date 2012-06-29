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

import java.util.List;
import org.jdelaunay.delaunay.error.DelaunayError;
import org.jdelaunay.delaunay.geometries.DTriangle;



public class DelaunayForHydroTest extends BaseUtility {

	/**
	 * After removing flat triangles the triangulation is not conform to the
	 * delaunay criterion
	 *
	 * @throws DelaunayError
	 */
	public void testRemoveFlatTriangles() throws DelaunayError {

		ConstrainedMesh aMesh = new ConstrainedMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		aMesh.setPoints(getPoints());
		aMesh.processDelaunay();

		List<DTriangle> triangles = aMesh.getTriangleList();

		int nbFlat = 0;
		for (DTriangle myTriangle : triangles) {

			if (myTriangle.isFlatSlope()) {
				nbFlat++;
			}
		}

		assertTrue(nbFlat > 0);
		aMesh.removeFlatTriangles();
		triangles = aMesh.getTriangleList();

		nbFlat = 0;
		for (DTriangle myTriangle : triangles) {

			if (myTriangle.isFlatSlope()) {
				nbFlat++;
			}
		}
		assertTrue(nbFlat == 0);
	}

}
