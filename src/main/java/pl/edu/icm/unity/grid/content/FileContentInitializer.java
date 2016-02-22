package pl.edu.icm.unity.grid.content;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.icm.unity.exceptions.EngineException;
import pl.edu.icm.unity.grid.content.model.InspectorsGroup;
import pl.edu.icm.unity.grid.content.model.UnicoreCentralGroup;
import pl.edu.icm.unity.grid.content.model.UnicoreContent;
import pl.edu.icm.unity.grid.content.model.UnicoreSiteGroup;
import pl.edu.icm.unity.grid.content.util.ResourceContents;
import pl.edu.icm.unity.grid.content.util.UnicoreContents;
import pl.edu.icm.unity.grid.content.util.UnicoreGroups;
import pl.edu.icm.unity.stdext.utils.InitializerCommon;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Populates DB with UNICORE related contents defined in configuration file.
 *
 * @author R.Kluszczynski
 */
@Component
public class FileContentInitializer extends ContentInitializer {
    private final UnicoreGroups unicoreGroups;
    private final ResourceContents resourceContents;

    @Autowired
    public FileContentInitializer(InitializerCommon commonInitializer,
                                  UnicoreContents unicoreContents,
                                  UnicoreGroups unicoreGroups,
                                  ResourceContents resourceContents) {
        super(commonInitializer, unicoreContents);
        this.unicoreGroups = unicoreGroups;
        this.resourceContents = resourceContents;
    }

    @Override
    protected void initializeSpecificContent() throws EngineException, IOException {
        initializeContentFromResource(
                "file:conf/content-init.json",
                "file:/etc/unity-idm/content-init.json",
                "classpath:content-all.json");
    }

    protected void initializeContentFromResource(String... resourcesLocations) throws EngineException {
        final UnicoreContent content = resourceContents.loadContentFromFile(resourcesLocations);

        final InspectorsGroup inspectorsGroup = content.getInspectorsGroup();
        processInspectorsGroup(inspectorsGroup);

        processCentralGroups(content.getUnicoreCentralGroups(), inspectorsGroup.getGroup());
        processSiteGroups(content.getUnicoreSiteGroups(), inspectorsGroup.getGroup());
    }

    private void processCentralGroups(List<UnicoreCentralGroup> centralGroups,
                                      String inspectorsGroupPath) throws EngineException {
        if (centralGroups == null) {
            log.debug("No central groups in configuration.");
            return;
        }
        log.trace(String.format("Processing central groups: %s", centralGroups));
        for (UnicoreCentralGroup centralGroup : centralGroups) {
            processCentralGroup(centralGroup, inspectorsGroupPath);
        }
    }

    private void processCentralGroup(UnicoreCentralGroup centralGroup,
                                     String inspectorsGroupPath) throws EngineException {
        final String centralGroupPath = centralGroup.getGroup();
        final List<String> sites = Lists.newArrayList();

        for (UnicoreSiteGroup siteSubGroup : centralGroup.getSites()) {
            sites.add(siteSubGroup.getGroup());

            processSiteGroup(
                    centralGroupPath + "/" + siteSubGroup.getGroup(),
                    siteSubGroup.getServers(),
                    Optional.ofNullable(siteSubGroup.getDefaultQueue()),
                    inspectorsGroupPath
            );
        }
        unicoreGroups.createUnicoreCentralGroupStructure(centralGroupPath, sites);

        resourceContents.addDistinguishedNamesToGroup(centralGroup.getServers(), centralGroupPath + "/servers");
        resourceContents.addDistinguishedNamesToGroup(centralGroup.getServers(), inspectorsGroupPath);
    }

    private void processSiteGroups(List<UnicoreSiteGroup> siteGroups,
                                   String inspectorsGroupPath) throws EngineException {
        if (siteGroups == null) {
            log.debug("No site groups in configuration.");
            return;
        }
        log.debug(String.format("Processing sites groups: %s", siteGroups));
        for (UnicoreSiteGroup siteGroup : siteGroups) {
            processSiteGroup(
                    siteGroup.getGroup(),
                    siteGroup.getServers(),
                    Optional.ofNullable(siteGroup.getDefaultQueue()),
                    inspectorsGroupPath);
        }
    }

    private void processSiteGroup(String siteGroupPath,
                                  List<String> siteGroupServers,
                                  Optional<String> defaultQueue,
                                  String inspectorsGroupPath) throws EngineException {
        unicoreGroups.createUnicoreSiteGroupStructure(siteGroupPath, defaultQueue);

        resourceContents.addDistinguishedNamesToGroup(siteGroupServers, siteGroupPath + "/servers");
        resourceContents.addDistinguishedNamesToGroup(siteGroupServers, inspectorsGroupPath);
    }

    private void processInspectorsGroup(InspectorsGroup inspectorsContent) throws EngineException {
        log.debug(String.format("Processing inspectors group: %s", inspectorsContent));

        final String groupPath = inspectorsContent.getGroup();

        unicoreContents.initializeRootAttributeStatements(groupPath);
        unicoreContents.createInspectorsGroup(groupPath);

        resourceContents.addDistinguishedNamesToGroup(inspectorsContent.getIdentities(), groupPath);
    }

    @Override
    public String getName() {
        return "configurationFileInitializer";
    }
}
