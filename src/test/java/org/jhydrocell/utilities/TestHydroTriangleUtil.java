package org.jhydrocell.utilities;

import org.jdelaunay.delaunay.DelaunayError;
import org.jdelaunay.delaunay.Edge;
import org.jdelaunay.delaunay.Point;
import org.jdelaunay.delaunay.DelaunayTriangle;

import com.vividsolutions.jts.geom.Coordinate;

public class TestHydroTriangleUtil {

	/**
	 * @param args
	 * @throws DelaunayError 
	 */
	public static void main(String[] args) throws DelaunayError {		
		int max=95;
		for(double i= -max; i<=max;i+=1.0)
		{
			Point p1=new Point(0, 0, 0);
			Point p2=new Point(10*Math.cos(Math.toRadians(i-45)), 10*Math.sin(Math.toRadians(i-45)), 10);
			Point p3=new Point(10*Math.cos(Math.toRadians(i+45)), 10*Math.sin(Math.toRadians(i+45)), 10);

			
//			Point p1=new Point(0, 0, 0);
//			Point p2=new Point(10, 0, 0);
//			Point p3=new Point(5, 5*Math.cos(Math.toRadians(i)), 5*Math.sin(Math.toRadians(i)));
			
			Edge e1= new Edge(p1, p2);
			Edge e2= new Edge(p2, p3);
			Edge e3= new Edge(p3, p1);
			DelaunayTriangle triangle = new DelaunayTriangle(e1, e2, e3);
			
//			HydroPolygonUtil h= new HydroPolygonUtil(triangle);
	
			System.out.println(p3.getY()+" "+p3.getZ()+" | "+HydroTriangleUtil.isLeftTriangleGoToEdge(e1)+" | "+HydroTriangleUtil.isRightTriangleGoToEdge(e1)+" | "+i);
			System.out.println("SlopeAzimut : "+HydroTriangleUtil.getSlopeAzimut(triangle));
			System.out.println("SlopeInDegree : "+HydroTriangleUtil.getSlopeInDegree(triangle));
			System.out.println("SlopeInPourcent : "+HydroTriangleUtil.getSlopeInPourcent(triangle));
			Coordinate normale =HydroTriangleUtil.getNormal(triangle);
			int coefAff=100;
			String r="x:";
			for(int j=0;j*j<(normale.x*coefAff * normale.x*coefAff);j++) {
				r += "_";
			}
			System.out.println(r+" "+normale.x);
			r="y:";
			for(int j=0;j*j<(normale.y*coefAff * normale.y*coefAff);j++) {
				r += "_";
			}
			System.out.println(r+" "+normale.y);
			r="z:";
			for(int j=0;j*j<(normale.z*coefAff * normale.z*coefAff);j++) {
				r += "_";
			}
			System.out.println(r+" "+normale.z);
//			System.out.println("\n");
//			System.out.println("3DVector : "+h.get3DVector()+"\n");
		}
		
	}

}
