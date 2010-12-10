/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdelaunay.delaunay;

import java.util.List;

/**
 *
 * @author alexis
 */
public class TestBoundaryBox extends BaseUtility {

	public void testGetPoints() throws DelaunayError{
		BoundaryBox bb = new BoundaryBox(0, 4, 0, 4, 7, 4);
		List<Point> list = bb.getPoints();
		assertTrue(list.contains(new Point(0,0,0)));
		assertTrue(list.contains(new Point(0,4,0)));
		assertTrue(list.contains(new Point(4,4,0)));
		assertTrue(list.contains(new Point(4,0,0)));
	}
}
