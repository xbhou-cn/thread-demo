package com.example;

import com.example.util.Common;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * <h1>原子累加器原理 - cas锁</h1>
 * <b>不建议在开发中使用，当while一直在循环时会消耗性能</b>
 *
 * @author hxb
 */
@Slf4j(topic = "c.Test14")
public class Test14 {

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            log.debug("start .....");
            LockCas.lock();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ignored) {
            }
            LockCas.unLock();
            log.debug("end ....");
        }, "t1").start();

        new Thread(() -> {
            log.debug("start .....");
            LockCas.lock();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ignored) {
            }
            LockCas.unLock();
            log.debug("end ....");
        }, "t2").start();
    }
}

class LockCas {
    private static final AtomicInteger LOCK = new AtomicInteger(0);

    public static void lock() {
        while (true) {
            if (LOCK.compareAndSet(0, 1)) {
                break;
            }
        }
    }

    public static void unLock() {
        LOCK.set(0);
    }
}