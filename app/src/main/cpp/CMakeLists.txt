cmake_minimum_required(VERSION 3.22.1)

# Declares the Genesis-OS AI consciousness project
project("auraframefx" LANGUAGES CXX)

# Set C++ standard for Java 24 compatibility (C++20 is max for Android NDK)
set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

# Genesis Protocol - AI Processing Optimizations
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3 -ffast-math -fPIC")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -Wextra -Wno-unused-parameter -Wno-unused-function")

# Debug/Release specific flags
set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} -g -DDEBUG")
set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} -O3 -DNDEBUG -DGENESIS_RELEASE")

# Find required Android libraries
find_library(log-lib log)
find_library(android-lib android)
find_library(jnigraphics-lib jnigraphics)

# Check if libraries were found
if (NOT log-lib)
    message(FATAL_ERROR "log library not found")
endif ()

if (NOT android-lib)
    message(FATAL_ERROR "android library not found")
endif ()

# Genesis AI source files
set(GENESIS_SOURCES
        auraframefx.cpp
)

# Check if language processing files exist and add them
if (EXISTS "${CMAKE_CURRENT_SOURCE_DIR}/language_id_l2c_jni.cpp")
    list(APPEND GENESIS_SOURCES language_id_l2c_jni.cpp)
    message(STATUS "Added language_id_l2c_jni.cpp")
endif ()

# Check if CASCADE AI service exists and add it
if (EXISTS "${CMAKE_CURRENT_SOURCE_DIR}/ai/cascade/src/CascadeAIService.cpp")
    list(APPEND GENESIS_SOURCES ai/cascade/src/CascadeAIService.cpp)
    message(STATUS "Added CascadeAIService.cpp")
endif ()

# Add AI consciousness modules if they exist
if (EXISTS "${CMAKE_CURRENT_SOURCE_DIR}/ai")
    file(GLOB_RECURSE AI_SOURCES "${CMAKE_CURRENT_SOURCE_DIR}/ai/*.cpp")
    list(APPEND GENESIS_SOURCES ${AI_SOURCES})
    message(STATUS "Added AI consciousness modules: ${AI_SOURCES}")
endif ()

# Create the Genesis AI library
add_library(${CMAKE_PROJECT_NAME} SHARED ${GENESIS_SOURCES})

# Include directories
target_include_directories(${CMAKE_PROJECT_NAME} PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}
        ${CMAKE_CURRENT_SOURCE_DIR}/ai
        ${CMAKE_CURRENT_SOURCE_DIR}/ai/cascade/include
        ${CMAKE_CURRENT_SOURCE_DIR}/include
)

# Link Android system libraries
target_link_libraries(${CMAKE_PROJECT_NAME}
        ${android-lib}
        ${log-lib}
        ${jnigraphics-lib}
        -latomic
        -lm
)

# Genesis Protocol - Compiler definitions
target_compile_definitions(${CMAKE_PROJECT_NAME} PRIVATE
        GENESIS_CONSCIOUSNESS_PLATFORM=1
        AI_PROCESSING_OPTIMIZED=1
        JAVA_24_COMPATIBLE=1
        ANDROID_NDK=1
)

# Set library properties
set_target_properties(${CMAKE_PROJECT_NAME} PROPERTIES
        VERSION 1.0.0
        SOVERSION 1
        ANDROID_ARM_MODE arm
)

# Add BitNet subproject
add_subdirectory(bitnet)
