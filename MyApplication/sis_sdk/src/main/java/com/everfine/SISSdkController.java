package com.everfine;


import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

import com.ble.ble.BleService;
import com.everfine.ble.LeProxy;
import com.everfine.ble.SPIC_Command;
import com.everfine.common.Common_Constant;
import com.everfine.ui.ScanBleActivity;
import com.everfine.util.SISLogUtil;


/**
 * author : ${CHENJIE} created at  2019-11-07 23:13 e_mail : chenjie_goodboy@163.com describle :
 */
public class SISSdkController {

  private Activity mActivity;
  public LeProxy mLeProxy;

  public static int commType = Common_Constant.COMM_BT;
  private SPIC_Command spic_command;

  /**
   * 初始化
   */
  public void init(Activity activity) {
    mActivity = activity;
    mLeProxy = new LeProxy(mActivity);

    spic_command = new SPIC_Command(null, null);

    bindService();
  }

  /**
   * 退出
   */
  public void onDestroy() {
    unbindService();
  }

  private void bindService() {
    //绑定服务
    mActivity.bindService(new Intent(mActivity, BleService.class), mConn, Activity.BIND_AUTO_CREATE);

  }

  private void unbindService() {
    //解绑服务
    mActivity.unbindService(mConn);
  }

  private final ServiceConnection mConn = new ServiceConnection() {

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      mLeProxy.initBleService(service);
    }
  };


  public void scanLeDevice(boolean enable) {
    mLeProxy.scanLeDevice(enable);
  }

  public void setOnScanListener(LeProxy.OnScanListener scanListener) {
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


  public void disconnected() {
    //处理断开连接

  }

  public void showToast(final String toast){
    mActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(mActivity,toast,Toast.LENGTH_SHORT).show();
      }
    });

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
  public void getElecCs() {

    if (mLeProxy.bRealConnect) {
      //链接成功
      byte[] byteOut = {0x7b, 0x7b, (byte) 0xB2, (byte) 0x30, (byte) 0xD0, 0x00, 0x00, (byte) 0xB2, 0x7d, 0x7d};
      boolean succ = mLeProxy.send(byteOut, 0);

      // int ret = spic_command.readBatteryLevel(commType, mLeProxy.linkDevice.getName());
      SISLogUtil.d("电量发送：" + succ);
      //    System.out.println("getElec"+ret);
    } else {
      //还没链接
      SISLogUtil.d("请先链接或等待链接成功");
    }
//
  }
  public void getElec() {

    if (mLeProxy.bRealConnect) {
      //链接成功
      int ret = spic_command.readBatteryLevel(commType, mLeProxy.linkDevice.getName());
      SISLogUtil.d("电量：" + ret);
      //    System.out.println("getElec"+ret);
    } else {
      //还没链接
      SISLogUtil.d("请先链接或等待链接成功");
    }

//

  }
