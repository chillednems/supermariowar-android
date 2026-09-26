package net.smwstuff.supermariowar;

import org.libsdl.app.SDLActivity;

/** SDL's Java runtime and the matching pinned native libraries. */
public final class GameActivity extends SDLActivity {
    @Override protected String[] getLibraries() {
        return new String[] { "SDL2", "SDL2_image", "SDL2_mixer", "main" };
    }
}
