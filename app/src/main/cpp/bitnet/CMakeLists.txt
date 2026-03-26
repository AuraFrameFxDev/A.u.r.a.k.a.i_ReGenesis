cmake_minimum_required(VERSION 3.22.1)

# ReGenesis OS - Subproject Toolchain Bypass
set(CMAKE_TRY_COMPILE_TARGET_TYPE "STATIC_LIBRARY")
set(CMAKE_C_COMPILER_WORKS 1)
set(CMAKE_CXX_COMPILER_WORKS 1)

# Initialize BitNet
project(bitnet LANGUAGES C CXX)

# SVE2 + I8MM Vectorization Flags for Snapdragon 8 Gen 3 (Cortex-X4 / A720)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3 -march=armv8.2-a+sve2+i8mm -mtune=cortex-a720 -ffast-math -D__ARM_FEATURE_SVE=1 -D__ARM_FEATURE_SVE2=1")
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -O3 -march=armv8.2-a+sve2+i8mm -mtune=cortex-a720 -ffast-math -D__ARM_FEATURE_SVE=1 -D__ARM_FEATURE_SVE2=1")

# Include directories
include_directories(.)
include_directories(src)

# For this PoC/Feasibility scaffold, we include the optimized kernels
add_library(bitnet SHARED
    bitnet_bridge.cpp
    ggml-bitnet.cpp
    src/ggml-bitnet.cpp
)

# Link against Android log
target_link_libraries(bitnet log android)

# Android 15+ 16KB page-size compatibility:
# Align LOAD segments to 16KB so the same .so runs on both 4KB and 16KB devices.
target_link_options(bitnet PRIVATE
    "-Wl,-z,max-page-size=16384"
    "-Wl,-z,common-page-size=16384"
)
