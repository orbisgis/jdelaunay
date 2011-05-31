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

/**
 * Hydronetwork Package.
 *
 * @author Jean-Yves MARTIN, Erwan BOCHER
 */
public final class HydroProperties {

        // ------------------------------------------------
        // Constraints types
        // The object is a wall
        public static final int WALL = 1;
        // The object is a sewer
        public static final int SEWER = 1 << 1;
        // the object is a road
        public static final int ROAD = 1 << 2;
        // The object is a ditch
        public static final int DITCH = 1 << 3;
        // The object is a river
        public static final int RIVER = 1 << 4;
        // The object is an urban parcel
        public static final int URBAN_PARCEL = 1 << 5;
        // The object is a rural parcel
        public static final int RURAL_PARCEL = 1 << 6;
        // The object is a level line
        public static final int LEVEL = 1 << 7;
        // The object is a sewer input
        public static final int SEWER_INPUT = 1 << 8;
        // The object is a sewer output
        public static final int SEWER_OUTPUT = 1 << 9;
        // ------------------------------------------------
        // Topographic types
        public static final int RIDGE = 1 << 10;
        public static final int TALWEG = 1 << 11;
        public static final int RIGHTSLOPE = 1 << 12;
        public static final int LEFTTSLOPE = 1 << 13;
        public static final int RIGHTSIDE = 1 << 14;
        public static final int LEFTSIDE = 1 << 15;
        public static final int RIGHTWELL = 1 << 16;
        public static final int LEFTWELL = 1 << 17;
        public static final int LEFTCOLINEAR = 1 << 18;
        public static final int RIGHTCOLINEAR = 1 << 19;
        public static final int DOUBLECOLINEAR = 1 << 20;
        public static final int FLAT = 1 << 21;
        public static final int BORDER = 1 << 22;
        // ------------------------------------------------
        // Generalities
        public static final int ANY = -1;
        public static final int NONE = 0;
        // ------------------------------------------------
        // Qualifications
        public static final String QUALIFTOPOGRAPHIC = "topographic";
        public static final String QUALIFMORPHOLOGIC = "morphologic";
        public static final String QUALIFTOPOLOGIC = "topologic";
        public static final String QUALIFANY = "any";
        public static final String QUALIFNONE = "none";
        // ------------------------------------------------

        /**
         * default constructor is kept private
         */
        private HydroProperties() {
        }

        /**
         * return string from int definition
         *
         * @param aType
         * @return
         * Trnasforms aType to a String.
         */
        public static String toString(int aType) {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < 32; i++) {
                        int property = 1 << i;

                        if ((aType & property) != 0) {
                                // Property is valid
                                if (!builder.toString().contentEquals("")) {
                                        builder.append(",");
                                }
                                builder.append(propertyToString(property));
                        }
                }
                return builder.toString();
        }

        /**
         * Generate String value for a property
         *
         * @param aProperty
         * @return aString
         * the string representation of aProperty
         */
        private static String propertyToString(int aProperty) {
                switch (aProperty) {
                        case WALL:
                                return "wall";
                        case SEWER:
                                return "sewer";
                        case SEWER_INPUT:
                                return "sewer input";
                        case SEWER_OUTPUT:
                                return "sewer output";
                        case ROAD:
                                return "road";
                        case DITCH:
                                return "ditch";
                        case RIVER:
                                return "river";
                        case URBAN_PARCEL:
                                return "urban parcel";
                        case RURAL_PARCEL:
                                return "rural parcel";
                        case LEVEL:
                                return "level line";
                        case RIDGE:
                                return "ridge";
                        case TALWEG:
                                return "talweg";
                        case RIGHTSLOPE:
                                return "right slope";
                        case LEFTTSLOPE:
                                return "left slope";
                        case RIGHTSIDE:
                                return "right side";
                        case LEFTSIDE:
                                return "left side";
                        case RIGHTWELL:
                                return "right well";
                        case LEFTWELL:
                                return "left well";
                        case RIGHTCOLINEAR:
                                return "right colinear";
                        case LEFTCOLINEAR:
                                return "left colinear";
                        case DOUBLECOLINEAR:
                                return "double colinear";
                        case FLAT:
                                return "flat";
                        case BORDER:
                                return "border";
                        case NONE:
                                return "none";
                        default:
                                return "";
                }
        }

        /**
         * return int from string definition
         *
         * @param aType
         * @return
         * The type associated to the string representation.
         */
        public static int fromString(String aType) {
                int i = 0;
                int res = 0;
                boolean found = false;
                while ((i < 32) && (!found)) {
                        res = 1 << i;
                        if (propertyToString(res).equals(aType)) {
                                found = true;
                        } else {
                                i++;
                        }
                }
                if (!found) {
                        res = 0;
                }
                return res;
        }

        /**
         * Check if type matches aType
         *
         * @param type
         * @param aType
         */
        public static boolean check(int type, String aType) {
                return ((type & fromString(aType)) != 0);
        }

        /**
         * Check if type matches aType
         *
         * @param type
         * @param aType
         */
        public static boolean check(int type, int aType) {
                return ((type & aType) != 0);
        }

        /**
         * Get property qualification
         * 
         * @param aProperty
         * @return
         * A string that determines if aProperty is topologic, topographic, or something else.
         */
        public static String getPropertyQualification(int aProperty) {
                switch (aProperty) {
                        case ANY:
                                return QUALIFANY;
                        case BORDER:
                                return QUALIFTOPOLOGIC;
                        case DITCH:
                                return QUALIFMORPHOLOGIC;
                        case DOUBLECOLINEAR:
                                return QUALIFTOPOGRAPHIC;
                        case FLAT:
                                return QUALIFTOPOGRAPHIC;
                        case LEFTCOLINEAR:
                                return QUALIFTOPOGRAPHIC;
                        case LEFTSIDE:
                                return QUALIFTOPOGRAPHIC;
                        case LEFTTSLOPE:
                                return QUALIFTOPOGRAPHIC;
                        case LEFTWELL:
                                return QUALIFTOPOGRAPHIC;
                        case LEVEL:
                                return QUALIFMORPHOLOGIC;
                        case NONE:
                                return QUALIFNONE;
                        case RIDGE:
                                return QUALIFTOPOGRAPHIC;
                        case RIGHTCOLINEAR:
                                return QUALIFTOPOGRAPHIC;
                        case RIGHTSIDE:
                                return QUALIFTOPOGRAPHIC;
                        case RIGHTSLOPE:
                                return QUALIFTOPOGRAPHIC;
                        case RIGHTWELL:
                                return QUALIFTOPOGRAPHIC;
                        case RIVER:
                                return QUALIFMORPHOLOGIC;
                        case ROAD:
                                return QUALIFMORPHOLOGIC;
                        case RURAL_PARCEL:
                                return QUALIFMORPHOLOGIC;
                        case SEWER:
                                return QUALIFMORPHOLOGIC;
                        case SEWER_INPUT:
                                return QUALIFMORPHOLOGIC;
                        case SEWER_OUTPUT:
                                return QUALIFMORPHOLOGIC;
                        case TALWEG:
                                return QUALIFTOPOGRAPHIC;
                        case URBAN_PARCEL:
                                return QUALIFMORPHOLOGIC;
                        case WALL:
                                return QUALIFMORPHOLOGIC;
                }
                return QUALIFNONE;
        }
}
