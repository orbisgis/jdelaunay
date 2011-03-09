package org.jdelaunay.delaunay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class that reprensent a node in the voronoi graph, that is constructed in order to remove
 * the flat triangles.
 * The points of the voronoi graph are the circumcenters of the triangles.
 * @author alexis
 */
class VoronoiNode implements Comparable<VoronoiNode>{

	private static final int HASHBASE = 7;
	private static final int HASHMULT = 71;

	//The nodes that are linked to this.
	private List<VoronoiNode> linkedNodes;
	//The triangle that has generated this node
	private DTriangle parent;
	//The point where the node is actually located.
	private DPoint location;

	/**
	 * Create a new VoronoiNode, given the Dtriangle parent that will determine
	 * its position.
	 * @param par
	 */
	public VoronoiNode(DTriangle par) throws DelaunayError{
		linkedNodes = new ArrayList<VoronoiNode>();
		parent = par;
		computeLocation();
	}

	/**
	 * Get the list of nodes linked to this.
	 * @return
	 */
	public List<VoronoiNode> getLinkedNodes() {
		return linkedNodes;
	}

	/**
	 * Get the position of this node.
	 * @return
	 */
	public DPoint getLocation() {
		return location;
	}

	/**
	 * Get the triangle that defines to this node.
	 * @return
	 */
	public DTriangle getParent() {
		return parent;
	}

	/**
	 * Replace the current parent with par, given in argument.
	 * Be careful when using this method ! It will remove all the
	 * references to other nodes in this node... but potential references
	 * to this node in other nodes will not be updated !
	 * @param parent
	 * @throws DelaunayError
	 */
	public void setParent(DTriangle par) throws DelaunayError {
		if(par == null){
			throw new DelaunayError("A VoronoiNode must have a parent !");
		}
		this.parent = par;
		linkedNodes = new ArrayList<VoronoiNode>();
		computeLocation();
	}

	/**
	 * Get the VoronoiNodes associated to the triangles that are neighbour
	 * to the parent of this.
	 * This can have 0 to 3 neighbours. It will directly depend on the nature of
	 * the edges that define this' parent. Indeed, the Voronoi Graph we will
	 * build **won't cross the constrained edges of the triangulation**.
	 * Consequently, the number of neighbours will be three minus the number of
	 * edges of this triangle that are constrained.
	 * The call to this method will fill or update the content of the linkedNodes
	 * list.
	 * @return
	 */
	public List<VoronoiNode> getNeighbourNodes() throws DelaunayError {
		linkedNodes = new ArrayList<VoronoiNode>();
		VoronoiNode vn ;
		DEdge cur;
		DTriangle curT;
		DEdge[] parEdges = parent.getEdges();
		for(int i=0; i<DTriangle.PT_NB; i++){
			cur = parEdges[i];
			if(!cur.isLocked()){
				curT = cur.getOtherTriangle(parent);
				if(curT != null){
					vn = new VoronoiNode(curT);
					linkedNodes.add(vn);
				}
			}
		}
		return linkedNodes;
	}

	/**
	 * Two VoronoiNodes are equal if and only if their defining triangles are.
	 * @param obj
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof VoronoiNode){
			return parent.equals(((VoronoiNode) obj).getParent());
		} else {
			return false;
		}
	}

	/**
	 * Compute a hashcode for this triangle node.
	 * @return
	 */
	@Override
	public int hashCode() {
		int hash = HASHBASE;
		hash = HASHMULT * hash + (this.parent != null ? this.parent.hashCode() : 0);
		return hash;
	}

	/**
	 * Performs a comparison between two VoronoiNodes.
	 * We first make a comparison between the location of each node. If their locations
	 * are equal (i.e. the parent triangles share the same circumcenter), we
	 * compare the parent triangles.
	 * @param o
	 * @return
	 */
	@Override
	public int compareTo(VoronoiNode o) {
		int c = location.compareTo(o.getLocation());
		if(c==0){
			return parent.compareTo(o.getParent());
		} else {
			return c;
		}
	}

