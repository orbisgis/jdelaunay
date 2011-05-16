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

import java.util.ArrayList;
import java.util.List;

import org.jdelaunay.delaunay.DelaunayError;
import org.jdelaunay.delaunay.DEdge;
import org.jdelaunay.delaunay.ConstrainedMesh;
import org.jdelaunay.delaunay.DPoint;
import org.jdelaunay.delaunay.DTriangle;
import org.jdelaunay.delaunay.Element;
import org.jdelaunay.delaunay.Tools;

/**
 * The representation of a hydrologic network, based on a constrained triangulation.
 * @author alexis, kwyhr
 */
public class HydroTINBuilder extends ConstrainedMesh {
        // Sewer elements

        private ArrayList<DPoint> listEntryPoints;
        private ArrayList<DPoint> listSewerPoints;
        private ArrayList<DEdge> listServerEdges;

        /**
         * Global initialization
         */
        private void init() {
                listEntryPoints = new ArrayList<DPoint>();
                listSewerPoints = new ArrayList<DPoint>();
                listServerEdges = new ArrayList<DEdge>();
        }

        /**
         * Standart constructor
         */
        public HydroTINBuilder() {
                super();
                init();
        }

        /**
         * Get underground sewer edges
         *
         * @return ListServerEdges
         */
        public final List<DEdge> getSewerEdges() {
                return this.listServerEdges;
        }

        /**
         * Get sewer entries (points connected to the ssurface)
         *
         * @return listEntryPoints
         */
        public final List<DPoint> getSewerEntries() {
                return this.listEntryPoints;
        }

        /**
         * Get underground and surface sewer points
         *
         * @return listSewerPoints
         */
        public final List<DPoint> getSewerPoints() {
                return this.listSewerPoints;
        }

