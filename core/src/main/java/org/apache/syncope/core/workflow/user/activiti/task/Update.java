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
package org.apache.syncope.core.workflow.user.activiti.task;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.syncope.common.mod.UserMod;
import org.apache.syncope.core.persistence.beans.user.SyncopeUser;
import org.apache.syncope.core.propagation.PropagationByResource;
import org.apache.syncope.core.workflow.user.activiti.ActivitiUserWorkflowAdapter;

public class Update extends AbstractActivitiDelegate {

    @Override
    protected void doExecute(final DelegateExecution execution) throws Exception {
        SyncopeUser user = (SyncopeUser) execution.getVariable(ActivitiUserWorkflowAdapter.SYNCOPE_USER);
        UserMod userMod = (UserMod) execution.getVariable(ActivitiUserWorkflowAdapter.USER_MOD);

        // update SyncopeUser
        PropagationByResource propByRes = dataBinder.update(user, userMod);

        // report updated user and propagation by resource as result
        execution.setVariable(ActivitiUserWorkflowAdapter.SYNCOPE_USER, user);
        execution.setVariable(ActivitiUserWorkflowAdapter.PROP_BY_RESOURCE, propByRes);
    }
}
