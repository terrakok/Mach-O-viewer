package com.github.terrakok

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * A wrapper around the `otool` system program for displaying parts of Mach-O files.
 * Provides all available API from the otool man page.
 * All functions are suspend and execute on [Dispatchers.IO].
 */
class Otool {

    /**
     * Executes the otool command with arbitrary arguments.
     * Use this if you need to combine flags that are not provided by specific methods.
     *
     * @param args The arguments to pass to the otool command.
     * @return The standard output of the otool command.
     */
    suspend fun otool(vararg args: String): String = execute(*args)

    /**
     * Executes the otool command with the given arguments.
     *
     * @param args The arguments to pass to the otool command.
     * @return The standard output of the otool command.
     * @throws Exception If the otool command fails or is not found.
     */
    private suspend fun execute(vararg args: String): String = withContext(Dispatchers.IO) {
        try {
            val process = ProcessBuilder("otool", *args)
                .redirectErrorStream(true)
                .start()

            val output = process.inputStream.bufferedReader().use { it.readText() }
            val exitValue = process.waitFor()

            if (exitValue != 0) {
                throw Exception("otool command failed with exit code $exitValue:\n$output")
            }

            output
        } catch (e: Exception) {
            throw Exception("Failed to execute otool: ${e.message}", e)
        }
    }

    /**
     * Display the fat headers.
     * Corresponds to the -f flag.
     *
     * @param path The path to the Mach-O file.
     * @param verbose If true, display the fat header verbosely (symbolically). Corresponds to -v.
     * @return The output of the otool command.
     */
    suspend fun getFatHeaders(path: String, verbose: Boolean = false): String {
        val args = mutableListOf<String>()
        args.add("-f")
        if (verbose) args.add("-v")
        args.add(path)
        return execute(*args.toTypedArray())
    }

    /**
     * Display the archive header.
     * Corresponds to the -a flag.
     *
     * @param path The path to the archive file.
     * @param arch Specify the architecture to display from a fat file.
     * @return The output of the otool command.
     */
    suspend fun getArchiveHeader(path: String, arch: String? = null): String {
        val args = mutableListOf<String>()
        arch?.let { args.add("-arch"); args.add(it) }
        args.add("-a")
        args.add(path)
        return execute(*args.toTypedArray())
    }

    /**
     * Display the mach header.
     * Corresponds to the -h flag.
     *
     * @param path The path to the Mach-O file.
     * @param arch Specify the architecture to display from a fat file.
     * @param verbose If true, display the mach header verbosely (symbolically). Corresponds to -v.
     * @return The output of the otool command.
     */
    suspend fun getMachHeader(
        path: String,
        arch: String? = null,
        verbose: Boolean = false
    ): String {
        val args = mutableListOf<String>()
        arch?.let { args.add("-arch"); args.add(it) }
        args.add("-h")
        if (verbose) args.add("-v")
        args.add(path)
        return execute(*args.toTypedArray())
    }

    /**
     * Display the load commands.
     * Corresponds to the -l flag.
     *
     * @param path The path to the Mach-O file.
     * @param arch Specify the architecture to display from a fat file.
     * @param verbose If true, display the load commands verbosely (symbolically). Corresponds to -v.
     * @return The output of the otool command.
     */
    suspend fun getLoadCommands(
        path: String,
        arch: String? = null,
        verbose: Boolean = false
    ): String {
        val args = mutableListOf<String>()
        arch?.let { args.add("-arch"); args.add(it) }
        args.add("-l")
        if (verbose) args.add("-v")
        args.add(path)
        return execute(*args.toTypedArray())
    }

    /**
     * Display the names and version numbers of the shared libraries that the object file uses.
     * Corresponds to the -L flag.
     *
     * @param path The path to the Mach-O file.
     * @param arch Specify the architecture to display from a fat file.
     * @return The output of the otool command.
     */
    suspend fun getSharedLibraries(path: String, arch: String? = null): String {
        val args = mutableListOf<String>()
        arch?.let { args.add("-arch"); args.add(it) }
        args.add("-L")
        args.add(path)
        return execute(*args.toTypedArray())
    }

