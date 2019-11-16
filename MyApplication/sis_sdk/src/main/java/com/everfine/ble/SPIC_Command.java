package com.everfine.ble;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.Arrays;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;

import com.ble.api.DataUtil;
//import com.everfine.sis_20.application.MyApplication;

//import com.everfine.core.ColorDataSPIC;
//import com.everfine.core.SPICMeterCalc;
import com.everfine.SISSdkController;
import com.everfine.core.Tool;

@SuppressLint("NewApi")
public class SPIC_Command {
    private static final int COMM_BT = 0x90;
    private static final int COMM_WIFI = 0x91;
    public static final int TYPE_SPIC_A = 0;
    public static final int TYPE_SPIC_B = 1;
    public static final int TYPE_SPIC_C = 2;
    public static final int DETECTOR_RANGE_AUTO = 0;
    public static final int TYPE_SPIC_UNKNOW = 100;
    public static final int RES_AD_SUCCESS = 1;
    public static final int RES_AD_FAIL = 2;
    public static final int RES_WL_WRONG = 11;
    public static final int RES_AD_AUTO_SIGNAL_SMALL = 3;
    public static final int RES_AD_AUTO_SIGNAL_LARGE = 4;
    public static final int RES_TEST_SUCCESS = 5;
    public static final int RES_LOSEFILE = 10;
    public static final int RES_COMMUNICATION_FAILED = 2;
    public static final int RES_SIGNAL_TOO_SMALL = 3;
    public static final int RES_SIGNAL_TOO_LARGE = 4;
    public static final int RES_TEST_CANCELED = 6;
    public static final int RES_STATUS_ERROR = 7;
    public static final int RES_PARAMS_ERROR = 8;
    public static final int RES_AD_OVER_FLOW = 9;
    public static final int STATUS_CAMERA_METERING = 6;
    private static final float K_AUTO_DECREASE_LOW_LEVEL = 0.2f;
    private static final float K_AUTO_INCREASE = 1.5f;
    public static final boolean NEW_GET_ZERO_MODE = true;
    private boolean mIsTesting = false;// 标志主界面循环测试或远程控制测试。
    private boolean mIsSampling = false;
    private boolean mIsSampleCanceled = false;
    private int mTestResult = RES_TEST_SUCCESS;
    public boolean isAutoIntCmd = false;
    public float mAutoIntNum = 0;
    public boolean mIsAutoInt = false;
    public boolean mIsZeroingAD = false;
    private boolean mIsInited = false;
    private boolean mIsStoped = false;
    private boolean mIsWorking = false;
    private boolean IntCorrectSuccess = false;
    private boolean mIsDemo = false;
    public int mIntNum = 0;
    private int mResponse = -1;
    private float[] m_fIntTime = null;
    public float[][] m_fDataAD0 = null;
    private float ZeroADAvg = 0;
    private long lastChargTime = 0;
    // add by huyubing 2014.1.11
    private int m_iAddtionGainRange = 0;
    private float m_fAddtionIntTime = 0.0f;
    private float[] m_fAddtionZeroAD = null;
    public float[] m_fDataAD = null;
    public float[] m_fLambda = null;
    public float[][] m_MultiSampleAD = null;
    public float AutoMaxad = 0, Max_AD = 0;
    public float m_fAutoIntTime = 5;
    public float[] m_fP = null;
    public int mAvgNumMax = 10;
    public int mAvgNum = 1;
    public static byte sampleType = 0x00;
    public float mIntTimeMin = 0.1f;
    public float mIntTimeMax = 5000;
    // public float mAutoIntMax = 60000;
    // SPIC、Meture切换
    public static final int TYPE_SPIC = 0;
    public static final int TYPE_METURE = 1;
    public static final int METER_METHOD_SPECTRUM_AND_CAMERA = 0;
    public static final int METER_METHOD_SPECTRUM_ONLY = 1;
    public static final int METER_METHOD_DETECTOR_ONLY = 2;

    public static final int STATUS_IDLE = 0;
    public static final int STATUS_UNINIT = 1;
    public static final int STATUS_INITED = 2;
    public static final int STATUS_SIMPLE_CMD = 3;
    public static final int STATUS_SAMPLE = 4;
    public static final int STATUS_BATTERY_LEVEL = 5;
    public static final int STATUS_AUTO_INT = 6;
    public static final int STATUS_SAMPLING = 7;
    public static final int STATUS_METERING = 8;

    public static final int ERROR_TIMEOUT = 100;
    public static final int ERROR_NO_RESPONSE = 101;
    public static final int ERROR_WRONG_STATUS = 102;
    public static final int ERROR_OPEN_COM_FAILED = 103;
    public static final int ERROR_BAD_DATA = 104;
    public static final int ERROR_SUCCESS = 105;
    public static final int ERROR_STATUS = 106;

    private static final int LEN_VERSION = 5;
    private static final int LEN_RETURN = 1;
    private static final int LEN_WIFI = 128;
    public static final int LEN_SN_CONTAINER = 16;

