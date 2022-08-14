package com.example;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 交替输出
 *
 * @author hxb
 */
@Slf4j(topic = "c.Test6")
public class Test6 {

    static Thread t1, t2, t3;

    public static void main(String[] args) throws InterruptedException {
        Park messageLock = new Park(3);

        t1 = new Thread(() -> {
            messageLock.print("a", t2);
        }, "t1");

        t2 = new Thread(() -> {
            messageLock.print("b", t3);
        }, "t2");

        t3 = new Thread(() -> {
            messageLock.print("c", t1);
        }, "t3");

        t1.start();
        t2.start();
        t3.start();
        TimeUnit.SECONDS.sleep(2);

        LockSupport.unpark(t1);
    }
}

@RequiredArgsConstructor
class Park {
    private final int size;

    public void print(String str, Thread next) {
        for (int i = 0; i < size; i++) {
            synchronized (this) {
                LockSupport.park();
                System.out.print(str);
                LockSupport.unpark(next);
            }
        }
    }
}
