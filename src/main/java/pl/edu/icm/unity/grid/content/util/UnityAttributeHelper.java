package pl.edu.icm.unity.grid.content.util;

import pl.edu.icm.unity.engine.api.msg.UnityMessageSource;
import pl.edu.icm.unity.exceptions.EngineException;
import pl.edu.icm.unity.stdext.attr.StringAttributeSyntax;
import pl.edu.icm.unity.types.basic.AttributeType;

/**
 * Helper class with used Unity methods.
 *
 * @author R.Kluszczynski
 */
final class UnityAttributeHelper {
    private UnityAttributeHelper() {
    }

    static void createStringAttributeIfNotExists(String attributeName,
                                                 UnityManagements unityManagements,
                                                 UnityMessageSource messageSource,
                                                 org.apache.logging.log4j.Logger log) throws EngineException {
        if (unityManagements.existsAttribute(attributeName)) {
            log.debug("Attribute '" + attributeName + "' already exists.");
            return;
        }

        final StringAttributeSyntax stringAttributeSyntax = new StringAttributeSyntax();
        stringAttributeSyntax.setMaxLength(512);
        stringAttributeSyntax.setMinLength(1);

        AttributeType newAttributeType = new AttributeType(attributeName, StringAttributeSyntax.ID, messageSource);
        newAttributeType.setMinElements(1);
        newAttributeType.setMaxElements(16);
        newAttributeType.setValueSyntaxConfiguration(stringAttributeSyntax.getSerializedConfiguration());

        unityManagements.addAttribute(newAttributeType);
    }
}
