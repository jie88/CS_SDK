package com.everfine.sis_sdk.ble;

import android.app.Activity;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SPIC_Command {


//    public SPIC_Command(SPICMeterCalc spic, InputStream in, OutputStream out
public SPIC_Command( InputStream in, OutputStream out
                        ) {
        // m_MultiSampleAD = new float[7][mNumEffictivePixel];
        //CalcCore = spic;
        // SnSocket = socket;
        SnInStream = in;
        SnOutStream = out;

        mInterface = new COMM_7B7DProtocol(this);
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
    //public SPICMeterCalc CalcCore = null;
    private static final String TAG = "SPIC_Command";
    private static final android.app.Activity Activity = null;
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

}
