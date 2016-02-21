package pl.edu.icm.unity.grid.content.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

/**
 * @author R.Kluszczynski
 */
public class InspectorsGroup {
    private final String groupPath;
    private final List<String> identities;

    @JsonCreator
    public InspectorsGroup(@JsonProperty("group") String groupPath,
                           @JsonProperty("identities") List<String> identities) {
        this.groupPath = groupPath;
        this.identities = identities;
    }

    public String getGroup() {
        return groupPath;
    }

    public List<String> getIdentities() {
        return identities;
    }

    @Override
    public String toString() {
        return String.format("InspectorsGroup{groupPath='%s', identities=%s}", groupPath, identities);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InspectorsGroup that = (InspectorsGroup) o;
        return Objects.equals(groupPath, that.groupPath) &&
                Objects.equals(identities, that.identities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupPath, identities);
    }
}
