package com.singingbush.barcodescanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.mirasense.scanditsdk.ScanditSDKBarcodePicker;
import com.mirasense.scanditsdk.ScanditSDKScanSettings;
import com.mirasense.scanditsdk.interfaces.ScanditSDKCode;
import com.mirasense.scanditsdk.interfaces.ScanditSDKOnScanListener;
import com.mirasense.scanditsdk.interfaces.ScanditSDKScanSession;

import static com.mirasense.scanditsdk.interfaces.ScanditSDK.Symbology;

/**
 * @author samael
 */
public class ScanditActivity extends Activity implements ScanditSDKOnScanListener {

    private static final String TAG = ScanditActivity.class.getSimpleName();
    private static final String API_KEY = "** API KEY HERE **";
    public static final int REQUEST_CODE = 6518;

    private ScanditSDKBarcodePicker picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG, "starting " + TAG);

        ScanditSDKScanSettings settings = ScanditSDKScanSettings.getDefaultSettings();
        final Symbology[] symbologies = new Symbology[] {Symbology.EAN13, Symbology.UPCE, Symbology.UPC12, Symbology.QR};
        settings.enableSymbologies(symbologies);

        picker = new ScanditSDKBarcodePicker(this, API_KEY, settings);
        picker.addOnScanListener(this);

        // set options for the overlay
        //picker.getOverlayView().showSearchBar(true); // allows manual entry of barcodes, defaults to false
        //picker.getOverlayView().setBeepEnabled(false); // enabled by default
        //picker.getOverlayView().setTorchEnabled(true); // enabled by default
        picker.getOverlayView().setViewfinderColor(0.3f, 0.7f, 0.7f); // feature is only available with the Scandit SDK Enterprise Packages
        picker.getOverlayView().setViewfinderDecodedColor(0.4f, 1.0f, 0.4f); // supposed to be Enterprise only - but works anyway
        setContentView(picker);
    }

    @Override
    protected void onResume() {
        picker.startScanning();
        super.onResume();
    }

    @Override
    protected void onPause() {
        picker.stopScanning();
        super.onPause();
    }

    @Override
    public void didScan(ScanditSDKScanSession session) {
        session.stopScanning();

        for(final ScanditSDKCode code : session.getNewlyDecodedCodes()) {
            Log.v(TAG, String.format("Scandit didScanBarcode: '%s' '%s'", code.getData(), code.getSymbologyString()));

            Intent result = new Intent();
            result.putExtra("SCAN_RESULT", code.getData());
            result.putExtra("SCAN_RESULT_FORMAT", code.getSymbologyString());
            setResult(Activity.RESULT_OK, result);

            finish();
        }
    }
}
