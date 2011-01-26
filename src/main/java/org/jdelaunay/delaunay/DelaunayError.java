package org.jdelaunay.delaunay;
/**
 * Delaunay Package.
 * 
 * @author Jean-Yves MARTIN, Erwan BOCHER
 * @date 2009-01-12
 * @version 1.0
 */


public class DelaunayError extends Exception {
	private static final long serialVersionUID = 1L;

	// error code saving
	private int code;

	// error codes
	public static final int DELAUNAY_ERROR_NO_ERROR = 0;
	public static final int DELAUNAY_ERROR_NO_MESH = 100;
	public static final int DELAUNAY_ERROR_NOT_GENERATED = 101;
	public static final int DELAUNAY_ERROR_GENERATED = 102;
	public static final int DELAUNAY_ERROR_NOT_ENOUGH_POINTS_FOUND = 103;
	public static final int DELAUNAY_ERROR_PROXIMITY = 104;
	public static final int DELAUNAY_ERROR_POINT_NOT_FOUND = 105;
	public static final int DELAUNAY_ERROR_CAN_NOT_CONNECT_POINT = 106;
	
	public static final int DELAUNAY_ERROR_NON_INSERTED_POINT = 200;
	public static final int DELAUNAY_ERROR_INCORRECT_TOPOLOGY = 201;
	public static final int DELAUNAY_ERROR_OUTSIDE_TRIANGLE = 202;
	public static final int DELAUNAY_ERROR_REMOVING_EDGE = 203;
	
	public static final int DELAUNAY_ERROR_ERROR_POINT_XYZ = 300;
	
	public static final int DELAUNAY_ERROR_INVALID_CALL = 998;
	public static final int DELAUNAY_ERROR_INTERNAL_ERROR = 999;
	public static final int DELAUNAY_ERROR_MISC = 1000;

	private String message="";

	public DelaunayError() {
		super();
		code = DELAUNAY_ERROR_INTERNAL_ERROR;
	}

	public DelaunayError(String s){
		super(s);
		message = s;
		code = DELAUNAY_ERROR_MISC;
	}

	public DelaunayError(int errorCode) {
		super();
		code = errorCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public final String getMessage() {
		switch (code) {
		case DELAUNAY_ERROR_NO_ERROR:
			return "no error";
		case DELAUNAY_ERROR_NO_MESH:
			return "no mesh found to start process";
		case DELAUNAY_ERROR_GENERATED:
			return "triangulation has already been processed";
		case DELAUNAY_ERROR_NOT_GENERATED:
			return "triangulation has not yet been processed";
		case DELAUNAY_ERROR_NOT_ENOUGH_POINTS_FOUND:
			return "not enough points found to triangularize";
		case DELAUNAY_ERROR_PROXIMITY :
			return "distance between the two points is too small";
		case DELAUNAY_ERROR_POINT_NOT_FOUND :
			return "point not found";
		case DELAUNAY_ERROR_CAN_NOT_CONNECT_POINT:
			return "Can't connect the point to the boundary";
		case DELAUNAY_ERROR_NON_INSERTED_POINT:
			return "one point is not inserted in the triangularization";
		case DELAUNAY_ERROR_INCORRECT_TOPOLOGY:
			return "Incorrect topology";
		case DELAUNAY_ERROR_OUTSIDE_TRIANGLE:
			return "point is outside the triangle";
		case DELAUNAY_ERROR_REMOVING_EDGE:
			return "Problem while removing an edge";
		case DELAUNAY_ERROR_ERROR_POINT_XYZ:
			return "point should have X, Y and Z coordinates";
			
		case DELAUNAY_ERROR_INVALID_CALL:
			return "Invalid function call";
		case DELAUNAY_ERROR_INTERNAL_ERROR:
			return "internal error, please call support";
			
		default:
			return message;
		}
	}
}
