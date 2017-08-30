package pl.edu.icm.unity.grid.content.util;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import pl.edu.icm.unity.base.utils.Log;
import pl.edu.icm.unity.engine.api.msg.UnityMessageSource;
import pl.edu.icm.unity.exceptions.EngineException;
import pl.edu.icm.unity.stdext.attr.EnumAttribute;
import pl.edu.icm.unity.stdext.attr.StringAttribute;
import pl.edu.icm.unity.types.basic.Attribute;
import pl.edu.icm.unity.types.basic.AttributeStatement;
import pl.edu.icm.unity.types.basic.AttributeStatement.ConflictResolution;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static pl.edu.icm.unity.engine.authz.RoleAttributeTypeProvider.AUTHORIZATION_ROLE;
import static pl.edu.icm.unity.grid.content.ContentConstants.LOG_GRID_CONTENT;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.ROLE;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.XLOGIN;
import static pl.edu.icm.unity.grid.content.util.UnityAttributeHelper.createStringAttributeIfNotExists;

/**
 * Component with methods operating on Unity IDM groups.
 *
 * @author R.Kluszczynski
 */
public class UnicoreGroups {
    private final UnityManagements unityManagements;
    private final UnityMessageSource messageSource;

    @Autowired
    public UnicoreGroups(UnityManagements unityManagements, UnityMessageSource messageSource) {
        this.unityManagements = unityManagements;
        this.messageSource = messageSource;
    }

    public void createInspectorsGroup(final String inspectorsGroupPath) throws EngineException {
        if (unityManagements.existsGroup(inspectorsGroupPath)) {
            log.debug(String.format("Inspectors group '%s' already exists. Skipping.", inspectorsGroupPath));
            return;
        }
        unityManagements.createPathGroups(inspectorsGroupPath);

        AttributeStatement[] inspectorsGroupStatements = {
                new AttributeStatement(
                        "true",
                        null,
                        ConflictResolution.overwrite,
                        EnumAttribute.of(AUTHORIZATION_ROLE, inspectorsGroupPath, "Inspector")
                )
        };
        unityManagements.updateGroupWithStatements(inspectorsGroupPath, Arrays.asList(inspectorsGroupStatements));

        log.info("Created inspectors group: " + inspectorsGroupPath);
    }

    public void createUnicoreCentralGroupStructure(String unicoreGroupPath, List<String> sites) throws EngineException {
        unityManagements.createPathGroups(unicoreGroupPath + "/servers");

        List<AttributeStatement> unicoreGroupStatements = Lists.newArrayList();
        for (String site : sites) {

            workaroundForUnityVersion_2_1_0(unicoreGroupStatements, unicoreGroupPath, site);

            unicoreGroupStatements.add(
                    new AttributeStatement(
                            "true",
                            unicoreGroupPath + "/" + site,
                            ConflictResolution.merge,
                            ROLE.getAttributeName(),
                            String.format("eattrs['%s']", ROLE.getAttributeName())
                    )
            );
        }
        unicoreGroupStatements.add(createRoleAttributeStatement(unicoreGroupPath, "servers", "server"));
        unityManagements.updateGroupWithStatements(unicoreGroupPath, unicoreGroupStatements);
    }

    private void workaroundForUnityVersion_2_1_0(List<AttributeStatement> unicoreGroupStatements, String unicoreGroupPath, String site) {
        unicoreGroupStatements.addAll(
                groupsToRoleMap.entrySet().stream()
                        .map(groupRoleEntry -> createRoleAttributeStatement(
                                unicoreGroupPath + "/" + site,
                                groupRoleEntry.getKey(),
                                groupRoleEntry.getValue())
                        )
                        .collect(Collectors.toList())
        );
    }

    public void createUnicoreSiteGroupStructure(final String unicoreSiteGroupPath,
                                                final Optional<ObjectNode> unicoreSiteGroupAttributes)
            throws EngineException {
        unityManagements.createPathGroups(unicoreSiteGroupPath);

        for (String subGroup : groupsToRoleMap.keySet()) {
            final String subGroupPath = String.format("%s/%s", unicoreSiteGroupPath, subGroup);
            unityManagements.createPathGroups(subGroupPath);
        }

        List<AttributeStatement> unicoreSiteGroupStatements = Lists.newArrayList();
        unicoreSiteGroupStatements.addAll(
                groupsToRoleMap.entrySet().stream()
                        .map(groupToRoleEntry -> createRoleAttributeStatement(
                                unicoreSiteGroupPath, groupToRoleEntry.getKey(), groupToRoleEntry.getValue()))
                        .collect(Collectors.toList())
        );
        unicoreSiteGroupStatements.addAll(
                groupsToRoleMap.entrySet().stream()
                        .filter(stringStringEntry -> stringStringEntry.getValue().equals("user"))
                        .map(groupToRoleEntry ->
                                createXloginAttributeStatement(unicoreSiteGroupPath + "/" + groupToRoleEntry.getKey()))
                        .collect(Collectors.toList())
        );

        if (unicoreSiteGroupAttributes.isPresent()) {
            final ObjectNode attributesNode = unicoreSiteGroupAttributes.get();
            final Iterator<String> iterator = attributesNode.fieldNames();
            while (iterator.hasNext()) {
                final String attributeKey = iterator.next();
                final String attributeValue = attributesNode.get(attributeKey).asText();

                // Note: at the moment assuming ONLY string attributes
                createStringAttributeIfNotExists(attributeKey, unityManagements, messageSource, log);

                final Attribute stringAttribute = StringAttribute.of(attributeKey, unicoreSiteGroupPath, attributeValue);

                unicoreSiteGroupStatements.add(
                        new AttributeStatement("true", null, ConflictResolution.overwrite, stringAttribute));
            }
        }

        unityManagements.updateGroupWithStatements(unicoreSiteGroupPath, unicoreSiteGroupStatements);
    }

    public void createUnicorePortalGroupStructure(String groupPath) throws EngineException {
        unityManagements.createPathGroups(groupPath);

        final List<AttributeStatement> groupStatements = Lists.newArrayList();
        final String[] portalAttributes = {"cn", "email", "o"};
        for (String attributeName : portalAttributes) {
            groupStatements.add(
                    new AttributeStatement(
                            "true",
                            "/",
                            ConflictResolution.skip,
                            attributeName,
                            String.format("eattrs['%s']", attributeName)
                    )
            );
        }
        unityManagements.updateGroupWithStatements(groupPath, groupStatements);
    }

    private AttributeStatement createXloginAttributeStatement(String extraAttributesGroup) {
        return new AttributeStatement(
                "true",
                extraAttributesGroup,
                ConflictResolution.overwrite,
                XLOGIN.getAttributeName(),
                String.format("eattrs['%s']", XLOGIN.getAttributeName())
        );
    }

    private AttributeStatement createRoleAttributeStatement(String unicorePath, String subGroup, String subGroupRole) {
        return new AttributeStatement(
                "groups contains '" + (unicorePath + "/" + subGroup) + "'",
                null,
                ConflictResolution.overwrite,
                EnumAttribute.of(ROLE.getAttributeName(), unicorePath, subGroupRole)
        );
    }

    private final static Map<String, String> groupsToRoleMap = ImmutableMap.of(
            "servers", "server",
            "agents", "user",
            "users", "user",
            "banned", "banned"
    );

    private static Logger log = Log.getLogger(LOG_GRID_CONTENT, UnicoreGroups.class);
}
