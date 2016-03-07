package pl.edu.icm.unity.grid.content.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

import static pl.edu.icm.unity.grid.content.util.CollectionsHelper.unmodifiableOrEmptyOnNull;

/**
 * @author R.Kluszczynski
 */
public class UnicoreCentralGroup {
    private final String groupPath;
    private final List<UnicoreSiteGroup> sites;
    private final List<String> servers;

    @JsonCreator
    public UnicoreCentralGroup(@JsonProperty("group") String groupPath,
                               @JsonProperty("sites") List<UnicoreSiteGroup> sites,
                               @JsonProperty("servers") List<String> servers) {
        this.groupPath = groupPath;
        this.sites = unmodifiableOrEmptyOnNull(sites);
        this.servers = unmodifiableOrEmptyOnNull(servers);
    }

    public String getGroup() {
        return groupPath;
    }

    public List<UnicoreSiteGroup> getSites() {
        return sites;
    }

    public List<String> getServers() {
        return servers;
    }

    @Override
    public String toString() {
        return String.format("UnicoreCentralGroup{groupPath='%s', sites=%s, servers=%s}", groupPath, sites, servers);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnicoreCentralGroup that = (UnicoreCentralGroup) o;
        return Objects.equals(groupPath, that.groupPath) &&
                Objects.equals(sites, that.sites) &&
                Objects.equals(servers, that.servers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupPath, sites, servers);
    }
}
