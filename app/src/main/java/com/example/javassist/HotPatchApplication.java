package com.example.javassist;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Environment;

import com.example.hotpatch.HotPatch;


/**
 * @author 4399lyh
 */
public class HotPatchApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        HotPatch.init(this);
        //获取补丁，如果存在就执行注入操作
        String dexPath = Environment.getExternalStorageDirectory().getAbsolutePath().concat("/patch_dex.jar");
        HotPatch.inject(dexPath);
    }
}
