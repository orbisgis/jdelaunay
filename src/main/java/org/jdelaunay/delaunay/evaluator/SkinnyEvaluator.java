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

import org.jdelaunay.delaunay.geometries.DTriangle;

/**
 * Evaluator used to determine if a triangle is skinny (ie if it has an angle 
 * inferior to a given value) or not.
 * @author Alexis Guéganno
 */
public class SkinnyEvaluator implements InsertionEvaluator {
        /**
         * The maximum valule that can be used in a skinny evaluator. As the Ruppert
         * algorithm won't end for angles superior to this value, such values are 
         * forbidden.
         */
        public final static double MAX_VALUE = 32;
        private double minAngle;

        /**
         * Instanciate a new SkinnyEvaluator, with the given threshold angle value,
         * in degrees.
         * @param minAngle 
         * @throws IllegalArgumentException
         *      if minAngle is greater than 32° or inferior to 0°
         */
        public SkinnyEvaluator(double minAngle) {
                if(minAngle > MAX_VALUE){
                        throw new IllegalArgumentException("you can't use threshold values greater than 32°");
                }
                if(minAngle < 0){
                        throw new IllegalArgumentException("Threshold value can't be negative");
                }
                this.minAngle = minAngle;
        }

        /**
         * Get the angle that is used to determine if triangles are skinny or not.
         * @return 
         *      The angle limit, in degrees.
         */
        public final double getMinAngle() {
                return minAngle;
        }

        /**
         * Set the minimum angle that will be used to determine if triangles are skinny,
         * for this evaluator, or not.
         * @param minAngle 
         *      The new limit, in degrees.
         * @throws IllegalArgumentException
         *      if minAngle is greater than 32° or inferior to 0°
         */
        public final void setMinAngle(double minAngle) {
                if(minAngle > MAX_VALUE){
                        throw new IllegalArgumentException("you can't use threshold values greater than 32°");
                }
                if(minAngle < 0){
                        throw new IllegalArgumentException("Threshold value can't be negative");
                }
                this.minAngle = minAngle;
        }
        
        @Override
        public final boolean evaluate(DTriangle dt) {
                return minAngle > dt.getMinAngle();
        }
        
}
