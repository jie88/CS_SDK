package com.everfine.sis_sdk.ble;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
//import cn.everfine.common.Common_Constant;
//import cn.everfine.core.Tool;
//import cn.everfine.lowerServer.BT_CommunicationThread;
//import cn.everfine.lowerServer.BT_ConnectThread;
//import cn.everfine.lowerServer.ReadMeterSocketOneData;
//import cn.everfine.lowerServer.SPIC_Command;

import com.ble.api.DataUtil;
import com.ble.ble.BleCallBack;
import com.ble.ble.BleService;
import com.everfine.sis_sdk.common.Tool;
//import com.everfine.sis_20.application.MyApplication;
//import com.everfine.sis_20.ui.MainActivity;

@SuppressLint("NewApi") @TargetApi(Build.VERSION_CODES.ECLAIR) public class LeProxy {
    // 各蓝牙事件的广播action
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

    public static  int iBluetoothDelay=2000;
    public static boolean instrument_isconnect=false;

    private static LeProxy mInstance;
    private BleService mBleService;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler = new Handler();
    private boolean mScanning = false;

    private LeProxy() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public BleService getDevice(){
        return mBleService;
    }

    public static LeProxy getInstance() {
        if (mInstance == null)
            mInstance = new LeProxy();
        return mInstance;
    }

    private OnScanListener mOnScanListener;

    public void setOnScanListener(OnScanListener scanListener) {
        this.mOnScanListener = scanListener;
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

    public interface OnScanListener {
        void onScanStart();

        void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord);

        void onScanStop();
    }

