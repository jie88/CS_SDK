package com.cs.sis_sdk.ble.conn;

import android.bluetooth.BluetoothGattCharacteristic;


public abstract class BleCharacterCallback extends BleCallback {
    public abstract void onSuccess(BluetoothGattCharacteristic characteristic);
}