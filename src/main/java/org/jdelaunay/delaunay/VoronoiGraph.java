package org.jdelaunay.delaunay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Used to build a local Constrained Voronoi graph. You have the coice to build
 * the complete graph that can be found from a triangle, or only the partial graph that
 * link flat triangles together.
 * @author alexis
 */
class VoronoiGraph {

	//The list of nodes contained in this graph, sorted.
	private List<VoronoiNode> sortedNodes;
	//The VoronoiNode that has been used as a start point to build this graph.
	private VoronoiNode startNode;
	//The first not flat node that has been found
	private VoronoiNode notFlat;

	/**
	 * Construct a new VoronoiGraph, with a sole triangle as a base. It will be
	 * fillable later.
	 * @param base
	 * @throws DelaunayError
	 */
	public VoronoiGraph(DTriangle base) throws DelaunayError{
		startNode = new VoronoiNode(base);
		sortedNodes = new ArrayList<VoronoiNode>();
		addNode(startNode);
		notFlat = null;
	}

	/**
	 * Get the nodes in a sorted list.
	 * @return
	 */
	public List<VoronoiNode> getSortedNodes() {
		return sortedNodes;
	}

	/**
	 * Get the node that is considered to be the start node.
	 * @return
	 */
	public VoronoiNode getStartNode() {
		return startNode;
	}

	/**
	 * Return the first not-flat triangle encountered during the triangulation.
	 * @return
	 */
	public VoronoiNode getNotFlat() {
		return notFlat;
	}

	/**
	 * Change this VoronoiGraph start node with startNode
	 * @param startNode
	 */
	public void setStartNode(VoronoiNode startNode) {
		this.startNode = startNode;
	}

	/**
	 * Change this VoronoiGraph start node, using root to create a new
	 * VoronoiNode.
	 * @param root
	 * @throws DelaunayError
	 */
	public void setStartNode(DTriangle root) throws DelaunayError{
		setStartNode(new VoronoiNode(root));
	}

	/**
	 * Add a node to the SortedNodes set.
	 * @param vn
	 */
	private void addNode(VoronoiNode vn){
		sortedNodes.add(vn);
	}

	/**
	 * Get the list of points that should be added to the mesh.
	 * @return
	 */
	public List<DPoint> getSkeletonPoints() {
		List<DPoint> ret = new ArrayList<DPoint>();
		for(VoronoiNode vn : sortedNodes){
			ret.add(vn.getLocation());
		}
		return ret;
	}

	/**
	 * Fill the graph until the first not flat triangle is found.
	 * @throws DelaunayError
	 */
	public void fillUntilNotFlatFound() throws DelaunayError {
		if(startNode.getParent().isFlatSlope()){
			processNeighbours(startNode);
		}
	}

	/**
	 * Compute the graph recursively.
	 * @param vn
	 * @throws DelaunayError
	 */
	private void processNeighbours(VoronoiNode vn) throws DelaunayError {
		vn.getParent().setSeenForFlatRemoval(true);
		List<VoronoiNode> neighbours = vn.getNeighbourNodes();
		List<VoronoiNode> toBeTreated = new ArrayList<VoronoiNode>();
		int index;
		for(VoronoiNode neigh : neighbours){
			//We don't want to create duplicate nodes.Consequently,
			//We make a search and replace the unwanted duplicates.
			index = Collections.binarySearch(sortedNodes, neigh);
			if(index >=0){
				vn.replaceNode(sortedNodes.get(index));
			} else {
				sortedNodes.add(-index-1, neigh);
				if(neigh.getParent().isFlatSlope()){
					toBeTreated.add(neigh);
				} else if(notFlat == null){
					notFlat = neigh;
				}
			}
		}
		for(VoronoiNode treat : toBeTreated){
			//we only process the nodes that were not already in the list,
			//and so either treated, either referenced to be treat in the
			//stack.
			processNeighbours(treat);
		}

	}

	/**
	 * If possible, assign a meaningful Z value to each point of the generated part
	 * of the skeleton.
	 * @throws DelaunayError
	 */
	//The method used here is presented by Dakowicz and Gold in "Extracting meaningful
	//slopes from terrain contours".
	public void assignZValues() throws DelaunayError {
		if(notFlat != null){
			//We need the radius and the Z value of the center of the
			//first non-flat triangle we found.
			double radiusNF = notFlat.getParent().getRadius();
			double zCenter = notFlat.getParent().getCircumCenter().z;
			double zMin = notFlat.getParent().getPoints().get(0).getZ();
			double zMax = notFlat.getParent().getPoints().get(0).getZ();
			for(int i=1; i<DTriangle.PT_NB; i++){
				zMin = zMin < notFlat.getParent().getPoints().get(i).getZ() ? zMin :
						notFlat.getParent().getPoints().get(i).getZ();
				zMax = zMax < notFlat.getParent().getPoints().get(i).getZ() ?
						notFlat.getParent().getPoints().get(i).getZ() : zMax;
			}
			//We can process each node iteratively
			for(VoronoiNode vn : sortedNodes){
				if(vn.getParent().isFlatSlope()){
					setZValue(radiusNF, zCenter, vn, zMin, zMax);
				}
			}
		}
	}

	/**
	 * Compute and assign a Z value to the voronoi node vn, given the radius of
	 * the triangle used for comparison, and the height of its circumcenter.
	 * @param radius
	 * @param center
	 * @param vn
	 * @throws DelaunayError
	 */
	private void setZValue(double radius, double cHeight, VoronoiNode vn, double zMin, double zMax) throws DelaunayError {
		DPoint location = vn.getLocation();
		double vnRadius = 0;
		if(vn.getLocation().equals(new DPoint(vn.getParent().getCircumCenter()))){
			vnRadius = vn.getParent().getRadius();
		} else {
			List<Double> dists = new ArrayList<Double>();
			for(int i=0; i<DTriangle.PT_NB; i++){
				dists.add(location.squareDistance(vn.getParent().getPoint(i)));
			}
			vnRadius = Collections.min(dists);
		}
		double flatHeight = vn.getParent().getPoint(0).getZ();
		double computedZ = (vnRadius/radius) * cHeight + (1-vnRadius/radius) * flatHeight;
		if(computedZ < zMin){
			computedZ = zMin;
		} else if(computedZ > zMax){
			computedZ = zMax;
		}
		location.setZ(computedZ);
	}
}
