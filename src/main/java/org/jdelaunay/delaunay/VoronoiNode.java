package org.jdelaunay.delaunay;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that reprensent a node in the voronoi graph, that is constructed in order to remove
 * the flat triangles.
 * The points of the voronoi graph are the circumcenters of the triangles.
 * @author alexis
 */
class VoronoiNode {

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
		location = new DPoint(parent.getCircumCenter());
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
		location = new DPoint(parent.getCircumCenter());
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

}
