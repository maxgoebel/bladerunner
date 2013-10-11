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
package at.tuwien.dbai.bladeRunner.views.bench;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import at.tuwien.dbai.bladeRunner.Activator;
import at.tuwien.dbai.bladeRunner.utils.TextUtils;
import at.tuwien.prip.common.utils.StringUtils;
import at.tuwien.prip.model.project.document.benchmark.Benchmark;
import at.tuwien.prip.model.project.document.benchmark.BenchmarkDocument;
import at.tuwien.prip.model.project.document.benchmark.HTMLBenchmarkDocument;
import at.tuwien.prip.model.project.document.benchmark.PdfBenchmarkDocument;

/**
 * BenchmarkLabelProvider.java
 * 
 * 
 * @author mcgoebel@gmail.com
 * @date Feb 17, 2013
 */
public class BenchmarkLabelProvider extends LabelProvider {

	/**
	 * Constructor.
	 */
	public BenchmarkLabelProvider() {

	}

	@Override
	public Image getImage(Object element)
	{
		if (element instanceof PdfBenchmarkDocument) 
		{
			ImageDescriptor desc = Activator.getImageDescriptor("/icons/annotator/obj16/pdfDoc16.png");
			if (desc != null)
			{
				Image img = desc.createImage();
				return img;
			}
		}
		else if (element instanceof HTMLBenchmarkDocument) 
		{
			ImageDescriptor desc = Activator.getImageDescriptor("/icons/annotator/obj16/htmlDoc16.png");
			if (desc != null)
			{
				Image img = desc.createImage();
				return img;
			}
		}
		return super.getImage(element);
	}

	@Override
	public String getText(Object element) {
		if (element instanceof Benchmark) {
			return ((Benchmark) element).getName();
		}
		if (element instanceof BenchmarkDocument) {
			String name = StringUtils.trimTo(((BenchmarkDocument) element).getFileName(), 58);
			return name;
		}
		return super.getText(element);
	}
}
