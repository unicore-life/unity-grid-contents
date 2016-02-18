package pl.edu.icm.unity.grid.content.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentEntries {
    private final List<ContentGroup> content;

    @JsonCreator
    public ContentEntries(@JsonProperty("content") List<ContentGroup> content) {
        this.content = content;
    }

    public List<ContentGroup> getContent() {
        return content;
    }

    @Override
    public String toString() {
        return String.format("ContentEntries{content=%s}", content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContentEntries that = (ContentEntries) o;
        return Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }
}
