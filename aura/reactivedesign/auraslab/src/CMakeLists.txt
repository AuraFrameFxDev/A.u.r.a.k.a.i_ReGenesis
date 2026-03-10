# Minimum CMake for Android NDK
cmake_minimum_required(VERSION 3.10.2)
project("native-lib")

# Build a shared library from the native source
add_library(native-lib SHARED
        native-lib.cpp
)

# Include libraries needed for logging
find_library(log-lib log)

target_link_libraries(native-lib
        ${log-lib}
)
