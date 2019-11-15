package com.cs.sis_sdk.util;


import android.content.Context;

import java.util.Calendar;

/**
 * author : ${CHENJIE}
 * created at  2017/5/15 20:13
 * e_mail : chenjie_goodboy@163.com
 * describle :
 */
public class TimeUtil {
  private Calendar c;
  private Context ct;
  public TimeUtil(Context ct){
    c = Calendar.getInstance();
    this.ct = ct;
  }
  public byte[] gettime(){
    int hour = c.get(Calendar.HOUR_OF_DAY);
    int minute = c.get(Calendar.MINUTE);
    int second = c.get(Calendar.SECOND);
    byte[] time = {0x01,0x02,0x00,(byte)hour,(byte)minute,(byte)second};
    return time;
  }
  public byte[] getDate(){
    int year = c.get(Calendar.YEAR);
    int low = year%256;
    int high = year/256;
    int month = c.get(Calendar.MONTH)+1;
    int day = c.get(Calendar.DAY_OF_MONTH);
    byte[] date = {0x01,0x03,(byte)low,(byte)high,(byte)month,(byte)day};
    return date;
  }
}
