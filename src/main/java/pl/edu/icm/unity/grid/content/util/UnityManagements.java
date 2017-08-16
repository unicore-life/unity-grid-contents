package pl.edu.icm.unity.grid.content.util;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import pl.edu.icm.unity.base.utils.Log;
import pl.edu.icm.unity.engine.api.AttributeTypeManagement;
import pl.edu.icm.unity.engine.api.AttributesManagement;
import pl.edu.icm.unity.engine.api.EntityManagement;
import pl.edu.icm.unity.engine.api.GroupsManagement;
import pl.edu.icm.unity.exceptions.EngineException;
import pl.edu.icm.unity.exceptions.IllegalGroupValueException;
import pl.edu.icm.unity.stdext.identity.X500Identity;
import pl.edu.icm.unity.types.basic.Attribute;
import pl.edu.icm.unity.types.basic.AttributeStatement;
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
import static pl.edu.icm.unity.grid.content.ContentConstants.LOG_GRID_CONTENT;
import static pl.edu.icm.unity.types.basic.EntityState.valid;

/**
 * Single point of entry to Unity IDM management system.
 * No other class in this project should access it directly.
 *
 * @author R.Kluszczynski
 */
@Component
class UnityManagements {
    private final AttributesManagement attributesManagement;
    private final AttributeTypeManagement attributeTypeManagement;
    private final GroupsManagement groupsManagement;
    private final EntityManagement entityManagement;

    @Autowired
    UnityManagements(@Qualifier("insecure") AttributesManagement attributesManagement,
                     @Qualifier("insecure") AttributeTypeManagement attributeTypeManagement,
                     @Qualifier("insecure") GroupsManagement groupsManagement,
                     @Qualifier("insecure") EntityManagement entityManagement) throws EngineException {
        this.attributesManagement = attributesManagement;
        this.attributeTypeManagement = attributeTypeManagement;
        this.groupsManagement = groupsManagement;
        this.entityManagement = entityManagement;
    }

    boolean existsAttribute(String attributeName) throws EngineException {
        try {
            return attributeTypeManagement
                    .getAttributeTypesAsMap()
                    .containsKey(attributeName);
        } catch (EngineException engineException) {
            log.warn(String.format("Could not check if attribute '%s' exists!", attributeName));
            throw engineException;
        }
    }

    AttributeType getAttribute(String attributeName) throws EngineException {
        return attributeTypeManagement
                .getAttributeTypesAsMap()
                .get(attributeName);
    }

    void addAttribute(AttributeType attributeType) throws EngineException {
        attributeTypeManagement
                .addAttributeType(attributeType);
        log.info(String.format("Added new attribute type: %s", attributeType));
    }

    void setAttribute(String identity, Attribute attribute) throws EngineException {
        attributesManagement.setAttribute(
                new EntityParam(
                        new IdentityTaV(X500Identity.ID, identity)), attribute, true);
        log.info(String.format("Updating for %s attribute: %s", identity, attribute));
    }

    boolean existsIdentity(String identity) throws EngineException {
        try {
            entityManagement.getEntity(
                    new EntityParam(
                            new IdentityTaV(X500Identity.ID, identity)));
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        } catch (EngineException engineException) {
            log.warn(String.format("Could not check if identity '%s' exists!", identity));
            throw engineException;
        }
    }

    void addEntity(String identity) throws EngineException {
        entityManagement.addEntity(
                new IdentityParam(X500Identity.ID, identity),
                EMPTY_REQUIREMENT,
                valid,
                false
        );
        log.info(String.format("Added entity with identity: '%s'", identity));
    }

    boolean existsGroup(String groupPath) throws EngineException {
        try {
            groupsManagement.getContents(groupPath, GroupContents.METADATA);
            return true;
        } catch (IllegalArgumentException e) {
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

    void updateRootGroupWithStatements(AttributeStatement[] statements) throws EngineException {
        final Group rootGroup = groupsManagement.getContents("/", GroupContents.EVERYTHING).getGroup();
        AttributeStatement[] currentStatements = rootGroup.getAttributeStatements();
        log.debug("Updating root group with current statements: " + Arrays.toString(currentStatements));

        List<AttributeStatement> updatedRootStatements = Lists.newArrayList(currentStatements);
        for (AttributeStatement statement : statements) {
            if (!existsStatementInList(statement, currentStatements)) {
                updatedRootStatements.add(statement);
            }
        }
        updateGroupWithStatements("/", updatedRootStatements);
    }

    void updateGroupWithStatements(String groupPath, Collection<AttributeStatement> statements) throws EngineException {
        updateGroupWithStatements(groupPath, statements.toArray(new AttributeStatement[statements.size()]));
    }

    private void updateGroupWithStatements(String groupPath, AttributeStatement[] statements) throws EngineException {
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

    private boolean existsStatementInList(AttributeStatement statement, AttributeStatement[] statements) {
        for (AttributeStatement attributeStatement : statements) {
            if (equalsAttributeStatements(statement, attributeStatement)) {
                return true;
            }
        }
        return false;
    }

    private boolean equalsAttributeStatements(AttributeStatement left, AttributeStatement right) {
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

    private static Logger log = Log.getLogger(LOG_GRID_CONTENT, UnityManagements.class);

    private static final String EMPTY_REQUIREMENT = "Empty requirement";
    private static final String ALREADY_GROUP_MEMBER_MESSAGE = "The entity is already a member of this group";
}
