package pl.edu.icm.unity.grid.content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.icm.unity.exceptions.EngineException;
import pl.edu.icm.unity.grid.content.util.ResourceManagement;
import pl.edu.icm.unity.grid.content.util.UnicoreEntities;
import pl.edu.icm.unity.grid.content.util.UnicoreGroups;
import pl.edu.icm.unity.grid.content.util.UnicoreTypes;

import java.io.IOException;

/**
 * Populates DB with TestBed UNICORE related contents.
 *
 * @author R.Kluszczynski
 */
@Component
public class TestbedGridContentInitializer extends FileContentInitializer {

    @Autowired
    public TestbedGridContentInitializer(UnicoreEntities unicoreEntities,
                                         UnicoreGroups unicoreGroups,
                                         UnicoreTypes unicoreTypes,
                                         ResourceManagement resourceManagement) {
        super(unicoreEntities, unicoreGroups, unicoreTypes, resourceManagement);
    }

    @Override
    protected void initializeSpecificContent() throws EngineException, IOException {
        initializeContentFromResource("classpath:content-testbed.json");
    }

    @Override
    public String getName() {
        return "testbedGridInitializer";
    }
}
