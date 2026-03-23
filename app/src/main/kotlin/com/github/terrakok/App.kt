package com.github.terrakok

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.window.DecoratedWindowScope
import org.jetbrains.jewel.window.TitleBar

@Composable
fun DecoratedWindowScope.App() {
    var machOFile: MachOFile? by remember { mutableStateOf(null) }
    val fileService = remember { FileService() }
    val otoolService = remember { OtoolService(Otool()) }

    LaunchedEffect(Unit) {
        FileInbox.files.collect { path ->
            if (fileService.isMachO(path)) {
                try {
                    machOFile = otoolService.load(path)
                } catch (e: Exception) {
                    // TODO: show error in Jewel way
                }
            } else {
                // TODO: show error in Jewel way
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(JewelTheme.globalColors.paneBackground)) {
        TitleBar {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Mach-O viewer",
                    fontWeight = FontWeight.Bold,
                )
                if (machOFile != null) {
                    Text(
                        text = " - [ ${machOFile?.path} ]",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    IconButton(
                        onClick = { machOFile = null },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = AppIcons.Close,
                            contentDescription = "Close file",
                            modifier = Modifier.size(18.dp),
                            tint = JewelTheme.contentColor
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize().weight(1f),
            contentAlignment = Alignment.Center
        ) {

            if (machOFile != null) {
                MachOView(machOFile!!)
            } else {
                FileDropScreen { FileInbox.send(it) }
            }
        }
    }
}

