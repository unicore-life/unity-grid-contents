package pl.edu.icm.unity.grid.content.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import pl.edu.icm.unity.exceptions.EngineException;
import pl.edu.icm.unity.server.utils.Log;
import pl.edu.icm.unity.stdext.attr.EnumAttribute;
import pl.edu.icm.unity.stdext.attr.StringAttribute;
import pl.edu.icm.unity.types.basic.AttributeStatement2;
import pl.edu.icm.unity.types.basic.AttributeVisibility;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static pl.edu.icm.unity.grid.content.ContentConstants.LOG_GRID_CONTENTS;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.DEFAULT_QUEUE;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.ROLE;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.XLOGIN;
import static pl.edu.icm.unity.stdext.utils.InitializerCommon.CN_ATTR;
import static pl.edu.icm.unity.stdext.utils.InitializerCommon.EMAIL_ATTR;
import static pl.edu.icm.unity.stdext.utils.InitializerCommon.ORG_ATTR;
import static pl.edu.icm.unity.sysattrs.SystemAttributeTypes.AUTHORIZATION_ROLE;

/**
 * @author R.Kluszczynski
 */
public class UnicoreGroups {
    private final UnityManagements unityManagements;

    @Autowired
    public UnicoreGroups(UnityManagements unityManagements) {
        this.unityManagements = unityManagements;
    }

    public void createInspectorsGroup(final String inspectorsGroupPath) throws EngineException {
        if (unityManagements.existsGroup(inspectorsGroupPath)) {
            log.debug(String.format("Inspectors group '%s' already exists. Skipping.", inspectorsGroupPath));
            return;
        }
        unityManagements.createPathGroups(inspectorsGroupPath);

        AttributeStatement2[] inspectorsGroupStatements = {
                new AttributeStatement2(
                        "true",
                        null,
                        AttributeStatement2.ConflictResolution.overwrite,
                        new EnumAttribute(AUTHORIZATION_ROLE, inspectorsGroupPath, AttributeVisibility.local, "Inspector")
                )
        };
        unityManagements.updateGroupWithStatements(inspectorsGroupPath, Arrays.asList(inspectorsGroupStatements));

        log.info("Created inspectors group: " + inspectorsGroupPath);
    }

    public void createUnicoreCentralGroupStructure(String unicoreGroupPath, List<String> sites) throws EngineException {
        unityManagements.createPathGroups(unicoreGroupPath + "/servers");

        List<AttributeStatement2> unicoreGroupStatements = Lists.newArrayList();
        for (String site : sites) {
            unicoreGroupStatements.add(
                    new AttributeStatement2(
                            "true",
                            unicoreGroupPath + "/" + site,
                            AttributeStatement2.ConflictResolution.merge,
                            AttributeVisibility.full,
                            unityManagements.getAttribute(ROLE.getAttributeName()),
                            String.format("eattrs['%s']", ROLE.getAttributeName())
                    )
            );
        }
        unicoreGroupStatements.add(createRoleAttributeStatement(unicoreGroupPath, "servers", "server"));
        unityManagements.updateGroupWithStatements(unicoreGroupPath, unicoreGroupStatements);
    }

    public void createUnicoreSiteGroupStructure(final String unicoreSiteGroupPath,
                                                final Optional<String> defaultQueue) throws EngineException {
        unityManagements.createPathGroups(unicoreSiteGroupPath);

        final Map<String, String> groupsToRole = Maps.newLinkedHashMap();
        groupsToRole.put("servers", "server");
        groupsToRole.put("agents", "user");
        groupsToRole.put("users", "user");
        groupsToRole.put("banned", "banned");

        for (String subGroup : groupsToRole.keySet()) {
            final String subGroupPath = String.format("%s/%s", unicoreSiteGroupPath, subGroup);
            unityManagements.createPathGroups(subGroupPath);
        }

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

        unityManagements.updateGroupWithStatements(unicoreSiteGroupPath, unicoreSiteGroupStatements);
    }

    public void createUnicorePortalGroupStructure(String groupPath) throws EngineException {
        unityManagements.createPathGroups(groupPath);

        final List<AttributeStatement2> groupStatements = Lists.newArrayList();
        final String[] portalAttributes = {CN_ATTR, EMAIL_ATTR, ORG_ATTR};
        for (String attributeName : portalAttributes) {
            groupStatements.add(
                    new AttributeStatement2(
                            "true",
                            "/",
                            AttributeStatement2.ConflictResolution.skip,
                            AttributeVisibility.full,
                            unityManagements.getAttribute(attributeName),
                            String.format("eattrs['%s']", attributeName)
                    )
            );
        }
        unityManagements.updateGroupWithStatements(groupPath, groupStatements);
    }

    private AttributeStatement2 createXloginAttributeStatement(String extraAttributesGroup) {
        try {
            return new AttributeStatement2(
                    "true",
                    extraAttributesGroup,
                    AttributeStatement2.ConflictResolution.overwrite,
                    AttributeVisibility.full,
                    unityManagements.getAttribute(XLOGIN.getAttributeName()),
                    String.format("eattrs['%s']", XLOGIN.getAttributeName())
            );
        } catch (EngineException e) {
            log.warn("Could not create attributeName!", e);
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
