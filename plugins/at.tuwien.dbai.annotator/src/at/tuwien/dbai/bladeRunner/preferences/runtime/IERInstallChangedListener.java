/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package at.tuwien.dbai.bladeRunner.preferences.runtime;

import org.eclipse.jface.util.PropertyChangeEvent;

import at.tuwien.dbai.bladeRunner.DocWrapUIUtils;

/**
 * A ER install changed listener is notified when the workspace default ER
 * install changes, or when an attribute of a specific ER install changes.
 * Listeners register with <code>ExtractionRuntime</code>.
 * <p>
 * Clients may implement this interface.
 * </p>
 * 
 * @since 2.0
 */
public interface IERInstallChangedListener {

	/**
	 * Property constant indicating the name associated with a ER install has
	 * changed.
	 */
	public static final String PROPERTY_NAME = DocWrapUIUtils
			.getUniqueIdentifier() + ".PROPERTY_NAME"; //$NON-NLS-1$

	/**
	 * Property constant indicating the install location of a ER install has
	 * changed.
	 */
	public static final String PROPERTY_INSTALL_LOCATION = DocWrapUIUtils
			.getUniqueIdentifier() + ".PROPERTY_INSTALL_LOCATION"; //$NON-NLS-1$

	/**
	 * Property constant indicating the ER arguments associated with a ER
	 * install has changed.
	 * 
	 * @since 3.2
	 */
	public static final String PROPERTY_ER_ARGUMENTS = DocWrapUIUtils
			.getUniqueIdentifier() + ".PROPERTY_ER_ARGUMENTS"; //$NON-NLS-1$

	/**
	 * Notification that the workspace default ER install has changed.
	 * 
	 * @param previous
	 *            the ER install that was previously assigned to the workspace,
	 *            possibly <code>null</code>
	 * @param current
	 *            the ER install that is currently assigned to the workspace,
	 *            possibly <code>null</code>
	 */
	public void defaultERInstallChanged(IERInstall previous, IERInstall current);

	/**
	 * Notification that a property of a ER install has changed.
	 * 
	 * @param event
	 *            event describing the change. The ER that has changed is the
	 *            source object associated with the event.
	 */
	public void erChanged(PropertyChangeEvent event);

	/**
	 * Notification that a ER has been created.
	 * 
	 * @param er
	 *            the er that has been created
	 */
	public void erAdded(IERInstall er);

	/**
	 * Notification that a ER has been disposed.
	 * 
	 * @param er
	 *            the er that has been disposed
	 */
	public void erRemoved(IERInstall er);

}
