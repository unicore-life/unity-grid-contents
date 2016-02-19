package pl.edu.icm.unity.grid.content.util;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import pl.edu.icm.unity.exceptions.EngineException;
import pl.edu.icm.unity.grid.content.ContentConstants;
import pl.edu.icm.unity.server.api.AttributesManagement;
import pl.edu.icm.unity.server.api.GroupsManagement;
import pl.edu.icm.unity.server.utils.Log;
import pl.edu.icm.unity.server.utils.UnityMessageSource;
import pl.edu.icm.unity.stdext.attr.EnumAttribute;
import pl.edu.icm.unity.stdext.attr.EnumAttributeSyntax;
import pl.edu.icm.unity.stdext.attr.StringAttributeSyntax;
import pl.edu.icm.unity.types.basic.AttributeStatement2;
import pl.edu.icm.unity.types.basic.AttributeType;
import pl.edu.icm.unity.types.basic.AttributeVisibility;
import pl.edu.icm.unity.types.basic.Group;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static pl.edu.icm.unity.grid.content.ContentConstants.LOG_GRID_CONTENTS;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.ADD_DEFAULT_GROUPS;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.DEFAULT_GROUP;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.DEFAULT_QUEUE;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.DEFAULT_ROLE;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.DEFAULT_SUPPLEMENTARY_GROUPS;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.DEFAULT_XLOGIN;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.GROUP;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.QUEUE;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.ROLE;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.SUPPLEMENTARY_GROUPS;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.VIRTUAL_ORGANISATIONS;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.XLOGIN;
import static pl.edu.icm.unity.sysattrs.SystemAttributeTypes.AUTHORIZATION_ROLE;

@Component
public class UnicoreContents {
    private final AttributesManagement attributesManagement;
    private final GroupsManagement groupsManagement;
    private final ManagementHelper managementHelper;
    private final UnityMessageSource messageSource;

    @Autowired
    public UnicoreContents(@Qualifier("insecure") AttributesManagement attributesManagement,
                           @Qualifier("insecure") GroupsManagement groupsManagement,
                           ManagementHelper managementHelper,
                           UnityMessageSource messageSource) {
        this.attributesManagement = attributesManagement;
        this.groupsManagement = groupsManagement;
        this.managementHelper = managementHelper;
        this.messageSource = messageSource;
    }

    public void createInspectorsGroup(final String inspectorsGroupPath) throws EngineException {
        managementHelper.createPathGroups(inspectorsGroupPath);

        AttributeStatement2[] inspectorsGroupStatements = {
                new AttributeStatement2(
                        "true",
                        null,
                        AttributeStatement2.ConflictResolution.overwrite,
                        new EnumAttribute(AUTHORIZATION_ROLE, inspectorsGroupPath, AttributeVisibility.local, "Inspector")
                )
        };
        Group inspectorsGroup = new Group(inspectorsGroupPath);
        inspectorsGroup.setAttributeStatements(inspectorsGroupStatements);
        groupsManagement.updateGroup(inspectorsGroup.toString(), inspectorsGroup);

        AttributeStatement2[] inspectorsRootStatements = {
                new AttributeStatement2(
                        "true",
                        inspectorsGroupPath,
                        AttributeStatement2.ConflictResolution.overwrite,
                        AttributeVisibility.full,
                        managementHelper.getAttribute(AUTHORIZATION_ROLE),
                        String.format("eattrs['%s']", AUTHORIZATION_ROLE))
        };
        Group rootGroup = new Group("/");
        rootGroup.setAttributeStatements(inspectorsRootStatements);
        groupsManagement.updateGroup(rootGroup.toString(), rootGroup);
    }

    public void initializeUnicoreAttributeTypes() throws EngineException {
        Map<String, AttributeType> existingAttributes = attributesManagement.getAttributeTypesAsMap();

        String[] serverEnumAttributes = {ROLE.getAttributeName(), DEFAULT_ROLE.getAttributeName()};
        for (String attribute : serverEnumAttributes) {
            if (existingAttributes.containsKey(attribute)) {
                log.warn("Attribute '" + attribute + "' already exists.");
                continue;
            }
            Set<String> allowedRoles = new HashSet<>();
            allowedRoles.add("user");
            allowedRoles.add("admin");
            allowedRoles.add("server");
            allowedRoles.add("banned");

            EnumAttributeSyntax enumAttributeSyntax = new EnumAttributeSyntax(allowedRoles);
            AttributeType roleAttributeType = new AttributeType(attribute, enumAttributeSyntax, messageSource);
            roleAttributeType.setMinElements(1);

            attributesManagement.addAttributeType(roleAttributeType);
            log.info("Added attribute type: " + roleAttributeType);
        }

        String[] stringAttributes = {
                XLOGIN.getAttributeName(),
                DEFAULT_XLOGIN.getAttributeName(),
                GROUP.getAttributeName(),
                DEFAULT_GROUP.getAttributeName(),
                SUPPLEMENTARY_GROUPS.getAttributeName(),
                DEFAULT_SUPPLEMENTARY_GROUPS.getAttributeName(),
                ADD_DEFAULT_GROUPS.getAttributeName(),
                QUEUE.getAttributeName(),
                DEFAULT_QUEUE.getAttributeName(),
                VIRTUAL_ORGANISATIONS.getAttributeName()
        };
        for (String attribute : stringAttributes) {
            if (existingAttributes.containsKey(attribute)) {
                log.warn("Attribute '" + attribute + "' already exists.");
                continue;
            }
            AttributeType newAttributeType = new AttributeType(attribute, new StringAttributeSyntax(), messageSource);
            newAttributeType.setMinElements(1);
            newAttributeType.setMaxElements(16);
            ((StringAttributeSyntax) newAttributeType.getValueType()).setMaxLength(200);
            ((StringAttributeSyntax) newAttributeType.getValueType()).setMinLength(1);

            attributesManagement.addAttributeType(newAttributeType);
            log.info("Added attribute type: " + newAttributeType);
        }
    }

    private static Logger log = Log.getLogger(LOG_GRID_CONTENTS, UnicoreContents.class);
}
