package com.everfine.sis_sdk.common;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.Arrays;

public class Tool {
	
	
	public static final String Tag = "SIS-20";
	static DecimalFormat df5 = new DecimalFormat("#####0.0000000000");
	public static float fFomat(float f){
		return Float.parseFloat(df5.format(f));
	}
	
	public static float getMax(float[] list, int num) {
		if (list == null || num > list.length) {
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

	/**
	 * @description:AD�����д���ĳADֵ��ֵ����
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

	public static float getAvg(float[] list, int num) {
		if (list == null || num > list.length) {
			return 0.0f;
		}
		float fAvg = 0.0f;
		for (int i = 0; i < num; i++) {
			fAvg += list[i];
		}
		fAvg /= num;
		return fAvg;
	}

	public static boolean getDemoAD(int pixNum, float intTime, float[] fDataAD) {
		try {
			Thread.sleep((long) intTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < pixNum; i++) {
			fDataAD[i] = 8000 + 10.0f * intTime * (float) Math.random() + i
					* 10.0f + (float) Math.random();
			if (fDataAD[i] > 64000.0f)
				fDataAD[i] = 64000.0f;
		}
		return true;
	}

	public static boolean getDemoAdZero(int pixNum, float intTime,
			float[] fDataAD) {
		try {
			Thread.sleep((long) intTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < pixNum; i++) {
			fDataAD[i] = 8000 + 0.1f * intTime;
		}
		return true;
	}

	// Newton's D-value
	public static float getNewtonInt(float[] xa, float[] ya, int n, float x) {
		int i, k = 1;
		float u;
		for (i = 1; i <= n - 2; i++) {
			if (x <= xa[i]) {
				k = i;
				break;
			} else {
				k = n - 1;
			}
		}
		u = (x - xa[k - 1]) / (xa[k] - xa[k - 1]);
		float out = ya[k - 1] + u * (ya[k] - ya[k - 1]);
//		Log.d(Tag, "xa[k,k-1]="+xa[k]+" "+xa[k-1] + "ya[k,k-1]="+ya[k]+","+ya[k-1]+" u="+u+
//				"  getNewtonInt out = "+ out);
		return out;
	}

	public static int byteToInt(byte[] b) {
		int s = 0;
		int s0 = b[0] & 0xff;// ���λ
		int s1 = b[1] & 0xff;
		int s2 = b[2] & 0xff;
		int s3 = b[3] & 0xff;
		s3 <<= 24;
		s2 <<= 16;
		s1 <<= 8;
		s = s0 | s1 | s2 | s3;
		return s;
	}

	public static int byteToInt2(byte[] b) {
		int s = 0;
		int s0 = b[2] & 0xff;// ���λ
		int s1 = b[3] & 0xff;
		s1 <<= 8;
		s = s0 | s1;
		return s;
	}


	@SuppressLint("UseValueOf")
	public static byte[] intToByte(int number) {
		int temp = number;
		byte[] b = new byte[4];
		for (int i = 0; i < b.length; i++) {
			b[i] = new Integer(temp & 0xff).byteValue();// �����λ���������λ
			temp = temp >> 8; // ������8λ
		}
		return b;
	}

	public static byte[] floatToByte(float f) {
		int i = Float.floatToIntBits(f);
		return intToByte(i);
	}
	
	public static String printStringArrayToString(String[] s)
	{
		if(s ==null)
			return "";
		String str = "";
		for(String ss:s)
			str+=ss+",";
		return str;
	}
	
	public static String printfloatToString(float[] f) {
		if(f==null)
			return "";
		int num = f.length;
		if(num == 0)
			return "";
		if(num>300)
			num = 300;
		String str = "";
		for(int i = 0; i<num; i++)
			str+=f[i]+",";
		return str;
	}
	
	public static String saveFloatToFile(float[] f, String filename) {
		int num = f.length;
		if(num>100)
			num = 100;
		String str = "";
		for(int i = 0; i<num; i++)
			str+=f[i]+",";
		return str;
	}


	public static String byteArrayToString(byte[] b) {
		String str = "";
		for(int i =0; i<b.length; i++)
		str += Integer.toHexString(b[i]&0xff)+" ";
		return str;
	}
	
	public static String byteArrayToAsciiString(byte[] b) {
		String str = "";
		for(int i =0; i<b.length; i++)
			str += Byte.toString(b[i])+" ";
		return str;
	}

	public static float byteToFloat(byte[] b) {
		if (b == null || b.length < 4) {
			return 0f;
		}
		return Float.intBitsToFloat(byteToInt(b));
	}
	public static byte[] byteArrayGetEffect(byte[] b, int maxLen) {
		if (b == null) {
			return null;
		}
		int effectLen = maxLen;
		for(int i = 0; i<maxLen; i++)
 {
			if (b[i] == (byte) 0x00) {
				effectLen = i;
				Log.d(Tag, "effectLen="+effectLen);
				break;
			}
		}
		return Arrays.copyOf(b, effectLen);
	}
}
