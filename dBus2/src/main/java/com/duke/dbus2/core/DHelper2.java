package com.duke.dbus2.core;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

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
import java.util.LinkedList;

/**
 * author: duke
 * dateTime: 2019-04-05 16:15
 * description: 数据处理辅助类
 */
public class DHelper2 {

    /**
     * 清除无效的观察者对象
     */
    static void clearInvalidObserver(@NonNull LinkedList<ObserverWrapper2> linkedList) {
        ObserverWrapper2 observerWrapper2;
        for (int i = 0; i < linkedList.size(); i++) {
            observerWrapper2 = linkedList.get(i);
            if (observerWrapper2 == null ||
                    DUtils2.isEmpty(observerWrapper2.dMethod2ArrayList) ||
                    DUtils2.isEmpty(observerWrapper2.observerWeakRef)) {
                linkedList.remove(observerWrapper2);
                i--;
            }
        }
    }

    /**
     * 移除观察者对象
     */
    static void removeObserver(@NonNull Object observer, @NonNull LinkedList<ObserverWrapper2> linkedList) {
        ObserverWrapper2 observerWrapper2;
        int size = linkedList.size();
        for (int i = 0; i < size; i++) {
            observerWrapper2 = linkedList.get(i);
            if (observerWrapper2 != null &&
                    !DUtils2.isEmpty(observerWrapper2.observerWeakRef) &&
                    observerWrapper2.observerWeakRef.get() == observer) {
                linkedList.remove(observerWrapper2);
                return;
            }
        }
    }

    /**
     * 当前list中是否包含指定的观察者对象
     *
     * @param observer 观察者对象
     * @return 是否包含
     */
    static boolean isRegistered(@NonNull Object observer, @NonNull LinkedList<ObserverWrapper2> linkedList) {
        ObserverWrapper2 observerWrapper2;
        int size = linkedList.size();
        for (int i = 0; i < size; i++) {
            observerWrapper2 = linkedList.get(i);
            if (observerWrapper2 != null &&
                    !DUtils2.isEmpty(observerWrapper2.observerWeakRef) &&
                    observerWrapper2.observerWeakRef.get() == observer) {
                return true;
            }
        }
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
        // 判断类的信息是否已经添加，避免重复添加
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
            if (paramTypes.length != 1) {
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
     * 切换线程
     *
     * @param event    调用参数对象
     * @param observer 观察者对象
     * @param dMethod2 观察者的注册方法
     */
    static void switchThread(@NonNull final Object event, @NonNull final Object observer, @NonNull final DMethod2 dMethod2) {
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
                if (event == null ||
                        observer == null ||
                        dMethod2 == null ||
                        dMethod2.method == null ||
                        dMethod2.paramClass == null) {
                    return;
                }
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
    private static void invoke(@NonNull Object event, @NonNull Object observer, @NonNull DMethod2 dMethod2) {
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
