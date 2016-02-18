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

/**
 * Populates DB with TestBed UNICORE related contents.
 *
 * @author R.Kluszczynski
 */
@Component
public class TestbedGridContentInitializer implements ServerInitializer {
    private final InitializerCommon commonInitializer;
    private final UnicoreContents unicoreContents;
    private final UnicoreGroups unicoreGroups;
    private final ResourceContents resourceContents;

    @Autowired
    public TestbedGridContentInitializer(InitializerCommon commonInitializer,
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

            final String testbedGroupPath = "/vo.plgrid.pl/testbed";
            String[] sites = {"ICM", "NCU"};
            for (String site : sites) {
                String testbedSiteGroupPath = testbedGroupPath + "/" + site;
                unicoreGroups.createUnicoreSiteGroupStructure(testbedSiteGroupPath);
            }
            unicoreGroups.createUnicoreGroupStructure(testbedGroupPath, sites);

            resourceContents.processGroupsIdentities("content-testbed.json");
        } catch (Exception e) {
            log.warn("Error loading default contents by: " + getName() + ". This is not critical.", e);
        }
    }

    @Override
    public String getName() {
        return "testbedGridInitializer";
    }

    private static Logger log = Log.getLogger(Log.U_SERVER, TestbedGridContentInitializer.class);
}
