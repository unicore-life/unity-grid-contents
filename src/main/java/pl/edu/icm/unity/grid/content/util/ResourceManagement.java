package pl.edu.icm.unity.grid.content.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import pl.edu.icm.unity.base.utils.Log;
import pl.edu.icm.unity.exceptions.EngineException;
import pl.edu.icm.unity.grid.content.model.UnicoreContent;

import java.io.IOException;
import java.util.Arrays;

import static pl.edu.icm.unity.grid.content.ContentConstants.LOG_GRID_CONTENTS;

/**
 * Helper method for working with resources.
 *
 * @author R.Kluszczynski
 */
@Component
public class ResourceManagement implements ResourceLoaderAware {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public UnicoreContent loadContentFromFile(String... initialContentLocations) throws EngineException {
        for (String contentLocation : initialContentLocations) {
            Resource resource = resourceLoader.getResource(contentLocation);
            if (resource.exists() && resource.isReadable()) {
                log.info(String.format("Reading content from resource '%s'.", contentLocation));
                try {
                    return objectMapper.readValue(resource.getInputStream(), UnicoreContent.class);
                } catch (IOException e) {
                    log.warn(String.format("Error reading from '%s'. Skipping.", contentLocation), e);
                }
            } else {
                log.info(String.format("Resource '%s' not exists or is not readable.", contentLocation));
            }
        }
        throw new EngineException(String.format(
                "There was no valid initial content at locations: %s", Arrays.toString(initialContentLocations)));
    }

    private static Logger log = Log.getLogger(LOG_GRID_CONTENTS, ResourceManagement.class);
}
