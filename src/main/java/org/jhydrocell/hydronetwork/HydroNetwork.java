package org.jhydrocell.hydronetwork;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

import org.jdelaunay.delaunay.DelaunayError;
import org.jdelaunay.delaunay.DEdge;
import org.jdelaunay.delaunay.ConstrainedMesh;
import org.jdelaunay.delaunay.DPoint;
import org.jdelaunay.delaunay.DTriangle;
import org.jhydrocell.utilities.HydroLineUtil;
import org.jhydrocell.utilities.MathUtil;
import org.jhydrocell.utilities.HydroTriangleUtil;
import org.jhydrocell.utilities.HydroPolygonUtil;

/**
 * The representation of a hydrologic network, based on a constrained triangulation.
 * @author alexis, kwyhr
 */
public class HydroNetwork extends ConstrainedMesh {
        // Sewer elements
        private ArrayList<DPoint> listEntryPoints;
        private ArrayList<DPoint> listSewerPoints;
        private ArrayList<DEdge> ListServerEdges;

        /**
         * Global initialization
         */
        private void init() {
                listEntryPoints = new ArrayList<DPoint>();
                listSewerPoints = new ArrayList<DPoint>();
                ListServerEdges = new ArrayList<DEdge>();
        }

        /**
         * Standart constructor
         */
        public HydroNetwork() {
                super();
                init();
        }

        /**
         * Get underground sewer edges
         *
         * @return ListServerEdges
         */
        public ArrayList<DEdge> getSewerEdges() {
                return this.ListServerEdges;
        }

        /**
         * Get sewer entries (points connected to the ssurface)
         *
         * @return listEntryPoints
         */
        public ArrayList<DPoint> getSewerEntries() {
                return this.listEntryPoints;
        }

        /**
         * Get underground and surface sewer points
         *
         * @return listSewerPoints
         */
        public ArrayList<DPoint> getSewerPoints() {
                return this.listSewerPoints;
        }

        // ----------------------------------------------------------------
        
