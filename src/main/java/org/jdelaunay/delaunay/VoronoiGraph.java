/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
}
