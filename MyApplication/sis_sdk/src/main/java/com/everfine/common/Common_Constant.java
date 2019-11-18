
/**
 * @FILE:Command.java
 * @AUTHOR:Cai Zehui
 * @DATE:2015-1-12
 **/
package com.everfine.common;
/*******************************************
 * @CLASS:Command
 * @DESCRIPTION:
 * @AUTHOR:Cai Zehui
 * @VERSION:v1.0
 *******************************************/
public class Common_Constant {

	public static final String SOFT_SERVER_VER = "V1.00";
	public static final int CMD_SERVER_SOFT_VER = 0x100;
	public static final int CMD_BT_OPEN = 0x101;
	public static final int CMD_BT_CLOSE = 0X102;
	public static final int CMD_BT_ISOPEN = 0X103;
	public static final int CMD_BT_ONLINE_LIST = 0X104;
	public static final int CMD_LIST_BT_SN = 0X105;
	public static final int CMD_LIST_WIFI_SN = 0X106;
	public static final int CMD_LIST_WIFI_SERVER_SN = 0X107;
	public static final int CMD_CONFIG_SERVER = 0x108;
	public static final int CMD_READ_SERVER_CONFIG = 0x109;
	public static final int CMD_WL_RANGE = 0x110;
	public static final int CMD_CCD_PIX = 0X111;
	public static final int CMD_BT_SEARCH = 0X112;
	// ////////////////��ѯ���񲿷�////////////////////////
	public static final int CMD_DEVICE_SOFT_VER = 0X120;
	public static final int CMD_BT_SETTOWORK_SN = 0X121;
	public static final int CMD_MODEL = 0X122;
	public static final int CMD_DEV_ADDR = 0X123;
	public static final int CMD_SN = 0X124;
	public static final int CMD_MODIFY_SN = 0X125;
	public static final int CMD_SERVICE_IP = 0X126;
	public static final int CMD_MODIFY_SERVICE_IP = 0X127;
	public static final int CMD_SET_WIFI = 0x128;
	public static final int CMD_TYPE = 0x129;
	public static final int CMD_INTTIMERANGE = 0X12a;
	public static final int CMD_CLIENT_SN = 0x12b;
	// ///////////////�ɶ����������///////////////////////////
	public static final int CMD_CHARGE = 0X140;
	public static final int CMD_WIFI = 0X141;
	public static final int CMD_SET_TEST = 0X142;
	public static final int CMD_READ_CONFIG = 0X143;
	public static final int CMD_CONFIG_METER = 0X144;
	public static final int CMD_STOP_MEAS = 0X145;
	public static final int CMD_READ_ALL_VALUE = 0X146;
	public static final int CMD_READ_SPCTRUMDATA = 0X147;
	public static final int CMD_ZERO_ALL_PROGRESS = 0x148;
	public static final int CMD_CHECK_FILE = 0X149;
	public static final int CMD_READ_FLASH_PROGRESS = 0x14a;
	public static final int CMD_WRITE_FLASH_PROGRESS = 0x14b;
	public static final int CMD_AUTO_INTTIME_PROGRESS = 0x14c;
	public static final int CMD_MEAS_PROGRESS = 0x14d;

	// ///////////////ָ��������ѯ����///////////////////////////
	public static final int CMD_AUTO_INTTIME = 0X160;
	public static final int CMD_START_MEAS = 0X161;

	public static final int CMD_MEAS_DETECTORPH = 0X164;
	public static final int CMD_ZERO = 0X165;
	public static final int CMD_ZERO_ALL = 0X166;
	public static final int CMD_ZERO_DETECTORPH = 0X167;
	public static final int CMD_SAMPLE_AD = 0X168;
	public static final int CMD_SAMPLE_DETECTORPH_AD = 0X169;
	public static final int CMD_SPECTRUM_CAL = 0X170;
	public static final int CMD_DETECTOR_CAL = 0X171;
	public static final int CMD_WRITE_ALL_TO_FLASH = 0X176;
	public static final int CMD_READ_ALL_FROM_FLASH = 0x177;

	public static final int CMD_SAVE_SPECT_CAL = 0x17a;
	public static final int CMD_SET_REST_TIME = 0x17b;
	public static final int CMD_READ_REST_TIME = 0x17c;
	public static final int CMD_READ_DELAY = 0x17d;
	public static final int CMD_WRITE_DELAY = 0x17e;

	public static final int STATE_INITING = 0;
	public static final int STATE_FLASH_INIT = 1;
	public static final int STATE_INIT_OK = 2;
	public static final int COMM_BT = 0x90;
	public static final int COMM_WIFI = 0x91;
	public static final int GR_HIGH = 0;
	public static final int GR_LOW = 1;
	public static final int MSG_NEW_BT_DEVICE = 0x14;
	public static final int MSG_NEW_WIFI_DEVICE = 0x16;
	public static final int MSG_REMOVE_BT_DEVICE = 0x15;
	public static final int MSG_REMOVE_WIFI_DEVICE = 0x18;

	public static final int BLOCK_DMAT_HIGH = 2;
	public static final int BLOCK_DMAT_LOW = 66;
	public static final int BLOCK_E = 130;
	public static final int BLOCK_FLUXK = 131;
	public static final int BLOCK_CAL_HIGH = 132;
	public static final int BLOCK_CAL_LOW = 140;
	public static final int BLOCK_PHE_HIGH = 148;
	public static final int BLOCK_PHE_LOW = 149;
	public static final int BLOCK_WAVE_HIGH = 150;
	public static final int BLOCK_WAVE_LOW = 151;
	public static final int BLOCK_INIT = 152;

	public static final int LENGTH_DMAT_HIGH = 65536 * 4;
	public static final int LENGTH_DMAT_LOW = 65536 * 4;
	public static final int LENTH_E = 1 * 4;
	public static final int LENTH_FLUXK = 7 * 4;
	public static final int LENTH_CAL_HIGH = 8002 * 4;
	public static final int LENTH_CAL_LOW = 8002 * 4;
	public static final int LENTH_PHE_HIGH = 2 * 4;
	public static final int LENTH_PHE_LOW = 2 * 4;
	public static final int LENTH_WAVE_HIGH = 8 * 4;
	public static final int LENTH_WAVE_LOW = 8 * 4;
	public static final int LENTH_INIT = 8 * 4;
}

