package com.duke.dbus2.entities;

import com.duke.dbus2.core.DHelper2;
import com.duke.dbus2.util.DUtils2;

import java.lang.reflect.Method;

/**
 * author: duke
 * dateTime: 2019-04-05 16:15
 * description: 订阅函数包装类
 */
public class DMethod2 {

    /**
     * 当前订阅函数
     */
    public Method method;
    /**
     * 在哪个线程执行的方法，参考 DThread2 接口
     */
    public int thread;

    /**
     * 方法的第一个参数类型，用于区分和寻找观察者的事件监听方法
     */
    public Class paramClass;

    public DMethod2(Method method, int thread, Class paramClass) {
        if (DUtils2.isNull(method) || DUtils2.isNull(paramClass)) {
            throw new IllegalArgumentException("method or paramClass is null exception.");
        }
        this.thread = DHelper2.checkOrSetDefaultThread(thread);
        this.method = method;
        this.paramClass = paramClass;
    }

}
