/*******************************************************************************
 * Copyright (c) 2013 Max Göbel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Max Göbel - initial API and implementation
 ******************************************************************************/
package at.tuwien.prip.model.graph;

import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import at.tuwien.prip.common.datastructures.HashMap2;
import at.tuwien.prip.common.datastructures.HashMapList;
import at.tuwien.prip.common.datastructures.Map2;
import at.tuwien.prip.common.datastructures.MapList;
import at.tuwien.prip.common.datastructures.StatMap;
import at.tuwien.prip.common.utils.PeekIterator;
import at.tuwien.prip.common.utils.SimpleTimer;
import at.tuwien.prip.model.document.segments.GenericSegment;
import at.tuwien.prip.model.document.segments.RectSegment;
import at.tuwien.prip.model.graph.comparators.HeightComparator;
import at.tuwien.prip.model.graph.comparators.WidthComparator;
import at.tuwien.prip.model.utils.Orientation;

/**
 * AdjacencyMatrix.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Nov 24, 2011
 */
public class AdjacencyMatrix<T extends GenericSegment>
{
	public List<T> nodes;

	public Cell<T>[][] cells;

	public Rectangle [] rows;

	public Rectangle [] cols;

	public int width;

	public int height;

	/**
	 * Constructor.
	 * 
	 * @param nodes
	 */
	public AdjacencyMatrix(List<T> nodes) {
		this(nodes, false);
	}

	/**
	 * Constructor.
	 * 
	 * @param nodes
	 */
	@SuppressWarnings("unchecked")
	public AdjacencyMatrix(List<T> nodes, boolean exact) 
	{
		this.nodes = nodes;

		List<Integer> xCoord = new ArrayList<Integer>();
		List<Integer> yCoord = new ArrayList<Integer>();

		//remember start to end positions
		Map<Integer,Integer> xxMap = new HashMap<Integer,Integer>();
		Map<Integer,Integer> yyMap = new HashMap<Integer,Integer>();

		Map2<Integer,Integer,T> xyMap = 
			new HashMap2<Integer,Integer,T>(null,null,null);

		int maxY = 0;
		int maxX = 0;

		//generate the coordinates of the matrix grid
		for (T n : nodes) 
		{
			if (n.getY2()>maxY) {
				maxY = (int) n.getY2();
			}
			if (n.getX2()>maxX) {
				maxX = (int) n.getX2();
			} 
			//the x coordinate
			int x1 = (int) n.getX1();
			int x2 = (int) n.getX2();

			if (!exact) {
				if (xCoord.contains(x1-1)) {
					x1-=1;
				}
				else if (xCoord.contains(x1+1)) {
					x1+=1;
				}
				else if (xCoord.contains(x1-2)) {
					x1-=2;
				}
				else if (xCoord.contains(x1+2)) {
					x1+=2;
				}
			}
			if (!xCoord.contains(x1)) {
				Object endX = xxMap.get(x1);
				if (endX==null || x2>(Integer)endX) {
					xxMap.put(x1, x2);
				}
				xCoord.add(x1);
			}

			if (exact) {
				//				if (xCoord.contains(x2-1)) {
				//					x2-=1;
				//				}
				//				else if (xCoord.contains(x2+1)) {
				//					x2+=1;
				//				}
				//				else if (xCoord.contains(x2-2)) {
				//					x2-=2;
				//				}
				//				else if (xCoord.contains(x2+2)) {
				//					x2+=2;
				//				}
				if (!xCoord.contains(x2)) {
					xCoord.add(x2);
				}
			}

			//same for y coordinate
			int y1 = (int) n.getY1();
			int y2 = (int) n.getY2();

			if (!exact) {
				if (yCoord.contains(y1-1)) {
					y1-=1;
				}
				else if (yCoord.contains(y1+1)) {
					y1+=1;
				}
				else if (yCoord.contains(y1-2)) {
					y1-=2;
				}
				else if (yCoord.contains(y1+2)) {
					y1+=2;
				}
			}
			if (!yCoord.contains(y1)) {
				yCoord.add(y1);
				Object endY = yyMap.get(y1);
				if (endY==null || y2>(Integer)endY) {
					yyMap.put(y1, y2);
				}
			}

			if (exact) {
				//				if (xCoord.contains(y2-1)) {
				//					y2-=1;
				//				}
				//				else if (xCoord.contains(y2+1)) {
				//					y2+=1;
				//				}
				//				else if (xCoord.contains(y2-2)) {
				//					y2-=2;
				//				}
				//				else if (xCoord.contains(y2+2)) {
				//					y2+=2;
				//				}
				if (!yCoord.contains(y2)) {
					yCoord.add(y2);
				}
			}

			xyMap.put(x1, y1, n);
		}

		//sort the coordinate arrays
		Collections.sort(yCoord);
		Collections.sort(xCoord);

		rows = new Rectangle[yCoord.size()];
		cols = new Rectangle[xCoord.size()];

		//construct the matrix
		cells = new Cell[yCoord.size()][xCoord.size()];
		for (int r=0; r<yCoord.size(); r++) 
		{ //rows
			for (int c=0; c<xCoord.size(); c++) 
			{ //cols
				T n = xyMap.get(xCoord.get(c), yCoord.get(r));
				int x1 = xCoord.get(c);
				int x2 = -1;
				if (c<xCoord.size()-1) {
					x2 = xCoord.get(c+1);
				} else {
					x2 = maxX;//(int) n.getSegX2();//xxMap.get(xCoord.get(c));
				}
				int y1 = yCoord.get(r);
				int y2 = -1;
				if (r<yCoord.size()-1) {
					y2 = yCoord.get(r+1);
				} else {
					y2 = maxY;//(int) n.getSegY2(); //yyMap.get(yCoord.get(r));
				}

				Rectangle bounds = new Rectangle(x1, y1, x2-x1, y2-y1);
				if (n==null) {
					cells[r][c] = new Cell<T>(bounds);
				} else {
					cells[r][c] = new Cell<T>(n, bounds);
				}
			}
		}

		/* set spans for cells */
		for (int row=0; row<cells.length; row++) 
		{
			for (int col=0; col<cells[row].length; col++) 
			{
				T node = cells[row][col].node;
				int vspan=1, hspan=1;

				if (node!=null)
				{
					int height = (int) (node.getY2() - node.getY1());

					for (int r=row; height>0 && r<cells.length; r++) 
					{
						int width = (int) (node.getX2() - node.getX1());
						for (int c=col; width>0 && c<cells[r].length; c++)
						{
							Cell<T> cell = cells[r][c];
							if (c==col) {
								height -= cell.bounds.height; //once per row
							}
							width -= cell.bounds.width;
							cell.belongsTo = cells[row][col];
							hspan++;
						}
						vspan++;
					}
				}
				cells[row][col].hspan = hspan;
				cells[row][col].vspan = vspan;
			}
		}

		width = maxX;
		height = maxY;
	}

