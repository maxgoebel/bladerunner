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
package at.tuwien.dbai.bladeRunner.preferences.runtime;

import java.io.File;

/**
 * Represents a particular installation of a ER. A ER instance holds all
 * parameters specific to a ER installation. ER instances can be created and
 * configured dynamically at run-time. This is typically done by the user
 * interactively in the UI.
 * <p>
 * A ER install is responsible for creating ER runners to launch an Web
 * navigation or extraction program in a specific mode.
 * </p>
 */
public interface IERInstall {
	/**
	 * Returns a ER runner that runs this installed ER in the given mode.
	 * 
	 * @param mode
	 *            the mode the ER should be launched in; one of the constants
	 *            declared in <code>org.eclipse.debug.core.ILaunchManager</code>
	 * @return a ERRunner for a given mode May return <code>null</code> if the
	 *         given mode is not supported by this ER.
	 * @see org.eclipse.debug.core.ILaunchManager
	 */
	// IVMRunner getVMRunner(String mode);

	/**
	 * Returns the id for this ER. ER IDs are unique within the ERs of a given
	 * ER type. The ER id is not intended to be presented to users.
	 * 
	 * @return the ER identifier. Must not return <code>null</code>.
	 */
	String getId();

	/**
	 * Returns the display name of this ER. The ER name is intended to be
	 * presented to users.
	 * 
	 * @return the display name of this ER. May return <code>null</code>.
	 */
	String getName();

	/**
	 * Sets the display name of this ER. The ER name is intended to be presented
	 * to users.
	 * 
	 * @param name
	 *            the display name of this ER
	 */
	void setName(String name);

	/**
	 * Returns the root directory of the install location of this ER.
	 * 
	 * @return the root directory of this ER installation. May return
	 *         <code>null</code>.
	 */
	File getInstallLocation();

	/**
	 * Sets the root directory of the install location of this ER.
	 * 
	 * @param installLocation
	 *            the root directory of this ER installation
	 */
	void setInstallLocation(File installLocation);

	/**
	 * Returns ER arguments to be used with this er install whenever this ER is
	 * launched as a raw string, or <code>null</code> if none.
	 * 
	 * @return ER arguments to be used with this er install whenever this Er is
	 *         launched as a raw string, or <code>null</code> if none
	 */
	public String getERArgs();

	/**
	 * Sets ER arguments to be used with this er install whenever this ER is
	 * launched as a raw string, possibly <code>null</code>.
	 * 
	 * @param erArgs
	 *            ER arguments to be used with this er install whenever this ER
	 *            is launched as a raw string, possibly <code>null</code>
	 */
	public void setERArgs(String erArgs);

}
