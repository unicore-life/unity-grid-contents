package pl.edu.icm.unity.grid.content.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import pl.edu.icm.unity.exceptions.EngineException;
import pl.edu.icm.unity.grid.content.ContentConstants;
import pl.edu.icm.unity.server.utils.Log;
import pl.edu.icm.unity.stdext.attr.EnumAttribute;
import pl.edu.icm.unity.stdext.attr.StringAttribute;
import pl.edu.icm.unity.types.basic.AttributeStatement2;
import pl.edu.icm.unity.types.basic.AttributeVisibility;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static pl.edu.icm.unity.grid.content.ContentConstants.LOG_GRID_CONTENTS;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.DEFAULT_QUEUE;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.ROLE;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.XLOGIN;

public class UnicoreGroups {
    private final ManagementHelper managementHelper;

    @Autowired
    public UnicoreGroups(ManagementHelper managementHelper) {
        this.managementHelper = managementHelper;
    }

    public void createUnicoreSiteGroupStructure(final String unicoreSitePath) throws EngineException {
        createUnicoreSiteGroupStructure(unicoreSitePath, Optional.empty());
    }

    public void createUnicoreGroupStructure(String unicoreGroupPath, String[] sites) throws EngineException {
        managementHelper.createPathGroups(unicoreGroupPath);

        List<AttributeStatement2> unicoreGroupStatements = Lists.newArrayList();
        for (String site : sites) {
            unicoreGroupStatements.add(
                    new AttributeStatement2(
                            "true",
                            unicoreGroupPath + "/" + site,
                            AttributeStatement2.ConflictResolution.merge,
                            AttributeVisibility.full,
                            managementHelper.getAttribute(ROLE.getAttributeName()),
                            String.format("eattrs['%s']", ROLE.getAttributeName())
                    )
            );
        }
        unicoreGroupStatements.add(
                createRoleAttributeStatement(unicoreGroupPath, "servers", "server"));
        managementHelper.updateGroupWithStatements(unicoreGroupPath, unicoreGroupStatements);
    }

    public void createUnicoreSiteGroupStructure(final String unicoreSiteGroupPath,
                                                final Optional<String> defaultQueue) throws EngineException {
        managementHelper.createPathGroups(unicoreSiteGroupPath);

        final Map<String, String> groupsToRole = Maps.newLinkedHashMap();
        groupsToRole.put("servers", "server");
        groupsToRole.put("agents", "user");
        groupsToRole.put("users", "user");
        groupsToRole.put("banned", "banned");

        groupsToRole.keySet().stream()
                .map(subGroup -> String.format("%s/%s", unicoreSiteGroupPath, subGroup))
                .forEach(managementHelper::addGroupIfNotExists);

        List<AttributeStatement2> unicoreSiteGroupStatements = Lists.newArrayList();
        unicoreSiteGroupStatements.addAll(
                groupsToRole.entrySet().stream()
                        .map(groupToRoleEntry -> createRoleAttributeStatement(
                                unicoreSiteGroupPath, groupToRoleEntry.getKey(), groupToRoleEntry.getValue()))
                        .collect(Collectors.toList())
        );
        unicoreSiteGroupStatements.addAll(
                groupsToRole.entrySet().stream()
                        .filter(stringStringEntry -> stringStringEntry.getValue().equals("user"))
                        .map(groupToRoleEntry ->
                                createXloginAttributeStatement(unicoreSiteGroupPath + "/" + groupToRoleEntry.getKey()))
                        .collect(Collectors.toList())
        );

        defaultQueue.ifPresent(defaultQueueName -> {
            final StringAttribute defaultQueueAttribute = new StringAttribute(
                    DEFAULT_QUEUE.getAttributeName(), unicoreSiteGroupPath, AttributeVisibility.full, defaultQueueName);
            unicoreSiteGroupStatements.add(
                    new AttributeStatement2("true", null, AttributeStatement2.ConflictResolution.overwrite, defaultQueueAttribute));
        });

        managementHelper.updateGroupWithStatements(unicoreSiteGroupPath, unicoreSiteGroupStatements);
    }

    private AttributeStatement2 createXloginAttributeStatement(String extraAttributesGroup) {
        try {
            return new AttributeStatement2(
                    "true",
                    extraAttributesGroup,
                    AttributeStatement2.ConflictResolution.overwrite,
                    AttributeVisibility.full,
                    managementHelper.getAttribute(XLOGIN.getAttributeName()),
                    String.format("eattrs['%s']", XLOGIN.getAttributeName())
            );
        } catch (EngineException e) {
            log.warn("Could not create attribute!", e);
            return null;
        }
    }

    private AttributeStatement2 createRoleAttributeStatement(String unicorePath, String subGroup, String subGroupRole) {
        return new AttributeStatement2(
                "groups contains '" + (unicorePath + "/" + subGroup) + "'",
                null,
                AttributeStatement2.ConflictResolution.overwrite,
                new EnumAttribute(ROLE.getAttributeName(), unicorePath, AttributeVisibility.full, subGroupRole)
        );
    }

    private static Logger log = Log.getLogger(LOG_GRID_CONTENTS, UnicoreGroups.class);
}
