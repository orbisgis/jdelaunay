package org.jhydrocell.hydronetwork;

/**
 * Hydronetwork Package.
 *
 * @author Jean-Yves MARTIN, Erwan BOCHER
 * @date 2009-01-12
 * @version 1.1
 */

public final class HydroProperties {
	// Constraints types
	/**
	 * The object is a wall
	 */
	private static int WALL = 0;
	/**
	 * The object is a sewer
	 */
	private static int SEWER = 0;
	/**
	 * the object is a road
	 */
	private static int ROAD = 0;
	/**
	 * The object is a ditch
	 */
	private static int DITCH = 0;
	/**
	 * The object is a river
	 */
	private static int RIVER = 0;
	/**
	 * The object is a parcel
	 */
	private static int PARCEL = 0;

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

	// Generalities
	public static final int ANY = -1;
	public static final int NONE = 0;

	/**
	 * default constructor is kept private
	 */
	private HydroProperties(){
	}

	/**
	 * return string from int definition
	 * 
	 * @param aType
	 * @return
	 */
	public static String toString(int aType) {
		StringBuilder builder = new StringBuilder();
		for (int i=0; i<64; i++) {
			int property = 1 << i;
			
			if ((aType & property) != 0) {
				// Property is valid
				if (! builder.toString().equals("")) {
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
	 */
	private static String propertyToString(int aProperty) {
		switch (aProperty) {
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
		default:
			return "";
		}
	}

        /**
         * Set the weight associated to the wall property
         * @param weight
         */
        public static void setWallWeight(int weight){
                WALL = weight;
        }

        /**
         * Get the weight associated to the wall property. This value must be between 0 and 1023 !
         * @param weight
         */
        public static int getWallWeight(){
                return WALL;
        }
        /**
         * Set the weight associated to the sewer property. This value must be between 0 and 1023 !
         * @param weight
         */
        public static void setSewerWeight(int weight){
                SEWER = weight;
        }

        /**
         * Get the weight associated to the sewer property. This value must be between 0 and 1023 !
         * @param weight
         */
        public static int getSewerWeight(){
                return WALL;
        }
        /**
         * Set the weight associated to the ditch property. This value must be between 0 and 1023 !
         * @param weight
         */
        public static void setDitchWeight(int weight){
                DITCH = weight;
        }

        /**
         * Get the weight associated to the ditch property. This value must be between 0 and 1023 !
         * @param weight
         */
        public static int getDitchWeight(){
                return WALL;
        }
        /**
         * Set the weight associated to the road property. This value must be between 0 and 1023 !
         * @param weight
         */
        public static void setRoadWeight(int weight){
                 ROAD= weight;
        }

        /**
         * Get the weight associated to the road property. This value must be between 0 and 1023 !
         * @param weight
         */
        public static int getRoadWeight(){
                return WALL;
        }
        /**
         * Set the weight associated to the river property. This value must be between 0 and 1023 !
         * @param weight
         */
        public static void setRiverWeight(int weight){
                 RIVER = weight;
        }

        /**
         * Get the weight associated to the river property. This value must be between 0 and 1023 !
         * @param weight
         */
        public static int getRiverWeight(){
                return WALL;
        }
        /**
         * Set the weight associated to the parcel property. This value must be between 0 and 1023 !
         * @param weight
         */
        public static void setParcelWeight(int weight){
                 PARCEL = weight;
        }

        /**
         * Get the weight associated to the parcel property. This value must be between 0 and 1023 !
         * @param weight
         */
        public static int getParcelWeight(){
                return WALL;
        }

	/**
	 * return int from string definition
	 * 
	 * @param aType
	 * @return
	 */
	public static int fromString(String aType) {
		int i = 0;
		int res = 0;
		boolean found = false;
		while ((i < 64) && (!found)) {
			res = 1 << i;
			if (propertyToString(res).equals(aType)) {
				found = true;
			}
			else {
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

}
