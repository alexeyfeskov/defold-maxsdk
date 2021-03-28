package com.defold.applovin;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

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

import org.json.JSONObject;
import org.json.JSONException;

public class AppLovinMaxJNI {

    private static final String TAG = "AppLovinMaxJNI";

    public static native void applovinAddToQueue(int msg, String json);

    // CONSTANTS:
    // duplicate of enums from applovin_callback_private.h:
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

    // duplicate of enums from applovin_private.h:
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

    private Activity activity;

    public AppLovinMaxJNI(Activity activity) {
        this.activity = activity;
    }

    public void initialize() {
        AppLovinSdk.getInstance(activity).setMediationProvider(AppLovinMediationProvider.MAX);
        AppLovinSdk.getInstance(activity).initializeSdk(new AppLovinSdk.SdkInitializationListener()
        {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration config)
            {
                sendSimpleMessage(MSG_INITIALIZATION, EVENT_COMPLETE);
            }
        } );
    }

    public void setMuted(boolean muted) {
        AppLovinSdk.getInstance(activity).getSettings().setMuted(muted);
    }

    public void setVerboseLogging(boolean isVerboseLoggingEnabled) {
        AppLovinSdk.getInstance(activity).getSettings().setVerboseLogging(isVerboseLoggingEnabled);
    }

    public void setHasUserConsent(boolean hasUserConsent) {
        AppLovinPrivacySettings.setHasUserConsent(hasUserConsent, activity);
    }

    public void setIsAgeRestrictedUser(boolean isAgeRestrictedUser) {
        AppLovinPrivacySettings.setIsAgeRestrictedUser(isAgeRestrictedUser, activity);
    }

    public void setDoNotSell(boolean doNotSell) {
        AppLovinPrivacySettings.setDoNotSell(doNotSell, activity);
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
        applovinAddToQueue(msg, message);
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
        applovinAddToQueue(msg, message);
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
        applovinAddToQueue(msg, message);
    }

//--------------------------------------------------
// Interstitial ADS

    private  MaxInterstitialAd interstitialAd;

    public void loadInterstitial(final String unitId) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                interstitialAd = new MaxInterstitialAd(unitId, activity);
                interstitialAd.setListener(new MaxAdListener() {
                    @Override
                    public void onAdLoaded(MaxAd ad) {
                        if (ad == interstitialAd) {
                            Log.d(TAG, "Prevent reporting onAdLoaded for obsolete InterstitialAd (loadInterstitial was called multiple times)");
                            return;
                        }

                        sendSimpleMessage(MSG_INTERSTITIAL, EVENT_LOADED);
                    }

                    @Override
                    public void onAdLoadFailed(String adUnitId, int errorCode) {
                        sendSimpleMessage(MSG_INTERSTITIAL, EVENT_FAILED_TO_LOAD,
                                "code", errorCode, "adUnitId", adUnitId);
                    }

                    @Override
                    public void onAdDisplayed(MaxAd ad) {
                        interstitialAd = null;
                        sendSimpleMessage(MSG_INTERSTITIAL, EVENT_OPENING,
                                "adUnitId", ad.getAdUnitId());
                    }

                    @Override
                    public void onAdHidden(MaxAd ad) {
                        sendSimpleMessage(MSG_INTERSTITIAL, EVENT_CLOSED,
                                "adUnitId", ad.getAdUnitId());
                    }

                    @Override
                    public void onAdClicked(MaxAd ad) {
                        sendSimpleMessage(MSG_INTERSTITIAL, EVENT_CLICKED,
                                "adUnitId", ad.getAdUnitId());
                    }

                    @Override
                    public void onAdDisplayFailed(MaxAd ad, int errorCode) {
                        sendSimpleMessage(MSG_INTERSTITIAL, EVENT_FAILED_TO_SHOW,
                                "code", errorCode, "adUnitId", ad.getAdUnitId());
                    }
                });

                interstitialAd.loadAd();
            }
        });
    }

    public void showInterstitial() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isInterstitialLoaded()) {
                    interstitialAd.showAd();
                } else {
                    // Log.d(TAG, "The interstitial ad wasn't ready yet.");
                    sendSimpleMessage(MSG_INTERSTITIAL, EVENT_NOT_LOADED,
                            "error", "Can't show Interstitial AD that wasn't loaded.");
                }
            }
        });
    }

    public boolean isInterstitialLoaded() {
        return interstitialAd != null && interstitialAd.isReady();
    }

