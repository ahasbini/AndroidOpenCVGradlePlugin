
cmake_minimum_required(VERSION 3.4.1 FATAL_ERROR)
include(ndk-stl-config.cmake)

find_package(OpenCV REQUIRED)
message(STATUS "OpenCV libraries: ${OpenCV_LIBS}")
message(STATUS ${OpenCV_VERSION})

#add_library( # Sets the name of the library.
#    sample-lib
#
#    # Sets the library as a shared library.
#    SHARED
#
#    # Provides a relative path to your source file(s).
#    src/android/main/jni/sample-lib.cpp
#    )
#
#find_library(log-lib log)
#find_library(android-lib android)
#target_link_libraries(sample-lib ${log-lib} ${android-lib} ${OpenCV_LIBS})