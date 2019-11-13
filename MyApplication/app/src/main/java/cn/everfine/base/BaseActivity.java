package cn.everfine.base;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * author : ${CHENJIE} created at  2019-11-13 10:41 e_mail : chenjie_goodboy@163.com describle :
 */
public class BaseActivity extends Activity {
  protected Context mContext;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mContext=this;
  }
}
