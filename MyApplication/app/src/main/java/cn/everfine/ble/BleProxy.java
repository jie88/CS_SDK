package cn.everfine.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.ble.api.DataUtil;
import com.ble.ble.BleCallBack;
import com.ble.ble.BleService;
import com.cs.sis_sdk.util.SISLogUtil;

import java.util.ArrayList;
import java.util.Timer;

public class BleProxy {
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
    private final static String TAG = "LeProxy";
    private static BleProxy mInstance;
    private BleService mBleService;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler = new Handler();
    private boolean mScanning = false;
    public static  int iTimeDelay=5000;
    public static ArrayList<String> bleName;
    public static ArrayList<String> bleAddress;
    public static  String strBle="";
    public static  String strBleAddress="";

    private BleProxy() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bleName = new ArrayList<String>();
        bleAddress = new ArrayList<String>();
    }

    //public BleService getDevice(){
    //    return mBleService;
   // }

    public static BleProxy getInstance() {
        if (mInstance == null)
            mInstance = new BleProxy();
        return mInstance;
    }

    public void initBleService(IBinder binder) {
        mBleService = ((BleService.LocalBinder) binder).getService(mBleCallBack);
        mBleService.initialize();// 这个必须有
        mBleService.setDecode(false);// 最初的模块数据都是加密的，所以SDK默认回去解密接收的数据，这里设置为不解密
    }
    public boolean bRealConnect = false;
    // 连接设备
    public boolean connect() {
        bRealConnect = false;
        if(strBleAddress != null && !strBleAddress.equals("")){
            System.out.println("connect() = " + strBleAddress);
            return mBleService.connect(strBleAddress, false);// boolean型参数代表是否开启断线重连
        }else{
            return false;
        }
    }
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
//            new Timer().schedule(new ServicesDiscoveredTask(address), 300, 100);
            log("发现服务" + address);
            boolean bR = requestMtu(248);
//            if(bR)
//                bRealConnect = true;
//            else
//                bRealConnect = false;
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
            // 断线
            broadcast(ACTION_GATT_DISCONNECTED, address);

//            MyApplication.instrument_isconnect = false;
//            BT_CommunicationThread.BTDevStatus = Common_Constant.STATE_INITING;
//            BT_ConnectThread.getInstance().cancelBTSocket();
            log("LeP 设备已断开" + address);
        }


        public void onCharacteristicChanged(String address, BluetoothGattCharacteristic characteristic) {
            // 接收到数据
//            broadcast(address, characteristic.getValue());
//
         //   SISLogUtil.d("接收数据 <- " + DataUtil.byteArrayToHex(characteristic.getValue()));
//            byte[] bData = characteristic.getValue();
//            int iBufferNum = characteristic.getValue().length;
//
//            if(bData[0] == 0x7B && bData[1] == 0x7B){
//                iNum = 0;
//            }
//            for(int i = 0;i<iBufferNum;i++){
//                byBuffer[iNum++] = bData[i];
//            }
//            if(iNum > 4 && byBuffer[4] == SPIC_Command.CMD_READ_FLASH){
//                MyApplication.readFlashProcess = 100*iNum/MyApplication.readFlashNum;
//            }
//
//            int iFindHeadPos = 0;
//            if (byBuffer[iNum - 2] == 0x7D
//                    && byBuffer[iNum - 1] == 0x7D){
////				log("接收数据  ----iNum = " + iNum + " == " + bytesToHexString(byBuffer,iNum));
//                for (int i = iNum - 3; i >= 1; i--) {
//                    if (byBuffer[i - 1] == 0x7B && byBuffer[i] == 0x7B) {
//                        iFindHeadPos = i - 1;
//                        break;
//                    }
//                }
//                System.out.println("iFindHeadPos = " + iFindHeadPos + "  iNum = " + iNum);
//
//                byte cmd = byBuffer[iFindHeadPos + 4];
//                ReadMeterSocketOneData oneData = new ReadMeterSocketOneData(
//                        iFindHeadPos, byBuffer, iNum - iFindHeadPos);
//
//                if (oneData.isDataOk()) {
//                    Log.d(TAG, "oneData is ok! CMD is " + Integer.toHexString(cmd&0xff));
//                    if (mListSocketOneData.size() >= 10)
//                        mListSocketOneData.remove(0);
//                    mListSocketOneData.add(oneData);
//                    Log.d(TAG, "oneData is ok! CMD is " +  Tool.byteArrayToAsciiString(mListSocketOneData.get(0).para));
//                }
//                else {
//                    mListSocketOneData.add(oneData);
//                    Log.d(TAG, "oneData is ERROR!");
//                }
//                iNum = 0;
//            }

        }

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
    private OnScanListener mOnScanListener;

    public void setOnScanListener(OnScanListener scanListener) {
        this.mOnScanListener = scanListener;
    }

    public void scanLeDevice(boolean enable) {
        System.out.println("scanLeDevice enable = " + enable + "  " + mScanning);
        if (enable && !mScanning) {
            mScanning = true;
            mHandler.postDelayed(mStopScan, iTimeDelay);
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
            if (mOnScanListener != null) {
                mOnScanListener.onLeScan(device, rssi, scanRecord);
                //System.out.println("蓝牙搜索到" + device.getName());
                //System.out.println("蓝牙搜索到" + device.getAddress());
//                if((!device.getName().isEmpty())&&(!device.getAddress().isEmpty()))
//                {
//                    strBle=device.getName();
//                    strBleAddress=device.getAddress();
//                    System.out.println("蓝牙搜索到" + device.getName());
//                    System.out.println("蓝牙搜索到" + device.getAddress());
//                    if(bleName.contains(device.getName()))
//                    {
//                        System.out.println("bleName.contains:" + device.getName());
//                    }
//                    if (bleName.size() > 0) {
//                        for (int i = 0; i < bleName.size(); i++) {
//
//                            if (!bleName.get(i).equals(
//                                    device.getName())) {
//                                bleName.add(device.getName());
//                                bleAddress.add(device.getAddress());
//                            }
//                            else
//                            {
//                                Log.d("Message", "is have,return");
//                            }
//                        }
//                    }
//
//
//                }
//                else
//                {
//                    System.out.println("蓝牙搜索到" + device.getName());
//                    System.out.println("蓝牙搜索到" + device.getAddress());
//                }
//
//
            }
            else
            {
                System.out.println("蓝牙mei 搜索到" );
            }
        }
    };
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
        return mBleService.requestMtu(strBleAddress, mtu);
    }
}
