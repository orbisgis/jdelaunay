package org.jdelaunay.delaunay;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * No regression test.
 * @author Adelin PIAU
 * @date 2010-11-08
 */
public class TestCheckMesh  extends BaseUtility {

	
	public void test_01() throws DelaunayError, IOException, ParseException, ClassNotFoundException {
		System.out.println("\n\ntest_1\n");
		WKTReader reader = new WKTReader();
		Polygon polygon = (Polygon) reader.read("POLYGON((3 3 0, 3 5 0, 8 6 0, 8 3 0, 3 3 0))");
		ConstraintPolygon aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(true);
		aPolygon.setUsePolygonZ(false);
		
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);

		BoundaryBox abox=new BoundaryBox();
		
		ArrayList<Point> listePoints=new ArrayList<Point>();
			listePoints.add(new Point(0, 3, 5));
			listePoints.add(new Point(2, 7, 5));
			listePoints.add(new Point(6, 8, 5));
			listePoints.add(new Point(11, 8, 5));
			listePoints.add(new Point(12, 6, 5));
		
		
		for(Point p:listePoints)
			abox.alterBox(p);
		aMesh.init(abox);
		
		aMesh.setPoints(listePoints);

		aMesh.processDelaunay();
		
		System.out.println("First check");
		aMesh.checkTriangularization();
		checkMeshTopo(aMesh);
		System.out.println("Next");
		

		aMesh.addPolygon(aPolygon);
		
//		show(aMesh);

