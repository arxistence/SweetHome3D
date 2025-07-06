# Sweet Home 3D - Ant to Gradle Migration Guide

## Overview

This document describes the translation of the Sweet Home 3D Ant build system to Gradle, maintaining all original functionality while adopting modern Gradle best practices.

## Migration Summary

### What Was Translated

The original `build.xml` contained 50+ targets covering:
- Core compilation and JAR building
- Multiple platform-specific builds (Windows, macOS, Linux)
- Java version compatibility handling
- JAR signing for deployment
- Installer generation for different platforms
- Source and documentation archives
- Java 3D and native library handling

### Modern Gradle Approach

The new `build.gradle` translates all functionality using:
- **Java Plugin**: Core compilation and JAR building
- **Application Plugin**: Executable distribution
- **Signing Plugin**: JAR signing capabilities
- **Distribution Plugin**: Archive generation
- **Custom Tasks**: Complex build logic not covered by standard plugins

## Key Features Preserved

### 1. Java Version Compatibility
```gradle
def javaVersion = JavaVersion.current()
def isLegacyJava = javaVersion.isJava8() || javaVersion.isJava7() || javaVersion.isJava6() || javaVersion.isJava5()

java {
    sourceCompatibility = isLegacyJava ? JavaVersion.VERSION_1_5 : JavaVersion.VERSION_1_8
    targetCompatibility = isLegacyJava ? JavaVersion.VERSION_1_5 : JavaVersion.VERSION_1_8
}
```

**Original Ant Logic:**
- Used conditional properties to set different Java versions for applets vs application
- Applet classes compiled with Java 1.1/1.2 for maximum compatibility
- Application classes compiled with Java 1.5/1.8 depending on JVM version

**Gradle Translation:**
- Dynamic Java version detection
- Conditional compilation targets
- Maintains backward compatibility for legacy Java versions

### 2. Multiple JAR Outputs
The original build created separate JARs for different purposes:

| JAR File | Purpose | Gradle Task |
|----------|---------|-------------|
| SweetHome3D.jar | Main application | `application` |
| Furniture.jar | Furniture catalog | `furniture` |
| Textures.jar | Texture resources | `textures` |
| Examples.jar | Example files | `examples` |
| Help.jar | Documentation | `help` |

### 3. Platform-Specific Native Libraries
```gradle
from(fileTree('lib')) {
    include 'windows/**/*.dll'
    include 'java3d-1.6/windows/**/*.dll'
    include 'yafaray/windows/**/*.dll'
    include 'linux/**/*.so'
    include 'java3d-1.6/linux/**/*.so'
    include 'yafaray/linux/**/*.so'
    include 'macosx/*.jnilib'
    include 'java3d-1.6/macosx/*.dylib'
    include 'yafaray/macosx/**/*.dylib'
}
```

**Handles:**
- Windows DLLs (32-bit and 64-bit)
- Linux shared objects
- macOS dynamic libraries
- Java 3D native libraries
- YafaRay rendering engine libraries

### 4. Java 3D Library Processing
Special handling for Java 3D libraries:
```gradle
tasks.register('joglJava3d', Jar) {
    from(zipTree('lib/java3d-1.6/jogl-all.jar')) {
        exclude 'com/jogamp/graph/**'
        exclude 'com/jogamp/newt/**'
        exclude 'com/jogamp/opengl/swt/**'
        // ... more exclusions
    }
}
```

**Purpose:**
- Reduces JAR size by excluding unused JOGL components
- Creates optimized Java 3D libraries for different architectures
- Maintains compatibility with Java 3D 1.5.2 and 1.6

### 5. JAR Signing
```gradle
tasks.register('signJars') {
    doLast {
        def alias = System.console()?.readLine('Enter alias for signature: ')
        def providerPath = System.console()?.readLine('Enter path of jarsigner provider [provider.cfg]: ') ?: 'provider.cfg'
        def timestampURL = System.console()?.readLine('Enter URL of timestamp authority [http://time.certum.pl/]: ') ?: 'http://time.certum.pl/'
        
        // Sign JARs with PKCS11 provider
    }
}
```

**Features:**
- Interactive credential prompting
- PKCS11 hardware token support
- Timestamp authority integration
- Batch signing of multiple JARs

## Platform-Specific Installers

### Windows Installer
```gradle
tasks.register('windowsInstaller') {
    doLast {
        exec {
            executable 'C:\\Program Files (x86)\\Inno Setup 5\\ISCC.exe'
            args = [file('install/windows/installerInnoSetup.iss').absolutePath]
        }
    }
}
```

**Requirements:**
- Windows operating system
- Inno Setup 5 Unicode
- Launch4j for executable creation
- Optional: SignTool for code signing

### macOS Installer
```gradle
tasks.register('macosxInstaller') {
    doLast {
        exec {
            executable 'hdiutil'
            args = [
                'create', '-fs', 'HFS+', '-format', 'UDBZ',
                '-srcfolder', "install/macosx/SweetHome3D-${version}",
                "install/SweetHome3D-${version}-macosx.dmg"
            ]
        }
    }
}
```

