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
package org.apache.syncope.core.persistence.beans;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * An execution (with result) of a Task.
 *
 * @see Task
 */
@Entity
public class TaskExec extends AbstractExec {

    private static final long serialVersionUID = 1909033231464074554L;

    /**
     * Id.
     */
    @Id
    private Long id;

    /**
     * The referred task.
     */
    @ManyToOne(optional = false)
    private Task task;

    public Long getId() {
        return id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    public String toString() {
        return new StringBuilder(getClass().getSimpleName()).append('{').
                append("id=").append(id).append(", ").
                append("startDate=").append(startDate).append(", ").
                append("endDate=").append(endDate).append(", ").
                append("task=").append(task).append(", ").
                append("status=").append(status).append(", ").
                append("message=").append(message).
                append('}').
                toString();
    }
}
