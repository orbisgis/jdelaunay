package org.jhydrocell.hydronetwork;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

import org.jdelaunay.delaunay.DelaunayError;
import org.jdelaunay.delaunay.Edge;
import org.jdelaunay.delaunay.MyMesh;
import org.jdelaunay.delaunay.Point;
import org.jdelaunay.delaunay.DelaunayTriangle;
import org.jhydrocell.utilities.HydroLineUtil;
import org.jhydrocell.utilities.HydroPolygonUtil;
import org.jhydrocell.utilities.MathUtil;

import com.vividsolutions.jts.geom.Coordinate;

public class HydroNetwork {
	private MyMesh theMesh;

	private LinkedList<Point> listEntry;
	private LinkedList<Point> listExit;
	private LinkedList<Point> listIntermediate;
	private LinkedList<Edge> listEdges;
	private LinkedList<Point> listPrepare;
	private int listDefinition;
	private boolean connectToSurface;

	/**
	 * Global initialization
	 */
	private void init() {
		theMesh = null;

		listEntry = new LinkedList<Point>();
		listExit = new LinkedList<Point>();
		listIntermediate = new LinkedList<Point>();
		listEdges = new LinkedList<Edge>();
		listPrepare = new LinkedList<Point>();
		listDefinition = 0;
		connectToSurface = true;
	}

	/**
	 * Constructor
	 */
	public HydroNetwork() {
		init();
	}

	/**
	 * Constructor
	 */
	public HydroNetwork(MyMesh aMesh) {
		init();
		theMesh = aMesh;
	}

