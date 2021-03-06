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
package org.apache.syncope.console.wicket.markup.html.form;

import org.apache.syncope.console.commons.XMLRolesReader;
import org.apache.syncope.console.wicket.ajax.markup.html.ClearIndicatingAjaxLink;
import org.apache.syncope.console.wicket.ajax.markup.html.IndicatingDeleteOnConfirmAjaxLink;
import org.apache.wicket.PageReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * This empty class must exist because there not seems to be alternative to provide specialized HTML for edit links.
 */
public class ActionLinksPanel extends Panel {

    private static final long serialVersionUID = 322966537010107771L;

    /**
     * Role reader for authorizations management.
     */
    @SpringBean
    protected XMLRolesReader xmlRolesReader;

    private final PageReference pageRef;

    public ActionLinksPanel(final String componentId, final IModel<?> model, final PageReference pageRef) {
        super(componentId, model);
        this.pageRef = pageRef;

        super.add(new Fragment("panelClaim", "emptyFragment", this));
        super.add(new Fragment("panelCreate", "emptyFragment", this));
        super.add(new Fragment("panelEdit", "emptyFragment", this));
        super.add(new Fragment("panelUserTemplate", "emptyFragment", this));
        super.add(new Fragment("panelRoleTemplate", "emptyFragment", this));
        super.add(new Fragment("panelEnable", "emptyFragment", this));
        super.add(new Fragment("panelSearch", "emptyFragment", this));
        super.add(new Fragment("panelDelete", "emptyFragment", this));
        super.add(new Fragment("panelExecute", "emptyFragment", this));
        super.add(new Fragment("panelDryRun", "emptyFragment", this));
        super.add(new Fragment("panelSelect", "emptyFragment", this));
        super.add(new Fragment("panelExport", "emptyFragment", this));
        super.add(new Fragment("panelSuspend", "emptyFragment", this));
        super.add(new Fragment("panelReactivate", "emptyFragment", this));
        super.add(new Fragment("panelReload", "emptyFragment", this));
        super.add(new Fragment("panelChangeView", "emptyFragment", this));
    }

    public void add(
            final ActionLink link, final ActionLink.ActionType type, final String pageId, final String actionId) {

        addWithRoles(link, type, xmlRolesReader.getAllAllowedRoles(pageId, actionId), true);
    }

    public void add(
            final ActionLink link, final ActionLink.ActionType type, final String pageId) {

        add(link, type, pageId, true);
    }

    public void add(
            final ActionLink link, final ActionLink.ActionType type, final String pageId, final boolean enabled) {

        addWithRoles(link, type, xmlRolesReader.getAllAllowedRoles(pageId, type.getActionId()), enabled);
    }

    public void addWithRoles(
            final ActionLink link, final ActionLink.ActionType type, final String roles) {
        addWithRoles(link, type, roles, true);
    }

