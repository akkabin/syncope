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
package org.apache.syncope.console.pages.panels;

import org.apache.syncope.common.search.MembershipCond;
import org.apache.syncope.common.search.NodeCond;
import org.apache.syncope.common.to.RoleTO;
import org.apache.syncope.console.pages.RoleModalPage;
import org.apache.syncope.console.rest.UserRestClient;
import org.apache.syncope.console.wicket.ajax.markup.html.ClearIndicatingAjaxButton;
import org.apache.syncope.console.wicket.markup.html.tree.TreeActionLinkPanel;
import org.apache.wicket.PageReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class RoleTabPanel extends Panel {

    private static final long serialVersionUID = 859236186975983959L;

    @SpringBean
    private UserRestClient restClient;

    public RoleTabPanel(final String id, final RoleTO roleTO, final ModalWindow window,
            final PageReference pageRef) {

        super(id);

        final Form form = new Form("roleForm");

        final TreeActionLinkPanel actionLink = new TreeActionLinkPanel("actionLink", roleTO.getId(),
                new CompoundPropertyModel(roleTO), window, pageRef);

        this.add(actionLink);
        this.add(new Label("displayName", roleTO.getDisplayName()));

        form.setModel(new CompoundPropertyModel(roleTO));
        form.setOutputMarkupId(true);

        final RolePanel rolePanel = new RolePanel("rolePanel", form, roleTO, RoleModalPage.Mode.ADMIN);
        rolePanel.setEnabled(false);
        form.add(rolePanel);

        final WebMarkupContainer userListContainer = new WebMarkupContainer("userListContainer");

        userListContainer.setOutputMarkupId(true);
        userListContainer.setEnabled(true);
        userListContainer.add(new UserSearchResultPanel("userList", true, null, pageRef, restClient));
        userListContainer.add(new ClearIndicatingAjaxButton("search", new ResourceModel("search"), pageRef) {

            private static final long serialVersionUID = -958724007591692537L;

            @Override
            protected void onSubmitInternal(final AjaxRequestTarget target, final Form<?> form) {
                final MembershipCond membershipCond = new MembershipCond();
                membershipCond.setRoleName(roleTO.getName());
                NodeCond cond = NodeCond.getLeafCond(membershipCond);

                userListContainer.replace(new UserSearchResultPanel("userList", true, cond, pageRef, restClient));

                target.add(userListContainer);
            }
        });

        form.add(userListContainer);
        add(form);
    }
}
