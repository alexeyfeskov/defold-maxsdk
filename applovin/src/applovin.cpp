#define EXTENSION_NAME AppLovinMaxExt
#define LIB_NAME "AppLovinMax"
#define MODULE_NAME "applovin"

#define DLIB_LOG_DOMAIN LIB_NAME
#include <dmsdk/sdk.h>

#if defined(DM_PLATFORM_ANDROID) //|| defined(DM_PLATFORM_IOS)

#include "applovin_private.h"
#include "applovin_callback_private.h"

namespace dmAppLovinMax {

static int Lua_Initialize(lua_State* L)
{
    DM_LUA_STACK_CHECK(L, 0);
    Initialize();
    return 0;
}

static int Lua_SetCallback(lua_State* L)
{
    DM_LUA_STACK_CHECK(L, 0);
    SetLuaCallback(L, 1);
    return 0;
}

static const luaL_reg Module_methods[] =
{
    {"initialize", Lua_Initialize},
    {"set_callback", Lua_SetCallback},
    {0, 0}
};

static void LuaInit(lua_State* L)
{
    DM_LUA_STACK_CHECK(L, 0);
    luaL_register(L, MODULE_NAME, Module_methods);

    #define SETCONSTANT(name) \
    lua_pushnumber(L, (lua_Number) name); \
    lua_setfield(L, -2, #name); \

    SETCONSTANT(MSG_EXAMPLE)
    SETCONSTANT(EVENT_EXAMPLE)

    #undef SETCONSTANT

    lua_pop(L, 1);
}

static dmExtension::Result AppInitializeAppLovinMax(dmExtension::AppParams* params)
{
    return dmExtension::RESULT_OK;
}

static dmExtension::Result InitializeAppLovinMax(dmExtension::Params* params)
{
    LuaInit(params->m_L);
    Initialize_Ext();
    InitializeCallback();
    dmLogInfo("Registered extension AppLovinMax");
    return dmExtension::RESULT_OK;
}

static dmExtension::Result AppFinalizeAppLovinMax(dmExtension::AppParams* params)
{
    return dmExtension::RESULT_OK;
}

static dmExtension::Result FinalizeAppLovinMax(dmExtension::Params* params)
{
    FinalizeCallback();
    return dmExtension::RESULT_OK;
}

static dmExtension::Result UpdateAppLovinMax(dmExtension::Params* params)
{
    UpdateCallback();
    return dmExtension::RESULT_OK;
}

} //namespace dmAppLovinMax

DM_DECLARE_EXTENSION(EXTENSION_NAME, LIB_NAME, dmAppLovinMax::AppInitializeAppLovinMax, dmAppLovinMax::AppFinalizeAppLovinMax, dmAppLovinMax::InitializeAppLovinMax, dmAppLovinMax::UpdateAppLovinMax, 0, dmAppLovinMax::FinalizeAppLovinMax)

#else

static  dmExtension::Result InitializeAppLovinMax(dmExtension::Params* params)
{
    dmLogInfo("Registered extension AppLovinMax (null)");
    return dmExtension::RESULT_OK;
}

static dmExtension::Result FinalizeAppLovinMax(dmExtension::Params* params)
{
    return dmExtension::RESULT_OK;
}

DM_DECLARE_EXTENSION(EXTENSION_NAME, LIB_NAME, 0, 0, InitializeAppLovinMax, 0, 0, FinalizeAppLovinMax)

#endif // IOS/Android
