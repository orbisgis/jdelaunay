package org.jdelaunay.utilities;

import org.jdelaunay.delaunay.MyEdge;
import org.jdelaunay.delaunay.MyPoint;
import org.jdelaunay.delaunay.MyTriangle;

import com.vividsolutions.jts.algorithm.Angle;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.Triangle;
import com.vividsolutions.jts.util.Assert;

public class HydroPolygonUtil {

	private double valeurPente = -1.0;

	private double orientationPente = -1.0;
	private Coordinate pente;
	private Polygon p;
	private Triangle triangle;

	private GeometryFactory gf = new GeometryFactory();

	public HydroPolygonUtil(MyTriangle myTriangle) {

		MyPoint[] points = myTriangle.points;

		Coordinate p1 = new Coordinate(points[0].x, points[0].y, points[0].z);
		Coordinate p2 = new Coordinate(points[1].x, points[1].y, points[1].z);
		Coordinate p3 = new Coordinate(points[2].x, points[2].y, points[2].z);

		LinearRing shell = gf.createLinearRing(new Coordinate[] { p1, p2, p3,
				p1 });

		p = gf.createPolygon(shell, null);

		triangle = JTSUtilities.createClockwiseTriangle(p1, p2, p3);

	}

	/**
	 *
	 * @return normal vector (that pointing to the sky --> z>0) of the face
	 *
	 */
	public Coordinate getNormal() {

		Coordinate n = new Coordinate();
		// vecteurs directeurs du triangle
		Coordinate v1 = new Coordinate(triangle.p1.x - triangle.p0.x,
				triangle.p1.y - triangle.p0.y, triangle.p1.z - triangle.p0.z);
		Coordinate v2 = new Coordinate(triangle.p2.x - triangle.p0.x,
				triangle.p2.y - triangle.p0.y, triangle.p2.z - triangle.p0.z);

		// calcul de la normale par produit vectoriel
		n.x = v1.y * v2.z - v1.z * v2.y;
		n.y = v1.z * v2.x - v1.x * v2.z;
		n.z = v1.x * v2.y - v1.y * v2.x;
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
	public Coordinate get3DVector() {
		if (pente == null) {
			pente = new Coordinate(0, 0, 0);
			// on recupere le vecteur normal
			Coordinate n = getNormal();
			// on en deduit le vecteur de ligne de niveau
			// Coordinate l = new Coordinate(n.y , -n.x, 0);
			// on ne traite pas les cas degeneres
			if (n.x == 0 && n.y == 0)
				pente = new Coordinate(0, 0, 0);
			else {
				if (n.x == 0)
					pente = new Coordinate(0, 1, -n.y / n.z);
				else if (n.y == 0)
					pente = new Coordinate(1, 0, -n.x / n.z);
				else
					pente = new Coordinate(n.x / n.y, 1, -1 / n.z
							* (n.x * n.x / n.y + n.y));
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
		}
		return pente;
	}

	/**
	 *
	 * @return pente du vecteur de plus forte pente de la face (dz/distance
	 *         horizontale)
	 */
	public double getSlope() {
		if (valeurPente == -1.0) {
			double d = 0.0;
			Coordinate p = get3DVector();
			if (p != null)
				// calcul de la distance horizontale
				// separant les 2 extremites du vecteur pente
				d = Math.sqrt(p.x * p.x + p.y * p.y);
			valeurPente = d == 0.0 ? 0.0 : p.z / d;
		}

		// debug obedel
		if (Math.abs(valeurPente) > 1.0) {
			// System.out.println("valeur de pente surprenante : " +
			// valeurPente);
			Coordinate p = get3DVector();

		}

		return valeurPente;
	}

	public boolean getPenteVersEdge(MyEdge myEdge) {
		boolean res = false;

		MyPoint pt1 = myEdge.getStart();
		MyPoint pt2 = myEdge.getEnd();

		// on determine les sommets A,B et C du triangle et on calle AB (ou BA)
		// sur e
		Coordinate A = new Coordinate(pt1.x, pt1.y, pt1.z);
		Coordinate B = new Coordinate(pt2.x, pt2.y, pt2.z);
		int i = 0;
		while (!(i == 4 || (!p.getCoordinates()[i].equals3D(A) && !p
				.getCoordinates()[i].equals3D(B))))
			i++;
		// Assert.isTrue(i!=4,"edge n'appartenant pas au triangle");
		if (i == 4)
			return res; // e n'appartient pas au triangle

		Coordinate C = p.getCoordinates()[i];
		Coordinate AB = MathUtil.DifferenceVectoriel(B, A);
		Coordinate AC = MathUtil.DifferenceVectoriel(C, A);
		// orientation CCW
		if (MathUtil.ProduitVectoriel(AB, AC).z < 0) {
			// echange A et B
			Coordinate D = A;
			A = B;
			B = D;
			AB = MathUtil.DifferenceVectoriel(B, A);
			AC = MathUtil.DifferenceVectoriel(C, A);
		}
		// test d'intersection entre AB et P
		Coordinate P = this.get3DVector();

		res = MathUtil.ProduitVectoriel(AB, P).z < 0;

		return res;
	}

	/**
	 * Pente topographique exprimée en degrées
	 *
	 * @return
	 */
	public double getSlopeInDegree() {
		return Math.abs(getSlope()) * 100;
	}

	public Coordinate getCentroid() {

		return new Coordinate(p.getInteriorPoint().getX(), p.getInteriorPoint()
				.getY());
	}

	/**
	 *
	 * @return angle entre le nord et la direction de plus forte pente (sens
	 *         descendant) de la face (en degres)
	 */
	public double getSlopeAzimut() {
		if (orientationPente == -1.) {
			Coordinate c1 = new Coordinate(0.0, 0.0, 0.0);
			Coordinate c2 = this.get3DVector();
			if (c2 == null)
				c2 = new Coordinate(0.0, 0.0, 0.0);
			if (c2.z > 0.0)
				c2.setCoordinate(new Coordinate(-c2.x, -c2.y, -c2.z));
			// l'ordre des coordonnees correspond a l'orientation de l'arc
			// "sommet haut vers sommet bas"
			double angleAxeX_rad = Angle.angle(c1, c2);
			// on considere que l'axe nord correspond a l'axe Y positif
			double angleAxeNord_rad = Angle.PI_OVER_2 - angleAxeX_rad;
			double angleAxeNord_deg = Angle.toDegrees(angleAxeNord_rad);
			// on renvoie toujours une valeur d'angle >= 0
			orientationPente = angleAxeNord_deg < 0.0 ? 360.0 + angleAxeNord_deg
					: angleAxeNord_deg;
		}
		return orientationPente;
	}





	public Coordinate getPenteEdgeIntersect(LineString e, Coordinate p) {
		Coordinate I = null;

		Coordinate eVector = MathUtil.getVector(e.getStartPoint()
				.getCoordinate(), e.getEndPoint().getCoordinate());

		// Coordinate AB = MathUtil.DifferenceVectoriel(B, A);
		Coordinate P = this.get3DVector();
		I = MathUtil.CalculIntersection(e.getStartPoint().getCoordinate(),
				eVector, p, P);
		Assert.isTrue(I != null,
				"Intersection detectee mais non verifiee par calcul");

		return I;
	}

}
