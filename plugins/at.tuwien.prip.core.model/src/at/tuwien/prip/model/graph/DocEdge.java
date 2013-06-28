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

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import at.tuwien.prip.model.graph.base.BaseEdge;
import at.tuwien.prip.model.utils.Orientation;


/**
 * This represents an edge in the document graph
 *
 * @author Tamir Hassan, pdfanalyser@tamirhassan.com
 * @author Max Goebel
 * @version PDF Analyser 0.9
 */
public class DocEdge extends BaseEdge<DocNode> 
implements Cloneable
{
	
	public static Color DEFAULT_COLOR = Color.decode("#0000B0");
    public static Color MOUSE_OVER_COLOR = Color.pink;
	
//	in TG Edge class
//    protected DocNode from;
//  	protected DocNode to;
    
  	protected double length = 0;
    protected float weight; //???

    
    protected int repetitions = 1;
    
    protected static int MAX_CONFIDENCE = 5;


	protected int logicalLength = EdgeConstants.LENGTH_GREATER;
	protected int matchLength = EdgeConstants.LENGTH_ANY;
	
	protected boolean mAlignTopLeft = false;
	protected boolean mAlignCentre = false;
	protected boolean mAlignBottomRight = false;
	
	protected boolean crossesRulingLine = false;
	protected boolean matchCrossesRulingLine = false;
	
	protected int readingOrder = EdgeConstants.REL_NONE;
	protected boolean matchReadingOrder = false;
	
	protected int superiorInferior = EdgeConstants.REL_NONE;
	protected boolean matchSuperiorInferior = false;
	

    protected Orientation orientation;
    

    /**
     * Constructor.
     *
     * @param todo: add parameters :)
     */

    public DocEdge(DocNode nodeFrom, DocNode nodeTo)
    {
//    	super(nodeFrom, nodeTo, DEFAULT_LENGTH);
    	from = nodeFrom;
    	to = nodeTo;
        this.weight = 1.0f;
        this.length = (int)Math.pow(weight, 0.85) + 10;
        this.edgeRelations = new ArrayList<EdgeRelation>();
    }
    
    /**
     * Constructor.
     * 
     * @param nodeFrom
     * @param nodeTo
     * @param weight
     */
    public DocEdge(DocNode nodeFrom, DocNode nodeTo, float weight)
    {
//    	super(nodeFrom, nodeTo, DEFAULT_LENGTH);
        this.weight = weight;
        this.length = (int)Math.pow(weight, 0.85) + 10;
        this.edgeRelations = new ArrayList<EdgeRelation>();
    }
    
    /**
     * Constructor.
     * 
     * @param nodeFrom
     * @param nodeTo
     * @param repetitions
     */
    public DocEdge(DocNode nodeFrom, DocNode nodeTo, int repetitions)
    {
//    	super(nodeFrom, nodeTo, DEFAULT_LENGTH);
        this.weight = 1.0f;
        this.repetitions = repetitions;
        this.length = (int)Math.pow(weight, 0.85) + 10;
        this.edgeRelations = new ArrayList<EdgeRelation>();
    }
    
    /**
     * 
     * Constructor: As well as the AdjacencyEdge (with docmodel segments)
     * the correct corresponding created DocNodes must be passed
     * 
     * @param ae
     * @param nodeFrom
     * @param nodeTo
     * @param dg
     */
    public DocEdge(AdjacencyEdge<?> ae, DocNode nodeFrom, DocNode nodeTo, DocumentGraph dg)
    {
//    	super(nodeFrom, nodeTo, DEFAULT_LENGTH);
        this.from = nodeFrom;
        this.to = nodeTo;
        this.weight = 1.0f; // currently we ignore the AG weights (they are not used/set...)
        this.length = (int)Math.pow(weight, 0.85) + 10;
        this.edgeRelations = new ArrayList<EdgeRelation>();
        
        switch (ae.getDirection())
        {
        
            case AdjacencyEdge.REL_LEFT:
               
            	relation = EdgeConstants.ADJ_LEFT;
                
                //weight = ae.getEdgeLength();
                weight = (int)Math.pow(ae.getPhysicalLength(), 0.85) + 10;
                
                EdgeRelation rel = new EdgeRelation(EEdgeRelation.LEFT_OF);
                double docRat = new Double(1d/(double)dg.getDimensions().width);
                double confidence = (-MAX_CONFIDENCE)*(docRat)* ae.getPhysicalLength() + MAX_CONFIDENCE;
                rel.setConfidence(confidence);
                rel.setWeight(EdgeConstants.WEIGHT_LENGTH);
                edgeRelations.add(rel);
                orientation = Orientation.HORIZONTAL;
                break;
        
            case AdjacencyEdge.REL_RIGHT:
            	
                relation = EdgeConstants.ADJ_RIGHT;

                //weight = ae.getEdgeLength();
                weight = (int)Math.pow(ae.getPhysicalLength(), 0.85) + 10;

                rel = new EdgeRelation(EEdgeRelation.RIGHT_OF);
                docRat = new Double(1d/(double)dg.getDimensions().width);
                confidence = (-MAX_CONFIDENCE)*(docRat)* ae.getPhysicalLength() + MAX_CONFIDENCE;
                
                if ((from.getSegText()!=null && from.getSegText().length()>0) && 
                		(to.getSegText()!=null && to.getSegText().length()>0))
                {
                	if (to.getSegText().endsWith(":")) {
                		confidence = 1d;
                	}
                }
                rel.setConfidence(confidence);
                rel.setWeight(EdgeConstants.WEIGHT_LENGTH);
                edgeRelations.add(rel);
                orientation = Orientation.HORIZONTAL;
                break;
        
            case AdjacencyEdge.REL_ABOVE:
                relation = EdgeConstants.ADJ_ABOVE;
                
                //weight = ae.getEdgeLength();
                weight = (int)Math.pow(ae.getPhysicalLength(), 0.85) + 10;
                
                rel = new EdgeRelation(EEdgeRelation.ABOVE);
                docRat = new Double(1d/(double)dg.getDimensions().height);
                confidence = (-MAX_CONFIDENCE)*(docRat)* ae.getPhysicalLength() + MAX_CONFIDENCE;
                rel.setConfidence(confidence);
                rel.setWeight(EdgeConstants.WEIGHT_LENGTH);
                edgeRelations.add(rel);
                orientation = Orientation.VERTICAL;
                break;
        
            case AdjacencyEdge.REL_BELOW:
                relation = EdgeConstants.ADJ_BELOW;
                
                //weight = ae.getEdgeLength();
                weight = (int)Math.pow(ae.getPhysicalLength(), 0.85);
                
                rel = new EdgeRelation(EEdgeRelation.BELOW);
                docRat = new Double(1d/(double)dg.getDimensions().height);
                confidence = (-MAX_CONFIDENCE)*(docRat)* ae.getPhysicalLength() + MAX_CONFIDENCE;
                rel.setConfidence(confidence);
                rel.setWeight(EdgeConstants.WEIGHT_LENGTH);
                edgeRelations.add(rel);
                orientation = Orientation.VERTICAL;
                break;
        }
        
        //check for alignment attributes
        if (from.getSegXmid()==to.getSegXmid()) 
        {
        	EdgeRelation rel = new EdgeRelation(EEdgeRelation.CENTER_ALIGNED);
        	double confidence = MAX_CONFIDENCE - (2 / (from.getWidth() + to.getWidth()));
        	rel.setConfidence(confidence);
        	rel.setWeight(EdgeConstants.WEIGHT_ALIGN);
        	edgeRelations.add(rel);
        } 
        else if (from.getSegX1()==to.getSegX1())
        {
        	EdgeRelation rel = new EdgeRelation(EEdgeRelation.LEFT_ALIGNED);
        	double confidence = MAX_CONFIDENCE - ((2*MAX_CONFIDENCE) / (from.getWidth() + to.getWidth()));
        	rel.setConfidence(confidence);
        	rel.setWeight(EdgeConstants.WEIGHT_ALIGN);
        	edgeRelations.add(rel);        	
        } 
        else if (from.getSegX2()==to.getSegX2()) 
        {
        	EdgeRelation rel = new EdgeRelation(EEdgeRelation.RIGHT_ALIGNED);
        	double confidence =  MAX_CONFIDENCE - ((2*MAX_CONFIDENCE) / (from.getWidth() + to.getWidth()));
        	rel.setConfidence(confidence);
        	rel.setWeight(EdgeConstants.WEIGHT_ALIGN);
        	edgeRelations.add(rel);
        }
        
        //check for inferior/superior attribute
        Font fromFont = from.getFont();
        Font toFont = to.getFont();
        if (fromFont!=null && toFont!=null) 
        {
        	float fromSize = from.getSegFontSize();
        	float toSize = to.getSegFontSize();
        	
        	if (fromSize>toSize || (fromFont.isBold() && !toFont.isBold())) 
        	{
        		EdgeRelation rel = new EdgeRelation(EEdgeRelation.SUPERIOR_TO);
        		double confidence = MAX_CONFIDENCE - (MAX_CONFIDENCE/(fromSize - toSize)); //the more the merrier
            	rel.setConfidence(confidence);
            	rel.setWeight(EdgeConstants.WEIGHT_SUB_INF);
            	edgeRelations.add(rel);
        	}
        	else if (toSize>fromSize || (toFont.isBold() && !fromFont.isBold())) 
        	{
        		EdgeRelation rel = new EdgeRelation(EEdgeRelation.INFERIOR_TO);
            	double confidence = MAX_CONFIDENCE - (MAX_CONFIDENCE/(fromSize - toSize)); //the more the merrier
            	rel.setConfidence(confidence);
            	rel.setWeight(EdgeConstants.WEIGHT_SUB_INF);
            	edgeRelations.add(rel);
        	}
        }
        
        //check for key-value relation
        if (from.getSegText()!=null && to.getSegText()!=null) 
        {
        	String text = cleanText(from.getSegText());
        	if (text.endsWith(":")) 
        	{
        		EdgeRelation rel = new EdgeRelation(EEdgeRelation.KEY_VALUE);
        		//TODO: define confidence
        		edgeRelations.add(rel);
        	}
        }
    }
    
	public Orientation getOrientation() {
		return orientation;
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}
	
	/**
	 * 
	 * Remove all leading/trailing white space, 
	 * carriage returns, etc...
	 * 
	 * @param text
	 * @return
	 */
	private static String cleanText (String text) {
		return text.replaceAll("[\n|\r|\t]","").trim();
	}

	public Object clone() 
	{
		return super.clone();
    }

    public String toString()
    {
        return ("AttributedEdge: " + relation + "\n NodeFrom: " + from + "\nNodeTo: " + to + "\n");
    }    
    
    public String getStringLabel()
    {
    	return ("Direction: " + relation + "     Length: " + weight);
    }

    /**
     * The length between the center points of the two end 
     * nodes of this edge.
     */
	public double computeLength()
	{		
		double aX = getFrom().getBoundingBox().getCenterX();
		double aY = getFrom().getBoundingBox().getCenterY();
		Point2D a = new Point2D.Double(aX, aY);

		double bX = getTo().getBoundingBox().getCenterX();
		double bY = getTo().getBoundingBox().getCenterY();
		Point2D b = new Point2D.Double(bX, bY);
		
		return a.distance(b);
	}
	
	public double getLength()
	{
		return length;
	}
	
	public static boolean isInverse(DocEdge e1, DocEdge e2)
	{
		if (e1.getRelation().equals(EdgeConstants.ADJ_LEFT))
			if (e2.getRelation().equals(EdgeConstants.ADJ_RIGHT)) return true;
		else if (e1.getRelation().equals(EdgeConstants.ADJ_RIGHT))
			if (e2.getRelation().equals(EdgeConstants.ADJ_LEFT)) return true;
		else if (e1.getRelation().equals(EdgeConstants.ADJ_ABOVE))
			if (e2.getRelation().equals(EdgeConstants.ADJ_BELOW)) return true;
		else if (e1.getRelation().equals(EdgeConstants.ADJ_BELOW))
			if (e2.getRelation().equals(EdgeConstants.ADJ_ABOVE)) return true;
		
		return false;
	}
	
//	public List<EdgeConstraint> getConstraints() {
//		return constraints;
//	}
//	
//	public void addConstraint (EdgeConstraint constraint) 
//	{
//		if (this.constraints==null) {
//			constraints = new ArrayList<EdgeConstraint>();
//		}
//		
//		constraints.add(constraint);
//	}
	
	/**
	 * @return Returns the weight.
	 */
	public float getWeight() {
	    return weight;
	}

	/**
	 * @param weight The weight to set.
	 */
	public void setWeight(float weight) {
	    this.weight = weight;
	}

	public int getRepetitions() {
		return repetitions;
	}

	public void setRepetitions(int repetitions) {
		this.repetitions = repetitions;
	}

	public int getLogicalLength() {
		return logicalLength;
	}

	public void setLogicalLength(int logicalLength) {
		this.logicalLength = logicalLength;
	}

	public int getMatchLength() {
		return matchLength;
	}

	public void setMatchLength(int matchLength) {
		this.matchLength = matchLength;
	}

	public boolean isMAlignTopLeft() {
		return mAlignTopLeft;
	}

	public void setMAlignTopLeft(boolean mAlignTopLeft) {
		this.mAlignTopLeft = mAlignTopLeft;
	}

	public boolean isMAlignCentre() {
		return mAlignCentre;
	}

	public void setMAlignCentre(boolean mAlignCentre) {
		this.mAlignCentre = mAlignCentre;
	}

	public boolean isMAlignBottomRight() {
		return mAlignBottomRight;
	}

	public void setMAlignBottomRight(boolean mAlignBottomRight) {
		this.mAlignBottomRight = mAlignBottomRight;
	}

	public boolean isCrossesRulingLine() {
		return crossesRulingLine;
	}

	public void setCrossesRulingLine(boolean crossesRulingLine) {
		this.crossesRulingLine = crossesRulingLine;
	}

	public boolean isMatchCrossesRulingLine() {
		return matchCrossesRulingLine;
	}

	public void setMatchCrossesRulingLine(boolean matchCrossesRulingLine) {
		this.matchCrossesRulingLine = matchCrossesRulingLine;
	}

	public int getReadingOrder() {
		return readingOrder;
	}

	public void setReadingOrder(int readingOrder) {
		this.readingOrder = readingOrder;
	}

	public boolean isMatchReadingOrder() {
		return matchReadingOrder;
	}

	public void setMatchReadingOrder(boolean matchReadingOrder) {
		this.matchReadingOrder = matchReadingOrder;
	}

	public int getSuperiorInferior() {
		return superiorInferior;
	}

	public void setSuperiorInferior(int superiorInferior) {
		this.superiorInferior = superiorInferior;
	}

	public boolean isMatchSuperiorInferior() {
		return matchSuperiorInferior;
	}

	public void setMatchSuperiorInferior(boolean matchSuperiorInferior) {
		this.matchSuperiorInferior = matchSuperiorInferior;
	}

	public boolean isVertical() {
		if (from.getBoundingBox().getCenterX()==to.getBoundingBox().getCenterX())
			return true;
		return false;
	}

	/**
	 * Compute the horizontal distance between two nodes.
	 * @return
	 */
	public double computeHorizontalDistance() 
	{
		double c1 = from.getBoundingBox().getCenterX();
		double c2 = to.getBoundingBox().getCenterX();
		
		if (c1<c2)
		{
			return to.getSegX1() - from.getSegX2();
		}
		else
		{
			return from.getSegX1() - to.getSegX2();
		}
	}


}


