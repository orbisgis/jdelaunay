package org.jdelaunay.delaunay;

/**
 * Delaunay Package.
 *
 * @author Jean-Yves MARTIN
 * @date 2009-01-12
 * @version 1.0
 */

import java.awt.*;
import java.io.*;
import java.util.*;

public class MyMesh {
	// Vectors with points and edges
	protected ArrayList<MyPoint> points;
	protected ArrayList<MyEdge> edges;
	protected LinkedList<MyTriangle> triangles;
	protected ArrayList<MyEdge> compEdges;

	// bounding box
	protected int maxx, maxy;
	private MyBox theBox;

	// Display elements
	private MyDrawing affiche;
	private long duration;
	private long startComputation;
	private boolean displayCircles;
	private MyPoint lastSewerPoint;
	private MyPoint lastWallPoint;
	private boolean meshComputed;

	// const strings
	static final public String MeshType_Wall = new String("Wall");
	static final public String MeshType_Sewer = new String("Sewer");

	/**
	 * Create an empty Mesh. Allocate data structures
	 * 
	 */
	public MyMesh() {
		// Generate vectors
		points = new ArrayList<MyPoint>();
		edges = new ArrayList<MyEdge>();
		triangles = new LinkedList<MyTriangle>();
		compEdges = new ArrayList<MyEdge>();

		maxx = 1200;
		maxy = 700;

		duration = 0;
		theBox = new MyBox();

		affiche = null;
		displayCircles = false;
		meshComputed = false;
		lastSewerPoint = null;
		lastWallPoint = null;
	}

	/**
	 * Set bounding box for the generation
	 * 
	 * @param maxx
	 * @param maxy
	 */
	public void setMax(int maxx, int maxy) {
		this.maxx = maxx;
		this.maxy = maxy;
	}

	/**
	 * Generate random points
	 * 
	 * @param _NbPoints
	 */
	public void setRandomPoints(int _NbPoints) {
		for (int i = 0; i < _NbPoints; i++) {
			// Generate random coordinates
			double x = Math.random() * maxx;
			double y = Math.random() * maxy;
			double z = Math.random() * 1000.0;

			points.add(new MyPoint(x, y, z, i));
		}
	}

	/**
	 * Generate random edges Can be applied only if points are created
	 * 
	 * @param _NbEdges
	 */
	public void setRandomEdges(int _NbEdges) {
		int NbPoints = points.size() - 1;
		if (NbPoints > 1) {
			for (int i = 0; i < _NbEdges; i++) {
				int start = (int) Math.round(Math.random() * NbPoints);
				int end = (int) Math.round(Math.random() * NbPoints);
				while (end == start)
					end = (int) Math.round(Math.random() * NbPoints);
				MyEdge anEdge = new MyEdge(points.get(start), points.get(end),
						i);
				anEdge.marked = 1;
				compEdges.add(anEdge);
			}
		}
	}

	/**
	 * Get JPanel in which Mesh is displayed
	 * 
	 * @return
	 */
	public MyDrawing getAffiche() {
		return affiche;
	}

	/**
	 * Set JPanel in which Mesh is displayed
	 * 
	 * @param affiche
	 */
	public void setAffiche(MyDrawing affiche) {
		this.affiche = affiche;
	}

	/**
	 * Get Duration
	 * 
	 * @return
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * @return
	 */
	public boolean isMeshComputed() {
		return meshComputed;
	}

	/**
	 * @param meshComputed
	 */
	public void setMeshComputed(boolean meshComputed) {
		this.meshComputed = meshComputed;
	}

	/**
	 * Display circles around triangles when displayed in the JPanel
	 * 
	 * @param displayCircles
	 */
	public void setDisplayCircles(boolean displayCircles) {
		this.displayCircles = displayCircles;
	}

	/**
	 * Compute Mesh bounding box
	 */
	public MyBox getBoundingBox() {
		theBox.init();

		for (MyPoint aPoint : points) {
			theBox.alterBox(aPoint);
		}
		return theBox;
	}

