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
import at.tuwien.prip.common.log.ErrorDump;
import at.tuwien.prip.common.utils.PeekIterator;
import at.tuwien.prip.common.utils.SimpleTimer;
import at.tuwien.prip.model.document.layout.Direction;
import at.tuwien.prip.model.document.segments.RectSegment;
import at.tuwien.prip.model.graph.hier.ISegHierGraph;
import at.tuwien.prip.model.utils.Orientation;

/**
 * DocumentMatrix.java
 *
 * Matrix representation of bounding boxes of a document. This is part of the
 * the Sliding Window algorithm implementation.
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Mar 9, 2011
 */
public class DocumentMatrix
{
	public List<DocNode> nodes;
	
	public Map<DocNode, Integer> colMap;
	
	public Map<DocNode, Integer> rowMap;

	public Cell[][] cells;

	public Rectangle [] rows;

	public Rectangle [] cols;

	public int width;

	public int height;

	/**
	 * Constructor.
	 * 
	 * @param g
	 */
	private DocumentMatrix(ISegmentGraph dg) {
		this(dg.getNodes());
	}

	/**
	 * Constructor.
	 * 
	 * @param g
	 */
	private DocumentMatrix(ISegmentGraph dg, boolean exact) {
		this(dg.getNodes(), exact);
	}

	/**
	 * Constructor.
	 * 
	 * @param nodes
	 */
	private DocumentMatrix(List<DocNode> nodes) {
		this(nodes, false);
	}

	/**
	 * Constructor.
	 * 
	 * @param nodes
	 */
	private DocumentMatrix(List<DocNode> nodes, boolean exact) 
	{
		this.nodes = nodes;

		colMap = new HashMap<DocNode, Integer>();
		rowMap = new HashMap<DocNode, Integer>();
		
		List<Integer> xCoord = new ArrayList<Integer>();
		List<Integer> yCoord = new ArrayList<Integer>();

		//remember start to end positions
		Map<Integer,Integer> xxMap = new HashMap<Integer,Integer>();
		Map<Integer,Integer> yyMap = new HashMap<Integer,Integer>();

		Map2<Integer,Integer,DocNode> xyMap = 
			new HashMap2<Integer,Integer,DocNode>(null,null,null);

		int maxY = 0;
		int maxX = 0;

		//generate the coordinates of the matrix grid
		for (DocNode n : nodes) 
		{
			if (n.getSegY2()>maxY) {
				maxY = (int) n.getSegY2();
			}
			if (n.getSegX2()>maxX) {
				maxX = (int) n.getSegX2();
			} 
			//the x coordinate
			int x1 = (int) n.getSegX1();
			int x2 = (int) n.getSegX2();

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
			int y1 = (int) n.getSegY1();
			int y2 = (int) n.getSegY2();

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
		for (int r=0; r<yCoord.size(); r++) { //rows
			for (int c=0; c<xCoord.size(); c++) { //cols
				DocNode n = xyMap.get(xCoord.get(c), yCoord.get(r));
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
					cells[r][c] = new Cell(bounds);
				} else {
					cells[r][c] = new Cell(n, bounds);
					colMap.put(n, c);
					rowMap.put(n, r);
				}
			}
		}

		for (int row=0; row<cells.length; row++) 
		{
			for (int col=0; col<cells[row].length; col++) 
			{
				DocNode node = cells[row][col].node;
				if (node!=null)
				{

					int height = (int) (node.getSegY2() - node.getSegY1());

					for (int r=row; height>0 && r<cells.length; r++) {
						int width = (int) (node.getSegX2() - node.getSegX1());
						for (int c=col; width>0 && c<cells[r].length; c++) {

							Cell cell = cells[r][c];
							if (c==col) {
								height -= cell.bounds.height; //once per row
							}
							width -= cell.bounds.width;
							cell.full = true;
						}
					}
				}
			}
		}

		width = maxX;
		height = maxY;
	}

	public int getRow(DocNode node) {
		return rowMap.get(node);
	}
	
	public int getCol(DocNode node) {
		return colMap.get(node);
	}
	
