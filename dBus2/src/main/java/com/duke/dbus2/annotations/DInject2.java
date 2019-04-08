package com.duke.dbus2.annotations;

import com.duke.dbus2.interfaces.DThread2;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * author: duke
 * dateTime: 2019-04-05 16:15
 * description: 注册事件的方法的注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DInject2 {

    /**
     * 当前注解的方法在哪个线程中执行
     *
     * @return 参考 DThread2 接口定义  0：UI线程，1：子线程
     */
    int thread() default DThread2.TYPE_MAIN_UI_THREAD;

}
