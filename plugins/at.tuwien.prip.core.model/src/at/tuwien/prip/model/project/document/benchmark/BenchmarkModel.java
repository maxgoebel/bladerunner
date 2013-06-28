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


/**
 *
 * @author max
 *
 */
public class BenchmarkModel 
{

	private List<Benchmark> benchmarks;
	
	private Benchmark currentBenchmark;
	private BenchmarkDocument currentDocument;
	
	/**
	 * Constructor.
	 */
	public BenchmarkModel() {
		benchmarks = new ArrayList<Benchmark>();
	}
	
	public List<Benchmark> getBenchmarks() {
		return benchmarks;
	}
	
	public Benchmark getCurrentBenchmark() {
		return currentBenchmark;
	}
	
	public BenchmarkDocument getCurrentDocument() {
		return currentDocument;
	}
	
	public void setCurrentBenchmark(Benchmark currentBenchmark) {
		this.currentBenchmark = currentBenchmark;
	}
	
	public void setCurrentDocument(BenchmarkDocument currentDocument) {
		this.currentDocument = currentDocument;
	}
}