	/**
	 * Convert this matrix to a full adjacency graph.
	 * Recomputes all edge relations.
	 * 
	 * @return
	 */
	public AdjacencyGraph<T> toAdjacencyGraph (boolean edges) 
	{
		AdjacencyGraph<T> result = new AdjacencyGraph<T>();
		result.am = this;

		SimpleTimer timer = new SimpleTimer();
		timer.startTask(0);

		/* set nodes */
		result.vert = nodes;
		result.horiz = nodes;

		result.edges.clear();

		List<AdjacencyEdge<T>> horizontalEdges = new ArrayList<AdjacencyEdge<T>>();
		List<AdjacencyEdge<T>> verticalEdges = new ArrayList<AdjacencyEdge<T>>();

		if (edges)
		{
			/* recompute edges */ 
			for (int row=0; row<cells.length; row++) 
			{
				for (int col=0; col<cells[row].length; col++)
				{
					Cell<T> c = cells[row][col];

					T cNode = null;
					if (c.node!=null) 
					{
						cNode = c.node;
					}
					else if (c.node==null && c.belongsTo!=null) 
					{
						cNode = c.belongsTo.node;
					} 
					else 
					{
						continue;
					}

					if (cNode==null) {
						continue;
					}

					/* search all east/west neighbors */
					List<T> neighsRight = new ArrayList<T>();

					//go right
					for (int m=1; m<cells[row].length-col; m++) 
					{
						Cell<T> right = cells[row][col+m];
						if (right.node!=null)// || (right.belongsTo.node!=cNode && right.belongsTo!=null)) 
						{
							//found right neighbor for cell c
							T node = right.node;
							//						if (node==null) 
							//						{
							//							node = right.belongsTo.node;
							//						}
							if (node!=null) 
							{
								if (!node.getBoundingRectangle().intersects(cNode.getBoundingRectangle()))
								{
									if (!node.equals(cNode)) 
									{
										if (node.getX1() >=  cNode.getX2()) {
											neighsRight.add(node);
											break;
										} 
									}
								}
								//							else {
								//								System.err.println("wrong neighbour");
								//							}
							}
							break;
						}
					}

					/* search all north/south neighbors */
					List<T> neighsBot = new ArrayList<T>();

					//go down
					for (int m=1; m<cells.length-row; m++) 
					{
						Cell<T> down = cells[row+m][col];
						if (down.node!=null || down.belongsTo!=null) 
						{
							//found north neighbor for cell c
							T node = down.node;
							if (node==null) 
							{
								node = down.belongsTo.node;
							}
							if (!node.getBoundingRectangle().intersects(cNode.getBoundingRectangle()))
							{
								if (!node.equals(cNode)) {
									neighsBot.add(node);
									break;
								}
							}
							break;
						}
					}

					if (neighsRight.size()>1||neighsBot.size()>1)
					{
						System.err.println();
					}
					for (T nr : neighsRight)
					{
						AdjacencyEdge<T> edge = 
							new AdjacencyEdge<T>(
									cNode, 
									nr, 
									AdjacencyEdge.REL_RIGHT);
						horizontalEdges.add(edge);

					}

					for (T nb : neighsBot)
					{
						AdjacencyEdge<T> edge = 
							new AdjacencyEdge<T>(
									nb,
									cNode,
									AdjacencyEdge.REL_ABOVE);
						verticalEdges.add(edge);
					}				
				}
			}
			result.edges.addAll(horizontalEdges);
			for (AdjacencyEdge<T> vEdge : verticalEdges)
			{
				if (checkVerticalAgainstAllHorizontal(vEdge,horizontalEdges))
				{
					result.edges.add(vEdge);
				}
			}
		}

		timer.stopTask(0);
		//		ErrorDump.debug(this, "Edges calculated in " + timer.getTimeMillis(0) + "ms");
		return result;
	}

