package org.jdelaunay.delaunay;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

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
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.grap.utilities.EnvelopeUtil;
import org.jhydrocell.utilities.HydroTriangleUtil;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import org.gdms.driver.DiskBufferDriver;

/**
 * postprocessing Package.
 * 
 * @author Adelin PIAU
 * @date 2010-07-27
 * @revision 2010-10-04
 * @version 1.0
 */
public final class MyMeshExport {

        static DataSourceFactory dsf = new DataSourceFactory();

        /**
         * This is a utility class, we don't want to manage a public constructor.
         */
        private MyMeshExport(){

        }

        /**
         * Export a TIN in gdms 3 files. (points, edges, triangles)
         * @param aMesh
         * @param path
         * @throws DriverException
         */
        public static void exportTIN(MyMesh aMesh, String path) throws DriverException {
                // save points
                Metadata metadata = new DefaultMetadata(new Type[]{
                                TypeFactory.createType(Type.GEOMETRY),
                                TypeFactory.createType(Type.INT),
                                TypeFactory.createType(Type.INT)}, new String[]{
                                "the_geom", "gid", "type"});

                DiskBufferDriver driver = new DiskBufferDriver(dsf, metadata);

                for (Point aPoint : aMesh.getPoints()) {
                        driver.addValues(new Value[]{ValueFactory.createValue(new GeometryFactory().createPoint(aPoint.getCoordinate())),
                                        ValueFactory.createValue(aPoint.getGID()),
                                        ValueFactory.createValue(aPoint.getProperty())});
                }

                saveDriver(path + "Points.gdms", driver);


                // save edges
                metadata = new DefaultMetadata(new Type[]{
                                TypeFactory.createType(Type.GEOMETRY),
                                TypeFactory.createType(Type.INT),
                                TypeFactory.createType(Type.INT),
                                TypeFactory.createType(Type.SHORT),
                                TypeFactory.createType(Type.SHORT),
                                TypeFactory.createType(Type.SHORT),
                                TypeFactory.createType(Type.SHORT),
                                TypeFactory.createType(Type.BOOLEAN),
                                TypeFactory.createType(Type.BOOLEAN)}, new String[]{
                                "the_geom", "gid", "type", "N_start", "N_end", "Triangle_L", "Triangle_R", "topo_L", "topo_R"});

                driver = new DiskBufferDriver(dsf, metadata);

                for (Edge anEdge : aMesh.getEdges()) {
                        Collection<LineString> lineStrings = new ArrayList<LineString>();
                        lineStrings.add(new GeometryFactory().createLineString(
                                new Coordinate[]{anEdge.getStartPoint().getCoordinate(), anEdge.getEndPoint().getCoordinate()}));

                        new GeometryFactory();
                        driver.addValues(new Value[]{ValueFactory.createValue(
                                        new GeometryFactory().createMultiLineString(
                                        GeometryFactory.toLineStringArray(lineStrings))),
                                        ValueFactory.createValue(anEdge.getGID()),
                                        ValueFactory.createValue(anEdge.getProperty()),
                                        ValueFactory.createValue(anEdge.getStartPoint().getGID()),
                                        ValueFactory.createValue(anEdge.getEndPoint().getGID()),
                                        ValueFactory.createValue((anEdge.getLeft() == null ? -1 : anEdge.getLeft().getGID())),
                                        ValueFactory.createValue((anEdge.getRight() == null ? -1 : anEdge.getRight().getGID())),
                                        ValueFactory.createValue((anEdge.getLeft() == null ? false : HydroTriangleUtil.isLeftTriangleGoToEdge(anEdge))),
                                        ValueFactory.createValue((anEdge.getRight() == null ? false : HydroTriangleUtil.isRightTriangleGoToEdge(anEdge)))
                                });
                }


                saveDriver(path + "Edges.gdms", driver);


                // save triangles
                metadata = new DefaultMetadata(new Type[]{
                                TypeFactory.createType(Type.GEOMETRY),
                                TypeFactory.createType(Type.INT),
                                TypeFactory.createType(Type.INT),
                                TypeFactory.createType(Type.DOUBLE),
                                TypeFactory.createType(Type.DOUBLE)}, new String[]{
                                "the_geom", "gid", "type", "slope", "direction"});

                driver = new DiskBufferDriver(dsf, metadata);

                for (DelaunayTriangle aTriangle : aMesh.getTriangles()) {
                        Collection<Polygon> polygons = new ArrayList<Polygon>();
                        polygons.add(new GeometryFactory().createPolygon(
                                new GeometryFactory().createLinearRing(new Coordinate[]{
                                        aTriangle.getPoint(0).getCoordinate(),
                                        aTriangle.getPoint(1).getCoordinate(),
                                        aTriangle.getPoint(2).getCoordinate(),
                                        aTriangle.getPoint(0).getCoordinate()}), null));

                        new GeometryFactory();
                        driver.addValues(new Value[]{
                                        ValueFactory.createValue(new GeometryFactory().createMultiPolygon(GeometryFactory.toPolygonArray(polygons))),
                                        ValueFactory.createValue(aTriangle.getGID()),
                                        ValueFactory.createValue(aTriangle.getProperty()),
                                        ValueFactory.createValue(HydroTriangleUtil.getSlopeInPourcent(aTriangle)),
                                        ValueFactory.createValue(HydroTriangleUtil.getSlopeAzimut(aTriangle))});
                }

                saveDriver(path + "Triangles.gdms", driver);

        }

