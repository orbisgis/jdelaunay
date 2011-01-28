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
	public DelaunayError(String s){
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
			ret= "no error";
			break;
		case DELAUNAY_ERROR_NO_MESH:
			ret= "no mesh found to start process";
			break;
		case DELAUNAY_ERROR_GENERATED:
			ret= "triangulation has already been processed";
			break;
		case DELAUNAY_ERROR_NOT_GENERATED:
			ret= "triangulation has not yet been processed";
			break;
		case DELAUNAY_ERROR_NOT_ENOUGH_POINTS_FOUND:
			ret= "not enough points found to triangularize";
			break;
		case DELAUNAY_ERROR_PROXIMITY :
			ret= "distance between the two points is too small";
			break;
		case DELAUNAY_ERROR_POINT_NOT_FOUND :
			ret= "point not found";
			break;
		case DELAUNAY_ERROR_CAN_NOT_CONNECT_POINT:
			ret= "Can't connect the point to the boundary";
			break;
		case DELAUNAY_ERROR_NON_INSERTED_POINT:
			ret= "one point is not inserted in the triangularization";
			break;
		case DELAUNAY_ERROR_INCORRECT_TOPOLOGY:
			ret= "Incorrect topology";
			break;
		case DELAUNAY_ERROR_OUTSIDE_TRIANGLE:
			ret= "point is outside the triangle";
			break;
		case DELAUNAY_ERROR_REMOVING_EDGE:
			ret= "Problem while removing an edge";
			break;
		case DELAUNAY_ERROR_ERROR_POINT_XYZ:
			ret= "point should have X, Y and Z coordinates";
			break;
		case DELAUNAY_ERROR_INVALID_CALL:
			ret= "Invalid function call";
			break;
		case DELAUNAY_ERROR_INTERNAL_ERROR:
			ret= "internal error, please call support";
			break;
		default:
			return message;
		}
		if(message.isEmpty()){
			return ret;
		} else {
			return ret+", "+message;
		}
	}
}
