cmake_minimum_required(VERSION 3.22.1)

# Placeholder CMake for BitNet scaffolding
project(bitnet)

# In a real integration, we would add the BitNet source files here
# file(GLOB SOURCES "src/*.cpp" "3rdparty/llama.cpp/ggml/src/*.cpp")
# add_library(bitnet SHARED ${SOURCES})

# For this PoC/Feasibility scaffold, we create a stub library
add_library(bitnet SHARED bitnet_bridge.cpp)

# Link against Android log
target_link_libraries(bitnet log android)
