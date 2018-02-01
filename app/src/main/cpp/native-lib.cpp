#include <string>
#include "input_pen.h"
#include "debug.h"
#include <jni.h>
#include <iostream>
#include <fcntl.h>
#include <assert.h>
#include <stdlib.h>
#include <malloc.h>
#include <JNIHelper.h>
#include <utils/Log.h>
#include <android_runtime/AndroidRuntime.h>
//Java类名

#define CLASS_NAME "com/jiagutech/jnitest/InputPen"

extern "C"
using namespace std;
using namespace Android;

static void checkAndClearExceptionFromCallback(JNIEnv* env, const char* methodName) {
    if (env->ExceptionCheck()) {
        LOGE("An exception was thrown by callback '%s'.", methodName);
        LOGE_EX(env);
//        cout<<"An exception was thrown by callback "<<methodName<<endl;
        env->ExceptionClear();
    }
}

//获得input_event数据的回调函数
static void GetEventCallback(__u16 type, __u16 code, __s32 value) {
    JNIEnv* env = android::AndroidRuntime::gAndroidRuntimeetJNIEnv();
//    invoke java callback method
    env->CallVoidMethod(mCallbacksObj, method_get_event, type, code, value);

    checkAndClearExceptionFromCallback(env, __FUNCTION__);
    cout<<"get"<<endl;
}

//创建线程的回调函数
static pthread_t CreateThreadCallback(const char* name, void (*start)(void *), void* arg) {
    return (pthread_t)AndroidRuntime::createJavaThread(name, start, arg);
//    return 0;
}

//释放线程资源的回调函数
static int DetachThreadCallback(void) {
    JavaVM* vm;
    jint result;

    vm = AndroidRuntime::GetJavaVM();
    if (vm == NULL) {
        LOGE("detach_thread_callback :getJavaVM failed\n");
//        cout<<"detach_thread_callback :getJavaVM failed"<<endl;
        return -1;
    }

    result = vm->DetachCurrentThread();
    if (result != JNI_OK)
        LOGE("ERROR: thread detach failed\n");
//        cout<<"ERROR: thread detach failed"<<endl;
    return result;
//    return 0;
}

//回调函数结构体变量
static input_callback mCallbacks = {
        GetEventCallback,
        CreateThreadCallback,
        DetachThreadCallback,
};
static jobject mCallbacksObj = NULL;

static jmethodID method_get_event;


//初始化Java的回调函数
static void jni_class_init_native
        (JNIEnv* env, jclass clazz) {
    LOGD("jni_class_init_native");
//    cout<<"jni_class_init_native"<<endl;
    method_get_event = env->GetMethodID(clazz, "getEvent", "(III)V");
}

//初始化
static jboolean jni_input_pen_init
        (JNIEnv *env, jobject obj) {
    LOGD("jni_input_pen_init");
//    cout<<"jni_input_pen_init"<<endl;
    if (!mCallbacksObj)
        mCallbacksObj = env->NewGlobalRef(obj);

//    return  true;
    return  input_pen_init(&mCallbacks);
}

//退出
static void jni_input_pen_exit
        (JNIEnv *env, jobject obj) {
    LOGD("jni_input_pen_exit");
//    cout<<"jni_input_pen_exit"<<endl;
//    a();
    input_pen_exit();
}

static const JNINativeMethod gMethods[] = {
        { "class_init_native","()V", (void *)jni_class_init_native },
        { "native_input_pen_init","()Z", (void *)jni_input_pen_init },
        { "native_input_pen_exit","()V", (void *)jni_input_pen_exit },
};

static int registerMethods(JNIEnv* env) {
    const char* const kClassName = CLASS_NAME;
    jclass clazz;
    /* look up the class */
    clazz = env->FindClass(kClassName);
    if (clazz == NULL) {
        LOGE("Can't find class %s/n", kClassName);
//        cout<<"Can't find class "<<kClassName<<endl;
        return -1;
    }
    /* register all the methods */
    if (env->RegisterNatives(clazz,gMethods,sizeof(gMethods)/sizeof(gMethods[0])) != JNI_OK) {
        LOGE("Failed registering methods for %s/n", kClassName);
//    cout<<"Failed registering methods for "<<kClassName<<endl;
        return -1;
    }
    /* fill out the rest of the ID cache */
    return 0;
}

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env = NULL;
    jint result = -1;
    LOGI("InputPen JNI_OnLoad");
//    cout<<"InputPen JNI_OnLoad"<<endl;
    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        LOGE("ERROR: GetEnv failed/n");
//        cout<<"ERROR: GetEnv failed\n";
        goto fail;
    }

    if (env == NULL) {
        goto fail;
    }
    if (registerMethods(env) != 0) {
        LOGE("ERROR: PlatformLibrary native registration failed/n");
//        cout<<"ERROR: PlatformLibrary native registration failed\n";
        goto fail;
    }
    /* success -- return valid version number */
    result = JNI_VERSION_1_4;
    fail:
    return result;
}

//JNI是Java调用C,所以是InpuPen调用cpp中的函数
//调用了C++，把inputPen中的函数用Java语言实现出来，并且在native-lib中调用