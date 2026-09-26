#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "$0")/.." && pwd)"
sdk="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-$HOME/Library/Android/sdk}}"
ndk="${ANDROID_NDK_HOME:-$sdk/ndk/29.0.14206865}"
cmake="${CMAKE:-cmake}"
build="$root/app/build/native/arm64-v8a"
mkdir -p "$build"
args=("-DCMAKE_TOOLCHAIN_FILE=$ndk/build/cmake/android.toolchain.cmake" -DANDROID_ABI=arm64-v8a -DANDROID_PLATFORM=android-35 -DCMAKE_BUILD_TYPE=Release -DBUILD_TESTS=OFF -DNO_NETWORK=ON -DSMW_INSTALL_PORTABLE=ON -DSDL2_FORCE_GLES=ON "-DFETCHCONTENT_SOURCE_DIR_SDL2=$root/third_party/SDL")
for dep in SDL2_IMAGE SDL2_MIXER TOML11 ZLIB; do
    var="SMW_SOURCE_$dep"
    if [[ -n "${!var:-}" ]]; then args+=("-DFETCHCONTENT_SOURCE_DIR_${dep}=${!var}"); fi
done
"$cmake" -S "$root/game" -B "$build" -G Ninja "${args[@]}"
"$cmake" --build "$build" --target smw -j "${SMW_JOBS:-4}"
libs="$root/app/build/generated/jniLibs/arm64-v8a"
mkdir -p "$libs"
for path in "$build/src/smw/libmain.so" "$build/_deps/sdl2-build/libSDL2.so" "$build/_deps/sdl2_image-build/libSDL2_image.so" "$build/_deps/sdl2_mixer-build/libSDL2_mixer.so"; do
    cp "$path" "$libs/"
done