    /**
     * Display just the install name of a shared library.
     * Corresponds to the -D flag.
     *
     * @param path The path to the shared library.
     * @param arch Specify the architecture to display from a fat file.
     * @return The output of the otool command.
     */
    suspend fun getSharedLibraryIdName(path: String, arch: String? = null): String {
        val args = mutableListOf<String>()
        arch?.let { args.add("-arch"); args.add(it) }
        args.add("-D")
        args.add(path)
        return execute(*args.toTypedArray())
    }

    /**
     * Display the contents of the (__TEXT,__text) section.
     * With [verbose], this disassembles the text.
     * With [verboseOperands], it also symbolically disassembles the operands.
     * Corresponds to the -t flag.
     *
     * @param path The path to the Mach-O file.
     * @param arch Specify the architecture to display from a fat file.
     * @param verbose If true, disassemble the text. Corresponds to -v.
     * @param verboseOperands If true, symbolically disassemble operands. Corresponds to -V.
     * @param noAddresses If true, don't print leading addresses or headers. Corresponds to -X.
     * @param llvmDisassembler If true, use the llvm disassembler. Corresponds to -q.
     * @param mcpu When disassembling, use the specified mcpu. Corresponds to -mcpu=arg.
     * @param printOpcodes When disassembling, print the disassembled opcodes. Corresponds to -j.
     * @param startSymbol When disassembling, start disassembly at the specified symbol. Corresponds to -p name.
     * @param dontAssumeMachOTarget If true, don't assume the target of a call or jump is another Mach-O file. Corresponds to -m.
     * @return The output of the otool command.
     */
    suspend fun getTextSection(
        path: String,
        arch: String? = null,
        verbose: Boolean = false,
        verboseOperands: Boolean = false,
        noAddresses: Boolean = false,
        llvmDisassembler: Boolean = false,
        mcpu: String? = null,
        printOpcodes: Boolean = false,
        startSymbol: String? = null,
        dontAssumeMachOTarget: Boolean = false
    ): String {
        val args = mutableListOf<String>()
        arch?.let { args.add("-arch"); args.add(it) }
        args.add("-t")
        if (verbose) args.add("-v")
        if (verboseOperands) args.add("-V")
        if (noAddresses) args.add("-X")
        if (llvmDisassembler) args.add("-q")
        mcpu?.let { args.add("-mcpu=$it") }
        if (printOpcodes) args.add("-j")
        startSymbol?.let { args.add("-p"); args.add(it) }
        if (dontAssumeMachOTarget) args.add("-m")
        args.add(path)
        return execute(*args.toTypedArray())
    }

    /**
     * Display the contents of the (__DATA,__data) section.
     * Corresponds to the -d flag.
     *
     * @param path The path to the Mach-O file.
     * @param arch Specify the architecture to display from a fat file.
     * @return The output of the otool command.
     */
    suspend fun getDataSection(path: String, arch: String? = null): String {
        val args = mutableListOf<String>()
        arch?.let { args.add("-arch"); args.add(it) }
        args.add("-d")
        args.add(path)
        return execute(*args.toTypedArray())
    }

    /**
     * Display the contents of the __OBJC segment.
     * Corresponds to the -o flag.
     *
     * @param path The path to the Mach-O file.
     * @param arch Specify the architecture to display from a fat file.
     * @param verbose If true, display the Objective-C segment verbosely. Corresponds to -v.
     * @return The output of the otool command.
     */
    suspend fun getObjectiveCSegment(
        path: String,
        arch: String? = null,
        verbose: Boolean = false
    ): String {
        val args = mutableListOf<String>()
        arch?.let { args.add("-arch"); args.add(it) }
        args.add("-o")
        if (verbose) args.add("-v")
        args.add(path)
        return execute(*args.toTypedArray())
    }

    /**
     * Display the relocation entries.
     * Corresponds to the -r flag.
     *
     * @param path The path to the Mach-O file.
     * @param arch Specify the architecture to display from a fat file.
     * @return The output of the otool command.
     */
    suspend fun getRelocationEntries(path: String, arch: String? = null): String {
        val args = mutableListOf<String>()
        arch?.let { args.add("-arch"); args.add(it) }
        args.add("-r")
        args.add(path)
        return execute(*args.toTypedArray())
    }

