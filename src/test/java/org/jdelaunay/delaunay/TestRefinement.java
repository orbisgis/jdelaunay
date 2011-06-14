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

import java.util.Collections;
import java.util.List;
import org.jdelaunay.delaunay.evaluator.SkinnyEvaluator;

/**
 * This class gathers some tests related to the mesh refinement.
 * @author alexis
 */
public class TestRefinement extends BaseUtility {
        
        
        public void testRefinementThreeConstraints() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		DEdge constr = new DEdge(4,0,0,4,6,0);
		mesh.addConstraintEdge(constr);
		constr = new DEdge(7,6,0,3,7,0);
		mesh.addConstraintEdge(constr);
		constr = new DEdge(0,2,0,3,7,0);
		mesh.addConstraintEdge(constr);
		mesh.processDelaunay();
                List<DEdge> edges = mesh.getEdges();
                SkinnyEvaluator se = new SkinnyEvaluator(15);
                mesh.refineMesh(0.01, se);
//                show(mesh);
                List<DEdge> cons = mesh.getConstraintEdges();
                edges = mesh.getEdges();
                for(DEdge ed : cons){
                        int ind = edges.indexOf(ed);
                        //To avoid any surprise about references, we make an affressive
                        //check here
                        assertTrue(ind>=0);
                        assertTrue(ed == edges.get(ind));
                }
                assertTrue(cons.size()==7);
                assertTrue(cons.contains(new DEdge(0,2,0,1.5,4.5,0)));
                assertTrue(cons.contains(new DEdge(1.5,4.5,0,3,7,0)));
                assertTrue(cons.contains(new DEdge(3,7,0,4,6.75,0)));
                assertTrue(cons.contains(new DEdge(4,6.75,0,5,6.5,0)));
                assertTrue(cons.contains(new DEdge(5,6.5,0,7,6,0)));
                assertTrue(cons.contains(new DEdge(4,0,0,4,3,0)));
                assertTrue(cons.contains(new DEdge(4,3,0,4,6,0)));
                List<DTriangle> tris = mesh.getTriangleList();
                assertTrue(tris.size()==10);
                assertTrue(tris.contains(new DTriangle(
                                new DEdge(0,2,0,4,0,0), 
                                new DEdge(4,0,0,4,3,0), 
                                new DEdge(4,3,0,0,2,0))));
                assertTrue(tris.contains(new DTriangle(
                                new DEdge(0,2,0,4,3,0), 
                                new DEdge(4,3,0,1.5,4.5,0), 
                                new DEdge(1.5,4.5,0,0,2,0))));
                assertTrue(tris.contains(new DTriangle(
                                new DEdge(4,6,0,4,3,0), 
                                new DEdge(4,3,0,1.5,4.5,0), 
                                new DEdge(1.5,4.5,0,4,6,0))));
                assertTrue(tris.contains(new DTriangle(
                                new DEdge(4,6,0,3,7,0), 
                                new DEdge(3,7,0,1.5,4.5,0), 
                                new DEdge(1.5,4.5,0,4,6,0))));
                assertTrue(tris.contains(new DTriangle(
                                new DEdge(4,6,0,3,7,0), 
                                new DEdge(3,7,0,4,6.75,0), 
                                new DEdge(4,6.75,0,4,6,0))));
                assertTrue(tris.contains(new DTriangle(
                                new DEdge(4,6,0,5,6.5,0), 
                                new DEdge(5,6.5,0,4,6.75,0), 
                                new DEdge(4,6.75,0,4,6,0))));
                assertTrue(tris.contains(new DTriangle(
                                new DEdge(4,6,0,5,6.5,0), 
                                new DEdge(5,6.5,0,5.5,3,0), 
                                new DEdge(5.5,3,0,4,6,0))));
                assertTrue(tris.contains(new DTriangle(
                                new DEdge(7,6,0,5,6.5,0), 
                                new DEdge(5,6.5,0,5.5,3,0), 
                                new DEdge(5.5,3,0,7,6,0))));
                assertTrue(tris.contains(new DTriangle(
                                new DEdge(4,6,0,4,3,0), 
                                new DEdge(4,3,0,5.5,3,0), 
                                new DEdge(5.5,3,0,4,6,0))));
                assertTrue(tris.contains(new DTriangle(
                                new DEdge(4,0,0,4,3,0), 
                                new DEdge(4,3,0,5.5,3,0), 
                                new DEdge(5.5,3,0,4,0,0))));
                assertCoherence(mesh);
                assertGIDUnicity(mesh);
                assertTrianglesTopology(mesh);
        }
        
        public void testRefineManyConstraints() throws DelaunayError {
		ConstrainedMesh mesh = new ConstrainedMesh();
		DEdge constr = new DEdge(0,3,0,8,3,0);
		mesh.addConstraintEdge(constr);
		constr = new DEdge(9,0,0,9,6,0);
		mesh.addConstraintEdge(constr);
		constr = new DEdge(12,6,0,8,7,0);
		mesh.addConstraintEdge(constr);
		constr = new DEdge(5,4,0,8,7,0);
		mesh.addConstraintEdge(constr);
		constr = new DEdge(12,6,0,12,7,0);
		mesh.addConstraintEdge(constr);
		constr = new DEdge(8,3,0,9,6,0);
		mesh.addConstraintEdge(constr);
		constr = new DEdge(8,7,0,12,12,0);
		mesh.addConstraintEdge(constr);
		mesh.addPoint(new DPoint(4,5,0));
		mesh.addPoint(new DPoint(4,1,0));
		mesh.addPoint(new DPoint(10,3,0));
		mesh.addPoint(new DPoint(11,9,0));
		mesh.processDelaunay();
                SkinnyEvaluator se = new SkinnyEvaluator(14);
                mesh.refineMesh(1, se);
//                show(mesh);
                assertTrue(true);
                assertTrianglesTopology(mesh);
                
                
        }
        
        /**
         * Tests that we throw the wanted exception when handling the refinement methods.
         * @throws DelaunayError 
         */
        public void testRefinementException() throws DelaunayError {
                ConstrainedMesh mesh = new ConstrainedMesh();
                mesh.addPoint(new DPoint(0,0,0));
                mesh.addPoint(new DPoint(3,0,0));
                mesh.addPoint(new DPoint(0,3,0));
                mesh.processDelaunay();
                SkinnyEvaluator se = new SkinnyEvaluator(15);
                try {
                        mesh.refineMesh(-20, se);
                        assertTrue(false);
                } catch (IllegalArgumentException e) {
                        assertTrue(true);
                }
                
        }
        
        /**
         * When adding a point during the refinement operation, we must be sure to
         * obtain, as the z-coordinate of our new point, the interpolation of a value made
         * in the triangle that contains the point. If this triangle is different 
         * fomr the triangle that has this point as its circumcenter, we definitely
         * must not keep the z-coordinate of the circumcenter.
         * In this test, the circumcenter of the triangle we want to refine is 
         * (5,4). Its z-coordinate must be 0, and not -15 (which is the original
         * z coordinate of this point as the circumcenter of the triangle
         * (0 4 10, 2 0 0, 2 8 0)
         * @throws DelaunayError 
         */
        public void testRefinemenHeight() throws DelaunayError {
                ConstrainedMesh mesh = new ConstrainedMesh();
                mesh.addPoint(new DPoint(0,4,10));
                mesh.addPoint(new DPoint(2,8,0));
                mesh.addPoint(new DPoint(2,0,0));
                mesh.addPoint(new DPoint(11,4,0));
                mesh.processDelaunay();
                int index = mesh.getTriangleList().indexOf(new DTriangle(new DPoint(0,4,10),new DPoint(2,0,0),new DPoint(2,8,0)));
                DTriangle tri = mesh.getTriangleList().get(index);
                mesh.insertTriangleCircumCenter(tri, false, 0.2);
                assertFalse(mesh.getPoints().contains(new DPoint(5,4,-15)));
                assertTrue(mesh.getPoints().contains(new DPoint(5,4,0)));
                assertTrianglesTopology(mesh);
                
        }
        
        /**
         * The same goal as in testRefinementHeight, but in this case the point is on
         * an edge of the mesh.
         * @throws DelaunayError 
         */
        
        public void testRefinementHeightBis() throws DelaunayError {
                ConstrainedMesh mesh = new ConstrainedMesh();
                mesh.addPoint(new DPoint(0,2,0));
                mesh.addPoint(new DPoint(5,2,0));
                mesh.addPoint(new DPoint(2,4,10));
                mesh.addPoint(new DPoint(2,0,0));
                mesh.processDelaunay();
                assertTrianglesTopology(mesh);
                int index = mesh.getTriangleList().indexOf(new DTriangle(new DPoint(2,4,10),new DPoint(2,0,0),new DPoint(0,2,0)));
                DTriangle tri = mesh.getTriangleList().get(index);
                mesh.insertTriangleCircumCenter(tri, false, 0.2);
                assertTrue(mesh.getPoints().contains(new DPoint(2,2,5)));
//                show(mesh);
                assertTrianglesTopology(mesh);
        }
        
        /**
         * A configuration from the contour line of the Chezine river. A NullPointerException
         * was thrown, due to a mismanagement of the edge-triangles association
         * during the triangle refinement.
         * @throws DelaunayError 
         */
        public void testRefinementChezine() throws DelaunayError {
                ConstrainedMesh mesh = new ConstrainedMesh();
                //Upper part.
                mesh.addConstraintEdge(new DEdge (      0, 19, 0, 
                                                        2, 20, 0));
                mesh.addConstraintEdge(new DEdge (      2, 20, 0, 
                                                        6, 23, 0));
                //lower part.
                mesh.addConstraintEdge(new DEdge (      3, 0, 0, 
                                                        6, 3, 0));
                mesh.addConstraintEdge(new DEdge (      6, 3, 0,  
                                                        12, 4, 0));
                mesh.processDelaunay();
                mesh.refineMesh(1, new SkinnyEvaluator(15));
                Collections.sort(mesh.getTriangleList());
//                show(mesh);
                assertCoherence(mesh);
                assertTrue(true);
//                2.929521276595745,13.69813829787234,0.0
        }
        
        /**
         * A configuration from the contour line of the Chezine river. A NPE
         * was thrown, due to a mismanagement of the data structures while trying to add
         * a point on an existing edge of the mesh. This has been fixed, by ensuring we
         * associate well the new edges to their triangles.
         * @throws DelaunayError 
         */
        public void testRefinementChezine2() throws DelaunayError {
                ConstrainedMesh mesh = new ConstrainedMesh();
                mesh.addConstraintEdge(new DEdge (0,  0,   0, 7,  9,  0));
                mesh.addConstraintEdge(new DEdge (7,  9,   0, 22, 20, 0));
                mesh.addConstraintEdge(new DEdge (7,  59,  0, 14, 55, 0));
//                mesh.addConstraintEdge(new DEdge (12,60,  0, 14, 55, 0));
                mesh.processDelaunay();
                mesh.edgeSplitting(1);
                assertTrianglesTopology(mesh);
//                show(mesh);
                mesh.refineMesh(1, new SkinnyEvaluator(20));
                assertCoherence(mesh);
                assertTrue(true);
                
        }
        
        /**
         * A configuration from the contour line of the Chezine river.
         * @throws DelaunayError 
         */
        public void testRefinementChezine3() throws DelaunayError {
                ConstrainedMesh mesh = new ConstrainedMesh();
                mesh.addConstraintEdge(new DEdge (0,  142, 0, 10, 143, 0));
                mesh.addConstraintEdge(new DEdge (10,  18, 0, 20,   0, 0));
                mesh.addConstraintEdge(new DEdge (10, 143, 0, 20, 149, 0));
                mesh.processDelaunay();
                mesh.edgeSplitting(1);
                assertTrianglesTopology(mesh);
//                show(mesh);
                mesh.refineMesh(.5, new SkinnyEvaluator(15));
                assertTrianglesTopology(mesh);
                assertTrue(true);
        }
}
