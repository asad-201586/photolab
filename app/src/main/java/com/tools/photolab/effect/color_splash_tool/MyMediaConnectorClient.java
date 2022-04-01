package com.tools.photolab.effect.color_splash_tool;

import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;

public class MyMediaConnectorClient implements MediaScannerConnectionClient {
    MediaScannerConnection MEDIA_SCANNER_CONNECTION;
    String _fisier;

    public MyMediaConnectorClient(String str) {
        this._fisier = str;
    }

    public void setScanner(MediaScannerConnection mediaScannerConnection) {
        this.MEDIA_SCANNER_CONNECTION = mediaScannerConnection;
    }

    public void onMediaScannerConnected() {
        this.MEDIA_SCANNER_CONNECTION.scanFile(this._fisier, null);
    }

    public void onScanCompleted(String str, Uri uri) {
        if (str.equals(this._fisier)) {
            this.MEDIA_SCANNER_CONNECTION.disconnect();
        }
    }
}
