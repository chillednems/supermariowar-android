# Super Mario War for Android

This repository packages the pinned [Super Mario War game](game) and SDL 2.32.10 as an Android 15 (API 35), arm64-v8a app. The first milestone supports local play and a fixed gamepad profile. Network play and editors are outside this APK.

## Build a debug APK

Install Android SDK platform/build-tools 35, NDK 29.0.14206865, JDK 17 or newer, CMake 3.24 or newer, and Ninja. Set `ANDROID_HOME` to the SDK directory; optionally set `ANDROID_NDK_HOME` for a different NDK location. Initialize both nested asset data and SDL sources:

```sh
git submodule update --init --recursive
./gradlew assembleDebug
```

The APK is `app/build/outputs/apk/debug/app-debug.apk`. Gradle builds the game and SDL libraries from source with CMake, then packages the pinned `game/data` tree as assets. On first launch a bootstrap screen copies assets to app-private files before starting SDL. Leave enough free storage for both the APK and extracted data. The app does not need external storage permissions or an `adb push` step.

CMake fetches the game dependency versions pinned in `game/cmake/BundledDeps.cmake`. For an offline build with already-fetched source trees, set `SMW_SOURCE_SDL2_IMAGE`, `SMW_SOURCE_SDL2_MIXER`, `SMW_SOURCE_TOML11`, and `SMW_SOURCE_ZLIB` to those trees before invoking Gradle. `third_party/SDL` is always the pinned Java and native SDL source. The native build only targets `smw`, with network and tests disabled.

The tested dependency revisions were SDL `5d249570393f7a37e037abf22cd6012a4cc56a71`, SDL_image `c1bf2245b0ba63a25afe2f8574d305feca25af77`, SDL_mixer `171eb2d420d5643e4ee11514a06e04a41a463bbd`, toml11 `be08ba2be2a964edcdb3d3e3ea8d100abc26f286`, zlib `da607da739fa6047df13e66a2af6b8bec7c2a498`, and game data `1139d89ef7e38368536317afd7db54cea2488d5b`. The game build declares release tags for the fetched dependencies; future tag changes could affect byte-for-byte reproducibility.

For local installation on an Android 15 arm64 device or emulator:

```sh
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

The debug APK uses Android's local debug signing key. It is for testing, not a release artifact. The user confirmed sound and controls on an earlier preview; the preview4 launcher fix still needs an Odin 3 check.

The app retains a running match when sent to the background. After choosing Exit, it ends that native session so the next launcher-icon tap starts with clean SDL and controller state. Settings and extracted assets stay in app-private storage.

When advancing the `game` or nested `game/data` pin, update `MainActivity.ASSET_VERSION` so existing installations refresh their staged asset tree on next launch. The bootstrap copies pinned assets without deleting user-created files.

`ci/android-debug.yml.template` contains the proposed APK build/check workflow. It is kept outside `.github/workflows` because the current chillednems GitHub OAuth credential lacks `workflow` scope; GitHub rejected a push containing an active workflow. Local build, signature, and 16 KB ZIP alignment checks passed, but hosted CI has not run.

## Controls

D-pad or left stick navigates and moves. SDL's standardized face-button A (bottom) selects and jumps, B (right) cancels in menus and jumps during a match, Y (top) runs, and X (left) or R1 uses powerups. L1 remains a powerup alias; Start pauses/selects. The gamepad profile is fixed for this Android milestone; the in-game Controls screen only changes keyboard bindings. Player 1 can play locally against CPU opponents.

The older `build.sh` and `custom_files` are retained for historical reference; the Gradle workflow above is the active build.