    static final byte CMD_READ_SN = (byte) 0xB0;
    private static final byte CMD_WRITE_SN = (byte) 0xB3;
    private static final byte CMD_READ_TYPE = (byte) 0xC8;
    private static final byte CMD_WRITE_TYPE = (byte) 0xCF;
    public static final byte CMD_SAMPLE_1 = (byte) 0xD5;
    private static final byte CMD_READ_E = (byte) 0xB9;
    public static final byte CMD_READ_VERSION = (byte) 0xBB;
    private static final byte CMD_CANCEL_SAMPLE = (byte) 0x2A;
    public static final byte CMD_READ_BATTERY_INFO = (byte) 0xD0;
    public static final byte CMD_WRITE_FLASH = (byte) 0xCD;
    public static final byte CMD_SET_REST_TIME = (byte) 0xCB;
    public static final byte CMD_READ_REST_TIME = (byte) 0xDA;
    public static final byte CMD_BOOT_LOADER = (byte) 0xEE;
    public static final byte CMD_READ_FLASH = (byte) 0xCC;
    private static final byte CMD_ERASURE_4KFLASH = (byte) 0xCE;
    private static final byte CMD_READ_WIFI_INFO = (byte) 0xD1;
    private static final byte CMD_WRITE_WIFI_INFO = (byte) 0xD2;
    private static final byte RES_WRITE_SN_SUCCESS = (byte) 0x55;
    private static final byte RES_WRITE_SN_FAILED = (byte) 0xAA;
    private static final byte RETURN_SUCCESS = (byte) 0x55;
    private static final byte RETURN_FAILED = (byte) 0xAA;
    private static final byte CMD_WRITE_DELAY = (byte) 0xA1;
    public static final int DEFAULT_MAX_SAMPLE_TIME = 10000;
    public static final int DEFAULT_MIN_SAMPLE_TIME = 3;
    public static final int COM_PORT_COM = 2; // 有线通讯串口
    public static final int COM_PORT_BLUETOOTH = 1; // 蓝牙通讯串口
    public static final int DEFAULT_COM_PORT = COM_PORT_COM;
    public static final int END4M = 4194304;
    public static final int LOW_BATTERY_NOT_USE_LEVEL = 5;
    public static final int LOW_BATTERY_LEVEL = 20;
    private static final float MAX_AD = 65000;
    private static final float K_AD_OVERFLOW = 0.92f;
    private static float K_AD_PASS = 0.6f;
    public static final float LIMIT_AD = MAX_AD * K_AD_OVERFLOW;
    private static float PASS_AD = MAX_AD * K_AD_PASS;
    public static final int BLOCKSIZE = 4096;
    // public int mNumEffictivePixel = 256;
    public static final int GR_HIGH = 0;
    public static final int GR_LOW = 1;
    private int mSampleADGainRange = GR_HIGH;
    private boolean mIsShowLowDebug = false;
    private int mStatus = STATUS_IDLE;
    // private Thread mSampleThread = null;
    private float[] mADData = null;
    private int mADCount = 0;
    private float mIntTime = 120;
    private int mMaxSampleTime = DEFAULT_MAX_SAMPLE_TIME;
    private int mMinSampleTime = DEFAULT_MIN_SAMPLE_TIME;
    private byte mSampleCmd = CMD_SAMPLE_1;
    public COMM_7B7DProtocol mInterface = null;
    private boolean mAutoIntFinded = false;// 标志自动积分是否找到了合适的积分时间。
   // public SPICMeterCalc CalcCore = null;
    private static final String TAG = "SPIC_Command";
    private static final Activity Activity = null;
    private boolean mAutoSeleCommPort = true;
    private boolean connect_result = false;
    private boolean isAutoFindingAD = true;
    private int mMeterType = TYPE_SPIC_B;
    private int mMeterMethod = METER_METHOD_SPECTRUM_AND_CAMERA;
    /***************** Detector part ******************/
    private int mLevelNum = 4;
    public float[] mZero = null;
    public float[] mK = null;
    private float mAd = 0;
    private float mELevel = 0;
    public int Batlevel = 0;
    private float AdZerotime = 0, AdZeroAvgAD = 0;
    byte[] address1 = new byte[4];
    byte[] address2 = new byte[4];
    byte[] byteOut1 = new byte[300];
    byte[] byteOut64 = new byte[64];
    byte[] startaddr = null;
    byte[] endaddr = null;
    public int batteryState = 0;
    private float new_initTime = 0.0f;
    private int new_Avg = 1;
    public static float multiple = 1.0f;
    private Socket SnSocket = null;
    public InputStream SnInStream = null;
    public OutputStream SnOutStream = null;
   // private WIFI_LowerServerThread snThread = null;
    // 50Hz
    private float[][] SumAddTime1 = {
            { 0.1f, 200.0f }, { 0.2f, 100.0f },
            { 0.5f, 40.0f }, { 0.8f, 20.0f },
            { 1.0f, 20.0f }, { 2.0f, 10.0f },
            { 5.0f, 8.0f }, { 8.0f, 5.0f },{ 10.0f, 4.0f },
            { 20.0f, 2.0f }, { 40.0f, 5.0f }, { 50.0f, 4.0f },
            { 100.0f, 1.0f }, { 110.0f, 1.0f }, { 120.0f, 1.0f },
            { 130.0f, 1.0f }, { 140.0f, 1.0f }, { 150.0f, 1.0f },
            { 200.0f, 1.0f }, { 300.0f, 1.0f }, { 400.0f, 1.0f },
            { 500.0f, 1.0f }};
    // 60Hz
    private float[][] SumAddTime2 = {{ 0.1f, 160.0f }, { 0.2f, 80.0f },
            { 0.5f, 64.0f },
            { 1.0f, 32.0f }, { 2.0f, 16.0f },{ 4.0f, 20.0f },
            { 5.0f, 16.0f }, { 8.333f, 24.0f },
            { 16.666f, 12.0f }, { 33.333f, 6.0f }, { 41.667f, 2.0f },
            { 50f, 2.0f },{ 58.333f, 2.0f },{75f, 1.0f },{ 83.333f, 1.0f },{91.666f, 1.0f },
            { 100.0f, 2.0f }, { 108.333f, 1.0f },{ 116.666f, 1.0f },
            { 125.0f, 1.0f }, { 200.0f, 1.0f }, { 300.0f, 1.0f },
            { 400.0f, 1.0f }, { 500.0f, 1.0f } };
    private float AutoProgress = 0, measProgress = 0, ZeroAllProgress = 0;
    public static final int RES_REMOVE_ZERO_FAILED = 104;
    public static final int RES_REMOVE_ZERO_SUCCESS = 105;
    public static final int RES_REMOVE_ZERO_OVERFLOW = 106;
    private static final float ZERO_AD_OVERFLOW = 5000;

    /*************************************/
//    public SPIC_Command(SPICMeterCalc spic, InputStream in, OutputStream out,
//                        WIFI_LowerServerThread thread) {
//        // m_MultiSampleAD = new float[7][mNumEffictivePixel];
//        CalcCore = spic;
//        // SnSocket = socket;
//        SnInStream = in;
//        SnOutStream = out;
//        snThread = thread;
//        mInterface = new COMM_7B7DProtocol(this);
//    }

    public Socket getSocket() {
        return SnSocket;
    }

