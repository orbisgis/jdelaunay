package org.jhydrocell.hydronetwork;

import java.util.*;
import com.vividsolutions.jts.geom.Coordinate;

import org.jdelaunay.delaunay.*;
import org.jhydrocell.utilities.*;

public class MyHydroNetwork {
	protected Delaunay myDelaunay;
	private MyMesh theMesh;
	
	private LinkedList<MyPoint> listEntry;
	private LinkedList<MyPoint> listExit;
	private LinkedList<MyPoint> listIntermediate;
	private LinkedList<MyEdge> listEdges;
	private LinkedList<MyPoint> listPrepare;
	private int listDefinition;
	private boolean connectToSurface;

	public MyHydroNetwork() {
		myDelaunay = new Delaunay();
		theMesh = null;

		listEntry = new LinkedList<MyPoint>();
		listExit = new LinkedList<MyPoint>();
		listIntermediate = new LinkedList<MyPoint>();
		listEdges = new LinkedList<MyEdge>();
		listPrepare = new LinkedList<MyPoint>();
		listDefinition = 0;
		connectToSurface = true;
}
	
	public MyHydroNetwork(Delaunay myDelaunay) {
		this.myDelaunay = myDelaunay;
		theMesh = myDelaunay.getMesh();

		listEntry = new LinkedList<MyPoint>();
		listExit = new LinkedList<MyPoint>();
		listIntermediate = new LinkedList<MyPoint>();
		listEdges = new LinkedList<MyEdge>();
		listPrepare = new LinkedList<MyPoint>();
		listDefinition = 0;
		connectToSurface = true;
}

