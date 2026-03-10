# 🛠️ Root Management Module

**System and ROM modification utilities with root access**

## 📋 Overview

The `rootmanagement` module provides advanced system modification capabilities, ROM tools, and root-level operations for AuraKai. It enables deep system customization while maintaining safety and recoverability.

## ✨ Features

### 🔧 ROM Tools
- **System Modification**: Safe system file editing
- **Partition Management**: Backup/restore partitions
- **Boot Image Tools**: Kernel and ramdisk modification
- **Recovery Integration**: Recovery mode operations

### 🔐 Root Operations
- **Privilege Management**: Controlled root access
- **SELinux Control**: Security policy management
- **Init.d Support**: Custom boot scripts
- **Module Management**: Magisk/Xposed module handling

### 💾 Backup & Recovery
- **Full System Backup**: Complete device backup
- **Selective Restore**: Choose what to restore
- **Cloud Backup**: Encrypted cloud storage
- **Boot Loop Protection**: Safety mechanisms

### ⚙️ System Tweaks
- **Performance Tuning**: CPU/GPU optimization
- **Battery Optimization**: Power management
- **Memory Management**: RAM optimization
- **Thermal Control**: Temperature management

## 🔌 Dependencies

### Core (auto-provided)
- androidx-core-ktx (API), appcompat, timber
- Hilt DI, Coroutines, Compose
- Java 24 bytecode target

### UI Framework
- Compose BOM, UI, Material3
- Material Icons Extended, Tooling

### Compose Integration
- androidx-activity-compose
- androidx-navigation-compose
- androidx-hilt-navigation-compose
- androidx-lifecycle-viewmodel-compose
- androidx-lifecycle-runtime-compose

### Background Processing
- androidx-work-runtime-ktx

### Serialization
- kotlinx-serialization-json

### Root Operations
- libsu-core, libsu-io, libsu-service

### Xposed Framework
- api-82.jar (compile-only)
- yukihookapi-ksp

## 🔧 Plugins
Via `genesis.android.library`: Android library, Kotlin, KSP, Compose, Hilt

## 📱 Build Configuration
**Namespace**: `dev.aurakai.auraframefx.genesis.oracledrive.rootmanagement`
**Java Target**: Java 24 bytecode

## 🛡️ Safety Features
- Automatic backups before modifications
- Boot loop detection and recovery
- Rollback support for failed changes
- Safe mode integration

## 📄 License
Part of the AuraKai Reactive Intelligence System
