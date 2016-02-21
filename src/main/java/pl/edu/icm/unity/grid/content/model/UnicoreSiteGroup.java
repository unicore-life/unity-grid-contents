package pl.edu.icm.unity.grid.content.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

/**
 * @author R.Kluszczynski
 */
public class UnicoreSiteGroup {
    private final String group;
    private final List<String> servers;
    private final String defaultQueue;

    @JsonCreator
    public UnicoreSiteGroup(@JsonProperty("group") String group,
                            @JsonProperty("servers") List<String> servers,
                            @JsonProperty("defaultQueue") String defaultQueue) {
        this.group = group;
        this.servers = servers;
        this.defaultQueue = defaultQueue;
    }

    public String getGroup() {
        return group;
    }

    public List<String> getServers() {
        return servers;
    }

    public String getDefaultQueue() {
        return defaultQueue;
    }

    @Override
    public String toString() {
        return String.format("UnicoreSiteGroup{group='%s', defaultQueue='%s', servers=%s}", group, defaultQueue, servers);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnicoreSiteGroup that = (UnicoreSiteGroup) o;
        return Objects.equals(group, that.group) &&
                Objects.equals(defaultQueue, that.defaultQueue) &&
                Objects.equals(servers, that.servers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, defaultQueue, servers);
    }
}
