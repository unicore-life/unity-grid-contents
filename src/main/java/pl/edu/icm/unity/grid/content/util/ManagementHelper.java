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
import pl.edu.icm.unity.types.basic.Group;
import pl.edu.icm.unity.types.basic.GroupContents;

import java.util.Collection;

import static java.lang.String.format;

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

    void createPathGroups(String groupPath) {
        final String[] pathElements = new Group(groupPath).getPath();
        String topGroupPath = "";
        for (String element : pathElements) {
            topGroupPath += ("/" + element);
            addGroupIfNotExists(topGroupPath);
        }
    }

    AttributeType getAttribute(String attributeName) throws EngineException {
        return attributesManagement
                .getAttributeTypesAsMap()
                .get(attributeName);
    }

    void addGroupIfNotExists(String groupPath) {
        try {
            groupsManagement.getContents(groupPath, GroupContents.METADATA);
            log.info(format("Group %s already exists...", groupPath));
        } catch (IllegalGroupValueException e) {
            Group newGroup = new Group(groupPath);
            try {
                groupsManagement.addGroup(newGroup);
                log.info(format("Added group '%s'", groupPath));
            } catch (EngineException ex) {
                log.warn("Could not add not existing group " + groupPath, ex);
            }
        } catch (EngineException e) {
            log.warn(format("Could not check if group %s exists (get contents).", groupPath));
        }
    }

    void updateGroupWithStatements(String groupPath, Collection<AttributeStatement2> statements) throws EngineException {
        Group group = new Group(groupPath);
        group.setAttributeStatements(
                statements.toArray(new AttributeStatement2[statements.size()]));
        groupsManagement.updateGroup(group.toString(), group);
    }

    private static Logger log = Log.getLogger(Log.U_SERVER, ManagementHelper.class);
}
