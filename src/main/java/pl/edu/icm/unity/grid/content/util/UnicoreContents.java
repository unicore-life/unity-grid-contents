package pl.edu.icm.unity.grid.content.util;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.icm.unity.exceptions.EngineException;
import pl.edu.icm.unity.server.utils.Log;
import pl.edu.icm.unity.server.utils.UnityMessageSource;
import pl.edu.icm.unity.stdext.attr.EnumAttribute;
import pl.edu.icm.unity.stdext.attr.EnumAttributeSyntax;
import pl.edu.icm.unity.stdext.attr.StringAttributeSyntax;
import pl.edu.icm.unity.types.basic.AttributeStatement2;
import pl.edu.icm.unity.types.basic.AttributeType;
import pl.edu.icm.unity.types.basic.AttributeVisibility;

import java.util.HashSet;
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

/**
 * @author R.Kluszczynski
 */
@Component
public class UnicoreContents {
    private final ManagementHelper managementHelper;
    private final UnityMessageSource messageSource;

    @Autowired
    public UnicoreContents(ManagementHelper managementHelper, UnityMessageSource messageSource) {
        this.managementHelper = managementHelper;
        this.messageSource = messageSource;
    }

    public void createInspectorsGroup(final String inspectorsGroupPath) throws EngineException {
        if (managementHelper.existsGroup(inspectorsGroupPath)) {
            log.debug(String.format("Inspectors group '%s' already exists. Skipping.", inspectorsGroupPath));
            return;
        }
        managementHelper.createPathGroups(inspectorsGroupPath);

        AttributeStatement2[] inspectorsGroupStatements = {
                new AttributeStatement2(
                        "true",
                        null,
                        AttributeStatement2.ConflictResolution.overwrite,
                        new EnumAttribute(AUTHORIZATION_ROLE, inspectorsGroupPath, AttributeVisibility.local, "Inspector")
                )
        };
        managementHelper.updateGroupWithStatements(inspectorsGroupPath, inspectorsGroupStatements);

        log.info("Created inspectors group: " + inspectorsGroupPath);
    }

    public void initializeRootAttributeStatements(String inspectorsGroupPath) throws EngineException {
        AttributeStatement2[] rootStatements = {
                AttributeStatement2.getFixedEverybodyStatement(
                        new EnumAttribute(AUTHORIZATION_ROLE, "/", AttributeVisibility.local, "Regular User")),
                new AttributeStatement2(
                        "groups contains '" + inspectorsGroupPath + "'",
                        null,
                        AttributeStatement2.ConflictResolution.overwrite,
                        new EnumAttribute(AUTHORIZATION_ROLE, "/", AttributeVisibility.local, "Inspector")
                )
        };
        managementHelper.updateGroupWithStatements("/", rootStatements);
    }

    public void initializeUnicoreAttributeTypes() throws EngineException {
        String[] serverEnumAttributes = {ROLE.getAttributeName(), DEFAULT_ROLE.getAttributeName()};
        for (String attribute : serverEnumAttributes) {
            if (managementHelper.existsAttribute(attribute)) {
                log.debug("Attribute '" + attribute + "' already exists.");
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

            managementHelper.addAttribute(roleAttributeType);
            log.trace("Added attribute type: " + roleAttributeType);
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
            if (managementHelper.existsAttribute(attribute)) {
                log.debug("Attribute '" + attribute + "' already exists.");
                continue;
            }
            AttributeType newAttributeType = new AttributeType(attribute, new StringAttributeSyntax(), messageSource);
            newAttributeType.setMinElements(1);
            newAttributeType.setMaxElements(16);
            ((StringAttributeSyntax) newAttributeType.getValueType()).setMaxLength(200);
            ((StringAttributeSyntax) newAttributeType.getValueType()).setMinLength(1);

            managementHelper.addAttribute(newAttributeType);
            log.trace("Added attribute type: " + newAttributeType);
        }
    }

    private static Logger log = Log.getLogger(LOG_GRID_CONTENTS, UnicoreContents.class);
}
