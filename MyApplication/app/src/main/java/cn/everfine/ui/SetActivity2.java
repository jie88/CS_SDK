package cn.everfine.ui;


import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;


import com.cs.sis_sdk.ble.SgBleManager;
import com.cs.sis_sdk.ble.data.ScanResult;
import com.cs.sis_sdk.ble.scan.ListScanCallback;
import com.cs.sis_sdk.ble.utils.BleLog;
import com.example.administrator.myapplication.R;

import java.util.ArrayList;

import cn.everfine.base.BaseActivity;
import cn.everfine.base.SISQuickAdapter;
import cn.everfine.base.ViewHolder;
import cn.everfine.bean.SetBean;
import cn.everfine.util.DialogUtil;

/**
 * author : ${CHENJIE} created at  2019-11-13 10:30 e_mail : chenjie_goodboy@163.com describle :
 */
public class SetActivity2 extends BaseActivity {


  private ArrayList<SetBean> setDatas = new ArrayList<>();
  private ListView setList;
  private ImageView imageback;
  private SISQuickAdapter<SetBean> setListAdapter;

  EditText editText; // dialog 里面的输入框

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_setting);
    init();

  }

  private void init() {
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
        break;
      case 5:
        showEditDialog(item);
        break;
      case 6:
        showEditDialog(item);
        break;

    }

    if (position == 4) {
      startScanBle();
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

  private void startScanBle() {
    SgBleManager bleManager = SgBleManager.getInstance(mContext);
    if (bleManager.isBlueEnable()) {
      bleManager.scanDevice(new ListScanCallback(1000 * 60) {
        @Override
        public void onScanning(ScanResult result) {
          BleLog.d("ScanResult :" + result);
        }

        @Override
        public void onScanComplete(ScanResult[] results) {

        }
      });
    }
  }

}
