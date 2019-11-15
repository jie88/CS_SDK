package com.everfine.sis_sdk.ble;

import android.app.Activity;
import  com.everfine.sis_sdk.common.Common_Constant;
public  class SisSdk extends Activity{

    public static SisSdk  instance = null;
    public static  LeProxy ble=null;
    public static  int iBluetoothDelay=2000;
    public static  String strBleAddress="";
    public static boolean instrument_isconnect = false;
    public static int elecvalue;//电量
    public static  int elecStatus;//设备状态


    private SisSdk() {
        ble =LeProxy.getInstance();
        if(ble==null)
        {
            System.out.println("ble==nul");
        }
        spic_command = new SPIC_Command(null, null);
    }
    public static SisSdk getInstance() {
        if (instance == null) {
            instance = new SisSdk();
        }
        return instance;
    }

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
    public static int commType = Common_Constant.COMM_BT;
    public  static String bleName="";//设备名,之前名称是sn
    public  static String bleAddress="";//设备名
    private SPIC_Command spic_command;
    public void getElec() {

      int ret=  spic_command.readBatteryLevel(commType,bleName);
      System.out.println("getElec"+ret);

    }


    /**
     * 搜索蓝牙设备
     * LeProxy 79
     */
    public   void  SearchBle() {

        System.out.println("SisSdk SearchBle()");

        ble.scanLeDevice(true);
        return ;
    }


    /*连接*/
    public  void connect( String addresss,String name) {
        bleAddress=addresss;
        if(ble.connect())
        {
            bleName=name;
            System.out.println("sissdk connect() =success" );
        }
        else
        {
            System.out.println("sissdk connect() =faile" );
        }

    }


    /*断开连接*/
    void disconnect(String address) {
        ble.disconnect(address);
        bleAddress="";
        bleName="";
    }

    //        测试接口（测试条件、自动积分等）；
//        读取光谱数据接口；
    /*
     * 通过搜MyApplication.setSpectrum_num(stu);
     * 到MessgDeal 218行else if (order.getCMD().equals("ReadSpectrumData?")) {
     *order.getParameters();转换过来
     * */
    public void readdata() {

        return;
    }

    //        读取数据项接口；
    /*MessgDeal 152行
    * else if (order.getCMD().equals("ReadAllValue?")) {
					// 读取所有数据*/
//        其他接口（如 获取电量、设备状态等）；


    /*设备状态
BT_CommunicationThread.BTDevStatus
     * */
    public void getStatus() {

    }



}

