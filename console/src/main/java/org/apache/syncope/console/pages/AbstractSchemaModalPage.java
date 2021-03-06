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
package org.apache.syncope.console.pages;

import org.apache.syncope.common.AbstractBaseBean;
import org.apache.syncope.common.types.AttributableType;
import org.apache.syncope.console.rest.SchemaRestClient;
import org.apache.wicket.PageReference;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Modal window with Schema form.
 */
public abstract class AbstractSchemaModalPage extends BaseModalPage {

    private static final long serialVersionUID = 7369215690388444748L;

    @SpringBean
    protected SchemaRestClient restClient;

    protected AttributableType kind;

    public AbstractSchemaModalPage(final AttributableType kind) {
        this.kind = kind;
    }

    public abstract void setSchemaModalPage(PageReference callerPageRef, ModalWindow window, AbstractBaseBean schema,
            boolean createFlag);

    public AttributableType getKind() {
        return kind;
    }
}