	/**
	 * Morphological qualification
	 * 
	 * @throws DelaunayError
	 */
	public void morphologicalQualification() throws DelaunayError {
		if (theMesh == null) {
			throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_NO_MESH);
		}
		else if (!theMesh.isMeshComputed()) {
			throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_NOT_GENERATED);
		}
		else {

			// Edges : topographic qualifications
			for (Edge edge : theMesh.getEdges()) {
				HydroLineUtil hydroLineUtil = new HydroLineUtil(edge);
				HydroPolygonUtil hydroPolygonUtil = null;
				DelaunayTriangle aTriangleLeft = edge.getLeft();
				DelaunayTriangle aTriangleRight = edge.getRight();

				boolean rightTtoEdge = false;
				boolean rightTColinear = false;
				boolean righTFlat = false;
				boolean leftTtoEdge = false;
				boolean leftTColinear = false;
				boolean leftTFlat = false;
				boolean rightBorder = false;
				boolean leftBorder = false;

				// Qualification des triangles
				if (aTriangleRight != null) {
					hydroPolygonUtil = new HydroPolygonUtil(aTriangleRight);
					boolean pointeVersEdge = hydroPolygonUtil
							.getPenteVersEdge(edge);
					if (pointeVersEdge) {
						rightTtoEdge = true;
					} else if (hydroPolygonUtil.getSlope() > 0) {
						if (MathUtil.isColinear(hydroLineUtil.get3DVector(),
								hydroPolygonUtil.get3DVector())) {
							rightTColinear = true;
						}
					} else if (hydroPolygonUtil.getSlope() == 0) {
						righTFlat = true;
					}
				}

				else {
					rightBorder = true;
				}

				if (aTriangleLeft != null) {

					hydroPolygonUtil = new HydroPolygonUtil(aTriangleLeft);
					boolean pointeVersEdge = hydroPolygonUtil
							.getPenteVersEdge(edge);

					if (pointeVersEdge) {
						leftTtoEdge = true;
					} else if (hydroPolygonUtil.getSlope() > 0) {
						if (MathUtil.isColinear(hydroLineUtil.get3DVector(),
								hydroPolygonUtil.get3DVector())) {
							leftTColinear = true;
						}
					} else if (hydroPolygonUtil.getSlope() == 0) {
						leftTFlat = true;
					}

				} else {
					leftBorder = true;
				}

				// Recupération des noeuds associés à l'edge

				// Qualification de la pente de l'edge parcouru
				int edgeGradient = edge.getGradient();
				if (!leftBorder && !rightBorder) {
					// Traitement des ridges
					if ((!rightTtoEdge && !leftTtoEdge)
							&& (!righTFlat && !leftTFlat)) {
						edge.addProperty(HydroProperties.RIDGE);
					}

					// Cas des talwegs
					else if (rightTtoEdge && leftTtoEdge) {
						edge.addProperty(HydroProperties.TALWEG);
						edge.getStart().addProperty(HydroProperties.TALWEG);
						edge.getEnd().addProperty(HydroProperties.TALWEG);

					}

					// Le triangle de gauche pointe sur l'edge mais pas le
					// triangle de droite
					else if ((leftTtoEdge && !rightTtoEdge) && !righTFlat) {
						edge.addProperty(HydroProperties.RIGHTSLOPE);
					}

					// Le triangle de droite pointe sur l'edge mais pas le
					// triangle de gauche
					else if ((rightTtoEdge && !leftTtoEdge) && (!leftTFlat)) {
						edge.addProperty(HydroProperties.LEFTTSLOPE);
					}

					// Traitement du rebord droit
					else if ((!rightTtoEdge && !leftTtoEdge)
							&& (!leftTFlat && righTFlat)) {
						edge.addProperty(HydroProperties.LEFTSIDE);
					}

					// Traitement du rebord gauche

					else if ((!leftTtoEdge && !rightTtoEdge)
							&& (!righTFlat && leftTFlat)) {
						edge.addProperty(HydroProperties.RIGHTSIDE);
					}

					// Traitement du fond gauche
					else if ((rightTtoEdge && !leftTtoEdge)
							&& (leftTFlat && !righTFlat)) {
						edge.addProperty(HydroProperties.LEFTWELL);
					}

					// Traitement du fond droit
					else if ((!rightTtoEdge && leftTtoEdge)
							&& (!leftTFlat && righTFlat)) {
						edge.addProperty(HydroProperties.RIGHTWELL);
					}

					// Cas particulier des talwegs colineaires

					// Talweg colineaire gauche
					else if ((!leftTtoEdge && rightTtoEdge) && leftTColinear) {
						edge.addProperty(HydroProperties.LEFTCOLINEAR);
						edge.getStart().addProperty(HydroProperties.TALWEG);
						edge.getEnd().addProperty(HydroProperties.TALWEG);

					}

					// Talweg colineaire droit
					else if ((leftTtoEdge && !rightTtoEdge) && rightTColinear) {
						edge.addProperty(HydroProperties.RIGHTCOLINEAR);
						edge.getStart().addProperty(HydroProperties.TALWEG);
						edge.getEnd().addProperty(HydroProperties.TALWEG);

					}

					// Les deux triangles sont colineaires
					else if ((!leftTtoEdge && !rightTtoEdge)
							&& (rightTColinear && leftTColinear)) {
						edge.addProperty(HydroProperties.DOUBLECOLINEAR);

						edge.getStart().addProperty(HydroProperties.TALWEG);
						edge.getEnd().addProperty(HydroProperties.TALWEG);

					}

					// Le reste est plat
					else {
						edge.addProperty(HydroProperties.FLAT);
					}
				}

				// Traitement des bords plats
				else {
					edge.addProperty(HydroProperties.BORDER);
				}
			}
		}
	}

	public void talwegBuilder() throws DelaunayError {
		if (theMesh == null) {
			throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_NO_MESH);
		}
		else if (!theMesh.isMeshComputed()) {
			throw new DelaunayError(DelaunayError.DELAUNAY_ERROR_NOT_GENERATED);
		}
		else {
			/**
			 * The code below is used to insert new talweg in the TIN
			 */

			ArrayList<Point> listPointAtraiter = new ArrayList<Point>();
			ArrayList<DelaunayTriangle> listTriangles = new ArrayList<DelaunayTriangle>();

			for (Edge edge : theMesh.getEdges()) {

				// Edge talweg
				if ((!edge.hasProperty(HydroProperties.TALWEG))
						|| (!edge.hasProperty(HydroProperties.LEFTCOLINEAR))
						|| (!edge.hasProperty(HydroProperties.RIGHTCOLINEAR))
						|| (!edge.hasProperty(HydroProperties.DOUBLECOLINEAR))) {

					Point uperPoint = theMesh.findUperPoint(edge);

					if (uperPoint.hasProperty(HydroProperties.TALWEG)) {
						if (!listPointAtraiter.contains(uperPoint)) {
							listPointAtraiter.add(uperPoint);

						}
					}
				}
			}
		}
	}

	/**
	 * post process the edges according to their type
	 */
	private void postProcessEdges() {
		LinkedList<Edge> addedEdges = new LinkedList<Edge>();
		ArrayList<Edge> theEdges = theMesh.getEdges();
		for (Edge anEdge : theEdges) {
			if (anEdge.hasProperty(HydroProperties.WALL)) {
				// Process wall : duplicate edge and changes connections
				if ((anEdge.getLeft() != null) && (anEdge.getRight() != null)) {
					// Something to do if and only if there are two triangles
					// connected
					Edge newEdge = new Edge(anEdge);

					// Changes left triangle connection
					DelaunayTriangle aTriangle = anEdge.getLeft();
					for (int i = 0; i < 3; i++) {
						if (aTriangle.getEdge(i) == anEdge) {
							aTriangle.setEdge(i, newEdge);
						}
					}

					// Changes edges connections
					newEdge.setRight(null);
					anEdge.setLeft(null);

					// add the new edge
					addedEdges.add(newEdge);
				}
			}
		}

		// add edges to the structure
		for (Edge anEdge : addedEdges) {
			theEdges.add(anEdge);
		}
	}

	// ----------------------------------------------------------------
	/**
	 * Defines a new branch type
	 * 
	 * @param branchType
	 * @param connectToSurface
	 * @throws DelaunayError
	 */
	private void branchStart(int branchType, boolean connectToSurface)
			throws DelaunayError {
		this.listEntry = new LinkedList<Point>();
		this.listExit = new LinkedList<Point>();
		this.listIntermediate = new LinkedList<Point>();
		this.listEdges = new LinkedList<Edge>();
		this.listDefinition = branchType;
		this.connectToSurface = connectToSurface;
	}

	/**
	 * Defines a new branch type on the surface
	 * 
	 * @param branchType
	 * @throws DelaunayError
	 */
	private void branchStart(int branchType) throws DelaunayError {
		branchStart(branchType, true);
	}

	/**
	 * defines a new branch
	 * 
	 * @param theList
	 * @throws DelaunayError
	 */
	private void setNewBranch(LinkedList theList) throws DelaunayError {
		Point lastPoint = null;
		int count = theList.size();
		Point aPoint = null;
		Coordinate aCoordinate = null;
		ListIterator iterList = theList.listIterator();
		while (iterList.hasNext()) {
			Object item = iterList.next();
			if (item instanceof Point) {
				aPoint = (Point) item;
			} else if (item instanceof Coordinate) {
				aCoordinate = (Coordinate) item;
				aPoint = new Point(aCoordinate.x, aCoordinate.y,
						aCoordinate.z);
			} else {
				aPoint = null;
			}

			count--;
			if (aPoint != null) {
				if (lastPoint == null) {
					// First point of the list
					if (listIntermediate.contains(aPoint)) {
						// Already an intermediate point => do nothing
					} else if (listExit.contains(aPoint)) {
						// It is an exit
						// It is also an entry
						// => becomes an intermediate
						listExit.remove(aPoint);
						listIntermediate.add(aPoint);
					} else if (!listEntry.contains(aPoint)) {
						// New entry
						listEntry.add(aPoint);
					}
					// else it is in Entry
				} else {
					// Intermediate point
					if (listIntermediate.contains(aPoint)) {
						// Already an intermediate point => do nothing
					} else if (listExit.contains(aPoint)) {
						// It is an exit
						if (count > 0) {
							// and not the last point
							// => becomes an intermediate
							listExit.remove(aPoint);
							listIntermediate.add(aPoint);
						}
					} else if (listEntry.contains(aPoint)) {
						// It is an entry
						// => becomes an intermediate
						listEntry.remove(aPoint);
						listIntermediate.add(aPoint);
					} else if (count > 0) {
						// new point => add it to Intermediate
						listIntermediate.add(aPoint);
					} else {
						// new point and Last point => Exit
						listExit.add(aPoint);
					}

					// Link lastPoint to new point
					Edge anEdge = new Edge(lastPoint, aPoint);
					anEdge.addProperty(listDefinition);
					listEdges.add(anEdge);
				}
				// other informations
				aPoint.addProperty(listDefinition);

				lastPoint = aPoint;
			}
		}
	}

	/**
	 * Validate branch and end that branch type
	 * 
	 * @throws DelaunayError
	 */
	private void branchValidate() throws DelaunayError {
		DelaunayTriangle referenceTriangle = null;
		ArrayList<Edge> edges = theMesh.getEdges();
		ArrayList<Point> points = theMesh.getPoints();

		// add every entry point to the mesh
		for (Point aPoint : listEntry) {
			if (points.contains(aPoint)) {
				// Already in the points list => do noting
			} else {
				aPoint.setMarked(0, true);//TODO check me
				referenceTriangle = theMesh.getTriangle(aPoint);
				if (referenceTriangle != null) {
					// Connect it to the surface
					double ZValue = referenceTriangle.softInterpolateZ(aPoint);
					aPoint.setZ(ZValue);

					theMesh.addPoint(referenceTriangle, aPoint);
				} else {
					theMesh.addPoint(aPoint);
				}
			}
		}

		// add every intermediate point to the point list
		// do not include them in the mesh
		for (Point aPoint : listIntermediate) {
			if (points.contains(aPoint)) {
				// Already in the points list => do noting
			} else {
				points.add(aPoint);
				aPoint.setMarked(0, true);//TODO check me
				referenceTriangle = theMesh.getTriangle(aPoint);
				if (referenceTriangle != null) {
					double ZValue = referenceTriangle.softInterpolateZ(aPoint);
					if (connectToSurface) {
						// Connect it to the surface
						aPoint.setZ(ZValue);

						theMesh.addPoint(referenceTriangle, aPoint);
					} else {
						if (aPoint.getZ() > ZValue) {
							aPoint.setZ(ZValue - 1.0);
						}
					}
				} else if (connectToSurface) {
					theMesh.addPoint(aPoint);
				}
			}
		}

		// add every exit point to the mesh
		for (Point aPoint : listExit) {
			if (points.contains(aPoint)) {
				// Already in the points list => do noting
			} else {
				aPoint.setMarked(0, true);//TODO check me
				referenceTriangle = theMesh.getTriangle(aPoint);
				if (referenceTriangle != null) {
					// Connect it to the surface
					double ZValue = referenceTriangle.softInterpolateZ(aPoint);
					aPoint.setZ(ZValue);

					theMesh.addPoint(referenceTriangle, aPoint);
				} else {
					theMesh.addPoint(aPoint);
				}
			}
		}

		// add edges
		for (Edge anEdge : listEdges) {
			anEdge.setMarked(0,true); //FIXME check if it's good ( old version : anEdge.setMarked(true); )
			if (connectToSurface) {
				theMesh.addEdge(anEdge);
			}
			else {
				anEdge.setOutsideMesh(true);
				edges.add(anEdge);
			}
		}

		// Reset informations
		listEntry = new LinkedList<Point>();
		listExit = new LinkedList<Point>();
		listIntermediate = new LinkedList<Point>();
		listEdges = new LinkedList<Edge>();
		listDefinition = 0;
		connectToSurface = true;
	}

	// ----------------------------------------------------------------
	/**
	 * add a sewer entry
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @throws DelaunayError
	 */
	public void addSewerEntry(double x, double y, double z)
			throws DelaunayError {
		// Search for the point
		Point sewerPoint = theMesh.getPoint(x, y, z);
		addSewerEntry(sewerPoint);
	}

	/**
	 * add a sewer entry
	 * 
	 * @param sewerPoint
	 * @throws DelaunayError
	 */
	public void addSewerEntry(Point sewerPoint) throws DelaunayError {
		listPrepare = new LinkedList<Point>();
		listPrepare.add(sewerPoint);
	}

	/**
	 * add a sewer exit
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @throws DelaunayError
	 */
	public void addSewerExit(double x, double y, double z) throws DelaunayError {
		// Search for the point
		Point sewerPoint = theMesh.getPoint(x, y, z);
		addSewerExit(sewerPoint);
	}

	/**
	 * add a sewer exit
	 * 
	 * @param sewerPoint
	 * @throws DelaunayError
	 */
	public void addSewerExit(Point sewerPoint) throws DelaunayError {
		listPrepare.add(sewerPoint);
		sewerSet(listPrepare);
		listPrepare = new LinkedList<Point>();
	}

	/**
	 * add a sewer point (neither start or exit
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @throws DelaunayError
	 */
	public void addSewerPoint(double x, double y, double z)
			throws DelaunayError {
		// Search for the point
		Point aPoint = theMesh.getPoint(x, y, z);
		addSewerPoint(aPoint);
	}

	/**
	 * add a sewer point (neither start or exit
	 * 
	 * @param sewerPoint
	 * @throws DelaunayError
	 */
	public void addSewerPoint(Point sewerPoint) throws DelaunayError {
		listPrepare.add(sewerPoint);
	}

	/**
	 * use a sewer point to start a new branch
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @throws DelaunayError
	 */
	public void setSewerPoint(double x, double y, double z)
			throws DelaunayError {
		// Search for the point
		addSewerEntry(x, y, z);
	}

	/**
	 * use a sewer point to start a new branch
	 * 
	 * @param sewerPoint
	 * @throws DelaunayError
	 */
	public void setSewerPoint(Point sewerPoint) throws DelaunayError {
		addSewerEntry(sewerPoint);
	}

	// ----------------------------------------------------------------
	/**
	 * Start sewers definition
	 * 
	 * @throws DelaunayError
	 */
	public void sewerStart() throws DelaunayError {
		branchStart(HydroProperties.SEWER, false);
	}

	/**
	 * define a new sewer branch
	 * 
	 * @param sewerPoint
	 * @throws DelaunayError
	 */
	public void sewerSet(LinkedList<Point> sewerList) throws DelaunayError {
		if (listDefinition == HydroProperties.NONE) {
			branchStart(HydroProperties.SEWER, false);
		}
		else if (listDefinition != HydroProperties.SEWER) {
			branchValidate();
			branchStart(HydroProperties.SEWER, false);
		}
		setNewBranch(sewerList);
	}

	/**
	 * Validate and end sewer definition
	 * 
	 * @throws DelaunayError
	 */
	public void sewerValidate() throws DelaunayError {
		if (listDefinition != HydroProperties.NONE) {
			branchValidate();
		}
		listDefinition = HydroProperties.NONE;
	}

	// ----------------------------------------------------------------
	/**
	 * Start ditches definition
	 * 
	 * @throws DelaunayError
	 */
	public void ditchStart() throws DelaunayError {
		branchStart(HydroProperties.DITCH);
	}

	/**
	 * define a new ditch branch
	 * 
	 * @param ditchList
	 * @throws DelaunayError
	 */
	public void ditchSet(LinkedList<Point> ditchList) throws DelaunayError {
		if (listDefinition == HydroProperties.NONE) {
			branchStart(HydroProperties.DITCH);
		}
		else if (listDefinition != HydroProperties.DITCH) {
			branchValidate();
			branchStart(HydroProperties.DITCH);
		}
		setNewBranch(ditchList);
	}

	/**
	 * Validate and end ditches definition
	 * 
	 * @throws DelaunayError
	 */
	public void ditchValidate() throws DelaunayError {
		if (listDefinition != HydroProperties.NONE) {
			branchValidate();
		}
		listDefinition = HydroProperties.NONE;
	}

	// ----------------------------------------------------------------
	/**
	 * Start rivers definition
	 * 
	 * @throws DelaunayError
	 */
	public void riverStart() throws DelaunayError {
		branchStart(HydroProperties.RIVER);
	}

	/**
	 * define a new river branch
	 * 
	 * @param riverList
	 * @throws DelaunayError
	 */
	public void riverSet(LinkedList<Point> riverList) throws DelaunayError {
		if (listDefinition == HydroProperties.NONE) {
			branchStart(HydroProperties.RIVER);
		}
		else if (listDefinition != HydroProperties.RIVER) {
			branchValidate();
			branchStart(HydroProperties.RIVER);
		}
		setNewBranch(riverList);
	}

	/**
	 * Validate and end rivers definition
	 * 
	 * @throws DelaunayError
	 */
	public void riverValidate() throws DelaunayError {
		if (listDefinition != HydroProperties.NONE) {
			branchValidate();
		}
		listDefinition = HydroProperties.NONE;
	}

	// ----------------------------------------------------------------
	/**
	 * Start walls definition
	 * 
	 * @throws DelaunayError
	 */
	public void wallStart() throws DelaunayError {
		branchStart(HydroProperties.WALL);
	}

	/**
	 * define a new wall branch
	 * 
	 * @param wallList
	 * @throws DelaunayError
	 */
	public void wallSet(LinkedList<Point> wallList) throws DelaunayError {
		if (listDefinition == HydroProperties.NONE) {
			branchStart(HydroProperties.WALL);
		}
		else if (listDefinition != HydroProperties.WALL) {
			branchValidate();
			branchStart(HydroProperties.WALL);
		}
		setNewBranch(wallList);
	}

	/**
	 * Validate and end walls definition
	 * 
	 * @throws DelaunayError
	 */
	public void wallValidate() throws DelaunayError {
		if (listDefinition != HydroProperties.NONE) {
			branchValidate();
		}
		listDefinition = HydroProperties.NONE;
	}

	// ----------------------------------------------------------------

}
