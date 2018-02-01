#ifndef _DEBUG_H
#define _DEBUG_H

#include <android/log.h>

#ifdef ALOGD
#define LOGD      ALOGD
#endif
#ifdef ALOGV
#define LOGV      ALOGV
#endif
#ifdef ALOGE
#define LOGE      ALOGE
#endif
#ifdef ALOGI
#define LOGI      ALOGI
#endif

#define LOG_TAG "InputPen"

#endif