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
package at.tuwien.dbai.bladeRunner;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.handlers.IHandlerService;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	private IWorkbenchWindow window;

	private TrayItem trayItem;
	private Image trayImage;

	private final static String COMMAND_ID = "at.tuwien.prip.docwrap.annotator.exitCommand";

	public ApplicationWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(1400, 900));
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(true);
		configurer.setShellStyle(SWT.MODELESS | SWT.SHELL_TRIM | SWT.BORDER);
		configurer.setShowPerspectiveBar(false);
		configurer.setTitle("BladeRunner");
		configurer.setShowProgressIndicator(true);
	}

	// As of here is the new stuff
	@Override
	public void postWindowOpen() {
		super.postWindowOpen();

		window = getWindowConfigurer().getWindow();
		trayItem = initTaskItem(window);
		// Some OS might not support tray items
		if (trayItem != null) {
			// minimizeBehavior();
			// Create exit and about action on the icon
			hookPopupMenu();
		}

		IStatusLineManager statusline = getWindowConfigurer()
				.getActionBarConfigurer().getStatusLineManager();
		statusline.setMessage(null, "Ready");

		IContributionItem[] mItems, mSubItems;
		IMenuManager mm = getWindowConfigurer().getActionBarConfigurer()
				.getMenuManager();
		mItems = mm.getItems();
		for (int i = 0; i < mItems.length; i++) {
			if (mItems[i] instanceof MenuManager) {
				mSubItems = ((MenuManager) mItems[i]).getItems();
				for (int j = 0; j < mSubItems.length; j++) {
					if (mItems[i].getId().equals("file"))
						((MenuManager) mItems[i])
								.remove("org.eclipse.ui.openLocalFile");
					else if (mItems[i].getId().equals("help")) {
						((MenuManager) mItems[i]).remove("group.updates");
						((MenuManager) mItems[i])
								.remove("org.eclipse.update.ui.updateMenu");
						((MenuManager) mItems[i])
								.remove("org.eclipse.ui.actions.showKeyAssistHandler");
					}
				}
			}
			if (mItems[i].getId().equals("org.eclipse.ui.run")) 
			{
				mm.remove(mItems[i]);
			} 
			else if (mItems[i].getId()
					.startsWith("org.eclipse.gmf.examples.")) {
				
				mm.remove(mItems[i]);
			} 
			else if (mItems[i].getId().equals(
					"org.eclipse.ui.externaltools.ExternalToolsSet")) 
			{
				mm.remove(mItems[i]);
			}
			else if (mItems[i].getId().startsWith("org.eclipse.search.")) {
				mm.remove(mItems[i]);
			}
		}
		mm.update(true);

		ICoolBarManager cm = getWindowConfigurer().getActionBarConfigurer()
				.getCoolBarManager();
		cm.removeAll();
		cm.update(true);

	}

	// Add a listener to the shell
	// private void minimizeBehavior() {
	// window.getShell().addShellListener(new ShellAdapter() {
	// // If the window is minimized hide the window
	// public void shellIconified(ShellEvent e) {
	// window.getShell().setVisible(false);
	// }
	// });
	// // If user double-clicks on the tray icons the application will be
	// // visible again
	// trayItem.addListener(SWT.DefaultSelection, new Listener() {
	// public void handleEvent(Event event) {
	// Shell shell = window.getShell();
	// if (!shell.isVisible()) {
	// window.getShell().setMinimized(false);
	// shell.setVisible(true);
	// }
	// }
	// });
	// }

	// We hook up on menu entry which allows to close the application
	private void hookPopupMenu() {
		trayItem.addListener(SWT.MenuDetect, new Listener() {
			public void handleEvent(Event event) {
				Menu menu = new Menu(window.getShell(), SWT.POP_UP);

				// Creates a new menu item that terminates the program
				// when selected
				MenuItem exit = new MenuItem(menu, SWT.NONE);
				exit.setText("Goodbye!");
				exit.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event event) {
						// Lets call our command
						IHandlerService handlerService = (IHandlerService) window
								.getService(IHandlerService.class);
						try {
							handlerService.executeCommand(COMMAND_ID, null);
						} catch (Exception ex) {
							throw new RuntimeException(COMMAND_ID);
						}
					}
				});
				// We need to make the menu visible
				menu.setVisible(true);
			}
		});
	}

	// This methods create the tray item and return a reference
	private TrayItem initTaskItem(IWorkbenchWindow window) {
		// final Tray tray = window.getShell().getDisplay().getSystemTray();
		// TrayItem trayItem = new TrayItem(tray, SWT.NONE);
		// trayImage = AbstractUIPlugin.imageDescriptorFromPlugin(
		// "at.tuwien.prip.docwrap.ide", "/icons/full/brand/priplogo.png")
		// .createImage();
		// trayItem.setImage(trayImage);
		// trayItem.setToolTipText("TrayItem");
		// return trayItem;
		return null;

	}

	// We need to clean-up after ourself
	@Override
	public void dispose() {
		if (trayImage != null) {
			trayImage.dispose();
		}
		if (trayItem != null) {
			trayItem.dispose();
		}
	}

}
