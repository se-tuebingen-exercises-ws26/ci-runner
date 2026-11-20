#!/bin/sh
cd "$(dirname "$0")"

sbt run || exit 1
if command -v wslpath >/dev/null 2>&1; then
    echo "Open file://$(wslpath -w $(pwd)/www/game.html) in your browser"
elif [ -f /proc/sys/fs/binfmt_misc/WSLInterop ]; then
    echo "Using WSL but no wslpath found."
    echo "Open file://<path to wsl root>/$(pwd)/www/game.html in your Browser."
    echo "You can, for example navigate to the file in the windows file explorer"
    echo "and open it from there (using the browser)."
    echo "Also feel free to ask a question in the forum about this."
elif command -v open >/dev/null 2>&1; then
    open ./www/game.html
elif command -v xdg-open >/dev/null 2>&1; then
    xdg-open ./www/game.html
elif command -v firefox >/dev/null 2>&1; then
    firefox ./www/game.html
elif command -v chromium >/dev/null 2>&1; then
    chromium ./www/game.html
elif command -v chrome >/dev/null 2>&1; then
    chrome ./www/game.html
else
    echo "Open file://$(pwd)/www/game.html in your browser"
fi