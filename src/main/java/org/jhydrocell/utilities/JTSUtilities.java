package org.jhydrocell.utilities;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Triangle;

public class JTSUtilities {

	/**
	 * Default constructor is kept private
	 */
	private JTSUtilities(){
	}

	/**
	 * Create a new Triangle with the coordinates in a clockwise direction.
	 * 
	 * @param c0
	 *            The first coordinate.
	 * @param c1
	 *            The second coordinate.
	 * @param c2
	 *            The third coordinate.
	 * @return The Triangle.
	 */
	public static Triangle createClockwiseTriangle(final Coordinate c0,
			final Coordinate c1, final Coordinate c2) {
		try {
			if (CGAlgorithms.computeOrientation(c0, c1, c2) == CGAlgorithms.CLOCKWISE) {
				return new Triangle(c0, c1, c2);
			} else {
				return new Triangle(c0, c2, c1);
			}
		} catch (IllegalStateException e) {
			throw e;
		}

	}
}