		System.out.println("Second check");
		aMesh.checkTriangularization();
		checkMeshTopo(aMesh);
//		saveRefMesh(aMesh, "dataForTest/TestCheckMesh-test_01-meshRef.bin");
//		checkMesh(aMesh, "dataForTest/TestCheckMesh-test_01-meshRef.bin");

		
		aMesh.VRMLexport("dataForTest/TestCheckMesh-test_01.wrl");
	}
	
	
	
	/**
	 * Check when adding a polygon after processDelaunay.
	 * @throws DelaunayError
	 * @throws IOException
	 * @throws ParseException
	 * @throws ClassNotFoundException
	 */
	public void test_02() throws DelaunayError, IOException, ParseException, ClassNotFoundException {
		System.out.println("\n\ntest_2\n");
			WKTReader reader = new WKTReader();
			Polygon polygon = (Polygon) reader.read("POLYGON((55 80 10, 55 90 10, 35 90 10, 40 120 10, 75 110 10, 55 80 10))");
			ConstraintPolygon aPolygon = new ConstraintPolygon(polygon, 500);
			 
			MyMesh aMesh = new MyMesh();
			aMesh.setPrecision(1.0e-3);
			aMesh.setVerbose(true);
			aMesh.setPoints(getPoints());
			aMesh.setMax(1300, 700);
			
			aMesh.processDelaunay();

			System.out.println("First check");
			aMesh.checkTriangularization();
			checkMeshTopo(aMesh);
			System.out.println("Next");
			
			
			aPolygon.setEmpty(true);
			aMesh.addPolygon(aPolygon);
	
//			show(aMesh);
			
			System.out.println("Second check");
			aMesh.checkTriangularization();
			checkMeshTopo(aMesh);
//			saveRefMesh(aMesh, "dataForTest/TestCheckMesh-test_02-meshRef.bin");
//			checkMesh(aMesh, "dataForTest/TestCheckMesh-test_02-meshRef.bin");

//			System.out.println("end\n\nPush Enter to finish...");
//			System.in.read();	
			aMesh.VRMLexport("dataForTest/TestCheckMesh-test_02.wrl");
	}
	
	
	
	/**
	 * Check when adding a level edge after processDelaunay.
	 * @throws DelaunayError
	 * @throws IOException
	 * @throws ParseException
	 * @throws ClassNotFoundException
	 */
	public void test_03() throws DelaunayError, IOException, ParseException, ClassNotFoundException {	
		System.out.println("\n\ntest_3\n");
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		
		ArrayList<Point> listePoints=new ArrayList<Point>();
		listePoints.add(new Point(0, 3, 0));
		listePoints.add(new Point(12, 3, 0));
		listePoints.add(new Point(0, 8, 0));
		listePoints.add(new Point(12, 8, 0));
	
		BoundaryBox abox=new BoundaryBox();
		for(Point p:listePoints)
			abox.alterBox(p);
		aMesh.init(abox);
	
		aMesh.setPoints(listePoints);
		
		aMesh.processDelaunay();
		
		System.out.println("First check");
		aMesh.checkTriangularization();
		checkMeshTopo(aMesh);
		System.out.println("Next");
		
		aMesh.addLevelEdge(new Edge(new Point(3, 5, 5),new Point(10, 5, 5)));
		
//		show(aMesh);
//		System.out.println("end\n\nPush Enter to finish...");
//		System.in.read();	
		
		System.out.println("Second check");
		aMesh.checkTriangularization();
		checkMeshTopo(aMesh);
//		saveRefMesh(aMesh, "dataForTest/TestCheckMesh-test_03-meshRef.bin");
//		checkMesh(aMesh, "dataForTest/TestCheckMesh-test_03-meshRef.bin");

		
		aMesh.VRMLexport("dataForTest/TestCheckMesh-test_03.wrl");
	}
	
	
	/**
	 * Check when adding a level edge before processDelaunay.
	 * @throws DelaunayError
	 * @throws IOException
	 * @throws ParseException
	 * @throws ClassNotFoundException
	 */
	public void test_04() throws DelaunayError, IOException, ParseException, ClassNotFoundException {	
		System.out.println("\n\ntest_4\n");
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		ArrayList<Point> listePoints=new ArrayList<Point>();
		listePoints.add(new Point(0, 3, 0));
		listePoints.add(new Point(12, 3, 0));
		listePoints.add(new Point(0, 8, 0));
		listePoints.add(new Point(12, 8, 0));
	
		BoundaryBox abox=new BoundaryBox();
		for(Point p:listePoints)
			abox.alterBox(p);
		aMesh.init(abox);
		aMesh.setPoints(listePoints);
		
		aMesh.addLevelEdge(new Edge(new Point(3, 5, 5),new Point(10, 5, 5)));
		aMesh.processDelaunay();
		
//		show(aMesh);
		

		aMesh.checkTriangularization();
		checkMeshTopo(aMesh);
//		saveRefMesh(aMesh, "dataForTest/TestCheckMesh-test_04-meshRef.bin");
//		checkMesh(aMesh, "dataForTest/TestCheckMesh-test_04-meshRef.bin");

		
		aMesh.VRMLexport("dataForTest/TestCheckMesh-test_04.wrl");
	}
	
	
	/**
	 * A simple HELLO.
	 * @throws DelaunayError
	 * @throws IOException
	 * @throws ParseException
	 * @throws ClassNotFoundException
	 */
	public void test_05() throws DelaunayError, IOException, ParseException, ClassNotFoundException {		
		System.out.println("\n\ntest_5\n");
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		
		
		ArrayList<Point> listePoints=new ArrayList<Point>();
		listePoints.add(new Point(0, 0, 0));
		listePoints.add(new Point(0, 12, 0));
		listePoints.add(new Point(35, 0, 0));
		listePoints.add(new Point(35, 12, 0));
		
		listePoints.add(new Point(7, 0, 0));
		listePoints.add(new Point(13, 0, 0));
		listePoints.add(new Point(19, 0, 0));
		listePoints.add(new Point(25, 0, 0));
		
		listePoints.add(new Point(7, 12, 0));
		listePoints.add(new Point(13, 12, 0));
		listePoints.add(new Point(19, 12, 0));
		listePoints.add(new Point(25, 12, 0));
		
		BoundaryBox abox=new BoundaryBox();
		for(Point p:listePoints)
			abox.alterBox(p);
		aMesh.init(abox);
		aMesh.setPoints(listePoints);
//		BoundaryBox abox=new BoundaryBox(0, 35, 0, 12, 0, 5);
//		aMesh.init(abox);

		int height=2;
		
		// H
		aMesh.addLevelEdge(new Edge(new Point(2, 2, height),new Point(2, 6, height)));// 	|
		aMesh.addLevelEdge(new Edge(new Point(2, 6, height),new Point(2, 10, height)));//	|
		aMesh.addLevelEdge(new Edge(new Point(2, 6, height),new Point(6, 6, height)));//	-
		aMesh.addLevelEdge(new Edge(new Point(6, 2, height),new Point(6, 6, height)));//	|
		aMesh.addLevelEdge(new Edge(new Point(6, 6, height),new Point(6, 10, height)));//	|
		
		// E
		aMesh.addLevelEdge(new Edge(new Point(8, 2, height),new Point(8, 6, height)));// 	|
		aMesh.addLevelEdge(new Edge(new Point(8, 6, height),new Point(8, 10, height)));//	|
		aMesh.addLevelEdge(new Edge(new Point(8, 2, height),new Point(12, 2, height)));//	-
		aMesh.addLevelEdge(new Edge(new Point(8, 6, height),new Point(12, 6, height)));//	-
		aMesh.addLevelEdge(new Edge(new Point(8, 10, height),new Point(12, 10, height)));//	-
		
		// L
		aMesh.addLevelEdge(new Edge(new Point(14, 2, height),new Point(14, 10, height)));// 	|
		aMesh.addLevelEdge(new Edge(new Point(14, 2, height),new Point(18, 2, height)));//	-
		
		// L
		aMesh.addLevelEdge(new Edge(new Point(20, 2, height),new Point(20, 10, height)));// 	|
		aMesh.addLevelEdge(new Edge(new Point(20, 2, height),new Point(24, 2, height)));//	-

		// O
		WKTReader reader = new WKTReader();
		Polygon polygon = (Polygon) reader.read("POLYGON((28 2 "+height+",26 5 "+height+", 26 7 "+height+", 28 10 "+height+", 31 10 "+height+", 33 7 "+height+", 33 5 "+height+", 31 2 "+height+" ,28 2 "+height+"))");
		ConstraintPolygon aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(true);
		aMesh.addPolygon(aPolygon);
		
		aMesh.processDelaunay();
		aMesh.removeFlatTriangles();
//		show(aMesh);
		

		
		aMesh.checkTriangularization();
		checkMeshTopo(aMesh);
//		saveRefMesh(aMesh, "dataForTest/TestCheckMesh-test_05-meshRef.bin");
//		checkMesh(aMesh, "dataForTest/TestCheckMesh-test_05-meshRef.bin");

		
		aMesh.VRMLexport("dataForTest/TestCheckMesh-test_05.wrl");
		
//		System.out.println("end\n\nPush Enter to finish...");
//		System.in.read();
		
	}
	
	
	public void test_06() throws DelaunayError, IOException, ParseException, ClassNotFoundException {
		System.out.println("\n\ntest_6\n");
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		

		BoundaryBox abox=new BoundaryBox(-1, 9, -1, 8, 0, 5);
		aMesh.init(abox);
		aMesh.addLevelEdge(new Edge(new Point(0, 0, 2),new Point(0, 7, 2)));
		aMesh.addLevelEdge(new Edge(new Point(8, 0, 5),new Point(8, 7, 5)));
	
		
		WKTReader reader = new WKTReader();
		Polygon polygon = (Polygon) reader.read("POLYGON((2 2 0, 2 5 0, 6 5 0, 6 2 0, 2 2 0))");
		ConstraintPolygon aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(true);
		aPolygon.setUsePolygonZ(false);
		aMesh.processDelaunay();
		
		System.out.println("First check");
		aMesh.checkTriangularization();
		checkMeshTopo(aMesh);
		System.out.println("Next");
		
		
		aMesh.addPolygon(aPolygon);

//		show(aMesh);
		
		System.out.println("Second check");
		aMesh.checkTriangularization();
		checkMeshTopo(aMesh);
		
		aMesh.VRMLexport("dataForTest/TestCheckMesh-test_06.wrl");
//		System.out.println("end\n\nPush Enter to finish...");
//		System.in.read();
		
//		saveRefMesh(aMesh, "dataForTest/TestCheckMesh-test_06-meshRef.bin");
//		checkMesh(aMesh, "dataForTest/TestCheckMesh-test_06-meshRef.bin");

		
		aMesh.VRMLexport("dataForTest/TestCheckMesh-test_06.wrl");
	}
	
	
	public void test_07() throws DelaunayError, IOException, ParseException, ClassNotFoundException {	
		System.out.println("\n\ntest_7\n");
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		

		BoundaryBox abox=new BoundaryBox(0, 12, 0, 12, 0, 5);
		aMesh.init(abox);
		aMesh.addLevelEdge(new Edge(new Point(2, 10, 2),new Point(2, 2, 2)));
		aMesh.addLevelEdge(new Edge(new Point(2, 2, 2),new Point(10, 2, 2)));
		
		aMesh.addLevelEdge(new Edge(new Point(7, 7, 6),new Point(10, 7, 6)));
		aMesh.addLevelEdge(new Edge(new Point(10, 7, 6),new Point(10, 10, 6)));
		aMesh.addLevelEdge(new Edge(new Point(10, 10, 6),new Point(7, 10, 6)));
		aMesh.addLevelEdge(new Edge(new Point(7, 10, 6),new Point(7, 7, 6)));
		
		aMesh.processDelaunay();
		
		System.out.println("First check");
		aMesh.checkTriangularization();
		checkMeshTopo(aMesh);
		System.out.println("Next");
		
//		aMesh.showDebug=true;
		
		WKTReader reader = new WKTReader();
		Polygon polygon = (Polygon) reader.read("POLYGON((8 8 7, 9 8 7, 9 9 7, 8 9 7, 8 8 7))");
		ConstraintPolygon aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(true);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		polygon = (Polygon) reader.read("POLYGON((3 3 3, 7 3 3, 7 5 3, 5 5 3, 5 7 3, 3 7 3, 3 3 3))");
		aPolygon = new ConstraintPolygon(polygon, 600);
		aPolygon.setEmpty(true);
		aPolygon.setUsePolygonZ(false);
		aMesh.addPolygon(aPolygon);


		aMesh.removeFlatTriangles();
		
		
//		show(aMesh);
		
		System.out.println("Second check");
		aMesh.checkTriangularization();
		checkMeshTopo(aMesh);
		
//		saveRefMesh(aMesh, "dataForTest/TestCheckMesh-test_07-meshRef.bin");
//		checkMesh(aMesh, "dataForTest/TestCheckMesh-test_07-meshRef.bin");

		
		aMesh.VRMLexport("dataForTest/TestCheckMesh-test_07.wrl");
		
//		System.out.println("end\n\nPush Enter to finish...");
//		System.in.read();

	}
	
	
	
	public void test_08() throws DelaunayError, IOException, ParseException, ClassNotFoundException {	
		System.out.println("\n\ntest_8\n");
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		
//		aMesh.showDebug=true;
		BoundaryBox abox=new BoundaryBox(0, 28, 0, 8, 0, 5);
		aMesh.init(abox);
		aMesh.addLevelEdge(new Edge(new Point(1, 1, 2),new Point(1, 4, 2)));
		aMesh.addLevelEdge(new Edge(new Point(1, 4, 2),new Point(1, 7, 2)));
		
		aMesh.addLevelEdge(new Edge(new Point(27, 1, 7),new Point(27, 7, 7)));
		
		
		aMesh.addLevelEdge(new Edge(new Point(1, 4, 2),new Point(15, 4, 2)));
		
		aMesh.processDelaunay();
		
		System.out.println("First check");
		aMesh.checkTriangularization();
		checkMeshTopo(aMesh);
		System.out.println("Next");
		
		aMesh.showDebug=true;
		
		WKTReader reader = new WKTReader();
		Polygon polygon = (Polygon) reader.read("POLYGON((3 2 6, 3 6 6, 7 6 6, 7 2 6, 3 2 6))");
		ConstraintPolygon aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(true);
		aPolygon.setUsePolygonZ(false);
		aMesh.addPolygon(aPolygon);
		
//		polygon = (Polygon) reader.read("POLYGON((9 2 9, 9 6 9, 13 6 9, 13 2 9, 9 2 9))");
//		aPolygon = new ConstraintPolygon(polygon, 600);
//		aPolygon.setEmpty(true);
//		aPolygon.setUsePolygonZ(false);
//		aMesh.addPolygon(aPolygon);
	
		
//		show(aMesh);
		
//		System.out.println("end\n\nPush Enter to finish...");
//		System.in.read();
		
		
		System.out.println("Second check");
		aMesh.checkTriangularization();
		checkMeshTopo(aMesh);
//		saveRefMesh(aMesh, "dataForTest/TestCheckMesh-test_08-meshRef.bin");
//		checkMesh(aMesh, "dataForTest/TestCheckMesh-test_08-meshRef.bin");

		
		aMesh.VRMLexport("dataForTest/TestCheckMesh-test_08.wrl");
		
//		System.out.println("end\n\nPush Enter to finish...");
//		System.in.read();
	}
	
	
	
	public void test_09() throws DelaunayError, IOException, ParseException, ClassNotFoundException {
		System.out.println("\n\ntest_9\n");
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		

		BoundaryBox abox=new BoundaryBox(0, 10, 0, 8, 0, 5);
		aMesh.init(abox);
		aMesh.addLevelEdge(new Edge(new Point(1, 1, 2),new Point(1, 4, 2)));
		aMesh.addLevelEdge(new Edge(new Point(1, 4, 2),new Point(1, 7, 2)));
		
		aMesh.addLevelEdge(new Edge(new Point(1, 4, 2),new Point(9, 4, 2)));
		
		aMesh.addLevelEdge(new Edge(new Point(9, 1, 2),new Point(9, 4, 2)));
		aMesh.addLevelEdge(new Edge(new Point(9, 4, 2),new Point(9, 7, 2)));
		
		aMesh.processDelaunay();
		
		System.out.println("First check");
		aMesh.checkTriangularization();
		checkMeshTopo(aMesh);
		System.out.println("Next");
		
		
		WKTReader reader = new WKTReader();
		Polygon polygon = (Polygon) reader.read("POLYGON((3 2 6, 3 6 6, 7 6 6, 7 2 6, 3 2 6))");
		ConstraintPolygon aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(true);
		aPolygon.setUsePolygonZ(false);
		aMesh.addPolygon(aPolygon);
		
//		show(aMesh);
		
		System.out.println("Second check");
		aMesh.checkTriangularization();
		
		checkMeshTopo(aMesh);
		
//		saveRefMesh(aMesh, "dataForTest/TestCheckMesh-test_09-meshRef.bin");
//		checkMesh(aMesh, "dataForTest/TestCheckMesh-test_09-meshRef.bin");

		
		aMesh.VRMLexport("dataForTest/TestCheckMesh-test_09.wrl");
		
//		System.out.println("end\n\nPush Enter to finish...");
//		System.in.read();
	}
	
	
	
	public void test_10() throws DelaunayError, IOException, ParseException, ClassNotFoundException {	
		System.out.println("\n\ntest_10\n");
		
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		

		BoundaryBox abox=new BoundaryBox(0, 7, 0, 8, 0, 5);
		aMesh.init(abox);
		aMesh.addLevelEdge(new Edge(new Point(1, 1, 0.5),new Point(1, 7, 3.5)));
		aMesh.addLevelEdge(new Edge(new Point(1, 7, 3.5),new Point(6, 7, 6)));
		aMesh.addLevelEdge(new Edge(new Point(6, 7, 6),new Point(6, 3, 8)));
		aMesh.addLevelEdge(new Edge(new Point(6, 3, 8),new Point(3, 3, 9.5)));
		aMesh.addLevelEdge(new Edge(new Point(3, 3, 9.5),new Point(3, 5, 10.5)));
		aMesh.addLevelEdge(new Edge(new Point(3, 5, 10.5),new Point(4, 5, 11)));
		
		aMesh.processDelaunay();
		
		
//		show(aMesh);
		
		aMesh.checkTriangularization();
		
		checkMeshTopo(aMesh);
		
//		saveRefMesh(aMesh, "dataForTest/TestCheckMesh-test_10-meshRef.bin");
//		checkMesh(aMesh, "dataForTest/TestCheckMesh-test_10-meshRef.bin");

		
		aMesh.VRMLexport("dataForTest/TestCheckMesh-test_10.wrl");
		
//		System.out.println("end\n\nPush Enter to finish...");
//		System.in.read();
	}
	
	
	
	/**
	 * Add a star polygon after processDelaunay.
	 * @throws DelaunayError
	 * @throws IOException
	 * @throws ParseException
	 * @throws ClassNotFoundException
	 */
	public void test_11_aStar() throws DelaunayError, IOException, ParseException, ClassNotFoundException {	
		System.out.println("\n\ntest_11\n");
		
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		

		ArrayList<Point> listePoints=new ArrayList<Point>();
		listePoints.add(new Point(0, 0, 0));
		listePoints.add(new Point(10, 0, 0));
		listePoints.add(new Point(0, 10, 0));
		listePoints.add(new Point(10, 10, 0));
	
		BoundaryBox abox=new BoundaryBox();
		for(Point p:listePoints)
			abox.alterBox(p);
		aMesh.init(abox);
		aMesh.setPoints(listePoints);	
		

		
		aMesh.processDelaunay();
		
		System.out.println("First check");
		aMesh.checkTriangularization();
		checkMeshTopo(aMesh);
		System.out.println("Next");
		
		WKTReader reader = new WKTReader();
		Polygon polygon = (Polygon) reader.read("POLYGON((1 5 2, 4 6 2, 5 9 2, 6 6 2, 9 5 2, 6 4 2, 5 1 2, 4 4 2, 1 5 2))");// a star
		ConstraintPolygon aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
//		show(aMesh);
		
		System.out.println("Second check");
		aMesh.checkTriangularization();
		
		checkMeshTopo(aMesh);
		
//		saveRefMesh(aMesh, "dataForTest/TestCheckMesh-test_11-meshRef.bin");
//		checkMesh(aMesh, "dataForTest/TestCheckMesh-test_11-meshRef.bin");

		
		aMesh.VRMLexport("dataForTest/TestCheckMesh-test_11.wrl");
		
//		System.out.println("end\n\nPush Enter to finish...");
//		System.in.read();
	}
	
	
	/**
	 * Add a star polygon before processDelaunay.
	 * @throws DelaunayError
	 * @throws IOException
	 * @throws ParseException
	 * @throws ClassNotFoundException
	 */
	public void test_12_aStar() throws DelaunayError, IOException, ParseException, ClassNotFoundException {	
		System.out.println("\n\ntest_12\n");
		
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		

		ArrayList<Point> listePoints=new ArrayList<Point>();
		listePoints.add(new Point(0, 0, 0));
		listePoints.add(new Point(10, 0, 0));
		listePoints.add(new Point(0, 10, 0));
		listePoints.add(new Point(10, 10, 0));
	
		BoundaryBox abox=new BoundaryBox();
		for(Point p:listePoints)
			abox.alterBox(p);
		aMesh.init(abox);
		aMesh.setPoints(listePoints);	
		

		WKTReader reader = new WKTReader();
		Polygon polygon = (Polygon) reader.read("POLYGON((1 5 2, 4 6 2, 5 9 2, 6 6 2, 9 5 2, 6 4 2, 5 1 2, 4 4 2, 1 5 2))");// a star
		ConstraintPolygon aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		aMesh.showDebug=true;
		aMesh.processDelaunay();
		
//		show(aMesh);
		
		aMesh.checkTriangularization();
		
		checkMeshTopo(aMesh);
		
//		saveRefMesh(aMesh, "dataForTest/TestCheckMesh-test_12-meshRef.bin");
//		checkMesh(aMesh, "dataForTest/TestCheckMesh-test_12-meshRef.bin");

		
		aMesh.VRMLexport("dataForTest/TestCheckMesh-test_12.wrl");
		
//		System.out.println("end\n\nPush Enter to finish...");
//		System.in.read();
	}
	
	
	/**
	 * Add a empty star polygon after processDelaunay.
	 * @throws DelaunayError
	 * @throws IOException
	 * @throws ParseException
	 * @throws ClassNotFoundException
	 */
	public void test_13_aStar() throws DelaunayError, IOException, ParseException, ClassNotFoundException {	
		System.out.println("\n\ntest_13\n");
		
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		

		ArrayList<Point> listePoints=new ArrayList<Point>();
		listePoints.add(new Point(0, 0, 0));
		listePoints.add(new Point(10, 0, 0));
		listePoints.add(new Point(0, 10, 0));
		listePoints.add(new Point(10, 10, 0));
	
		BoundaryBox abox=new BoundaryBox();
		for(Point p:listePoints)
			abox.alterBox(p);
		aMesh.init(abox);
		aMesh.setPoints(listePoints);	

		
		aMesh.processDelaunay();
		
		System.out.println("First check");
		aMesh.checkTriangularization();
		checkMeshTopo(aMesh);
		System.out.println("Next");
		
		WKTReader reader = new WKTReader();
		Polygon polygon = (Polygon) reader.read("POLYGON((1 5 2, 4 6 2, 5 9 2, 6 6 2, 9 5 2, 6 4 2, 5 1 2, 4 4 2, 1 5 2))");// a star
		ConstraintPolygon aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(true);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		
//		show(aMesh);
		
		System.out.println("Second check");
		aMesh.checkTriangularization();
		
		checkMeshTopo(aMesh);
		
//		saveRefMesh(aMesh, "dataForTest/TestCheckMesh-test_13-meshRef.bin");
//		checkMesh(aMesh, "dataForTest/TestCheckMesh-test_13-meshRef.bin");

		
		aMesh.VRMLexport("dataForTest/TestCheckMesh-test_13.wrl");

	}
	
	
	/**
	 * Triangulate only a star polygon.
	 * @throws DelaunayError
	 * @throws IOException
	 * @throws ParseException
	 * @throws ClassNotFoundException
	 */
	public void test_14_onlyaStar() throws DelaunayError, IOException, ParseException, ClassNotFoundException {	
		System.out.println("\n\ntest_14\n");
		
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		
		
		ArrayList<Point> listePoints=new ArrayList<Point>();
		listePoints.add(new Point(1, 1, 0));
		listePoints.add(new Point(9, 1, 0));
		listePoints.add(new Point(1, 9, 0));
		listePoints.add(new Point(9, 9, 0));
	
		BoundaryBox abox=new BoundaryBox();
		for(Point p:listePoints)
			abox.alterBox(p);
		aMesh.init(abox);
		aMesh.setPoints(listePoints);	
		

		aMesh.processDelaunay();
//		
		WKTReader reader = new WKTReader();
		Polygon polygon = (Polygon) reader.read("POLYGON((1 5 2, 4 6 3, 5 9 2, 6 6 3, 9 5 2, 6 4 3, 5 1 2, 4 4 3, 1 5 2))");// a star
		ConstraintPolygon aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setUsePolygonZ(true);
		
		aPolygon.setEmpty(false);
//		aPolygon.setMustBeTriangulated(true);
				
//		aMesh.addPolygon(aPolygon);
		aMesh.addBridge(aPolygon);
		
//		aMesh.processDelaunay();
		
		
		polygon = (Polygon) reader.read("POLYGON((1 5 5, 4 6 6, 5 9 5, 6 6 6, 9 5 5, 6 4 6, 5 1 5, 4 4 6, 1 5 5))");// a star
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setUsePolygonZ(true);
		aPolygon.setEmpty(false);
		aMesh.addBridge(aPolygon);
		
//		show(aMesh);
		
		System.out.println(aMesh.getPoints());
		System.out.println(aMesh.getEdges());
		System.out.println(aMesh.getTriangles());
		
//		assertEquals("Number edges", 13, aMesh.getNbEdges());
		
		
//		aMesh.checkTriangularization();
		
//		checkMeshTopo(aMesh);
		
//		saveRefMesh(aMesh, "dataForTest/TestCheckMesh-test_14-meshRef.bin");
//		checkMesh(aMesh, "dataForTest/TestCheckMesh-test_14-meshRef.bin");

		
		aMesh.VRMLexport("dataForTest/TestCheckMesh-test_14.wrl");

		
//		System.out.println("end\n\nPush Enter to finish...");
//		System.in.read();
	}
	
	
	
	
	public void test_15() throws DelaunayError, IOException, ParseException, ClassNotFoundException {	
		System.out.println("\n\ntest_15\n");
		
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		
		
		ArrayList<Point> listePoints=new ArrayList<Point>();
		listePoints.add(new Point(0, 0, 0));
		listePoints.add(new Point(10, 0, 0));
		listePoints.add(new Point(0, 11, 0));
		listePoints.add(new Point(10, 11, 0));
	
		BoundaryBox abox=new BoundaryBox();
		for(Point p:listePoints)
			abox.alterBox(p);
		aMesh.init(abox);
		aMesh.setPoints(listePoints);	

		
		aMesh.processDelaunay();
		
		System.out.println("check 1");
		aMesh.checkTriangularization();
		checkMeshTopo(aMesh);
		System.out.println("Next");
		
		
		
		WKTReader reader = new WKTReader();
		Polygon polygon = (Polygon) reader.read("POLYGON((1 1 2, 1 5 2, 5 5 2, 5 1 2, 1 1 2))");
		ConstraintPolygon aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		
		System.out.println("check 2");
		aMesh.checkTriangularization();
		checkMeshTopo(aMesh);
		System.out.println("Next");
		
		polygon = (Polygon) reader.read("POLYGON((1 6 5, 1 10 5, 5 10 5, 5 6 5, 1 6 5))");
		aPolygon = new ConstraintPolygon(polygon, 200);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		
		System.out.println("check 3");
		aMesh.checkTriangularization();
		checkMeshTopo(aMesh);
		System.out.println("Next");
		
		polygon = (Polygon) reader.read("POLYGON((6 3 7, 6 8 7, 8 8 7, 8 3 7, 6 3 7))");
		aPolygon = new ConstraintPolygon(polygon, 200);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		
		System.out.println("check 4");
		aMesh.checkTriangularization();
		checkMeshTopo(aMesh);
		System.out.println("Next");
		
		polygon = (Polygon) reader.read("POLYGON((6 0 3, 6 2 3, 9 2 3, 9 0 3, 6 0 3))");
		aPolygon = new ConstraintPolygon(polygon, 200);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		System.out.println("check 5");
		aMesh.checkTriangularization();
		checkMeshTopo(aMesh);
		
//		saveRefMesh(aMesh, "dataForTest/TestCheckMesh-test_15-meshRef.bin");
//		checkMesh(aMesh, "dataForTest/TestCheckMesh-test_15-meshRef.bin");

		
		aMesh.VRMLexport("dataForTest/TestCheckMesh-test_15.wrl");
		
		
	}
	
	
	public void test_16() throws DelaunayError, IOException, ParseException, ClassNotFoundException {	
		System.out.println("\n\ntest_16\n");
		
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		
		
		BoundaryBox abox=new BoundaryBox(0, 10, 0, 11, 0, 5);
		aMesh.init(abox);
		
		WKTReader reader = new WKTReader();
		Polygon polygon = (Polygon) reader.read("POLYGON((1 1 2, 1 5 2, 5 5 2, 5 1 2, 1 1 2))");
		ConstraintPolygon aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		polygon = (Polygon) reader.read("POLYGON((1 6 5, 1 10 5, 5 10 5, 5 6 5, 1 6 5))");
		aPolygon = new ConstraintPolygon(polygon, 200);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		polygon = (Polygon) reader.read("POLYGON((6 3 7, 6 8 7, 8 8 7, 8 3 7, 6 3 7))");
		aPolygon = new ConstraintPolygon(polygon, 200);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		polygon = (Polygon) reader.read("POLYGON((6 0 3, 6 2 3, 9 2 3, 9 0 3, 6 0 3))");
		aPolygon = new ConstraintPolygon(polygon, 200);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		
		aMesh.processDelaunay();
		
//		show(aMesh);
		
		System.out.println("check 1");
		aMesh.checkTriangularization();
		checkMeshTopo(aMesh);
		
//		saveRefMesh(aMesh, "dataForTest/TestCheckMesh-test_16-meshRef.bin");
//		checkMesh(aMesh, "dataForTest/TestCheckMesh-test_16-meshRef.bin");


		aMesh.VRMLexport("dataForTest/TestCheckMesh-test_16.wrl");
		
	}
	
	
	public void test_17() throws DelaunayError, IOException, ParseException, ClassNotFoundException {	
		System.out.println("\n\ntest_17\n");
		
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		
		
		BoundaryBox abox=new BoundaryBox(0, 10, 0, 10, 0, 5);
		aMesh.init(abox);
		
		ArrayList<Point> listePoints=new ArrayList<Point>();
		listePoints.add(new Point(1,1, 0));
		listePoints.add(new Point(5, 9, 0));
		listePoints.add(new Point(5,5, 0));
		listePoints.add(new Point(9, 5, 0));
		listePoints.add(new Point(8, 8, 0));
		
		
		aMesh.setPoints(listePoints);
		
		
		aMesh.processDelaunay();
		
		
		WKTReader reader = new WKTReader();
		Polygon polygon = (Polygon) reader.read("POLYGON((2 2 2, 3 4 2, 4 3 2, 2 2 2))");
		ConstraintPolygon aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);

		
		
