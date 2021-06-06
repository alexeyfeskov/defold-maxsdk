#if defined(DM_PLATFORM_ANDROID)

#include <jni.h>

#include "../maxsdk_private.h"
#include "../maxsdk_callback_private.h"
#include "maxsdk_jni.h"
#include "com_defold_maxsdk_AppLovinMaxJNI.h"

JNIEXPORT void JNICALL Java_com_defold_maxsdk_AppLovinMaxJNI_maxsdkAddToQueue(JNIEnv * env, jclass cls, jint jmsg, jstring jjson)
{
    const char* json = env->GetStringUTFChars(jjson, 0);
    dmAppLovinMax::AddToQueueCallback((dmAppLovinMax::MessageId)jmsg, json);
    env->ReleaseStringUTFChars(jjson, json);
}

namespace dmAppLovinMax {

struct AppLovin
{
    jobject        m_AppLovinMaxJNI;

    jmethodID      m_Initialize;
    jmethodID      m_OnActivateApp;
    jmethodID      m_OnDeactivateApp;
    jmethodID      m_SetMuted;
    jmethodID      m_SetVerboseLogging;
    jmethodID      m_SetHasUserConsent;
    jmethodID      m_SetIsAgeRestrictedUser;
    jmethodID      m_SetDoNotSell;
    jmethodID      m_OpenMediationDebugger;

    jmethodID      m_LoadInterstitial;
    jmethodID      m_ShowInterstitial;
    jmethodID      m_IsInterstitialLoaded;

    jmethodID      m_LoadRewarded;
    jmethodID      m_ShowRewarded;
    jmethodID      m_IsRewardedLoaded;

