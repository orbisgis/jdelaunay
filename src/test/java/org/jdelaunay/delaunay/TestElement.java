package org.jdelaunay.delaunay;

/**
 * Test methods that are common to the children of Element
 * @author alexis
 */
public class TestElement extends BaseUtility {

	public void testProperty() throws DelaunayError{
		DPoint pt = new DPoint();
		pt.addProperty(4);
		assertTrue(pt.hasProperty(4));
		pt.addProperty(6);
		assertTrue(pt.hasProperty(4));
		assertTrue(pt.hasProperty(2));
		assertFalse(pt.hasProperty(8));
		pt.setProperty(12);
		assertFalse(pt.hasProperty(2));
		assertTrue(pt.hasProperty(4));
		pt.setProperty(28);
		assertTrue(pt.hasProperty(24));
		pt.removeProperties();
		assertTrue(pt.getProperty()==0);
	}

	public void testExternalGID() throws DelaunayError {
		DPoint pt = new DPoint (0,0,0);
		assertTrue(pt.getExternalGID() == -1);
		pt.setExternalGID(5);
		assertTrue(pt.getExternalGID() == 5);
	}

}