//		show(aMesh);
		
//		System.out.println("end\n\nPush Enter to finish...");
//		System.in.read();
		
		System.out.println("check 1");
		aMesh.checkTriangularization();
		checkMeshTopo(aMesh);
		
//		saveRefMesh(aMesh, "dataForTest/TestCheckMesh-test_17-meshRef.bin");
//		checkMesh(aMesh, "dataForTest/TestCheckMesh-test_17-meshRef.bin");


		aMesh.VRMLexport("dataForTest/TestCheckMesh-test_17.wrl");
		
//		System.out.println("end\n\nPush Enter to finish...");
//		System.in.read();
		
	}
	
	
	public void test_18() throws DelaunayError, IOException, ParseException, ClassNotFoundException {	
		System.out.println("\n\ntest_18\n");
		
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		
		
		BoundaryBox abox=new BoundaryBox(0, 10, 0, 8, 0, 5);
		aMesh.init(abox);
		
		ArrayList<Point> listePoints=new ArrayList<Point>();
		listePoints.add(new Point(1, 4, 0));
		listePoints.add(new Point(1, 7, 0));
		listePoints.add(new Point(3, 1, 0));
		listePoints.add(new Point(3, 4, 0));
		listePoints.add(new Point(9, 4, 0));
		
		
		aMesh.setPoints(listePoints);
		
		
		aMesh.processDelaunay();
		
		
		WKTReader reader = new WKTReader();
		Polygon polygon = (Polygon) reader.read("POLYGON((4 3 2, 4 5 2, 5 5 2,4 3 2 ))");
		ConstraintPolygon aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
//		show(aMesh);
		
		
		System.out.println("check");
		checkMeshTopo(aMesh);
		aMesh.checkTriangularization();
		
//		saveRefMesh(aMesh, "dataForTest/TestCheckMesh-test_18-meshRef.bin");
//		checkMesh(aMesh, "dataForTest/TestCheckMesh-test_18-meshRef.bin");


		aMesh.VRMLexport("dataForTest/TestCheckMesh-test_18.wrl");
		
//		System.out.println("end\n\nPush Enter to finish...");
//		System.in.read();
		
	}
	
	
	
	public void test_OrbisGIS() throws DelaunayError, IOException, ParseException, ClassNotFoundException {	
		System.out.println("\n\nOrbisGIS\n");
		
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		
		
		ArrayList<Point> listePoints=new ArrayList<Point>();
			listePoints.add(new Point(0, 13, 0));
			listePoints.add(new Point(0, -1, 0));
			listePoints.add(new Point(13, 13, 0));
			listePoints.add(new Point(13, -1, 0));
			listePoints.add(new Point(21, 13, 0));
			listePoints.add(new Point(21, -1, 0));
			listePoints.add(new Point(31, 13, 0));
			listePoints.add(new Point(31, -1, 0));
			listePoints.add(new Point(37, 13, 0));
			listePoints.add(new Point(37, -1, 0));
			listePoints.add(new Point(46, 13, 0));
			listePoints.add(new Point(46, -1, 0));
			listePoints.add(new Point(61, 13, 0));
			listePoints.add(new Point(57, -1, 0));
			listePoints.add(new Point(69, 13, 0));
			listePoints.add(new Point(68, -1, 0));
			listePoints.add(new Point(81, 13, 0));
			listePoints.add(new Point(81, -1, 0));
			
		aMesh.setPoints(listePoints);
		

		
		// O
		WKTReader reader = new WKTReader();
		Polygon polygon = (Polygon) reader.read("POLYGON((1 4 0, 1 8 0, 5 12 0, 8 12 0, 12 8 0, 12 4 0, 8 0 0, 5 0 0, 1 4 0))");// contour O
		ConstraintPolygon aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		polygon = (Polygon) reader.read("POLYGON((2 4 2, 2 8 2, 5 11 2, 8 11 2, 11 8 2, 11 4 2, 8 1 2, 5 1 2, 2 4 2))");// O
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		polygon = (Polygon) reader.read("POLYGON((4 4 2, 4 8 2, 5 9 2, 8 9 2, 9 8 2, 9 4 2, 8 3 2, 5 3 2, 4 4 2))");// inside O
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		polygon = (Polygon) reader.read("POLYGON((5 5 0, 5 7 0, 6 8 0, 7 8 0, 8 7 0, 8 5 0, 7 4 0, 6 4 0, 5 5 0))");// inside O
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		

		// r
		polygon = (Polygon) reader.read("POLYGON((14 0 0, 14 8 0, 17 8 0, 17.5 7 0, 18 8 0, 20 8 0, 20 4 0, 18 4 0, 18 0 0, 14 0 0))");// contour r
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		
		polygon = (Polygon) reader.read("POLYGON((15 1 2, 15 7 2, 17 7 2, 17 6 2, 18 7 2, 19 7 2, 19 5 2, 17 5 2, 17 1 2 ,15 1 2))");// r
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		// b
		polygon = (Polygon) reader.read("POLYGON((22 0 0, 22 11 0, 26 11 0, 26 8 0, 29 8 0, 30 7 0, 30 1 0, 29 0 0, 26 0 0, 25.5 1 0, 25 0 0, 22 0 0))");// contour b
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		polygon = (Polygon) reader.read("POLYGON((23 1 2, 23 10 2, 25 10 2, 25 6 2, 26 7 2, 28 7 2, 29 6 2, 29 2 2, 28 1 2, 26 1 2, 25 2 2, 25 1 2,23 1 2))");// b
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		polygon = (Polygon) reader.read("POLYGON((25 3 2, 25 5 2, 26 6 2, 27 6 2, 28 5 2, 28 3 2, 27 2 2, 26 2 2,25 3 2))");// inside b
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aMesh.addPolygon(aPolygon);
		
		polygon = (Polygon) reader.read("POLYGON((25.5 3.5 0, 25.5 4.5 0, 26 5 0, 27 5 0, 27.5 4.5 0, 27.5 3.5 0, 27 3 0, 26 3 0,25.5 3.5 0))");// inside b
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		// i
		polygon = (Polygon) reader.read("POLYGON((32 0 0, 32 7.5 0, 36 7.5 0, 36 0 0, 32 0 0))");// contour |
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);

		polygon = (Polygon) reader.read("POLYGON((33 1 2, 33 7 2, 35 7 2, 35 1 2, 33 1 2))");// |
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		polygon = (Polygon) reader.read("POLYGON((32 7.5 0, 32 11 0, 36 11 0, 36 7.5 0, 32 7.5 0))");// contour .
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		polygon = (Polygon) reader.read("POLYGON((33 8 2, 33 10 2, 35 10 2, 35 8 2, 33 8 2))");//  .
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		

		// s
		polygon = (Polygon) reader.read("POLYGON((38 1.5 0, 38 3 0, 39 3.5 0, 40 3 0, 41 3 0, 39 4 0, 38 5 0, 38 6 0, 40 8 0, 43 8 0, 45 7 0, 45 5 0, 44 4.50 0, 43 5 0, 42 5 0, 44 4 0, 45 3 0, 45 2 0, 43 0 0, 40 0 0, 38 1.5 0))");// contour s
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		polygon = (Polygon) reader.read("POLYGON((39 1.5 2, 39 3 2, 40 2.5 2, 42 2.5 2, 42 3 2, 40 4 2, 39 5 2, 39 6 2, 40 7 2, 43 7 2, 44 6.5 2, 44 5 2, 43 5.5 2, 41 5.5 2, 41 5 2, 43 4 2, 44 3 2, 44 2 2, 43 1 2, 40 1 2 ,39 1.5 2))");// s
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		// G
		polygon = (Polygon) reader.read("POLYGON((47 4 0, 47 7 0, 48.5 10 0, 52 12 0, 55 12 0, 58 11 0, 59 10 0, 58 7 0, 56 1 0, 55 0 0, 50 0 0, 48 2 0,47 4 0))");// contour G
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		polygon = (Polygon) reader.read("POLYGON((48 4 2, 48 7 2, 49 9 2, 52 11 2, 55 11 2, 57 10.5 2, 58 9.5 2, 57.5 8 2, 56.5 9 2, 54 9.5 2, 52 9 2, 51 8 2, 50 6 2, 50 4 2, 51 3 2, 53 3 2, 54 5 2, 52 5 2, 53 7 2, 57 7 2, 55 2 2, 54 1 2, 50 1 2, 49 2 2, 48 4 2))");// G
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		aMesh.addLevelEdge(new Edge(new Point(58, 7, 0),new Point(56, 8, 0)));// inside G
		aMesh.addLevelEdge(new Edge(new Point(56, 8, 0),new Point(53, 8, 0)));// inside G
		aMesh.addLevelEdge(new Edge(new Point(53, 8, 0),new Point(52, 7, 0)));// inside G
		aMesh.addLevelEdge(new Edge(new Point(52, 7, 0),new Point(51, 5, 0)));// inside G
		aMesh.addLevelEdge(new Edge(new Point(51, 5, 0),new Point(52, 4, 0)));// inside G
		aMesh.addLevelEdge(new Edge(new Point(52, 4, 0),new Point(53, 4, 0)));// inside G

		
		// I
		polygon = (Polygon) reader.read("POLYGON((59 0 0, 60 4 0, 61.5 4 0, 62 8 0, 60 8 0, 61 12 0, 68 12 0, 67 8 0, 66 8 0, 65.5 4 0, 67 4 0, 66 0 0, 59 0 0))");// contour I
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		polygon = (Polygon) reader.read("POLYGON((60 1 2, 60.5 3 2, 62 3 2, 63 9 2, 61.5 9 2, 62 11 2, 67 11 2, 66.5 9 2, 65 9 2, 64 3 2, 65.5 3 2, 65 1 2, 60 1 2 ))");// I
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		// S
		polygon = (Polygon) reader.read("POLYGON((67.5 1.5 0, 68.5 5 0, 71 4 0, 73 4 0, 71 5 0, 69.5 6 0, 68.5 8 0, 69 10 0, 70 11 0, 72 12 0, 77 12 0, 79 11 0, 80 10 0, 78.5 5.5 0, 77 7 0, 75 8 0, 73.5 8 0, 73.5 7.5 0, 77 5.5 0, 78 4 0, 78 2 0, 77 1 0, 75 0 0, 71 0 0, 67.5 1.5 0))");// contour S
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		polygon = (Polygon) reader.read("POLYGON((68.5 2 2, 69 4 2, 71 3 2, 74 3 2, 75 4 2, 71 6 2, 69.5 8 2, 70 9 2, 71 10 2, 73 11 2, 75 11 2, 77.5 10.5 2, 79 9.5 2, 78 7 2, 77 8.5 2, 75 9 2, 73 9 2, 72 8 2, 72 7.5 2, 77 4.5 2, 77.5 3 2, 77 2 2, 75 1 2, 71 1 2, 68.5 2 2))");// S
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		aMesh.showDebug=true;
		aMesh.processDelaunay();
	
