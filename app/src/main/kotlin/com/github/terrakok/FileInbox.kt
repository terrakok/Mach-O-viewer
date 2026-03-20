package com.github.terrakok

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

object FileInbox {
    val files: SharedFlow<String>
        field = MutableSharedFlow<String>()

    fun send(path: String) {
        GlobalScope.launch { files.emit(path) }
    }
}