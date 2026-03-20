package com.github.terrakok

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MachOView(machOFile: MachOFile) {
    var selectedBinary by remember { mutableStateOf(machOFile.binaries.first()) }
    var selectedItem by remember(selectedBinary) { mutableStateOf<Any>(selectedBinary.header) }
    var showBinaryPopup by remember { mutableStateOf(false) }

    DropdownMenu(
        expanded = showBinaryPopup,
        onDismissRequest = { showBinaryPopup = false },
        offset = DpOffset(100.dp, 80.dp),
    ) {
        machOFile.binaries.forEach { bin ->
            DropdownMenuItem(
                text = { Text(bin.architecture) },
                onClick = {
                    selectedBinary = bin
                    showBinaryPopup = false
                }
            )
        }
    }

    Row(modifier = Modifier.fillMaxSize()) {
        // Sidebar
        Box(
            modifier = Modifier
                .width(300.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Tree(
                machOFile = machOFile,
                binary = selectedBinary,
                selectedItem = selectedItem,
                onBinaryClick = {
                    showBinaryPopup = true
                }
            ) { selectedItem = it }
        }

        VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // Main content
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.fillMaxSize()) {
                    TableHeader()
                    TableContent(selectedItem, machOFile.content)
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Box(modifier = Modifier.weight(1f)) {
                HexViewer(machOFile.content)
            }
        }
    }
}

