package pl.edu.icm.unity.grid.content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.icm.unity.exceptions.EngineException;
import pl.edu.icm.unity.grid.content.util.ResourceContents;
import pl.edu.icm.unity.grid.content.util.UnicoreContents;
import pl.edu.icm.unity.grid.content.util.UnicoreGroups;
import pl.edu.icm.unity.stdext.utils.InitializerCommon;

import java.io.IOException;

/**
 * Populates DB with TestBed UNICORE related contents.
 *
 * @author R.Kluszczynski
 */
@Component
public class TestbedGridContentInitializer extends ContentInitializer {
    private final UnicoreGroups unicoreGroups;
    private final ResourceContents resourceContents;

    @Autowired
    public TestbedGridContentInitializer(InitializerCommon commonInitializer,
                                         UnicoreContents unicoreContents,
                                         UnicoreGroups unicoreGroups,
                                         ResourceContents resourceContents) {
        super(commonInitializer, unicoreContents);
        this.unicoreGroups = unicoreGroups;
        this.resourceContents = resourceContents;
    }

    @Override
    protected void initializeSpecificContent() throws EngineException, IOException {
        final String centralGroupPath = "/vo.plgrid.pl/testbed";
        String[] sites = {"ICM", "NCU"};
        for (String site : sites) {
            String siteGroupPath = centralGroupPath + "/" + site;
            unicoreGroups.createUnicoreSiteGroupStructure(siteGroupPath);
        }
        unicoreGroups.createUnicoreCentralGroupStructure(centralGroupPath, sites);

        resourceContents.processGroupsIdentities("content-testbed.json");
    }

    @Override
    public String getName() {
        return "testbedGridInitializer";
    }
}
