//package com.cs.sis_sdk.ble.callback;
//
//
//import com.clj.fastble.callback.BleScanCallback;
//import com.clj.fastble.data.BleDevice;
//import com.cs.sis_sdk.util.SISLogUtil;
//
//import java.util.List;
//
//
//
//public abstract class SISBleScanCallback extends BleScanCallback {
//  public SISBleScanCallback() {
//  }
//
//  @Override
//  public void onScanning(BleDevice bleDevice) {
//
//  }
//
//  @Override
//  public void onScanStarted(boolean b) {
//    SISLogUtil.d("onScanStarted " + b);
//  }
//
//  @Override
//  public void onScanFinished(List<BleDevice> list) {
//    SISLogUtil.d("onScanFinished " + list.size());
//  }
//}
