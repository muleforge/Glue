/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transports.soap.glue;

import electric.glue.context.ServiceContext;
import electric.service.IService;

import org.mule.umo.lifecycle.InitialisationException;

/**
 * <code>GlueInitialisable</code> can be implemented by a Mule component that will
 * be used as an Glue Soap service to customise the Glue Service object
 * 
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */
public interface GlueInitialisable
{
    public void initialise(IService service, ServiceContext context) throws InitialisationException;
}
