package com.duke.dbus2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.duke.dbus2.annotations.DInject2;
import com.duke.dbus2.core.DBus2;
import com.duke.dbus2.event.Event1;
import com.duke.dbus2.event.Event2;
import com.duke.dbus2.interfaces.DThread2;
import com.duke.dbus2.util.DUtils2;

public class Main3Activity extends AppCompatActivity {
    private TextView textView3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
//        DBus2.getInstance().register(this);
//        DBus2.getInstance().register(this);
//        DBus2.getInstance().register(this);
//        DBus2.getInstance().register(this);
//        DBus2.getInstance().register(this);
        App.getApp().setActivity(this);
//        Log.v("sadfasdf","page3 size = " + DBus2.getInstance().getObserverSize());
        textView3 = findViewById(R.id.textview3);
        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DBus2.getInstance().post(new Event1("event1"));
                startActivity(new Intent(Main3Activity.this, Main4Activity.class));
                Main3Activity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBus2.getInstance().unRegister(this);
        DBus2.getInstance().unRegister(this);
        Log.v("sadfasdf", "page3 取消注册");
    }

//    @DInject2()
//    public void a1(Event1 e) {
//        if (DUtils2.isNull(e)) {
//            return;
//        }
//        Log.v("sadfasdf", "page3 收到消息1 =" + e.text + " , thread = " + Thread.currentThread().getName());
//    }
//
//    @DInject2(thread = DThread2.TYPE_NEW_CHILD_THREAD)
//    public void a2(Event2 e) {
//        if (DUtils2.isNull(e)) {
//            return;
//        }
//        Log.v("sadfasdf", "page3 收到消息2 =" + e.text + " , thread = " + Thread.currentThread().getName());
//    }
}
