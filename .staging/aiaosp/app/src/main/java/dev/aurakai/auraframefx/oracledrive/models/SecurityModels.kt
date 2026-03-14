    fun isValid(): Boolean = enabled && interval > 0
    val reason: String get() = if (!enabled) "Sync disabled" else if (interval <= 0) "Invalid interval" else "Valid"
}