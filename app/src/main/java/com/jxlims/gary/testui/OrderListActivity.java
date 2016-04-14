package com.jxlims.gary.testui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class OrderListActivity extends AppCompatActivity {
    SimpleAdapter adapter =null;
    MyListView listView =null;
    List<HashMap<String, Object>> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        listView=  (MyListView) this.findViewById(R.id.orderlistView);

        data = new ArrayList<HashMap<String,Object>>();

        HashMap<String, Object> item = new HashMap<String, Object>();
        item.put("id", 1);
        item.put("name", "PTS0117454");
        item.put("phone", "水");
        item.put("address", "PH值");
        item.put("amount", "未完成");
        data.add(item);

        HashMap<String, Object> item1 = new HashMap<String, Object>();
        item1.put("id", 2);
        item1.put("name", "PTS0117454");
        item1.put("phone", "水");
        item1.put("address", "PH值");
        item1.put("amount", "未完成");
        data.add(item1);



        //创建SimpleAdapter适配器将数据绑定到item显示控件上
        adapter=  new SimpleAdapter(this, data, R.layout.orderitem,
                new String[]{"name", "phone","address", "amount"}, new int[]{R.id.ordername, R.id.orderphone,R.id.orderaddress, R.id.orderamount});
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
                            item3.put("name", "PTS988757");
                            item3.put("phone", "废气");
                            item3.put("address", "甲烷");
                            item3.put("amount", "未完成");
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
            intent.setClass(OrderListActivity.this,SampleingListActivity.class );
            intent.putExtra("postValue", "this Is Post Value:" +personid);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem m= menu.add(0,1,1,"新增");


        return super.onCreateOptionsMenu(menu);
    }

}