	/**
	 * Add the bounding box to current data
	 */
	public void addBoundingBox() {
		getBoundingBox();

		// Add bounding Box
		MyPoint aPoint1 = new MyPoint(theBox.minx, theBox.miny);
		MyPoint aPoint2 = new MyPoint(theBox.minx, theBox.maxy);
		MyPoint aPoint3 = new MyPoint(theBox.maxx, theBox.maxy);
		MyPoint aPoint4 = new MyPoint(theBox.maxx, theBox.miny);

		points.add(aPoint1);
		points.add(aPoint2);
		points.add(aPoint3);
		points.add(aPoint4);

		// Generate lines, taking into account the fact there are points withe
		// the same x and y
		MyTools.quickSort_Points(points);
		MyPoint LastPoint;

		// Do not remove points order because it is linked to the order we chose
		// for the points
		// join points 1 and 2 - same x
		LastPoint = aPoint1;
		for (MyPoint aPoint : points) {
			if (aPoint.x == LastPoint.x) {
				compEdges.add(new MyEdge(LastPoint, aPoint));
				LastPoint = aPoint;
			}
		}
		compEdges.add(new MyEdge(LastPoint, aPoint2));

		// join points 2 and 3 - same y
		LastPoint = aPoint2;
		for (MyPoint aPoint : points) {
			if (aPoint.y == LastPoint.y) {
				compEdges.add(new MyEdge(LastPoint, aPoint));
				LastPoint = aPoint;
			}
		}
		compEdges.add(new MyEdge(LastPoint, aPoint3));

		// join points 1 and 4 - same y
		LastPoint = aPoint1;
		for (MyPoint aPoint : points) {
			if (aPoint.y == LastPoint.y) {
				compEdges.add(new MyEdge(LastPoint, aPoint));
				LastPoint = aPoint;
			}
		}
		compEdges.add(new MyEdge(LastPoint, aPoint4));

		// join points 4 and 3 - same x
		LastPoint = aPoint4;
		for (MyPoint aPoint : points) {
			if (aPoint.x == LastPoint.x) {
				compEdges.add(new MyEdge(LastPoint, aPoint));
				LastPoint = aPoint;
			}
		}
		compEdges.add(new MyEdge(LastPoint, aPoint3));
	}

	/**
	 * Get the current number of points in the Mesh
	 * 
	 * @return NbPoints
	 */
	public int getNbPoints() {
		return points.size();
	}

	/**
	 * Get the current number of edges in the Mesh
	 * 
	 * @return NbEdges
	 */
	public int getNbEdges() {
		return edges.size();
	}

	/**
	 * Add an edge to the mesh
	 */
	public void addEdge(MyPoint aPoint1, MyPoint aPoint2) {
		if (!points.contains(aPoint1))
			points.add(aPoint1);
		if (!points.contains(aPoint2))
			points.add(aPoint2);
		MyEdge anEdge = new MyEdge(aPoint1, aPoint2);
		compEdges.add(anEdge);
	}

	/**
	 * Add an edge to the mesh
	 */
	public void addEdge(MyEdge anEdge) {
		MyPoint aPoint1 = anEdge.getStart();
		MyPoint aPoint2 = anEdge.getEnd();
		addEdge(aPoint1, aPoint2);
	}

	/**
	 * Get the current number of triangles in the Mesh
	 * 
	 * @return NbTriangles
	 */
	public int getNbTriangles() {
		return triangles.size();
	}

	/**
	 * Get the points structure
	 * 
	 * @return points
	 */
	public ArrayList<MyPoint> getPoints() {
		return points;
	}

	/**
	 * Set the points as the points of the array
	 * 
	 * @param _point
	 */
	public void setPoints(ArrayList<MyPoint> _point) {
		points = new ArrayList<MyPoint>();
		for (MyPoint aPoint : _point)
			points.add(aPoint);
	}

	/**
	 * Set the points as the array
	 * 
	 * @param _point
	 */
	public void setPointsRef(ArrayList<MyPoint> _point) {
		points = _point;
	}

	/**
	 * Get the edges structure
	 * 
	 * @return edges
	 */
	public ArrayList<MyEdge> getEdges() {
		return edges;
	}

	/**
	 * Set the edges as the edges of the ArrayList
	 * 
	 * @param _edges
	 */
	public void setEdges(ArrayList<MyEdge> _edges) {
		compEdges = new ArrayList<MyEdge>();
		for (MyEdge anEdge : _edges)
			compEdges.add(anEdge);
	}

