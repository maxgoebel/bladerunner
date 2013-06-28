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
package at.tuwien.prip.model.task;

import java.util.Map;

import at.tuwien.prip.model.task.actions.TaskAction;

/**
 * TaskState.java
 *
 *
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Feb 17, 2011
 */
public class TaskState {

	private String name;
	
	private String description;
	
	private boolean isFinished;
	
	private Map<TaskAction, TaskState> transitionMatrix;
	
	private TaskState previous;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isFinished() {
		return isFinished;
	}

	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}

	public Map<TaskAction, TaskState> getTransitionMatrix() {
		return transitionMatrix;
	}

	public void setTransitionMatrix(Map<TaskAction, TaskState> transitionMatrix) {
		this.transitionMatrix = transitionMatrix;
	}

	public TaskState getPrevious() {
		return previous;
	}

	public void setPrevious(TaskState previous) {
		this.previous = previous;
	}
		
}
