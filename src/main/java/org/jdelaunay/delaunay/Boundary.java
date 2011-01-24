package org.jdelaunay.delaunay;

import java.util.ArrayList;
import java.util.List;

/**
 * The boundary of the mesh. During the computation of the mesh, the boundary is
 * considered as a set of parts (BoundaryPart instance) that are sorted vertically.
 * When adding a point, we will check to which part of the boundary it can be linked,
 * by using the vertical sort that has been made before.
 * @author alexis
 */
class Boundary {

        //The boundary
        List<BoundaryPart> boundary;

        Boundary(){
                boundary = new ArrayList<BoundaryPart>();
        }

        /**
         * Retrieve the boundary of the mesh as a list of boundary parts.
         * @return
         */
        List<BoundaryPart> getBoundary(){
                return boundary;
        }

        void setBoundary(List<BoundaryPart> bound){
                boundary = bound;
        }

        /**
         * Connect a new Point to the boundary. This operation will alter the
         * boundary, by potentially add or remove some boundary parts. Moreover,
         * in every cases, at least one BoundaryPart will be modified.
         * @param pt
         */
        void insertPoint(Point pt){
                
        }

}