//--------------------------------------------------
// Rewarded ADS

    private MaxRewardedAd rewardedAd;

    public void loadRewarded(final String unitId) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rewardedAd = MaxRewardedAd.getInstance(unitId, activity);
                rewardedAd.setListener(new MaxRewardedAdListener() {
                    @Override
                    public void onAdLoaded(MaxAd ad) {
                        if (ad == rewardedAd) {
                            Log.d(TAG, "Prevent reporting onAdLoaded for obsolete RewardedAd (loadRewarded was called multiple times)");
                            return;
                        }

                        sendSimpleMessage(MSG_REWARDED, EVENT_LOADED);
                    }

                    @Override
                    public void onAdLoadFailed(String adUnitId, int errorCode) {
                        sendSimpleMessage(MSG_REWARDED, EVENT_FAILED_TO_LOAD,
                                "code", errorCode, "adUnitId", adUnitId);
                    }

                    @Override
                    public void onAdDisplayed(MaxAd ad) {
                        rewardedAd = null;
                        sendSimpleMessage(MSG_REWARDED, EVENT_OPENING,
                                "adUnitId", ad.getAdUnitId());
                    }

                    @Override
                    public void onAdHidden(MaxAd ad) {
                        sendSimpleMessage(MSG_REWARDED, EVENT_CLOSED,
                                "adUnitId", ad.getAdUnitId());
                    }

                    @Override
                    public void onAdClicked(MaxAd ad) {
                        sendSimpleMessage(MSG_REWARDED, EVENT_CLICKED,
                                "adUnitId", ad.getAdUnitId());
                    }

                    @Override
                    public void onAdDisplayFailed(MaxAd ad, int errorCode) {
                        sendSimpleMessage(MSG_REWARDED, EVENT_FAILED_TO_SHOW,
                                "code", errorCode, "adUnitId", ad.getAdUnitId());
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

                rewardedAd.loadAd();
            }
        });
    }

    public void showRewarded() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isRewardedLoaded()) {
                    rewardedAd.showAd();
                } else {
                    // Log.d(TAG, "The rewarded ad wasn't ready yet.");
                    sendSimpleMessage(MSG_REWARDED, EVENT_NOT_LOADED,
                            "error", "Can't show Rewarded AD that wasn't loaded.");
                }
            }
        });
    }

    public boolean isRewardedLoaded() {
        return rewardedAd != null && rewardedAd.isReady();
    }