	public static DocumentMatrix newInstance (ISegmentGraph dg) 
	{
		if (dg instanceof ISegHierGraph) 
		{
			dg = ((ISegHierGraph) dg).getBaseGraph();
		}
		return new DocumentMatrix(dg);
	}

	public static DocumentMatrix newInstance (ISegmentGraph dg, boolean exact)
	{
		if (dg instanceof ISegHierGraph) 
		{
			dg = ((ISegHierGraph) dg).getBaseGraph();
		}
		return new DocumentMatrix(dg, exact);
	}

	public static DocumentMatrix newInstance (List<DocNode> nodes) 
	{
		return new DocumentMatrix(nodes);
	}

	public static DocumentMatrix newInstance (List<DocNode> nodes, boolean exact) 
	{
		return new DocumentMatrix(nodes, exact);
	}

	public DM_Iterator iterator ()
	{
		return new DM_Iterator();
	}

	public DM_Iterator iterator (DocNode start) 
	{
		return new DM_Iterator(start); 
	}

	public DM_Iterator iterator (DocNode start, int rightStop) 
	{
		return new DM_Iterator(start, rightStop); 
	}

	/**
	 * 
	 * @param node
	 * @param dir
	 * @return
	 */
	public DocNode findNextNeighbour(DocNode node, Direction dir)
	{
		DocNode result = null;
		int a = -1; int b = -1;
OUT:		for (int i=0; i<cells.length; i++)
		{
			for (int j=0; j<cells[i].length; j++)
			{
				DocNode n = cells[i][j].node;
				if (n!=null && n.equals(node))
				{
					a = i; b = j;
					break OUT;
				}
			}
		}
		if (a==-1||b==-1)
			return result;
		
		switch (dir)
		{
		case SOUTH:
			while (a<cells.length-1)
			{
				a++;
				if (cells[a][b].node!=null) 
				{
					return cells[a][b].node;
				}
			}
			break;

		case NORTH:
			while (a>0)
			{
				a--;
				if (cells[a][b].node!=null) 
				{
					return cells[a][b].node;
				}
			}
			break;
			
		case EAST:
			while (b<cells[a].length-1)
			{
				b++;
				if (cells[a][b].node!=null) 
				{
					return cells[a][b].node;
				}
			}
			break;
			
		case WEST:
			while (b>0)
			{
				b--;
				if (cells[a][b].node!=null) 
				{
					return cells[a][b].node;
				}
			}
			break;
		}
		return result;
	}
	