//		show(aMesh);
//		System.out.println("end\n\nPush Enter to finish...");
//		System.in.read();
		
		System.out.println("check all");
		checkMeshTopo(aMesh);
		aMesh.checkTriangularization();
		
//		saveRefMesh(aMesh, "dataForTest/TestCheckMesh-test_OrbisGIS-meshRef.bin");
//		checkMesh(aMesh, "dataForTest/TestCheckMesh-test_OrbisGIS-meshRef.bin");


		aMesh.VRMLexport("dataForTest/TestCheckMesh-test_OrbisGIS.wrl");
		
//		System.out.println("end\n\nPush Enter to finish...");
//		System.in.read();
		
	}
	
	
	public void test_OrbisGIS2() throws DelaunayError, IOException, ParseException, ClassNotFoundException {	
		System.out.println("\n\nOrbisGIS2\n");
		
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		
		ArrayList<Point> listePoints=new ArrayList<Point>();
			listePoints.add(new Point(0, 13, 0));
			listePoints.add(new Point(0, -1, 0));
			listePoints.add(new Point(13, 13, 0));
			listePoints.add(new Point(13, -1, 0));
			listePoints.add(new Point(21, 13, 0));
			listePoints.add(new Point(21, -1, 0));
			listePoints.add(new Point(31, 13, 0));
			listePoints.add(new Point(31, -1, 0));
			listePoints.add(new Point(37, 13, 0));
			listePoints.add(new Point(37, -1, 0));
			listePoints.add(new Point(46, 13, 0));
			listePoints.add(new Point(46, -1, 0));
			listePoints.add(new Point(61, 13, 0));
			listePoints.add(new Point(57, -1, 0));
			listePoints.add(new Point(69, 13, 0));
			listePoints.add(new Point(68, -1, 0));
			listePoints.add(new Point(81, 13, 0));
			listePoints.add(new Point(81, -1, 0));
			
		aMesh.setPoints(listePoints);
		
		
		aMesh.processDelaunay();
		
		// O
		WKTReader reader = new WKTReader();
		Polygon polygon = (Polygon) reader.read("POLYGON((1 4 0, 1 8 0, 5 12 0, 8 12 0, 12 8 0, 12 4 0, 8 0 0, 5 0 0, 1 4 0))");// contour O
		ConstraintPolygon aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		polygon = (Polygon) reader.read("POLYGON((2 4 2, 2 8 2, 5 11 2, 8 11 2, 11 8 2, 11 4 2, 8 1 2, 5 1 2, 2 4 2))");// O
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		polygon = (Polygon) reader.read("POLYGON((4 4 2, 4 8 2, 5 9 2, 8 9 2, 9 8 2, 9 4 2, 8 3 2, 5 3 2, 4 4 2))");// inside O
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		polygon = (Polygon) reader.read("POLYGON((5 5 0, 5 7 0, 6 8 0, 7 8 0, 8 7 0, 8 5 0, 7 4 0, 6 4 0, 5 5 0))");// inside O
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
//		System.out.println("\ncheck the O");
//		checkMeshTopo(aMesh);
//		aMesh.checkTriangularization();
//		System.out.println("\n");
		
		
		// r
		System.out.println("\ncontour r\n");
		polygon = (Polygon) reader.read("POLYGON((14 0 0, 14 8 0, 17 8 0, 17.5 7 0, 18 8 0, 20 8 0, 20 4 0, 18 4 0, 18 0 0, 14 0 0))");// contour r
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		// debug mode
//		show(aMesh);
//		aMesh.VRMLexport("dataForTest/TestCheckMesh-test_OrbisGIS2.wrl");
//		System.out.println("end\n\nPush Enter to finish...");
//		System.in.read();
//		// end debug mode
		System.out.println("\nr\n");
		polygon = (Polygon) reader.read("POLYGON((15 1 2, 15 7 2, 17 7 2, 17 6 2, 18 7 2, 19 7 2, 19 5 2, 17 5 2, 17 1 2 ,15 1 2))");// r
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
//		System.out.println("\ncheck the r2");
//		checkMeshTopo(aMesh);
//		aMesh.checkTriangularization();
//		System.out.println("\n");

		// b
		System.out.println("\ncontour b\n");
		polygon = (Polygon) reader.read("POLYGON((22 0 0, 22 11 0, 26 11 0, 26 8 0, 29 8 0, 30 7 0, 30 1 0, 29 0 0, 26 0 0, 25.5 1 0, 25 0 0, 22 0 0))");// contour b
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		System.out.println("\ncheck the b1");
		checkMeshTopo(aMesh);
		aMesh.checkTriangularization();
		System.out.println("\n");
		
		aMesh.showDebug=true;
		polygon = (Polygon) reader.read("POLYGON((23 1 2, 23 10 2, 25 10 2, 25 6 2, 26 7 2, 28 7 2, 29 6 2, 29 2 2, 28 1 2, 26 1 2, 25 2 2, 25 1 2,23 1 2))");// b
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
//		aMesh.showDebug=false;
		
		
		polygon = (Polygon) reader.read("POLYGON((25 3 2, 25 5 2, 26 6 2, 27 6 2, 28 5 2, 28 3 2, 27 2 2, 26 2 2,25 3 2))");// inside b
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		polygon = (Polygon) reader.read("POLYGON((25.5 3.5 0, 25.5 4.5 0, 26 5 0, 27 5 0, 27.5 4.5 0, 27.5 3.5 0, 27 3 0, 26 3 0,25.5 3.5 0))");// inside b
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		System.out.println("\ncheck the b");
		checkMeshTopo(aMesh);
		aMesh.checkTriangularization();
		System.out.println("\n");
		
		// i
		polygon = (Polygon) reader.read("POLYGON((32 0 0, 32 7.5 0, 36 7.5 0, 36 0 0, 32 0 0))");// contour |
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);

		polygon = (Polygon) reader.read("POLYGON((33 1 2, 33 7 2, 35 7 2, 35 1 2, 33 1 2))");// |
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		polygon = (Polygon) reader.read("POLYGON((32 7.5 0, 32 11 0, 36 11 0, 36 7.5 0, 32 7.5 0))");// contour .
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		polygon = (Polygon) reader.read("POLYGON((33 8 2, 33 10 2, 35 10 2, 35 8 2, 33 8 2))");//  .
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		System.out.println("\ncheck the i");
		checkMeshTopo(aMesh);
		aMesh.checkTriangularization();
		System.out.println("\n");
		
		// s
		polygon = (Polygon) reader.read("POLYGON((38 1.5 0, 38 3 0, 39 3.5 0, 40 3 0, 41 3 0, 39 4 0, 38 5 0, 38 6 0, 40 8 0, 43 8 0, 45 7 0, 45 5 0, 44 4.50 0, 43 5 0, 42 5 0, 44 4 0, 45 3 0, 45 2 0, 43 0 0, 40 0 0, 38 1.5 0))");// contour s
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		polygon = (Polygon) reader.read("POLYGON((39 1.5 2, 39 3 2, 40 2.5 2, 42 2.5 2, 42 3 2, 40 4 2, 39 5 2, 39 6 2, 40 7 2, 43 7 2, 44 6.5 2, 44 5 2, 43 5.5 2, 41 5.5 2, 41 5 2, 43 4 2, 44 3 2, 44 2 2, 43 1 2, 40 1 2 ,39 1.5 2))");// s
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addPolygon(aPolygon);
		
		
//		System.out.println("\ncheck the s");
//		checkMeshTopo(aMesh);
//		aMesh.checkTriangularization();
//		System.out.println("\n");
		
		
//		// G
//		polygon = (Polygon) reader.read("POLYGON((47 4 0, 47 7 0, 48.5 10 0, 52 12 0, 55 12 0, 58 11 0, 59 10 0, 58 7 0, 56 1 0, 55 0 0, 50 0 0, 48 2 0,47 4 0))");// contour G
//		aPolygon = new ConstraintPolygon(polygon, 500);
//		aPolygon.setEmpty(false);
//		aPolygon.setUsePolygonZ(true);
//		aMesh.addPolygon(aPolygon);
//		
//		polygon = (Polygon) reader.read("POLYGON((48 4 2, 48 7 2, 49 9 2, 52 11 2, 55 11 2, 57 10.5 2, 58 9.5 2, 57.5 8 2, 56.5 9 2, 54 9.5 2, 52 9 2, 51 8 2, 50 6 2, 50 4 2, 51 3 2, 53 3 2, 54 5 2, 52 5 2, 53 7 2, 57 7 2, 55 2 2, 54 1 2, 50 1 2, 49 2 2, 48 4 2))");// G
//		aPolygon = new ConstraintPolygon(polygon, 500);
//		aPolygon.setEmpty(false);
//		aPolygon.setUsePolygonZ(true);
//		aMesh.addPolygon(aPolygon);
//		
//		aMesh.addLevelEdge(new Edge(new Point(58, 7, 0),new Point(56, 8, 0)));// inside G
//		aMesh.addLevelEdge(new Edge(new Point(56, 8, 0),new Point(53, 8, 0)));// inside G
//		aMesh.addLevelEdge(new Edge(new Point(53, 8, 0),new Point(52, 7, 0)));// inside G
//		aMesh.addLevelEdge(new Edge(new Point(52, 7, 0),new Point(51, 5, 0)));// inside G
//		aMesh.addLevelEdge(new Edge(new Point(51, 5, 0),new Point(52, 4, 0)));// inside G
//		aMesh.addLevelEdge(new Edge(new Point(52, 4, 0),new Point(53, 4, 0)));// inside G
//		
//		
//		System.out.println("\ncheck the G");
//		checkMeshTopo(aMesh);
//		aMesh.checkTriangularization();
//		System.out.println("\n");
//		
//		// I
//		polygon = (Polygon) reader.read("POLYGON((59 0 0, 60 4 0, 61.5 4 0, 62 8 0, 60 8 0, 61 12 0, 68 12 0, 67 8 0, 66 8 0, 65.5 4 0, 67 4 0, 66 0 0, 59 0 0))");// contour I
//		aPolygon = new ConstraintPolygon(polygon, 500);
//		aPolygon.setEmpty(false);
//		aPolygon.setUsePolygonZ(true);
//		aMesh.addPolygon(aPolygon);
//		
//		polygon = (Polygon) reader.read("POLYGON((60 1 2, 60.5 3 2, 62 3 2, 63 9 2, 61.5 9 2, 62 11 2, 67 11 2, 66.5 9 2, 65 9 2, 64 3 2, 65.5 3 2, 65 1 2, 60 1 2 ))");// I
//		aPolygon = new ConstraintPolygon(polygon, 500);
//		aPolygon.setEmpty(false);
//		aPolygon.setUsePolygonZ(true);
//		aMesh.addPolygon(aPolygon);
//		
//		System.out.println("\ncheck the I");
//		checkMeshTopo(aMesh);
//		aMesh.checkTriangularization();
//		System.out.println("\n");
//		
//		// S
//		polygon = (Polygon) reader.read("POLYGON((67.5 1.5 0, 68.5 5 0, 71 4 0, 73 4 0, 71 5 0, 69.5 6 0, 68.5 8 0, 69 10 0, 70 11 0, 72 12 0, 77 12 0, 79 11 0, 80 10 0, 78.5 5.5 0, 77 7 0, 75 8 0, 73.5 8 0, 73.5 7.5 0, 77 5.5 0, 78 4 0, 78 2 0, 77 1 0, 75 0 0, 71 0 0, 67.5 1.5 0))");// contour S
//		aPolygon = new ConstraintPolygon(polygon, 500);
//		aPolygon.setEmpty(false);
//		aPolygon.setUsePolygonZ(true);
//		aMesh.addPolygon(aPolygon);
//		
//		polygon = (Polygon) reader.read("POLYGON((68.5 2 2, 69 4 2, 71 3 2, 74 3 2, 75 4 2, 71 6 2, 69.5 8 2, 70 9 2, 71 10 2, 73 11 2, 75 11 2, 77.5 10.5 2, 79 9.5 2, 78 7 2, 77 8.5 2, 75 9 2, 73 9 2, 72 8 2, 72 7.5 2, 77 4.5 2, 77.5 3 2, 77 2 2, 75 1 2, 71 1 2, 68.5 2 2))");// S
//		aPolygon = new ConstraintPolygon(polygon, 500);
//		aPolygon.setEmpty(false);
//		aPolygon.setUsePolygonZ(true);
//		aMesh.addPolygon(aPolygon);
	
