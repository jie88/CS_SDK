package com.cs.sis_sdk.ble;


import android.app.Application;

public class SISBleManager {



  public void init(Application app) {

  }

  public static SISBleManager getInstance() {
    return SISBleManager.BleManagerHolder.sBleManager;
  }

  private SISBleManager() {
  }

  private static class BleManagerHolder {
    private static final SISBleManager sBleManager = new SISBleManager();

    private BleManagerHolder() {
    }
  }
}
