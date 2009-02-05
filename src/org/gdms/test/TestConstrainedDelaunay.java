package org.gdms.test;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.gdms.algorithm.jts.operation.UniqueSegmentsExtracter;
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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

public class TestConstrainedDelaunay {

	public static DataSourceFactory dsf = new DataSourceFactory();

	public static String path = "data/courbesZ.shp";

	// public static String path = "data/pointsaltimercier.shp";

	// public static String path = "data/cantons.shp";

	//public static String path = "data/courbesdem10_zone.shp";



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

		LinkedList<MyEdge> breaklines = new LinkedList<MyEdge>();

		DataSource mydata = dsf.getDataSource(new File(path));

		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(mydata);
		sds.open();


		for (int i = 0; i < sds.getRowCount(); i++) {

			Geometry geom = sds.getGeometry(i);

			for (int j = 0; j < geom.getNumGeometries(); j++) {

				Geometry subGeom = geom.getGeometryN(j);

				if (subGeom.getDimension() == 0) {

					for (int k = 0; k < subGeom.getCoordinates().length; k++) {

						Coordinate coord = subGeom.getCoordinates()[k];

						points.add(new MyPoint(coord.x, coord.y, coord.z));
					}

				}

				else {

					UniqueSegmentsExtracter uniqueSegmentsExtracter = new UniqueSegmentsExtracter(
							geom);
					List<LineString> list = uniqueSegmentsExtracter
							.getSegmentAsLineString();

					for (int l = 0; l < list.size(); l++) {

						LineString ls = (LineString) list.get(l);

						Coordinate coord0 = ls.getCoordinates()[0];

						Coordinate coord1 = ls.getCoordinates()[1];

						MyPoint p0 = new MyPoint(coord0.x, coord0.y, coord0.z);

						MyPoint p1 = new MyPoint(coord1.x, coord1.y, coord1.z);

						points.add(p0);
						points.add(p1);
						MyEdge edge = new MyEdge(p0, p1);
						breaklines.add(edge);

					}

				}

			}

		}

		sds.close();

		MyMesh aMesh = new MyMesh();
		aMesh.setPoints(points);
		aMesh.setEdges(breaklines);

		Delaunay delaunay = new Delaunay(aMesh);

		aMesh.setStart();

		// process triangularization
		delaunay.processDelaunay();
		// Refine Mesh
		delaunay.refineMesh();

		// aMesh.saveMeshXML();

		saveEdges(delaunay);

		aMesh.setEnd();

		System.out.println("Temps de triangulation et de sauvegarde "
				+ aMesh.getDuration());

	}

	public static void saveEdges(Delaunay delaunay) throws DriverException {

		Metadata metadata = new DefaultMetadata(new Type[] {
				TypeFactory.createType(Type.SHORT),
				TypeFactory.createType(Type.GEOMETRY) }, new String[] { "gid",
				"the_geom" });

		ObjectMemoryDriver driver = new ObjectMemoryDriver(metadata);

		GeometryFactory gf = new GeometryFactory();

		LinkedList<MyEdge> edges = delaunay.getMesh().getEdges();

		for (int i = 0; i < edges.size(); i++) {

			MyEdge edge = edges.get(i);

			MyPoint p1 = edge.point(0);

			MyPoint p2 = edge.point(1);

			Coordinate[] coords = new Coordinate[] {
					new Coordinate(p1.getX(), p1.getY(), p1.getZ()),
					new Coordinate(p2.getX(), p2.getY(), p2.getZ()) };
			Geometry line = gf.createLineString(coords);

			driver.addValues(new Value[] { ValueFactory.createValue(i),
					ValueFactory.createValue(line) });

		}

		File gdmsFile = new File("tinEdges.shp");
		gdmsFile.delete();
		dsf.getSourceManager().register("result", gdmsFile);

		DataSource ds = dsf.getDataSource(driver);
		ds.open();
		dsf.saveContents("result", ds);
		ds.close();

	}

}
