#include "LuaUtils.h"

namespace dmAppLovinMax {

bool luaL_checkbool(lua_State *L, int numArg)
{
    bool b = false;
    if (lua_isboolean(L, numArg))
    {
        b = lua_toboolean(L, numArg);
    }
    else
    {
        luaL_typerror(L, numArg, lua_typename(L, LUA_TBOOLEAN));
    }
    return b;
}

char* luaL_checkstringd(lua_State *L, int numArg, const char* def)
{
    int type = lua_type(L, numArg);
    if (type != LUA_TNONE && type != LUA_TNIL)
    {
        return (char*)luaL_checkstring(L, numArg);
    }
    return (char*)def;
}

void luaL_push_pair_str_num(lua_State *L, const char *key, int value)
{
    lua_pushstring(L, key);
    lua_pushnumber(L, value);
    lua_settable(L, -3);
}

void luaL_push_pair_str_str(lua_State *L, const char *key, const char *value)
{
    lua_pushstring(L, key);
    lua_pushstring(L, value);
    lua_settable(L, -3);
}

}//namespace dmAppLovinMax
