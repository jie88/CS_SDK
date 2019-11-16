/**
 * @FILE:ReadMeterSocketOneData.java
 * @AUTHOR:Cai Zehui
 * @DATE:2015-1-14 ����10:29:49
 **/
package com.everfine.ble;

import android.util.Log;

public class ReadMeterSocketOneData {
	private static final String TAG = "ReadMeterSocketOneData";
	//private byte[] byteOneData = null;
	private boolean bDataOk = false;

	public byte cmd = 0x00;
	public byte[] para = null;
	public int iParaNum = 0;

	@SuppressWarnings("unused")
	public ReadMeterSocketOneData(int iHeadPos, byte[] byteBuffer, int iLen) {
		byte[] byteOneData = new byte[iLen];
		for (int i = 0; i < iLen; i++)
			byteOneData[i] = byteBuffer[iHeadPos + i];
		if (false) {
			String str = "";
			for (int i = 0; i < iLen; i++)
				str += Integer.toHexString(byteOneData[i] & 0xff) + " ";
			System.out.println("str=" + str);
		}
		int decode = decode(byteOneData);
		System.out.println("ReadMeterSocketOneData decode  = " + decode);
		bDataOk = (decode == 0);
	}

	public boolean isDataOk() {
		return bDataOk;
	}

	private int decode(byte[] byteOneData) {
		if (byteOneData == null)
			return 1;

		if (byteOneData[0] != '{' || byteOneData[1] != '{')
			return 2;

		if (byteOneData[byteOneData.length - 2] != '}'
				|| byteOneData[byteOneData.length - 1] != '}')
			return 3;

		int i;
		cmd = byteOneData[4];
		para = new byte[byteOneData.length];
		for (i = 0; i < byteOneData.length - 7; i++)
			para[i] = byteOneData[7 + i];

		iParaNum = doRead7B7D(para, byteOneData.length - 7);
		if (iParaNum == -1)
			return 4;
		iParaNum -= 3;
		if (iParaNum < 0)
			return 5;
		return 0;
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
						// ��������
						data1[iNewByteNum] = data[i];
						iNewByteNum++;

						// ��һ����Ҫ����
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

			// ��������
			data1[iNewByteNum] = data[i];
			iNewByteNum++;
		}

		// �޸�����
		for (i = 0; i < iNewByteNum; i++)
			data[i] = data1[i];

		if (iErrN > 0) {
			Log.d("Com7B7D", "doRead7B7D error, iErrN=" + iErrN);
			return -1;
		}

		return iNewByteNum;
	}
}
