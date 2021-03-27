#if defined(DM_PLATFORM_ANDROID)

#include <jni.h>

#include "../applovin_private.h"
#include "../applovin_callback_private.h"
#include "applovin_jni.h"
#include "com_defold_applovin_AppLovinMaxJNI.h"

JNIEXPORT void JNICALL Java_com_defold_applovin_AppLovinMaxJNI_applovinAddToQueue(JNIEnv * env, jclass cls, jint jmsg, jstring jjson)
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
    jmethodID      m_LoadInterstitial;
    jmethodID      m_ShowInterstitial;
    jmethodID      m_SetMuted;
    jmethodID      m_SetVerboseLogging;
    jmethodID      m_SetHasUserConsent;
    jmethodID      m_SetIsAgeRestrictedUser;
    jmethodID      m_SetDoNotSell;
    jmethodID      m_IsInterstitialLoaded;
};

static AppLovin       g_applovin;

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

    jstring jstr = env->NewStringUTF(cstr);
    env->CallVoidMethod(instance, method, jstr);
    env->DeleteLocalRef(jstr);
}

static void CallVoidMethodCharInt(jobject instance, jmethodID method, const char* cstr, int cint)
{
    ThreadAttacher attacher;
    JNIEnv *env = attacher.env;

    jstring jstr = env->NewStringUTF(cstr);
    env->CallVoidMethod(instance, method, jstr, cint);
    env->DeleteLocalRef(jstr);
}

static void CallVoidMethodInt(jobject instance, jmethodID method, int cint)
{
    ThreadAttacher attacher;
    JNIEnv *env = attacher.env;

    env->CallVoidMethod(instance, method, cint);
}

static void InitJNIMethods(JNIEnv* env, jclass cls)
{
    g_applovin.m_Initialize = env->GetMethodID(cls, "initialize", "()V");
    g_applovin.m_SetMuted               = env->GetMethodID(cls, "setMuted", "(Z)V");
    g_applovin.m_SetVerboseLogging      = env->GetMethodID(cls, "setVerboseLogging", "(Z)V");
    g_applovin.m_SetHasUserConsent      = env->GetMethodID(cls, "setHasUserConsent", "(Z)V");
    g_applovin.m_SetIsAgeRestrictedUser = env->GetMethodID(cls, "setIsAgeRestrictedUser", "(Z)V");
    g_applovin.m_SetDoNotSell           = env->GetMethodID(cls, "setDoNotSell", "(Z)V");
    g_applovin.m_LoadInterstitial = env->GetMethodID(cls, "loadInterstitial", "(Ljava/lang/String;)V");
    g_applovin.m_ShowInterstitial = env->GetMethodID(cls, "showInterstitial", "()V");
    g_applovin.m_IsInterstitialLoaded = env->GetMethodID(cls, "isInterstitialLoaded", "()Z");
}

void Initialize_Ext()
{
    ThreadAttacher attacher;
    JNIEnv *env = attacher.env;
    ClassLoader class_loader = ClassLoader(env);
    jclass cls = class_loader.load("com.defold.applovin.AppLovinMaxJNI");

    InitJNIMethods(env, cls);

    jmethodID jni_constructor = env->GetMethodID(cls, "<init>", "(Landroid/app/Activity;)V");

    g_applovin.m_AppLovinMaxJNI = env->NewGlobalRef(env->NewObject(cls, jni_constructor, dmGraphics::GetNativeAndroidActivity()));
}

void Initialize()
{
    CallVoidMethod(g_applovin.m_AppLovinMaxJNI, g_applovin.m_Initialize);
}

void SetMuted(bool muted)
{
    CallVoidMethodBool(g_applovin.m_AppLovinMaxJNI, g_applovin.m_SetMuted, muted);
}

void SetVerboseLogging(bool verbose)
{
    CallVoidMethodBool(g_applovin.m_AppLovinMaxJNI, g_applovin.m_SetVerboseLogging, verbose);
}

void SetHasUserConsent(bool hasConsent)
{
    CallVoidMethodBool(g_applovin.m_AppLovinMaxJNI, g_applovin.m_SetHasUserConsent, hasConsent);
}

void SetIsAgeRestrictedUser(bool ageRestricted)
{
    CallVoidMethodBool(g_applovin.m_AppLovinMaxJNI, g_applovin.m_SetIsAgeRestrictedUser, ageRestricted);
}

void SetDoNotSell(bool doNotSell)
{
    CallVoidMethodBool(g_applovin.m_AppLovinMaxJNI, g_applovin.m_SetDoNotSell, doNotSell);
}

void LoadInterstitial(const char* unitId)
{
    CallVoidMethodChar(g_applovin.m_AppLovinMaxJNI, g_applovin.m_LoadInterstitial, unitId);
}

void ShowInterstitial()
{
    CallVoidMethod(g_applovin.m_AppLovinMaxJNI, g_applovin.m_ShowInterstitial);
}

bool IsInterstitialLoaded()
{
    return CallBoolMethod(g_applovin.m_AppLovinMaxJNI, g_applovin.m_IsInterstitialLoaded);
}

}//namespace dmAppLovinMax

#endif
