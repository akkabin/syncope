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
package org.apache.syncope.core.persistence.validation.entity;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.syncope.common.types.EntityViolationType;
import org.apache.syncope.common.types.AttributeSchemaType;
import org.apache.syncope.core.persistence.beans.AbstractSchema;

public class SchemaValidator extends AbstractValidator implements ConstraintValidator<SchemaCheck, AbstractSchema> {

    @Override
    public void initialize(final SchemaCheck constraintAnnotation) {
    }

    @Override
    public boolean isValid(final AbstractSchema object, final ConstraintValidatorContext context) {

        boolean isValid = false;
        EntityViolationType violation = null;

        try {
            if (object == null) {
                isValid = true;
            } else {
                isValid = object.getType() == null || !object.getType().equals(AttributeSchemaType.Enum)
                        || object.getEnumerationValues() != null;

                if (!isValid) {
                    violation = EntityViolationType.InvalidSchemaTypeSpecification;

                    throw new Exception(object + " miss enumeration values");
                }

                isValid = object.isMultivalue()
                        ? !object.isUniqueConstraint()
                        : true;

                if (!isValid) {
                    violation = EntityViolationType.MultivalueAndUniqueConstraint;

                    throw new Exception(object + " cannot be multivalue and have unique constraint at the same time");
                }
            }

            return isValid;
        } catch (Exception e) {
            LOG.error("Error saving schema", e);

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(getTemplate(violation, e.getMessage())).
                    addNode(object.getClass().getSimpleName()).addConstraintViolation();

            return false;
        }
    }
}
