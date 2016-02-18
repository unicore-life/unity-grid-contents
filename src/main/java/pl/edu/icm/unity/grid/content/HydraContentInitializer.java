package pl.edu.icm.unity.grid.content;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.icm.unity.grid.content.util.ResourceContents;
import pl.edu.icm.unity.grid.content.util.UnicoreContents;
import pl.edu.icm.unity.grid.content.util.UnicoreGroups;
import pl.edu.icm.unity.server.utils.Log;
import pl.edu.icm.unity.server.utils.ServerInitializer;
import pl.edu.icm.unity.stdext.utils.InitializerCommon;

import java.util.Optional;

/**
 * Populates DB with UNICORE's ICM related contents.
 *
 * @author R.Kluszczynski
 */
@Component
public class HydraContentInitializer implements ServerInitializer {
    private final InitializerCommon commonInitializer;
    private final UnicoreContents unicoreContents;
    private final UnicoreGroups unicoreGroups;
    private final ResourceContents resourceContents;

    @Autowired
    public HydraContentInitializer(InitializerCommon commonInitializer,
                                   UnicoreContents unicoreContents,
                                   UnicoreGroups unicoreGroups,
                                   ResourceContents resourceContents) {
        this.commonInitializer = commonInitializer;
        this.unicoreContents = unicoreContents;
        this.unicoreGroups = unicoreGroups;
        this.resourceContents = resourceContents;
    }

    @Override
    public void run() {
        try {
            commonInitializer.initializeCommonAttributeTypes();
            commonInitializer.assignCnToAdmin();
            commonInitializer.initializeCommonAttributeStatements();

            unicoreContents.initializeUnicoreAttributeTypes();
            unicoreContents.createInspectorsGroup("/_internal/inspectors");

            unicoreGroups.createUnicoreSiteGroupStructure("/vo.icm.edu.pl/unicore", Optional.of("hydra"));

            resourceContents.processGroupsIdentities("content-hydra.json");
        } catch (Exception e) {
            log.warn("Error loading default contents by: " + getName() + ". This is not critical.", e);
        }
    }

    @Override
    public String getName() {
        return "hydraInitializer";
    }

    private static Logger log = Log.getLogger(Log.U_SERVER, HydraContentInitializer.class);
}
