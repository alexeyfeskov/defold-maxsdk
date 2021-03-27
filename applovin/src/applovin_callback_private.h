#pragma once

#include "applovin_private.h"
#include <dmsdk/sdk.h>

namespace dmAppLovinMax {

// The same events and messages are in AppLovinMaxJNI.java
// If you change enums here, pls make sure you update them here as well

enum MessageId
{
    MSG_EXAMPLE = 1,
};

enum MessageEvent
{
    EVENT_EXAMPLE = 1,
};

struct CallbackData
{
    MessageId msg;
    char* json;
};

void SetLuaCallback(lua_State* L, int pos);
void UpdateCallback();
void InitializeCallback();
void FinalizeCallback();

void AddToQueueCallback(MessageId type, const char*json);

} //namespace dmAppLovinMax
