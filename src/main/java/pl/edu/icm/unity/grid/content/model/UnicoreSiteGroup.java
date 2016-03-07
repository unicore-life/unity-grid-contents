package pl.edu.icm.unity.grid.content.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Objects;

import static pl.edu.icm.unity.grid.content.model.CollectionsHelper.unmodifiableOrEmptyOnNull;

/**
 * @author R.Kluszczynski
 */
public class UnicoreSiteGroup {
    private final String group;
    private final List<ObjectNode> agents;
    private final List<String> banned;
    private final List<String> servers;
    private final String defaultQueue;

    @JsonCreator
    public UnicoreSiteGroup(@JsonProperty("group") String group,
                            @JsonProperty("agents") List<ObjectNode> agents,
                            @JsonProperty("banned") List<String> banned,
                            @JsonProperty("servers") List<String> servers,
                            @JsonProperty("defaultQueue") String defaultQueue) {
        this.group = group;
        this.agents = unmodifiableOrEmptyOnNull(agents);
        this.banned = unmodifiableOrEmptyOnNull(banned);
        this.servers = unmodifiableOrEmptyOnNull(servers);
        this.defaultQueue = defaultQueue;
    }

    public String getGroup() {
        return group;
    }

    public List<ObjectNode> getAgents() {
        return agents;
    }

    public List<String> getBanned() {
        return banned;
    }

    public List<String> getServers() {
        return servers;
    }

    public String getDefaultQueue() {
        return defaultQueue;
    }

    @Override
    public String toString() {
        return String.format("UnicoreSiteGroup{group='%s', agents=%s, banned=%s, servers=%s, defaultQueue='%s'}",
                group, agents, banned, servers, defaultQueue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnicoreSiteGroup that = (UnicoreSiteGroup) o;
        return Objects.equals(group, that.group) &&
                Objects.equals(agents, that.agents) &&
                Objects.equals(banned, that.banned) &&
                Objects.equals(servers, that.servers) &&
                Objects.equals(defaultQueue, that.defaultQueue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, agents, banned, servers, defaultQueue);
    }
}
