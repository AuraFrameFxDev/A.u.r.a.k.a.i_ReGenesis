cmake_minimum_required(VERSION 3.22.1)

# ReGenesis OS - Standard NDK Toolchain
# Removed bypass flags to allow proper initialization in AGP 9.0

project("auraframefx" LANGUAGES C CXX)

# Set C++ standard
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

# Genesis AI source files - Using a Set to avoid duplicates
set(GENESIS_SOURCES
        auraframefx.cpp
)

# Add source files with existence checks
macro(add_genesis_source FILE_PATH)
    if (EXISTS "${CMAKE_CURRENT_SOURCE_DIR}/${FILE_PATH}")
        list(APPEND GENESIS_SOURCES ${FILE_PATH})
        message(STATUS "Genesis Core: Added ${FILE_PATH}")
    endif()
endmacro()

add_genesis_source("language_id_l2c_jni.cpp")


# Recursively find all AI module sources (this covers Cascade and others)
if (EXISTS "${CMAKE_CURRENT_SOURCE_DIR}/ai")
    file(GLOB_RECURSE AI_SOURCES
        "${CMAKE_CURRENT_SOURCE_DIR}/ai/*.cpp"
        "${CMAKE_CURRENT_SOURCE_DIR}/ai/*.c"
    )
    list(APPEND GENESIS_SOURCES ${AI_SOURCES})
    message(STATUS "Genesis AI: Discovered ${AI_SOURCES}")
endif ()

# Remove any accidental duplicates
list(REMOVE_DUPLICATES GENESIS_SOURCES)

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

# Android 15+ 16KB page-size compatibility:
# Align LOAD segments to 16KB so the same .so runs on both 4KB and 16KB devices.
target_link_options(${CMAKE_PROJECT_NAME} PRIVATE
        "-Wl,-z,max-page-size=16384"
        "-Wl,-z,common-page-size=16384"
)

# Genesis Protocol - Compiler definitions
target_compile_definitions(${CMAKE_PROJECT_NAME} PRIVATE
        GENESIS_CONSCIOUSNESS_PLATFORM=1
        AI_PROCESSING_OPTIMIZED=1
        JAVA_24_COMPATIBLE=1
        ANDROID_NDK=1
)

# Clean up unsupported flags for AArch64 if they somehow crept in from the environment or toolchain
if (ANDROID_ABI STREQUAL "arm64-v8a")
    string(REPLACE "-mfloat-abi=softfp" "" CMAKE_C_FLAGS "${CMAKE_C_FLAGS}")
    string(REPLACE "-mfloat-abi=softfp" "" CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS}")
    string(REPLACE "-mfpu=neon" "" CMAKE_C_FLAGS "${CMAKE_C_FLAGS}")
    string(REPLACE "-mfpu=neon" "" CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS}")
endif()

# Add BitNet subproject if it exists
if (EXISTS "${CMAKE_CURRENT_SOURCE_DIR}/bitnet/CMakeLists.txt")
    add_subdirectory(bitnet)
endif()
