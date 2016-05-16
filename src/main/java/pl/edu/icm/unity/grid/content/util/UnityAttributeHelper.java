package pl.edu.icm.unity.grid.content.util;

import org.apache.log4j.Logger;
import pl.edu.icm.unity.exceptions.EngineException;
import pl.edu.icm.unity.server.utils.UnityMessageSource;
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
                                                 Logger log) throws EngineException {
        if (unityManagements.existsAttribute(attributeName)) {
            log.debug("Attribute '" + attributeName + "' already exists.");
            return;
        }

        AttributeType newAttributeType = new AttributeType(attributeName, new StringAttributeSyntax(), messageSource);
        newAttributeType.setMinElements(1);
        newAttributeType.setMaxElements(16);
        ((StringAttributeSyntax) newAttributeType.getValueType()).setMaxLength(512);
        ((StringAttributeSyntax) newAttributeType.getValueType()).setMinLength(1);

        unityManagements.addAttribute(newAttributeType);
    }
}
