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
package at.tuwien.dbai.bladeRunner.control;

import java.util.ArrayList;
import java.util.List;

import at.tuwien.prip.model.project.document.benchmark.BenchmarkModel;

/**
 * ModelController.java
 * 
 * 
 * @author mcgoebel@gmail.com
 * @date Feb 19, 2013
 */
public class ModelController {

	private BenchmarkModel model;

	private List<IModelChangedListener> listeners;

	/**
	 * 
	 */
	public ModelController() {
		this.listeners = new ArrayList<IModelChangedListener>();
	}

	/**
	 * 
	 * @param model
	 */
	public void modelChanged(BenchmarkModel model) 
	{
		this.model = model;
		ModelChangedEvent event = new ModelChangedEvent(model);

		// notify observer:
		for (IModelChangedListener listener : listeners) {
			listener.modelChanged(event);
		}
	}

	public BenchmarkModel getModel() {
		return model;
	}

	public void addModelChangedListener(IModelChangedListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}

	public void removeModelChangedListener(IModelChangedListener listener) {
		if (this.listeners.contains(listener)) {
			this.listeners.remove(listener);
		}
	}

}
