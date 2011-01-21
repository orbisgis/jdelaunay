
package org.jdelaunay.delaunay;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides tests that run on the BoundaryPart class.
 * @author alexis
 */
public class TestBoundaryPart extends BaseUtility {

	/**
	 *
	 */
	public void testCanBeNext(){
		BoundaryPart bp1 = new BoundaryPart(new Edge(0,0,0,2,2,0));
		List<Edge> be = new ArrayList<Edge>();
		be.add(new Edge(0,0,0,1,4,0));
		bp1.setBoundaryEdges(be);
		BoundaryPart bp2 = new BoundaryPart(new Edge(1,4,0,2,2,0));
		assertTrue(bp1.canBeNext(bp2));
		bp2 = new BoundaryPart(new Edge(8,4,0,2,2,0));
		assertFalse(bp1.canBeNext(bp2));
		bp2 = new BoundaryPart(new Edge(0,0,0,2,2,0));
		assertFalse(bp1.canBeNext(bp2));
	}

}