@Composable
fun HexViewer(content: ByteArray) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "File Hex Viewer",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(8.dp)
        )
        HorizontalDivider()
        SelectionContainer {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                val bytesPerLine = 16
                val lineCount = (content.size + bytesPerLine - 1) / bytesPerLine
                items(lineCount) { lineIndex ->
                    val start = lineIndex * bytesPerLine
                    val end = (start + bytesPerLine).coerceAtMost(content.size)

                    Row(modifier = Modifier.padding(horizontal = 8.dp)) {
                        // Address
                        Text(
                            text = "%08X".format(start),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.width(80.dp)
                        )

                        Spacer(modifier = Modifier.width(24.dp))

                        // Hex (Raw data)
                        val hexStringBuilder = StringBuilder()
                        for (i in start until end) {
                            hexStringBuilder.append("%02X ".format(content[i].toInt() and 0xFF))
                        }
                        Text(
                            text = hexStringBuilder.toString().padEnd(bytesPerLine * 3),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            modifier = Modifier.width(380.dp)
                        )
                        Spacer(modifier = Modifier.weight(1f))

                        // ASCII (Hex viewer representation)
                        val asciiStringBuilder = StringBuilder()
                        for (i in start until end) {
                            val c = content[i].toInt() and 0xFF
                            if (c in 32..126) {
                                asciiStringBuilder.append(c.toChar())
                            } else {
                                asciiStringBuilder.append(".")
                            }
                        }
                        Text(
                            text = asciiStringBuilder.toString(),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Tree(
    machOFile: MachOFile,
    binary: MachOBinary,
    selectedItem: Any?,
    onBinaryClick: () -> Unit,
    onItemSelected: (Any) -> Unit
) {
    var loadCommandsExpanded by remember { mutableStateOf(true) }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            val fewBinaries = machOFile.binaries.size > 1
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(enabled = fewBinaries) { onBinaryClick() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Executable (${binary.architecture})",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                if (fewBinaries) {
                    Icon(
                        imageVector = AppIcons.Edit,
                        contentDescription = "Select architecture",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        item {
            TreeItem(
                text = "Mach Header",
                isSelected = selectedItem == binary.header,
                indent = 16.dp,
                onClick = { onItemSelected(binary.header) }
            )
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
            binary.loadCommands.forEach { command ->
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
    indent: Dp = 0.dp,
    isExpandable: Boolean = false,
    isExpanded: Boolean = false,
    onExpandClick: () -> Unit = {},
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
    val textColor =
        if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(start = indent, top = 4.dp, bottom = 4.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isExpandable) {
            Icon(
                imageVector = if (isExpanded) AppIcons.RadixTriangleDown else AppIcons.RadixTriangleRight,
                contentDescription = "Expand/collapse",
                tint = textColor,
                modifier = Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(50))
                    .clickable { onExpandClick() }
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
fun TableContent(selectedItem: Any?, content: ByteArray) {
    val data = getTableData(selectedItem, content)
    SelectionContainer {
        LazyColumn {
            itemsIndexed(data) { index, row ->
                val backgroundColor =
                    if (index % 2 == 0) Color.Transparent else MaterialTheme.colorScheme.surfaceContainerLow
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundColor)
                        .padding(vertical = 2.dp),
                ) {
                    TableCell(row.address, Modifier.width(100.dp))
                    TableCell(row.data, Modifier.width(150.dp))
                    TableCell(row.description, Modifier.width(250.dp))
                    TableCell(row.value, Modifier.weight(1f))
                }
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
        maxLines = 1,
        overflow = TextOverflow.MiddleEllipsis
    )
}

data class RowData(
    val description: String,
    val value: String,
    val address: String = "",
    val data: String = ""
)

fun getTableData(item: Any?, content: ByteArray): List<RowData> {
    fun Long.toHexAddress() = "%08X".format(this)

    fun readHex(offset: Long, size: Int): String {
        return try {
            if (offset < 0 || offset + size > content.size) return ""
            val start = offset.toInt()
            val end = start + size
            val sb = StringBuilder()
            for (i in start until end) {
                sb.append("%02X".format(content[i]))
            }
            sb.toString()
        } catch (e: Exception) {
            ""
        }
    }

    return when (item) {
        is MachHeader -> {
            val offset = item.offset
            val is64Bit = item.magic.contains("64")
            val rows = mutableListOf(
                RowData("Magic", item.magic, offset.toHexAddress(), readHex(offset, 4)),
                RowData("CPU Type", item.cputype, (offset + 4).toHexAddress(), readHex(offset + 4, 4)),
                RowData("CPU Subtype", item.cpusubtype, (offset + 8).toHexAddress(), readHex(offset + 8, 4)),
                RowData("File Type", item.filetype, (offset + 12).toHexAddress(), readHex(offset + 12, 4)),
                RowData("Number of Commands", item.ncmds.toString(), (offset + 16).toHexAddress(), readHex(offset + 16, 4)),
                RowData("Size of Commands", item.sizeofcmds.toString(), (offset + 20).toHexAddress(), readHex(offset + 20, 4)),
                RowData("Flags", item.flags, (offset + 24).toHexAddress(), readHex(offset + 24, 4))
            )
            if (is64Bit) {
                rows.add(RowData("Reserved", item.reserved, (offset + 28).toHexAddress(), readHex(offset + 28, 4)))
            }
            rows
        }

        is Section -> {
            val offset = item.structureOffset
            val is64Bit = item.addr.removePrefix("0x").length > 8
            val pSize = if (is64Bit) 8 else 4

            val rows = mutableListOf(
                RowData("Section Name", item.sectname, offset.toHexAddress(), readHex(offset, 16)),
                RowData("Segment Name", item.segname, (offset + 16).toHexAddress(), readHex(offset + 16, 16))
            )
            var current = offset + 32
            rows.add(RowData("Address", item.addr, current.toHexAddress(), readHex(current, pSize)))
            current += pSize
            rows.add(RowData("Size", item.size, current.toHexAddress(), readHex(current, pSize)))
            current += pSize
            rows.add(RowData("Offset", item.offset.toString(), current.toHexAddress(), readHex(current, 4)))
            current += 4
            rows.add(RowData("Alignment", item.align, current.toHexAddress(), readHex(current, 4)))
            current += 4
            rows.add(RowData("Relocation Offset", item.reloff.toString(), current.toHexAddress(), readHex(current, 4)))
            current += 4
            rows.add(RowData("Number of Relocations", item.nreloc.toString(), current.toHexAddress(), readHex(current, 4)))
            current += 4
            rows.add(RowData("Flags (Type/Attributes)", "${item.type} / ${item.attributes}", current.toHexAddress(), readHex(current, 4)))
            current += 4
            rows.add(RowData("Reserved1", item.reserved1, current.toHexAddress(), readHex(current, 4)))
            current += 4
            rows.add(RowData("Reserved2", item.reserved2, current.toHexAddress(), readHex(current, 4)))
            current += 4
            if (is64Bit) {
                rows.add(RowData("Reserved3", item.reserved3, current.toHexAddress(), readHex(current, 4)))
            }
            rows
        }

        is SegmentCommand -> {
            val offset = item.offset
            val is64Bit = item.cmd == "LC_SEGMENT_64"
            val addrSize = if (is64Bit) 8 else 4

            val rows = mutableListOf(
                RowData("Command", item.cmd, offset.toHexAddress(), readHex(offset, 4)),
                RowData("Command Size", item.cmdSize.toString(), (offset + 4).toHexAddress(), readHex(offset + 4, 4)),
                RowData("Segment Name", item.segname, (offset + 8).toHexAddress(), readHex(offset + 8, 16))
            )

            var current = offset + 24
            rows.add(RowData("VM Address", item.vmaddr, current.toHexAddress(), readHex(current, addrSize)))
            current += addrSize
            rows.add(RowData("VM Size", item.vmsize, current.toHexAddress(), readHex(current, addrSize)))
            current += addrSize
            rows.add(RowData("File Offset", item.fileoff.toString(), current.toHexAddress(), readHex(current, addrSize)))
            current += addrSize
            rows.add(RowData("File Size", item.filesize.toString(), current.toHexAddress(), readHex(current, addrSize)))
            current += addrSize
            rows.add(RowData("Maximum VM Protection", item.maxprot, current.toHexAddress(), readHex(current, 4)))
            current += 4
            rows.add(RowData("Initial VM Protection", item.initprot, current.toHexAddress(), readHex(current, 4)))
            current += 4
            rows.add(RowData("Number Of Sections", item.nsects.toString(), current.toHexAddress(), readHex(current, 4)))
            current += 4
            rows.add(RowData("Flags", item.flags, current.toHexAddress(), readHex(current, 4)))

            rows
        }

        is DyldInfoCommand -> {
            val offset = item.offset
            listOf(
                RowData("Command", item.cmd, offset.toHexAddress(), readHex(offset, 4)),
                RowData("Command Size", item.cmdSize.toString(), (offset + 4).toHexAddress(), readHex(offset + 4, 4)),
                RowData("Rebase Offset", item.rebaseOff.toString(), (offset + 8).toHexAddress(), readHex(offset + 8, 4)),
                RowData("Rebase Size", item.rebaseSize.toString(), (offset + 12).toHexAddress(), readHex(offset + 12, 4)),
                RowData("Bind Offset", item.bindOff.toString(), (offset + 16).toHexAddress(), readHex(offset + 16, 4)),
                RowData("Bind Size", item.bindSize.toString(), (offset + 20).toHexAddress(), readHex(offset + 20, 4)),
                RowData("Weak Bind Offset", item.weakBindOff.toString(), (offset + 24).toHexAddress(), readHex(offset + 24, 4)),
                RowData("Weak Bind Size", item.weakBindSize.toString(), (offset + 28).toHexAddress(), readHex(offset + 28, 4)),
                RowData("Lazy Bind Offset", item.lazyBindOff.toString(), (offset + 32).toHexAddress(), readHex(offset + 32, 4)),
                RowData("Lazy Bind Size", item.lazyBindSize.toString(), (offset + 36).toHexAddress(), readHex(offset + 36, 4)),
                RowData("Export Offset", item.exportOff.toString(), (offset + 40).toHexAddress(), readHex(offset + 40, 4)),
                RowData("Export Size", item.exportSize.toString(), (offset + 44).toHexAddress(), readHex(offset + 44, 4))
            )
        }

        is SymtabCommand -> {
            val offset = item.offset
            listOf(
                RowData("Command", item.cmd, offset.toHexAddress(), readHex(offset, 4)),
                RowData("Command Size", item.cmdSize.toString(), (offset + 4).toHexAddress(), readHex(offset + 4, 4)),
                RowData("Symbol Offset", item.symoff.toString(), (offset + 8).toHexAddress(), readHex(offset + 8, 4)),
                RowData("Number of Symbols", item.nsyms.toString(), (offset + 12).toHexAddress(), readHex(offset + 12, 4)),
                RowData("String Table Offset", item.stroff.toString(), (offset + 16).toHexAddress(), readHex(offset + 16, 4)),
                RowData("String Table Size", item.strsize.toString(), (offset + 20).toHexAddress(), readHex(offset + 20, 4))
            )
        }

        is DysymtabCommand -> {
            val offset = item.offset
            listOf(
                RowData("Command", item.cmd, offset.toHexAddress(), readHex(offset, 4)),
                RowData("Command Size", item.cmdSize.toString(), (offset + 4).toHexAddress(), readHex(offset + 4, 4)),
                RowData("Index to local symbols", item.ilocalsym.toString(), (offset + 8).toHexAddress(), readHex(offset + 8, 4)),
                RowData("Number of local symbols", item.nlocalsym.toString(), (offset + 12).toHexAddress(), readHex(offset + 12, 4)),
                RowData("Index to externally defined symbols", item.iextdefsym.toString(), (offset + 16).toHexAddress(), readHex(offset + 16, 4)),
                RowData("Number of externally defined symbols", item.nextdefsym.toString(), (offset + 20).toHexAddress(), readHex(offset + 20, 4)),
                RowData("Index to undefined symbols", item.iundefsym.toString(), (offset + 24).toHexAddress(), readHex(offset + 24, 4)),
                RowData("Number of undefined symbols", item.nundefsym.toString(), (offset + 28).toHexAddress(), readHex(offset + 28, 4)),
                RowData("Table of contents offset", item.tocoff.toString(), (offset + 32).toHexAddress(), readHex(offset + 32, 4)),
                RowData("Number of entries in table of contents", item.ntoc.toString(), (offset + 36).toHexAddress(), readHex(offset + 36, 4)),
                RowData("Module table offset", item.modtaboff.toString(), (offset + 40).toHexAddress(), readHex(offset + 40, 4)),
                RowData("Number of entries in module table", item.nmodtab.toString(), (offset + 44).toHexAddress(), readHex(offset + 44, 4)),
                RowData("External reference symbol table offset", item.extrefsymoff.toString(), (offset + 48).toHexAddress(), readHex(offset + 48, 4)),
                RowData("Number of entries in external reference symbol table", item.nextrefsyms.toString(), (offset + 52).toHexAddress(), readHex(offset + 52, 4)),
                RowData("Indirect symbol table offset", item.indirectsymoff.toString(), (offset + 56).toHexAddress(), readHex(offset + 56, 4)),
                RowData("Number of entries in indirect symbol table", item.nindirectsyms.toString(), (offset + 60).toHexAddress(), readHex(offset + 60, 4)),
                RowData("External relocation entries offset", item.extreloff.toString(), (offset + 64).toHexAddress(), readHex(offset + 64, 4)),
                RowData("Number of external relocation entries", item.nextrel.toString(), (offset + 68).toHexAddress(), readHex(offset + 68, 4)),
                RowData("Local relocation entries offset", item.locreloff.toString(), (offset + 72).toHexAddress(), readHex(offset + 72, 4)),
                RowData("Number of local relocation entries", item.nlocrel.toString(), (offset + 76).toHexAddress(), readHex(offset + 76, 4))
            )
        }

        is DylibCommand -> {
            val offset = item.offset
            listOf(
                RowData("Command", item.cmd, offset.toHexAddress(), readHex(offset, 4)),
                RowData("Command Size", item.cmdSize.toString(), (offset + 4).toHexAddress(), readHex(offset + 4, 4)),
                RowData("Name", item.name, (offset + 24).toHexAddress(), ""),
                RowData("Time Stamp", item.timestamp, (offset + 8).toHexAddress(), readHex(offset + 8, 4)),
                RowData("Current Version", item.currentVersion, (offset + 12).toHexAddress(), readHex(offset + 12, 4)),
                RowData("Compatibility Version", item.compatibilityVersion, (offset + 16).toHexAddress(), readHex(offset + 16, 4))
            )
        }

        is UuidCommand -> {
            val offset = item.offset
            listOf(
                RowData("Command", item.cmd, offset.toHexAddress(), readHex(offset, 4)),
                RowData("Command Size", item.cmdSize.toString(), (offset + 4).toHexAddress(), readHex(offset + 4, 4)),
                RowData("UUID", item.uuid, (offset + 8).toHexAddress(), readHex(offset + 8, 16))
            )
        }

        is DylinkerCommand -> {
            val offset = item.offset
            listOf(
                RowData("Command", item.cmd, offset.toHexAddress(), readHex(offset, 4)),
                RowData("Command Size", item.cmdSize.toString(), (offset + 4).toHexAddress(), readHex(offset + 4, 4)),
                RowData("Name", item.name, (offset + 12).toHexAddress(), "")
            )
        }

        is BuildVersionCommand -> {
            val offset = item.offset
            val rows = mutableListOf(
                RowData("Command", item.cmd, offset.toHexAddress(), readHex(offset, 4)),
                RowData("Command Size", item.cmdSize.toString(), (offset + 4).toHexAddress(), readHex(offset + 4, 4)),
                RowData("Platform", item.platform, (offset + 8).toHexAddress(), readHex(offset + 8, 4)),
                RowData("Min OS", item.minos, (offset + 12).toHexAddress(), readHex(offset + 12, 4)),
                RowData("SDK", item.sdk, (offset + 16).toHexAddress(), readHex(offset + 16, 4)),
                RowData("Number of Tools", item.ntools.toString(), (offset + 20).toHexAddress(), readHex(offset + 20, 4))
            )
            item.tools.forEachIndexed { index, tool ->
                val toolOffset = offset + 24 + index * 8
                rows.add(RowData("Tool", tool.tool, toolOffset.toHexAddress(), readHex(toolOffset, 4)))
                rows.add(RowData("Version", tool.version, (toolOffset + 4).toHexAddress(), readHex(toolOffset + 4, 4)))
            }
            rows
        }

        is SourceVersionCommand -> {
            val offset = item.offset
            listOf(
                RowData("Command", item.cmd, offset.toHexAddress(), readHex(offset, 4)),
                RowData("Command Size", item.cmdSize.toString(), (offset + 4).toHexAddress(), readHex(offset + 4, 4)),
                RowData("Version", item.version, (offset + 8).toHexAddress(), readHex(offset + 8, 8))
            )
        }

        is MainCommand -> {
            val offset = item.offset
            listOf(
                RowData("Command", item.cmd, offset.toHexAddress(), readHex(offset, 4)),
                RowData("Command Size", item.cmdSize.toString(), (offset + 4).toHexAddress(), readHex(offset + 4, 4)),
                RowData("Entry Offset", item.entryoff.toString(), (offset + 8).toHexAddress(), readHex(offset + 8, 8)),
                RowData("Stack Size", item.stacksize.toString(), (offset + 16).toHexAddress(), readHex(offset + 16, 8))
            )
        }

        is LinkeditDataCommand -> {
            val offset = item.offset
            listOf(
                RowData("Command", item.cmd, offset.toHexAddress(), readHex(offset, 4)),
                RowData("Command Size", item.cmdSize.toString(), (offset + 4).toHexAddress(), readHex(offset + 4, 4)),
                RowData("Data Offset", item.dataoff.toString(), (offset + 8).toHexAddress(), readHex(offset + 8, 4)),
                RowData("Data Size", item.datasize.toString(), (offset + 12).toHexAddress(), readHex(offset + 12, 4))
            )
        }

        is UnknownLoadCommand -> {
            val offset = item.offset
            listOf(
                RowData("Command", item.cmd, offset.toHexAddress(), readHex(offset, 4)),
                RowData("Command Size", item.cmdSize.toString(), (offset + 4).toHexAddress(), readHex(offset + 4, 4))
            ) + item.lines.map { RowData("Info", it) }
        }

        else -> emptyList()
    }
}

val LoadCommand.displayName: String
    get() = when (this) {
        is SegmentCommand -> "$cmd ($segname)"
        is DylibCommand -> "$cmd (${name.substringAfterLast("/")})"
        is DylinkerCommand -> "$cmd (${name.substringAfterLast("/")})"
        else -> cmd
    }