	/**
	 * Set the edges as the edges of the LinkedList
	 * 
	 * @param _edges
	 */
	public void setEdges(LinkedList<MyEdge> _edges) {
		compEdges = new ArrayList<MyEdge>();
		for (MyEdge anEdge : _edges)
			compEdges.add(anEdge);
	}

	/**
	 * Set the edges as the LinkedList
	 * 
	 * @param _edges
	 */
	public void setEdgesRef(ArrayList<MyEdge> _edges) {
		compEdges = _edges;
	}

	/**
	 * Get the complementary edges structure This structure emorize the edges
	 * that have to be added to the triangularization
	 * 
	 * @return edges
	 */
	public ArrayList<MyEdge> getCompEdges() {
		return compEdges;
	}

	/**
	 * Get the triangle structure
	 * 
	 * @return triangle
	 */
	public LinkedList<MyTriangle> getTriangles() {
		return triangles;
	}

	/**
	 * get a point from its GID
	 * 
	 * @param gid
	 * @return aPoint
	 */
	public MyPoint getPointFromGID(int gid) {
		MyPoint aPoint = null;
		ListIterator<MyPoint> iterPoint = points.listIterator();
		while ((aPoint == null) && (iterPoint.hasNext())) {
			MyPoint vPoint = iterPoint.next();
			if (vPoint.gid == gid)
				aPoint = vPoint;
		}
		return aPoint;
	}

	/**
	 * get an edge from its GID
	 * 
	 * @param gid
	 * @return aPoint
	 */
	public MyEdge getEdgeFromGID(int gid) {
		MyEdge anEdge = null;
		ListIterator<MyEdge> iterEdge = edges.listIterator();
		while ((anEdge == null) && (iterEdge.hasNext())) {
			MyEdge vEdge = iterEdge.next();
			if (vEdge.gid == gid)
				anEdge = vEdge;
		}

		iterEdge = compEdges.listIterator();
		while ((anEdge == null) && (iterEdge.hasNext())) {
			MyEdge vEdge = iterEdge.next();
			if (vEdge.gid == gid)
				anEdge = vEdge;
		}
		return anEdge;
	}

	/**
	 * Start timer
	 */
	public void setStart() {
		startComputation = Calendar.getInstance().getTime().getTime();
	}

	/**
	 * End of timer - generate duration
	 */
	public void setEnd() {
		duration = Calendar.getInstance().getTime().getTime()
				- startComputation;
	}

	/**
	 * add a sewer entry
	 * 
	 * @param sewerPoint
	 * @throws DelaunayError
	 */
	public void addSewerEntry(MyPoint sewerPoint) throws DelaunayError {
		if (!points.contains(sewerPoint))
			throw new DelaunayError(
					DelaunayError.DelaunayError_invalidSewerPoint);
		else if (lastSewerPoint != null)
			throw new DelaunayError(
					DelaunayError.DelaunayError_invalidSewerStart);
		else {
			sewerPoint.setPointType(MeshType_Sewer);
			lastSewerPoint = sewerPoint;
		}
	}

	/**
	 * add a sewer exit
	 * 
	 * @param sewerPoint
	 * @throws DelaunayError
	 */
	public void addSewerExit(MyPoint sewerPoint) throws DelaunayError {
		if (!points.contains(sewerPoint))
			throw new DelaunayError(
					DelaunayError.DelaunayError_invalidSewerPoint);
		else if (lastSewerPoint == null)
			throw new DelaunayError(DelaunayError.DelaunayError_invalidSewerEnd);
		else if (lastSewerPoint.z <= sewerPoint.z)
			throw new DelaunayError(
					DelaunayError.DelaunayError_invalidSewerDirection);
		else {
			sewerPoint.setPointType(MeshType_Sewer);
			MyEdge anEdge = new MyEdge(lastSewerPoint, sewerPoint,
					MeshType_Sewer);
			anEdge.marked = 1;
			anEdge.outsideMesh = true;
			compEdges.add(anEdge);
			lastSewerPoint = null;
		}
	}