  //  public WIFI_LowerServerThread getThread() {
//        return snThread;
//    }

//    public void init() {
//        mIntNum = CalcCore.multiZero_GetADZeroIntTimeNum();
//        m_fDataAD0 = new float[mIntNum][CalcCore.mNumEffictivePixel];
//        m_fIntTime = CalcCore.m_fListIntTime;
//        m_fDataAD = new float[CalcCore.mNumEffictivePixel];
//        mZero = new float[mLevelNum];
//        mK = new float[mLevelNum];
//        // System.out.println("8888888888888"+CalcCore.DevType);
//        setMeterType(CalcCore.DevType.equals("A") ? TYPE_SPIC_A : TYPE_SPIC_B);
//        // m_fAddtionZeroAD = new float[mNumEffictivePixel];
//        m_fAddtionZeroAD = CalcCore.mAdZero.m_fAddtionZeroAD;
//        if (m_fAddtionZeroAD == null)
//            m_fAddtionZeroAD = new float[CalcCore.mNumEffictivePixel];
//        for (int i = 0; i < CalcCore.mNumEffictivePixel; i++)
//            m_fAddtionZeroAD[i] = 0;
//        mStatus = STATUS_INITED;
//    }
    public SPIC_Command( InputStream in, OutputStream out
    ) {
        // m_MultiSampleAD = new float[7][mNumEffictivePixel];
        //CalcCore = spic;
        // SnSocket = socket;
        SnInStream = in;
        SnOutStream = out;

        mInterface = new COMM_7B7DProtocol(this);
    }
    public void correct(int gainrange, int commType, String SN) {
//        mIsStoped = false;
//        mIsWorking = true;
//        mIsZeroingAD = true;
//        // add by huyubing 2014.1.11
//        if (!GetAutoInt() && NEW_GET_ZERO_MODE) {
//
//            AddtionZeroAD(commType, SN);
//            mIsZeroingAD = false;
//            return;
//        }
//        boolean result = false;
//        mResponse = gainrange;
//        Log.d(TAG, "AD correct start!!!! TestGainRange=" + mResponse
//                + " mIntNum = " + mIntNum);
//        // mResponse = SpecMeterComm.GR_HIGH;
//
//        if (mResponse == GR_HIGH) {
//            for (int i = 0; i < mIntNum; i++) {
//                System.out.println(TAG +  "AD correct. TestGainRange=" + mResponse
//                        + ", IntTime=" + m_fIntTime[i] + "mIntNum=" + i);
//                result = false;
//
//                for (int j = 0; j < 3; j++) {
//                    if (mIsStoped) {
//                        break;
//                    }
//                    System.out.println("correct m_fDataAD0 length is "
//                            + m_fDataAD0.length);
//                    setAdZerotime(m_fIntTime[i]);
//                    result = sampleAdZero(m_fIntTime[i], mResponse,
//                            m_fDataAD0[i], commType, SN);
//                    System.out.println("AD correct. ZeroAll result" + result);
//                    setAdZeroAvgAD(Tool.getAvg(m_fDataAD0[i], 256));
//                    if (result) {//
//                        break;
//                    }
//                    if(!LeProxy.bRealConnect)
//                        break;
//                }
//                if(!LeProxy.bRealConnect || !result) {
//                    mIsWorking = false;
//                    break;
//                }
//                setZeroAllProgress(i * 100 / mIntNum);
//                System.out.println("AD correct. ZeroAll progress" + getZeroAllProgress());
//                if (!result) {// 不成功 读之前值
//                    mIsWorking = false;
//                    return;
//                }
//                Log.d(TAG, " *****    " + m_fIntTime[i] + " *****");
//                System.out.println(Tool.printfloatToString(m_fDataAD0[i]));
//            }// ////for(int i=0;i<mIntNum;i++)
//            mIsZeroingAD = false;
//            System.out.println(TAG + "correct ad success!");
//            CalcCore.multiZero_SetADZero(mResponse, 0, m_fDataAD0, SN);
//            // save(GR_HIGH);
//        }
//
//        else
//        // mResponse = SpecMeterComm.GR_LOW;
//        {
//            for (int i = 0; i < mIntNum; i++) {
//                Log.d(TAG, "AD correct. TestGainRange=" + mResponse
//                        + ", IntTime=" + m_fIntTime[i]);
//                result = false;
//                for (int j = 0; j < 3; j++) {
//                    if (mIsStoped) {
//                        break;
//                    }
//                    result = sampleAdZero(m_fIntTime[i], mResponse,
//                            m_fDataAD0[i], commType, SN);
//                    setAdZerotime(m_fIntTime[i]);
//                    setAdZeroAvgAD(Tool.getAvg(m_fDataAD0[i], 256));
//                    Log.d(TAG, "time=" + m_fIntTime[i] + ",");
//                    if (result) {
//                        break;
//                    }
//                }
//
//                setZeroAllProgress(i * 100 / mIntNum);
//            }
//        }// else
//        System.out.println("spiccommand m-fDataAD0 length is "
//                + m_fDataAD0.length);
//        CalcCore.multiZero_SetADZero(mResponse, 0, m_fDataAD0, SN);
//        mIsZeroingAD = false;
//        setZeroAllProgress(100);
    }

    private void ExchangeForInitTime(float initTime, int which) {
        if (which == 0) {
            new_initTime = initTime;
            new_Avg = 1;
            multiple = 1.0f;
        } else if (which == 1) {
            new_initTime = SumAddTime1[0][0];
            new_Avg = (int) SumAddTime1[0][1];
            multiple = initTime / new_initTime;
            for(int index = 21;index >= 0;index--){
                if (initTime >= SumAddTime1[index][0]){
                    new_initTime = SumAddTime1[index][0];
                    new_Avg = (int) SumAddTime1[index][1];
                    multiple = initTime / new_initTime;
                    break;
                }
            }

        } else if (which == 2) {
            new_initTime = SumAddTime2[0][0];
            new_Avg = (int) SumAddTime2[0][1];
            multiple = initTime / new_initTime;
            for(int index = 23;index >= 0;index--){
                if (initTime >= SumAddTime2[index][0]){
                    new_initTime = SumAddTime2[index][0];
                    new_Avg = (int) SumAddTime2[index][1];
                    multiple = initTime / new_initTime;
                    break;
                }
            }
        }
    }

    public String GetVersion(int commType, String SN) {
        int level = -1;
        for (int i = 0; i < 2; i++) {
            level = readBatteryLevel(commType, SN);
            if (level >= 0) {
                break;
            }
        }
        if (level < 0) {
            return "";
        }
        return getVersion(commType, SN);
    }
    public void GetDelay(int commType, String SN) {
        int level = -1;
        for (int i = 0; i < 2; i++) {
            level = readBatteryLevel(commType, SN);
            if (level >= 0) {
                break;
            }
        }
        if (level < 0) {
            return;
        }
        Log.d(TAG, "getDelay2.");

        int delay = 40;
        byte[] byteOut = new byte[64];
        byte[] byteSN = new byte[2];

        int iOutN = 0;
        byteOut[iOutN++] = CMD_WRITE_DELAY;

        mStatus = STATUS_SIMPLE_CMD;
        boolean rtn = mInterface.doCommand(byteOut, iOutN - 1, byteSN,
                LEN_VERSION, commType, SN);
        System.out.println("mStatus = STATUS_IDLE 1");
        mStatus = STATUS_IDLE;

        SISSdkController.iReadDelay = byteSN[0] & 0xFF + 256*(byteSN[1]&0xFF);
    }

