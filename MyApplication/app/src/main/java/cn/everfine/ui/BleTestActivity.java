package cn.everfine.ui;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.cs.sis_sdk.ble.SISBleManager;
import com.cs.sis_sdk.ble.callback.SISBleScanCallback;
import com.cs.sis_sdk.util.SISLogUtil;
import com.example.administrator.myapplication.R;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import java.util.ArrayList;
import java.util.List;

import cn.everfine.base.BaseActivity;
import cn.everfine.base.SISQuickAdapter;
import cn.everfine.base.ViewHolder;
import cn.everfine.util.DialogUtil;

/**
 * author : ${CHENJIE} created at  2019-11-14 12:34 e_mail : chenjie_goodboy@163.com describle :
 */
public class BleTestActivity extends BaseActivity {

  protected SISQuickAdapter<BleDevice> bleAdapter;
  protected List<BleDevice> mData = new ArrayList<>();

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ble_test);
    initBleDialogAdapter();
    initBle();

  }

  private void initView() {

  }


  /**
   * 初始化蓝牙adapter
   */
  private void initBleDialogAdapter() {
    bleAdapter = new SISQuickAdapter<BleDevice>(mContext, mData, R.layout.view_item_ble) {
      @Override
      public void convert(ViewHolder helper, BleDevice item, final int position) {
        String name = item.getName();
        if (TextUtils.isEmpty(name)) {
          helper.setText(R.id.bleName, " mac: " + item.getMac());
        } else {
          helper.setText(R.id.bleName, " mac: " + item.getMac());
        }


      }
    };
  }

  /**
   * 弹出蓝牙dialog
   */
  private void showBleDialog() {
    DialogUtil.showBleDialog(mContext, bleAdapter, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        SISBleManager.getInstance().connectBle(mData.get(which));

        dialog.dismiss();
      }
    });
  }

  /**
   * 初始化蓝牙
   */
  private void initBle() {
    SISBleManager.getInstance().init(getApplication());
    BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
//        .setServiceUuids(serviceUuids)      // 只扫描指定的服务的设备，可选
//        .setDeviceName(true, names)   // 只扫描指定广播名的设备，可选
        //.setDeviceMac("00:1A:95:93:03:85")                  // 只扫描指定mac的设备，可选
        .setAutoConnect(true)      // 连接时的autoConnect参数，可选，默认false
        .setScanTimeOut(10000)              // 扫描超时时间，可选，默认10秒
        .build();
    BleManager.getInstance().initScanRule(scanRuleConfig);
  }


  /**
   * 将扫描到的蓝牙设备添加到列表
   */
  private synchronized void addBleDevice(final BleDevice bleDevice) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        //判断改蓝牙设备是否已经在列表中，不在则添加
        boolean exit = false;
        for (BleDevice device : mData) {
          if (device.getMac().equalsIgnoreCase(bleDevice.getMac())) {
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


  public void doSearch() {
    mData.clear();
    showBleDialog();
    SISBleManager.getInstance().startScan(new SISBleScanCallback() {
      @Override
      public void onScanning(BleDevice bleDevice) {

        addBleDevice(bleDevice);

//        if ("00:1A:95:93:03:85".equalsIgnoreCase(bleDevice.getMac())) {
//          SISBleManager.getInstance().connectBle(bleDevice);
//          //SISBleManager.getInstance().stopScan();
//        }
        SISLogUtil.d("mac " + bleDevice.getMac());
        super.onScanning(bleDevice);
      }
    });

  }

  public void search(View view) {
    AndPermission.with(mContext)
        .permission("android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION", "android.permission.BLUETOOTH", "android.permission.BLUETOOTH_ADMIN")
        .onGranted(new Action() {
          @Override
          public void onAction(List<String> permissions) {
            // 权限获取成功
            doSearch();
          }
        }).onDenied(new Action() {
      @Override
      public void onAction(List<String> permissions) {
        // 权限被拒绝
        //denied.onAction(permissions);

      }
    }).start();


  }


  private void requestP() {

  }

}