/**

//SOME OBSOLETE STUFF...

//	public int getMultipleMatch() {
//		return multipleMatch;
//	}
//
//	public void setMultipleMatch(int multipleMatch) {
//		this.multipleMatch = multipleMatch;
//	}
//
//	public boolean isRemoveFromInstance() {
//		return removeFromInstance;
//	}
//
//	public void setRemoveFromInstance(boolean removeFromInstance) {
//		this.removeFromInstance = removeFromInstance;
//	}
//
//	public float getMatchMinLength() {
//		return matchMinLength;
//	}
//
//	public void setMatchMinLength(float matchMinLength) {
//		this.matchMinLength = matchMinLength;
//	}
//
//	public float getMatchMaxLength() {
//		return matchMaxLength;
//	}
//
//	public void setMatchMaxLength(float matchMaxLength) {
//		this.matchMaxLength = matchMaxLength;
//	}

//	public void addAsXMLEdge(Document resultDocument, Element parent)//, GenericSegment pageDim)
//    {
//        Element newNodeElement;
//        newNodeElement = resultDocument.createElement("edge");
//        
//        setXMLEdgeAttributes(resultDocument, newNodeElement);
//        
//        parent.appendChild(newNodeElement);
//    }
//	
//	public void setXMLEdgeAttributes(Document resultDocument, Element nodeElement)
//	{
//		Element newAttribElement;
//		/*
//		newAttribElement = resultDocument.createElement("hash-code");
//        nodeElement.appendChild(newAttribElement);
//    	newAttribElement.appendChild
//    		(resultDocument.createTextNode(Integer.toString(hashCode())));
//    	*/
//		
//		newAttribElement = resultDocument.createElement("node-from");
//        nodeElement.appendChild(newAttribElement);
//    	newAttribElement.appendChild
////    		(resultDocument.createTextNode(Integer.toString(from.getSegID())));
//    		(resultDocument.createTextNode(from.getID()));
//		
//    	newAttribElement = resultDocument.createElement("node-to");
//        nodeElement.appendChild(newAttribElement);
//    	newAttribElement.appendChild
////    		(resultDocument.createTextNode(Integer.toString(to.getSegID())));
//    		(resultDocument.createTextNode(to.getID()));
//    	
//    	newAttribElement = resultDocument.createElement("weight");
//        nodeElement.appendChild(newAttribElement);
//    	newAttribElement.appendChild
//    		(resultDocument.createTextNode(Float.toString(weight)));
//    	
//    	newAttribElement = resultDocument.createElement("relation");
//        nodeElement.appendChild(newAttribElement);
//    	newAttribElement.appendChild
//    		(resultDocument.createTextNode(relation.toString()));
//    	
//		newAttribElement = resultDocument.createElement("remove-from-instance");
//        nodeElement.appendChild(newAttribElement);
//    	newAttribElement.appendChild
//    		(resultDocument.createTextNode(Boolean.toString(removeFromInstance)));
//    	
//    	newAttribElement = resultDocument.createElement("multiple-match");
//        nodeElement.appendChild(newAttribElement);
//    	newAttribElement.appendChild
//    		(resultDocument.createTextNode(Integer.toString(multipleMatch)));
//    	
//    	newAttribElement = resultDocument.createElement("match-min-length");
//	    nodeElement.appendChild(newAttribElement);
//	 	newAttribElement.appendChild
//     		(resultDocument.createTextNode(Float.toString(matchMinLength)));
//	 	
//	 	newAttribElement = resultDocument.createElement("match-max-length");
//	    nodeElement.appendChild(newAttribElement);
//	 	newAttribElement.appendChild
//     		(resultDocument.createTextNode(Float.toString(matchMaxLength)));
//	 	
//		newAttribElement = resultDocument.createElement("logical-length");
//	    nodeElement.appendChild(newAttribElement);
//	 	newAttribElement.appendChild
//     		(resultDocument.createTextNode(Integer.toString(logicalLength)));
//	 	
//	 	newAttribElement = resultDocument.createElement("match-length");
//	    nodeElement.appendChild(newAttribElement);
//	 	newAttribElement.appendChild
//     		(resultDocument.createTextNode(Integer.toString(matchLength)));
//	 	
//	 	/*
//	 	newAttribElement = resultDocument.createElement("align-top-left");
//	    nodeElement.appendChild(newAttribElement);
//	 	newAttribElement.appendChild
//     		(resultDocument.createTextNode(Boolean.toString(alignTopLeft)));
//	 	
//	 	newAttribElement = resultDocument.createElement("align-centre");
//	    nodeElement.appendChild(newAttribElement);
//	 	newAttribElement.appendChild
//     		(resultDocument.createTextNode(Boolean.toString(alignCentre)));
//	 	
//	 	newAttribElement = resultDocument.createElement("align-bottom-right");
//	    nodeElement.appendChild(newAttribElement);
//	 	newAttribElement.appendChild
//     		(resultDocument.createTextNode(Boolean.toString(alignBottomRight)));
//	 	*/
//	 	
//	 	newAttribElement = resultDocument.createElement("match-align-top-left");
//	    nodeElement.appendChild(newAttribElement);
//	 	newAttribElement.appendChild
//     		(resultDocument.createTextNode(Boolean.toString(mAlignTopLeft)));
//	 	
//	 	newAttribElement = resultDocument.createElement("match-align-centre");
//	    nodeElement.appendChild(newAttribElement);
//	 	newAttribElement.appendChild
//     		(resultDocument.createTextNode(Boolean.toString(mAlignCentre)));
//	 	
//	 	newAttribElement = resultDocument.createElement("match-align-bottom-right");
//	    nodeElement.appendChild(newAttribElement);
//	 	newAttribElement.appendChild
//     		(resultDocument.createTextNode(Boolean.toString(mAlignBottomRight)));
//	 	
//	 	newAttribElement = resultDocument.createElement("crosses-ruling-line");
//	    nodeElement.appendChild(newAttribElement);
//	 	newAttribElement.appendChild
//     		(resultDocument.createTextNode(Boolean.toString(crossesRulingLine)));
//	 	
//	 	newAttribElement = resultDocument.createElement("match-crosses-ruling-line");
//	    nodeElement.appendChild(newAttribElement);
//	 	newAttribElement.appendChild
//     		(resultDocument.createTextNode(Boolean.toString(matchCrossesRulingLine)));
//	 	
//	 	newAttribElement = resultDocument.createElement("reading-order");
//	    nodeElement.appendChild(newAttribElement);
//	 	newAttribElement.appendChild
//     		(resultDocument.createTextNode(Integer.toString(readingOrder)));
//	 	
//	 	newAttribElement = resultDocument.createElement("match-reading-order");
//	    nodeElement.appendChild(newAttribElement);
//	 	newAttribElement.appendChild
//     		(resultDocument.createTextNode(Boolean.toString(matchReadingOrder)));
//	 	
//	 	newAttribElement = resultDocument.createElement("superior-inferior");
//	    nodeElement.appendChild(newAttribElement);
//	 	newAttribElement.appendChild
//     		(resultDocument.createTextNode(Integer.toString(superiorInferior)));
//	 	
//	 	newAttribElement = resultDocument.createElement("match-superior-inferior");
//	    nodeElement.appendChild(newAttribElement);
//	 	newAttribElement.appendChild
//     		(resultDocument.createTextNode(Boolean.toString(matchSuperiorInferior)));
//	}
//	
//	public void clearWrapperEdits()
//	{
//		matchMinLength = 0.0f;
//		matchMaxLength = 0.0f;
//		matchLength = EdgeConstants.LENGTH_ANY;
//
//		mAlignTopLeft = false;
//		mAlignCentre = false;
//		mAlignBottomRight = false;
//		
//		matchCrossesRulingLine = false;
//		matchReadingOrder = false;
//		matchSuperiorInferior = false;
//		
//		//matchN = false;
//		multipleMatch = MATCH_ONE;
//		removeFromInstance = false;
//	}
	//	// this method adapted from WikiEdge.java
