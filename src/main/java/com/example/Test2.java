package com.example;

import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.Test2")
public class Test2 {
    // 线程1等待线程2的结果
    public static void main(String[] args) {
        GuardedObject guardedObject = new GuardedObject();
        new Thread(() -> {
            Object obj = guardedObject.get(2000);
            log.debug("拿到对象：{}", obj);
        }, "t1").start();

        new Thread(() -> {
            try {
                Thread.sleep(1999);
            } catch (InterruptedException e) {
            }
            Object obj = new Object();
            log.debug("设置对象：{}", obj.toString());
            guardedObject.set(obj);
        }, "t2").start();
    }
}

class GuardedObject {
    // 结果
    private Object response;

    // 获取结果
    public Object get() {
        return this.get(-1);
    }

    // 获取结果
    public synchronized Object get(long timeout) {
        // 开始时间
        long begin = System.currentTimeMillis();
        long passedTime = 0;
        while (response == null) {
            if (timeout >= 0 && passedTime >= timeout) {
                break;
            }
            try {
                if (timeout >= 0) {
                    this.wait(timeout - passedTime);
                } else {
                    this.wait();
                }
            } catch (InterruptedException ignore) {
            }
            passedTime = System.currentTimeMillis() - begin;
        }
        return response;
    }

    public synchronized void set(Object response) {
        this.response = response;
        this.notifyAll();
    }
}