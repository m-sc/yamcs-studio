package org.yamcs.studio.ui.eventlog;

import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.yamcs.protobuf.Events.GetEventsRequest;
import org.yamcs.protobuf.Yamcs.Event;
import org.yamcs.studio.core.ConnectionManager;
import org.yamcs.studio.core.model.TimeCatalogue;
import org.yamcs.studio.core.ui.utils.TimeInterval;
import org.yamcs.studio.core.web.BulkResponseHandler;
import org.yamcs.studio.core.web.RestClient;
import org.yamcs.utils.TimeEncoding;

public class ImportPastEventsDialog extends TitleAreaDialog {

    private static final Logger log = Logger.getLogger(ImportPastEventsDialog.class.getName());

    private EventLogView eventLogView;

    private DateTime startDate;
    private DateTime startTime;
    private Calendar startTimeValue;

    private DateTime stopDate;
    private DateTime stopTime;
    private Calendar stopTimeValue;

    public ImportPastEventsDialog(Shell parentShell, EventLogView eventLogView) {
        super(parentShell);
        this.eventLogView = eventLogView;
    }

    @Override
    public void create() {
        super.create();
        setTitle("Import Past Events");
    }

    private void validate() {
        String errorMessage = null;
        Calendar start = toCalendar(startDate, startTime);
        Calendar stop = toCalendar(stopDate, stopTime);
        if (start.after(stop))
            errorMessage = "Stop has to be greater than start";

        setErrorMessage(errorMessage);
        getButton(IDialogConstants.OK_ID).setEnabled(errorMessage == null);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));

        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 20;
        layout.marginWidth = 20;
        layout.verticalSpacing = 2;
        container.setLayout(layout);

        Label lbl = new Label(container, SWT.NONE);
        lbl.setText("Start:");
        Composite startComposite = new Composite(container, SWT.NONE);
        RowLayout rl = new RowLayout();
        rl.marginLeft = 0;
        rl.marginTop = 0;
        rl.marginBottom = 0;
        rl.center = true;
        startComposite.setLayout(rl);
        startDate = new DateTime(startComposite, SWT.DATE | SWT.LONG | SWT.DROP_DOWN | SWT.BORDER);
        startDate.addListener(SWT.Selection, e -> validate());
        startTime = new DateTime(startComposite, SWT.TIME | SWT.LONG | SWT.BORDER);
        startTime.addListener(SWT.Selection, e -> validate());
        if (startTimeValue != null) {
            startDate.setDate(startTimeValue.get(Calendar.YEAR), startTimeValue.get(Calendar.MONTH),
                    startTimeValue.get(Calendar.DAY_OF_MONTH));
            startTime.setTime(startTimeValue.get(Calendar.HOUR_OF_DAY), startTimeValue.get(Calendar.MINUTE),
                    startTimeValue.get(Calendar.SECOND));
        }

        lbl = new Label(container, SWT.NONE);
        lbl.setText("Stop:");
        Composite stopComposite = new Composite(container, SWT.NONE);
        rl = new RowLayout();
        rl.marginLeft = 0;
        rl.marginTop = 0;
        rl.marginBottom = 0;
        rl.center = true;
        rl.fill = true;
        stopComposite.setLayout(rl);
        stopDate = new DateTime(stopComposite, SWT.DATE | SWT.LONG | SWT.DROP_DOWN | SWT.BORDER);
        stopDate.addListener(SWT.Selection, e -> validate());
        stopTime = new DateTime(stopComposite, SWT.TIME | SWT.LONG | SWT.BORDER);
        stopTime.addListener(SWT.Selection, e -> validate());
        if (stopTimeValue != null) {
            stopDate.setDate(stopTimeValue.get(Calendar.YEAR), stopTimeValue.get(Calendar.MONTH),
                    stopTimeValue.get(Calendar.DAY_OF_MONTH));
            stopTime.setTime(stopTimeValue.get(Calendar.HOUR_OF_DAY), stopTimeValue.get(Calendar.MINUTE),
                    stopTimeValue.get(Calendar.SECOND));
        }

        return container;
    }

    private static Calendar toCalendar(DateTime dateWidget, DateTime timeWidget) {
        Calendar cal = Calendar.getInstance(TimeCatalogue.getInstance().getTimeZone());
        cal.set(dateWidget.getYear(), dateWidget.getMonth(), dateWidget.getDay());
        cal.set(Calendar.HOUR_OF_DAY, timeWidget.getHours());
        cal.set(Calendar.MINUTE, timeWidget.getMinutes());
        cal.set(Calendar.SECOND, timeWidget.getSeconds());
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    @Override
    protected void okPressed() {
        RestClient restClient = ConnectionManager.getInstance().getRestClient();
        if (restClient == null) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), "Could not import events\n",
                    "" + "Disconnected from Yamcs");
            return;
        }

        getButton(IDialogConstants.OK_ID).setEnabled(false);

        GetEventsRequest.Builder reqBuilder = GetEventsRequest.newBuilder();
        reqBuilder.setStart(TimeEncoding.fromCalendar(toCalendar(startDate, startTime)));
        reqBuilder.setStop(TimeEncoding.fromCalendar(toCalendar(stopDate, stopTime)));

        restClient.getEvents(reqBuilder.build(), new BulkResponseHandler<Event>() {

            @Override
            public void onMessages(List<Event> events) {
                Display.getDefault().asyncExec(() -> {
                    eventLogView.addEvents(events);
                });
            }

            @Override
            public void onEndOfStream() {
                Display.getDefault().asyncExec(() -> {
                    ImportPastEventsDialog.super.okPressed();
                });
            }

            @Override
            public void onException(Exception e) {
                Display.getDefault().asyncExec(() -> {
                    log.log(Level.SEVERE, "Could not import events", e);
                    MessageDialog.openError(Display.getCurrent().getActiveShell(), "Could not import events",
                            e.getMessage());
                    getButton(IDialogConstants.OK_ID).setEnabled(true);
                });
            }
        });
    }

    public void initialize(TimeInterval interval, List<String> packets, List<String> ppGroups) {
        startTimeValue = TimeEncoding.toCalendar(interval.calculateStart());
        startTimeValue.setTimeZone(TimeCatalogue.getInstance().getTimeZone());
        stopTimeValue = TimeEncoding.toCalendar(interval.calculateStop());
        stopTimeValue.setTimeZone(TimeCatalogue.getInstance().getTimeZone());
    }

    @Override
    public boolean close() {
        return super.close();
    }
}
