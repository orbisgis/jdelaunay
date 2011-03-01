
package org.jdelaunay.delaunay;

import java.util.List;

/**
 *
 * @author alexis
 */
public class TestBoundaryBox extends BaseUtility {

	public void testGetPoints() throws DelaunayError{
		BoundaryBox bb = new BoundaryBox(0, 4, 0, 4, 7, 4);
		List<DPoint> list = bb.getPoints();
		assertTrue(list.contains(new DPoint(0,0,0)));
		assertTrue(list.contains(new DPoint(0,4,0)));
		assertTrue(list.contains(new DPoint(4,4,0)));
		assertTrue(list.contains(new DPoint(4,0,0)));
	}
}
