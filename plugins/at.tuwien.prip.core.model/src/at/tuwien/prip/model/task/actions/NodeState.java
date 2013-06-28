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
package at.tuwien.prip.model.task.actions;

import at.tuwien.prip.model.graph.base.BaseNode;

/**
 * NodeState.java
 *
 *
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Mar 29, 2011
 */
public class NodeState implements IState {

	private BaseNode node;

	public NodeState(BaseNode node) {
		this.node = node;
	}
	
	public void setNode(BaseNode node) {
		this.node = node;
	}

	public BaseNode getNode() {
		return node;
	}
	
	@Override
	public String toString() {
		return node.toString();
	}
}
