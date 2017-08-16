/*
 * Script with grid content initialization.
 *
 * Sets up grid content configuration based on file.
 */

import groovy.transform.Field
import pl.edu.icm.unity.grid.content.GridContentHelper

@Field final String CONTENT_FILE = "/etc/unity-idm/scripts/content-testbed.json"

//if (!isColdStart) {
//    log.info("Grid content already initialized, skipping it...")
//    return
//}

log.info("Initialization of grid content based on file {}...", CONTENT_FILE)
try {
    GridContentHelper.initialize(CONTENT_FILE)
}
catch (Exception e) {
    log.warn("Error loading grid content", e)
}
