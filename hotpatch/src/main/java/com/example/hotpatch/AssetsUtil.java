package com.example.hotpatch;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author 4399lyh
 */
public class AssetsUtil {
    public static void copyAssets(Context context, String assetsName, String destFilePath) throws IOException {
        File file = new File(destFilePath);
        FileOutputStream out = new FileOutputStream(file);
        InputStream inputStream = context.getAssets().open(assetsName);

        byte[] buf = new byte[1024];
        int len;
        while ((len = inputStream.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
        inputStream.close();
        out.close();
    }
}
