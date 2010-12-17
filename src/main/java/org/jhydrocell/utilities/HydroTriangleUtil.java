package org.jhydrocell.utilities;

import org.jdelaunay.delaunay.Edge;
import org.jdelaunay.delaunay.Point;
import org.jdelaunay.delaunay.DelaunayTriangle;

import com.vividsolutions.jts.algorithm.Angle;
import com.vividsolutions.jts.geom.Coordinate;

public class HydroTriangleUtil {

//	private GeometryFactory gf = new GeometryFactory();

	/**
	 *
	 * @return normal vector (that pointing to the sky --> z>0) of the face
	 *
	 */
	public static Coordinate getNormal(DelaunayTriangle triangle) {

		Coordinate n = new Coordinate();

		// calcul de la normale par produit vectoriel
		n.x = (triangle.getPoint(1).getY()-triangle.getPoint(0).getY()) * (triangle.getPoint(2).getZ()-triangle.getPoint(0).getZ()) - (triangle.getPoint(1).getZ()-triangle.getPoint(0).getZ()) * (triangle.getPoint(2).getY()-triangle.getPoint(0).getY());
		n.y = (triangle.getPoint(1).getZ()-triangle.getPoint(0).getZ()) * (triangle.getPoint(2).getX()-triangle.getPoint(0).getX()) - (triangle.getPoint(1).getX()-triangle.getPoint(0).getX()) * (triangle.getPoint(2).getZ()-triangle.getPoint(0).getZ());
		n.z = (triangle.getPoint(1).getX()-triangle.getPoint(0).getX()) * (triangle.getPoint(2).getY()-triangle.getPoint(0).getY()) - (triangle.getPoint(1).getY()-triangle.getPoint(0).getY()) * (triangle.getPoint(2).getX()-triangle.getPoint(0).getX());
		// normage du vecteur
		double norme = Math.sqrt(n.x * n.x + n.y * n.y + n.z * n.z);
		n.x = n.x / norme;
		n.y = n.y / norme;
		n.z = n.z / norme;
		// on veut que la normale pointe vers le ciel
		if (n.z < 0) {
			n.x = -n.x;
			n.y = -n.y;
			n.z = -n.z;
		}
		return n;
	}

	/**
	 *
	 * @return steepest path vector of the face also direction
	 */
	public static Coordinate get3DVector(DelaunayTriangle triangle) {
		Coordinate	pente = new Coordinate(0, 0, 0);
			// on recupere le vecteur normal
			Coordinate n = getNormal(triangle);
			// on en deduit le vecteur de ligne de niveau
			// Coordinate l = new Coordinate(n.y , -n.x, 0);
			// on ne traite pas les cas degeneres
			if (n.x == 0 && n.y == 0) {
			pente = new Coordinate(0, 0, 0);
		}
			else {
				if (n.x == 0) {
				pente = new Coordinate(0, 1, -n.y / n.z);
			}
				else if (n.y == 0) {
				pente = new Coordinate(1, 0, -n.x / n.z);
			}
				else {
				pente = new Coordinate(n.x / n.y, 1, -1 / n.z * (n.x * n.x / n.y + n.y));
			}
				// on choisit le sens descendant
				if (pente.z > 0) {
					pente.x = -pente.x;
					pente.y = -pente.y;
					pente.z = -pente.z;
				}
				double norme = Math.sqrt(pente.x * pente.x + pente.y * pente.y
						+ pente.z * pente.z);
				// normage du vecteur
				if (norme > 0) {
					pente.x = pente.x / norme;
					pente.y = pente.y / norme;
					pente.z = pente.z / norme;
				}
			}
		return pente;
	}

	
	public static boolean getPenteVersEdge(Edge myEdge, DelaunayTriangle triangle) {
		boolean res = false;

		// on determine les sommets A,B et C du triangle et on calle AB (ou BA)
		// sur e
		Coordinate a = myEdge.getStartPoint().getCoordinate();
		Coordinate b = myEdge.getEndPoint().getCoordinate();
		int i = 0;
		while (!(i == 4 || (!triangle.getPoint(i).getCoordinate().equals3D(a) && !triangle.getPoint(i).getCoordinate().equals3D(b)))) {
			i++;
		}

		if (i == 4) {
			return res;
		} // e n'appartient pas au triangle

		Coordinate c = triangle.getPoint(i).getCoordinate();
		Coordinate ab = MathUtil.differenceVectoriel(b, a);
		Coordinate ac = MathUtil.differenceVectoriel(c, a);
		// orientation CCW
		if (MathUtil.produitVectoriel(ab, ac).z < 0) {
			// echange A et B
			Coordinate d = a;
			a = b;
			b = d;
			ab = MathUtil.differenceVectoriel(b, a);
			ac = MathUtil.differenceVectoriel(c, a);
		}
		// test d'intersection entre AB et P
		Coordinate p = get3DVector(triangle);

		res = MathUtil.produitVectoriel(ab, p).z < 0;

		return res;
	}
	
	
	public static boolean isLeftTriangleGoToEdge(Edge edge)
	{
		if(edge.getLeft()!=null)
		{
			Point p= edge.getLeft().getAlterPoint(edge);
			if(p.getZ() <edge.getStartPoint().getZ() && p.getZ() <edge.getEndPoint().getZ()) {
				return false;
			}
			
			return getPenteVersEdge(edge, edge.getLeft());
		}
		return false;
	}
	
	
	public static boolean isRightTriangleGoToEdge(Edge edge)
	{
		if(edge.getRight()!=null)
		{
			Point p= edge.getRight().getAlterPoint(edge);
			if(p.getZ() <edge.getStartPoint().getZ() && p.getZ() <edge.getEndPoint().getZ()) {
				return false;
			}
			
			return getPenteVersEdge(edge, edge.getRight());
		}
		return false;
	}
	
	
	
