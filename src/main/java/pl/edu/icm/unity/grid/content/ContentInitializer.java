package pl.edu.icm.unity.grid.content;

import org.apache.log4j.Logger;
import pl.edu.icm.unity.exceptions.EngineException;
import pl.edu.icm.unity.grid.content.util.UnicoreContents;
import pl.edu.icm.unity.server.utils.Log;
import pl.edu.icm.unity.server.utils.ServerInitializer;
import pl.edu.icm.unity.stdext.utils.InitializerCommon;

import java.io.IOException;

import static pl.edu.icm.unity.grid.content.ContentConstants.LOG_GRID_CONTENTS;

abstract class ContentInitializer implements ServerInitializer {
    private final InitializerCommon commonInitializer;
    protected final UnicoreContents unicoreContents;

    ContentInitializer(InitializerCommon commonInitializer, UnicoreContents unicoreContents) {
        this.commonInitializer = commonInitializer;
        this.unicoreContents = unicoreContents;
    }

    @Override
    public void run() {
        try {
            commonInitializer.initializeCommonAttributeTypes();
            commonInitializer.assignCnToAdmin();

            unicoreContents.initializeUnicoreAttributeTypes();

            initializeSpecificContent();

            log.info("Initializer [" + getName() + "] finished.");
        } catch (Exception e) {
            log.warn("Error loading default contents by: " + getName() + ". This is not critical.", e);
        }
    }

    protected abstract void initializeSpecificContent() throws EngineException, IOException;

    protected static Logger log = Log.getLogger(LOG_GRID_CONTENTS, ContentInitializer.class);
}
