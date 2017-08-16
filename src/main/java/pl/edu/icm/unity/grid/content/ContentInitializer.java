package pl.edu.icm.unity.grid.content;

import org.apache.logging.log4j.Logger;
import pl.edu.icm.unity.base.utils.Log;
import pl.edu.icm.unity.engine.api.server.ServerInitializer;
import pl.edu.icm.unity.exceptions.EngineException;
import pl.edu.icm.unity.grid.content.util.UnicoreTypes;

import java.io.IOException;

import static pl.edu.icm.unity.grid.content.ContentConstants.LOG_GRID_CONTENT;

/**
 * @author R.Kluszczynski
 */
abstract class ContentInitializer implements ServerInitializer {
    protected final UnicoreTypes unicoreTypes;

    ContentInitializer(UnicoreTypes unicoreTypes) {
        this.unicoreTypes = unicoreTypes;
    }

    @Override
    public void run() {
        try {
            unicoreTypes.initializeUnicoreAttributeTypes();

            initializeSpecificContent();

            log.info("Initializer [" + getName() + "] finished.");
        } catch (Exception e) {
            log.warn("Error loading default contents by: " + getName() + ". This is not critical.", e);
        }
    }

    protected abstract void initializeSpecificContent() throws EngineException, IOException;

    protected static Logger log = Log.getLogger(LOG_GRID_CONTENT, ContentInitializer.class);
}
