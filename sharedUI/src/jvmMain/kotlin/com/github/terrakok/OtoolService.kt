package com.github.terrakok

data class MachOFile(
    val path: String,
    val header: MachHeader,
    val loadCommands: List<LoadCommand>
)

data class MachHeader(
    val magic: String,
    val cputype: String,
    val cpusubtype: String,
    val filetype: String,
    val ncmds: Long,
    val sizeofcmds: Long,
    val flags: String
)

sealed interface LoadCommand {
    val cmd: String
    val cmdSize: Long
}

data class Section(
    val sectname: String,
    val segname: String,
    val addr: String,
    val size: String,
    val offset: Long,
    val align: String,
    val reloff: Long,
    val nreloc: Long,
    val type: String,
    val attributes: String,
    val reserved1: String,
    val reserved2: String
)

data class SegmentCommand(
    override val cmd: String,
    override val cmdSize: Long,
    val segname: String,
    val vmaddr: String,
    val vmsize: String,
    val fileoff: Long,
    val filesize: Long,
    val maxprot: String,
    val initprot: String,
    val nsects: Int,
    val flags: String,
    val sections: List<Section>
) : LoadCommand

data class DyldInfoCommand(
    override val cmd: String,
    override val cmdSize: Long,
    val rebaseOff: Long,
    val rebaseSize: Long,
    val bindOff: Long,
    val bindSize: Long,
    val weakBindOff: Long,
    val weakBindSize: Long,
    val lazyBindOff: Long,
    val lazyBindSize: Long,
    val exportOff: Long,
    val exportSize: Long
) : LoadCommand

data class SymtabCommand(
    override val cmd: String,
    override val cmdSize: Long,
    val symoff: Long,
    val nsyms: Long,
    val stroff: Long,
    val strsize: Long
) : LoadCommand

data class DysymtabCommand(
    override val cmd: String,
    override val cmdSize: Long,
    val ilocalsym: Long,
    val nlocalsym: Long,
    val iextdefsym: Long,
    val nextdefsym: Long,
    val iundefsym: Long,
    val nundefsym: Long,
    val tocoff: Long,
    val ntoc: Long,
    val modtaboff: Long,
    val nmodtab: Long,
    val extrefsymoff: Long,
    val nextrefsyms: Long,
    val indirectsymoff: Long,
    val nindirectsyms: Long,
    val extreloff: Long,
    val nextrel: Long,
    val locreloff: Long,
    val nlocrel: Long
) : LoadCommand

data class DylinkerCommand(
    override val cmd: String,
    override val cmdSize: Long,
    val name: String
) : LoadCommand

data class UuidCommand(
    override val cmd: String,
    override val cmdSize: Long,
    val uuid: String
) : LoadCommand

data class BuildVersionCommand(
    override val cmd: String,
    override val cmdSize: Long,
    val platform: String,
    val minos: String,
    val sdk: String,
    val ntools: Int,
    val tools: List<ToolEntry>
) : LoadCommand

data class ToolEntry(
    val tool: String,
    val version: String
)

data class SourceVersionCommand(
    override val cmd: String,
    override val cmdSize: Long,
    val version: String
) : LoadCommand

data class MainCommand(
    override val cmd: String,
    override val cmdSize: Long,
    val entryoff: Long,
    val stacksize: Long
) : LoadCommand

data class DylibCommand(
    override val cmd: String,
    override val cmdSize: Long,
    val name: String,
    val timestamp: String,
    val currentVersion: String,
    val compatibilityVersion: String
) : LoadCommand

data class LinkeditDataCommand(
    override val cmd: String,
    override val cmdSize: Long,
    val dataoff: Long,
    val datasize: Long
) : LoadCommand

data class UnknownLoadCommand(
    override val cmd: String,
    override val cmdSize: Long,
    val lines: List<String>
) : LoadCommand

