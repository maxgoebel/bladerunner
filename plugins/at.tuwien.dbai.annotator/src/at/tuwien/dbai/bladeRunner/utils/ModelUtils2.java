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
package at.tuwien.dbai.bladeRunner.utils;

import at.tuwien.prip.model.project.annotation.Annotation;
import at.tuwien.prip.model.project.annotation.AnnotationLabel;
import at.tuwien.prip.model.project.annotation.AnnotationType;
import at.tuwien.prip.model.project.annotation.LabelAnnotation;
import at.tuwien.prip.model.project.document.benchmark.BenchmarkDocument;

/**
 * ModelUtils2.java
 * 
 * 
 * 
 * @author mcg <mcgoebel@gmail.com> Aug 22, 2012
 */
public class ModelUtils2 {

	/**
	 * 
	 * @param benchDoc
	 * @param type
	 * @return
	 */
	public static Annotation findNamedAnnotation(BenchmarkDocument benchDoc,
			AnnotationType type) {
		for (Annotation annotation : benchDoc.getAnnotations()) {
			if (annotation.getType().equals(type)) {
				return annotation;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param benchDoc
	 * @param label
	 * @return
	 */
	public static LabelAnnotation getLabelAnnotation(
			BenchmarkDocument benchDoc, AnnotationLabel label) {
		for (Annotation annotation : benchDoc.getAnnotations()) {
			if (annotation.getType().equals(AnnotationType.LABEL)) {
				LabelAnnotation labelAnn = (LabelAnnotation) annotation;
				if (labelAnn.getLabel().equals(label)) {
					return labelAnn;
				}
			}
		}
		return null;
	}

}
