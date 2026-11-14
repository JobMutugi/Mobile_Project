#!/usr/bin/env sh
# Small Gradle bootstrap for this starter project.
# It downloads the official Gradle 8.13 binary distribution once, then forwards all arguments.
# This avoids depending on a separately bundled gradle-wrapper.jar in this package.
set -eu
GRADLE_VERSION="9.6.0"
BASE_DIR="${GRADLE_USER_HOME:-$HOME/.gradle}/manual-wrapper"
INSTALL_DIR="$BASE_DIR/gradle-$GRADLE_VERSION"
ZIP_FILE="$BASE_DIR/gradle-$GRADLE_VERSION-bin.zip"
GRADLE_BIN="$INSTALL_DIR/bin/gradle"

if [ ! -x "$GRADLE_BIN" ]; then
  mkdir -p "$BASE_DIR"
  if [ ! -f "$ZIP_FILE" ]; then
    URL="https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"
    echo "Downloading official Gradle $GRADLE_VERSION. Internet access is required for the first run."
    if command -v curl >/dev/null 2>&1; then
      curl -fL --retry 2 "$URL" -o "$ZIP_FILE"
    elif command -v wget >/dev/null 2>&1; then
      wget -O "$ZIP_FILE" "$URL"
    else
      python3 - "$URL" "$ZIP_FILE" <<'PY'
import sys, urllib.request
urllib.request.urlretrieve(sys.argv[1], sys.argv[2])
PY
    fi
  fi
  if command -v unzip >/dev/null 2>&1; then
    unzip -q -o "$ZIP_FILE" -d "$BASE_DIR"
  else
    python3 - "$ZIP_FILE" "$BASE_DIR" <<'PY'
import sys, zipfile
with zipfile.ZipFile(sys.argv[1]) as z:
    z.extractall(sys.argv[2])
PY
  fi
fi
exec "$GRADLE_BIN" "$@"
