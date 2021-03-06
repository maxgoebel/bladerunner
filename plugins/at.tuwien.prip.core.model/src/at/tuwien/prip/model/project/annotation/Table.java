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
package at.tuwien.prip.model.project.annotation;

import java.util.List;

public class Table {

	List<Region> regions;
	
	int id;
	
	public Table(int id) {
		this.id = id;
	}
	
	public void setRegions(List<Region> regions) {
		this.regions = regions;
	}
	
	public List<Region> getRegions() {
		return regions;
	}
	
	public int getId() {
		return id;
	}
	
}