	/**
	 * 
	 * @param edge
	 * @param hEdges
	 * @return
	 */
	private boolean checkVerticalAgainstAllHorizontal (
			AdjacencyEdge<T> edge, 
			List<AdjacencyEdge<T>> hEdges) 
	{
		for (AdjacencyEdge<T> e : hEdges) 
		{
			if (lineIntersect(e, edge)) 
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @param edge1
	 * @param edge2
	 * @return
	 */
	public boolean lineIntersect(AdjacencyEdge<T> edge1, AdjacencyEdge<T> edge2)
	{
		Line2D line1 = edge1.toLine2D();
		Line2D line2 = edge2.toLine2D();
		return line1.intersectsLine(line2);
	}

	/**
	 * 
	 * @param edge1
	 * @param edge2
	 * @return
	 */
	public boolean boxIntersect(AdjacencyEdge<T> edge1, AdjacencyEdge<T> edge2)
	{
		Rectangle2D rectangle1 = edge1.toBox();
		Rectangle2D rectangle2 = edge2.toBox();
		return rectangle1.intersects(rectangle2);
	}

	public AM_Iterator iterator () {
		return new AM_Iterator();
	}

	public AM_Iterator iterator (T start) {
		return new AM_Iterator(start); 
	}

	public AM_Iterator iterator (T start, int rightStop) {
		return new AM_Iterator(start, rightStop); 
	}

	public List<Integer> getSalientColumns () {
		StatMap<Integer> colStatMap = new StatMap<Integer>();
		for (int row=0; row<cells.length; row++) {
			for (int col=0; col<cells[row].length; col++) {
				if (cells[row][col].node!=null) {
					colStatMap.increment(col);
				}
			}
		}
		return colStatMap.getSignificant(1);//0.1 //getByFrequency(20);
		//getSignificant(0.1);//
	}

	public List<Integer> getNonNullColumns () {
		StatMap<Integer> colStatMap = new StatMap<Integer>();
		for (int row=0; row<cells.length; row++) {
			for (int col=0; col<cells[row].length; col++) {
				if (cells[row][col].node!=null) {
					colStatMap.increment(col);
				}
			}
		}
		return colStatMap.getNonNullValue();//getByFrequency();
	}

	public List<Integer> getSalientRows () {
		List<Integer> result = new ArrayList<Integer>();
		StatMap<Integer> rowStatMap = new StatMap<Integer>();
		for (int row=0; row<cells.length; row++) {
			for (int col=0; col<cells[row].length; col++) {
				if (cells[row][col]!=null) {
					rowStatMap.increment(col);
				}
			}
		}
		return result;
	}

	/**
	 * Find and return the largest n of both vertical and horizontal 
	 * whitespace gaps in the form of rectangular segments.
	 * 
	 * @param n size of returned boxes, ordered by importance
	 * @param orientation HORIZONTAL, VERTICAL, or BOTH
	 * @return
	 */
	public List<RectSegment> getBoxesOfWhitespace (int n, Orientation orientation) 
	{
		//get by largest volume
		MapList<Integer, RectSegment> vol2SegMap = new HashMapList<Integer, RectSegment>();
		List<RectSegment> tmp = new ArrayList<RectSegment>();	

		if (orientation==Orientation.HORIZONTAL || orientation==Orientation.BOTH)
		{
			//longest row:
			for (int row=0; row<cells.length; row++) 
			{
				RectSegment gs = null;

				for (int col=0; col<cells[row].length; col++) 
				{
					Cell<T> cell = cells[row][col];
					if (cell.node==null && cell.belongsTo==null) 
					{
						//add to rectangle
						Rectangle r = cell.bounds;
						RectSegment newGs = new RectSegment(
								(float)r.x,
								(float)(r.x + r.width),
								(float)r.y,
								(float)(r.y + r.height));
						if (gs==null) {
							gs = newGs;
						} else {
							gs.growBoundingBox(newGs);
						}

						if (col==cells[row].length-1) {
							tmp.add(gs);
						}
					} 
					else
					{
						if (gs!=null) {
							tmp.add(gs);
						}
						gs = null;
					}
				}			
			}

			Collections.sort(tmp, new WidthComparator());
			Collections.reverse(tmp);
			for (RectSegment rs : tmp) {
				vol2SegMap.putmore(((int)rs.getArea()), rs);
			}
		}

		if (orientation==Orientation.VERTICAL || orientation==Orientation.BOTH) 
		{
			tmp = new ArrayList<RectSegment>();

			//longest column:
			for (int  col=0; col<cells[0].length; col++) 
			{
				RectSegment gs = null;

				for (int row=0; row<cells.length; row++) 
				{
					Cell<T> cell = cells[row][col];
					if (cell.node==null && cell.belongsTo==null) 
					{
						//add to rectangle
						Rectangle r = cell.bounds;
						RectSegment newGs = new RectSegment(
								(float)r.x,
								(float)(r.x + r.width),
								(float)r.y,
								(float)(r.y + r.height));
						if (gs==null) {
							gs = newGs;
						} else {
							gs.growBoundingBox(newGs);
						}

						if (row==cells.length-1) {
							tmp.add(gs);
						}

					} 				
					else 
					{
						if (gs!=null) {
							tmp.add(gs);
						}
						gs = null;
					}
				}
			}

			Collections.sort(tmp, new HeightComparator());
			Collections.reverse(tmp);
			for (RectSegment rs : tmp) {
				vol2SegMap.putmore(((int)rs.getArea()), rs);
			}
		}

		//get whitespace rivers by largest volume
		List<RectSegment> result = new ArrayList<RectSegment>();	
		List<Integer> keys = new ArrayList<Integer>(vol2SegMap.keySet());
		Collections.sort(keys);
		Collections.reverse(keys);
		for (int i=0; i<n && i<keys.size(); i++) 
		{
			result.addAll(vol2SegMap.get(keys.get(i)));
		}

		return result;
	}

	/******************************************************************
	 * 
	 * Cell - A single cell of the document matrix
	 * 
	 * A cell *MAY* contain a DocNode, but does not have to.
	 * 
	 */
	public class Cell <S> 
	{
		Cell<S> belongsTo = null;

		public S node;

		public Rectangle bounds;

		public int hspan=0, vspan=0;

		public Cell(Rectangle bounds) {
			this.bounds = bounds;
		}

		public Cell(S docNode, Rectangle bounds) {
			this (bounds);
			this.node = docNode;
		}

		@Override
		public String toString() {
			if (node!=null) {
				return node.toString();
			}
			else return "{}";
		}
	}

	/**
	 * 
	 * @return
	 */
	public List<RectSegment> toSegments () 
	{
		List<RectSegment> result = new ArrayList<RectSegment>();
		for (int row=0; row<cells.length; row++) 
		{			
			for (int col=0; col<cells[row].length; col++) 
			{
				Cell<T> cell = cells[row][col];
				Rectangle r = cell.bounds;
				RectSegment rs = new RectSegment(
						(float)r.x,
						(float)(r.x + r.width),
						(float)r.y,
						(float)(r.y + r.height));

				/* check if an empty cells belongs to someone else */
				if (cell.belongsTo!=null) 
				{
					rs.setFilled(true);
				} else {
					rs.setFilled(false);
				}
				result.add(rs);
			}
		}
		return result;
	}

	/*********************************************************
	 * 
	 * DM_Iterator
	 * 
	 * An iterator of a DocumentMatrix. Iterates first all 
	 * columns left to right, then all rows top to bottom.
	 */
	public class AM_Iterator extends PeekIterator<T>
	implements Iterator<T> 
	{
		private List<T> nodeList;

		private Iterator<T> iterator;

		/**
		 * Constructor.
		 */
		private AM_Iterator() {

			nodeList = new ArrayList<T>();

			for (int row=0; row<cells.length; row++) {
				for (int col=0; col<cells[row].length; col++) {
					if (cells[row][col].node!=null) {
						nodeList.add(cells[row][col].node);
					}
				}
			}

			this.iterator = nodeList.iterator();
		}

		/**
		 * Constructor.
		 */
		private AM_Iterator(T start) {

			nodeList = new ArrayList<T>();

			int leftStop = 0;
			boolean trigger = false;
			for (int row=0; row<cells.length; row++) {
				for (int col=0; col<cells[row].length; col++) {
					if (cells[row][col].node!=null) {
						T node = cells[row][col].node;
						if (node.equals(start)) {
							leftStop = col - 10;
							trigger = true;
						}
						if (trigger && col>=leftStop) {
							nodeList.add(node);
						}
					}
				}
			}

			this.iterator = nodeList.iterator();
		}

		/**
		 * Constructor.
		 */
		private AM_Iterator(T start, int rightStop) 
		{
			nodeList = new ArrayList<T>();
			int leftStop = 0;
			boolean trigger = false;
			for (int row=0; row<cells.length; row++) {
				for (int col=0; col<cells[row].length; col++) {
					if (cells[row][col].node!=null) {
						T node = cells[row][col].node;
						if (node.equals(start)) 
						{
							leftStop = col - 10;
							if (rightStop<col) 
							{
								this.iterator =  nodeList.iterator();
								return;
							}
							trigger = true;
						}
						if (trigger && col>=leftStop && col<rightStop) {
							nodeList.add(node);
						}
					}
				}
			}

			this.iterator = nodeList.iterator();
		}

		@Override
		public void remove() {
			this.iterator.remove();
		}

		public int getWidth() {
			return cells[0].length;
		}

		@Override
		protected T internalNext() throws Exception {
			if(!iterator.hasNext()) return(null);
			return(iterator.next());
		}

	}

}//DocumentMatrix



///* compute horizontal span */
//int hspan=1;
//while (cells[row].length>col+hspan && (cells[row][col+hspan].node==null&&cells[row][col+hspan].belongsTo==c))
//{
//	hspan++;
//}
//
///* compute horizontal span */
//int vspan=1;
//while (cells.length>row+vspan && (cells[row+vspan][col].node==null&&cells[row+vspan][col].belongsTo==c))
//{		
//	vspan++;
//}
//
///* search all north/south neighbors */
//List<T> neighsTop = new ArrayList<T>();
//List<T> neighsBot = new ArrayList<T>();
//
//BitSet bsTop = new BitSet(hspan);
//BitSet bsBot = new BitSet(hspan);
//
//for (int l=hspan-1; l>=0; l--) 
//{
//	
//	//search up
//	if (bsTop.get(l)) 
//	{
//		continue; //this neighbor is already defined
//	}
//	else 
//	{
//		//go up
//		for (int m=1; m<=row; m++) 
//		{
//			Cell<T> up = cells[row-m][col+l];
//			if (up.node!=null || up.belongsTo!=null) 
//			{
//				//found north neighbor for cell c
//				T node = up.node;
//				if (node==null) 
//				{
//					node = up.belongsTo.node;
//				}
//				
//				neighsTop.add(node);
//				bsTop.set(l); //remember
//				break;
//			}
//		}
//	}
//
//	//search up
//	if (bsBot.get(l)) 
//	{
//		continue; //this neighbor is already defined
//	}
//	else 
//	{
//		//go down
//		for (int m=1; m<cells.length-row; m++) 
//		{
//			Cell<T> down = cells[row+m][col+l];
//			if (down.node!=null || down.belongsTo!=null) 
//			{
//				//found north neighbor for cell c
//				T node = down.node;
//				if (node==null) 
//				{
//					node = down.belongsTo.node;
//				}
//
//				neighsBot.add(node);
//				bsBot.set(l); //remember
//				break;
//			}
//		}
//	}
//}
