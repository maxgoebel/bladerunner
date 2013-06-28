/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies Ltd. - ongoing enhancements
 *******************************************************************************/
package org.eclipse.atf.mozilla.ide.ui.netmon;

import org.eclipse.atf.mozilla.ide.network.IHTTPRequest;
import org.eclipse.atf.mozilla.ide.network.IHTTPResponse;
import org.eclipse.atf.mozilla.ide.network.INetworkCall;
import org.eclipse.atf.mozilla.ide.network.IRequest;
import org.eclipse.atf.mozilla.ide.network.IResponse;
import org.eclipse.atf.mozilla.ide.network.IStatusChange;
import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.atf.mozilla.ide.ui.netmon.payload.IPayloadRenderStrategy;
import org.eclipse.atf.mozilla.ide.ui.netmon.payload.ITextPayloadRenderStrategy;
import org.eclipse.atf.mozilla.ide.ui.netmon.payload.ImagePayloadRenderStrategy;
import org.eclipse.atf.mozilla.ide.ui.netmon.payload.PayloadRenderStrategyFactory;
import org.eclipse.atf.mozilla.ide.ui.netmon.payload.SimpleTextPayloadRenderStrategy;
import org.eclipse.atf.mozilla.ide.ui.netmon.payload.UnsupportedContentStrategy;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.progress.UIJob;

public class RequestResponsePanel {

	protected Label requestLabel = null;
	protected Label responseLabel = null;

	//call details
	protected SourceViewer requestText = null; //request body
	protected SourceViewer responseText = null; //response body
	protected TableViewer requestHeadersViewer = null; //request headers
	protected TableViewer responseHeadersViewer = null; //response headers

	protected Composite responseUnsupported;
	protected Composite responseImage;
	protected Label imageLabel;
	protected CTabItem responseBodyItem;

	//Copy Menu
	protected Menu menu;
	protected MenuItem copy;
	protected Clipboard clipboard;

	//Formatting details
	protected Button formatButton;

	private IStructuredSelection selection;

	/**
	 * This is a self scheduling Job that runs in the UI thread (because there is acess to UI
	 * components) and tries to get the body of the response. The Response object could
	 * be waiting for access to cache so the response is not guaranteed to be ready.
	 * 
	 * @author Gino Bustelo
	 *
	 */
	protected class ResponseBodyGetterJob extends UIJob {

		protected int triesLeft = 5;

		protected final static int DELAY = 250;

		protected IHTTPResponse response = null;

		public ResponseBodyGetterJob(IHTTPResponse response) {
			super("Response Body Getter");
			this.response = response;
		}

		public IStatus runInUIThread(IProgressMonitor monitor) {

			//			MozIDEUIPlugin.debug( "ResponseBodyGetterJob: tries left <"+triesLeft+">..." );

			triesLeft--;

			Object body = response.getBody();

			if (!monitor.isCanceled()) {
				if (body != null) {

					//					MozIDEUIPlugin.debug( "ResponseBodyGetterJob: got body, setting..." );

					//set the text in the UI and that's it
					responseText.getDocument().set(body.toString());

				} else {
					//reschedule if not reach max
					if (triesLeft > 0) {
						//						MozIDEUIPlugin.debug( "ResponseBodyGetterJob: try again..." );
						schedule(DELAY);
					} else {
						//done trying
						//						MozIDEUIPlugin.debug( "ResponseBodyGetterJob: done trying." );
						responseText.getDocument().set("Cache not responding!");
					}

				}
			}

			return Status.OK_STATUS;
		}

	};

	//cached instance if need to cancel
	protected ResponseBodyGetterJob responseBodyGetterJob = null;

