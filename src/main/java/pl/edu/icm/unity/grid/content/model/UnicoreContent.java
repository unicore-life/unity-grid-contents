package pl.edu.icm.unity.grid.content.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

/**
 * @author R.Kluszczynski
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnicoreContent {
    private final InspectorsGroup inspectorsGroup;
    private final List<UnicoreCentralGroup> unicoreCentralGroups;
    private final List<UnicoreSiteGroup> unicoreSiteGroups;

    @JsonCreator
    public UnicoreContent(@JsonProperty("inspectorsGroup") InspectorsGroup inspectorsGroup,
                          @JsonProperty("unicoreCentralGroups") List<UnicoreCentralGroup> unicoreCentralGroups,
                          @JsonProperty("unicoreSiteGroups") List<UnicoreSiteGroup> unicoreSiteGroups) {
        this.inspectorsGroup = inspectorsGroup;
        this.unicoreCentralGroups = unicoreCentralGroups;
        this.unicoreSiteGroups = unicoreSiteGroups;
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

    @Override
    public String toString() {
        return String.format("UnicoreContent{inspectorsGroup=%s, unicoreCentralGroups=%s, unicoreSiteGroups=%s}",
                inspectorsGroup, unicoreCentralGroups, unicoreSiteGroups);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnicoreContent that = (UnicoreContent) o;
        return Objects.equals(inspectorsGroup, that.inspectorsGroup) &&
                Objects.equals(unicoreCentralGroups, that.unicoreCentralGroups) &&
                Objects.equals(unicoreSiteGroups, that.unicoreSiteGroups);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inspectorsGroup, unicoreCentralGroups, unicoreSiteGroups);
    }
}
