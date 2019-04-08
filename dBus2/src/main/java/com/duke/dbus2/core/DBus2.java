package com.duke.dbus2.core;

import android.support.annotation.NonNull;

import com.duke.dbus2.entities.DMethod2;
import com.duke.dbus2.entities.ObserverWrapper2;
import com.duke.dbus2.util.DLog2;
import com.duke.dbus2.util.DUtils2;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * author: duke
 * dateTime: 2019-04-05 16:15
 * description: 消息客车工具类，对外门面代理类
 */
public class DBus2 {

    public static final String TAG = DBus2.class.getSimpleName();

    /**
     * 观察者 类 和 注册方法 的集合
     */
    private static volatile HashMap<Class, ArrayList<DMethod2>> classMethodMap = new HashMap<>();

    /**
     * 观察者 对象 集合
     */
    private static ObserverWrapper2 observerWrapper2Header;

    private static class Inner {
        private static final DBus2 instance = new DBus2();
    }

    private DBus2() {
    }

    public static synchronized DBus2 getInstance() {
        return Inner.instance;
    }

    public void isDebug(boolean debug) {
        DLog2.isDebug(debug);
    }

    /**
     * 清理无效的观察者对象
     */
    private void clearInvalidObserver() {
        observerWrapper2Header = DHelper2.clearAndGetObserverHeader(observerWrapper2Header);
    }

    /**
     * 获取观察者数量
     *
     * @return 观察者数量
     */
    public int getObserverSize() {
        return DHelper2.getObserverSize(observerWrapper2Header);
    }

    /**
     * 检查当前观察者对象是否注册过
     *
     * @param observer 观察者对象
     * @return true：已经注册；false：未注册
     */
    private boolean isRegistered(@NonNull Object observer) {
        return DHelper2.isRegistered(observer, observerWrapper2Header);
    }

    /**
     * 注册消息监听
     *
     * @param observer 观察者对象
     */
    public void register(Object observer) {
        long startTime = System.currentTimeMillis();
        clearInvalidObserver();
        if (DUtils2.isNull(observer)) {
            return;
        }
        if (isRegistered(observer)) {
            DLog2.logD(observer.getClass().getSimpleName() + " observer has registered event.");
            return;
        }
        ArrayList<DMethod2> list = DHelper2.addClassInfoAndGetMethods(observer, classMethodMap);
        if (!DUtils2.isEmpty(list)) {
            // 在链表头位置插入新的观察者对象
            observerWrapper2Header = new ObserverWrapper2(observer, list, observerWrapper2Header);
        }
        DLog2.logTime(startTime, "register");
    }

    /**
     * 反注册。也可以不反注册，因为观察者对象使用了弱引用的方式
     *
     * @param observer 观察者对象
     */
    public void unRegister(Object observer) {
        long startTime = System.currentTimeMillis();
        clearInvalidObserver();
        if (DUtils2.isNull(observer)) {
            return;
        }
        if (DUtils2.isNull(observerWrapper2Header)) {
            return;
        }
        if (!DUtils2.isEmpty(observerWrapper2Header.observerWeakRef) &&
                observerWrapper2Header.observerWeakRef.get() == observer) {
            // 发现等待移除的节点，是头节点
            observerWrapper2Header = observerWrapper2Header.next;
            return;
        }

        ObserverWrapper2 preObserver = observerWrapper2Header;
        ObserverWrapper2 indexObserver = preObserver.next;
        while (indexObserver != null) {
            if (!DUtils2.isEmpty(indexObserver.observerWeakRef) &&
                    indexObserver.observerWeakRef.get() == observer) {
                // 当前指针向后移动
                indexObserver = indexObserver.next;
                // 丢弃链表中无效的元素
                preObserver.next = indexObserver;
                // 已经移除了特定观察者对象，退出循环
                break;
            } else {
                // 链表前一个参考标记向后移动
                preObserver = indexObserver;
                // 当前指针向后移动
                indexObserver = indexObserver.next;
            }
        }

        DLog2.logTime(startTime, "unRegister");
    }

    /**
     * 发送消息
     *
     * @param event 事件数据对象
     */
    public void post(Object event) {
        if (DUtils2.isNull(event)) {
            return;
        }
        if (DUtils2.isNull(observerWrapper2Header)) {
            return;
        }
        ObserverWrapper2 node = observerWrapper2Header;
        Object observer;
        // 循环所有的观察者
        while (!DUtils2.isNull(node)) {
            if (DUtils2.isEmpty(node.dMethod2ArrayList)) {
                // 下一个循环
                node = node.next;
                continue;
            }
            if (DUtils2.isEmpty(node.observerWeakRef)) {
                // 下一个循环
                node = node.next;
                continue;
            }
            observer = node.observerWeakRef.get();
            if (DUtils2.isNull(observer)) {
                // 下一个循环
                node = node.next;
                continue;
            }
            DHelper2.findAndInvokeMethod(event, observer, node.dMethod2ArrayList);
            // 下一个循环
            node = node.next;
        }
    }

}

