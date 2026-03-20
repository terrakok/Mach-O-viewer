package com.github.terrakok

import androidx.compose.ui.draganddrop.DragAndDropEvent
import java.awt.FileDialog
import java.awt.Frame
import java.awt.datatransfer.DataFlavor
import java.io.File

fun pickFile(onFileSelected: (String?) -> Unit) {
    val fileDialog = FileDialog(null as Frame?, "Select a file", FileDialog.LOAD)
    fileDialog.isVisible = true
    if (fileDialog.file != null) {
        onFileSelected(fileDialog.file)
    } else {
        onFileSelected(null)
    }
}

@Suppress("UNCHECKED_CAST")
fun DragAndDropEvent.getFiles(): List<String> {
    try {
        val transferable = getTransferable(this)
        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            val files = transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<File>
            return files.map { it.path }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return emptyList()
}

private fun getTransferable(event: DragAndDropEvent): java.awt.datatransfer.Transferable? {
    // 1. Try "transferable" field directly on the event
    try {
        val field = findField(event::class.java, "transferable")
        if (field != null) {
            field.isAccessible = true
            return field.get(event) as? java.awt.datatransfer.Transferable
        }
    } catch (e: Exception) {
        // ignore
    }

    // 2. Try "nativeEvent" field
    try {
        val field = findField(event::class.java, "nativeEvent")
        if (field != null) {
            field.isAccessible = true
            val nativeEvent = field.get(event)
            if (nativeEvent is java.awt.dnd.DropTargetDropEvent) {
                return nativeEvent.transferable
            }
            if (nativeEvent is java.awt.dnd.DropTargetDragEvent) {
                return nativeEvent.transferable
            }
        }
    } catch (e: Exception) {
        // ignore
    }

    return null
}

private fun findField(clazz: Class<*>, name: String): java.lang.reflect.Field? {
    var current: Class<*>? = clazz
    while (current != null) {
        try {
            return current.getDeclaredField(name)
        } catch (e: NoSuchFieldException) {
            current = current.superclass
        }
    }
    return null
}
