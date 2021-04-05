package com.defold.maxsdk;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinPrivacySettings;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.applovin.sdk.AppLovinSdkUtils;

import com.facebook.ads.AdSettings;

import org.json.JSONObject;
import org.json.JSONException;

public class AppLovinMaxJNI {

    private static final String TAG = "AppLovinMaxJNI";

    public static native void maxsdkAddToQueue(int msg, String json);

    // CONSTANTS:
    // duplicate of enums from maxsdk_callback_private.h:
    private static final int MSG_INTERSTITIAL =         1;
    private static final int MSG_REWARDED =             2;
    private static final int MSG_BANNER =               3;
    private static final int MSG_INITIALIZATION =       4;

    private static final int EVENT_CLOSED =             1;
    private static final int EVENT_FAILED_TO_SHOW =     2;
    private static final int EVENT_OPENING =            3;
    private static final int EVENT_FAILED_TO_LOAD =     4;
    private static final int EVENT_LOADED =             5;
    private static final int EVENT_NOT_LOADED =         6;
    private static final int EVENT_EARNED_REWARD =      7;
    private static final int EVENT_COMPLETE =           8;
    private static final int EVENT_CLICKED =            9;
    private static final int EVENT_DESTROYED =          10;
    private static final int EVENT_EXPANDED =           11;
    private static final int EVENT_COLLAPSED =          12;

    // duplicate of enums from maxsdk_private.h:
    private static final int SIZE_BANNER =              0;
    private static final int SIZE_LEADER =              1;
    private static final int SIZE_MREC =                2;

    private static final int POS_NONE =                 0;
    private static final int POS_TOP_LEFT =             1;
    private static final int POS_TOP_CENTER =           2;
    private static final int POS_TOP_RIGHT =            3;
    private static final int POS_BOTTOM_LEFT =          4;
    private static final int POS_BOTTOM_CENTER =        5;
    private static final int POS_BOTTOM_RIGHT =         6;
    private static final int POS_CENTER =               7;
    // END CONSTANTS

    private Activity mActivity;

    public AppLovinMaxJNI(final Activity activity) {
        mActivity = activity;
    }

    public void initialize() {
        AppLovinSdk.getInstance(mActivity).setMediationProvider(AppLovinMediationProvider.MAX);
        AppLovinSdk.getInstance(mActivity).initializeSdk(new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration config) {
                sendSimpleMessage(MSG_INITIALIZATION, EVENT_COMPLETE);
            }
        });
    }

    public void onActivateApp()
    {
        pauseBanner();
    }

    public void onDeactivateApp()
    {
        resumeBanner();
    }

    public void setMuted(boolean muted) {
        AppLovinSdk.getInstance(mActivity).getSettings().setMuted(muted);
    }

    public void setVerboseLogging(boolean isVerboseLoggingEnabled) {
        AppLovinSdk.getInstance(mActivity).getSettings().setVerboseLogging(isVerboseLoggingEnabled);
    }

    public void setHasUserConsent(boolean hasUserConsent) {
        AppLovinPrivacySettings.setHasUserConsent(hasUserConsent, mActivity);
    }

    public void setIsAgeRestrictedUser(boolean isAgeRestrictedUser) {
        AppLovinPrivacySettings.setIsAgeRestrictedUser(isAgeRestrictedUser, mActivity);
    }

    public void setDoNotSell(boolean doNotSell) {
        AppLovinPrivacySettings.setDoNotSell(doNotSell, mActivity);
    }

    public void setFbDataProcessingOptions(String mode, int country, int state) {
        if (mode != null) {
            AdSettings.setDataProcessingOptions(new String[] {mode}, country, state);
            Log.d(TAG, String.format("AdSettings.setDataProcessingOptions(new String[] {`%s`}, %d, %d)", mode, country, state));
        } else {
            AdSettings.setDataProcessingOptions(new String[] {});
            Log.d(TAG, "AdSettings.setDataProcessingOptions(new String[] {})");
        }
    }

    public void openMediationDebugger() {
        AppLovinSdk.getInstance(mActivity).showMediationDebugger();
    }

    // https://www.baeldung.com/java-json-escaping
    private String getJsonConversionErrorMessage(String messageText) {
        String message = null;
        try {
            JSONObject obj = new JSONObject();
            obj.put("error", messageText);
            message = obj.toString();
        } catch (JSONException e) {
            message = "{ \"error\": \"Error while converting simple message to JSON.\" }";
        }
        return message;
    }

    private void sendSimpleMessage(int msg, int eventId) {
        String message = null;
        try {
            JSONObject obj = new JSONObject();
            obj.put("event", eventId);
            message = obj.toString();
        } catch (JSONException e) {
            message = getJsonConversionErrorMessage(e.getMessage());
        }
        maxsdkAddToQueue(msg, message);
    }

    private void sendSimpleMessage(int msg, int eventId, String key_2, String value_2) {
        String message = null;
        try {
            JSONObject obj = new JSONObject();
            obj.put("event", eventId);
            obj.put(key_2, value_2);
            message = obj.toString();
        } catch (JSONException e) {
            message = getJsonConversionErrorMessage(e.getMessage());
        }
        maxsdkAddToQueue(msg, message);
    }

    private void sendSimpleMessage(int msg, int eventId, String key_2, int value_2, String key_3, String value_3) {
        String message = null;
        try {
            JSONObject obj = new JSONObject();
            obj.put("event", eventId);
            obj.put(key_2, value_2);
            obj.put(key_3, value_3);
            message = obj.toString();
        } catch (JSONException e) {
            message = getJsonConversionErrorMessage(e.getMessage());
        }
        maxsdkAddToQueue(msg, message);
    }

