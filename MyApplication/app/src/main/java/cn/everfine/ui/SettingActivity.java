//package cn.everfine.ui;
//
//import android.app.AlertDialog;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.content.res.Resources;
//import android.os.Bundle;
//
//import android.app.Activity;
//
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//
//import android.view.ViewGroup;
//
//import android.widget.ArrayAdapter;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//
//import com.cs.sis_sdk.ble.BleProxy;
//import com.example.administrator.myapplication.R;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import cn.everfine.base.SISQuickAdapter;
//import cn.everfine.base.ViewHolder;
//
//import cn.everfine.ble.SisSdk;
//import cn.everfine.util.DialogUtil;
//
//public class SettingActivity extends Activity implements BleProxy.OnScanListener {
//    ThreeListviewAdapter listViewAdapter;
//    private Button btn;
//    private Resources resources;
//    private ImageView imageback;
//    private ArrayList<String> beans;
//    private ArrayList<String> beandetail;
//    private BleProxy bleProxy;
//    public static ArrayAdapter<String> mNewBtDevicesArrayAdapter;
//    private SisSdk sisSdk;
//    protected SISQuickAdapter<String> listAdapter;
//    public static ArrayList<String> bleName;
//    public static ArrayList<String> bleAddress;
//    private DeviceListAdapter mDeviceListAdapter;
//    protected List<String> mData ;
//   private  AlertDialog.Builder listDialog;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_setting);
//        init();
//        mData = new ArrayList<>();
//        bleAddress= new ArrayList<>();
//        //mData.add("ble");
//        initBleDialogAdapter();
//    }
//    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
//
//        @Override
//        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    System.out.println("device.getName() = " + device.getName());
//                }
//            });
//        }
//    };
//
//
//    @Override
//    public void onScanStart() {
//        // TODO Auto-generated method stub
//        mNewBtDevicesArrayAdapter.clear();
//        mData.clear();
//
//    }
//    @Override
//    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
//        // TODO Auto-generated method stub
//        System.out.println("onLeScan device = " + device.getName() + "  " + device.getAddress());
//        //if (device != null && device.getName() != null && device.getName().length() == 16)
//        if((device.getName()!=null)&&(device.getAddress()!=null))
//                {
//                    //strBle=device.getName();
//                    //strBleAddress=device.getAddress();
//                    System.out.println("蓝牙搜索到" + device.getName());
//                    System.out.println("蓝牙搜索到" + device.getAddress());
//                    if(mData.contains(device.getName()))
//                    {
//                        System.out.println("bleName.contains:" + device.getName());
//                    }
//                    if (mData.size() > 0) {
//                        int isnot_have=0;
//                        for (int i = 0; i < mData.size(); i++) {
//
//                            if (!mData.get(i).equals(
//                                    device.getName())) {
//                                isnot_have++;
//
//                            }
//                            if(isnot_have==mData.size()){
//                                mData.add(device.getName());
//                                System.out.println("蓝牙搜索到mData添加" + device.getName());
//                                listAdapter.notifyDataSetChanged();
//                                bleAddress.add(device.getAddress());
//                            }
//                        }
//                    }
//                    else
//                    {
//                        mData.add(device.getName());
//                        listAdapter.notifyDataSetChanged();
//                    }
//
//
//                }
//                else
//                {
//                    System.out.println("蓝牙搜索到" + device.getName());
//                    System.out.println("蓝牙搜索到" + device.getAddress());
//                }
//
//    }
//    @Override
//    public void onScanStop() {
//        // TODO Auto-generated method stub
//    }
//    private class DeviceListAdapter extends BaseAdapter {
//        List<BluetoothDevice> deviceList = new ArrayList<>();
//
//        void addDevice(BluetoothDevice device) {
//            if (!deviceList.contains(device)) {
//                deviceList.add(device);
//                notifyDataSetChanged();
//            }
//        }
//
//        void clear() {
//            deviceList.clear();
//            notifyDataSetChanged();
//        }
//
//        @Override
//        public int getCount() {
//            return deviceList.size();
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public BluetoothDevice getItem(int position) {
//            return deviceList.get(position);
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            TextView textView;
//            if (convertView == null) {
//                textView = new TextView(SettingActivity.this);
//                textView.setPadding(24, 18, 24, 18);
//                textView.setGravity(Gravity.CENTER_VERTICAL);
//                convertView = textView;
//            } else {
//                textView = (TextView) convertView;
//            }
//            BluetoothDevice device = getItem(position);
//            textView.setText(device.getName() + '\n' + device.getAddress());
//            return convertView;
//        }
//    }
//    public  void showBleDialog(Context context, BaseAdapter adapter, DialogInterface.OnClickListener listener){
//         listDialog =
//                new AlertDialog.Builder(context);
//        listDialog.setTitle("蓝牙列表");
//        listDialog.setAdapter(adapter,listener);
//
//        listDialog.show();
//    }
//    private void listOnclick(int position)
//    {
//        switch (position)
//        {
//            case 0:
//
//                break;
//            case 4:
//                System.out.println("搜索蓝牙");
//                sisSdk.SearchBle();
//
//                DialogUtil.showBleDialog(SettingActivity.this,listAdapter);
//
//                break;
//        }
//
//    }
//    private void initBleDialogAdapter(){
//        listAdapter = new SISQuickAdapter<String>(SettingActivity.this, mData, R.layout.view_item_ble_dialog) {
//            @Override
//            public void convert(ViewHolder helper, String item, final int position) {
//
//                helper.setText(R.id.bleName,item);
//
//                helper.getConvertView().setOnClickListener(
//                        new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                System.out.println(position+"  "+mData.get(position));
//                                //sendmsd dismiss
//
//                                BleProxy.getInstance().strBleAddress=mData.get(position);
//                            }
//                        }
//                );
//            }
//        };
//    }
//
//    private void init(){
//        sisSdk=SisSdk.getInstance();
//        resources = getResources();
//        bleProxy= BleProxy.getInstance();
//        bleProxy.setOnScanListener(this);
//        mDeviceListAdapter = new DeviceListAdapter();
//        mNewBtDevicesArrayAdapter = new ArrayAdapter<String>(this,
//                R.layout.device_name);
//        beans = new ArrayList<String>();
//        beandetail = new ArrayList<String>();
//        beans.add(resources.getString(R.string.set_0));
//        beandetail.add("");
//        beans.add(resources.getString(R.string.disconnect));
//        beandetail.add("");
//        beans.add(resources.getString(R.string.setdefault));
//        beandetail.add("200.0");
//        beans.add(resources.getString(R.string.setauto));
//        beandetail.add("5000.0");
//        beans.add(resources.getString(R.string.search));
//        beandetail.add("");
//        beans.add(resources.getString(R.string.readflash));
//        beandetail.add("");
//        beans.add(resources.getString(R.string.pingjun));
//        beandetail.add("1");
//        beans.add(resources.getString(R.string.beice));
//        beandetail.add("DC");
//        beans.add(resources.getString(R.string.getelec));
//        beandetail.add("");
//        listViewAdapter = new ThreeListviewAdapter(this, beans,beandetail, false);
//
//        ListView settinglist = (ListView) findViewById(R.id.setting_list);
//        settinglist.setAdapter(listViewAdapter);
//        //btn = (Button) findViewById(R.id.button);
//        imageback  = (ImageView) findViewById(R.id.back);
//        imageback.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//
//
//    }
//    public class ThreeListviewAdapter extends BaseAdapter {
//
//        private Context context;
//        private ArrayList<String> beans;
//        private ArrayList<String> beandetail;
//        private boolean showIv;
//
//        public ThreeListviewAdapter(Context context, ArrayList<String> beans, ArrayList<String> beandetail,boolean showIv) {
//            this.beans = beans;
//            this.beandetail = beandetail;
//            this.context = context;
//            this.showIv = showIv;
//        }
//        public void setBean(ArrayList<String> beans)
//        {
//            this.beans=beans;
//        }
//        @Override
//        public int getCount() {
//            if (beans != null) {
//                return beans.size();
//            } else {
//                return 0;
//            }
//        }
//        @Override
//        public Object getItem(int position) {
//            if (beans != null) {
//                return beans.get(position);
//            } else {
//                return 0;
//            }
//        }
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//            final ViewHolder holder;
//            String beanstr = beans.get(position);
//            String beandetailstr = beandetail.get(position);
//            if (convertView == null) {
//                LayoutInflater inflater = LayoutInflater.from(context);
//                convertView = inflater.inflate(R.layout.settinglistitem, null);
//                holder = new ViewHolder();
//                holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
//                holder.tvDetail = (TextView) convertView.findViewById(R.id.tv_detail);
//                holder.bSet = (Button) convertView.findViewById(R.id.btn_set);
//
//                convertView.setTag(holder);
//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }
//            holder.bSet.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    listOnclick(position);
//
//                }
//            });
//            holder.tvContent.setText(beanstr);
//            holder.tvDetail.setText(beandetailstr);
//            return convertView;
//        }
//        class ViewHolder {
//
//            TextView tvContent;
//            TextView tvDetail;
//            Button bSet;
//        }
//    }
//
//}
//
