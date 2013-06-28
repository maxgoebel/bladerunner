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
package at.tuwien.dbai.bladeRunner.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import at.tuwien.dbai.bladeRunner.LearnUIPlugin;

/**
 * PrefInitializer.java
 * 
 * Initialize our preferences at runtime.
 * 
 * @author mcg <mcgoebel@gmail.com>
 * @date May 23, 2011
 */
public class PrefInitializer extends AbstractPreferenceInitializer {
	public PrefInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = LearnUIPlugin.getDefault()
				.getPreferenceStore();
		store.setDefault("prefStoreName", "annotationTool");
		store.setDefault("docGraphDetail", "PDF Instruction");
	}

}
