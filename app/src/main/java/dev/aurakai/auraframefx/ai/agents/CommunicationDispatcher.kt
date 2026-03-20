package dev.aurakai.auraframefx.ai.agents

import java.util.concurrent.CopyOnWriteArrayList

/**
 * Global dispatcher for all system communications.
 * 
 * Part of the "Cascade connection" initiative.
 */
object CommunicationDispatcher {
    private val observers = CopyOnWriteArrayList<CommunicationObserver>()

    fun registerObserver(observer: CommunicationObserver) {
        observers.addIfAbsent(observer)
    }

    fun unregisterObserver(observer: CommunicationObserver) {
        observers.remove(observer)
    }

    fun dispatchEvent(event: CommunicationEvent) {
        observers.forEach { it.onCommunicationEvent(event) }
    }
}
