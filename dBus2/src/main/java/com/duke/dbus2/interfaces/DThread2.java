package com.duke.dbus2.interfaces;

/**
 * author: duke
 * dateTime: 2019-04-05 16:15
 * description: 常量，标记线程类型
 */
public interface DThread2 {

    // UI 线程
    int TYPE_MAIN_UI_THREAD = 0;

    // 当前子线程
    int TYPE_CURRENT_CHILD_THREAD = 1;

    // 新的子线程
    int TYPE_NEW_CHILD_THREAD = 2;

}
