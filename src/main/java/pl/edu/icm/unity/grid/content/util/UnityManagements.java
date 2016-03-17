package pl.edu.icm.unity.grid.content.util;

import com.google.common.collect.Lists;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import pl.edu.icm.unity.exceptions.EngineException;
import pl.edu.icm.unity.exceptions.IllegalGroupValueException;
import pl.edu.icm.unity.exceptions.IllegalIdentityValueException;
import pl.edu.icm.unity.server.api.AttributesManagement;
import pl.edu.icm.unity.server.api.GroupsManagement;
import pl.edu.icm.unity.server.api.IdentitiesManagement;
import pl.edu.icm.unity.server.utils.Log;
import pl.edu.icm.unity.stdext.identity.X500Identity;
import pl.edu.icm.unity.types.EntityState;
import pl.edu.icm.unity.types.basic.Attribute;
import pl.edu.icm.unity.types.basic.AttributeStatement2;
import pl.edu.icm.unity.types.basic.AttributeType;
import pl.edu.icm.unity.types.basic.EntityParam;
import pl.edu.icm.unity.types.basic.Group;
import pl.edu.icm.unity.types.basic.GroupContents;
import pl.edu.icm.unity.types.basic.IdentityParam;
import pl.edu.icm.unity.types.basic.IdentityTaV;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.lang.String.format;
import static pl.edu.icm.unity.grid.content.ContentConstants.LOG_GRID_CONTENTS;

/**
 * Single point of entry to Unity IDM management system.
 * No other class in this project should access it directly.
 *
 * @author R.Kluszczynski
 */
@Component
class UnityManagements {
    private final AttributesManagement attributesManagement;
    private final GroupsManagement groupsManagement;
    private final IdentitiesManagement identitiesManagement;

    @Autowired
    UnityManagements(@Qualifier("insecure") AttributesManagement attributesManagement,
                     @Qualifier("insecure") GroupsManagement groupsManagement,
                     @Qualifier("insecure") IdentitiesManagement identitiesManagement) throws EngineException {
        this.attributesManagement = attributesManagement;
        this.groupsManagement = groupsManagement;
        this.identitiesManagement = identitiesManagement;
    }

    boolean existsAttribute(String attributeName) throws EngineException {
        try {
            return attributesManagement
                    .getAttributeTypesAsMap()
                    .containsKey(attributeName);
        } catch (EngineException engineException) {
            log.warn(String.format("Could not check if attribute '%s' exists!", attributeName));
            throw engineException;
        }
    }

    AttributeType getAttribute(String attributeName) throws EngineException {
        return attributesManagement
                .getAttributeTypesAsMap()
                .get(attributeName);
    }

    void addAttribute(AttributeType attributeType) throws EngineException {
        attributesManagement
                .addAttributeType(attributeType);
        log.info(String.format("Added new attribute type: %s", attributeType));
    }

    <T> void setAttribute(String identity, Attribute<T> attribute) throws EngineException {
        attributesManagement.setAttribute(
                new EntityParam(
                        new IdentityTaV(X500Identity.ID, identity)), attribute, true);
        log.info(String.format("Updating for %s attribute: %s", identity, attribute));
    }

    boolean existsIdentity(String identity) throws EngineException {
        try {
            identitiesManagement.getEntity(
                    new EntityParam(
                            new IdentityTaV(X500Identity.ID, identity)));
            return true;
        } catch (IllegalIdentityValueException e) {
            return false;
        } catch (EngineException engineException) {
            log.warn(String.format("Could not check if identity '%s' exists!", identity));
            throw engineException;
        }
    }

    void addEntity(String identity) throws EngineException {
        identitiesManagement.addEntity(
                new IdentityParam(X500Identity.ID, identity),
                EMPTY_REQUIREMENT,
                EntityState.valid,
                false
        );
        log.info(String.format("Added entity with identity: '%s'", identity));
    }

    boolean existsGroup(String groupPath) throws EngineException {
        try {
            groupsManagement.getContents(groupPath, GroupContents.METADATA);
            return true;
        } catch (IllegalGroupValueException e) {
            return false;
        } catch (EngineException engineException) {
            log.warn(String.format("Could not check if group '%s' exists (get metadata contents)!", groupPath));
            throw engineException;
        }
    }

