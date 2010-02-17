package org.jhydrocell.hydronetwork;

/**
 * Hydronetwork Package.
 *
 * @author Jean-Yves MARTIN, Erwan BOCHER
 * @date 2009-01-12
 * @version 1.1
 */

public class ConstraintType {

	static final public int WALL = 1;
	static final public int SEWER = 2;
	static final public int ROAD = 4;
	static final public int DITCH = 8;
	static final public int RIVER = 16;
	static final public int PARCEL = 32;

	static final public int ANY = -1;
	static final public int NONE = 0;

	
	/**
	 * return string from int definition
	 * @param aType
	 * @return
	 */
	public static String toString(int aType) {
		switch (aType) {
		case WALL : return "wall";
		case SEWER : return "sewer";
		case ROAD : return "road";
		case DITCH : return "ditch";
		case RIVER : return "river";
		case PARCEL : return "parcel";
		default:return "";
		}
	}
	
	/**
	 * return int from string definition
	 * @param aType
	 * @return
	 */
	public static int fromString(String aType) {
		int res = 1;
		boolean found = false;
		while ((res <= 65536) && (! found)) {
			if (toString(res).equals(aType))
				found = true;
			else
				res *=2;
		}
		if (! found)
			res = 0;
		return res;
	}
	
	/**
	 * Check if type matches aType
	 * @param type
	 * @param aType
	 */
	public static boolean check(int type, String aType) {
		return ((type & ConstraintType.fromString(aType)) != 0);
	}

	/**
	 * Check if type matches aType
	 * @param type
	 * @param aType
	 */
	public static boolean check(int type, int aType) {
		return ((type & aType) != 0);
	}

}
