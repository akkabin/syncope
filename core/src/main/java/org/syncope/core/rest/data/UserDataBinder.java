/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.syncope.core.rest.data;

import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.spi.Step;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javassist.NotFoundException;
import javax.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.syncope.client.mod.MembershipMod;
import org.syncope.client.mod.UserMod;
import org.syncope.client.to.MembershipTO;
import org.syncope.client.to.UserTO;
import org.syncope.client.validation.SyncopeClientCompositeErrorException;
import org.syncope.client.validation.SyncopeClientException;
import org.syncope.core.persistence.beans.AbstractAttribute;
import org.syncope.core.persistence.beans.AbstractDerivedAttribute;
import org.syncope.core.persistence.beans.TargetResource;
import org.syncope.core.persistence.beans.membership.Membership;
import org.syncope.core.persistence.beans.membership.MembershipAttribute;
import org.syncope.core.persistence.beans.membership.MembershipDerivedAttribute;
import org.syncope.core.persistence.beans.role.SyncopeRole;
import org.syncope.core.persistence.beans.user.SyncopeUser;
import org.syncope.core.persistence.propagation.ResourceOperations;
import org.syncope.types.ResourceOperationType;
import org.syncope.types.SyncopeClientExceptionType;

@Component
public class UserDataBinder extends AbstractAttributableDataBinder {