    private LeScanCallback mLeScanCallback = new LeScanCallback() {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (mOnScanListener != null)
                mOnScanListener.onLeScan(device, rssi, scanRecord);
        }
    };

    public void initBleService(IBinder binder) {
        mBleService = ((BleService.LocalBinder) binder).getService(mBleCallBack);
        System.out.println("initBleService(IBinder binder) "  );
        if(mBleService==null)
        {
            System.out.println("initBleService   mBleService==null "  );
        }
        mBleService.initialize();// 这个必须有
        mBleService.setDecode(false);// 最初的模块数据都是加密的，所以SDK默认回去解密接收的数据，这里设置为不解密
    }

    public BluetoothDevice getConnectedDevice() {
        List<BluetoothDevice> devices = mBleService.getConnectedDevices();
        if (devices.size() > 0)
            return devices.get(0);// Demo的写法只能连接一个设备，所以直接取第一个设备了
        return null;
    }

    // 连接设备
    public boolean connect() {
        bRealConnect = false;
        if(SisSdk.getInstance().bleAddress != null && !SisSdk.getInstance().bleAddress.equals("")){
            System.out.println("connect() = " + SisSdk.getInstance().bleAddress);
            //System.out.println("connect() = " + SisSdk.strBleAddress );
            if(mBleService==null)
            {
                System.out.println("mBleService==null = "  );
            }

            return mBleService.connect(SisSdk.getInstance().bleAddress, false);// boolean型参数代表是否开启断线重连
        }else{
            return false;
        }
    }

    // 断开连接
    public void disconnect(String address) {
        System.out.println("LeProxy disconnect address = " + address);
        instrument_isconnect = false;
        if(address != null && !address.equals("")){
            mBleService.disconnect(address);

        }
    }

    public static String bytesToHexString(byte[] src,int num) {
        StringBuilder str = new StringBuilder("");
        if (src == null || src.length <= 0)
            return null;
        for (int i = 0; i < num; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            str.append(" " + hv);
        }
        return str.toString();
    }
    String sData = "";
    // 发送数据
    public boolean send( byte[] data,int iLength) {
        sData = DataUtil.byteArrayToHex(data);
        System.out.println("LeProxy send sData = " + sData);
        if (mBleService != null) {
            return mBleService.send(SisSdk.getInstance().bleAddress, data, false);
        }else{
            return false;
        }
//		if(iLength > 20){
//			byte[] byOut = new byte[20];
//			int iNum = iLength/20;
//			int last = iLength%20;
//			for(int i = 0;i<iNum;i++){
////				System.arraycopy(data, i*20, byOut, 0, 20);
//				for(int j= 0;j<20;j++){
//					byOut[j] = data[i * 20 + j];
//				}
//				if(i == 0)
//					System.out.println("LeP send data = " + bytesToHexString(byOut,byOut.length));
//				mBleService.send(MainActivity.getInstance().address, byOut, false);// boolean型参数代表是否加密数据
//				try {
//					Thread.sleep(20);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch	block
//					e.printStackTrace();
//				}
//			}
//			if(last > 0){
//				byOut = new byte[last];
////				System.arraycopy(data, iNum*20, byOut, 0, last);
//				for(int j= 0;j<last;j++){
//					byOut[j] = data[iNum*20 + j];
//				}
////				System.out.println("LeP  send data = " + bytesToHexString(byOut,byOut.length));
//				mBleService.send(MainActivity.getInstance().address, byOut, false);// boolean型参数代表是否加密数据
//			}
//
//			return true;
//
//		}else{
//			byte[] byOut = new byte[iLength];
////			System.arraycopy(data, 0, byOut, 0, iLength);
//			for(int j= 0;j<iLength;j++){
//				byOut[j] = data[j];
//			}
//			System.out.println("LeP send data = " + bytesToHexString(byOut,byOut.length));
//			return mBleService.send(MainActivity.getInstance().address, byOut, false);// boolean型参数代表是否加密数据
//		}
    }


    public boolean bRealConnect = false;

    private BleCallBack mBleCallBack = new BleCallBack() {
        @Override
        public void onConnected(String address) {
            // 这里只是建立连接，尚不能交互数据
            broadcast(ACTION_GATT_CONNECTED, address);

            log("设备已连接" + address);
        }

        @Override
        public void onConnectTimeout(String address) {
            // 连接超时，开启断线重连不会走这个回调方法
            broadcast(ACTION_CONNECT_TIMEOUT, address);
            log("连接超时" + address);

//			connect();
        }

        @Override
        public void onServicesDiscovered(final String address) {
            // 拿到服务，可进行数据通讯，不过有些手机还需要延时，不然会断线
            new Timer().schedule(new ServicesDiscoveredTask(address), 300, 100);
            log("发现服务" + address);
            boolean bR = requestMtu(248);
            if(bR)
                bRealConnect = true;
            else
                bRealConnect = false;
//			System.out.println("requestMtu  bR = " + bR);
        }

        public void onCharacteristicWrite(String address, BluetoothGattCharacteristic characteristic, int status) {
            /*
             * if (BluetoothGatt.GATT_SUCCESS == status) { }
             */
//			boolean bR = (sData.equals(DataUtil.byteArrayToHex(characteristic.getValue())));
            log("onCharacteristicWrite  ->" + DataUtil.byteArrayToHex(characteristic.getValue()));
        }

        @Override
        public void onDisconnected(String address) {
            // 断线，类文件不存在，暂时删除
//            broadcast(ACTION_GATT_DISCONNECTED, address);
//
//            instrument_isconnect = false;
//            BT_CommunicationThread.BTDevStatus = Common_Constant.STATE_INITING;
//            BT_ConnectThread.getInstance().cancelBTSocket();
            log("LeP 设备已断开" + address);
        }


        public void onCharacteristicChanged(String address, BluetoothGattCharacteristic characteristic) {
            // 接收到数据
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
                //MyApplication.readFlashProcess = 100*iNum/MyApplication.readFlashNum;
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
        @Override
        public void onMtuChanged(String address, int mtu, int status) {
//            if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.i(TAG, "onMtuChanged() - " + address  + "  status = " + status + ", MTU has been " + mtu);
//            } else {
//                Log.e(TAG, "onMtuChanged() - " + address + ", MTU request failed: " + status);
//            }
            Intent intent = new Intent(ACTION_MTU_CHANGED);
            intent.putExtra(EXTRA_ADDRESS, address);
            intent.putExtra(EXTRA_MTU, mtu);
            intent.putExtra(EXTRA_STATUS, status);
            LocalBroadcastManager.getInstance(mBleService).sendBroadcast(intent);
        }
    };

//    public byte[] byBuffer = new byte[4194304];
//    public int iNum = 0;

//类文件不存在提示，暂时删除
//    public ArrayList<ReadMeterSocketOneData> mListSocketOneData = new ArrayList<ReadMeterSocketOneData>();

//    private final static String TAG = "LeProxy";

    // 这种写法仅供参考，因为蓝牙操作需要有间隔所以用定时器处理了一下
    private class ServicesDiscoveredTask extends TimerTask {
        String address;
        int i;

        ServicesDiscoveredTask(String address) {
            this.address = address;
        }

        @Override
        public void run() {
            if (i == 0) {
                boolean ok = mBleService.enableNotification(address);// 开启0x1002的通知【接收数据】
                log("开启通知" + ok);

            } else {
                broadcast(ACTION_GATT_SERVICES_DISCOVERED, address);
                cancel();
            }
            i++;
        }
    }

    private void log(String msg) {
        Log.i("LeProxy", "" + msg);
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

    /**
     * 请求更新MTU，会触发onMtuChanged()回调，如果请求成功，则APP一次最多可以发送MTU-3字节的数据，
     * 如默认MTU为23，APP一次最多可以发送20字节的数据
     * <p>
     * 注：更新MTU要求手机系统版本不低于Android5.0
     */
    public boolean requestMtu(int mtu) {
        return mBleService.requestMtu(SisSdk.getInstance().bleAddress, mtu);
    }

}