	/**
	 * add a sewer point (neither start or exit
	 * 
	 * @param sewerPoint
	 * @throws DelaunayError
	 */
	public void addSewerPoint(MyPoint sewerPoint) throws DelaunayError {
		if (lastSewerPoint == null)
			throw new DelaunayError(
					DelaunayError.DelaunayError_invalidSewerPoint);
		else if (lastSewerPoint.z <= sewerPoint.z)
			throw new DelaunayError(
					DelaunayError.DelaunayError_invalidSewerDirection);
		else {
			sewerPoint.setPointType(MeshType_Sewer);
			points.add(sewerPoint);
			sewerPoint.marked = true;
			MyEdge anEdge = new MyEdge(lastSewerPoint, sewerPoint,
					MeshType_Sewer);
			anEdge.marked = 1;
			anEdge.outsideMesh = true;
			compEdges.add(anEdge);
			lastSewerPoint = sewerPoint;
		}
	}

	/**
	 * use a sewer point to start a new branch
	 * 
	 * @param sewerPoint
	 * @throws DelaunayError
	 */
	public void setSewerPoint(MyPoint sewerPoint) throws DelaunayError {
		if (lastSewerPoint != null)
			throw new DelaunayError(DelaunayError.DelaunayError_invalidSewerEnd);
		else if (!sewerPoint.getPointType().equals("Sewer"))
			throw new DelaunayError(
					DelaunayError.DelaunayError_invalidSewerStart);
		else {
			lastSewerPoint = sewerPoint;
		}
	}

	/**
	 * Add a wall point start
	 * 
	 * @param wallPoint
	 * @throws DelaunayError
	 */
	public void addWallStart(MyPoint wallPoint) throws DelaunayError {
		if (!isMeshComputed())
			throw new DelaunayError(DelaunayError.DelaunayError_notGenerated);
		else if (!points.contains(wallPoint))
			throw new DelaunayError(
					DelaunayError.DelaunayError_invalidWallPoint);
		else {
			// Wall point start
			lastWallPoint = wallPoint;
			wallPoint.setPointType(MeshType_Wall);
		}
	}

	/**
	 * Add a wall point end
	 * 
	 * @param wallPoint
	 * @throws DelaunayError
	 */
	public void addWallEnd(MyPoint wallPoint) throws DelaunayError {
		if (!isMeshComputed())
			throw new DelaunayError(DelaunayError.DelaunayError_notGenerated);
		else if (!points.contains(wallPoint))
			throw new DelaunayError(
					DelaunayError.DelaunayError_invalidWallPoint);
		else if (lastWallPoint == null)
			throw new DelaunayError(
					DelaunayError.DelaunayError_invalidWallStart);
		else {
			// Wall point end
			wallPoint.setPointType(MeshType_Wall);

			MyEdge anEdge = new MyEdge(lastWallPoint, wallPoint, MeshType_Wall);
			anEdge.marked = 1;
			compEdges.add(anEdge);

			lastSewerPoint = null;
		}
	}

	/**
	 * Draw Mesh in the JPanel : triangles and edges. If duration is positive,
	 * also display it Must be used only when using package drawing
	 * 
	 * @param g
	 */
	public void displayObject(Graphics g) {
		getBoundingBox();
		double scaleX, scaleY;
		double minX, minY;

		scaleX = 1200 / (theBox.maxx - theBox.minx);
		scaleY = 600 / (theBox.maxy - theBox.miny);
		if (scaleX > scaleY)
			scaleX = scaleY;
		else
			scaleY = scaleX;
		minX = theBox.minx;
		minY = theBox.maxy;
		int decalageX = 10;
		int decalageY = 630;

		g.setColor(Color.black);
		g.drawString(triangles.size() + " Triangles - " + edges.size()
				+ " Edges - " + points.size() + " Points", decalageX,
				30 + decalageY);
		if (duration > 0) {
			g.drawString("Computation time : " + duration + " ms", decalageX,
					45 + decalageY);
		}

		g.setColor(Color.white);
		g.fillRect(decalageX - 5, 30 - 5, decalageX - 5 + 1200, 30 - 5 + 600);

		// Draw triangles
		if (!triangles.isEmpty()) {
			for (MyTriangle aTriangle : triangles) {
				aTriangle.setColor(g);
				aTriangle.displayObject(g, decalageX, decalageY, minX, minY,
						scaleX, scaleY);
			}

			if (displayCircles)
				for (MyTriangle aTriangle : triangles) {
					aTriangle.setColor(g);
					aTriangle.displayObjectCircles(g, decalageX, decalageY);
				}
		}
		// Draw lines
		if (false)
			if (!compEdges.isEmpty())
				for (MyEdge aVertex : compEdges) {
					aVertex.setColor(g);
					aVertex.displayObject(g, decalageX, decalageY, minX, minY,
							scaleX, scaleY);
				}

		if (false)
			if (!edges.isEmpty())
				for (MyEdge aVertex : edges) {
					aVertex.setColor(g);
					aVertex.displayObject(g, decalageX, decalageY, minX, minY,
							scaleX, scaleY);
				}

		int psize = points.size();
		if (false)
			if ((psize > 0) && (psize < 100)) {
				for (MyPoint aPoint : points) {
					aPoint.setColor(g);
					aPoint.displayObject(g, decalageX, decalageY, minX, minY,
							scaleX, scaleY);
				}
			}
	}

