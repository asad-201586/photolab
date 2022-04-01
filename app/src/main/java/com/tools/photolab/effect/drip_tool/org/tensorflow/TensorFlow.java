package com.tools.photolab.effect.drip_tool.org.tensorflow;

public final class TensorFlow {
    private static native void libraryDelete(long j);

    private static native long libraryLoad(String str);

    private static native byte[] libraryOpList(long j);

    public static native byte[] registeredOpList();

    public static native String version();

    public static byte[] loadLibrary(String str) {
        try {
            long libraryLoad = libraryLoad(str);
            try {
                return libraryOpList(libraryLoad);
            } finally {
                libraryDelete(libraryLoad);
            }
        } catch (RuntimeException e) {
            throw new UnsatisfiedLinkError(e.getMessage());
        }
    }

    private TensorFlow() {
    }

    static void init() {
        NativeLibrary.load();
    }

    static {
        init();
    }
}
