package com.builtbymoby.anode.utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

public class Utils {

    private Utils() {};

    public static boolean isExternalStorageRemovable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    public static File getExternalCacheDir(Context context) {
        if (hasExternalCacheDir()) {
            return context.getExternalCacheDir();
        }

        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    public static boolean hasExternalCacheDir() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }
    
    public static byte[] readBytes(InputStream inputStream) throws IOException {
    	ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

    	int bufferSize = 1024;
    	byte[] buffer = new byte[bufferSize];

    	int len = 0;
    	while ((len = inputStream.read(buffer)) != -1) {
    		byteBuffer.write(buffer, 0, len);
    	}
    	
    	return byteBuffer.toByteArray();
    }

}