package org.jdelaunay.delaunay;

public class TopoType {

	// Topographic types
	public static final int RIDGE = 0;
	public static final int TALWEG = 2;
	public static final int RIGHTSLOPE = 4;
	public static final int LEFTTSLOPE = 8;
	public static final int RIGHTSIDE = 16;
	public static final int LEFTSIDE = 32;
	public static final int RIGHTWELL = 64;
	public static final int LEFTWELL = 128;
	public static final int LEFTCOLINEAR = 256;
	public static final int RIGHTCOLINEAR = 512;
	public static final int DOUBLECOLINEAR = 1024;
	public static final int FLAT = 2048;
	public static final int BORDER = 4096;

	public static String getTopoName(int topoType) {

		switch (topoType) {
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
			return "unknown";
		}

	}
}