    private String getVersion(int commType, String SN) {
        Log.d(TAG, "getVersion.");

        String version = "";
        byte[] byteOut = new byte[64];
        byte[] byteSN = new byte[11];

        int iOutN = 0;
        byteOut[iOutN++] = CMD_READ_VERSION;

        mStatus = STATUS_SIMPLE_CMD;
        boolean rtn = mInterface.doCommand(byteOut, iOutN - 1, byteSN,
                11, commType, SN);
        System.out.println("mStatus = STATUS_IDLE 1");
        mStatus = STATUS_IDLE;

        try {
            byte[] bVer = new byte[5];
            for(int i = 0;i<5;i++){
                bVer[i] = byteSN[i];
            }
            version = new String(bVer, "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return version;
        }
        return version;
    }

    public boolean setRestTime(int time, int commType, String SN) {
        Log.d(TAG, "setRestTime");
        byte[] byteOut = new byte[64];
        byte[] byteRest = new byte[1];
        int iOutN = 0;
        byteOut[iOutN++] = CMD_SET_REST_TIME;
        byteOut[iOutN++] = (byte) time;
        mStatus = STATUS_SIMPLE_CMD;
        boolean rtn = mInterface.doCommand(byteOut, iOutN - 1, byteRest, 1,
                commType, SN);
        System.out.println("mStatus = STATUS_IDLE 2");
        mStatus = STATUS_IDLE;
        if (!rtn) {
            return false;
        }

        if (byteRest[0] == RETURN_SUCCESS) {
            return true;
        } else {
            Log.d(TAG, "set restTime error!");
            return false;
        }
    }

    public boolean goInBootLoader(int commType, String SN) {
        Log.d(TAG, "goInBootLoader");
        byte[] byteOut = new byte[64];
        byte[] byteIn = new byte[1];
        int iOutN = 0;
        byteOut[iOutN++] = CMD_BOOT_LOADER;
        mStatus = STATUS_SIMPLE_CMD;
        boolean rtn = mInterface.doCommand(byteOut, iOutN - 1, byteIn, 1,
                commType, SN);
        if (!rtn) {
            return false;
        }
        System.out.println("mStatus = STATUS_IDLE 3");
        mStatus = STATUS_IDLE;

//        if (commType == COMM_BT) {
//            System.out.println("LeP closethread goInBootLoader");
//            BT_ConnectThread.getCommThread().setflashLoaderMode(true);
//            BT_ConnectThread.getCommThread().closethread();
//        } else if (commType == COMM_WIFI) {
//            mInterface.getWifiThread(SN).setflashLoaderMode(true);
//            mInterface.getWifiThread(SN).Clearthread();
//        }
        if (byteIn[0] == RETURN_SUCCESS) {
            Log.d(TAG, "goInBootLoader success!");
            return true;
        } else {
            Log.d(TAG, "goInBootLoader error!");
            return false;
        }
    }

    public int readRestTime(int commType, String SN) {
        Log.d(TAG, "readRestTime");
        return 0;
    }

    public boolean setWifiInfo(String[] wifiinfo, int commType, String SN) {
        Log.d(TAG, "setWifiInfo." + wifiinfo[0] + " " + wifiinfo[1] + " "
                + wifiinfo[2] + " " + wifiinfo[3]);
        int level = -1;
        int iCommNum = 2;
        for (int i = 0; i < iCommNum; i++) {
            level = readBatteryLevel(commType, SN);
            if (level >= 0) {
                break;
            }
        }
        byte[] byteOut = new byte[128];
        for (int j = 0; j < 128; j++)
            byteOut[j] = 0;
        byte[] byteIn = new byte[LEN_RETURN];
        int iOutN = 0;
        byteOut[iOutN++] = CMD_WRITE_WIFI_INFO;
        String data = wifiinfo[0] + ":" + wifiinfo[1] + ":" + wifiinfo[2] + ":"
                + wifiinfo[3] + ":";
        byte[] databyte = data.getBytes();
        for (int i = 0; i < databyte.length; i++)
            byteOut[iOutN++] = databyte[i];
        boolean rtn = mInterface.doCommand(byteOut, iOutN - 1, byteIn,
                LEN_RETURN, commType, SN);
        if (!rtn) {
            return false;
        }

        if (byteIn[0] == RETURN_SUCCESS) {
            return true;
        } else {
            Log.d(TAG, "set wifi error!");
            return false;
        }
    }

    public boolean readWifiInfo(String[] wifiinfo, int commType, String SN) {
        Log.d(TAG, "readWifiInfo.");
        byte[] byteOut = new byte[64];
        byte[] byteIn = new byte[128];
        int iOutN = 0;
        byteOut[iOutN++] = CMD_READ_WIFI_INFO;
        boolean rtn = mInterface.doCommand(byteOut, iOutN - 1, byteIn,
                LEN_WIFI, commType, SN);
        String info = "";
        if (rtn) {
            try {
                info = new String(byteIn, "US-ASCII");
                System.out.println(info);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            wifiinfo[0] = info.split(":")[0];
            wifiinfo[1] = info.split(":")[1];
            wifiinfo[2] = info.split(":")[2];
            wifiinfo[3] = info.split(":")[3];
        }
        System.out.println("wifiinfo.length= " + wifiinfo.length + "info="
                + wifiinfo[0] + "," + wifiinfo[1] + "," + wifiinfo[2] + ","
                + wifiinfo[3]);
        return rtn;
    }

    public synchronized int readBatteryLevel(int commType, String SN) {
        System.out.println("getBatteryInfo.");
        //读取电量
        if(mStatus == STATUS_SAMPLE){
            return 2;
        }
        byte[] byteOut = new byte[64];
        byte[] byteIn = new byte[2];
        int iOutN = 0;
        byteOut[iOutN++] = CMD_READ_BATTERY_INFO;
        mStatus = STATUS_BATTERY_LEVEL;
        boolean rtn = mInterface.doCommand(byteOut, iOutN - 1, byteIn, 2,
                commType, SN);
        System.out.println("mStatus = STATUS_IDLE 4");
        mStatus = STATUS_IDLE;
        batteryState = byteIn[1];
        Log.d(TAG, "getBatteryLevel level=" + byteIn[0]);
        if (byteIn != null && byteIn[0] > 0) {
            Batlevel = byteIn[0];
        }
        lastChargTime = System.currentTimeMillis();
        return byteIn[0];
    }

    private int getBatteryLevel(int commType, String SN) {
        if (mStatus != STATUS_BATTERY_LEVEL) {
            Log.w(TAG, "getBatteryLevel error. s=" + mStatus);
            return ERROR_STATUS;
        }

        byte[] byteIn = new byte[2];
        int rtn = 0;
//        rtn = mInterface.getRespond(CMD_READ_BATTERY_INFO, byteIn, 2, commType,
//                SN);
//        if (rtn != COMM_7B7DProtocol.RES_GET_RESPONSE_SUCCESS) {
//            Log.w(TAG, "getBatteryLevel error! res=" + rtn);
//            return -1;
//        }
        batteryState = byteIn[1];
        Log.d(TAG, "getBatteryLevel level=" + byteIn[0]);
        if (byteIn != null && byteIn[0] > 0) {
            Batlevel = byteIn[0];
        }
        lastChargTime = System.currentTimeMillis();
        return byteIn[0];
    }

    public EAdResult ReadEAd(int level, int commType, String SN) {
        /*
         * int level1 = -1; int iCommNum = 2; for (int i = 0; i < iCommNum; i++)
         * { level1 = readBatteryLevel(commType,SN); if (level1 >= 0) { break; }
         * }
         *
         * if (level1 < 0) return null;
         */
        return readEAd(level, commType, SN);
    }

    public class EAdResult {
        int level;
        float ad;
    }

    // SPIC_Command EAdResult = DetectorMeter EAdResult;
    private EAdResult readEAd(int level, int commType, String SN) {
        Log.d(TAG, "getEAd. level=" + level);
        if (level < 0 || level > 4) {
            return null;
        }

        byte[] byteOut = new byte[64];
        byte[] byteIn = new byte[5];

        int iOutN = 0;
        byteOut[iOutN++] = CMD_READ_E;
        if (level == 0) {
            byteOut[iOutN++] = (byte) 0x00;
        } else if (level == 1) {
            byteOut[iOutN++] = (byte) 0x01;
        } else if (level == 2) {
            byteOut[iOutN++] = (byte) 0x02;
        } else if (level == 3) {
            byteOut[iOutN++] = (byte) 0x03;
        } else if (level == 4) {
            byteOut[iOutN++] = (byte) 0x04;
        }

        mStatus = STATUS_SIMPLE_CMD;
        boolean rtn = mInterface.doCommand(byteOut, iOutN - 1, byteIn, 5,
                commType, SN);
        System.out.println("mStatus = STATUS_IDLE 5");
        mStatus = STATUS_IDLE;
        if (!rtn) {
            Log.d(TAG, "sample eAD rtn is false");
            return null;
        }

        EAdResult r = new EAdResult();

        if (byteIn[0] == (byte) 0x01) {
            r.level = 1;
        } else if (byteIn[0] == (byte) 0x02) {
            r.level = 2;
        } else if (byteIn[0] == (byte) 0x03) {
            r.level = 3;
        } else if (byteIn[0] == (byte) 0x04) {
            r.level = 4;
        }

        byte[] byteTemp = new byte[4];
        for (int i = 0; i < 4; i++) {
            byteTemp[i] = byteIn[i + 1];
        }
        int iAD = byteToInt(byteTemp);
        r.ad = Float.intBitsToFloat(iAD);
        System.out.println("光度r.level=" + r.level + " r.ad=" + r.ad);
        return r;
    }

    public static int byteToInt(byte[] b) {
        int s = 0;
        int s0 = b[0] & 0xff;// 最低位
        int s1 = b[1] & 0xff;
        int s2 = b[2] & 0xff;
        int s3 = b[3] & 0xff;
        s3 <<= 24;
        s2 <<= 16;
        s1 <<= 8;
        s = s0 | s1 | s2 | s3;
        return s;
    }

    public boolean cancelSample(int commType, String SN) {
        Log.d(TAG, "cancelSample.");
        if (mStatus != STATUS_SAMPLE) {
            Log.w(TAG, "cancelSample wrong status.");
            return false;
        }
        byte[] byteOut = new byte[10];
        byte[] byteIn = new byte[5];

        int iOutN = 0;
        byteOut[iOutN++] = CMD_CANCEL_SAMPLE;

        if (mStatus != STATUS_IDLE) {
            return false;
        }
        mStatus = STATUS_SIMPLE_CMD;
        boolean rtn = mInterface.doCommand(byteOut, iOutN - 1, byteIn, 5,
                commType, SN);
        System.out.println("mStatus = STATUS_IDLE 6");
        mStatus = STATUS_IDLE;
        if (!rtn) {
            return false;
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

    public boolean SampleAD(int avg, float inttime, int gainrange,
                            float[] dataAD, int count, int commType, String SN) {
        if (System.currentTimeMillis() - lastChargTime > 100000)
            readBatteryLevel(commType, SN);
        boolean bR = Impl_sampleAD(avg, inttime, gainrange, dataAD, count,
                commType, SN);
        // StringBuffer buf = null;
        // buf = new StringBuffer();
        // for(float i:dataAD)
        // buf.append(i+",");
        // System.out.println("inttime="+inttime+" avg="+avg+" 数据消息为："+buf.toString());
        System.out.println("Impl_sampleAD结果为" + bR);
        return bR;
    }

    private boolean Impl_sampleAD(int avg, float inttime, int gainrange,
                                  float[] dataAD, int count, int commType, String SN) {
        if (dataAD == null || dataAD.length < count) {
            return false;
        }
        mStatus = STATUS_SAMPLE;

        Log.d(TAG, SN + "sampleAD. inttime=" + inttime + ", gainrange="
                + gainrange);
        byte[] byteOut = new byte[64];
        int iOutN = 0;
        // 命令码，光谱1采样 0xB5，光谱1采样 0xB6。
        // 修改 2014.10.29 只有一个光谱，且命令为D5
        mSampleCmd = CMD_SAMPLE_1;
        byteOut[iOutN++] = mSampleCmd;
        if (inttime > mMaxSampleTime) {
            inttime = mMaxSampleTime;
        }
        int iIntTime = (int) (inttime + 0.5f);
        byteOut[iOutN++] = sampleType;// 00： 直流，01 ： 50HZ， 02 ： 60HZ。
        System.out.println("LeP sampleType = " + sampleType);
        if (sampleType == 0 || iIntTime > 500) {
            byteOut[iOutN++] = (byte) avg;
            new_initTime = inttime;
            multiple = 1.0f;
        } else {
            ExchangeForInitTime(inttime, sampleType);
            byteOut[iOutN++] = (byte) (new_Avg);
        }
        byteOut[iOutN++] = (byte) (gainrange == 0 ? 0x01 : 0x02);// 01高灵敏档，02低灵敏档。
        byte[] time = Tool.floatToByte(new_initTime);
        byteOut[iOutN++] = (byte) time[0];
        byteOut[iOutN++] = (byte) time[1];
        byteOut[iOutN++] = (byte) time[2];
        byteOut[iOutN++] = (byte) time[3];
        byteOut[iOutN++] = (byte) count;
        byteOut[iOutN++] = (byte) (count / 256);

        boolean res = false;

        int rtn = mInterface.sendCommand(byteOut, iOutN - 1, commType, SN);
        if (rtn != COMM_7B7DProtocol.RES_SEND_CMD_SUCCESS) {
            // mInterface.closeCom();
            Log.w(TAG, "Sample error! rtn=" + res);
            return false;
        }

        mADData = dataAD;
        mADCount = count;

        // 采样线程提早醒来，getSample中会有1s超时
        // mIntTime = iIntTime - 500;

        if (iIntTime - 500 > 0) {
            int NN = iIntTime / 5;
            for(int i = 0;i<NN;i++) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                if(!LeProxy.bRealConnect)
                    break;
            }
        }
        if(!LeProxy.bRealConnect)
            return false;

        res = true;
        rtn = getSample(commType, SN);
        if (rtn != ERROR_SUCCESS) {
            Log.w(TAG, "getSample error! error=" + rtn);
            res = false;
        }

        System.out.println("mStatus = STATUS_IDLE 7");
        mStatus = STATUS_IDLE;
        // mSampleThread = null;
        return res;
    }

    private int getSample(int commType, String SN) {
        if (mStatus != STATUS_SAMPLE) {
            Log.w(TAG, SN + "Sample AD status error. s=" + mStatus);
            return ERROR_STATUS;
        }

        int error = ERROR_SUCCESS;
        byte[] byteIn = new byte[2 * mADCount];
        Log.d(TAG, "rtn = mInterface.getRespond参数为" + mSampleCmd + ",bytein,"
                + mADCount);
        int rtn = 0;
//shanchu
//        rtn = mInterface.getRespond(mSampleCmd, byteIn, 2 * mADCount, commType,
//                SN);

        if (rtn != COMM_7B7DProtocol.RES_GET_RESPONSE_SUCCESS) {
            Log.w(TAG, "getSample error! res=" + rtn);
            error = ERROR_BAD_DATA;
            if (rtn == COMM_7B7DProtocol.RES_GET_RESPOND_NO_RESPOND) {
                error = ERROR_NO_RESPONSE;
            } else if (rtn == COMM_7B7DProtocol.RES_GET_RESPOND_DATA_NULL) {
                error = ERROR_TIMEOUT;
            }
            return error;
        }

        Log.d(TAG, "Sample AD com is OK!");
        for (int i = 0; i < mADCount; i++) {
            mADData[i] = 0;
            if (byteIn[i * 2] >= 0)
                mADData[i] += byteIn[i * 2];
            else
                mADData[i] += (256 + byteIn[i * 2]);

            if (byteIn[i * 2 + 1] >= 0)
                mADData[i] += byteIn[i * 2 + 1] * 256;
            else
                mADData[i] += (256 + byteIn[i * 2 + 1]) * 256;
            mADData[i] = mADData[i] * multiple;
        }
        System.out.println("sampleType="+sampleType+" new_initTime="+new_initTime+" multiple="+multiple+"  mADData = " + Arrays.toString(mADData));
        return error;
    }

    public synchronized int getStatus() {
        return mStatus;
    }

    public String GetSN(int commType, String SN) {
        int level = -1;
        for (int i = 0; i < 2; i++) {
            level = readBatteryLevel(commType, SN);
            if (level >= 0) {
                break;
            }
        }
        if (level < 0) {
            connect_result = false;
            return "";
        }
        return getSN(commType, SN);
    }

    private String getSN(int commType, String SN) {
        Log.d(TAG, "getSN.");

        String sn = "";
        byte[] byteOut = new byte[64];
        byte[] byteIn = new byte[LEN_SN_CONTAINER];

        int iOutN = 0;
        byteOut[iOutN++] = CMD_READ_SN;

        if (mStatus != STATUS_IDLE) {
            connect_result = false;
            return sn;
        }
        mStatus = STATUS_SIMPLE_CMD;
        boolean rtn = mInterface.doCommand(byteOut, iOutN - 1, byteIn,
                LEN_SN_CONTAINER, commType, SN);
        System.out.println("mStatus = STATUS_IDLE 8");
        mStatus = STATUS_IDLE;
        if (!rtn) {
            connect_result = false;
            return sn;
        }
        int len = 0;
        for (int i = 0; i < LEN_SN_CONTAINER; i++) {
            if (byteIn[i] == 0) {
                break;
            } else {
                len++;
            }
        }
        if (len == 0) {
            connect_result = true;
            return sn;
        }
        byte[] byteSN = new byte[len];
        for (int i = 0; i < len; i++) {
            byteSN[i] = byteIn[i];
        }
        try {
            sn = new String(byteSN, "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            connect_result = true;
            return sn;
        }
        connect_result = true;
        return sn;
    }

    public boolean SetSN(String sn, int commType, String SN) {
        int level = -1;
        for (int i = 0; i < 2; i++) {
            level = readBatteryLevel(commType, SN);
            if (level >= 0) {
                break;
            }
        }
        if (level < 0) {
            return false;
        }
        return setSN(sn, commType, SN);
    }

    private boolean setSN(String sn, int commType, String SN) {
        Log.d("SPIC", "setSN. sn=" + sn);
        if (sn.length() > LEN_SN_CONTAINER)
            return false;

        byte[] byteOut = new byte[LEN_SN_CONTAINER + 1];
        byte[] byteIn = new byte[1];

        int iOutN = 0;
        byteOut[iOutN++] = CMD_WRITE_SN;
        byte[] byteSn;
        try {
            byteSn = sn.getBytes("US-ASCII");
            for (int i = 0; i < sn.length(); i++) {
                byteOut[iOutN++] = byteSn[i];
            }
            for (int i = sn.length(); i < LEN_SN_CONTAINER; i++) {
                byteOut[iOutN++] = 0;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }

        if (mStatus != STATUS_IDLE) {
            return false;
        }
        mStatus = STATUS_SIMPLE_CMD;
        boolean rtn = mInterface.doCommand(byteOut, iOutN - 1, byteIn, 1,
                commType, SN);
        System.out.println("mStatus = STATUS_IDLE 9");
        mStatus = STATUS_IDLE;
        if (!rtn) {
            return false;
        }

        if (byteIn[0] == RES_WRITE_SN_SUCCESS) {
            return true;
        } else {
            return false;
        }
    }

    public int getType(int commType, String SN) {
        Log.d("SPIC", "getType.");

        byte[] byteOut = new byte[64];
        byte[] byteIn = new byte[1];

        int iOutN = 0;
        byteOut[iOutN++] = CMD_READ_TYPE;

        if (mStatus != STATUS_IDLE) {
            return -1;
        }
        mStatus = STATUS_SIMPLE_CMD;
        boolean rtn = mInterface.doCommand(byteOut, iOutN - 1, byteIn, 1,
                commType, SN);
        System.out.println("mStatus = STATUS_IDLE 10");
        mStatus = STATUS_IDLE;
        if (!rtn) {
            return -1;
        }

        if (byteIn[0] == (byte) 0x00) {
            return TYPE_SPIC_A;
        } else if (byteIn[0] == (byte) 0x01) {
            return TYPE_SPIC_B;
        } else if (byteIn[0] == (byte) 0x02) {
            return TYPE_SPIC_C;
        } else {
            return TYPE_SPIC_UNKNOW;
        }
    }

    public boolean setType(int type, int commType, String SN) {
        Log.d("SPIC", "setType. type=" + type);
        if (type <= 0 || type > 3)
            return false;

        byte[] byteOut = new byte[64];
        byte[] byteIn = new byte[1];

        int iOutN = 0;
        byteOut[iOutN++] = CMD_WRITE_TYPE;
        if (type == TYPE_SPIC_A) {
            byteOut[iOutN++] = (byte) 0x00;
        } else if (type == TYPE_SPIC_B) {
            byteOut[iOutN++] = (byte) 0x01;
        } else if (type == TYPE_SPIC_C) {
            byteOut[iOutN++] = (byte) 0x02;
        }

        if (mStatus != STATUS_IDLE) {
            return false;
        }
        mStatus = STATUS_SIMPLE_CMD;
        boolean rtn = mInterface.doCommand(byteOut, iOutN - 1, byteIn, 1,
                commType, SN);
        System.out.println("mStatus = STATUS_IDLE 11");
        mStatus = STATUS_IDLE;
        if (!rtn) {
            return false;
        }

        if (byteIn[0] == RES_WRITE_SN_SUCCESS) {
            return true;
        } else {
            return false;
        }
    }


    public EAdResult sampleEAd(int level, int commType, String SN) {
        EAdResult r = null;
        int num = 2;
        for (int i = 0; i < num; i++) {
            r = ReadEAd(level, commType, SN);
            if (r != null) {
                return r;
            }
            Log.w(TAG, "sampleEAd 1 error!");
        }
        return r;
    }















    float[] pAD;
    float[][] multiAD;






    public float getZeroADAvg() {
        return ZeroADAvg;
    }



    public boolean erasureAll(int commType, String SN) {
        return erasureArea(0, END4M, commType, SN);
    }

    // public boolean erasureArea(int startBlockNum,int blocknum, int commType,
    // String SN)
    // {
    // boolean rtn = false;
    // for(int i = 0; i<blocknum; i++)
    // {
    // rtn = erasure4K(startBlockNum, commType, SN);
    // if(!rtn)
    // return false;
    // startBlockNum+=4096;
    // }
    // return rtn;
    // }
    public boolean erasureArea(int start, int end, int commType, String SN) {
        byte[] byteOut = new byte[64];
        byte[] byteIn = new byte[1];
        int iOutN = 0;
        byteOut[iOutN++] = CMD_ERASURE_4KFLASH;
        // int start = startBlockNum*BLOCKSIZE;
        startaddr = new byte[4];
        startaddr[3] = (byte) start;
        startaddr[2] = (byte) (start / 256);
        startaddr[1] = (byte) (start / (65536));
        startaddr[0] = 0x00;
        endaddr = new byte[4];
        endaddr[3] = (byte) end;
        endaddr[2] = (byte) (end / 256);
        endaddr[1] = (byte) (end / (65536));
        endaddr[0] = (byte) (end / (65536 * 256));

        byteOut[iOutN++] = startaddr[0];
        byteOut[iOutN++] = startaddr[1];
        byteOut[iOutN++] = startaddr[2];
        byteOut[iOutN++] = startaddr[3];
        byteOut[iOutN++] = endaddr[0];
        byteOut[iOutN++] = endaddr[1];
        byteOut[iOutN++] = endaddr[2];
        byteOut[iOutN++] = endaddr[3];
        boolean rtn = mInterface.doCommand(byteOut, iOutN - 1, byteIn, 1,
                commType, SN);
        if (rtn && byteIn[0] == RETURN_SUCCESS) {
            System.out.println("erasureArea success");
            return true;
        }
        return false;
    }

    public boolean writeFlash(int startBlockNum, int length, float[] data,
                              int commType, String SN) {
        byte[] FtoB;
        int num = data.length * 4;
        byte[] databyte = new byte[num];
        for (int i = 0; i < data.length; i++) {
            FtoB = Tool.floatToByte(data[i]);
            databyte[i * 4] = FtoB[0];
            databyte[i * 4 + 1] = FtoB[1];
            databyte[i * 4 + 2] = FtoB[2];
            databyte[i * 4 + 3] = FtoB[3];
        }
        return writeFlash(startBlockNum, length, databyte, commType, SN);
    }

    public boolean writeFlash(int startBlockNum, int length, byte[] data,
                              int commType, String SN) {
        try {
            Thread.sleep(40);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int unit = 128;
        int num = length / unit;
        int last = length % unit;
        boolean res = false;
        int start = startBlockNum * BLOCKSIZE;
        if(startBlockNum == 2){
            start = BLOCKSIZE - 512;
        }
        int end = start + length - 1;

        System.out.println("end-->" + end + "  length = " + length);
        if(num > 0){
            for (int i = 0; i < num; i++) {
                System.out.println("LeP i  ==  " + i);
                res = write256Flash(start, unit, end, data, i * unit, commType, SN);
                if (!res)
                    return false;
                start += unit;
                try {
                    Thread.sleep(SISSdkController.iWriteDelay);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        if (last > 0) {
            try {
                Thread.sleep(SISSdkController.iWriteDelay);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("start " + start + "  last  ==  " + last);

            res = write256Flash(start, last, end, data, num * unit, commType,
                    SN);
            if (!res)
                return false;
        }
        Log.d(TAG, "num==" + num + "last=" + last + " start=" + start
                + " end =" + end);

        System.out.println("write over length is " + length);
        byte[] byteIn = new byte[1];
        //shanchu
        int res1=0;
//        int res1 = mInterface.getRespond(CMD_WRITE_FLASH, byteIn, 1, commType,
//                SN);
        System.out.println("mInterface.getRespond res1 = " + res1);
        if (res1 == COMM_7B7DProtocol.RES_GET_RESPONSE_SUCCESS
                && byteIn[0] == RETURN_SUCCESS) {
            System.out.println("receive respond success!");
            return true;
        } else
            return false;
    }

    public boolean write256Flash(int start, int length, int end, byte[] data,
                                 int datastart, int commType, String SN) {
        // System.out.println("start="+start+" length= "+length+"datastart="+datastart);
        // byteOut1 = new byte[data.length+64];
        // end = start + length;
        int iOutN = 0;
        startaddr = new byte[4];
        startaddr[3] = (byte) start;
        startaddr[2] = (byte) (start / 256);
        startaddr[1] = (byte) (start / (65536));
        startaddr[0] = 0x00;
        endaddr = new byte[4];
        endaddr[3] = (byte) end;
        endaddr[2] = (byte) (end / 256);
        endaddr[1] = (byte) (end / (65536));
        endaddr[0] = 0x00;
        byteOut1[iOutN++] = CMD_WRITE_FLASH;
        byteOut1[iOutN++] = startaddr[0];
        byteOut1[iOutN++] = startaddr[1];
        byteOut1[iOutN++] = startaddr[2];
        byteOut1[iOutN++] = startaddr[3];
        byteOut1[iOutN++] = (byte) (length / 256);
        byteOut1[iOutN++] = (byte) (length % 256);
        byteOut1[iOutN++] = endaddr[0];
        byteOut1[iOutN++] = endaddr[1];
        byteOut1[iOutN++] = endaddr[2];
        byteOut1[iOutN++] = endaddr[3];
        for (int j = 0; j < length; j++)
            byteOut1[iOutN++] = data[datastart + j];
        int rtn = mInterface.sendCommand(byteOut1, iOutN - 1, commType, SN);
        // boolean rtn = mInterface.doCommand(byteOut, iOutN - 1, byteIn,
        // 1,commType, SN);
        // 查询命令需要100ms延时
        if (rtn == COMM_7B7DProtocol.RES_SEND_CMD_SUCCESS) {
            // System.out.println("send success");
            return true;
        } else {
            System.out.println("send error");
            return false;
        }
    }

    public float[] readFlash(int startBlockNum, int length, int commType,
                             String SN) {
        byte[] getbyte = readFlashbyte(startBlockNum, length, commType, SN);
        return ByteToFloat(getbyte, getbyte.length);
    }

    public byte[] readFlashbyte(int startBlockNum, int length, int commType,
                                String SN) {
        byte[] byteIn = new byte[length];
        boolean res = false;
        float start = startBlockNum * BLOCKSIZE;
        if(startBlockNum == 2){
            start = BLOCKSIZE - 512;
        }
        address1[3] = (byte) start;
        address1[2] = (byte) (start / 256);
        address1[1] = (byte) (start / (65536));
        address1[0] = (byte) (start / (65536 * 256));
        address2[3] = (byte) length;
        address2[2] = (byte) (length / 256);
        address2[1] = (byte) (length / (65536));
        address2[0] = (byte) (length / (65536 * 256));
        // System.out.println("readFlash1---2");
        return readFlash(address1, address2, byteIn, commType, SN);
    }

    public byte[] readFlash(byte[] startAddress, byte[] endAddress,
                            byte[] byteIn, int commType, String SN) {
        System.out.println("readFlash2");
        int iOutN = 0;
        byteOut64[iOutN++] = CMD_READ_FLASH;
        byteOut64[iOutN++] = startAddress[0];
        byteOut64[iOutN++] = startAddress[1];
        byteOut64[iOutN++] = startAddress[2];
        byteOut64[iOutN++] = startAddress[3];
        byteOut64[iOutN++] = endAddress[0];
        byteOut64[iOutN++] = endAddress[1];
        byteOut64[iOutN++] = endAddress[2];
        byteOut64[iOutN++] = endAddress[3];
        boolean rtn = mInterface.doCommand(byteOut64, iOutN - 1, byteIn,
                byteIn.length, commType, SN);
        System.out.println("readflash docommand over");
        if (rtn) {
            return byteIn;
        }
        return null;
    }

    public boolean cancelSampling(int commType, String SN) {
        Log.d(TAG, "cancelSampling  " + SN);
        byte[] byteOut = new byte[64];
        int iOutN = 0;
        byteOut[iOutN++] = CMD_CANCEL_SAMPLE;
        int rtn = mInterface.sendCommand(byteOut, iOutN - 1, commType, SN);
        if (rtn != COMM_7B7DProtocol.RES_SEND_CMD_SUCCESS) {
            return false;
        }
        return true;
    }

    private int getSampleNum(float time) {
        // TODO Auto-generated method stub
        if (time < 0)
            return 0;
        int num = (int) (time / getMaxSampleTime());// num次累加采样
        return num + 1;
    }


    public static float getMax(float[] list, int num) {
        if (list == null || num > list.length) {
            Log.e(TAG, "list==null || num>list.length");
            return 0.0f;
        }
        float max = 0.0f;
        for (int i = 0; i < num; i++) {
            if (list[i] > max) {
                max = list[i];
            }
        }
        return max;
    }

    public int GetDevStatus() {
        return mStatus;
    }
//
//    public float GetMinIntTime() {
//        return CalcCore.getMinIntTime();
//    }
//
//    public float GetMaxIntTime() {
//        return CalcCore.getMaxIntTime();
//    }

//    public void SetMinIntTime(float min) {
//        CalcCore.setMinIntTime(min);
//        mIntTimeMin = min;
//    }
//
//    public void SetMaxIntTime(float max) {
//        CalcCore.setMaxIntTime(max);
//        mIntTimeMax = max;
//    }

    public int GetMaxAvgNum() {
        return mAvgNumMax;
    }

    public float getAutoIntTime() {
        return m_fAutoIntTime;
    }

    public float getAutoMaxAD() {
        return AutoMaxad;
    }

    public float getMaxAD() {
        return Max_AD;
    }

    public void SetMaxAvgNum(int max) {
        mAvgNumMax = max;
    }

    public void SetAvgNum(int iAvgNum) {
        mAvgNum = iAvgNum;
        if (mAvgNum > mAvgNumMax) {
            mAvgNum = mAvgNumMax;
        }
    }

    public void setIsTesting(boolean testing) {
        mIsTesting = testing;
        if (mIsTesting == false) {
            mIsSampleCanceled = true;
        }
        if(mIsSampling){
            mIsSampling = false;
        }
    }

    public boolean isTesting() {
        return mIsTesting;
    }

    public int GetAvgNum() {
        return mAvgNum;
    }

    public void SetAutoInt(boolean auto) {
        mIsAutoInt = auto;
    }

    public boolean GetAutoInt() {
        return mIsAutoInt;
    }

    public void setMaxSampleTime(float time) {
        mMaxSampleTime = (int) (time + 0.5f);
    }

    public float getMaxSampleTime() {
        return mMaxSampleTime;
    }

    public void setMinSampleTime(float time) {
        mMinSampleTime = (int) (time + 0.5f);
    }

    public float getMinSampleTime() {
        return mMinSampleTime;
    }

    public float getDefaultMaxSampleTime() {
        return DEFAULT_MAX_SAMPLE_TIME;
    }

    public float getDefaultMinSampleTime() {
        return DEFAULT_MIN_SAMPLE_TIME;
    }

    public int getGainRange() {
        return mSampleADGainRange;
    }

//    public void setGainRange(int gainrange) {
//        mSampleADGainRange = gainrange;
//        CalcCore.setBinRange(gainrange);
//    }



    public float GetIntTime() {
        return mIntTime;
    }

    public void setSampleType(byte type) {
        sampleType = type;
    }

    public void setMeterType(int type) {
        mMeterType = type;
    }

    public int getMeterType() {
        return mMeterType;
    }

    public void setMeterMethod(int method) {
        mMeterMethod = method;
    }

    public int getMeterMethod() {
        return mMeterMethod;
    }

    public void setAutoProgress(float pro) {
        if (getAutoProgress() == 100) {
            AutoProgress = pro;
        } else if (pro > getAutoProgress())
            AutoProgress = pro;
    }

    public float getAutoProgress() {
        return AutoProgress;
    }

    public void setMeasProgress(float pro) {
        if (getMeasProgress() == 100) {
            measProgress = pro;
        } else if (pro > getMeasProgress())
            measProgress = pro;
    }

    public float getMeasProgress() {
        return measProgress;
    }

    public void setZeroAllProgress(float pro) {
        if (getZeroAllProgress() == 100) {
            ZeroAllProgress = pro;
        } else if (pro > getZeroAllProgress())
            ZeroAllProgress = pro;
    }

    public float getZeroAllProgress() {
        return ZeroAllProgress;
    }

    public void setAdZerotime(float ad) {
        AdZerotime = ad;
    }

    public void setAdZeroAvgAD(float ad) {
        AdZeroAvgAD = ad;
    }

    public float getAdZerotime() {
        return AdZerotime;
    }

    public float getAdZeroAvgAD() {
        return AdZeroAvgAD;
    }

    public int getbatteryState() {
        return batteryState;
    }

    /**
     * @description:AD数组中大于某AD值的值个数
     */
    public static int getOverADNum(float[] list, int num, int iSetOverAd) {
        if (list == null || num > list.length) {
            return 0;
        }
        int iOverAdNum = 0;
        for (int i = 0; i < num; i++) {
            if (list[i] > iSetOverAd) {
                iOverAdNum++;
            }
        }
        return iOverAdNum;
    }




    public float[] ByteToFloat(byte[] src, int length) {
        if (src == null || length == 0)
            return null;
        int Flength = length / 4;
        float[] get = new float[Flength];
        byte[] get4 = new byte[4];
        for (int j = 0; j < Flength; j++)
            for (int k = 0; k < 4; k++) {
                get4[k] = src[j * 4 + k];
                get[j] = Tool.byteToFloat(get4);
            }
        return get;
    }

    public float[] ByteToInt(byte[] src, int length) {
        if (src == null)
            return null;
        int Flength = length / 4;
        float[] get = new float[Flength];
        byte[] get4 = new byte[4];
        for (int j = 0; j < Flength; j++)
            for (int k = 0; k < 4; k++) {
                get4[k] = src[j * 4 + k];
                get[j] = Tool.byteToInt(get4);
            }
        return get;
    }

    public byte[] floatToByte(float[] src) {
        if (src == null)
            return null;
        byte[] FtoB;
        int num = src.length * 4;
        byte[] databyte = new byte[num];
        for (int i = 0; i < src.length; i++) {
            FtoB = Tool.floatToByte(src[i]);
            databyte[i * 4] = FtoB[0];
            databyte[i * 4 + 1] = FtoB[1];
            databyte[i * 4 + 2] = FtoB[2];
            databyte[i * 4 + 3] = FtoB[3];
        }
        return databyte;
    }

    public float[] concatAll(float[] first, float[]... rest) {
        int totallLength = first.length;
        for (float[] array : rest) {
            totallLength += array.length;
        }
        float[] result = Arrays.copyOf(first, totallLength);
        int offset = first.length;
        for (float[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    public byte[] concatAll(byte[] first, byte[]... rest) {
        int totallLength = 0;
        if (first != null)
            totallLength = first.length;
        for (byte[] array : rest) {
            if (array != null)
                totallLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totallLength);
        int offset = first.length;
        for (byte[] array : rest) {
            if (array == null)
                continue;
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    public boolean setDelayTime(int time, int commType, String SN) {
        Log.d(TAG, "setDelayTime");
        byte[] byteOut = new byte[64];
        byte[] byteRest = new byte[2];
        int iOutN = 0;
        byteOut[iOutN++] = CMD_WRITE_DELAY;
        byteOut[iOutN++] = (byte) (time/256);
        byteOut[iOutN++] = (byte) (time%256);
        mStatus = STATUS_SIMPLE_CMD;
        boolean rtn = mInterface.doCommand(byteOut, iOutN - 1, byteRest, 1,
                commType, SN);
        System.out.println("mStatus = STATUS_IDLE 2");
        mStatus = STATUS_IDLE;
        if (!rtn) {
            return false;
        }

        System.out.println("setDelayTime = byteRest[0] = " + byteRest[0]);
        if (byteRest[0] == RETURN_SUCCESS) {
            return true;
        } else {
            Log.d(TAG, "set restTime error!");
            return false;
        }
    }
}
