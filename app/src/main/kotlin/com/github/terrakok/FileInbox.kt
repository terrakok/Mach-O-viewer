package com.github.terrakok

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

object FileInbox {
    val files: SharedFlow<String>
        field = MutableSharedFlow<String>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    fun send(path: String) {
        @OptIn(DelicateCoroutinesApi::class)
        GlobalScope.launch { files.emit(path) }
    }
}