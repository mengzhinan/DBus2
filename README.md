# DBus2

**DBus** 是本人自己开发的，类似 EventBus 的消息订阅发布框架。链接：[DBus](https://github.com/mengzhinan/DBus "点击查看 DBus 源码")   --   [我的博客](https://blog.csdn.net/fesdgasdgasdg/article/details/79121783 "点击查看博客")

**DBus2** 是在 **DBus** 的基础上优化改进版本。

## 用法很简单：


'''java
public class MainActivity extends AppCompatActivity {
    private TextView textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 订阅消息
        DBus2.getInstance().register(this);
        
        textView1 = findViewById(R.id.textview1);
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                // 可在任意线程、任意位置发布消息事件
                DBus2.getInstance().post(new Event1("event1"));
                
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 移除消息订阅
        DBus2.getInstance().unRegister(this);
    }

    // 必须标记 DInject2 注解
    // 方法只能包含一个参数，方法修饰符和返回值无限制
    // Event1 参数类型，与 post 的参数类型相同。
    // 默认在 UI 线程中接收消息
    @DInject2()
    public void a1(Event1 e) {
        // Event1 接收 post 的参数
        // 收到消息，默认在 UI 线程中接收消息
    }

    // 必须标记 DInject2 注解
    // 方法只能包含一个参数，方法修饰符和返回值无限制
    // Event1 参数类型，与 post 的参数类型相同。
    // 默认在 UI 线程中接收消息，可以自己执行线程
    @DInject2(thread = DThread2.TYPE_CURRENT_CHILD_THREAD)
    public void a2(Event2 e) {
        // Event2 接收 post 的参数
        // 收到消息，在子线程中接收消息。
        // 如果 post 时已经是子线程了，则在当前子线程中接收消息
    }
}
'''


