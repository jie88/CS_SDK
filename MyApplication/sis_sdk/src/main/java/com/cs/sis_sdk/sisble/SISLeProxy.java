package com.cs.sis_sdk.sisble;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import android.text.TextUtils;


import com.ble.api.DataUtil;
import com.ble.ble.BleCallBack;
import com.ble.ble.BleService;
import com.cs.sis_sdk.util.SISLogUtil;


@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.ECLAIR)
public class SISLeProxy {

  private BluetoothAdapter mBluetoothAdapter;
  public boolean mScanning = false;

  //蓝牙扫描时间
  public static int iBluetoothDelay = 5000;


  private Handler mHandler = new Handler();


  private BleService mBleService;

  //连接的蓝牙设备
  public BluetoothDevice linkDevice;


  private Context mContext;

  public SISLeProxy(Activity context) {
    mContext = context;
    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
  }


  private OnScanListener mOnScanListener;

  public void setOnScanListener(OnScanListener scanListener) {
    this.mOnScanListener = scanListener;
  }

  public interface OnScanListener {
    void onScanStart();

    void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord);

    void onScanStop();
  }

  public void scanLeDevice(boolean enable) {
    System.out.println("scanLeDevice enable = " + enable + "  " + mScanning);
    if (enable && !mScanning) {
      mScanning = true;
      mHandler.postDelayed(mStopScan, iBluetoothDelay);
      if (mOnScanListener != null)
        mOnScanListener.onScanStart();

      mBluetoothAdapter.startLeScan(mLeScanCallback);
      return;
    }
    if (!enable && mScanning) {
      mBluetoothAdapter.stopLeScan(mLeScanCallback);
      mHandler.removeCallbacks(mStopScan);
      mScanning = false;
      if (mOnScanListener != null)
        mOnScanListener.onScanStop();
    }
  }

  private final Runnable mStopScan = new Runnable() {

    @Override
    public void run() {
      scanLeDevice(false);
    }
  };

  private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
      if (mOnScanListener != null)
        mOnScanListener.onLeScan(device, rssi, scanRecord);
    }
  };


  public void initBleService(IBinder binder) {
    mBleService = ((BleService.LocalBinder) binder).getService(mBleCallBack);
    mBleService.initialize();// 这个必须有
    mBleService.setDecode(false);// 最初的模块数据都是加密的，所以SDK默认回去解密接收的数据，这里设置为不解密
  }

  private BleCallBack mBleCallBack = new BleCallBack() {
    @Override
    public void onConnected(String address) {
      // 这里只是建立连接，尚不能交互数据
      // broadcast(ACTION_GATT_CONNECTED, address);

      SISLogUtil.d("设备已连接" + address);
    }

    @Override
    public void onConnectTimeout(String address) {
      // 连接超时，开启断线重连不会走这个回调方法
      SISLogUtil.d("连接超时" + address);
//      broadcast(ACTION_CONNECT_TIMEOUT, address);
//      log("连接超时" + address);
//      if(MyApplication.iReConnect < 3) {
//        connect();
//      }
    }

    @Override
    public void onServicesDiscovered(final String address) {
      SISLogUtil.d("拿到服务" + address);

      // 拿到服务，可进行数据通讯，不过有些手机还需要延时，不然会断线
      mHandler.postDelayed(new Runnable() {
        @Override
        public void run() {
          boolean ok = mBleService.enableNotification(address);// 开启0x1002的通知【接收数据】
          SISLogUtil.d("开启通知" + ok);
        }
      }, 300);

      boolean bR = requestMtu(248);
      if (bR) {
        bRealConnect = true;
      } else {
        bRealConnect = false;
      }
//			System.out.println("requestMtu  bR = " + bR);
    }

    @Override
    public void onCharacteristicWrite(String address, BluetoothGattCharacteristic characteristic, int status) {

      SISLogUtil.d("写数据" + address);
      /*
       * if (BluetoothGatt.GATT_SUCCESS == status) { }
       */
//			boolean bR = (sData.equals(DataUtil.byteArrayToHex(characteristic.getValue())));
      SISLogUtil.d("onCharacteristicWrite  ->" + DataUtil.byteArrayToHex(characteristic.getValue()));
    }

    @Override
    public void onDisconnected(String address) {
      SISLogUtil.d("设备已断开" + address);
      // 断线
//      broadcast(ACTION_GATT_DISCONNECTED, address);
//
//      MyApplication.instrument_isconnect = false;
//      BT_CommunicationThread.BTDevStatus = Common_Constant.STATE_INITING;
//      BT_ConnectThread.getInstance().cancelBTSocket();
//      log("LeP 设备已断开" + address);
    }

    @Override
    public void onCharacteristicChanged(String address, BluetoothGattCharacteristic characteristic) {
// 接收到数据
      SISLogUtil.d("接收数据 <- " + DataUtil.byteArrayToHex(characteristic.getValue()));
//
    }

    @Override
    public void onMtuChanged(String address, int mtu, int status) {
//            if (status == BluetoothGatt.GATT_SUCCESS) {
      SISLogUtil.d("onMtuChanged() - " + address + "  status = " + status + ", MTU has been " + mtu);
//            } else {
//                Log.e(TAG, "onMtuChanged() - " + address + ", MTU request failed: " + status);
//            }
//      Intent intent = new Intent(ACTION_MTU_CHANGED);
//      intent.putExtra(EXTRA_ADDRESS, address);
//      intent.putExtra(EXTRA_MTU, mtu);
//      intent.putExtra(EXTRA_STATUS, status);
//      LocalBroadcastManager.getInstance(mBleService).sendBroadcast(intent);
    }
  };


  /**
   * 请求更新MTU，会触发onMtuChanged()回调，如果请求成功，则APP一次最多可以发送MTU-3字节的数据， 如默认MTU为23，APP一次最多可以发送20字节的数据
   * <p>
   * 注：更新MTU要求手机系统版本不低于Android5.0
   */
  public boolean requestMtu(int mtu) {
    return mBleService.requestMtu(linkDevice.getAddress(), mtu);
  }


  public boolean bRealConnect = false;

  // 连接设备
  public boolean connect(BluetoothDevice bleDevice) {
    linkDevice = bleDevice;
    bRealConnect = false;
    if (!TextUtils.isEmpty(linkDevice.getAddress())) {
      SISLogUtil.d("connect() = " + linkDevice.getAddress());
      return mBleService.connect(bleDevice.getAddress(), false);// boolean型参数代表是否开启断线重连
    } else {
      return false;
    }
  }


  public boolean isSupportBle() {
    return Build.VERSION.SDK_INT >= 18 && mContext.getApplicationContext().getPackageManager().hasSystemFeature("android.hardware.bluetooth_le");
  }

  public void enableBluetooth() {
    if (this.mBluetoothAdapter != null) {
      this.mBluetoothAdapter.enable();
    }

  }

  public void disableBluetooth() {
    if (this.mBluetoothAdapter != null && this.mBluetoothAdapter.isEnabled()) {
      this.mBluetoothAdapter.disable();
    }

  }


  public boolean isBlueEnable() {
    return this.mBluetoothAdapter != null && this.mBluetoothAdapter.isEnabled();
  }

}
