package com.singingbush.barcodescanner;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

public class ZBarScannerActivity extends Activity implements Camera.PreviewCallback {

    private static final String TAG = ZBarScannerActivity.class.getSimpleName();
    public static final int REQUEST_CODE = 6528;

    private static final String NATIVE_LIB = "iconv";

    public static final String SCAN_MODES = "SCAN_MODES";
    public static final String ERROR_INFO = "ERROR_INFO";


    private CameraPreview mPreview;
    private Camera mCamera;
    private ImageScanner mScanner;
    private Handler mAutoFocusHandler;
    private boolean mPreviewing = true;

    static {
        System.loadLibrary(NATIVE_LIB);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!isCameraAvailable()) {
            // Cancel request if there is no rear-facing camera.
            cancelRequest();
            return;
        }

        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAutoFocusHandler = new Handler();

        // Create and configure the ImageScanner;
        setupScanner();

        // Create a RelativeLayout container that will hold a SurfaceView,
        // and set it as the content of our activity.
        mPreview = new CameraPreview(this, this, autoFocusCB);
        setContentView(mPreview);
    }

    public void setupScanner() {
        mScanner = new ImageScanner();
        mScanner.setConfig(0, Config.X_DENSITY, 3);
        mScanner.setConfig(0, Config.Y_DENSITY, 3);

        int[] symbols = getIntent().getIntArrayExtra(SCAN_MODES);
        if (symbols != null) {
            mScanner.setConfig(Symbol.NONE, Config.ENABLE, 0);
            for (int symbol : symbols) {
                mScanner.setConfig(symbol, Config.ENABLE, 1);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Open the default i.e. the first rear facing camera.
        mCamera = Camera.open();
        if(mCamera == null) {
            // Cancel request if mCamera is null.
            cancelRequest();
            return;
        }

        mPreview.setCamera(mCamera);
        mPreview.showSurfaceView();

        mPreviewing = true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Because the Camera object is a shared resource, it's very
        // important to release it when the activity is paused.
        if (mCamera != null) {
            mPreview.setCamera(null);
            mCamera.cancelAutoFocus();
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();

            // According to Jason Kuang on http://stackoverflow.com/questions/6519120/how-to-recover-camera-preview-from-sleep,
            // there might be surface recreation problems when the device goes to sleep. So lets just hide it and
            // recreate on resume
            mPreview.hideSurfaceView();

            mPreviewing = false;
            mCamera = null;
        }
    }

    public boolean isCameraAvailable() {
        PackageManager pm = getPackageManager();
        if (pm != null) {
            return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
        } else {
            return false;
        }
    }

    public void cancelRequest() {
        Intent dataIntent = new Intent();
        dataIntent.putExtra(ERROR_INFO, "Camera unavailable");
        setResult(Activity.RESULT_CANCELED, dataIntent);
        finish();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        final Camera.Parameters parameters = camera.getParameters();
        final Camera.Size size = parameters.getPreviewSize();

        final Image zbarImage = new Image(size.width, size.height, "Y800"); // for some reason it needs the constructor with String arg
        zbarImage.setData(data);

        int result = mScanner.scanImage(zbarImage);

        if (result != 0) {
            mCamera.cancelAutoFocus();
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mPreviewing = false;

            SymbolSet symbols = mScanner.getResults();

            for (final Symbol symbol : symbols) {
                final String symData = symbol.getData();
                final int symType = symbol.getType();

                if (!TextUtils.isEmpty(symData)) {
                    Log.v(TAG, String.format("Zbar found barcode: '%s' '%s'", symData, symType));

                    Intent dataIntent = new Intent();
                    dataIntent.putExtra("SCAN_RESULT", symData);
                    dataIntent.putExtra("SCAN_RESULT_FORMAT", String.valueOf(symType));
                    setResult(Activity.RESULT_OK, dataIntent);

                    finish();
                }
            }
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if(mCamera != null && mPreviewing) {
                mCamera.autoFocus(autoFocusCB);
            }
        }
    };

    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            mAutoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };
}
