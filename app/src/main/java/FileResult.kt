//package dev.aurakai.auraframefx.oracledrive
//
//import kotlinx.coroutines.CoroutineDispatcher
//import kotlinx.coroutines.CoroutineScope
//
//sealed class FileResult(
//    val context: CoroutineDispatcher,
//    val block: suspend (CoroutineScope) -> Unit
//) {
//    companion object {
//        annotation class Success(val context: CoroutineDispatcher, val block: suspend (CoroutineScope) -> Unit)
//    }
//}
