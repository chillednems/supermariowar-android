package net.smwstuff.supermariowar;

import org.libsdl.app.SDLActivity;

/** SDL's Java runtime and the matching pinned native libraries. */
public final class GameActivity extends SDLActivity {
    @Override protected String[] getLibraries() {
        return new String[] { "SDL2", "SDL2_image", "SDL2_mixer", "main" };
    }

    @Override protected void onDestroy() {
        boolean finished = isFinishing();
        super.onDestroy(); // SDL joins its native game thread before returning.
        if (finished) {
            // The game owns process-global SDL/controller state. A new launcher
            // start needs a fresh native session after an explicit game exit.
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}