//--------------------------------------------------
// Interstitial ADS

    private  MaxInterstitialAd mInterstitialAd;

    public void loadInterstitial(final String unitId) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final MaxInterstitialAd adInstance = new MaxInterstitialAd(unitId, mActivity);
                adInstance.setListener(new MaxAdListener() {
                    @Override
                    public void onAdLoaded(MaxAd ad) {
                        mInterstitialAd = adInstance;
                        sendSimpleMessage(MSG_INTERSTITIAL, EVENT_LOADED,
                                "network", ad.getNetworkName());
                    }

                    @Override
                    public void onAdLoadFailed(String adUnitId, int errorCode) {
                        sendSimpleMessage(MSG_INTERSTITIAL, EVENT_FAILED_TO_LOAD,
                                "code", errorCode, "error", getErrorMessage(adUnitId, errorCode));
                    }

                    @Override
                    public void onAdDisplayed(MaxAd ad) {
                        if (mInterstitialAd == adInstance) {
                            mInterstitialAd = null;
                        }

                        sendSimpleMessage(MSG_INTERSTITIAL, EVENT_OPENING,
                                "network", ad.getNetworkName());
                    }

                    @Override
                    public void onAdDisplayFailed(MaxAd ad, int errorCode) {
                        if (mInterstitialAd == adInstance) {
                            mInterstitialAd = null;
                        }

                        sendSimpleMessage(MSG_INTERSTITIAL, EVENT_FAILED_TO_SHOW,
                                "code", errorCode, "error", getErrorMessage(ad, errorCode));
                    }

                    @Override
                    public void onAdHidden(MaxAd ad) {
                        sendSimpleMessage(MSG_INTERSTITIAL, EVENT_CLOSED,
                                "network", ad.getNetworkName());
                    }

                    @Override
                    public void onAdClicked(MaxAd ad) {
                        sendSimpleMessage(MSG_INTERSTITIAL, EVENT_CLICKED,
                                "network", ad.getNetworkName());
                    }
                });

                adInstance.loadAd();
            }
        });
    }

    public void showInterstitial(final String placement) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isInterstitialLoaded()) {
                    mInterstitialAd.showAd(placement);
                } else {
                    // Log.d(TAG, "The interstitial ad wasn't ready yet.");
                    sendSimpleMessage(MSG_INTERSTITIAL, EVENT_NOT_LOADED,
                            "error", "Can't show Interstitial AD that wasn't loaded.");
                }
            }
        });
    }

    public boolean isInterstitialLoaded() {
        return mInterstitialAd != null && mInterstitialAd.isReady();
    }

