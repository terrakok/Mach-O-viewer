# Mach-O Viewer
Mach-O Viewer is a desktop application for exploring and analyzing Mach-O binary files, including universal (fat) binaries. Built with Compose Multiplatform.

![App Screenshot](media/app.png)

## Features
- **Support for Mach-O universal (fat) binaries**: Inspect all architectures in a single file.
- **Detailed Mach-O Structure View**: Browse headers, load commands, and sections with full field information.
- **Interactive Hex Viewer**: Click on addresses in the parsed table to jump to the corresponding raw bytes in the hex viewer.
- **Visual Highlighting**: Highlight data ranges in both hex and ASCII representations.
- **Drag-and-Drop Support**: Open files by dragging them onto the application window or the application icon.
- **Command-line arguments**: Open files by passing paths directly to the executable.
- **GraalVM Native Image**: Faster startup and lower memory usage.
- **File associations**: Double-click `.dylib`, `.o`, `.a`, and `.bundle` files to open them directly.
- **Auto-update**: The app checks for new releases on GitHub and updates itself automatically.

## Installation

### Releases
1. Download the latest `.dmg` from the [Releases](https://github.com/terrakok/Mach-O-viewer/releases) page.
2. Open the downloaded `.dmg` and drag the **Mach-O viewer.app** to your **Applications** folder.
3. Since the app is not signed, you may need to run the following command to allow it to run:
   ```bash
   xattr -d com.apple.quarantine /Applications/Mach-O\ viewer.app
   ```

## Development
To run the desktop application:
```bash
./gradlew :app:run
```

To run with **hot reload** support:
```bash
./gradlew :app:hotRun --auto
```

## Support the Developer

<p align="center">
  <a href="https://www.buymeacoffee.com/terrakok" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/default-green.png" alt="Buy Me A Coffee" width="200"></a>
</p>

If you enjoy the project and want to support its development, consider buying me a coffee. Your support helps keep this project free and open-source!
