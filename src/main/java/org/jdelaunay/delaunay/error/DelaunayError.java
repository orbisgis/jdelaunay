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
package org.jdelaunay.delaunay.error;

/**
 * The exception that is used to describe the kind of errors that can happen when
 * building or using a ConstrainedMesh or its elements in this library.
 * 
 * @author Jean-Yves Martin
 * @author Erwan Bocher
 * @author Alexis Guéganno
 */
public class DelaunayError extends Exception {

        private static final long serialVersionUID = 1L;
        
        // error code saving
        private int code;
        // error codes
        /**
         * An error has been thrown, but it shouldn't happen...
         */
        public static final int DELAUNAY_ERROR_NO_ERROR = 0;
        /**
         * There is no mesh to work with.
         */
        public static final int DELAUNAY_ERROR_NO_MESH = 100;
        /**
         * The mesh has not been generated, and it should have.
         */
        public static final int DELAUNAY_ERROR_NOT_GENERATED = 101;
        /**
         * The mesh has been generated, and it shouldn't.
         */
        public static final int DELAUNAY_ERROR_GENERATED = 102;
        /**
         * We haven't found enough points to process our operation.
         */
        public static final int DELAUNAY_ERROR_NOT_ENOUGH_POINTS_FOUND = 103;
        /**
         * The points are too close.
         */
        public static final int DELAUNAY_ERROR_PROXIMITY = 104;
        /**
         * can't find the asked point.
         */
        public static final int DELAUNAY_ERROR_POINT_NOT_FOUND = 105;
        /**
         * Failed at connecting the point to the mesh.
         */
        public static final int DELAUNAY_ERROR_CAN_NOT_CONNECT_POINT = 106;
        /**
         * Failed at splitting a boundary part.
         */
        public static final int DELAUNAY_ERROR_CAN_NOT_SPLIT_BP = 107;
        /**
         * Failed at splitting a boundary part.
         */
        public static final int DELAUNAY_ERROR_MESH_ALREADY_EXISTS = 108;
        /**
         * The point has not been inserted during the triangulation.
         */
        public static final int DELAUNAY_ERROR_NON_INSERTED_POINT = 200;
        /**
         * The topology is wrong.
         */
        public static final int DELAUNAY_ERROR_INCORRECT_TOPOLOGY = 201;
        public static final int DELAUNAY_ERROR_OUTSIDE_TRIANGLE = 202;
        /**
         * Failed at removing an edge.
         */
        public static final int DELAUNAY_ERROR_REMOVING_EDGE = 203;
        /**
         *
         */
        public static final int DELAUNAY_ERROR_ERROR_POINT_XYZ = 300;
        /**
         * Bad call to a method
         */
        public static final int DELAUNAY_ERROR_INVALID_CALL = 998;
        /**
         * Inner error.
         */
        public static final int DELAUNAY_ERROR_INTERNAL_ERROR = 999;
        /**
         * Other error
         */
        public static final int DELAUNAY_ERROR_MISC = 1000;
        private String message = "";

        /**
         * Default constructor, the associated message is DELAUNAY_INTERNAL_ERROR
         */
        public DelaunayError() {
                super();
                code = DELAUNAY_ERROR_INTERNAL_ERROR;
        }

        /**
         * DelaunayError instanciated with a custom message. The inner error code
         * is DELAUNAY_EROR_MISC
         * @param s
         */
        public DelaunayError(String s) {
                super(s);
                message = s;
                code = DELAUNAY_ERROR_MISC;
        }

        /**
         * DelaunayError created with the wanted error code.
         * @param errorCode
         */
        public DelaunayError(int errorCode) {
                super();
                code = errorCode;
        }

        /**
         * DelaunayError created with both a custom message and a given error code.
         * @param errorCode
         * @param s
         */
        public DelaunayError(int errorCode, String s) {
                super();
                message = s;
                code = errorCode;
        }
        /*
         * (non-Javadoc)
         *
         * @see java.lang.Throwable#getMessage()
         */

        @Override
        public final String getMessage() {
                String ret;
                switch (code) {
                        case DELAUNAY_ERROR_NO_ERROR:
                                ret = "no error";
                                break;
                        case DELAUNAY_ERROR_NO_MESH:
                                ret = "no mesh found to start process";
                                break;
                        case DELAUNAY_ERROR_GENERATED:
                                ret = "triangulation has already been processed";
                                break;
                        case DELAUNAY_ERROR_NOT_GENERATED:
                                ret = "triangulation has not yet been processed";
                                break;
                        case DELAUNAY_ERROR_NOT_ENOUGH_POINTS_FOUND:
                                ret = "not enough points found to triangularize";
                                break;
                        case DELAUNAY_ERROR_PROXIMITY:
                                ret = "distance between the two points is too small";
                                break;
                        case DELAUNAY_ERROR_POINT_NOT_FOUND:
                                ret = "point not found";
                                break;
                        case DELAUNAY_ERROR_CAN_NOT_CONNECT_POINT:
                                ret = "Can't connect the point to the boundary";
                                break;
                        case DELAUNAY_ERROR_CAN_NOT_SPLIT_BP:
                                ret = "Can't split this boundary part";
                                break;
                        case DELAUNAY_ERROR_MESH_ALREADY_EXISTS:
                                ret = "mesh already defined";
                                break;
                        case DELAUNAY_ERROR_NON_INSERTED_POINT:
                                ret = "one point is not inserted in the triangularization";
                                break;
                        case DELAUNAY_ERROR_INCORRECT_TOPOLOGY:
                                ret = "Incorrect topology";
                                break;
                        case DELAUNAY_ERROR_OUTSIDE_TRIANGLE:
                                ret = "point is outside the triangle";
                                break;
                        case DELAUNAY_ERROR_REMOVING_EDGE:
                                ret = "Problem while removing an edge";
                                break;
                        case DELAUNAY_ERROR_ERROR_POINT_XYZ:
                                ret = "point should have X, Y and Z coordinates";
                                break;
                        case DELAUNAY_ERROR_INVALID_CALL:
                                ret = "Invalid function call";
                                break;
                        case DELAUNAY_ERROR_INTERNAL_ERROR:
                                ret = "internal error, please call support";
                                break;
                        default:
                                return message;
                }
                if (message.isEmpty()) {
                        return ret;
                } else {
                        return ret + ", " + message;
                }
        }
}