	public List<Integer> getSalientColumns ()
	{
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

	public List<Integer> getNonNullColumns () 
	{
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

	public List<Integer> getSalientRows ()
	{
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
		SimpleTimer timer = new SimpleTimer();
		timer.startTask(0);
		//get by largest volume
		MapList<Integer, RectSegment> vol2SegMap = new HashMapList<Integer, RectSegment>();
		List<RectSegment> tmp = new ArrayList<RectSegment>();	

		if (cells.length==0)
			return tmp;
		
		/* HORIZONTAL CASE */
		if (orientation==Orientation.HORIZONTAL || orientation==Orientation.BOTH)
		{
			//longest row:
			for (int row=0; row<cells.length; row++) 
			{
				RectSegment gs = null;

				for (int col=0; col<cells[row].length; col++) 
				{
					Cell cell = cells[row][col];
					if (cell.node==null && !cell.full) 
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

			for (RectSegment rs : tmp) {
				vol2SegMap.putmore(((int)rs.getArea()), rs);
			}
		}

		/* VERTICAL CASE */
		if (orientation==Orientation.VERTICAL || orientation==Orientation.BOTH) 
		{
			tmp = new ArrayList<RectSegment>();

			//longest column:
			for (int  col=0; col<cells[0].length; col++) 
			{
				RectSegment gs = null;

				for (int row=0; row<cells.length; row++) 
				{
					Cell cell = cells[row][col];
					if (cell.node==null && !cell.full) 
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

			for (RectSegment rs : tmp) {
				if (rs.getArea()>3000) {
					vol2SegMap.putmore(((int)rs.getArea()), rs);
				}
			}
		}
		
		/* get whitespace rivers by largest volume */
		List<RectSegment> result = new ArrayList<RectSegment>();	
		List<Integer> keys = new ArrayList<Integer>(vol2SegMap.keySet());
		
		Collections.sort(keys);
		Collections.reverse(keys);
		for (int i=0; i<n && i<keys.size(); i++) 
		{
			result.addAll(vol2SegMap.get(keys.get(i)));
		}
		
		timer.stopTask(0);
		ErrorDump.debug(this, "WS computation finished in "+timer.getTimeMillis(0)+"ms");
		return result;
	}

	/**
	 * Add reading order relations to a graph.
	 * 
	 * @param g
	 */
	public void addReadingOrderRelations (ISegmentGraph g) 
	{

		if (g instanceof ISegHierGraph) 
		{	
			g = ((ISegHierGraph) g).getBaseGraph();
		}

		DocNode prev = null;

		DM_Iterator it = iterator();
		while (it.hasNext())
		{
			DocNode node = it.next();

			if (prev==null) {
				prev = node;
				continue;
			}

			//			if (prev.getBoundingBox().getBounds().getX()<=node.getBoundingBox().getBounds().getX())
			//			{

			if (g.getEdgeBetween(prev, node)==null) {
				DocEdge edge = new DocEdge(prev, node);
				edge.setRelation("reading-order");
				edge.getEdgeRelations().add(new EdgeRelation(EEdgeRelation.READING_ORDER_AFTER));
				edge.orientation = Orientation.DIAGONAL;
				g.addEdge(edge);
			}

			//			}

			prev = node;
		}
	}

	/******************************************************************
	 * 
	 * Cell - A single cell of the document matrix
	 * 
	 * A cell *MAY* contain a DocNode, but does not have to.
	 * 
	 */
	public class Cell {

		boolean full = false;

		public DocNode node;

		public Rectangle bounds;

		public Cell(Rectangle bounds) {
			this.bounds = bounds;
		}

		public Cell(DocNode docNode, Rectangle bounds) {
			this (bounds);
			this.node = docNode;
		}

		@Override
		public String toString() {
			if (node!=null) {
				return node.segText;
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
				Cell cell = cells[row][col];
				Rectangle r = cell.bounds;
				RectSegment rs = new RectSegment(
						(float)r.x,
						(float)(r.x + r.width),
						(float)r.y,
						(float)(r.y + r.height));
				if (cell.full) {
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
	public class DM_Iterator extends PeekIterator<DocNode>
	implements Iterator<DocNode> 
	{

		private List<DocNode> nodeList;

		private Iterator<DocNode> iterator;

		/**
		 * Constructor.
		 */
		private DM_Iterator() {

			nodeList = new ArrayList<DocNode>();

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
		private DM_Iterator(DocNode start) {

			nodeList = new ArrayList<DocNode>();

			int leftStop = 0;
			boolean trigger = false;
			for (int row=0; row<cells.length; row++) {
				for (int col=0; col<cells[row].length; col++) {
					if (cells[row][col].node!=null) {
						DocNode node = cells[row][col].node;
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
		private DM_Iterator(DocNode start, int rightStop) 
		{
			nodeList = new ArrayList<DocNode>();
			int leftStop = 0;
			boolean trigger = false;
			for (int row=0; row<cells.length; row++) {
				for (int col=0; col<cells[row].length; col++) {
					if (cells[row][col].node!=null) {
						DocNode node = cells[row][col].node;
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
		protected DocNode internalNext() throws Exception {
			if(!iterator.hasNext()) return(null);
			return(iterator.next());
		}

	}

	/**
	 * Serialize to text.
	 * @return
	 */
	public String serializeText() 
	{
		int row = 0;
		StringBuffer sb = new StringBuffer();
		DM_Iterator it = iterator();
		while (it.hasNext()) 
		{
			DocNode node = it.next();
			int r = getRow(node);
			if(r>row) {
				sb.append("\n");
				row = r;
			}
			if (node.getSegText()!=null) 
			{
				sb.append(node.getSegText() + " ");
			}
		}
		return sb.toString();
	}

}//DocumentMatrix
