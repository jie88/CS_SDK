package com.everfine.ble;

import android.annotation.SuppressLint;
import android.util.Log;

import com.everfine.SISSdkController;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

@SuppressLint("SimpleDateFormat")
public class COMM_7B7DProtocol {
	private static final int COMM_BT = 0x90;
	private static final int COMM_WIFI = 0x91;

	private static final int LOWER_MSG = 0x10;
	public static final int RES_SEND_CMD_SUCCESS = 200;
	public static final int RES_SEND_CMD_FAILED = 201;
	public static final int RES_GET_RESPONSE_SUCCESS = 202;
	public static final int RES_SEND_CMD_PARAM_ERROR = 203;
	public static final int RES_GET_RESPOND_PARAM_ERROR = 204;
	public static final int RES_GET_RESPOND_NO_RESPOND = 205;
	public static final int RES_GET_RESPOND_DATA_NULL = 206;
	public static final int RES_GET_RESPOND_CMD_ERROR = 207;
	public static final int RES_GET_RESPOND_FORMAT_ERROR = 208;
	public static final int RES_GET_RESPOND_LENGTH_ERROR = 209;
	public static final int RES_CMD_SUCCESS = 210;

	static final byte CMD_WRITE_FLASH = (byte) 0xCD;
	static final byte CMD_READ_FLASH = (byte) 0xCC;

	private static final byte DST_ADDRESS = (byte) 0xB2;
	private static final byte SOURCE_ADDRESS = (byte) 0x30;

	private final int mTryTime = 4;

	private Socket DevSocket = null;
	private InputStream DevInStream = null;
	// private byte [] byteOut = new byte[512];
	public boolean needprint = false;
	public int writetime = 0;
	private int wrongtime = 0;
	public SPIC_Command spic_command = null;
	private static final String TAG = "COMM_7B7D";


	public COMM_7B7DProtocol(SPIC_Command spic) {
		spic_command = spic;
	}
	public COMM_7B7DProtocol( ) {

	}

	public int sendCommand(byte OutD[], int nW, int commType, String SN) {
		if (OutD == null || nW < 0) {
			return RES_SEND_CMD_PARAM_ERROR;
		}


		byte bCmd = 0x00;
		bCmd = OutD[0];
		if (OutD[0] != CMD_WRITE_FLASH && OutD[0] != CMD_READ_FLASH)
			Log.d(TAG,
					SN + " sendCommand. Cmd = "
							+ Integer.toHexString(bCmd & 0xff));
		// Arrays.fill(byteOut, (byte) 0);
		int iOutN = 0;
		byte[] byteOut = new byte[512];
		// byte[] bat = {0x7b,0x7b,(byte) 0xb2,0x30,(byte) 0xd0,0x00,0x00,(byte)
		// 0xb2,0x7d,0x7d};
		// ͷ
		byteOut[iOutN++] = 0x7b;
		byteOut[iOutN++] = 0x7b;
		// Ŀ���ַ
		byteOut[iOutN++] = DST_ADDRESS;
		// Դ��ַ
		byteOut[iOutN++] = SOURCE_ADDRESS;
		// ����
		byteOut[iOutN++] = bCmd;
		// ����
		byteOut[iOutN++] = (byte) (nW / 100);
		byteOut[iOutN++] = (byte) (nW % 100);
		// ����
		for (int i = 1; i <= nW; i++) {
			byteOut[iOutN++] = OutD[i];
		}

		// LRC
		byteOut[iOutN] = 0x00;
		for (int i = 2; i < iOutN; i++) {
			byteOut[iOutN] += byteOut[i];
		}

		// byteOut[iOutN] = (byte)byteOut[iOutN];
		iOutN++;
		// β
		byteOut[iOutN++] = 0x7d;
		byteOut[iOutN++] = 0x7d;
		String str = "";
		for (int i = 0; i < iOutN; i++)
			str += Integer.toHexString(byteOut[i] & 0xff) + " ";
		iOutN = doWrite7B7D(byteOut, iOutN);
		// System.out.println("iOutN ="+iOutN);
		// for (int i = 0; i < iOutN; i++)
		// str += Integer.toHexString(byteOut[i] & 0xff) + " ";
		// System.out.println("str=" + str);
		// System.out.println(new String(byteOut));
		if (commType == COMM_BT)
			try {

					SISSdkController.getInstance().mLeProxy.send(byteOut, iOutN);

			} catch (Exception e) {
				Log.d(TAG, "LeP BT Write exception e = " + e.toString());

			}

		return RES_SEND_CMD_SUCCESS;
	}

	public synchronized boolean doCommand(byte OutD[], int nW, byte InD[],
			int nR, int commType, String SN) {
		boolean res = true;

		if (doCommandImpl(OutD, nW, InD, nR, commType, SN) != RES_CMD_SUCCESS) {
			Log.w(TAG, "doCommand error!");
			return false;
		}
		return res;
	}

