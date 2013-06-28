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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import at.tuwien.prip.common.utils.ListUtils;
import at.tuwien.prip.model.project.document.benchmark.Benchmark;
import at.tuwien.prip.model.project.document.benchmark.BenchmarkDocument;
import at.tuwien.prip.model.project.document.benchmark.BenchmarkModel;

/**
 * 
 * @author max
 * 
 */
public class BenchmarkContentProvider implements ITreeContentProvider {

	BenchmarkModel model;

	public BenchmarkContentProvider() {
		super();
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
//		model = (BenchmarkModel) newInput;
	}

	public Object[] getElements(Object inputElement) {
		
		if (inputElement instanceof ArrayList<?>)
		{
			@SuppressWarnings("unchecked")
			ArrayList<BenchmarkDocument> objs = (ArrayList<BenchmarkDocument>) inputElement;
			Collections.sort(objs, new Comparator<BenchmarkDocument>() {
				@Override
				public int compare(BenchmarkDocument o1, BenchmarkDocument o2) {
					return o1.getFileName().compareTo(o2.getFileName());
				}
			});
			return ((ArrayList) inputElement).toArray();
		}
		return getChildren(inputElement);
		// if (inputElement instanceof BenchmarkModel)
		// {
		// BenchmarkModel benchmarkModel = (BenchmarkModel) inputElement;
		// return new Object[]{benchmarkModel};
		// }
		// else if (inputElement instanceof Benchmark)
		// {
		// Benchmark benchmark = (Benchmark) inputElement;
		// return new Object[]{benchmark};
		// }
		// else if (inputElement instanceof BenchmarkItem)
		// {
		// BenchmarkItem item = (BenchmarkItem) inputElement;
		// return new Object[]{item};
		// }
		// if (model == null)
		// return null;
		//
		// return ListUtils.toArray(model.getBenchmarks(),Benchmark.class);
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof BenchmarkModel) {
			BenchmarkModel benchmarkModel = (BenchmarkModel) element;
			return benchmarkModel.getBenchmarks().size() > 0;
		} else if (element instanceof Benchmark) {
			Benchmark benchmark = (Benchmark) element;
			return benchmark.getDocuments().size() > 0;
		}
		return false;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof BenchmarkModel) {
			BenchmarkModel benchmarkModel = (BenchmarkModel) parentElement;
			return ListUtils.toArray(benchmarkModel.getBenchmarks(),
					Benchmark.class);
		} else if (parentElement instanceof Benchmark) {
			Benchmark benchmark = (Benchmark) parentElement;
			//order by filename
			List<BenchmarkDocument> documents = benchmark.getDocuments();
			Collections.sort(documents, new Comparator<BenchmarkDocument>() {
				@Override
				public int compare(BenchmarkDocument o1, BenchmarkDocument o2) {
					return o1.getFileName().compareTo(o2.getFileName());
				}
			});
			return ListUtils.toArray(documents,	BenchmarkDocument.class);
		}
		return null;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}
}