//--------------------------------------------------
// Rewarded ADS

    private MaxRewardedAd mRewardedAd;

    public void loadRewarded(final String unitId) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final MaxRewardedAd adInstance = MaxRewardedAd.getInstance(unitId, mActivity);
                adInstance.setListener(new MaxRewardedAdListener() {
                    @Override
                    public void onAdLoaded(MaxAd ad) {
                        mRewardedAd = adInstance;
                        sendSimpleMessage(MSG_REWARDED, EVENT_LOADED,
                                "network", ad.getNetworkName());
                    }

                    @Override
                    public void onAdLoadFailed(String adUnitId, int errorCode) {
                        sendSimpleMessage(MSG_REWARDED, EVENT_FAILED_TO_LOAD,
                                "code", errorCode, "error", getErrorMessage(adUnitId, errorCode));
                    }

                    @Override
                    public void onAdDisplayed(MaxAd ad) {
                        if (mRewardedAd == adInstance) {
                            mRewardedAd = null;
                        }

                        sendSimpleMessage(MSG_REWARDED, EVENT_OPENING,
                                "network", ad.getNetworkName());
                    }

                    @Override
                    public void onAdDisplayFailed(MaxAd ad, int errorCode) {
                        if (mRewardedAd == adInstance) {
                            mRewardedAd = null;
                        }

                        sendSimpleMessage(MSG_REWARDED, EVENT_FAILED_TO_SHOW,
                                "code", errorCode, "error", getErrorMessage(ad, errorCode));
                    }

                    @Override
                    public void onAdHidden(MaxAd ad) {
                        sendSimpleMessage(MSG_REWARDED, EVENT_CLOSED,
                                "network", ad.getNetworkName());
                    }

                    @Override
                    public void onAdClicked(MaxAd ad) {
                        sendSimpleMessage(MSG_REWARDED, EVENT_CLICKED,
                                "network", ad.getNetworkName());
                    }

                    @Override
                    public void onRewardedVideoStarted(MaxAd ad) {
                        // Log.d(TAG, "onRewardedVideoStarted");
                    }

                    @Override
                    public void onRewardedVideoCompleted(MaxAd ad) {
                        // Log.d(TAG, "onRewardedVideoCompleted");
                    }

                    @Override
                    public void onUserRewarded(MaxAd ad, MaxReward reward) {
                        int rewardAmount = reward.getAmount();
                        String rewardType = reward.getLabel();
                        sendSimpleMessage(MSG_REWARDED, EVENT_EARNED_REWARD,
                                "amount", rewardAmount, "type", rewardType);
                    }
                });

                adInstance.loadAd();
            }
        });
    }

    public void showRewarded(final String placement) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isRewardedLoaded()) {
                    mRewardedAd.showAd(placement);
                } else {
                    // Log.d(TAG, "The rewarded ad wasn't ready yet.");
                    sendSimpleMessage(MSG_REWARDED, EVENT_NOT_LOADED,
                            "error", "Can't show Rewarded AD that wasn't loaded.");
                }
            }
        });
    }

    public boolean isRewardedLoaded() {
        return mRewardedAd != null && mRewardedAd.isReady();
    }

