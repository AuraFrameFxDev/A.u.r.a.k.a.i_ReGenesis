cmake_minimum_required(VERSION 3.22.1)

# ReGenesis OS - Subproject Toolchain Bypass
set(CMAKE_TRY_COMPILE_TARGET_TYPE "STATIC_LIBRARY")
set(CMAKE_C_COMPILER_WORKS 1)
set(CMAKE_CXX_COMPILER_WORKS 1)

# Initialize BitNet without checking languages automatically
project(bitnet NONE)

# Manually enable languages
enable_language(C)
enable_language(CXX)

# For this PoC/Feasibility scaffold, we create a stub library
add_library(bitnet SHARED bitnet_bridge.cpp)

# Link against Android log
target_link_libraries(bitnet log android)
