/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transports.soap.glue;

import org.mule.providers.AbstractConnector;
import org.mule.umo.UMOComponent;
import org.mule.umo.UMOException;
import org.mule.umo.lifecycle.InitialisationException;
import org.mule.umo.endpoint.UMOEndpoint;
import org.mule.umo.provider.UMOMessageReceiver;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <code>GlueConnector</code> instantiates a Glue soap server and allows beans to
 * be dynamically exposed via web services simply by registering with the connector.
 */

public class GlueConnector extends AbstractConnector
{
    private List serverEndpoints = new ArrayList();
    private Map context;

    public GlueConnector()
    {
        super();
        registerSupportedProtocol("http");
    }

    public String getProtocol()
    {
        return "glue";
    }


    public UMOMessageReceiver createReceiver(UMOComponent component, UMOEndpoint endpoint) throws Exception
    {
        boolean createServer = shouldCreateServer(endpoint.getEndpointURI().getAddress());

        UMOMessageReceiver receiver = serviceDescriptor.createMessageReceiver(this, component, endpoint,
            new Object[]{Boolean.valueOf(createServer)});

        if (createServer)
        {
            serverEndpoints.add(endpoint.getEndpointURI().getAddress());
        }
        return receiver;
    }

    protected Object getReceiverKey(UMOComponent component, UMOEndpoint endpoint)
    {
        return endpoint.getEndpointURI().getAddress() + "/" + component.getDescriptor().getName();
    }

    private boolean shouldCreateServer(String endpoint) throws URISyntaxException
    {
        URI uri = new URI(endpoint);
        String ep = uri.getScheme() + "://" + uri.getHost();
        if (uri.getPort() != -1)
        {
            ep += ":" + uri.getPort();
        }

        for (Iterator iterator = serverEndpoints.iterator(); iterator.hasNext();)
        {
            String s = (String)iterator.next();
            if (s.startsWith(ep))
            {
                return false;
            }
        }
        return true;
    }

    public Map getContext()
    {
        return context;
    }

    public void setContext(Map context)
    {
        this.context = context;
    }

    public boolean supportsProtocol(String protocol)
    {
        return super.supportsProtocol(protocol) || protocol.toLowerCase().equals("glue:http");
    }

    protected void doConnect() throws Exception
    {
        //no op
    }

    protected void doDisconnect() throws Exception
    {
        //no op
    }

    protected void doDispose()
    {
        if(context!=null)
        {
            context.clear();
        }

        if(serverEndpoints!=null)
        {
            serverEndpoints.clear();
        }
    }

    protected void doInitialise() throws InitialisationException
    {
        //no op
    }

    protected void doStart() throws UMOException
    {
        //no op
    }

    protected void doStop() throws UMOException
    {
        //no op
    }
}
