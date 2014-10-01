/**
 *
 * jDelaunay is a library dedicated to the processing of Delaunay and constrained
 * Delaunay triangulations from PSLG inputs.
 *
 * This library is developed at French IRSTV institute as part of the AvuPur and Eval-PDU project,
 * funded by the French Agence Nationale de la Recherche (ANR) under contract
 * ANR-07-VULN-01 and ANR-08-VILL-0005-01 .
 *
 * jDelaunay is distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2010-2012 IRSTV FR CNRS 2488
 *
 * jDelaunay is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * jDelaunay is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * jDelaunay. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.jdelaunay.delaunay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jdelaunay.delaunay.error.DelaunayError;
import org.jdelaunay.delaunay.geometries.DEdge;
import org.jdelaunay.delaunay.geometries.DPoint;
import org.jdelaunay.delaunay.geometries.DTriangle;
import org.jdelaunay.delaunay.tools.Tools;

/**
 * Used to build a local Constrained Voronoi graph. You have the choice to build
 * the complete graph that can be found from a triangle, or only the partial graph that
 * link flat triangles together.
 * @author Alexis Guéganno
 */
class VoronoiGraph {

	/**
	 * The list of nodes contained in this graph, sorted.
	 */
	private List<VoronoiNode> sortedNodes;
	/**
	 * The VoronoiNode that has been used as a start point to build this graph.
	 */
	private VoronoiNode startNode;
	/**
	 * The first not flat node that has been found
	 */
	private VoronoiNode notFlat;
	/**
	 * The last flat node that has been found
	 */
	private VoronoiNode lastFlat;
	/**
	 * True if this graph can be used to insert points in the mesh.
	 */
	private boolean useful = false;

	/**
	 * Build a new VoronoiGraph, with a sole triangle as a base. It will be
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
	 * returns true if the location's values have been changed by a call to
	 * assignZValues.
	 * @return
	 */
	public boolean isUseful() {
		return useful;
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
					lastFlat = vn;
				}
			}
		}
		for(VoronoiNode treat : toBeTreated){
			//we only process the nodes that were not already in the list,
			//and so either treated, either referenced to be treat in the
			//stack.
                        if(!treat.getParent().isSeenForFlatRemoval()){
                                processNeighbours(treat);
                        }
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
		useful = false;
		if(notFlat == null){
			useful = assignZValuesWithoutNotFlat();
		} else {
			useful = assignZValuesWithNotFlat();
		}
	}

	/**
	 * We try to remove flat triangles even when we can't connect the partial voronoi
	 * graph to a non-flat triangle. We are going to look at the triangles that
	 * are behind the curve lines to detemine if we are on a pit or on the top
	 * of a hill.
	 * @return true if the location points are useful after a call to this method.
	 *		They are useful if their Z has been set.
	 * @throws DelaunayError
	 */
	private boolean assignZValuesWithoutNotFlat() throws DelaunayError {
		//true if the flat area is upper than its sides.
		boolean upper = false;
		boolean set = false;
		double maxRadius = 0.0;
		double currentRad;
		double height = sortedNodes.get(0).getParent().getPoint(0).getZ();
		double delta;
		DTriangle otherT ;
		DEdge[]  edges;
		double extZ;
		for(VoronoiNode vn : sortedNodes){
			currentRad = vn.getRadius();
			maxRadius = maxRadius < currentRad ? currentRad : maxRadius;
			edges = vn.getParent().getEdges();
			for(int i = 0; i<DTriangle.PT_NB; i++){
				if(edges[i].isLocked()){
					otherT = edges[i].getOtherTriangle(vn.getParent());
					if(otherT != null){
						extZ = otherT.getOppositePoint(edges[i]).getZ();
						if(!set && Math.abs(extZ - height)>Tools.EPSILON){
							set = true;
							upper = height > extZ;
						} else if(upper && !(height-extZ > -Tools.EPSILON) || !upper && !(extZ -height > -Tools.EPSILON)){
							return false;
						}
					}
				}
			}
		}
		if(!set){
			return false;
		}
		//And here we assign the values. If we're here, then we are in a manageable
		//situation
		for(VoronoiNode vn : sortedNodes){
			currentRad = vn.getRadius();
			delta = currentRad/maxRadius;
			if(upper){
				vn.getLocation().setZ(height + delta);
			} else {
				vn.getLocation().setZ(height - delta);
			}
		}
		return true;
	}

	/**
	 * Get the max depth of the graph.
	 * @return
	 * @throws DelaunayError
	 */
	public final int getMaxDepth() throws DelaunayError{
		for(VoronoiNode vn : sortedNodes){
			vn.setSeen(false);
		}
		return lastFlat != null ? getMaxLength(lastFlat) : -1;
	}

	/**
	 * Compute recursively the depth of the graph.
	 * @param vn
	 * @return
	 * @throws DelaunayError
	 */
	private int getMaxLength(VoronoiNode vn) throws DelaunayError {
		int length = 0;
		vn.setSeen(true);
		for(VoronoiNode voro : vn.getLinkedNodes()){
			if(!voro.isSeen()){
				length = Math.max(length, getMaxLength(voro));
			}
		}
		return length+1;
	}

	/**
	 * Method used to compute the Z of the locations when a non flat triangle
	 * has been connected to the graph
	 * @return true, as the points are always usable after a call to this method.
	 * @throws DelaunayError
	 */
	private boolean assignZValuesWithNotFlat() throws DelaunayError {
		//We need the radius and the Z value of the center of the
		//first non-flat triangle we found.
		double flatZ = lastFlat.getParent().getPoint(0).getZ();
		DTriangle dt = notFlat.getParent();
		double zNf=Double.NaN;
		for(int i=0; i<DTriangle.PT_NB;i++){
			if(Math.abs(dt.getPoint(i).getZ()-flatZ)>Tools.EPSILON){
				zNf = dt.getPoint(i).getZ();
				break;
			}
		}
		if(Double.isNaN(zNf)){
			throw new DelaunayError("Well... this triangle was supposed not to be flat. U mad ?");
		}
		double flatHeight = lastFlat.getParent().getPoint(0).getZ();
		int length = getMaxLength(lastFlat)+1;
		double delta = (zNf - flatHeight)/(length+1);
		notFlat.getLocation().setZ(zNf-delta);
		//We can process each node iteratively
		for(VoronoiNode vn : sortedNodes){
			vn.setSeen(false);
		}
		assignValues(lastFlat, delta,zNf-delta,flatHeight);
		return true;
	}

	/**
	 * Compute the z values for the node locations recursively.
	 * @param vn
	 * @param delta
	 * @param prevAlt
	 * @param flatHeight
	 * @throws DelaunayError
	 */
	private void assignValues(final VoronoiNode vn, final double delta, final double prevAlt, final double flatHeight) throws DelaunayError{
		if(!vn.getParent().isFlatSlope()){
			return;
		}
		double alt = prevAlt - delta;
		if((prevAlt > flatHeight && alt < flatHeight)||(prevAlt < flatHeight && alt > flatHeight)){
			final double deltaBis = delta/4;
			assignValues(vn, deltaBis, prevAlt, flatHeight);
		} else {
			vn.setSeen(true);
			vn.getLocation().setZ(alt);
			for(VoronoiNode vor : vn.getLinkedNodes()){
				if(!vor.isSeen()){
					assignValues(vor, delta, alt, flatHeight);
				}
			}
		}
	}

}
