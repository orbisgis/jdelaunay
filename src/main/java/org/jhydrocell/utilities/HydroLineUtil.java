package org.jhydrocell.utilities;

import org.jdelaunay.delaunay.*;

import com.vividsolutions.jts.algorithm.Angle;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

public class HydroLineUtil {

	private Coordinate pente = null;

	private double valeurPente = -1.0;

	private double orientationPente = -1.0;

	private LineString geom;

	GeometryFactory gf = new GeometryFactory();

	public HydroLineUtil(MyEdge myEdge) {

		MyPoint pt1 = myEdge.getStart();
		MyPoint pt2 = myEdge.getEnd();
		Coordinate p1 = pt1.getCoordinate();
		Coordinate p2 = pt2.getCoordinate();
		geom = gf.createLineString(new Coordinate[] { p1, p2 });

	}

	/**
	 *
	 * @return pente de l'edge (dz/distance horizontale)
	 */
	public double getSlope() {
		if (valeurPente == -1.) {
			Coordinate c1 = this.geom.getCoordinates()[0];
			Coordinate c2 = this.geom.getCoordinates()[this.geom.getNumPoints() - 1];
			// l'ordre des coordonnees correspond a l'orientation de l'arc
			double dz = c2.z - c1.z;
			// calcul de la distance horizontale separant les 2 extremites de
			// l'edge
			double d = Math.sqrt((c2.x - c1.x) * (c2.x - c1.x) + (c2.y - c1.y)
					* (c2.y - c1.y));
			valeurPente = d == 0.0 ? 0.0 : dz / d;
		}
		return valeurPente;
	}

	/**
	 * Pente topographique exprimée en degrées
	 *
	 * @return
	 */
	public double getSlopeInDegree() {
		return Math.abs(getSlope()) * 100;
	}

	/**
	 *
	 * @return angle entre le nord et l'edge (sens descendant) (en degres)
	 */
	public double getSlopeAzimut() {
		if (orientationPente == -1.) {
			Coordinate c1 = this.geom.getCoordinates()[0];
			Coordinate c2 = this.geom.getCoordinates()[this.geom.getNumPoints() - 1];
			// l'ordre des coordonnees correspond a l'orientation de l'arc
			// "sommet haut vers sommet bas"
			double angleAxeX_rad = c1.z >= c2.z ? Angle.angle(c1, c2) : Angle
					.angle(c2, c1);
			// on considere que l'axe nord correspond a l'axe Y positif
			double angleAxeNord_rad = Angle.PI_OVER_2 - angleAxeX_rad;
			double angleAxeNord_deg = Angle.toDegrees(angleAxeNord_rad);
			// on renvoie toujours une valeur d'angle >= 0
			orientationPente = angleAxeNord_deg < 0.0 ? 360.0 + angleAxeNord_deg
					: angleAxeNord_deg;
		}
		return orientationPente;
	}

	/**
	 *
	 * @return vecteur de l'edge
	 */
	public Coordinate get3DVector() {
		if (pente == null) {
			Coordinate d = new Coordinate();
			Geometry g = this.geom;
			d.x = g.getCoordinates()[1].x - g.getCoordinates()[0].x;
			d.y = g.getCoordinates()[1].y - g.getCoordinates()[0].y;
			d.z = g.getCoordinates()[1].z - g.getCoordinates()[0].z;
			double norme = Math.sqrt(d.x * d.x + d.y * d.y + d.z * d.z);
			// normage du vecteur
			if (norme > 0) {
				d.x = d.x / norme;
				d.y = d.y / norme;
				d.z = d.z / norme;
			}
			pente = d;
		}
		return pente;
	}

}
