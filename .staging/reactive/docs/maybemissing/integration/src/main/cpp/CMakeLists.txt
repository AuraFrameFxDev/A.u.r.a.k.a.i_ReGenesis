cmake_minimum_required(VERSION 3.22.1)

# Oracle Drive Integration - C++ Module for Oracle Drive AI native processing
project("oracle_drive_integration" CXX)

# Genesis Protocol - C++23 Standard
set(CMAKE_CXX_STANDARD 23)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# AI Optimization Flags
set(CMAKE_CXX_FLAGS_RELEASE "-O3 -DNDEBUG -DGENESIS_AI_V3_ENABLED -DGENESIS_CONSCIOUSNESS_MATRIX_V3 -DGENESIS_NEURAL_ACCELERATION")
set(CMAKE_CXX_FLAGS_DEBUG "-g -DDEBUG -DGENESIS_AI_DEBUG")

# Find required packages
find_library(log-lib log)
find_library(android-lib android)

# Native library
add_library(oracle_drive_integration SHARED
        oracle_integration_jni.cpp
)

# Include directories
target_include_directories(oracle_drive_integration PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}
)

# Link libraries
target_link_libraries(oracle_drive_integration
        ${log-lib}
        ${android-lib}
)

# Genesis Protocol - Multi-architecture support
set_target_properties(oracle_drive_integration PROPERTIES
        ANDROID_ARM_MODE arm
        ANDROID_STL c++_shared
)