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

package org.eclipse.atf.mozilla.ide.ui.source;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.atf.mozilla.ide.common.IDOMNodeSelection;
import org.eclipse.atf.mozilla.ide.common.IWebBrowser;
import org.eclipse.atf.mozilla.ide.core.ContentFormatter;
import org.eclipse.atf.mozilla.ide.events.DOMDocumentEvent;
import org.eclipse.atf.mozilla.ide.events.DOMDocumentListener;
import org.eclipse.atf.mozilla.ide.events.DOMMutationListener;
import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.atf.mozilla.ide.ui.browser.views.IBrowserView;
import org.eclipse.atf.mozilla.ide.ui.common.SelectionProviderHandler;
import org.eclipse.atf.mozilla.ide.ui.common.configs.HTMLDOMSourceViewerConfiguration;
import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.Page;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMSerializer;
import org.mozilla.xpcom.Mozilla;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Page displaying the source of the currently
 * selected DOM node.
 * 
 * @author Kevin Sawicki (ksawicki@us.ibm.com)
 *
 */
public class DOMSourcePage extends Page implements IDOMSourcePage, IBrowserView, ISelectionListener {

	//this reference abstracts the Browser Editor
	protected IWebBrowser documentContainer = null;
	protected nsIDOMNode currentlySelectedNode = null;
	private DOMDocumentListener domDocumentListener = new DOMDocumentListener() {

		public void documentUnloaded(DOMDocumentEvent event) {
			if (event.isTop()) {
				clearSource();
			}
		}

	};
	private DOMMutationListener domMutationListener = new DOMMutationListener() {
		public void attributeAdded(nsIDOMElement ownerElement, String attributeName) {
			if (currElement != null && ownerElement.equals(currElement)) {
				showDomChange();
			}
		}

		public void attributeModified(nsIDOMElement ownerElement, String attributeName, String newValue, String previousValue) {
			if (currElement != null && ownerElement.equals(currElement)) {
				showDomChange();
			}
		}

		public void attributeRemoved(nsIDOMElement ownerElement, String attributeName) {
			if (currElement != null && ownerElement.equals(currElement)) {
				showDomChange();
			}
		}

		public void nodeInserted(nsIDOMNode parentNode, nsIDOMNode insertedNode) {
			if (currentlySelectedNode != null && parentNode.equals(currentlySelectedNode)) {
				showDomChange();
			}
		}
	};

	protected Menu menu;
	protected MenuItem update;
	protected MenuItem validate;
	protected MenuItem refresh;
	protected nsIDOMElement currElement;

	protected SourceViewer sourceViewer = null;
	protected HTMLDOMSourceViewerConfiguration config;
	protected StyledText status_bar;
	protected StyleRange sr;
	protected SashForm displayArea = null;
	protected Composite top = null;
	protected static final String STATUS = "Source Status:  ";
	protected static final String PREPROCESSING = "Loading...";
	protected static final String UNSAVED_UNVALIDATED = "Unsaved and unvalidated changes to source.";
	protected static final String REMOVED = "Node has been removed.";
	protected static final String CHANGED = "Node has been changed in browser.";
	protected static final String NO_CHANGES = "No unsaved changes.";
	protected static final String NO_SAVE = "Could not update. ";
	protected String edited = "";
	protected Action saveAction;
	protected Action validateAction;
	protected Action refreshAction;
	protected SelectionProviderHandler provider = new SelectionProviderHandler();

	protected Color colorWhite = new Color(Display.getCurrent(), new RGB(225, 225, 225));
	protected Color colorBlack = new Color(Display.getCurrent(), new RGB(0, 0, 0));
	protected Color colorRed = new Color(Display.getCurrent(), new RGB(255, 0, 0));

	static final protected Document emptyDoc = new Document("");

	/*
	 * This Job gets the serialized DOM Node scrubbed for Internal ATF elements
	 * and also adds formatting. It is done in a Job to avoid locking the UI
	 * in the case where the DOM is to big.
	 */
	protected class DOMSourcePreprocessingJob extends Job {

