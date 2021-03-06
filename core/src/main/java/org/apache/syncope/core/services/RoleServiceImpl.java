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
package org.apache.syncope.core.services;

import java.net.URI;
import java.util.List;

import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.syncope.common.SyncopeConstants;
import org.apache.syncope.common.mod.RoleMod;
import org.apache.syncope.common.search.NodeCond;
import org.apache.syncope.common.services.InvalidSearchConditionException;
import org.apache.syncope.common.services.RoleService;
import org.apache.syncope.common.to.RoleTO;
import org.apache.syncope.core.rest.controller.RoleController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService, ContextAware {

    @Autowired
    private RoleController roleController;

    private UriInfo uriInfo;

    @Override
    public List<RoleTO> children(final Long roleId) {
        return roleController.children(roleId);
    }

    @Override
    public int count() {
        return roleController.list().size();
    }

    @Override
    public Response create(final RoleTO roleTO) {
        RoleTO created = roleController.create(new DummyHTTPServletResponse(), roleTO);
        URI location = uriInfo.getAbsolutePathBuilder().path(created.getId() + "").build();
        return Response.created(location)
                .header(SyncopeConstants.REST_HEADER_ID, created.getId())
                .entity(created)
                .build();
    }

    @Override
    public RoleTO delete(final Long roleId) {
        return roleController.delete(roleId);
    }

    @Override
    public List<RoleTO> list() {
        return roleController.list();
    }

    @Override
    public List<RoleTO> list(final int page, final int size) {
        throw new ServiceUnavailableException();
    }

    @Override
    public RoleTO parent(final Long roleId) {
        return roleController.parent(roleId);
    }

    @Override
    public RoleTO read(final Long roleId) {
        return roleController.read(roleId);
    }

    @Override
    public List<RoleTO> search(final NodeCond searchCondition) throws InvalidSearchConditionException {
        return roleController.search(searchCondition);
    }

    @Override
    public List<RoleTO> search(final NodeCond searchCondition, final int page, final int size) throws InvalidSearchConditionException {
        return roleController.search(searchCondition, page, size);
    }

    @Override
    public int searchCount(final NodeCond searchCondition) throws InvalidSearchConditionException {
        return (Integer) roleController.searchCount(searchCondition).getModel().values().iterator().next();
    }

    @Override
    public RoleTO selfRead(final Long roleId) {
        return roleController.selfRead(roleId);
    }

    @Override
    public void setUriInfo(final UriInfo ui) {
        this.uriInfo = ui;
    }

    @Override
    public RoleTO update(final Long roleId, final RoleMod roleMod) {
        return roleController.update(roleMod);
    }

}
