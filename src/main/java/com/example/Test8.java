package com.example;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * ABA问题，无法判断变量是否被其他线程修改，一般情况下不影响业务
 */
@Slf4j(topic = "c.Text8")
public class Test8 {
    static AtomicReference<String> ref = new AtomicReference<>("A");

    public static void main(String[] args) throws InterruptedException {
        log.debug("main start ...");
        // 获取A
        String a = ref.get();
        // 将 A -> B -> A
        other();
        TimeUnit.SECONDS.sleep(1);
        // 尝试修改为C, 无法判断数据是否被其他线程修改
        log.debug("change A -> C {}", ref.compareAndSet(a, "C"));
    }

    private static void other() throws InterruptedException {
        new Thread(() -> {
            log.debug("change A -> B {}", ref.compareAndSet(ref.get(), "B"));
        }, "t1").start();
        Thread.sleep(500);
        new Thread(() -> {
            log.debug("change B -> A {}", ref.compareAndSet(ref.get(), "A"));
        }, "t2").start();
    }
}