	private int doCommandImpl(byte OutD[], int nW, byte InD[], int nR,
			int commType, String SN) {
		Log.d(TAG, "ctrlCom.");
		int res = 0;

		//
		//
		res = sendCommand(OutD, nW, commType, SN);
		if (res != RES_SEND_CMD_SUCCESS) {
			Log.w(TAG, "ctrlCom error! res=" + res);
			return res;
		}

		//
//		res = getRespond(OutD[0], InD, nR, commType, SN);
//		if (res != RES_GET_RESPONSE_SUCCESS) {
//			Log.w(TAG, "ctrlCom error! res=" + res);
//			return res;
//		}

		return RES_CMD_SUCCESS;
	}

	private static String byteToString(byte[] buf, int num) {
		String str = "";
		for (int i = 0; i < num; i++) {
			str += Integer.toHexString(buf[i] & 0xff) + " ";
		}
		return str;
	}

//	public int getRespond(byte bCmd, byte InD[], int nR, int commType, String SN) {
//		Log.d(TAG,
//				SN + "  getRespond. Cmd = " + Integer.toHexString(bCmd & 0xff));
//		if (InD == null || nR < 0) {
//			if (InD == null)
//				Log.e(TAG, "InD is null!");
//			if (nR < 0)
//				Log.e(TAG, "nR < 0");
//			return RES_GET_RESPOND_PARAM_ERROR;
//		}
//		int delay = 7000;
//		if (bCmd == SPIC_Command.CMD_WRITE_FLASH) {
//			delay = 500;
//		}else if(bCmd == SPIC_Command.CMD_READ_FLASH){
//			if(nR < 1000)
//				delay = 1000;
//			else
//				delay = 300000;
//		}else if(bCmd == SPIC_Command.CMD_READ_BATTERY_INFO){
//			delay = 500;
//		}
//		if (bCmd == SPIC_Command.CMD_SAMPLE_1) {
//			delay = 15000 * spic_command.GetAvgNum();
//		}
//
//
//			if (commType == COMM_BT) {
//			if (BT_ConnectThread.getCommThread() == null)
//				return RES_GET_RESPOND_DATA_NULL;
//			ReadMeterSocketOneData data = null;
//			data = BT_ConnectThread.getCommThread().findMeterSocketOneData(
//					bCmd, delay);
//			if (data == null) {
//				Log.e(TAG, "LeProxy COMM_7B7D BT data == null wrongtime = " + wrongtime);
//				wrongtime++;
//				if (BT_ConnectThread.getCommThread() != null && wrongtime >= 5)
//					BT_ConnectThread.getCommThread().closethread();
//				return RES_GET_RESPOND_DATA_NULL;
//			}
//			wrongtime = 0;
//			System.out.println("LeProxy data.iParaNum = " + data.iParaNum + "  nR=" + nR);
//			if(data.cmd == SPIC_Command.CMD_READ_VERSION){
//
//			}else if (nR != data.iParaNum) {
//				Log.e(TAG, "LeProxy RES_GET_RESPOND_LENGTH_ERROR" + "nR=" + nR
//						+ ",paranum=" + data.iParaNum);
//				return RES_GET_RESPOND_LENGTH_ERROR;
//			}
//			for (int i = 0; i < nR; i++)
//				InD[i] = data.para[i];
//			Log.d(TAG, "RES_GET_RESPONSE_SUCCESS");
//			return RES_GET_RESPONSE_SUCCESS;
//		}
//		return RES_GET_RESPOND_PARAM_ERROR;
//	}



	/**
	 * @description:�����ݻ�LRC�к���0x7b 0x7d �������֮�����Ч����0x80
	 * @author:Cai Zehui
	 * @return:int
	 * @param data
	 * @param n
	 * @return
	 */
	private int doWrite7B7D(byte[] data, int n) {
		if (data == null || n <= 0) {
			return -1;
		}
		int i, m;
		byte[] data1 = new byte[n];
		for (i = 0; i < n; i++) {
			data1[i] = data[i];
		}

		for (i = 0, m = 0; i < n; i++) {
			data[m] = data1[i];
			m++;

			if (i >= 7 && i < (n - 2)) {
				if (data1[i] == 0x7B || data1[i] == 0x7D) {
					data[m] = (byte) 0x80;
					m++;
				}
			}
		}
		return m;
	}

	private int doRead7B7D(byte[] data, int n) {
		if (data == null || n <= 0) {
			return -1;
		}
		byte[] data1 = new byte[n];
		int iNewByteNum = 0;
		int i;

		int iErrN = 0;
		for (i = 0; i < n; i++) {
			if (data[i] == (byte) 0x7B || data[i] == (byte) 0x7D) {
				if ((i + 1) <= (n - 1)) {
					if (data[i + 1] == (byte) 0x80) {
						data1[iNewByteNum] = data[i];
						iNewByteNum++;
						i++;
						continue;
					} else {
						if (data[i + 1] != (byte) 0x7B
								&& data[i + 1] != (byte) 0x7D) {
							Log.d(TAG, "doRead7B7D error,i=" + i + ",data="
									+ data[i + 1]);
							iErrN++;
						}
					}
				}
			}

			data1[iNewByteNum] = data[i];
			iNewByteNum++;
		}

		for (i = 0; i < iNewByteNum; i++)
			data[i] = data1[i];

		if (iErrN > 0) {
			Log.d("Com7B7D", "doRead7B7D error, iErrN=" + iErrN);
			return -1;
		}

		return iNewByteNum;
	}
}
