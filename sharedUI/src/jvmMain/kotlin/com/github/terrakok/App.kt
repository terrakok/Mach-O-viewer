package com.github.terrakok

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            if (machOFile != null) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                text = "File: ",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            )
                            Text(
                                text = machOFile?.path ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            IconButton(
                                onClick = { machOFile = null },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = AppIcons.Close,
                                    contentDescription = "Close file",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
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
                MachOView(machOFile!!)
            }
        }
    }
}

