package pl.edu.icm.unity.grid.content.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.isNull;

/**
 * Helper class with used collections methods.
 *
 * @author R.Kluszczynski
 */
public final class CollectionsHelper {
    private CollectionsHelper() {
    }

    public static <E> List<E> unmodifiableOrEmptyOnNull(List<E> objectList) {
        return isNull(objectList) ? Collections.emptyList() : Collections.unmodifiableList(objectList);
    }

    public static <E> boolean isNullOrEmpty(Collection<E> collection) {
        return isNull(collection) || collection.isEmpty();
    }
}
