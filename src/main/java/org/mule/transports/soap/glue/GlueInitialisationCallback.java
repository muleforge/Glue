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

import org.mule.config.i18n.CoreMessages;
import org.mule.impl.InitialisationCallback;
import org.mule.umo.lifecycle.InitialisationException;

import electric.glue.context.ServiceContext;
import electric.registry.Registry;
import electric.registry.RegistryException;
import electric.service.IService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <code>GlueInitialisationCallback</code> is invoked when an Glue service
 * component is created from its descriptor.
 * 
 */
public class GlueInitialisationCallback implements InitialisationCallback
{
    /**
     * logger used by this class
     */
    protected static Logger logger = LoggerFactory.getLogger(GlueInitialisationCallback.class);

    private IService service;
    private ServiceContext context;
    private String servicePath;
    private boolean invoked = false;

    public GlueInitialisationCallback(IService service, String path, ServiceContext context)
    {
        this.service = service;
        this.servicePath = path;
        this.context = context;
        if (context == null)
        {
            this.context = new ServiceContext();
        }
    }

    public void initialise(Object component) throws InitialisationException
    {
        // only call this once
        if (invoked)
        {
            return;
        }
        if (component instanceof GlueInitialisable)
        {
            logger.debug("Calling Glue initialisation for component: " + component.getClass().getName());
            ((GlueInitialisable)component).initialise(service, context);
        }
        invoked = true;
        try
        {
            logger.debug("Publishing service " + servicePath + " to Glue registry.");
            Registry.publish(servicePath, service, context);
        }
        catch (RegistryException e)
        {
            throw new InitialisationException(CoreMessages.failedToLoad(component.getClass().getName()), e, this);
        }
    }
}
