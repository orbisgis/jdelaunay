/* Ce package est inclus dans la distribution logicielle FlowTIN.
 * Il permet de charger et de travailler sur un r�seau de triangles irr�guliers (TIN), construit � partir
 * de l'application TopoTIN.
 *
 * Ce package a �t� developp� par Olivier Bedel dans le cadre de la th�se de doctorat en g�ographie
 * d'Erwan Bocher, intitul�e : Impacts des activit�s humaines sur le parcours des �coulements de surface dans un bassin versant bocager :
 * essai de mod�lisation spatiale.  Application au Bassin versant du Jaudy-Guindy-Bizien.
 *
 *  Le manuscrit est disponible en t�l�chargement sur le site th�se en ligne du CNRS.
 *
 *
 * Cette th�se a b�n�fici� du soutien financier : de l�Union europ�enne, du Conseil R�gional de Bretagne, de l�Agence de l�eau Loire-Bretagne,
 * du Conseil G�n�ral des C�tes-d�Armor et du Bassin versant du Jaudy-Guindy-Bizien[2] par l�interm�diaire du Syndicat intercommunal d�adduction d�eau du Tr�gor, ma�tre d�ouvrage.
 * Elle a �t� r�alis�e � l�Universit� de Rennes 2 Haute-Bretagne au Laboratoire RESO UMR ESO CNRS 6590 en partenariat avec le Laboratoire COSTEL UMR LETG 6554.
 *
 *
 * Created on 25 ao�t 2004
 *
 * Olivier BEDEL et Erwan Bocher
 * Bassin Versant du Jaudy-Guindy-Bizien,
 * Laboratoire RESO UMR ESO 6590 CNRS, Universit� de Rennes 2
 *
 * Ce programme est distribu� sous la licence logiciel libre du CNRS CeCILL.
 * Pour utiliser ce programme ou le modifier vous devez accepter pleinement et respecter les conditions de cette licence.
 */

package org.jhydrocell.utilities;

import com.vividsolutions.jts.geom.Coordinate;

public class MathUtil {

	private static final double EPSILON = 1.0E-8;

	public static Coordinate getVector(Coordinate v1, Coordinate v2) {
		Coordinate v3 = new Coordinate(0, 0, 0);
		v3.x = v2.x - v1.x;
		v3.y = v2.y - v1.y;
		v3.z = v2.z - v1.z;

		return v3;
	}

	public static Coordinate ProduitVectoriel(Coordinate v1, Coordinate v2) {
		Coordinate v3 = new Coordinate(0, 0, 0);
		v3.x = v1.y * v2.z - v1.z * v2.y;
		v3.y = v1.z * v2.x - v1.x * v2.z;
		v3.z = v1.x * v2.y - v1.y * v2.x;
		return v3;
	}

	public static boolean IsColinear(Coordinate v1, Coordinate v2) {
		double res = 0;
		res += Math.abs(v1.y * v2.z - v1.z * v2.y);
		res += Math.abs(v1.z * v2.x - v1.x * v2.z);
		res += Math.abs(v1.x * v2.y - v1.y * v2.x);
		if (res < EPSILON)
			return true;

		else
			return false;
	}

	public static Coordinate DifferenceVectoriel(Coordinate v1, Coordinate v2) {
		Coordinate v3 = new Coordinate(0, 0, 0);
		v3.x = v1.x - v2.x;
		v3.y = v1.y - v2.y;
		v3.z = v1.z - v2.z;
		return v3;
	}

	public static Coordinate SommeVectoriel(Coordinate v1, Coordinate v2) {
		Coordinate v3 = new Coordinate(0, 0, 0);
		v3.x = v1.x + v2.x;
		v3.y = v1.y + v2.y;
		v3.z = v1.z + v2.z;
		return v3;
	}

	public static double ProduitScalaire(Coordinate v1, Coordinate v2) {
		double p;
		p = v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
		return p;
	}

	public static Coordinate Produit(Coordinate v1, double k) {
		Coordinate v3 = new Coordinate(0, 0, 0);
		v3.x = k * v1.x;
		v3.y = k * v1.y;
		v3.z = k * v1.z;
		return v3;
	}

	public static int Sign(double d) {
		if (d > 0)
			return 1;
		else if (d < 0)
			return -1;
		else
			return 0;
	}

	public static double Min(double d1, double d2) {
		if (d1 < d2)
			return d1;
		else
			return d2;
	}

