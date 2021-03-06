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
package org.apache.syncope.core.workflow;

import java.util.Collections;
import java.util.Set;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.syncope.core.propagation.PropagationByResource;

public class WorkflowResult<T> {

    private T result;

    private PropagationByResource propByRes;

    private Set<String> performedTasks;

    public WorkflowResult(final T result, final PropagationByResource propByRes, final String performedTask) {
        this.result = result;
        this.propByRes = propByRes;
        this.performedTasks = Collections.singleton(performedTask);
    }

    public WorkflowResult(final T result, final PropagationByResource propByRes, final Set<String> performedTasks) {
        this.result = result;
        this.propByRes = propByRes;
        this.performedTasks = performedTasks;
    }

    public T getResult() {
        return result;
    }

    public void setResult(final T result) {
        this.result = result;
    }

    public Set<String> getPerformedTasks() {
        return performedTasks;
    }

    public void setPerformedTasks(final Set<String> performedTasks) {
        this.performedTasks = performedTasks;
    }

    public PropagationByResource getPropByRes() {
        return propByRes;
    }

    public void setPropByRes(final PropagationByResource propByRes) {
        this.propByRes = propByRes;
    }

    @Override
    public boolean equals(final Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
