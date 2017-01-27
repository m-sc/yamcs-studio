package org.yamcs.studio.core.ui.connections;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;
import org.yamcs.studio.core.ConnectionManager;
import org.yamcs.studio.core.StudioConnectionListener;

/**
 * Used in plugin.xml core-expressions to keep track of connection state
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ConnectionStateProvider extends AbstractSourceProvider implements StudioConnectionListener {

    private static final Logger log = Logger.getLogger(ConnectionStateProvider.class.getName());

    public static final String STATE_KEY_CONNECTED = "org.yamcs.studio.ui.state.connected";
    private static final String[] SOURCE_NAMES = { STATE_KEY_CONNECTED };

    private boolean connected = false;

    public ConnectionStateProvider() {
        ConnectionManager.getInstance().addStudioConnectionListener(this);
    }

    @Override
    public Map getCurrentState() {
        Map map = new HashMap(1);
        map.put(STATE_KEY_CONNECTED, connected);
        return map;
    }

    // Utility method for easier programmatic access.
    public boolean isConnected() {
        return connected;
    }

    @Override
    public String[] getProvidedSourceNames() {
        return SOURCE_NAMES;
    }

    @Override
    public void onStudioConnect() {
        Display.getDefault().asyncExec(() -> {
            connected = true;
            Map newState = getCurrentState();
            log.fine(String.format("Fire new connection state %s", newState));
            fireSourceChanged(ISources.WORKBENCH, newState);
        });
    }

    @Override
    public void onStudioDisconnect() {
        Display.getDefault().asyncExec(() -> {
            connected = false;
            Map newState = getCurrentState();
            log.fine(String.format("Fire new connection state %s", newState));
            fireSourceChanged(ISources.WORKBENCH, newState);
        });
    }

    @Override
    public void dispose() {
        ConnectionManager.getInstance().removeStudioConnectionListener(this);
    }
}