        /**
         * Morphological qualification
         *
         * @throws DelaunayError
         */
        public void morphologicalQualification() throws DelaunayError {
                if (!this.isMeshComputed()) {
                        throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_NOT_GENERATED);
                } else {
                        // Edges : topographic qualifications
                        for (DEdge edge : this.getEdges()) {
                                HydroLineUtil hydroLineUtil = new HydroLineUtil(edge);
                                HydroPolygonUtil hydroPolygonUtil = null;
                                DTriangle aTriangleLeft = edge.getLeft();
                                DTriangle aTriangleRight = edge.getRight();

                                boolean rightTtoEdge = false;
                                boolean rightTColinear = false;
                                boolean righTFlat = false;
                                boolean leftTtoEdge = false;
                                boolean leftTColinear = false;
                                boolean leftTFlat = false;
                                boolean rightBorder = false;
                                boolean leftBorder = false;

                                // Qualification des triangles
                                if (aTriangleRight != null) {
                                        boolean pointeVersEdge = HydroTriangleUtil.getPenteVersEdge(edge, aTriangleRight);
                                        if (pointeVersEdge) {
                                                rightTtoEdge = true;
                                        } else if (HydroTriangleUtil.getSlope(aTriangleRight) > 0) {
                                                if (MathUtil.isColinear(hydroLineUtil.get3DVector(),
                                                        HydroTriangleUtil.get3DVector(aTriangleRight))) {
                                                        rightTColinear = true;
                                                }
                                        } else if (HydroTriangleUtil.getSlope(aTriangleRight) == 0) {
                                                righTFlat = true;
                                        }
                                } else {
                                        rightBorder = true;
                                }

                                if (aTriangleLeft != null) {

                                        boolean pointeVersEdge = HydroTriangleUtil.getPenteVersEdge(edge, aTriangleLeft);

                                        if (pointeVersEdge) {
                                                leftTtoEdge = true;
                                        } else if (HydroTriangleUtil.getSlope(aTriangleLeft) > 0) {
                                                if (MathUtil.isColinear(hydroLineUtil.get3DVector(),
                                                        HydroTriangleUtil.get3DVector(aTriangleLeft))) {
                                                        leftTColinear = true;
                                                }
                                        } else if (HydroTriangleUtil.getSlope(aTriangleLeft) == 0) {
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
                                                edge.getStart().addProperty(HydroProperties.TALWEG);
                                                edge.getEnd().addProperty(HydroProperties.TALWEG);

                                        } // Le triangle de gauche pointe sur l'edge mais pas le
                                        // triangle de droite
                                        else if ((leftTtoEdge && !rightTtoEdge) && !righTFlat) {
                                                edge.addProperty(HydroProperties.RIGHTSLOPE);
                                        } // Le triangle de droite pointe sur l'edge mais pas le
                                        // triangle de gauche
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
                                                edge.getStart().addProperty(HydroProperties.TALWEG);
                                                edge.getEnd().addProperty(HydroProperties.TALWEG);

                                        } // Talweg colineaire droit
                                        else if ((leftTtoEdge && !rightTtoEdge) && rightTColinear) {
                                                edge.addProperty(HydroProperties.RIGHTCOLINEAR);
                                                edge.getStart().addProperty(HydroProperties.TALWEG);
                                                edge.getEnd().addProperty(HydroProperties.TALWEG);

                                        } // Les deux triangles sont colineaires
                                        else if ((!leftTtoEdge && !rightTtoEdge)
                                                && (rightTColinear && leftTColinear)) {
                                                edge.addProperty(HydroProperties.DOUBLECOLINEAR);

                                                edge.getStart().addProperty(HydroProperties.TALWEG);
                                                edge.getEnd().addProperty(HydroProperties.TALWEG);

                                        } // Le reste est plat
                                        else {
                                                edge.addProperty(HydroProperties.FLAT);
                                        }
                                } // Traitement des bords plats
                                else {
                                        edge.addProperty(HydroProperties.BORDER);
                                }
                        }
                }
        }

        /**
         * post process the edges according to their type
         */
        private void postProcessEdges() {
                List<DEdge> addedEdges = new LinkedList<DEdge>();
                List<DEdge> theEdges = this.getEdges();
                for (DEdge anEdge : theEdges) {
                        if (anEdge.hasProperty(HydroProperties.getWallWeight())) {
                                // Process wall : duplicate edge and changes connections
                                if ((anEdge.getLeft() != null) && (anEdge.getRight() != null)) {
                                        // Something to do if and only if there are two triangles
                                        // connected
                                        DEdge newEdge = new DEdge(anEdge);
                                        // Changes left triangle connection
                                        DTriangle aTriangle = anEdge.getLeft();
                                        for (int i = 0; i < 3; i++) {
                                                if (aTriangle.getEdge(i) == anEdge) {
                                                        aTriangle.setEdge(i, newEdge);
                                                }
                                        }

                                        // Changes edges connections
                                        newEdge.setRight(null);
                                        anEdge.setLeft(null);

                                        // add the new edge
                                        addedEdges.add(newEdge);
                                }
                        }
                }

                // add edges to the structure
                for (DEdge anEdge : addedEdges) {
                        theEdges.add(anEdge);
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
                        aPoint.setProperty(hydroProperty);
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
                        aPoint.setProperty(hydroProperty);
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
        public DPoint addSewerEntry(double x, double y, double z)
                throws DelaunayError {
                // Add point to the Mesh
                DPoint sewerPoint = createPointOnSurface(x, y, z, HydroProperties.getSewerWeight());

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
        public DPoint addSewerEntry(DPoint sewerPoint) throws DelaunayError {
                // Add point to the Mesh
                sewerPoint = createPointOnSurface(sewerPoint, HydroProperties.getSewerWeight());

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
        public DPoint addSewerExit(double x, double y, double z)
                throws DelaunayError {
                // Add point to the Mesh
                return addSewerEntry(x, y, z);
        }

        /**
         * Add a sewer exit. The point is added to the list of points of the Mesh.
         * It will be used for the delaunay triangularization.
         *
         * @param sewerPoint
         * @return thePoint : the DPoint
         * @throws DelaunayError
         */
        public DPoint addSewerExit(DPoint sewerPoint) throws DelaunayError {
                // Add point to the Mesh
                return addSewerEntry(sewerPoint);
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
        public DPoint addSewerPoint(double x, double y, double z)
                throws DelaunayError {
                DPoint sewerPoint = null;
                // Create a new point
                if (this.isMeshComputed()) {
                        throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_GENERATED);
                } else {
                        sewerPoint = new DPoint(x, y, z);
                        sewerPoint.setProperty(HydroProperties.getSewerWeight());
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
        public DPoint addSewerPoint(DPoint sewerPoint)
                throws DelaunayError {
                // Create a new point
                if (sewerPoint == null) {
                        throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_ERROR_POINT_XYZ);
                } else if (this.isMeshComputed()) {
                        throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_GENERATED);
                } else {
                        sewerPoint.setProperty(HydroProperties.getSewerWeight());
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
                int theSize = this.ListServerEdges.size();
                int i = 0;
                while ((i < theSize) && (found == null)) {
                        DEdge anEdge = this.ListServerEdges.get(i);
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
        public void setSewerConnection(DPoint sewerPoint1, DPoint sewerPoint2)
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
                                this.ListServerEdges.add(anEdge);

                                // Add sewer property
                                sewerPoint1.setProperty(HydroProperties.getSewerWeight());
                                sewerPoint2.setProperty(HydroProperties.getSewerWeight());
                                anEdge.setProperty(HydroProperties.getSewerWeight());
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
        public void createHydroEdge(DPoint point1, DPoint point2, int hydroProperty)
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
                        point1.setProperty(hydroProperty);
                        point2.setProperty(hydroProperty);
                        anEdge.setProperty(hydroProperty);
                }
        }
        // ----------------------------------------------------------------
}
