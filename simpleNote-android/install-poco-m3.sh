#!/usr/bin/env bash
cd "$(dirname "$0")"
ADB_BIN="/Users/admin/Library/Android/sdk/platform-tools/adb"
if [ ! -f "$ADB_BIN" ]; then
    ADB_BIN=$(which adb 2>/dev/null)
fi
echo "Menginstal SimpleNote APK ke Poco M3 via ADB..."
"$ADB_BIN" install -r app-debug.apk
echo "Membuka aplikasi di layar HP..."
"$ADB_BIN" shell am start -n com.simplenote.app/.MainActivity
