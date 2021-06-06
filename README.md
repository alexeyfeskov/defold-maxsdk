# AppLovin Max SDK

This is a native extension for [Defold engine](http://www.defold.com) with partial implementation of [AppLovin Max SDK](https://www.applovin.com/max/)

ATTENTION! Currently only Android SDK is supported! No iOs support in current implementation! Even for Android only AdColony, AppLovin, Pangle, Facebook, InMobi, Tapjoy mediation adapters are supported.

Supported ad types: [Interstitials](https://dash.applovin.com/documentation/mediation/android/getting-started/interstitials), [Rewarded Ads](https://dash.applovin.com/documentation/mediation/android/getting-started/rewarded-ads), [Banners](https://dash.applovin.com/documentation/mediation/android/getting-started/banners), [MRECs](https://dash.applovin.com/documentation/mediation/android/getting-started/mrecs)

Used AppLovin Max SDK v10.1.2.

*Disclaimer: This extension is independent and unofficial, and not associated with AppLovin in any way.*

## Installation

You can use the Max SDK extension in your own project by adding this project as a [Defold library dependency](http://www.defold.com/manuals/libraries/).
Open your game.project file and in the dependencies field under project add:

>https://github.com/alexeyfeskov/defold-maxsdk/archive/master.zip

or point to the ZIP file of a [specific release](https://github.com/alexeyfeskov/defold-maxsdk/releases).

#### Android

You must enable [AndroidX support](https://defold.com/manuals/android/#using-androidx) for your project.

Also you must add [SDK key from Applovin Dashboard](https://dash.applovin.com/docs/integration#androidEventTracking) to `game.project` file by adding lines:

```
[applovin]
sdk_key_android = YOUR_SDK_KEY_HERE
```

## Example

See the [example folder](https://github.com/alexeyfeskov/defold-maxsdk/tree/master/example) for understand how to use extension. Especially [ads.gui_script](https://github.com/alexeyfeskov/defold-maxsdk/blob/master/example/ads.gui_script) file.

## LUA Api

Please, read original [Android API docs](https://dash.applovin.com/documentation/mediation/android/getting-started/integration)

```lua
---------------------------------------
-- Initialize SDK and start load ads --
---------------------------------------
if maxsdk then
    -- To enable LDU with geolocation https://developers.facebook.com/docs/audience-network/guides/ccpa
    maxsdk.set_fb_data_processing_options("LDU", 0, 0)
    maxsdk.set_has_user_consent(true) -- GDPR
    maxsdk.set_is_age_restricted_user(false)
    maxsdk.set_do_not_sell(false) -- CCPA for all others mediated networks

    maxsdk.set_muted(false)
    maxsdk.set_verbose_logging(true)
    
    maxsdk.set_callback(maxsdk_callback)
    maxsdk.initialize()
end

-----------------------
-- Start loading ads --
-----------------------
if maxsdk then
    -- After SDK was initialized (`maxsdk.MSG_INITIALIZATION` event) - you can start loading ads
    maxsdk.load_interstitial(interstitial_ad_unit)
    maxsdk.load_rewarded(rewarded_ad_unit)
    maxsdk.load_banner(banner_ad_unit, maxsdk.SIZE_BANNER)
    -- Supported banner sizes:
    -- maxsdk.SIZE_BANNER
    -- maxsdk.SIZE_LEADER
    -- maxsdk.SIZE_MREC (use separate ad_unit, all other APIs are same to banners)

    -- To validate integration you can use Mediation Debugger
    -- https://dash.applovin.com/documentation/mediation/android/testing-networks/mediation-debugger
    maxsdk.open_mediation_debugger()
end

--------------
-- Show ads --
--------------
-- all `show_***` functions have optional `string` parameter to define placement
if maxsdk and maxsdk.is_interstitial_loaded() then
    maxsdk.show_interstitial()
end

if maxsdk and maxsdk.is_rewarded_loaded() then
    maxsdk.show_rewarded()
end

if maxsdk and maxsdk.is_banner_loaded() then
    maxsdk.show_banner(maxsdk.POS_TOP_CENTER)
    -- Supported banner positions:
    -- maxsdk.POS_BOTTOM_CENTER
    -- maxsdk.POS_BOTTOM_LEFT
    -- maxsdk.POS_BOTTOM_RIGHT
    -- maxsdk.POS_NONE
    -- maxsdk.POS_TOP_LEFT
    -- maxsdk.POS_TOP_CENTER
    -- maxsdk.POS_TOP_RIGHT
    -- maxsdk.POS_CENTER

    -- Also you can: temporary hide banner
    maxsdk.hide_banner()
    -- ..show in other position
    maxsdk.show_banner(maxsdk.POS_BOTTOM_CENTER)
    -- ..or unload
    maxsdk.destroy_banner()
end

------------------------
-- Receive SDK events --
------------------------
function maxsdk_callback(self, message_id, message)
    if message_id == maxsdk.MSG_INITIALIZATION then
        print("MSG_INITIALIZATION")

    elseif message_id == maxsdk.MSG_INTERSTITIAL then
        if message.event == maxsdk.EVENT_CLOSED then
            print("EVENT_CLOSED: Interstitial AD closed")
        elseif message.event == maxsdk.EVENT_CLICKED then
            print("EVENT_CLICKED: Interstitial AD clicked")
        elseif message.event == maxsdk.EVENT_FAILED_TO_SHOW then
            print("EVENT_FAILED_TO_SHOW: Interstitial AD failed to show", message.code, message.error)
        elseif message.event == maxsdk.EVENT_OPENING then
            print("EVENT_OPENING: Interstitial AD is opening")
        elseif message.event == maxsdk.EVENT_FAILED_TO_LOAD then
            print("EVENT_FAILED_TO_LOAD: Interstitial AD failed to load", message.code, message.error)
        elseif message.event == maxsdk.EVENT_LOADED then
            print("EVENT_LOADED: Interstitial AD loaded. Network:", message.network)
        elseif message.event == maxsdk.EVENT_NOT_LOADED then
            print("EVENT_NOT_LOADED: can't call show_interstitial() before EVENT_LOADED", message.code, message.error)
        end

    elseif message_id == maxsdk.MSG_REWARDED then
        if message.event == maxsdk.EVENT_CLOSED then
            print("EVENT_CLOSED: Rewarded AD closed")
        elseif message.event == maxsdk.EVENT_FAILED_TO_SHOW then
            print("EVENT_FAILED_TO_SHOW: Rewarded AD failed to show", message.code, message.error)
        elseif message.event == maxsdk.EVENT_OPENING then
            print("EVENT_OPENING: Rewarded AD is opening")
        elseif message.event == maxsdk.EVENT_FAILED_TO_LOAD then
            print("EVENT_FAILED_TO_LOAD: Rewarded AD failed to load", message.code, message.error)
        elseif message.event == maxsdk.EVENT_LOADED then
            print("EVENT_LOADED: Rewarded AD loaded. Network:", message.network)
        elseif message.event == maxsdk.EVENT_NOT_LOADED then
            print("EVENT_NOT_LOADED: can't call show_rewarded() before EVENT_LOADED", message.code, message.error)
        elseif message.event == maxsdk.EVENT_EARNED_REWARD then
            print("EVENT_EARNED_REWARD: Reward: ", message.amount, message.type)
        end

    elseif message_id == maxsdk.MSG_BANNER then
        if message.event == maxsdk.EVENT_LOADED then
            print("EVENT_LOADED: Banner AD loaded. Network:", message.network)
        elseif message.event == maxsdk.EVENT_OPENING then
            print("EVENT_OPENING: Banner AD is opening")
        elseif message.event == maxsdk.EVENT_FAILED_TO_LOAD then
            print("EVENT_FAILED_TO_LOAD: Banner AD failed to load", message.code, message.error)
        elseif message.event == maxsdk.EVENT_FAILED_TO_SHOW then
            print("EVENT_FAILED_TO_SHOW: Banner AD failed to show", message.code, message.error)
        elseif message.event == maxsdk.EVENT_EXPANDED then
            print("EVENT_EXPANDED: Banner AD expanded")
        elseif message.event == maxsdk.EVENT_COLLAPSED then
            print("EVENT_COLLAPSED: Banner AD coppalsed")
        elseif message.event == maxsdk.EVENT_CLICKED then
            print("EVENT_CLICKED: Banner AD clicked")
        elseif message.event == maxsdk.EVENT_CLOSED then
            print("EVENT_CLOSED: Banner AD closed")
        elseif message.event == maxsdk.EVENT_DESTROYED then
            print("EVENT_DESTROYED: Banner AD destroyed")
        elseif message.event == maxsdk.EVENT_NOT_LOADED then
            print("EVENT_NOT_LOADED: can't call show_banner() before EVENT_LOADED", message.code, message.error)
        end
    end
end
```

Feel free to push a Pull Request with other features implementation.