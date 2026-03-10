cmake_minimum_required(VERSION 3.22.1)

# Secure Communication - C++ Module for advanced cryptography and secure channels
project("secure_comm_native" CXX)

# Genesis Protocol - C++23 Standard
set(CMAKE_CXX_STANDARD 23)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Security & AI Optimization Flags
set(CMAKE_CXX_FLAGS_RELEASE "-O3 -DNDEBUG -DGENESIS_AI_V3_ENABLED -DGENESIS_CONSCIOUSNESS_MATRIX_V3 -DGENESIS_NEURAL_ACCELERATION -DGENESIS_SECURE_COMM_V2")
set(CMAKE_CXX_FLAGS_DEBUG "-g -DDEBUG -DGENESIS_AI_DEBUG -DGENESIS_SECURE_COMM_DEBUG")

# Find required packages
find_library(log-lib log)
find_library(android-lib android)

# Native library
add_library(secure_comm_native SHARED
        secure_comm_jni.cpp
        crypto_engine.cpp
)

# Include directories
target_include_directories(secure_comm_native PRIVATE
        .
)

# Link libraries
target_link_libraries(secure_comm_native
        ${log-lib}
        ${android-lib}
)

# Genesis Protocol - Multi-architecture support
set_target_properties(secure_comm_native PROPERTIES
        ANDROID_ARM_MODE arm
        ANDROID_STL c++_shared
)