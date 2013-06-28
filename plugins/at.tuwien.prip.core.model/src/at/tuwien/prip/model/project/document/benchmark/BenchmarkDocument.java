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

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import at.tuwien.prip.model.graph.ISegmentGraph;
import at.tuwien.prip.model.project.annotation.Annotation;
import at.tuwien.prip.model.project.document.AbstractDocument;
import at.tuwien.prip.model.project.document.DocumentFormat;

/**
 * 
 * @author max
 *
 */
@Entity
public class BenchmarkDocument extends AbstractDocument
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6488924500835941107L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany
	protected List<Annotation> annotations;
	
	@OneToMany
	protected List<Annotation> groundTruth;
	
	
	/**
	 * Constructor.
	 */
	public BenchmarkDocument() 
	{
		this.annotations = new ArrayList<Annotation>();
		this.groundTruth = new ArrayList<Annotation>();
	}
	
	public List<Annotation> getAnnotations() {
		return annotations;
	}
	
	public void setAnnotations(List<Annotation> annotations) {
		this.annotations = annotations;
	}
	
	public List<Annotation> getGroundTruth() {
		return groundTruth;
	}
	
	public void setGroundTruth(List<Annotation> groundTruth) {
		this.groundTruth = groundTruth;
	}
		
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public String getUri() {
		return uri;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;		
	}

	@Override
	public DocumentFormat getFormat() {
		return format;
	}

	@Override
	public void setFormat(DocumentFormat format) {
		this.format = format;
	}

	@Override
	public double getScale() {
		return scale;
	}

	@Override
	public void setScale(double scale) {
		this.scale = scale;
	}

	@Override
	public Rectangle getBounds() {
		return bounds;
	}
	
}
