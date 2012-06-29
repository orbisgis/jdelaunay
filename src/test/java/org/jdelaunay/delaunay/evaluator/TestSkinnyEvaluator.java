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
package org.jdelaunay.delaunay.evaluator;

import junit.framework.TestCase;
import org.jdelaunay.delaunay.geometries.DEdge;
import org.jdelaunay.delaunay.geometries.DTriangle;
import org.jdelaunay.delaunay.error.DelaunayError;

/**
 * Class used to test the skinny evaluator.
 * @author Alexis Guéganno
 */
public class TestSkinnyEvaluator extends TestCase{
        
        /**
         * Basic tests upon instanciation.
         */
        public void testInstanciationGetSet(){
                SkinnyEvaluator se = new SkinnyEvaluator(20);
                assertTrue(se.getMinAngle() == 20);
                se.setMinAngle(15);
                assertTrue(se.getMinAngle() == 15);
        }     
        
        /**
         * Users are not supposed to be allowed to put a threshold value greater 
         * than 32. If they try, an exception must be thrown.
         */
        public void testThresholdValueException(){
                try{
                        SkinnyEvaluator se = new SkinnyEvaluator(33);
                        assertTrue(false);
                } catch (IllegalArgumentException e){
                        assertTrue(true);
                }
                try{
                        SkinnyEvaluator se = new SkinnyEvaluator(-1);
                        assertTrue(false);
                } catch (IllegalArgumentException e){
                        assertTrue(true);
                }
                SkinnyEvaluator se = new SkinnyEvaluator(20);
                try {
                        se.setMinAngle(33);
                        assertTrue(false);
                } catch (IllegalArgumentException e) {
                        assertTrue(true);
                }
                try {
                        se.setMinAngle(-1);
                        assertTrue(false);
                } catch (IllegalArgumentException e) {
                        assertTrue(true);
                }
        }
        
        public void testIsSkinny() throws DelaunayError {
                SkinnyEvaluator se = new SkinnyEvaluator(10);
                DTriangle dt = new DTriangle(new DEdge(0,1,0,3,4,0),
                        new DEdge(3,4,0,4,0,0), new DEdge(0,1,0,4,0,0));
                assertFalse(se.evaluate(dt));
                dt = new DTriangle(new DEdge(0,0,0,0,1,0), new DEdge(0,1,0,10,0,0), new DEdge(10,0,0,0,0,0));
                assertTrue(se.evaluate(dt));
        }
}
