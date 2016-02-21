package pl.edu.icm.unity.grid.content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.icm.unity.exceptions.EngineException;
import pl.edu.icm.unity.grid.content.util.ResourceContents;
import pl.edu.icm.unity.grid.content.util.UnicoreContents;
import pl.edu.icm.unity.grid.content.util.UnicoreGroups;
import pl.edu.icm.unity.stdext.utils.InitializerCommon;

import java.io.IOException;

import static java.util.Optional.of;

/**
 * Populates DB with UNICORE's ICM related contents.
 *
 * @author R.Kluszczynski
 */
@Component
public class HydraContentInitializer extends ContentInitializer {
    private final UnicoreGroups unicoreGroups;
    private final ResourceContents resourceContents;

    @Autowired
    public HydraContentInitializer(InitializerCommon commonInitializer,
                                   UnicoreContents unicoreContents,
                                   UnicoreGroups unicoreGroups,
                                   ResourceContents resourceContents) {
        super(commonInitializer, unicoreContents);
        this.unicoreGroups = unicoreGroups;
        this.resourceContents = resourceContents;
    }

    @Override
    protected void initializeSpecificContent() throws EngineException, IOException {
        unicoreContents.createInspectorsGroup("/_internal/inspectors");

        unicoreGroups.createUnicoreSiteGroupStructure("/vo.icm.edu.pl/unicore", of("hydra"));

        resourceContents.processGroupsIdentities("content-hydra.json");
    }

    @Override
    public String getName() {
        return "hydraInitializer";
    }
}