    public ResourceOperations create(SyncopeUser user, UserTO userTO)
            throws SyncopeClientCompositeErrorException, NotFoundException {

        SyncopeClientCompositeErrorException scce =
                new SyncopeClientCompositeErrorException(
                HttpStatus.BAD_REQUEST);

        // In case of overwrite, take into account memberships formerly
        // assigned to this user
        Set<Long> formerMembershipIds = new HashSet<Long>();
        for (Membership membership : user.getMemberships()) {
            formerMembershipIds.add(membership.getId());
        }

        // password
        // TODO: check password policies
        SyncopeClientException invalidPassword = new SyncopeClientException(
                SyncopeClientExceptionType.InvalidPassword);
        if (userTO.getPassword() == null || userTO.getPassword().isEmpty()) {
            LOG.error("No password provided");

            invalidPassword.addElement("Null password");
        } else {
            user.setPassword(userTO.getPassword());
        }

        if (!invalidPassword.getElements().isEmpty()) {
            scce.addException(invalidPassword);
        }

        // memberships
        SyncopeRole role = null;
        for (MembershipTO membershipTO : userTO.getMemberships()) {
            role = syncopeRoleDAO.find(membershipTO.getRoleId());

            if (role == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Ignoring invalid role "
                            + membershipTO.getRoleName());
                }
            } else {
                Membership membership = null;
                if (user.getId() != null) {
                    membership = user.getMembership(role.getId()) == null
                            ? membershipDAO.find(user, role)
                            : user.getMembership(role.getId());
                }
                if (membership == null) {
                    membership = new Membership();
                    membership.setSyncopeRole(role);
                    membership.setSyncopeUser(user);

                    user.addMembership(membership);
                } else {
                    formerMembershipIds.remove(membership.getId());
                }

                membership = (Membership) fill(membership, membershipTO,
                        AttributableUtil.MEMBERSHIP, scce);
            }
        }

        // attributes, derived attributes and resources
        user = (SyncopeUser) fill(
                user, userTO, AttributableUtil.USER, scce);

        // remove from the DB any former membership that has not been
        // renewed in this overwrite
        ResourceOperations resourceOperations = new ResourceOperations();
        if (!formerMembershipIds.isEmpty()) {
            UserMod userMod = new UserMod();
            for (Long membershipId : formerMembershipIds) {
                userMod.addMembershipToBeRemoved(membershipId);
            }
            resourceOperations = update(user, userMod);
        }

        return resourceOperations;
    }

    public ResourceOperations update(SyncopeUser user, UserMod userMod)
            throws SyncopeClientCompositeErrorException {

        SyncopeClientCompositeErrorException scce =
                new SyncopeClientCompositeErrorException(
                HttpStatus.BAD_REQUEST);

        // password
        if (userMod.getPassword() != null) {
            user.setPassword(userMod.getPassword());
        }

        // attributes, derived attributes and resources
        ResourceOperations resourceOperations =
                fill(user, userMod, AttributableUtil.USER, scce);

        // store the role ids of membership required to be added
        Set<Long> membershipToBeAddedRoleIds = new HashSet<Long>();
        for (MembershipMod membershipToBeAdded :
                userMod.getMembershipsToBeAdded()) {

            membershipToBeAddedRoleIds.add(membershipToBeAdded.getRole());
        }

        // memberships to be removed
        Membership membership = null;
        for (Long membershipToBeRemovedId :
                userMod.getMembershipsToBeRemoved()) {

            if (LOG.isDebugEnabled()) {
                LOG.debug("Membership to be removed: "
                        + membershipToBeRemovedId);
            }

            membership = membershipDAO.find(membershipToBeRemovedId);
            if (membership == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(
                            "Invalid membership id specified to be removed: "
                            + membershipToBeRemovedId);
                }
            } else {
                for (TargetResource resource :
                        membership.getSyncopeRole().getTargetResources()) {

                    if (!membershipToBeAddedRoleIds.contains(
                            membership.getSyncopeRole().getId())) {

                        resourceOperations.add(ResourceOperationType.DELETE,
                                resource);
                    }
                }

                // In order to make the removeMembership() below to work,
                // we need to be sure to take exactly the same membership
                // of the user object currently in memory (which has potentially
                // some modifications compared to the one stored in the DB
                membership = user.getMembership(
                        membership.getSyncopeRole().getId());
                if (membershipToBeAddedRoleIds.contains(
                        membership.getSyncopeRole().getId())) {

                    for (AbstractAttribute attribute :
                            membership.getAttributes()) {

                        attributeDAO.delete(attribute.getId(),
                                MembershipAttribute.class);
                    }
                    membership.getAttributes().clear();

                    for (AbstractDerivedAttribute derivedAttribute :
                            membership.getDerivedAttributes()) {

                        derivedAttributeDAO.delete(derivedAttribute.getId(),
                                MembershipDerivedAttribute.class);
                    }
                    membership.getDerivedAttributes().clear();
                } else {
                    user.removeMembership(membership);

                    membershipDAO.delete(membershipToBeRemovedId);
                }
            }
        }

        // memberships to be added
        SyncopeRole role = null;
        for (MembershipMod membershipMod :
                userMod.getMembershipsToBeAdded()) {

            if (LOG.isDebugEnabled()) {
                LOG.debug("Membership to be added: role("
                        + membershipMod.getRole() + ")");
            }

            role = syncopeRoleDAO.find(membershipMod.getRole());
            if (role == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Ignoring invalid role "
                            + membershipMod.getRole());
                }
            } else {
                membership = user.getMembership(role.getId());
                if (membership == null) {
                    membership = new Membership();
                    membership.setSyncopeRole(role);
                    membership.setSyncopeUser(user);

                    user.addMembership(membership);

                    resourceOperations.addAll(ResourceOperationType.UPDATE,
                            role.getTargetResources());
                }

                resourceOperations.merge(fill(membership, membershipMod,
                        AttributableUtil.MEMBERSHIP, scce));
            }
        }

        return resourceOperations;
    }

    public UserTO getUserTO(SyncopeUser user, Workflow userWorkflow) {
        UserTO userTO = new UserTO();
        userTO.setId(user.getId());
        userTO.setToken(user.getToken());
        userTO.setTokenExpireTime(user.getTokenExpireTime());
        userTO.setPassword(user.getPassword());

        String status = null;
        try {
            List<Step> currentSteps = userWorkflow.getCurrentSteps(
                    user.getWorkflowId());

            if (currentSteps != null && !currentSteps.isEmpty()) {
                status = currentSteps.iterator().next().getStatus();
            } else {
                LOG.error("Could not find status information for " + user);
            }
        } catch (EntityNotFoundException e) {
            LOG.error("Could not find workflow entry with id "
                    + user.getWorkflowId());
        }
        userTO.setStatus(status);

        userTO = (UserTO) fillTO(userTO, user.getAttributes(),
                user.getDerivedAttributes(), user.getTargetResources());

        MembershipTO membershipTO = null;
        for (Membership membership : user.getMemberships()) {
            membershipTO = new MembershipTO();
            membershipTO.setId(membership.getId());
            membershipTO.setRoleId(membership.getSyncopeRole().getId());
            membershipTO.setRoleName(membership.getSyncopeRole().getName());

            membershipTO = (MembershipTO) fillTO(membershipTO,
                    membership.getAttributes(),
                    membership.getDerivedAttributes(),
                    membership.getTargetResources());

            userTO.addMembership(membershipTO);
        }

        return userTO;
    }
}
