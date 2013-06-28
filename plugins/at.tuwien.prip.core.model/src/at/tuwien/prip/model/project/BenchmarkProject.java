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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import at.tuwien.prip.model.project.document.AbstractDocument;
import at.tuwien.prip.model.project.document.benchmark.Benchmark;
import at.tuwien.prip.model.project.document.benchmark.BenchmarkDocument;

/**
 * BenchmarkProject.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Nov 8, 2012
 */
@Entity
public class BenchmarkProject 
implements IProject
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Benchmark benchmark;
	
	private String name;
	
	private String projectResource;
	
	/**
	 * Constructor.
	 */
	public BenchmarkProject()
	{
		
	}
	
	/**
	 * Constructor.
	 */
	public BenchmarkProject(String name, String projectResource) 
	{
		this.name = name;
		this.projectResource = projectResource;
		this.benchmark = new Benchmark(name);
	}
	
	public Benchmark getBenchmark() {
		return benchmark;
	}
		
	@Override
	public String getName() {
		return name;
	}
	
//	protected abstract ProjectPage createProjectPage();
	
	@Override
	public void addDocument(AbstractDocument document) 
	{
		if (benchmark!=null && document instanceof BenchmarkDocument)
		{
			BenchmarkDocument item = (BenchmarkDocument) document;
			benchmark.getDocuments().add(item);
		}
	}

	@Override
	public List<AbstractDocument> getDocuments() {
		List<AbstractDocument> result = new ArrayList<AbstractDocument>();
		for (AbstractDocument doc : benchmark.getDocuments())
		{
			result.add(doc);
		}
		return result;
	}
	
	public void setBenchmark(Benchmark benchmark) {
		this.benchmark = benchmark;
	}

	@Override
	public void removeDocument(AbstractDocument document) 
	{
		if (benchmark!=null && document instanceof BenchmarkDocument)
		{
			BenchmarkDocument item = (BenchmarkDocument) document;
			benchmark.getDocuments().remove(item);
		}
	}
	
	@Override
	public String getProjectResource() {
		return projectResource;
	}

	public void setProjectResource(String projectResource) {
		this.projectResource = projectResource;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
}
