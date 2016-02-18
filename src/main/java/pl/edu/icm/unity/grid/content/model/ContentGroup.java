package pl.edu.icm.unity.grid.content.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentGroup {
    private final String groupPath;
    private final List<String> contentIdentities;
    private final List<String> contentStatements;

    @JsonCreator
    public ContentGroup(
            @JsonProperty("path") java.lang.String groupPath,
            @JsonProperty("identities") List<String> contentIdentities,
            @JsonProperty("statements") List<String> contentStatements) {
        this.groupPath = groupPath;
        this.contentIdentities = contentIdentities;
        this.contentStatements = contentStatements;
    }

    public String getGroupPath() {
        return groupPath;
    }

    public List<String> getContentIdentities() {
        return contentIdentities;
    }

    public List<String> getContentStatements() {
        return contentStatements;
    }

    @Override
    public java.lang.String toString() {
        return java.lang.String.format("ContentGroup{groupPath='%s', contentIdentities=%s, contentStatements=%s}",
                groupPath, contentIdentities, contentStatements);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContentGroup that = (ContentGroup) o;
        return Objects.equals(groupPath, that.groupPath) &&
                Objects.equals(contentIdentities, that.contentIdentities) &&
                Objects.equals(contentStatements, that.contentStatements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupPath, contentIdentities, contentStatements);
    }
}
