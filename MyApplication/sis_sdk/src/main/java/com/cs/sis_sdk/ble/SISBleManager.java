package com.cs.sis_sdk.ble;


import android.app.Application;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.os.Handler;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleIndicateCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.cs.sis_sdk.ble.callback.SISBleScanCallback;
import com.cs.sis_sdk.util.HexUtil;
import com.cs.sis_sdk.util.SISLogUtil;
import com.cs.sis_sdk.util.TimeUtil;

import java.util.List;

public class SISBleManager {


  private static final String UUID_SERVICE_BATTERY = "0000180f-0000-1000-8000-00805f9b34fb";
  private static final String UUID_BATTERY_LEVEL = "00002a19-0000-1000-8000-00805f9b34fb";

  //
  private static final String UUID_SERVICE_INFO = "00001c00-d102-11e1-9b23-00025b00a5a5";
  private static final String UUID_Charac_INFO = "00001c01-d103-11e1-9b23-00025b00a5a5";


  private Context mContext;

  private boolean deviceConnected = false;


  Handler handler = new Handler();

  private BleDevice linkedDevice; //当前链接的设备



  /**
   * 初始化
   */
  public void init(Application app) {
    mContext=app.getApplicationContext();

    BleManager.getInstance().init(app);
    BleManager.getInstance()
        .enableLog(true)
        .setReConnectCount(1, 5000)
        .setOperateTimeout(5000);
  }


  /**
   * 停止扫描
   */
  public void stopScan() {
    BleManager.getInstance().cancelScan();
  }

  public void startScan(SISBleScanCallback callback) {
    if (BleManager.getInstance().isSupportBle()) {
      //设备支持蓝牙
      if (BleManager.getInstance().isBlueEnable()) {
        //设备蓝牙已开启

        scanBle(callback);

      } else {
        //设备蓝牙未开启
      }
    } else {
      //设备不支持蓝牙
    }
  }


  private void scanBle(final SISBleScanCallback callback) {
    BleManager.getInstance().scan(new BleScanCallback() {
      @Override
      public void onScanStarted(boolean success) {
        if (null != callback) {
          callback.onScanStarted(success);
        }
      }

      @Override
      public void onLeScan(BleDevice bleDevice) {
        if (null != callback) {
          callback.onLeScan(bleDevice);
        }
      }

      @Override
      public void onScanning(BleDevice bleDevice) {
        if (null != callback) {
          callback.onScanning(bleDevice);
        }
      }

      @Override
      public void onScanFinished(List<BleDevice> scanResultList) {
        if (null != callback) {
          callback.onScanFinished(scanResultList);
        }
      }
    });
  }


  public void connectBle(BleDevice device) {
    if(deviceConnected){
      return;
    }
    stopScan();
    linkedDevice = device;
    BleManager.getInstance().connect(device, new BleGattCallback() {
      @Override
      public void onStartConnect() {
        SISLogUtil.d("connectBle onStartConnect  开始连接");
        deviceConnected = false;
      }

      @Override
      public void onConnectFail(BleDevice bleDevice, BleException e) {
        SISLogUtil.d("connectBle onConnectFail  连接失败");
        deviceConnected = false;
      }

      @Override
      public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt bluetoothGatt, int i) {
        if (null != bluetoothGatt) {
          bluetoothGatt.discoverServices();
        }
        deviceConnected = true;
        SISLogUtil.d("connectBle onConnectSuccess  连接成功");
      }

      @Override
      public void onDisConnected(boolean b, BleDevice bleDevice, BluetoothGatt bluetoothGatt, int i) {
        SISLogUtil.d("connectBle onDisConnected  断开连接");
        deviceConnected = false;
      }


      @Override
      public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        SISLogUtil.d("connectBle onServicesDiscovered  发现服务");

        handler.post(new Runnable() {
          @Override
          public void run() {
            listen_notify(UUID_SERVICE_BATTERY, UUID_BATTERY_LEVEL);
            listen_notify(UUID_SERVICE_INFO, UUID_Charac_INFO);
            //listen_notify(UUID_SERVICE_INFO, UUID_HEART_RATE_MEASUREMENT);
            handler.postDelayed(sendDada, 2000);

          }
        });

      }

      @Override
      public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);

        byte[] data = characteristic.getValue();
        String strData = HexUtil.encodeHexStr(data);

        if (data != null && data.length > 0) {
          if (data[0] == 0x36){
            double pressure = Integer.parseInt(strData.substring(9), 16) / 1000.0;
            SISLogUtil.i("特征变化 " + "压强:" + pressure);
          }else if (data[0] == 0x37){

            double shidu = Integer.parseInt(strData.substring(8, 12), 16) / 10.0;
            SISLogUtil.i("特征变化 " + "湿度:" + shidu);
          }else if (data[0] == 0x38) {

            int a = Integer.parseInt(strData.substring(8, 12), 16);
            if (a > 1000) {
              a = a - 65536;
            }
            double wendu = a / 10.0;
            SISLogUtil.i("特征变化 " + "温度:" + wendu);
          }
        }

