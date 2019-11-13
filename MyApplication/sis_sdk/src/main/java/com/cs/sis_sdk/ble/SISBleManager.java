//package com.cs.sis_sdk.ble;
//
//
//import android.app.Application;
//import android.bluetooth.BluetoothGatt;
//
//import com.clj.fastble.BleManager;
//import com.clj.fastble.callback.BleGattCallback;
//import com.clj.fastble.callback.BleScanCallback;
//import com.clj.fastble.data.BleDevice;
//import com.clj.fastble.exception.BleException;
//import com.cs.sis_sdk.ble.callback.SISBleScanCallback;
//
//import java.util.List;
//
//public class SISBleManager {
//
//
//  /**
//   * 初始化
//   */
//  public void init(Application app) {
//    BleManager.getInstance().init(app);
//    BleManager.getInstance()
//        .enableLog(true)
//        .setReConnectCount(1, 5000)
//        .setOperateTimeout(5000);
//  }
//
//
//  /**
//   * 停止扫描
//   */
//  public void stopScan() {
//
//  }
//
//  public void startScan(SISBleScanCallback callback) {
//    if (BleManager.getInstance().isSupportBle()) {
//      //设备支持蓝牙
//      if (BleManager.getInstance().isBlueEnable()) {
//        //设备蓝牙已开启
//
//        scanBle(callback);
//
//      } else {
//        //设备蓝牙未开启
//      }
//    } else {
//      //设备不支持蓝牙
//    }
//  }
//
//
//  private void scanBle(final SISBleScanCallback callback) {
//    BleManager.getInstance().scan(new BleScanCallback() {
//      @Override
//      public void onScanStarted(boolean success) {
//        if (null != callback) {
//          callback.onScanStarted(success);
//        }
//      }
//
//      @Override
//      public void onLeScan(BleDevice bleDevice) {
//        if (null != callback) {
//          callback.onLeScan(bleDevice);
//        }
//      }
//
//      @Override
//      public void onScanning(BleDevice bleDevice) {
//        if (null != callback) {
//          callback.onScanning(bleDevice);
//        }
//      }
//
//      @Override
//      public void onScanFinished(List<BleDevice> scanResultList) {
//        if (null != callback) {
//          callback.onScanFinished(scanResultList);
//        }
//      }
//    });
//  }
//
//
//
//  public void connectBle(BleDevice device){
//    BleManager.getInstance().connect(device, new BleGattCallback() {
//      @Override
//      public void onStartConnect() {
//
//      }
//
//      @Override
//      public void onConnectFail(BleDevice bleDevice, BleException e) {
//
//      }
//
//      @Override
//      public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt bluetoothGatt, int i) {
//
//      }
//
//      @Override
//      public void onDisConnected(boolean b, BleDevice bleDevice, BluetoothGatt bluetoothGatt, int i) {
//
//      }
//    });
//  }
//
//
//
//  public static SISBleManager getInstance() {
//    return SISBleManager.BleManagerHolder.sBleManager;
//  }
//
//  private SISBleManager() {
//  }
//
//  private static class BleManagerHolder {
//    private static final SISBleManager sBleManager = new SISBleManager();
//
//    private BleManagerHolder() {
//    }
//  }
//}
