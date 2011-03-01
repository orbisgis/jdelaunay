package org.jhydrocell.utilities;

import org.jdelaunay.delaunay.DelaunayError;
import org.jdelaunay.delaunay.DEdge;
import org.jdelaunay.delaunay.DPoint;
import org.jdelaunay.delaunay.DTriangle;

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
			DPoint p1=new DPoint(0, 0, 0);
			DPoint p2=new DPoint(10*Math.cos(Math.toRadians(i-45)), 10*Math.sin(Math.toRadians(i-45)), 10);
			DPoint p3=new DPoint(10*Math.cos(Math.toRadians(i+45)), 10*Math.sin(Math.toRadians(i+45)), 10);

			
//			DPoint p1=new DPoint(0, 0, 0);
//			DPoint p2=new DPoint(10, 0, 0);
//			DPoint p3=new DPoint(5, 5*Math.cos(Math.toRadians(i)), 5*Math.sin(Math.toRadians(i)));
			
			DEdge e1= new DEdge(p1, p2);
			DEdge e2= new DEdge(p2, p3);
			DEdge e3= new DEdge(p3, p1);
			DTriangle triangle = new DTriangle(e1, e2, e3);
			
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
