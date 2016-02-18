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
 * Populates DB with Polish Grid UNICORE related contents.
 *
 * @author R.Kluszczynski
 */
@Component
public class PolishGridContentInitializer implements ServerInitializer {
    private final InitializerCommon commonInitializer;
    private final UnicoreContents unicoreContents;
    private final UnicoreGroups unicoreGroups;
    private final ResourceContents resourceContents;

    @Autowired
    public PolishGridContentInitializer(InitializerCommon commonInitializer,
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

            String[] sites = {"CYFRONET", "ICM", "PCSS", "WCSS", "TASK"};
            for (String site : sites) {
                String plgridSiteGroupPath = "/vo.plgrid.pl/unicore/" + site;
                unicoreGroups.createUnicoreSiteGroupStructure(plgridSiteGroupPath);
            }

            resourceContents.processGroupsIdentities("content-plgrid.json");
        } catch (Exception e) {
            log.warn("Error loading default contents by: " + getName() + ". This is not critical.", e);
        }
    }

    @Override
    public String getName() {
        return "polishGridInitializer";
    }

    private static Logger log = Log.getLogger(Log.U_SERVER, PolishGridContentInitializer.class);
}
