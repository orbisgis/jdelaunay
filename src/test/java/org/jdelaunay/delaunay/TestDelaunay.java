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

public class TestDelaunay extends BaseUtility {
	/**
	 * Test random generation of points
	 * @throws DelaunayError
	 */
	public void testDelaunayRandomPoints() throws DelaunayError {
		ConstrainedMesh aMesh = new ConstrainedMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		aMesh.setPoints(getRandomPoints(100));

		
		long start = System.currentTimeMillis();
		
		aMesh.processDelaunay();
		long end = System.currentTimeMillis();
		System.out.println("Duration " + (end-start)+"ms");
//		show(aMesh);
		assertTrue(true);
		System.out.println("end");
	}

	/**
	 * Test points at the same location in 3D
	 * Use a predefined set of points and add the first one
	 * The final set of points must be decremented by 1
	 * 
	 * @throws DelaunayError
	 */
	public void testDelaunayDuplicateXYZPoint() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.setPrecision(1.0e-3);
		mesh.setVerbose(true);
		
		ArrayList<DPoint> pts = getPoints();
		DPoint addedPoint = new DPoint(pts.get(1));
		int ptsSize = pts.size();
		mesh.setPoints(pts);
		mesh.addPoint(addedPoint);
//		show(mesh);

		assertTrue(mesh.getPoints().size() == (ptsSize));
	}

	/**
	 * Test points at the same location in 2D
	 * Use a predefined set of points and add the first one
	 * The final set of points must be decremented by 1
	 * @throws DelaunayError
	 */
	public void testDelaunayDuplicateXYPoint() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.setPrecision(1.0e-3);
		mesh.setVerbose(true);
		
		ArrayList<DPoint> pts = getPoints();
		DPoint addedPoint = new DPoint(pts.get(1));
		addedPoint.setZ(addedPoint.getZ() + 10);
		int ptsSize = pts.size();
		
		mesh.setPoints(pts);
		mesh.addPoint(addedPoint);

		assertTrue(mesh.getPoints().size() == (ptsSize ));
	}

	/**
	 * Test points not at the same location in 2D / epsilon
	 * The final set of points must be the same
	 * @throws DelaunayError
	 */
	public void testDelaunayQualification_GoodMesh1() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.setPrecision(1.0e-3);
		mesh.setVerbose(true);

		ArrayList<DPoint> pts = new ArrayList<DPoint>();
                pts.add(new DPoint(0,0,0));
                pts.add(new DPoint(1,0,0));
                pts.add(new DPoint(1,1,0));
                pts.add(new DPoint(0,1,0));
		int ptsSize = pts.size();

		mesh.setPoints(pts);
                mesh.dataQualification(1.0e-5);

		assertTrue(mesh.getPoints().size() == ptsSize);
	}

	/**
	 * Test points not at the same location in 2D / epsilon
         * One point is close to another one but not enough
	 * The final set of points must be the same
	 * @throws DelaunayError
	 */
	public void testDelaunayQualification_GoodMesh2() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.setPrecision(1.0e-3);
		mesh.setVerbose(true);

		ArrayList<DPoint> pts = new ArrayList<DPoint>();
                pts.add(new DPoint(0,0,0));
                pts.add(new DPoint(1,0,0));
                pts.add(new DPoint(1,1,0));
                pts.add(new DPoint(0,1,0));
                pts.add(new DPoint(0.0001,0,0));
		int ptsSize = pts.size();

		mesh.setPoints(pts);
                mesh.dataQualification(1.0e-5);

		assertTrue(mesh.getPoints().size() == ptsSize);
	}

	/**
	 * Test points not at the same location in 2D / epsilon
         * One point is close to another one, enough to be removed
	 * The final set of points must be equal to the initial one -1
	 * @throws DelaunayError
	 */
	public void testDelaunayQualification_1PointDuplicated() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.setPrecision(1.0e-3);
		mesh.setVerbose(true);

		ArrayList<DPoint> pts = new ArrayList<DPoint>();
                pts.add(new DPoint(0,0,0));
                pts.add(new DPoint(1,0,0));
                pts.add(new DPoint(1,1,0));
                pts.add(new DPoint(0,1,0));
                pts.add(new DPoint(0.000001,0,0));
		int ptsSize = pts.size();

		mesh.setPoints(pts);
                mesh.dataQualification(1.0e-5);

		assertTrue(mesh.getPoints().size() == (ptsSize-1));
	}

	/**
	 * Test points not at the same location in 2D / epsilon
         * All points are close to the first one
	 * The final set of points must be equal to 1
	 * @throws DelaunayError
	 */
	public void testDelaunayQualification_AllPointDuplicated() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.setPrecision(1.0e-3);
		mesh.setVerbose(true);

		ArrayList<DPoint> pts = new ArrayList<DPoint>();
                pts.add(new DPoint(0,0,0));
                pts.add(new DPoint(0.000001,0,0));
                pts.add(new DPoint(0.000001,0.000001,0));
                pts.add(new DPoint(0,0.000001,0));
 		int ptsSize = pts.size();

		mesh.setPoints(pts);
                mesh.dataQualification(1.0e-5);

		assertTrue(mesh.getPoints().size() == 1);
	}

	/**
	 * Test points not at the same location in 2D / epsilon
         * One point is close to another one, enough to be removed
         * Data include constraintEdges that does not use the bad point
	 * The final set of points must be equal to iniial - 1
         * Constraind edges may not have changed
	 * @throws DelaunayError
	 */
	public void testDelaunayQualification_ContraintEdgesNoModification() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.setPrecision(1.0e-3);
		mesh.setVerbose(true);

		ArrayList<DPoint> pts = new ArrayList<DPoint>();
                DPoint pt1 = new DPoint(0,0,0);
                DPoint pt2 = new DPoint(0,1,0);
                DPoint pt3 = new DPoint(1,1,0);
                DPoint pt4 = new DPoint(2,1,0);
                DPoint pt5 = new DPoint(1,1.0000001,0);
                pts.add(pt1);
                pts.add(pt2);
                pts.add(pt3);
                pts.add(pt4);
                pts.add(pt5);

                ArrayList<DEdge> constr = new ArrayList<DEdge>();
                constr.add(new DEdge(pt1, pt2));
                constr.add(new DEdge(pt2, pt3));
                constr.add(new DEdge(pt3, pt4));
                int constrSize = constr.size();
                
		mesh.setPoints(pts);
		mesh.setConstraintEdges(constr);
                mesh.dataQualification(1.0e-5);

                ArrayList<DEdge> resList = (ArrayList<DEdge>)mesh.getConstraintEdges();
		assertTrue(resList.size() == constrSize);
                for (int i=0; i<resList.size() ; i++) {
                	DEdge e1 = resList.get(i);
                        DEdge e2 = constr.get(i);
                        assertTrue ((e1.getStartPoint().equals(e2.getStartPoint())) && (e1.getEndPoint().equals(e2.getEndPoint())));
                }
	}

	/**
	 * Test points not at the same location in 2D / epsilon
         * One point is close to another one, enough to be removed
         * onstraintEdges does not include the bad point
	 * The final set of points must be equal to iniial - 1
         * Constraind edges may not have changed except last one
	 * @throws DelaunayError
	 */
	public void testDelaunayQualification_ContraintEdgesModification1() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.setPrecision(1.0e-3);
		mesh.setVerbose(true);

		ArrayList<DPoint> pts = new ArrayList<DPoint>();
                DPoint pt1 = new DPoint(0,0,0);
                DPoint pt2 = new DPoint(0,1,0);
                DPoint pt3 = new DPoint(1,1,0);
                DPoint pt4 = new DPoint(0,1,0);
                DPoint pt5 = new DPoint(1,1.0000001,0);
                pts.add(pt1);
                pts.add(pt2);
                pts.add(pt3);
                pts.add(pt4);
                pts.add(pt5);

                ArrayList<DEdge> constr = new ArrayList<DEdge>();
                constr.add(new DEdge(pt1, pt2));
                constr.add(new DEdge(pt2, pt3));
                constr.add(new DEdge(pt5, pt4));
                int constrSize = constr.size();

		mesh.setPoints(pts);
		mesh.setConstraintEdges(constr);
                mesh.dataQualification(1.0e-5);

                ArrayList<DEdge> resList = (ArrayList<DEdge>)mesh.getConstraintEdges();
		assertTrue(resList.size() == constrSize);
                for (int i=0; i<constrSize-1 ; i++) {
                	DEdge e1 = resList.get(i);
                        DEdge e2 = constr.get(i);
                        assertTrue ((e1.getStartPoint().equals(e2.getStartPoint())) && (e1.getEndPoint().equals(e2.getEndPoint())));
                }
               	DEdge e_err = resList.get(constrSize-1);
