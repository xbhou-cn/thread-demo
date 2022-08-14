package com.example;

import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.TestCorrectPostureStep1")
class TestCorrectPostureStep1 {
    private static final Object LOCK = new Object();
    // 有没有烟
    static boolean HAS_CIGARETTE = false;
    static boolean HAS_TAKEOUT = false;

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            synchronized (LOCK) {
                log.debug("有烟没？【{}】", HAS_CIGARETTE);
                while (!HAS_CIGARETTE) {
                    log.debug("没烟，先歇会！");
                    try {
                        LOCK.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("有烟没？【{}】", HAS_CIGARETTE);
                if (HAS_CIGARETTE) {
                    log.debug("可以开始干活了！");
                }
            }
        }, "小南").start();

        new Thread(() -> {
            synchronized (LOCK) {
                log.debug("有外卖没？【{}】", HAS_TAKEOUT);
                while (!HAS_TAKEOUT) {
                    log.debug("没外卖，先歇会！");
                    try {
                        LOCK.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("有外卖没？【{}】", HAS_TAKEOUT);
                if (HAS_TAKEOUT) {
                    log.debug("可以开始干活了！");
                }
            }
        }, "小女").start();

        TimeUnit.SECONDS.sleep(1);

        new Thread(() -> {
            synchronized (LOCK) {
                HAS_CIGARETTE = true;
                log.debug("烟到了哦！");
                LOCK.notifyAll();
            }
        }, "送烟的").start();

        new Thread(() -> {
            synchronized (LOCK) {
                HAS_TAKEOUT = true;
                log.debug("外卖到了哦！");
                LOCK.notifyAll();
            }
        }, "送外卖的").start();
    }
}