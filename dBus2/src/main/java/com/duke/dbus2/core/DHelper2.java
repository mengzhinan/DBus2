package com.duke.dbus2.core;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.duke.dbus2.annotations.DInject2;
import com.duke.dbus2.entities.DHandlerBean2;
import com.duke.dbus2.entities.DMethod2;
import com.duke.dbus2.entities.ObserverWrapper2;
import com.duke.dbus2.executers.DExecutor2;
import com.duke.dbus2.interfaces.DThread2;
import com.duke.dbus2.util.DLog2;
import com.duke.dbus2.util.DUtils2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * author: duke
 * dateTime: 2019-04-05 16:15
 * description: 数据处理辅助类
 */
public class DHelper2 {

    /**
     * 检查并设置默认的线程类型
     *
     * @param thread 当前的线程值
     * @return 修正后的合法线程值
     */
    public static int checkOrSetDefaultThread(int thread) {
        if (thread != DThread2.TYPE_MAIN_UI_THREAD &&
                thread != DThread2.TYPE_CURRENT_CHILD_THREAD &&
                thread != DThread2.TYPE_NEW_CHILD_THREAD) {
            // 默认在主线程执行
            thread = DThread2.TYPE_MAIN_UI_THREAD;
        }
        return thread;
    }

    /**
     * 清理无效的观察者对象
     */
    static ObserverWrapper2 clearAndGetObserverHeader(ObserverWrapper2 oldHeader) {
        long startTime = System.currentTimeMillis();
        if (DUtils2.isNull(oldHeader)) {
            return null;
        }
        // 循环，找到有效的头节点
        do {
            if (DUtils2.isEmpty(oldHeader.observerWeakRef)) {
                // 链表头移到下一个节点
                oldHeader = oldHeader.next;
            } else {
                break;
            }
        } while (oldHeader != null);
        if (DUtils2.isNull(oldHeader)) {
            return null;
        }

        // 保存新的链表头
        ObserverWrapper2 newHeader = oldHeader;

        // 清空无效的节点
        // 清空无效的节点
        ObserverWrapper2 preObserver = oldHeader;
        ObserverWrapper2 indexObserver = preObserver.next;
        while (indexObserver != null) {
            if (DUtils2.isEmpty(indexObserver.observerWeakRef)) {
                // 当前指针向后移动
                indexObserver = indexObserver.next;
                // 丢弃链表中无效的元素
                preObserver.next = indexObserver;
            } else {
                // 链表前一个参考标记向后移动
                preObserver = indexObserver;
                // 当前指针向后移动
                indexObserver = indexObserver.next;
            }
        }

        DLog2.logTime(startTime, "clearAndGetObserverHeader");
        return newHeader;
    }

    /**
     * 获取观察者数量
     *
     * @return 观察者数量
     */
    static int getObserverSize(ObserverWrapper2 header) {
        clearAndGetObserverHeader(header);
        if (DUtils2.isNull(header)) {
            return 0;
        }
        int size = 0;
        ObserverWrapper2 node = header;
        while (node != null) {
            size++;
            node = node.next;
        }
        return size;
    }

    /**
     * 检查当前观察者对象是否注册过
     *
     * @param observer 观察者对象
     * @return true：已经注册；false：未注册
     */
    static boolean isRegistered(Object observer, ObserverWrapper2 header) {
        if (DUtils2.isNull(observer)) {
            // 观察者对象为 null，视为已经注册
            return true;
        }
        if (DUtils2.isNull(header)) {
            // 链表为 null，未注册
            return false;
        }
        ObserverWrapper2 node = header;
        do {
            if (!DUtils2.isEmpty(node.observerWeakRef) && node.observerWeakRef.get() == observer) {
                return true;
            } else {
                node = node.next;
            }
        } while (node != null);
        return false;
    }

    /**
     * 获取类和方法信息
     *
     * @param observer 观察者类对象
     * @return 注册方法集合
     */
    static ArrayList<DMethod2> addClassInfoAndGetMethods(Object observer,
                                                         HashMap<Class, ArrayList<DMethod2>> classMethodMap) {
        if (DUtils2.isNull(classMethodMap)) {
            throw new IllegalArgumentException("classMethodMap is null exception.");
        }
        if (DUtils2.isNull(observer)) {
            return new ArrayList<>();
        }
        Class clazz = observer.getClass();
        if (classMethodMap.containsKey(clazz)) {
            return classMethodMap.get(clazz);
        }
        ArrayList<DMethod2> dMethod2List = new ArrayList<>();
        // 如果是直接定位方法名
        // clazz.getMethods() 获取公共的可访问的方法，但是缺点是包含父类的方法
        // clazz.getDeclaredMethods() 获取自己类定义的方法，缺点是还包含私有的和不可访问的方法
        // 查找特定的函数(方法名，参数)
        // Method method = clazz.getDeclaredMethod("方法名", Object.class);
        // 获取当前类声明的所有方法
        Method[] methods = clazz.getDeclaredMethods();
        if (methods.length <= 0) {
            // 当前类没有方法
            return dMethod2List;
        }
        DInject2 dInject2;
        for (Method method : methods) {
            if (!isMethodParamOK(method)) {
                // 方法是否有参数，参数是否合法，最少有一个参数
                continue;
            }
            // 循环，获取每个函数的注解对象
            dInject2 = method.getAnnotation(DInject2.class);
            if (DUtils2.isNull(dInject2)) {
                // 如果没有注解，忽略此方法
                continue;
            }
            // 获取注解 thread 参数值
            int thread = dInject2.thread();
            dMethod2List.add(new DMethod2(method, thread, method.getParameterTypes()[0]));
        }
        if (!DUtils2.isEmpty(dMethod2List)) {
            // 记录类及方法信息
            classMethodMap.put(clazz, dMethod2List);
        }
        return dMethod2List;
    }

