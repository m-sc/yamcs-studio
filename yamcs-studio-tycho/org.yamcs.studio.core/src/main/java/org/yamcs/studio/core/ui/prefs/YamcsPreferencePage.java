package org.yamcs.studio.core.ui.prefs;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.yamcs.studio.core.YamcsPlugin;

/**
 * This class represents a preference page that is contributed to the Preferences dialog. By
 * subclassing <samp>FieldEditorPreferencePage</samp>, we can use the field support built into JFace
 * that allows us to create a page that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the preference store that
 * belongs to the main plug-in class. That way, preferences can be accessed directly via the
 * preference store.
 */
public class YamcsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    private StringFieldEditor yamcsHost;
    private IntegerFieldEditor yamcsPort;
    private StringFieldEditor yamcsInstance;
    private StringFieldEditor mdbNamespace;

    public YamcsPreferencePage() {
        super(FieldEditorPreferencePage.GRID);
        setPreferenceStore(YamcsPlugin.getDefault().getPreferenceStore());
        setDescription("Set connection properties to a Yamcs server");
    }

    /**
     * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to
     * manipulate various types of preferences. Each field editor knows how to save and restore
     * itself.
     */
    @Override
    public void createFieldEditors() {
        yamcsHost = new StringFieldEditor("yamcs_host", "Host", getFieldEditorParent());
        addField(yamcsHost);
        yamcsPort = new IntegerFieldEditor("yamcs_port", "Port", getFieldEditorParent());
        addField(yamcsPort);
        yamcsInstance = new StringFieldEditor("yamcs_instance", "Instance", getFieldEditorParent());
        addField(yamcsInstance);

        addField(new SpacerFieldEditor(getFieldEditorParent()));
        mdbNamespace = new StringFieldEditor("mdb_namespace", "MDB Namespace", getFieldEditorParent());
        addField(mdbNamespace);
    }

    @Override
    public void init(IWorkbench workbench) {
    }

    @Override
    protected void checkState() {
        super.checkState();
        if (!isValid())
            return;
        String yamcsHostText = yamcsHost.getStringValue();
        String yamcsInstanceText = yamcsInstance.getStringValue();
        if (!(yamcsHostText.trim().matches("[a-zA-Z\\-\\.0-9_]+")) || !(yamcsInstanceText.trim().matches("[a-zA-Z\\.\\-0-9_]+"))) {
            setErrorMessage("Not a valid host name");
            setValid(false);
        } else {
            setErrorMessage(null);
            setValid(true);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        super.propertyChange(event);
        if (event.getProperty().equals(FieldEditor.VALUE)) {
            if (event.getSource() == yamcsHost
                    || event.getSource() == yamcsPort
                    || event.getSource() == yamcsInstance
                    || event.getSource() == mdbNamespace) {
                checkState();
            }
        }
    }

    @Override
    public boolean performOk() {
        // Detect changes (there's probably a better way to do this)
        YamcsPlugin plugin = YamcsPlugin.getDefault();
        boolean changed = !yamcsHost.getStringValue().equals(plugin.getHost()) ||
                yamcsPort.getIntValue() != plugin.getWebPort() ||
                !yamcsInstance.getStringValue().equals(plugin.getInstance()) ||
                !mdbNamespace.getStringValue().equals(plugin.getMdbNamespace());
        // Save to store
        boolean ret = super.performOk();
        // Hint that user should restart
        if (changed)
            askRestart();
        return ret;
    }

    /**
     * Shows a dialog asking to restart workspace if pkg-config preferences have been changed.
     */
    private static void askRestart() {
        MessageDialog dialog = new MessageDialog(null, "Restart workspace?", null, "Changes made to Yamcs" +
                " preferences require a restart in order to take effect.\n\n" +
                "Would you like to restart Yamcs Studio now?",
                MessageDialog.QUESTION, new String[] { "Yes", "No" }, 0);
        if (dialog.open() == 0)
            Display.getDefault().asyncExec(() -> PlatformUI.getWorkbench().restart());
    }
}
