package com.duke.dbus2.entities;

import com.duke.dbus2.util.DUtils2;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * author: duke
 * dateTime: 2019-04-05 16:15
 * description: 观察者包装类
 */
public class ObserverWrapper2 {

    /**
     * 下一个节点
     */
    public ObserverWrapper2 next;

    /**
     * 观察者弱引用对象
     */
    public WeakReference<Object> observerWeakRef;

    /**
     * 观察者关联的注册方法集合
     */
    public ArrayList<DMethod2> dMethod2ArrayList;

    public ObserverWrapper2(Object observer,
                            ArrayList<DMethod2> dMethod2ArrayList,
                            ObserverWrapper2 header) {
        if (DUtils2.isNull(observer) || DUtils2.isEmpty(dMethod2ArrayList)) {
            throw new IllegalArgumentException("observer or method list is null exception.");
        }
        this.observerWeakRef = new WeakReference<>(observer);
        this.dMethod2ArrayList = dMethod2ArrayList;
        this.next = header;
    }

}