		/*
		 * This is the initial string that is set and processed by the Job
		 */
		protected String domAsText = null;

		public DOMSourcePreprocessingJob(String domAsText) {
			super("DOM Source Preprocessing Job");

			this.domAsText = domAsText;
		}

		protected IStatus run(IProgressMonitor monitor) {

			try {

				//using Transformers and the SAX Filtering support to re-serialize the domSource
				//without the ATF internal elements
				Transformer xformer = TransformerFactory.newInstance().newTransformer(); //pass-through transformer
				xformer.setOutputProperty("method", "xml");
				xformer.setOutputProperty("omit-xml-declaration", "yes");
				xformer.setOutputProperty("indent", "yes");

				SAXParserFactory saxFact = SAXParserFactory.newInstance();
				saxFact.setFeature("http://xml.org/sax/features/validation", false);

				SAXParser parser = saxFact.newSAXParser();

				//Using the SAX Filtering technique
				Source input = new SAXSource(new XMLFilterImpl(parser.getXMLReader()) {

					//need this to keep track of where we are to filter the endElements and any other character content
					protected int level = 0; //this should never be negative

					public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {

						//this means that we are within a node that is being filtered
						if (level > 0) {
							level++;
							return; //skip
						}

						//check for the ATF Internal flag (class=+___ATF_INTERNAL")
						String classAttrValue = atts.getValue("class");
						if (classAttrValue != null && classAttrValue.equals(MozIDEUIPlugin.ATF_INTERNAL)) {
							//skipping but setting the level to 1
							level = 1;
						} else {
							super.startElement(uri, localName, qName, atts);
						}
					}

					public void endElement(String uri, String localName, String qName) throws SAXException {
						if (level > 0) {
							level--; //skipping but updating how deep we are
						} else {
							super.endElement(uri, localName, qName);
						}
					}

					public void characters(char[] ch, int start, int length) throws SAXException {
						//only pipe characters if we are not inside a filtered section
						if (level == 0)
							super.characters(ch, start, length);
					}

				}, new InputSource(new ByteArrayInputStream(domAsText.getBytes("UTF-8"))));

				StringWriter writer = new StringWriter();
				Result output = new StreamResult(writer);
				xformer.transform(input, output);

				domAsText = writer.toString();

			} catch (Exception e) {
				MozIDEUIPlugin.log(e);
				//if there is any exception, then use the RegEx (which is not perfect but seems to work in our cases)
				domAsText = ContentFormatter.removeFlashingDiv(domAsText);
				domAsText = ContentFormatter.removeInternalScriptNode(domAsText);
			}

			//pretty up the code
			domAsText = ContentFormatter.format(domAsText, "html");

			return Status.OK_STATUS;
		}

		public String getResultText() {
			return domAsText;
		}

	};

	//reference to the current Preprocessing Job that is running
	protected DOMSourcePreprocessingJob currentFormmatingJob = null;