	/**
	 * Morphological qualification
	 *
	 * @throws DelaunayError
	 */
	public void morphologicalQualification() throws DelaunayError {

		if (theMesh == null)
			throw new DelaunayError(DelaunayError.DelaunayError_noMesh);
		else if (! theMesh.isMeshComputed())
			throw new DelaunayError(DelaunayError.DelaunayError_notGenerated);
		else {

			/**
			 * Edges : topographic qualifications
			 */
			for (MyEdge edge : theMesh.getEdges()) {

				HydroLineUtil hydroLineUtil = new HydroLineUtil(edge);
				HydroPolygonUtil hydroPolygonUtil = null;
				MyTriangle aTriangleLeft = edge.getLeft();
				MyTriangle aTriangleRight = edge.getRight();

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
						if (MathUtil.IsColinear(hydroLineUtil.get3DVector(),
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
						if (MathUtil.IsColinear(hydroLineUtil.get3DVector(),
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

						edge.setTopo(TopoType.RIDGE);

					}

					// Cas des talwegs
					else if (rightTtoEdge && leftTtoEdge) {

						edge.setTopo(TopoType.TALWEG);
						edge.getStart().setTopo(TopoType.TALWEG);
						edge.getEnd().setTopo(TopoType.TALWEG);

					}

					// Le triangle de gauche pointe sur l'edge mais pas le
					// triangle de droite
					else if ((leftTtoEdge && !rightTtoEdge) && !righTFlat) {

						edge.setTopo(TopoType.RIGHTSLOPE);

					}

					// Le triangle de droite pointe sur l'edge mais pas le
					// triangle de gauche
					else if ((rightTtoEdge && !leftTtoEdge) && (!leftTFlat)) {

						edge.setTopo(TopoType.LEFTTSLOPE);

					}

					// Traitement du rebord droit
					else if ((!rightTtoEdge && !leftTtoEdge)
							&& (!leftTFlat && righTFlat)) {
						edge.setTopo(TopoType.LEFTSIDE);
					}

					// Traitement du rebord gauche

					else if ((!leftTtoEdge && !rightTtoEdge)
							&& (!righTFlat && leftTFlat)) {
						edge.setTopo(TopoType.RIGHTSIDE);
					}

					// Traitement du fond gauche
					else if ((rightTtoEdge && !leftTtoEdge)
							&& (leftTFlat && !righTFlat)) {

						edge.setTopo(TopoType.LEFTWELL);
					}

					// Traitement du fond droit
					else if ((!rightTtoEdge && leftTtoEdge)
							&& (!leftTFlat && righTFlat)) {

						edge.setTopo(TopoType.RIGHTWELL);
					}

					// Cas particulier des talwegs colineaires

					// Talweg colineaire gauche

					else if ((!leftTtoEdge && rightTtoEdge) && leftTColinear) {

						edge.setTopo(TopoType.LEFTCOLINEAR);
						edge.getStart().setTopo(TopoType.TALWEG);
						edge.getEnd().setTopo(TopoType.TALWEG);

					}

					// Talweg colineaire droit

					else if ((leftTtoEdge && !rightTtoEdge) && rightTColinear) {

						edge.setTopo(TopoType.RIGHTCOLINEAR);
						edge.getStart().setTopo(TopoType.TALWEG);
						edge.getEnd().setTopo(TopoType.TALWEG);

					}

					// Les deux triangles sont colineaires

					else if ((!leftTtoEdge && !rightTtoEdge)
							&& (rightTColinear && leftTColinear)) {

						edge.setTopo(TopoType.DOUBLECOLINEAR);

						edge.getStart().setTopo(TopoType.TALWEG);
						edge.getEnd().setTopo(TopoType.TALWEG);

					}

					// Le reste est plat
					else {

						edge.setTopo(TopoType.FLAT);

					}

				}

				// Traitement des bords plats
				else {
					edge.setTopo(TopoType.BORDER);
				}

			}

		}
	}

	public void talwegBuilder() throws DelaunayError {

		if (theMesh == null)
			throw new DelaunayError(DelaunayError.DelaunayError_noMesh);
		else if (! theMesh.isMeshComputed())
			throw new DelaunayError(DelaunayError.DelaunayError_notGenerated);
		else {
		/**
		 * The code below is used to insert new talweg in the TIN
		 */

		ArrayList<MyPoint> listPointAtraiter = new ArrayList<MyPoint>();
		ArrayList<MyTriangle> listTriangles = new ArrayList<MyTriangle>();

		for (MyEdge edge : theMesh.getEdges()) {

			// Edge talweg
			if ((edge.getTopo() != TopoType.TALWEG)
					|| (edge.getTopo() != TopoType.LEFTCOLINEAR)
					|| (edge.getTopo() != TopoType.RIGHTCOLINEAR)
					|| (edge.getTopo() != TopoType.DOUBLECOLINEAR)) {

				MyPoint uperPoint = myDelaunay.findUperPoint(edge);

				if (uperPoint.getTopo() == TopoType.TALWEG) {
					if (!listPointAtraiter.contains(uperPoint)) {
						listPointAtraiter.add(uperPoint);

					}
				}
			}
		}

		theMesh.setAllGids();
	}
	}

	/**
	 * post process the edges according to their type
	 */
	private void postProcessEdges() {
		LinkedList<MyEdge> addedEdges = new LinkedList<MyEdge>();
		ArrayList<MyEdge> theEdges = theMesh.getEdges();
		for (MyEdge anEdge : theEdges) {
			if (anEdge.getType() == ConstraintType.WALL) {
				// Process wall : duplicate edge and changes connections
				if ((anEdge.getLeft() != null) && (anEdge.getRight() != null)) {
					// Something to do if and only if there are two triangles
					// connected
					MyEdge newEdge = new MyEdge(anEdge);

					// Changes left triangle connection
					MyTriangle aTriangle = anEdge.getLeft();
					for (int i = 0; i < 3; i++) {
						if (aTriangle.edge(i) == anEdge)
							aTriangle.setEdge(i, newEdge);
					}

					// Changes edges connections
					newEdge.setRight( null );
					anEdge.setLeft ( null );

					// add the new edge
					addedEdges.add(newEdge);
				}
			}
		}

		// add edges to the structure
		for (MyEdge anEdge : addedEdges) {
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
		this.listEntry = new LinkedList<MyPoint>();
		this.listExit = new LinkedList<MyPoint>();
		this.listIntermediate = new LinkedList<MyPoint>();
		this.listEdges = new LinkedList<MyEdge>();
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
		MyPoint lastPoint = null;
		int count = theList.size();
		MyPoint aPoint = null;
		Coordinate aCoordinate = null;
		ListIterator iterList = theList.listIterator();
		while (iterList.hasNext()) {
			Object item = iterList.next();
			if (item instanceof MyPoint) {
				aPoint = (MyPoint) item;
			} else if (item instanceof Coordinate) {
				aCoordinate = (Coordinate) item;
				aPoint = new MyPoint(aCoordinate.x, aCoordinate.y,
						aCoordinate.z);
			} else
				aPoint = null;

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
					MyEdge anEdge = new MyEdge(lastPoint, aPoint,
							listDefinition);
					listEdges.add(anEdge);
				}
				// other informations
				aPoint.setType(listDefinition);

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
		MyTriangle referenceTriangle = null;
		ArrayList<MyEdge> edges = theMesh.getEdges();
		ArrayList<MyPoint> points = theMesh.getPoints();

		// add every entry point to the mesh
		for (MyPoint aPoint : listEntry) {
			if (points.contains(aPoint)) {
				// Already in the points list => do noting
			} else {
				aPoint.setMarked(true);
				referenceTriangle = myDelaunay.getTriangle(aPoint);
				if (referenceTriangle != null) {
					// Connect it to the surface
					double ZValue = referenceTriangle.getSurfacePoint(aPoint);
					aPoint.setZ( ZValue);

					myDelaunay.addPoint(referenceTriangle, aPoint);
				} else {
					referenceTriangle = myDelaunay.addPoint(aPoint);

					if (referenceTriangle != null) {
						double ZValue = 0;
						for (int i = 0; i < 3; i++) {
							if (referenceTriangle.point(i) != aPoint)
								ZValue += referenceTriangle.point(i).getZ();
						}
						aPoint.setZ( ZValue / 2);
					}
				}
			}
		}

		// add every intermediate point to the point list
		// do not include them in the mesh
		for (MyPoint aPoint : listIntermediate) {
			if (points.contains(aPoint)) {
				// Already in the points list => do noting
			} else {
				points.add(aPoint);
				aPoint.setMarked (true);
				referenceTriangle = myDelaunay.getTriangle(aPoint);
				if (referenceTriangle != null) {
					double ZValue = referenceTriangle.getSurfacePoint(aPoint);
					if (connectToSurface) {
						// Connect it to the surface
						aPoint.setZ( ZValue );

						myDelaunay.addPoint(referenceTriangle, aPoint);
					} else {
						if (aPoint.getZ() > ZValue)
							aPoint.setZ( ZValue - 1.0);
					}
				} else if (connectToSurface) {
					referenceTriangle = myDelaunay.addPoint(aPoint);

					if (referenceTriangle != null) {
						double ZValue = 0;
						for (int i = 0; i < 3; i++) {
							if (referenceTriangle.point(i) != aPoint)
								ZValue += referenceTriangle.point(i).getZ();
						}
						aPoint.setZ( ZValue / 2);
					}
				}
			}
		}

		// add every exit point to the mesh
		for (MyPoint aPoint : listExit) {
			if (points.contains(aPoint)) {
				// Already in the points list => do noting
			} else {
				aPoint.setMarked(true);
				referenceTriangle = myDelaunay.getTriangle(aPoint);
				if (referenceTriangle != null) {
					// Connect it to the surface
					double ZValue = referenceTriangle.getSurfacePoint(aPoint);
					aPoint.setZ( ZValue);

					myDelaunay.addPoint(referenceTriangle, aPoint);
				} else {
					referenceTriangle = myDelaunay.addPoint(aPoint);

					if (referenceTriangle != null) {
						double ZValue = 0;
						for (int i = 0; i < 3; i++) {
							if (referenceTriangle.point(i) != aPoint)
								ZValue += referenceTriangle.point(i).getZ();
						}
						aPoint.setZ( ZValue / 2);
					}
				}
			}
		}

		// add edges
		for (MyEdge anEdge : listEdges) {
			anEdge.setMarked (true);
			if (connectToSurface)
				myDelaunay.addEdge(anEdge);
			else {
				anEdge.setOutsideMesh ( true );
				edges.add(anEdge);
			}
		}

		theMesh.setAllGids();

		// Reset informations
		listEntry = new LinkedList<MyPoint>();
		listExit = new LinkedList<MyPoint>();
		listIntermediate = new LinkedList<MyPoint>();
		listEdges = new LinkedList<MyEdge>();
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
	public void addSewerEntry(double x, double y) throws DelaunayError {
		// Search for the point
		MyPoint sewerPoint = theMesh.getPoint(x, y);
		addSewerEntry(sewerPoint);
	}

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
		MyPoint sewerPoint = theMesh.getPoint(x, y);
		addSewerEntry(sewerPoint);
	}

	/**
	 * add a sewer entry
	 *
	 * @param sewerPoint
	 * @throws DelaunayError
	 */
	public void addSewerEntry(MyPoint sewerPoint) throws DelaunayError {
		listPrepare = new LinkedList<MyPoint>();
		listPrepare.add(sewerPoint);
	}

	/**
	 * add a sewer exit
	 *
	 * @param x
	 * @param y
	 * @throws DelaunayError
	 */
	public void addSewerExit(double x, double y) throws DelaunayError {
		// Search for the point
		MyPoint sewerPoint = theMesh.getPoint(x, y);
		addSewerExit(sewerPoint);
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
		MyPoint sewerPoint = theMesh.getPoint(x, y, z);
		addSewerExit(sewerPoint);
	}

	/**
	 * add a sewer exit
	 *
	 * @param sewerPoint
	 * @throws DelaunayError
	 */
	public void addSewerExit(MyPoint sewerPoint) throws DelaunayError {
		listPrepare.add(sewerPoint);
		sewerSet(listPrepare);
		listPrepare = new LinkedList<MyPoint>();
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
		MyPoint aPoint = theMesh.getPoint(x, y, z);
		addSewerPoint(aPoint);
	}

	/**
	 * add a sewer point (neither start or exit
	 *
	 * @param sewerPoint
	 * @throws DelaunayError
	 */
	public void addSewerPoint(MyPoint sewerPoint) throws DelaunayError {
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
	public void setSewerPoint(MyPoint sewerPoint) throws DelaunayError {
		addSewerEntry(sewerPoint);
	}

	// ----------------------------------------------------------------
	/**
	 * Start sewers definition
	 *
	 * @throws DelaunayError
	 */
	public void sewerStart() throws DelaunayError {
		branchStart(ConstraintType.SEWER, false);
	}

	/**
	 * define a new sewer branch
	 *
	 * @param sewerPoint
	 * @throws DelaunayError
	 */
	public void sewerSet(LinkedList<MyPoint> sewerList) throws DelaunayError {
		if (listDefinition == ConstraintType.NONE)
			branchStart(ConstraintType.SEWER, false);
		else if (listDefinition != ConstraintType.SEWER) {
			branchValidate();
			branchStart(ConstraintType.SEWER, false);
		}
		setNewBranch(sewerList);
	}

	/**
	 * Validate and end sewer definition
	 *
	 * @throws DelaunayError
	 */
	public void sewerValidate() throws DelaunayError {
		if (listDefinition != ConstraintType.NONE)
			branchValidate();
		listDefinition = ConstraintType.NONE;
	}

	// ----------------------------------------------------------------
	/**
	 * Start ditches definition
	 *
	 * @throws DelaunayError
	 */
	public void ditchStart() throws DelaunayError {
		branchStart(ConstraintType.DITCH);
	}

	/**
	 * define a new ditch branch
	 *
	 * @param ditchList
	 * @throws DelaunayError
	 */
	public void ditchSet(LinkedList<MyPoint> ditchList) throws DelaunayError {
		if (listDefinition == ConstraintType.NONE)
			branchStart(ConstraintType.DITCH);
		else if (listDefinition != ConstraintType.DITCH) {
			branchValidate();
			branchStart(ConstraintType.DITCH);
		}
		setNewBranch(ditchList);
	}

	/**
	 * Validate and end ditches definition
	 *
	 * @throws DelaunayError
	 */
	public void ditchValidate() throws DelaunayError {
		if (listDefinition != ConstraintType.NONE)
			branchValidate();
		listDefinition = ConstraintType.NONE;
	}

	// ----------------------------------------------------------------
	/**
	 * Start rivers definition
	 *
	 * @throws DelaunayError
	 */
	public void riverStart() throws DelaunayError {
		branchStart(ConstraintType.RIVER);
	}

	/**
	 * define a new river branch
	 *
	 * @param riverList
	 * @throws DelaunayError
	 */
	public void riverSet(LinkedList<MyPoint> riverList) throws DelaunayError {
		if (listDefinition == ConstraintType.NONE)
			branchStart(ConstraintType.RIVER);
		else if (listDefinition != ConstraintType.RIVER) {
			branchValidate();
			branchStart(ConstraintType.RIVER);
		}
		setNewBranch(riverList);
	}

	/**
	 * Validate and end rivers definition
	 *
	 * @throws DelaunayError
	 */
	public void riverValidate() throws DelaunayError {
		if (listDefinition != ConstraintType.NONE)
			branchValidate();
		listDefinition = ConstraintType.NONE;
	}

	// ----------------------------------------------------------------
	/**
	 * Start walls definition
	 *
	 * @throws DelaunayError
	 */
	public void wallStart() throws DelaunayError {
		branchStart(ConstraintType.WALL);
	}

	/**
	 * define a new wall branch
	 *
	 * @param wallList
	 * @throws DelaunayError
	 */
	public void wallSet(LinkedList<MyPoint> wallList) throws DelaunayError {
		if (listDefinition == ConstraintType.NONE)
			branchStart(ConstraintType.WALL);
		else if (listDefinition != ConstraintType.WALL) {
			branchValidate();
			branchStart(ConstraintType.WALL);
		}
		setNewBranch(wallList);
	}

	/**
	 * Validate and end walls definition
	 *
	 * @throws DelaunayError
	 */
	public void wallValidate() throws DelaunayError {
		if (listDefinition != ConstraintType.NONE)
			branchValidate();
		listDefinition = ConstraintType.NONE;
	}

	// ----------------------------------------------------------------

}
