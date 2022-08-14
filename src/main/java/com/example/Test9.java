package com.example;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * ABA问题，无法判断变量是否被其他线程修改，一般情况下不影响业务
 * <p>
 * AtomicStampedReference可以解决ABA问题,通过设置版本号
 */
@Slf4j(topic = "c.Text8")
public class Test9 {
    static AtomicStampedReference<String> ref = new AtomicStampedReference<>("A", 0);

    public static void main(String[] args) throws InterruptedException {
        log.debug("main start ...");
        // 获取A
        String a = ref.getReference();
        // 获取版本号
        int stamp = ref.getStamp();
        log.debug("{}", stamp);
        // 将 A -> B -> A
        other();
        TimeUnit.SECONDS.sleep(1);
        // 尝试修改为C, 需要传当前版本号，以及修改后的版本号
        log.debug("change A -> C {}", ref.compareAndSet(a, "C", stamp, stamp + 1));
    }

    private static void other() throws InterruptedException {
        new Thread(() -> {
            log.debug("change A -> B {}", ref.compareAndSet(ref.getReference(), "B", ref.getStamp(), ref.getStamp() + 1));
        }, "t1").start();
        Thread.sleep(500);
        new Thread(() -> {
            log.debug("change B -> A {}", ref.compareAndSet(ref.getReference(), "A", ref.getStamp(), ref.getStamp() + 1));
        }, "t2").start();
    }
}