    void createPathGroups(String groupPath) throws EngineException {
        final String[] pathElements = new Group(groupPath).getPath();
        String topGroupPath = "";
        for (String element : pathElements) {
            topGroupPath += ("/" + element);

            if (!existsGroup(topGroupPath)) {
                addGroup(topGroupPath);
            }
        }
        log.debug(String.format("Groups exists or created for path: '%s'", groupPath));
    }

    void addMemberFromParentGroup(String groupPath, EntityParam entityParam) throws EngineException {
        try {
            groupsManagement.addMemberFromParent(groupPath, entityParam);
            log.debug(String.format("Added entity '%s' to group: '%s'", entityParam, groupPath));
        } catch (IllegalGroupValueException illegalGroupException) {
            final String message = illegalGroupException.getMessage();
            final Level logLevel =
                    (message != null && message.startsWith(ALREADY_GROUP_MEMBER_MESSAGE)) ? Level.INFO : Level.WARN;

            log.log(logLevel, String.format("Entity '%s' not added to group '%s': %s", entityParam, groupPath, message));
        } catch (EngineException engineException) {
            log.warn(String.format("Problem adding entity '%s' to group '%s'", entityParam, groupPath), engineException);
            throw engineException;
        }
    }

    void updateRootGroupWithStatements(AttributeStatement2[] statements) throws EngineException {
        final Group rootGroup = groupsManagement.getContents("/", GroupContents.EVERYTHING).getGroup();
        AttributeStatement2[] currentStatements = rootGroup.getAttributeStatements();
        log.debug("Updating root group with current statements: " + Arrays.toString(currentStatements));

        List<AttributeStatement2> updatedRootStatements = Lists.newArrayList(currentStatements);
        for (AttributeStatement2 statement : statements) {
            if (!existsStatementInList(statement, currentStatements)) {
                updatedRootStatements.add(statement);
            }
        }
        updateGroupWithStatements("/", updatedRootStatements);
    }

    void updateGroupWithStatements(String groupPath, Collection<AttributeStatement2> statements) throws EngineException {
        updateGroupWithStatements(groupPath, statements.toArray(new AttributeStatement2[statements.size()]));
    }

    private void updateGroupWithStatements(String groupPath, AttributeStatement2[] statements) throws EngineException {
        Group group = "/".equals(groupPath) ?
                groupsManagement.getContents("/", GroupContents.METADATA).getGroup() : new Group(groupPath);
        group.setAttributeStatements(statements);
        groupsManagement.updateGroup(group.toString(), group);
        log.trace(String.format("Group '%s' updated with statements: %s", group, Arrays.toString(statements)));
    }

    private void addGroup(String groupPath) throws EngineException {
        Group newGroup = new Group(groupPath);
        try {
            groupsManagement.addGroup(newGroup);
            log.info(format("Added new group: '%s'", groupPath));
        } catch (EngineException engineException) {
            log.warn(String.format("Could not add not existing group '%s'!", groupPath), engineException);
            throw engineException;
        }
    }

    private boolean existsStatementInList(AttributeStatement2 statement, AttributeStatement2[] statements) {
        for (AttributeStatement2 attributeStatement : statements) {
            if (equalsAttributeStatements(statement, attributeStatement)) {
                return true;
            }
        }
        return false;
    }

    private boolean equalsAttributeStatements(AttributeStatement2 left, AttributeStatement2 right) {
        if (!left.getCondition().equals(right.getCondition())) {
            return false;
        }
        if (left.dynamicAttributeMode() != right.dynamicAttributeMode()) {
            return false;
        }
        if (!left.dynamicAttributeMode() && !right.dynamicAttributeMode()) {
            return left.getFixedAttribute().equals(right.getFixedAttribute());
        }
        if (left.dynamicAttributeMode() && right.dynamicAttributeMode()) {
            if (!left.getDynamicAttributeType().equals(right.getDynamicAttributeType()) ||
                    !left.getDynamicAttributeExpression().equals(right.getDynamicAttributeExpression())) {
                return false;
            }
        }
        return true;
    }

    private static Logger log = Log.getLogger(LOG_GRID_CONTENTS, UnityManagements.class);

    private static final String EMPTY_REQUIREMENT = "Empty requirement";
    private static final String ALREADY_GROUP_MEMBER_MESSAGE = "The entity is already a member of this group";
}
