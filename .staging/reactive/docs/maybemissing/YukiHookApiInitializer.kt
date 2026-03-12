package dev.aurakai.auraframefx

class YukiHookApiInitializer : IYukiHookXposedInit {
    fun onInit() = dev.aurakai.auraframefx.xposed.GenesisHookEntry.onInit()
}

private fun Unit.onInit(): Any {
    TODO("Not yet implemented")
}

open annotation class IYukiHookXposedInit
