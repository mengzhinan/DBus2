package com.duke.dbus2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.duke.dbus2.annotations.DInject2;
import com.duke.dbus2.core.DBus2;
import com.duke.dbus2.event.Event1;
import com.duke.dbus2.event.Event2;
import com.duke.dbus2.interfaces.DThread2;
import com.duke.dbus2.util.DUtils2;

public class MainActivity extends AppCompatActivity {
    private TextView textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBus2.getInstance().register(this);
        DBus2.getInstance().register(this);
        DBus2.getInstance().register(this);
        Log.v("sadfasdf", "page1 size = " + DBus2.getInstance().getObserverSize());
        textView1 = findViewById(R.id.textview1);
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBus2.getInstance().post(new Event1("event1"));
                startActivity(new Intent(MainActivity.this, Main2Activity.class));
                MainActivity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBus2.getInstance().unRegister(this);
        DBus2.getInstance().unRegister(this);
        DBus2.getInstance().unRegister(this);
        Log.v("sadfasdf", "page1 取消注册");
    }

    @DInject2()
    public void a1(Event1 e) {
        if (DUtils2.isNull(e)) {
            return;
        }
        Log.v("sadfasdf", "page1 收到消息1 =" + e.text + " , thread = " + Thread.currentThread().getName());
    }

    @DInject2(thread = DThread2.TYPE_CURRENT_CHILD_THREAD)
    public void a2(Event2 e) {
        if (DUtils2.isNull(e)) {
            return;
        }
        Log.v("sadfasdf", "page1 收到消息2 =" + e.text + " , thread = " + Thread.currentThread().getName());
    }
}