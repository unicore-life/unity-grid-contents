package pl.edu.icm.unity.grid.content.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import pl.edu.icm.unity.exceptions.EngineException;
import pl.edu.icm.unity.exceptions.IllegalGroupValueException;
import pl.edu.icm.unity.exceptions.IllegalIdentityValueException;
import pl.edu.icm.unity.grid.content.model.ContentEntries;
import pl.edu.icm.unity.grid.content.model.ContentGroup;
import pl.edu.icm.unity.server.api.GroupsManagement;
import pl.edu.icm.unity.server.api.IdentitiesManagement;
import pl.edu.icm.unity.server.utils.Log;
import pl.edu.icm.unity.stdext.identity.X500Identity;
import pl.edu.icm.unity.types.EntityState;
import pl.edu.icm.unity.types.basic.EntityParam;
import pl.edu.icm.unity.types.basic.Group;
import pl.edu.icm.unity.types.basic.IdentityParam;
import pl.edu.icm.unity.types.basic.IdentityTaV;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class ResourceContents {
    private final GroupsManagement groupsManagement;
    private final IdentitiesManagement identitiesManagement;
    private final ManagementHelper managementHelper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ResourceContents(@Qualifier("insecure") GroupsManagement groupsManagement,
                            @Qualifier("insecure") IdentitiesManagement identitiesManagement,
                            ManagementHelper managementHelper) {
        this.groupsManagement = groupsManagement;
        this.identitiesManagement = identitiesManagement;
        this.managementHelper = managementHelper;
    }

    public void processGroupsIdentities(String resourcePath) throws IOException, EngineException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        ContentEntries entries = objectMapper.readValue(inputStream, ContentEntries.class);

        List<ContentGroup> contentGroups = entries.getContent();
        for (ContentGroup group : contentGroups) {
            managementHelper.createPathGroups(group.getGroupPath());
            addIdentitiesIfNotExists(group.getContentIdentities());
            addIdentitiesToGroup(group.getGroupPath(), group.getContentIdentities());
        }
    }

    private void addIdentitiesToGroup(String groupPath, List<String> contentIdentities) {
        if (contentIdentities == null) {
            return;
        }
        final String[] pathElements = new Group(groupPath).getPath();
        String topGroupPath = "";
        for (String element : pathElements) {
            topGroupPath += ("/" + element);

            final String parentGroupPath = topGroupPath;
            contentIdentities.stream()
                    .map(id -> new IdentityTaV(X500Identity.ID, id))
                    .map(EntityParam::new)
                    .forEach(entityParam -> {
                        try {
                            groupsManagement.addMemberFromParent(parentGroupPath, entityParam);
                            log.info(String.format("Added %s to group: %s", entityParam, groupPath));
                        } catch (IllegalGroupValueException e) {
                            log.warn(String.format("Identity %s not added to group %s: %s",
                                    entityParam, groupPath, e.getMessage()));
                        } catch (EngineException e) {
                            log.warn(String.format("Problem adding %s to group: %s", entityParam, groupPath), e);
                        }
                    });
        }
    }

    private void addIdentitiesIfNotExists(List<String> contentIdentities) throws EngineException {
        if (contentIdentities == null) {
            return;
        }
        for (String identity : contentIdentities) {
            try {
                identitiesManagement.getEntity(
                        new EntityParam(
                                new IdentityTaV(X500Identity.ID, identity)
                        )
                );
                log.info(String.format("Identity '%s' already exists...", identity));
            } catch (IllegalIdentityValueException e) {
                identitiesManagement.addEntity(
                        new IdentityParam(X500Identity.ID, identity),
                        "Empty requirement",
                        EntityState.valid,
                        false
                );
                log.info(String.format("Added identity '%s'", identity));
            }
        }
    }

    private static Logger log = Log.getLogger(Log.U_SERVER, ResourceContents.class);
}