    public void addWithRoles(
            final ActionLink link, final ActionLink.ActionType type, final String roles, final boolean enabled) {

        Fragment fragment = null;

        switch (type) {

            case CLAIM:
                fragment = new Fragment("panelClaim", "fragmentClaim", this);

                fragment.addOrReplace(new ClearIndicatingAjaxLink("claimLink", pageRef) {

                    private static final long serialVersionUID = -7978723352517770644L;

                    @Override
                    protected void onClickInternal(final AjaxRequestTarget target) {
                        link.onClick(target);
                    }
                });
                break;

            case CREATE:
                fragment = new Fragment("panelCreate", "fragmentCreate", this);

                fragment.addOrReplace(new ClearIndicatingAjaxLink("createLink", pageRef) {

                    private static final long serialVersionUID = -7978723352517770644L;

                    @Override
                    protected void onClickInternal(final AjaxRequestTarget target) {
                        link.onClick(target);
                    }
                });
                break;

            case EDIT:
                fragment = new Fragment("panelEdit", "fragmentEdit", this);

                fragment.addOrReplace(new ClearIndicatingAjaxLink("editLink", pageRef) {

                    private static final long serialVersionUID = -7978723352517770644L;

                    @Override
                    protected void onClickInternal(final AjaxRequestTarget target) {
                        link.onClick(target);
                    }
                });
                break;

            case USER_TEMPLATE:
                fragment = new Fragment("panelUserTemplate", "fragmentUserTemplate", this);

                fragment.addOrReplace(new ClearIndicatingAjaxLink("userTemplateLink", pageRef) {

                    private static final long serialVersionUID = -7978723352517770644L;

                    @Override
                    protected void onClickInternal(final AjaxRequestTarget target) {
                        link.onClick(target);
                    }
                });
                break;

            case ROLE_TEMPLATE:
                fragment = new Fragment("panelRoleTemplate", "fragmentRoleTemplate", this);

                fragment.addOrReplace(new ClearIndicatingAjaxLink("roleTemplateLink", pageRef) {

                    private static final long serialVersionUID = -7978723352517770644L;

                    @Override
                    protected void onClickInternal(final AjaxRequestTarget target) {
                        link.onClick(target);
                    }
                });
                break;

            case ENABLE:
                fragment = new Fragment("panelEnable", "fragmentEnable", this);

                fragment.addOrReplace(new ClearIndicatingAjaxLink("enableLink", pageRef) {

                    private static final long serialVersionUID = -7978723352517770644L;

                    @Override
                    protected void onClickInternal(final AjaxRequestTarget target) {
                        link.onClick(target);
                    }
                });
                break;

            case SEARCH:
                fragment = new Fragment("panelSearch", "fragmentSearch", this);

                fragment.addOrReplace(new ClearIndicatingAjaxLink("searchLink", pageRef) {

                    private static final long serialVersionUID = -7978723352517770644L;

                    @Override
                    protected void onClickInternal(final AjaxRequestTarget target) {
                        link.onClick(target);
                    }
                });
                break;

            case EXECUTE:
                fragment = new Fragment("panelExecute", "fragmentExecute", this);

                fragment.addOrReplace(new ClearIndicatingAjaxLink("executeLink", pageRef) {

                    private static final long serialVersionUID = -7978723352517770644L;

                    @Override
                    protected void onClickInternal(final AjaxRequestTarget target) {
                        link.onClick(target);
                    }
                });
                break;

            case DRYRUN:
                fragment = new Fragment("panelDryRun", "fragmentDryRun", this);

                fragment.addOrReplace(new ClearIndicatingAjaxLink("dryRunLink", pageRef) {

                    private static final long serialVersionUID = -7978723352517770644L;

                    @Override
                    protected void onClickInternal(final AjaxRequestTarget target) {
                        link.onClick(target);
                    }
                });
                break;

            case DELETE:
                fragment = new Fragment("panelDelete", "fragmentDelete", this);

                fragment.addOrReplace(new IndicatingDeleteOnConfirmAjaxLink("deleteLink", pageRef) {

                    private static final long serialVersionUID = -7978723352517770644L;

                    @Override
                    protected void onClickInternal(final AjaxRequestTarget target) {
                        link.onClick(target);
                    }
                });

                break;

            case SELECT:
                fragment = new Fragment("panelSelect", "fragmentSelect", this);

                fragment.addOrReplace(new ClearIndicatingAjaxLink("selectLink", pageRef) {

                    private static final long serialVersionUID = -7978723352517770644L;

                    @Override
                    protected void onClickInternal(final AjaxRequestTarget target) {
                        link.onClick(target);
                    }
                });

                break;

            case EXPORT:
                fragment = new Fragment("panelExport", "fragmentExport", this);

                fragment.addOrReplace(new ClearIndicatingAjaxLink("exportLink", pageRef) {

                    private static final long serialVersionUID = -7978723352517770644L;

                    @Override
                    protected void onClickInternal(final AjaxRequestTarget target) {
                        link.onClick(target);
                    }
                });
                break;

            case SUSPEND:
                fragment = new Fragment("panelSuspend", "fragmentSuspend", this);

                fragment.addOrReplace(new ClearIndicatingAjaxLink<Void>("suspendLink", pageRef) {

                    private static final long serialVersionUID = -6957616042924610291L;

                    @Override
                    protected void onClickInternal(final AjaxRequestTarget target) {
                        link.onClick(target);
                    }
                });
                break;

            case REACTIVATE:
                fragment = new Fragment("panelReactivate", "fragmentReactivate", this);

                fragment.addOrReplace(new ClearIndicatingAjaxLink<Void>("reactivateLink", pageRef) {

                    private static final long serialVersionUID = -6957616042924610292L;

                    @Override
                    protected void onClickInternal(final AjaxRequestTarget target) {
                        link.onClick(target);
                    }
                });
                break;

            case RELOAD:
                fragment = new Fragment("panelReload", "fragmentReload", this);

                fragment.addOrReplace(new ClearIndicatingAjaxLink<Void>("reloadLink", pageRef) {

                    private static final long serialVersionUID = -6957616042924610293L;

                    @Override
                    protected void onClickInternal(final AjaxRequestTarget target) {
                        link.onClick(target);
                    }
                });
                break;

            case CHANGE_VIEW:
                fragment = new Fragment("panelChangeView", "fragmentChangeView", this);

                fragment.addOrReplace(new ClearIndicatingAjaxLink<Void>("changeViewLink", pageRef) {

                    private static final long serialVersionUID = -6957616042924610292L;

                    @Override
                    protected void onClickInternal(final AjaxRequestTarget target) {
                        link.onClick(target);
                    }
                });
                break;
            default:
            // do nothink
        }

        if (fragment != null) {
            fragment.setEnabled(enabled);
            MetaDataRoleAuthorizationStrategy.authorize(fragment, ENABLE, roles);
            super.addOrReplace(fragment);
        }
    }

    public void remove(final ActionLink.ActionType type) {
        switch (type) {
            case CLAIM:
                super.addOrReplace(new Fragment("panelClaim", "emptyFragment", this));
                break;

            case CREATE:
                super.addOrReplace(new Fragment("panelCreate", "emptyFragment", this));
                break;

            case EDIT:
                super.addOrReplace(new Fragment("panelEdit", "emptyFragment", this));
                break;

            case USER_TEMPLATE:
                super.addOrReplace(new Fragment("panelUserTemplate", "emptyFragment", this));
                break;

            case SEARCH:
                super.addOrReplace(new Fragment("panelSearch", "emptyFragment", this));
                break;

            case EXECUTE:
                super.addOrReplace(new Fragment("panelExecute", "emptyFragment", this));
                break;

            case DRYRUN:
                super.addOrReplace(new Fragment("panelDryRun", "emptyFragment", this));
                break;

            case DELETE:
                super.addOrReplace(new Fragment("panelDelete", "emptyFragment", this));
                break;

            case SELECT:
                super.addOrReplace(new Fragment("panelSelect", "emptyFragment", this));
                break;

            case EXPORT:
                super.addOrReplace(new Fragment("panelExport", "emptyFragment", this));
                break;

            case SUSPEND:
                super.addOrReplace(new Fragment("panelSuspend", "emptyFragment", this));
                break;

            case REACTIVATE:
                super.addOrReplace(new Fragment("panelReactivate", "emptyFragment", this));
                break;

            case RELOAD:
                super.addOrReplace(new Fragment("panelReload", "emptyFragment", this));
                break;

            case CHANGE_VIEW:
                super.addOrReplace(new Fragment("panelChangeView", "emptyFragment", this));
                break;
            default:
            // do nothing
        }
    }
}
