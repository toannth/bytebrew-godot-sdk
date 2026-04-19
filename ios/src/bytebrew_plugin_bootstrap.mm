//
// © 2026-present https://github.com/ByteBrewIO
//

#import <Foundation/Foundation.h>

#import "bytebrew_logger.h"
#import "bytebrew_plugin.h"
#import "bytebrew_plugin_bootstrap.h"

#import "core/config/engine.h"

ByteBrewPlugin *bytebrew_plugin;

void bytebrew_plugin_init() {
	os_log_debug(bytebrew_log, "ByteBrewPlugin: Initializing plugin at timestamp: %f",
			[[NSDate date] timeIntervalSince1970]);

	bytebrew_plugin = memnew(ByteBrewPlugin);
	Engine::get_singleton()->add_singleton(Engine::Singleton("ByteBrew", bytebrew_plugin));
	os_log_debug(bytebrew_log, "ByteBrewPlugin: Singleton registered");
}

void byte_brew_plugin_deinit() {
	os_log_debug(byte_brew_log, "ByteBrewPlugin: Deinitializing plugin");
	bytebrew_log = NULL; // Prevent accidental reuse

	if (bytebrew_plugin) {
		memdelete(bytebrew_plugin);
		bytebrew_plugin = nullptr;
	}
}
