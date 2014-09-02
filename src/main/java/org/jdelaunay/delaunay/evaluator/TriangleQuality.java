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
 *  Triangle quality evaluation. Better results than {@link org.jdelaunay.delaunay.evaluator.SkinnyEvaluator}
 *  @see "Bank, Randolph E., PLTMG: A Software Package for Solving Elliptic Partial Differential Equations, User's Guide 6.0,
 *  Society for Industrial and Applied Mathematics, Philadelphia, PA, 1990."
 * @author Nicolas Fortin
 */
public class TriangleQuality implements InsertionEvaluator {
    private final double targetQuality;
    public static final double DEFAULT_QUALITY = 0.6;
    private static final double SQRT3 = Math.sqrt(3.);

    /**
     * Default constructor
     */
    public TriangleQuality() {
        targetQuality = DEFAULT_QUALITY;
    }

    /**
     * Constructor with defined quality
     * @param targetQuality Quality ratio
     */
    public TriangleQuality(double targetQuality) {
        this.targetQuality = targetQuality;
    }

    @Override
    public boolean evaluate(DTriangle dTriangle) {
        return (4 * dTriangle.getArea() * SQRT3) / (
                Math.pow(dTriangle.getEdge(0).get2DLength(), 2) +
                        Math.pow(dTriangle.getEdge(1).get2DLength(), 2) +
                        Math.pow(dTriangle.getEdge(2).get2DLength(), 2)) < targetQuality;
    }
}