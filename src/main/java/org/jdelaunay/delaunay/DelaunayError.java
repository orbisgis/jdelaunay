package org.jdelaunay.delaunay;
/**
 * Delaunay Package.
 * 
 * @author Jean-Yves MARTIN, Erwan BOCHER
 * @date 2009-01-12
 * @version 1.0
 */


public class DelaunayError extends Throwable {
	private static final long serialVersionUID = 1L;

	// error code saving
	private int code;

	// error codes
	public final static int DELAUNAY_ERROR_NO_ERROR = 0;
	public final static int DELAUNAY_ERROR_NO_MESH = 100;
	public final static int DELAUNAY_ERROR_NOT_GENERATED = 101;
	public final static int DELAUNAY_ERROR_GENERATED = 102;
	public final static int DELAUNAY_ERROR_NOT_ENOUGH_POINTS_FOUND = 103;
	public final static int DELAUNAY_ERROR_PROXIMITY = 104;
	public final static int DELAUNAY_ERROR_POINT_NOT_FOUND = 105;
	
	public final static int DELAUNAY_ERROR_NON_INSERTED_POINT = 200;
	public final static int DELAUNAY_ERROR_INCORRECT_TOPOLOGY = 201;
	public final static int DELAUNAY_ERROR_OUTSIDE_TRIANGLE = 202;
	
	public final static int DELAUNAY_ERROR_ERROR_POINT_XYZ = 300;
	
	public final static int DELAUNAY_ERROR_INVALID_CALL = 998;
	public final static int DELAUNAY_ERROR_INTERNAL_ERROR = 999;
	public final static int DELAUNAY_ERROR_MISC = 1000;

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
	public String getMessage() {
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
		case DELAUNAY_ERROR_NON_INSERTED_POINT:
			return "one point is not inserted in the triangularization";
		case DELAUNAY_ERROR_INCORRECT_TOPOLOGY:
			return "Incorrect topology";
		case DELAUNAY_ERROR_OUTSIDE_TRIANGLE:
			return "point is outside the triangle";
		
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
