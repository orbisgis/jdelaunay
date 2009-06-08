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
	private double bminx, bmaxx, bminy, bmaxy;
	private MyPoint pminx, pmaxx, pminy, pmaxy;

	// Display elements
	private MyDrawing affiche;
	private long duration;
	private long startComputation;
	private boolean displayCircles;

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
		bminx = bmaxx = bminy = bmaxy = 0.0;
		pminx = pmaxx = pminy = pmaxy = null;

		affiche = null;
		displayCircles = false;
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
	public void getBoundingBox() {
		bminx = bmaxx = bminy = bmaxy = 0.0;
		pminx = pmaxx = pminy = pmaxy = null;

		for (MyPoint aPoint : points) {
			double x = aPoint.getX();
			double y = aPoint.getY();

			// update bounding box
			if (pminx == null) {
				bminx = bmaxx = x;
				bminy = bmaxy = y;
				pminx = pmaxx = pminy = pmaxy = aPoint;
			} else {
				if (bminx > x) {
					bminx = x;
					pminx = aPoint;
				} else if (bmaxx < x) {
					bmaxx = x;
					pmaxx = aPoint;
				}
				if (bminy > y) {
					bminy = y;
					pminy = aPoint;
				} else if (bmaxy < y) {
					bmaxy = y;
					pmaxy = aPoint;
				}
			}
		}
	}

	/**
	 * Add the bounding box to current data
	 */
	public void addBoundingBox() {
		getBoundingBox();

		// Add bounding Box
		MyPoint aPoint1 = new MyPoint(bminx, bminy);
		MyPoint aPoint2 = new MyPoint(bminx, bmaxy);
		MyPoint aPoint3 = new MyPoint(bmaxx, bmaxy);
		MyPoint aPoint4 = new MyPoint(bmaxx, bminy);

		points.add(aPoint1);
		points.add(aPoint2);
		points.add(aPoint3);
		points.add(aPoint4);

		compEdges.add(new MyEdge(aPoint1, pminx));
		compEdges.add(new MyEdge(pminx, aPoint2));

		compEdges.add(new MyEdge(aPoint2, pmaxy));
		compEdges.add(new MyEdge(pmaxy, aPoint3));

		compEdges.add(new MyEdge(aPoint3, pmaxx));
		compEdges.add(new MyEdge(pmaxx, aPoint4));

		compEdges.add(new MyEdge(aPoint4, pminy));
		compEdges.add(new MyEdge(pminy, aPoint1));
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
	 * Draw Mesh in the JPanel : triangles and edges. If duration is positive,
	 * also display it Must be used only when using package drawing
	 *
	 * @param g
	 */
	public void displayObject(Graphics g) {
		getBoundingBox();
		double scaleX, scaleY;
		double minX, minY;

		scaleX = 1200 / (bmaxx - bminx);
		scaleY = 600 / (bmaxy - bminy);
		if (scaleX > scaleY)
			scaleX = scaleY;
		else
			scaleY = scaleX;
		minX = bminx;
		minY = bmaxy;
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
				if (aTriangle.isFlatSlope()){

					System.out.println("Plat " + aTriangle.getGid());
				}
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
		if ((psize >0) && (psize<100)) {
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


	public void VRMLexport() {
		VRMLexport("Mesh.wrl");
	}

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
			writer.write("coord Coordinate {\n");
			writer.write("point [\n");
			writer.write("#x y z pt\n");
			double x=0, y=0, z=0, z1=0;
			for (MyPoint aPoint : points) {
				writer.write(" #Point "+ (aPoint.gid-1) +"\n");
				writer.write(" " + aPoint.x + " "+aPoint.y + " " + aPoint.z +"\n");

				x += aPoint.x;
				y += aPoint.y;
				if (z < aPoint.z) z = aPoint.z;
				if (z1 > aPoint.z) z1 = aPoint.z;
			}
			if (points.size() > 0) {
				x /= points.size();
				y /= points.size();
			}
			z += (z-z1) + 10;
			writer.write("] # end point\n");
			writer.write("} # end coord\n");
			writer.write("\n");
			writer.write("coordIndex [\n");
			for (MyTriangle aTriangle : triangles) {
				writer.write("#triangle " + (aTriangle.gid-1) + "\n");
				for (int i=0; i<3; i++)
					writer.write((aTriangle.points[i].gid-1) + "\t");
				writer.write("-1\n");
			}
			writer.write("\n");
			writer.write("] # end coordIndex\n");
			writer.write("\n");
			writer.write("# color definitions\n");
			writer.write("colorPerVertex FALSE\n");
			writer.write("color Color {\n");
			writer.write("color [\n");
			writer.write("#defining a palette of colors to use in the colorIndex\n");
			writer.write("0.0 1.0 0.0 # color #0 is green\n");
			writer.write("1.0 0.0 0.0 # color #1 is red\n");
			writer.write("0.0 0.0 1.0 # color #2 is blue\n");
			writer.write("] # end inner color group\n");
			writer.write("} # end color node\n");
			writer.write("colorIndex [\n");
			writer.write("#color node\n");
			for (MyTriangle aTriangle : triangles) {
				writer.write("0 #triangle " + (aTriangle.gid-1)+ "\n");
			}
			writer.write("] # end colorIndex\n");
			writer.write("\n");
			writer.write("} # end geometry\n");
			writer.write("} # end shape\n");
			writer.write("] # end children\n");
			writer.write("} # end transform\n");
			writer.write("\n");
			writer.write("Viewpoint {\n");
			writer.write("description \"middle\"\n");
			writer.write("position "+x+" "+y+" "+z+"\n");
			writer.write("} # end viewpoint\n");
			writer.write("\n");

			writer.close();
		} catch (IOException e) {
		}

	}

	/**
	 * sort criteria for GIDs
	 *
	 * @param v1
	 * @param v2
	 * @return
	 */
	private int sortCriteria(int v1, int v2) {
		int value = 0;
		if (v1 == v2)
			value = 0;
		else if (v1 == -1)
			value = -1;
		else if (v2 == -1)
			value = 1;
		else if (v1 < v2)
			value = 1;
		else
			value = -1;
		return value;
	}
	/**
	 * Quick sort on points Ordered according to x and y
	 *
	 * @param min_index
	 * @param max_index
	 */
	private void quickSort_Points(int min_index,
			int max_index) {
		int i, j;
		int enreg_ref;
		int cle_ref1;
		boolean found;
		MyPoint anObject;

		i = min_index;
		j = max_index;
		enreg_ref = (max_index + min_index) / 2;
		anObject = points.get(enreg_ref);
		cle_ref1 = anObject.getGid();
		do {
			// first : increasing index
			found = false;
			while (!found) {
				if (i > max_index)
					found = true;
				else {
					anObject = points.get(i);
					int cle = anObject.getGid();
					if (sortCriteria(cle, cle_ref1) <= 0)
						found = true;
					else
						i++;
				}
			}
			// second : decreasing index
			found = false;
			while (!found) {
				if (min_index > j)
					found = true;
				else {
					anObject = points.get(j);
					int cle = anObject.getGid();
					if (sortCriteria(cle, cle_ref1) >= 0)
						found = true;
					else
						j--;
				}
			}
			// exchange values
			if (i <= j) {
				// we can change values
				anObject = points.get(i);
				points.set(i, points.get(j));
				points.set(j, anObject);

				i++;
				j--;
			}
		} while (i <= j);

		// Recurrent calls
		if (min_index < j) {
			// if left side is not empty
			quickSort_Points(min_index, j);
		}
		if (max_index > i) {
			// if right side is not empty
			quickSort_Points(i, max_index);
		}
	}

	/**
	 * Quick sort on points Ordered according to x and y
	 *
	 * @param min_index
	 * @param max_index
	 */
	private void quickSort_Edges(int min_index,
			int max_index) {
		int i, j;
		int enreg_ref;
		int cle_ref1;
		boolean found;
		MyEdge anObject;

		i = min_index;
		j = max_index;
		enreg_ref = (max_index + min_index) / 2;
		anObject = edges.get(enreg_ref);
		cle_ref1 = anObject.getGid();
		do {
			// first : increasing index
			found = false;
			while (!found) {
				if (i > max_index)
					found = true;
				else {
					anObject = edges.get(i);
					int cle = anObject.getGid();
					if (sortCriteria(cle, cle_ref1) <= 0)
						found = true;
					else
						i++;
				}
			}
			// second : decreasing index
			found = false;
			while (!found) {
				if (min_index > j)
					found = true;
				else {
					anObject = edges.get(j);
					int cle = anObject.getGid();
					if (sortCriteria(cle, cle_ref1) >= 0)
						found = true;
					else
						j--;
				}
			}
			// exchange values
			if (i <= j) {
				// we can change values
				anObject = edges.get(i);
				edges.set(i, edges.get(j));
				edges.set(j, anObject);

				i++;
				j--;
			}
		} while (i <= j);

		// Recurrent calls
		if (min_index < j) {
			// if left side is not empty
			quickSort_Edges(min_index, j);
		}
		if (max_index > i) {
			// if right side is not empty
			quickSort_Edges(i, max_index);
		}
	}

	/**
	 * Quick sort on points Ordered according to x and y
	 *
	 * @param min_index
	 * @param max_index
	 */
	private void quickSort_Triangles( int min_index,
			int max_index) {
		int i, j;
		int enreg_ref;
		int cle_ref1;
		boolean found;
		MyTriangle anObject;

		i = min_index;
		j = max_index;
		enreg_ref = (max_index + min_index) / 2;
		anObject = triangles.get(enreg_ref);
		cle_ref1 = anObject.getGid();
		do {
			// first : increasing index
			found = false;
			while (!found) {
				if (i > max_index)
					found = true;
				else {
					anObject = triangles.get(i);
					int cle = anObject.getGid();
					if (sortCriteria(cle, cle_ref1) <= 0)
						found = true;
					else
						i++;
				}
			}
			// second : decreasing index
			found = false;
			while (!found) {
				if (min_index > j)
					found = true;
				else {
					anObject = triangles.get(j);
					int cle = anObject.getGid();
					if (sortCriteria(cle, cle_ref1) >= 0)
						found = true;
					else
						j--;
				}
			}
			// exchange values
			if (i <= j) {
				// we can change values
				anObject = triangles.get(i);
				triangles.set(i, triangles.get(j));
				triangles.set(j, anObject);

				i++;
				j--;
			}
		} while (i <= j);

		// Recurrent calls
		if (min_index < j) {
			// if left side is not empty
			quickSort_Triangles(min_index, j);
		}
		if (max_index > i) {
			// if right side is not empty
			quickSort_Triangles(i, max_index);
		}
	}




	/**
	 * Set missing GIDs for edges and points
	 */
	public void setAllGids() {
		// Process points
		quickSort_Points( 0, points.size()-1);
		ListIterator<MyPoint> iterPoint = points.listIterator();
		MyPoint vPoint = iterPoint.next();

		int lastIndex = 0;
		for (MyPoint aPoint:points) {
			if (aPoint.gid < 0) {
				lastIndex++;
				int gid = vPoint.getGid();
				while (gid == lastIndex) {
					lastIndex++;
					vPoint = iterPoint.next();
					gid = vPoint.getGid();
				}
				aPoint.setGid(lastIndex);
			}
			else
				lastIndex = aPoint.gid;
		}
		quickSort_Points(0, points.size()-1);

		// Process edges
		quickSort_Edges(0, edges.size()-1);
		ListIterator<MyEdge> iterEdge = edges.listIterator();
		MyEdge vEdge = iterEdge.next();

		lastIndex = 0;
		for (MyEdge anEdge:edges) {
			if (anEdge.gid < 0) {
				lastIndex++;
				int gid = vEdge.getGid();
				while (gid == lastIndex) {
					lastIndex++;
					vEdge = iterEdge.next();
					gid = vEdge.getGid();
				}
				anEdge.setGid(lastIndex);
			}
			else
				lastIndex = anEdge.gid;
		}
		quickSort_Edges(0, edges.size()-1);

		// Process triangles
		quickSort_Triangles(0, triangles.size()-1);
		ListIterator<MyTriangle> iterTriangle = triangles.listIterator();
		MyTriangle vTriangle = iterTriangle.next();
		lastIndex = 0;
		for (MyTriangle aTriangle:triangles) {
			if (aTriangle.gid < 0) {
				lastIndex++;
				int gid = vTriangle.getGid();
				while (gid == lastIndex) {
					lastIndex++;
					vTriangle = iterTriangle.next();
					gid = vTriangle.getGid();
				}
				aTriangle.setGid(lastIndex);
			}
			else
				lastIndex = aTriangle.gid;
		}
		quickSort_Triangles(0, triangles.size()-1);
	}
}
