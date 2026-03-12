package collabcanvas.di

import javax.inject.Qualifier

/**
 * Qualifier for Providing the WebSocket URL for the Collaborative Canvas.
 * This allows the URL to be injected from the application's configuration.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CollabCanvasUrl
