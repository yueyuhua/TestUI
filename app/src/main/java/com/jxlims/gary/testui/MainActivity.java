package com.jxlims.gary.testui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.jxlims.gary.myprinter.WorkService;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    SimpleAdapter adapter =null;
    MyListView listView =null;
    List<HashMap<String, Object>> data;
    private static Handler mHandler = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new MHandler(this);
        WorkService.addHandler(mHandler);

        if (null == WorkService.workThread) {
            Intent intent = new Intent(this, WorkService.class);
            startService(intent);
        }

        listView=  (MyListView) this.findViewById(R.id.listView);
        data = new ArrayList<HashMap<String,Object>>();

            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("id", 1);
            item.put("name", "东莞骏翔");
            item.put("phone", "2016-03-01");
            item.put("address", "广东，东莞，松山湖科技九1");
            item.put("amount", "13717399813");
            data.add(item);

        HashMap<String, Object> item1 = new HashMap<String, Object>();
        item1.put("id", 2);
        item1.put("name", "立创检测");
        item1.put("phone", "2016-05-03");
        item1.put("address", "广东，东莞，松山湖科技九2");
        item1.put("amount", "869877575");
        data.add(item1);




        //创建SimpleAdapter适配器将数据绑定到item显示控件上
        adapter=  new SimpleAdapter(this, data, R.layout.item,
                new String[]{"name", "phone","address", "amount"}, new int[]{R.id.name, R.id.phone,R.id.address, R.id.amount});
        //实现列表的显示
        listView.setAdapter(adapter);

        //条目点击事件
        listView.setOnItemClickListener(new ItemClickListener());

        listView.setOnRefreshListener(new MyListView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // TODO Auto-generated method stub
                // 刷新操作

                new AsyncTask<Void, Void, Void>(){

                    @Override
                    protected Void doInBackground(Void... params) {
                        // TODO Auto-generated method stub

                        SystemClock.sleep(1000);
                        Boolean IsExists=false;
                        Iterator iter = data.iterator();
                        while (iter.hasNext()) {
                        HashMap<String, Object> entry = (HashMap<String, Object>) iter.next();

                            Object key = entry.get("id");
                            if(key.toString()=="4")
                                IsExists=true;
                        }

                        if(IsExists==false) {
                            HashMap<String, Object>   item3 = new HashMap<String, Object>();
                            item3.put("id", 4);
                            item3.put("name", "测试公司");
                            item3.put("phone", "2017-01-05");
                            item3.put("address", "广东，东莞，松山湖科技九3");
                            item3.put("amount", "87865437");
                            data.add(item3);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        // TODO Auto-generated method stub
                        super.onPostExecute(result);

                        adapter.notifyDataSetChanged();

                        listView.onRefreshComplete();

                    }

                }.execute();

            }
        });

    }
    //获取点击事件
    private final class ItemClickListener implements AdapterView.OnItemClickListener {

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView listView = (ListView) parent;
            HashMap<String, Object> data = (HashMap<String, Object>) listView.getItemAtPosition(position);
            String personid = data.get("id").toString();
         //   Toast.makeText(getApplicationContext(), personid, 1).show();
            Intent intent=new Intent();
            intent.setClass(MainActivity.this,OrderListActivity.class );
            intent.putExtra("postValue", "this Is Post Value:" +personid);
             startActivity(intent);
        }
    }

    private final class MenuItemClickListener implements  MenuItem.OnMenuItemClickListener
    {


        @Override
        public boolean onMenuItemClick(MenuItem item) {
            Intent intent=new Intent();
            intent.setClass(MainActivity.this,SystemSetActivity.class );
            intent.putExtra("postValue", "this Is Post Value:");
            startActivity(intent);
            return false;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem m=  menu.add(0,1,1,"设置");
        m.setOnMenuItemClickListener( new MenuItemClickListener());
        return super.onCreateOptionsMenu(menu);
    }
    static class MHandler extends Handler {

        WeakReference<MainActivity> mActivity;

        MHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity theActivity = mActivity.get();
            switch (msg.what) {

            }
        }
    }


}
