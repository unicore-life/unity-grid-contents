package pl.edu.icm.unity.grid.content;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.icm.unity.exceptions.EngineException;
import pl.edu.icm.unity.grid.content.util.ResourceContents;
import pl.edu.icm.unity.grid.content.util.UnicoreContents;
import pl.edu.icm.unity.grid.content.util.UnicoreGroups;
import pl.edu.icm.unity.stdext.utils.InitializerCommon;

import java.io.IOException;
import java.util.List;

/**
 * Populates DB with Polish Grid UNICORE related contents.
 *
 * @author R.Kluszczynski
 */
@Component
public class PolishGridContentInitializer extends ContentInitializer {
    private final UnicoreGroups unicoreGroups;
    private final ResourceContents resourceContents;

    @Autowired
    public PolishGridContentInitializer(InitializerCommon commonInitializer,
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

        final String centralGroupPath = "/vo.plgrid.pl/unicore";
        List<String> sites = Lists.newArrayList("CYFRONET", "ICM", "PSNC", "WCSS", "TASK");
        for (String site : sites) {
            String siteGroupPath = centralGroupPath + "/" + site;
            unicoreGroups.createUnicoreSiteGroupStructure(siteGroupPath);
        }
        unicoreGroups.createUnicoreCentralGroupStructure(centralGroupPath, sites);

        resourceContents.processGroupsIdentities("content-plgrid.json");
    }

    @Override
    public String getName() {
        return "polishGridInitializer";
    }
}
