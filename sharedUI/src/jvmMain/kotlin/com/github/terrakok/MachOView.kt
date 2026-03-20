package com.github.terrakok

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MachOView(machOFile: MachOFile) {
    var selectedItem by remember { mutableStateOf<Any?>(null) }

    Row(modifier = Modifier.fillMaxSize()) {
        // Sidebar
        Column(
            modifier = Modifier
                .width(300.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Tree(machOFile, selectedItem) { selectedItem = it }
        }

        VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // Main content
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background)
        ) {
            TableHeader()
            TableContent(selectedItem)
        }
    }
}

@Composable
fun Tree(
    machOFile: MachOFile,
    selectedItem: Any?,
    onItemSelected: (Any) -> Unit
) {
    var loadCommandsExpanded by remember { mutableStateOf(true) }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            val rootName = "Executable (${machOFile.header?.cputype ?: "unknown"})"
            TreeItem(
                text = rootName,
                isSelected = false,
                onClick = {}
            )
        }
        item {
            machOFile.header?.let { header ->
                TreeItem(
                    text = "Mach Header",
                    isSelected = selectedItem == header,
                    indent = 16.dp,
                    onClick = { onItemSelected(header) }
                )
            }
        }
        item {
            TreeItem(
                text = "Load Commands",
                isSelected = false,
                indent = 16.dp,
                isExpandable = true,
                isExpanded = loadCommandsExpanded,
                onExpandClick = { loadCommandsExpanded = !loadCommandsExpanded },
                onClick = { loadCommandsExpanded = !loadCommandsExpanded }
            )
        }
        if (loadCommandsExpanded) {
            machOFile.loadCommands.forEach { command ->
                item {
                    var expanded by remember { mutableStateOf(false) }
                    val hasSections = command is SegmentCommand && command.sections.isNotEmpty()

                    TreeItem(
                        text = command.displayName,
                        isSelected = selectedItem == command,
                        indent = 32.dp,
                        isExpandable = hasSections,
                        isExpanded = expanded,
                        onExpandClick = { expanded = !expanded },
                        onClick = {
                            onItemSelected(command)
                        }
                    )

                    if (expanded && command is SegmentCommand) {
                        command.sections.forEach { section ->
                            TreeItem(
                                text = "    ${section.sectname}",
                                isSelected = selectedItem == section,
                                indent = 48.dp,
                                onClick = { onItemSelected(section) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TreeItem(
    text: String,
    isSelected: Boolean,
    indent: androidx.compose.ui.unit.Dp = 0.dp,
    isExpandable: Boolean = false,
    isExpanded: Boolean = false,
    onExpandClick: () -> Unit = {},
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
    val textColor = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(start = indent, top = 4.dp, bottom = 4.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isExpandable) {
            Text(
                text = if (isExpanded) "▼" else "▶",
                color = textColor,
                fontSize = 10.sp,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onExpandClick() },
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        } else {
            Spacer(modifier = Modifier.size(20.dp))
        }
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1
        )
    }
}

@Composable
fun TableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 4.dp)
    ) {
        TableCell("Address", Modifier.width(100.dp), fontWeight = FontWeight.Bold)
        TableCell("Data", Modifier.width(150.dp), fontWeight = FontWeight.Bold)
        TableCell("Description", Modifier.width(250.dp), fontWeight = FontWeight.Bold)
        TableCell("Value", Modifier.weight(1f), fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TableContent(selectedItem: Any?) {
    val data = getTableData(selectedItem)
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(data) { index, row ->
            val backgroundColor = if (index % 2 == 0) Color.Transparent else MaterialTheme.colorScheme.surfaceContainerLow
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .padding(vertical = 2.dp)
            ) {
                TableCell(row.address, Modifier.width(100.dp))
                TableCell(row.data, Modifier.width(150.dp))
                TableCell(row.description, Modifier.width(250.dp))
                TableCell(row.value, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun RowScope.TableCell(
    text: String,
    modifier: Modifier,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Text(
        text = text,
        modifier = modifier.padding(horizontal = 8.dp),
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.bodySmall.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = fontWeight
        ),
        maxLines = 1
    )
}

data class RowData(
    val description: String,
    val value: String,
    val address: String = "",
    val data: String = ""
)

fun getTableData(item: Any?): List<RowData> {
    return when (item) {
        is MachHeader -> listOf(
            RowData("Magic", item.magic),
            RowData("CPU Type", item.cputype),
            RowData("CPU Subtype", item.cpusubtype),
            RowData("File Type", item.filetype),
            RowData("Number of Commands", item.ncmds.toString()),
            RowData("Size of Commands", item.sizeofcmds.toString()),
            RowData("Flags", item.flags)
        )
        is Section -> listOf(
            RowData("Section Name", item.sectname),
            RowData("Segment Name", item.segname),
            RowData("Address", item.addr),
            RowData("Size", item.size),
            RowData("Offset", item.offset.toString()),
            RowData("Alignment", item.align),
            RowData("Relocation Offset", item.reloff.toString()),
            RowData("Number of Relocations", item.nreloc.toString()),
            RowData("Type", item.type),
            RowData("Attributes", item.attributes),
            RowData("Reserved1", item.reserved1),
            RowData("Reserved2", item.reserved2)
        )
        is SegmentCommand -> listOf(
            RowData("Command", item.cmd),
            RowData("Command Size", item.cmdSize.toString()),
            RowData("Segment Name", item.segname),
            RowData("VM Address", item.vmaddr),
            RowData("VM Size", item.vmsize),
            RowData("File Offset", item.fileoff.toString()),
            RowData("File Size", item.filesize.toString()),
            RowData("Maximum VM Protection", item.maxprot),
            RowData("Initial VM Protection", item.initprot),
            RowData("Number Of Sections", item.nsects.toString()),
            RowData("Flags", item.flags)
        )
        is DyldInfoCommand -> listOf(
            RowData("Command", item.cmd),
            RowData("Command Size", item.cmdSize.toString()),
            RowData("Rebase Offset", item.rebaseOff.toString()),
            RowData("Rebase Size", item.rebaseSize.toString()),
            RowData("Bind Offset", item.bindOff.toString()),
            RowData("Bind Size", item.bindSize.toString()),
            RowData("Weak Bind Offset", item.weakBindOff.toString()),
            RowData("Weak Bind Size", item.weakBindSize.toString()),
            RowData("Lazy Bind Offset", item.lazyBindOff.toString()),
            RowData("Lazy Bind Size", item.lazyBindSize.toString()),
            RowData("Export Offset", item.exportOff.toString()),
            RowData("Export Size", item.exportSize.toString())
        )
        is SymtabCommand -> listOf(
            RowData("Command", item.cmd),
            RowData("Command Size", item.cmdSize.toString()),
            RowData("Symbol Offset", item.symoff.toString()),
            RowData("Number of Symbols", item.nsyms.toString()),
            RowData("String Table Offset", item.stroff.toString()),
            RowData("String Table Size", item.strsize.toString())
        )
        is DysymtabCommand -> listOf(
            RowData("Command", item.cmd),
            RowData("Command Size", item.cmdSize.toString()),
            RowData("Index to local symbols", item.ilocalsym.toString()),
            RowData("Number of local symbols", item.nlocalsym.toString())
            // ... truncated for brevity, can add more if needed
        )
        is DylibCommand -> listOf(
            RowData("Command", item.cmd),
            RowData("Command Size", item.cmdSize.toString()),
            RowData("Name", item.name),
            RowData("Time Stamp", item.timestamp),
            RowData("Current Version", item.currentVersion),
            RowData("Compatibility Version", item.compatibilityVersion)
        )
        is UuidCommand -> listOf(
            RowData("Command", item.cmd),
            RowData("Command Size", item.cmdSize.toString()),
            RowData("UUID", item.uuid)
        )
        is BuildVersionCommand -> listOf(
            RowData("Command", item.cmd),
            RowData("Command Size", item.cmdSize.toString()),
            RowData("Platform", item.platform),
            RowData("Min OS", item.minos),
            RowData("SDK", item.sdk)
        )
        is MainCommand -> listOf(
            RowData("Command", item.cmd),
            RowData("Command Size", item.cmdSize.toString()),
            RowData("Entry Offset", item.entryoff.toString()),
            RowData("Stack Size", item.stacksize.toString())
        )
        is LinkeditDataCommand -> listOf(
            RowData("Command", item.cmd),
            RowData("Command Size", item.cmdSize.toString()),
            RowData("Data Offset", item.dataoff.toString()),
            RowData("Data Size", item.datasize.toString())
        )
        is UnknownLoadCommand -> listOf(
            RowData("Command", item.cmd),
            RowData("Command Size", item.cmdSize.toString())
        ) + item.lines.map { RowData("Info", it) }
        else -> emptyList()
    }
}

val LoadCommand.displayName: String
    get() = when (this) {
        is SegmentCommand -> "$cmd ($segname)"
        is DylibCommand -> "$cmd (${name.substringAfterLast("/")})"
        else -> cmd
    }
