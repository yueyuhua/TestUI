package com.jxlims.gary.testui;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import android.os.Handler;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import com.jxlims.gary.myprinter.Global;
import com.jxlims.gary.myprinter.WorkService;

public class SystemSetActivity extends AppCompatActivity  implements
        AdapterView.OnItemClickListener {

    private ProgressDialog dialog;
    private static ListView listView;
    public static final String ICON = "ICON";
    public static final String PRINTERNAME = "PRINTERNAME";
    public static final String PRINTERMAC = "PRINTERMAC";
    private static List<Map<String, Object>> boundedPrinters;

    private static Handler mHandler = null;
    private static String TAG = "SystemSetActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_set);
        dialog = new ProgressDialog(this);
        boundedPrinters = getBoundedPrinters();
        listView = (ListView) findViewById(R.id.listviewsettingconnect);
        listView.setAdapter(new SimpleAdapter(this, boundedPrinters,
                R.layout.listprinter, new String[] {
                PRINTERNAME, PRINTERMAC }, new int[] {
                R.id.tvListItemPrinterName,
                R.id.tvListItemPrinterMac }));
        listView.setOnItemClickListener(this);

        mHandler = new MHandler(this);
        WorkService.addHandler(mHandler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WorkService.delHandler(mHandler);
        mHandler = null;
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long id) {
        // TODO Auto-generated method stub

        ListView listView = (ListView) arg0;
        HashMap<String, Object> data = (HashMap<String, Object>) listView.getItemAtPosition(position);
        String address = data.get(PRINTERMAC).toString();
        //boundedPrinters.get()
      //  String address = (String) boundedPrinters.get(position).get(PRINTERMAC);
        dialog.setMessage(Global.toast_connecting + " " + address);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
      //  Toast.makeText(getApplicationContext(), address, Toast.LENGTH_SHORT).show();
        WorkService.workThread.connectBt(address);
    }

    private List<Map<String, Object>> getBoundedPrinters() {

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            return list;
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                .getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a
                // ListView
                Map<String, Object> map = new HashMap<String, Object>();
                map.put(ICON, android.R.drawable.stat_sys_data_bluetooth);
                // Toast.makeText(this,
                // ""+device.getBluetoothClass().getMajorDeviceClass(),
                // Toast.LENGTH_LONG).show();
                map.put(PRINTERNAME, device.getName());
                map.put(PRINTERMAC, device.getAddress());
                list.add(map);
            }
        }
        return list;
    }

    static class MHandler extends Handler {

        WeakReference<SystemSetActivity> mActivity;

        MHandler(SystemSetActivity activity) {
            mActivity = new WeakReference<SystemSetActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SystemSetActivity theActivity = mActivity.get();
            switch (msg.what) {
                /**
                 * DrawerService 的 onStartCommand会发送这个消息
                 */

                case Global.MSG_WORKTHREAD_SEND_CONNECTBTRESULT: {
                    int result = msg.arg1;
                    Toast.makeText(
                            theActivity,
                            (result == 1) ? Global.toast_success
                                    : Global.toast_fail, Toast.LENGTH_SHORT).show();
                    Log.v(TAG, "Connect Result: " + result);
                    theActivity.dialog.cancel();
                    break;
                }

            }
        }
    }


}
