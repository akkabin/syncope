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

import java.util.List;
import org.apache.syncope.common.to.AbstractAttributableTO;
import org.apache.syncope.common.to.AttributeTO;
import org.apache.syncope.common.to.RoleTO;
import org.apache.syncope.common.to.UserTO;
import org.apache.syncope.common.types.AttributableType;
import org.apache.syncope.console.rest.SchemaRestClient;
import org.apache.syncope.console.wicket.markup.html.form.AjaxDecoratedCheckbox;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.IAjaxCallListener;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DerivedAttributesPanel extends Panel {

    /**
     * Logger.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(DerivedAttributesPanel.class);

    private static final long serialVersionUID = -5387344116983102292L;

    @SpringBean
    private SchemaRestClient schemaRestClient;

    public <T extends AbstractAttributableTO> DerivedAttributesPanel(final String id, final T entityTO) {
        super(id);
        setOutputMarkupId(true);

        final IModel<List<String>> derivedSchemaNames = new LoadableDetachableModel<List<String>>() {

            private static final long serialVersionUID = 5275935387613157437L;

            @Override
            protected List<String> load() {
                if (entityTO instanceof RoleTO) {
                    return schemaRestClient.getDerivedSchemaNames(AttributableType.ROLE);
                } else if (entityTO instanceof UserTO) {
                    return schemaRestClient.getDerivedSchemaNames(AttributableType.USER);
                } else {
                    return schemaRestClient.getDerivedSchemaNames(AttributableType.MEMBERSHIP);
                }
            }
        };

        final WebMarkupContainer attributesContainer = new WebMarkupContainer("derAttrContainer");

        attributesContainer.setOutputMarkupId(true);
        add(attributesContainer);

        AjaxButton addAttributeBtn = new IndicatingAjaxButton("addAttributeBtn", new ResourceModel("addAttributeBtn")) {

            private static final long serialVersionUID = -4804368561204623354L;

            @Override
            protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                entityTO.getDerivedAttributes().add(new AttributeTO());
                target.add(attributesContainer);
            }

            @Override
            protected void onError(final AjaxRequestTarget target, final Form<?> form) {
                target.add(attributesContainer);
            }
        };

        add(addAttributeBtn.setDefaultFormProcessing(Boolean.FALSE));

        final ListView<AttributeTO> attributes = new ListView<AttributeTO>("attributes",
                new PropertyModel<List<? extends AttributeTO>>(entityTO, "derivedAttributes")) {

            private static final long serialVersionUID = 9101744072914090143L;

            @Override
            protected void populateItem(final ListItem<AttributeTO> item) {
                final AttributeTO attributeTO = item.getModelObject();

                item.add(new AjaxDecoratedCheckbox("toRemove", new Model<Boolean>(Boolean.FALSE)) {

                    private static final long serialVersionUID = 7170946748485726506L;

                    @Override
                    protected void onUpdate(final AjaxRequestTarget target) {
                        entityTO.getDerivedAttributes().remove(attributeTO);
                        target.add(attributesContainer);
                    }

                    @Override
                    protected void updateAjaxAttributes(final AjaxRequestAttributes attributes) {
                        super.updateAjaxAttributes(attributes);

                        IAjaxCallListener ajaxCallListener = new AjaxCallListener() {

                            private static final long serialVersionUID = 7160235486520935153L;

                            @Override
                            public CharSequence getPrecondition(final Component component) {
                                return "if (!confirm('" + getString("confirmDelete") + "')) return false;";
                            }
                        };
                        attributes.getAjaxCallListeners().add(ajaxCallListener);
                    }
                });

                final DropDownChoice<String> schemaChoice = new DropDownChoice<String>("schema",
                        new PropertyModel<String>(attributeTO, "schema"), derivedSchemaNames);

                schemaChoice.add(new AjaxFormComponentUpdatingBehavior("onblur") {

                    private static final long serialVersionUID = -1107858522700306810L;

                    @Override
                    protected void onUpdate(final AjaxRequestTarget art) {

                        attributeTO.setSchema(schemaChoice.getModelObject());
                    }
                });

                item.add(schemaChoice.setRequired(true));

                schemaChoice.setOutputMarkupId(true);
                schemaChoice.setRequired(true);
                item.add(schemaChoice);

                final List<String> values = attributeTO.getValues();
                if (values == null || values.isEmpty()) {
                    item.add(new TextField<String>("value",
                            new Model<String>(null)).setVisible(Boolean.FALSE));
                } else {
                    item.add(new TextField<String>("value",
                            new Model<String>(values.get(0))).setEnabled(Boolean.FALSE));
                }
            }
        };

        attributesContainer.add(attributes);
    }
}
