#include <jni.h>
/* Header for class com_defold_maxsdk_AppLovinMaxJNI */

#ifndef COM_DEFOLD_APPLOVIN_APPLOVINMAXJNI_H
#define COM_DEFOLD_APPLOVIN_APPLOVINMAXJNI_H
#ifdef __cplusplus
extern "C" {
#endif
	/*
	* Class:     com_defold_maxsdk_AppLovinMaxJNI
	* Method:    maxsdkAddToQueue_first_arg
	* Signature: (ILjava/lang/String;I)V
	*/
	JNIEXPORT void JNICALL Java_com_defold_maxsdk_AppLovinMaxJNI_maxsdkAddToQueue
		(JNIEnv *, jclass, jint, jstring);

#ifdef __cplusplus
}
#endif
#endif
