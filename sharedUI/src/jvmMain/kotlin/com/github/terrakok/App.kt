package com.github.terrakok

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.terrakok.theme.AppTheme
import kotlinx.coroutines.launch

@Composable
fun App() = AppTheme {
    var machOFile: MachOFile? by remember { mutableStateOf(null) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val fileService = remember { FileService() }
    val otoolService = remember { OtoolService(Otool()) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (machOFile == null) {
                FileDropScreen(
                    onFileDropped = { droppedPath ->
                        scope.launch {
                            if (fileService.isMachO(droppedPath)) {
                                try {
                                    machOFile = otoolService.load(droppedPath)
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("Failed to parse file: ${e.message}")
                                }
                            } else {
                                snackbarHostState.showSnackbar("File is not a Mach-O file")
                            }
                        }
                    }
                )
            } else {
                Box(Modifier.fillMaxSize()) {
                    MachOView(machOFile!!)
                    
                    // Simple back button to return to drop screen
                    Button(
                        onClick = { machOFile = null },
                        modifier = Modifier.padding(16.dp).align(Alignment.BottomEnd)
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

