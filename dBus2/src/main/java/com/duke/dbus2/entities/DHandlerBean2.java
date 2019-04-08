package com.duke.dbus2.entities;

import java.lang.ref.WeakReference;

/**
 * author: duke
 * dateTime: 2019-04-05 16:15
 * description: 仅仅用户在 handler 间传递时使用的 bean
 */
public class DHandlerBean2 {

    public Object event;
    public WeakReference<Object> observerWeakRef;
    public DMethod2 dMethod2;

    public DHandlerBean2(Object event, Object observer, DMethod2 dMethod2) {
        this.event = event;
        this.observerWeakRef = new WeakReference<>(observer);
        this.dMethod2 = dMethod2;
    }

}
