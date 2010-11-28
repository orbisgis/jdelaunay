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
	private static int code;

	// error codes
	public final static int DelaunayError_noError = 0;
	public final static int DelaunayError_noMesh = 100;
	public final static int DelaunayError_notGenerated = 101;
	public final static int DelaunayError_Generated = 102;
	public final static int DelaunayError_notEnoughPointsFound = 103;
	public final static int DelaunayError_proximity = 104;
	public final static int DelaunayError_pointNotFound = 105;
	
	public final static int DelaunayError_nonInsertedPoint = 200;
	public final static int DelaunayError_incorrectTopology = 201;
	public final static int DelaunayError_outsideTriangle = 202;
	
	public final static int DelaunayError_errorPointxyz = 300;
	
	public final static int DelaunayError_invalidCall = 998;
	public final static int DelaunayError_internalError = 999;

	public DelaunayError() {
		super();
		code = DelaunayError_internalError;
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
		case DelaunayError_noError:
			return "no error";
		case DelaunayError_noMesh:
			return "no mesh found to start process";
		case DelaunayError_Generated:
			return "triangulation has already been processed";
		case DelaunayError_notGenerated:
			return "triangulation has not yet been processed";
		case DelaunayError_notEnoughPointsFound:
			return "not enough points found to triangularize";
		case DelaunayError_proximity :
			return "distance between the two points is too small";
		case DelaunayError_pointNotFound :
			return "point not found";
		case DelaunayError_nonInsertedPoint:
			return "one point is not inserted in the triangularization";
		case DelaunayError_incorrectTopology:
			return "Incorrect topology";
		case DelaunayError_outsideTriangle:
			return "point is outside the triangle";
		
		case DelaunayError_errorPointxyz:
			return "point should have X, Y and Z coordinates";
			
		case DelaunayError_invalidCall:
			return "Invalid function call";
		case DelaunayError_internalError:
			return "internal error, please call support";
			
		default:
			return "";
		}
	}
}
