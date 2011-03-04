/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdelaunay.delaunay;

import java.util.ArrayList;
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
	VoronoiNode startNode;

	public VoronoiGraph(DTriangle base) throws DelaunayError{
		startNode = new VoronoiNode(base);
		sortedNodes = new ArrayList<VoronoiNode>();
		addNode(startNode);
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
	 * Add a node to the SortedNodes set.
	 * @param vn
	 */
	private void addNode(VoronoiNode vn){
		sortedNodes.add(vn);
	}

}
