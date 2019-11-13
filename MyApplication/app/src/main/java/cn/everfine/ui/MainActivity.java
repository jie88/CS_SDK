package cn.everfine.ui;
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.myapplication.R;

public class MainActivity extends Activity {
    private Button btn;
    private TextView getpermission,setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }
    void init()
    {
        //btn = (Button) findViewById(R.id.button);
        getpermission =(TextView) findViewById(R.id.getpermisstion);
        getpermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // tvw_search.setText"蓝牙ID";
                Intent intent = new Intent();

                intent.setClass(MainActivity.this, GetPermissionActivity.class);
                startActivity(intent);
                //finish();
            }
        });
        setting =(TextView) findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // tvw_search.setText"蓝牙ID";
                Intent intent = new Intent();

                intent.setClass(MainActivity.this, SetActivity2.class);
                startActivity(intent);
                //finish();
            }
        });

    }
}


