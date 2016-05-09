package pl.edu.icm.unity.grid.content;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.icm.unity.exceptions.EngineException;
import pl.edu.icm.unity.grid.content.model.InspectorsGroup;
import pl.edu.icm.unity.grid.content.model.UnicoreCentralGroup;
import pl.edu.icm.unity.grid.content.model.UnicoreContent;
import pl.edu.icm.unity.grid.content.model.UnicoreSiteGroup;
import pl.edu.icm.unity.grid.content.util.ResourceManagement;
import pl.edu.icm.unity.grid.content.util.UnicoreEntities;
import pl.edu.icm.unity.grid.content.util.UnicoreGroups;
import pl.edu.icm.unity.grid.content.util.UnicoreTypes;
import pl.edu.icm.unity.stdext.identity.X500Identity;
import pl.edu.icm.unity.stdext.utils.InitializerCommon;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static pl.edu.icm.unity.grid.content.util.CollectionsHelper.isListNullOrEmpty;

/**
 * Populates DB with UNICORE related contents defined in configuration file.
 *
 * @author R.Kluszczynski
 */
@Component
public class FileContentInitializer extends ContentInitializer {
    private final UnicoreGroups unicoreGroups;
    private final UnicoreEntities unicoreEntities;
    private final ResourceManagement resourceManagement;

    @Autowired
    public FileContentInitializer(InitializerCommon commonInitializer,
                                  UnicoreEntities unicoreEntities,
                                  UnicoreGroups unicoreGroups,
                                  UnicoreTypes unicoreTypes,
                                  ResourceManagement resourceManagement) {
        super(commonInitializer, unicoreTypes);
        this.unicoreGroups = unicoreGroups;
        this.unicoreEntities = unicoreEntities;
        this.resourceManagement = resourceManagement;
    }

    @Override
    protected void initializeSpecificContent() throws EngineException, IOException {
        initializeContentFromResource(
                "file:conf/content-init.json",
                "file:/etc/unity-idm/content-init.json",
                "classpath:content-all.json");
    }

    protected void initializeContentFromResource(String... resourcesLocations) throws EngineException {
        final UnicoreContent content = resourceManagement.loadContentFromFile(resourcesLocations);

        final InspectorsGroup inspectorsGroup = content.getInspectorsGroup();
        processInspectorsGroup(inspectorsGroup);

        processCentralGroups(content.getUnicoreCentralGroups(), inspectorsGroup.getGroup());
        processSiteGroups(content.getUnicoreSiteGroups(), inspectorsGroup.getGroup());
        processPortalGroups(content.getUnicorePortalGroups());
    }

    private void processInspectorsGroup(InspectorsGroup inspectorsContent) throws EngineException {
        log.debug(String.format("Processing inspectors group: %s", inspectorsContent));

        final String groupPath = inspectorsContent.getGroup();
        unicoreTypes.initializeRootAttributeStatements(groupPath);
        unicoreGroups.createInspectorsGroup(groupPath);
        unicoreEntities.addDistinguishedNamesToGroup(inspectorsContent.getIdentities(), groupPath);
    }

    private void processCentralGroups(List<UnicoreCentralGroup> centralGroups,
                                      String inspectorsGroupPath) throws EngineException {
        if (isListNullOrEmpty(centralGroups)) {
            log.debug("No central groups in configuration.");
            return;
        }
        log.trace(String.format("Processing central groups: %s", centralGroups));
        for (UnicoreCentralGroup centralGroup : centralGroups) {
            processCentralGroup(centralGroup, inspectorsGroupPath);
        }
    }

    private void processCentralGroup(final UnicoreCentralGroup centralGroup,
                                     final String inspectorsGroupPath) throws EngineException {
        final String centralGroupPath = centralGroup.getGroup();
        final List<String> sites = Lists.newArrayList();

        for (UnicoreSiteGroup siteSubGroup : centralGroup.getSites()) {
            sites.add(siteSubGroup.getGroup());
            processSiteGroup(centralGroupPath + "/" + siteSubGroup.getGroup(), siteSubGroup, inspectorsGroupPath);
        }
        unicoreGroups.createUnicoreCentralGroupStructure(centralGroupPath, sites);

        unicoreEntities.addDistinguishedNamesToGroup(centralGroup.getServers(), centralGroupPath + "/servers");
        unicoreEntities.addDistinguishedNamesToGroup(centralGroup.getServers(), inspectorsGroupPath);
    }

    private void processSiteGroups(List<UnicoreSiteGroup> siteGroups,
                                   String inspectorsGroupPath) throws EngineException {
        if (isListNullOrEmpty(siteGroups)) {
            log.debug("No site groups in configuration.");
            return;
        }
        log.debug(String.format("Processing sites groups: %s", siteGroups));
        for (UnicoreSiteGroup siteGroup : siteGroups) {
            processSiteGroup(siteGroup.getGroup(), siteGroup, inspectorsGroupPath);
        }
    }

    private void processSiteGroup(final String siteGroupPath,
                                  final UnicoreSiteGroup siteGroup,
                                  final String inspectorsGroupPath) throws EngineException {
        final List<String> siteGroupServers = siteGroup.getServers();
        final Optional<String> defaultQueue = Optional.ofNullable(siteGroup.getDefaultQueue());

        unicoreGroups.createUnicoreSiteGroupStructure(siteGroupPath, siteGroup.getAttributes(), defaultQueue);

        for (ObjectNode agentNode : siteGroup.getAgents()) {
            processSiteAgent(siteGroupPath, agentNode);
        }
        unicoreEntities.addDistinguishedNamesToGroup(siteGroup.getBanned(), siteGroupPath + "/banned");

        unicoreEntities.addDistinguishedNamesToGroup(siteGroupServers, siteGroupPath + "/servers");
        unicoreEntities.addDistinguishedNamesToGroup(siteGroupServers, inspectorsGroupPath);
    }

    private void processSiteAgent(String siteGroupPath, ObjectNode agentNode) throws EngineException {
        final String x500NameKey = X500Identity.ID;
        final JsonNode x500NameNode = agentNode.findValue(x500NameKey);
        if (x500NameNode == null || "".equalsIgnoreCase(x500NameNode.asText(""))) {
            log.warn(String.format("No '%s' key in agent entry '%s'. Skipping it.", x500NameKey, agentNode.asText()));
            return;
        }

        final String identityX500Name = x500NameNode.asText();
        final String siteAgentsGroupPath = siteGroupPath + "/agents";
        unicoreEntities.addDistinguishedNamesToGroup(Arrays.asList(identityX500Name), siteAgentsGroupPath);

        final Iterator<String> iterator = agentNode.fieldNames();
        while (iterator.hasNext()) {
            final String attributeKey = iterator.next();
            if (x500NameKey.equals(attributeKey)) {
                continue;
            }

            final String attributeValue = agentNode.get(attributeKey).asText();
            unicoreEntities.setEntityGroupAttribute(identityX500Name, siteAgentsGroupPath, attributeKey, attributeValue);
        }
        log.info(String.format("Agent '%s' added to group '%s'.", identityX500Name, siteAgentsGroupPath));
    }

    private void processPortalGroups(List<String> portalGroups) throws EngineException {
        if (isListNullOrEmpty(portalGroups)) {
            log.debug("No portal groups in configuration.");
            return;
        }
        log.debug(String.format("Processing portal groups: %s", portalGroups));
        for (String group : portalGroups) {
            unicoreGroups.createUnicorePortalGroupStructure(group);
        }
    }

    @Override
    public String getName() {
        return "configurationFileInitializer";
    }
}