	/**
	 *
	 * @return angle entre le nord et la direction de plus forte pente (sens
	 *         descendant) de la face (en degres)
	 */
	public static double getSlopeAzimut(DelaunayTriangle triangle) {
		double orientationPente;
			Coordinate c1 = new Coordinate(0.0, 0.0, 0.0);
			Coordinate c2 = get3DVector(triangle);
			if (c2 == null) {
			c2 = new Coordinate(0.0, 0.0, 0.0);
		}
			if (c2.z > 0.0) {
			c2.setCoordinate(new Coordinate(-c2.x, -c2.y, -c2.z));
		}
			// l'ordre des coordonnees correspond a l'orientation de l'arc
			// "sommet haut vers sommet bas"
			double angleAxeXrad = Angle.angle(c1, c2);
			// on considere que l'axe nord correspond a l'axe Y positif
			double angleAxeNordrad = Angle.PI_OVER_2 - angleAxeXrad;
			double angleAxeNorddeg = Angle.toDegrees(angleAxeNordrad);
			// on renvoie toujours une valeur d'angle >= 0
			orientationPente = angleAxeNorddeg < 0.0 ? 360.0 + angleAxeNorddeg
					: angleAxeNorddeg;
		return orientationPente;
	}
	
	
	
	/**
	 *
	 * @return pente du vecteur de plus forte pente de la face (dz/distance
	 *         horizontale)
	 */
	public static double getSlope(DelaunayTriangle triangle) {
			double d = 0.0, valeurPente;
			Coordinate p = get3DVector(triangle);
			if (p != null) {
			d = Math.sqrt(p.x * p.x + p.y * p.y);
		}
			valeurPente = d == 0.0 ? 0.0 : p.z / d;

		return valeurPente;
	}

//	public boolean getPenteVersEdge(Edge myEdge) {
//		boolean res = false;
//
//		// on determine les sommets A,B et C du triangle et on calle AB (ou BA)
//		// sur e
//		Coordinate A = myEdge.getStartPoint().getCoordinate();
//		Coordinate B = myEdge.getEndPoint().getCoordinate();
//		int i = 0;
//		while (!(i == 4 || (!triangle.getPoint(i).getCoordinate().equals3D(A) && !triangle.getPoint(i).getCoordinate().equals3D(B))))
//			i++;
//		// Assert.isTrue(i!=4,"edge n'appartenant pas au triangle");
//		if (i == 4)
//			return res; // e n'appartient pas au triangle
//
//		Coordinate C = triangle.getPoint(i).getCoordinate();
//		Coordinate AB = MathUtil.DifferenceVectoriel(B, A);
//		Coordinate AC = MathUtil.DifferenceVectoriel(C, A);
//		// orientation CCW
//		if (MathUtil.ProduitVectoriel(AB, AC).z < 0) {
//			// echange A et B
//			Coordinate D = A;
//			A = B;
//			B = D;
//			AB = MathUtil.DifferenceVectoriel(B, A);
//			AC = MathUtil.DifferenceVectoriel(C, A);
//		}
//		// test d'intersection entre AB et P
//		Coordinate P = this.get3DVector();
//
//		res = MathUtil.ProduitVectoriel(AB, P).z < 0;
//
//		return res;
//	}

	
	
	
	
	
	/**
	 * Pente topographique exprimée en degrées
	 *
	 * @return
	 */
	public static double getSlopeInPourcent(DelaunayTriangle triangle) {
		return Math.abs(getSlope(triangle)) * 100;
	}
	
	
	/**
	 * Pente topographique exprimée en degrées
	 *
	 * @return
	 */
	public static double getSlopeInDegree(DelaunayTriangle triangle) {
		return Math.round(360/(2*Math.PI)*Math.atan(getSlopeInPourcent(triangle)/100));
	}
	
	

//	public Coordinate getCentroid() {
//
//		return new Coordinate(p.getInteriorPoint().getX(), p.getInteriorPoint()
//				.getY());
//	}







//	public Coordinate getPenteEdgeIntersect(LineString e, Coordinate p) {
//		Coordinate I = null;
//
//		Coordinate eVector = MathUtil.getVector(e.getStartPoint()
//				.getCoordinate(), e.getEndPoint().getCoordinate());
//
//		// Coordinate AB = MathUtil.DifferenceVectoriel(B, A);
//		Coordinate P = this.get3DVector();
//		I = MathUtil.CalculIntersection(e.getStartPoint().getCoordinate(),
//				eVector, p, P);
//		Assert.isTrue(I != null,
//				"Intersection detectee mais non verifiee par calcul");
//
//		return I;
//	}

	

}