//		show(aMesh);
		
//		System.out.println("check all");
//		checkMeshTopo(aMesh);
//		aMesh.checkTriangularization();
		
//		saveRefMesh(aMesh, "dataForTest/TestCheckMesh-test_OrbisGIS2-meshRef.bin");
//		checkMesh(aMesh, "dataForTest/TestCheckMesh-test_OrbisGIS2-meshRef.bin");


		aMesh.VRMLexport("dataForTest/TestCheckMesh-test_OrbisGIS2.wrl");
		
//		System.out.println("end\n\nPush Enter to finish...");
//		System.in.read();
		
	}
	
	
	public void test_OrbisGIS3() throws DelaunayError, IOException, ParseException, ClassNotFoundException {	
		System.out.println("\n\nOrbisGIS3\n");
		
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		
		ArrayList<Point> listePoints=new ArrayList<Point>();
			listePoints.add(new Point(0, 13, 0));
			listePoints.add(new Point(0, -1, 0));
			listePoints.add(new Point(13, 13, 0));
			listePoints.add(new Point(13, -1, 0));
			listePoints.add(new Point(21, 13, 0));
			listePoints.add(new Point(21, -1, 0));
			listePoints.add(new Point(31, 13, 0));
			listePoints.add(new Point(31, -1, 0));
			listePoints.add(new Point(37, 13, 0));
			listePoints.add(new Point(37, -1, 0));
			listePoints.add(new Point(46, 13, 0));
			listePoints.add(new Point(46, -1, 0));
			listePoints.add(new Point(61, 13, 0));
			listePoints.add(new Point(57, -1, 0));
			listePoints.add(new Point(69, 13, 0));
			listePoints.add(new Point(68, -1, 0));
			listePoints.add(new Point(81, 13, 0));
			listePoints.add(new Point(81, -1, 0));
			
		aMesh.setPoints(listePoints);
		
		
		aMesh.processDelaunay();
		WKTReader reader = new WKTReader();
		Polygon polygon;
		ConstraintPolygon aPolygon;
		
		// O
		polygon = (Polygon) reader.read("POLYGON((2 4 10, 2 8 10, 5 11 10, 8 11 10, 11 8 10, 11 4 10, 8 1 10, 5 1 10, 2 4 10))");// O
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addBridge(aPolygon);
		
		// r
		polygon = (Polygon) reader.read("POLYGON((15 1 10, 15 7 10, 17 7 10, 17 6 10, 18 7 10, 19 7 10, 19 5 10, 17 5 10, 17 1 10 ,15 1 10))");// r
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addBridge(aPolygon);
		

		// b
		polygon = (Polygon) reader.read("POLYGON((23 1 10, 23 10 10, 25 10 10, 25 6 10, 26 7 10, 28 7 10, 29 6 10, 29 2 10, 28 1 10, 26 1 10, 25 2 10, 25 1 10,23 1 10))");// b
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addBridge(aPolygon);
		
		// i
		polygon = (Polygon) reader.read("POLYGON((33 1 10, 33 7 10, 35 7 10, 35 1 10, 33 1 10))");// |
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addBridge(aPolygon);
		
		polygon = (Polygon) reader.read("POLYGON((33 8 10, 33 10 10, 35 10 10, 35 8 10, 33 8 10))");//  .
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addBridge(aPolygon);
		
	
		// s		
		polygon = (Polygon) reader.read("POLYGON((39 1.5 10, 39 3 10, 40 2.5 10, 42 2.5 10, 42 3 10, 40 4 10, 39 5 10, 39 6 10, 40 7 10, 43 7 10, 44 6.5 10, 44 5 10, 43 5.5 10, 41 5.5 10, 41 5 10, 43 4 10, 44 3 10, 44 2 10, 43 1 10, 40 1 10, 39 1.5 10))");// s
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addBridge(aPolygon);
		
		
		// G
		
		polygon = (Polygon) reader.read("POLYGON((48 4 10, 48 7 10, 49 9 10, 52 11 10, 55 11 10, 57 10.5 10, 58 9.5 10, 57.5 8 10, 56.5 9 10, 54 9.5 10, 52 9 10, 51 8 10, 50 6 10, 50 4 10, 51 3 10, 53 3 10, 54 5 10, 52 5 10, 53 7 10, 57 7 10, 55 2 10, 54 1 10, 50 1 10, 49 2 10, 48 4 10))");// G
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addBridge(aPolygon);
		
		
		
		// I		
		polygon = (Polygon) reader.read("POLYGON((60 1 10, 60.5 3 10, 62 3 10, 63 9 10, 61.5 9 10, 62 11 10, 67 11 10, 66.5 9 10, 65 9 10, 64 3 10, 65.5 3 10, 65 1 10, 60 1 10 ))");// I
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addBridge(aPolygon);
		
		// S
		polygon = (Polygon) reader.read("POLYGON((68.5 2 10, 69 4 10, 71 3 10, 74 3 10, 75 4 10, 71 6 10, 69.5 8 10, 70 9 10, 71 10 10, 73 11 10, 75 11 10, 77.5 10.5 10, 79 9.5 10, 78 7 10, 77 8.5 10, 75 9 10, 73 9 10, 72 8 10, 72 7.5 10, 77 4.5 10, 77.5 3 10, 77 2 10, 75 1 10, 71 1 10, 68.5 2 10))");// S
		aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addBridge(aPolygon);
	
//		show(aMesh);
		
//		System.out.println("check all");
//		checkMeshTopo(aMesh);
//		aMesh.checkTriangularization();
		
//		saveRefMesh(aMesh, "dataForTest/TestCheckMesh-test_OrbisGIS3-meshRef.bin");
//		checkMesh(aMesh, "dataForTest/TestCheckMesh-test_OrbisGIS3-meshRef.bin");


		aMesh.VRMLexport("dataForTest/TestCheckMesh-test_OrbisGIS3.wrl");
		
//		System.out.println("end\n\nPush Enter to finish...");
//		System.in.read();
		
	}
	
	public void test_bridge() throws DelaunayError, IOException, ParseException, ClassNotFoundException {	
		System.out.println("\n\ntest_bridge\n");
		
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		
		ArrayList<Point> listePoints=new ArrayList<Point>();
		listePoints.add(new Point(0, 0, 0));
		listePoints.add(new Point(13, 0, 0));
		listePoints.add(new Point(0, 8, 0));
		listePoints.add(new Point(13, 8, 0));
	
		BoundaryBox abox=new BoundaryBox();
		for(Point p:listePoints)
			abox.alterBox(p);
		aMesh.init(abox);
		aMesh.setPoints(listePoints);		
		
		aMesh.processDelaunay();
		
		
		WKTReader reader = new WKTReader();
//		Polygon polygon = (Polygon) reader.read("POLYGON((2 2 0, 2 6 0, 5 6 2, 8 6 0, 8 2 0, 5 2 2, 2 2 0))");
		Polygon polygon = (Polygon) reader.read("POLYGON((2 2 0, 2 6 0, 5 8 2, 8 8 2, 11 6 0, 11 2 0, 8 4 2, 5 4 2, 2 2 0))");

		ConstraintPolygon aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addBridge(aPolygon);
		
//		show(aMesh);
		
		
//		System.out.println("check");
//		checkMeshTopo(aMesh);
//		aMesh.checkTriangularization();
		
//		saveRefMesh(aMesh, "dataForTest/TestCheckMesh-test_bridge-meshRef.bin");
//		checkMesh(aMesh, "dataForTest/TestCheckMesh-test_bridge-meshRef.bin");


		aMesh.VRMLexport("dataForTest/TestCheckMesh-test_bridge.wrl");
		
//		System.out.println("end\n\nPush Enter to finish...");
//		System.in.read();
		
	}
	
	
	public void test_bridge2() throws DelaunayError, IOException, ParseException, ClassNotFoundException {	
		System.out.println("\n\ntest_bridge2\n");
		
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		
		
		ArrayList<Point> listePoints=new ArrayList<Point>();
		listePoints.add(new Point(0, 0, 0));
		listePoints.add(new Point(8, 0, 0));
		listePoints.add(new Point(0, 10, 0));
		listePoints.add(new Point(8, 10, 0));
	
		BoundaryBox abox=new BoundaryBox();
		for(Point p:listePoints)
			abox.alterBox(p);
		aMesh.init(abox);
		aMesh.setPoints(listePoints);	
		
		
		aMesh.processDelaunay();
		
		
		WKTReader reader = new WKTReader();
		Polygon polygon = (Polygon) reader.read("POLYGON((6 1 0, 4 1 1, 2 1 1, 2 3 1, 2 4 1.5, 2 6 1.5, 2 7 1, 2 9 1, 4 9 1, 6 9 0, 6 7 0, 4 7 1, 4 6 1.5, 4 4 1.5, 4 3 1, 6 3 0 ,6 1 0))");
//		Polygon polygon = (Polygon) reader.read("POLYGON((4 1 1, 2 1 1, 2 3 1, 2 4 1.5, 2 6 1.5, 2 7 1, 2 9 1, 4 9 1, 4 7 1, 4 6 1.5, 4 4 1.5, 4 3 1, 4 1 1))");
		ConstraintPolygon aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addBridge(aPolygon);
		
//		show(aMesh);
		
		
//		System.out.println("check");
//		checkMeshTopo(aMesh);
//		aMesh.checkTriangularization();
		
//		saveRefMesh(aMesh, "dataForTest/TestCheckMesh-test_bridge2-meshRef.bin");
//		checkMesh(aMesh, "dataForTest/TestCheckMesh-test_bridge2-meshRef.bin");


		aMesh.VRMLexport("dataForTest/TestCheckMesh-test_bridge2.wrl");
		
//		System.out.println("end\n\nPush Enter to finish...");
//		System.in.read();
		
	}
	
	public void test_bridge2_2() throws DelaunayError, IOException, ParseException, ClassNotFoundException {	
		System.out.println("\n\ntest_bridge2_2\n");
		
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
				
		
		ArrayList<Point> listePoints=new ArrayList<Point>();
		listePoints.add(new Point(0, 0, 0));
		listePoints.add(new Point(8, 0, 0));
		listePoints.add(new Point(0, 10, 0));
		listePoints.add(new Point(8, 10, 0));
	
		BoundaryBox abox=new BoundaryBox();
		for(Point p:listePoints)
			abox.alterBox(p);
		aMesh.init(abox);
		aMesh.setPoints(listePoints);	
		
		
		aMesh.processDelaunay();
		
//		aMesh.showDebug=true;
		WKTReader reader = new WKTReader();
		Polygon polygon = (Polygon) reader.read("POLYGON((2 1 0, 4 1 1, 6 1 1, 6 3 1, 6 4 1.5, 6 6 1.5, 6 7 1, 6 9 1, 4 9 1, 2 9 0, 2 7 0, 4 7 1, 4 6 1.5, 4 4 1.5, 4 3 1, 2 3 0 ,2 1 0))");
		ConstraintPolygon aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addBridge(aPolygon);
		
//		show(aMesh);
		
		
//		System.out.println("check");
//		checkMeshTopo(aMesh);
//		aMesh.checkTriangularization();
		
//		saveRefMesh(aMesh, "dataForTest/TestCheckMesh-test_bridge2_2-meshRef.bin");
//		checkMesh(aMesh, "dataForTest/TestCheckMesh-test_bridge2_2-meshRef.bin");


		aMesh.VRMLexport("dataForTest/TestCheckMesh-test_bridge2_2.wrl");
		
//		System.out.println("end\n\nPush Enter to finish...");
//		System.in.read();
		
	}
	
	
	public void test_bridge3_G() throws DelaunayError, IOException, ParseException, ClassNotFoundException {	
		System.out.println("\n\ntest_bridge3_G\n");
		
		MyMesh aMesh = new MyMesh();
		aMesh.setPrecision(1.0e-3);
		aMesh.setVerbose(true);
		
		ArrayList<Point> listePoints=new ArrayList<Point>();
		listePoints.add(new Point(45, 0, 0));
		listePoints.add(new Point(46, 0, 0));
		listePoints.add(new Point(45, 1, 0));
		listePoints.add(new Point(46, 1, 0));
	
		BoundaryBox abox=new BoundaryBox();
		for(Point p:listePoints) {
			abox.alterBox(p);
		}
		aMesh.init(abox);
		aMesh.setPoints(listePoints);	
		

		aMesh.processDelaunay();
		
		
		WKTReader reader = new WKTReader();
		Polygon polygon = (Polygon) reader.read("POLYGON((48 4 2, 48 7 2, 49 9 2, 52 11 2, 55 11 2, 57 10.5 2, 58 9.5 2, 57.5 8 2, 56.5 9 2, 54 9.5 2, 52 9 2, 51 8 2, 50 6 2, 50 4 2, 51 3 2, 53 3 2, 54 5 2, 52 5 2, 53 7 2, 57 7 2, 55 2 2, 54 1 2, 50 1 2, 49 2 2, 48 4 2))");// G
		ConstraintPolygon aPolygon = new ConstraintPolygon(polygon, 500);
		aPolygon.setEmpty(false);
		aPolygon.setUsePolygonZ(true);
		aMesh.addBridge(aPolygon);
		
//		show(aMesh);
		
		
		System.out.println("check");
		assertTrue("Duplicate edges", checkNoDuplicateEdge(aMesh.getEdges()));
		assertTrue("Triangle of edge not found", checkEdgeTriangleExiste(aMesh));
		aMesh.checkTriangularization();
		
//		saveRefMesh(aMesh, "dataForTest/TestCheckMesh-test_bridge3_G-meshRef.bin");
//		checkMesh(aMesh, "dataForTest/TestCheckMesh-test_bridge3_G-meshRef.bin");


		aMesh.VRMLexport("dataForTest/TestCheckMesh-test_bridge3_G.wrl");
		
//		System.out.println("end\n\nPush Enter to finish...");
//		System.in.read();
		
	}

		
	public void test_pause() throws IOException
	{
		System.out.println("end\n\nPush Enter to finish...");
//		System.in.read();
	}
	
	
	
	//__________________________________________________________________________________________________
	//
	// 		Check and save mesh.
	//
	
	
	
	
	
	/**
	 * Check if the topology of the mesh is good.
	 * @param aMesh
	 */
	public static void checkMeshTopo(MyMesh aMesh) throws DelaunayError{
		assertTrue("Duplicate edges", checkNoDuplicateEdge(aMesh.getEdges()));
		assertTrue("Triangle of edge not found", checkEdgeTriangleExiste(aMesh));
		assertTrue("Edge intersection", checkEdgeNoIntersection(aMesh.getEdges()));
	}
	
	
	
	/**
	 * Check if we haven't got duplicate edges.
	 * @param edgeList
	 * @return true if the mesh is good.
	 */
	public static boolean checkNoDuplicateEdge(ArrayList<Edge> edgeList)
	{
		boolean ok=true;
		for(int i=0; i<edgeList.size();i++) {
			for (int j = i + 1; j < edgeList.size(); j++) {
				if ((edgeList.get(i).getStartPoint().equals(edgeList.get(j).getStartPoint()) &&
					edgeList.get(i).getEndPoint().equals(edgeList.get(j).getEndPoint())) ||
					(edgeList.get(i).getStartPoint().equals(edgeList.get(j).getEndPoint()) &&
					edgeList.get(i).getEndPoint().equals(edgeList.get(j).getStartPoint()))) {
					System.err.println("Duplicate edge : " + edgeList.get(i) + " " +
						edgeList.get(i).getIndicator() + " == " +
						edgeList.get(j) + " " + edgeList.get(j).getIndicator());
					ok = false;
				}
			}
		}
		return ok;
	}
	
	
	public static boolean checkEdgeTriangleExiste(MyMesh aMesh)
	{
		boolean ok=true;
		ArrayList<DelaunayTriangle> triangles = aMesh.getTriangles();
		for(Edge anEdge : aMesh.getEdges())
		{
				boolean findleft= (anEdge.getLeft()!=null?(anEdge.getLeft().getGID()==-1):true);
				boolean findright= (anEdge.getRight()!=null?(anEdge.getRight().getGID()==-1):true);
				
				for(int i=0; i<triangles.size() && !(findleft && findright); i++)
				{
					if(anEdge.getLeft()!=null && triangles.get(i).getGID()==anEdge.getLeft().getGID()) {
					findleft = true;
				}
					if(anEdge.getRight()!=null && triangles.get(i).getGID()==anEdge.getRight().getGID()) {
					findright = true;
				}
				}
			
				if(!findleft)
				{
					System.err.println("Not found triangle with GID "+anEdge.getLeft().getGID()+". Left triangle of edge "+anEdge);
					ok=false;
				}
				
				if(!findright)
				{
					System.err.println("Not found triangle with GID "+anEdge.getRight().getGID()+". Right triangle of edge "+anEdge);
					ok=false;
				}
		}
		
		return ok;
	}

	
	public static boolean checkEdgeNoIntersection(ArrayList<Edge> edgeList) throws DelaunayError{
		boolean ok=true;
		for(int i=0; i<edgeList.size();i++) {
			for (int j = i + 1; j < edgeList.size(); j++) {
				if (edgeList.get(i).intersects(edgeList.get(j).getStartPoint(), edgeList.get(j).getEndPoint()) == 1) {
					System.err.println("Intersection between " + edgeList.get(i) + " and " + edgeList.get(j));
					ok = false;
				}
			}
		}

		return ok;
	}
	
	
	/**
	 * Check edge of mesh.
	 * @param aMesh
	 * @param pathFileRef
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws DelaunayError 
	 */
	public static void checkMesh(MyMesh aMesh, String pathFileRef) throws IOException, ClassNotFoundException, DelaunayError
	{
		ArrayList<Point> pointListToCheck= aMesh.getPoints();
		ArrayList<Edge> edgeListToCheck= aMesh.getEdges();
		ArrayList<DelaunayTriangle> triangleListToCheck= aMesh.getTriangles();
		
		System.out.println("\nChecking mesh...\n");
		FileInputStream fis = new FileInputStream(pathFileRef);
		ObjectInputStream ois = new ObjectInputStream(fis);
		ArrayList<Point> pointListRef = (ArrayList<Point>) ois.readObject();
		ArrayList<Edge> edgeListRef = (ArrayList<Edge>) ois.readObject();
		ArrayList<DelaunayTriangle> tiangleListRef = (ArrayList<DelaunayTriangle>) ois.readObject();
		ois.close();
		
		
//		assertEquals("Same number of point", pointListRef.size(), pointListToCheck.size());
//		assertEquals("Same number of edge", edgeListRef.size(), edgeListToCheck.size());
//		assertEquals("Same number of triangle", tiangleListRef.size(), triangleListToCheck.size());
		
		if(pointListRef.size()!= pointListToCheck.size()) {
			System.err.println("Not same number of points! Reference list : " + pointListRef.size() + " " + (pointListRef.size() > pointListToCheck.size() ? ">" : "<") + " new list : " + pointListToCheck.size());
		}
			
		boolean find, meshIsGood=true;
		int i;
		
		//
		// 		checking points
		//
		for(Point aPoint: pointListRef)
		{
			find=false;
			for(i=0;i<pointListToCheck.size()&&!find;i++)
			{
				find=	aPoint.getCoordinate().equals(pointListToCheck.get(i).getCoordinate())
					&&	aPoint.isUseByPolygon() == pointListToCheck.get(i).isUseByPolygon()
					&&	aPoint.isUseByLevelEdge() == pointListToCheck.get(i).isUseByLevelEdge();
			}
			if(!find)
			{
				meshIsGood=false;
				System.err.println("Point not found! : "+aPoint+" "+aPoint.getIndicator()+" | "+(aPoint.isUseByLevelEdge()?"lvl":"")+"  "+(aPoint.isUseByPolygon()?"poly":""));
			}
			else
			{
				pointListToCheck.remove(i-1);
			}
		}
		
		if(!pointListToCheck.isEmpty())
		{
			meshIsGood=false;
			for(Point aPoint:pointListToCheck) {
				System.err.println("Not fond in reference! : " + aPoint + " " + aPoint.getIndicator() + " | " + (aPoint.isUseByLevelEdge() ? "lvl" : "") + "  " + (aPoint.isUseByPolygon() ? "poly" : ""));
			}
		}
		
		assertTrue("Mesh isn't good! Some points are wrong.", meshIsGood);
		
		
		

		//
		// 		checking edges
		//
		
		if(edgeListRef.size() != edgeListToCheck.size()) {
			System.err.println("Not same number of edges! Reference list : " + edgeListRef.size() + " " + (edgeListRef.size() > edgeListToCheck.size() ? ">" : "<") + " new list : " + edgeListToCheck.size());
		}

		
		for(Edge anEdge: edgeListRef)
		{
			find=false;
			for(i=0;i<edgeListToCheck.size()&&!find;i++)
			{
				find=
					(		/* check points' coordinates */ 
							( anEdge.getStartPoint().getCoordinate().equals(edgeListToCheck.get(i).getStartPoint().getCoordinate()) &&
									anEdge.getEndPoint().getCoordinate().equals(edgeListToCheck.get(i).getEndPoint().getCoordinate()) )
							||( anEdge.getStartPoint().getCoordinate().equals(edgeListToCheck.get(i).getEndPoint().getCoordinate()) &&
									anEdge.getEndPoint().getCoordinate().equals(edgeListToCheck.get(i).getStartPoint().getCoordinate()) )
					)
					&&
						anEdge.isUseByPolygon() == edgeListToCheck.get(i).isUseByPolygon()
					&&
						anEdge.isLevelEdge() == edgeListToCheck.get(i).isLevelEdge();

//				System.out.println(anEdge+" "+(find?"=":"!")+"= "+edgeList.get(i));
			}
			if(!find)
			{
				meshIsGood=false;
				System.err.println("Not found! : "+anEdge+" "+anEdge.getIndicator()+" | "+(anEdge.isLevelEdge()?"lvl":"")+"  "+(anEdge.isUseByPolygon()?"poly":""));
			}
			else
			{
				edgeListToCheck.remove(i-1);
			}
		}
		
		if(!edgeListToCheck.isEmpty())
		{
			meshIsGood=false;
			for(Edge anEdge:edgeListToCheck) {
				System.err.println("Not fond in reference! : " + anEdge + " " + anEdge.getIndicator() + " | " + (anEdge.isLevelEdge() ? "lvl" : "") + "  " + (anEdge.isUseByPolygon() ? "poly" : ""));
			}
		}
		
		assertTrue("Mesh isn't good! Some edges are wrong.", meshIsGood);

		
		


		//
		// 		checking triangles
		//
		assertEquals("Same number of triangle", tiangleListRef.size(), triangleListToCheck.size());

		
		
		
		
		//
		// 		checking triangulation
		//
		aMesh.checkTriangularization();
		
		
		System.out.println("Checking mesh done.\n");
	}
	
	/**
	 * Save a mesh for use it as an reference.
	 * @param edgeList
	 * @param pathFileRef
	 * @throws IOException
	 */
	public static void saveRefMesh(MyMesh aMesh, String pathFileRef) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(pathFileRef);
		ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(fos));
		oos.writeObject(aMesh.getPoints());
		oos.writeObject(aMesh.getEdges());
		oos.writeObject(aMesh.getTriangles());
		oos.close();
	}
	
	
	
	
}
