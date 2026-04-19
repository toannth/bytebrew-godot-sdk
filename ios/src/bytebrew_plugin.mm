//
// © 2026-present https://github.com/ByteBrewIO
//

#import "bytebrew_plugin.h"


#import "bytebrew_logger.h"

const String TEMPLATE_READY_SIGNAL = "template_ready";

ByteBrewPlugin *ByteBrewPlugin::instance = NULL;

void ByteBrewPlugin::_bind_methods() {

	ADD_SIGNAL(MethodInfo(TEMPLATE_READY_SIGNAL, PropertyInfo(Variant::DICTIONARY, "a_dict")));
}

ByteBrewPlugin::ByteBrewPlugin() {
	os_log_debug(byte_brew_log, "Plugin singleton constructor");

	ERR_FAIL_COND(instance != NULL);

	instance = this;
}

ByteBrewPlugin::~ByteBrewPlugin() {
	os_log_debug(byte_brew_log, "Plugin singleton destructor");

	if (instance == this) {
		instance = nullptr;
	}
}
