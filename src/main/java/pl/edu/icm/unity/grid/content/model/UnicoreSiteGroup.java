package pl.edu.icm.unity.grid.content.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static pl.edu.icm.unity.grid.content.util.CollectionsHelper.unmodifiableOrEmptyOnNull;

/**
 * @author R.Kluszczynski
 */
public class UnicoreSiteGroup {
    private final String group;
    private final List<ObjectNode> agents;
    private final List<String> banned;
    private final List<String> servers;
    private final Optional<ObjectNode> attributes;

    @JsonCreator
    public UnicoreSiteGroup(@JsonProperty("group") String group,
                            @JsonProperty("agents") List<ObjectNode> agents,
                            @JsonProperty("banned") List<String> banned,
                            @JsonProperty("servers") List<String> servers,
                            @JsonProperty("attributes") ObjectNode attributes) {
        this.group = group;
        this.agents = unmodifiableOrEmptyOnNull(agents);
        this.banned = unmodifiableOrEmptyOnNull(banned);
        this.servers = unmodifiableOrEmptyOnNull(servers);
        this.attributes = attributes == null ? Optional.empty() : Optional.of(attributes.deepCopy());
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

    public Optional<ObjectNode> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return String.format("UnicoreSiteGroup{group='%s', agents=%s, banned=%s, servers=%s, attributes=%s}",
                group, agents, banned, servers, attributes);
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
                Objects.equals(attributes, that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, agents, banned, servers, attributes);
    }
}