//                assertTrue ((e_err.getStartPoint().equals(pt3)) && (e_err.getEndPoint().equals(pt4)));
 	}

	/**
	 * Test points not at the same location in 2D / epsilon
         * One point is close to another one, enough to be removed
         * onstraintEdges includes the bad point
	 * The final set of points must be equal to iniial - 1
         * Constraind edges may not have changed except last one
	 * @throws DelaunayError
	 */
	public void testDelaunayQualification_ContraintEdgesModification2() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.setPrecision(1.0e-3);
		mesh.setVerbose(true);

		ArrayList<DPoint> pts = new ArrayList<DPoint>();
                DPoint pt1 = new DPoint(0,0,0);
                DPoint pt2 = new DPoint(0,1,0);
                DPoint pt3 = new DPoint(1,1,0);
                DPoint pt4 = new DPoint(0,1,0);
                DPoint pt5 = new DPoint(1,1.0000001,0);
                pts.add(pt1);
                pts.add(pt2);
                pts.add(pt3);
                pts.add(pt4);
                pts.add(pt5);

                ArrayList<DEdge> constr = new ArrayList<DEdge>();
                constr.add(new DEdge(pt1, pt2));
                constr.add(new DEdge(pt2, pt3));
                constr.add(new DEdge(pt4, pt5));
                int constrSize = constr.size();

		mesh.setPoints(pts);
		mesh.setConstraintEdges(constr);
                mesh.dataQualification(1.0e-5);

                ArrayList<DEdge> resList = (ArrayList<DEdge>)mesh.getConstraintEdges();
		assertTrue(resList.size() == constrSize);
                for (int i=0; i<constrSize-1 ; i++) {
                	DEdge e1 = resList.get(i);
                        DEdge e2 = constr.get(i);
                        assertTrue ((e1.getStartPoint().equals(e2.getStartPoint())) && (e1.getEndPoint().equals(e2.getEndPoint())));
                }
               	DEdge e_err = resList.get(constrSize-1);
                assertTrue ((e_err.getStartPoint().equals(pt4)) && (e_err.getEndPoint().equals(pt3)));
 	}

 	/**
	 * Test points not at the same location in 2D / epsilon
         * One point is close to another one, enough to be removed
         * onstraintEdges includes the bad point linked to the closest one
	 * The final set of points must be equal to iniial - 1
         * One constraint disappears
	 * @throws DelaunayError
	 */
	public void testDelaunayQualification_EmptyEdge() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		mesh.setPrecision(1.0e-3);
		mesh.setVerbose(true);

		ArrayList<DPoint> pts = new ArrayList<DPoint>();
                DPoint pt1 = new DPoint(0,0,0);
                DPoint pt2 = new DPoint(0,1,0);
                DPoint pt3 = new DPoint(1,1,0);
                DPoint pt4 = new DPoint(0,1,0);
                DPoint pt5 = new DPoint(1,1.0000001,0);
                pts.add(pt1);
                pts.add(pt2);
                pts.add(pt3);
                pts.add(pt4);
                pts.add(pt5);

                ArrayList<DEdge> constr = new ArrayList<DEdge>();
                constr.add(new DEdge(pt1, pt2));
                constr.add(new DEdge(pt2, pt3));
                constr.add(new DEdge(pt3, pt5));
                int constrSize = constr.size();

		mesh.setPoints(pts);
		mesh.setConstraintEdges(constr);
                mesh.dataQualification(1.0e-5);

                ArrayList<DEdge> resList = (ArrayList<DEdge>)mesh.getConstraintEdges();
		assertTrue(resList.size() == constrSize-1);
 	}

        /**
	 * Check if 2 points are linked by two different edges 
	 * @throws DelaunayError
//	 */
//	public void testDelaunayDupplicateEdges() throws DelaunayError {
//		ConstrainedMesh aMesh = new ConstrainedMesh();
//		aMesh.setPrecision(1.0e-3);
//		aMesh.setVerbose(true);
//
//		aMesh.setMax(1300, 700);
//		aMesh.setRandomPoints(1000);
//
//		// process triangularization
//		aMesh.processDelaunay();
//
//		boolean correct = true;
//		ArrayList<Edge> edgeList = aMesh.getEdges();
//
//		for (Edge anEdge:edgeList) {
//			Edge myEdge;
//			DPoint start = anEdge.getStart();
//			DPoint end = anEdge.getEnd();
//
//			ListIterator<Edge> iterEdge = edgeList.listIterator();
//			while ((correct) && (iterEdge.hasNext())) {
//				myEdge = iterEdge.next();
//				if (anEdge != myEdge) {
//					if ((start == myEdge.getStart()) && (end == myEdge.getEnd())) {
//						correct = false;
//					} else if ((end == myEdge.getStart()) && (start == myEdge.getEnd())) {
//						correct = false;
//					}
//				}
//			}
//			assertTrue(correct);
//		}
//	}

	/**
	 * Refine Mesh - test triangles area
	 * @throws DelaunayError
//	 */
//	public void testDelaunayPointsRefinementMaxArea() throws DelaunayError {
//		MyMesh aMesh = new MyMesh();
//		aMesh.setPrecision(1.0e-3);
//		aMesh.setVerbose(true);
//
//		aMesh.setPoints(getPoints());
//
//		aMesh.processDelaunay();
//
//		aMesh.setMaxArea(1000);
//		assertTrue(aMesh.getMaxArea() == 1000);
//
//		aMesh.setRefinment(MyMesh.REFINEMENT_MAX_AREA);
//		aMesh.refineMesh();
////		show(aMesh);
//
//		for (DelaunayTriangle myTriangle : aMesh.getTriangles()) {
//			if (myTriangle.computeArea() > 1000) {
//				assertTrue(false);
//			}
//		}
//		System.out.println("finish");
//	}
}
