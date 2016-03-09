package pl.edu.icm.unity.grid.content.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

import static pl.edu.icm.unity.grid.content.util.CollectionsHelper.unmodifiableOrEmptyOnNull;

/**
 * @author R.Kluszczynski
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnicoreContent {
    private final InspectorsGroup inspectorsGroup;
    private final List<UnicoreCentralGroup> unicoreCentralGroups;
    private final List<UnicoreSiteGroup> unicoreSiteGroups;
    private final List<String> unicorePortalGroups;

    @JsonCreator
    public UnicoreContent(@JsonProperty("inspectorsGroup") InspectorsGroup inspectorsGroup,
                          @JsonProperty("unicoreCentralGroups") List<UnicoreCentralGroup> unicoreCentralGroups,
                          @JsonProperty("unicoreSiteGroups") List<UnicoreSiteGroup> unicoreSiteGroups,
                          @JsonProperty("unicorePortalGroups") List<String> unicorePortalGroups) {
        this.inspectorsGroup = inspectorsGroup;
        this.unicoreCentralGroups = unmodifiableOrEmptyOnNull(unicoreCentralGroups);
        this.unicoreSiteGroups = unmodifiableOrEmptyOnNull(unicoreSiteGroups);
        this.unicorePortalGroups = unmodifiableOrEmptyOnNull(unicorePortalGroups);
    }

    public InspectorsGroup getInspectorsGroup() {
        return inspectorsGroup;
    }

    public List<UnicoreCentralGroup> getUnicoreCentralGroups() {
        return unicoreCentralGroups;
    }

    public List<UnicoreSiteGroup> getUnicoreSiteGroups() {
        return unicoreSiteGroups;
    }

    public List<String> getUnicorePortalGroups() {
        return unicorePortalGroups;
    }

    @Override
    public String toString() {
        return String.format("UnicoreContent{inspectorsGroup=%s, unicoreCentralGroups=%s, unicoreSiteGroups=%s, " +
                "unicorePortalGroups=%s}", inspectorsGroup, unicoreCentralGroups, unicoreSiteGroups, unicorePortalGroups);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnicoreContent that = (UnicoreContent) o;
        return Objects.equals(inspectorsGroup, that.inspectorsGroup) &&
                Objects.equals(unicoreCentralGroups, that.unicoreCentralGroups) &&
                Objects.equals(unicoreSiteGroups, that.unicoreSiteGroups) &&
                Objects.equals(unicorePortalGroups, that.unicorePortalGroups);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inspectorsGroup, unicoreCentralGroups, unicoreSiteGroups, unicorePortalGroups);
    }
}
