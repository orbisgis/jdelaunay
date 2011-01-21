package org.jdelaunay.delaunay;

/**
 * Test methods that are common to the children of Element
 * @author alexis
 */
public class TestElement extends BaseUtility {

	public void testProperty() throws DelaunayError{
		Point pt = new Point();
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

}
