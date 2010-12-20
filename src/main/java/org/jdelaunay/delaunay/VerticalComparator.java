/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdelaunay.delaunay;

import java.io.Serializable;
import java.util.Comparator;
import org.apache.log4j.Logger;

/**
 * The VerticalComparator class will be used to perform sorting and searching operations
 * in vertically sorted lists. We can't use directly the Comparable interface in Edge,
 * as it is already used for the "left-right" method.
 * @author alexis
 */
public class VerticalComparator implements Comparator<Edge>, Serializable {

	private static Logger log = Logger.getLogger(VerticalComparator.class);

	//The absciss where we are going to make the comparison.
	private double abs;

	/**
	 * Vertical comparator constructor.
	 * @param x
	 */
	public VerticalComparator(double x){
		abs = x;
	}

	/**
	 * Set the absciss where we are going to work.
	 * @param x
	 */
	public final void setAbs(double x){
		abs = x;
	}

	/**
	 * get the absciss where we are currently working.
	 * @return
	 */
	public final double getAbs(){
		return abs;
	}

	/**
	 * This comparison method is a vertical sorting test :
	 * Sort two edges (this and edge, indeed), and sort them according to their intersection point
	 * with the line l of equation x=abs.
	 * if p1 (p2) is the intersection between l and the line defined by this (edge),
	 * this method returns :
	 *  * -1 if p1 < p2 or ( p1 == p2 and this is "under" edge)
	 *  * 0 if p1 == p2 and (this and edge are colinear)
	 *  * 1 if p1 > p2 or (p1 == p2 and edge is under this)
	 *
	 * In our case, we will return -2 if one of the edge is vertical, and has absciss
	 * other than abs.
	 * @param edge1
	 * @param edge2
	 * @return
	 */
	@Override
	public final int compare(Edge edge1, Edge edge2) {
		int c;
		Point pEdge1 = null;
		Point pEdge2 = null;
		try{
			pEdge1 = edge1.getPointFromItsX(abs);
			pEdge2 = edge2.getPointFromItsX(abs);
		} catch (DelaunayError e){
			log.error("Problem while processing the points from their absciss !");
		}
		if (pEdge1 == null || pEdge2 == null) {
			c=-2;
		} else {
			c = pEdge1.compareTo2D(pEdge2);
			if (c == 0) {
				if(edge1.isVertical()){
					c = edge1.getPointRight().compareTo2D(edge2.getPointRight());
				} else if(edge2.isVertical()){
					c = edge2.getPointRight().compareTo2D(edge1.getPointRight());
				} else {
					double deltaXT = edge1.getPointRight().getX()-edge1.getPointLeft().getX();
					double deltaYT = edge1.getPointRight().getY()-edge1.getPointLeft().getY();
					double deltaXO = edge2.getPointRight().getX()-edge2.getPointLeft().getX();
					double deltaYO = edge2.getPointRight().getY()-edge2.getPointLeft().getY();
					double cT = deltaYT / deltaXT;
					double cO = deltaYO / deltaXO;
					if(-Tools.EPSILON < cT - cO && cT - cO < Tools.EPSILON){
						c = edge1.getPointRight().compareTo2D(edge2.getPointRight());
						if(c==0){
							c = edge1.getPointLeft().compareTo2D(edge2.getPointLeft());
						}
					} else if(cT < cO){
						c = -1;
					} else {
						c=1;
					}
				}
			}
		}
		return c;
	}

}
