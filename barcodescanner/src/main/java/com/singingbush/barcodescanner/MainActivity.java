package com.singingbush.barcodescanner;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG, "starting " + TAG);

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            Log.d(TAG, "scan result: " + scanResult);
        } else {
            Log.d(TAG, "scan result was NULL");
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            Button scanButton = (Button)rootView.findViewById(R.id.scan_button);
            scanButton.setOnClickListener(this);

            Button aboutButton = (Button)rootView.findViewById(R.id.about_button);
            aboutButton.setOnClickListener(this);

            Button exitButton = (Button)rootView.findViewById(R.id.exit_button);
            exitButton.setOnClickListener(this);

            return rootView;
        }

        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.scan_button:
                    Log.v(TAG, "scan barcode button clicked");
                    scanBarcode();
                    break;
                case R.id.about_button:
                    Log.v(TAG, "about button clicked");
                    Intent intent = new Intent(getActivity(), AboutActivity.class);
                    getActivity().startActivity(intent);
                    break;
                case R.id.exit_button:
                    Log.v(TAG, "exit button clicked");
                    getActivity().finish();
                    break;
            }
        }

        private void scanBarcode() {
            IntentIntegrator integrator = new IntentIntegrator(getActivity());
            // possible barcode types are:
            // "UPC_A", "UPC_E", "EAN_8", "EAN_13", "CODE_39",
            // "CODE_93", "CODE_128", "ITF", "RSS_14", "RSS_EXPANDED"
            Collection<String> BARCODE_TYPES =
                    Collections.unmodifiableCollection(Arrays.asList("UPC_A", "UPC_E", "EAN_8", "EAN_13"));
            integrator.initiateScan(BARCODE_TYPES);
        }

    }

}