        /**
         * Import triangle of a TIN in a new MyMesh.
         * @param path
         * @return
         * @throws DriverException
         * @throws DataSourceCreationException
         * @throws DriverLoadException
         * @throws DelaunayError
         */
        public static MyMesh importTINTriangle(String path) throws DriverException, DataSourceCreationException, DelaunayError {
                MyMesh aMesh = new MyMesh();

                DataSource mydata;
                SpatialDataSourceDecorator sds;
                mydata = dsf.getDataSource(new File(path));
                sds = new SpatialDataSourceDecorator(mydata);
                sds.open();

                // set bounding box
                Envelope env = sds.getFullExtent();
                Geometry geomEnv = EnvelopeUtil.toGeometry(env);
                Coordinate[] coords = geomEnv.getCoordinates();
                BoundaryBox abox = new BoundaryBox();
                for (int i = 0; i < coords.length - 1; i++) {
                        abox.alterBox(coords[i].x, coords[i].y, 0);
                }
                aMesh.init(abox);


                // add triangle to the mesh
                for (long i = 0; i < sds.getRowCount(); i++) {
                        Geometry geom = sds.getGeometry(i);
                        for (int j = 0; j < geom.getNumGeometries(); j++) {
                                Geometry subGeom = geom.getGeometryN(j);
                                if (subGeom instanceof MultiPolygon) {
                                        DelaunayTriangle atriangle = new DelaunayTriangle(
                                                new Edge(new Point(subGeom.getCoordinates()[0]), new Point(subGeom.getCoordinates()[1])),
                                                new Edge(new Point(subGeom.getCoordinates()[1]), new Point(subGeom.getCoordinates()[2])),
                                                new Edge(new Point(subGeom.getCoordinates()[2]), new Point(subGeom.getCoordinates()[3])));
                                        aMesh.addTriangle(atriangle);
                                }
                        }
                }

                sds.close();
                return aMesh;
        }

        private static void saveDriver(String name, ObjectDriver driver) throws DriverException {
                DataSourceFactory locDsf = new DataSourceFactory();
                File gdmsFile = new File(name);
                if(gdmsFile.delete()){
                        locDsf.getSourceManager().register(name, gdmsFile);
                        DataSource ds = locDsf.getDataSource(driver);
                        ds.open();
                        locDsf.saveContents(name, ds);
                        ds.close();
                }
        }
}
