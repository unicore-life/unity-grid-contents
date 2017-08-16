package pl.edu.icm.unity.grid.content;

import pl.edu.icm.unity.exceptions.EngineException;

public final class GridContentHelper {
    private static FileContentInitializer contentInitializer;

    public static void initialize(String... contentLocations) throws EngineException {
        contentInitializer.initializeContentFromLocations(contentLocations);
    }

    static void setContentInitializer(FileContentInitializer fileContentInitializer) {
        contentInitializer = fileContentInitializer;
    }
}
