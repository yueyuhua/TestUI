package com.jxlims.gary.testui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class OtherActivity extends AppCompatActivity {
private   TextView myTextView=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
        Intent intent=getIntent();
        String postValue= intent.getStringExtra("postValue");
          myTextView=(TextView)findViewById(R.id.myTextView);

        myTextView.setText(postValue);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,1,1,"Exit");
        menu.add(0,2,2,"About");
        return super.onCreateOptionsMenu(menu);
    }


}
