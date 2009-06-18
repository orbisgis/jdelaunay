package org.jdelaunay.delaunay;
/**
 * Delaunay Package.
 * 
 * @author Jean-Yves MARTIN
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
	
	public final static int DelaunayError_invalidSewerPoint = 301;
	public final static int DelaunayError_invalidSewerStart = 302;
	public final static int DelaunayError_invalidSewerEnd = 303;
	public final static int DelaunayError_invalidSewerDirection = 304;

	public final static int DelaunayError_invalidWallPoint = 311;
	public final static int DelaunayError_invalidWallStart = 312;
	public final static int DelaunayError_invalidWallEnd = 313;

	public final static int DelaunayError_internalError = 999;

	public DelaunayError(int ErrorCode) {
		super();
		code = ErrorCode;
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
		case DelaunayError_internalError:
			return "internal error, please call support";
		case DelaunayError_outsideTriangle:
			return "point is outside the triangle";
		case DelaunayError_invalidSewerPoint :
			return "invalid sewer point";
		case DelaunayError_invalidSewerStart :
			return "invalid sewer start point";
		case DelaunayError_invalidSewerEnd :
			return "invalid sewer exit point";
		case DelaunayError_invalidSewerDirection :
			return "invalid sewer direction";
		case DelaunayError_invalidWallPoint :
			return "invalid wall point";
		case DelaunayError_invalidWallStart :
			return "invalid wall point start";
		case DelaunayError_invalidWallEnd :
			return "invalid wall point end";
		
		default:
			return "";
		}
	}
}
