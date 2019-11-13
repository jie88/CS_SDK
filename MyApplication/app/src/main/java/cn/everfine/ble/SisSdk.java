package cn.everfine.ble;
import android.bluetooth.BluetoothAdapter;

import java.util.ArrayList;

import cn.everfine.ble.BleProxy;

public class SisSdk {

    public static SisSdk  instance = null;
    public BleProxy ble;
    private SisSdk() {
        ble =BleProxy.getInstance();
    }
    public static SisSdk getInstance() {
        if (instance == null) {
            instance = new SisSdk();
        }
        return instance;
    }

    //        设备连接关接口（搜索、连接、断开等）；

    /**
     * 搜索蓝牙设备
     * mainActivity
     * <p>
     * LeProxy 79
     */
    public   String  SearchBle() {

        System.out.println("SisSdk SearchBle()");
        ble.scanLeDevice(true);
        return "这是蓝牙ID";
    }

    static class ClassInterface implements sdkInterface {
        @Override
        public void setBleName(ArrayList<String> bleName, ArrayList<String> bleAddress) {
            //fragmentCloud.updateCloudFile(fileNames,fileDates);
        }
    }
    /*连接
    LeProxy 137*/
    void connect() {

    }

    /*断开连接
     * LeProxy  148行*/
    void disconnect() {

    }

    //        测试接口（测试条件、自动积分等）；
//        读取光谱数据接口；
    /*
     * 通过搜MyApplication.setSpectrum_num(stu); 到MessgDeal 218行else if (order.getCMD().equals("ReadSpectrumData?")) {
     *order.getParameters();转换过来
     * */
    public void readdata() {

        int i;
        return;
    }

    //        读取数据项接口；
    /*MessgDeal 152行
    * else if (order.getCMD().equals("ReadAllValue?")) {
					// 读取所有数据*/
//        其他接口（如 获取电量、设备状态等）；
    /*
     * 获取电量
     * MessgDeal 363
     * MainActivity 795仪器电量返回
     * MainActivity 468发送查询电量命令，order.getCMD().equals("Charge?"
     * *DealtwithCmd
     * sendUpLowMessage
     *命令Common_Constant.CMD_CHARGE，0X140
     * BT_Communication.msgExcute处理命令
     * getChargeThread.start
     */
    public void getElec() {

    }


    /*设备状态
BT_CommunicationThread.BTDevStatus
     * */
    public void getStatus() {
    }



}