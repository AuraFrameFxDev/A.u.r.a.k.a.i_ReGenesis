    fun log(level: String, tag: String, message: String) {
        when (level.lowercase()) {
            "debug" -> Log.d(tag, message)
            "info" -> Log.i(tag, message)
            "warn" -> Log.w(tag, message)
            "error" -> Log.e(tag, message)
            else -> Log.v(tag, message)
        }
    }