	/**
	 * Refresh Mesh display in the JPanel Must be used only when using package
	 * drawing
	 */
	public void refresh() {
		if (affiche != null)
			affiche.refresh();
	}

	/**
	 * Save the Mesh elements in a XML file
	 */
	public void saveMeshXML() {
		Writer writer;
		try {
			writer = new FileWriter("Mesh.xml");
			writer.write("<Mesh>\n");
			writer.write("\t<Points>\n");
			for (MyPoint aPoint : points) {
				writer.write("\t\t<Point id=\"" + points.indexOf(aPoint)
						+ "\">\n");
				writer.write("\t\t\t<X>" + aPoint.x + "</X>\n");
				writer.write("\t\t\t<Y>" + aPoint.y + "</Y>\n");
				writer.write("\t\t\t<Z>" + aPoint.z + "</Z>\n");
				if (aPoint.type == null)
					writer.write("\t\t\t<Type />\n");
				else
					writer.write("\t\t\t<Type>" + aPoint.type + "</Type>\n");
				writer.write("\t\t</Point>\n");
			}
			writer.write("\t</Points>\n");

			writer.write("\t<Edges>\n");
			for (MyEdge anEdge : edges) {
				writer.write("\t\t<Segment id=\"" + edges.indexOf(anEdge)
						+ "\">\n");
				writer.write("\t\t\t<Start>" + points.indexOf(anEdge.point[0])
						+ "</Start>\n");
				writer.write("\t\t\t<End>" + points.indexOf(anEdge.point[1])
						+ "</End>\n");
				if (anEdge.type == null)
					writer.write("\t\t\t<Type />\n");
				else
					writer.write("\t\t\t<Type>" + anEdge.type + "</Type>\n");
				if (anEdge.left == null)
					writer.write("\t\t\t<Left>-1</Left>\n");
				else
					writer.write("\t\t\t<Left>"
							+ triangles.indexOf(anEdge.left) + "</Left>\n");
				if (anEdge.right == null)
					writer.write("\t\t\t<Right>-1</Right>\n");
				else
					writer.write("\t\t\t<Right>"
							+ triangles.indexOf(anEdge.right) + "</Right>\n");
				writer.write("\t\t</Segment>\n");
			}
			writer.write("\t</Edges>\n");

			writer.write("\t<Triangles>\n");
			for (MyTriangle aTriangle : triangles) {
				writer.write("\t\t<Triangle id=\""
						+ triangles.indexOf(aTriangle) + "\">\n");
				for (int i = 0; i < 3; i++)
					writer.write("\t\t\t<Edge>"
							+ edges.indexOf(aTriangle.edges[i]) + "</Edge>\n");
				writer.write("\t\t</Triangle>\n");
			}
			writer.write("\t</Triangles>\n");

			writer.write("</Mesh>\n");
			writer.close();
		} catch (IOException e) {
		}
	}

	/**
	 * Save the Mesh in a file
	 */
	public void saveMesh() {
		saveMesh("Mesh.txt");
	}

	/**
	 * Save the Mesh in a file
	 * 
	 * @param path
	 */
	public void saveMesh(String path) {
		Writer writer;
		try {
			writer = new FileWriter(path);
			for (MyPoint aPoint : points) {
				writer.write(aPoint.x + "\t" + aPoint.y + "\t" + aPoint.z
						+ "\t" + aPoint.gid + "\n");
			}

			writer.write("\n");
			for (MyEdge anEdge : edges) {
				writer.write(anEdge.point[0].gid + "\t" + anEdge.point[1].gid
						+ "\t" + anEdge.gid + "\n");
			}
			for (MyEdge anEdge : compEdges) {
				writer.write(anEdge.point[0].gid + "\t" + anEdge.point[1].gid
						+ "\t" + anEdge.gid + "\n");
			}

			writer.close();
		} catch (IOException e) {
		}
	}

