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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdelaunay.delaunay.display.MeshDrawer;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import junit.framework.TestCase;

public class BaseUtility extends TestCase {

	// ---------------------------------------------------------------------------
	// Utilities
	/**
	 * Generate an array of points
	 * @return
	 * @throws DelaunayError 
	 */
	public static ArrayList<DPoint> getPoints() throws DelaunayError {
		ArrayList<DPoint> points = new ArrayList<DPoint>();
		points.add(new DPoint(12, 10, 2));
		points.add(new DPoint(120, 10, 20));
		points.add(new DPoint(12, 100, 12));
		points.add(new DPoint(102, 100, 1));
		points.add(new DPoint(52, 100, 1));
		points.add(new DPoint(10, 50, 5));
		points.add(new DPoint(50, 50, 1));
		points.add(new DPoint(150, 50, 11));
		points.add(new DPoint(50, 150, 2));
		points.add(new DPoint(5, 50, 3));
		points.add(new DPoint(5, 5, 10));

		return points;
	}

	/**
	 * Generate an array of edges
	 * @return
	 * @throws DelaunayError 
	 */
	public static ArrayList<DEdge> getBreaklines() throws DelaunayError {
		ArrayList<DEdge> edges = new ArrayList<DEdge>();
		edges.add(new DEdge(new DPoint(12, 10, 2), new DPoint(102, 100, 1)));
		edges.add(new DEdge(new DPoint(50, 10, 2), new DPoint(10, 10, 1)));
		edges.add(new DEdge(new DPoint(120, 10, 2), new DPoint(102, 10, 1)));

		return edges;
	}

	/**
	 * Get a list of number points, randomly generated.
	 * @param number
	 * @return
	 * @throws DelaunayError
	 */
	public static List<DPoint> getRandomPoints(int number) throws DelaunayError {
		ArrayList<DPoint> rand = new ArrayList<DPoint>();
		double abs;
		double ord;
		for(int i =0; i< number; i++){
			abs = Math.random()*100;
			ord = Math.random()*100;
			rand.add(new DPoint(abs, ord, 0));
		}
		return rand;
	}

	/**
	 * show Mesh in 2D
	 * @param myMesh
	 */
	public static void show(ConstrainedMesh myMesh) {
		MeshDrawer aff2 = new MeshDrawer();
		aff2.add(myMesh);
                aff2.setVisible(true);
                try {
                        System.in.read();
                } catch (IOException ex) {
                        Logger.getLogger(BaseUtility.class.getName()).log(Level.WARNING, null, ex);
                }
	}

	// ---------------------------------------------------------------------------
	// Assertions
	/**
	 * Check coherence
	 * An edge is made of 2 different points
	 * A DTriangle is made of 3 different edges
	 * @param aMesh
	 */
	public void assertCoherence(ConstrainedMesh aMesh) {
		// Assert edges correctly defined
		boolean correct = true;
		DEdge myEdge;
		ListIterator<DEdge> iterEdge = aMesh.getEdges().listIterator();
		while ((correct) && (iterEdge.hasNext())) {
			myEdge = iterEdge.next();
			if (myEdge.getStart() == null) {
				correct = false;
			} else if (myEdge.getEnd() == null) {
				correct = false;
			} else if (myEdge.getStart() == myEdge.getEnd()) {
				correct = false;
			}
			assertTrue(correct);
		}

		// Assert triangles correctly defined
		DTriangle myTriangle;
		ListIterator<DTriangle> iterTriangle = aMesh.getTriangleList().listIterator();
		while ((correct) && (iterTriangle.hasNext())) {
			myTriangle = iterTriangle.next();
			for (int i=0; i<3; i++) {
				if (myTriangle.getEdge(i) == null) {
					correct = false;
				}
			}
			assertTrue(correct);
		}
		
		iterTriangle = aMesh.getTriangleList().listIterator();
		while ((correct) && (iterTriangle.hasNext())) {
			myTriangle = iterTriangle.next();
			for (int i=0; i<3; i++) {
				for (int j=0; j<3; j++) {
					if (j != i && myTriangle.getEdge(i) == myTriangle.getEdge(j)) {
							correct = false;
					}
				}
			}
			assertTrue(correct);
		}
	}

	/**
	 * Check if each point is used in the Mesh
	 * Check that a point belongs to one edge
	 * @param aMesh
	 */
	public void assertUseEachPoint(ConstrainedMesh aMesh) {
		// Assert
		for (DPoint aPoint : aMesh.getPoints()) {
			// point must belong to an edge
			int GID = aPoint.getGID();
			
			boolean found = false;
			DEdge myEdge;
			ListIterator<DEdge> iterEdge = aMesh.getEdges().listIterator();
			while ((! found) && (iterEdge.hasNext())) {
				myEdge = iterEdge.next();
				if (GID == myEdge.getStart().getGID()) {
					found = true;
				} else if (GID == myEdge.getEnd().getGID()) {
					found = true;
				}
			}
			assertTrue(found);
		}
	}

	/**
	 * Check if each edge is used in the Mesh
	 * Check that an edge belongs to one triangle
	 * @param aMesh
	 */
	public void assertUseEachEdge(ConstrainedMesh aMesh)  {
		// Assert
		for (DEdge anEdge : aMesh.getEdges()) {
			// point must belong to an edge
			int GID = anEdge.getGID();
			
			boolean found = false;
			DTriangle myTriangle;
			ListIterator<DTriangle> iterTriangle = aMesh.getTriangleList().listIterator();
			while ((! found) && (iterTriangle.hasNext())) {
				myTriangle = iterTriangle.next();
				for (int i=0; i<3; i++) {
					if (GID == myTriangle.getEdge(i).getGID()) {
						found = true;
					}
				}
			}
			assertTrue(found);
		}
	}

	/**
	 * Check if 2 points are linked by two different edges 
	 * @param aMesh
	 */
	public void assertDupplicateEdges(ConstrainedMesh aMesh) {
	}
	
	/**
	 * test GIDs
	 * Check GIDs for DPoint, Edges and Triangles
	 * GIDs must exist for each element (GID >= 0).
	 * GIDs are unique for each kind of element.
	 *
	 * @param aMesh
	 */
	public void assertGIDUnicity(ConstrainedMesh aMesh) {
		ArrayList<Integer> gids;
		
		// Test points
		gids = new ArrayList<Integer>();
		for (DPoint myPoint : aMesh.getPoints()) {
			int gid = myPoint.getGID();

			if ((gids.contains(gid)) || (gid < 0)) {
				assertTrue(false);
			} else {
				gids.add(gid);
			}
		}

		// Test edges
		gids = new ArrayList<Integer>();
		for (DEdge myEdge : aMesh.getEdges()) {
			int gid = myEdge.getGID();

			if ((gids.contains(gid)) || (gid < 0)) {
				assertTrue(false);
			} else {
				gids.add(gid);
			}
		}

		// Test triangles
		gids= new ArrayList<Integer>();
		for (DTriangle myTriangle : aMesh.getTriangleList()) {
			int gid = myTriangle.getGID();

			if ((gids.contains(gid)) || (gid < 0)) {
				assertTrue(false);
			} else {
				gids.add(gid);
			}
		}
	}

	public void assertConstraintsAreLocked(ConstrainedMesh mesh){
		for(DEdge ed : mesh.getConstraintEdges()){
			if(!ed.isLocked()){
				assertTrue(false);
			}
		}
		assertTrue(true);
	}

}
