#define EXTENSION_NAME AppLovinMaxExt
#define LIB_NAME "AppLovinMax"
#define MODULE_NAME "maxsdk"

#define DLIB_LOG_DOMAIN LIB_NAME
#include <dmsdk/sdk.h>

#if defined(DM_PLATFORM_ANDROID) //|| defined(DM_PLATFORM_IOS)

#include "utils/LuaUtils.h"
#include "maxsdk_private.h"
#include "maxsdk_callback_private.h"

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

static int Lua_SetMuted(lua_State* L)
{
    DM_LUA_STACK_CHECK(L, 0);
    if (lua_type(L, 1) != LUA_TBOOLEAN) {
        char msg[256];
        snprintf(msg, sizeof(msg), "Expected boolean, got %s. Wrong type for SetMuted '%s'.", luaL_typename(L, 1), lua_tostring(L, 1));
        luaL_error(L, msg);
        return 0;
    }
    bool muted_lua = luaL_checkbool(L, 1);
    SetMuted(muted_lua);
    return 0;
}

static int Lua_SetVerboseLogging(lua_State* L)
{
    DM_LUA_STACK_CHECK(L, 0);
    if (lua_type(L, 1) != LUA_TBOOLEAN) {
        char msg[256];
        snprintf(msg, sizeof(msg), "Expected boolean, got %s. Wrong type for SetVerboseLogging '%s'.", luaL_typename(L, 1), lua_tostring(L, 1));
        luaL_error(L, msg);
        return 0;
    }
    bool verbose_lua = luaL_checkbool(L, 1);
    SetVerboseLogging(verbose_lua);
    return 0;
}

static int Lua_SetHasUserConsent(lua_State* L)
{
    DM_LUA_STACK_CHECK(L, 0);
    if (lua_type(L, 1) != LUA_TBOOLEAN) {
        char msg[256];
        snprintf(msg, sizeof(msg), "Expected boolean, got %s. Wrong type for SetHasUserConsent '%s'.", luaL_typename(L, 1), lua_tostring(L, 1));
        luaL_error(L, msg);
        return 0;
    }
    bool hasConsent_lua = luaL_checkbool(L, 1);
    SetHasUserConsent(hasConsent_lua);
    return 0;
}

static int Lua_SetIsAgeRestrictedUser(lua_State* L)
{
    DM_LUA_STACK_CHECK(L, 0);
    if (lua_type(L, 1) != LUA_TBOOLEAN) {
        char msg[256];
        snprintf(msg, sizeof(msg), "Expected boolean, got %s. Wrong type for SetIsAgeRestrictedUser '%s'.", luaL_typename(L, 1), lua_tostring(L, 1));
        luaL_error(L, msg);
        return 0;
    }
    bool ageRestricted_lua = luaL_checkbool(L, 1);
    SetIsAgeRestrictedUser(ageRestricted_lua);
    return 0;
}

static int Lua_SetDoNotSell(lua_State* L)
{
    DM_LUA_STACK_CHECK(L, 0);
    if (lua_type(L, 1) != LUA_TBOOLEAN) {
        char msg[256];
        snprintf(msg, sizeof(msg), "Expected boolean, got %s. Wrong type for SetDoNotSell '%s'.", luaL_typename(L, 1), lua_tostring(L, 1));
        luaL_error(L, msg);
        return 0;
    }
    bool doNotSell_lua = luaL_checkbool(L, 1);
    SetDoNotSell(doNotSell_lua);
    return 0;
}

static int Lua_LoadInterstitial(lua_State* L)
{
    DM_LUA_STACK_CHECK(L, 0);
    if (lua_type(L, 1) != LUA_TSTRING) {
        char msg[256];
        snprintf(msg, sizeof(msg), "Expected string, got %s. Wrong type for Interstitial UnitId variable '%s'.", luaL_typename(L, 1), lua_tostring(L, 1));
        luaL_error(L, msg);
        return 0;
    }
    const char* unitId_lua = luaL_checkstring(L, 1);
    LoadInterstitial(unitId_lua);
    return 0;
}