    /**
     * Display the contents of the __LINKEDIT segment's symbolic information.
     * Corresponds to the -S flag.
     *
     * @param path The path to the Mach-O file.
     * @param arch Specify the architecture to display from a fat file.
     * @return The output of the otool command.
     */
    suspend fun getLinkEditSegment(path: String, arch: String? = null): String {
        val args = mutableListOf<String>()
        arch?.let { args.add("-arch"); args.add(it) }
        args.add("-S")
        args.add(path)
        return execute(*args.toTypedArray())
    }

    /**
     * Display the indirect symbol table.
     * Corresponds to the -I flag.
     *
     * @param path The path to the Mach-O file.
     * @param arch Specify the architecture to display from a fat file.
     * @param verbose If true, display the indirect symbol table verbosely. Corresponds to -v.
     * @return The output of the otool command.
     */
    suspend fun getIndirectSymbolTable(
        path: String,
        arch: String? = null,
        verbose: Boolean = false
    ): String {
        val args = mutableListOf<String>()
        arch?.let { args.add("-arch"); args.add(it) }
        args.add("-I")
        if (verbose) args.add("-v")
        args.add(path)
        return execute(*args.toTypedArray())
    }

    /**
     * Display the data in code table.
     * Corresponds to the -G flag.
     *
     * @param path The path to the Mach-O file.
     * @param arch Specify the architecture to display from a fat file.
     * @return The output of the otool command.
     */
    suspend fun getDataInCodeTable(path: String, arch: String? = null): String {
        val args = mutableListOf<String>()
        arch?.let { args.add("-arch"); args.add(it) }
        args.add("-G")
        args.add(path)
        return execute(*args.toTypedArray())
    }

    /**
     * Display the contents of the specified section.
     * Corresponds to the -s segname sectname flag.
     *
     * @param path The path to the Mach-O file.
     * @param segname The segment name.
     * @param sectname The section name.
     * @param arch Specify the architecture to display from a fat file.
     * @param verbose If true, display the section verbosely. Corresponds to -v.
     * @param verboseOperands If true, symbolically disassemble operands. Corresponds to -V.
     * @param noAddresses If true, don't print leading addresses or headers. Corresponds to -X.
     * @param llvmDisassembler If true, use the llvm disassembler. Corresponds to -q.
     * @param mcpu When disassembling, use the specified mcpu. Corresponds to -mcpu=arg.
     * @param printOpcodes When disassembling, print the disassembled opcodes. Corresponds to -j.
     * @param startSymbol When disassembling, start disassembly at the specified symbol. Corresponds to -p name.
     * @param dontAssumeMachOTarget If true, don't assume the target of a call or jump is another Mach-O file. Corresponds to -m.
     * @return The output of the otool command.
     */
    suspend fun getSection(
        path: String,
        segname: String,
        sectname: String,
        arch: String? = null,
        verbose: Boolean = false,
        verboseOperands: Boolean = false,
        noAddresses: Boolean = false,
        llvmDisassembler: Boolean = false,
        mcpu: String? = null,
        printOpcodes: Boolean = false,
        startSymbol: String? = null,
        dontAssumeMachOTarget: Boolean = false
    ): String {
        val args = mutableListOf<String>()
        arch?.let { args.add("-arch"); args.add(it) }
        args.add("-s")
        args.add(segname)
        args.add(sectname)
        if (verbose) args.add("-v")
        if (verboseOperands) args.add("-V")
        if (noAddresses) args.add("-X")
        if (llvmDisassembler) args.add("-q")
        mcpu?.let { args.add("-mcpu=$it") }
        if (printOpcodes) args.add("-j")
        startSymbol?.let { args.add("-p"); args.add(it) }
        if (dontAssumeMachOTarget) args.add("-m")
        args.add(path)
        return execute(*args.toTypedArray())
    }

    /**
     * Display the argument strings (argc, argv, envp) from a core file.
     * Corresponds to the -c flag.
     *
     * @param path The path to the core file.
     * @param arch Specify the architecture to display from a fat file.
     * @return The output of the otool command.
     */
    suspend fun getArgumentStrings(path: String, arch: String? = null): String {
        val args = mutableListOf<String>()
        arch?.let { args.add("-arch"); args.add(it) }
        args.add("-c")
        args.add(path)
        return execute(*args.toTypedArray())
    }
}
