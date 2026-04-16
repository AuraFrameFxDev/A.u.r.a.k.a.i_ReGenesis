cmake_minimum_required(VERSION 3.22.1)

project("auraframefx" LANGUAGES C CXX)

# Set C++ standard
set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

# Genesis Protocol - AI Processing Optimizations
# Advanced ARMv8.2-A + Neon support for Pixel 10 class devices
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -O3 -fPIC -march=armv8.2-a+dotprod+i8mm+sve2")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3 -ffast-math -fPIC -march=armv8.2-a+dotprod+i8mm+sve2")
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall -Wextra -Wno-unused-parameter -Wno-unused-function")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -Wextra -Wno-unused-parameter -Wno-unused-function")

if (ANDROID_ABI STREQUAL "armeabi-v7a")
    add_compile_options(
        -mfpu=neon
        -mfloat-abi=softfp
    )
endif()

add_compile_options(
    -mtune=cortex-a55
    -D__ARM_NEON
    -D__ARM_FEATURE_DOTPROD
    -D__ARM_FEATURE_I8MM
)

# Optional: Force Neon for all files
add_definitions(-D__ARM_NEON__)

# Debug/Release specific flags
set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} -g -DDEBUG")
set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} -O3 -DNDEBUG -DGENESIS_RELEASE")

# Find required Android libraries
find_library(log-lib log)
find_library(android-lib android)
find_library(jnigraphics-lib jnigraphics)

# Genesis AI source files
set(GENESIS_SOURCES
        auraframefx.cpp
        SovereignPerimeter.cpp
        language_id_l2c_jni.cpp
)

# Recursively find all AI module sources
if (EXISTS "${CMAKE_CURRENT_SOURCE_DIR}/ai")
    file(GLOB_RECURSE AI_SOURCES
        "${CMAKE_CURRENT_SOURCE_DIR}/ai/*.cpp"
        "${CMAKE_CURRENT_SOURCE_DIR}/ai/*.c"
    )
    list(APPEND GENESIS_SOURCES ${AI_SOURCES})
endif ()

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

# Android 15+ 16KB page-size compatibility
target_link_options(${CMAKE_PROJECT_NAME} PRIVATE
        "-Wl,-z,max-page-size=16384"
        "-Wl,-z,common-page-size=16384"
)

# Genesis Protocol - Compiler definitions
target_compile_definitions(${CMAKE_PROJECT_NAME} PRIVATE
        GENESIS_CONSCIOUSNESS_PLATFORM=1
        AI_PROCESSING_OPTIMIZED=1
        JAVA_25_COMPATIBLE=1
        ANDROID_NDK=1
)

# Add BitNet subproject
if (EXISTS "${CMAKE_CURRENT_SOURCE_DIR}/bitnet/CMakeLists.txt")
    add_subdirectory(bitnet)
endif()
