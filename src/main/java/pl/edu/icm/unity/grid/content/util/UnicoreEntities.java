package pl.edu.icm.unity.grid.content.util;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.icm.unity.exceptions.EngineException;
import pl.edu.icm.unity.server.utils.Log;
import pl.edu.icm.unity.stdext.identity.X500Identity;
import pl.edu.icm.unity.types.basic.EntityParam;
import pl.edu.icm.unity.types.basic.Group;
import pl.edu.icm.unity.types.basic.IdentityTaV;

import java.util.List;

import static pl.edu.icm.unity.grid.content.ContentConstants.LOG_GRID_CONTENTS;

/**
 * @author R.Kluszczynski
 */
@Component
public class UnicoreEntities {
    private final UnityManagements unityManagements;

    @Autowired
    public UnicoreEntities(UnityManagements unityManagements) {
        this.unityManagements = unityManagements;
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
                    .forEach(entity -> unityManagements.addMemberFromParentGroup(parentGroupPath, entity));
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

    private static Logger log = Log.getLogger(LOG_GRID_CONTENTS, UnicoreEntities.class);
}
