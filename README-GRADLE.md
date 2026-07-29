# Sweet Home 3D - Gradle Build System

## Overview

This document describes the Gradle build system for Sweet Home 3D.

## Quick Start

### Prerequisites

- Java 21 (the build uses a Java 21 toolchain; no earlier JDK is supported)
- Platform-specific tools (see Platform Requirements below)

### Basic Build Commands

```bash
# Build the main application
./gradlew build

# Create executable JAR
./gradlew jarExecutable

# Build all components
./gradlew assemble

# Run tests
./gradlew test

# Clean build artifacts
./gradlew clean
```

### Platform-Specific Installers

```bash
# Windows installer (Windows only)
./gradlew windowsInstaller

# macOS installer (macOS only)
./gradlew macosxInstaller

# Linux installers
./gradlew linux32Installer
./gradlew linux64Installer

# Portable archive
./gradlew portableArchive
```

## Available Tasks

### Core Build Tasks

| Task | Description | Output |
|------|-------------|--------|
| `build` | Builds all components | `build/` directory |
| `application` | Main application JAR | `build/SweetHome3D.jar` |
| `furniture` | Furniture catalog JAR | `build/Furniture.jar` |
| `textures` | Texture resources JAR | `build/Textures.jar` |
| `examples` | Example files JAR | `build/Examples.jar` |
| `help` | Help documentation JAR | `build/Help.jar` |

### Distribution Tasks

| Task | Description | Output |
|------|-------------|--------|
| `jarExecutable` | Executable JAR with all dependencies | `install/SweetHome3D-{version}.jar` |
| `sourceArchive` | Source code archive | `install/SweetHome3D-{version}-src.zip` |
| `javadocArchive` | API documentation archive | `install/SweetHome3D-{version}-javadoc.zip` |
| `portableArchive` | Portable installation files | `install/portable/` |

### Platform Installers

| Task | Description | Platform | Output |
|------|-------------|----------|--------|
| `windowsInstaller` | Windows installer | Windows | `install/SweetHome3D-{version}-windows.exe` |
| `macosxInstaller` | macOS disk image | macOS | `install/SweetHome3D-{version}-macosx.dmg` |
| `linux32Installer` | Linux 32-bit archive | Any | `install/SweetHome3D-{version}-linux-x86.tgz` |
| `linux64Installer` | Linux 64-bit archive | Any | `install/SweetHome3D-{version}-linux-x64.tgz` |

### Utility Tasks

| Task | Description | Purpose |
|------|-------------|---------|
| `java3dLibraries` | Build Java 3D libraries | Platform support |
| `jdepend` | Dependency analysis | Code quality |
| `javadoc` | Generate API documentation | Documentation |

## Configuration

The Java version is fixed by the Gradle toolchain block in `build.gradle` (Java 21) — there is no per-platform JRE path configuration to edit. Dependency versions live in `gradle/libs.versions.toml`. Windows/macOS installer tooling paths (Inno Setup, hdiutil) are being replaced by jpackage; see the project's modernization design doc for the current state of packaging.

## Platform Requirements

### Windows
- **Required**: Windows 10 or later
- **Tools**: 
  - Inno Setup 5 Unicode (for installer creation)
  - Launch4j (for executable creation)
  - Windows SDK with SignTool (for code signing, optional)
- **JRE**: Windows 32-bit and 64-bit JREs

### macOS
- **Required**: macOS 10.14 or later
- **Tools**: 
  - Xcode Command Line Tools
  - codesign (for code signing, optional)
  - hdiutil (included with macOS)
- **JRE**: Universal JRE supporting x86_64 and ARM64

### Linux
- **Required**: Any modern Linux distribution
- **Tools**: Standard build tools (tar, gzip)
- **JRE**: Linux 32-bit and 64-bit JREs

## Java 3D Support

The build system handles Java 3D libraries automatically:

### Supported Versions
- Java 3D 1.6.2a (custom JogAmp/eTeks build, in `lib/java3d-1.6/`)
- JOGL 2.5.0 (OpenGL integration, in `lib/java3d-1.6/`)

### Native Libraries
Automatically includes platform-specific native libraries:
- **Windows**: DLL files for 32-bit and 64-bit
- **Linux**: SO files for 32-bit and 64-bit
- **macOS**: DYLIB files for x86_64 and ARM64

### Library Optimization
The build creates optimized JOGL libraries by:
- Excluding unused components (NEWT, SWT integration)
- Removing debug and trace classes
- Optimizing for 3D graphics only

## Build Outputs

### Directory Structure
```
build/                          # Compiled classes and intermediate JARs
├── classes/                    # Compiled Java classes
├── SweetHome3D.jar            # Main application JAR
├── Furniture.jar              # Furniture catalog
├── Textures.jar               # Texture resources
├── Examples.jar               # Example files
└── Help.jar                   # Help documentation

install/                        # Final distribution files
├── SweetHome3D-{version}.jar  # Executable JAR
├── SweetHome3D-{version}-src.zip        # Source archive
├── SweetHome3D-{version}-javadoc.zip    # Documentation
├── SweetHome3D-{version}-windows.exe    # Windows installer
├── SweetHome3D-{version}-macosx.dmg     # macOS installer
├── SweetHome3D-{version}-linux-x86.tgz  # Linux 32-bit
├── SweetHome3D-{version}-linux-x64.tgz  # Linux 64-bit
└── portable/                            # Portable files

deploy/                         # Web deployment files
└── lib/                       # Third-party dependency jars
```

## Development Workflow

### IDE Integration
The Gradle build works seamlessly with:
- **IntelliJ IDEA**: Import as Gradle project
- **Eclipse**: Use Gradle integration plugin
- **Visual Studio Code**: Use Gradle extension

### Incremental Builds
Gradle provides fast incremental builds:
- Only rebuilds changed files
- Caches compilation results
- Parallel task execution

### Debugging
For debugging build issues:
```bash
# Verbose output
./gradlew build --info

# Debug output
./gradlew build --debug

# Profile build performance
./gradlew build --profile
```

## History

This build was originally translated from an Ant `build.xml`, which has since been deleted along with the rest of the Ant-era tooling (the Gradle tasks above are now the only build entry points).

## Troubleshooting

### Common Issues

**Java 3D Libraries Not Found**
```bash
# Check lib directory structure
ls -la lib/java3d-1.6/
# Should contain: j3dcore.jar, j3dutils.jar, vecmath.jar, etc.
```

**Compilation Errors**
```bash
# Check Java version
java -version
# Verify source/target compatibility in build.gradle
```

**Platform-Specific Build Failures**
```bash
# Check required tools are installed
which innosetup  # Windows
which hdiutil    # macOS
which tar        # Linux
```

### Getting Help
1. Review Gradle documentation: https://docs.gradle.org/

## Performance Tips

### Build Performance
```bash
# Enable parallel builds
./gradlew build --parallel

# Enable build cache
./gradlew build --build-cache

# Use daemon (default)
./gradlew build --daemon
```

### Memory Configuration
Adjust in `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=1g
```

## License

This build system is part of Sweet Home 3D and follows the same licensing:
- GNU General Public License v2.0
- See LICENSE.TXT for details

## Contributing

When contributing to the build system:
1. Test on all supported platforms
2. Follow Gradle best practices
3. Update documentation for any changes

## Changelog

### Version 7.5
- Initial Gradle build system
- Modern Gradle practices implementation
- Platform-specific installer support
- Java 3D library optimization
- Comprehensive documentation