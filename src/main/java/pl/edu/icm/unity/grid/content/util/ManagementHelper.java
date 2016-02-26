package pl.edu.icm.unity.grid.content.util;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import pl.edu.icm.unity.exceptions.EngineException;
import pl.edu.icm.unity.exceptions.IllegalGroupValueException;
import pl.edu.icm.unity.server.api.AttributesManagement;
import pl.edu.icm.unity.server.api.GroupsManagement;
import pl.edu.icm.unity.server.utils.Log;
import pl.edu.icm.unity.types.basic.AttributeStatement2;
import pl.edu.icm.unity.types.basic.AttributeType;
import pl.edu.icm.unity.types.basic.EntityParam;
import pl.edu.icm.unity.types.basic.Group;
import pl.edu.icm.unity.types.basic.GroupContents;

import java.util.Collection;

import static java.lang.String.format;
import static pl.edu.icm.unity.grid.content.ContentConstants.LOG_GRID_CONTENTS;

/**
 * @author R.Kluszczynski
 */
@Component
class ManagementHelper {
    private final AttributesManagement attributesManagement;
    private final GroupsManagement groupsManagement;

    @Autowired
    ManagementHelper(@Qualifier("insecure") AttributesManagement attributesManagement,
                     @Qualifier("insecure") GroupsManagement groupsManagement) throws EngineException {
        this.attributesManagement = attributesManagement;
        this.groupsManagement = groupsManagement;
    }

    void createPathGroups(String groupPath) throws EngineException {
        final String[] pathElements = new Group(groupPath).getPath();
        String topGroupPath = "";
        for (String element : pathElements) {
            topGroupPath += ("/" + element);
            addGroupIfNotExists(topGroupPath);
        }
    }

    boolean existsAttribute(String attributeName) throws EngineException {
        return attributesManagement
                .getAttributeTypesAsMap()
                .containsKey(attributeName);
    }

    AttributeType getAttribute(String attributeName) throws EngineException {
        return attributesManagement
                .getAttributeTypesAsMap()
                .get(attributeName);
    }

    void addAttribute(AttributeType attributeType) throws EngineException {
        attributesManagement
                .addAttributeType(attributeType);
    }

    void addGroupIfNotExists(String groupPath) throws EngineException {
        if (!existsGroup(groupPath)) {
            Group newGroup = new Group(groupPath);
            try {
                groupsManagement.addGroup(newGroup);
                log.info(format("Added group '%s'", groupPath));
            } catch (EngineException engineException) {
                log.warn(String.format("Could not add not existing group '%s'!", groupPath), engineException);
                throw engineException;
            }
        }
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

    void updateGroupWithStatements(String groupPath, Collection<AttributeStatement2> statements) throws EngineException {
        updateGroupWithStatements(groupPath, statements.toArray(new AttributeStatement2[statements.size()]));
    }

    void updateGroupWithStatements(String groupPath, AttributeStatement2[] statements) throws EngineException {
        Group group = "/".equals(groupPath) ?
                groupsManagement.getContents("/", GroupContents.METADATA).getGroup() : new Group(groupPath);
        group.setAttributeStatements(statements);
        groupsManagement.updateGroup(group.toString(), group);
    }

    void addMemberFromParent(String groupPath, EntityParam entityParam) {
        try {
            groupsManagement.addMemberFromParent(groupPath, entityParam);
            log.debug(String.format("Added %s to group: %s", entityParam, groupPath));
        } catch (IllegalGroupValueException e) {
            log.warn(String.format("Identity %s not added to group %s: %s",
                    entityParam, groupPath, e.getMessage()));
        } catch (EngineException e) {
            log.warn(String.format("Problem adding %s to group: %s", entityParam, groupPath), e);
        }
    }

    private static Logger log = Log.getLogger(LOG_GRID_CONTENTS, ManagementHelper.class);
}