	public static double Max(double d1, double d2) {
		if (d1 > d2)
			return d1;
		else
			return d2;
	}

	public static double Norme(Coordinate v) {
		return v.z == Double.NaN ? Math.sqrt(v.x * v.x + v.y * v.y) : Math
				.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
	}

	public static Coordinate NormeVectoriel(Coordinate v) {
		double norme = Norme(v);
		Coordinate vNorme = (Coordinate) v.clone();
		if (norme != 0.0)
			vNorme = new Coordinate(v.x / norme, v.y / norme, v.z / norme);
		return vNorme;
	}

	/**
	 * Calcule le point d'intersection de 2 droites coplanaires
	 *
	 * @param p1
	 *            point de la premiere droite
	 * @param v1
	 *            vecteur directeur de la premiere droite
	 * @param p2
	 *            point de la seconde droite
	 * @param v2
	 *            vecteur directeur de la seconde droite
	 * @return coordonnees du point d'intersection des 2 droites ou null si les
	 *         droites sont parrallèles
	 */

	public static Coordinate CalculIntersection(Coordinate p1, Coordinate v1,
			Coordinate p2, Coordinate v2) {
		double delta;
		double k;
		Coordinate i = null;
		// methode de Cramer pour determiner l'intersection de 2 droites du plan
		delta = v1.x * (-v2.y) - (-v1.y) * v2.x;
		if (delta != 0) {
			k = ((p2.x - p1.x) * (-v2.y) - (p2.y - p1.y) * (-v2.x)) / delta;
			i = new Coordinate();
			i.x = p1.x + k * v1.x;
			i.y = p1.y + k * v1.y;
			i.z = p1.z + k * v1.z;
		}
		return i;
	}

	/**
	 * Arrrondi une valeur double a p chiffre apres la virgule
	 *
	 * @param v
	 *            valeur double a arrondir
	 * @param p
	 *            precision (nombre de chiffres apres la virgule a prendre en
	 *            compte)
	 * @return v arrondi a p chiffre apres la virgule
	 */

	public static double round_double(double v, int p) {
		double vRounded = v;
		double m = 1.0;
		for (int i = 0; i < p; i++)
			m = m * 10.0;
		vRounded = Math.rint(v * m) / m;

		return vRounded;
	}

	 public static Coordinate getIntersection(Coordinate p1, Coordinate p2, Coordinate p3, Coordinate v) {
	        Coordinate intersection = null;

	        //MyPoint p4 = point[1];

	        // (x2 - x1) t1 - vx t2 = (x3 - x1)
	        // (y2 - y1) t1 - vy t2 = (y3 - y1)

	        double a1 = p2.x - p1.x;
	        double b1 = v.x;
	        double c1 = p3.x - p1.x;
	        double a2 = p2.y - p1.y;
	        double b2 = v.y;
	        double c2 = p3.y - p1.y;

	        // d = vx (y2 - y1) - (x2 - x1) * vy
	        double d = b1 * a2 - b2 * a1;
	        if (d != 0) {
	            // t1 = ((y3 - y1) vx - (x3 - x1) vy) / d
	            // t2 = ((x2 - x1) (y3 - y1) - (y2 - y1) (x3 - x1)) / d

	            double t1 = (c2 * b1 - c1 * b2) / d;
	            double t2 = (a1 * c2 - a2 * c1) / d;

	            if ((-EPSILON <= t1) && (t1 <= 1 + EPSILON)) {
	                // it intersects
	                if (t1 <= EPSILON)
	                    intersection = p1;
	                else if (t1 >= 1 - EPSILON)
	                    intersection = p2;
	                else {
	                    // x = x2 t1 + (1 - t1) x1
	                    // y = y2 t1 + (1 - t1) y1
	                    // z = z2 t1 + (1 - t1) z1
	                    double x = p2.x * t1 + (1 - t1) * p1.x;
	                    double y = p2.y * t1 + (1 - t1) * p1.y;
	                    double z = p2.z * t1 + (1 - t1) * p1.z;

	                    intersection = new Coordinate(x, y, z);

	                    // Last verification
	                    if (p1.distance(intersection) < EPSILON)
	                        intersection = p1;
	                    else if (p2.distance(intersection) < EPSILON)
	                        intersection = p2;
	                    else if (p3.distance(intersection) < EPSILON)
	                        intersection = p3;
	                }
	            }
	        }

	        return intersection;
	    }


}
