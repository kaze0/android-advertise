package com.mikedg.wearble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.UUID;


public class MainActivity extends Activity {

    private static final String TAG = "WearBLE";

    private BluetoothLeAdvertiser mBluetoothAdvertiser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupBLE();
//        setupSensors();
    }
    private static final ParcelUuid URI_STOLEN = ParcelUuid.fromString("0000FED8-0000-1000-8000-00805F9B34FB"); //Grabbed this from a random Github project since it seemed to indicate that it was a working URI

    /**
     * Advertising is a bitch. Many devices don't support it for a variety of reasons.
     * Some details into that here: https://code.google.com/p/android-developer-preview/issues/detail?id=1570
     * List of known advertising supported device, http://altbeacon.github.io/android-beacon-library/beacon-transmitter.html
     * Quick test, try as beacon function, not sure if it works on wear (probably not too well if it does) https://play.google.com/store/apps/details?id=com.radiusnetworks.locate
     */
    private void setupBLE() {
        //FIXME: no checks to make sure bluetooth is enabled, this will crash if it's not
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();

        Log.d(TAG, "isMultipleAdvertisementSupported: " + bluetoothAdapter.isMultipleAdvertisementSupported());
        ((TextView)findViewById(R.id.textView1)).setText("" + bluetoothAdapter.isMultipleAdvertisementSupported());
        AdvertiseSettings.Builder settingsBuilder = new AdvertiseSettings.Builder();
        //FIXME: set settings
        AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder();
        //FIXME: create data
        byte[] manufacturerSpecificData = new byte[9];
        byte[] servicedata = new byte[10]; //No particular reason for sizes
        ParcelUuid serviceDataUuid = new ParcelUuid(UUID.randomUUID()); //FIXME: not sure random is going to make debugging these easy

        dataBuilder.addManufacturerData(1, manufacturerSpecificData);
        /**
         * addServiceData and addServiceUuid seem to cause startAdvertising to fail with errorCode = 1
         * This is on a Nexus 9
         *
         * Originally suspected a bad URI might have been the problem, but that didn't seem to be the case after switching to URI_STOLEN
         */
//        dataBuilder.addServiceData(serviceDataUuid, servicedata); //Cause fail with 1
//        dataBuilder.addServiceData(serviceDataUuid, new byte[1]); //Even one byte is a fail, wtf?, bad UUID? gotta be that because this and the add service UUID?
//        dataBuilder.addServiceData(URI_STOLEN, new byte[1]);  //stil bad, so maybe it's not the UUID or the error code is too cryptic

//        dataBuilder.addServiceUuid(new ParcelUuid(UUID.randomUUID())); //FIXME: random bad again?, also causes fail with 1
        //***************

        dataBuilder.setIncludeDeviceName(true);
        dataBuilder.setIncludeTxPowerLevel(true);
        mBluetoothAdvertiser.startAdvertising(settingsBuilder.build(), dataBuilder.build(), new AdvertiseCallback() { //FIXME: NPE if bluetooth disabled
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
                Log.d(TAG, "onStartSuccess");
                ((TextView)findViewById(R.id.textView2)).setText("Successfully advertising");
            }

            @Override
            public void onStartFailure(int errorCode) {
                /**
                 * Pretty sure we are getting the following back if doing addServiceData or addServiceUUID

                 * Failed to start advertising as the advertise data to be broadcasted is larger than 31 bytes.
                    public static final int ADVERTISE_FAILED_DATA_TOO_LARGE = 1;

                */
                super.onStartFailure(errorCode);
                ((TextView)findViewById(R.id.textView2)).setText("Failed to start advertising: " + errorCode);

                Log.d(TAG, "onStartFailure: " + errorCode);
            }
        });
    }

    /**
     * Sensor related setup, just logging for now
     */
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
//    public void setupSensors() {
//        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//
//        mSensorManager.registerListener(new SensorEventListener() {
//            @Override
//            public void onSensorChanged(SensorEvent event) {
//                Log.d(TAG, "onSensorChanged");
//            }
//
//            @Override
//            public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//            }
//        }, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
//
//    }
}
