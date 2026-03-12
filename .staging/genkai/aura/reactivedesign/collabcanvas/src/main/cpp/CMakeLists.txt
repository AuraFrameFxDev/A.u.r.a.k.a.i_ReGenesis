# Minimum required CMake version
cmake_minimum_required(VERSION 3.22.1)

# Project name and version
project(collab-canvas-native VERSION 1.0.0 LANGUAGES CXX)

# Set C++ standard
set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Add source files
add_library(
        collab-canvas-native
        SHARED
        collab_canvas_native.cpp
)

# Include directories
target_include_directories(
        collab-canvas-native
        PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}
)

# Link libraries
target_link_libraries(
        collab-canvas-native
        # Add any required Android or third-party libraries here
        # Example: android log library
        log
)

# Set output directory
set_target_properties(
        collab-canvas-native
        PROPERTIES
        LIBRARY_OUTPUT_DIRECTORY ${CMAKE_LIBRARY_OUTPUT_DIRECTORY}
        RUNTIME_OUTPUT_DIRECTORY ${CMAKE_RUNTIME_OUTPUT_DIRECTORY}
)

# Ensure output directory exists for all ABIs
if (DEFINED CMAKE_LIBRARY_OUTPUT_DIRECTORY)
    file(MAKE_DIRECTORY ${CMAKE_LIBRARY_OUTPUT_DIRECTORY})
endif ()
if (DEFINED CMAKE_RUNTIME_OUTPUT_DIRECTORY)
    file(MAKE_DIRECTORY ${CMAKE_RUNTIME_OUTPUT_DIRECTORY})
endif ()