//--------------------------------------------------
// Banner ADS

    private enum BannerState {
        /**
         * No loaded banner
         */
        NONE,

        /**
         * Banner is loaded but not visible
         */
        HIDDEN,

        /**
         * Banner is loaded and visible, auto-refresh enabled
         */
        SHOWN,

        /**
         * Needs to relayout banner and resume auto-refresh after app activated (focused)
         */
        PAUSED,
    }


    private BannerState mBannerState = BannerState.NONE;
    private RelativeLayout mBannerLayout;
    private MaxAd mLoadedBanner;
    private MaxAdView mBannerAdView;
    private int mBannerGravity = Gravity.NO_GRAVITY;

    public void loadBanner(final String unitId, final int bannerSize) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                destroyBannerUiThread();
                MaxAdFormat adFormat = getMaxAdFormat(bannerSize);
                mBannerAdView = new MaxAdView(unitId, adFormat, mActivity);

                final MaxAdView view = mBannerAdView;
                view.setBackgroundColor(Color.TRANSPARENT);
                view.setListener(new MaxAdViewAdListener() {
                    @Override
                    public void onAdExpanded(MaxAd ad) {
                        sendSimpleMessage(MSG_BANNER, EVENT_EXPANDED,
                                "network", ad.getNetworkName());
                    }

                    @Override
                    public void onAdCollapsed(MaxAd ad) {
                        sendSimpleMessage(MSG_BANNER, EVENT_COLLAPSED,
                                "network", ad.getNetworkName());
                    }

                    @Override
                    public void onAdLoaded(final MaxAd ad) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (view != mBannerAdView) {
                                    Log.d(TAG, "Prevent reporting onAdLoaded for obsolete BannerAd (loadBanner was called multiple times)");
                                    view.destroy();
                                    return;
                                }

                                // ad format can be changed while auto-refreshing banner
                                // needs to re-create layout each time
                                recreateBannerLayout(view, ad.getFormat());
                                mLoadedBanner = ad;
                                if (mBannerState == BannerState.SHOWN) {
                                    mBannerLayout.setVisibility(View.VISIBLE);
                                }

                                sendSimpleMessage(MSG_BANNER, EVENT_LOADED,
                                        "network", ad.getNetworkName());
                            }
                        });
                    }

                    @Override
                    public void onAdLoadFailed(String adUnitId, int errorCode) {
                        sendSimpleMessage(MSG_BANNER, EVENT_FAILED_TO_LOAD,
                                "code", errorCode, "error", getErrorMessage(adUnitId, errorCode));
                    }

                    @Override
                    public void onAdDisplayed(MaxAd ad) {
                        // DO NOT USE - THIS IS RESERVED FOR FULLSCREEN ADS ONLY AND WILL BE REMOVED IN A FUTURE SDK RELEASE
                    }

                    @Override
                    public void onAdHidden(MaxAd ad) {
                        // DO NOT USE - THIS IS RESERVED FOR FULLSCREEN ADS ONLY AND WILL BE REMOVED IN A FUTURE SDK RELEASE
                    }

                    @Override
                    public void onAdClicked(MaxAd ad) {
                        sendSimpleMessage(MSG_BANNER, EVENT_CLICKED,
                                "network", ad.getNetworkName());
                    }

                    @Override
                    public void onAdDisplayFailed(MaxAd ad, int errorCode) {
                        sendSimpleMessage(MSG_BANNER, EVENT_FAILED_TO_SHOW,
                                "code", errorCode, "error", getErrorMessage(ad, errorCode));
                    }
                });

                view.loadAd();
                view.stopAutoRefresh();
            }
        });
    }

    public void destroyBanner() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                destroyBannerUiThread();
            }
        });
    }

    public void showBanner(final int pos, final String placement) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isBannerLoaded()) {
                    mBannerGravity = getGravity(pos);
                    mBannerAdView.setPlacement(placement);
                    if (mBannerState != BannerState.PAUSED) // to prevent auto-refresh while app in background (will show onAppActivated)
                    {
                        showBannerUiThread();
                    }
                }
                else
                {
                    // Log.d(TAG, "The banner ad wasn't ready yet.");
                    sendSimpleMessage(MSG_REWARDED, EVENT_NOT_LOADED,
                            "error", "Can't show Banner AD that wasn't loaded.");
                }
            }
        });
    }

    public void hideBanner() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isBannerLoaded()) {
                    mBannerAdView.stopAutoRefresh();
                    mBannerLayout.setVisibility(View.GONE);
                    mBannerState = BannerState.HIDDEN;
                }
            }
        });
    }

    public boolean isBannerLoaded() {
        return mBannerAdView != null && mLoadedBanner != null;
    }

    public boolean isBannerShown() {
        return isBannerLoaded() && mBannerState == BannerState.SHOWN;
    }

    private void destroyBannerUiThread() {
        if (!isBannerLoaded()) {
            return;
        }

        mBannerAdView.stopAutoRefresh();
        mBannerAdView.destroy();
        removeBannerLayout();
        mBannerAdView = null;
        mLoadedBanner = null;
        mBannerState = BannerState.NONE;
        sendSimpleMessage(MSG_BANNER, EVENT_DESTROYED);
    }

    private void showBannerUiThread() {
        recreateBannerLayout(mBannerAdView, mLoadedBanner.getFormat());
        mBannerLayout.setVisibility(View.VISIBLE);
        mBannerAdView.startAutoRefresh();
        mBannerState = BannerState.SHOWN;
    }

    private void pauseBanner() {
        if (isBannerShown()) {
            mBannerAdView.stopAutoRefresh();
            mBannerState = BannerState.PAUSED;
        }
    }

    private void resumeBanner() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mBannerState == BannerState.PAUSED) {
                    showBannerUiThread();
                }
            }
        });
    }

    private int getGravity(final int bannerPosConst) {
        int bannerPos = Gravity.NO_GRAVITY; //POS_NONE
        switch (bannerPosConst) {
            case POS_TOP_LEFT:
                bannerPos = Gravity.TOP | Gravity.LEFT;
                break;
            case POS_TOP_CENTER:
                bannerPos = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                break;
            case POS_TOP_RIGHT:
                bannerPos = Gravity.TOP | Gravity.RIGHT;
                break;
            case POS_BOTTOM_LEFT:
                bannerPos = Gravity.BOTTOM | Gravity.LEFT;
                break;
            case POS_BOTTOM_CENTER:
                bannerPos = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                break;
            case POS_BOTTOM_RIGHT:
                bannerPos = Gravity.BOTTOM | Gravity.RIGHT;
                break;
            case POS_CENTER:
                bannerPos = Gravity.CENTER;
                break;
        }
        return bannerPos;
    }

    private MaxAdFormat getMaxAdFormat(final int bannerSizeConst) {
        switch (bannerSizeConst) {
            case SIZE_MREC:
                return MaxAdFormat.MREC;
            case SIZE_LEADER:
                return MaxAdFormat.LEADER;
            case SIZE_BANNER:
            default:
                return MaxAdFormat.BANNER;
        }
    }

    private void removeBannerLayout() {
        if (mBannerLayout != null) {
            mBannerLayout.removeAllViews();
            mActivity.getWindowManager().removeView(mBannerLayout);
            mBannerLayout = null;
        }
    }

    private void recreateBannerLayout(MaxAdView adView, MaxAdFormat adFormat) {
        removeBannerLayout();
        mBannerLayout = new RelativeLayout(mActivity);
        mBannerLayout.setVisibility(View.GONE);
        mBannerLayout.addView(adView, getAdLayoutParams(adFormat));
        mActivity.getWindowManager().addView(mBannerLayout, getWindowLayoutParams());
    }

    private WindowManager.LayoutParams getWindowLayoutParams() {
        WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
        windowParams.x = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParams.y = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        windowParams.gravity = mBannerGravity;
        return windowParams;
    }

    private RelativeLayout.LayoutParams getAdLayoutParams(MaxAdFormat adFormat) {
        // TODO how to determine is adaptive banner? see MaxAdFormat.getAdaptiveSize()
        // NOTE: Only AdMob / Google Ad Manager currently has support for adaptive banners and the maximum height is 15% the height of the screen.
        AppLovinSdkUtils.Size adSize = adFormat.getSize();
        int widthDp = adSize.getWidth();
        int heightDp = adSize.getHeight();
        int widthPx = AppLovinSdkUtils.dpToPx(mActivity, widthDp);
        int heightPx = AppLovinSdkUtils.dpToPx(mActivity, heightDp);
        int width = (adFormat == MaxAdFormat.MREC) ? widthPx : RelativeLayout.LayoutParams.MATCH_PARENT;
        RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(width, heightPx);
        adParams.setMargins(0, 0, 0, 0);
        Log.d(TAG, String.format("getAdLayoutParams format: %s size (%d x %d)dp (%d x %d)px",
                adFormat.getLabel(), widthDp, heightDp, widthPx, heightPx));

        return adParams;
    }

    private String getErrorMessage(final int errorCode) {
        switch (errorCode) {
            case -1:
                return "Unspecified error with one of the mediated network SDKs.";
            case 204:
                return "NO_FILL no ads are currently eligible for your device.";
            case -102:
                return "Ad request timed out (usually due to poor connectivity).";
            case -103:
                return "Device is not connected to the internet (e.g. airplane mode).";
            case -2051:
                return "Device is not connected to a VPN or the VPN connection is not working properly (Users in China Only).";
            case -5001:
                return "Ad failed to load due to various reasons (such as no networks being able to fill).";
            case -5201:
                return "Internal state error with the AppLovin MAX SDK.";
            case -5601:
                return "Provided Activity instance has been garbage collected while the AppLovin MAX SDK attempts to re-load an expired ad.";
            default:
                return "Unknown error";
        }
    }

    private String getErrorMessage(final String adUnitId, final int errorCode) {
        return String.format("%s\nAdUnitId:%s", getErrorMessage(errorCode), adUnitId);
    }

    private String getErrorMessage(final MaxAd ad, final int errorCode) {
        return String.format("%s\nFormat:%s AdUnitId:%s Network:%s",
                getErrorMessage(errorCode), ad.getFormat(), ad.getAdUnitId(), ad.getNetworkName());
    }
}