	/**
	 * Read Mesh points from the file
	 */
	public void readMesh() {
		readMesh("Mesh.txt");
	}

	/**
	 * Read Mesh points from the file
	 */
	public void readMesh(String path) {
		Reader reader;
		try {
			String delimiteurs = "\t";
			reader = new FileReader(path);

			BufferedReader in = new BufferedReader(reader);
			String ligne = in.readLine();
			int step = 0;
			int i = 0;
			while (ligne != null) {
				StringTokenizer st = new StringTokenizer(ligne, delimiteurs);
				switch (step) {
				case 0:
					MyPoint aPoint = new MyPoint();
					i = 0;
					while (st.hasMoreTokens()) {
						String mot = st.nextToken();
						switch (i) {
						case 0:
							aPoint.x = Double.parseDouble(mot);
							break;
						case 1:
							aPoint.y = Double.parseDouble(mot);
							break;
						case 2:
							aPoint.z = Double.parseDouble(mot);
							break;
						case 3:
							aPoint.gid = Integer.parseInt(mot);
							break;
						}
						i++;
					}
					if (i >= 3)
						points.add(aPoint);
					else
						step++;
					break;
				case 1:
					MyEdge anEdge = new MyEdge();
					int gid;
					i = 0;
					while (st.hasMoreTokens()) {
						String mot = st.nextToken();
						switch (i) {
						case 0:
							gid = Integer.parseInt(mot);
							anEdge.point[0] = getPointFromGID(gid);
							break;
						case 1:
							gid = Integer.parseInt(mot);
							anEdge.point[1] = getPointFromGID(gid);
							break;
						case 2:
							anEdge.gid = Integer.parseInt(mot);
							break;
						}
						i++;
					}
					if (i >= 1)
						compEdges.add(anEdge);
					else
						step++;
					break;
				}
				ligne = in.readLine();
			}
		} catch (IOException e) {
		}
	}