//	public void paint(Graphics g, TGPanel tgPanel) {
//		
//		// these two lines and hints uncommented 31.10.08
//		Graphics2D g2d = (Graphics2D)g;
//		g2d.setRenderingHints(Utils.hints);
//		
//	    Color c = getColor();
//	    
//	    // do this, as we don't want the mouseOverColor to be altered
//	    //if (tgPanel.getMouseOverN()==from || tgPanel.getMouseOverE()==this) 
//	    //    c = MOUSE_OVER_COLOR; 
//	    //else
//	    //    c = col;        
//	    
//	  //  if (this == tgPanel.getSelect())
//	    //if (isSelected())
//	    //	c = c.darker(); // TODO: replace with outline drawing
//	    
//	    //if (isMatchN())
//	    if (getMultipleMatch() != MATCH_ONE)
//	    	c = c.darker().darker();
//	
//	    if (isRemoveFromInstance())
//	    	c = c.brighter();
//	    
//	    if (tgPanel.getMouseOverN()==from || tgPanel.getMouseOverE()==this) 
//	        c = MOUSE_OVER_COLOR; 
//	    
//		int x1=(int) from.drawx;
//		int y1=(int) from.drawy;
//		int x2=(int) to.drawx;
//		int y2=(int) to.drawy;
//		
//		int arrowWidth = 3;
//		int outlineWidth = 2;
//		int backgroundWidth = arrowWidth + outlineWidth;
//		if (intersects(tgPanel.getSize())) {
//	    
//			if (this == tgPanel.getSelect()) // if TG-Modified not in classpath above TGWikiBrowser, will not compile here!
//			{
//				int x3=x1;
//	            int y3=y1;
//	            
//				double dist=Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
//	            if (dist>10) {
//	                double adjustDistRatio = (dist-10)/dist;
//	                x3=(int) (x1+(x2-x1)*adjustDistRatio);
//	                y3=(int) (y1+(y2-y1)*adjustDistRatio);
//	            }
//	
//	            x3=(int) ((x3*4+x1)/5.0);
//	            y3=(int) ((y3*4+y1)/5.0);
//	            
//				g.setColor(c.darker());
//				if (x2 > x1 && y2 > y1 || x1 > x2 && y1 > y2)
//	            {
//	            	//int[] a1 = {x1-5, x2-1, x2+1, x1+5};
//	            	//int[] a2 = {y1+5, y2+1, y2-1, y1-5};
//	            	////int[] a1 = {x1-6, x3-2, x2, x3+2, x1+6};
//	            	////int[] a2 = {y1+6, y3+2, y2, y3-2, y1-6};
//	            	int[] a1 = {x1-backgroundWidth, x2-1, x2+1, x1+backgroundWidth};
//	            	int[] a2 = {y1+backgroundWidth, y2+1, y2-1, y1-backgroundWidth};
//	            	g.fillPolygon(a1, a2, 4);
//	            }
//	            else
//	            {
//	            	//int[] a1 = {x1-6, x3-2, x2, x3+2, x1+6};
//	            	//int[] a2 = {y1-6, y3-2, y2, y3+2, y1+6};
//	            	int[] a1 = {x1-backgroundWidth, x2-1, x2+1, x1+backgroundWidth};
//	            	int[] a2 = {y1-backgroundWidth, y2-1, y2+1, y1+backgroundWidth};
//	            	g.fillPolygon(a1, a2, 4);
//	            }
//				//g.setColor(c);
//			}
//			
//			g.setColor(c);
//			
//			int x3=x1; //x2;//x1;
//	        int y3=y1; //y2;//y1;
//	
//	        
//	        double dist=Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
//	        if (dist>10) {
//	            double adjustDistRatio = (dist-10)/dist;
//	            x3=(int) (x1+(x2-x1)*adjustDistRatio);
//	            y3=(int) (y1+(y2-y1)*adjustDistRatio);
//	        }
//	
//	        x3=(int) ((x3*4+x1)/5.0);
//	        y3=(int) ((y3*4+y1)/5.0);
//	        
//	        // doesn't quite meet.... :(
//	        // g.drawLine(x1, y1, x2, y2);
//	        
//	        if (x2 > x1 && y2 > y1 || x1 > x2 && y1 > y2)
//	        {
//	        	//int[] a1 = {x1-4, x3-0, x3+0, x1+4};
//	        	//int[] a2 = {y1+4, y3+0, y3-0, y1-4};
//	        	////int[] a1 = {x1-4, x3, x2, x3, x1+4};
//	        	////int[] a2 = {y1+4, y3, y2, y3, y1-4};
//	        	int[] a1 = {x1-arrowWidth, x2, x1+arrowWidth};
//	        	int[] a2 = {y1+arrowWidth, y2, y1-arrowWidth};
//	        	g.fillPolygon(a1, a2, 3);
//	        }
//	        else
//	        {
//	        	//int[] a1 = {x1-4, x3-0, x3+0, x1+4};
//	        	//int[] a2 = {y1-4, y3-0, y3+0, y1+4};
//	        	////int[] a1 = {x1-4, x3, x2, x3, x1+4};
//	        	////int[] a2 = {y1-4, y3, x2, y3, y1+4};
//	        	int[] a1 = {x1-arrowWidth, x2, x1+arrowWidth};
//	        	int[] a2 = {y1-arrowWidth, y2, y1+arrowWidth};
//	        	g.fillPolygon(a1, a2, 3);
//	        }
//	        
//			//paintArrow(g, x1, y1, x2, y2, c);
//	        
//			/*
//	        // x3 is 75% along the width of the arrow
//	        // makes the arrows more pointed :)
//	        int x3=x1;
//	        int y3=y1;
//	
//	        double dist=Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
//	        if (dist>2) {
//	            double adjustDistRatio = (dist-2)/dist;
//	            x3=(int) (x1+(x2-x1)*adjustDistRatio);
//	            y3=(int) (y1+(y2-y1)*adjustDistRatio);
//	        }
//	
//	        x3=(int) ((x3*4+x1)/5.0);
//	        y3=(int) ((y3*4+y1)/5.0);
//	        
//	        if (this == tgPanel.getSelect())
//	        {
//	        	// g.setLineWidth or whatever...
//	            g.setColor(c.darker().darker());
//	
//	            g.drawLine(x3, y3, x2, y2);
//	            if (x2 > x1 && y2 > y1 || x1 > x2 && y1 > y2)
//	            {
//	            	g.drawLine(x3-1, y3+1, x2-1, y2+1);
//	            	g.drawLine(x3+1, y3-1, x2+1, y2-1);
//	            	
//	            	g.drawLine(x1-4, y1+4, x3, y3);
//	            	g.drawLine(x1+4, y1-4, x3, y3);
//	            	
//	            	g.drawLine(x1-5, y1+5, x3-1, y3+1);
//	            	g.drawLine(x1+5, y1-5, x3+1, y3-1);
//	            }
//	            else
//	            {
//	            	g.drawLine(x3-1, y3-1, x2-1, y2-1);
//	            	g.drawLine(x3+1, y3+1, x2+1, y2+1);
//	            	
//	            	g.drawLine(x1-4, y1-4, x3, y3);
//	            	g.drawLine(x1+4, y1+4, x3, y3);
//	            	
//	            	g.drawLine(x1-5, y1-5, x3-1, y3-1);
//	            	g.drawLine(x1+5, y1+5, x3+1, y3+1);
//	            }
//	        }
//	        */
//		}
//	}
//
//	public static void paintLine(Graphics g, int x1, int y1, int x2, int y2, Color c)
//	{
//		
//	}

