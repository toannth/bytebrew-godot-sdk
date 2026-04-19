//
// © 2026-present https://github.com/ByteBrewIO
//

#ifndef byte_brew_plugin_h
#define byte_brew_plugin_h

#import <Foundation/Foundation.h>

#include "core/object/class_db.h"
#include "core/object/object.h"

@class ByteBrew;

extern const String TEMPLATE_READY_SIGNAL;
// TODO: Declare all signals

class ByteBrewPlugin : public Object {
	GDCLASS(ByteBrewPlugin, Object);

private:
	static ByteBrewPlugin *instance; // Singleton instance

	static void _bind_methods();

public:
	// TODO: Declare all methods

	ByteBrewPlugin();
	~ByteBrewPlugin();
};

#endif /* byte_brew_plugin_h */
