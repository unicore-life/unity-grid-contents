package pl.edu.icm.unity.grid.content.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import pl.edu.icm.unity.exceptions.EngineException;
import pl.edu.icm.unity.grid.content.model.UnicoreContent;
import pl.edu.icm.unity.server.utils.Log;
import pl.edu.icm.unity.stdext.identity.X500Identity;
import pl.edu.icm.unity.types.basic.EntityParam;
import pl.edu.icm.unity.types.basic.Group;
import pl.edu.icm.unity.types.basic.IdentityTaV;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static pl.edu.icm.unity.grid.content.ContentConstants.LOG_GRID_CONTENTS;

/**
 * @author R.Kluszczynski
 */
@Component
public class ResourceContents implements ResourceLoaderAware {
    private final UnityManagements unityManagements;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ResourceLoader resourceLoader;

    @Autowired
    public ResourceContents(UnityManagements unityManagements) {
        this.unityManagements = unityManagements;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public UnicoreContent loadContentFromFile(String... resourcesLocations) throws EngineException {
        for (String location : resourcesLocations) {
            Resource resource = resourceLoader.getResource(location);
            if (resource.exists() && resource.isReadable()) {
                log.info(String.format("Reading content from resource '%s'.", location));
                try {
                    return objectMapper.readValue(resource.getInputStream(), UnicoreContent.class);
                } catch (IOException e) {
                    log.warn(String.format("Error reading content '%s'", location), e);
                }
            } else {
                log.info(String.format("Resource '%s' not exists or is not readable.", location));
            }
        }
        throw new EngineException("There was no valid initial content at locations: " + Arrays.toString(resourcesLocations));
    }

    public void addDistinguishedNamesToGroup(List<String> certificateIdentities,
                                             String groupPath) throws EngineException {
        addIdentitiesIfNotExists(certificateIdentities);
        addIdentitiesToGroup(groupPath, certificateIdentities);
    }

    private void addIdentitiesToGroup(String groupPath, List<String> contentIdentities) {
        if (contentIdentities == null) {
            return;
        }
        final String[] pathElements = new Group(groupPath).getPath();
        String topGroupPath = "";
        for (String element : pathElements) {
            topGroupPath += ("/" + element);

            final String parentGroupPath = topGroupPath;
            contentIdentities.stream()
                    .map(id -> new IdentityTaV(X500Identity.ID, id))
                    .map(EntityParam::new)
                    .forEach(entity -> unityManagements.addMemberFromParent(parentGroupPath, entity));
        }
    }

    private void addIdentitiesIfNotExists(List<String> contentIdentities) throws EngineException {
        if (contentIdentities == null) {
            return;
        }
        for (String identity : contentIdentities) {
            if (unityManagements.existsIdentity(identity)) {
                log.debug("Identity '" + identity + "' already exists.");
                continue;
            }
            unityManagements.addEntity(identity);
        }
    }

    private static Logger log = Log.getLogger(LOG_GRID_CONTENTS, ResourceContents.class);
}
