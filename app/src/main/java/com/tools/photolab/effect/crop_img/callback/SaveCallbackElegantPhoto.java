package com.tools.photolab.effect.crop_img.callback;

import android.net.Uri;

public interface SaveCallbackElegantPhoto extends CallbackElegantPhoto {
  void onSuccess(Uri uri);
}
