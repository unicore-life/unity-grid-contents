package pl.edu.icm.unity.grid.content;

import pl.edu.icm.unity.exceptions.EngineException;

public final class GroovyHelper {
    private static FileContentInitializer contentInitializer;

    public static void initialize(String... contentLocations) throws EngineException {
        contentInitializer.initializeContentFromResource(contentLocations);
    }

    static void setContentInitializer(FileContentInitializer fileContentInitializer) {
        contentInitializer = fileContentInitializer;
    }
}
