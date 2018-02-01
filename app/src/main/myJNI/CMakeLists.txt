# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

LOCAL_SHARED_LIBRARIES := liblog libcutils libandroid_runtime libnativehelper
add_library( # Sets the name of the library.
             input_pen

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             com_jiagutech_jnitest_InputPen.cpp)
target_link_libraries( # Specifies the target library.
             input_pen

             # Links the target library to the log library
             # included in the NDK.
             ${log-lib} )