        SISLogUtil.i("特征变化 " + "data:" + strData);

      }

      @Override
      public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);

        byte[] data = characteristic.getValue();
        String strData = HexUtil.encodeHexStr(data);
        //int getbattery = getBattery(strData);
        SISLogUtil.i("读特征 " + "data:" + strData);


      }
    });
  }


  Runnable sendDada = new Runnable() {
    @Override
    public void run() {
      try {
        if (deviceConnected) {
          sendData();
          handler.postDelayed(sendDada, 6000);
        }
      } catch (Exception e) {
      }
    }
  };
  private void sendData() {
    final byte[] loop_data = {0x01, 0x07};
    SISLogUtil.d("发送数据");
    write(UUID_SERVICE_INFO, UUID_Charac_INFO, loop_data);

    TimeUtil timeUtil = new TimeUtil(mContext);

    write(UUID_SERVICE_INFO, UUID_Charac_INFO, timeUtil.gettime());
    write(UUID_SERVICE_INFO, UUID_Charac_INFO, timeUtil.getDate());
    readData();
  }
  private void readData() {

    //bleManager.getBleBluetooth().getBluetoothGatt().readCharacteristic(bleManager.getBleBluetooth().newBleConnector().getCharacteristic());
    read(UUID_SERVICE_INFO, UUID_Charac_INFO);
    // read
  }


  /**
   * write
   */
  private void write(String uuid_service,
                     String uuid_characteristic_write,
                     byte[] data) {
    if (null == linkedDevice) {
      return ;
    }
    BleManager.getInstance().write(
        linkedDevice,
        uuid_service,
        uuid_characteristic_write,
        data,
        new BleWriteCallback() {
          @Override
          public void onWriteSuccess(int current, int total, byte[] justWrite) {
            // 发送数据到设备成功（分包发送的情况下，可以通过方法中返回的参数可以查看发送进度）
          }

          @Override
          public void onWriteFailure(BleException exception) {
            // 发送数据到设备失败
          }
        });
  }

  /**
   * read
   */
  private void read(String uuid_service,
                    String uuid_characteristic_read) {


    if (null == linkedDevice) {
      return ;
    }
    BleManager.getInstance().read(
        linkedDevice,
        uuid_service,
        uuid_characteristic_read,
        new BleReadCallback() {
          @Override
          public void onReadSuccess(byte[] data) {
            // 读特征值数据成功
          }

          @Override
          public void onReadFailure(BleException exception) {
            // 读特征值数据失败
          }
        });
  }


  /**
   * indicate
   */
  private void listen_indicate(String uuid_service, String uuid_characteristic_indicate) {
    if (null == linkedDevice) {
      return ;
    }

    BleManager.getInstance().indicate(
        linkedDevice,
        uuid_service,
        uuid_characteristic_indicate,
        new BleIndicateCallback() {
          @Override
          public void onIndicateSuccess() {
            // 打开通知操作成功
          }

          @Override
          public void onIndicateFailure(BleException exception) {
            // 打开通知操作失败
          }

          @Override
          public void onCharacteristicChanged(byte[] data) {
            // 打开通知后，设备发过来的数据将在这里出现
          }
        });
  }

  /**
   * stop indicate
   */
  private boolean stop_indicate(String uuid_service, String uuid_characteristic_indicate) {

    if (null == linkedDevice) {
      return false;
    }
    return BleManager.getInstance().stopIndicate(linkedDevice,uuid_service, uuid_characteristic_indicate);
  }



  /**
   * open notify
   */
  private void listen_notify(String uuid_service, String uuid_characteristic_notify) {

    if (null == linkedDevice) {
      return;
    }
    BleManager.getInstance().notify(
        linkedDevice,
        uuid_service,
        uuid_characteristic_notify,
        new BleNotifyCallback() {
          @Override
          public void onNotifySuccess() {
            // 打开通知操作成功
          }

          @Override
          public void onNotifyFailure(BleException exception) {
            // 打开通知操作失败
          }

          @Override
          public void onCharacteristicChanged(byte[] data) {
            // 打开通知后，设备发过来的数据将在这里出现
          }
        });
  }

  /**
   * stop notify
   */
  private boolean stop_notify(String uuid_service, String uuid_characteristic_notify) {
    if (null == linkedDevice) {
      return false;
    }

    return BleManager.getInstance().stopNotify(linkedDevice, uuid_service, uuid_characteristic_notify);


  }

  public static SISBleManager getInstance() {
    return SISBleManager.BleManagerHolder.sBleManager;
  }

  private SISBleManager() {
  }

  private static class BleManagerHolder {
    private static final SISBleManager sBleManager = new SISBleManager();

    private BleManagerHolder() {
    }
  }
}
