package cn.everfine.ui;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;


import com.ble.ble.BleService;
import com.cs.sis_sdk.sisble.SISLeProxy;
import com.cs.sis_sdk.util.SISLogUtil;
import com.example.administrator.myapplication.R;

import java.util.ArrayList;
import java.util.List;

import cn.everfine.base.BaseActivity;
import cn.everfine.base.SISQuickAdapter;
import cn.everfine.base.ViewHolder;
import cn.everfine.bean.SetBean;

import cn.everfine.util.DialogUtil;


public class SetActivity2 extends BaseActivity {


  private ArrayList<SetBean> setDatas = new ArrayList<>();
  private ListView setList;
  private ImageView imageback;
  private SISQuickAdapter<SetBean> setListAdapter;


  protected SISQuickAdapter<BluetoothDevice> bleAdapter;
  protected List<BluetoothDevice> mData = new ArrayList<>();
  EditText editText; // dialog 里面的输入框

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_setting);
    init();
    initBle();

  }


  private void init() {
    initBleDialogAdapter();
    initSetAdapter();
    initDialogEdit();
    setList = (ListView) findViewById(R.id.setting_list);
    setList.setAdapter(setListAdapter);

    imageback = (ImageView) findViewById(R.id.back);
    imageback.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
  }

  private void initDialogEdit() {
    editText = new EditText(mContext);
  }

  private void initSetAdapter() {


    String[] left = {getString(R.string.set_0), getString(R.string.disconnect),
        getString(R.string.setdefault), getString(R.string.setauto)
        , getString(R.string.search), getString(R.string.readflash)
        , getString(R.string.pingjun), getString(R.string.beice)
        , getString(R.string.getelec)};
    String[] right = {"", "", "200.0"
        , "5000.0", "", ""
        , "1", "DC", ""};


    for (int i = 0; i < left.length; i++) {
      SetBean bean = new SetBean(left[i], right[i]);
      setDatas.add(bean);
    }
    setListAdapter = new SISQuickAdapter<SetBean>(mContext, setDatas, R.layout.settinglistitem) {
      @Override
      public void convert(ViewHolder helper, final SetBean item, final int position) {
        helper.setText(R.id.tv_content, item.setLeftStr);
        helper.setText(R.id.tv_detail, item.setRightStr);
        helper.getConvertView().findViewById(R.id.btn_set).setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            listOnclick(item, position);
          }
        });
      }
    };
  }

  private void listOnclick(final SetBean item, int position) {

    switch (position) {
      case 2:
        showEditDialog(item);
        break;
      case 3:
        showEditDialog(item);
        break;
      case 4:
        startScanBle();
        showBleDialog();
        break;
      case 5:
        showEditDialog(item);
        break;
      case 6:
        showEditDialog(item);
        break;

    }

  }


  private void showEditDialog(final SetBean item) {

    DialogUtil.showEditDialog(mContext, editText, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        item.setRightStr = editText.getText().toString();
      }
    });
  }


  /**
   * 初始化蓝牙adapter
   */
  private void initBleDialogAdapter() {
    bleAdapter = new SISQuickAdapter<BluetoothDevice>(mContext, mData, R.layout.view_item_ble_dialog) {
      @Override
      public void convert(ViewHolder helper, BluetoothDevice item, final int position) {
        String name = item.getName();
        if (TextUtils.isEmpty(name)) {
          helper.setText(R.id.bleName, " mac: " + item.getAddress());
        } else {
          helper.setText(R.id.bleName, " mac: " + item.getAddress());
        }


      }
    };
  }

  /**
   * 弹出蓝牙dialog
   */
  private void showBleDialog() {
    DialogUtil.showBleDialog(mContext, bleAdapter, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        SISLeProxy.getInstance().scanLeDevice(false);
        connectBle(mData.get(which));
        dialog.dismiss();
      }
    });
  }


  /**
   * 初始化蓝牙
   */
  private void initBle() {

    //绑定服务
    bindService(new Intent(this, BleService.class), mConn, BIND_AUTO_CREATE);

    //添加蓝牙扫描回调
    SISLeProxy.getInstance().setOnScanListener(new SISLeProxy.OnScanListener() {
      @Override
      public void onScanStart() {

      }

      @Override
      public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        //扫描到蓝牙设备添加到列表
        addBleDevice(device);
        SISLogUtil.d("onScanning " + device.getName());
      }

      @Override
      public void onScanStop() {

      }
    });
  }


  private final ServiceConnection mConn = new ServiceConnection() {

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      SISLeProxy.getInstance().initBleService(service);
    }
  };

  /**
   * 开始扫描蓝牙
   */
  private void startScanBle() {
    SISLeProxy.getInstance().scanLeDevice(true);
  }

  /**
   * 停止扫描蓝牙
   */
  private void stopScanBle() {
    SISLeProxy.getInstance().scanLeDevice(false);
  }

  /**
   * 将扫描到的蓝牙设备添加到列表
   */
  private synchronized void addBleDevice(final BluetoothDevice bleDevice) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        //判断改蓝牙设备是否已经在列表中，不在则添加
        boolean exit = false;
        for (BluetoothDevice device : mData) {
          if (device.getAddress().equalsIgnoreCase(bleDevice.getAddress())) {
            exit = true;
            break;
          }
        }
        if (!exit) {
          mData.add(bleDevice);
          bleAdapter.notifyDataSetChanged();
        }
      }
    });

  }

  /**
   * 连接蓝牙
   */
  private void connectBle(BluetoothDevice device) {
    if (null != device) {
      SISLeProxy.getInstance().connect(device);
    }
  }
}
