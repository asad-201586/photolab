package com.tools.photolab.effect.drip_tool.org.tensorflow.contrib.android;

import android.os.Build;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class RunStats implements AutoCloseable {
    private static byte[] fullTraceRunOptions = {8, 3};
    private long nativeHandle = allocate();

    private static native void add(long j, byte[] bArr);

    private static native long allocate();

    private static native void delete(long j);

    private static native String summary(long j);

    public static byte[] runOptions() {
        return fullTraceRunOptions;
    }

    public void close() {
        if (this.nativeHandle != 0) {
            delete(this.nativeHandle);
        }
        this.nativeHandle = 0;
    }

    public synchronized void add(byte[] bArr) {
        add(this.nativeHandle, bArr);
    }

    public synchronized String summary() {
        return summary(this.nativeHandle);
    }
}
