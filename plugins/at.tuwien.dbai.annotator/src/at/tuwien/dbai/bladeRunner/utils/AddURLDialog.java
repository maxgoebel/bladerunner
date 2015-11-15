package at.tuwien.dbai.bladeRunner.utils;

import java.net.URI;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import at.tuwien.dbai.bladeRunner.LearnUIPlugin;
import at.tuwien.dbai.bladeRunner.i18n.mt;
import at.tuwien.dbai.bladeRunner.preferences.PreferenceConstants;
import at.tuwien.prip.common.utils.StringUtils;



/**
 * Example dialog that shows different field assist capabilities.
 */
public class AddURLDialog extends StatusDialog {

    abstract class SmartField
    {
    	ControlDecoration field;
        IControlContentAdapter contentAdapter;
        FieldDecoration errorDecoration, warningDecoration;

        SmartField(ControlDecoration field, IControlContentAdapter adapter) {
            this.field = field;
            this.contentAdapter = adapter;
        }

        String getContents() {
            return contentAdapter.getControlContents(field.getControl());
        }

        boolean isRequiredField() {
            return true;
        }

        FieldDecoration getErrorDecoration() {
            if (errorDecoration == null) {
                FieldDecoration standardError =
                    FieldDecorationRegistry.getDefault().
                    getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
                if (getErrorMessage() == null) {
                    errorDecoration = standardError;
                } else {
                    errorDecoration =
                        new FieldDecoration(standardError.getImage(),
                                            getErrorMessage());
                }
            }
            return errorDecoration;
        }

        FieldDecoration getWarningDecoration() {
            if (warningDecoration == null) {
                FieldDecoration standardWarning = FieldDecorationRegistry
                        .getDefault().getFieldDecoration(
                                FieldDecorationRegistry.DEC_WARNING);
                if (getWarningMessage() == null) {
                    warningDecoration = standardWarning;
                } else {
                    warningDecoration = new FieldDecoration(standardWarning
                            .getImage(), getWarningMessage());
                }
            }
            return warningDecoration;

        }

        abstract boolean isValid();

        abstract boolean isWarning();

        String getErrorMessage() {
            return null;
        }

        String getWarningMessage() {
            return null;
        }

    }

    class UrlField extends SmartField {

        UrlField(ControlDecoration field, IControlContentAdapter adapter) {
            super(field, adapter);
        }

        boolean isValid() {
            String contents = getContents();
            try {
                if (!contents.startsWith("about:")) {
                    URI.create(contents);
                }

                return true;
            } catch (Exception e) {
                return false;
            }
        }

        String getErrorMessage() {
            return mt.msg_error_address_is_not_valid_url;
        }

        boolean isWarning() {
            String uri = getContents();
            return
                uri.length()>0 &&
                !uri.startsWith("about:") &&
                !uri.startsWith("file:") &&
                !uri.startsWith("http://") &&
                !uri.startsWith("https://");
        }

        String getWarningMessage() {
            return mt.msg_warn_address_should_start_with_http_or_https_prefix;
        }
    }

    private List<String> previousURLs;
    private String initialBrowserURL;
    private String currentBrowserURL = "";

    private Color defaultTextColor, errorColor;

    /**
     * Open the add URL dialog.
     * @param parent the parent shell
     * @param browserURL the current URL in browser
     */
    public AddURLDialog(Shell parent, String browserURL) {
        super(parent);
        setTitle(mt.dlg_Enter_Address_of_Doc_to_Add);
        setStatusLineAboveButtons(false);
        this.initialBrowserURL = browserURL;
        getPreferenceValues();
    }

    private void getPreferenceValues() {
        IPreferenceStore store = LearnUIPlugin.getDefault().getPreferenceStore();
        String urls = store.getString(PreferenceConstants.PREF_ADD_URLS);
        previousURLs = StringUtils.split(urls, "#####");
    }

