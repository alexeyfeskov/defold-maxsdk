#include <jni.h>
/* Header for class com_defold_applovin_AppLovinMaxJNI */

#ifndef COM_DEFOLD_APPLOVIN_APPLOVINMAXJNI_H
#define COM_DEFOLD_APPLOVIN_APPLOVINMAXJNI_H
#ifdef __cplusplus
extern "C" {
#endif
	/*
	* Class:     com_defold_applovin_AppLovinMaxJNI
	* Method:    applovinAddToQueue_first_arg
	* Signature: (ILjava/lang/String;I)V
	*/
	JNIEXPORT void JNICALL Java_com_defold_applovin_AppLovinMaxJNI_applovinAddToQueue
		(JNIEnv *, jclass, jint, jstring);

#ifdef __cplusplus
}
#endif
#endif
