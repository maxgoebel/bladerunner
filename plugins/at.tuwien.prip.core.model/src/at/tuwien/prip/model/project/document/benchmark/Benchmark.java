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
package at.tuwien.prip.model.project.document.benchmark;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;


/**
 * 
 * @author max
 *
 */
@Entity
public class Benchmark
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String uri;
	
	@OneToMany
	private List<BenchmarkDocument> documents;
		
	private String name;
	
	/**
	 * Constructor.
	 */
	public Benchmark()
	{
		documents = new ArrayList<BenchmarkDocument>();
	}
	
	/**
	 * Constructor.
	 * @param name
	 */
	public Benchmark(String uri) 
	{
		this();
		this.uri = uri;
		this.name = uri;
	}
	
	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public List<BenchmarkDocument> getDocuments() {
		return documents;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
