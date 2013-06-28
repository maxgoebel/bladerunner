package at.tuwien.dbai.bladeRunner.utils;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class InfoPopUp extends PopupDialog {

    /**
     * The text control that displays the text.
     */
    private Text text;

    /**
     * The String shown in the popup.
     */
    private String contents = "Test";

    private final static int SHELL_STYLE = PopupDialog.INFOPOPUP_SHELLSTYLE;

    public InfoPopUp(Shell parent, String infoText) {
        this(parent, SHELL_STYLE, false, false, false, false, false, null,
                infoText);
    }

    public InfoPopUp(Shell parent, String titleText, String infoText) {
        this(parent, SHELL_STYLE, false, false, false, true, true, titleText,
                infoText);
    }

    public InfoPopUp(Shell parent, int shellStyle, boolean takeFocusOnOpen,
            boolean persistSize, boolean persistLocation,
            boolean showDialogMenu, boolean showPersistActions,
            String titleText, String infoText) {
        super(parent, shellStyle, takeFocusOnOpen, persistSize,
                persistLocation, showDialogMenu, showPersistActions, titleText,
                infoText);
    }
}