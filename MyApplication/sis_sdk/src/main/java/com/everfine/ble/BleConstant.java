package com.everfine.ble;


/**
 * 蓝牙数据
 */
public class BleConstant {

  /**
   * 帧格式为：帧头（0x7b 0x7b） + 目的地址（1byte）+ 源地址（1byte） + 命令（1byte） + 长度（2byte） + 数据（n*byte） + LRC（1byte）
   * + 帧尾（0x7d 0x7d）；
   */

  //帧头 第一字节
  public static final int BT_DATA_HEAD_0 = (byte) 0x7b;
  //帧头 第二字节
  public static final int BT_DATA_HEAD_1 = (byte) 0x7b;

  //目的地址 字节
  public static final int BT_DATA_DST_ADDRESS = (byte) 0xB2;

  //源地址 字节
  public static final int BT_DATA_SOURCE_ADDRESS = (byte) 0x30;

  //读取电量信息命令
  public static final int BT_DATA_READ_BATTERY_INFO = (byte) 0xD0;


}