//	public boolean isAlignTopLeft() 
//	{
//		if (relation.equals(EdgeConstants.ADJ_BELOW))
//		{
//			return (Utils.within(from.getSegX1(), to.getSegX1(), ALIGN_TOLERANCE));
//		}
//		else if (relation.equals(EdgeConstants.ADJ_RIGHT))
//		{
//			return (Utils.within(from.getSegY1(), to.getSegY1(), ALIGN_TOLERANCE));
//		}
//		//return alignTopLeft;
//		return false;
//	}
//	
//	public boolean isAlignCentre() 
//	{
//		if (relation.equals(EdgeConstants.ADJ_BELOW))
//		{
//			return (Utils.within(from.getSegXmid(), to.getSegXmid(), ALIGN_TOLERANCE));
//		}
//		else if (relation.equals(EdgeConstants.ADJ_RIGHT))
//		{
//			return (Utils.within(from.getSegYmid(), to.getSegYmid(), ALIGN_TOLERANCE));
//		}
//		//return alignCentre;
//		return false;
//	}
//	
//	public boolean isAlignBottomRight() 
//	{
//		if (relation.equals(EdgeConstants.ADJ_BELOW))
//		{
//			return (Utils.within(from.getSegX2(), to.getSegX2(), ALIGN_TOLERANCE));
//		}
//		else if (relation.equals(EdgeConstants.ADJ_RIGHT))
//		{
//			return (Utils.within(from.getSegY2(), to.getSegY2(), ALIGN_TOLERANCE));
//		}
//		//return alignBottomRight;
//		return false;
//	}
//public DocEdge(Element nodeElement, List<DocNode> nodes)
//{
////	super(null, null);
//	NodeList nl; Element el; NodeList textNL; String val;
//	
//	//-------
// nl = nodeElement.getElementsByTagName("node-from");
// el = (Element)nl.item(0);
// textNL = el.getChildNodes();
// val = ((Node)textNL.item(0)).getNodeValue().trim();
// int nodeFromHC = Integer.parseInt(val);
// 
//	//-------
// nl = nodeElement.getElementsByTagName("node-to");
// el = (Element)nl.item(0);
// textNL = el.getChildNodes();
// val = ((Node)textNL.item(0)).getNodeValue().trim();
// int nodeToHC = Integer.parseInt(val);
// 
//// for (DocNode n : nodes)
//// {
//// 	if (Integer.parseInt(n.getID()) == nodeFromHC)
//// 		from = n;
//// 	if (Integer.parseInt(n.getID()) == nodeToHC)
//// 		to = n;
//// }
// 
// //-------
// nl = nodeElement.getElementsByTagName("relation");
// el = (Element)nl.item(0);
//// textNL = el.getChildNodes();
// val = ((Node)textNL.item(0)).getNodeValue().trim();
// relation = val;
//// relation = model.getOntProperty(relationString);
// 
// //weight = ae.getEdgeLength();
// //weight = (int)Math.pow(getEdgeLength(), 0.85) + 10;
// 
// //-------
// nl = nodeElement.getElementsByTagName("weight");
// el = (Element)nl.item(0);
// textNL = el.getChildNodes();
// val = ((Node)textNL.item(0)).getNodeValue().trim();
// weight = Float.parseFloat(val);
// 
// //-------
// nl = nodeElement.getElementsByTagName("remove-from-instance");
// el = (Element)nl.item(0);
// textNL = el.getChildNodes();
// val = ((Node)textNL.item(0)).getNodeValue().trim();
// removeFromInstance = Boolean.parseBoolean(val);
//
// //-------
// nl = nodeElement.getElementsByTagName("multiple-match");
// el = (Element)nl.item(0);
// textNL = el.getChildNodes();
// val = ((Node)textNL.item(0)).getNodeValue().trim();
// multipleMatch = Integer.parseInt(val);
// 
// //-------
// nl = nodeElement.getElementsByTagName("match-min-length");
// el = (Element)nl.item(0);
// textNL = el.getChildNodes();
// val = ((Node)textNL.item(0)).getNodeValue().trim();
// matchMinLength = Float.parseFloat(val);
// 
// //-------
// nl = nodeElement.getElementsByTagName("match-max-length");
// el = (Element)nl.item(0);
// textNL = el.getChildNodes();
// val = ((Node)textNL.item(0)).getNodeValue().trim();
// matchMaxLength = Float.parseFloat(val);
// 
// //-------
// nl = nodeElement.getElementsByTagName("logical-length");
// el = (Element)nl.item(0);
// textNL = el.getChildNodes();
// val = ((Node)textNL.item(0)).getNodeValue().trim();
// logicalLength = Integer.parseInt(val);
//
// //-------
// nl = nodeElement.getElementsByTagName("match-length");
// el = (Element)nl.item(0);
// textNL = el.getChildNodes();
// val = ((Node)textNL.item(0)).getNodeValue().trim();
// matchLength = Integer.parseInt(val);
//
// /*
// //-------
// nl = nodeElement.getElementsByTagName("align-top-left");
// el = (Element)nl.item(0);
// textNL = el.getChildNodes();
// val = ((Node)textNL.item(0)).getNodeValue().trim();
// alignTopLeft = Boolean.parseBoolean(val);
//
// //-------
// nl = nodeElement.getElementsByTagName("align-centre");
// el = (Element)nl.item(0);
// textNL = el.getChildNodes();
// val = ((Node)textNL.item(0)).getNodeValue().trim();
// alignCentre = Boolean.parseBoolean(val);
//
// //-------
// nl = nodeElement.getElementsByTagName("align-bottom-right");
// el = (Element)nl.item(0);
// textNL = el.getChildNodes();
// val = ((Node)textNL.item(0)).getNodeValue().trim();
// alignBottomRight = Boolean.parseBoolean(val);
//	*/
// 
// //-------
// nl = nodeElement.getElementsByTagName("match-align-top-left");
// el = (Element)nl.item(0);
// textNL = el.getChildNodes();
// val = ((Node)textNL.item(0)).getNodeValue().trim();
// mAlignTopLeft = Boolean.parseBoolean(val);
//
// //-------
// nl = nodeElement.getElementsByTagName("match-align-centre");
// el = (Element)nl.item(0);
// textNL = el.getChildNodes();
// val = ((Node)textNL.item(0)).getNodeValue().trim();
// mAlignCentre = Boolean.parseBoolean(val);
//
// //-------
// nl = nodeElement.getElementsByTagName("match-align-bottom-right");
// el = (Element)nl.item(0);
// textNL = el.getChildNodes();
// val = ((Node)textNL.item(0)).getNodeValue().trim();
// mAlignBottomRight = Boolean.parseBoolean(val);
//
// //-------
// nl = nodeElement.getElementsByTagName("crosses-ruling-line");
// el = (Element)nl.item(0);
// textNL = el.getChildNodes();
// val = ((Node)textNL.item(0)).getNodeValue().trim();
// crossesRulingLine = Boolean.parseBoolean(val);
//
// //-------
// nl = nodeElement.getElementsByTagName("match-crosses-ruling-line");
// el = (Element)nl.item(0);
// textNL = el.getChildNodes();
// val = ((Node)textNL.item(0)).getNodeValue().trim();
// matchCrossesRulingLine= Boolean.parseBoolean(val);
//
// //-------
// nl = nodeElement.getElementsByTagName("reading-order");
// el = (Element)nl.item(0);
// textNL = el.getChildNodes();
// val = ((Node)textNL.item(0)).getNodeValue().trim();
// readingOrder = Integer.parseInt(val);
//
// //-------
// nl = nodeElement.getElementsByTagName("match-reading-order");
// el = (Element)nl.item(0);
// textNL = el.getChildNodes();
// val = ((Node)textNL.item(0)).getNodeValue().trim();
// matchReadingOrder = Boolean.parseBoolean(val);
//
// //-------
// nl = nodeElement.getElementsByTagName("superior-inferior");
// el = (Element)nl.item(0);
// textNL = el.getChildNodes();
// val = ((Node)textNL.item(0)).getNodeValue().trim();
// superiorInferior = Integer.parseInt(val);
//
// //-------
// nl = nodeElement.getElementsByTagName("match-superior-inferior");
// el = (Element)nl.item(0);
// textNL = el.getChildNodes();
// val = ((Node)textNL.item(0)).getNodeValue().trim();
// matchSuperiorInferior = Boolean.parseBoolean(val);
//}
//// match generic fields
//public static int MATCH_ONE = 0;
//public static int MATCH_N_TIL_FIRST = 1;
//public static int MATCH_N_TIL_LAST = 2;
//public static int MATCH_N_ANY = 3;
//
////protected boolean matchN = false;
//protected int multipleMatch = MATCH_ONE;
//protected boolean removeFromInstance = false;
////protected boolean selected = false;
//
//// fields specific to DocEdge
//
//
//public static float ALIGN_TOLERANCE = 3.0f;
//
//protected float matchMinLength = 0.0f;
//protected float matchMaxLength = 0.0f;
//
//replaced by calculated fields
//protected boolean alignTopLeft = false;
//protected boolean alignCentre = false;
//protected boolean alignBottomRight = false;
