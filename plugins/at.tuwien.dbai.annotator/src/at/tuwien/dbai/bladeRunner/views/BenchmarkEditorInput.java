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
package at.tuwien.dbai.bladeRunner.views;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import at.tuwien.dbai.bladeRunner.Activator;
import at.tuwien.prip.model.project.document.benchmark.BenchmarkDocument;

public class BenchmarkEditorInput implements IEditorInput {

	private BenchmarkDocument benchmark;

	public BenchmarkEditorInput(BenchmarkDocument benchmark) {
		this.benchmark = benchmark;
	}

	public BenchmarkDocument getBenchmarkDocument() {
		return benchmark;
	}

	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return Activator.getImageDescriptor("/icons/someicon.png");
	}

	@Override
	public IPersistableElement getPersistable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getToolTipText() {
		return "Benchmark Editor";
	}

	@Override
	public String getName() {
		if (this.benchmark.getName() == null) {
			return this.benchmark.getFileName();
		}
		return benchmark.getName();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BenchmarkEditorInput)
		{
			BenchmarkEditorInput in = (BenchmarkEditorInput) obj;
			BenchmarkDocument doc = in.getBenchmarkDocument();
			if (doc.getFileName().equals(getBenchmarkDocument().getFileName()))
			{
				return true;
			}
		}
		return super.equals(obj);
	}
}
