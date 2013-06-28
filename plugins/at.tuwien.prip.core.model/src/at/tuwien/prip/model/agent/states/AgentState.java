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
package at.tuwien.prip.model.agent.states;

import java.awt.Font;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import at.tuwien.prip.common.utils.ListUtils;
import at.tuwien.prip.model.agent.AC;
import at.tuwien.prip.model.agent.IAgent;
import at.tuwien.prip.model.agent.attributes.AgentStateAttribute;
import at.tuwien.prip.model.agent.attributes.AttributeType;
import at.tuwien.prip.model.agent.attributes.SemanticAttribute;
import at.tuwien.prip.model.agent.constraint.EdgeConstraint;
import at.tuwien.prip.model.agent.labels.LayoutLabel;
import at.tuwien.prip.model.document.segments.SegmentType;
import at.tuwien.prip.model.document.semantics.SemanticText;
import at.tuwien.prip.model.document.semantics.WordSemantics;
import at.tuwien.prip.model.graph.DocNode;
import at.tuwien.prip.model.graph.DocumentGraph;
import at.tuwien.prip.model.graph.ISegmentGraph;
import at.tuwien.prip.model.graph.base.BaseNode;

/**
 * AgentState.java
 *
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Oct 24, 2011
 */
public abstract class AgentState extends BaseNode
implements IAgentState, java.io.Serializable
{
	/* */
	private static final long serialVersionUID = 7360338855762161145L;

	/* a name */
	protected String name;

	/* a list of attributes */
	protected transient List<AgentStateAttribute> attributes;

	/* the 'owning' agent */
	private transient IAgent owner;

	/* remember conflicting states */
	private transient List<AgentState> conflictingStates;

	/* the supporting agents */
	private List<IAgent> support;

	/* the (sub) graph this state affects */
	private ISegmentGraph graph;

	private DocNode node;

	private boolean reconsider;

	/* parent states */
	private List<AgentState> parents;

	/* child states */
	private List<AgentState> children;

	/* all descendants */
	private List<AgentState> descendants;

	/* all dependents */
	private List<AgentState> dependents;

	/* a list of agents that have processed this state */
	private List<Class<?>> processedBy;

	/* a list of agents that have utilized this state */
	private List<IAgent> utilizedBy;

	/* */
	private LayoutLabel annotation;

	/* the (edge) constraints associated with this state */
	private List<EdgeConstraint> constraints;

	protected SegmentType segType;

	/* Text specific information */
	protected TextContent textContent;

	/* a numeric type associated with this state */
	protected int stateLevel;

	private boolean dirty;

	private boolean blocked;

	private transient IAgent blockedBy;

	protected Rectangle bounds;

	protected volatile double utility = -1;

	protected double confidence;

	protected double complexity;

	/**
	 * Constructor.
	 */
	private AgentState()
	{
		this.attributes = new ArrayList<AgentStateAttribute>();
		this.parents = new ArrayList<AgentState>();
		this.constraints = new ArrayList<EdgeConstraint>();
		this.conflictingStates = new ArrayList<AgentState>();
		this.support = new ArrayList<IAgent>();
		this.processedBy = new ArrayList<Class<?>>();
		this.utilizedBy = new ArrayList<IAgent>();
		this.children = new ArrayList<AgentState>();
		this.descendants = new ArrayList<AgentState>();
		this.dependents = new ArrayList<AgentState>();
		owner = null;
	}

	/**
	 * Constructor.
	 * @param stateLevel
	 * @param segType
	 * @param layoutType
	 * @param owner
	 * @param bounds
	 * @param reconsider
	 */
	public AgentState (
			int stateLevel,
			SegmentType segType,
			double complexity,
			double confidence,
			IAgent owner,
			Rectangle bounds,
			boolean reconsider)
	{
		this();
		this.stateLevel = stateLevel;
		this.segType = segType;
		this.owner = owner;
		this.reconsider = reconsider;
		this.bounds = bounds;
	}

	/**
	 * Constructor.
	 * @param ll
	 * @param stateLevel
	 * @param type
	 * @param owner, the agent that owns this state
	 * @param reconsider, a flag to tell agent to reconsider even if owner
	 */
	public AgentState(
			LayoutLabel ll,
			IAgent owner,
			boolean reconsider)
	{
		this();
		this.stateLevel = ll.getStateLevel();
		this.segType = ll.getSegmentType();
		this.owner = owner;
		this.reconsider = reconsider;
		this.confidence = ll.getConfidence();
		this.setAnnotation(ll);
		this.setConstraints(ll.getConstraints());

		// compute dimensions
		Rectangle dimensions = null;
		for (AgentState state : ll.getAffectedStates())
		{
			if (dimensions==null)
			{
				dimensions = state.getBounds();
			}
			else
			{
				dimensions = dimensions.union(state.getBounds());
			}
		}
		this.bounds = dimensions;

		String text = "";
		Font font = null;
		for (AgentState affected : ll.getAffectedStates())
		{
			this.descendants.addAll(affected.getDescendants());
			this.descendants.add(affected);
			text += affected.getTextContent().getText()+" ";
			font = affected.getTextContent().getFont();
		}
		this.textContent = new TextContent(text.trim(), font);
		ListUtils.unique(descendants);
	}

	/**
	 * Constructor.
	 * Initialize the state with a graph.
	 * @param graph
	 */
	public AgentState(ISegmentGraph dg)
	{
		this();
		this.graph = dg;
		this.stateLevel = AC.A.PAGE;
		this.bounds = dg.getDimensions();
	}

	/**
	 * Constructor.
	 *
	 * @param node
	 */
	public AgentState(DocNode node)
	{
		this();
		this.graph = new DocumentGraph();
		this.node = node;

		List<DocNode> nodes = new ArrayList<DocNode>();
		nodes.add(node);
		((DocumentGraph)graph).setNodes(nodes);
		if (node.getSegType()==SegmentType.Block) {
			this.stateLevel = AC.A.TEXTLINE; //basic
		}
		else if (node.getSegType()==SegmentType.Word) {
			this.stateLevel = AC.A.WORD; //basic
		}

		this.bounds = node.getBoundingBox().getBounds();
		this.segType = node.getSegType();
		this.name = node.getSegText();
		this.confidence = 1f;
		this.complexity = 1d;

		this.textContent = new TextContent(
				node.getSegText(), node.getFont());
	}

	/**
	 *
	 * @param agent
	 * @return
	 */
	public boolean needsProcessing(IAgent agent)
	{
		if (getOwner()!=null && getOwner().getName().equals(agent.getName()))
		{
			return false;
		}
		if (getProcessedBy().contains(agent.getClass()))
		{
			return false;
		}
//		if (getAcceptors().contains(agent))
//		{
//			return false;
//		}
//		for (IAgent pb : processedBy)
//		{
//			if (pb.getName().equals(agent.getName()))
//			{
//				return false;
//			}
//		}
		return true;
	}

	/**
	 *
	 * @return
	 */
	public List<AgentState> getLeafChildren ()
	{
		List<AgentState> leafs = new ArrayList<AgentState>();
		Stack<AgentState> stack = new Stack<AgentState>();
		stack.push(this);

		while (!stack.isEmpty())
		{
			AgentState top = stack.pop();
			List<AgentState> children = top.getChildren();
			for (AgentState child : children)
			{
				if (child.getChildren().size()>0)
				{
					stack.push(child);
				}
				else
				{
					leafs.add(child);
				}
			}
		}
		return leafs;
	}

	/**
	 *
	 * @param state
	 */
	public void propagateUtility (AgentState state)
	{
		if (!this.dependents.contains(state))
		{
			dependents.add(state);
		}
//		owner.computeUtility(this);
	}

	public double getComplexity() {
		return complexity;
	}

	public double getConfidence() {
		return confidence;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public List<EdgeConstraint> getConstraints() {
		return constraints;
	}

	public void setConstraints(List<EdgeConstraint> constraints) {
		this.constraints = constraints;
	}

	public void updateConstraints(List<EdgeConstraint> constraints)
	{
			this.constraints = constraints;
	}

	public TextContent getTextContent() {
		return textContent;
	}

	public void setTextContent(TextContent textContent) {
		this.textContent = textContent;
	}

	public IAgent getOwner() {
		return owner;
	}

	public int getStateLevel() {
		return stateLevel;
	}

	public void setDirty (boolean dirty) {
		this.dirty = dirty;
	}

	public ISegmentGraph getGraph () {
		return this.graph;
	}

//	public List<IAgent> getAcceptors() {
//		return acceptors;
//	}
//
//	public List<IAgent> getProcessedBy() {
//		return processedBy;
//	}
//
//	public void addAcceptor(IAgent agent) {
//		if (!acceptors.contains(agent))	{
//			acceptors.add(agent);
//		}
//	}
//
//	public void clearAcceptors() {
//		acceptors.clear();
//	}

	public boolean isDirty() {
		return dirty;
	}

	public LayoutLabel getAnnotation() {
		return annotation;
	}

	public void setAnnotation(LayoutLabel annotation) {
		this.annotation = annotation;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlockedBy(IAgent blockedBy) {
		this.blockedBy = blockedBy;
	}

	public IAgent getBlockedBy() {
		return blockedBy;
	}

	public void setOwner(IAgent owner) {
		this.owner = owner;
	}

	/**
	 *
	 * @param child
	 */
	public void addAsChild (AgentState child)
	{
		if (!children.contains(child))
		{
			children.add(child);
		}
		if (!child.parents.contains(this))
		{
			child.parents.add(this);
		}
	}

	public void removeChild (AgentState child)
	{
		children.remove(child);
	}

	public List<AgentState> getChildren() {
		return children;
	}

	public List<AgentState> getDescendants() {
		return descendants;
	}

	public List<AgentState> getParents() {
		return parents;
	}

	public SegmentType getSegType() {
		return segType;
	}

	public void setSegType(SegmentType segType) {
		this.segType = segType;
	}

	public boolean isReconsider() {
		return reconsider;
	}

	public void setReconsider(boolean reconsider) {
		this.reconsider = reconsider;
	}

	public int getX1() 	{
		return getBounds().x;
	}

	public int getX2() 	{
		return getBounds().x + getBounds().width;
	}

	public int getY1() 	{
		return getBounds().y;
	}

	public int getY2() 	{
		return getBounds().y + getBounds().height;
	}

	@Override
	public String toString() {
		if (this.name!=null)
		{
			return this.name;
		}
		return super.toString();
	}

	/**
	 *
	 */
	public double computeUtility ()
	{
		this.utility = 0d;
		for (AgentState child : children)
		{
			utility += child.getUtility();
		}
		double util = Math.max(0, utility) * this.confidence;
		return util;
	}

	/**
	 *
	 * @param attribute
	 * @return
	 */
	public boolean containsAttribute(AttributeType attType)
	{
		for (AgentStateAttribute att : getAttributes())
		{
			if (att.getType()==attType)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * @return
	 */
	public boolean containsSemanticAttribute (WordSemantics semantics)
	{
		for (AgentStateAttribute att : getAttributes())
		{
			if (att.getType()==AttributeType.SEMANTIC)
			{
				SemanticAttribute semAtt = (SemanticAttribute) att;
				List<SemanticText> semList = semAtt.getSemantics();
				for (SemanticText semText : semList)
				{
					if (semText.containsSemantics(semantics))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	public void setUtility(double utility) {
		this.utility = utility;
	}

	public double getUtility() {
		return utility;
	}

	public List<AgentState> getDependents() {
		return dependents;
	}


	public void addAttribute (AgentStateAttribute attribute)
	{
		this.attributes.add(attribute);
	}

	public void removeAttribute (AgentStateAttribute attribute)
	{
		this.attributes.remove(attribute);
	}

	public List<AgentStateAttribute> getAttributes() {
		return attributes;
	}

	public List<IAgent> getSupport() {
		return support;
	}

	public List<AgentState> getConflictingStates() {
		return conflictingStates;
	}

	/**
	 * Add an agent as support
	 * @param agent
	 */
	public void addSupport (IAgent agent)
	{
		if (!this.support.contains(agent))
		{
			this.support.add(agent);
		}
	}

	/**
	 * Add a conflicting state
	 * @para m state
	 */
	public void addConflictingState (AgentState state)
	{
		if (!this.conflictingStates.contains(state))
		{
			this.conflictingStates.add(state);
		}
	}

	public DocNode getNode() {
		return node;
	}

	@Override
	public List<Class<?>> getProcessedBy() {
		return processedBy;
	}

	@Override
	public List<IAgent> getUtilizedBy() {
		return utilizedBy;
	}
}