	public RequestResponsePanel(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;
		layout.marginHeight = 1;
		layout.marginWidth = 1;
		layout.verticalSpacing = 1;

		composite.setLayout(layout);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		requestLabel = new Label(composite, SWT.NONE);
		requestLabel.setText("Request:");
		requestLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false));

		Composite comp2 = new Composite(composite, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 1;
		layout.marginWidth = 1;
		layout.verticalSpacing = 1;
		layout.makeColumnsEqualWidth = false;
		comp2.setLayout(layout);
		comp2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		responseLabel = new Label(comp2, SWT.NONE);
		responseLabel.setText("Response:");
		responseLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false));

		formatButton = new Button(comp2, SWT.CHECK);
		formatButton.setText("Format");
		formatButton.setVisible(false);
		formatButton.setSelection(false);

		formatButton.addSelectionListener(new SelectionListener() {

			/**
			 * If this button is visible, we can assume that the content is text.
			 */
			public void widgetSelected(SelectionEvent e) {
				if (selection == null)
					return;

				INetworkCall call = (INetworkCall) selection.getFirstElement();

				IResponse res = call.getResponse();

				if (res instanceof IHTTPResponse) {
					PayloadRenderStrategyFactory strategyFactory = PayloadRenderStrategyFactory.getInstance();

					//we can assume that if we are here, the content is text
					ITextPayloadRenderStrategy strategy = (ITextPayloadRenderStrategy) strategyFactory.getStrategy((IHTTPResponse) res);

					SourceViewerConfiguration viewerConf = strategy.getConfiguration();

					responseText.unconfigure();

					if (viewerConf != null && formatButton.getSelection()) {
						responseText.configure(viewerConf);
					}

					responseText.refresh();
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}

		});

		//insert a tab panel with Headers and Body tabs

		//REQUEST TAB FOLDER
		CTabFolder requestTabFolder = new CTabFolder(composite, SWT.BOTTOM | SWT.BORDER | SWT.FLAT);
		requestTabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		CTabItem requestHeaderItem = new CTabItem(requestTabFolder, SWT.NONE);
		requestHeaderItem.setText("Headers");
		CTabItem requestBodyItem = new CTabItem(requestTabFolder, SWT.NONE);
		requestBodyItem.setText("Body");

		//request headers table
		requestHeadersViewer = createHeadersViewer(requestTabFolder, new HeaderContentProvider());

		//request body text
		requestText = createBodyText(requestTabFolder);

		requestHeaderItem.setControl(requestHeadersViewer.getControl());
		requestBodyItem.setControl(requestText.getControl());

		requestTabFolder.setSelection(requestHeaderItem);

		//RESPONSE TAB FOLDER
		CTabFolder responseTabFolder = new CTabFolder(composite, SWT.BOTTOM | SWT.BORDER | SWT.FLAT);
		responseTabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		CTabItem responseHeaderItem = new CTabItem(responseTabFolder, SWT.NONE);
		responseHeaderItem.setText("Headers");
		responseBodyItem = new CTabItem(responseTabFolder, SWT.NONE);
		responseBodyItem.setText("Body");

		//response headers table
		responseHeadersViewer = createHeadersViewer(responseTabFolder, new HeaderContentProvider());

		//response body text
		responseText = createBodyText(responseTabFolder);

		responseImage = new ScrolledComposite(responseTabFolder, SWT.H_SCROLL | SWT.V_SCROLL);
		// this is a system color, and must not be disposed
		Color white = parent.getDisplay().getSystemColor(SWT.COLOR_WHITE);
		responseImage.setBackground(white);

		//responseImage.setLayout( new FillLayout() );
		imageLabel = new Label(responseImage, SWT.NONE);
		((ScrolledComposite) responseImage).setContent(imageLabel);
		imageLabel.setText("Image Here");
		imageLabel.setSize(100, 50);

		responseUnsupported = new Composite(responseTabFolder, SWT.NONE);
		responseUnsupported.setLayout(new FillLayout());

		responseImage.setBackground(white);

		Label unsupportedLabel = new Label(responseUnsupported, SWT.NONE);
		unsupportedLabel.setText("Cannot display... Unsupported MIME type!");

		//body = new Composite(responseTabFolder, SWT.NONE );

		responseHeaderItem.setControl(responseHeadersViewer.getControl());
		//GINO: REDO FORMATING: responseBodyItem.setControl( body );
		responseBodyItem.setControl(responseText.getControl());

		responseTabFolder.setSelection(responseHeaderItem);

		createCopyMenu();
	}

	private void createCopyMenu() {
		//Set up copy menu for response text
		menu = new Menu(responseText.getControl());
		clipboard = new Clipboard(menu.getDisplay());
		responseText.getControl().setMenu(menu);
		menu.setEnabled(true);
		copy = new MenuItem(menu, SWT.PUSH);
		copy.setText("Copy");
		copy.setImage(MozIDEUIPlugin.getDefault().getImage(MozIDEUIPlugin.COPYRESPONSE_ID));
		copy.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				try {
					Point p = responseText.getSelectedRange();
					int offset = p.x;
					int length = p.y;
					String content = responseText.getDocument().get(offset, length);
					if (content != null && content.length() > 0) {
						clipboard.setContents(new String[] { content }, new Transfer[] { TextTransfer.getInstance() });
					}
				} catch (BadLocationException e1) {
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		//set up copy menu for request text
		menu = new Menu(requestText.getControl());
		requestText.getControl().setMenu(menu);
		menu.setEnabled(true);
		copy = new MenuItem(menu, SWT.PUSH);
		copy.setText("Copy");
		copy.setImage(MozIDEUIPlugin.getDefault().getImage(MozIDEUIPlugin.COPYRESPONSE_ID));
		copy.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				try {
					Point p = requestText.getSelectedRange();
					int offset = p.x;
					int length = p.y;
					String content = requestText.getDocument().get(offset, length);
					if (content != null && content.length() > 0) {
						clipboard.setContents(new String[] { content }, new Transfer[] { TextTransfer.getInstance() });
					}
				} catch (BadLocationException e1) {
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	/*
	 * Creates the table that is used to show the headers for either the request
	 * or response.
	 */
	protected TableViewer createHeadersViewer(Composite parent, HeaderContentProvider provider) {
		TableViewer headerViewer = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);

		Table t = headerViewer.getTable();

		t.setLinesVisible(true);
		t.setHeaderVisible(false);

		// 1st column 
		TableColumn column = new TableColumn(t, SWT.LEFT | SWT.FILL, 0);
		column.setText("Name");

		// 2nd column 
		column = new TableColumn(t, SWT.LEFT | SWT.FILL, 1);
		column.setText("Value");

		//Listener to set up the initial Widths of the table's columns
		t.addControlListener(new ControlAdapter() {

			public void controlResized(ControlEvent e) {
				try {
					Table resizedTable = (Table) e.getSource();

					int w = resizedTable.getClientArea().width;

					resizedTable.getColumn(0).setWidth(w / 2);
					resizedTable.getColumn(1).setWidth(w / 2);

					//remove the listener so that it is only done once
					resizedTable.removeControlListener(this);
				} catch (Exception exception) {
					//if anything 
				}
			}

		});

		headerViewer.setLabelProvider(new HeaderLabelProvider());
		headerViewer.setContentProvider(provider);

		return headerViewer;
	}

	/*
	 * Creates the Text widget used to show the body content of a Request
	 */
	protected SourceViewer createBodyText(Composite parent) {

		SourceViewer bodyText = new SourceViewer(parent, null, SWT.V_SCROLL | SWT.H_SCROLL);
		bodyText.setEditable(false);
		bodyText.setDocument(new Document(""));

		return bodyText;
	}

	public void clear() {
		clearRequestArea();
		clearResponseArea();
	}

	protected void clearResponseArea() {
		//set responseText as the visible control and set to ""
		responseText.getDocument().set("");
		responseBodyItem.setControl(responseText.getControl());

		formatButton.setVisible(false);

		responseHeadersViewer.setInput(null);

		//no need to have this job running because the call list is now clear
		if (responseBodyGetterJob != null)
			responseBodyGetterJob.cancel();

	}

	protected void clearRequestArea() {
		//clear the body text for the request
		requestText.getDocument().set("");

		//clear the headers
		requestHeadersViewer.setInput(null);
	}

	public void setSelection(IStructuredSelection selection) {
		this.selection = selection;

		if (selection.isEmpty()) {
			//clear the details area if there is nothing selected or more than one call selected
			clear();
		} else {

			//cancel the job for previous selection because there is no need to wait.
			if (responseBodyGetterJob != null)
				responseBodyGetterJob.cancel();

			Object element = selection.getFirstElement();

			if (element instanceof IStatusChange)
				element = ((IStatusChange) element).getCall();

			if (!(element instanceof INetworkCall))
				return;

			INetworkCall call = (INetworkCall) element;

			//set information for Request side of call
			IRequest req = call.getRequest();
			requestHeadersViewer.setInput(req);

			if (req instanceof IHTTPRequest) {
				requestText.getDocument().set(((IHTTPRequest) req).getBody());
			} else {
				requestText.getDocument().set("");
			}

			//set response side of the call (Note that the call might be ongoing and response not available
			IResponse res = call.getResponse();

			if (res != null) {
				responseHeadersViewer.setInput(res);

				//render the body
				if (res instanceof IHTTPResponse) {
					//show the correct control based on the MIME

					PayloadRenderStrategyFactory strategyFactory = PayloadRenderStrategyFactory.getInstance();

					IPayloadRenderStrategy strategy = strategyFactory.getStrategy((IHTTPResponse) res);

					//Handle Text Payload (html, css, js, xml...)
					if (strategy.getRenderType() == SimpleTextPayloadRenderStrategy.RENDER_TYPE) {

						//perform this in a job
						Object responseBody = ((IHTTPResponse) res).getBody();

						if (responseBody != null) {
							responseText.getDocument().set(responseBody.toString());
						}
						/*
						 * Assume that there is cache access going on so launch a job to poll for the body
						 */
						else {
							responseText.getDocument().set("Loading...");

							responseBodyGetterJob = new ResponseBodyGetterJob(((IHTTPResponse) res));

							responseBodyGetterJob.setUser(false);
							responseBodyGetterJob.setPriority(Job.INTERACTIVE);

							responseBodyGetterJob.schedule();
						}

						responseBodyItem.setControl(responseText.getControl());

						//set the formatting settings
						ITextPayloadRenderStrategy textStrategy = (ITextPayloadRenderStrategy) strategy;
						SourceViewerConfiguration sourceConfig = textStrategy.getConfiguration();

						//if sourceConfig is null, then assume that this text can't be formatted
						if (sourceConfig == null) {
							formatButton.setVisible(false);
							responseText.unconfigure();
						} else {
							formatButton.setVisible(true);

							//need to check the selection status of the button
							if (formatButton.getSelection()) {
								responseText.unconfigure();
								responseText.configure(textStrategy.getConfiguration());
							} else {
								responseText.unconfigure();
							}
						}

						responseText.refresh();

					}
					//Handle Image Payload (gif, png, jpg...)
					else if (strategy.getRenderType() == ImagePayloadRenderStrategy.RENDER_TYPE) {
						//read the image
						responseBodyItem.setControl(responseImage);

						//dispose the previous image
						if (imageLabel.getImage() != null) {
							imageLabel.getImage().dispose();
						}

						//@GINO: Need to cache the image data and not access the net every time
						//Should try to use the Browser's cache
						Image image = ImageDescriptor.createFromURL(call.getRequest().getURL()).createImage();
						imageLabel.setImage(image);
						imageLabel.setSize(image.getBounds().width, image.getBounds().height);

						formatButton.setVisible(false);

					} else if (strategy.getRenderType() == UnsupportedContentStrategy.RENDER_TYPE) {
						responseBodyItem.setControl(responseUnsupported);

						formatButton.setVisible(false);
					} else {
						clearResponseArea(); //there might not be a need for else
					}

				} else {
					//currently only supporting HTTP so this is here of future support
					responseText.getDocument().set("");
					responseBodyItem.setControl(responseText.getControl());
					responseText.refresh();
				}
			} else {
				clearResponseArea();
			}
		}

	}

	public void dispose() {
		clipboard.dispose();
		if ((imageLabel != null) && (!imageLabel.isDisposed())) {
			imageLabel.dispose();
			imageLabel = null;
		}
	}
}
