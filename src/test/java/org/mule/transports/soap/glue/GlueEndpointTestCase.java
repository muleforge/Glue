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

import org.mule.impl.endpoint.MuleEndpointURI;
import org.mule.providers.service.ConnectorFactory;
import org.mule.providers.service.ConnectorServiceDescriptor;
import org.mule.tck.AbstractMuleTestCase;
import org.mule.umo.endpoint.UMOEndpointURI;
import org.mule.config.MuleProperties;


public class GlueEndpointTestCase extends AbstractMuleTestCase
{
    public void testEndpoint() throws Exception
    {
        String url = "glue:http://www.xmethods.net/wsdl/query.wsdl?method=getSomething&param1=1&param2=2";
        UMOEndpointURI endpointUri = new MuleEndpointURI(url);
        assertEquals("glue", endpointUri.getSchemeMetaInfo());
        // it's up to the client to actually strip off the method name if
        // necessary
        assertEquals("http://www.xmethods.net/wsdl/query.wsdl?method=getSomething&param1=1&param2=2",
            endpointUri.getAddress());
        assertEquals("getSomething", endpointUri.getParams().getProperty(MuleProperties.MULE_METHOD_PROPERTY));
        assertEquals(3, endpointUri.getParams().size());

        url = "glue:http://www.xmethods.net/wsdl/query.wsdl?method=getSomething&param1=1&param2=2";
        endpointUri = new MuleEndpointURI(url);
        assertEquals("glue", endpointUri.getSchemeMetaInfo());
        assertEquals("http://www.xmethods.net/wsdl/query.wsdl?method=getSomething&param1=1&param2=2",
            endpointUri.getAddress());
        assertEquals("getSomething", endpointUri.getParams().getProperty(MuleProperties.MULE_METHOD_PROPERTY));
        assertEquals(3, endpointUri.getParams().size());
    }

    public void testEndpointWithUserInfo() throws Exception
    {
        String url = "glue:http://admin:pwd@www.xmethods.net/wsdl/query.wsdl?method=getSomething&param1=1&param2=2";
        UMOEndpointURI endpointUri = new MuleEndpointURI(url);
        assertEquals("glue", endpointUri.getSchemeMetaInfo());
        // it's up to the client to actually strip off the method name if
        // necessary
        assertEquals("http://www.xmethods.net/wsdl/query.wsdl?method=getSomething&param1=1&param2=2",
            endpointUri.getAddress());
        assertEquals("getSomething", endpointUri.getParams().getProperty(MuleProperties.MULE_METHOD_PROPERTY));
        assertEquals(3, endpointUri.getParams().size());
        assertEquals("admin:pwd", endpointUri.getUserInfo());
        assertEquals("admin", endpointUri.getUsername());
        assertEquals("pwd", endpointUri.getPassword());
    }

    public void testEndpointFinder() throws Exception
    {
        String url = "soap:http://www.xmethods.net/wsdl/query.wsdl?method=getSomething&param1=1&param2=2";
        UMOEndpointURI endpointUri = new MuleEndpointURI(url);
        assertEquals("soap", endpointUri.getSchemeMetaInfo());
        // it's up to the client to actually strip off the method name if
        // necessary
        assertEquals("http://www.xmethods.net/wsdl/query.wsdl?method=getSomething&param1=1&param2=2",
            endpointUri.getAddress());
        assertEquals("getSomething", endpointUri.getParams().getProperty(MuleProperties.MULE_METHOD_PROPERTY));
        assertEquals(3, endpointUri.getParams().size());

        ConnectorServiceDescriptor csd = ConnectorFactory.getServiceDescriptor("soap");
        assertEquals("glue", csd.getProtocol());
        assertEquals("org.mule.transports.soap.glue.GlueConnector", csd.getConnector());

    }
}