static int Lua_ShowInterstitial(lua_State* L)
{
    DM_LUA_STACK_CHECK(L, 0);
    const char* placement_lua = luaL_checkstringd(L, 1, NULL);
    ShowInterstitial(placement_lua);
    return 0;
}

static int Lua_IsInterstitialLoaded(lua_State* L)
{
    DM_LUA_STACK_CHECK(L, 1);
    bool is_loaded = IsInterstitialLoaded();
    lua_pushboolean(L, is_loaded);
    return 1;
}

static int Lua_LoadRewarded(lua_State* L)
{
    DM_LUA_STACK_CHECK(L, 0);
    if (lua_type(L, 1) != LUA_TSTRING) {
        char msg[256];
        snprintf(msg, sizeof(msg), "Expected string, got %s. Wrong type for Rewarded UnitId variable '%s'.", luaL_typename(L, 1), lua_tostring(L, 1));
        luaL_error(L, msg);
        return 0;
    }
    const char* unitId_lua = luaL_checkstring(L, 1);
    LoadRewarded(unitId_lua);
    return 0;
}

static int Lua_ShowRewarded(lua_State* L)
{
    DM_LUA_STACK_CHECK(L, 0);
    const char* placement_lua = luaL_checkstringd(L, 1, NULL);
    ShowRewarded(placement_lua);
    return 0;
}

static int Lua_IsRewardedLoaded(lua_State* L)
{
    DM_LUA_STACK_CHECK(L, 1);
    bool is_loaded = IsRewardedLoaded();
    lua_pushboolean(L, is_loaded);
    return 1;
}

static int Lua_LoadBanner(lua_State* L)
{
    DM_LUA_STACK_CHECK(L, 0);
    if (lua_type(L, 1) != LUA_TSTRING) {
        char msg[256];
        snprintf(msg, sizeof(msg), "Expected string, got %s. Wrong type for Banner UnitId variable '%s'.", luaL_typename(L, 1), lua_tostring(L, 1));
        luaL_error(L, msg);
        return 0;
    }
    const char* unitId_lua = luaL_checkstring(L, 1);
    BannerSize bannerSize_lua = SIZE_BANNER;
    if (lua_type(L, 2) != LUA_TNONE) {
        bannerSize_lua = (BannerSize)luaL_checknumber(L, 2);
    }
    LoadBanner(unitId_lua, bannerSize_lua);
    return 0;
}

static int Lua_DestroyBanner(lua_State* L)
{
    DM_LUA_STACK_CHECK(L, 0);
    DestroyBanner();
    return 0;
}

static int Lua_ShowBanner(lua_State* L)
{
    DM_LUA_STACK_CHECK(L, 0);
    BannerPosition bannerPos_lua = POS_NONE;
    if (lua_type(L, 1) != LUA_TNONE) {
        bannerPos_lua = (BannerPosition)luaL_checknumber(L, 1);
    }
    const char* placement_lua = luaL_checkstringd(L, 2, NULL);
    ShowBanner(bannerPos_lua, placement_lua);
    return 0;
}

static int Lua_HideBanner(lua_State* L)
{
    DM_LUA_STACK_CHECK(L, 0);
    HideBanner();
    return 0;
}

static int Lua_IsBannerLoaded(lua_State* L)
{
    DM_LUA_STACK_CHECK(L, 1);
    bool is_loaded = IsBannerLoaded();
    lua_pushboolean(L, is_loaded);
    return 1;
}

static int Lua_IsBannerShown(lua_State* L)
{
    DM_LUA_STACK_CHECK(L, 1);
    bool is_loaded = IsBannerShown();
    lua_pushboolean(L, is_loaded);
    return 1;
}

