package com.github.terrakok

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * A service for general file operations.
 */
class FileService {

    /**
     * Checks if the file at the given path is a Mach-O file.
     * It uses the system `file` command for this check.
     *
     * @param path The path to the file.
     * @return True if the file is a Mach-O file, false otherwise.
     */
    suspend fun isMachO(path: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val process = ProcessBuilder("file", "-b", path)
                .redirectErrorStream(true)
                .start()

            val output = process.inputStream.bufferedReader().use { it.readText() }
            val exitValue = process.waitFor()

            if (exitValue != 0) {
                return@withContext false
            }

            output.contains("Mach-O", ignoreCase = true)
        } catch (_: Exception) {
            false
        }
    }
}
