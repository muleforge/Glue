/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transports.soap.glue.i18n;

import org.mule.config.i18n.MessageFactory;
import org.mule.config.i18n.Message;

import java.net.URI;

/**
 * Internationalised messages for the Glue transport
 */
public class GlueMessages extends MessageFactory
{
    private static final String BUNDLE_PATH = getBundlePath("glue");

    public static Message noServiceInterfacesFor(String serviceName)
    {
        return createMessage(BUNDLE_PATH, 2, serviceName);
    }

}