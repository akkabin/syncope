/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.syncope.core.init;

import org.apache.syncope.core.init.ConnInstanceLoader;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.apache.syncope.core.AbstractTest;
import org.apache.syncope.core.persistence.dao.ResourceDAO;
import org.apache.syncope.core.propagation.ConnectorFacadeProxy;
import org.apache.syncope.core.util.ApplicationContextManager;
import org.apache.syncope.core.util.ConnBundleManager;

@Transactional
public class ConnInstanceLoaderTest extends AbstractTest {

    private ConnInstanceLoader cil;

    @Autowired
    private ResourceDAO resourceDAO;

    @Autowired
    private ConnBundleManager connBundleManager;

    @Before
    public void before() {
        cil = new ConnInstanceLoader();
        ReflectionTestUtils.setField(cil, "resourceDAO", resourceDAO);
        ReflectionTestUtils.setField(cil, "connBundleManager", connBundleManager);

        // Remove any other connector instance bean set up by
        // standard ConnInstanceLoader.load()
        for (String bean : ApplicationContextManager.getApplicationContext().getBeanNamesForType(
                ConnectorFacadeProxy.class)) {

            cil.unregisterConnector(bean);
        }
    }

    @Test
    public void load() {
        cil.load();

        assertEquals(resourceDAO.findAll().size(), ApplicationContextManager.getApplicationContext()
                .getBeanNamesForType(ConnectorFacadeProxy.class).length);
    }
}