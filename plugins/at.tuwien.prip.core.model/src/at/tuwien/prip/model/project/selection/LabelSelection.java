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
package at.tuwien.prip.model.project.selection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.Transient;

import at.tuwien.prip.model.project.annotation.AnnotationLabel;

/**
 * LabelSelection.java
 * 
 *
 *
 * @author mcg <mcgoebel@gmail.com>
 * Nov 12, 2012
 */
@Entity
public class LabelSelection extends SinglePageSelection 
{
	@Transient
	private AnnotationLabel annotationLabel; //actual enum; not stored in db
	
	@Column(name="LABEL")  
	private int labelCode; // enum code gets stored in db

	@PrePersist
	void populateDBFields(){
		labelCode = annotationLabel.getCode();
	}

	@PostLoad
	void populateTransientFields(){
		annotationLabel = AnnotationLabel.valueOf(labelCode);
	}

	private String subLabel;

	@OneToOne
	private AbstractSelection selection;

	public LabelSelection() {
		super("LABEL");
	}
	
	public AnnotationLabel getAnnotationLabel() {
		return annotationLabel;
	}

	public String getSubLabel() {
		return subLabel;
	}

	public void setAnnotationLabel(AnnotationLabel label) {
		this.annotationLabel = label;
	}

	public void setSubLabel(String subLabel) {
		this.subLabel = subLabel;
	}

	public AbstractSelection getSelection() {
		return selection;
	}

	public void setSelection(AbstractSelection selection) {
		this.selection = selection;
	}
}
