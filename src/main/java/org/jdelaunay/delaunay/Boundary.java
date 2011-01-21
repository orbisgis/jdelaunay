package org.jdelaunay.delaunay;

/**
 * The boundary of the mesh. During the computation of the mesh, the boundary is
 * considered as a set of parts (BoundaryPart instance) that are sorted vertically.
 * When adding a point, we will check to which part of the boundary it can be linked,
 * by using the vertical sort that has been made before.
 * @author alexis
 */
public class Boundary {

}
