package cn.everfine.ui;

import android.content.DialogInterface;

import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.cs.sis_sdk.SISSdkController;

import com.example.administrator.myapplication.R;

import java.util.ArrayList;


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


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_setting);
    //初始化SDK
    SISSdkController.getInstance().init(SetActivity2.this);
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
      case 0:
        break;
      case 1:
        //断开连接
        SISSdkController.getInstance().disconnected();
        break;
      case 2:
        showEditDialog(item);
        break;
      case 3:
        showEditDialog(item);
        break;
      case 4:
        //开始扫描
        SISSdkController.getInstance().startScanBle(mContext);
        break;
      case 5:
        showEditDialog(item);
        break;
      case 6:
        showEditDialog(item);
        break;
      case 7:
        break;
      case 8:
        //开始扫描
        SISSdkController.getInstance().getElec();
        break;
    }
  }
  private void showEditDialog(final SetBean item) {
    final EditText editText = new EditText(mContext);
    DialogUtil.showEditDialog(mContext, editText, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        item.setRightStr = editText.getText().toString();
      }
    });
  }
  @Override
  protected void onDestroy() {
    SISSdkController.getInstance().onDestroy();
    super.onDestroy();
  }
}
