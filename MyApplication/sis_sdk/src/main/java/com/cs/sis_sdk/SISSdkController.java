package com.cs.sis_sdk;


import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;

import com.cs.sis_sdk.sisble.SISLeProxy;
import com.cs.sis_sdk.ui.ScanBleActivity;


/**
 * author : ${CHENJIE} created at  2019-11-07 23:13 e_mail : chenjie_goodboy@163.com describle :
 */
public class SISSdkController {

  private Activity mActivity;
  private SISLeProxy mLeProxy;


  public void init(Activity activity) {
    mActivity = activity;
    mLeProxy = new SISLeProxy(mActivity);
  }

  public void scanLeDevice(boolean enable) {
    mLeProxy.scanLeDevice(enable);
  }
  public void setOnScanListener(SISLeProxy.OnScanListener scanListener) {
    mLeProxy.setOnScanListener(scanListener);
  }

  public void startScanBle(Activity mActivity) {
    if (mLeProxy.mScanning) {
      //正在扫描中
      return;
    }
    if (mLeProxy.isSupportBle()) {
      //设备支持蓝牙
      if (mLeProxy.isBlueEnable()) {
        //设备蓝牙已开启
        mActivity.startActivity(ScanBleActivity.creatIntent(mActivity));
      } else {
        //设备蓝牙未开启
      }
    } else {
      //设备不支持蓝牙
    }
  }


  public void connectDevice(BluetoothDevice device) {

    // 开始处理链接
    mLeProxy.connect(device);
  }


  public void disconnected(){
   //处理断开连接
  }

  private static class SingletonHolder {
    private static final SISSdkController INSTANCE = new SISSdkController();
  }

  private SISSdkController() {
  }

  public static final SISSdkController getInstance() {
    return SingletonHolder.INSTANCE;
  }


}