public void test()
{
  //固定积分200ms测试
//  G609979CM1391150 sendCommand. Cmd = d5
//  I/System.out: LeProxy send sData = 7B 7B B2 30 D5 00 09 00 01 01 00 00 48 43 20 01 6E 7D 7D 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
//  D/BleService: setCharacteristicWriteType() - uuid: 00001001-0000-1000-8000-00805f9b34fb[write_no_response]
//  D/SPIC_Command: rtn = mInterface.getRespond参数为-43,bytein,288
//  D/COMM_7B7D: G609979CM1391150  getRespond. Cmd = d5
//  I/LeProxy: onCharacteristicWrite  ->7B 7B B2 30 D5 00 09 00 01 01 00 00 48 43 20 01 6E 7D 7D 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
//  D/MainActivity: Client is over!
//        I/LeProxy: 接收数据 <- 7B 7B 30 B2 D5 05 4C DA 11 5F 11 B2 11 D6 12 AB 11 22 12 DF 12 7A 11 E2 11 DE 11 3C 11 0E 13 68 11 06 13 BF 11 DD 11 55 12 5E 12 B1 11 10 12 90 11 71 13 2E 12 9C 11 73 11 44 12 AF 11 50 12 13 12 87 11 9C 12 9C 11 0A 13 4C 12 A0 11 1F 12 23 12 D1 11 35 12 D6 10 02 12 2E 12 7D 80 12 EE 11 EE 11 BA 11 1B 12 79 11 CE 12 59 11 BD 11 3B 12 92 12 44 12 4A 12 28 11 17 12 A2 11 D6 11 B6 12 9A 11 C2 11 A6 12 5E 12 14 12 1C 12 BF 11 3D 12 68 11 96 11 A8 12 B5 11 A0 11 A2 12 7A 11 F4 11 A1 11 87 12 2C 12 ED 11 49 12 CF 11 88 11 26 12 95 11 34 12 C7 11 A9 11 9D 12 02 12 35 12 DF 13 37 12 64 12 1F 12 E9 11 A7 11 82 12 D4 11 6B 12 2E 13 66 11 5F 12 C9 11 72 12 A0 11 BD 11 6C 12 AA 11 5A 12 4F 12 D6 11 AE 11 1B 12 D8 11 29 12 ED 11 14 12 EA
//  I/LeProxy: 接收数据 <- 11 3A 12 E7 11 84 13 8D 11 C1 11 EA 12 23 12 C6 11 37 12 B8 11 AD 11 42 11 29 12 AB 11 E8 11 51 12 18 11 46 12 C7 11 3D 11 9A 12 E7 11 37 12 19 12 E2 11 F8 11 FC 11 94 11 FB 11 67 12 47 12 44 12 FF 11 5A 12 5C 11 6D 11 EF 11 D1 11 4F 12 C1 11 7C 12 5F 12 04 12 75 11 5D 12 EB 11 2E 12 73 12 4E 11 3F 12 19 12 66 12 8F 12 A8 11 57 12 42 12 4D 11 AA 12 AB 11 94 14 34 12 73 11 C0 11 FA 11 41 11 41 12 0A 12 A3 11 6E 12 B6 11 A4 11 8E 12 E3 11 05 12 E2 11 1E 12 5E 12 DC 11 8D 11 26 12 CF 11 A9 12 B8 11 66 12 3C 13 B3 11 59 12 52 12 A0 11 EF 11 E5 12 C7 11 6B 13 8F 12 93 12 65 12 D4 11 71 12 C0 11 27 12 07 12 49 12 53 12 B5 12 CB 11 20 12 DD 11 18 12 7E 12 6A 11 C2 11 C1 12 43 12 F6 12 97 12 05 12 73 12 FA 11 2A 12 12 12 FC 12 56 12
//  接收数据 <- 00 12 92 11 58 12
//  I/LeProxy: 接收数据 <- E6 11 76 12
//  I/LeProxy: 接收数据 <- 95 13 A5 11 CE 12 DF 11 DA 11 0E 13 0C 12 2F 12 3E 12 C1 11 7A 12 31 12 A4 12 03 13 93 12 B5 12 1A 13 32 12 48 12 ED 11 41 12 08 13 DE 12 8C 12 DA 12 CC 11 2C 13 1A 13 2A 12 32 13 01 12 61 12 C3 12 DE 12 04 13 75 12 B3 12 2B 13 07 12 B5 12 FC 12 78 12 C8 7D 7D
//  I/System.out: iFindHeadPos = 0  iNum = 587
//  I/System.out: ReadMeterSocketOneData decode  = 0

}

  public static   int average_number = 1;
  public static String DC = "DC";
  public static String S_Connect = "BT";
  public  int Zero()
  {

    // LeProxy send sData = 7B 7B B2 30 D5 00 09 00 01 01 00 00 48 43 20 01 6E 7D 7D
//    接收数据 <- 7B 7B 30 B2 D5 05 4C B8 11 BE 11 58 11 95 12 D1 11 08 12 CF 12 56 11 C5 11 D2 11 32 11 CD 12 67 11 98 12 BA 11 CF 11 53 12 2A 12 B8 11 CB 11 4E 11 93 13 27 12 B7 11 C8 11 6F 12 36 12 FD 11 03 12 AA 11 97 12 6D 11 F6 12 66 12 D4 11 5F 12 69 12 D5 11 1E 12 14 11 45 12 24 12 D2 12 37 12 B1 11 D1 11 0A 12 6F 11 08 13 C9 11 F4 11 96 12 A1 12 1A 12 4C 12 16 11 23 12 C7 11 BE 11 AA 12 C6 11 D6 11 9F 12 47 12 19 12 0F 12 EA 11 59 12 36 11 DA 11 89 12 78 11 E4 11 19 13 B0 11 EE 11 A7 11 45 12 26 12 1E 12 4F 12 CF 11 9C 11 6C 12 6A 11 C4 11 F4 11 29 11 6D 12 F1 11 32 12 0B 14 20 12 9D 12 0A 12 F2 11 AF 11 5E 12 3B 12 64 12 4B 13 71 11 92 12 B1 11 1C 12 76 11 AD 11 32 12 A6 11 77 12 49 12 AF 11 ED 11 14 12 23 12 16 12 EE 11 EE 11 CE 11
//    I/LeProxy: 接收数据 <- 1B 12 D3 11 3E 13 B0 11 DF 11 2B 13 7D 80 12 BF 11 F9 11 42 12 14 12 91 11 2C 12 D1 11 AF 11 01 12 88 11 48 12 BE 11 72 11 73 12 04 12 4E 12 C4 11 2B 12 3C 12 E8 11 B3 11 FE 11 DC 11 05 12 E3 11 1A 12 26 12 7B 80 11 A8 11 6B 12 B8 11 3D 12 0D 12 C4 12 35 12 05 12 6C 11 6F 12 CE 11 7D 80 12 78 12 58 11 79 12 08 12 F0 11 EE 12 18 12 49 12 6D 12 B3 11 73 12 AD 11 7C 14 06 12 AB 11 B0 11 D5 11 42 11 35 12 BE 11 36 12 48 12 0A 12 8E 11 B2 12 6C 12 26 12 02 12 3E 12 34 12 9B 11 CC 11 31 12 91 11 9C 12 A3 11 34 12 33 13 1B 12 31 12 8C 12 47 11 3D 12 22 13 D8 11 5E 13 C5 12 3D 12 9C 12 E3 11 81 12 2E 12 44 12 F5 11 79 12 AF 12 AE 12 D6 11 22 12 26 12 1A 12 80 12 8A 11 98 11 A6 12 DB 11 CF 12 A6 12 28 12 86 12 1A 12 4F 12 A7 12 C5 12
//    接收数据 <- 7C 12 E6 11 CB 11 51 12 E7 11
//    I/LeProxy: 接收数据 <- 44 12 AA 13 04 12 60 12 C4 11 BC 11 B6 12 D4 11 54 12 2A 12 F3 11 67 12 5E 12 98 12 D6 12 9E 12 BE 12 44 13 43 12 46 12 CF 11 2E 12 D8 12 B8 12 82 12 A8 12 DC 11 26 13 D8 12 91 12 1E 13 C8 11 7A 12 C1 12 FA 12 CF 12 D3 12 9D 12 89 13 64 12 67 12 4E 13 98 12 EA 7D 7D
//    I/System.out: iFindHeadPos = 0  iNum = 589
//    ReadMeterSocketOneData decode  = 0
//    D/LeProxy: oneData is ok! CMD is d5
//    oneData is ok! CMD is -1 -1 26 -107 95 125 125 0 0 0 0 0 0 0

    //固定时间校零
    if (mLeProxy.bRealConnect) {
      //链接成功
      byte[] byteOut = {0x7b, 0x7b, (byte) 0xB2, (byte) 0x30, (byte) 0xD5, 0x00, 0x09, 0x00,0x01,0x01,0x00,
              0x00,0x48,0x43,0x20,0x01,(byte) 0x6E, 0x7D, 0x7D};
      boolean succ = mLeProxy.send(byteOut, 0);

      // int ret = spic_command.readBatteryLevel(commType, mLeProxy.linkDevice.getName());
      SISLogUtil.d("电量发送：" + succ);
      //    System.out.println("getElec"+ret);
    } else {
      //还没链接
      SISLogUtil.d("请先链接或等待链接成功");
    }
  return 0;
  }
  //Impl_sampleAD里面sent
  public int ZeroAll()
  {
    int ret=-1;
    if (!mLeProxy.bRealConnect) {

      ret =-2;
      //还没链接
      SISLogUtil.d("请先链接或等待链接成功");
      return ret;
    }


    String H_L = "High";
    String[] para=new String[]{
            S_Connect,
            mLeProxy.linkDevice.getName(),
            DC,
            H_L,
            average_number + ""};
    if (para.length == 5) {
      spic_command.SetAvgNum(Integer.parseInt(para[4]));
      spic_command.SetAutoInt(true);
      spic_command.setZeroAllProgress(5);
      int gainrange = para[3].equals("High") ? Common_Constant.GR_HIGH
              : Common_Constant.GR_LOW;
      spic_command.setSampleType((byte)0x00);//校零时，默认DC模式
      //spic_command.correct(gainrange, commType, Sn);
      spic_command.correct(gainrange, commType, LeProxy.linkDevice.getName());
      String[] s = new String[1];
      //s[0] = Sn;
      s[0] = LeProxy.linkDevice.getName();
      spic_command.SetAutoInt(false);
      spic_command.setZeroAllProgress(100);
     // respondcmd("ZeroAll", "OK", s, sock);
    } //else
      //respondcmd("ZeroAll", "ERROR", PARANULL, sock);

    return ret;

  }


  private static class SingletonHolder {
    private static final SISSdkController INSTANCE = new SISSdkController();
  }

  private SISSdkController() {
  }

  public static final SISSdkController getInstance() {
    return SingletonHolder.INSTANCE;
  }
  public static  boolean bDemoMode = false;
  public static  boolean bDemoModeExit = false;
  public static String app_name = "";
  public static boolean bLoadImage = false;
  public static boolean bZeroFromMainActivity = false;
  public static boolean bSelectMode = false;
  public static int Time = 0;
  public static int iCharge = -1;
  public static int iLocalCrc = -1;
  public static int iNewCrc = -1;
  public static String sSoftVer = "";
  public static int readFlashProcess = 0;
  public static int readFlashNum = 120000;
  public static int iWriteDelay = 45;
  public static int iReadDelay = 100;
  public static int iBluetoothDelay = 5000;
  //光谱定标
  public static float s_cct = 2856f;
  public static float s_e1 = 1001f;
  public static boolean s_agile_low = true;

  private String S_CCT = "s_cct";
  private String S_E1 = "s_e1";
  private String S_AGILE_LOW = "s_agile_low";
  private String S_WRITE_DELAY = "s_write_delay";
  private String S_READ_DELAY = "s_read_delay";

  //白光分级x，y轴格数
  public static int x_num = 8;
  public static int y_num = 8;

  private String X_NUM = "x_num";
  private String Y_NUM = "y_num";

  //3.19新增休眠时间设定
  public static String dormancy_time = "10";
  public static String dev_type = "A";
  public static float start_wavelengh = 380;
  public static float stop_wavelengh = 760;
  public static float min_integral_time = 0.1f;
  public static float max_integral_time = 5000;
  public static boolean is_mixed_light_revise = true;
  public static boolean is_linetype_revise = true;
  public static boolean is_eve_emes = true;
  public static boolean is_E = false;//植物光学度量显示

  public static float WEAK_AD = 3000.0f;

  public static float Parameter_setting_one = 0.01f;
  public static int Parameter_setting_one_int = 0;
  public static float e1 = 100f;
  public static float s_p1 = 1f;

  private String PARAMETER_SETTING_ONE = "Parameter_setting_one";
  private String PARAMETER_SETTING_ONE_INT = "Parameter_setting_one_int";
  private String E1 = "e1";
  private String S_P1 = "s_p1";
  private String SHARED_DC = "DC";



}
