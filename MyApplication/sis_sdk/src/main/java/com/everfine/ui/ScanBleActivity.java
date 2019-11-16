package com.everfine.ui;


import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import android.widget.ListView;

import com.cs.sis_sdk.R;

import com.everfine.SISSdkController;
import com.everfine.ble.LeProxy;
import com.everfine.ui.base.SISQuickAdapter;
import com.everfine.ui.base.ViewHolder;
import com.everfine.util.SISLogUtil;

import java.util.ArrayList;
import java.util.List;


public class ScanBleActivity extends Activity {

  private ListView listView;
  private Context mContext;


  protected SISQuickAdapter<BluetoothDevice> bleAdapter;
  protected List<BluetoothDevice> mData = new ArrayList<>();

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mContext = this;
    setContentView(R.layout.activity_scan_ble);
    initView();
  }

  private void initView() {

    initBle();
    initBleDialogAdapter();
    startScanBle();
  }

  /**
   * 初始化蓝牙adapter
   */
  private void initBleDialogAdapter() {
    bleAdapter = new SISQuickAdapter<BluetoothDevice>(mContext, mData, R.layout.view_item_ble) {
      @Override
      public void convert(ViewHolder helper, final BluetoothDevice item, final int position) {
        String name = item.getName();

        helper.setText(R.id.bleName,  item.getName());

        if (TextUtils.isEmpty(name)) {
          helper.setText(R.id.bleName, item.getName());
        } else {

        }
        helper.getConvertView().setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            SISSdkController.getInstance().connectDevice(item);
            finish();
          }
        });

      }
    };
    listView = (ListView) findViewById(R.id.listView);
    listView.setAdapter(bleAdapter);

  }


  /**
   * 初始化蓝牙
   */
  private void initBle() {

//    //绑定服务
//    bindService(new Intent(this, BleService.class), mConn, BIND_AUTO_CREATE);

    //添加蓝牙扫描回调
    SISSdkController.getInstance().setOnScanListener(new LeProxy.OnScanListener() {
      @Override
      public void onScanStart() {

      }

      @Override
      public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        //扫描到蓝牙设备添加到列表
        addBleDevice(device);
        SISLogUtil.d("onScanning " + device.getName());
      }

      @Override
      public void onScanStop() {

      }
    });
  }

  /**
   * 开始扫描蓝牙
   */
  private void startScanBle() {
    SISSdkController.getInstance().scanLeDevice(true);
  }

  /**
   * 停止扫描蓝牙
   */
  private void stopScanBle() {
    SISSdkController.getInstance().scanLeDevice(false);
  }

  /**
   * 将扫描到的蓝牙设备添加到列表
   */
  private synchronized void addBleDevice(final BluetoothDevice bleDevice) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        //判断改蓝牙设备是否已经在列表中，不在则添加
        boolean exit = false;
        for (BluetoothDevice device : mData) {
          if (device.getAddress().equalsIgnoreCase(bleDevice.getAddress())) {
            exit = true;
            break;
          }
        }
        if (!exit) {
          mData.add(bleDevice);
          bleAdapter.notifyDataSetChanged();
        }
      }
    });

  }

  public static Intent creatIntent(Context context) {
    Intent intent = new Intent(context, ScanBleActivity.class);
    return intent;
  }
}
