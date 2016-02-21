package pl.edu.icm.unity.grid.content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.icm.unity.exceptions.EngineException;
import pl.edu.icm.unity.grid.content.util.ResourceContents;
import pl.edu.icm.unity.grid.content.util.UnicoreContents;
import pl.edu.icm.unity.grid.content.util.UnicoreGroups;
import pl.edu.icm.unity.stdext.utils.InitializerCommon;

import java.io.IOException;

/**
 * Populates DB with Polish Grid UNICORE related contents.
 *
 * @author R.Kluszczynski
 */
@Component
public class PolishGridContentInitializer extends FileContentInitializer {

    @Autowired
    public PolishGridContentInitializer(InitializerCommon commonInitializer,
                                        UnicoreContents unicoreContents,
                                        UnicoreGroups unicoreGroups,
                                        ResourceContents resourceContents) {
        super(commonInitializer, unicoreContents, unicoreGroups, resourceContents);
    }

    @Override
    protected void initializeSpecificContent() throws EngineException, IOException {
        initializeContentFromResource("content-plgrid.json");
    }

    @Override
    public String getName() {
        return "polishGridInitializer";
    }
}
