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
package at.tuwien.prip.model.project;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


/**
 * 
 * ProjectModel.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Jun 10, 2012
 */
@Entity
public class ProjectModel
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private List<IProject> projects;

	private IProject activeProject;

	public ProjectModel() {
		this.projects = new ArrayList<IProject>();
	}

	public IProject getActiveProject() {
		return activeProject;
	}

	public void setActiveProject(IProject activeProject) {
		this.activeProject = activeProject;
	}

	public List<IProject> getProjects() {
		return projects;
	}

	public void addProject(IProject project) {
		if (!projects.contains(project))
		{
			projects.add(project);
		}
	}

	public void removeProject(IProject project)
	{
		if (projects.contains(project))
		{
			projects.remove(project);
		}
		if (activeProject==project)
		{
			activeProject=null;
		}
	}
}