static const luaL_reg Module_methods[] =
{
    {"initialize", Lua_Initialize},
    {"set_callback", Lua_SetCallback},
    {"set_muted", Lua_SetMuted},
    {"set_verbose_logging", Lua_SetVerboseLogging},
    {"set_has_user_consent", Lua_SetHasUserConsent},
    {"set_is_age_restricted_user", Lua_SetIsAgeRestrictedUser},
    {"set_do_not_sell", Lua_SetDoNotSell},

    {"load_interstitial", Lua_LoadInterstitial},
    {"show_interstitial", Lua_ShowInterstitial},
    {"is_interstitial_loaded", Lua_IsInterstitialLoaded},

    {"load_rewarded", Lua_LoadRewarded},
    {"show_rewarded", Lua_ShowRewarded},
    {"is_rewarded_loaded", Lua_IsRewardedLoaded},

    {"load_banner", Lua_LoadBanner},
    {"destroy_banner", Lua_DestroyBanner},
    {"show_banner", Lua_ShowBanner},
    {"hide_banner", Lua_HideBanner},
    {"is_banner_loaded", Lua_IsBannerLoaded},
    {"is_banner_shown", Lua_IsBannerShown},
    {0, 0}
};

static void LuaInit(lua_State* L)
{
    DM_LUA_STACK_CHECK(L, 0);
    luaL_register(L, MODULE_NAME, Module_methods);

    #define SETCONSTANT(name) \
    lua_pushnumber(L, (lua_Number) name); \
    lua_setfield(L, -2, #name); \

    SETCONSTANT(MSG_INTERSTITIAL)
    SETCONSTANT(MSG_REWARDED)
    SETCONSTANT(MSG_BANNER)
    SETCONSTANT(MSG_INITIALIZATION)

    SETCONSTANT(EVENT_CLOSED)
    SETCONSTANT(EVENT_FAILED_TO_SHOW)
    SETCONSTANT(EVENT_OPENING)
    SETCONSTANT(EVENT_FAILED_TO_LOAD)
    SETCONSTANT(EVENT_LOADED)
    SETCONSTANT(EVENT_NOT_LOADED)
    SETCONSTANT(EVENT_EARNED_REWARD)
    SETCONSTANT(EVENT_COMPLETE)
    SETCONSTANT(EVENT_CLICKED)
    SETCONSTANT(EVENT_DESTROYED)
    SETCONSTANT(EVENT_EXPANDED)
    SETCONSTANT(EVENT_COLLAPSED)

    SETCONSTANT(SIZE_BANNER)
    SETCONSTANT(SIZE_LEADER)
    SETCONSTANT(SIZE_MREC)

    SETCONSTANT(POS_NONE)
    SETCONSTANT(POS_TOP_LEFT)
    SETCONSTANT(POS_TOP_CENTER)
    SETCONSTANT(POS_TOP_RIGHT)
    SETCONSTANT(POS_BOTTOM_LEFT)
    SETCONSTANT(POS_BOTTOM_CENTER)
    SETCONSTANT(POS_BOTTOM_RIGHT)
    SETCONSTANT(POS_CENTER)

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

static void OnEventAppLovinMax(dmExtension::Params* params, const dmExtension::Event* event)
{
    if(event->m_Event == dmExtension::EVENT_ID_ACTIVATEAPP)
    {
        OnActivateApp();
    }
    else if(event->m_Event == dmExtension::EVENT_ID_DEACTIVATEAPP)
    {
        OnDeactivateApp();
    }
}

} //namespace dmAppLovinMax

DM_DECLARE_EXTENSION(EXTENSION_NAME, LIB_NAME, dmAppLovinMax::AppInitializeAppLovinMax, dmAppLovinMax::AppFinalizeAppLovinMax, dmAppLovinMax::InitializeAppLovinMax, dmAppLovinMax::UpdateAppLovinMax, dmAppLovinMax::OnEventAppLovinMax, dmAppLovinMax::FinalizeAppLovinMax)

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
