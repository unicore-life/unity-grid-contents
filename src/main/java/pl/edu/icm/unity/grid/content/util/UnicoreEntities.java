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
        if (certificateIdentities == null) {
            return;
        }

        final String[] pathElements = new Group(groupPath).getPath();
        String topGroupPath = "";
        for (String element : pathElements) {
            topGroupPath += ("/" + element);

            addIdentitiesToGroup(certificateIdentities, topGroupPath);
        }
    }

    private void addIdentitiesToGroup(List<String> certificateIdentities, String groupPath) throws EngineException {
        for (String identity : certificateIdentities) {
            EntityParam entityParam = new EntityParam(
                    new IdentityTaV(X500Identity.ID, identity)
            );

            if (!unityManagements.existsIdentity(identity)) {
                unityManagements.addEntity(identity);
            } else {
                log.debug("Identity '" + identity + "' already exists.");
            }
            unityManagements.addMemberFromParentGroup(groupPath, entityParam);
        }
    }

    private static Logger log = Log.getLogger(LOG_GRID_CONTENTS, UnicoreEntities.class);
}
