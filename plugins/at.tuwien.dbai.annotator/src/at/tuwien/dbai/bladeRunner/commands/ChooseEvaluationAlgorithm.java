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
package at.tuwien.dbai.bladeRunner.commands;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

/**
 * ChooseEvaluationAlgorithm.java
 * 
 * 
 * 
 * @author: Max Goebel <mcgoebel@gmail.com>
 * @date: Mar 26, 2011
 */
public class ChooseEvaluationAlgorithm extends AbstractHandler implements
		IElementUpdater {

	UIElement element;

	public ChooseEvaluationAlgorithm() {

		ICommandService commandService = (ICommandService) PlatformUI
				.getWorkbench().getService(ICommandService.class);
		final Command comm1 = commandService
				.getCommand("at.tuwien.prip.docwrap.ide.chooseUllmann");
		final Command comm2 = commandService
				.getCommand("at.tuwien.prip.docwrap.ide.chooseGED");
		comm1.addExecutionListener(new IExecutionListener() {

			@Override
			public void preExecute(String commandId, ExecutionEvent event) {
			}

			@Override
			public void postExecuteSuccess(String commandId, Object returnValue) {
				try {
					element.setText(comm1.getName());
				} catch (NotDefinedException e) {
					e.printStackTrace();
				}
				comm1.setEnabled(false);
				comm2.setEnabled(true);
			}

			@Override
			public void postExecuteFailure(String commandId,
					ExecutionException exception) {
			}

			@Override
			public void notHandled(String commandId,
					NotHandledException exception) {
			}
		});
		comm2.addExecutionListener(new IExecutionListener() {

			@Override
			public void preExecute(String commandId, ExecutionEvent event) {
			}

			@Override
			public void postExecuteSuccess(String commandId, Object returnValue) {
				try {
					element.setText(comm2.getName());
				} catch (NotDefinedException e) {
					e.printStackTrace();
				}
				comm1.setEnabled(false);
				comm2.setEnabled(true);
			}

			@Override
			public void postExecuteFailure(String commandId,
					ExecutionException exception) {
			}

			@Override
			public void notHandled(String commandId,
					NotHandledException exception) {
			}
		});
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		return null;
	}

	@Override
	public void updateElement(UIElement element, Map parameters) {
		this.element = element;
	}

}
