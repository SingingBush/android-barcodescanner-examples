package com.singingbush.barcodescanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.mirasense.scanditsdk.ScanditSDKBarcodePicker;
import com.mirasense.scanditsdk.interfaces.ScanditSDKListener;

/**
 * @author samael
 */
public class ScanditActivity extends Activity implements ScanditSDKListener {

    private static final String TAG = ScanditActivity.class.getSimpleName();
    private static final String API_KEY = "** API KEY HERE **";
    public static final int REQUEST_CODE = 6518;

    private ScanditSDKBarcodePicker picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG, "starting " + TAG);

        picker = new ScanditSDKBarcodePicker(this, API_KEY);
        picker.setGS1DataBarExpandedEnabled(false); // disabling GS1
        picker.getOverlayView().addListener(this);

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
    public void didCancel() {
        picker.stopScanning();
        finish();
    }

    /**
     *  Called when a barcode has been decoded successfully.
     *
     *  @param barcode Scanned barcode content.
     *  @param symbology Scanned barcode symbology.
     */
    @Override
    public void didScanBarcode(String barcode, String symbology) {
        Log.v(TAG, String.format("Scandit didScanBarcode: '%s' '%s'", barcode, symbology));
        picker.stopScanning();

        Bundle conData = new Bundle();
        conData.putString("SCAN_RESULT", barcode);
        conData.putString("SCAN_RESULT_FORMAT", symbology);
        Intent intent = new Intent();
        intent.putExtras(conData);
        setResult(RESULT_OK, intent);
        
        finish();
    }

    @Override
    public void didManualSearch(String s) {
        // not using this
    }
}
