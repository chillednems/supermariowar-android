package net.smwstuff.supermariowar;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/** Installs the real filesystem asset tree before SDL starts. */
public final class MainActivity extends Activity {
    private static final String TAG = "SuperMarioWar";
    private static final String ASSET_VERSION = "3fab0b99a7ac267ff4aa6ec2ae086348b51c022f";

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        TextView status = new TextView(this);
        status.setGravity(Gravity.CENTER);
        status.setText("Installing game data…");
        setContentView(status);
        new Thread(() -> {
            try {
                File marker = new File(getFilesDir(), ".assets-" + ASSET_VERSION);
                if (!marker.isFile()) {
                    copyTree(getAssets(), "data", new File(getFilesDir(), "data"));
                    if (!marker.createNewFile()) throw new IOException("Cannot mark installed assets");
                }
                ensureDirectory(new File(getFilesDir(), "data/maps/cache"));
                ensureDirectory(new File(getFilesDir(), "data/screenshots"));
                runOnUiThread(() -> {
                    startActivity(new Intent(this, GameActivity.class));
                    finish();
                });
            } catch (IOException error) {
                Log.e(TAG, "Could not install game assets", error);
                runOnUiThread(() -> status.setText("Game data installation failed: " + error.getMessage()));
            }
        }, "smw-asset-install").start();
    }

    private static void ensureDirectory(File directory) throws IOException {
        if (!directory.isDirectory() && !directory.mkdirs())
            throw new IOException("Cannot create " + directory);
    }

    private static void copyTree(AssetManager assets, String path, File dest) throws IOException {
        String[] children = assets.list(path);
        if (children == null) throw new IOException("Cannot list " + path);
        if (children.length == 0) {
            if (!path.startsWith("data/")) throw new IOException("Missing game assets: " + path);
            File parent = dest.getParentFile();
            if (!parent.isDirectory() && !parent.mkdirs()) throw new IOException("Cannot create " + parent);
            try (InputStream input = assets.open(path); FileOutputStream output = new FileOutputStream(dest)) {
                byte[] buffer = new byte[16384];
                int count;
                while ((count = input.read(buffer)) != -1) output.write(buffer, 0, count);
            }
        } else {
            if (!dest.isDirectory() && !dest.mkdirs()) throw new IOException("Cannot create " + dest);
            for (String child : children) copyTree(assets, path + "/" + child, new File(dest, child));
        }
    }
}
