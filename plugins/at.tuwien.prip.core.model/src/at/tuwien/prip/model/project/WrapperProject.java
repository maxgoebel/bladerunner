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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import at.tuwien.prip.model.project.document.AbstractDocument;

/**
 * WrapperProject.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Jun 3, 2012
 */
@Entity
public class WrapperProject 
implements IProject, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6412308703835283293L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToMany
	private List<AbstractDocument> documents;
	
	private String name;
	
	private String projectResource;
	
	/**
	 * Constructor.
	 */
	public WrapperProject() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Constructor.
	 * @param name
	 * @param projectResource
	 */
	public WrapperProject(String name, String projectResource) 
	{	
		this.name = name;
		this.projectResource = projectResource;
		this.documents = new ArrayList<AbstractDocument>();
	}

//	protected abstract ProjectPage createProjectPage();
	
	public List<AbstractDocument> getDocuments()
	{
		return documents;
	}
	
	public String getProjectResource() {
		return projectResource;
	}

//	@Override
	public void addDocument(AbstractDocument document)
	{
		if (!documents.contains(document))
		{
			documents.add(document);
		}
	}
	
//	@Override
	public void removeDocument(AbstractDocument document)
	{
		if (documents.contains(document))
		{
			documents.remove(document);
		}
	}
	
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
}