	public void createControl(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = false;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		top = new Composite(parent, SWT.NONE);
		top.setLayout(layout);
		top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		status_bar = new StyledText(top, SWT.NONE);
		status_bar.setBackground(colorWhite);
		sr = new StyleRange(0, 14, colorBlack, colorWhite, SWT.BOLD);
		status_bar.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		status_bar.setEditable(false);
		status_bar.setEnabled(false);
		status_bar.setText(STATUS);
		status_bar.setStyleRange(sr);
		displayArea = new SashForm(top, SWT.BORDER);
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = false;
		layout.marginHeight = 10;
		layout.marginWidth = 10;
		layout.verticalSpacing = 5;
		displayArea.setLayout(layout);
		displayArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		config = new HTMLDOMSourceViewerConfiguration(provider);
		sourceViewer = new SourceViewer(displayArea, null, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		sourceViewer.configure(config);
		sourceViewer.setEditable(false);
		sourceViewer.setDocument(emptyDoc);
		sourceViewer.addTextListener(new ITextListener() {
			public void textChanged(TextEvent event) {
				//Check document event since a null document event occurs when
				//hyperlink is moused over and no change has been made
				if (!edited.equals(event.getText()) && event.getDocumentEvent() != null) {
					status_bar.setText(STATUS + UNSAVED_UNVALIDATED);
					StyleRange sr1 = new StyleRange(STATUS.length(), UNSAVED_UNVALIDATED.length(), colorRed, colorWhite);
					status_bar.setStyleRange(sr);
					status_bar.setStyleRange(sr1);
				}
			}
		});

		sourceViewer.refresh();
		menu = new Menu(sourceViewer.getControl());
		menu.setEnabled(true);
		update = new MenuItem(menu, SWT.PUSH);
		update.setText("Update browser page");
		update.setEnabled(false);
		update.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				save();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		update.setImage(MozIDEUIPlugin.getDefault().getImage(MozIDEUIPlugin.E_UPDATE_ID));
		refresh = new MenuItem(menu, SWT.PUSH);
		refresh.setText("Refresh from browser");
		refresh.setEnabled(false);
		refresh.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				refresh();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}

		});
		refresh.setImage(MozIDEUIPlugin.getDefault().getImage(MozIDEUIPlugin.E_REFRESH_ID));
		validate = new MenuItem(menu, SWT.PUSH);
		validate.setText("Validate source");
		validate.setEnabled(false);
		validate.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				validate();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		validate.setImage(MozIDEUIPlugin.getDefault().getImage(MozIDEUIPlugin.VALIDATE_ID));
		sourceViewer.getControl().setMenu(menu);
		IToolBarManager toolBarManager = getSite().getActionBars().getToolBarManager();
		saveAction = new Action() {
			public void run() {
				save();
			}
		};
		saveAction.setImageDescriptor(MozIDEUIPlugin.getDefault().getImageDescriptorFromRegistry(MozIDEUIPlugin.E_UPDATE_ID));
		saveAction.setToolTipText("Update browser source with changes");
		saveAction.setEnabled(false);

		toolBarManager.add(saveAction);
		refreshAction = new Action() {
			public void run() {
				refresh();
			}
		};
		refreshAction.setEnabled(false);
		refreshAction.setToolTipText("Refresh source from browser page");
		refreshAction.setImageDescriptor(MozIDEUIPlugin.getDefault().getImageDescriptorFromRegistry(MozIDEUIPlugin.E_REFRESH_ID));
		toolBarManager.add(refreshAction);
		validateAction = new Action() {
			public void run() {
				validate();
			}
		};
		validateAction.setEnabled(false);
		validateAction.setToolTipText("Validate DOM source");
		validateAction.setImageDescriptor(MozIDEUIPlugin.getDefault().getImageDescriptorFromRegistry(MozIDEUIPlugin.VALIDATE_ID));
		toolBarManager.add(validateAction);
		getSite().setSelectionProvider(provider);

	}

	private void save() {
		boolean saved = config.save();
		if (saved) {
			currElement = (nsIDOMElement) config.getNode().queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
			status_bar.setText(STATUS + NO_CHANGES);
			StyleRange sr1 = new StyleRange(STATUS.length(), NO_CHANGES.length(), colorBlack, colorWhite);
			status_bar.setStyleRange(sr);
			status_bar.setStyleRange(sr1);
			currentlySelectedNode = config.getNode();
		} else {
			String validated = config.validate();
			status_bar.setText(STATUS + NO_SAVE + validated);
			StyleRange sr1 = new StyleRange(STATUS.length(), NO_SAVE.length() + validated.length(), colorRed, colorWhite);
			showErrors();
			status_bar.setStyleRange(sr);
			status_bar.setStyleRange(sr1);
		}
	}

	private void showErrors() {
		if (config.getErrorOffset() != -1) {
			sourceViewer.setSelectedRange(config.getErrorOffset(), 0);
			sourceViewer.revealRange(config.getErrorOffset(), 0);
		}
	}

	private void validate() {
		String validated = config.validate();
		status_bar.setText(STATUS + validated);
		StyleRange sr1 = null;
		if (validated.indexOf(HTMLDOMSourceViewerConfiguration.ERROR) == -1) {
			sr1 = new StyleRange(STATUS.length(), validated.length(), colorBlack, colorWhite);
		} else {
			sr1 = new StyleRange(STATUS.length(), validated.length(), colorRed, colorWhite);
		}
		showErrors();
		status_bar.setStyleRange(sr);
		status_bar.setStyleRange(sr1);
	}

	private void refresh() {
		displaySource(currentlySelectedNode);
	}

	public Control getControl() {
		return top;
	}

	public void setFocus() {
	}

	/*
	 * Do some cleanup by removing listeners hooked to the nsIDOMDocument
	 */
	public void dispose() {

		MozIDEUIPlugin.getDefault().getApplicationEventAdmin().removeEventListener(documentContainer, domDocumentListener);
		MozIDEUIPlugin.getDefault().getApplicationEventAdmin().removeEventListener(documentContainer, domMutationListener);

		//dispose of colors used
		colorWhite.dispose();
		colorBlack.dispose();
		colorRed.dispose();

		super.dispose();
	}

	/*
	 * IDOMSource interface
	 * 
	 * This method MUST only be called once at init time. It is part of the setup
	 * needed by the DOMSource.
	 */
	public void setWebBrowser(IWebBrowser documentContainer) {

		//prevent this method to be called more than once
		if (this.documentContainer != null)
			throw new AssertionFailedException("DOMSourcePage already initialized, cannot call setDOMDocumentContainer() more than once.");

		//save a reference
		this.documentContainer = documentContainer;

		//add as listener so that it gets notified of future changes to the Document in the
		//browser editor
		MozIDEUIPlugin.getDefault().getApplicationEventAdmin().addEventListener(documentContainer, domDocumentListener);
		MozIDEUIPlugin.getDefault().getApplicationEventAdmin().addEventListener(documentContainer, domMutationListener);

		//the documentContainer is set as the input of the tabFolder in the createControl method
	}

	/*
	 * This is an implementation of the ISelectionListner interface but it is invoked by
	 * the host PageBookView. The PageBookView is the one that registers as the
	 * ISelectionListener and it routes selectionChanged calls to the page that is
	 * visible.
	 * 
	 * This way, we limit the number of events going arounds that will potentially
	 * get ignored.
	 * 
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection instanceof IDOMNodeSelection) {
			if (selection.isEmpty()) {
				currentlySelectedNode = ((IDOMNodeSelection) selection).getSelectedNode();
				clearSource();
			} else {
				if (currentlySelectedNode == null || !currentlySelectedNode.equals(((IDOMNodeSelection) selection).getSelectedNode())) {
					currentlySelectedNode = ((IDOMNodeSelection) selection).getSelectedNode();
					displaySource(((IDOMNodeSelection) selection).getSelectedNode());
					if (((IDOMNodeSelection) selection).getSelectedNode().getNodeType() == nsIDOMElement.ELEMENT_NODE) {
						currElement = (nsIDOMElement) ((IDOMNodeSelection) selection).getSelectedNode().queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
					}
					update.setEnabled(true);
					validate.setEnabled(true);
					refresh.setEnabled(true);
				}
			}
		}
	}

	/*
	 * The display Source has been split in half so that some of its processing
	 * can be done on a separate Thread.
	 * 
	 * The first half is the setting up and scheduling of a Job that will
	 * preprocess the serialized DOM using JAXP to eliminate the ATF Internal
	 * elements and Pretty Format the Markup. This is necessary because all
	 * interaction with Mozilla would need to be done in the UI thread and the
	 * Preprocessing could be a long running operation that can bog down the UI.
	 * Using JAXP, allows this operation to be fully done on a separate thread.
	 * 
	 * The second half takes care of updating the UI with the new text. This is
	 * the overloaded method displaySource( String ).
	 */
	protected void displaySource(nsIDOMNode node) {
		config.setNode(node);
		config.clear();
		config.getNodes(node, 0);

		status_bar.setText(STATUS + PREPROCESSING);
		StyleRange sr1 = new StyleRange(STATUS.length(), PREPROCESSING.length(), colorBlack, colorWhite);
		status_bar.setStyleRange(sr);
		status_bar.setStyleRange(sr1);

		clearSource();

		/*
		 * To avoid doing the filtering operation on the UI thread, need to get
		 * a serialized version of the DOM through XPCOM, but then on a Worker
		 * thread do the filtering of the tree using JAXP.
		 */
		nsIDOMSerializer domSerializer = (nsIDOMSerializer) Mozilla.getInstance().getComponentManager().createInstanceByContractID("@mozilla.org/xmlextras/xmlserializer;1", null, nsIDOMSerializer.NS_IDOMSERIALIZER_IID);

		String text = domSerializer.serializeToString(node);

		if (currentFormmatingJob != null)
			currentFormmatingJob.cancel(); //try to cancel

		//only do the text scrubbing for Elements and Domcuments
		if (node.getNodeType() == nsIDOMNode.ELEMENT_NODE || node.getNodeType() == nsIDOMNode.DOCUMENT_NODE) {
			currentFormmatingJob = new DOMSourcePreprocessingJob(text);

			currentFormmatingJob.addJobChangeListener(new IJobChangeListener() {

				public void aboutToRun(IJobChangeEvent event) {
				}

				public void awake(IJobChangeEvent event) {
				}

				public void done(IJobChangeEvent event) {
					event.getJob().removeJobChangeListener(this);

					if (event.getJob() == currentFormmatingJob) {

						final String domSource = currentFormmatingJob.getResultText();

						//there are UI operation in this block that need to be
						//done in the UI thread
						getSite().getShell().getDisplay().asyncExec(new Runnable() {

							public void run() {
								displaySource(domSource);
							}

						});

						currentFormmatingJob = null;
					}

				}

				public void running(IJobChangeEvent event) {
				}

				public void scheduled(IJobChangeEvent event) {
				}

				public void sleeping(IJobChangeEvent event) {
				}

			});

			currentFormmatingJob.schedule();
		} else {

			//only selections that are Element and Document go through the Job,
			//the rest go through here
			displaySource(text);
		}

	}

	protected void displaySource(String text) {

		//text = ContentFormatter.format(text, "html");
		Document doc = new Document(text);
		edited = text;
		status_bar.setText(STATUS + NO_CHANGES);
		saveAction.setEnabled(true);
		validateAction.setEnabled(true);
		refreshAction.setEnabled(true);
		StyleRange sr1 = new StyleRange(STATUS.length(), NO_CHANGES.length(), colorBlack, colorWhite);
		status_bar.setStyleRange(sr);
		status_bar.setStyleRange(sr1);
		sourceViewer.setDocument(doc);
		config.setDocument(doc);
		sourceViewer.setEditable(true);
		sourceViewer.refresh();

	}

	protected void clearSource() {
		update.setEnabled(false);
		validate.setEnabled(false);
		refresh.setEnabled(false);
		saveAction.setEnabled(false);
		validateAction.setEnabled(false);
		refreshAction.setEnabled(false);
		sourceViewer.setEditable(false);
		sourceViewer.setDocument(emptyDoc);
		sourceViewer.refresh();

	}

	private void showDomChange() {
		status_bar.setText(STATUS + CHANGED);
		StyleRange sr1 = new StyleRange(STATUS.length(), CHANGED.length(), colorRed, colorWhite);
		status_bar.setStyleRange(sr);
		status_bar.setStyleRange(sr1);
	}
}
