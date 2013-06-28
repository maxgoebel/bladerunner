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

/**
 * 
 * ActionState.java
 *
 * An action state is the combination of a state and an action.
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Mar 29, 2011
 */
public class ActionState {

	private IState state;

	private Action action;

	public IState getState() {
		return state;
	}

	public void setState(IState state) {
		this.state = state;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public Action getAction() {
		return action;
	}
	
	@Override
	public String toString() {
		return state.toString() + "\nAction: " +action.getName();
	}

}
