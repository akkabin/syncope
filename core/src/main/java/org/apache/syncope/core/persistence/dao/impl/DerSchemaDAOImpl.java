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
package org.apache.syncope.core.persistence.dao.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.TypedQuery;
import org.apache.syncope.core.persistence.beans.AbstractDerAttr;
import org.apache.syncope.core.persistence.beans.AbstractDerSchema;
import org.apache.syncope.core.persistence.beans.user.UMappingItem;
import org.apache.syncope.core.persistence.dao.DerAttrDAO;
import org.apache.syncope.core.persistence.dao.DerSchemaDAO;
import org.apache.syncope.core.persistence.dao.ResourceDAO;
import org.apache.syncope.core.util.AttributableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DerSchemaDAOImpl extends AbstractDAOImpl implements DerSchemaDAO {

    @Autowired
    private DerAttrDAO derivedAttributeDAO;

    @Autowired
    private ResourceDAO resourceDAO;

    @Override
    public <T extends AbstractDerSchema> T find(final String name, final Class<T> reference) {
        return entityManager.find(reference, name);
    }

    @Override
    public <T extends AbstractDerSchema> List<T> findAll(final Class<T> reference) {
        TypedQuery<T> query = entityManager.createQuery("SELECT e FROM " + reference.getSimpleName() + " e", reference);
        return query.getResultList();
    }

    @Override
    public <T extends AbstractDerSchema> T save(final T derivedSchema) {
        return entityManager.merge(derivedSchema);
    }

    @Override
    public void delete(final String name, final AttributableUtil attributableUtil) {
        final AbstractDerSchema derivedSchema = find(name, attributableUtil.derSchemaClass());
        if (derivedSchema == null) {
            return;
        }

        List<? extends AbstractDerAttr> attributes = getAttributes(derivedSchema, attributableUtil.derAttrClass());

        final Set<Long> derivedAttributeIds = new HashSet<Long>(attributes.size());

        Class<? extends AbstractDerAttr> attributeClass = null;
        for (AbstractDerAttr attribute : attributes) {
            derivedAttributeIds.add(attribute.getId());
            attributeClass = attribute.getClass();
        }

        for (Long derivedAttributeId : derivedAttributeIds) {
            derivedAttributeDAO.delete(derivedAttributeId, attributeClass);
        }

        resourceDAO.deleteMapping(name, attributableUtil.derIntMappingType(), UMappingItem.class);

        entityManager.remove(derivedSchema);
    }

    @Override
    public <T extends AbstractDerAttr> List<T> getAttributes(final AbstractDerSchema derivedSchema,
            final Class<T> reference) {

        TypedQuery<T> query = entityManager.createQuery("SELECT e FROM " + reference.getSimpleName() + " e"
                + " WHERE e.derivedSchema=:schema", reference);
        query.setParameter("schema", derivedSchema);

        return query.getResultList();
    }
}
