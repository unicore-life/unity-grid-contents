package pl.edu.icm.unity.grid.content.model;

import java.util.Collections;
import java.util.List;

/**
 * @author R.Kluszczynski
 */
final class CollectionsHelper {
    private CollectionsHelper() {
    }

    static <T> List<T> unmodifiableOrEmptyOnNull(List<T> objectList) {
        return objectList == null ? Collections.emptyList() : Collections.unmodifiableList(objectList);
    }
}
