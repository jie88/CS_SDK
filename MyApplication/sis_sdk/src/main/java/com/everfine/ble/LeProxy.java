package com.everfine.ble;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;


import com.ble.api.DataUtil;
import com.ble.ble.BleCallBack;
import com.ble.ble.BleService;
import com.everfine.SISSdkController;
import com.everfine.util.SISLogUtil;

import java.util.ArrayList;
import com.everfine.core.Tool;

@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.ECLAIR)
public class LeProxy {

  private BluetoothAdapter mBluetoothAdapter;
  public boolean mScanning = false;
  //蓝牙扫描时间
  public static int iBluetoothDelay = 5000;
  private Handler mHandler = new Handler();
  private BleService mBleService;
  //连接的蓝牙设备
  public  static BluetoothDevice linkDevice;
  private Context mContext;
  public static boolean bRealConnect = false;
  public LeProxy(Activity context) {
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
      SISSdkController.getInstance().showToast("连接超时");
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

      broadcast(address, characteristic.getValue());

      log("接收数据 <- " + DataUtil.byteArrayToHex(characteristic.getValue()));
      byte[] bData = characteristic.getValue();
      int iBufferNum = characteristic.getValue().length;

      if(bData[0] == 0x7B && bData[1] == 0x7B){
        iNum = 0;
      }
      for(int i = 0;i<iBufferNum;i++){
        byBuffer[iNum++] = bData[i];
      }
      if(iNum > 4 && byBuffer[4] == SPIC_Command.CMD_READ_FLASH){
        //   MyApplication.readFlashProcess = 100*iNum/MyApplication.readFlashNum;
      }

      int iFindHeadPos = 0;
      if (byBuffer[iNum - 2] == 0x7D
              && byBuffer[iNum - 1] == 0x7D){
//				log("接收数据  ----iNum = " + iNum + " == " + bytesToHexString(byBuffer,iNum));
        for (int i = iNum - 3; i >= 1; i--) {
          if (byBuffer[i - 1] == 0x7B && byBuffer[i] == 0x7B) {
            iFindHeadPos = i - 1;
            break;
          }
        }
        System.out.println("iFindHeadPos = " + iFindHeadPos + "  iNum = " + iNum);

        byte cmd = byBuffer[iFindHeadPos + 4];
        ReadMeterSocketOneData oneData = new ReadMeterSocketOneData(
                iFindHeadPos, byBuffer, iNum - iFindHeadPos);

        if (oneData.isDataOk()) {
          Log.d(TAG, "oneData is ok! CMD is " + Integer.toHexString(cmd&0xff));
          if (mListSocketOneData.size() >= 10)
            mListSocketOneData.remove(0);
          mListSocketOneData.add(oneData);
          Log.d(TAG, "oneData is ok! CMD is " +  Tool.byteArrayToAsciiString(mListSocketOneData.get(0).para));
        }
        else {
          mListSocketOneData.add(oneData);
          Log.d(TAG, "oneData is ERROR!");
        }
        iNum = 0;
      }
    }
    public byte[] byBuffer = new byte[4194304];
    public int iNum = 0;

    public ArrayList<ReadMeterSocketOneData> mListSocketOneData = new ArrayList<ReadMeterSocketOneData>();

    private final static String TAG = "LeProxy";
    private void log(String msg) {
      Log.i("LeProxy", "" + msg);
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

  String sData = "";
  // 发送数据
  public boolean send(byte[] data, int iLength) {
    sData = DataUtil.byteArrayToHex(data);
    System.out.println("LeProxy send sData = " + sData);
    if (mBleService != null) {
      return mBleService.send(linkDevice.getAddress(), data, false);
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
  private void broadcast(String action, String address) {
    Intent intent = new Intent(action);
    intent.putExtra(EXTRA_ADDRESS, address);
    LocalBroadcastManager.getInstance(mBleService).sendBroadcast(intent);
  }

  private void broadcast(String address, byte[] data) {
    Intent intent = new Intent(ACTION_DATA_AVAILABLE);
    intent.putExtra(EXTRA_ADDRESS, address);
    intent.putExtra(EXTRA_DATA, data);
    LocalBroadcastManager.getInstance(mBleService).sendBroadcast(intent);
  }

  public static final String ACTION_CONNECT_TIMEOUT = ".LeProxy.ACTION_CONNECT_TIMEOUT";
  public static final String ACTION_CONNECT_ERROR = ".LeProxy.ACTION_CONNECT_ERROR";
  public static final String ACTION_GATT_CONNECTED = ".LeProxy.ACTION_GATT_CONNECTED";
  public static final String ACTION_GATT_DISCONNECTED = ".LeProxy.ACTION_GATT_DISCONNECTED";
  public static final String ACTION_GATT_SERVICES_DISCOVERED = ".LeProxy.ACTION_GATT_SERVICES_DISCOVERED";
  public static final String ACTION_DATA_AVAILABLE = ".LeProxy.ACTION_DATA_AVAILABLE";

  public static final String EXTRA_ADDRESS = ".LeProxy.EXTRA_ADDRESS";
  public static final String EXTRA_DATA = ".LeProxy.EXTRA_DATA";

  public static final String ACTION_MTU_CHANGED = ".LeProxy.ACTION_MTU_CHANGED";

  public static final String EXTRA_UUID = ".LeProxy.EXTRA_UUID";
  public static final String EXTRA_RSSI = ".LeProxy.EXTRA_RSSI";
  public static final String EXTRA_MTU = ".LeProxy.EXTRA_MTU";
  public static final String EXTRA_STATUS = ".LeProxy.EXTRA_STATUS";

}