class OtoolService(
    private val otool: Otool
) {
    suspend fun load(path: String): MachOFile {
        val commandsContent = otool.getLoadCommands(path, verbose = true)
        val lines = commandsContent.lines().filter { it.isNotBlank() }
        if (lines.isEmpty()) error("Empty commands content")

        val path = lines[0].removeSuffix(":")
        val loadCommands = mutableListOf<LoadCommand>()

        var i = 1
        while (i < lines.size) {
            val line = lines[i].trim()
            if (line.startsWith("Load command") || line.startsWith("Load command:", ignoreCase = true)) {
                val (command, nextIndex) = parseLoadCommand(lines, i)
                loadCommands.add(command)
                i = nextIndex
            } else {
                i++
            }
        }

        val headerContent = otool.getMachHeader(path, verbose = true)
        val header = parseMachHeader(headerContent) ?: error("Failed to parse Mach-O header")

        return MachOFile(path, header, loadCommands)
    }

    private fun parseMachHeader(content: String): MachHeader? {
        val lines = content.lines().filter { it.isNotBlank() }
        if (lines.size < 3) return null
        
        // Skip header lines
        var headerIndex = lines.indexOfFirst { it.contains("magic") && it.contains("cputype") }
        if (headerIndex == -1) return null
        
        val valuesLine = lines[headerIndex + 1].trim()
        val values = valuesLine.split(Regex("\\s+"))
        
        // magic cputype cpusubtype caps filetype ncmds sizeofcmds flags
        // Sometimes "caps" is present, sometimes not.
        // Verbose output has caps? Let's check.
        // MH_MAGIC_64 X86_64 ALL 0x00 EXECUTE 16 1296 NOUNDEFS DYLDLINK TWOLEVEL PIE
        
        if (values.size < 7) return null
        
        return MachHeader(
            magic = values[0],
            cputype = values[1],
            cpusubtype = values[2],
            filetype = if (values.size == 8) values[4] else values[3],
            ncmds = (if (values.size == 8) values[5] else values[4]).toLongOrNull() ?: 0L,
            sizeofcmds = (if (values.size == 8) values[6] else values[5]).toLongOrNull() ?: 0L,
            flags = values.last()
        )
    }

    private fun parseLoadCommand(lines: List<String>, startIndex: Int): Pair<LoadCommand, Int> {
        val cmdLine = lines[startIndex + 1].trim()
        val cmd = cmdLine.substringAfter("cmd ").trim()
        val sizeLine = lines[startIndex + 2].trim()
        val cmdSize = sizeLine.substringAfter("cmdsize ").trim().toLong()

        return when (cmd) {
            "LC_SEGMENT_64", "LC_SEGMENT" -> parseSegmentCommand(lines, startIndex + 1, cmd, cmdSize)
            "LC_DYLD_INFO_ONLY", "LC_DYLD_INFO" -> parseDyldInfoCommand(lines, startIndex + 1, cmd, cmdSize)
            "LC_SYMTAB" -> parseSymtabCommand(lines, startIndex + 1, cmd, cmdSize)
            "LC_DYSYMTAB" -> parseDysymtabCommand(lines, startIndex + 1, cmd, cmdSize)
            "LC_LOAD_DYLINKER" -> parseDylinkerCommand(lines, startIndex + 1, cmd, cmdSize)
            "LC_UUID" -> parseUuidCommand(lines, startIndex + 1, cmd, cmdSize)
            "LC_BUILD_VERSION" -> parseBuildVersionCommand(lines, startIndex + 1, cmd, cmdSize)
            "LC_SOURCE_VERSION" -> parseSourceVersionCommand(lines, startIndex + 1, cmd, cmdSize)
            "LC_MAIN" -> parseMainCommand(lines, startIndex + 1, cmd, cmdSize)
            "LC_LOAD_DYLIB", "LC_ID_DYLIB", "LC_LOAD_WEAK_DYLIB", "LC_REEXPORT_DYLIB" -> parseDylibCommand(lines, startIndex + 1, cmd, cmdSize)
            "LC_FUNCTION_STARTS", "LC_DATA_IN_CODE", "LC_CODE_SIGNATURE", "LC_DYLIB_CODE_SIGN_DRS" -> parseLinkeditDataCommand(lines, startIndex + 1, cmd, cmdSize)
            else -> parseUnknownCommand(lines, startIndex + 1, cmd, cmdSize)
        }
    }

    private fun parseSegmentCommand(lines: List<String>, cmdIndex: Int, cmd: String, cmdSize: Long): Pair<LoadCommand, Int> {
        var i = cmdIndex + 2
        val fields = mutableMapOf<String, String>()
        val sections = mutableListOf<Section>()

        while (i < lines.size) {
            val line = lines[i].trim()
            if (line.startsWith("Load command") || line.startsWith("Section")) break
            val key = line.substringBefore(" ").trim()
            val value = line.substringAfter(" ").trim()
            fields[key] = value
            i++
        }

        while (i < lines.size && lines[i].trim().startsWith("Section")) {
            val (section, nextIndex) = parseSection(lines, i)
            sections.add(section)
            i = nextIndex
        }

        return SegmentCommand(
            cmd = cmd,
            cmdSize = cmdSize,
            segname = fields["segname"] ?: "",
            vmaddr = fields["vmaddr"] ?: "",
            vmsize = fields["vmsize"] ?: "",
            fileoff = fields["fileoff"]?.toLongOrNull() ?: 0L,
            filesize = fields["filesize"]?.toLongOrNull() ?: 0L,
            maxprot = fields["maxprot"] ?: "",
            initprot = fields["initprot"] ?: "",
            nsects = fields["nsects"]?.toIntOrNull() ?: 0,
            flags = fields["flags"] ?: "",
            sections = sections
        ) to i
    }

    private fun parseSection(lines: List<String>, startIndex: Int): Pair<Section, Int> {
        var i = startIndex + 1
        val fields = mutableMapOf<String, String>()
        while (i < lines.size) {
            val line = lines[i].trim()
            if (line.startsWith("Load command") || line.startsWith("Section") || line.startsWith("attributes")) break
            val key = line.substringBefore(" ").trim()
            val value = line.substringAfter(" ").trim()
            fields[key] = value
            i++
        }

        var attributes = ""
        if (i < lines.size && lines[i].trim().startsWith("attributes")) {
            attributes = lines[i].trim().substringAfter("attributes ").trim()
            i++
        }

        while (i < lines.size) {
            val line = lines[i].trim()
            if (line.startsWith("Load command") || line.startsWith("Section")) break
            val key = line.substringBefore(" ").trim()
            val value = line.substringAfter(" ").trim()
            fields[key] = value
            i++
        }

        return Section(
            sectname = fields["sectname"] ?: "",
            segname = fields["segname"] ?: "",
            addr = fields["addr"] ?: "",
            size = fields["size"] ?: "",
            offset = fields["offset"]?.toLongOrNull() ?: 0L,
            align = fields["align"] ?: "",
            reloff = fields["reloff"]?.toLongOrNull() ?: 0L,
            nreloc = fields["nreloc"]?.toLongOrNull() ?: 0L,
            type = fields["type"] ?: "",
            attributes = attributes,
            reserved1 = fields["reserved1"] ?: "",
            reserved2 = fields["reserved2"] ?: ""
        ) to i
    }

    private fun parseDyldInfoCommand(lines: List<String>, cmdIndex: Int, cmd: String, cmdSize: Long): Pair<LoadCommand, Int> {
        var i = cmdIndex + 2
        val fields = mutableMapOf<String, String>()
        while (i < lines.size) {
            val line = lines[i].trim()
            if (line.startsWith("Load command")) break
            val key = line.substringBefore(" ").trim()
            val value = line.substringAfter(" ").trim()
            fields[key] = value
            i++
        }
        return DyldInfoCommand(
            cmd = cmd,
            cmdSize = cmdSize,
            rebaseOff = fields["rebase_off"]?.toLongOrNull() ?: 0L,
            rebaseSize = fields["rebase_size"]?.toLongOrNull() ?: 0L,
            bindOff = fields["bind_off"]?.toLongOrNull() ?: 0L,
            bindSize = fields["bind_size"]?.toLongOrNull() ?: 0L,
            weakBindOff = fields["weak_bind_off"]?.toLongOrNull() ?: 0L,
            weakBindSize = fields["weak_bind_size"]?.toLongOrNull() ?: 0L,
            lazyBindOff = fields["lazy_bind_off"]?.toLongOrNull() ?: 0L,
            lazyBindSize = fields["lazy_bind_size"]?.toLongOrNull() ?: 0L,
            exportOff = fields["export_off"]?.toLongOrNull() ?: 0L,
            exportSize = fields["export_size"]?.toLongOrNull() ?: 0L
        ) to i
    }

    private fun parseSymtabCommand(lines: List<String>, cmdIndex: Int, cmd: String, cmdSize: Long): Pair<LoadCommand, Int> {
        var i = cmdIndex + 2
        val fields = mutableMapOf<String, String>()
        while (i < lines.size) {
            val line = lines[i].trim()
            if (line.startsWith("Load command")) break
            val key = line.substringBefore(" ").trim()
            val value = line.substringAfter(" ").trim()
            fields[key] = value
            i++
        }
        return SymtabCommand(
            cmd = cmd,
            cmdSize = cmdSize,
            symoff = fields["symoff"]?.toLongOrNull() ?: 0L,
            nsyms = fields["nsyms"]?.toLongOrNull() ?: 0L,
            stroff = fields["stroff"]?.toLongOrNull() ?: 0L,
            strsize = fields["strsize"]?.toLongOrNull() ?: 0L
        ) to i
    }

    private fun parseDysymtabCommand(lines: List<String>, cmdIndex: Int, cmd: String, cmdSize: Long): Pair<LoadCommand, Int> {
        var i = cmdIndex + 2
        val fields = mutableMapOf<String, String>()
        while (i < lines.size) {
            val line = lines[i].trim()
            if (line.startsWith("Load command")) break
            val key = line.substringBefore(" ").trim()
            val value = line.substringAfter(" ").trim()
            fields[key] = value
            i++
        }
        return DysymtabCommand(
            cmd = cmd,
            cmdSize = cmdSize,
            ilocalsym = fields["ilocalsym"]?.toLongOrNull() ?: 0L,
            nlocalsym = fields["nlocalsym"]?.toLongOrNull() ?: 0L,
            iextdefsym = fields["iextdefsym"]?.toLongOrNull() ?: 0L,
            nextdefsym = fields["nextdefsym"]?.toLongOrNull() ?: 0L,
            iundefsym = fields["iundefsym"]?.toLongOrNull() ?: 0L,
            nundefsym = fields["nundefsym"]?.toLongOrNull() ?: 0L,
            tocoff = fields["tocoff"]?.toLongOrNull() ?: 0L,
            ntoc = fields["ntoc"]?.toLongOrNull() ?: 0L,
            modtaboff = fields["modtaboff"]?.toLongOrNull() ?: 0L,
            nmodtab = fields["nmodtab"]?.toLongOrNull() ?: 0L,
            extrefsymoff = fields["extrefsymoff"]?.toLongOrNull() ?: 0L,
            nextrefsyms = fields["nextrefsyms"]?.toLongOrNull() ?: 0L,
            indirectsymoff = fields["indirectsymoff"]?.toLongOrNull() ?: 0L,
            nindirectsyms = fields["nindirectsyms"]?.toLongOrNull() ?: 0L,
            extreloff = fields["extreloff"]?.toLongOrNull() ?: 0L,
            nextrel = fields["nextrel"]?.toLongOrNull() ?: 0L,
            locreloff = fields["locreloff"]?.toLongOrNull() ?: 0L,
            nlocrel = fields["nlocrel"]?.toLongOrNull() ?: 0L
        ) to i
    }

    private fun parseDylinkerCommand(lines: List<String>, cmdIndex: Int, cmd: String, cmdSize: Long): Pair<LoadCommand, Int> {
        var i = cmdIndex + 2
        val line = lines[i].trim()
        val name = line.substringAfter("name ").trim().substringBefore(" (offset")
        return DylinkerCommand(cmd, cmdSize, name) to i + 1
    }

    private fun parseUuidCommand(lines: List<String>, cmdIndex: Int, cmd: String, cmdSize: Long): Pair<LoadCommand, Int> {
        var i = cmdIndex + 2
        val line = lines[i].trim()
        val uuid = line.substringAfter("uuid ").trim()
        return UuidCommand(cmd, cmdSize, uuid) to i + 1
    }

    private fun parseBuildVersionCommand(lines: List<String>, cmdIndex: Int, cmd: String, cmdSize: Long): Pair<LoadCommand, Int> {
        var i = cmdIndex + 2
        val fields = mutableMapOf<String, String>()
        val tools = mutableListOf<ToolEntry>()
        while (i < lines.size) {
            val line = lines[i].trim()
            if (line.startsWith("Load command") || line.startsWith("tool")) break
            val key = line.substringBefore(" ").trim()
            val value = line.substringAfter(" ").trim()
            fields[key] = value
            i++
        }
        while (i < lines.size && lines[i].trim().startsWith("tool")) {
            val toolLine = lines[i].trim()
            val versionLine = lines[i + 1].trim()
            tools.add(ToolEntry(
                tool = toolLine.substringAfter("tool ").trim(),
                version = versionLine.substringAfter("version ").trim()
            ))
            i += 2
        }
        return BuildVersionCommand(
            cmd = cmd,
            cmdSize = cmdSize,
            platform = fields["platform"] ?: "",
            minos = fields["minos"] ?: "",
            sdk = fields["sdk"] ?: "",
            ntools = fields["ntools"]?.toIntOrNull() ?: 0,
            tools = tools
        ) to i
    }

    private fun parseSourceVersionCommand(lines: List<String>, cmdIndex: Int, cmd: String, cmdSize: Long): Pair<LoadCommand, Int> {
        var i = cmdIndex + 2
        val line = lines[i].trim()
        val version = line.substringAfter("version ").trim()
        return SourceVersionCommand(cmd, cmdSize, version) to i + 1
    }

    private fun parseMainCommand(lines: List<String>, cmdIndex: Int, cmd: String, cmdSize: Long): Pair<LoadCommand, Int> {
        var i = cmdIndex + 2
        val fields = mutableMapOf<String, String>()
        while (i < lines.size) {
            val line = lines[i].trim()
            if (line.startsWith("Load command")) break
            val key = line.substringBefore(" ").trim()
            val value = line.substringAfter(" ").trim()
            fields[key] = value
            i++
        }
        return MainCommand(
            cmd = cmd,
            cmdSize = cmdSize,
            entryoff = fields["entryoff"]?.toLongOrNull() ?: 0L,
            stacksize = fields["stacksize"]?.toLongOrNull() ?: 0L
        ) to i
    }

    private fun parseDylibCommand(lines: List<String>, cmdIndex: Int, cmd: String, cmdSize: Long): Pair<LoadCommand, Int> {
        var i = cmdIndex + 2
        val fields = mutableMapOf<String, String>()
        while (i < lines.size) {
            val line = lines[i].trim()
            if (line.startsWith("Load command")) break
            when {
                line.startsWith("name ") -> {
                    fields["name"] = line.substringAfter("name ").trim().substringBefore(" (offset")
                }
                line.startsWith("time stamp ") -> {
                    fields["timestamp"] = line.substringAfter("time stamp ").trim()
                }
                line.contains(" version ") -> {
                    val key = line.substringBefore(" version ").trim()
                    fields[key + "Version"] = line.substringAfter(" version ").trim()
                }
                else -> {
                    val key = line.substringBefore(" ").trim()
                    val value = line.substringAfter(" ").trim()
                    fields[key] = value
                }
            }
            i++
        }
        return DylibCommand(
            cmd = cmd,
            cmdSize = cmdSize,
            name = fields["name"] ?: "",
            timestamp = fields["timestamp"] ?: "",
            currentVersion = fields["currentVersion"] ?: "",
            compatibilityVersion = fields["compatibilityVersion"] ?: ""
        ) to i
    }

    private fun parseLinkeditDataCommand(lines: List<String>, cmdIndex: Int, cmd: String, cmdSize: Long): Pair<LoadCommand, Int> {
        var i = cmdIndex + 2
        val fields = mutableMapOf<String, String>()
        while (i < lines.size) {
            val line = lines[i].trim()
            if (line.startsWith("Load command")) break
            val key = line.substringBefore(" ").trim()
            val value = line.substringAfter(" ").trim()
            fields[key] = value
            i++
        }
        return LinkeditDataCommand(
            cmd = cmd,
            cmdSize = cmdSize,
            dataoff = fields["dataoff"]?.toLongOrNull() ?: 0L,
            datasize = fields["datasize"]?.toLongOrNull() ?: 0L
        ) to i
    }

    private fun parseUnknownCommand(lines: List<String>, cmdIndex: Int, cmd: String, cmdSize: Long): Pair<LoadCommand, Int> {
        var i = cmdIndex + 2
        val unknownLines = mutableListOf<String>()
        while (i < lines.size) {
            val line = lines[i].trim()
            if (line.startsWith("Load command")) break
            unknownLines.add(line)
            i++
        }
        return UnknownLoadCommand(cmd, cmdSize, unknownLines) to i
    }
}
