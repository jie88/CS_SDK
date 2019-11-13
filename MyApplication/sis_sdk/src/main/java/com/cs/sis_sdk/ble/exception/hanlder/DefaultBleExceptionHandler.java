package com.cs.sis_sdk.ble.exception.hanlder;

import android.content.Context;
import android.util.Log;

import com.cs.sis_sdk.ble.exception.ConnectException;
import com.cs.sis_sdk.ble.exception.GattException;
import com.cs.sis_sdk.ble.exception.InitiatedException;
import com.cs.sis_sdk.ble.exception.OtherException;
import com.cs.sis_sdk.ble.exception.TimeoutException;


public class DefaultBleExceptionHandler extends BleExceptionHandler {

    private static final String TAG = "BleExceptionHandler";
    private Context context;

    public DefaultBleExceptionHandler(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    protected void onConnectException(ConnectException e) {
        Log.e(TAG, e.getDescription());
    }

    @Override
    protected void onGattException(GattException e) {
        Log.e(TAG, e.getDescription());
    }

    @Override
    protected void onTimeoutException(TimeoutException e) {
        Log.e(TAG, e.getDescription());
    }

    @Override
    protected void onInitiatedException(InitiatedException e) {
        Log.e(TAG, e.getDescription());
    }

    @Override
    protected void onOtherException(OtherException e) {
        Log.e(TAG, e.getDescription());
    }
}
