package com.singingbush.barcodescanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.ebay.redlasersdk.BarcodeResult;
import com.ebay.redlasersdk.BarcodeScanActivity;
import com.ebay.redlasersdk.RedLaserExtras;

import java.util.Map;
import java.util.Set;

/**
 * @author samael
 */
public class RedLaserActivity extends BarcodeScanActivity {

    private static final String TAG = RedLaserActivity.class.getSimpleName();
    public static final int REQUEST_CODE = 6538;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        enabledTypes.setEan13(true);
        enabledTypes.setUpce(true);
        enabledTypes.setQRCode(true);


        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    /**
     *  Because the camera gets initialized in super.onResume(), we can't find out
     *  whether the torch is available until after super.onResume() completes.
     *  So, we enable or disable the 'Light' button here instead of in onCreate().
     */
    @Override
    protected void onResume() {
        super.onResume();
        //toggleTorchButton.setEnabled(isTorchAvailable());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     *  Called by the SDK repeatedly while scanning is happening. This allows
     *  for scanning multiple codes in a session.
     */
    @Override
    protected void onScanStatusUpdate(Map<String, Object> scanStatus) {
        @SuppressWarnings(value = "unchecked")
        Set<BarcodeResult> allResults = (Set<BarcodeResult>) scanStatus.get(Status.STATUS_FOUND_BARCODES);

        for(final BarcodeResult bResult : allResults) {
            Log.v(TAG, String.format("Red Laser onScanStatusUpdate: '%s' '%s'", bResult.barcodeString, bResult.getBarcodeType()));

            Intent result = new Intent();
            result.putExtra("SCAN_RESULT", bResult.barcodeString);
            result.putExtra("SCAN_RESULT_FORMAT", bResult.getBarcodeType());
            setResult(Activity.RESULT_OK, result);

            finish();
        }
    }
}
