package com.duke.dbus2;

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

public class Main4Activity extends AppCompatActivity {
    private TextView textView4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        DBus2.getInstance().register(this);
        DBus2.getInstance().register(this);
        DBus2.getInstance().register(this);
        Log.v("sadfasdf", "page4 size = " + DBus2.getInstance().getObserverSize());
        textView4 = findViewById(R.id.textview4);
        textView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBus2.getInstance().unRegister(Main4Activity.this);
                DBus2.getInstance().unRegister(Main4Activity.this);
                DBus2.getInstance().unRegister(Main4Activity.this);
                DBus2.getInstance().unRegister(Main4Activity.this);
                DBus2.getInstance().unRegister(Main4Activity.this);
                DBus2.getInstance().unRegister(Main4Activity.this);
                Log.v("sadfasdf", "page4 size = " + DBus2.getInstance().getObserverSize());
                Log.v("sadfasdf", "page4 weak = " + App.getApp().getActivity());
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        Log.v("sadfasdf", "page4 size = " + DBus2.getInstance().getObserverSize());
//                        DBus2.getInstance().post(new Event1("event1"));
//                        DBus2.getInstance().post(new Event2("event2"));
                    }
                };
                new Thread(runnable).start();
//                DBus2.getInstance().post(new Event1("event1"));
//                DBus2.getInstance().post(new Event2("event2"));
//                startActivity(new Intent(Main4Activity.this, MainActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBus2.getInstance().unRegister(this);
        DBus2.getInstance().unRegister(this);
        DBus2.getInstance().unRegister(this);
        Log.v("sadfasdf", "page4 取消注册");
    }

    @DInject2()
    public void a1(Event1 e) {
        if (DUtils2.isNull(e)) {
            return;
        }
        Log.v("sadfasdf", "page4 收到消息1 =" + e.text + " , thread = " + Thread.currentThread().getName());
    }

    @DInject2(thread = DThread2.TYPE_CURRENT_CHILD_THREAD)
    public void a2(Event2 e) {
        if (DUtils2.isNull(e)) {
            return;
        }
        Log.v("sadfasdf", "page4 收到消息2 =" + e.text + " , thread = " + Thread.currentThread().getName());
    }
}
