package org.gdms.test;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.jdelaunay.delaunay.Delaunay;
import org.jdelaunay.delaunay.DelaunayError;
import org.jdelaunay.delaunay.MyEdge;
import org.jdelaunay.delaunay.MyMesh;
import org.jdelaunay.delaunay.MyPoint;
import org.jdelaunay.delaunay.MyTriangle;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class TestDelaunay {

	public static DataSourceFactory dsf = new DataSourceFactory();

	 //public static String path = "data/courbesZ.shp";

	//public static String path = "data/courbesdem10_zone.shp";

	// public static String path = "data/multilinestring2d.shp";

	// public static String path = "data/cantons.shp";

	 public static String path = "data/pointsaltimercier.shp";

	/**
	 * @param args
	 * @throws DriverException
	 * @throws DataSourceCreationException
	 * @throws DriverLoadException
	 * @throws DelaunayError
	 */
	public static void main(String[] args) throws DriverLoadException,
			DataSourceCreationException, DriverException, DelaunayError {

		ArrayList<MyPoint> points = new ArrayList<MyPoint>();

		DataSource mydata = dsf.getDataSource(new File(path));

		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(mydata);
		sds.open();

		for (int i = 0; i < sds.getRowCount(); i++) {

			Geometry geom = sds.getGeometry(i);

			double altitude = sds.getFieldValue(i, sds.getFieldIndexByName("Altitude")).getAsDouble();
			for (int j = 0; j < geom.getNumGeometries(); j++) {

				Geometry subGeom = geom.getGeometryN(j);

				for (int k = 0; k < subGeom.getCoordinates().length; k++) {
					Coordinate coord = subGeom.getCoordinates()[k];

					points.add(new MyPoint(coord.x, coord.y, altitude));

				}

			}

		}

		sds.close();

		MyMesh aMesh = new MyMesh();

		aMesh.setPoints(points);
		// aMesh.setMax(1300, 700);

		Delaunay delaunay = new Delaunay(aMesh);

		aMesh.setStart();

		// process triangularization
		delaunay.processDelaunay();
		// Refine Mesh
		delaunay.refineMesh();

		// aMesh.saveMeshXML();

		saveEdges(delaunay);

		aMesh.setEnd();


	System.out.println("Temps de triangulation et de sauvegarde " +  aMesh.getDuration());

	}

	public static void saveEdges(Delaunay delaunay) throws DriverException {

		Metadata metadata = new DefaultMetadata(new Type[] {
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.STRING),
				TypeFactory.createType(Type.GEOMETRY) }, new String[] { "gid",
				"start_node", "end_node", "left_triangle", "right_triangle",
				"type", "the_geom" });

		ObjectMemoryDriver driver = new ObjectMemoryDriver(metadata);

		GeometryFactory gf = new GeometryFactory();

		ArrayList<MyPoint> points = delaunay.getMesh().getPoints();

		LinkedList<MyEdge> edges = delaunay.getMesh().getEdges();

		LinkedList<MyTriangle> triangles = delaunay.getMesh().getTriangles();

		for (int i = 0; i < edges.size(); i++) {

			MyEdge edge = edges.get(i);

			MyPoint p1 = edge.point(0);

			MyPoint p2 = edge.point(1);

			Coordinate[] coords = new Coordinate[] {
					new Coordinate(p1.getX(), p1.getY(), p1.getZ()),
					new Coordinate(p2.getX(), p2.getY(), p2.getZ()) };

			Geometry line = gf.createLineString(coords);

			int edgeId = edges.indexOf(edge);

			int startIdPoints = points.indexOf(edge.getStart());

			int endIdPoints = points.indexOf(edge.getEnd());

			int leftId = -1;
			if (edge.getLeft() == null) {

			} else {
				leftId = triangles.indexOf(edge.getLeft());
			}

			int rightId = -1;
			if (edge.getRight() == null) {

			} else {
				rightId = triangles.indexOf(edge.getRight());
			}

			String edgeType = edge.getEdgeType();

			driver.addValues(new Value[] { ValueFactory.createValue(edgeId),
					ValueFactory.createValue(startIdPoints),
					ValueFactory.createValue(endIdPoints),
					ValueFactory.createValue(leftId),
					ValueFactory.createValue(rightId),
					ValueFactory.createValue(edgeType),
					ValueFactory.createValue(line) });

		}

		File gdmsFile = new File("/tmp/tinEdges.shp");
		gdmsFile.delete();
		dsf.getSourceManager().register("result", gdmsFile);

		DataSource ds = dsf.getDataSource(driver);
		ds.open();
		dsf.saveContents("result", ds);
		ds.close();

	}

}
