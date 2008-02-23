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

import org.mule.config.MuleProperties;
import org.mule.config.i18n.CoreMessages;
import org.mule.impl.MuleDescriptor;
import org.mule.providers.AbstractMessageReceiver;
import org.mule.providers.ConnectException;
import org.mule.providers.soap.ServiceProxy;
import org.mule.transports.soap.glue.i18n.GlueMessages;
import org.mule.umo.UMOComponent;
import org.mule.umo.UMOException;
import org.mule.umo.endpoint.UMOEndpoint;
import org.mule.umo.lifecycle.InitialisationException;
import org.mule.umo.provider.UMOConnector;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import electric.glue.context.ApplicationContext;
import electric.glue.context.ServiceContext;
import electric.registry.Registry;
import electric.registry.RegistryException;
import electric.server.http.HTTP;
import electric.service.virtual.VirtualService;
import electric.util.Context;
import electric.util.interceptor.ReceiveThreadContext;
import electric.util.interceptor.SendThreadContext;

/**
 * <code>GlueMessageReceiver</code> is used to receive Glue bounded services for
 * Mule components. services are bound in the Glue Registry using the Virtualservice
 * implementation
 * 
 */

public class GlueMessageReceiver extends AbstractMessageReceiver
{
    private boolean createServer = false;

    public GlueMessageReceiver(UMOConnector connector,
                               UMOComponent component,
                               UMOEndpoint endpoint,
                               Boolean createServer) throws InitialisationException
    {
        super(connector, component, endpoint);
        this.createServer = createServer.booleanValue();
    }

    public void doConnect() throws Exception
    {
        try
        {
            Class[] interfaces = ServiceProxy.getInterfacesForComponent(component);
            if (interfaces.length == 0)
            {
                throw new InitialisationException(
                     GlueMessages.noServiceInterfacesFor(component.getDescriptor().getName()), this);
            }

            // this is always initialisaed as synchronous as ws invocations
            // should
            // always execute in a single thread unless the endpont has
            // explicitly
            // been set to run asynchronously
            if (!endpoint.isSynchronousSet() && !endpoint.isSynchronous())
            {
                logger.debug("overriding endpoint synchronicity and setting it to true. Web service requests are executed in a single thread");
                endpoint.setSynchronous(true);
            }

            if (createServer)
            {
                HTTP.startup(getEndpointURI().getScheme() + "://" + getEndpointURI().getHost() + ":"
                             + getEndpointURI().getPort());
                registerContextHeaders();
            }

            VirtualService.enable();
            VirtualService vService = new VirtualService(interfaces, GlueServiceProxy.createServiceHandler(
                this, endpoint.isSynchronous()));

            // Add initialisation callback for the Glue service
            // The callback will actually register the service
            MuleDescriptor desc = (MuleDescriptor)component.getDescriptor();
            String serviceName = getEndpointURI().getPath();
            if (!serviceName.endsWith("/"))
            {
                serviceName += "/";
            }
            serviceName += component.getDescriptor().getName();
            desc.addInitialisationCallback(new GlueInitialisationCallback(vService, serviceName,
                new ServiceContext()));

        }
        catch (ClassNotFoundException e)
        {
            throw new InitialisationException(CoreMessages.cannotLoadFromClasspath(e.getMessage()), e,
                this);
        }
        catch (UMOException e)
        {
            throw new InitialisationException(CoreMessages.failedToCreate(component.getDescriptor().getName()), e,
                this);
        }
        catch (Exception e)
        {
            throw new InitialisationException(CoreMessages.failedToStart("Soap Server"), e, this);
        }
    }

    protected void doStart() throws UMOException
    {
        //no op
    }

    protected void doStop() throws UMOException
    {
        //no op
    }

    public void doDisconnect() throws Exception
    {
        if (createServer)
        {
            try
            {
                HTTP.shutdown(getEndpointURI().getScheme() + "://" + getEndpointURI().getHost() + ":"
                              + getEndpointURI().getPort());
            }
            catch (IOException e)
            {
                throw new ConnectException(e, this);
            }
        }
    }

    protected void registerContextHeaders()
    {
        ApplicationContext.addOutboundSoapRequestInterceptor(new SendThreadContext(
            MuleProperties.MULE_CORRELATION_ID_PROPERTY));
        ApplicationContext.addOutboundSoapRequestInterceptor(new SendThreadContext(
            MuleProperties.MULE_CORRELATION_SEQUENCE_PROPERTY));
        ApplicationContext.addOutboundSoapRequestInterceptor(new SendThreadContext(
            MuleProperties.MULE_CORRELATION_GROUP_SIZE_PROPERTY));
        ApplicationContext.addOutboundSoapRequestInterceptor(new SendThreadContext(
            MuleProperties.MULE_REPLY_TO_PROPERTY, true));

        ApplicationContext.addInboundSoapRequestInterceptor(new ReceiveThreadContext(
            MuleProperties.MULE_CORRELATION_ID_PROPERTY));
        ApplicationContext.addInboundSoapRequestInterceptor(new ReceiveThreadContext(
            MuleProperties.MULE_CORRELATION_SEQUENCE_PROPERTY));
        ApplicationContext.addInboundSoapRequestInterceptor(new ReceiveThreadContext(
            MuleProperties.MULE_CORRELATION_GROUP_SIZE_PROPERTY));
        ApplicationContext.addInboundSoapRequestInterceptor(new ReceiveThreadContext(
            MuleProperties.MULE_REPLY_TO_PROPERTY, true));
    }

    /**
     * Template method to dispose any resources associated with this receiver. There
     * is not need to dispose the connector as this is already done by the framework
     */
    protected void doDispose()
    {
        try
        {
            Registry.unpublish(component.getDescriptor().getName());
        }
        catch (RegistryException e)
        {
            logger.error(CoreMessages.failedToUnregister(component.getDescriptor().getName(), endpoint.getEndpointURI()), e);
        }
    }

    protected Context getContext()
    {
        Context c = null;
        if (endpoint.getProperties() != null)
        {
            c = (Context)endpoint.getProperties().get("glueContext");
            if (c == null && endpoint.getProperties().size() > 0)
            {
                c = new Context();
                for (Iterator iterator = endpoint.getProperties().entrySet().iterator(); iterator.hasNext();)
                {
                    Map.Entry entry = (Map.Entry)iterator.next();
                    c.addProperty(entry.getKey().toString(), entry.getValue());
                }
            }
        }
        return c;
    }
}