//--------------------------------------------------
// Banner ADS

    private LinearLayout layout;
    private MaxAd loadedBanner;
    private MaxAdView bannerAdView;
    private boolean isShown = false;
    private int bannerGravity = Gravity.NO_GRAVITY;

    public void loadBanner(final String unitId, final int bannerSize) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                destroyBannerUiThread();
                MaxAdFormat adFormat = getMaxAdFormat(bannerSize);
                bannerAdView = new MaxAdView(unitId, adFormat, activity);

                final MaxAdView view = bannerAdView;
                view.setBackgroundColor(Color.TRANSPARENT);
                view.setListener(new MaxAdViewAdListener() {
                    @Override
                    public void onAdExpanded(MaxAd ad) {
                        sendSimpleMessage(MSG_BANNER, EVENT_EXPANDED,
                                "adUnitId", ad.getAdUnitId());
                    }

                    @Override
                    public void onAdCollapsed(MaxAd ad) {
                        sendSimpleMessage(MSG_BANNER, EVENT_COLLAPSED,
                                "adUnitId", ad.getAdUnitId());
                    }

                    @Override
                    public void onAdLoaded(final MaxAd ad) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (view != bannerAdView) {
                                    Log.d(TAG, "Prevent reporting onAdLoaded for obsolete BannerAd (loadBanner was called multiple times)");
                                    return;
                                }

                                // ad format can be changed while auto-refreshing banner
                                // needs to re-create layout each time
                                recreateBannerLayout(view, ad.getFormat());
                                loadedBanner = ad;
                                if (!isShown) {
                                    layout.setVisibility(View.GONE);
                                }

                                sendSimpleMessage(MSG_BANNER, EVENT_LOADED,
                                        "adUnitId", ad.getAdUnitId());
                            }
                        });
                    }

                    @Override
                    public void onAdLoadFailed(String adUnitId, int errorCode) {
                        sendSimpleMessage(MSG_BANNER, EVENT_FAILED_TO_LOAD,
                                "code", errorCode, "adUnitId", adUnitId);
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
                                "adUnitId", ad.getAdUnitId());
                    }

                    @Override
                    public void onAdDisplayFailed(MaxAd ad, int errorCode) {
                        sendSimpleMessage(MSG_BANNER, EVENT_FAILED_TO_SHOW,
                                "code", errorCode, "adUnitId", ad.getAdUnitId());
                    }
                });

                view.loadAd();
            }
        });
    }

    public void destroyBanner() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                destroyBannerUiThread();
            }
        });
    }

    private void destroyBannerUiThread() {
        Log.d(TAG, "destroyBannerUiThreadw");
        if (!isBannerLoaded()) {
            return;
        }

        removeBannerLayout();
        bannerAdView.destroy();
        bannerAdView = null;
        loadedBanner = null;
        isShown = false;
        sendSimpleMessage(MSG_BANNER, EVENT_DESTROYED);
    }

    public void showBanner(final int pos) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isBannerLoaded()) {
                    return;
                }

                bannerGravity = getGravity(pos);
                recreateBannerLayout(bannerAdView, loadedBanner.getFormat());
                layout.setVisibility(View.VISIBLE);
                bannerAdView.startAutoRefresh();
                isShown = true;
            }
        });
    }

    public void hideBanner() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isBannerLoaded()) {
                    return;
                }

                Log.d(TAG, "hideBanner runOnUiThread setVisibility");
                layout.setVisibility(View.GONE);
                bannerAdView.stopAutoRefresh();
                isShown = false;
            }
        });
    }

    public boolean isBannerLoaded() {
        return bannerAdView != null && loadedBanner != null;
    }

    public boolean isBannerShown() {
        return isBannerLoaded() && isShown;
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
        if (layout != null) {
            layout.removeAllViews();
            activity.getWindowManager().removeView(layout);
            layout = null;
        }
    }

    private void recreateBannerLayout(MaxAdView adView, MaxAdFormat adFormat) {
        removeBannerLayout();
        layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setVisibility(View.GONE);
        layout.addView(adView, getAdLayoutParams(adFormat));
        activity.getWindowManager().addView(layout, getWindowLayoutParams());
    }

    private WindowManager.LayoutParams getWindowLayoutParams() {
        WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
        windowParams.x = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParams.y = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        windowParams.gravity = bannerGravity;
        return windowParams;
    }

    private LinearLayout.LayoutParams getAdLayoutParams(MaxAdFormat adFormat) {
        // TODO how to determine is adaptive banner? see MaxAdFormat.getAdaptiveSize()
        // NOTE: Only AdMob / Google Ad Manager currently has support for adaptive banners and the maximum height is 15% the height of the screen.
        AppLovinSdkUtils.Size adSize = adFormat.getSize();
        int widthDp = adSize.getWidth();
        int heightDp = adSize.getHeight();
        int widthPx = AppLovinSdkUtils.dpToPx(activity, widthDp);
        int heightPx = AppLovinSdkUtils.dpToPx(activity, heightDp);
        // int width = (adFormat == MaxAdFormat.MREC) ? widthPx : LinearLayout.LayoutParams.MATCH_PARENT;
        LinearLayout.LayoutParams adParams = new LinearLayout.LayoutParams(widthPx, heightPx);
        adParams.setMargins(0, 0, 0, 0);
        Log.d(TAG, String.format("getAdLayoutParams format: %s size (%d x %d)dp (%d x %d)px",
                adFormat.getLabel(), widthDp, heightDp, widthPx, heightPx));

        return adParams;
    }
}