    /**
     * 判断参数类型
     *
     * @param method 待处理的方法
     * @return 是否是特定参数，特定修饰符
     */
    private static boolean isMethodParamOK(Method method) {
        try {
            if (DUtils2.isNull(method)) {
                return false;
            }
            // 获取参数类型
            Class[] paramTypes = method.getParameterTypes();
            //判断参数个数
            if (paramTypes.length < 1) {
                return false;
            }
            //判断参数类型
            //return DData.class.getName().equals(paramTypes[0].getName());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            DLog2.logD(e.getMessage());
        }
        return false;
    }

    /**
     * 循环当前观察者的所有注册方法
     *
     * @param event             调用参数对象
     * @param observer          观察者对象
     * @param dMethod2ArrayList 观察者的所有注册方法
     */
    static void findAndInvokeMethod(Object event, Object observer, ArrayList<DMethod2> dMethod2ArrayList) {
        if (DUtils2.isNull(event) ||
                DUtils2.isNull(observer) ||
                DUtils2.isEmpty(dMethod2ArrayList)) {
            return;
        }
        int size = dMethod2ArrayList.size();
        // 循环当前观察者的所有注册方法
        for (int i = 0; i < size; i++) {
            switchThread(event, observer, dMethod2ArrayList.get(i));
        }
    }

    /**
     * 切换线程
     *
     * @param event    调用参数对象
     * @param observer 观察者对象
     * @param dMethod2 观察者的注册方法
     */
    private static void switchThread(final Object event, final Object observer, final DMethod2 dMethod2) {
        if (DUtils2.isNull(dMethod2) ||
                DUtils2.isNull(dMethod2.method) ||
                DUtils2.isNull(dMethod2.paramClass) ||
                DUtils2.isNull(event) ||
                DUtils2.isNull(observer) ||
                event.getClass() != dMethod2.paramClass) {
            // 如果参数类型不一致，不执行调用
            return;
        }
        try {
            if (dMethod2.thread == DThread2.TYPE_MAIN_UI_THREAD) {
                // 主线程必须要用 handler 调用
                Message message = innerHandler.obtainMessage();
                message.obj = new DHandlerBean2(event, observer, dMethod2);
                innerHandler.sendMessage(message);
            } else if (dMethod2.thread == DThread2.TYPE_CURRENT_CHILD_THREAD) {
                if (!DHelper2.isMainThread()) {
                    // 当前就是子线程，直接在当前线程执行
                    invoke(event, observer, dMethod2);
                } else {
                    // 在子线程中执行
                    DExecutor2.get().execute(new Runnable() {
                        @Override
                        public void run() {
                            invoke(event, observer, dMethod2);
                        }
                    });
                }
            } else {
                // 在子线程中执行
                DExecutor2.get().execute(new Runnable() {
                    @Override
                    public void run() {
                        invoke(event, observer, dMethod2);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            DLog2.logD(e.getMessage());
        }
    }

    private static final Handler innerHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (DUtils2.isNull(msg) || !(msg.obj instanceof DHandlerBean2)) {
                return;
            }
            DHandlerBean2 dHandlerBean2 = (DHandlerBean2) msg.obj;
            if (DUtils2.isEmpty(dHandlerBean2.observerWeakRef)) {
                return;
            }
            try {
                Object event = dHandlerBean2.event;
                Object observer = dHandlerBean2.observerWeakRef.get();
                DMethod2 dMethod2 = dHandlerBean2.dMethod2;
                invoke(event, observer, dMethod2);
            } catch (Exception e) {
                e.printStackTrace();
                DLog2.logD(e.getMessage());
            }
        }
    };

    /**
     * 开始执行调用
     *
     * @param event    调用参数对象
     * @param observer 观察者对象
     * @param dMethod2 观察者的注册方法
     */
    private static void invoke(Object event, Object observer, DMethod2 dMethod2) {
        if (DUtils2.isNull(dMethod2) ||
                DUtils2.isNull(dMethod2.method) ||
                DUtils2.isNull(dMethod2.paramClass) ||
                DUtils2.isNull(event) ||
                DUtils2.isNull(observer)) {
            return;
        }
        try {
            dMethod2.method.setAccessible(true);
            // core, 反射调用
            dMethod2.method.invoke(observer, event);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            DLog2.logD(e.getMessage());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            DLog2.logD(e.getMessage());
        }
    }

    private static boolean isMainThread() {
        try {
            return Thread.currentThread() == Looper.getMainLooper().getThread();
        } catch (Exception e) {
            e.printStackTrace();
            DLog2.logD(e.getMessage());
        }
        return false;
    }
}