**Features:**
- Universal binary support (x86_64 and ARM64)
- Code signing with Developer ID
- Notarization support
- DMG creation with compression

### Linux Installers
```gradle
tasks.register('linux64Installer') {
    doLast {
        ant.tar(destfile: "install/SweetHome3D-${version}-linux-x64.tgz", compression: 'gzip') {
            // TAR archive creation
        }
    }
}
```

**Supports:**
- 32-bit and 64-bit architectures
- Bundled JRE inclusion
- Executable permission handling
- Compressed TAR archives

## Available Tasks

### Core Build Tasks
- `build` - Builds all components
- `application` - Main application JAR
- `furniture` - Furniture catalog JAR
- `textures` - Texture resources JAR
- `examples` - Example files JAR
- `help` - Help documentation JAR

### Distribution Tasks
- `jarExecutable` - Executable JAR with all dependencies
- `sourceArchive` - Source code archive
- `javadocArchive` - API documentation archive
- `portableArchive` - Portable installation files

### Platform Installers
- `windowsInstaller` - Windows installer (.exe)
- `macosxInstaller` - macOS installer (.dmg)
- `linux32Installer` - Linux 32-bit installer (.tgz)
- `linux64Installer` - Linux 64-bit installer (.tgz)

### Utility Tasks
- `signJars` - Sign JARs for deployment
- `java3dLibraries` - Build Java 3D libraries
- `jdepend` - Dependency analysis

## Configuration Requirements

### Platform-Specific Paths
Update these paths in `build.gradle` for your environment:
```gradle
ext {
    javaHome_windows_x86 = 'C:\\Program Files (x86)\\Java\\jre1.8.0_202'
    javaHome_windows_x64 = 'C:\\Program Files\\OpenJDK\\zulu11.62.17-ca-jdk11.0.18-win_x64'
    javaHome_macosx_x86_64 = '/Library/Java/JavaVirtualMachines/zulu-15.0.10-macosx_x86_64.jdk/Contents/Home'
    javaHome_macosx_arm64 = '/Library/Java/JavaVirtualMachines/zulu-15.0.10-macosx_aarch64.jdk/Contents/Home'
    javaHome_linux_x86 = 'jre1.8.0_202'
    javaHome_linux_x64 = 'jre1.8.0_202'
}
```

### Required Tools
- **Windows**: Inno Setup 5 Unicode, Launch4j, SignTool (optional)
- **macOS**: Xcode command line tools, codesign (optional)
- **Linux**: Standard build tools

## Migration Benefits

### Gradle Advantages
1. **Incremental Builds**: Only rebuild what changed
2. **Parallel Execution**: Tasks run in parallel when possible
3. **Dependency Management**: Better handling of JAR dependencies
4. **IDE Integration**: Better support in IntelliJ IDEA, Eclipse
5. **Plugin Ecosystem**: Access to Gradle plugin repository

### Build Performance
- Faster incremental builds
- Configuration caching
- Build cache support
- Parallel task execution

### Modern Tooling
- Gradle wrapper ensures consistent builds
- Better integration with CI/CD systems
- Improved dependency resolution
- Task input/output tracking

## Compatibility Notes

### Java 3D Dependencies
The build maintains compatibility with both Java 3D 1.5.2 and 1.6:
- Automatic library selection based on platform
- JOGL integration for OpenGL rendering
- Native library bundling for all platforms

### Legacy Java Support
- Maintains compatibility with Java 1.5+ for application code
- Special handling for applet classes (Java 1.1 compatibility)
- Conditional compilation based on runtime Java version

### Directory Structure
The Gradle build maintains the same output directory structure as the original Ant build:
- `build/` - Compiled classes and intermediate JARs
- `install/` - Final distribution files
- `deploy/` - Web deployment files

## Migration Checklist

- [ ] Update platform-specific JRE paths in `build.gradle`
- [ ] Verify required tools are installed (Inno Setup, Launch4j, etc.)
- [ ] Test core build: `./gradlew build`
- [ ] Test executable JAR: `./gradlew jarExecutable`
- [ ] Test platform-specific installers
- [ ] Verify JAR signing works with your certificates
- [ ] Update CI/CD scripts to use Gradle instead of Ant
- [ ] Update documentation references

## Troubleshooting

### Common Issues

1. **Java 3D Libraries Not Found**
   - Ensure `lib/java3d-1.6/` directory exists
   - Check that native libraries are present for your platform

2. **Compilation Errors**
   - Verify Java version compatibility settings
   - Check that `lib/` directory contains all required JARs

3. **Platform-Specific Build Failures**
   - Ensure required tools are installed and in PATH
   - Check platform-specific path configurations

4. **JAR Signing Failures**
   - Verify certificate configuration
   - Check PKCS11 provider configuration
   - Ensure jarsigner is available

### Getting Help
- Check Gradle documentation: https://docs.gradle.org/
- Review original Ant build.xml for reference
- Consult Sweet Home 3D documentation