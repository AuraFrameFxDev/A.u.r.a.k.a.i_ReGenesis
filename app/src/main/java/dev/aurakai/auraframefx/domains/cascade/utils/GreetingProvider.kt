package dev.aurakai.auraframefx.domains.cascade.utils

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GreetingProvider @Inject constructor() {
    /**
     * Returns the application's greeting identifier.
     *
     * This function always returns the fixed greeting string "A.u.r.a.K.a.i".
     *
     * @return the greeting string.
     */
    fun getGreeting(): String = "A.u.r.a.K.a.i"
}


