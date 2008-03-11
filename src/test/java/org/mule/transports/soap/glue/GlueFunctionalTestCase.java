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

import org.mule.config.ExceptionHelper;
import org.mule.config.builders.QuickConfigurationBuilder;
import org.mule.tck.providers.soap.AbstractSoapUrlEndpointFunctionalTestCase;
import org.mule.umo.UMOException;
import org.mule.umo.lifecycle.InitialisationException;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class GlueFunctionalTestCase extends AbstractSoapUrlEndpointFunctionalTestCase
{

    public static class ComponentWithoutInterfaces
    {
        public String echo(String msg)
        {
            return msg;
        }
    }

    public String getConfigResources()
    {
        return "glue-" + getTransportProtocol() + "-mule-config.xml";
    }

    protected String getTransportProtocol()
    {
        return "http";
    }

    protected String getSoapProvider()
    {
        return "glue";
    }

    /**
     * The Glue service requires that the component implements at least one interface
     * This just tests that we get the correct exception if no interfaces are
     * implemented
     *
     * @throws Throwable
     */
    public void testComponentWithoutInterfaces() throws Throwable
    {
        try
        {
            QuickConfigurationBuilder builder = new QuickConfigurationBuilder();
            builder.registerComponent(ComponentWithoutInterfaces.class.getName(),
                    "testComponentWithoutInterfaces", getComponentWithoutInterfacesEndpoint(), null, null);
            fail();
        }
        catch (UMOException e)
        {
            e = ExceptionHelper.getRootMuleException(e);
            assertTrue(e instanceof InitialisationException);
        }
    }

    //@Override
    protected String getWsdlEndpoint()
    {
        try
        {
            return "http://" + InetAddress.getLocalHost().getHostAddress() + ":62108/mule/mycomponent.wsdl";
        }
        catch (UnknownHostException e)
        {
            fail(e.getMessage());
            return null;
        }
    }
}
