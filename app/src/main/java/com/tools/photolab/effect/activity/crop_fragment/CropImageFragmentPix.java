package com.tools.photolab.effect.activity.crop_fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tools.photolab.BuildConfig;
import com.tools.photolab.R;

import com.tools.photolab.effect.activity.CropPhotoActivity;
import com.tools.photolab.effect.crop_img.CropImageView;
import com.tools.photolab.effect.crop_img.callback.CropCallbackElegantPhoto;
import com.tools.photolab.effect.crop_img.callback.LoadCallbackElegantPhoto;
import com.tools.photolab.effect.crop_img.callback.SaveCallbackElegantPhoto;
import com.tools.photolab.effect.crop_img.utilElegantPhoto.Utils;

import java.io.File;

public class CropImageFragmentPix extends Fragment {
    private final String TAG = CropImageFragmentPix.class.getSimpleName();

    private final int REQUEST_PICK_IMAGE = 10011;
    private final int REQUEST_SAF_PICK_IMAGE = 10012;
    private final String PROGRESS_DIALOG = "ProgressDialog";
    private final String KEY_FRAME_RECT = "FrameRect";
    private final String KEY_SOURCE_URI = "SourceUri";

    // Views ///////////////////////////////////////////////////////////////////////////////////////
    private CropImageView mCropView;
    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.JPEG;
    private RectF mFrameRect = null;
    private Uri mSourceUri = null;

    // Note: only the system can call this constructor by reflection.
    public CropImageFragmentPix() {
    }

    public static CropImageFragmentPix newInstance() {
        CropImageFragmentPix fragment = new CropImageFragmentPix();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crop_image, null, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // bind Views
        bindViews(view);

        // setDebug is used for display cropping boundry rect information
        // mCropView.setDebug(true);

        if (savedInstanceState != null) {
            // restore data
            mFrameRect = savedInstanceState.getParcelable(KEY_FRAME_RECT);
            mSourceUri = savedInstanceState.getParcelable(KEY_SOURCE_URI);
        }

        if (mSourceUri == null) {
            mSourceUri = ((CropPhotoActivity) getActivity()).currentImgUri;

        } else {
            // default data
            mSourceUri = getUriFromDrawableResId(getContext(), R.mipmap.ic_launcher);
            Log.e("aoki", "mSourceUri = " + mSourceUri);
        }

        // load image
        mCropView.load(mSourceUri)
                .initialFrameRect(mFrameRect)
                .useThumbnail(true)
                .execute(mLoadCallback);

        mCropView.setCropMode(CropImageView.CropMode.SQUARE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save data
        outState.putParcelable(KEY_FRAME_RECT, mCropView.getActualCropRect());
        outState.putParcelable(KEY_SOURCE_URI, mCropView.getSourceUri());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (resultCode == Activity.RESULT_OK) {
            // reset frame rect
            mFrameRect = null;
            switch (requestCode) {
                case REQUEST_PICK_IMAGE:
                    mSourceUri = result.getData();
                    mCropView.load(mSourceUri)
                            .initialFrameRect(mFrameRect)
                            .useThumbnail(true)
                            .execute(mLoadCallback);
                    break;
                case REQUEST_SAF_PICK_IMAGE:
                    mSourceUri = Utils.ensureUriPermission(getContext(), result);
                    mCropView.load(mSourceUri)
                            .initialFrameRect(mFrameRect)
                            .useThumbnail(true)
                            .execute(mLoadCallback);
                    break;
            }
        }
    }

    // Bind views //////////////////////////////////////////////////////////////////////////////////
    private void bindViews(View view) {
        mCropView = (CropImageView) view.findViewById(R.id.cropImageView);
        view.findViewById(R.id.buttonCancel).setOnClickListener(btnListener);
        view.findViewById(R.id.buttonRotateLeft).setOnClickListener(btnListener);
        view.findViewById(R.id.buttonRotateRight).setOnClickListener(btnListener);
        view.findViewById(R.id.buttonDone).setOnClickListener(btnListener);

    }

    public void cropImage() {
        showProgress();
        mCropView.crop(mSourceUri).execute(mCropCallback);
    }

    public void showProgress() {
        ProgressDialogFragmentPix f = ProgressDialogFragmentPix.getInstance();
        getFragmentManager().beginTransaction().add(f, PROGRESS_DIALOG).commitAllowingStateLoss();
    }

    public void dismissProgress() {
        if (!isResumed()) return;
        FragmentManager manager = getFragmentManager();
        if (manager == null) return;
        ProgressDialogFragmentPix f = (ProgressDialogFragmentPix) manager.findFragmentByTag(PROGRESS_DIALOG);
        if (f != null) {
            getFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
        }
    }

    public Uri createSaveUri() {
        try {
            return createNewUri(getContext(), mCompressFormat);
        } catch (Exception ignored) {
            return null;
        }
    }

    public String getDirPath() {
        String dirPath = "";
        File imageDir = null;
        File extStorageDir = Environment.getExternalStorageDirectory();
        if (extStorageDir.canWrite()) {
            imageDir = new File(extStorageDir.getPath() + "/simplecropview");
        }
        if (imageDir != null) {
            if (!imageDir.exists()) {
                imageDir.mkdirs();
            }
            if (imageDir.canWrite()) {
                dirPath = imageDir.getPath();
            }
        }
        return dirPath;
    }

    public Uri getUriFromDrawableResId(Context context, int drawableResId) {
        String builder = ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + context.getResources().getResourcePackageName(drawableResId)
                + "/" + context.getResources().getResourceTypeName(drawableResId)
                + "/" + context.getResources().getResourceEntryName(drawableResId);
        return Uri.parse(builder);
    }

    public Uri createNewUri(Context context, Bitmap.CompressFormat format) {
        return createTempUri(context);
    }

    public Uri createTempUri(Context context) {
        Uri mSelectedImageUri;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mSelectedImageUri = Uri.fromFile(new File(context.getCacheDir(), "cropped"));
        } else {
            mSelectedImageUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(context.getCacheDir(), "cropped"));
        }
        return mSelectedImageUri;
    }

    // Handle button event /////////////////////////////////////////////////////////////////////////
    private final View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonCancel:
                    ((CropPhotoActivity) getActivity()).cancelCropping();
                    break;
                case R.id.buttonRotateLeft:
                    mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
                    break;
                case R.id.buttonRotateRight:
                    mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
                    break;
                case R.id.buttonDone:
                    cropImage();
                    break;
            }
        }
    };

    // Callbacks ///////////////////////////////////////////////////////////////////////////////////
    private final LoadCallbackElegantPhoto mLoadCallback = new LoadCallbackElegantPhoto() {
        @Override
        public void onSuccess() {
        }

        @Override
        public void onError(Throwable e) {
        }
    };

    private final CropCallbackElegantPhoto mCropCallback = new CropCallbackElegantPhoto() {
        @Override
        public void onSuccess(Bitmap cropped) {
            Uri saveUri = createSaveUri();
            if (saveUri != null) {
                mCropView.save(cropped)
                        .compressFormat(mCompressFormat)
                        .execute(saveUri, mSaveCallback);
            }
        }

        @Override
        public void onError(Throwable e) {
            Log.e("TAG", "dfjk");
        }
    };

    private final SaveCallbackElegantPhoto mSaveCallback = new SaveCallbackElegantPhoto() {
        @Override
        public void onSuccess(Uri outputUri) {
            dismissProgress();
            ((CropPhotoActivity) getActivity()).startResultActivity(outputUri);
        }

        @Override
        public void onError(Throwable e) {
            dismissProgress();
        }
    };
}