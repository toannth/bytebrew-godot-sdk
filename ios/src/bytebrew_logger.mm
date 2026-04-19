//
// © 2026-present https://github.com/ByteBrewIO
//

#import "bytebrew_logger.h"

// Define and initialize the shared os_log_t instance
os_log_t bytebrew_log;

__attribute__((constructor)) // Automatically runs at program startup
static void initialize_bytebrew_log(void) {
	bytebrew_log = os_log_create("org.godotengine.plugin.bytebrew", "ByteBrew");
}
