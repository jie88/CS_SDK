package cn.everfine.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;

public class DialogUtil {

    public static void showBleDialog(Context context, ListAdapter adapter){
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(context);
        listDialog.setTitle("蓝牙列表");
        listDialog.setAdapter(adapter,null);
        listDialog.show();
    }

    public static void showEditDialog(Context context, View view, DialogInterface.OnClickListener listener){

        AlertDialog.Builder inputDialog =
            new AlertDialog.Builder(context);
        inputDialog.setTitle("我是一个输入Dialog").setView(view);
        inputDialog.setPositiveButton("确定",
            listener).show();
    }

}