        // ----------------------------------------------------------------        
        /**
         * Morphological qualification
         *
         * @throws DelaunayError
         */
        public final void morphologicalQualification() throws DelaunayError {
                if (!this.isMeshComputed()) {
                        throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_NOT_GENERATED);
                } else {
                        // Edges : topographic qualifications
                        for (DEdge edge : this.getEdges()) {
                                edge.forceTopographicOrientation();
                                DTriangle aTriangleLeft = edge.getLeft();
                                DTriangle aTriangleRight = edge.getRight();
                                boolean edgeIsFlat = edge.isFlatSlope();

                                boolean rightTtoEdge = false;
                                boolean rightTColinear = false;
                                boolean righTFlat = false;
                                boolean leftTtoEdge = false;
                                boolean leftTColinear = false;
                                boolean leftTFlat = false;
                                boolean rightBorder = false;
                                boolean leftBorder = false;

                                //On ajoutte l'information pour savoir si l'edge est plat.
                                if (edgeIsFlat) {
                                        edge.addProperty(HydroProperties.FLAT);
                                }

                                // Qualification des triangles
                                if (aTriangleRight != null) {
                                        boolean pointeVersEdge = aTriangleRight.isTopoOrientedToEdge(edge);
                                        if (pointeVersEdge) {
                                                rightTtoEdge = true;
                                        } else if (aTriangleRight.getSlope() > 0) {
                                                if (Tools.isColinear(edge.getDirectionVector(),
                                                        aTriangleRight.getSteepestVector())) {
                                                        rightTColinear = true;
                                                }
                                        } else if (aTriangleRight.getSlope() == 0) {
                                                righTFlat = true;
                                        }
                                } else {
                                        rightBorder = true;
                                }

                                if (aTriangleLeft != null) {
                                        boolean pointeVersEdge = aTriangleLeft.isTopoOrientedToEdge(edge);
                                        if (pointeVersEdge) {
                                                leftTtoEdge = true;
                                        } else if (aTriangleLeft.getSlope() > 0) {
                                                if (Tools.isColinear(edge.getDirectionVector(),
                                                        aTriangleLeft.getSteepestVector())) {
                                                        leftTColinear = true;
                                                }
                                        } else if (aTriangleLeft.getSlope() == 0) {
                                                leftTFlat = true;
                                        }

                                } else {
                                        leftBorder = true;
                                }

                                // Recupération des noeuds associés à l'edge

                                // Qualification de la pente de l'edge parcouru
                                if (!leftBorder && !rightBorder) {
                                        // Traitement des ridges
                                        if ((!rightTtoEdge && !leftTtoEdge)
                                                && (!righTFlat && !leftTFlat)) {
                                                edge.addProperty(HydroProperties.RIDGE);
                                        } // Cas des talwegs
                                        else if (rightTtoEdge && leftTtoEdge) {
                                                edge.addProperty(HydroProperties.TALWEG);
                                                if (edgeIsFlat){
                                                        edge.addProperty(HydroProperties.FLAT);
                                                }
                                        } // Le triangle de gauche pointe sur l'edge mais pas le
                                        // triangle de droite qui descend
                                        else if ((leftTtoEdge && !rightTtoEdge) && !righTFlat) {
                                                edge.addProperty(HydroProperties.RIGHTSLOPE);
                                        } // Le triangle de droite pointe sur l'edge mais pas le
                                        // triangle de gauche qui descend
                                        else if ((rightTtoEdge && !leftTtoEdge) && (!leftTFlat)) {
                                                edge.addProperty(HydroProperties.LEFTTSLOPE);
                                        } // Traitement du rebord droit
                                        else if ((!rightTtoEdge && !leftTtoEdge)
                                                && (!leftTFlat && righTFlat)) {
                                                edge.addProperty(HydroProperties.LEFTSIDE);
                                        } // Traitement du rebord gauche
                                        else if ((!leftTtoEdge && !rightTtoEdge)
                                                && (!righTFlat && leftTFlat)) {
                                                edge.addProperty(HydroProperties.RIGHTSIDE);
                                        } // Traitement du fond gauche
                                        else if ((rightTtoEdge && !leftTtoEdge)
                                                && (leftTFlat && !righTFlat)) {
                                                edge.addProperty(HydroProperties.LEFTWELL);
                                        } // Traitement du fond droit
                                        else if ((!rightTtoEdge && leftTtoEdge)
                                                && (!leftTFlat && righTFlat)) {
                                                edge.addProperty(HydroProperties.RIGHTWELL);
                                        } // Cas particulier des talwegs colineaires
                                        // Talweg colineaire gauche
                                        else if ((!leftTtoEdge && rightTtoEdge) && leftTColinear) {
                                                edge.addProperty(HydroProperties.LEFTCOLINEAR);

                                        } // Talweg colineaire droit
                                        else if ((leftTtoEdge && !rightTtoEdge) && rightTColinear) {
                                                edge.addProperty(HydroProperties.RIGHTCOLINEAR);

                                        } // Les deux triangles sont colineaires
                                        else if ((!leftTtoEdge && !rightTtoEdge)
                                                && (rightTColinear && leftTColinear)) {
                                                edge.addProperty(HydroProperties.DOUBLECOLINEAR);

                                        } // Le reste est plat
                                        else if (leftTFlat && righTFlat) {
                                                edge.addProperty(HydroProperties.FLAT);
                                        } else {
                                                edge.addProperty(HydroProperties.NONE);
                                        }
                                } // Traitement des bords plats
                                else {
                                        edge.addProperty(HydroProperties.BORDER);
                                }
                        }
                }
        }

        // ----------------------------------------------------------------
        /**
         * Create a new point on the Mesh surface. Set the hydroProperty to the point.
         *
         * @param x
         * @param y
         * @param z
         * @param hydroProperty
         * @return thePoint the DPoint point created
         * @throws DelaunayError
         */
        private DPoint createPointOnSurface(double x, double y, double z, int hydroProperty)
                throws DelaunayError {
                DPoint aPoint = null;
                if (this.isMeshComputed()) {
                        throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_GENERATED);
                } else {
                        aPoint = this.getPoint(x, y, z);
                        if (aPoint == null) {
                                // Point is not in the Messh.
                                // Create point and add it to the Mesh point list
                                aPoint = new DPoint(x, y, z);
                                this.addPoint(aPoint);
                        }
                        aPoint.addProperty(hydroProperty);
                }
                return aPoint;
        }

        /**
         * Create a new point on the Mesh surface. Set the hydroProperty to the point.
         *
         * @param aPoint
         * @param hydroProperty
         * @return thePoint : the DPoint
         * @throws DelaunayError
         */
        private DPoint createPointOnSurface(DPoint aPoint, int hydroProperty)
                throws DelaunayError {
                // Search for the point
                if (aPoint == null) {
                        throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_ERROR_POINT_XYZ);
                } else if (this.isMeshComputed()) {
                        throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_GENERATED);
                } else {
                        if (!this.getPoints().contains(aPoint)) {
                                // Point is not in the Mesh.
                                // Add point to Mesh the point list
                                this.addPoint(aPoint);
                        }
                        aPoint.addProperty(hydroProperty);
                }
                return aPoint;
        }

        /**
         * Add a sewer entry. The point is added to the list of points of the Mesh.
         * It will be used for the delaunay triangularization.
         *
         * @param x
         * @param y
         * @param z
         * @return thePoint : the DPoint
         * @throws DelaunayError
         */
        public final DPoint addSewerEntry(double x, double y, double z)
                throws DelaunayError {
                // Add point to the Mesh
                DPoint sewerPoint = createPointOnSurface(x, y, z, HydroProperties.SEWER_INPUT);

                // And add it to the sewer points
                this.listEntryPoints.add(sewerPoint);
                this.listSewerPoints.add(sewerPoint);

                return sewerPoint;
        }

        /**
         * Add a sewer entry. The point is added to the list of points of the Mesh.
         * It will be used for the delaunay triangularization.
         *
         * @param sewerPoint
         * @return thePoint : the DPoint
         * @throws DelaunayError
         */
        public final DPoint addSewerEntry(DPoint sewerPoint) throws DelaunayError {
                // Add point to the Mesh
                sewerPoint = createPointOnSurface(sewerPoint, HydroProperties.SEWER_INPUT);

                // And add it to the sewer points
                this.listEntryPoints.add(sewerPoint);
                this.listSewerPoints.add(sewerPoint);

                return sewerPoint;
        }

        /**
         * Add a sewer exit. The point is added to the list of points of the Mesh.
         * It will be used for the delaunay triangularization.
         *
         * @param x
         * @param y
         * @param z
         * @return thePoint : the DPoint
         * @throws DelaunayError
         */
        public final DPoint addSewerExit(double x, double y, double z)
                throws DelaunayError {
                // Add point to the Mesh
                DPoint sewerPoint = createPointOnSurface(x, y, z, HydroProperties.SEWER_OUTPUT);
                // And add it to the sewer points
                this.listEntryPoints.add(sewerPoint);
                this.listSewerPoints.add(sewerPoint);

                return sewerPoint;
        }

        /**
         * Add a sewer exit. The point is added to the list of points of the Mesh.
         * It will be used for the delaunay triangularization.
         *
         * @param sewerPoint
         * @return thePoint : the DPoint
         * @throws DelaunayError
         */
        public final DPoint addSewerExit(DPoint sewerPoint) throws DelaunayError {
                sewerPoint = createPointOnSurface(sewerPoint, HydroProperties.SEWER_OUTPUT);
                // And add it to the sewer points
                this.listEntryPoints.add(sewerPoint);
                this.listSewerPoints.add(sewerPoint);

                return sewerPoint;
        }

        /**
         * Add a sewer point (neither start or exit). The point is not on the
         * surface. It is not added to the Mesh Points. It will not be used in
         * the Delaunay triangularization
         *
         * @param x
         * @param y
         * @param z
         * @return thePoint : the DPoint
         * @throws DelaunayError
         */
        public final DPoint addSewerPoint(double x, double y, double z)
                throws DelaunayError {
                DPoint sewerPoint = null;
                // Create a new point
                if (this.isMeshComputed()) {
                        throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_GENERATED);
                } else {
                        sewerPoint = new DPoint(x, y, z);
                        sewerPoint.setProperty(HydroProperties.SEWER);
                        this.listSewerPoints.add(sewerPoint);
                }
                return sewerPoint;
        }

        /**
         * Add a sewer point (neither start or exit). The point is not on the
         * surface. It is not added to the Mesh Points. It will not be used in
         * the Delaunay triangularization
         *
         * @param x
         * @param y
         * @param z
         * @return thePoint : the DPoint
         * @throws DelaunayError
         */
        public final DPoint addSewerPoint(DPoint sewerPoint)
                throws DelaunayError {
                // Create a new point
                if (sewerPoint == null) {
                        throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_ERROR_POINT_XYZ);
                } else if (this.isMeshComputed()) {
                        throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_GENERATED);
                } else {
                        sewerPoint.addProperty(HydroProperties.SEWER);
                        this.listSewerPoints.add(sewerPoint);
                }
                return sewerPoint;
        }

        /**
         * Look for a sewer that already connects the two points
         *
         * @param sewerPoint1
         * @param sewerPoint2
         * @return theEdge : the edge that connects the two sewer points, null if it does not exist
         */
        private DEdge searchSewerEdge(DPoint sewerPoint1, DPoint sewerPoint2) {
                DEdge found = null;

                // Process edges until we find it
                int theSize = this.listServerEdges.size();
                int i = 0;
                while ((i < theSize) && (found == null)) {
                        DEdge anEdge = this.listServerEdges.get(i);
                        // Points can be start or end of the edge
                        if ((sewerPoint1 == anEdge.getStart()) && (sewerPoint2 == anEdge.getEnd())) {
                                found = anEdge;
                        } else if ((sewerPoint2 == anEdge.getStart()) && (sewerPoint1 == anEdge.getEnd())) {
                                found = anEdge;
                        } else {
                                i++;
                        }
                }
                return found;
        }

        /**
         * Connect two sewer points.  Add SEWER property to the points and to the edge.
         *
         * @param sewerPoint1
         * @param sewerPoint2
         * @throws DelaunayError
         */
        public final void setSewerConnection(DPoint sewerPoint1, DPoint sewerPoint2)
                throws DelaunayError {
                if (sewerPoint1 == null) {
                        throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_ERROR_POINT_XYZ);
                } else if (sewerPoint2 == null) {
                        throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_ERROR_POINT_XYZ);
                } else if (!this.listSewerPoints.contains(sewerPoint1)) {
                        throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_ERROR_POINT_XYZ);
                } else if (!this.listSewerPoints.contains(sewerPoint2)) {
                        throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_ERROR_POINT_XYZ);
                } else {
                        if (searchSewerEdge(sewerPoint1, sewerPoint2) == null) {
                                // Create edge and add it to the sewer edge list
                                DEdge anEdge = new DEdge(sewerPoint1, sewerPoint2);
                                this.listServerEdges.add(anEdge);

                                // Add sewer property
                                sewerPoint1.addProperty(HydroProperties.SEWER);
                                sewerPoint2.addProperty(HydroProperties.SEWER);
                                anEdge.addProperty(HydroProperties.SEWER);
                        }
                }
        }

        // ----------------------------------------------------------------
        /**
         * Look for an edge that already connects the two points.
         *
         * @param point1
         * @param point2
         * @return theEdge : the edge that connects the two points, null if it does not exist
         */
        private DEdge searchEdge(DPoint point1, DPoint point2) {
                DEdge found = null;

                // Process edges until we find it
                int theSize = this.getEdges().size();
                int i = 0;
                while ((i < theSize) && (found == null)) {
                        DEdge anEdge = this.getEdges().get(i);
                        if ((point1 == anEdge.getStart()) && (point2 == anEdge.getEnd())) {
                                found = anEdge;
                        } else if ((point2 == anEdge.getStart()) && (point1 == anEdge.getEnd())) {
                                found = anEdge;
                        } else {
                                i++;
                        }
                }
                return found;
        }

        /**
         * connect two points to build a hydro Edge. Add property to the points and to the edge.
         *
         * @param point1
         * @param point2
         * @param hydroProperty
         * @throws DelaunayError
         */
        public final void createHydroEdge(DPoint point1, DPoint point2, int hydroProperty)
                throws DelaunayError {
                if (point1 == null) {
                        throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_ERROR_POINT_XYZ);
                } else if (point2 == null) {
                        throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_ERROR_POINT_XYZ);
                } else if (!this.listSewerPoints.contains(point1)) {
                        throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_ERROR_POINT_XYZ);
                } else if (!this.listSewerPoints.contains(point2)) {
                        throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_ERROR_POINT_XYZ);
                } else {
                        DEdge anEdge = searchEdge(point1, point2);
                        if (anEdge == null) {
                                // Edge does not exist => create it
                                anEdge = new DEdge(point1, point2);

                                // And add it to the constraints
                                this.addEdge(anEdge);
                        }

                        // Add property
                        point1.addProperty(hydroProperty);
                        point2.addProperty(hydroProperty);
                        anEdge.addProperty(hydroProperty);
                }
        }
        // ----------------------------------------------------------------

        /**
         * Find the triangle the point projects in (only x and y values are used).
         * SHOULD BE OPIMISED WITH RTREES.
         *
         * @param aPoint
         * @return theTriangle : the triangle the point projects in. null if there is none.
         */
        private DTriangle findPointProjectionIn(DPoint aPoint) {
                DTriangle found = null;

                // Process edges until we find it
                int theSize = this.getTriangleList().size();
                int i = 0;
                while ((i < theSize) && (found == null)) {
                        DTriangle aTriangle = this.getTriangleList().get(i);
                        if (aTriangle.contains(aPoint)) {
                                found = aTriangle;
                        } else {
                                i++;
                        }
                }

                return found;
        }

        // Undocummented method
        public final DTriangle publicFindPointProjectionIn(DPoint aPoint) {
                return findPointProjectionIn(aPoint);
        }

        /**
         * give the edge that leads to geatest slope when turning around aPoint
         *
         * @param aPoint an extremity of an edge
         * @param anElement
         * @return the edge that leads to geatest slope
         */
        private DEdge turnAroundthePoint(DPoint aPoint, Element anElement) {
                DEdge selectedEdge = null;

                // NOTE :
                // If the point is on the mesh border, Turning aroung will lead currentElement to null.
                // So, we must restart at first element and turn around the unprocessed elements.

                Element firstElement = anElement;       // The element we start with
                Element currentElement = anElement;     // The current ellement we process
                Element previousElement = null;         // The element before currentElement
                Element secondElement = null;           // First not null element that is not firstElement

                double maxSlope = 0;                    // Current value of geatest slope

                boolean firstIteration = true;
                boolean processRestarted = false;
                while (((currentElement != firstElement) || (firstIteration)) && (currentElement != null)) {
                        if ((secondElement == null) && (currentElement != firstElement)) {
                                secondElement = currentElement;
                        }
                        firstIteration = false;
                        if (currentElement instanceof DTriangle) {

                                // CurrentElement is a triangle that contains aPoint
                                // We go to the next edge of the Triangle
                                // that contains aPoint and is not the previous one
                                boolean found = false;
                                DTriangle aTriangle = (DTriangle) currentElement;
                                int i = 0;
                                while ((i < DTriangle.PT_NB) && (!found)) {
                                        DEdge possibleEdge = aTriangle.getEdge(i);
                                        if (possibleEdge != previousElement) {
                                                // The edge is not the previous one
                                                if ((possibleEdge.getStart() == aPoint)
                                                        || (possibleEdge.getEnd() == aPoint)) {
                                                        // The edge contains aPoint => it is the one we look for.
                                                        previousElement = currentElement;
                                                        currentElement = possibleEdge;
                                                        found = true;
                                                } else {
                                                        i++;
                                                }
                                        } else {
                                                i++;
                                        }
                                }
                        } else {
                                // currentElement is an edge that contains aPoint
                                // We take it as a solution if the slope value of the edge
                                // is greater than minZ
                                DEdge theEdge = (DEdge) currentElement;

                                // Get point that is lower than aPoint
                                double theSlope = theEdge.getSlope();
                                if (theEdge.getEnd() == aPoint) {
                                        theSlope = -theSlope;
                                }

                                if (theSlope < maxSlope) {
                                        maxSlope = theSlope;
                                        selectedEdge = theEdge;
                                }

                                // We go to next element which is the triangle
                                // that is not the previous one
                                if (theEdge.getLeft() == (DTriangle) previousElement) {
                                        previousElement = currentElement;
                                        currentElement = theEdge.getRight();
                                } else {
                                        previousElement = currentElement;
                                        currentElement = theEdge.getLeft();
                                }

                                if ((currentElement == null) && (!processRestarted)) {
                                        // Ok, we are on the border
                                        // We did not restart the process => we can do it once
                                        processRestarted = true;
                                        currentElement = firstElement;
                                        previousElement = secondElement;
                                        firstIteration = true;
                                }

                        }
                }
                return selectedEdge;
        }

        /**
         * intersects the edge and the half-line that start in p1 directed with v1.
         * returns null if there is no intersection
         *
         * @param p1 a point inside a triangle
         * @param v1 triangle's slope
         * @param anEdge an edge of the triangle
         * @return intersection
         */
        private static DPoint getIntersection(DPoint point1, DPoint v1, DEdge anEdge) throws DelaunayError {
                DPoint intersection = null;
                DPoint p3 = anEdge.getPointLeft();
                DPoint p4 = anEdge.getPointRight();
                DPoint p1 = point1;

                // (v1.x) t1 - (x4 - x3) t2 = (x3 - x1)
                // (v1.y) t1 - (y4 - y3) t2 = (y3 - y1)

                double deltaXO = v1.getX();
                double deltaXT = p4.getX() - p3.getX();
                double c1 = p3.getX() - p1.getX();
                double deltaYO = v1.getY();
                double deltaYT = p4.getY() - p3.getY();
                double c2 = p3.getY() - p1.getY();

                // d = (x4 - x3) (y2 - y1) - (x2 - x1) * (y4 - y3)
                double d = deltaXT * deltaYO - deltaYT * deltaXO;
                if (Math.abs(d) > Tools.EPSILON) {
                        //The two edges are not colinear.
                        // t1 = ((y3 - y1) (x4 - x3) - (x3 - x1) (y4 - y3)) / d
                        // t2 = ((v1.x) (y3 - y1) - (v1.y) (x3 - x1)) / d

                        double t1 = (c2 * deltaXT - c1 * deltaYT) / d;
                        double t2 = (deltaXO * c2 - deltaYO * c1) / d;

                        // There is no upper limit to t1 value
                        if ((-Tools.EPSILON <= t1) && (-Tools.EPSILON <= t2) && (t2 <= 1 + Tools.EPSILON)) {
                                // it intersects
                                if (t2 <= Tools.EPSILON) {
                                        intersection = p3;
                                } else if (t2 >= 1 - Tools.EPSILON) {
                                        intersection = p4;
                                } else if (t1 <= Tools.EPSILON) {
                                        intersection = p1;
                                } else {
                                        // We use t2 to compute values
                                        // x = x4 t2 + (1 - t2) x3
                                        // y = y4 t2 + (1 - t2) y3
                                        // z = z4 t2 + (1 - t2) z3
                                        double x = p4.getX() * t2 + (1 - t2) * p3.getX();
                                        double y = p4.getY() * t2 + (1 - t2) * p3.getY();
                                        double z = p4.getZ() * t2 + (1 - t2) * p3.getZ();

                                        intersection = new DPoint(x, y, z);
                                }
                        }
                } else {
                        //d==0 : the two edges are colinear
                        double test;
                        if (Math.abs(deltaXO) < Tools.EPSILON2) {
                                test = c1 / deltaXT - c2 / deltaYT;
                        } else {
                                test = c1 / deltaXO - c2 / deltaYO;
                        }
                        if (Math.abs(test) > Tools.EPSILON) {
                                //the two supporting lines are different
                                intersection = null;
                        } else {
                                // we have one supporting line
                                // So, p1 is between p3 and p4.
                                intersection = p1;
                        }

                }
                return intersection;
        }
             
}
