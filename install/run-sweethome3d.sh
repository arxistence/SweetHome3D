#!/bin/bash

# Sweet Home 3D Startup Script
# This script extracts native JOGL libraries and runs Sweet Home 3D with proper library paths

set -e

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Sweet Home 3D JAR file
JAR_FILE="$SCRIPT_DIR/SweetHome3D-clean-7.5.jar"

# Check if JAR file exists
if [ ! -f "$JAR_FILE" ]; then
    echo "Error: Sweet Home 3D JAR file not found: $JAR_FILE"
    echo "Please ensure SweetHome3D-clean-7.5.jar is in the same directory as this script."
    exit 1
fi

# Create temporary directory for native libraries
TEMP_DIR=$(mktemp -d)
NATIVE_DIR="$TEMP_DIR/native"
mkdir -p "$NATIVE_DIR"

# Cleanup function
cleanup() {
    if [ -d "$TEMP_DIR" ]; then
        rm -rf "$TEMP_DIR"
    fi
}
trap cleanup EXIT

echo "Sweet Home 3D - Starting application..."
echo "Extracting native libraries..."

# Detect operating system and architecture
OS_NAME=$(uname -s)
ARCH=$(uname -m)

# Determine platform-specific library paths and extensions
case "$OS_NAME" in
    "Darwin")
        # macOS
        PLATFORM="macosx"
        LIB_EXT="dylib"
        NATIVE_PATH="java3d-1.6/macosx"
        ;;
    "Linux")
        # Linux
        PLATFORM="linux"
        LIB_EXT="so"
        if [ "$ARCH" = "x86_64" ]; then
            NATIVE_PATH="java3d-1.6/linux/amd64"
        else
            NATIVE_PATH="java3d-1.6/linux/i586"
        fi
        ;;
    "CYGWIN"* | "MINGW"* | "MSYS"*)
        # Windows
        PLATFORM="windows"
        LIB_EXT="dll"
        if [ "$ARCH" = "x86_64" ]; then
            NATIVE_PATH="java3d-1.6/windows/amd64"
        else
            NATIVE_PATH="java3d-1.6/windows/i586"
        fi
        ;;
    *)
        echo "Warning: Unsupported operating system: $OS_NAME"
        echo "Attempting to run without native library extraction..."
        PLATFORM="unknown"
        ;;
esac

# Extract native libraries if platform is supported
if [ "$PLATFORM" != "unknown" ]; then
    echo "Detected platform: $PLATFORM ($ARCH)"
    echo "Extracting libraries from: $NATIVE_PATH"

    # Extract native libraries using jar command
    cd "$TEMP_DIR"
    jar -xf "$JAR_FILE" "$NATIVE_PATH/" 2>/dev/null || {
        echo "Warning: Could not extract native libraries from $NATIVE_PATH"
        echo "The application may still work with system-installed libraries."
    }

    # Copy extracted libraries to native directory
    if [ -d "$NATIVE_PATH" ]; then
        cp "$NATIVE_PATH"/*."$LIB_EXT" "$NATIVE_DIR/" 2>/dev/null || true
        EXTRACTED_COUNT=$(ls "$NATIVE_DIR"/*."$LIB_EXT" 2>/dev/null | wc -l || echo "0")
        echo "Extracted $EXTRACTED_COUNT native libraries"

        # List extracted libraries for debugging
        if [ "$EXTRACTED_COUNT" -gt 0 ]; then
            echo "Native libraries:"
            ls -la "$NATIVE_DIR"/*."$LIB_EXT"
        fi
    fi
fi

# Set up Java system properties
JAVA_OPTS=""

# Set library path if we extracted libraries
if [ -d "$NATIVE_DIR" ] && [ "$(ls -A "$NATIVE_DIR" 2>/dev/null)" ]; then
    JAVA_OPTS="$JAVA_OPTS -Djava.library.path=$NATIVE_DIR"
fi

# Sweet Home 3D specific properties
JAVA_OPTS="$JAVA_OPTS -Dcom.eteks.sweethome3d.j3d.version=1.6"
JAVA_OPTS="$JAVA_OPTS -Djogamp.gluegen.UseTempJarCache=false"

# Memory settings
JAVA_OPTS="$JAVA_OPTS -Xmx2g"

# macOS specific settings
if [ "$PLATFORM" = "macosx" ]; then
    JAVA_OPTS="$JAVA_OPTS -Xdock:name=Sweet Home 3D"
    JAVA_OPTS="$JAVA_OPTS -Xdock:icon=$SCRIPT_DIR/SweetHome3DIcon.icns"
    JAVA_OPTS="$JAVA_OPTS -Dapple.laf.useScreenMenuBar=true"
fi

# Debug output (uncomment for troubleshooting)
# JAVA_OPTS="$JAVA_OPTS -Djogamp.debug=all"
# JAVA_OPTS="$JAVA_OPTS -Dcom.eteks.sweethome3d.debug=true"

echo "Starting Sweet Home 3D..."
echo "Java options: $JAVA_OPTS"
echo ""

# Run Sweet Home 3D
exec java $JAVA_OPTS -jar "$JAR_FILE" "$@"