    protected Control createDialogArea(Composite parent)
    {
        Composite main = (Composite) super.createDialogArea(parent);

        initializeDialogUnits(main);

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        main.setLayout(layout);

        Label label = new Label(main, SWT.LEFT);
        label.setText(mt.lb_URL_Address);

        // Create a combo field representing a user name
        Combo combo = new Combo(parent, SWT.BORDER | SWT.DROP_DOWN);
//        ControlDecoration decoration = new ControlDecoration(control, SWT.BORDER | SWT.DROP_DOWN);

        ControlDecoration deco = new ControlDecoration(combo, SWT.BORDER | SWT.DROP_DOWN);
//        ControlDecoration field = new ControlDecoration(main, SWT.BORDER | SWT.DROP_DOWN,
//            new IControlCreator() {
//                public Control createControl(Composite parent, int style) {
//                    return new Combo(parent, style);
//                }
//            });
        final UrlField urlField = new UrlField(deco, new ComboContentAdapter());

//        Combo combo = (Combo) deco.getControl();
        combo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                currentBrowserURL = urlField.getContents();
                handleModify(urlField);
            }
        });
        combo.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent event) {
                handleFocusGained(urlField);
            }

            public void focusLost(FocusEvent event) {
                handleFocusLost(urlField);
            }

        });

        combo.setItems(previousURLs.toArray(new String[0]));
        if (initialBrowserURL!=null) {
            urlField.contentAdapter.
            setControlContents(deco.getControl(),
                               initialBrowserURL, 0);
            currentBrowserURL = initialBrowserURL;
        }
        deco.getControl().setLayoutData(getDecoratedFieldGridData());
        installContentProposalAdapter(combo, new ComboContentAdapter());
        // prime the required field color by calling the focus lost handler.
        handleModify(urlField);
        handleFocusLost(urlField);

        Dialog.applyDialogFont(main);

        return main;
    }

    private void handleModify(SmartField smartField) {
        // Error indicator supercedes all others
        if (!smartField.isValid()) {
            showError(smartField);
        } else {
            hideError(smartField);
            if (smartField.isWarning()) {
                showWarning(smartField);
            } else {
                hideWarning(smartField);
            }

            if (smartField.getContents().length()==0) {
                IStatus s = new Status(IStatus.ERROR, LearnUIPlugin.getDefault().getBundle().getSymbolicName(), 0, "", null);
                updateButtonsEnableState(s);
            }
        }
    }

    private void handleFocusGained(SmartField smartField) {
        // only set color if error color not already showing
        if (!smartField.isValid())
            return;
    }

    private void handleFocusLost(SmartField smartField) {
        // only set color if error color not showing
        if (!smartField.isValid())
            return;
    }

    private void showError(SmartField smartField) {
        FieldDecoration dec = smartField.getErrorDecoration();
        IStatus s = new Status(IStatus.ERROR, LearnUIPlugin.getDefault().getBundle().getSymbolicName(), 0, dec.getDescription(), null);
        updateButtonsEnableState(s);
//        smartField.field.addFieldDecoration(dec, SWT.BOTTOM | SWT.LEFT, false);
        smartField.field.getControl().
        setBackground(getErrorColor(smartField.field.getControl()));
    }

    private void hideError(SmartField smartField) {
//        FieldDecoration dec = smartField.getErrorDecoration();
        updateButtonsEnableState(Status.OK_STATUS);
//        smartField.field.hideDecoration(dec);
        smartField.field.getControl().setBackground(defaultTextColor);
    }

    private void showWarning(SmartField smartField) {
        FieldDecoration dec = smartField.getWarningDecoration();
        IStatus s = new Status(IStatus.WARNING, LearnUIPlugin.getDefault().getBundle().getSymbolicName(), 0, dec.getDescription(), null);
        updateButtonsEnableState(s);
//        smartField.field.addFieldDecoration(dec, SWT.BOTTOM | SWT.LEFT,false);
    }

    private void hideWarning(SmartField smartField) {
//        FieldDecoration dec = smartField.getWarningDecoration();
        updateButtonsEnableState(Status.OK_STATUS);
//        smartField.field.hideDecoration(dec);
    }

    private void installContentProposalAdapter(Control control, IControlContentAdapter contentAdapter) {
        boolean propagate = false;
        KeyStroke keyStroke;
        char[] autoActivationCharacters = null;
        int autoActivationDelay = 1000;

        try {
            keyStroke = KeyStroke.getInstance("Ctrl+Space");
        } catch (ParseException e) {
            keyStroke = KeyStroke.getInstance(SWT.F10);
        }

        ContentProposalAdapter adapter =
            new ContentProposalAdapter(control,
                                       contentAdapter, getContentProposalProvider(),
                                       keyStroke, autoActivationCharacters);
        adapter.setAutoActivationDelay(autoActivationDelay);
        adapter.setPropagateKeys(propagate);
        adapter.setFilterStyle(getContentAssistFilterStyle());
        adapter.setProposalAcceptanceStyle(getContentAssistAcceptance());
    }

    private IContentProposalProvider getContentProposalProvider() {
        return new IContentProposalProvider() {
            public IContentProposal[] getProposals(String contents, int position) {
                IContentProposal[] proposals = new IContentProposal[previousURLs.size()];
                int i=0;
                for (final String url : previousURLs) {
                    proposals[i] = new IContentProposal() {
                        public String getContent() {
                            return url;
                        }

                        public String getLabel() {
                            return url;
                        }

                        public String getDescription() {
                            return null;
                        }

                        public int getCursorPosition() {
                            return url.length();
                        }
                    };
                    i++;
                }
                return proposals;
            }
        };
    }

    private int getContentAssistAcceptance() {
        return ContentProposalAdapter.PROPOSAL_REPLACE;
    }

    private int getContentAssistFilterStyle() {
        return ContentProposalAdapter.FILTER_CHARACTER;//FILTER_CUMULATIVE;
    }

    private GridData getDecoratedFieldGridData() {
        return new GridData(450, SWT.DEFAULT);

    }

    private Color getErrorColor(Control control)
    {
        if (errorColor == null) {
//            RGB rgb = FieldAssistColors.computeErrorFieldBackgroundRGB(control);
        	errorColor = JFaceColors.getErrorBackground(control.getDisplay());
//            errorColor = new Color(control.getDisplay(), );
        }
        return errorColor;
    }

    @Override
    protected void okPressed()
    {
        if (currentBrowserURL.length()>0)
        {
        	if (!currentBrowserURL.startsWith("http://"))
        	{
        		currentBrowserURL = "http://" + currentBrowserURL;
        	}
            IPreferenceStore store = LearnUIPlugin.getDefault().getPreferenceStore();
            previousURLs.add(0, currentBrowserURL);
            store.setValue(PreferenceConstants.PREF_ADD_URLS,
                           StringUtils.concat(previousURLs, "#####"));
        }

        super.okPressed();
    }

    public boolean close() {
        if (errorColor != null) {
            errorColor.dispose();
        }
        return super.close();
    }

    public String getURL() {
        return currentBrowserURL;
    }

}
