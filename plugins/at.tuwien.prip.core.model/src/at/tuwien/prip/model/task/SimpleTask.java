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

import java.util.List;

import at.tuwien.prip.model.task.actions.TaskAction;

/**
 * SimpleTask.java
 *
 * A simple task model.
 * This represents a workflow.
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Feb 17, 2011
 */
public class SimpleTask {

	private String name;
	
	private String description;
	
	private List<TaskAction> actions;
		
	private TaskState start;
	
	private TaskState end;

	public TaskState getStart() {
		return start;
	}

	public void setStart(TaskState start) {
		this.start = start;
	}

	public TaskState getEnd() {
		return end;
	}

	public void setEnd(TaskState end) {
		this.end = end;
	}
	
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

	public List<TaskAction> getActions() {
		return actions;
	}

	public void setActions(List<TaskAction> actions) {
		this.actions = actions;
	}

}