	/**
	 * Compute the location (in the 2d space) of this node. It will be the Circumcenter
	 * if it is visible from every points in this triangle, the barycenter otherwise.
	 *
	 * In the current case, we must consider edges of the boundary as if they were
	 * constrained edges. Indeed we don't want to add points ouside of the
	 * envelope of the current boundary.
	 * @throws DelaunayError
	 */
	private void computeLocation() throws DelaunayError {
		DPoint defloc = new DPoint(parent.getCircumCenter());
		DEdge[] edges = parent.getEdges();
		DPoint last;
		//To be sure that the current edge is connected to two triangles, as we don't want to
		//add points outside the current mesh.
		boolean twoAssociatedTriangles;
		for(int i =0; i<DTriangle.PT_NB; i++){
			last = parent.getAlterPoint(edges[i]);
			twoAssociatedTriangles = edges[i].getRight() != null && edges[i].getLeft()!=null;
			//If the circumcenter is right to one edge where the last point is
			//on the left (or the contrary) we must make further process.
			if(!edges[i].isRight(last)==edges[i].isRight(defloc)){
				if((edges[i].isLocked() || !twoAssociatedTriangles)){
					//If the center is on the other side of the constraint, we stop our progress.
					location = parent.getBarycenter();
					return;
				} else {
					//We must check we don't intersect a constraint.
					defloc = checkLocationValidity(defloc, new DEdge(defloc, parent.getAlterPoint(edges[i])),edges[i], parent);
					if(defloc==null){
						location = parent.getBarycenter();
						return;
					}
				}
			}
		}
		location = defloc;
	}

	/**
	 * This method tries to find an intersection between ray (which is an edge
	 * between the triangle and its circumcenter) and a constraint edge.
	 * @param center
	 * @param ray
	 * @param line
	 * @param prev
	 * @return
	 * @throws DelaunayError
	 */
	private DPoint checkLocationValidity(final DPoint center, DEdge ray, DEdge line, DTriangle prev) throws DelaunayError {
		DTriangle next = line.getOtherTriangle(prev);
		if(next==null){
			return null;
		}
		boolean stop = true;
		DEdge[] edges = next.getEdges();
		DEdge nextEd = null;
		for(int i=0; i<DTriangle.PT_NB; i++){
			if(!edges[i].equals(line) ){
				int inter =edges[i].intersects(ray);
				if(inter == DEdge.INTERSECT || inter ==DEdge.SHARE_EXTREMITY || inter == DEdge.SHARE_EDGE_PART){
					stop = false;
					nextEd = edges[i];
					if(nextEd.isLocked()){
						return null;
					}
				}
			}
		}
		if(stop){
			return center;
		} else {
			return checkLocationValidity(center, ray, nextEd, next);
		}
	}

	/**
	 * Replace the node that is equal to alreadySeen, if any, with alreadySeen.
	 * If none of the edges is equal to alraedySeen, an exception is thrown.
	 * Note that this method is not public API, and is designed to be used
	 * by VoronoiGraph only.
	 * @param alreadySeen
	 * @throws DelaunayError
	 */
	void replaceNode(VoronoiNode alreadySeen) throws DelaunayError {
		int index = linkedNodes.indexOf(alreadySeen);
		if(index<0){
			throw new DelaunayError("Wait... the node given in argument is supposed to be a neighbour of this one!");
		} else {
			linkedNodes.set(index, alreadySeen);
		}
	}

	/**
	 * Gets the radius of this VoronoiNode. It will be the radius of the circumcenter
	 * if the location of this is the circumcenter of its parent triangle, or
	 * the minimum distance between the barycenter and the points of the triangle
	 * otherwise.
	 * @return
	 * @throws DelaunayError
	 */
	double getRadius() throws DelaunayError {
		if(location.equals(new DPoint(parent.getCircumCenter()))){
			return parent.getRadius();
		} else {
			List<Double> dists = new ArrayList<Double>();
			for(int i=0; i<DTriangle.PT_NB; i++){
				dists.add(location.squareDistance(getParent().getPoint(i)));
			}
			return Collections.min(dists);
		}
	}

}
