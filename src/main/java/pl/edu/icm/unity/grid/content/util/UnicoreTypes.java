package pl.edu.icm.unity.grid.content.util;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.icm.unity.base.utils.Log;
import pl.edu.icm.unity.engine.api.msg.UnityMessageSource;
import pl.edu.icm.unity.exceptions.EngineException;
import pl.edu.icm.unity.stdext.attr.EnumAttribute;
import pl.edu.icm.unity.stdext.attr.EnumAttributeSyntax;
import pl.edu.icm.unity.types.basic.AttributeStatement;
import pl.edu.icm.unity.types.basic.AttributeType;

import java.util.HashSet;
import java.util.Set;

import static pl.edu.icm.unity.engine.authz.RoleAttributeTypeProvider.AUTHORIZATION_ROLE;
import static pl.edu.icm.unity.grid.content.ContentConstants.LOG_GRID_CONTENT;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.ADD_DEFAULT_GROUPS;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.DEFAULT_GROUP;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.DEFAULT_PROJECT;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.DEFAULT_QUEUE;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.DEFAULT_ROLE;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.DEFAULT_SUPPLEMENTARY_GROUPS;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.DEFAULT_XLOGIN;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.GROUP;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.PROJECT;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.QUEUE;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.ROLE;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.SUPPLEMENTARY_GROUPS;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.VIRTUAL_ORGANISATIONS;
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.XLOGIN;
import static pl.edu.icm.unity.grid.content.util.UnityAttributeHelper.createStringAttributeIfNotExists;

/**
 * Component with methods workgin with Unity IDM attributes.
 *
 * @author R.Kluszczynski
 */
@Component
public class UnicoreTypes {
    private final UnityManagements unityManagements;
    private final UnityMessageSource messageSource;

    @Autowired
    public UnicoreTypes(UnityManagements unityManagements, UnityMessageSource messageSource) {
        this.unityManagements = unityManagements;
        this.messageSource = messageSource;
    }

    public void initializeRootAttributeStatements(String inspectorsGroupPath) throws EngineException {
        AttributeStatement[] rootStatements = {
                AttributeStatement.getFixedEverybodyStatement(
                        EnumAttribute.of(AUTHORIZATION_ROLE, "/", "Regular User")),
                new AttributeStatement(
                        "groups contains '" + inspectorsGroupPath + "'",
                        null,
                        AttributeStatement.ConflictResolution.overwrite,
                        EnumAttribute.of(AUTHORIZATION_ROLE, "/", "Inspector")
                )
        };
        unityManagements.updateRootGroupWithStatements(rootStatements);
    }

    public void initializeUnicoreAttributeTypes() throws EngineException {
        String[] serverEnumAttributes = {ROLE.getAttributeName(), DEFAULT_ROLE.getAttributeName()};
        for (String attribute : serverEnumAttributes) {
            if (unityManagements.existsAttribute(attribute)) {
                log.debug("Attribute '" + attribute + "' already exists.");
                continue;
            }
            Set<String> allowedRoles = new HashSet<>();
            allowedRoles.add("user");
            allowedRoles.add("admin");
            allowedRoles.add("server");
            allowedRoles.add("banned");

            EnumAttributeSyntax enumAttributeSyntax = new EnumAttributeSyntax(allowedRoles);

            AttributeType roleAttributeType = new AttributeType(attribute, EnumAttributeSyntax.ID, messageSource);
            roleAttributeType.setMinElements(1);
            roleAttributeType.setValueSyntaxConfiguration(enumAttributeSyntax.getSerializedConfiguration());

            unityManagements.addAttribute(roleAttributeType);
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
                PROJECT.getAttributeName(),
                DEFAULT_PROJECT.getAttributeName(),
                VIRTUAL_ORGANISATIONS.getAttributeName()
        };
        for (String attribute : stringAttributes) {
            createStringAttributeIfNotExists(attribute, unityManagements, messageSource, log);
        }
    }

    private static Logger log = Log.getLogger(LOG_GRID_CONTENT, UnicoreTypes.class);
}