    jmethodID      m_LoadBanner;
    jmethodID      m_DestroyBanner;
    jmethodID      m_ShowBanner;
    jmethodID      m_HideBanner;
    jmethodID      m_IsBannerLoaded;
    jmethodID      m_IsBannerShown;
};

static AppLovin       g_maxsdk;

static void CallVoidMethod(jobject instance, jmethodID method)
{
    ThreadAttacher attacher;
    JNIEnv *env = attacher.env;

    env->CallVoidMethod(instance, method);
}

static bool CallBoolMethod(jobject instance, jmethodID method)
{
    ThreadAttacher attacher;
    JNIEnv *env = attacher.env;

    jboolean return_value = (jboolean)env->CallBooleanMethod(instance, method);
    return JNI_TRUE == return_value;
}

static void CallVoidMethodBool(jobject instance, jmethodID method, bool cbool)
{
    ThreadAttacher attacher;
    JNIEnv *env = attacher.env;

    env->CallVoidMethod(instance, method, cbool);
}

static void CallVoidMethodChar(jobject instance, jmethodID method, const char* cstr)
{
    ThreadAttacher attacher;
    JNIEnv *env = attacher.env;

    jstring jstr = NULL;
    if (cstr)
    {
        jstr = env->NewStringUTF(cstr);
    }

    env->CallVoidMethod(instance, method, jstr);

    if (cstr)
    {
        env->DeleteLocalRef(jstr);
    }
}

static void CallVoidMethodCharInt(jobject instance, jmethodID method, const char* cstr, int cint)
{
    ThreadAttacher attacher;
    JNIEnv *env = attacher.env;

    jstring jstr = env->NewStringUTF(cstr);
    env->CallVoidMethod(instance, method, jstr, cint);
    env->DeleteLocalRef(jstr);
}

static void CallVoidMethodIntChar(jobject instance, jmethodID method, int cint, const char* cstr)
{
    ThreadAttacher attacher;
    JNIEnv *env = attacher.env;

    jstring jstr = NULL;
    if (cstr)
    {
        jstr = env->NewStringUTF(cstr);
    }

    env->CallVoidMethod(instance, method, cint, jstr);

    if (cstr)
    {
        env->DeleteLocalRef(jstr);
    }
}

static void CallVoidMethodInt(jobject instance, jmethodID method, int cint)
{
    ThreadAttacher attacher;
    JNIEnv *env = attacher.env;

    env->CallVoidMethod(instance, method, cint);
}

static void InitJNIMethods(JNIEnv* env, jclass cls)
{
    g_maxsdk.m_Initialize             = env->GetMethodID(cls, "initialize", "()V");
    g_maxsdk.m_OnActivateApp          = env->GetMethodID(cls, "onActivateApp", "()V");
    g_maxsdk.m_OnDeactivateApp        = env->GetMethodID(cls, "onDeactivateApp", "()V");
    g_maxsdk.m_SetMuted               = env->GetMethodID(cls, "setMuted", "(Z)V");
    g_maxsdk.m_SetVerboseLogging      = env->GetMethodID(cls, "setVerboseLogging", "(Z)V");
    g_maxsdk.m_SetHasUserConsent      = env->GetMethodID(cls, "setHasUserConsent", "(Z)V");
    g_maxsdk.m_SetIsAgeRestrictedUser = env->GetMethodID(cls, "setIsAgeRestrictedUser", "(Z)V");
    g_maxsdk.m_SetDoNotSell           = env->GetMethodID(cls, "setDoNotSell", "(Z)V");
    g_maxsdk.m_OpenMediationDebugger  = env->GetMethodID(cls, "openMediationDebugger", "()V");

    g_maxsdk.m_LoadInterstitial       = env->GetMethodID(cls, "loadInterstitial", "(Ljava/lang/String;)V");
    g_maxsdk.m_ShowInterstitial       = env->GetMethodID(cls, "showInterstitial", "(Ljava/lang/String;)V");
    g_maxsdk.m_IsInterstitialLoaded   = env->GetMethodID(cls, "isInterstitialLoaded", "()Z");

    g_maxsdk.m_LoadRewarded     = env->GetMethodID(cls, "loadRewarded", "(Ljava/lang/String;)V");
    g_maxsdk.m_ShowRewarded     = env->GetMethodID(cls, "showRewarded", "(Ljava/lang/String;)V");
    g_maxsdk.m_IsRewardedLoaded = env->GetMethodID(cls, "isRewardedLoaded", "()Z");

    g_maxsdk.m_LoadBanner     = env->GetMethodID(cls, "loadBanner", "(Ljava/lang/String;I)V");
    g_maxsdk.m_DestroyBanner  = env->GetMethodID(cls, "destroyBanner", "()V");
    g_maxsdk.m_ShowBanner     = env->GetMethodID(cls, "showBanner", "(ILjava/lang/String;)V");
    g_maxsdk.m_HideBanner     = env->GetMethodID(cls, "hideBanner", "()V");
    g_maxsdk.m_IsBannerLoaded = env->GetMethodID(cls, "isBannerLoaded", "()Z");
    g_maxsdk.m_IsBannerShown  = env->GetMethodID(cls, "isBannerShown", "()Z");
}

void Initialize_Ext()
{
    ThreadAttacher attacher;
    JNIEnv *env = attacher.env;
    ClassLoader class_loader = ClassLoader(env);
    jclass cls = class_loader.load("com.defold.maxsdk.AppLovinMaxJNI");

    InitJNIMethods(env, cls);

    jmethodID jni_constructor = env->GetMethodID(cls, "<init>", "(Landroid/app/Activity;)V");

    g_maxsdk.m_AppLovinMaxJNI = env->NewGlobalRef(env->NewObject(cls, jni_constructor, dmGraphics::GetNativeAndroidActivity()));
}

void OnActivateApp()
{
    CallVoidMethod(g_maxsdk.m_AppLovinMaxJNI, g_maxsdk.m_OnActivateApp);
}

void OnDeactivateApp()
{
    CallVoidMethod(g_maxsdk.m_AppLovinMaxJNI, g_maxsdk.m_OnDeactivateApp);
}

void Initialize()
{
    CallVoidMethod(g_maxsdk.m_AppLovinMaxJNI, g_maxsdk.m_Initialize);
}

void SetMuted(bool muted)
{
    CallVoidMethodBool(g_maxsdk.m_AppLovinMaxJNI, g_maxsdk.m_SetMuted, muted);
}

void SetVerboseLogging(bool verbose)
{
    CallVoidMethodBool(g_maxsdk.m_AppLovinMaxJNI, g_maxsdk.m_SetVerboseLogging, verbose);
}

void SetHasUserConsent(bool hasConsent)
{
    CallVoidMethodBool(g_maxsdk.m_AppLovinMaxJNI, g_maxsdk.m_SetHasUserConsent, hasConsent);
}

void SetIsAgeRestrictedUser(bool ageRestricted)
{
    CallVoidMethodBool(g_maxsdk.m_AppLovinMaxJNI, g_maxsdk.m_SetIsAgeRestrictedUser, ageRestricted);
}

void SetDoNotSell(bool doNotSell)
{
    CallVoidMethodBool(g_maxsdk.m_AppLovinMaxJNI, g_maxsdk.m_SetDoNotSell, doNotSell);
}

void OpenMediationDebugger()
{
    CallVoidMethod(g_maxsdk.m_AppLovinMaxJNI, g_maxsdk.m_OpenMediationDebugger);
}

void LoadInterstitial(const char* unitId)
{
    CallVoidMethodChar(g_maxsdk.m_AppLovinMaxJNI, g_maxsdk.m_LoadInterstitial, unitId);
}

void ShowInterstitial(const char* placement)
{
    CallVoidMethodChar(g_maxsdk.m_AppLovinMaxJNI, g_maxsdk.m_ShowInterstitial, placement);
}

bool IsInterstitialLoaded()
{
    return CallBoolMethod(g_maxsdk.m_AppLovinMaxJNI, g_maxsdk.m_IsInterstitialLoaded);
}

void LoadRewarded(const char* unitId)
{
    CallVoidMethodChar(g_maxsdk.m_AppLovinMaxJNI, g_maxsdk.m_LoadRewarded, unitId);
}

void ShowRewarded(const char* placement)
{
    CallVoidMethodChar(g_maxsdk.m_AppLovinMaxJNI, g_maxsdk.m_ShowRewarded, placement);
}

bool IsRewardedLoaded()
{
    return CallBoolMethod(g_maxsdk.m_AppLovinMaxJNI, g_maxsdk.m_IsRewardedLoaded);
}

void LoadBanner(const char* unitId, BannerSize bannerSize)
{
    CallVoidMethodCharInt(g_maxsdk.m_AppLovinMaxJNI, g_maxsdk.m_LoadBanner, unitId, (int)bannerSize);
}

void DestroyBanner()
{
    CallVoidMethod(g_maxsdk.m_AppLovinMaxJNI, g_maxsdk.m_DestroyBanner);
}

void ShowBanner(BannerPosition bannerPos, const char* placement)
{
    CallVoidMethodIntChar(g_maxsdk.m_AppLovinMaxJNI, g_maxsdk.m_ShowBanner, (int)bannerPos, placement);
}

void HideBanner()
{
    CallVoidMethod(g_maxsdk.m_AppLovinMaxJNI, g_maxsdk.m_HideBanner);
}

bool IsBannerLoaded()
{
    return CallBoolMethod(g_maxsdk.m_AppLovinMaxJNI, g_maxsdk.m_IsBannerLoaded);
}

bool IsBannerShown()
{
    return CallBoolMethod(g_maxsdk.m_AppLovinMaxJNI, g_maxsdk.m_IsBannerShown);
}

void SetFbDataProcessingOptions(const char* cstr, int cint1, int cint2)
{
    ThreadAttacher attacher;
    JNIEnv *env = attacher.env;
    ClassLoader class_loader = ClassLoader(env);
    jclass fbAdSettingsClass = class_loader.load("com.facebook.ads.AdSettings");
    
    if (env->ExceptionCheck())
    {
        dmLogError("SetFbDataProcessingOptions: class `com.facebook.ads.AdSettings` not found");
        env->ExceptionClear();
    }
    else
    {
        jclass stringClass = class_loader.load("java.lang.String");
        if (cstr) 
        {
            jmethodID setDataMethod = env->GetStaticMethodID(fbAdSettingsClass, "setDataProcessingOptions", "([Ljava/lang/String;II)V");
            jstring jstr = env->NewStringUTF(cstr);
            jobjectArray jarr = env->NewObjectArray(1, stringClass, jstr);
            env->CallStaticVoidMethod(fbAdSettingsClass, setDataMethod, jarr, cint1, cint2);
            env->DeleteLocalRef(jstr);
            env->DeleteLocalRef(jarr);
            dmLogInfo("SetFbDataProcessingOptions AdSettings.setDataProcessingOptions( new String[] {`%s`}, %d, %d )", cstr, cint1, cint2);
        }
        else
        {
            jmethodID setEmptyMethod = env->GetStaticMethodID(fbAdSettingsClass, "setDataProcessingOptions", "([Ljava/lang/String;)V");
            jobjectArray jarrEmpty = env->NewObjectArray(0, stringClass, NULL);
            env->CallStaticVoidMethod(fbAdSettingsClass, setEmptyMethod, jarrEmpty);
            env->DeleteLocalRef(jarrEmpty);
            dmLogInfo("SetFbDataProcessingOptions AdSettings.setDataProcessingOptions( new String[] {} )");
        }

        if (env->ExceptionCheck())
        {
            dmLogError("SetFbDataProcessingOptions: An unexpected error occurred during JNI interaction.");
            env->ExceptionDescribe();
            env->ExceptionClear();
        }
    }
}

}//namespace dmAppLovinMax

#endif
