package com.duke.dbus2.core;

import com.duke.dbus2.entities.DMethod2;
import com.duke.dbus2.entities.ObserverWrapper2;
import com.duke.dbus2.util.DLog2;
import com.duke.dbus2.util.DUtils2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

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
    private static final HashMap<Class, ArrayList<DMethod2>> mClassMethodMap = new HashMap<>();

    /**
     * 观察者 对象 集合
     */
    private static final LinkedList<ObserverWrapper2> mObserverLinkedList = new LinkedList<>();

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
        DHelper2.clearInvalidObserver(mObserverLinkedList);
    }

    /**
     * 获取观察者数量
     *
     * @return 观察者数量
     */
    public int getObserverSize() {
        return mObserverLinkedList.size();
    }

    /**
     * 注册消息监听
     *
     * @param observer 观察者对象
     */
    public void register(Object observer) {
        long startTime = System.currentTimeMillis();
        if (DUtils2.isNull(observer)) {
            return;
        }
        if (DHelper2.isRegistered(observer, mObserverLinkedList)) {
            DLog2.logD(observer.getClass().getSimpleName() + " observer has registered event.");
            return;
        }
        ArrayList<DMethod2> list = DHelper2.addClassInfoAndGetMethods(observer, mClassMethodMap);
        if (!DUtils2.isEmpty(list)) {
            // 在链表头位置插入新的观察者对象
            mObserverLinkedList.addFirst(new ObserverWrapper2(observer, list));
        }
        clearInvalidObserver();
        DLog2.logTime(startTime, "register");
    }

    /**
     * 反注册。也可以不反注册，因为观察者对象使用了弱引用的方式
     *
     * @param observer 观察者对象
     */
    public void unRegister(Object observer) {
        long startTime = System.currentTimeMillis();
        if (DUtils2.isNull(observer)) {
            return;
        }
        DHelper2.removeObserver(observer, mObserverLinkedList);
        clearInvalidObserver();
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
        // 观察者包装类数量
        int listSize = mObserverLinkedList.size();
        // 观察者包装类
        ObserverWrapper2 observerWrapper2;
        // 实际观察者对象
        Object observer;
        // 观察者订阅方法数量
        int methodSize;
        // 观察者订阅方法对象
        DMethod2 dMethod2;
        // 遍历所有的观察者对象
        for (int i = 0; i < listSize; i++) {
            observerWrapper2 = mObserverLinkedList.get(i);
            if (observerWrapper2 == null ||
                    DUtils2.isEmpty(observerWrapper2.dMethod2ArrayList) ||
                    DUtils2.isEmpty(observerWrapper2.observerWeakRef)) {
                continue;
            }
            observer = observerWrapper2.observerWeakRef.get();
            // 遍历某观察者的所有订阅方法
            methodSize = observerWrapper2.dMethod2ArrayList.size();
            for (int j = 0; j < methodSize; j++) {
                dMethod2 = observerWrapper2.dMethod2ArrayList.get(j);
                if (DUtils2.isNull(dMethod2) ||
                        DUtils2.isNull(dMethod2.method) ||
                        DUtils2.isNull(dMethod2.paramClass) ||
                        DUtils2.isNull(observer) ||
                        event.getClass() != dMethod2.paramClass) {
                    // 如果参数类型不一致，不执行调用
                    continue;
                }
                DHelper2.switchThread(event, observer, dMethod2);
            }
        }
    }
}

