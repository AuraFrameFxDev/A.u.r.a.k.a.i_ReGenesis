# ===== GENESIS-OS ROM TOOLS CMAKE CONFIGURATION =====
# CMake configuration for bleeding-edge ROM modification tools

cmake_minimum_required(VERSION 3.22.1)

# ===== PROJECT SETUP =====
project(romtools VERSION 1.0.0 LANGUAGES CXX)

# ===== C++ STANDARD =====
set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

# ===== ANDROID SPECIFIC SETTINGS =====
if (ANDROID)
    set(ANDROID_STL c++_shared)

    # ROM Tools specific definitions
    add_definitions(-DROM_TOOLS_BUILD=1)
    add_definitions(-DLIVE_ROM_EDITING=1)

    # Debug/Release specific definitions
    if (CMAKE_BUILD_TYPE STREQUAL "Debug")
        add_definitions(-DDEBUG=1)
        add_definitions(-DROM_DEBUG_BUILD=1)
    else ()
        add_definitions(-DNDEBUG=1)
        add_definitions(-DROM_RELEASE_BUILD=1)
    endif ()
endif ()

# ===== COMPILER FLAGS =====
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++20 -fPIC -Wall -Wextra")

if (CMAKE_BUILD_TYPE STREQUAL "Debug")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -g -O0")
else ()
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3 -DNDEBUG")
endif ()

# ===== ANDROID LOG LIBRARY =====
find_library(log-lib log)

# ===== SOURCE FILES =====
set(ROMTOOLS_SOURCES
        romtools_native.cpp
)

# ===== CREATE NATIVE LIBRARY =====
add_library(romtools SHARED ${ROMTOOLS_SOURCES})

# ===== LINK LIBRARIES =====
target_link_libraries(romtools
        ${log-lib}
)

# ===== INCLUDE DIRECTORIES =====
target_include_directories(romtools PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}
)

# ===== EXPORT SYMBOLS =====
set_target_properties(romtools PROPERTIES
        LINK_FLAGS "-Wl,--exclude-libs,ALL"
)

# ===== INSTALLATION =====
install(TARGETS romtools
        LIBRARY DESTINATION lib
        ARCHIVE DESTINATION lib
)

# ===== GENESIS-OS ROM TOOLS READY =====
message(STATUS "🛠️ Genesis-OS ROM Tools CMake configuration complete!")
message(STATUS "🔧 Native ROM modification capabilities enabled")
message(STATUS "🚀 C++20 with maximum optimization for bleeding-edge performance")
