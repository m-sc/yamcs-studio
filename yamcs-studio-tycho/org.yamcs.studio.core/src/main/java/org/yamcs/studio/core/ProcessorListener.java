package org.yamcs.studio.core;

import org.yamcs.protobuf.YamcsManagement.ClientInfo;
import org.yamcs.protobuf.YamcsManagement.ProcessorInfo;
import org.yamcs.protobuf.YamcsManagement.Statistics;

/**
 * Server-wide updates on yamcs processors and their connected clients. Register for updates with
 * YamcsPlugin
 */
public interface ProcessorListener {

    /**
     * Called when *any* processor is updated. Includes 'replay state', 'replay configuration'.
     */
    public void processorUpdated(ProcessorInfo processorInfo);

    /**
     * Called when *any* processor is closed
     */
    public void yProcessorClosed(ProcessorInfo processorInfo);

    /**
     * Called when *any* processor's statistics are updated. Includes current time of the replay.
     */
    public void updateStatistics(Statistics stats);

    /**
     * Called when *any* client's ClientInfo was updated. Includes things like the subscribed
     * processor.
     */
    public void clientUpdated(ClientInfo clientInfo);

    /**
     * Called when *any* client was disconnected.
     */
    public void clientDisconnected(ClientInfo clientInfo);
}