	/**
	 * Save Mesh
	 * 
	 * @param path
	 */
	public void saveMeshUTF(String path) {
		try {
			DataOutputStream output = new DataOutputStream(
					new FileOutputStream(path));
			int NbTriangles = getNbTriangles();

			output.writeUTF("Mesh triangles quantity:\n");
			output.writeUTF(NbTriangles + "\n");
			output.writeUTF("Triangle\tP1\tP2\tP3\n");
			int i = 0;
			for (MyTriangle aTriangle : triangles) {
				i++;
				output.writeUTF(i + "\t");
				for (int j = 0; j < 3; j++) {
					MyPoint aPoint = aTriangle.points[j];
					output.writeUTF(aPoint.x + "\t" + aPoint.y + "\t"
							+ aPoint.z);
					if (j < 2)
						output.writeUTF("\t");
					else
						output.writeUTF("\n");
				}
			}
			output.writeUTF("\n");
			output.flush();
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Export to VRML file Mesh.wrl
	 */
	public void VRMLexport() {
		VRMLexport("Mesh.wrl");
	}

	/**
	 * Export to VRML file
	 * 
	 * @param path
	 */
	public void VRMLexport(String path) {
		try {
			Writer writer = new FileWriter(path);

			writer.write("#VRML V2.0 utf8\n");
			writer.write("Background{\n");
			writer.write("skyColor 0 0.3 0.8\n");
			writer.write("}\n");
			writer.write("\n");
			writer.write("Transform {\n");
			writer.write("scale 1 1 1\n");
			writer.write("children [\n");
			writer.write("Shape {\n");
			writer.write("appearance Appearance {\n");
			writer.write("material Material {\n");
			writer.write("diffuseColor 1 1 .6\n");
			writer.write("} # end material\n");
			writer.write("} # end appearance\n");
			writer.write("geometry IndexedFaceSet {\n");
			writer.write("convex FALSE\n");
			writer.write("solid FALSE\n");
			writer.write("\n");
			int size = points.size();
			double zcamera = 0;
			double dx=0, dy=0, dz=0;
			if (size > 3) {
				double xmin = 0, xmax = 0;
				double ymin = 0, ymax = 0;
				double zmin = 0, zmax = 0;
				xmin = points.get(0).x;
				xmax = xmin;
				ymin = points.get(0).y;
				ymax = ymin;
				zmin = points.get(0).z;
				zmax = zmin;
				for (MyPoint aPoint : points) {
					if (xmax < aPoint.x)
						xmax = aPoint.x;
					if (xmin > aPoint.x)
						xmin = aPoint.x;
					if (ymax < aPoint.y)
						ymax = aPoint.y;
					if (ymin > aPoint.y)
						ymin = aPoint.y;
					if (zmax < aPoint.z)
						zmax = aPoint.z;
					if (zmin > aPoint.z)
						zmin = aPoint.z;
				}

				double distance = xmax - xmin;
				if (distance < ymax - ymin)
					distance = ymax - ymin;
				if (distance < zmax - zmin)
					distance = zmax - zmin;

				dx = (xmax + xmin) / 2;
				dy = (ymax + ymin) / 2;
				dz = zmin;
				zcamera = (zmax - zmin) + distance;

				writer.write("coord Coordinate {\n");
				writer.write("point [\n");
				writer.write("#x y z pt\n");
				for (MyPoint aPoint : points) {
					writer.write(" #Point " + (aPoint.gid - 1) + "\n");
					writer.write(" " + (aPoint.x - dx) + " " + (aPoint.y - dy)
							+ " " + (aPoint.z - dz) + "\n");
				}
				// add points for walls
				for (MyEdge anEdge : compEdges) {
					if (anEdge.getType() == MeshType_Wall) {
						writer.write("#wall points\n");
						for (int i = 0; i < 2; i++) {
							MyPoint aPoint = anEdge.point[i];
							writer.write(" #Wall Point " + (aPoint.gid - 1)
									+ "\n");
							writer.write(" " + (aPoint.x - dx) + " "
									+ (aPoint.y - dy) + " "
									+ (aPoint.z - dz + 2) + "\n");
						}
					}
				}

				writer.write("] # end point\n");
				writer.write("} # end coord\n");
				writer.write("\n");
				writer.write("coordIndex [\n");
				for (MyTriangle aTriangle : triangles) {
					writer.write("#triangle " + (aTriangle.gid - 1) + "\n");
					for (int i = 0; i < 3; i++)
						writer.write((aTriangle.points[i].gid - 1) + "\t");
					writer.write("-1\n");
				}

				// add walls
				int index = points.size();
				for (MyEdge anEdge : compEdges) {
					if (anEdge.getType() == MeshType_Wall) {
						writer.write("#wall " + (anEdge.gid - 1) + "\n");
						writer.write((anEdge.point[0].gid - 1) + "\t");
						writer.write((anEdge.point[1].gid - 1) + "\t");
						writer.write((index + 1) + "\t");
						writer.write((index) + "\t");
						writer.write("-1\n");
						index += 2;
					}
				}

				writer.write("\n");
				writer.write("] # end coordIndex\n");
				writer.write("\n");
				writer.write("# color definitions\n");
				writer.write("colorPerVertex FALSE\n");
				writer.write("color Color {\n");
				writer.write("color [\n");
				writer
						.write("#defining a palette of colors to use in the colorIndex\n");
				writer.write("0.0 1.0 0.0 # color #0 is green\n");
				writer.write("1.0 0.75 0.5 # color #1 is wall\n");
				writer.write("] # end inner color group\n");
				writer.write("} # end color node\n");
				writer.write("colorIndex [\n");
				writer.write("#color node\n");
				for (MyTriangle aTriangle : triangles) {
					writer.write("0 #triangle " + (aTriangle.gid - 1) + "\n");
				}
				for (MyEdge anEdge : compEdges) {
					if (anEdge.getType() == MeshType_Wall) {
						writer.write("1 #wall edge " + (anEdge.gid - 1) + "\n");
					}
				}
				writer.write("] # end colorIndex\n");
				writer.write("\n");
			}
			writer.write("} # end geometry\n");
			writer.write("} # end shape\n");
			writer.write("] # end children\n");
			writer.write("} # end transform\n");
			writer.write("\n");

			// Add sewer
			if (false)
			for (MyEdge anEdge : compEdges) {
				if (anEdge.getType() == MeshType_Sewer) {
					// Add sewer element NOT FINISHED SO LET IT UNREACHABLE
					MyPoint aPoint1 = anEdge.getStart();
					MyPoint aPoint2 = anEdge.getEnd();
					double length = Math.sqrt(aPoint1.squareDistance(aPoint2));
					
					writer.write("# sewer\n");
					writer.write("Transform {\n");
					writer.write("translation " + (aPoint1.x - dx) + " " + (aPoint1.y - dy) + " " + (aPoint1.z - dz) + " " + "\n");
					writer.write("rotation 1 0 0 0\n");
					writer.write("children [\n");
					writer.write(" Shape {\n");
					writer.write("geometry Cylinder {\n");
					writer.write("height "+length+"\n");
					writer.write("radius .2\n");
					writer.write("top TRUE\n");
					writer.write("side TRUE\n");
					writer.write("bottom FALSE\n");
					writer.write("} # end geometry\n");
					writer.write("appearance Appearance {\n");
					writer.write(" material Material {\n");
					writer.write("diffuseColor .8 1 .8\n");
					writer.write("} # end material\n");
					writer.write("} # end appearance\n");
					writer.write("} # end shape\n");
					writer.write("] # end chilren\n");
					writer.write("} # end Transform\n");
				}
			}

			writer.write("\n");
			writer.write("Viewpoint {\n");
			writer.write("description \"middle\"\n");
			writer.write("position 0 0 " + zcamera + "\n");
			writer.write("} # end viewpoint\n");
			writer.write("\n");

			writer.close();
		} catch (IOException e) {
		}

	}

	/**
	 * Set missing GIDs for points
	 */
	protected void SetAllGIDs_Point() {
		int nextIndex = 0;
		int curIndex = 0;
		ListIterator<MyPoint>iterPoints1 = points.listIterator();
		ListIterator<MyPoint>iterPoints2 = points.listIterator();

		// sort points
		MyTools.quickSortGID_Points(points, 0, points.size()-1);
		
		// Then process every point
		while (iterPoints1.hasNext()) {
			MyPoint aPoint = iterPoints1.next();
			if (aPoint.getGid() <= 0) {
				// need to set it
				curIndex++;

				// reach next possible value
				while ((iterPoints2.hasNext()) && (curIndex >= nextIndex)) {
					MyPoint testPoint = iterPoints2.next();
					nextIndex = testPoint.getGid();
					if (curIndex <= nextIndex) {
						curIndex++;
					}
				}
				
				aPoint.setGid(curIndex);
			}
		}
		
		// At least, sort points
		MyTools.quickSortGID_Points(points, 0, points.size()-1);
	}

	/**
	 * Set missing GIDs for edges
	 */
	protected void SetAllGIDs_Edges() {
		int nextIndex = 0;
		int curIndex = 0;
		ListIterator<MyEdge>iterEdges1 = edges.listIterator();
		ListIterator<MyEdge>iterEdges2 = edges.listIterator();

		// sort edges
		MyTools.quickSortGID_Edges(edges, 0, edges.size()-1);
		
		// Then process every edge
		while (iterEdges1.hasNext()) {
			MyEdge anEdge = iterEdges1.next();
			if (anEdge.getGid() <= 0) {
				// need to set it
				curIndex++;

				// reach next possible value
				while ((iterEdges2.hasNext()) && (curIndex >= nextIndex)) {
					MyEdge testEdge = iterEdges2.next();
					nextIndex = testEdge.getGid();
					if (curIndex <= nextIndex) {
						curIndex++;
					}
				}
				
				anEdge.setGid(curIndex);
			}
		}
		
		// At least, sort edges
		MyTools.quickSortGID_Edges(edges, 0, edges.size()-1);
	}

	/**
	 * Set GIDs for triangles
	 */
	protected void SetAllGIDs_Triangle() {
		int curIndex = 0;
		for (MyTriangle aTriangle : triangles) {
			curIndex++;
			aTriangle.setGid(curIndex);
		}
	}

	/**
	 * Set missing GIDs for points, edges and triangles
	 */
	public void setAllGids() {
		// Process points
		SetAllGIDs_Point();

		// Process edges
		SetAllGIDs_Edges();

		// Process triangles
		SetAllGIDs_Triangle();
	}
}
