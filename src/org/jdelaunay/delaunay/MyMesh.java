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
		double cle_ref1;
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
					if ((cle > cle_ref1) || (cle < 0))
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
					if ((cle < cle_ref1) && (cle >= 0))
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
		double cle_ref1;
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
					if ((cle > cle_ref1) || (cle < 0))
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
					if ((cle < cle_ref1) && (cle >= 0))
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
	 * Set missing GIDs for edges and points
	 */
	public void setAllGids() {
		quickSort_Points( 0, points.size()-1);
		ListIterator<MyPoint> iterPoint = points.listIterator();
		MyPoint vPoint = iterPoint.next();

		int lastIndex = -1;
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
		}
		quickSort_Points(0, points.size()-1);

		quickSort_Edges(0, edges.size()-1);
		ListIterator<MyEdge> iterEdge = edges.listIterator();
		MyEdge vEdge = iterEdge.next();

		lastIndex = -1;
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
		}
		quickSort_Edges(0, edges.size()-1);
	}

}
